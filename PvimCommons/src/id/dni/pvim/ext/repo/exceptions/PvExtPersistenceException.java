/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.dni.pvim.ext.repo.exceptions;

/**
 *
 * @author darryl.sulistyan
 */
public class PvExtPersistenceException extends Exception {

    public PvExtPersistenceException() {
    }

    public PvExtPersistenceException(String message) {
        super(message);
    }

    public PvExtPersistenceException(String message, Throwable cause) {
        super(message, cause);
    }

    public PvExtPersistenceException(Throwable cause) {
        super(cause);
    }
    
}
