/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.dni.pv.ext.web.servlet.in;

import id.dni.pvim.ext.web.in.OperationError;

/**
 *
 * @author darryl.sulistyan
 */
public class PvWsDefaultResponse {
    
    private OperationError err;
    private Object response;

    public OperationError getErr() {
        return err;
    }

    public void setErr(OperationError err) {
        this.err = err;
    }

    public Object getResponse() {
        return response;
    }

    public void setResponse(Object response) {
        this.response = response;
    }
    
    
    
}
