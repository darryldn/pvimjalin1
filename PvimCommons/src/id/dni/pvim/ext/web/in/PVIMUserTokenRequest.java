/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.dni.pvim.ext.web.in;

/**
 *
 * @author darryl.sulistyan
 */
public class PVIMUserTokenRequest {
    
    private PVIMAuthToken auth;
    private UserToken token;

    public PVIMAuthToken getAuth() {
        return auth;
    }

    public void setAuth(PVIMAuthToken auth) {
        this.auth = auth;
    }

    public UserToken getToken() {
        return token;
    }

    public void setToken(UserToken token) {
        this.token = token;
    }
    
}
