/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.dni.ext.dmz.controller;

import com.google.gson.Gson;
import id.dni.ext.dmz.exception.RemoteServiceException;
import id.dni.ext.firebase.user.msg.FbAuthUserServiceResponse;
import id.dni.ext.dmz.json.FcmMessageWrapperJson;
import id.dni.ext.dmz.service.FirebaseService;
import id.dni.ext.dmz.service.LoginService;
import id.dni.ext.firebase.cloud.msg.json.FcmMessageDownstreamResponseJson;
import id.dni.ext.firebase.user.msg.FbAuthUserJson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 *
 * @author darryl.sulistyan
 */
@Controller
public class FirebaseProxyController {
    
    private LoginService loginService;
    private FirebaseService firebaseService;
    
    @Autowired
    public void setLoginService(LoginService l) {
        this.loginService = l;
    }
    
    @Autowired
    public void setFirebaseService(FirebaseService l) {
        this.firebaseService = l;
    }
    
    @RequestMapping(value = "/firebase/messaging/user/create",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<String> sendPvimUsers(@RequestBody String userJson) 
            throws RemoteServiceException {
        
        Gson gson = new Gson();
        
        try {
            
            FbAuthUserJson user = gson.fromJson(userJson, FbAuthUserJson.class);
            FbAuthUserServiceResponse createUser = this.firebaseService.createUser(user);
            if (createUser.isSuccess()) {
                return new ResponseEntity<>(gson.toJson(createUser), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(gson.toJson(createUser), HttpStatus.BAD_REQUEST);
            }
            
        } catch (RemoteServiceException ex) {
            throw ex;
            
        } finally {
            
        }
        
    }
    
    @RequestMapping(value = "/firebase/messaging/user/delete",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<String> deletePvimUsers(@RequestBody String userJson) 
            throws RemoteServiceException {
        
        Gson gson = new Gson();
        
        try {
            
            FbAuthUserJson user = gson.fromJson(userJson, FbAuthUserJson.class);
            FbAuthUserServiceResponse removeUser = this.firebaseService.removeUser(user);
            if (removeUser.isSuccess()) {
                return new ResponseEntity<>(gson.toJson(removeUser), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(gson.toJson(removeUser), HttpStatus.BAD_REQUEST);
            }
            
        } catch (RemoteServiceException ex) {
            throw ex;
            
        } finally {
            
        }
        
    }
    
    @RequestMapping(value = "/firebase/messaging/send",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<String> getDeviceComponentsPost(@RequestBody String fbMessagingJson) 
            throws RemoteServiceException {
         
        Gson gson = new Gson();
        
        FcmMessageWrapperJson wrapped = gson.fromJson(fbMessagingJson, FcmMessageWrapperJson.class);
        
        try {
            // Plan: This entire module (PvimDmz.war) will only be exposed to internal network.
            // So, no need for logins.
//            ProviewLoginRequest req = new ProviewLoginRequest();
//            req.setUsername(wrapped.getAuth().getUsername());
//            req.setUsername(wrapped.getAuth().getPassword());
//            ProviewLoginResponse resp = this.loginService.login(req);
            
//            if (resp == null || !resp.isSuccess()) {
//                throw new RemoteServiceException("User does not exist");
//            }
            
            FcmMessageDownstreamResponseJson sendMessage = this.firebaseService.sendMessage(wrapped.getMsg());
            if (sendMessage.getSuccess() == 1) {
                return new ResponseEntity<>(gson.toJson(sendMessage), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(gson.toJson(sendMessage), HttpStatus.BAD_REQUEST);
            }
            
        } catch (RemoteServiceException ex) {
            throw ex;
            
        }
        
    }
    
}
