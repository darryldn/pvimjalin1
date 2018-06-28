/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package springstuff.json;

import java.util.List;

/**
 *
 * @author darryl.sulistyan
 */
public class DeviceComponentStateJson {
    
    private String deviceid;
    private String deviceType;
    private List<ComponentStateJson> components;
    private MachineGpsJson location;

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public String getDeviceid() {
        return deviceid;
    }

    public void setDeviceid(String deviceid) {
        this.deviceid = deviceid;
    }

    public List<ComponentStateJson> getComponents() {
        return components;
    }

    public void setComponents(List<ComponentStateJson> components) {
        this.components = components;
    }

    public MachineGpsJson getLocation() {
        return location;
    }

    public void setLocation(MachineGpsJson location) {
        this.location = location;
    }
    
    
    
}
