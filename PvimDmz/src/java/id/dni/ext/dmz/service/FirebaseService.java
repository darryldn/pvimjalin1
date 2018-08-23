/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.dni.ext.dmz.service;

import id.dni.ext.dmz.exception.RemoteServiceException;
import id.dni.ext.firebase.user.msg.FbAuthUserServiceResponse;
import id.dni.ext.firebase.cloud.msg.json.FcmMessageDownstreamResponseJson;
import id.dni.ext.firebase.cloud.msg.json.FcmMessageJson;
import id.dni.ext.firebase.user.msg.FbAuthUserJson;

/**
 *
 * @author darryl.sulistyan
 */
public interface FirebaseService {
    
    public FcmMessageDownstreamResponseJson sendMessage(FcmMessageJson message) throws RemoteServiceException;
    
    public FbAuthUserServiceResponse createUser(FbAuthUserJson user) throws RemoteServiceException;
    
    public FbAuthUserServiceResponse removeUser(FbAuthUserJson user) throws RemoteServiceException;
    
}
