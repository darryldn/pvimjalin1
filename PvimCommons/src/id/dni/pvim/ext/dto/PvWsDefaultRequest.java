/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.dni.pvim.ext.dto;

import id.dni.pvim.ext.web.in.PVAuthToken;
import java.util.Map;

/**
 *
 * @author darryl.sulistyan
 */
public class PvWsDefaultRequest {
    
    private PVAuthToken auth;
    private Map<String, String> data;

    public PVAuthToken getAuth() {
        return auth;
    }

    public void setAuth(PVAuthToken auth) {
        this.auth = auth;
    }

    public Map<String, String> getData() {
        return data;
    }

    public void setData(Map<String, String> data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "PvWsDefaultRequest{" + "auth=" + auth + ", data=" + data + '}';
    }
    
    
    
    
}
