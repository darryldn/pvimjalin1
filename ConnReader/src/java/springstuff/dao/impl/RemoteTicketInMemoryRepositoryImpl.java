/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package springstuff.dao.impl;

import id.dni.pvim.ext.repo.exceptions.PvExtPersistenceException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import springstuff.model.PvimTicketVo;
import springstuff.dao.IPvimTicketMonitorRepository;

/**
 *
 * @author darryl.sulistyan
 */
@Service
@Qualifier("InMemoryRepository")
public class RemoteTicketInMemoryRepositoryImpl implements IPvimTicketMonitorRepository {

    private Map<String, PvimTicketVo> inMemoryDB;
    
    @PostConstruct
    public void init() {
        inMemoryDB = new ConcurrentHashMap<>();
    }
    
    @Override
    public void updateTicket(PvimTicketVo ticket) throws PvExtPersistenceException {
        Logger.getLogger(this.getClass().getName()).log(Level.INFO, "udpateTicket(" + ticket + ")");
        if (ticket == null) {
            return;
        }
//        inMemoryDB.put(ticket.getTicketNumber(), ticket);
        inMemoryDB.put(ticket.getTicketId(), ticket);
    }

    @Override
    public void removeTicket(PvimTicketVo ticket) throws PvExtPersistenceException {
        if (ticket == null) {
            return;
        }
//        inMemoryDB.remove(ticket.getTicketNumber());
        inMemoryDB.remove(ticket.getTicketId());
    }

    @Override
    public List<PvimTicketVo> getTickets(List<String> ticketNumbers) throws PvExtPersistenceException {
        List<PvimTicketVo> result = new ArrayList<>();
        for (String tn : ticketNumbers) {
            if (inMemoryDB.containsKey(tn)) {
                result.add(inMemoryDB.get(tn));
            }
        }
        return result;
    }

    @Override
    public List<PvimTicketVo> getAllTickets() throws PvExtPersistenceException {
        return new ArrayList<>(inMemoryDB.values());
    }

    @Override
    public PvimTicketVo getTicket(String ticketNumber) throws PvExtPersistenceException {
        Logger.getLogger(this.getClass().getName()).log(Level.INFO, "getTicket(" + ticketNumber + ")");
        if (ticketNumber == null) {
            return null;
        }
        return inMemoryDB.get(ticketNumber);
    }
    
}
