/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.dni.ext.web.ws.obj.firebase;

import java.util.Map;

/**
 * Java class representation of the JSON data of ATM / device in Firebase.
 * @author darryl.sulistyan
 */
public class FbDeviceJson {
    
    private String deviceID;
    private String name;
    
    // in Firebase sample data, the type is an integer!
    // already changed to string data!
    private String type;
    
    // only component errors
    //  contents would be like:
    /*
    status {
        "Cardless Reader" : "FAIL",
        "EPP Pin Pad" : "FAIL",
        "camera fascia" : "FAIL",
        ....
    }
    If no error, this entry does not exist in firebase (simply null)
    */
    private Map<String, Object> status;
    private long timestamp;

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getDeviceID() {
        return deviceID;
    }

    public void setDeviceID(String deviceID) {
        this.deviceID = deviceID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Map<String, Object> getStatus() {
        return status;
    }

    public void setStatus(Map<String, Object> status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "FbDeviceJson{" + "deviceID=" + deviceID + ", name=" + name + ", type=" + type + ", status=" + status + ", timestamp=" + timestamp + '}';
    }
    
    
    
}
