/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.dni.pvim.ext.db.config;


/**
 *
 * @author darryl.sulistyan
 */
public class DBConnectionConfig {
    
    private DBConnectionConfig() {
        
    }
    
    public static String getPVIMDataSourceJDNIName() {
        return PVDbConfigLoader.getInstance().get("PVIMDataSourceJNDI");
    }
    
    public static String getPVDataSourceJDNIName() {
        return PVDbConfigLoader.getInstance().get("PVDataSourceJDNI");
    }
    
}
