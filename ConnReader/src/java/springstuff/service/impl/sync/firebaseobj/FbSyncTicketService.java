/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package springstuff.service.impl.sync.firebaseobj;

import id.dni.ext.web.ws.obj.firebase.FbTicketDto;
import id.dni.pvim.ext.repo.db.ITicketRepository;
import id.dni.pvim.ext.repo.db.spec.impl.GetTicketByNumberSpecification;
import id.dni.pvim.ext.repo.exceptions.PvExtPersistenceException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import springstuff.service.FirebaseDatabaseObjSynchronizerService;

/**
 *
 * @author darryl.sulistyan
 */
@Service
@Qualifier("FbSyncTicketService")
public class FbSyncTicketService implements FirebaseDatabaseObjSynchronizerService {

    private String path;
    private ITicketRepository repo;
    
    @Value("${firebase.database.ticket.root}")
    public void setPath(String path) {
        this.path = path;
    }
    
    @Autowired
    public void setTicketRepo(ITicketRepository repo) {
        this.repo = repo;
    }
    
    @Override
    public boolean deleteObj(Object obj) {
        if (obj instanceof FbTicketDto) {
            FbTicketDto ticket = (FbTicketDto) obj;
            String ticketNumber = ticket.getTicketNumber();
            try {
                List l = this.repo.query(new GetTicketByNumberSpecification(ticketNumber)); 
                return l == null || l.isEmpty(); // remove if entry not found in DB.
            } catch (PvExtPersistenceException ex) {
                Logger.getLogger(FbSyncATMsService.class.getName()).log(Level.SEVERE, null, ex);
                return false;
            }
        }
        return false;
    }

    @Override
    public String getRootPath() {
        return this.path;
    }

    @Override
    public Class<?> getClassObj() {
        return FbTicketDto.class;
    }
    
}
