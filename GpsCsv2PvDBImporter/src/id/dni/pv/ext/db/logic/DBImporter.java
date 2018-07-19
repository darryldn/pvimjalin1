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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.MapListHandler;
import org.apache.log4j.Logger;

/**
 *
 * @author darryl.sulistyan
 */
public class DBImporter {
    
    private static final Logger LOGGER = Logger.getLogger(DBImporter.class);
    
    private final String csvInput;
    private final String connectionUrl;
    private final String username;
    private final String password;
    
    private static final String 
            SQL_UPDATE_PV_LATITUDE = "update BASEDATA set value=? where id=? and idtype=0 and reference=999916",
            SQL_UPDATE_PV_LONGITUDE= "update BASEDATA set value=? where id=? and idtype=0 and reference=999915",
            SQL_UPDATE_PV_ADDRESS= "update BASEDATA set value=? where id=? and idtype=0 and reference=999997",
            SQL_UPDATE_PV_LOCATION= "update BASEDATA set value=? where id=? and idtype=0 and reference=999992",
            SQL_INSERT_PV_LATITUDE = "insert into BASEDATA values(?, 0, 999916, ?)",
            SQL_INSERT_PV_LONGITUDE= "insert into BASEDATA values(?, 0, 999915, ?)",
            SQL_INSERT_PV_ADDRESS = "insert into BASEDATA values(?, 0, 999997, ?)",
            SQL_INSERT_PV_LOCATION= "insert into BASEDATA values(?, 0, 999992, ?)",
            SQL_ID_EXISTS = "select 1 from DEVICE where deviceid=?";
    
    
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
            // check if deviceID exist
            List<Map<String, Object>> query = runner.query(conn, SQL_ID_EXISTS, 
                    new MapListHandler(), deviceID);
            
            if (query != null && !query.isEmpty()) {
                // okay, the deviceID exists in PV. Insert the geo data now!
                updated = runner.update(conn, insertSql, deviceID, value);
                if (updated == 0) {
                    LOGGER.warn(String.format(" - Cannot update device information for %s", deviceID));
                }
                
            } else {
                LOGGER.warn(String.format(" - No device data in ProView for %s", deviceID));
                
            }
            
        }
        
        return updated;
    }
    
    public void importCsv() throws SQLException, FileNotFoundException, IOException {
        
        Connection conn = null;
        boolean isSuccess = false;
        BufferedReader in = null;
        
        try {
            conn = DriverManager.getConnection(this.connectionUrl, username, password);
            conn.setAutoCommit(false);
            in = new BufferedReader(new FileReader(csvInput));
            List<MachineRecord> machines = new ArrayList<>();
            
            Iterable<CSVRecord> csvRecords = CSVFormat.RFC4180.withHeader("NO", "Bank", "ID ATM", "LOKASI " +"(2)", "ALAMAT " +"(3)", 
                    "Latitude", "Longitude", "Update Date").withFirstRecordAsHeader().parse(in);
            for (CSVRecord csvRecord : csvRecords) {
                MachineRecord m = new MachineRecord();
                
                m.setBank(csvRecord.get("Bank"));
                m.setId(csvRecord.get("ID ATM"));
                m.setLocation(csvRecord.get("LOKASI " +"(2)"));
                m.setAddress(csvRecord.get("ALAMAT " +"(3)"));
                m.setLatitude(csvRecord.get("Latitude"));
                m.setLongitude(csvRecord.get("Longitude"));
                m.setUpdateDate(csvRecord.get("Update Date"));
                
                LOGGER.debug("Reading: m=" + m);
                machines.add(m);
            }
            
            QueryRunner runner = new QueryRunner();
            
//            String csv;
            for (MachineRecord machine : machines) {
//                String[] param = csv.split(",");
                // NO	Bank	ID ATM	"LOKASI(2)"	"ALAMAT(3)"	Latitude	Longitude	Update Date
                String deviceID = machine.getId(); //param[2];
                String location = machine.getLocation(); //param[3];
                String address = machine.getAddress(); //param[4];
                String latitude = machine.getLatitude(); //param[5];
                String longitude = machine.getLongitude(); //param[6];
                
                try {
                    Double.parseDouble(latitude);
                    Double.parseDouble(longitude);
                    updateOrInsert(runner, conn, SQL_UPDATE_PV_LATITUDE, SQL_INSERT_PV_LATITUDE, deviceID, latitude);
                    updateOrInsert(runner, conn, SQL_UPDATE_PV_LONGITUDE, SQL_INSERT_PV_LONGITUDE, deviceID, longitude);
                    updateOrInsert(runner, conn, SQL_UPDATE_PV_ADDRESS, SQL_INSERT_PV_ADDRESS, deviceID, address);
                    updateOrInsert(runner, conn, SQL_UPDATE_PV_LOCATION, SQL_INSERT_PV_LOCATION, deviceID, location);
                    
                } catch (NumberFormatException ex) {
                    LOGGER.warn(String.format(
                            " - Input Error for deviceID %s. Latitude or Longitude is not a number.", 
                            deviceID));
                }
            }
            
            conn.commit();
            isSuccess = true;
            
        } finally {
            if (!isSuccess && conn != null) {
                conn.rollback();
            }
            if (in != null) {
                in.close();
            }
            DbUtils.close(conn);
            
        }
        
    }
    
}
