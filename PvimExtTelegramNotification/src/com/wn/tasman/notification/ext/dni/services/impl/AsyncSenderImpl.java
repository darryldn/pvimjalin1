/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

// the package must begin with com.wn.tasman
// to take advantage of automatic spring scanning of Pvim.ear
package com.wn.tasman.notification.ext.dni.services.impl;

import com.wn.tasman.notification.ext.dni.services.AsyncSender;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;

/**
 *
 * @author darryl.sulistyan
 */
@Service
public class AsyncSenderImpl implements AsyncSender {
    
    @Override
    @Async
    public <T> Future<T> doAsync(Callable<T> callable) {
        T ret = null;
        try {
            ret = callable.call();
        } catch (Exception ex) {
            Logger.getLogger(AsyncSenderImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return new AsyncResult<>(ret);
    }
    
//    @Override
//    @Asynchronous
//    public Future<TelegramRequestResult> doAsync(String phoneNum, long chatID, String telegramContent) {
//        
//        // TODO: Parallelize this!
//        if (MessageSender.sendMessageAndSwallowLogs(chatID, telegramContent)) {
//            logger.logInfo("Success sending message");
//            
//            return new AsyncResult<>(new TelegramRequestResult(phoneNum, chatID, telegramContent, S_OK));
//        } else {
//            logger.logError("Error, cannot send message to chatID: "
//                    + chatID + " for message: " + messageContent);
//            result.add(new TelegramRequestResult(phoneNum, chatID, messageContent, E_ERR));
//        }
//        
//    }
    
}
