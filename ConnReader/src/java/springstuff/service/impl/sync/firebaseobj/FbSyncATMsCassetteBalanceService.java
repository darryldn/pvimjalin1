/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package springstuff.service.impl.sync.firebaseobj;

import id.dni.ext.web.ws.obj.firebase.FbAtmCassetteBalanceWrapperJson;
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
@Qualifier("FbSyncATMsCassetteBalanceService")
public class FbSyncATMsCassetteBalanceService implements FirebaseDatabaseObjSynchronizerService {

    private String path;
    private IDeviceRepository repo;
    
    @Value("${firebase.database.machinecassettebalance.root}")
    public void setPath(String path) {
        this.path = path;
    }
    
    @Autowired
    public void setDeviceRepo(IDeviceRepository repo) {
        this.repo = repo;
    }
    
    @Override
    public boolean deleteObj(Object obj) {
        if (obj instanceof FbAtmCassetteBalanceWrapperJson) {
            FbAtmCassetteBalanceWrapperJson device = (FbAtmCassetteBalanceWrapperJson) obj;
            String id = device.getDeviceId();
            try {
                return !this.repo.isDeviceExist(id);
            } catch (PvExtPersistenceException ex) {
                Logger.getLogger(FbSyncATMsCassetteBalanceService.class.getName()).log(Level.SEVERE, null, ex);
                return false;
            }
        }
        return false;
    }

    @Override
    public String getRootPath() {
        return this.path;
    }

    @Override
    public Class<?> getClassObj() {
        return FbAtmCassetteBalanceWrapperJson.class;
    }
    
}
