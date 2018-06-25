/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package springstuff.service;

import id.dni.pvim.ext.repo.exceptions.PvExtPersistenceException;
import id.dni.pvim.ext.telegram.pojo.TelegramUpdateObjPOJO;

/**
 *
 * @author darryl.sulistyan
 */
public interface TelegramChatService {
    
    public void consume(TelegramUpdateObjPOJO message) throws PvExtPersistenceException;
    
}
