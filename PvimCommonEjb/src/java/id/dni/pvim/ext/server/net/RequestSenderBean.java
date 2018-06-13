/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.dni.pvim.ext.server.net;

import id.dni.pvim.ext.telegram.commons.sender.MessageSender;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.ejb.AccessTimeout;
import javax.ejb.Asynchronous;
import javax.ejb.Lock;
import static javax.ejb.LockType.READ;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;

/**
 *
 * @author darryl.sulistyan
 */
@Stateless
public class RequestSenderBean implements RequestSenderBeanLocal {
    
    @Resource
    private SessionContext ctx;
    
//    @Asynchronous
//    @Override
//    @Lock(READ)
//    @AccessTimeout(-1)
//    public void asyncFireAndForget() {
//        
//    }
    
    @Asynchronous
    @Override
    public void asyncSendTelegramReply(long chatID, String message) {
        try {
            MessageSender.sendMessageAndSwallowLogs(chatID, message);
        } catch (Exception ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            ctx.setRollbackOnly();
        }
    }
}
