/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package springstuff.json;

import id.dni.pvim.ext.web.in.PVIMAuthToken;

/**
 *
 * @author darryl.sulistyan
 */
public class PVIMGetUserRequest {
    
    private PVIMAuthToken auth;
    private PVIMGetUserRequestPayload user;

    public PVIMAuthToken getAuth() {
        return auth;
    }

    public void setAuth(PVIMAuthToken auth) {
        this.auth = auth;
    }

    public PVIMGetUserRequestPayload getUser() {
        return user;
    }

    public void setUser(PVIMGetUserRequestPayload user) {
        this.user = user;
    }
    
    
    
}
