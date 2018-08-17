/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package springstuff.controller;

import com.google.gson.Gson;
import id.dni.pvim.ext.repo.exceptions.PvExtPersistenceException;
import id.dni.pvim.ext.web.in.OperationError;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import springstuff.json.ComponentStateJson;
import springstuff.json.DeviceComponentResponseJson;
import springstuff.json.DeviceComponentStateJson;
import springstuff.json.MachineGpsJson;
import springstuff.model.ComponentStateVo;
import springstuff.service.DeviceComponentService;

/**
 *
 * @author darryl.sulistyan
 */
@Controller
public class ComponentStateController {
    
    private DeviceComponentService deviceComponentService;
    
    @Autowired
    public void setDeviceComponentService(DeviceComponentService d) {
        this.deviceComponentService = d;
    }
    
    @RequestMapping(value = "/device", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String getDeviceComponents(@RequestParam(value = "id", required = false) String deviceID) {
        DeviceComponentResponseJson response = new DeviceComponentResponseJson();
        try {
            Map<String, List<ComponentStateVo>> result;
            if (deviceID != null) {
                result = new HashMap<>();
                result.put(deviceID, deviceComponentService.getDeviceComponentState(deviceID));
            } else {
                result = this.deviceComponentService.getAllDevices();
            }
            List<DeviceComponentStateJson> list = new ArrayList<>();
            
            for (Map.Entry<String, List<ComponentStateVo>> entry : result.entrySet()) {
                DeviceComponentStateJson ds = new DeviceComponentStateJson();
                ds.setComponents(new ArrayList<>());
                ds.setDeviceid(entry.getKey());
                
                List<ComponentStateVo> entryComponents = entry.getValue();
                ComponentStateVo ev = null;
                for (ComponentStateVo c : entryComponents) {
                    ds.getComponents().add(new ComponentStateJson(c.getComponent(), c.getComponentState()));
                    ev = c;
                }
                
                if (ev != null) {
                    String sLat = ev.getLatitude();
                    String sLon = ev.getLongitude();
                    
                    if (sLat != null && sLon != null) {
                        try {
                            double lat = Double.parseDouble(sLat);
                            double lon = Double.parseDouble(sLon);
                            ds.setLocation(new MachineGpsJson(lat, lon));
                        } catch (NumberFormatException ex) {
                            
                        }
                    }
                    if (ev.getDeviceDescr() != null) {
                        ds.setDeviceType(ev.getDeviceDescr().trim());
                    }
                }
                
                list.add(ds);
            }
            response.setDevices(list);
            
        } catch (PvExtPersistenceException ex) {
            Logger.getLogger(ComponentStateController.class.getName()).log(Level.SEVERE, null, ex);
            OperationError err = new OperationError();
            err.setErrCode("-15001");
            err.setErrMsg(ex.getMessage());
            response.setErr(err);
            
        }
        
        Gson gson = new Gson();
        return gson.toJson(response);
    }
    
}
