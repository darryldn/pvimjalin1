/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.dni.pvim.ext.web.rest;

import id.dni.pvim.ext.web.in.OperationError;
import com.wn.econnect.inbound.wsi.ticket.TicketDto;

/**
 *
 * @author darryl.sulistyan
 */
public class PVIMGetTicketByNumberResponse {
    
    private RestTicketDto ticket;
    private OperationError err;

    public RestTicketDto getTicket() {
        return ticket;
    }

    public void setTicket(TicketDto ticket) {
        this.ticket = new RestTicketDto(ticket);
    }

    public OperationError getErr() {
        return err;
    }

    public void setErr(OperationError err) {
        this.err = err;
    }
    
    
    
}
