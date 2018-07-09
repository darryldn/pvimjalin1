/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.dni.pvim.ext.repo.db.vo;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author darryl.sulistyan
 */
public class DeviceVo extends GenericVo {

    public static final String TABLE_NAME = "DEVICE";
    public static final String 
            FIELD_DEVICEID = "DEVICEID";
    
    private final Map<String, FieldData> tbl;
    public DeviceVo() {
        tbl = new HashMap<>();
        tbl.put(FIELD_DEVICEID, new FieldData.Builder().setFieldName(FIELD_DEVICEID).setPartOfPk(true).build());
    }
    
    @Override
    public String getTableName() {
        return TABLE_NAME;
    }

    @Override
    public Map<String, FieldData> getFieldDescriptor() {
        return tbl;
    }
    
    public String getDeviceID() {
        return (String) tbl.get(FIELD_DEVICEID).getValue();
    }
    
}
