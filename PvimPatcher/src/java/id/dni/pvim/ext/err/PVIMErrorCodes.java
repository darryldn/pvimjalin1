/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.dni.pvim.ext.err;

/**
 *
 * @author darryl.sulistyan
 */
public final class PVIMErrorCodes {
 
    public static final int 
            S_OK = 0,
            E_CONFIG = -1,
            E_NET = -2,
            E_INPUT_NO_NOTES = -3,
            E_DATABASE_ERROR = -2400001;
    
    private PVIMErrorCodes() {
        
    }
}
