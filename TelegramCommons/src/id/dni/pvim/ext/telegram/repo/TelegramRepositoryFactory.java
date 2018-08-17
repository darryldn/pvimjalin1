/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.dni.pvim.ext.telegram.repo;

import id.dni.pvim.ext.repo.db.ISlmUserRepository;
import id.dni.pvim.ext.repo.impl.SlmUserRepository;
import id.dni.pvim.ext.telegram.repo.impl.TelegramSubscribersRepository;
import java.sql.Connection;

/**
 *
 * @author darryl.sulistyan
 */
public class TelegramRepositoryFactory {
    
    private static final TelegramRepositoryFactory INSTANCE = new TelegramRepositoryFactory();
    
    private TelegramRepositoryFactory() {
        
    }
    
    public static TelegramRepositoryFactory getInstance() {
        return INSTANCE;
    }
    
    public ISlmUserRepository getSlmUserRepository(Object trxConnection) {
        return new SlmUserRepository((Connection) trxConnection);
    }
    
    public ITelegramSuscribersRepository getTelegramSubscribersRepository(Object trxConnection) {
        return new TelegramSubscribersRepository((Connection) trxConnection);
    }
    
}
