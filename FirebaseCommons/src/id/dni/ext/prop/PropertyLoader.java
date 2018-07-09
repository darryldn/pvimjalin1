/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.dni.ext.prop;

import id.dni.pvim.ext.web.in.Commons;
import java.io.IOException;
import java.util.Properties;

/**
 *
 * @author darryl.sulistyan
 */
public class PropertyLoader {
    
    public static Properties load(String prop) throws IOException {
        return Commons.loadConfig(PropertyLoader.class, prop);
    }
    
}
