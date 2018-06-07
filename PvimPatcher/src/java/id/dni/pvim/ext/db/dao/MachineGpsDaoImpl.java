/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.dni.pvim.ext.db.dao;

import java.math.BigDecimal;

/**
 *
 * @author darryl.sulistyan
 */
public class MachineGpsDaoImpl implements IMachineGpsDao {

    
    
    @Override
    public DBMachineGpsDao getMachineGps(String deviceID) {
        // TODO: Implement the machine gps reading from ProView server DB
        
        // now, just feed dummy data
        if (deviceID == null) {
            return null;
        }
        
        DBMachineGpsDao gps = new DBMachineGpsDao();
        gps.setDescription("DUMMY ATM");
        gps.setDeviceID(deviceID);
        gps.setLastupdate(System.currentTimeMillis());
        gps.setLatitude(new BigDecimal(-6.2024327));
        gps.setLongitude(new BigDecimal(106.8212618));
        gps.setStatus("OK");
        return gps;
        
    }
    
}
