/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.dni.pv.ext.db.logic;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.dbutils.QueryRunner;

/**
 *
 * @author darryl.sulistyan
 */
public class DBImporter {
    
    private final String csvInput;
    private final String connectionUrl;
    private final String username;
    private final String password;
    
    private static final String 
            SQL_UPDATE_PV_LATITUDE = "update BASEDATA set value=? where id=? and idtype=0 and reference=999916",
            SQL_UPDATE_PV_LONGITUDE= "update BASEDATA set value=? where id=? and idtype=0 and reference=999915",
            SQL_INSERT_PV_LATITUDE = "insert into BASEDATA values(?, 0, 999916, ?)",
            SQL_INSERT_PV_LONGITUDE= "insert into BASEDATA values(?, 0, 999915, ?)";
    
    
    public DBImporter(String connectionUrl, String username, 
            String password, String driverClass, String csvInput) throws ClassNotFoundException {
        
        Class.forName(driverClass);
        this.csvInput = csvInput;
        this.connectionUrl = connectionUrl;
        this.username = username;
        this.password = password;
        
    }
    
    private int updateOrInsert(QueryRunner runner, Connection conn, String updateSql, String insertSql, 
            String deviceID, String value) throws SQLException {
        
        int updated;
                
        updated = runner.update(conn, updateSql, value, deviceID);
        if (updated == 0) {
            updated = runner.update(conn, insertSql, deviceID, value);
            if (updated == 0) {
                Logger.getLogger(this.getClass().getName()).log(Level.WARNING, 
                        " - Cannot insert device information for {0}", deviceID);
            }
        }
        
        return updated;
    }
    
    public void importCsv() throws SQLException, FileNotFoundException, IOException {
        
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(this.connectionUrl, username, password);
            conn.setAutoCommit(false);
            
            BufferedReader in = new BufferedReader(new FileReader(csvInput));
            
            QueryRunner runner = new QueryRunner();
            
            String csv;
            while ((csv = in.readLine()) != null) {
                String[] param = csv.split(",");
                String deviceID = param[0];
                String latitude = param[1];
                String longitude = param[2];
                
                try {
                    Double.parseDouble(latitude);
                    Double.parseDouble(longitude);
                    updateOrInsert(runner, conn, SQL_UPDATE_PV_LATITUDE, SQL_INSERT_PV_LATITUDE, deviceID, latitude);
                    updateOrInsert(runner, conn, SQL_UPDATE_PV_LONGITUDE, SQL_INSERT_PV_LONGITUDE, deviceID, longitude);
                    
                } catch (NumberFormatException ex) {
                    Logger.getLogger(this.getClass().getName()).log(Level.WARNING, 
                            " - Input Error for deviceID {0}. Latitude or Longitude is not a number.", 
                            deviceID);
                }
            }
            
            conn.commit();
            
        } finally {
            if (conn != null) {
                conn.rollback();
            }
            DbUtils.close(conn);
            
        }
        
    }
    
}
