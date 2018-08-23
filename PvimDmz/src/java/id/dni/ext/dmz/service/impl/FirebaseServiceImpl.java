/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.dni.ext.dmz.service.impl;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import com.google.firebase.messaging.AndroidConfig;
import com.google.firebase.messaging.AndroidNotification;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.gson.Gson;
import id.dni.ext.dmz.exception.RemoteServiceException;
import id.dni.ext.firebase.user.msg.FbAuthUserServiceResponse;
import id.dni.ext.dmz.service.FirebaseService;
import id.dni.ext.firebase.cloud.msg.json.FcmMessageDownstreamResponseJson;
import id.dni.ext.firebase.cloud.msg.json.FcmMessageJson;
import id.dni.ext.firebase.user.msg.FbAuthUserJson;
import id.dni.pvim.ext.web.in.Commons;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 *
 * @author darryl.sulistyan
 */
@Service
public class FirebaseServiceImpl implements FirebaseService {
    
    private String firebaseServiceAuth;
    private String firebaseDatabaseUrl;
    private String firebaseRootPath;
    private int firebaseTimeout;

    @Value("${firebase.service_auth}")
    public void setFirebaseServiceAuthJsonFile(String j) {
        this.firebaseServiceAuth = j;
    }

    @Value("${firebase.database.url}")
    public void setFirebaseDatabaseUrl(String url) {
        this.firebaseDatabaseUrl = url;
    }

    @Value("${firebase.database.root}")
    public void setFirebaseRootPath(String path) {
        this.firebaseRootPath = path;
    }

    @Value("${firebase.database.timeout}")
    public void setFirebaseTimeout(String strTimeout) {
        try {
            firebaseTimeout = Integer.parseInt(strTimeout);
        } catch (NumberFormatException ex) {
            firebaseTimeout = 3000;
        }
    }

//    private DatabaseReference ref;

    @PostConstruct
    public void init() {
        try {
            InputStream serviceAuthJson = this.getClass().getResourceAsStream(this.firebaseServiceAuth);
//        System.setProperty("https.proxyHost", "proxy-pdb.wincor-nixdorf.com");
//        System.setProperty("https.proxyPort", "81");
            FirebaseOptions opt = new FirebaseOptions.Builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAuthJson))
                    .setDatabaseUrl(this.firebaseDatabaseUrl)
                    .setConnectTimeout(firebaseTimeout)
                    .setReadTimeout(firebaseTimeout)
                    .build();

