/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.dni.pvim.ext.web.rest;

import id.dni.pvim.ext.db.dao.DBMachineGpsDao;
import id.dni.pvim.ext.web.in.OperationError;

/**
 *
 * @author darryl.sulistyan
 */
public class PVGetDeviceIDResponse {
    
    private OperationError err;
    private DBMachineGpsDao location;

    public OperationError getErr() {
        return err;
    }

    public void setErr(OperationError err) {
        this.err = err;
    }

    public DBMachineGpsDao getLocation() {
        return location;
    }

    public void setLocation(DBMachineGpsDao location) {
        this.location = location;
    }
    
    
    
}
