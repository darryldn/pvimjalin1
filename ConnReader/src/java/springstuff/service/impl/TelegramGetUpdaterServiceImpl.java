/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package springstuff.service.impl;

import com.google.gson.Gson;
import id.dni.pvim.ext.repo.exceptions.PvExtPersistenceException;
import id.dni.pvim.ext.telegram.commons.config.TelegramConfig;
import id.dni.pvim.ext.telegram.pojo.TelegramGetUpdatesPOJO;
import id.dni.pvim.ext.telegram.pojo.TelegramMessageContentPOJO;
import id.dni.pvim.ext.telegram.pojo.TelegramUpdateObjPOJO;
import id.dni.pvim.ext.web.in.Commons;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import springstuff.service.TelegramChatService;
import springstuff.service.TelegramGetUpdaterService;

/**
 *
 * @author darryl.sulistyan
 */
// no @Service annotation because this is defined as @Bean in beans.xml file.
// I should've moved this somewhere else!
public class TelegramGetUpdaterServiceImpl implements TelegramGetUpdaterService {
    
    private TelegramChatService chatbot;
    private long lastOffset = 0;
    private boolean useOffset = false;
    
    @Autowired
    public void setChatbot(TelegramChatService chatbot) {
        this.chatbot = chatbot;
    }
    
//    @Scheduled(fixedRateString = "${telegram.getupdates.fixedRate}")
    @Scheduled(fixedRateString = "${telegram.bot_request_interval}")
    public void getUpdates() {
        Logger.getLogger(this.getClass().getName()).log(Level.FINEST,
                " - timer executed");
        
        // 1. Send request
        String urlStr;// = TelegramConfig.getGetUpdatesUrl();
        if (useOffset) {
            urlStr = TelegramConfig.getGetUpdatesUrl(lastOffset);
        } else {
            urlStr = TelegramConfig.getGetUpdatesUrl();
        }

        try {
            Logger.getLogger(this.getClass().getName()).log(Level.FINE, " - urlStr = {0}", urlStr);
            
            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            //conn.setRequestProperty("Content-Type", "application/json; charset=utf-8");
            conn.setConnectTimeout(TelegramConfig.getGetUpdatesConnectTimeout());
            conn.setReadTimeout(TelegramConfig.getGetUpdatesRequestTimeout());
            // requestMethod defaults to GET

            // 2. Open request
            String returnData;
            try ( // Read the response XML
                    InputStream inputStream = conn.getInputStream()) {
                returnData = Commons.inputStreamToString(inputStream);
            }

            Logger.getLogger(this.getClass().getName()).log(Level.FINE,
                    " - obtain request result from telegram: {0}", returnData);

            // 3. Pass object to TelegramChatBot
            Gson gson = new Gson();
            TelegramGetUpdatesPOJO pojo = gson.fromJson(returnData, TelegramGetUpdatesPOJO.class);
            if (pojo != null && pojo.getOk()) {
                if (pojo.getResult() == null || pojo.getResult().isEmpty()) {
                    useOffset = false;
                    
                } else {
                    useOffset = true;
                    for (TelegramUpdateObjPOJO updateObj : pojo.getResult()) {

                        if (updateObj == null) {
                            continue;
                        }

                        TelegramMessageContentPOJO content = updateObj.getMessage();
                        if (content == null) {
                            continue;
                        }

                        lastOffset = updateObj.getUpdate_id()+1;
                        
                        try {
                            chatbot.consume(updateObj);
                        } catch (PvExtPersistenceException ex) {
                            Logger.getLogger(TelegramGetUpdaterServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
                            // ignore, and continue with other requests
                        }
                    }
                }
            }

        } catch (MalformedURLException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);

        } catch (IOException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);

        }
        
    }
    
}
