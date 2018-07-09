/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package springstuff.service.impl.sync.firebaseobj;

import id.dni.ext.web.ws.obj.firebase.FbDeviceJson;
import id.dni.pvim.ext.repo.db.IDeviceRepository;
import id.dni.pvim.ext.repo.exceptions.PvExtPersistenceException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import springstuff.service.FirebaseDatabaseObjSynchronizerService;

/**
 *
 * @author darryl.sulistyan
 */
@Service
@Qualifier("FbSyncATMsService")
public class FbSyncATMsService implements FirebaseDatabaseObjSynchronizerService {

    private String path;
    private IDeviceRepository repo;
    
    @Value("${firebase.database.machinestatus.root}")
    public void setPath(String path) {
        this.path = path;
    }
    
    @Autowired
    public void setDeviceRepo(IDeviceRepository repo) {
        this.repo = repo;
    }
    
    @Override
    public boolean deleteObj(Object obj) {
        Logger.getLogger(this.getClass().getName()).log(Level.INFO, ">> deleteObj(" + obj + ")");
        boolean ret = false;
        try {
            if (obj instanceof FbDeviceJson) {
                FbDeviceJson device = (FbDeviceJson) obj;
                String id = device.getDeviceID();
                Logger.getLogger(this.getClass().getName()).log(Level.INFO, "id = (" + id + ")");
                try {
                    ret = !this.repo.isDeviceExist(id);
                    return ret;
                } catch (PvExtPersistenceException ex) {
                    Logger.getLogger(FbSyncATMsService.class.getName()).log(Level.SEVERE, null, ex);
                    ret = false;
                    return ret;
                }
            }
            ret = false;
            return ret;
            
        } finally {
            Logger.getLogger(this.getClass().getName()).log(Level.INFO, "<< deleteObj(): " + ret);
            
        }
    }

    @Override
    public String getRootPath() {
        return this.path;
    }

    @Override
    public Class<?> getClassObj() {
        return FbDeviceJson.class;
    }
    
}
