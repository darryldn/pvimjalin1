/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.dni.pvim.ext.repo.boot;

import id.dni.pvim.ext.db.vo.DBTicketNotesVo;
import id.dni.pvim.ext.repo.exceptions.PvExtPersistenceException;
import id.dni.pvim.ext.repo.IRepository;
import java.util.List;

/**
 *
 * @author darryl.sulistyan
 */
public interface ITicketNotesRepository extends IRepository<DBTicketNotesVo> {
    
    public List<DBTicketNotesVo> getTicketNotes(String ticketNumber) throws PvExtPersistenceException;
    
    public List<DBTicketNotesVo> getTicketNotesWithParent(String ticketNumber) throws PvExtPersistenceException;
    
}
