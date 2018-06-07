/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.dni.pvim.ext.web.rest;

import id.dni.pvim.ext.db.dao.DBMachineGpsDao;
import id.dni.pvim.ext.db.dao.DaoFactory;
import id.dni.pvim.ext.db.dao.IMachineGpsDao;
import id.dni.pvim.ext.db.exception.PvExtPersistenceException;
import id.dni.pvim.ext.err.PVErrorCodes;
import id.dni.pvim.ext.web.in.OperationError;
import id.dni.pvim.ext.web.in.PVAuthToken;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author darryl.sulistyan
 */
public class LocationOperation {
    
    public PVGetDeviceIDResponse getDeviceIDLocation(PVGetDeviceIDRequest request) {
        PVAuthToken auth = request.getAuth(); // not used
        String deviceID = request.getDeviceID();
        PVGetDeviceIDResponse response = new PVGetDeviceIDResponse();
        
        IMachineGpsDao repository = DaoFactory.getInstance().getMachineGpsDao();
        try {
            DBMachineGpsDao machineLocation = repository.getMachineGps(deviceID);
            response.setLocation(machineLocation);
            
        } catch (PvExtPersistenceException ex) {
            Logger.getLogger(LocationOperation.class.getName()).log(Level.SEVERE, null, ex);
            OperationError err = new OperationError();
            err.setErrCode("" + PVErrorCodes.E_DATABASE_ERROR);
            err.setErrMsg(ex.getMessage());
            response.setErr(err);
            
        }
        
        return response;
    }
    
}
