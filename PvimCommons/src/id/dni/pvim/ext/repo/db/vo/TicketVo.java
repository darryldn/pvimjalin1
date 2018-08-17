/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.dni.pvim.ext.repo.db.vo;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author darryl.sulistyan
 */
public class TicketVo extends GenericVo {

    public static final String TABLE_NAME = "TICKET",
            FIELD_TICKETID = "ticket_id",
            FIELD_TICKETNUMBER = "ticket_num",
            FIELD_LASTUPDATED = "last_updated",
            FIELD_ASSIGNEEID = "ASSIGNEE_ID";
    
    private final Map<String, FieldData> tbl;
    public TicketVo () {
        tbl = new HashMap<>();
        tbl.put(FIELD_TICKETID, new FieldData.Builder().setFieldName(FIELD_TICKETID).setPartOfPk(true).build());
        tbl.put(FIELD_TICKETNUMBER, new FieldData.Builder().setFieldName(FIELD_TICKETNUMBER).build());
        tbl.put(FIELD_LASTUPDATED, new FieldData.Builder().setFieldName(FIELD_LASTUPDATED).build());
        tbl.put(FIELD_ASSIGNEEID, new FieldData.Builder().setFieldName(FIELD_ASSIGNEEID).build());
    }
    
    
    @Override
    public String getTableName() {
        return TABLE_NAME;
    }

    @Override
    public Map<String, FieldData> getFieldDescriptor() {
        return tbl;
    }
    
    public String geTicketID() {
        return (String) tbl.get(FIELD_TICKETID).getValue();
    }
    
    public String getTicketNumber() {
        return (String) tbl.get(FIELD_TICKETNUMBER).getValue();
    }
    
    public String getAssigneeID() {
        return (String) tbl.get(FIELD_ASSIGNEEID).getValue();
    }
    
    public long getLastUpdated() {
        Object o = tbl.get(FIELD_LASTUPDATED).getValue();
        if (o instanceof Timestamp) {
            return ((Timestamp) o).getTime();
        } else if (o instanceof Date) {
            return ((Date) o).getTime();
        }
        throw new ClassCastException("Cannot cast " + 
                o.getClass().getName() + " to " + 
                Timestamp.class.getName() + " or " + 
                Date.class.getName());
    }
    
}
