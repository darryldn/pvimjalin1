/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wn.tasman.notification.ext.dni.services.impl;

import com.google.gson.Gson;
import com.wn.tasman.notification.ext.dni.services.FirebaseService;
import id.dni.pvim.ext.net.TransferTicketDto;
import id.dni.pvim.ext.web.in.Commons;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 *
 * @author darryl.sulistyan
 */
@Service
@Qualifier("DummyFirebaseServiceImpl")
public class DummyFirebaseServiceImpl implements FirebaseService {

    private Properties prop;
    
    public void setProperties(Properties prop) {
        this.prop = prop;
    }
    
    @PostConstruct
    public void init() {
        if (this.prop == null) {
            InputStream firebasePropIn = this.getClass().getResourceAsStream("/firebase.properties");
            prop = new Properties();
            try {
                prop.load(firebasePropIn);
            } catch (IOException ex) {
                Logger.getLogger(DummyFirebaseServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                try {
                    firebasePropIn.close();
                } catch (IOException ex) {
                }
            }
        }
    }
    
    /**
     * Send ticket to ConnReader.war to be sent to Firebase.
     * Why not just add Firebase library to Pvim.ear instead? That has been tried
     * and somehow, it interferes with Spring classloader, I think, and because of that,
     * It seems that somehow the TicketManager is loaded twice with different classloaders.
     * Due to that, The TicketManagerImpl from classloader A cannot be cast to TicketManager from classloader B
     * and Pvim.ear fails spectacularly throwing license errors!
     * 
     * The ticket is serialized to json string and sent via Http request to TicketController in ConnReader.war
     * 
     * @param ticketMap
     * @param url 
     */
    private void sendRemote(TransferTicketDto ticketMap, String url) {
        try {
            Logger.getLogger(this.getClass().getName()).log(Level.INFO, "ticket: {0}", ticketMap);
            
            String strUrl = url;
            int timeout = Integer.parseInt(this.prop.getProperty("firebase.database.timeout"));
            Gson gson = new Gson();
            String jsonData = gson.toJson(ticketMap);
            String returnData = Commons.postJsonRequest(strUrl, timeout, jsonData);
            Logger.getLogger(this.getClass().getName()).log(Level.INFO, "obtained: {0}", returnData);
            
        } catch (MalformedURLException ex) {
            Logger.getLogger(DummyFirebaseServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(DummyFirebaseServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @Override
    @Async
    public void send(TransferTicketDto ticketMap) {
        sendRemote(ticketMap, this.prop.getProperty("firebase.workaround.url"));
    }

    @Override
    @Async
    public void remove(TransferTicketDto ticketMap) {
        sendRemote(ticketMap, this.prop.getProperty("firebase.workaround.url.removeticket"));
    }
    
}
