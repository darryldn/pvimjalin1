/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
// use com.wn.* package because the logging is open there. 
// see tasman-client.jar#rab-log4j.xml file for more details
package com.wn.tasman.id.dni.pvim.ext.notification.telegram;

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
import com.wn.tasman.util.NotificationRecordHelper;
import com.wn.tasman.util.SLMUtil;
import id.dni.pvim.ext.jmsmsg.JmsMsgConstants;
import id.dni.pvim.ext.net.RemoteMessagingResult;
import id.dni.pvim.ext.net.SendTicketRemoteResponseJson;
import id.dni.pvim.ext.net.TransferTicketDto;
import id.dni.pvim.ext.repo.exceptions.PvExtPersistenceException;
import id.dni.pvim.ext.web.in.Commons;
import java.sql.SQLException;
import java.util.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
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
     *
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

    private SendTicketRemoteResponseJson sendToFirebaseNoHibernate(final String ticketId, final String statusId,
            final List<String> accounts, final String context) {

        TicketDto ticketDto = new TicketDto(); // dummy data
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
        trs.setContext(context);

        SendTicketRemoteResponseJson sendSync = firebaseService.sendSync(trs);
        return sendSync;
    }

    /**
     * Function to send the ticket to firebase when onMessage is notified
     *
     * @param ticketId
     * @param statusId
     */
