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
 *create table [pvim].[dbo].[PVIM_EXT_USERTOKEN] (
	userid varchar(128) not null,
	msgtoken varchar(1024) not null,
	lastupdate datetime not null,
	constraint pk_usertoken primary key (userid)
);
 * @author darryl.sulistyan
 */
public class SlmUserTokenVo extends GenericVo {

    public static final String TABLE_NAME = "PVIM_EXT_USERTOKEN";
    public static final String 
            FIELD_USER_ID = "userid",
            FIELD_MESSAGETOKEN = "msgtoken",
            FIELD_LASTUPDATE = "lastupdate";
    
    private final Map<String, FieldData> tbl;
    public SlmUserTokenVo() {
        tbl = new HashMap<>();
        tbl.put(FIELD_USER_ID, new FieldData.Builder().setFieldName(FIELD_USER_ID).setPartOfPk(true).build());
        tbl.put(FIELD_MESSAGETOKEN, new FieldData.Builder().setFieldName(FIELD_MESSAGETOKEN).build());
        tbl.put(FIELD_LASTUPDATE, new FieldData.Builder().setFieldName(FIELD_LASTUPDATE).build());
    }
    
    @Override
    public String getTableName() {
        return TABLE_NAME;
    }

    @Override
    public Map<String, FieldData> getFieldDescriptor() {
        return tbl;
    }
    
    public String getUserID() {
        return (String) tbl.get(FIELD_USER_ID).getValue();
    }
    
    public void setUserID(String id) {
        tbl.get(FIELD_USER_ID).setValue(id);
    }
    
    public String getMessageToken() {
        return (String) tbl.get(FIELD_MESSAGETOKEN).getValue();
    }
    
    public void setMessageToken(String id) {
        tbl.get(FIELD_MESSAGETOKEN).setValue(id);
    }
    
    public long getLastUpdated() {
        Object o = tbl.get(FIELD_LASTUPDATE).getValue();
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
    
    public void setLastUpdated(long date) {
        tbl.get(FIELD_LASTUPDATE).setValue(new Timestamp(date));
    }
    
}
