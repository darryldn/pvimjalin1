/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.dni.pvim.ext.repo.db.spec.impl;

import id.dni.pvim.ext.repo.db.spec.ISqlSpecification;
import id.dni.pvim.ext.repo.db.vo.TicketVo;

/**
 *
 * @author darryl.sulistyan
 */
public class GetAllTicketsSpecification implements ISqlSpecification {

    private static final String SQL = 
            new StringBuilder()
            .append("select ")
                    .append(TicketVo.FIELD_TICKETID).append(", ")
                    .append(TicketVo.FIELD_TICKETNUMBER).append(", ")
                    .append(TicketVo.FIELD_LASTUPDATED).append(", ") 
                    .append(TicketVo.FIELD_ASSIGNEEID).append(" ")
            .append(" from ")
                    .append(TicketVo.TABLE_NAME).append(" ")
            .toString();
    
    public GetAllTicketsSpecification() {
    }
    
    @Override
    public String toParameterizedSqlQuery() {
        return SQL;
    }

    @Override
    public Object[] getSqlParams() {
        return null;
    }
}
