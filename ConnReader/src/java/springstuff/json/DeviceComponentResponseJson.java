/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package springstuff.json;

import id.dni.pvim.ext.web.in.OperationError;
import java.util.List;

/**
 *
 * @author darryl.sulistyan
 */
public class DeviceComponentResponseJson {
    
    private OperationError err;
    private List<DeviceComponentStateJson> devices;

    public OperationError getErr() {
        return err;
    }

    public void setErr(OperationError err) {
        this.err = err;
    }

    public List<DeviceComponentStateJson> getDevices() {
        return devices;
    }

    public void setDevices(List<DeviceComponentStateJson> devices) {
        this.devices = devices;
    }
    
    
}