//    private void sendToFirebase(final String ticketId, final String statusId,
//            final List<String> accounts, final String context) {
//        if (this.firebaseService != null) {
//            if (this.pvimHibernateTemplate != null) {
//                this.pvimHibernateTemplate.execute(new HibernateCallback() {
//                    @Override
//                    public Void doInHibernate(org.hibernate.Session session)
//                            throws org.hibernate.HibernateException, SQLException {
//
//                        StringBuilder accountStrSb = new StringBuilder();
//                        for (String a : accounts) {
//                            accountStrSb.append(a).append(",");
//                        }
//                        String accountStr = accountStrSb.toString();
//
//                        // in order to fully utilize the hibernate, one should re-obtain
//                        // the objects via hibernate session.
//                        // using the objects directly (for example, from Ticket object from onMessage function)
//                        // can cause HibernateException no session error.
//                        // Ripped from EmailMessageListener.class
////                        Ticket ticket = (Ticket) session.get(Ticket.class, ticketId);
////                        if (ticket == null) {
////                            try {
////                                Thread.sleep(1000L * TelegramNotificationMessageListener.this.delay);
////                            } catch (InterruptedException ignore) {
////                            }
////                            ticket = (Ticket) session.get(Ticket.class, ticketId);
////                        }
//                        // ticketMap is used back then to communicate to Firebase
//                        // To update Firebase DB, it is required to obtain ticket info
//                        // we no longer need to do so, because sendFirebase only sends
//                        // notification in DMZ because Firebase is scrapped.
//                        // Ticket is no longer needed
//                        //
////                        if (ticket != null) {
////                            Ticket parent = ticket.getParentTicket();
////                            List<NotePair> an = new ArrayList<>();
////                            if (parent != null) {
////                                an.addAll(grabNotes(parent.getTicketRecords()));
////                            }
////                            an.addAll(grabNotes(ticket.getTicketRecords()));
////                            Collections.sort(an);
////                            StringBuilder notes = new StringBuilder();
////                            for (NotePair np : an) {
////                                notes.append(np.note).append(System.lineSeparator());
////                            }
////                            TicketDto ticketDto = SLMUtil.convertTicketToTicketDto(ticket);
////                            ticketDto.setNote(notes.toString());
////                            long lastupdated = ticket.getLastUpdated().getTime();
//                        TicketDto ticketDto = new TicketDto(); // dummy data
//                        String modifCtx = context;
//                        if (context.startsWith(JmsMsgConstants.DISPATCH_PREFIX)) {
//                            modifCtx = "pvim " + context; // if dispatch_prefix is used, the DMZ will send to assignee.
//                            // so, modify the message so it won't happen
//                        }
//
//                        long lastupdated = System.currentTimeMillis(); // For transferticketdto
//                        // the issue is for reminders, no ticket updates are issued.
//
//                        // Map has to be used instead of TicketDto because TicketDto                                               
//                        // class from rab-server.jar is used in Spring IoC context
//                        // and using it as parameter in our service (FirebaseService) can cause
//                        // some issues on classloading.
//                        Map<String, Object> ticketMap = TicketUtil.ticketDto2Map(ticketDto);
//
//                        TransferTicketDto trs = new TransferTicketDto(ticketId);
//                        trs.setLastupdated(lastupdated);
//                        trs.setTicketMap(ticketMap);
//                        trs.setAccountList(accounts);
//                        trs.setContext(modifCtx);
//
//                        SendTicketRemoteResponseJson sendSync = firebaseService.sendSync(trs);
//                        if (sendSync.getErr() != null) {
//                            createFatalNotificationRecord(accountStr, modifCtx, ticketId, statusId);
//                        } else {
//                            List<RemoteMessagingResult> rl = sendSync.getResult();
//                            if (rl == null) {
//                                rl = Collections.EMPTY_LIST;
//                            }
//                            for (RemoteMessagingResult result : rl) {
//                                createNotificationRecordOnResult(ticketId, statusId, result);
//                            }
//                        }
//
////                            if ("7".equals(statusId)) { // ticket in Deleted state
////                                firebaseService.remove(trs);
////                            } else {
////                                firebaseService.send(trs);
////                            }
////                        } else {
////                            logger.logError("Unable to obtain ticket after delay!");
////                            createFatalNotificationRecord(accountStr, context, ticketId, statusId);
////                        }
//                        return null;
//                    }
//
//                });
//
//            } else {
//                logger.logError("No pvimHibernateTemplate object!");
//            }
//
//        } else {
//            logger.logInfo("Firebase service not registered");
//        }
//    }

    static List<String> extractAccounts(String accounts, Set<String> ignoredAccounts) {
        String[] listAccounts = accounts.split(",");
        List<String> trimmedAccounts = new ArrayList<>();
        for (int i = 0; i < listAccounts.length; ++i) {
            if (!Commons.isEmptyStrIgnoreSpaces(listAccounts[i])) {
                String account = listAccounts[i].trim();
                if (!ignoredAccounts.contains(account)) {
                    trimmedAccounts.add(listAccounts[i].trim());
                }
            }
        }
        return trimmedAccounts;
    }

    private void createFatalNotificationRecord(String accounts, String context, String ticketId, String statusId) {
        logger.logInfo("Notification error, record the failure");
        NotificationRecord nr = new NotificationRecord(null, "tlg-FCM", new Timestamp(new Date().getTime()), "0", null);
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
    }

    private void createNotificationRecordOnResult(String ticketId, String statusId, RemoteMessagingResult rr) {
        NotificationRecord nr2 = new NotificationRecord(null,
                rr.getSource(), new Timestamp(rr.getDate()), "0", null);

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
            nr2.setSucFlag("0");
        }

        if (rr.getReceiverName() != null) {
            nr2.setReceiverName(rr.getReceiverName());
        }

        this.notificationManagerDAO.createNotificationRecord(nr2);
        NotificationRecordHelper.getInstance().recordNotificationToHistory(nr2, statusId);
    }

    static String joinStr(List<String> l, String sep) {
        if (l == null || l.isEmpty()) {
            return "";
        }
        String s = l.get(0);
        StringBuilder sb = new StringBuilder(s);
        int ls = l.size();
        for (int i = 1; i < ls; ++i) {
            sb.append(sep).append(l.get(i));
        }
        return sb.toString();
    }

    private static final class WorkerCallable implements Callable<Object> {

        private final TelegramNotificationMessageListener owner;
        private final NotificationRequest message;

        public WorkerCallable(TelegramNotificationMessageListener owner, NotificationRequest message) {
            this.owner = owner;
            this.message = message;
        }

        @Override
        public Object call() throws Exception {
            owner.logger.logInfo(">> call()");
            owner.__onMessageInternal(message);
            owner.logger.logInfo("<< call(): returns NULL");
            return null;
        }

    }

    @Override
    public void onMessage(Message message) {

        if (message instanceof MapMessage) {
            this.logger.logInfo(">> onMessage()");
            
            String accounts = "",
                    context = "",
                    ticketId = "",
                    statusId = "";

            try {
                accounts = ((MapMessage) message).getString("accounts");
                context = ((MapMessage) message).getString("context");
                ticketId = ((MapMessage) message).getString("ticketId");
                statusId = ((MapMessage) message).getString("ticketStatusId");
                
                NotificationRequest notificationRequest = new NotificationRequest();
                notificationRequest.setAccounts(accounts);
                notificationRequest.setContext(context);
                notificationRequest.setStatusId(statusId);
                notificationRequest.setTicketId(ticketId);
                
                this.logger.logInfo(" - Dispatching the work to async service");
                if (this.asyncSenderService != null) {                
                    this.asyncSenderService.doAsync(new WorkerCallable(this, notificationRequest));
                } else {
                    this.logger.logWarning(" - async sender service is not available, resort to normal sync call");
                    __onMessageInternal(notificationRequest);
                }

            } catch (Exception ex) {
                this.logger.logError(ex);
                createFatalNotificationRecord(accounts, context, ticketId, statusId);
                this.ticketManager.createTicketForNotificationError();
                
            }

            this.logger.logInfo("<< onMessage()");
        }
    }

