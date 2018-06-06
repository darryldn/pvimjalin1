/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.dni.pvim.ext.db.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author darryl.sulistyan
 */
class PVDbConfigLoader {
    private static final PVDbConfigLoader ME = new PVDbConfigLoader();
    private Properties prop;
    private static final String SOAPCONFIG = "/dbconfig.properties";
    
    private PVDbConfigLoader() {
        init();
    }
    
    private void init() {
        InputStream resourceAsStream = PVDbConfigLoader.class.getResourceAsStream(SOAPCONFIG);
        try {
            prop = new Properties();
            prop.load(resourceAsStream);
            Logger.getLogger(PVDbConfigLoader.class.getName()).log(Level.INFO, "Read properties: {0}", prop);
            
        } catch (IOException ex) {
            Logger.getLogger(PVDbConfigLoader.class.getName()).log(Level.SEVERE, null, ex);
            
        } finally {
            try {
                resourceAsStream.close();
            } catch (IOException ex) {
            }
        }
    }
    
    public static PVDbConfigLoader getInstance() {
        return ME;
    }
    
    public String get(String key) {
        return prop.getProperty(key);
    }
    
    public String get(String key, String defaults) {
        return prop.getProperty(key, defaults);
    }
}
