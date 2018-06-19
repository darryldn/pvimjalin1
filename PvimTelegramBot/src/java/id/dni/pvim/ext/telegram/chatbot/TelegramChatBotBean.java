/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.dni.pvim.ext.telegram.chatbot;

import id.dni.pvim.ext.repo.exceptions.PvExtPersistenceException;
import id.dni.pvim.ext.server.net.RequestSenderBeanLocal;
import id.dni.pvim.ext.telegram.pojo.TelegramMessageChatPOJO;
import id.dni.pvim.ext.telegram.pojo.TelegramMessageContentPOJO;
import id.dni.pvim.ext.telegram.pojo.TelegramUpdateObjPOJO;
import id.dni.pvim.ext.telegram.repo.ISlmUserRepository;
import id.dni.pvim.ext.telegram.repo.ITelegramSuscribersRepository;
import id.dni.pvim.ext.telegram.repo.SlmUserRepository;
import id.dni.pvim.ext.telegram.repo.TelegramSubscribersRepository;
import id.dni.pvim.ext.telegram.repo.db.vo.TelegramSubscriberVo;
import id.dni.pvim.ext.telegram.repo.spec.TelegramSubscribersByChatIDSpec;
import id.dni.pvim.ext.telegram.repo.spec.TelegramSubscribersPhoneNumSpecification;
import id.dni.pvim.ext.web.in.Commons;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;

/**
 *
 * @author darryl.sulistyan
 */
@Stateless
public class TelegramChatBotBean implements TelegramChatBotBeanLocal {

    @Resource
    private SessionContext ctx;

    @EJB
    private RequestSenderBeanLocal telegramRequestBean;
    
    private final ITelegramSuscribersRepository subscriberRepo = new TelegramSubscribersRepository();
    private final ISlmUserRepository slmUserRepo = new SlmUserRepository();

    private String getUsage() {
        StringBuilder sb = new StringBuilder();
        sb.append("Commands:");
        sb.append("/help: Print this text");
        sb.append("/reg mobile-number: Register your phone number for notifications");
        sb.append("/unreg: Unregister phone number");
        return sb.toString();
    }

    private String genPassKey() {
        StringBuilder sb = new StringBuilder();
        Random r = new Random();
        for (int i = 0; i < 6; ++i) {
            sb.append(r.nextInt(10));
        }
        return sb.toString();
    }

    private static boolean eq(Object a, Object b) {
        if (a == null && b == null) {
            return true;
        }

        if (a == null || b == null) {
            return false;
        }

        return a.equals(b);
    }
    
    private void sendTelegramMessage(long chatID, String message) {
        telegramRequestBean.asyncSendTelegramReply(chatID, message);
    }

    private void sendTelegramMessage(
            long chatID, boolean isSuccess, String successMessage, String failMessage) {

        if (isSuccess) {
            //MessageSender.sendMessageAndSwallowLogs(chatID, successMessage);
            telegramRequestBean.asyncSendTelegramReply(chatID, successMessage);

        } else {
            //MessageSender.sendMessageAndSwallowLogs(chatID, failMessage);
            telegramRequestBean.asyncSendTelegramReply(chatID, failMessage);

        }

    }

