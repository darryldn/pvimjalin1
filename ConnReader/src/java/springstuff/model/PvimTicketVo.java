/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package springstuff.model;

/**
 *
 * @author darryl.sulistyan
 */
public class PvimTicketVo {
    
//    private String ticketNumber;
    private boolean successfullyUpdated;
    private long lastupdated;
    private String ticketId;

    public String getTicketId() {
        return ticketId;
    }

    public void setTicketId(String ticketId) {
        this.ticketId = ticketId;
    }

//    public String getTicketNumber() {
//        return ticketNumber;
//    }
//
//    public void setTicketNumber(String ticketNumber) {
//        this.ticketNumber = ticketNumber;
//    }

    public long getLastupdated() {
        return lastupdated;
    }

    public void setLastupdated(long lastupdated) {
        this.lastupdated = lastupdated;
    }

    public boolean isSuccessfullyUpdated() {
        return successfullyUpdated;
    }

    public void setSuccessfullyUpdated(boolean successfullyUpdated) {
        this.successfullyUpdated = successfullyUpdated;
    }

    @Override
    public String toString() {
        return "PvimTicketVo{" + "successfullyUpdated=" + successfullyUpdated + ", lastupdated=" + lastupdated + ", ticketId=" + ticketId + '}';
    }

    
    
    
}
