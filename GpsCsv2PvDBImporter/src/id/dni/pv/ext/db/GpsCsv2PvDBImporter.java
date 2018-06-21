/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.dni.pv.ext.db;

import id.dni.pv.ext.db.logic.DBImporter;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

/**
 *
 * @author darryl.sulistyan
 */
public class GpsCsv2PvDBImporter {
    
    private static final Logger LOGGER = Logger.getLogger(GpsCsv2PvDBImporter.class);
    
    private static void renameFile(String file, String tag) {
        File csvInput = new File(file);
        if (csvInput.exists() && !csvInput.isDirectory()) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
            String newFileName = String.format("%s.%s.%s", csvInput, sdf.format(new Date()), tag);
            File renamed = new File(newFileName);
            if (!csvInput.renameTo(renamed)) {
                LOGGER.warn(String.format(" - Failed renaming file %s to %s", file, newFileName));
            }
        } else {
            LOGGER.error(String.format("File %s does not exist", file));
        }
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        try {
            PropertyConfigurator.configure("log4j.properties");
        } catch (Exception e) {
        }
        
        LOGGER.debug(">> main()");
        BufferedInputStream bis = null;
        String gpsInputFile = null;
        
        try {

            Properties prop = new Properties();
            bis = new BufferedInputStream(new FileInputStream("dbconfig.properties"));
            prop.load(bis);

            String connectionUrl = prop.getProperty("db.connectionurl");
            String password = prop.getProperty("db.password");
            String username = prop.getProperty("db.username");
            String driverClass = prop.getProperty("db.driver");
            gpsInputFile = prop.getProperty("db.csv.input");

            LOGGER.info("Reading properties:");
            LOGGER.info(String.format("connectionUrl: %s", connectionUrl));
            LOGGER.info(String.format("password: %s", password));
            LOGGER.info(String.format("username: %s", username));
            LOGGER.info(String.format("driverClass: %s", driverClass));
            LOGGER.info(String.format("gpsInputFile: %s", gpsInputFile));
            
            new DBImporter(connectionUrl, username, password, driverClass, gpsInputFile).importCsv();
            renameFile(gpsInputFile, "IMPORTED");
            
        } catch (Exception e) {
            LOGGER.fatal("Fatal error", e);
            if (gpsInputFile != null) {
                renameFile(gpsInputFile, "ERROR");
            }
            
        } finally {
            if (bis != null) {
                try {
                    bis.close();
                } catch (IOException ex) {
                }
            }
            
            LOGGER.debug("<< main()");
            
        }
        
    }
    
}
