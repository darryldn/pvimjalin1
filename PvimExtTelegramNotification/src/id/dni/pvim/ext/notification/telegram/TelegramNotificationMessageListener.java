/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.dni.pvim.ext.notification.telegram;

import com.rab.core.server.ServiceDispatch;
import com.rab.core.util.Logger;
import com.wn.econnect.inbound.wsi.ticket.TicketDto;
import com.wn.tasman.basedata.SystemVariableManager;
import com.wn.tasman.notification.dao.NotificationManagerDAO;
import com.wn.tasman.notification.ext.dni.services.AsyncSender;
import com.wn.tasman.notification.ext.dni.services.FirebaseService;
import com.wn.tasman.ticket.TicketManager;
import com.wn.tasman.ticket.domain.NotificationRecord;
import com.wn.tasman.ticket.domain.Ticket;
import com.wn.tasman.ticket.domain.TicketRecord;
import com.wn.tasman.user.dao.UserDao;
import com.wn.tasman.user.domain.User;
import com.wn.tasman.util.NotificationRecordHelper;
import com.wn.tasman.util.SLMUtil;
import id.dni.pvim.ext.db.config.PVIMDBConnectionFactory;
import id.dni.pvim.ext.db.trx.IProViewTrx;
import id.dni.pvim.ext.net.TransferTicketDto;
import id.dni.pvim.ext.repo.exceptions.PvExtPersistenceException;
import id.dni.pvim.ext.telegram.commons.config.TelegramConfig;
import id.dni.pvim.ext.telegram.commons.sender.MessageSender;
import id.dni.pvim.ext.telegram.repo.ITelegramSuscribersRepository;
import id.dni.pvim.ext.telegram.repo.TelegramRepositoryFactory;
import id.dni.pvim.ext.telegram.repo.db.vo.TelegramSubscriberVo;
import id.dni.pvim.ext.telegram.repo.spec.TelegramSubscribersListOfPhonesSpec;
import id.dni.pvim.ext.web.in.Commons;
import java.sql.SQLException;
import java.util.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.HibernateTemplate;

/**
 *
 * @author darryl.sulistyan
 */
public class TelegramNotificationMessageListener implements MessageListener {

//    @EJB
    private AsyncSender asyncSenderService;
    private FirebaseService firebaseService;

    @Autowired
    public void setAsyncSender(AsyncSender s) {
        this.asyncSenderService = s;
    }

    @Autowired(required = false)
    @Qualifier("DummyFirebaseServiceImpl")
    public void setFirebaseService(FirebaseService s) {
        this.firebaseService = s;
    }

    private final Logger logger;
    private SystemVariableManager systemVariableManager;
    private NotificationManagerDAO notificationManagerDAO;
    private Integer delay;
    private TicketManager ticketManager;
    private UserDao userDao;
    private HibernateTemplate pvimHibernateTemplate;
    private static final String DISPATCH_PREFIX = "dispatch:";
    private ITelegramSuscribersRepository gSubscriberRepos = null;
    private static final int S_OK = 0, E_ERR = -1;

    public TelegramNotificationMessageListener() {
        logger = Logger.getLogger(TelegramNotificationMessageListener.class);
    }

    public void setDelay(Integer delay) {
        this.delay = delay;
    }

    public void setTicketManager(TicketManager ticketManager) {
        this.ticketManager = ticketManager;
    }

    public void setNotificationManagerDAO(
            NotificationManagerDAO notificationManagerDAO) {
        this.notificationManagerDAO = notificationManagerDAO;
    }

    public void setSystemVariableManager(SystemVariableManager systemVariableManager) {
        this.systemVariableManager = systemVariableManager;
    }

    void setSubscriberRepository(ITelegramSuscribersRepository repo) {
        this.gSubscriberRepos = repo;
    }

    public Ticket getTicket(String ticketId)
            throws InterruptedException {
        Ticket ticket = null;
        try {
            Thread.sleep(1000L * this.delay);
            ticket = this.ticketManager.getTicket(ticketId);
            if (ticket == null) {
                Thread.sleep(1000L * this.delay);
                ticket = this.ticketManager.getTicket(ticketId);
            }
        } catch (InterruptedException e) {
            this.logger.logError("Failed to get ticket when delay current thread", e);
            throw e;
        }
        return ticket;
    }

