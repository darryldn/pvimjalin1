/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.dni.ext.dmz.service;

import id.dni.ext.dmz.exception.RemoteServiceException;
import id.dni.ext.firebase.cloud.msg.json.FcmMessageDownstreamResponseJson;
import id.dni.ext.firebase.cloud.msg.json.FcmMessageJson;

/**
 *
 * @author darryl.sulistyan
 */
public interface FirebaseService {
    
    public FcmMessageDownstreamResponseJson sendMessage(FcmMessageJson message) throws RemoteServiceException;
    
}
