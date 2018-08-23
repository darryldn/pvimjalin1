/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package springstuff.service.firebase;

import id.dni.ext.firebase.cloud.msg.json.FcmMessageDownstreamResponseJson;
import id.dni.ext.firebase.cloud.msg.json.FcmMessageJson;
import id.dni.ext.firebase.user.msg.FbAuthUserJson;
import id.dni.ext.firebase.user.msg.FbAuthUserServiceResponse;
import springstuff.exceptions.RemoteWsException;

/**
 *
 * @author darryl.sulistyan
 */
public interface FirebaseCloudMessagingService {
    
    public void sendMessageAsync(FcmMessageJson message) throws RemoteWsException;
    
    public FcmMessageDownstreamResponseJson sendMessage(FcmMessageJson message) throws RemoteWsException;
    
    public FbAuthUserServiceResponse createUser(FbAuthUserJson userJson) throws RemoteWsException;
    
    public FbAuthUserServiceResponse removeUser(FbAuthUserJson userJson) throws RemoteWsException;
    
}
