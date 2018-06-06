/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.dni.pvim.ext.web.rest;

import id.dni.pvim.ext.web.in.PVIMAuthToken;

/**
 *{
	auth: {
		username: ....,
		password: ....
	},
	ticketNumber: ....
}
 * @author darryl.sulistyan
 */
public class PVIMGetTicketByNumberRequest {
    
    private PVIMAuthToken auth;
    private String ticketNumber;

    public PVIMAuthToken getAuth() {
        return auth;
    }

    public void setAuth(PVIMAuthToken auth) {
        this.auth = auth;
    }

    public String getTicketNumber() {
        return ticketNumber;
    }

    public void setTicketNumber(String ticketNumber) {
        this.ticketNumber = ticketNumber;
    }

    @Override
    public String toString() {
        return "PVIMGetTicketByNumberRequest{" + "auth=" + auth + ", ticketNumber=" + ticketNumber + '}';
    }
    
    
    
}
