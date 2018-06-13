/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.dni.pvim.ext.telegram.repo;

import id.dni.pvim.ext.repo.ISpecification;
import id.dni.pvim.ext.repo.db.spec.ISqlSpecification;
import id.dni.pvim.ext.repo.exceptions.PvExtPersistenceException;
import id.dni.pvim.ext.telegram.repo.db.vo.NewlyAssignedTicketVo;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.MapListHandler;

/**
 *
 * @author darryl.sulistyan
 */
class NewlyAssignedTicketsRepository implements INewlyAssignedTicketRepository {

    private final DataSource ds;
    
    // TODO: for simulation!
    private final SimNewAstckRepo sim;
    
    public NewlyAssignedTicketsRepository(DataSource ds) {
        this.ds = ds;
        sim = new SimNewAstckRepo();
    }
    
    @Override
    public List<NewlyAssignedTicketVo> query(ISpecification specification) throws PvExtPersistenceException {
        return sim.query(specification);
    }
    
    private List<NewlyAssignedTicketVo> _query(ISpecification specification) throws PvExtPersistenceException {
        
        ISqlSpecification sqlSpec = (ISqlSpecification) specification;
        String sql = sqlSpec.toParameterizedSqlQuery();
        Object[] params = sqlSpec.getSqlParams();
        
        MapListHandler handler = new MapListHandler();
        QueryRunner runner = new QueryRunner(ds);
        
        try {
            List<Map<String, Object>> result;
            if (params == null || params.length == 0) {
                result = runner.query(sql, handler);
            } else {
                result = runner.query(sql, handler, params);
            }
            List<NewlyAssignedTicketVo> list = new ArrayList<>();
            for (Map<String, Object> mp : result) {
                NewlyAssignedTicketVo vo = new NewlyAssignedTicketVo();
                vo.setTicket_id((String) mp.get("TICKET_ID"));
                vo.setTicket_number((String) mp.get("TICKET_NUMBER"));
                vo.setChat_id(Long.parseLong((String) mp.get("CHAT_ID")));
                list.add(vo);
            }
            
            return list;
        } catch (SQLException ex) {
            throw new PvExtPersistenceException(ex);
        }
        
    }
    
}
