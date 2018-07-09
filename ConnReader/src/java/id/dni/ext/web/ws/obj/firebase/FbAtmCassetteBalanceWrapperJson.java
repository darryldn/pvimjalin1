/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.dni.ext.web.ws.obj.firebase;

import id.dni.ext.web.ws.obj.firebase.internal.FbCassetteBalanceJson;

/**
 *
 * @author darryl.sulistyan
 */
public class FbAtmCassetteBalanceWrapperJson {
    
    private FbCassetteBalanceJson cassette_balance;
    private String deviceId;
    
    public FbCassetteBalanceJson getCassette_balance() {
        return cassette_balance;
    }

    public void setCassette_balance(FbCassetteBalanceJson cassette_balance) {
        this.cassette_balance = cassette_balance;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    @Override
    public String toString() {
        return "FbAtmCassetteBalanceWrapperJson{" + "cassette_balance=" + cassette_balance + ", deviceId=" + deviceId + '}';
    }

    
    
    
    
}
