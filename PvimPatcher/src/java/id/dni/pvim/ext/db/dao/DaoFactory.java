/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.dni.pvim.ext.db.dao;

/**
 *
 * @author darryl.sulistyan
 */
public class DaoFactory {
    
    private final static DaoFactory INSTANCE = new DaoFactory();
    
    private DaoFactory() {
        
    }
    
    public static DaoFactory getInstance() {
        return INSTANCE;
    }
    
    public ITicketNotesDao getTicketNotesDao() {
        return new TicketNotesDaoImpl();
    }
    
    public IMachineGpsDao getMachineGpsDao() {
        return new MachineGpsDaoImpl();
    }
    
}
