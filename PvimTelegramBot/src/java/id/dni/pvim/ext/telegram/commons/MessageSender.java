/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.dni.pvim.ext.telegram.commons;

import id.dni.pvim.ext.telegram.chatbot.TelegramChatBotBean;
import id.dni.pvim.ext.telegram.web.servlet.TelegramConfig;
import id.dni.pvim.ext.web.in.Commons;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author darryl.sulistyan
 */
public class MessageSender {
    
    public static boolean sendMessageAndSwallowLogs(long chatID, String content) {
        try {
            return sendMessage(chatID, content);
            
        } catch (MalformedURLException ex) {
            Logger.getLogger(TelegramChatBotBean.class.getName()).log(Level.SEVERE, null, ex);
            
        } catch (IOException ex) {
            Logger.getLogger(TelegramChatBotBean.class.getName()).log(Level.SEVERE, null, ex);
            
        }
        return false;
    }
    
    public static boolean sendMessage(long chatID, String content) 
            throws UnsupportedEncodingException, MalformedURLException, IOException {
        
        String requestUrl = TelegramConfig.getSendMessageUrlBasic(chatID, content);
        
        Logger.getLogger(MessageSender.class.getName()).log(Level.FINEST, 
                    " - prepare to sendMessage using URL: {0}", requestUrl);
        
        URL url = new URL(requestUrl);
        HttpURLConnection  conn =  (HttpURLConnection) url.openConnection();
        conn.setDoOutput(true);
        //conn.setRequestProperty("Content-Type", "application/json; charset=utf-8");
        conn.setConnectTimeout(TelegramConfig.getGetUpdatesConnectTimeout());
        conn.setReadTimeout(TelegramConfig.getGetUpdatesRequestTimeout());
        
        String returnData;
        try ( // Read the response XML
                InputStream inputStream = conn.getInputStream()) {
            returnData = Commons.inputStreamToString(inputStream);
        }
        
        Logger.getLogger(MessageSender.class.getName()).log(Level.FINEST, 
                    " - obtain request result from telegram: {0}", returnData);
        
        // TODO: Record error logs from returnData?
        
        return true;
    }
    
}
