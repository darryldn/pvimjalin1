/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.dni.pvim.ext.repo.db.vo;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

/**
create table [pvim].[dbo].[PVIM_EXT_FELOC] (
	engineer_id varchar(128),
	lastupdate datetime not null,
	latitude decimal(18,10),
	longitude decimal(18,10),
	notes varchar(2048),
	constraint pk_feloc primary key (engineer_id)
);
 * @author darryl.sulistyan
 */
public class SlmLocationVo extends GenericVo {

    public static final String TABLE_NAME = "PVIM_EXT_FELOC";
    public static final String 
            FIELD_ENGINEER_ID = "engineer_id",
            FIELD_LASTUPDATE = "lastupdate",
            FIELD_LATITUDE = "latitude",
            FIELD_LONGITUDE = "longitude",
            FIELD_NOTES = "notes";
    
    private final Map<String, FieldData> tbl;
    public SlmLocationVo() {
        tbl = new HashMap<>();
        tbl.put(FIELD_ENGINEER_ID, new FieldData.Builder().setFieldName(FIELD_ENGINEER_ID).setPartOfPk(true).build());
        tbl.put(FIELD_LASTUPDATE, new FieldData.Builder().setFieldName(FIELD_LASTUPDATE).build());
        tbl.put(FIELD_LATITUDE, new FieldData.Builder().setFieldName(FIELD_LATITUDE).build());
        tbl.put(FIELD_LONGITUDE, new FieldData.Builder().setFieldName(FIELD_LONGITUDE).build());
        tbl.put(FIELD_NOTES, new FieldData.Builder().setFieldName(FIELD_NOTES).build());
    }
    
    @Override
    public String getTableName() {
        return TABLE_NAME;
    }

    @Override
    public Map<String, FieldData> getFieldDescriptor() {
        return tbl;
    }
    
    public String getEngineerID() {
        return (String) tbl.get(FIELD_ENGINEER_ID).getValue();
    }
    
    public void setEngineerID(String id) {
        tbl.get(FIELD_ENGINEER_ID).setValue(id);
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
    
    private BigDecimal convert(Object o) {
        if (o instanceof BigDecimal) {
            return (BigDecimal) o;
        } else if (o instanceof Double) {
            Double d = (Double) o;
            return new BigDecimal(d);
        }
        throw new ClassCastException("Cannot cast " + 
                o.getClass().getName() + " to " + 
                BigDecimal.class.getName() + " or " + 
                Double.class.getName());
    }
    
    public BigDecimal getLatitude() {
        Object o = tbl.get(FIELD_LATITUDE).getValue();
        return convert(o);
    }
    
    public void setLatitude(double latitude) {
        tbl.get(FIELD_LATITUDE).setValue(new BigDecimal(latitude));
    }
    
    public BigDecimal getLongitude() {
        Object o = tbl.get(FIELD_LONGITUDE).getValue();
        return convert(o);
    }
    
    public void setLongitude(double longitude) {
        tbl.get(FIELD_LONGITUDE).setValue(new BigDecimal(longitude));
    }
    
    public String getNotes() {
        return (String) tbl.get(FIELD_NOTES).getValue();
    }
    
    public void setNotes(String notes) {
        tbl.get(FIELD_NOTES).setValue(notes);
    }
}
