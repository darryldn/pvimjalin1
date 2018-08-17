/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package springstuff.json;

import id.dni.ext.web.ws.obj.firebase.FbPvimSlmUserJson;
import id.dni.pvim.ext.web.in.OperationError;

/**
 *
 * @author darryl.sulistyan
 */
public class PVIMGetUserResponse {
    
    private OperationError err;
    private FbPvimSlmUserJson user;

    public OperationError getErr() {
        return err;
    }

    public void setErr(OperationError err) {
        this.err = err;
    }

    public FbPvimSlmUserJson getUser() {
        return user;
    }

    public void setUser(FbPvimSlmUserJson user) {
        this.user = user;
    }
    
    
    
    
}
