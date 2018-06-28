/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package springstuff.dao.spec;

import id.dni.pvim.ext.repo.db.spec.ISqlSpecification;
import springstuff.model.ComponentStateVo;

/**
 *
 * @author darryl.sulistyan
 */
public class GetComponentByDeviceIdSpec implements ISqlSpecification {

     private static final String SQL = 
            new StringBuilder()
                    .append("select ")
                    .append(ComponentStateVo.FIELD_DEVICEID).append(",")
                    .append(ComponentStateVo.FIELD_COMPONENT).append(",")
                    .append(ComponentStateVo.FIELD_COMPONENTSTATE).append(",")
                    .append(ComponentStateVo.FIELD_LATITUDE).append(",")
                    .append(ComponentStateVo.FIELD_LONGITUDE).append(" ")
                    .append(" from ").append(ComponentStateVo.TABLE_NAME)
                    .append(" where ").append(ComponentStateVo.FIELD_DEVICEID).append("=?")
//                    .append(" where ").append(ComponentStateVo.FIELD_COMPONENT).append(" is not null ")
                    .toString();
    private final String deviceID;
    
    public GetComponentByDeviceIdSpec(String deviceID) {
        this.deviceID = deviceID;
    }
    
    @Override
    public String toParameterizedSqlQuery() {
        return SQL;
    }

    @Override
    public Object[] getSqlParams() {
        return new Object[]{deviceID};
    }
    
}
