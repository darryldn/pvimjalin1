/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.dni.pvim.ext.telegram.chatbot;

import id.dni.pvim.ext.repo.exceptions.PvExtPersistenceException;
import id.dni.pvim.ext.telegram.commons.sender.MessageSender;
import id.dni.pvim.ext.telegram.pojo.TelegramMessageChatPOJO;
import id.dni.pvim.ext.telegram.pojo.TelegramMessageContentPOJO;
import id.dni.pvim.ext.telegram.pojo.TelegramMessagePOJO;
import id.dni.pvim.ext.telegram.repo.ITelegramSuscribersRepository;
import id.dni.pvim.ext.telegram.repo.TelegramSubscribersRepository;
import id.dni.pvim.ext.telegram.repo.db.vo.TelegramSubscriberVo;
import id.dni.pvim.ext.telegram.repo.spec.TelegramSubscribersByChatIDSpec;
import id.dni.pvim.ext.telegram.repo.spec.TelegramSubscribersPhoneNumSpecification;
import id.dni.pvim.ext.web.in.Commons;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;

/**
 *
 * @author darryl.sulistyan
 */
@Stateless
public class TelegramChatBotBean implements TelegramChatBotBeanLocal {

    private String getUsage() {
        StringBuilder sb = new StringBuilder();
        sb.append("Commands:");
        sb.append("/help: Print this text");
        sb.append("/reg phone-number: Register your phone number for notifications");
        return sb.toString();
    }
    
    private String genPassKey() {
        StringBuilder sb = new StringBuilder();
        Random r = new Random();
        for (int i=0; i<6; ++i) {
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
    
    private static void sendTelegramMessage(
            long chatID, boolean isSuccess, String successMessage, String failMessage) {
        
        if (isSuccess) {
            MessageSender.sendMessageAndSwallowLogs(chatID, successMessage);

        } else {
            MessageSender.sendMessageAndSwallowLogs(chatID, failMessage);

        }
        
    }
    
    @Override
    public void consume(TelegramMessagePOJO message) {
        if (message == null) {
            return;
        }
        
        TelegramMessageContentPOJO content = message.getMessage();
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
                    MessageSender.sendMessageAndSwallowLogs(id, "No phone number given");
                    
                } else {
                    // TODO: Find the phone number in ProView.
                    // If not registered, send error message saying no phone number found
                    
                    String phoneNum = probablePhoneNum;
                    long chatID = id;
                    ITelegramSuscribersRepository repos = new TelegramSubscribersRepository();
                    
                    try {
                        // modify the code to enforce 1-1 relationship between phone num and chat id
                        TelegramSubscriberVo existingPhone = repos.querySingleResult(
                                new TelegramSubscribersPhoneNumSpecification(phoneNum));
                        if (existingPhone != null) {
                            if (chatID != existingPhone.getChat_id()) {
                                // new ChatID is found with same phone number. Update entry in DB
                                existingPhone.setChat_id(chatID);
                                existingPhone.setLastupdate(System.currentTimeMillis());
                                boolean ok = repos.update(existingPhone);
                                sendTelegramMessage(chatID, ok, "Update chatID successful", 
                                        "Update chatID failed, try again later");
                            }
                            // entry exists in DB, no need to do anything
                            
                        } else {
                            existingPhone = repos.querySingleResult(
                                new TelegramSubscribersByChatIDSpec(chatID));
                            if (existingPhone != null) {
                                if (!eq(existingPhone.getPhone_num(), phoneNum)) {
                                    // new phone Number for existing chatID. Update entry in DB
                                    existingPhone.setPhone_num(phoneNum);
                                    existingPhone.setLastupdate(System.currentTimeMillis());
                                    boolean ok = repos.update(existingPhone);
                                    sendTelegramMessage(chatID, ok, "Update mobile number successful", 
                                            "Update mobile number failed, try again later");
                                }
                            } else {
                                // completely new entry
                                TelegramSubscriberVo newSub = new TelegramSubscriberVo();
                                newSub.setChat_id(chatID);
                                newSub.setLastupdate(System.currentTimeMillis());
                                newSub.setPhone_num(phoneNum);
                                newSub.setSubs_id(Commons.randomUUID());
                                newSub.setPasskey(genPassKey());
                                boolean ok = repos.insert(newSub);
                                sendTelegramMessage(chatID, ok, 
                                        String.format(
                                            "Registration successful with number %s! Your passKey is %s", 
                                            probablePhoneNum, newSub.getPasskey()), 
                                        "Registration failed. Please try again later");
                                
                            }
                        }
                        
                    } catch (PvExtPersistenceException ex) {
                        Logger.getLogger(TelegramChatBotBean.class.getName()).log(Level.SEVERE, "Database error", ex);
                        MessageSender.sendMessageAndSwallowLogs(id, "Registration failed. Contact system administrator for details");
                        
                    }
                }
                
            }
            
        } else {
            
            if (commands.length == 1) {
                String cmd = commands[0].toLowerCase();
                if ("/help".equals(cmd)) {
                    MessageSender.sendMessageAndSwallowLogs(id, getUsage());

                }
            }
        }
        
    }
    
}
