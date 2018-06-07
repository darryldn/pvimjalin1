/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.dni.pvim.ext.web.rest;

import id.dni.pvim.ext.web.in.PVAuthToken;

/**
 *
 * @author darryl.sulistyan
 */
public class PVGetDeviceIDRequest {
    
    private PVAuthToken auth;
    private String deviceID;

    public PVAuthToken getAuth() {
        return auth;
    }

    public void setAuth(PVAuthToken auth) {
        this.auth = auth;
    }

    public String getDeviceID() {
        return deviceID;
    }

    public void setDeviceID(String deviceID) {
        this.deviceID = deviceID;
    }
    
    
    
}
