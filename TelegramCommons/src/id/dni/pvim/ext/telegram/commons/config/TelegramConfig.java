/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.dni.pvim.ext.telegram.commons.config;

import id.dni.pvim.ext.web.in.Commons;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * TODO: Implement TelegramConfig class to use telegram.properties file 
 * @author darryl
 */
public class TelegramConfig {
    
    private static final TelegramConfig INSTANCE = new TelegramConfig();
    private static final String TELEGRAM_CONFIG_FILE = "/telegram.properties";
    private final Properties prop;
    
    private TelegramConfig() {
        
        // for quick setting here first. changed later
        Properties temp;
        try {
            temp = Commons.loadConfig(this.getClass(), TELEGRAM_CONFIG_FILE);
        } catch (IOException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            temp = new Properties();
        }
        
        prop = temp;
    }
    
    public static TelegramConfig getInstance() {
        return INSTANCE;
    }
    
    // convinience methods
    public static String getBotApiKey() {
        return TelegramConfig.getInstance().get("telegram.bot_api_key");
    }
    
    private static String getString(String key, String defaultValue) {
        String v = TelegramConfig.getInstance().get(key);
        return v == null ? defaultValue : v;
    }
    
    private static long getLong(String key, long defaultValue) {
        try {
            return Long.parseLong(TelegramConfig.getInstance().get(key));
        } catch (NumberFormatException ex) {
            return defaultValue;
        }
    }
    
    public static long getGetUpdatesInterval() {
        return getLong("telegram.bot_request_interval", 60000);
    }
    
    public static String getTelegramBotApiBaseUrl() {
        return getString("telegram.api_base_url", "https://api.telegram.org/bot");
    }
    
    public static String getGetUpdatesUrl() {
        String botApiKey = getBotApiKey();
        String baseurl = getTelegramBotApiBaseUrl();
        return baseurl + botApiKey + "/getUpdates";
    }
    
    /**
     * Allow forgetting previous messages. Any message that has update_id &lt;= updateOffset
     * will not be returned by Telegram server.
     * 
     * The problem is that Telegram server will recalculate the updateOffset when
     * there's no chat in 1 week or more.
     * @param updateOffset
     * @return 
     */
    public static String getGetUpdatesUrl(long updateOffset) {
        String botApiKey = getBotApiKey();
        String baseurl = getTelegramBotApiBaseUrl();
        return baseurl + botApiKey + "/getUpdates?offset=" + updateOffset;
    }
    
    /**
     * Obtains the request url to sendMessage to telegram bot.
     * @param chatID
     * @param content encoded automatically using UTF-8
     * @return
     * @throws UnsupportedEncodingException 
     */
    public static String getSendMessageUrlBasic(long chatID, String content) 
            throws UnsupportedEncodingException {
        
        String botApiKey = getBotApiKey();
        String baseurl = getTelegramBotApiBaseUrl();
        return baseurl + botApiKey + "/sendMessage?chat_id=" + chatID + "&text=" + URLEncoder.encode(content, "UTF-8");
        
    }
    
    public static int getGetUpdatesRequestTimeout() {
        return (int) getLong("telegram.get_updates_request_timeout", 25000);
    }
    
    public static int getGetUpdatesConnectTimeout() {
        return (int) getLong("telegram.get_updates_connect_timeout", 25000);
    }
    
    public static long getNotificationInterval() {
        return getLong("telegram.bot_notification_scan_interval", 60000);
    }
    
    public static long getStaleNotifiedTicketsRemoverInterval() {
        return getLong("telegram.stale_ticket_scan_interval", 24L * 60 * 60 * 1000);
    }
    
    public static String getDeeplinkPrefix() {
        return getString("telegram.deeplink_prefix", "http://www.jalin.com/ticket?");
    }
    
    public String get(String key) {
        return prop.getProperty(key);
    }
    
}
