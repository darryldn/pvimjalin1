/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.dni.pvim.ext.db.dao;

import id.dni.pvim.ext.db.config.PVIMDBConnectionFactory;
import id.dni.pvim.ext.db.exception.PVIMDBException;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.MapListHandler;

/**
 *
 * @author darryl.sulistyan
 */
public class TicketNotesDaoImpl implements ITicketNotesDao {

    // SELECT TICKET_ID, CREATE_TIME, NOTE FROM [pvim].[dbo].[TICKET_RECORD] where 
    // TICKET_ID=(SELECT TICKET_ID FROM pvim.dbo.TICKET WHERE TICKET_NUM='T201805212331.4' AND NOTE_SOURCE_FLAG=1)
    // TICKET_NUM is unique indexed.
    private static final String SQL_GET_TICKET_NOTES
            = "SELECT TICKET_ID, CREATE_TIME, NOTE FROM TICKET_RECORD where TICKET_ID=(SELECT TICKET_ID FROM TICKET WHERE TICKET_NUM=? AND NOTE_SOURCE_FLAG=1)";
    
    @Override
    public List<DBTicketNotesDao> getTicketNotes(String ticketNumber) throws PVIMDBException {
        
        MapListHandler handler = new MapListHandler();
        QueryRunner runner = new QueryRunner(PVIMDBConnectionFactory.getInstance().getDataSource());
        try {
            List<Map<String, Object>> result = runner.query(
                    SQL_GET_TICKET_NOTES, handler, ticketNumber);
            List<DBTicketNotesDao> ticketNotes = new ArrayList<>();
            for (Map<String, Object> mp : result) {
                DBTicketNotesDao dao = new DBTicketNotesDao();
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
                    throw new PVIMDBException("Unknown class from DB for datetime: " + o.getClass().getName());
                }
                dao.setCreateTime(time);
                
                ticketNotes.add(dao);
            }
            return ticketNotes;
        } catch (SQLException ex) {
            throw new PVIMDBException(ex);
        }
        
    }
    
}
