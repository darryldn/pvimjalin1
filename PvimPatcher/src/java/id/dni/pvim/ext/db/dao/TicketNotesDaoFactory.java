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
public class TicketNotesDaoFactory {
    
    private final static TicketNotesDaoFactory INSTANCE = new TicketNotesDaoFactory();
    
    private TicketNotesDaoFactory() {
        
    }
    
    public static TicketNotesDaoFactory getInstance() {
        return INSTANCE;
    }
    
    public ITicketNotesDao getDao() {
        return new TicketNotesDaoImpl();
    }
    
}
