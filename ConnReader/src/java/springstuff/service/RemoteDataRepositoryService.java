/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package springstuff.service;

import id.dni.pvim.ext.net.TransferTicketDto;
import java.util.List;
import springstuff.exceptions.RemoteRepositoryException;
import springstuff.json.DeviceComponentStateJson;

/**
 *
 * @author darryl.sulistyan
 */
public interface RemoteDataRepositoryService {
    
    public void send(List<DeviceComponentStateJson> devices) throws RemoteRepositoryException;
    
    public void sendTickets(List<TransferTicketDto> tickets) throws RemoteRepositoryException;
    
    public void removeTickets(List<TransferTicketDto> tickets) throws RemoteRepositoryException;
    
    public void periodicUpdateLastupdate();
    
    public void periodicSendPvimSlmUserData();
    
}
