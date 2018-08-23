/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.dni.ext.firebase.user.msg;

/**
 *
 * @author darryl.sulistyan
 */
public class FbAuthUserServiceResponse {
    
    private boolean success;

    public FbAuthUserServiceResponse() {
        success = false;
    }
    
    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
    
    
    
}
