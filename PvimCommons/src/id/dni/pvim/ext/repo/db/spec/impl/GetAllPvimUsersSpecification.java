/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.dni.pvim.ext.repo.db.spec.impl;

import id.dni.pvim.ext.repo.db.spec.ISqlSpecification;
import id.dni.pvim.ext.repo.db.vo.SlmUserVo;

/**
 *
 * @author darryl.sulistyan
 */
public class GetAllPvimUsersSpecification implements ISqlSpecification {

    private static final String SQL = 
            new StringBuilder()
            .append("select ")
                    .append(SlmUserVo.FIELD_USER_ID).append(", ")
                    .append(SlmUserVo.FIELD_USER_TYPE).append(", ")
                    .append(SlmUserVo.FIELD_MOBILE).append(",") 
                    .append(SlmUserVo.FIELD_LOGIN_NAME).append(",") 
                    .append(SlmUserVo.FIELD_EMAIL).append(", ")
                    .append(SlmUserVo.FIELD_LOCKED).append(" ")
            .append(" from ")
                    .append(SlmUserVo.TABLE_NAME).append(" ")
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
