/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.dni.pvim.ext.telegram.repo.db.vo;

import id.dni.pvim.ext.repo.db.vo.FieldData;
import id.dni.pvim.ext.repo.db.vo.ITableDescriptorVo;
import id.dni.pvim.ext.repo.exceptions.PvExtPersistenceException;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

/**
 *-- for check whether notification for ticket ID T has been sent
create table PVIM_EXT_TICKET_NOTIFIED (
	ticket_id varchar(32) not null,
	lastupdate datetime not null,
	constraint pk_tcn_0 primary key (ticket_id)
);
 * @author darryl.sulistyan
 */
public class NotifiedTicketsVo implements ITableDescriptorVo {

    private static final String TABLE_NAME = "PVIM_EXT_TICKET_NOTIFIED";
    private static final String FN_TICKET_ID = "TICKET_ID";
    private static final String FN_LASTUPDATE = "LASTUPDATE";
    
    private final Map<String, FieldData> tbl;
    public NotifiedTicketsVo() {
        tbl = new HashMap<>();
        tbl.put(FN_TICKET_ID, new FieldData.Builder().setFieldName(FN_TICKET_ID).setPartOfPk(true).build());
        tbl.put(FN_LASTUPDATE, new FieldData.Builder().setFieldName(FN_LASTUPDATE).build());
    }
    
    @Override
    public String getTableName() {
        return TABLE_NAME;
    }

    @Override
    public Map<String, FieldData> getFieldDescriptor() {
        return tbl;
    }

    @Override
    public void fillDataFromMap(Map<String, Object> fromDB) throws PvExtPersistenceException {
        for (Map.Entry<String, Object> k : fromDB.entrySet()) {
            tbl.get(k.getKey()).setValue(k.getValue());
        }
    }
    
    public String getTicketID() {
        return (String) tbl.get(FN_TICKET_ID).getValue();
    }
    
    public void setTicketID(String ticketID) {
        tbl.get(FN_TICKET_ID).setValue(ticketID);
    }
    
    public long getLastupdated() {
        return ((Timestamp)tbl.get(FN_LASTUPDATE).getValue()).getTime();
    }
    
    public void setLastupdated(long time) {
        tbl.get(FN_LASTUPDATE).setValue(new Timestamp(time));
    }
    
    public void setLastupdated() {
        setLastupdated(System.currentTimeMillis());
    }
    
}
