/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.dni.pvim.ext.db.exception;

/**
 *
 * @author darryl.sulistyan
 */
public class PVIMDBException extends Exception {

    public PVIMDBException() {
    }

    public PVIMDBException(String message) {
        super(message);
    }

    public PVIMDBException(String message, Throwable cause) {
        super(message, cause);
    }

    public PVIMDBException(Throwable cause) {
        super(cause);
    }
    
}
