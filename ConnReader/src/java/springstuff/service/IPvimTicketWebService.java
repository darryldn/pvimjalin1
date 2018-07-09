/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package springstuff.service;

import id.dni.ext.web.ws.obj.RestTicketDto;
import id.dni.pvim.ext.web.in.PVIMAuthToken;
import java.util.List;
import springstuff.exceptions.RemoteWsException;

/**
 *
 * @author darryl.sulistyan
 */
public interface IPvimTicketWebService {
    
    public RestTicketDto createTicket(RestTicketDto ticket, PVIMAuthToken auth) throws RemoteWsException;
    
    public List<RestTicketDto> getOpenTickets(String machineNo, PVIMAuthToken auth) throws RemoteWsException;
    
    public RestTicketDto getTicketByNumber(String ticketNumber, PVIMAuthToken auth) throws RemoteWsException;
    
    public RestTicketDto updateTicket(RestTicketDto ticket, PVIMAuthToken auth) throws RemoteWsException;
    
}
