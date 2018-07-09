/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package springstuff.service.impl.sync.firebaseobj;

import id.dni.ext.web.ws.obj.firebase.FbPvimSlmUserJson;
import id.dni.pvim.ext.repo.db.ISlmUserRepository;
import id.dni.pvim.ext.repo.db.spec.impl.GetPvimUserByIdSpecification;
import id.dni.pvim.ext.repo.exceptions.PvExtPersistenceException;
import java.util.List;
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
@Qualifier("FbSyncSlmUserService")
public class FbSyncSlmUserService implements FirebaseDatabaseObjSynchronizerService {

    private String path;
    private ISlmUserRepository repo;
    
    @Value("${firebase.database.slmuser.root}")
    public void setPath(String path) {
        this.path = path;
    }
    
    @Autowired
    public void setSlmUserRepo(ISlmUserRepository repo) {
        this.repo = repo;
    }
    
    @Override
    public boolean deleteObj(Object obj) {
        if (obj instanceof FbPvimSlmUserJson) {
            FbPvimSlmUserJson user = (FbPvimSlmUserJson) obj;
            String id = user.getUserId();
            try {
                List l = this.repo.query(new GetPvimUserByIdSpecification(id));
                return l == null || l.isEmpty();
            } catch (PvExtPersistenceException ex) {
                Logger.getLogger(FbSyncATMsService.class.getName()).log(Level.SEVERE, null, ex);
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
        return FbPvimSlmUserJson.class;
    }
    
}
