/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.dni.pv.ext.ws.dispatcher;

import id.dni.pvim.ext.web.in.Commons;
import java.io.IOException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author darryl.sulistyan
 */
public class WsConfig {
    
    private static final WsConfig ME = new WsConfig();
    private Properties prop;
    private Map<String, String> exposed;
    private static final String SOAPCONFIG = "/wsconfig.properties";
    
    private WsConfig() {
    }
    
    public void init() {
        try {
            prop = Commons.loadConfig(WsConfig.class, SOAPCONFIG);
        } catch (IOException ex) {
            Logger.getLogger(WsConfig.class.getName()).log(Level.SEVERE, null, ex);
        }
//        
//        InputStream resourceAsStream = WsConfig.class.getResourceAsStream(SOAPCONFIG);
//        try {
//            prop = new Properties();
//            prop.load(resourceAsStream);
//            Logger.getLogger(WsConfig.class.getName()).log(Level.INFO, "Read properties: {0}", prop);
//            
//        } catch (IOException ex) {
//            Logger.getLogger(WsConfig.class.getName()).log(Level.SEVERE, null, ex);
//            
//        } finally {
//            try {
//                resourceAsStream.close();
//            } catch (IOException ex) {
//            }
//        }
        
        exposed = convertProp2Map();
    }
    
    public static WsConfig getInstance() {
        return ME;
    }
    
    private Map<String, String> convertProp2Map() {
        Map<String, String> data = new HashMap<>();
        Enumeration e = prop.propertyNames();
        while (e.hasMoreElements()) {
            String key = (String) e.nextElement();
            data.put(key, prop.getProperty(key));
        }
        return data;
    }
    
    public Map<String, String> getProperties() {
        return Collections.unmodifiableMap(this.exposed);
    }
    
    public String get(String key) {
        return prop.getProperty(key);
    }
    
    public String get(String key, String defaults) {
        return prop.getProperty(key, defaults);
    }
}
