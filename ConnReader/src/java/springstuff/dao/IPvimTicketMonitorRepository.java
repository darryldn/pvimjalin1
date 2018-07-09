/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package springstuff.dao;

import id.dni.pvim.ext.repo.exceptions.PvExtPersistenceException;
import java.util.List;
import springstuff.model.PvimTicketVo;

/**
 * Repository to monitor whether remote server ticket in firebase, or whatever
 * depending on the impl, is ok or not.
 * 
 * If local server cannot send to remote server, it marks error.
 * @author darryl.sulistyan
 */
public interface IPvimTicketMonitorRepository {
    
    /**
     * include insert
     * @param ticket
     * @throws PvExtPersistenceException 
     */
    public void updateTicket(PvimTicketVo ticket) throws PvExtPersistenceException;
    
    public void removeTicket(PvimTicketVo ticket) throws PvExtPersistenceException;
    
    public PvimTicketVo getTicket(String ticketNumber) throws PvExtPersistenceException;
    
    public List<PvimTicketVo> getTickets(List<String> ticketNumbers) throws PvExtPersistenceException;
    
    public List<PvimTicketVo> getAllTickets() throws PvExtPersistenceException;
    
}
