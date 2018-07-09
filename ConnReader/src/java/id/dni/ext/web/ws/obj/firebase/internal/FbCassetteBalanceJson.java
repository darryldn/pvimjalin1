/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.dni.ext.web.ws.obj.firebase.internal;

import id.dni.pvim.ext.dto.PvWsCassette;
import id.dni.pvim.ext.web.in.OperationError;
import java.util.Map;

/**
 *
 * @author darryl.sulistyan
 */
public class FbCassetteBalanceJson {
    private Map<String, PvWsCassette> cassettes;
    private OperationError err;
    private Long timestamp; // use object form to allow null values.

    public Map<String, PvWsCassette> getCassettes() {
        return cassettes;
    }

    public void setCassettes(Map<String, PvWsCassette> cassettes) {
        this.cassettes = cassettes;
    }

    public OperationError getErr() {
        return err;
    }

    public void setErr(OperationError err) {
        this.err = err;
    }

    @Override
    public String toString() {
        return "FbCassetteBalanceJson{" + "cassettes=" + cassettes + ", err=" + err + ", timestamp=" + timestamp + '}';
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }
    
    
    
}
