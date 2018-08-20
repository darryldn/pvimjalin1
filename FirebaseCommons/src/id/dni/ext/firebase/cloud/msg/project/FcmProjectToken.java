/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.dni.ext.firebase.cloud.msg.project;

/**
 *
 * @author darryl.sulistyan
 */
public class FcmProjectToken {
    
    private String fcmUrl;
    private String authTokenStr;

    public String getAuthTokenStr() {
        return authTokenStr;
    }

    public void setAuthTokenStr(String authTokenStr) {
        this.authTokenStr = authTokenStr;
    }

    public String getFcmUrl() {
        return fcmUrl;
    }

    public void setFcmUrl(String fcmUrl) {
        this.fcmUrl = fcmUrl;
    }
    
    
    
}
