/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package springstuff.service.impl;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import springstuff.service.FirebaseDatabaseObjSynchronizerService;
import springstuff.service.FirebaseDatabaseSynchronizerService;
import springstuff.service.firebase.FirebaseDatabaseReferenceService;

/**
 *
 * @author darryl.sulistyan
 */
@Service
public class FirebaseDatabaseSynchronizerServiceImpl implements FirebaseDatabaseSynchronizerService {

    private final List<FirebaseDatabaseObjSynchronizerService> syncServiceList = new ArrayList<>();
    private FirebaseDatabaseReferenceService firebaseDB;

    // Add the service implementers here
    @Autowired
    @Qualifier("FbSyncATMsService")
    public void setFirebaseSyncAtmService(FirebaseDatabaseObjSynchronizerService service) {
        this.addSyncServiceList(service);
    }

    @Autowired
    @Qualifier("FbSyncATMsCassetteBalanceService")
    public void setFirebaseCassetteBalanceService(FirebaseDatabaseObjSynchronizerService service) {
        this.addSyncServiceList(service);
    }

    @Autowired
    @Qualifier("FbSyncSlmUserService")
    public void setSlmUserService(FirebaseDatabaseObjSynchronizerService service) {
        this.addSyncServiceList(service);
    }

    @Autowired
    @Qualifier("FbSyncTicketService")
    public void setTicketService(FirebaseDatabaseObjSynchronizerService service) {
        this.addSyncServiceList(service);
    }
    // end

    private void addSyncServiceList(FirebaseDatabaseObjSynchronizerService service) {
        syncServiceList.add(service);
    }

    @Autowired
    public void setFirebaseDatabaseReferenceService(FirebaseDatabaseReferenceService firebase) {
        this.firebaseDB = firebase;
    }

    @Scheduled(cron = "${synchronizer.cron}")
    public void synchronize() {

        for (FirebaseDatabaseObjSynchronizerService service : syncServiceList) {
            String dbRootPath = service.getRootPath();

            final DatabaseReference ref = firebaseDB.getDatabaseReference(dbRootPath);
            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot ds) {
                    Logger.getLogger(this.getClass().getName()).log(Level.INFO, "addListenerForSingleValueEvent in");
                    Logger.getLogger(this.getClass().getName()).log(Level.INFO, "ChildrenCount: {0}", ds.getChildrenCount());

                    for (DataSnapshot childDs : ds.getChildren()) {
                        String childKey = childDs.getKey();
                        Logger.getLogger(this.getClass().getName()).log(Level.INFO, "childKey: {0}", childKey);
                        
                        Class<?> clazz = service.getClassObj();
                        Logger.getLogger(this.getClass().getName()).log(Level.INFO, "clazz2: {0}", clazz);
                        
                        try {
                            // this can throw various exceptions
                            // in particular, if the value cannot be casted, for example, from Long to String
                            // will throw runtime exception.
                            Object res = childDs.getValue(clazz);
                            Logger.getLogger(this.getClass().getName()).log(Level.INFO, "res.class: {0}", (res != null ? res.getClass() : "null"));
                        
                            boolean isDeleted = service.deleteObj(res);
                            Logger.getLogger(this.getClass().getName()).log(Level.INFO, "isDeleted: {0}", isDeleted);

                            if (isDeleted) {
                                ref.child(childKey).removeValue(new DatabaseReference.CompletionListener() {
                                    @Override
                                    public void onComplete(DatabaseError de, DatabaseReference dr) {
                                        if (de != null) {
                                            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, de);
                                        }
                                    }
                                });
                            }
                        } catch (Exception ex) {
                            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
                        }
                    }

                }

                @Override
                public void onCancelled(DatabaseError de) {
                    // does nothing
                }

            });
        }

    }

}
