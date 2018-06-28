/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.dni.pvim.ext.repo.db.spec.impl;

import id.dni.pvim.ext.repo.db.spec.ISqlSpecification;

/**
 *
 * @author darryl.sulistyan
 */
public class AllDevicesGpsBasedataSpecification implements ISqlSpecification {

    private static final String SQL_QUERY = 
            "SELECT id" +
            "      ,idtype" +
            "      ,reference" +
            "      ,value" + // latitude = 999916, longitude = 999915
            "  FROM basedata where idtype=0 and reference in (999915, 999916)";
    
    @Override
    public String toParameterizedSqlQuery() {
        return SQL_QUERY;
    }

    @Override
    public Object[] getSqlParams() {
        return null;
    }
    
}
