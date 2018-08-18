/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package springstuff.service.impl;

import id.dni.pvim.ext.repo.db.IDBMachineBasedataRepository;
import id.dni.pvim.ext.repo.db.spec.impl.DeviceGpsBasedataSpecification;
import id.dni.pvim.ext.repo.db.vo.DBMachineBasedataVo;
import id.dni.pvim.ext.repo.exceptions.PvExtPersistenceException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import springstuff.dao.IComponentStateRepository;
import springstuff.dao.spec.GetAllComponentStateWithDeviceDescrSpec;
import springstuff.dao.spec.GetComponentByDeviceIdSpec;
import springstuff.exceptions.RemoteRepositoryException;
import springstuff.json.ComponentStateJson;
import springstuff.json.DeviceComponentStateJson;
import springstuff.json.MachineGpsJson;
import springstuff.model.ComponentStateVo;
import springstuff.service.DeviceComponentService;
import springstuff.service.RemoteDataRepositoryService;

/**
 * Service to send component information periodically to firebase server.
 * component information is the broken components in the ATM.
 * 
 * @author darryl.sulistyan
 */
@Service
public class DeviceComponentServiceImpl implements DeviceComponentService {
    
    private IDBMachineBasedataRepository machineBasedataRepo;
    private IComponentStateRepository componentStateRepo;
    private RemoteDataRepositoryService remoteDataRepositoryService;
    
    @Autowired
    public void setComponentStateRepository(IComponentStateRepository repo) {
        this.componentStateRepo = repo;
    }
    
    @Autowired
    public void setMachineBasedataRepo(IDBMachineBasedataRepository repo) {
        this.machineBasedataRepo = repo;
    }
    
    @Autowired
//    @Qualifier("simpleurlRemoteDataRepositoryService")
    @Qualifier("firebaseRemoteDataRepositoryService")
    public void setRemoteDataRepositoryService(RemoteDataRepositoryService s) {
        this.remoteDataRepositoryService = s;
    }
    
    @Override
    @Transactional(transactionManager = "pvTransactionManager", rollbackFor = PvExtPersistenceException.class)
    public List<ComponentStateVo> getDeviceComponentState(String deviceID) throws PvExtPersistenceException {
        List<ComponentStateVo> components = this.componentStateRepo.query(new GetComponentByDeviceIdSpec(deviceID));
        return components;
    }
    
    @Override
    @Transactional(transactionManager = "pvTransactionManager", rollbackFor = PvExtPersistenceException.class)
    public Map<String, List<ComponentStateVo>> getAllDevices() throws PvExtPersistenceException {
        return getAllDevices(10, 1);
//        List<ComponentStateVo> devices = this.componentStateRepo.query(new GetAllComponentStateWithDeviceDescrSpec());
//        Map<String, List<ComponentStateVo>> result = new HashMap<>();
//        for (ComponentStateVo c : devices) {
//            if (!result.containsKey(c.getDeviceID())) {
//                result.put(c.getDeviceID(), new ArrayList<>());
//            }
//            result.get(c.getDeviceID()).add(c);
//        }
//        return result;
    }

    @Override
    @Transactional(transactionManager = "pvTransactionManager", rollbackFor = PvExtPersistenceException.class)
    public Map<String, List<ComponentStateVo>> getAllDevices(int pageSize, int pageNum) throws PvExtPersistenceException {
        List<ComponentStateVo> devices = this.componentStateRepo.query(new 
                GetAllComponentStateWithDeviceDescrSpec(pageSize, pageNum));
        Map<String, List<ComponentStateVo>> result = new HashMap<>();
        for (ComponentStateVo c : devices) {
            if (!result.containsKey(c.getDeviceID())) {
                result.put(c.getDeviceID(), new ArrayList<>());
            }
            result.get(c.getDeviceID()).add(c);
        }
        return result;
    }
    
    private static class Gps {
        private final String machineID;
        private final MachineGpsJson gps;

        public Gps(String machineID, MachineGpsJson gps) {
            this.machineID = machineID;
            this.gps = gps;
        }

        public String getMachineID() {
            return machineID;
        }

        public MachineGpsJson getGps() {
            return gps;
        }
    }
    
    @Transactional(transactionManager = "pvTransactionManager", rollbackFor = PvExtPersistenceException.class)
    private MachineGpsJson getMachineGps(String deviceID) throws PvExtPersistenceException {
        
        if (deviceID == null) {
            return null;
        }
        
        List<DBMachineBasedataVo> machinegps = machineBasedataRepo.query(
                new DeviceGpsBasedataSpecification(deviceID));
        if (machinegps.isEmpty()) {
            return null;
        }
        
        double latitude = 0.0;
        double longitude = 0.0;
        int cnt = 0;
        for (DBMachineBasedataVo vo : machinegps) {
            if (vo.getReference() == 999916) {
                latitude = Double.parseDouble(vo.getValue());
                cnt++;
            } else if (vo.getReference() == 999915) {
                longitude = Double.parseDouble(vo.getValue());
                cnt++;
            }
            if (cnt >= 2) {
                break;
            }
        }
        
        if (cnt < 2) {
            Logger.getLogger(this.getClass().getName()).log(Level.FINE, 
                    " - Device Error, latitude or longitude data not complete for deviceID: {0}", deviceID);
            return null;
        }
        
        Logger.getLogger(this.getClass().getName()).log(Level.FINE, 
                " - deviceID: {0} latitude: {1} longitude: {2}", 
                new Object[]{deviceID, latitude, longitude});
        
        MachineGpsJson gps = new MachineGpsJson(latitude, longitude);
        return gps;
        
    }
    
    /**
     *
     */
    @Transactional(transactionManager = "pvTransactionManager", rollbackFor = PvExtPersistenceException.class)
    @Scheduled(fixedRateString = "${device.component.send_request_to_parse_server_interval}")
    @Override
    public void sendToParseServer() throws PvExtPersistenceException {
        
        try {
            Map<String, List<ComponentStateVo>> allDevices = getAllDevices();
            List<DeviceComponentStateJson> devices = new ArrayList<>();
            
            for (Map.Entry<String, List<ComponentStateVo>> e : allDevices.entrySet()) {
                DeviceComponentStateJson device = new DeviceComponentStateJson();
                if (e.getKey() != null) {
                    device.setDeviceid(e.getKey().trim());
                }
                device.setComponents(new ArrayList<>());
                
                List<ComponentStateVo> ev = e.getValue();
                if (ev != null && !ev.isEmpty()) {
                    for (ComponentStateVo c : e.getValue()) {
                        if (c.getComponent() != null) {
                            device.getComponents().add(new ComponentStateJson(c.getComponent().trim(), c.getComponentState()));
                        }
                    }
                    String sLat = ev.get(0).getLatitude();
                    String sLon = ev.get(0).getLongitude();
                    
                    if (sLat != null && sLon != null) {
                        try {
                            double lat = Double.parseDouble(sLat);
                            double lon = Double.parseDouble(sLon);
                            device.setLocation(new MachineGpsJson(lat, lon));
                        } catch (NumberFormatException ex) {
                            
                        }
                    }
                    if (ev.get(0).getDeviceDescr() != null) {
                        device.setDeviceType(ev.get(0).getDeviceDescr().trim());
                    }
                }
                
                devices.add(device);
            }
            
            this.remoteDataRepositoryService.send(devices);
            
        } catch (PvExtPersistenceException ex) {
            throw ex;
            
        } catch (RemoteRepositoryException ex) {
            throw new PvExtPersistenceException(ex);
            
        }
        
    }
    
}