    // must be lazily loaded because sometimes, when it is done earlier, somehow
    // the ServiceDispatch is not ready and kaboom, throws exception.
    private synchronized UserDao getUserDao() {
        if (this.userDao == null) {
            ApplicationContext appCtx = ServiceDispatch.getaCtx();
            this.userDao = (UserDao) appCtx.getBean("userDao");
        }
        return this.userDao;
    }

    // ditto with above
    private synchronized HibernateTemplate getPvimHibernateTemplate() {
        if (this.pvimHibernateTemplate == null) {
            ApplicationContext appCtx = ServiceDispatch.getaCtx();
            this.pvimHibernateTemplate = ((HibernateTemplate) appCtx.getBean("pvimHibernateTemplate"));
        }
        return this.pvimHibernateTemplate;
    }

    /**
     * Just a convenience function to call one mobile number, instead of an array
     * @param mobile
     * @return
     * @throws PvExtPersistenceException 
     */
    private List<TelegramSubscriberVo> getSubscribers(String mobile) throws PvExtPersistenceException {
        return getSubscribers(new String[]{mobile});
    }

    /**
     * Function to get list of telegram subscribers from list of mobile phone numbers
     * @param mobile
     * @return
     * @throws PvExtPersistenceException 
     */
    private List<TelegramSubscriberVo> getSubscribers(String[] mobile) throws PvExtPersistenceException {
        // since table PVIM_EXT_TELEGRAM_SUBSCRIBER is not mapped to Hibernate, 
        // pvimHibernateTemplate cannot be used here
        
        IProViewTrx pvimTx = PVIMDBConnectionFactory.getInstance().getTransaction();
        try {
            pvimTx.begin();

            Object conn = pvimTx.getTrxConnection();
            ITelegramSuscribersRepository subscriberRepos;

            // allows for overriding implementation of subscriberRepository.
            // useful for unit testing
            if (gSubscriberRepos != null) {
                subscriberRepos = gSubscriberRepos;
            } else {
                subscriberRepos = TelegramRepositoryFactory.getInstance().getTelegramSubscribersRepository(conn);
            }

            List<TelegramSubscriberVo> subscribers = subscriberRepos.query(
                    new TelegramSubscribersListOfPhonesSpec(mobile));
            pvimTx.commit();
            return subscribers;

        } catch (PvExtPersistenceException ex) {
            pvimTx.rollback();
            throw ex;

        } catch (Exception ex) {
            pvimTx.rollback();
            throw new PvExtPersistenceException(ex);

        } finally {
            pvimTx.close();

        }
    }

    /**
     * Convenience function to create notification record POJO.
     * This, however, does not insert the record to database yet. To do that,
     * call NotificationRecordHelper createNotificationRecord method.
     * 
     * To insert the record to TICKET_HISTOY, call the following function:
     * NotificationRecordHelper.getInstance().recordNotificationToHistory(nr, statusId);
     * 
     * @param receiver any string, will be put into RECEIVER column in NOTIFICATION_RECORD. This MAY NOT NULL!
     * @param context the content of the notification
     * @param ticketId
     * @return 
     */
    private NotificationRecord createNotificationRecord(String receiver, String context, String ticketId) {
        NotificationRecord nr = new NotificationRecord(null, "telegram", new Timestamp(new Date().getTime()), "0", null);
        nr.setReceiver(receiver);
        if (context.length() > 2500) {
            context = context.substring(0, 2500);
        }
        nr.setContent(context);
        if (ticketId != null) {
            nr.setTicket(ticketId);
        }
        return nr;
    }

    private static class ResultHolder {
        Future<TelegramRequestResult> future;
        TelegramSubscriberVo subscriber;
    }