//    @Override
    private void __onMessageInternal(NotificationRequest message) {
        
        long startExec = System.currentTimeMillis();
        
//            logger.logInfo("asyncSenderService: " + this.asyncSenderService); // asyncSender is NOT NULL!!
//            boolean isError = false;
        String accounts = null, context = null, ticketId = null, statusId = null;
        
        // keeping track of accounts that has been successfully notified
        // so they won't need to be retried
        Set<String> successfulAccounts = new HashSet<>();
        
        int retryTotal = 2;
        boolean mustRetry;
        this.logger.logInfo(" - number of possible retries: " + (retryTotal - 1));
        
        for (int i = 0; i < retryTotal; ++i) {
            this.logger.logInfo(" - Attempt " + (i + 1) + " START");

            String failedAccounts = "";
            mustRetry = false;

            try {
                accounts = message.getAccounts();
                context = message.getContext();
                ticketId = message.getTicketId();
                statusId = message.getStatusId();

//                    accounts = ((MapMessage) message).getString("accounts");
//                    context = ((MapMessage) message).getString("context");
//                    ticketId = ((MapMessage) message).getString("ticketId");
//                    statusId = ((MapMessage) message).getString("ticketStatusId");
                this.logger.logInfo(" - accounts : " + accounts + "; ticketId : " + ticketId + "; ");
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

                this.logger.logInfo(" - context: " + context + " statusId: " + statusId);

                this.userDao = getUserDao();
                this.pvimHibernateTemplate = getPvimHibernateTemplate();

                List<String> trimmedAccounts = extractAccounts(accounts, successfulAccounts);

                logger.logInfo(" - Sending " + trimmedAccounts + " notification");
                //                this.sendToFirebase(ticketId, statusId, trimmedAccounts, context);

                String accountStr = joinStr(trimmedAccounts, ",");

                if (context.startsWith(JmsMsgConstants.DISPATCH_PREFIX)) {
                    logger.logWarning(" - prefix " + JmsMsgConstants.DISPATCH_PREFIX + " should not be used");
                    context = "pvim " + context; // if dispatch_prefix is used, the DMZ will send to assignee.
                    // so, modify the message so it won't happen
                }

                SendTicketRemoteResponseJson sendSync
                        = this.sendToFirebaseNoHibernate(ticketId, statusId, trimmedAccounts, context);

                if (sendSync.getErr() != null) {
                    this.logger.logError(String.format(" - Error with code=[%s] message=[%s] to accounts [%s]",
                            sendSync.getErr().getErrCode(), sendSync.getErr().getErrMsg(), accountStr));
                    mustRetry = true;
                    failedAccounts = accountStr;
//                        createFatalNotificationRecord(accountStr, context, ticketId, statusId);

                } else {
                    List<RemoteMessagingResult> rl = sendSync.getResult();
                    if (rl == null) {
                        rl = Collections.EMPTY_LIST;
                    }
                    for (RemoteMessagingResult result : rl) {
                        if (result.getStatus() == S_OK) {
                            createNotificationRecordOnResult(ticketId, statusId, result);

                            // OK, now, this mobile should not be retried
                            successfulAccounts.add(result.getMobile());
                            this.logger.logInfo(" - Successful sending to " + result.getMobile() + " via "
                                    + result.getSource());
                        } else {
                            this.logger.logInfo(" - Possible failure sending to " + result.getMobile() + " via "
                                    + result.getSource());
                        }
                    }

                    // check whether there exists accounts that are not successful
                    List<String> remainingAccounts = extractAccounts(accounts, successfulAccounts);
                    if (!remainingAccounts.isEmpty()) {
                        mustRetry = true;
                        failedAccounts = joinStr(remainingAccounts, ",");
                        this.logger.logWarning(" - Unable to send notification to the following account(s): " + failedAccounts);
                    }

                }

            } catch (Exception ex) {
                mustRetry = true;
                logger.logError("General Exception", ex);

            }

            if (!mustRetry) {
                break;
            }

            // all retries has been exhausted, but there still retries!
            // log as error
            if (i == retryTotal - 1 && mustRetry && !Commons.isEmptyStrIgnoreSpaces(failedAccounts)) {
                this.logger.logError(" - Unable to send notification to the following accounts after retries: " + failedAccounts);
                createFatalNotificationRecord(failedAccounts, context, ticketId, statusId);
                this.ticketManager.createTicketForNotificationError();

            }
        }
//        } else {
//            this.logger.logInfo("Ignoring non-MapMessages-type messages");
//        }
    
        this.logger.logInfo("Message notification completes in " + (System.currentTimeMillis() - startExec) + " ms");
    }

}
