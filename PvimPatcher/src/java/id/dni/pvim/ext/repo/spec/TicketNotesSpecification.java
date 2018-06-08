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
public class TicketNotesSpecification implements ISqlSpecification {

    private static final String SQL_GET_TICKET_NOTES = 
            "SELECT TICKET_ID, CREATE_TIME, NOTE FROM TICKET_RECORD where TICKET_ID in "
            + "(SELECT TICKET_ID FROM TICKET WHERE TICKET_NUM=?) AND NOTE_SOURCE_FLAG=1";
    
    private final String ticketNumber;
    
    public TicketNotesSpecification(String ticketNumber) {
        this.ticketNumber = ticketNumber;
    }
    
    @Override
    public String toParameterizedSqlQuery() {
        return SQL_GET_TICKET_NOTES;
    }

    @Override
    public Object[] getSqlParams() {
        return (Object[])(new String[]{ticketNumber});
    }
    
}
