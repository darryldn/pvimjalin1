/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.dni.pvim.ext.telegram.getupdates.bean;

import com.google.gson.Gson;
import id.dni.pvim.ext.repo.exceptions.PvExtPersistenceException;
import id.dni.pvim.ext.telegram.chatbot.TelegramChatBotBean;
import id.dni.pvim.ext.telegram.chatbot.TelegramChatBotBeanLocal;
import id.dni.pvim.ext.telegram.pojo.TelegramGetUpdatesPOJO;
import id.dni.pvim.ext.telegram.pojo.TelegramUpdateObjPOJO;
import id.dni.pvim.ext.telegram.commons.config.TelegramConfig;
import id.dni.pvim.ext.telegram.pojo.TelegramMessageContentPOJO;
import id.dni.pvim.ext.telegram.repo.ITelegramSuscribersRepository;
import id.dni.pvim.ext.telegram.repo.TelegramSubscribersRepository;
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
        timerInfo.setKey(lk + 1);
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

            Logger.getLogger(this.getClass().getName()).log(Level.FINEST,
                    " - obtain request result from telegram: {0}", returnData);

            // 3. Pass object to TelegramChatBot
            Gson gson = new Gson();
            TelegramGetUpdatesPOJO pojo = gson.fromJson(returnData, TelegramGetUpdatesPOJO.class);
            if (pojo != null && pojo.getOk()) {
                for (TelegramUpdateObjPOJO updateObj : pojo.getResult()) {
                    
                    if (updateObj == null) {
                        continue;
                    }

                    TelegramMessageContentPOJO content = updateObj.getMessage();
                    if (content == null) {
                        continue;
                    }

                    // check last update date from subscribers table
                    long latestLastupdated;
                    {
                        ITelegramSuscribersRepository repo = new TelegramSubscribersRepository();
                        try {
                            latestLastupdated = repo.queryLatestLastprocessedTimestamp();
                            long chatDate = content.getDate() * 1000;
                            if (latestLastupdated >= chatDate) { // chat with chatDate is already processed
                                Logger.getLogger(this.getClass().getName()).log(Level.FINEST,
                                        "ignore update_id {0}", updateObj.getUpdate_id());
                                continue;
                            }
                        } catch (PvExtPersistenceException ex) {
                            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
                            continue;
                        }
                    }

                    // date is in seconds precision.
                    // since it is very rare that a person can type two messages in one second
                    // except using hacks via url directly of course!
                    // because F*CK EM!
                    long chatDate = content.getDate();
                    chatDate *= 1000; // transform to ms
                    try {
                        ITelegramSuscribersRepository repos = new TelegramSubscribersRepository();
                        repos.setLatestLastprocessedTimestamp(chatDate); // failed or not, update last processed!

                    } catch (PvExtPersistenceException ex) {
                        Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
                        continue;

                    }
                    
                    chatbot.consume(updateObj);
                }
            }

        } catch (MalformedURLException ex) {
            Logger.getLogger(TelegramUpdaterBean.class.getName()).log(Level.SEVERE, null, ex);

        } catch (IOException ex) {
            Logger.getLogger(TelegramUpdaterBean.class.getName()).log(Level.SEVERE, null, ex);

        }

    }

}
