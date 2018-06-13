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
class NewlyAssignedTicketSpec implements ISqlSpecification {

    private static final String SQL_QUERY = 
            "select t.TICKET_ID, t.TICKET_NUM, s.chat_id from (" +
            "    select tx.TICKET_ID, tx.ASSIGNEE_ID, tx.TICKET_NUM, tx.STATUS_ID from TICKET tx left join PVIM_EXT_TICKET_NOTIFIED n on tx.TICKET_ID = n.ticket_id where n.ticket_id IS NULL" +
            ") t, SLM_USER u, PVIM_EXT_TELEGRAM_SUBSCRIBERS s " +
            "where t.ASSIGNEE_ID = u.USER_ID and s.phone_num = u.MOBILE and t.STATUS_ID = '11'";
    
    public NewlyAssignedTicketSpec() {
        
    }
    
    @Override
    public String toParameterizedSqlQuery() {
        return SQL_QUERY;
    }

    @Override
    public Object[] getSqlParams() {
         return null;
    }
    
}
