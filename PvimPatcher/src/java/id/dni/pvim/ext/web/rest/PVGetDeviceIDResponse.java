/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.dni.pvim.ext.web.rest;

import id.dni.pvim.ext.db.vo.DBMachineGpsVo;
import id.dni.pvim.ext.web.in.OperationError;

/**
 *
 * @author darryl.sulistyan
 */
public class PVGetDeviceIDResponse {
    
    private OperationError err;
    private DBMachineGpsVo location;

    public OperationError getErr() {
        return err;
    }

    public void setErr(OperationError err) {
        this.err = err;
    }

    public DBMachineGpsVo getLocation() {
        return location;
    }

    public void setLocation(DBMachineGpsVo location) {
        this.location = location;
    }
    
    
    
}
