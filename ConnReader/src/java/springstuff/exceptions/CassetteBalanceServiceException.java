/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package springstuff.exceptions;

import id.dni.pvim.ext.web.in.OperationError;

/**
 *
 * @author darryl.sulistyan
 */
public class CassetteBalanceServiceException extends Exception {
    
    private final OperationError err;

    public CassetteBalanceServiceException(OperationError err) {
        this.err = err;
    }

    public OperationError getErr() {
        return err;
    }
    
    
    
}