    List<TelegramRequestResult> dispatchTicketToSubscribers(List<TelegramSubscriberVo> subscribers, String messageContent) {
        logger.logInfo("Start Enumerating subscribers: ");

        List<ResultHolder> futures = new ArrayList<>();
        final String telegramContent = messageContent;

        for (TelegramSubscriberVo subscriber : subscribers) {
            logger.logInfo("Subscriber id: " + subscriber.getSubs_id());

            final long chatID = subscriber.getChat_id();
            logger.logInfo("chatID: " + chatID);

            final TelegramSubscriberVo finalSubs = subscriber;
            ResultHolder rh = new ResultHolder();
            rh.subscriber = subscriber;
            rh.future = this.asyncSenderService.doAsync(new Callable<TelegramRequestResult>() {
                @Override
                public TelegramRequestResult call() throws Exception {
                    if (MessageSender.sendMessageAndSwallowLogs(chatID, telegramContent)) {
                        logger.logInfo("Success sending message to chatID: "
                                + chatID + " for message: " + telegramContent + " mobile: " + finalSubs.getPhone_num());
                        return (new TelegramRequestResult(finalSubs.getPhone_num(), chatID, telegramContent, S_OK));
                    } else {
                        logger.logError("Error, cannot send message to chatID: "
                                + chatID + " for message: " + telegramContent + " mobile: " + finalSubs.getPhone_num());
                        return (new TelegramRequestResult(finalSubs.getPhone_num(), chatID, telegramContent, E_ERR));
                    }
                }
            });
            futures.add(rh);
        }

        List<TelegramRequestResult> result = new ArrayList<>();
        for (ResultHolder requestHolder : futures) {
            TelegramSubscriberVo subscriber = requestHolder.subscriber;
            Future<TelegramRequestResult> future = requestHolder.future;

            TelegramRequestResult req;
            try {
                req = future.get();
            } catch (InterruptedException | ExecutionException ex) {
                this.logger.logError(ex);
                req = null;
            }

            if (req == null) { // if throw exception in doAsync, it returns null.
                req = (new TelegramRequestResult(subscriber.getPhone_num(), subscriber.getChat_id(), telegramContent, E_ERR));
            }

            result.add(req);
        }

        return result;
    }

    private String getTelegramMessage(String ticketNumber, String messageContent) {
        String telegramContent;

        if (Commons.isEmptyStrIgnoreSpaces(messageContent)) {
            String deeplink = TelegramConfig.getDeeplinkPrefix();
            telegramContent = deeplink + ticketNumber;
            logger.logError("No messageContent sent, default to telegram deeplink: " + telegramContent);

        } else {
            telegramContent = messageContent;

        }
        return telegramContent;
    }

    // just a holder class to hold note and timestamp;
    // in order to sort the note on timestamp desc, latest note will be shown first.
    private static class NotePair implements Comparable<NotePair> {

        private String note;
        private long timestamp;

        public NotePair(String note, long timestamp) {
            this.note = note;
            this.timestamp = timestamp;
        }
        
        @Override
        public int compareTo(NotePair o) {
            if (timestamp > o.timestamp) {
                return -1;
            } else if (timestamp == o.timestamp) {
                return 0;
            } else {
                return 1;
            }
        }
        
    }
    
    /**
     * Obtain notes from a set of ticketRecords.
     * @param ticketRecords
     * @return 
     */
    private List<NotePair> grabNotes(Set ticketRecords) {
        List<NotePair> np = new ArrayList<>();
        
//        StringBuilder sb = new StringBuilder();
        for (Object o : ticketRecords) {
            TicketRecord r = (TicketRecord) o;
            if ("1".equals(r.getNoteSourceFlag())) {
                np.add(new NotePair(r.getNote(), r.getCreateTime().getTime()));
//                sb.append(r.getNote()).append(System.lineSeparator());
            }
        }
        return np;
    }

