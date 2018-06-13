/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.dni.pvim.ext.telegram.repo.spec;

import id.dni.pvim.ext.repo.db.spec.ISqlSpecification;

/**
 *
 * @author darryl.sulistyan
 */
class RemoveClosedOrDeletedTicketsFromNotifiedSpec implements ISqlSpecification {

    private static final String SQL = 
            "DELETE FROM PVIM_EXT_TICKET_NOTIFIED " +
            "where ticket_id in (" +
            "   SELECT t.TICKET_ID " +
            "   FROM   TICKET t " +
            "   WHERE  t.STATUS_ID = '7' or t.STATUS_ID = '6' " +
            "   )";
    
    @Override
    public String toParameterizedSqlQuery() {
        return SQL;
    }

    @Override
    public Object[] getSqlParams() {
        return null;
    }
    
}
