/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package springstuff.service.impl;

import com.google.firebase.messaging.AndroidConfig;
import com.google.firebase.messaging.AndroidNotification;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;

import com.google.gson.Gson;
import id.dni.ext.firebase.cloud.msg.FcmMessageService;
import id.dni.ext.firebase.cloud.msg.json.FcmMessageDownstreamResponseJson;
import id.dni.ext.firebase.cloud.msg.json.FcmMessageJson;
import id.dni.ext.firebase.cloud.msg.json.FcmMessageNotificationJson;
import id.dni.ext.firebase.cloud.msg.project.FcmProjectToken;
import id.dni.pvim.ext.web.in.Commons;
import id.dni.pvim.ext.web.in.PVIMAuthToken;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import springstuff.exceptions.RemoteWsException;
import springstuff.service.AsyncRunnerService;
import springstuff.service.firebase.FirebaseCloudMessagingService;

/**
 *
 * @author darryl.sulistyan
 */
@Service
public class FirebaseCloudMessagingServiceImpl implements FirebaseCloudMessagingService {

    private AsyncRunnerService async;
    private FcmMessageService fcmService;
    
    @Autowired
    public void setService(AsyncRunnerService service) {
        this.async = service;
    }
    
    @PostConstruct
    public void init() {
        this.fcmService = new FcmMessageService();
    }
    
    private String fcmUrl;
    private String fcmProxyUrl;
    private String fcmProxyUser;
    private String fcmProxyPass;
    
    private int fcmTimeout;
    private String fcmServerAuthLegacyKey;
    private String fcmServerAuthKey;
    
    public void setFcmMessageService(FcmMessageService service) {
        this.fcmService = service;
    }
    
    @Value("${firebase.cloudmessaging.proxy.server.url}")
    public void setFcmProxyUrl(String url) {
        this.fcmProxyUrl = url;
    }
    
    @Value("${firebase.cloudmessaging.proxy.server.username}")
    public void setFcmProxyUsername(String u) {
        this.fcmProxyUser = u;
    }
    
    @Value("${firebase.cloudmessaging.proxy.server.password}")
    public void setFcmProxyPassword(String u) {
        this.fcmProxyPass = u;
    }
    
    @Value("${firebase.cloudmessaging.server.url}")
    public void setFcmUrl(String url) {
        this.fcmUrl =url;
    }
    
    @Value("${firebase.cloudmessaging.server.timeout}")
    public void setFcmTimeout(String strTimeout) {
        try {
            this.fcmTimeout = Integer.parseInt(strTimeout);
        } catch (NumberFormatException ex) {
            this.fcmTimeout = 10000;
        }
    }
    
    @Value("${firebase.cloudmessaging.server.key.legacy}")
    public void setFcmServerLegacyKey(String key) {
        this.fcmServerAuthLegacyKey = key;
    }
    
    @Value("${firebase.cloudmessaging.server.key}")
    public void setFcmServerKey(String key) {
        this.fcmServerAuthKey = key;
    }
    
