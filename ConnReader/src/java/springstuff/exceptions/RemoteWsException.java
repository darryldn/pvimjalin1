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
public class RemoteWsException extends Exception {

    public RemoteWsException() {
        super();
    }

    public RemoteWsException(String message) {
        super(message);
    }

    public RemoteWsException(String message, Throwable cause) {
        super(message, cause);
    }

    public RemoteWsException(Throwable cause) {
        super(cause);
    }
    
}
