/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.dni.pvim.ext.web.rest;

import id.dni.pvim.ext.web.in.PVIMAuthToken;
import id.dni.pvim.ext.web.in.PVIMLocation;

/**
 *
 * @author darryl.sulistyan
 */
public class PVIMEngineerLocationRequest {
    
    private PVIMAuthToken auth;
    private PVIMLocation loc;

    public PVIMAuthToken getAuth() {
        return auth;
    }

    public void setAuth(PVIMAuthToken auth) {
        this.auth = auth;
    }

    public PVIMLocation getLoc() {
        return loc;
    }

    public void setLoc(PVIMLocation loc) {
        this.loc = loc;
    }
    
    
    
}
