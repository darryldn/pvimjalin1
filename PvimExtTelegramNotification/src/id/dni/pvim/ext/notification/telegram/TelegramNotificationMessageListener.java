/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.dni.pvim.ext.notification.telegram;

import com.rab.core.server.ServiceDispatch;
import com.rab.core.util.Logger;
import com.wn.tasman.basedata.SystemVariableManager;
import com.wn.tasman.notification.dao.NotificationManagerDAO;
import com.wn.tasman.notification.ext.dni.services.AsyncSender;
import com.wn.tasman.ticket.TicketManager;
import com.wn.tasman.ticket.domain.NotificationRecord;
import com.wn.tasman.ticket.domain.Ticket;
import com.wn.tasman.user.dao.UserDao;
import com.wn.tasman.user.domain.User;
import com.wn.tasman.util.NotificationRecordHelper;
import id.dni.pvim.ext.db.config.PVIMDBConnectionFactory;
import id.dni.pvim.ext.db.trx.IProViewTrx;
import id.dni.pvim.ext.repo.exceptions.PvExtPersistenceException;
import id.dni.pvim.ext.telegram.commons.config.TelegramConfig;
import id.dni.pvim.ext.telegram.commons.sender.MessageSender;
import id.dni.pvim.ext.telegram.repo.ITelegramSuscribersRepository;
import id.dni.pvim.ext.telegram.repo.TelegramRepositoryFactory;
import id.dni.pvim.ext.telegram.repo.db.vo.TelegramSubscriberVo;
import id.dni.pvim.ext.telegram.repo.spec.TelegramSubscribersListOfPhonesSpec;
import id.dni.pvim.ext.web.in.Commons;
import java.util.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.orm.hibernate3.HibernateTemplate;

/**
 *
 * @author darryl.sulistyan
 */
public class TelegramNotificationMessageListener implements MessageListener {

//    @EJB
    @Autowired
    private AsyncSender asyncSenderService;
    
