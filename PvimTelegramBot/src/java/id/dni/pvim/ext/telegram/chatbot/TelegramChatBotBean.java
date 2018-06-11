/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.dni.pvim.ext.telegram.chatbot;

import id.dni.pvim.ext.repo.exceptions.PvExtPersistenceException;
import id.dni.pvim.ext.telegram.commons.MessageSender;
import id.dni.pvim.ext.telegram.pojo.TelegramMessageChatPOJO;
import id.dni.pvim.ext.telegram.pojo.TelegramMessageContentPOJO;
import id.dni.pvim.ext.telegram.pojo.TelegramMessagePOJO;
import id.dni.pvim.ext.telegram.repo.ITelegramSuscribersRepository;
import id.dni.pvim.ext.telegram.repo.TelegramSubscribersRepository;
import id.dni.pvim.ext.telegram.repo.db.vo.TelegramSubscriberVo;
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
                    
                    // save chat to DB
                    TelegramSubscriberVo subscriber = new TelegramSubscriberVo();
                    subscriber.setChat_id(id);
                    subscriber.setLastupdate(System.currentTimeMillis());
                    subscriber.setPhone_num(probablePhoneNum);
                    subscriber.setSubs_id(Commons.randomUUID());
                    
                    String passKey = genPassKey();
                    subscriber.setPasskey(passKey);
                    ITelegramSuscribersRepository repos = new TelegramSubscribersRepository();
                    try {
                        boolean isSuccess = repos.insert(subscriber);
                        if (!isSuccess) {
                            MessageSender.sendMessageAndSwallowLogs(id, "Registration failed. Please try again later");
                            
                        } else {
                            MessageSender.sendMessageAndSwallowLogs(id, String.format(
                                    "Registration successful with number %s! Your passKey is %s", 
                                    probablePhoneNum, passKey));
                            
                        }
                        
                    } catch (PvExtPersistenceException ex) {
                        Logger.getLogger(TelegramChatBotBean.class.getName()).log(Level.SEVERE, null, ex);
                        MessageSender.sendMessageAndSwallowLogs(id, "Registration failed. Error message: " + ex.getMessage());
                        
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
