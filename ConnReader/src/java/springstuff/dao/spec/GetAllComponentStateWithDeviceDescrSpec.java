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
public class GetAllComponentStateWithDeviceDescrSpec implements ISqlSpecification {
    
    private static final String SQL = 
            new StringBuilder()
                    .append("select ")
                    .append("f.").append(ComponentStateVo.FIELD_DEVICEID).append(",")
                    .append("p.").append(ComponentStateVo.FIELD_DEVICEDESCR).append(",")
                    .append("f.").append(ComponentStateVo.FIELD_COMPONENT).append(",")
                    .append("f.").append(ComponentStateVo.FIELD_COMPONENTSTATE).append(",")
                    .append("f.").append(ComponentStateVo.FIELD_LATITUDE).append(",")
                    .append("f.").append(ComponentStateVo.FIELD_LONGITUDE).append(" ")
                    .append(" from ").append(ComponentStateVo.TABLE_NAME).append(" f, deviceprofile p")
                    .append(" where ").append("f.devicetype = p.devicetype")
//                    .append(" where ").append(ComponentStateVo.FIELD_COMPONENT).append(" is not null ")
                    .toString();
    
    @Override
    public String toParameterizedSqlQuery() {
        return SQL;
    }

    @Override
    public Object[] getSqlParams() {
        return null;
    }
    
}
