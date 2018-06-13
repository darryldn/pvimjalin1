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
import com.wn.tasman.ticket.TicketManager;
import com.wn.tasman.ticket.domain.NotificationRecord;
import com.wn.tasman.ticket.domain.Ticket;
import com.wn.tasman.user.dao.UserDao;
import com.wn.tasman.user.domain.User;
import com.wn.tasman.util.NotificationRecordHelper;
import id.dni.pvim.ext.repo.exceptions.PvExtPersistenceException;
import id.dni.pvim.ext.telegram.commons.config.TelegramConfig;
import id.dni.pvim.ext.telegram.commons.sender.MessageSender;
import id.dni.pvim.ext.telegram.repo.ITelegramSuscribersRepository;
import id.dni.pvim.ext.telegram.repo.TelegramSubscribersRepository;
import id.dni.pvim.ext.telegram.repo.db.vo.TelegramSubscriberVo;
import id.dni.pvim.ext.telegram.repo.spec.TelegramSubscribersPhoneNumSpecification;
import java.util.Date;
import java.sql.Timestamp;
import java.util.List;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageListener;
import org.springframework.context.ApplicationContext;

/**
 *
 * @author darryl.sulistyan
 */
public class TelegramNotificationMessageListener implements MessageListener {

    private final Logger logger;
    private SystemVariableManager systemVariableManager;
    private NotificationManagerDAO notificationManagerDAO;
    private Integer delay;
    private TicketManager ticketManager;
    private UserDao userDao;
    
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

    @Override
    public void onMessage(Message message) {
        if (message instanceof MapMessage) {
            NotificationRecord nr = new NotificationRecord(null, "telegram", new Timestamp(new Date().getTime()), "0", null);
            try {
                String accounts = ((MapMessage)message).getString("accounts");
                String context = ((MapMessage)message).getString("context");
                String ticketId = ((MapMessage)message).getString("ticketId");
                String statusId = ((MapMessage)message).getString("ticketStatusId");
                this.logger.logInfo("accounts : " + accounts + "; ticketId : " + ticketId + "; ");
                nr.setReceiver(accounts);
                if (context.length() > 2500) {
                    context = context.substring(0, 2500);
                  }
                nr.setContent(context);
                if (ticketId != null) {
                    nr.setTicket(ticketId);
                }
                this.notificationManagerDAO.createNotificationRecord(nr);
                
                NotificationRecordHelper helper = NotificationRecordHelper.getInstance();
                helper.recordNotificationToHistory(nr, statusId);
                
                ApplicationContext appCtx = ServiceDispatch.getaCtx();
                this.userDao = (UserDao) appCtx.getBean("userDao");
                
                logger.logError("Start Telegram Notification: ");
                
                // send shit to telegram
                Ticket ticket = getTicket(ticketId);
                if (ticket == null) {
                    logger.logError("Unable to read ticket data from ticketManager");
                    
                } else {
                    logger.logError("Start working on the ticket");
                    
                    String userid = ticket.getSlmUserByAssigneeId().getUserId();
                    logger.logError("user = " + userid);
                    
                    String ticketNumber = ticket.getTicketNum();
                    User safe_user = this.userDao.getUser(userid);
                    String mobile = safe_user.getMobile();
                    
                    logger.logError("ticketNumber = " + ticketNumber + " mobile = " + mobile);
                    
                    ITelegramSuscribersRepository subscriberRepos = 
                            new TelegramSubscribersRepository();
                    List<TelegramSubscriberVo> subscribers = subscriberRepos.query(
                            new TelegramSubscribersPhoneNumSpecification(mobile));
                    
                    logger.logError("Start Enumerating subscribers: ");
                    
                    for (TelegramSubscriberVo subscriber : subscribers) {
                        logger.logError("Subscriber id: " + subscriber.getSubs_id());
                        
                        long chatID = subscriber.getChat_id();
                        logger.logError("chatID: " + chatID);
                        
                        String deeplink = TelegramConfig.getDeeplinkPrefix();
                        String telegramContent = deeplink + ticketNumber;
                        
                        if (MessageSender.sendMessageAndSwallowLogs(chatID, telegramContent)) {
                            logger.logError("Success sending message");
                        } else {
                            logger.logError("Error, cannot send message to chatID: " + 
                                    chatID + " for ticketNumber: " + ticketNumber);
                        }
                    }
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
