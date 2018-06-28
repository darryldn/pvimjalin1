/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package springstuff.service;

import id.dni.pvim.ext.repo.exceptions.PvExtPersistenceException;
import java.util.List;
import java.util.Map;
import springstuff.model.ComponentStateVo;

/**
 *
 * @author darryl.sulistyan
 */
public interface DeviceComponentService {
    
    public List<ComponentStateVo> getDeviceComponentState(String deviceID) throws PvExtPersistenceException;
    
    public Map < String, List<ComponentStateVo> > getAllDevices() throws PvExtPersistenceException;
    
    public void sendToParseServer() throws PvExtPersistenceException;
    
}
