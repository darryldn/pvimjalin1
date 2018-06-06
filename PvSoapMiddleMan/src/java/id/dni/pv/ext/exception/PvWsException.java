/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.dni.pv.ext.exception;

/**
 *
 * @author darryl.sulistyan
 */
public class PvWsException extends Exception {

    public PvWsException() {
    }

    public PvWsException(String message) {
        super(message);
    }

    public PvWsException(String message, Throwable cause) {
        super(message, cause);
    }

    public PvWsException(Throwable cause) {
        super(cause);
    }
    
}
