/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.dni.pvim.ext.net;

import id.dni.pvim.ext.web.in.OperationError;
import java.util.List;

/**
 *
 * @author darryl.sulistyan
 */
public class SendTicketRemoteResponseJson {
    
    private OperationError err;
    private List<RemoteMessagingResult> result;

    public OperationError getErr() {
        return err;
    }

    public void setErr(OperationError err) {
        this.err = err;
    }

    public List<RemoteMessagingResult> getResult() {
        return result;
    }

    public void setResult(List<RemoteMessagingResult> result) {
        this.result = result;
    }

    @Override
    public String toString() {
        return "SendTicketRemoteResponseJson{" + "err=" + err + ", result=" + result + '}';
    }
    
    
}
