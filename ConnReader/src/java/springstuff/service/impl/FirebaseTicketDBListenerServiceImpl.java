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
import id.dni.ext.web.ws.obj.RestTicketDto;
import id.dni.ext.web.ws.obj.firebase.FbTicketDto;
import id.dni.pvim.ext.err.PVIMErrorCodes;
import id.dni.pvim.ext.repo.exceptions.PvExtPersistenceException;
import id.dni.pvim.ext.web.in.OperationError;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import springstuff.exceptions.RemoteWsException;
import springstuff.model.PvimTicketVo;
import springstuff.service.RemoteDataRepositoryListenerService;
import springstuff.service.firebase.FirebaseDatabaseReferenceService;
import springstuff.service.IPvimTicketWebService;
import springstuff.dao.IPvimTicketMonitorRepository;

/**
 *
 * @author darryl.sulistyan
 */
@Service
@Qualifier("FirebaseTicketDBListenerServiceImpl")
public class FirebaseTicketDBListenerServiceImpl implements RemoteDataRepositoryListenerService {
    
    private FirebaseDatabaseReferenceService firebaseDB;
    private String ticketsFirebaseDBPath;
    private IPvimTicketWebService ticketWs;
    private IPvimTicketMonitorRepository syncTicketRepo;
    private DatabaseReference.CompletionListener resetEngineerIdCallback;
    
    @Autowired
    public void setFirebaseDatabaseReferenceService(FirebaseDatabaseReferenceService firebase) {
        this.firebaseDB = firebase;
    }
    
    @Autowired
    public void setTicketWebService(IPvimTicketWebService ws) {
        this.ticketWs = ws;
    }
    
    @Autowired
    public void setSyncTicketRepo(IPvimTicketMonitorRepository remote) {
        this.syncTicketRepo = remote;
    }
    
    @Value("${firebase.database.ticket.root}")
    public void setTicketsFirebaseDBPath(String path) {
        this.ticketsFirebaseDBPath = path;
    }
    
    void setResetEngineerIdCallback(DatabaseReference.CompletionListener callback) {
        this.resetEngineerIdCallback = callback;
    }
    
    private OperationError extractFromRemoteWsException(RemoteWsException ex) {
        Throwable t = ex.getCause();
        OperationError err = new OperationError();
        if (t != null && t instanceof com.wn.econnect.inbound.wsi.ticket.PvimWSException) {
            com.wn.econnect.inbound.wsi.ticket.PvimWSException pvex = 
                    (com.wn.econnect.inbound.wsi.ticket.PvimWSException) t;
            err.setErrCode(pvex.getFaultInfo().getErrorCode().getValue());
            err.setErrMsg(pvex.getFaultInfo().getErrorMsg().getValue());
        } else {
            err.setErrCode(PVIMErrorCodes.E_UNKNOWN_ERROR + "");
            err.setErrMsg("Internal server error, please contact administrator");
        }
        return err;
    }
    
    @PostConstruct
    public void init() {
        final DatabaseReference db = this.firebaseDB.getDatabaseReference(this.ticketsFirebaseDBPath);
        
        final IPvimTicketMonitorRepository remoteRepo = this.syncTicketRepo;
        db.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot ds, String prevChildKey) {
                // does nothing
                // On startup, this downloads entire subtree under the reference path
                // Also when a ticket is added, it also dowloads it.
                // This could cause problem of synchronizing data from firebase to PVIM.
            }

