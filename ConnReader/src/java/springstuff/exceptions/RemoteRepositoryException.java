/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package springstuff.exceptions;

/**
 *
 * @author darryl.sulistyan
 */
public class RemoteRepositoryException extends Exception {

    public RemoteRepositoryException() {
    }

    public RemoteRepositoryException(String message) {
        super(message);
    }

    public RemoteRepositoryException(String message, Throwable cause) {
        super(message, cause);
    }

    public RemoteRepositoryException(Throwable cause) {
        super(cause);
    }
    
}
