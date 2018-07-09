/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package springstuff.service.impl;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import id.dni.ext.web.ws.obj.firebase.FbAtmCassetteBalanceWrapperJson;
import id.dni.ext.web.ws.obj.firebase.internal.FbCassetteBalanceJson;
import id.dni.pvim.ext.dto.PvWsCassette;
import id.dni.pvim.ext.web.in.OperationError;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import springstuff.exceptions.CassetteBalanceServiceException;
import springstuff.exceptions.RemoteWsException;
import springstuff.service.IPvWebService;
import springstuff.service.RemoteDataRepositoryListenerService;
import springstuff.service.firebase.FirebaseDatabaseReferenceService;

/**
 *
 * @author darryl.sulistyan
 */
@Service
@Qualifier("FirebaseCassetteBalanceListenerServiceImpl")
public class FirebaseCassetteBalanceListenerServiceImpl implements RemoteDataRepositoryListenerService {
    
    private FirebaseDatabaseReferenceService firebaseDB;
    private String atmDevicesFirebaseDbPath;
    
    private IPvWebService pvws;
    
    @Autowired
    public void setPvWebService(IPvWebService ws) {
        this.pvws = ws;
    }
    
    @Value("${firebase.database.machinecassettebalance.root}")
    public void setAtmDevicesFirebaseDBPath(String path) {
        this.atmDevicesFirebaseDbPath = path;
    }
    
    @Autowired
    public void setFirebaseDatabaseReferenceService(FirebaseDatabaseReferenceService firebase) {
        this.firebaseDB = firebase;
    }
    
    void requestService(FbAtmCassetteBalanceWrapperJson original, String deviceId) {
        FbCassetteBalanceJson cassetteBalance = new FbCassetteBalanceJson();
        
        try {
            List<PvWsCassette> cassetteBalanceList = this.pvws.getCassetteBalance(deviceId);
            Map<String, PvWsCassette> m = new HashMap<>();
            for (PvWsCassette cassette : cassetteBalanceList) {
                m.put(cassette.getCassetteId(), cassette);
            }
            cassetteBalance.setCassettes(m);
        } catch (RemoteWsException ex) {
            Logger.getLogger(FirebaseCassetteBalanceListenerServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
            Throwable cause = ex.getCause();
            OperationError err = null;
            if (cause instanceof CassetteBalanceServiceException) {
                err = ((CassetteBalanceServiceException) cause).getErr();
            }
            if (err == null) {
                err = new OperationError();
                err.setErrCode("-20000");
                err.setErrMsg("Cannot obtain cassete balance information.");
            }
            cassetteBalance.setErr(err);
        }
        
        cassetteBalance.setTimestamp(System.currentTimeMillis());
        original.setDeviceId(deviceId);
        original.setCassette_balance(cassetteBalance);
    }
    
    void handleModification(DatabaseReference db, DataSnapshot ds, String prevKey) {
        
        try {
        String deviceId = ds.getKey();
        Logger.getLogger(this.getClass().getName()).log(Level.INFO, " - handleModification, deviceId: " + deviceId);
        
        FbAtmCassetteBalanceWrapperJson json = ds.getValue(FbAtmCassetteBalanceWrapperJson.class);
        Logger.getLogger(this.getClass().getName()).log(Level.INFO, " - handleModification, FbDeviceJson: " + json);
        if (json != null) {
//            Logger.getLogger(this.getClass().getName()).log(Level.INFO, " - handleModification, FbDeviceJson: " + json);
            FbCassetteBalanceJson cassetteBalance = json.getCassette_balance();
            if (cassetteBalance == null || 
                    cassetteBalance.getTimestamp() == null || cassetteBalance.getTimestamp() == 0 || 
                    cassetteBalance.getCassettes() == null || cassetteBalance.getCassettes().isEmpty()) {
                Logger.getLogger(this.getClass().getName()).log(Level.INFO, " - handleModification, cassetteBalance: " + cassetteBalance);
                requestService(json, deviceId);
                db.child(deviceId)/*.child("cassette_balance")*/.setValue(json/*.getCassette_balance()*/, 
                        new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError de, DatabaseReference dr) {
                        if (de != null) {
                            Logger.getLogger(this.getClass().getName())
                                    .log(Level.SEVERE, "Error exception " + de);
                        }
                    }
                });
            }
        }
        } catch (Exception ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @PostConstruct
    public void init() {
        final DatabaseReference db = this.firebaseDB.getDatabaseReference(this.atmDevicesFirebaseDbPath);
        db.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot ds, String string) {
                handleModification(db, ds, string);
            }

            @Override
            public void onChildChanged(DataSnapshot ds, String string) {
                handleModification(db, ds, string);
            }

            @Override
            public void onChildRemoved(DataSnapshot ds) {
                // does nothing
            }

            @Override
            public void onChildMoved(DataSnapshot ds, String string) {
                // does nothing
            }

            @Override
            public void onCancelled(DatabaseError de) {
                // does nothing
            }
            
        });
        
        
    }
    
}
