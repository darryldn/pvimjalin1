/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package springstuff.json;

import id.dni.ext.web.ws.obj.RestTicketDto;
import id.dni.pvim.ext.web.in.PVIMAuthToken;

/**
 *
 * @author darryl.sulistyan
 */
public class PVIMUpdateTicketRequest {
    
    private PVIMAuthToken auth;
    private RestTicketDto ticket;

    public PVIMAuthToken getAuth() {
        return auth;
    }

    public void setAuth(PVIMAuthToken auth) {
        this.auth = auth;
    }

    public RestTicketDto getTicket() {
        return ticket;
    }

    public void setTicket(RestTicketDto ticket) {
        this.ticket = ticket;
    }
    
    
    
}