            FirebaseApp.initializeApp(opt);
            
        } catch (IOException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            throw new RuntimeException(ex);
            
        }
    }
    
    @Override
    public FcmMessageDownstreamResponseJson sendMessage(FcmMessageJson message) throws RemoteServiceException {
        
        Logger.getLogger(this.getClass().getName()).log(Level.INFO, 
                    ">> sendMessage");
        
        Gson gson = new Gson();
        Logger.getLogger(this.getClass().getName()).log(Level.INFO, 
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
            Logger.getLogger(this.getClass().getName()).log(Level.INFO, 
                    " - Send notification return {0}", response);
            FcmMessageDownstreamResponseJson responseObj = new FcmMessageDownstreamResponseJson();
            responseObj.setSuccess(1);
            return responseObj;
            
        } catch (FirebaseMessagingException ex) {
            throw new RemoteServiceException(ex);
            
        } finally {
        
            Logger.getLogger(this.getClass().getName()).log(Level.INFO, 
                    "<< sendMessage returns {0}", response);
        }
    }

    @Override
    public FbAuthUserServiceResponse createUser(FbAuthUserJson user) throws RemoteServiceException {
        
        FbAuthUserServiceResponse response = new FbAuthUserServiceResponse();
        final FirebaseAuth auth = FirebaseAuth.getInstance(FirebaseApp.getInstance());
        
        if (!Commons.isEmptyStrIgnoreSpaces(user.getEmail())) {
            UserRecord userByEmail; 
            boolean doCreateUser;
            try {
                userByEmail = auth.getUserByEmail(user.getEmail());
                if (userByEmail != null) {
                    Logger.getLogger(this.getClass().getName()).log(Level.FINE, 
                        " - user with email {0} Already exists, displayName: {1}", 
                        new Object[]{userByEmail.getEmail(), userByEmail.getDisplayName()});
                    doCreateUser = false;
                } else {
                    Logger.getLogger(this.getClass().getName()).log(Level.WARNING, 
                        " - user with email {0} returns null", 
                        new Object[]{user.getEmail()});
                    doCreateUser = true;
                }
            } catch (FirebaseAuthException ex) {
                Logger.getLogger(this.getClass().getName()).log(Level.WARNING, null, ex);
                doCreateUser = true;
            }
            
            if (doCreateUser) {
                Logger.getLogger(this.getClass().getName()).log(Level.INFO, 
                        " - user with email {0} does not exist! Create user now", user.getEmail());
                // user does not exist. Create now
                UserRecord.CreateRequest cr = new UserRecord.CreateRequest();
                cr.setEmail(user.getEmail());
                cr.setPassword(user.getPassword());
                
                try {
                    auth.createUser(cr);
                    Logger.getLogger(this.getClass().getName()).log(Level.INFO, 
                            " - user with email {0} created successfully", user.getEmail());
                } catch (FirebaseAuthException ex1) {
                    throw new RemoteServiceException(ex1);
                }
            }
            
            response.setSuccess(true);
//            
//            try {
//                
//                UserRecord userByEmail = auth.getUserByEmail(user.getEmail());
//                if (userByEmail == null) {
//                    Logger.getLogger(this.getClass().getName()).log(Level.WARNING, "user with email {0} does not exist!", user.getEmail());
//                    auth.createUser(cr);
//                    response.setSuccess(true);
//                } else {
//                    Logger.getLogger(this.getClass().getName()).log(Level.WARNING, "user with email {0} exists! displayName: {1}", 
//                            new Object[] {user.getEmail(), userByEmail.getDisplayName()});
//                }
//                
//            } catch (FirebaseAuthException ex) {
//                // this generates TOO MUCH error, so, set the error log to FINE
//                Logger.getLogger(this.getClass().getName()).log(Level.WARNING, null, ex);
//                response.setSuccess(false);
//            }
        } else {
            Logger.getLogger(this.getClass().getName()).log(Level.WARNING, 
                    "unable to send user: {0} because its email ({1}) is not valid / empty!", 
                    new Object[]{user.getUsername(), user.getEmail()});
            throw new RemoteServiceException(new IllegalArgumentException("Email invalid / empty"));
            
        }
        
        return response;
    }

    @Override
    public FbAuthUserServiceResponse removeUser(FbAuthUserJson user) throws RemoteServiceException {
        FbAuthUserServiceResponse response = new FbAuthUserServiceResponse();
        final FirebaseAuth auth = FirebaseAuth.getInstance(FirebaseApp.getInstance());
        
        if (!Commons.isEmptyStrIgnoreSpaces(user.getEmail())) {
            UserRecord userByEmail = null;
            
            try {
                userByEmail = auth.getUserByEmail(user.getEmail());
            } catch (FirebaseAuthException ex) {
                // user does not exist.
                Logger.getLogger(this.getClass().getName()).log(Level.WARNING, null, ex);
                response.setSuccess(true);
            }
            
            try {
                if (userByEmail != null) {
                    auth.deleteUser(userByEmail.getUid());
                    response.setSuccess(true);
                }
            } catch (FirebaseAuthException ex) {
                Logger.getLogger(this.getClass().getName()).log(Level.WARNING, null, ex);
                throw new RemoteServiceException(ex);
            }
            
        } else {
            Logger.getLogger(this.getClass().getName()).log(Level.WARNING, 
                    "unable to send user: {0} because its email ({1}) is not valid / empty!", 
                    new Object[]{user.getUsername(), user.getEmail()});
            throw new RemoteServiceException(new IllegalArgumentException("Email invalid / empty"));
        }
        
        return response;
    }
    
    
    
}
