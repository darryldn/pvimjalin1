/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package springstuff.service.impl;

import id.dni.pvim.ext.telegram.commons.sender.MessageSender;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import springstuff.service.TelegramMessageSenderService;

/**
 *
 * @author darryl.sulistyan
 */
@Service
public class TelegramMessageSenderServiceImpl implements TelegramMessageSenderService {

    @Override
    @Async
    public void asyncSendTelegramReply(long chatID, String message) {
        try {
            MessageSender.sendMessageAndSwallowLogs(chatID, message);
        } catch (Exception ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
