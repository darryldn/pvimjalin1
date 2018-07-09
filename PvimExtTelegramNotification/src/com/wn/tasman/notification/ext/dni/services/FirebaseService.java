/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wn.tasman.notification.ext.dni.services;

import id.dni.pvim.ext.net.TransferTicketDto;

/**
 *
 * @author darryl.sulistyan
 */
public interface FirebaseService {
    
    public void send(TransferTicketDto ticketMap);
    
    public void remove(TransferTicketDto ticketMap);
    
}
