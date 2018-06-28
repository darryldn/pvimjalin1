/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.dni.pvim.ext.repo.boot;

import id.dni.pvim.ext.repo.db.IDBMachineBasedataRepository;
import id.dni.pvim.ext.db.config.PVIMDBConnectionFactory;
import id.dni.pvim.ext.repo.impl.DBMachineBasedataRepository;
import id.dni.pvim.ext.repo.db.TicketNotesDBRepositoryImpl;

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
