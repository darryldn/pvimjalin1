/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.dni.pvim.ext.repo.boot;

import id.dni.pvim.ext.repo.db.IDBMachineBasedataRepository;
import id.dni.pvim.ext.db.config.PVIMDBConnectionFactory;
import id.dni.pvim.ext.repo.db.ISlmUserRepository;
import id.dni.pvim.ext.repo.db.ITicketRepository;
import id.dni.pvim.ext.repo.db.TicketDBRepositoryImpl;
import id.dni.pvim.ext.repo.impl.DBMachineBasedataRepository;
import id.dni.pvim.ext.repo.db.TicketNotesDBRepositoryImpl;
import id.dni.pvim.ext.repo.impl.SlmUserRepository;
import java.sql.Connection;

/**
 *
 * @author darryl.sulistyan
 */
public class RepositoryFactory {
    
    private final static RepositoryFactory INSTANCE = new RepositoryFactory();
    
    private RepositoryFactory() {
        
    }
    
    public static RepositoryFactory getInstance() {
        return INSTANCE;
    }
    
    public ISlmUserRepository getSlmUserRepository(Object trxObject) {
        return new SlmUserRepository((Connection) trxObject);
    }
    
    public ITicketRepository getTicketRepository(Object trxObject) {
        return new TicketDBRepositoryImpl((Connection) trxObject);
    }
    
    public ITicketNotesRepository getTicketNotesRepository() {
        return new TicketNotesDBRepositoryImpl(PVIMDBConnectionFactory.getInstance().getDataSource());
    }
    
    public IDBMachineBasedataRepository getMachineBasedataRepository() {
        return new DBMachineBasedataRepository();
    }
    
//    public IMachineGpsDao getMachineGpsDao() {
//        return new MachineGpsDBDaoImpl();
//    }
    
}
