/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.dni.pv.ext.db;

import id.dni.pv.ext.db.logic.DBImporter;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author darryl.sulistyan
 */
public class GpsCsv2PvDBImporter {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception {
        
        Logger.getLogger(GpsCsv2PvDBImporter.class.getName()).log(Level.INFO, ">> main()");
        try {

            Properties prop = new Properties();
            BufferedInputStream bis;
            try {
                bis = new BufferedInputStream(new FileInputStream("dbconfig.properties"));
                prop.load(bis);
            } catch (FileNotFoundException e) {
                Logger.getLogger(GpsCsv2PvDBImporter.class.getName()).log(Level.SEVERE, null, e);
                throw e;
            } catch (IOException e) {
                Logger.getLogger(GpsCsv2PvDBImporter.class.getName()).log(Level.SEVERE, null, e);
                throw e;
            }

            String connectionUrl = prop.getProperty("db.connectionurl");
            String password = prop.getProperty("db.password");
            String username = prop.getProperty("db.username");
            String driverClass = prop.getProperty("db.driver");
            String gpsInputFile = prop.getProperty("db.csv.input");

            new DBImporter(connectionUrl, username, password, driverClass, gpsInputFile).importCsv();
            
        } finally {
            Logger.getLogger(GpsCsv2PvDBImporter.class.getName()).log(Level.INFO, "<< main()");
            
        }
        
    }
    
}
