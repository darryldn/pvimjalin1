/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.dni.pvim.ext.db.dao;

import id.dni.pvim.ext.db.exception.PVIMDBException;
import java.util.List;

/**
 *
 * @author darryl.sulistyan
 */
public interface ITicketNotesDao {
    
    public List<DBTicketNotesDao> getTicketNotes(String ticketNumber) throws PVIMDBException;
    
}
