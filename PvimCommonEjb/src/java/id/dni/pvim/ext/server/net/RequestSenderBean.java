/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.dni.pvim.ext.server.net;

import javax.ejb.AccessTimeout;
import javax.ejb.Asynchronous;
import javax.ejb.Lock;
import static javax.ejb.LockType.READ;
import javax.ejb.Stateless;

/**
 *
 * @author darryl.sulistyan
 */
@Stateless
public class RequestSenderBean implements RequestSenderBeanLocal {
    
    @Asynchronous
    @Override
    @Lock(READ)
    @AccessTimeout(-1)
    public void asyncFireAndForget() {
        
    }
}
