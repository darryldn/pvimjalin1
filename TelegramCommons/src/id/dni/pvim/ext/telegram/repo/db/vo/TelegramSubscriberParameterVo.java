/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.dni.pvim.ext.telegram.repo.db.vo;

import id.dni.pvim.ext.repo.db.vo.FieldData;
import id.dni.pvim.ext.repo.db.vo.GenericVo;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

/**
create table PVIM_EXT_TELEGRAM_SUBSCRIBERS_PARAMS (
	par_name varchar(128) not null,
	par_value varchar(2048),
	lastupdate datetime not null,
	constraint pk_subs_par primary key (par_name)
);
 * @author darryl.sulistyan
 */
public class TelegramSubscriberParameterVo extends GenericVo {

    public static final String 
            TABLE_NAME = "PVIM_EXT_TELEGRAM_SUBSCRIBERS_PARAMS",
            FIELD_PAR_NAME = "PAR_NAME",
            FIELD_PAR_VALUE = "PAR_VALUE",
            FIELD_LASTUPDATE = "LASTUPDATE";
    
    private final Map<String, FieldData> tbl;
    public TelegramSubscriberParameterVo() {
        tbl = new HashMap<>();
        tbl.put(FIELD_PAR_NAME, new FieldData.Builder().setFieldName(FIELD_PAR_NAME).setPartOfPk(true).build());
        tbl.put(FIELD_PAR_VALUE, new FieldData.Builder().setFieldName(FIELD_PAR_VALUE).build());
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

//    @Override
//    public void fillDataFromMap(Map<String, Object> fromDB) throws PvExtPersistenceException {
//        for (Map.Entry<String, Object> k : fromDB.entrySet()) {
//            tbl.get(k.getKey()).setValue(k.getValue());
//        }
//    }
    
    public void setParName(String parameterName) {
        tbl.get(FIELD_PAR_NAME).setValue(parameterName);
    }
    
    public void setParValue(String parameterValue) {
        tbl.get(FIELD_PAR_VALUE).setValue(parameterValue);
    }
    
    public void setLastupdate(long lastUpdated) {
        tbl.get(FIELD_LASTUPDATE).setValue(new Timestamp(lastUpdated));
    }
    
    public void setToCurrentTimestamp() {
        setLastupdate(System.currentTimeMillis());
    }
    
    
    public String getParName() {
        return (String) tbl.get(FIELD_PAR_NAME).getValue();
    }
    
    public String getParValue() {
        return (String) tbl.get(FIELD_PAR_VALUE).getValue();
    }
    
    public long getLastupdate() {
        Object o = tbl.get(FIELD_LASTUPDATE).getValue();
        if (o instanceof Timestamp) {
            return ((Timestamp) o).getTime();
        } else if (o instanceof Date) {
            return ((Date) o).getTime();
        }
        throw new ClassCastException("Cannot cast " 
                + o.getClass().getName() + " to " + 
                Timestamp.class.getName() + " or " + Date.class.getName());
    }
    
}