    @Override
    public void sendMessageAsync(final FcmMessageJson message) throws RemoteWsException {
        
        final FcmProjectToken projectToken = new FcmProjectToken();
        
        if (Commons.isEmptyStrIgnoreSpaces(this.fcmServerAuthKey)) {
            projectToken.setAuthTokenStr(this.fcmServerAuthLegacyKey);
        } else {
            projectToken.setAuthTokenStr(this.fcmServerAuthKey);
        }
        
        projectToken.setFcmUrl(fcmUrl);
        
        this.async.run(new Runnable() {
            @Override
            public void run() {
                try {
                    FcmMessageDownstreamResponseJson sendSimpleNotification = 
                            fcmService.sendSimpleNotification(projectToken, message, fcmTimeout);
                    Gson gson = new Gson();
                    Logger.getLogger(FirebaseCloudMessagingServiceImpl.class.getName()).log(Level.INFO, 
                            " - Send notification return {0}", new Object[]{gson.toJson(sendSimpleNotification)});
                } catch (IOException ex) {
                    Logger.getLogger(FirebaseCloudMessagingServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        
    }
    
    private FcmMessageDownstreamResponseJson sendMessageViaProxyWithPvimAuth(FcmMessageJson message) throws RemoteWsException {
        Logger.getLogger(FirebaseCloudMessagingServiceImpl.class.getName()).log(Level.INFO, 
                    ">> sendMessageViaProxyNoAuth");
        
        Gson gson = new Gson();
        Logger.getLogger(FirebaseCloudMessagingServiceImpl.class.getName()).log(Level.INFO, 
                    " - message: {0}", gson.toJson(message));
        
        try {
            
            Map<String, Object> jsonObj = new HashMap<>();
            jsonObj.put("msg", message);
            
            PVIMAuthToken auth = new PVIMAuthToken();
            auth.setUsername(this.fcmProxyUser);
            auth.setPassword(this.fcmProxyPass);
            jsonObj.put("auth", auth);
            
            String jsonObjStr = gson.toJson(jsonObj);
            Logger.getLogger(FirebaseCloudMessagingServiceImpl.class.getName()).log(Level.INFO, 
                    " - jsonObjStr: {0}", jsonObjStr);
            
            String postJsonRequest = Commons.postJsonRequest(this.fcmProxyUrl, fcmTimeout, jsonObjStr);
            Logger.getLogger(FirebaseCloudMessagingServiceImpl.class.getName()).log(Level.INFO, 
                    " - obtain result: {0}", postJsonRequest);
            FcmMessageDownstreamResponseJson responseObj = new FcmMessageDownstreamResponseJson();
            responseObj.setSuccess(1);
            return responseObj;
            
        } catch (IOException ex) {
            throw new RemoteWsException(ex);
            
        } finally {
            Logger.getLogger(FirebaseCloudMessagingServiceImpl.class.getName()).log(Level.INFO, 
                        "<< sendMessageViaProxyNoAuth");
        }
    }
    
    private FcmMessageDownstreamResponseJson sendMessage2(FcmMessageJson message) 
            throws RemoteWsException {
        
        Logger.getLogger(FirebaseCloudMessagingServiceImpl.class.getName()).log(Level.INFO, 
                    ">> sendMessage2");
        
        Gson gson = new Gson();
        Logger.getLogger(FirebaseCloudMessagingServiceImpl.class.getName()).log(Level.INFO, 
                    " - message: {0}", gson.toJson(message));
        
        Message fbMessage = Message.builder()
                .setAndroidConfig(AndroidConfig.builder()
                        .setPriority(AndroidConfig.Priority.HIGH)
                        .setNotification(AndroidNotification.builder()
                                .setTitle(message.getNotification().getTitle())
                                .setBody(message.getNotification().getBody())
                                .setSound("default")
                                .build()
                        ).build()
                )
                .setToken(message.getTo())
                .build();
        
        String response = "ERROR";
        try {
            response = FirebaseMessaging.getInstance().send(fbMessage);
            Logger.getLogger(FirebaseCloudMessagingServiceImpl.class.getName()).log(Level.INFO, 
                    " - Send notification return {0}", response);
            FcmMessageDownstreamResponseJson responseObj = new FcmMessageDownstreamResponseJson();
            responseObj.setSuccess(1);
            return responseObj;
            
        } catch (FirebaseMessagingException ex) {
            throw new RemoteWsException(ex);
            
        } finally {
        
            Logger.getLogger(FirebaseCloudMessagingServiceImpl.class.getName()).log(Level.INFO, 
                    "<< sendMessage2 returns ", response);
        }
    }

    @Override
    public FcmMessageDownstreamResponseJson sendMessage(FcmMessageJson message) 
            throws RemoteWsException {
        
        //return sendMessage2(message);
        return sendMessageViaProxyWithPvimAuth(message);
        
//        final FcmProjectToken projectToken = new FcmProjectToken();
//        
//        if (Commons.isEmptyStrIgnoreSpaces(this.fcmServerAuthKey)) {
//            projectToken.setAuthTokenStr(this.fcmServerAuthLegacyKey);
//        } else {
//            projectToken.setAuthTokenStr(this.fcmServerAuthKey);
//        }
//        
//        projectToken.setFcmUrl(fcmUrl);
//        
//        try {
//            FcmMessageDownstreamResponseJson sendSimpleNotification = 
//                    fcmService.sendSimpleNotification(projectToken, message, fcmTimeout);
//            Gson gson = new Gson();
//            Logger.getLogger(FirebaseCloudMessagingServiceImpl.class.getName()).log(Level.INFO, 
//                    " - Send notification return {0}", new Object[]{gson.toJson(sendSimpleNotification)});
//            return sendSimpleNotification;
//            
//        } catch (IOException ex) {
//            Logger.getLogger(FirebaseCloudMessagingServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
//            throw new RemoteWsException(ex);
//        }
    }
    
}
