/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.dni.pvim.ext.repo.db.spec.impl;

import id.dni.pvim.ext.repo.db.spec.ISqlSpecification;
import id.dni.pvim.ext.repo.db.vo.SlmLocationVo;

/**
 *
 * @author darryl.sulistyan
 */
public class GetPvimEngineerLocationByIdSpecification implements ISqlSpecification {
    
    private static final String SQL = 
            new StringBuilder()
            .append("select ")
                    .append(SlmLocationVo.FIELD_ENGINEER_ID).append(", ")
                    .append(SlmLocationVo.FIELD_LASTUPDATE).append(", ")
                    .append(SlmLocationVo.FIELD_LATITUDE).append(", ") 
                    .append(SlmLocationVo.FIELD_LONGITUDE).append(", ")
                    .append(SlmLocationVo.FIELD_NOTES).append(" ")
            .append(" from ")
                    .append(SlmLocationVo.TABLE_NAME).append(" ")
            .append(" where ")
                    .append(SlmLocationVo.FIELD_ENGINEER_ID).append("=?")
            .toString();
    
    private final String id;
    public GetPvimEngineerLocationByIdSpecification(String id) {
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