    public void setAsyncSender(AsyncSender s) {
        this.asyncSenderService = s;
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

    private synchronized UserDao getUserDao() {
        if (this.userDao == null) {
            ApplicationContext appCtx = ServiceDispatch.getaCtx();
            this.userDao = (UserDao) appCtx.getBean("userDao");
        }
        return this.userDao;
    }

    private synchronized HibernateTemplate getPvimHibernateTemplate() {
        if (this.pvimHibernateTemplate == null) {
            ApplicationContext appCtx = ServiceDispatch.getaCtx();
            this.pvimHibernateTemplate = ((HibernateTemplate) appCtx.getBean("pvimHibernateTemplate"));
        }
        return this.pvimHibernateTemplate;
    }
    
    private List<TelegramSubscriberVo> getSubscribers(String mobile) throws PvExtPersistenceException {
        return getSubscribers(new String[]{mobile});
    }

    private List<TelegramSubscriberVo> getSubscribers(String[] mobile) throws PvExtPersistenceException {
        IProViewTrx pvimTx = PVIMDBConnectionFactory.getInstance().getTransaction();
        try {
            pvimTx.begin();

            Object conn = pvimTx.getTrxConnection();
            ITelegramSuscribersRepository subscriberRepos;
            
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
                        logger.logInfo("Success sending message to chatID: " + 
                                chatID + " for message: " + telegramContent + " mobile: " + finalSubs.getPhone_num());
                        return (new TelegramRequestResult(finalSubs.getPhone_num(), chatID, telegramContent, S_OK));
                    } else {
                        logger.logError("Error, cannot send message to chatID: " +
                                chatID + " for message: " + telegramContent + " mobile: " + finalSubs.getPhone_num());
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
    
    private List<TelegramRequestResult> dispatchTicket(Ticket ticket, String messageContent) throws PvExtPersistenceException {
        String userid = ticket.getSlmUserByAssigneeId().getUserId();
                    logger.logInfo("Assigned userID = " + userid);

//        String ticketNumber = ticket.getTicketNum();
        User safe_user = this.userDao.getUser(userid);
        String mobile = safe_user.getMobile();

        return sendToSpecifiedAccountsWithoutDispatch(ticket, new String[]{mobile}, messageContent);
        
//        logger.logInfo("ticketNumber = " + ticketNumber + " mobile = " + mobile);
//        List<TelegramSubscriberVo> subscribers = getSubscribers(mobile);
//        
//        logger.logInfo("Start Enumerating subscribers: ");
//        String telegramContent = getTelegramMessage(ticketNumber, messageContent);
//        dispatchTicketToSubscribers(subscribers, telegramContent);
    }

    private List<TelegramRequestResult> sendToSpecifiedAccountsWithoutDispatch(Ticket ticket, String[] accounts, String messageContent) 
            throws PvExtPersistenceException {
        
        String ticketNumber = ticket.getTicketNum();
        List<TelegramSubscriberVo> subs = getSubscribers(accounts);
        String telegramContent = getTelegramMessage(ticketNumber, messageContent);
        return dispatchTicketToSubscribers(subs, telegramContent);
        
    }
    
    @Override
    public void onMessage(Message message) {
        if (message instanceof MapMessage) {
            logger.logInfo("asyncSenderService: " + this.asyncSenderService); // asyncSender is NOT NULL!!
            // injection successful!
            
//            NotificationRecord nr = new NotificationRecord(null, "telegram", new Timestamp(new Date().getTime()), "0", null);
            try {
                String accounts = ((MapMessage) message).getString("accounts");
                String context = ((MapMessage) message).getString("context");
                String ticketId = ((MapMessage) message).getString("ticketId");
                String statusId = ((MapMessage) message).getString("ticketStatusId");
                this.logger.logInfo("accounts : " + accounts + "; ticketId : " + ticketId + "; ");
//                nr.setReceiver(accounts);
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

                } else {
                    logger.logInfo("Start working on the ticket");

//                    if ("15".equals(statusId) || "4".equals(statusId)) {
//                        // 15 = suspended, 4 = fixed
//                        // TODO: Send also activate when ticket is suspended / fixed, send to Operator
//                        logger.logError(String.format("Ticket status: %s!! Supposed to be sending email here!", 
//                                statusId));
//
//                    } else if ("11".equals(statusId)) {
                    // the following only applies when ticket is assigned!
//                    logger.logInfo("Ticket status is assigned, send to technician!");

                    if (context != null) {
                        List<TelegramRequestResult> result = Collections.EMPTY_LIST;
                        
                        if (context.startsWith(DISPATCH_PREFIX)) { // set keyword to activate "dispatch"-ing tickets to technicians
                            context = context.substring(DISPATCH_PREFIX.length());
                            result = dispatchTicket(ticket, context);
                            
                        } else {
                            String[] listAccounts = accounts.split(",");
                            List<String> trimmedAccounts = new ArrayList<>();
                            for (int i=0; i<listAccounts.length; ++i) {
                                if (!Commons.isEmptyStrIgnoreSpaces(listAccounts[i])) {
                                    trimmedAccounts.add(listAccounts[i].trim());
                                }
                            }
                            if (!trimmedAccounts.isEmpty()) {
                                String[] sfa = new String[trimmedAccounts.size()];
                                sfa = trimmedAccounts.toArray(sfa);
                                result = sendToSpecifiedAccountsWithoutDispatch(ticket, sfa, context);
                                
                            } else {
                                logger.logError("No accounts available for dispatching ticket: " + ticketId);
                                
                            }
                            
                        }
                        
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
                logger.logError("JMSException", ex);

            } catch (InterruptedException ex) {
                logger.logError("InterruptedException", ex);

            } catch (PvExtPersistenceException ex) {
                logger.logError("PvExtPersistenceException", ex);

            } catch (Exception ex) {
                logger.logError("General Exception", ex);

            }
        }
    }

}
