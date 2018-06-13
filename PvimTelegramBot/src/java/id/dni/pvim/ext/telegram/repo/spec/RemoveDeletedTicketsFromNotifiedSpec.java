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
class RemoveDeletedTicketsFromNotifiedSpec implements ISqlSpecification {

    private static final String SQL =
            "DELETE FROM PVIM_EXT_TICKET_NOTIFIED " +
            "WHERE ticket_id not in (" +
            "   SELECT t.TICKET_ID " +
            "   FROM   TICKET t " +
            "   );";
    
    @Override
    public String toParameterizedSqlQuery() {
        return SQL;
    }

    @Override
    public Object[] getSqlParams() {
        return null;
    }
    
}
