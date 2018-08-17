/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.dni.pvim.ext.web.rest;

import id.dni.pvim.ext.db.config.PVIMDBConnectionFactory;
import id.dni.pvim.ext.db.trx.IProViewTrx;
import id.dni.pvim.ext.db.vo.DBMachineGpsVo;
import id.dni.pvim.ext.repo.boot.RepositoryFactory;
import id.dni.pvim.ext.repo.exceptions.PvExtPersistenceException;
import id.dni.pvim.ext.repo.db.vo.DBMachineBasedataVo;
import id.dni.pvim.ext.err.PVErrorCodes;
import id.dni.pvim.ext.err.PVIMErrorCodes;
import id.dni.pvim.ext.repo.db.IDBMachineBasedataRepository;
import id.dni.pvim.ext.repo.db.ISlmLocationRepository;
import id.dni.pvim.ext.repo.db.spec.impl.DeviceGpsBasedataSpecification;
import id.dni.pvim.ext.repo.db.spec.impl.GetPvimEngineerLocationByIdSpecification;
import id.dni.pvim.ext.repo.db.vo.SlmLocationVo;
import id.dni.pvim.ext.web.in.OperationError;
import id.dni.pvim.ext.web.in.PVAuthToken;
import id.dni.pvim.ext.web.in.PVIMAuthToken;
import id.dni.pvim.ext.web.in.PVIMLocation;
import java.math.BigDecimal;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author darryl.sulistyan
 */
public class LocationOperation {
    
    private DBMachineGpsVo getMachineGps(String deviceID) throws PvExtPersistenceException {
        // TODO: Implement the machine gps reading from ProView server DB
        
        // now, just feed dummy data
        if (deviceID == null) {
            return null;
        }
        
        IDBMachineBasedataRepository repo = RepositoryFactory.getInstance().getMachineBasedataRepository();
        List<DBMachineBasedataVo> machinegps = repo.query(new DeviceGpsBasedataSpecification(deviceID));
        if (machinegps.isEmpty()) {
            return null;
        }
        
        double latitude = 0.0;
        double longitude = 0.0;
        int cnt = 0;
        for (DBMachineBasedataVo vo : machinegps) {
            if (vo.getReference() == 999916) {
                latitude = Double.parseDouble(vo.getValue());
                cnt++;
            } else if (vo.getReference() == 999915) {
                longitude = Double.parseDouble(vo.getValue());
                cnt++;
            }
            if (cnt >= 2) {
                break;
            }
        }
        
        if (cnt < 2) {
            Logger.getLogger(this.getClass().getName()).log(Level.FINE, 
                    " - Device Error, latitude or longitude data not complete for deviceID: {0}", deviceID);
            return null;
        }
        
        Logger.getLogger(this.getClass().getName()).log(Level.FINE, 
                " - deviceID: {0} latitude: {1} longitude: {2}", 
                new Object[]{deviceID, latitude, longitude});
        
        DBMachineGpsVo gps = new DBMachineGpsVo();
        gps.setDescription("DUMMY ATM");
        gps.setDeviceID(deviceID);
        gps.setLastupdate(System.currentTimeMillis());
        gps.setLatitude(new BigDecimal(latitude));
        gps.setLongitude(new BigDecimal(longitude));
        gps.setStatus("OK");
        return gps;
        
    }
    
