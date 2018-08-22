/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.dni.ext.dmz.json;

import id.dni.ext.firebase.cloud.msg.json.FcmMessageJson;
import id.dni.pvim.ext.web.in.PVIMAuthToken;

/**
 *
 * @author darryl.sulistyan
 */
public class FcmMessageWrapperJson {
    
    private PVIMAuthToken auth;
    private FcmMessageJson msg;

    public PVIMAuthToken getAuth() {
        return auth;
    }

    public void setAuth(PVIMAuthToken auth) {
        this.auth = auth;
    }

    public FcmMessageJson getMsg() {
        return msg;
    }

    public void setMsg(FcmMessageJson msg) {
        this.msg = msg;
    }
    
    
    
}
