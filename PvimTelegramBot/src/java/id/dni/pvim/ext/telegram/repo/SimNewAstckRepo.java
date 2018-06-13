/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.dni.pvim.ext.telegram.repo;

import id.dni.pvim.ext.repo.ISpecification;
import id.dni.pvim.ext.repo.exceptions.PvExtPersistenceException;
import id.dni.pvim.ext.telegram.repo.db.vo.NewlyAssignedTicketVo;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author darryl.sulistyan
 */
class SimNewAstckRepo implements INewlyAssignedTicketRepository {

    @Override
    public List<NewlyAssignedTicketVo> query(ISpecification specification) throws PvExtPersistenceException {
        List<NewlyAssignedTicketVo> l = new ArrayList<>();
        
        for (int i=0; i<5; ++i) {
            NewlyAssignedTicketVo vo = new NewlyAssignedTicketVo();
            vo.setChat_id(10000+i);
            vo.setTicket_number("TICKET_NUM_" + i);
            vo.setTicket_id("TICKET_ID_" + i);
            l.add(vo);
        }
        
        return l;
    }
    
}
