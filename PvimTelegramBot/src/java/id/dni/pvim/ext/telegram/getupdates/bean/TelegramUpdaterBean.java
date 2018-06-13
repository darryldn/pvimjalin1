/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.dni.pvim.ext.telegram.getupdates.bean;

import com.google.gson.Gson;
import id.dni.pvim.ext.telegram.chatbot.TelegramChatBotBeanLocal;
import id.dni.pvim.ext.telegram.pojo.TelegramGetUpdatesPOJO;
import id.dni.pvim.ext.telegram.pojo.TelegramMessagePOJO;
import id.dni.pvim.ext.telegram.commons.config.TelegramConfig;
import id.dni.pvim.ext.web.in.Commons;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.Timeout;
import javax.ejb.Timer;
import javax.ejb.TimerService;

/**
 * Periodically fires getUpdate to telegram server to get updates for chats.
 * Chat information is automatically removed after 24-hours in telegram server
 * 
 * @author darryl
 */
@Singleton
public class TelegramUpdaterBean implements TelegramUpdaterBeanLocal {

    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
    
    @Resource
    private TimerService timerService;
    
    @EJB
    private TelegramChatBotBeanLocal chatbot;
    
    private static final String TIMER_NAME = "@id.dni.pvim.ext.telegram.web.servlet.TelegramUpdaterBean#TIMER";
    
    @Override
    public void init() {
        
        int lk = 0;
        
        // cancelling previous timers.
        // assume timers are persistent!
        for (Object obj : timerService.getTimers()) {
            javax.ejb.Timer timer = (javax.ejb.Timer) obj;
            Serializable s = timer.getInfo();
            if (s instanceof TelegramTimerInfo) {
                TelegramTimerInfo timerInfo = (TelegramTimerInfo) s;
                String scheduled = timerInfo.getTimerName();
                if (scheduled.equals(TIMER_NAME)) {
                    lk = timerInfo.getKey();
                    Logger.getLogger(this.getClass().getName()).log(Level.FINEST, 
                            " - previous timer cancelled! Timer info: {0}", timerInfo);
                    timer.cancel();
                }
            }
        }

        TelegramTimerInfo timerInfo = new TelegramTimerInfo();
        timerInfo.setTimerName(TIMER_NAME);
        timerInfo.setKey(lk+1);
        timerService.createTimer(0, TelegramConfig.getGetUpdatesInterval(), timerInfo);
        
    }
    
    @Timeout
    private void getUpdates(Timer timer) {
        Logger.getLogger(this.getClass().getName()).log(Level.FINEST, 
                " - timer executed {0}", timer.getInfo());
        
        // 1. Send request
        String urlStr = TelegramConfig.getGetUpdatesUrl();
        try {
            URL url = new URL(urlStr);
            HttpURLConnection  conn =  (HttpURLConnection) url.openConnection();
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
            
            Logger.getLogger(this.getClass().getName()).log(Level.FINEST, 
                    " - obtain request result from telegram: {0}", returnData);
            
            // 3. Pass object to TelegramChatBot
            Gson gson = new Gson();
            TelegramGetUpdatesPOJO pojo = gson.fromJson(returnData, TelegramGetUpdatesPOJO.class);
            if (pojo != null && pojo.getOk()) {
                for (TelegramMessagePOJO message : pojo.getResult()) {
                    chatbot.consume(message);
                }
            }
            
        } catch (MalformedURLException ex) {
            Logger.getLogger(TelegramUpdaterBean.class.getName()).log(Level.SEVERE, null, ex);
            
        } catch (IOException ex) {
            Logger.getLogger(TelegramUpdaterBean.class.getName()).log(Level.SEVERE, null, ex);
            
        }
        
    }
    
}