    public PVIMEngineerLocationResponse updateEngineerLocation(PVIMEngineerLocationRequest request) {
        PVIMAuthToken auth = request.getAuth(); // not used
        PVIMLocation reqLoc = request.getLoc();
        
        PVIMEngineerLocationResponse response = new PVIMEngineerLocationResponse();
        if (reqLoc == null) {
            OperationError err = new OperationError();
            err.setErrCode("" + PVIMErrorCodes.E_INPUT_ERROR);
            err.setErrMsg("No Input given");
            response.setErr(err);
            return response;
        }
        
        IProViewTrx pvimTx = PVIMDBConnectionFactory.getInstance().getTransaction();
        boolean isRollback = false;
        boolean isSuccess = false;
        try {
            pvimTx.begin();
            Object trxObject = pvimTx.getTrxConnection();
            ISlmLocationRepository slmLocationRepos = RepositoryFactory.getInstance().getSlmLocationRepository(trxObject);
            SlmLocationVo newLoc = new SlmLocationVo();
            newLoc.setEngineerID(reqLoc.getId());
            newLoc.setLatitude(reqLoc.getLatitude());
            newLoc.setLongitude(reqLoc.getLongitude());
            newLoc.setNotes(reqLoc.getNotes());
            newLoc.setLastUpdated(System.currentTimeMillis());
            
            isSuccess = slmLocationRepos.update(newLoc);
            if (!isSuccess) {
                isSuccess = slmLocationRepos.insert(newLoc);
            }
            
            if (isSuccess) {
                response.setLoc(reqLoc);
            } else {
                OperationError err = new OperationError();
                err.setErrCode("" + PVIMErrorCodes.E_DATABASE_ERROR);
                err.setErrMsg("Failed update location data");
                response.setErr(err);
            }
            
        } catch (PvExtPersistenceException ex) {
            Logger.getLogger(LocationOperation.class.getName()).log(Level.SEVERE, null, ex);
            isRollback = true;
            OperationError err = new OperationError();
            err.setErrCode(PVIMErrorCodes.E_DATABASE_ERROR + "");
            err.setErrMsg("Unable to get location data");
            response.setErr(err);
            
        } finally {
            try {
                if (isRollback) {
                    pvimTx.rollback();
                } else {
                    pvimTx.commit();
                }
                pvimTx.close();
            } catch (PvExtPersistenceException ex) {
                Logger.getLogger(LocationOperation.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        }
        
        return response;
    }
    
    public PVIMEngineerLocationResponse getEngineerLocation(PVIMEngineerLocationRequest request) {
        PVIMAuthToken auth = request.getAuth(); // not used
        PVIMLocation reqLoc = request.getLoc();
        
        PVIMEngineerLocationResponse response = new PVIMEngineerLocationResponse();
        if (reqLoc == null) {
            OperationError err = new OperationError();
            err.setErrCode("" + PVIMErrorCodes.E_INPUT_ERROR);
            err.setErrMsg("No Input given");
            response.setErr(err);
            return response;
        }
        
        IProViewTrx pvimTx = PVIMDBConnectionFactory.getInstance().getTransaction();
        boolean isRollback = false;
        try {
            pvimTx.begin();
            Object trxObject = pvimTx.getTrxConnection();
            ISlmLocationRepository slmLocationRepos = RepositoryFactory.getInstance().getSlmLocationRepository(trxObject);
            List<SlmLocationVo> loclist = slmLocationRepos.query(new GetPvimEngineerLocationByIdSpecification(reqLoc.getId()));
            
            for (SlmLocationVo loc : loclist) {
                PVIMLocation ploc = new PVIMLocation();
                ploc.setId(loc.getEngineerID());
                if (loc.getLatitude() != null) {
                    ploc.setLatitude(loc.getLatitude().doubleValue());
                }
                if (loc.getLongitude() != null) {
                    ploc.setLongitude(loc.getLongitude().doubleValue());
                }
                ploc.setNotes(loc.getNotes());
                response.setLoc(ploc);
                
                break;
            }
            
            if (response.getLoc() == null) {
                OperationError err = new OperationError();
                err.setErrCode(PVIMErrorCodes.E_INPUT_ERROR + "");
                err.setErrMsg("No Engineer found");
                response.setErr(err);
            }
            
        } catch (PvExtPersistenceException ex) {
            Logger.getLogger(LocationOperation.class.getName()).log(Level.SEVERE, null, ex);
            isRollback = true;
            OperationError err = new OperationError();
            err.setErrCode(PVIMErrorCodes.E_DATABASE_ERROR + "");
            err.setErrMsg("Unable to get location data");
            response.setErr(err);
            
        } finally {
            try {
                if (isRollback) {
                    pvimTx.rollback();
                } else {
                    pvimTx.commit();
                }
                pvimTx.close();
            } catch (PvExtPersistenceException ex) {
                Logger.getLogger(LocationOperation.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        }
        
        return response;
    }
    
    public PVGetDeviceIDResponse getDeviceIDLocation(PVGetDeviceIDRequest request) {
        PVAuthToken auth = request.getAuth(); // not used
        String deviceID = request.getDeviceID();
        PVGetDeviceIDResponse response = new PVGetDeviceIDResponse();
        
        try {
            DBMachineGpsVo machineLocation = /*repository.*/getMachineGps(deviceID);
            if (machineLocation != null) {
                response.setLocation(machineLocation);
            } else {
                OperationError err=  new OperationError();
                err.setErrCode("" + PVErrorCodes.E_CONFIG);
                err.setErrMsg("No GPS information exist for this device");
                response.setErr(err);
            }
            
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
