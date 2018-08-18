/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.dni.pvim.ext.repo.db.spec.impl;

import id.dni.pvim.ext.repo.db.pagination.IPaginator;
import id.dni.pvim.ext.repo.db.pagination.Mssql2012Paginator;
import id.dni.pvim.ext.repo.db.spec.ISqlSpecification;
import id.dni.pvim.ext.repo.db.vo.TicketVo;

/**
 *
 * @author darryl.sulistyan
 */
public class GetTicketByAssigneeIdSpecification implements ISqlSpecification {

    private static final String SQL = 
            new StringBuilder()
            .append("select ")
                    .append(TicketVo.FIELD_TICKETID).append(", ")
                    .append(TicketVo.FIELD_TICKETNUMBER).append(", ")
                    .append(TicketVo.FIELD_LASTUPDATED).append(", ") 
                    .append(TicketVo.FIELD_ASSIGNEEID).append(" ")
            .append(" from ")
                    .append(TicketVo.TABLE_NAME).append(" ")
            .append(" where ")
                    .append(TicketVo.FIELD_ASSIGNEEID).append("=?")
            .toString();
    
    private final String assigneeId;
    private final int pageSize;
    private final int pageNum;
    
    public GetTicketByAssigneeIdSpecification(String assigneeId) {
        this(assigneeId, -1, -1);
    }
    
    public GetTicketByAssigneeIdSpecification(String assigneeId, int pageSize, int pageNum) {
        this.assigneeId = assigneeId;
        this.pageNum = pageNum;
        this.pageSize = pageSize;
    }
    
    @Override
    public String toParameterizedSqlQuery() {
        if (this.pageNum > 0 && this.pageSize > 0) {
            IPaginator paginator = new Mssql2012Paginator.Builder()
                    .setSql(SQL)
                    .setOrderByColumn(TicketVo.FIELD_TICKETID)
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
        return new Object[]{assigneeId};
    }
    
}
