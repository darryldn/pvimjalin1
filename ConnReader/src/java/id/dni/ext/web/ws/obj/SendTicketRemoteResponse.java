/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.dni.ext.web.ws.obj;

import id.dni.pvim.ext.web.in.OperationError;

/**
 *
 * @author darryl.sulistyan
 */
public class SendTicketRemoteResponse {
    
    private RestTicketDto ticket;
    private OperationError err;

    public RestTicketDto getTicket() {
        return ticket;
    }

    public void setTicket(RestTicketDto ticket) {
        this.ticket = ticket;
    }

    public OperationError getErr() {
        return err;
    }

    public void setErr(OperationError err) {
        this.err = err;
    }
    
    
    
}
