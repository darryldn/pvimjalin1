/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package springstuff.model;

import id.dni.pvim.ext.repo.db.vo.FieldData;
import id.dni.pvim.ext.repo.db.vo.GenericVo;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author darryl.sulistyan
 */
public class ComponentStateVo extends GenericVo {

    public static final String TABLE_NAME = "v_device_fail",
            FIELD_DEVICEID = "deviceid",
            FIELD_COMPONENTID = "componentid",
            FIELD_COMPONENT = "messagetext",
            FIELD_COMPONENTSTATE = "devicefailstate",
            FIELD_DEVICEDESCR = "devicedescr",
            FIELD_LATITUDE = "latitude",
            FIELD_LONGITUDE = "longitude";
    
    private final Map<String, FieldData> tbl;
    public ComponentStateVo () {
        tbl = new HashMap<>();
        tbl.put(FIELD_DEVICEID, new FieldData.Builder().setFieldName(FIELD_DEVICEID).build());
        tbl.put(FIELD_COMPONENT, new FieldData.Builder().setFieldName(FIELD_COMPONENT).build());
        tbl.put(FIELD_COMPONENTID, new FieldData.Builder().setFieldName(FIELD_COMPONENTID).build());
        tbl.put(FIELD_COMPONENTSTATE, new FieldData.Builder().setFieldName(FIELD_COMPONENTSTATE).build());
        tbl.put(FIELD_LATITUDE, new FieldData.Builder().setFieldName(FIELD_LATITUDE).build());
        tbl.put(FIELD_LONGITUDE, new FieldData.Builder().setFieldName(FIELD_LONGITUDE).build());
        tbl.put(FIELD_DEVICEDESCR, new FieldData.Builder().setFieldName(FIELD_DEVICEDESCR).build());
    }
    
    @Override
    public String getTableName() {
        return TABLE_NAME;
    }

    @Override
    public Map<String, FieldData> getFieldDescriptor() {
        return tbl;
    }
    
    private String getString(String field) {
        Object o = tbl.get(field).getValue();
        if (o == null) return null;
        return o.toString();
    }
    
    public String getDeviceID() {
        return (String) tbl.get(FIELD_DEVICEID).getValue();
    }
    
    public String getComponent() {
        return getString(FIELD_COMPONENT);
    }
    
    public String getComponentState() {
        return getString(FIELD_COMPONENTSTATE);
    }
    
    public String getLatitude() {
        return (String) tbl.get(FIELD_LATITUDE).getValue();
    }
    
    public String getLongitude() {
        return (String) tbl.get(FIELD_LONGITUDE).getValue();
    }
    
    public String getComponentID() {
        return getString(FIELD_COMPONENTID);
    }
    
    public String getDeviceDescr() {
        return getString(FIELD_DEVICEDESCR);
    }
    
}
