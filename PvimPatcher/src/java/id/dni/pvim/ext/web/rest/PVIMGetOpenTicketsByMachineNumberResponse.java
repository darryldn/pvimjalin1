/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.dni.pvim.ext.web.rest;

import id.dni.pvim.ext.web.in.OperationError;
import java.util.List;

/**
 *
 * @author darryl.sulistyan
 */
public class PVIMGetOpenTicketsByMachineNumberResponse {
    
    private List<RestTicketDto> tickets;
    private OperationError err;

    public List<RestTicketDto> getTickets() {
        return tickets;
    }

    public void setTickets(List<RestTicketDto> tickets) {
        this.tickets = tickets;
    }

    public OperationError getErr() {
        return err;
    }

    public void setErr(OperationError err) {
        this.err = err;
    }
    
}
