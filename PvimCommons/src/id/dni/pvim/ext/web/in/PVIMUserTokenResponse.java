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
public class PVIMUserTokenResponse {
    
    private OperationError err;
    private UserToken token;

    public OperationError getErr() {
        return err;
    }

    public void setErr(OperationError err) {
        this.err = err;
    }

    public UserToken getToken() {
        return token;
    }

    public void setToken(UserToken token) {
        this.token = token;
    }

    
    
}
