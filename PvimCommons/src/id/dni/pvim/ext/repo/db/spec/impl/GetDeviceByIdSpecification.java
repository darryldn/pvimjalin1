/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.dni.pvim.ext.repo.db.spec.impl;

import id.dni.pvim.ext.repo.db.spec.ISqlSpecification;
import id.dni.pvim.ext.repo.db.vo.DeviceVo;

/**
 *
 * @author darryl.sulistyan
 */
public class GetDeviceByIdSpecification implements ISqlSpecification {

    private static final String SQL = 
            new StringBuilder()
            .append("select ")
                    .append(DeviceVo.FIELD_DEVICEID).append(" ")
            .append(" from ")
                    .append(DeviceVo.TABLE_NAME).append(" ")
            .append(" where ")
                    .append(DeviceVo.FIELD_DEVICEID).append("=?")
            .toString();
    
    private final String deviceId;
    
    public GetDeviceByIdSpecification(String deviceId) {
        this.deviceId = deviceId;
    }
    
    @Override
    public String toParameterizedSqlQuery() {
        return SQL;
    }

    @Override
    public Object[] getSqlParams() {
        return new Object[]{deviceId};
    }
    
}
