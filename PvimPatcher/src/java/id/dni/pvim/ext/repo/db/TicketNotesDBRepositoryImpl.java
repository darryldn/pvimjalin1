/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.dni.pvim.ext.repo.db;

import id.dni.pvim.ext.repo.db.spec.ISqlSpecification;
import id.dni.pvim.ext.db.vo.DBTicketNotesVo;
import id.dni.pvim.ext.repo.exceptions.PvExtPersistenceException;
import id.dni.pvim.ext.repo.ISpecification;
import id.dni.pvim.ext.repo.spec.TicketNotesSpecification;
import id.dni.pvim.ext.repo.spec.TicketNotesWithParentSpecification;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.MapListHandler;
import id.dni.pvim.ext.repo.boot.ITicketNotesRepository;

/**
 *
 * @author darryl.sulistyan
 */
public class TicketNotesDBRepositoryImpl implements ITicketNotesRepository {

    private final DataSource dataSource;
    
    public TicketNotesDBRepositoryImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public List<DBTicketNotesVo> getTicketNotes(String ticketNumber) throws PvExtPersistenceException {
        return query(new TicketNotesSpecification(ticketNumber));
    }

    @Override
    public List<DBTicketNotesVo> getTicketNotesWithParent(String ticketNumber) throws PvExtPersistenceException {
        return query(new TicketNotesWithParentSpecification(ticketNumber));
    }

    @Override
    public List<DBTicketNotesVo> query(ISpecification specification) throws PvExtPersistenceException {
        ISqlSpecification sqlSpec = (ISqlSpecification) specification;
        String sql = sqlSpec.toParameterizedSqlQuery();
        Object[] params = sqlSpec.getSqlParams();
        
        MapListHandler handler = new MapListHandler();
        QueryRunner runner = new QueryRunner(dataSource);
        try {
            List<Map<String, Object>> result = runner.query(sql, handler, (Object[]) params);
            List<DBTicketNotesVo> ticketNotes = new ArrayList<>();
            for (Map<String, Object> mp : result) {
                DBTicketNotesVo dao = new DBTicketNotesVo();
                dao.setTicketID((String) mp.get("TICKET_ID"));
                dao.setNotes((String) mp.get("NOTE"));
                
                Object o = mp.get("CREATE_TIME");
                long time = 0;
                if (o instanceof java.sql.Timestamp) {
                    Timestamp t = (Timestamp) o;
                    time = t.getTime();
                } else if (o instanceof java.sql.Date) {
                    Date d = (Date) o;
                    time = d.getTime();
                } else {
                    throw new PvExtPersistenceException("Unknown class from DB for datetime: " + o.getClass().getName());
                }
                dao.setCreateTime(time);
                
                ticketNotes.add(dao);
            }
            return ticketNotes;
        } catch (SQLException ex) {
            throw new PvExtPersistenceException(ex);
        }
    }
    
}