    /**
     * Function to send the ticket to firebase when onMessage is notified
     * @param ticketId
     * @param statusId 
     */
    private void sendToFirebase(final String ticketId, final String statusId, final List<String> accounts) {
        if (this.firebaseService != null) {
            if (this.pvimHibernateTemplate != null) {
                this.pvimHibernateTemplate.execute(new HibernateCallback() {
                    @Override
                    public Void doInHibernate(org.hibernate.Session session)
                            throws org.hibernate.HibernateException, SQLException {
                        
                        // in order to fully utilize the hibernate, one should re-obtain
                        // the objects via hibernate session.
                        // using the objects directly (for example, from Ticket object from onMessage function)
                        // can cause HibernateException no session error.
                        
                        // Ripped from EmailMessageListener.class
                        Ticket ticket = (Ticket) session.get(Ticket.class, ticketId);
                        if (ticket == null) {
                            try {
                                Thread.sleep(1000L * TelegramNotificationMessageListener.this.delay);
                            } catch (InterruptedException ignore) {
                            }
                            ticket = (Ticket) session.get(Ticket.class, ticketId);
                        }
                        
                        if (ticket != null) {
                            Ticket parent = ticket.getParentTicket();
                            List<NotePair> an = new ArrayList<>();
                            if (parent != null) {
                                an.addAll(grabNotes(parent.getTicketRecords()));
                            }
                            an.addAll(grabNotes(ticket.getTicketRecords()));
                            Collections.sort(an);
                            StringBuilder notes = new StringBuilder();
                            for (NotePair np : an) {
                                notes.append(np.note).append(System.lineSeparator());
                            }
                            TicketDto ticketDto = SLMUtil.convertTicketToTicketDto(ticket);
                            ticketDto.setNote(notes.toString());
//                            long lastupdated = ticket.getLastUpdated().getTime();
                            long lastupdated = System.currentTimeMillis(); // For transferticketdto
                                                                           // the issue is for reminders, no ticket updates are issued.
                            
                            // Map has to be used instead of TicketDto because TicketDto                                               
                            // class from rab-server.jar is used in Spring IoC context
                            // and using it as parameter in our service (FirebaseService) can cause
                            // some issues on classloading.
                            Map<String, Object> ticketMap = TicketUtil.ticketDto2Map(ticketDto);
                            
                            TransferTicketDto trs = new TransferTicketDto(ticketId);
                            trs.setLastupdated(lastupdated);
                            trs.setTicketMap(ticketMap);
                            trs.setAccountList(accounts);
                            
                            if ("7".equals(statusId)) { // ticket in Deleted state
                                firebaseService.remove(trs);
                            } else {
                                firebaseService.send(trs);
                            }
                        } else {
                            logger.logError("Unable to obtain ticket after delay!");
                        }

                        return null;
                    }

                });

            } else {
                logger.logError("No pvimHibernateTemplate object!");
            }

        } else {
            logger.logInfo("Firebase service not registered");
        }
    }

    /**
     * Send ticket to its assigned user via telegram
     * @param ticket
     * @param messageContent
     * @return
     * @throws PvExtPersistenceException 
     */
    private List<TelegramRequestResult> dispatchTicket(Ticket ticket, String messageContent) throws PvExtPersistenceException {
        String userid = ticket.getSlmUserByAssigneeId().getUserId();
        logger.logInfo("Assigned userID = " + userid);

//        String ticketNumber = ticket.getTicketNum();
        User safe_user = this.userDao.getUser(userid);
        String mobile = safe_user.getMobile();

        if (Commons.isEmptyStrIgnoreSpaces(mobile)) {
            this.logger.logError("User with id " + userid + " has no mobile number registered!");
            List<TelegramRequestResult> rsa = new ArrayList<>();
            TelegramRequestResult res = new TelegramRequestResult("userid:" + userid, 0, messageContent, E_ERR);
            res.setReceiverName(safe_user.getName());
            rsa.add(res);
            return rsa;
        }

        return sendToSpecifiedAccountsWithoutDispatch(ticket, new String[]{mobile}, messageContent);

//        logger.logInfo("ticketNumber = " + ticketNumber + " mobile = " + mobile);
//        List<TelegramSubscriberVo> subscribers = getSubscribers(mobile);
//        
//        logger.logInfo("Start Enumerating subscribers: ");
//        String telegramContent = getTelegramMessage(ticketNumber, messageContent);
//        dispatchTicketToSubscribers(subscribers, telegramContent);
    }

    /**
     * Send ticket to specified accounts
     * @param ticket
     * @param accounts list of phone numbers
     * @param messageContent
     * @return
     * @throws PvExtPersistenceException 
     */
    private List<TelegramRequestResult> sendToSpecifiedAccountsWithoutDispatch(Ticket ticket, String[] accounts, String messageContent)
            throws PvExtPersistenceException {

        if (accounts.length == 0) {
            this.logger.logError("Error sending message. No target accounts selected");
            return Collections.EMPTY_LIST;
        }

        String ticketNumber = ticket.getTicketNum();
        List<TelegramSubscriberVo> subs = getSubscribers(accounts);

        Set<String> subsPhones = new HashSet<>();
        for (TelegramSubscriberVo sub : subs) {
            subsPhones.add(sub.getPhone_num());
        }

        List<TelegramRequestResult> errUsers = new ArrayList<>();
        for (String account : accounts) {
            if (Commons.isEmptyStrIgnoreSpaces(account)) {
                TelegramRequestResult res = new TelegramRequestResult("N/A", 0, messageContent, E_ERR);
                this.logger.logError("Error sending message. No mobile number registered.");
                errUsers.add(res);

            } else if (!subsPhones.contains(account)) {
                // found unregistered user! Must record it as failure!
                TelegramRequestResult res = new TelegramRequestResult(account, 0, messageContent, E_ERR);
                this.logger.logError("Error sending message. Mobile number " + account + " has not subscribed to telegram bot!");
                errUsers.add(res);
            }
        }

        String telegramContent = getTelegramMessage(ticketNumber, messageContent);
        List<TelegramRequestResult> result = dispatchTicketToSubscribers(subs, telegramContent);
        result.addAll(errUsers);
        return result;
    }
    