    @Override
    public void consume(TelegramUpdateObjPOJO updateObj) {
        if (updateObj == null) {
            return;
        }

        TelegramMessageContentPOJO content = updateObj.getMessage();
        if (content == null) {
            return;
        }

        TelegramMessageChatPOJO user = content.getChat();

        long id = user.getId();
        String text = content.getText();

        // Don't link against name here.
        // it is possible that the user already installed Telegram with different
        // name registered from ProView.
        if (Commons.isEmptyStrIgnoreSpaces(text)) {
            return;
        }
        
        long chatDate = content.getDate();
        chatDate *= 1000; // transform to ms
        
        // commands:
        // /help --> print usage
        // /reg (phone number)
        // else, ignore
        String[] commands = text.split(" +"); // split for one or more spaces
        if (commands.length >= 2) {
            String cmd = commands[0].toLowerCase();
            if ("/reg".equals(cmd)) {

                String probablePhoneNum = commands[1];
                if (Commons.isEmptyStrIgnoreSpaces(probablePhoneNum)) {
                    sendTelegramMessage(id, "No phone number given");

                } else {

                    try {
                        // Find the phone number in ProViewIM.
                        // If not registered, send error updateObj saying no phone number found
                        boolean isMobileExist;
                        ISlmUserRepository userRepo = slmUserRepo;
                        isMobileExist = userRepo.isMobileExist(probablePhoneNum);

                        if (isMobileExist) {

                            String phoneNum = probablePhoneNum;
                            long chatID = id;
                            ITelegramSuscribersRepository repos = subscriberRepo;

                            // modify the code to enforce 1-1 relationship between phone num and chat id
                            TelegramSubscriberVo existingPhone = repos.querySingleResult(
                                    new TelegramSubscribersPhoneNumSpecification(phoneNum));
                            if (existingPhone != null) {
                                if (chatID != existingPhone.getChat_id()/* && chatDate > existingPhone.getLastupdate()*/) {
                                    // this shouldn't happen because Telegram also
                                    // associates mobile number with chat id
                                    // Different machine with same mobile with obtain same chat id as well
                                    // from Telegram.
                                    
                                    // new ChatID is found with same phone number. Update entry in DB
                                    existingPhone.setChat_id(chatID);
                                    existingPhone.setLastupdate(chatDate);
                                    boolean ok = repos.update(existingPhone);
                                    sendTelegramMessage(chatID, ok, "Update chatID successful",
                                            "Update chatID failed, try again later");
                                    
                                }
                                // entry exists in DB, no need to do anything

                            } else {
                                existingPhone = repos.querySingleResult(
                                        new TelegramSubscribersByChatIDSpec(chatID));
                                if (existingPhone != null) {
                                    if (!eq(existingPhone.getPhone_num(), phoneNum)/* && chatDate > existingPhone.getLastupdate()*/) {
                                        // new phone Number for existing chatID. Update entry in DB
                                        existingPhone.setPhone_num(phoneNum);
                                        existingPhone.setLastupdate(chatDate);
                                        boolean ok = repos.update(existingPhone);
                                        sendTelegramMessage(chatID, ok, "Update mobile number successful",
                                                "Update mobile number failed, try again later");
                                        
                                    }
                                } else {
                                    
                                    // completely new entry
                                    TelegramSubscriberVo newSub = new TelegramSubscriberVo();
                                    newSub.setChat_id(chatID);
                                    //newSub.setLastupdate(System.currentTimeMillis());
                                    newSub.setLastupdate(chatDate);
                                    newSub.setPhone_num(phoneNum);
                                    newSub.setSubs_id(Commons.randomUUID());
                                    newSub.setPasskey(genPassKey());
                                    boolean ok = repos.insert(newSub);
//                                    sendTelegramMessage(chatID, ok,
//                                            String.format(
//                                                    "Registration successful with number %s! Your passKey is %s",
//                                                    probablePhoneNum, newSub.getPasskey()),
//                                            "Registration failed. Please try again later");
                                    sendTelegramMessage(chatID, ok,
                                            String.format(
                                                    "Registration successful with number %s!",
                                                    probablePhoneNum),
                                            "Registration failed. Please try again later");

                                }
                            }
                            
                        } else {
                            sendTelegramMessage(id, "Registration failed. The mobile number is not found in PVIM database");
                            
                        }

                    } catch (PvExtPersistenceException ex) {
                        Logger.getLogger(TelegramChatBotBean.class.getName()).log(Level.SEVERE, "Database error", ex);
                        sendTelegramMessage(id, "Registration failed. Contact system administrator for details");
                        ctx.setRollbackOnly();

                    }
                }

            }

        } else {

            if (commands.length == 1) {
                String cmd = commands[0].toLowerCase();
                if ("/help".equals(cmd)) {
                    sendTelegramMessage(id, getUsage());

                } else if ("/unreg".equals(cmd)) {
                    
                    try {
                        ITelegramSuscribersRepository repo = subscriberRepo;
                        TelegramSubscriberVo subs = repo.querySingleResult(new TelegramSubscribersByChatIDSpec(id));
                        if (subs != null) {
                            boolean isSuccess = repo.delete(subs);
                            if (isSuccess) {
                                sendTelegramMessage(id, "Unregistration successful!");
                            } else {
                                sendTelegramMessage(id, "Unregistration failed!");
                            }
                        }
                        
                    } catch (PvExtPersistenceException ex) {
                        Logger.getLogger(TelegramChatBotBean.class.getName()).log(Level.SEVERE, null, ex);
                        sendTelegramMessage(id, "Registration failed. Contact system administrator for details");
                        ctx.setRollbackOnly();
                    }
                    
                }
            }
        }

    }

}