            @Override
            public void onChildChanged(DataSnapshot ds, String prevChildKey) {
                // TODO: implement onChildChanged on firebase child listener
                Logger.getLogger(this.getClass().getName()).log(Level.INFO, "onChildChanged fired");
                
                FbTicketDto dto = ds.getValue(FbTicketDto.class);
                
                // the server will send engineer_id as empty data, or null
                // and it needs to be filled by vendor.
                // this allows filtering updates that are caused by me.
                // Firebase does not discriminate. All callbacks for update is called
                // even if it is self-caused.
                // if this is not done, it can cause feedback loop
                // because updateTicket can change ticket state, and in turn,
                // activates the smsListener (replaced by my telegramListener)
                // sends the ticket back to firebase server (this is expected)
                // and firebase sends update notification to me.
                if (dto != null) {
                    if (dto.getEngineer_id() != null && !"".equals(dto.getEngineer_id())) {
                    
                        RestTicketDto restDto = FbTicketDto.getData(dto);
                        PvimTicketVo syncTicket;
                        RestTicketDto result = null;
                        OperationError err = null;
                        
                        try {
                            result = ticketWs.updateTicket(restDto, null);
                            Logger.getLogger(this.getClass().getName()).log(Level.INFO, 
                                    "onChildChanged, Successfully update ticket via Webservice, returned {0}", result);
                        } catch (RemoteWsException ex) {
                            Logger.getLogger(FirebaseTicketDBListenerServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
                            err = extractFromRemoteWsException(ex);
                        }

                        Logger.getLogger(FirebaseTicketDBListenerServiceImpl.class.getName()).log(Level.INFO, " - continue execution");
                        
                        String ticketNumber = restDto.getTicketNumber();
                        Logger.getLogger(FirebaseTicketDBListenerServiceImpl.class.getName()).log(Level.INFO, " - ticketNumber: " + ticketNumber);
                        String ticketId = dto.getTicketId();
                        Logger.getLogger(FirebaseTicketDBListenerServiceImpl.class.getName()).log(Level.INFO, " - ticketId: " + ticketId);
                        
                        try {
                            syncTicket = remoteRepo.getTicket(ticketNumber);
                            if (syncTicket == null) {
                                syncTicket = new PvimTicketVo();
//                                syncTicket.setTicketNumber(ticketNumber);
                                syncTicket.setTicketId(ticketId);
                            }
                            Logger.getLogger(FirebaseTicketDBListenerServiceImpl.class.getName()).log(Level.INFO, " - result is null?" + (result == null));
                            syncTicket.setSuccessfullyUpdated(result != null);
                            remoteRepo.updateTicket(syncTicket);
                        } catch (PvExtPersistenceException ex) {
                            Logger.getLogger(FirebaseTicketDBListenerServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        
                        // reset engineer_id in firebase
//                        Map<String, Object> um = new HashMap<>();
//                        um.put("engineer_id", "");
                        DatabaseReference.CompletionListener cb;
                        if (FirebaseTicketDBListenerServiceImpl.this.resetEngineerIdCallback != null) {
                            cb = FirebaseTicketDBListenerServiceImpl.this.resetEngineerIdCallback;
                        } else {
                            cb = new DatabaseReference.CompletionListener() {
                                @Override
                                public void onComplete(DatabaseError de, DatabaseReference dr) {
                                    if (de != null) {
                                        Logger.getLogger(FirebaseTicketDBListenerServiceImpl.class.getName())
                                                .log(Level.WARNING, " - Error " + de);
                                    }
                                }
                            };
                        }
                        
                        dto.setErr(err);
                        dto.setEngineer_id("");
                        if (result != null) {
                            dto.setLastupdated(System.currentTimeMillis());
                            FbTicketDto.setData(dto, result);
                        }
                        db.child(ticketId).setValue(dto, cb);
//                        db.child(ticketId).child("engineer_id").setValue("", cb);
                        
                    } else {
                        Logger.getLogger(this.getClass().getName()).log(Level.WARNING, 
                                "onChildChanged, self-caused update handled. Ignore data for {0}!", prevChildKey);
                    }
                } else {
                    Logger.getLogger(this.getClass().getName()).log(Level.WARNING, 
                            "onChildChanged, datasnapshot for {0} is null!", prevChildKey);
                }
                
                Logger.getLogger(FirebaseTicketDBListenerServiceImpl.class.getName()).log(Level.INFO, "<< onChildChanged finished");
            }

            @Override
            public void onChildRemoved(DataSnapshot ds) {
                // does nothing
                // PVIM does not have remove ticket service
//                RestTicketDto ticket = ds.getValue(RestTicketDto.class);
//                Logger.getLogger(this.getClass().getName()).log(Level.WARNING, "onChildRemoved is called, ticket: {0}", ticket);
            }

            @Override
            public void onChildMoved(DataSnapshot ds, String prevChildKey) {
                // does nothing
            }

            @Override
            public void onCancelled(DatabaseError de) {
                // does nothing
            }
        });
        
    }
    
}
