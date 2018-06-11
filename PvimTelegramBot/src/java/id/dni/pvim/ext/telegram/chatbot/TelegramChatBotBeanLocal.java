/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.dni.pvim.ext.telegram.chatbot;

import id.dni.pvim.ext.telegram.pojo.TelegramMessagePOJO;
import javax.ejb.Local;

/**
 *
 * @author darryl.sulistyan
 */
@Local
public interface TelegramChatBotBeanLocal {

    public void consume(TelegramMessagePOJO message);
    
}
