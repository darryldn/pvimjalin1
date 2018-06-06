/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.dni.pvim.ext.conf;

import id.dni.pvim.ext.web.in.Commons;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author darryl.sulistyan
 */
public class PatcherConfig {
    
    private static final PatcherConfig ME = new PatcherConfig();
    private Properties prop;
    private static final String SOAPCONFIG = "/soapconfig.properties";
    
    private PatcherConfig() {
    }
    
    public void init() {
        try {
            prop = Commons.loadConfig(PatcherConfig.class, SOAPCONFIG);
        } catch (IOException ex) {
            Logger.getLogger(PatcherConfig.class.getName()).log(Level.SEVERE, null, ex);
        }
//        
//        InputStream resourceAsStream = PatcherConfig.class.getResourceAsStream(SOAPCONFIG);
//        try {
//            prop = new Properties();
//            prop.load(resourceAsStream);
//            Logger.getLogger(PatcherConfig.class.getName()).log(Level.INFO, "Read properties: {0}", prop);
//            
//        } catch (IOException ex) {
//            Logger.getLogger(PatcherConfig.class.getName()).log(Level.SEVERE, null, ex);
//            
//        } finally {
//            try {
//                resourceAsStream.close();
//            } catch (IOException ex) {
//            }
//        }
    }
    
    public static PatcherConfig getInstance() {
        return ME;
    }
    
    public String get(String key) {
        return prop.getProperty(key);
    }
    
    public String get(String key, String defaults) {
        return prop.getProperty(key, defaults);
    }
    
    
}
