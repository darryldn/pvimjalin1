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
public class SlmUserVo extends GenericVo {

    public static final String TABLE_NAME = "SLM_USER";
    public static final String 
            FIELD_USER_ID = "USER_ID",
            FIELD_MOBILE = "MOBILE",
            FIELD_EMAIL = "EMAIL",
            FIELD_USER_TYPE = "USER_TYPE",
            FIELD_LOGIN_NAME = "LOGIN_NAME",
            FIELD_LOCKED = "LOCKED";
            
    
    private final Map<String, FieldData> tbl;
    public SlmUserVo() {
        tbl = new HashMap<>();
        tbl.put(FIELD_USER_ID, new FieldData.Builder().setFieldName(FIELD_USER_ID).setPartOfPk(true).build());
        tbl.put(FIELD_MOBILE, new FieldData.Builder().setFieldName(FIELD_MOBILE).build());
        tbl.put(FIELD_EMAIL, new FieldData.Builder().setFieldName(FIELD_EMAIL).build());
        tbl.put(FIELD_USER_TYPE, new FieldData.Builder().setFieldName(FIELD_USER_TYPE).build());
        tbl.put(FIELD_LOGIN_NAME, new FieldData.Builder().setFieldName(FIELD_LOGIN_NAME).build());
        tbl.put(FIELD_LOCKED, new FieldData.Builder().setFieldName(FIELD_LOCKED).build());
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
    
    public String getMobile() {
        return (String) tbl.get(FIELD_MOBILE).getValue();
    }
    
    public String getEmail() {
        return (String) tbl.get(FIELD_EMAIL).getValue();
    }
    
    public String getUserType() {
        return (String) tbl.get(FIELD_USER_TYPE).getValue();
    }
    
    public String getLoginName() {
        return (String) tbl.get(FIELD_LOGIN_NAME).getValue();
    }
    
    public String getLocked() {
        return (String) tbl.get(FIELD_LOCKED).getValue();
    }
    
//    public void setMobile(String mobile) {
//        tbl.get(FIELD_MOBILE).setValue(mobile);
//    }
    
}