    private List<String> extractAccounts(String accounts) {
        String[] listAccounts = accounts.split(",");
        List<String> trimmedAccounts = new ArrayList<>();
        for (int i = 0; i < listAccounts.length; ++i) {
            if (!Commons.isEmptyStrIgnoreSpaces(listAccounts[i])) {
                trimmedAccounts.add(listAccounts[i].trim());
            }
        }
        return trimmedAccounts;
    }

    @Override
    public void onMessage(Message message) {
        if (message instanceof MapMessage) {
            logger.logInfo("asyncSenderService: " + this.asyncSenderService); // asyncSender is NOT NULL!!
            boolean isError = false;
            String accounts = null, context = null, ticketId = null, statusId = null;
            
            // injection successful!

//            NotificationRecord nr = new NotificationRecord(null, "telegram", new Timestamp(new Date().getTime()), "0", null);
            try {
                accounts = ((MapMessage) message).getString("accounts");
                context = ((MapMessage) message).getString("context");
                ticketId = ((MapMessage) message).getString("ticketId");
                statusId = ((MapMessage) message).getString("ticketStatusId");
                this.logger.logInfo("accounts : " + accounts + "; ticketId : " + ticketId + "; ");
//                nr.setReceiver(accounts);

                if (context == null) {
                    context = "";
                }
                // context is the content template of the notification / reminder, in plain text and all
                // ${XXX} has been replaced with actual content!!
                // see NotificationTemplate in Pvim console.
                if (context.length() > 2500) {
                    context = context.substring(0, 2500);
                }
//                nr.setContent(context);
//                if (ticketId != null) {
//                    nr.setTicket(ticketId);
//                }
//                this.notificationManagerDAO.createNotificationRecord(nr);

//                NotificationRecordHelper helper = NotificationRecordHelper.getInstance();
//                helper.recordNotificationToHistory(nr, statusId);
                this.userDao = getUserDao();
                this.pvimHibernateTemplate = getPvimHibernateTemplate();

//                ApplicationContext appCtx = ServiceDispatch.getaCtx();
//                this.userDao = (UserDao) appCtx.getBean("userDao");
                // send shit to telegram
                Ticket ticket = getTicket(ticketId);
                if (ticket == null) {
                    logger.logError("Unable to read ticket data from ticketManager");
                    isError = true;

                } else {
                    List<String> trimmedAccounts = extractAccounts(accounts);
                    
                    logger.logInfo("Sending to firebase server");
                    this.sendToFirebase(ticketId, statusId, trimmedAccounts);

//                    if ("15".equals(statusId) || "4".equals(statusId)) {
//                        // 15 = suspended, 4 = fixed
//                        // TODO: Send also activate when ticket is suspended / fixed, send to Operator
//                        logger.logError(String.format("Ticket status: %s!! Supposed to be sending email here!", 
//                                statusId));
//
//                    } else if ("11".equals(statusId)) {
                    // the following only applies when ticket is assigned!
//                    logger.logInfo("Ticket status is assigned, send to technician!");

                    // It seems that telegram won't be used. If so, comment out
                    // the following if block.
                    //if (context != null) {
                    if (!Commons.isEmptyStrIgnoreSpaces(context)) {
                        List<TelegramRequestResult> result = Collections.EMPTY_LIST;

                        if (context.startsWith(DISPATCH_PREFIX)) { // set keyword to activate "dispatch"-ing tickets to technicians
                            context = context.substring(DISPATCH_PREFIX.length());
                            result = dispatchTicket(ticket, context);

                        } else {        
                            if (!trimmedAccounts.isEmpty()) {
                                String[] sfa = new String[trimmedAccounts.size()];
                                sfa = trimmedAccounts.toArray(sfa);
                                result = sendToSpecifiedAccountsWithoutDispatch(ticket, sfa, context);

                            } else {
                                logger.logError("No accounts available for dispatching ticket: " + ticketId);
                                // no notification error record should be present.
                                // because no fatal error and it is expected to be
                                // error if no account target (mobile numbers)
                                // available to be sent to.

                            }

                        }

                        // Now, create a notification record
                        // see entry in NOTIFICATION_RECORD table. Any result, error or not
                        // must be recorded.
                        for (TelegramRequestResult rr : result) {
                            NotificationRecord nr2 = new NotificationRecord(null,
                                    "telegram", new Timestamp(rr.getDate()), "0", null);

                            nr2.setReceiver(rr.getMobile());
                            nr2.setContent(rr.getMessage());
                            if (ticketId != null) {
                                nr2.setTicket(ticketId);
                            }
//                            nr2.setReceiverName(ticketId);

                            // from default Sms listener
                            if (rr.getStatus() == S_OK) {
                                nr2.setSucFlag("1");
                            } else {
                                nr2.setSucFlag("2");
                            }

                            if (rr.getReceiverName() != null) {
                                nr2.setReceiverName(rr.getReceiverName());
                            }

                            this.notificationManagerDAO.createNotificationRecord(nr2);
                            NotificationRecordHelper.getInstance().recordNotificationToHistory(nr2, statusId);

                        }
                    }
//
//                    String userid = ticket.getSlmUserByAssigneeId().getUserId();
//                    logger.logInfo("Assigned userID = " + userid);
//
//                    String ticketNumber = ticket.getTicketNum();
//                    User safe_user = this.userDao.getUser(userid);
//                    String mobile = safe_user.getMobile();
//
//                    logger.logInfo("ticketNumber = " + ticketNumber + " mobile = " + mobile);
//
//                    List<TelegramSubscriberVo> subscribers = getSubscribers(mobile);
//
////                        IProViewTrx pvimTx = PVIMDBConnectionFactory.getInstance().getTransaction();
////                        pvimTx.begin();
////                        
////                        Object conn = pvimTx.getTrxConnection();
////                        ITelegramSuscribersRepository subscriberRepos
////                                = TelegramRepositoryFactory.getInstance().getTelegramSubscribersRepository(conn);
////                        List<TelegramSubscriberVo> subscribers = subscriberRepos.query(
////                                new TelegramSubscribersPhoneNumSpecification(mobile));
//                    logger.logInfo("Start Enumerating subscribers: ");
//
//                    for (TelegramSubscriberVo subscriber : subscribers) {
//                        logger.logInfo("Subscriber id: " + subscriber.getSubs_id());
//
//                        long chatID = subscriber.getChat_id();
//                        logger.logInfo("chatID: " + chatID);
//
//                        String deeplink = TelegramConfig.getDeeplinkPrefix();
//                        String telegramContent = deeplink + ticketNumber;
//
//                        if (MessageSender.sendMessageAndSwallowLogs(chatID, telegramContent)) {
//                            logger.logInfo("Success sending message");
//                        } else {
//                            logger.logError("Error, cannot send message to chatID: "
//                                    + chatID + " for ticketNumber: " + ticketNumber);
//                        }
//                    }
//                    }
                }

            } catch (JMSException ex) {
                isError = true;
                logger.logError("JMSException", ex);

            } catch (InterruptedException ex) {
                isError = true;
                logger.logError("InterruptedException", ex);
                
            } catch (PvExtPersistenceException ex) {
                isError = true;
                logger.logError("PvExtPersistenceException", ex);

            } catch (Exception ex) {
                isError = true;
                logger.logError("General Exception", ex);

            }
            
            if (isError) {
                logger.logInfo("Notification error, record the failure");
                NotificationRecord nr = new NotificationRecord(null, "telegram", new Timestamp(new Date().getTime()), "0", null);
                nr.setReceiver(accounts);
                nr.setContent(context);
                if (ticketId != null) {
                    nr.setTicket(ticketId);
                }
                
                // taken from EmailMessageListener class
                nr.setSucFlag("0");
                this.notificationManagerDAO.createNotificationRecord(nr);
                NotificationRecordHelper helper = NotificationRecordHelper.getInstance();
                helper.recordNotificationToHistory(nr, statusId);
                
                // hmmm, should create ticket for notification error?
                this.ticketManager.createTicketForNotificationError();
                
            }
        }
    }

}
