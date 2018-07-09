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
public class PVErrorCodes {
    
    public static final int 
            S_OK = 0,
            E_CONFIG = -99001,
            E_NET = -99002,
            E_INPUT_NO_NOTES = -99003,
            E_INTERNAL_SERVER_ERROR = -99004,
            E_DATABASE_ERROR = -99005,
            E_UNKNOWN_ERROR = -99006;
    
    private PVErrorCodes() {
        
    }
    
}
