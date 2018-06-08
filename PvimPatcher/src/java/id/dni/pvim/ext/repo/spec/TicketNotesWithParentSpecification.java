/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.dni.pvim.ext.repo.spec;

import id.dni.pvim.ext.repo.db.spec.ISqlSpecification;

/**
 *
 * @author darryl.sulistyan
 */
public class TicketNotesWithParentSpecification implements ISqlSpecification {

    private static final String SQL_GET_TICKET_NOTES_WITH_PARENT =
            "select TICKET_ID, CREATE_TIME, NOTE from TICKET_RECORD where TICKET_ID in "
            + "( "
            + "(SELECT TICKET_ID FROM TICKET WHERE TICKET_NUM=?) "
            + "UNION "
            + "(SELECT PARENT_ID FROM TICKET WHERE TICKET_NUM=? and PARENT_ID IS NOT NULL) "
            + ") "
            + "AND NOTE_SOURCE_FLAG=1 "
            + "ORDER BY CREATE_TIME ASC";
    
    private final String ticketNumber;
    
    public TicketNotesWithParentSpecification(String ticketNumber) {
        this.ticketNumber = ticketNumber;
    }
    
    @Override
    public String toParameterizedSqlQuery() {
        return SQL_GET_TICKET_NOTES_WITH_PARENT;
    }

    @Override
    public Object[] getSqlParams() {
        return (Object[])(new String[]{ticketNumber, ticketNumber});
    }
    
}
