/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.dni.pvim.ext.repo.db.spec.impl;

import id.dni.pvim.ext.repo.db.spec.ISqlSpecification;

/**
 * grabs data from basedata table in proview for latitude and longitude
 * @author darryl.sulistyan
 */
public class DeviceGpsBasedataSpecification implements ISqlSpecification {

    private static final String SQL_QUERY = 
            "SELECT id" +
            "      ,idtype" +
            "      ,reference" +
            "      ,value" + // latitude = 999916, longitude = 999915
            "  FROM basedata where id=? and idtype=0 and reference in (999915, 999916)";
    private final String deviceID;
    
    public DeviceGpsBasedataSpecification(String deviceID) {
        this.deviceID = deviceID;
    }
    
    @Override
    public String toParameterizedSqlQuery() {
        return SQL_QUERY;
    }

    @Override
    public Object[] getSqlParams() {
        return (Object[])(new String[]{deviceID});
    }
    
}
