/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.dni.pvim.ext.web.rest;

import id.dni.pvim.ext.web.in.OperationError;
import id.dni.pvim.ext.web.in.PVIMLocation;
import java.util.List;

/**
 *
 * @author darryl.sulistyan
 */
public class PVIMEngineerLocationResponse {
    
    private OperationError err;
    private List<PVIMLocation> loc;

    public OperationError getErr() {
        return err;
    }

    public void setErr(OperationError err) {
        this.err = err;
    }

    public List<PVIMLocation> getLoc() {
        return loc;
    }

    public void setLoc(List<PVIMLocation> loc) {
        this.loc = loc;
    }
    
    
    
}
