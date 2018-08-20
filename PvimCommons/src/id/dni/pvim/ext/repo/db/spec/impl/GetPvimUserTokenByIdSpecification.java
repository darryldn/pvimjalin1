/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.dni.pvim.ext.repo.db.spec.impl;

import id.dni.pvim.ext.repo.db.spec.ISqlSpecification;
import id.dni.pvim.ext.repo.db.vo.SlmUserTokenVo;

/**
 *
 * @author darryl.sulistyan
 */
public class GetPvimUserTokenByIdSpecification implements ISqlSpecification {

    private static final String SQL = 
            new StringBuilder()
            .append("select ")
                    .append(SlmUserTokenVo.FIELD_USER_ID).append(", ")
                    .append(SlmUserTokenVo.FIELD_MESSAGETOKEN).append(", ")
                    .append(SlmUserTokenVo.FIELD_LASTUPDATE).append(" ") 
            .append(" from ")
                    .append(SlmUserTokenVo.TABLE_NAME).append(" ")
            .append(" where ")
                    .append(SlmUserTokenVo.FIELD_USER_ID).append("=?")
            .toString();
    
    private final String id;
    public GetPvimUserTokenByIdSpecification(String id) {
        this.id = id;
    }
    
    @Override
    public String toParameterizedSqlQuery() {
        return SQL;
    }

    @Override
    public Object[] getSqlParams() {
        return new Object[]{id};
    }
    
}
