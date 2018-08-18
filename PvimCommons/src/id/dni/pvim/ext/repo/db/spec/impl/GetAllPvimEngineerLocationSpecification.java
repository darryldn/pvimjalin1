/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.dni.pvim.ext.repo.db.spec.impl;

import id.dni.pvim.ext.repo.db.pagination.IPaginator;
import id.dni.pvim.ext.repo.db.pagination.Mssql2012Paginator;
import id.dni.pvim.ext.repo.db.spec.ISqlSpecification;
import id.dni.pvim.ext.repo.db.vo.SlmLocationVo;

/**
 *
 * @author darryl.sulistyan
 */
public class GetAllPvimEngineerLocationSpecification implements ISqlSpecification {
    
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
            .toString();
    
    private final int pageSize;
    private final int pageNum;
    public GetAllPvimEngineerLocationSpecification() {
        this(-1, -1);
    }
    
    public GetAllPvimEngineerLocationSpecification(int pageSize, int pageNum) {
        this.pageNum = pageNum;
        this.pageSize = pageSize;
    }
    
    @Override
    public String toParameterizedSqlQuery() {
        if (this.pageNum > 0 && this.pageSize > 0) {
            IPaginator paginator = new Mssql2012Paginator.Builder()
                    .setSql(SQL)
                    .setOrderByColumn(SlmLocationVo.FIELD_ENGINEER_ID)
                    .setPageNumber(pageNum)
                    .setPageSize(pageSize)
                    .build();
            return paginator.getPaginatedSql();
            
        } else {
            return SQL;
            
        }
    }

    @Override
    public Object[] getSqlParams() {
        return null;
    }
    
}
