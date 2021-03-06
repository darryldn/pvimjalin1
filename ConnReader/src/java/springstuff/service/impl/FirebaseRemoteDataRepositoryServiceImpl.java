/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package springstuff.service.impl;

import id.dni.ext.firebase.cloud.msg.json.FcmMessageDownstreamResponseJson;
import id.dni.ext.firebase.cloud.msg.json.FcmMessageJson;
import id.dni.ext.firebase.cloud.msg.json.FcmMessageNotificationJson;
import id.dni.ext.firebase.user.msg.FbAuthUserJson;
import id.dni.ext.web.ws.obj.RestTicketDto;
import id.dni.ext.web.ws.obj.firebase.FbTicketDto;
import id.dni.pvim.ext.net.TransferTicketDto;
import id.dni.pvim.ext.repo.exceptions.PvExtPersistenceException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import springstuff.exceptions.RemoteRepositoryException;
import springstuff.json.DeviceComponentStateJson;
import id.dni.ext.web.ws.obj.firebase.FbPvimSlmUserJson;
import id.dni.pvim.ext.jmsmsg.JmsMsgConstants;
import id.dni.pvim.ext.net.RemoteMessagingResult;
import id.dni.pvim.ext.repo.db.ISlmUserRepository;
import id.dni.pvim.ext.repo.db.ISlmUserTokenRepository;
import id.dni.pvim.ext.repo.db.spec.impl.GetAllPvimUsersSpecification;
import id.dni.pvim.ext.repo.db.spec.impl.GetPvimUserByLoginNameSpecification;
import id.dni.pvim.ext.repo.db.spec.impl.GetPvimUserByMobileSpecification;
import id.dni.pvim.ext.repo.db.spec.impl.GetPvimUserTokenByIdSpecification;
import id.dni.pvim.ext.repo.db.vo.SlmUserTokenVo;
import id.dni.pvim.ext.repo.db.vo.SlmUserVo;
import id.dni.pvim.ext.telegram.commons.sender.MessageSender;
import id.dni.pvim.ext.telegram.repo.ITelegramSuscribersRepository;
import id.dni.pvim.ext.telegram.repo.db.vo.TelegramSubscriberVo;
import id.dni.pvim.ext.telegram.repo.spec.TelegramSubscribersListOfPhonesSpec;
import id.dni.pvim.ext.web.in.Commons;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import springstuff.exceptions.RemoteWsException;
import springstuff.service.AsyncRunnerService;
import springstuff.service.LoginService;
import springstuff.service.RemoteDataRepositoryService;
import springstuff.service.firebase.FirebaseCloudMessagingService;

/**
 * This class assumes that PVIM webservice already works correctly. No need to
 * change the isSuccess in pvimTicketMonitorRepository
 * 
 * Class to send data to firebase server
 *
 * @author darryl.sulistyan
 */
@Service
@Qualifier("firebaseRemoteDataRepositoryService")
public class FirebaseRemoteDataRepositoryServiceImpl implements RemoteDataRepositoryService {

    // firebase db reference
//    private FirebaseDatabaseReferenceService firebaseDB;
    
//    private ITicketRepository ticketRepository; // tiket repository in PVIM
    
    // repository to monitor whether pvim ticket webservice is error or not
//    private IPvimTicketMonitorRepository pvimTicketMonitorRepository; // ticket repository in firebase
    
    // repository for SLM_USER table in pvim database
    private ISlmUserRepository pvimSlmUserRepository;
    
//    private CacheService cacheService;

//    private String gpsFirebaseDBPath;
//    private String machineStatusFirebaseDBPath;
//    private String ticketsFirebaseDBPath;
//    private String pvimSlmUserFirebaseDBPath;
//    private String ticketsNotifFirebaseDBPath;
//    private String ticketChangedFirebaseDBPath;
    
//    private AsyncRunnerService asyncService;
    private int maxWaitTimeForQueryCompletion;

//    @Value("${firebase.database.geofire.root}")
//    public void setGpsFirebaseDBPath(String path) {
//        this.gpsFirebaseDBPath = path;
//    }
//
//    @Value("${firebase.database.machinestatus.root}")
//    public void setMachineStatusFirebaseDBPath(String path) {
//        this.machineStatusFirebaseDBPath = path;
//    }
//
//    @Value("${firebase.database.ticket.root}")
//    public void setTicketsFirebaseDBPath(String path) {
//        this.ticketsFirebaseDBPath = path;
//    }
//    
//    @Value("${firebase.database.ticketnotiftrigger.root}")
//    public void setTicketsNotifFirebaseDBPath(String path) {
//        this.ticketsNotifFirebaseDBPath = path;
//    }
//    
//    @Value("${firebase.database.slmuser.root}")
//    public void setPvimSlmUserFirebaseDBPath(String path) {
//        this.pvimSlmUserFirebaseDBPath = path;
//    }
    
//    @Value("${firebase.database.ticketchangedtrigger.root}")
//    public void setTicketChangedFirebaseDBPath(String path) {
//        this.ticketChangedFirebaseDBPath = path;
//    }

//    @Autowired
//    public void setRemoteTicketRepository(IPvimTicketMonitorRepository remote) {
//        this.pvimTicketMonitorRepository = remote;
//    }

    // unused function
//    @Autowired
//    public void setTicketRepository(ITicketRepository repo) {
////        this.ticketRepository = repo;
//    }

//    @Autowired
//    public void setFirebaseDB(FirebaseDatabaseReferenceService db) {
//        this.firebaseDB = db;
//    }
    
    @Autowired
    public void setPvimSlmUserRepository(ISlmUserRepository repo) {
        this.pvimSlmUserRepository = repo;
    }
    
//    @Autowired(required = false)
//    public void setCacheService(CacheService cache) {
//        this.cacheService = cache;
//    }

//    @Autowired
//    public void setAsyncRunnerService(AsyncRunnerService service) {
//        this.asyncService = service;
//    }
    @Value("${firebase.database.max_query_wait_time}")
    public void setMaxWaitTimeForQueryCompletion(String time) {
        try {
            if (time == null || "".equals(time.trim())) {
                this.maxWaitTimeForQueryCompletion = 10000;
            } else {
                this.maxWaitTimeForQueryCompletion = Integer.parseInt(time);
                if (this.maxWaitTimeForQueryCompletion < 0) {
                    this.maxWaitTimeForQueryCompletion = 10000;
                }
            }
        } catch (NumberFormatException ex) {
            this.maxWaitTimeForQueryCompletion = 10000;
        }
    }
    
    private FirebaseCloudMessagingService fcmService;
    
    @Autowired
    public void setFirebaseCloudMessagingService(FirebaseCloudMessagingService s) {
        this.fcmService = s;
    }
    
    private ISlmUserTokenRepository userTokenRepo;
    
    @Autowired
    public void setSlmUserTokenRepository(ISlmUserTokenRepository userTokenRepo) {
        this.userTokenRepo = userTokenRepo;
    }
    
    private ITelegramSuscribersRepository telegramRepos;
    
    @Autowired
    public void setTelegramSubscribersRepository(ITelegramSuscribersRepository telegramRepos) {
        this.telegramRepos = telegramRepos;
    }
    
    private AsyncRunnerService async;
    
    @Autowired
    public void setAsyncRunnerService(AsyncRunnerService service) {
        this.async = service;
    }
    
    private LoginService loginService;
    
    @Autowired
    public void setLoginService(LoginService loginService) {
        this.loginService = loginService;
    }
    
    private String loginUrl;
    private int timeout;

    @Value("${Login.url}")
    public void setLoginUrl(String l) {
        this.loginUrl = l;
    }

    @Value("${Login.timeout}")
    public void setLoginTimeout(String t) {
        try {
            timeout = Integer.parseInt(t);
        } catch (NumberFormatException ex) {
            timeout = 3000;
        }
    }
    
    private String notifTitle;
    private String notifPrio;
    
    @Value("${Notification.title}")
    public void setNotifTitle(String t) {
        this.notifTitle = t;
    }
    
    @Value("${Notification.priority}")
    public void setNotifPrio(String prio) {
        this.notifPrio = prio;
    }
    

    /**
     * Remove tickets in firebase database.
     * This function assumes that the structure is:
     * /tickets {
     *      /<ticket ID>: {
     *          24 values, see RestTicketDto class for entries.
     *      },
     *      ....
     * }
     * Ticket ID =/= ticket number. Ticket ID is mapped to TICKET_ID column in TICKET table
     * while ticket number is mapped to TICKET_NUMBER.
     * Ticket number contains dot (.) character which is forbidden in path (or object key) in
     * firebase. TicketID is purely alphanumeric 32 digit.
     * 
     * This function just loops for every tickets, and deletes entry under /tickets/<ticketID>/*
     * 
     * @param tickets
     * @throws RemoteRepositoryException 
     */
    @Override
    public void removeTickets(List<TransferTicketDto> tickets) throws RemoteRepositoryException {
//        final DatabaseReference db = this.firebaseDB.getDatabaseReference(this.ticketsFirebaseDBPath);
//        final IPvimTicketMonitorRepository remoteRepo = this.pvimTicketMonitorRepository;
//
//        for (TransferTicketDto ticket : tickets) {
//            RestTicketDto rest = RestTicketDto.convert(ticket.getTicketMap());
//            final String ticketId = ticket.getTicketId();
//            db.child(ticketId).removeValue( // ticketNumber contains (.) character, forbidden by Firebase as path!
//                    new DatabaseReference.CompletionListener() {
//                @Override
//                public void onComplete(DatabaseError de, DatabaseReference dr) {
//                    if (de == null) {
//                        try {
////                            remoteRepo.removeTicket(remoteRepo.getTicket(rest.getTicketNumber()));
//                            remoteRepo.removeTicket(remoteRepo.getTicket(ticketId));
//                        } catch (PvExtPersistenceException ex) {
//                            Logger.getLogger(FirebaseRemoteDataRepositoryServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
//                        }
//                    } else {
//                        Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, de);
//                    }
//                }
//            });
//
//        }
    }

    // ticket in firebase is planned to be removed. So, this is commented out
    @Override
    //@Scheduled(fixedRateString = "${TicketService.firebase.update.timestamp.interval}")
    public void periodicUpdateLastupdate() {
//        Logger.getLogger(this.getClass().getName()).log(Level.INFO, ">> periodicUpdateLastupdate()");
//        try {
//            List<PvimTicketVo> tickets = pvimTicketMonitorRepository.getAllTickets();
//
//            if (!tickets.isEmpty()) {
//                final DatabaseReference db = this.firebaseDB.getDatabaseReference(this.ticketsFirebaseDBPath);
//                for (PvimTicketVo ticket : tickets) {
//                    Logger.getLogger(this.getClass().getName()).log(Level.INFO, " - reading ticket: {0}", ticket);
//                    if (ticket.isSuccessfullyUpdated()) {
//                        final long lastupdated = System.currentTimeMillis();
//                    ticket.setLastupdated(System.currentTimeMillis());
////                    ticketsDb.child(ticket.getTicketNumber()).child("lastupdated").setValue(ticket.getLastupdated(), 
//                        db.child(ticket.getTicketId()).child("lastupdated").setValue(lastupdated,
//                                new DatabaseReference.CompletionListener() {
//                            @Override
//                            public void onComplete(DatabaseError de, DatabaseReference dr) {
//                                if (de == null) {
//                                    try {
//                                        pvimTicketMonitorRepository.updateTicket(ticket);
//                                    } catch (PvExtPersistenceException ex) {
//                                        Logger.getLogger(FirebaseRemoteDataRepositoryServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
//                                    }
//                                } else {
//                                    Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, de);
//                                }
//                            }
//                        });
//                    }
//                }
//            } else {
//                Logger.getLogger(this.getClass().getName()).log(Level.INFO, " - ticket is empty!");
//            }
//
//        } catch (PvExtPersistenceException ex) {
//            Logger.getLogger(FirebaseRemoteDataRepositoryServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
//        } finally {
//            Logger.getLogger(this.getClass().getName()).log(Level.INFO, "<< periodicUpdateLastupdate()");
//        }

    }

//    private static class DumbFirebaseCompletionListener implements DatabaseReference.CompletionListener {
//
//        private final String message;
//
//        public DumbFirebaseCompletionListener(String message) {
//            this.message = message;
//        }
//
//        public DumbFirebaseCompletionListener() {
//            this(null);
//        }
//
//        @Override
//        public void onComplete(DatabaseError de, DatabaseReference dr) {
//            if (de != null) {
//                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, de);
//            } else if (this.message != null) {
//                Logger.getLogger(this.getClass().getName()).log(Level.INFO, this.message);
//            }
//        }
//
//    }

    @Override
    public void send(List<DeviceComponentStateJson> devices) throws RemoteRepositoryException {
//
//        if (this.gpsFirebaseDBPath != null && !"".equals(this.gpsFirebaseDBPath.trim())) {
//
////            GeoFire geoFire = new GeoFire(this.firebaseDB.getDatabaseReference(this.gpsFirebaseDBPath));
//            for (DeviceComponentStateJson device : devices) {
//
////                // although geoFire.setLocation is async as it is, it is important
////                // to run this via asyncService to limit the number of parallel
////                // threads within a manageable number, via executor pool.
//                // No need to do that. In the background, Firebase database already limits
//                // the number of threads in the background.
////                final int maxWaitQuery = this.maxWaitTimeForQueryCompletion;
////                this.asyncService.run(new Runnable() {
////                    
////                    private final Object lock = new Object();
////                    
////                    @Override
////                    public void run() {
////                        // this is async call
////                        // geofire does not support bulk uploads, unfortunatelly
////                        // because it is not meant to be used this way!!
//                if (device.getLocation() != null) {
//                    try {
//                        // no need to check valid key here because it will be
//                        // captured in Exception ex below.
////                        geoFire.setLocation(device.getDeviceid().trim(),
////                                new GeoLocation(device.getLocation().getLatitude(),
////                                        device.getLocation().getLongitude()),
////                                new GeoFire.CompletionListener() {
////                            @Override
////                            public void onComplete(String string, DatabaseError de) {
////                                if (de != null) {
////                                    Logger.getLogger(this.getClass().getName()).log(Level.WARNING,
////                                            "Failed send data on device: {0} with error: {1}",
////                                            new Object[]{device.getDeviceid(), de});
////                                } else {
////                                    Logger.getLogger(this.getClass().getName()).log(Level.INFO,
////                                            "Device {0} is saved successfully!",
////                                            new Object[]{device.getDeviceid()});
////                                }
////        //                                synchronized(lock) {
////        //                                    lock.notifyAll();
////        //                                }
////                            }
////                        });
//                    } catch (Exception ex) {
//                        // geofire throws IllegalArgumentException if latitude / longitude is error
//                        // any other error, must not be thrown. Just log it and ignore. May not influence
//                        // other devices
//                        
//                        Logger.getLogger(this.getClass().getName()).log(Level.WARNING,
//                                String.format("Failed send data on device: %s with error: %s",
//                                device.getDeviceid(), ex.getMessage()), ex);
//                    }
//                }
////                        
////                        // method must block here.
////                        // if this does not block, the guard of number of threads
////                        // from asyncService will be useless.
////                        synchronized(lock) {
////                            try {
////                                lock.wait(maxWaitQuery);
////                            } catch (InterruptedException ex) {
////                            }
////                        }
////                    }
////                });
//
//            }
//
//        }

//        if (this.machineStatusFirebaseDBPath != null && !"".equals(this.machineStatusFirebaseDBPath.trim())) {
//            final DatabaseReference db = this.firebaseDB.getDatabaseReference(this.machineStatusFirebaseDBPath);
//            final Map<String, Object> fbDevices = new HashMap<>();
//
//            for (DeviceComponentStateJson device : devices) {
//                FbDeviceJson fbDevice = new FbDeviceJson();
//
//                if (device.getDeviceid() != null) {
//                    fbDevice.setDeviceID(device.getDeviceid().trim());
//                }
//                if (device.getDeviceType() != null) {
//                    fbDevice.setType(device.getDeviceType().trim());
//                }
//                fbDevice.setTimestamp(System.currentTimeMillis());
//
//                Map<String, Object> deviceStatus = new HashMap<>();
//                List<ComponentStateJson> deviceComponents = device.getComponents();
//                for (ComponentStateJson component : deviceComponents) {
//                    if (component.getComponent() != null) {
//                        deviceStatus.put(component.getComponent().trim(), component.getState()); // remove trailing spaces
//                    }
//                }
//                fbDevice.setStatus(deviceStatus);
//                
//                String deviceID = fbDevice.getDeviceID().trim();
//                if (FirebaseUtil.isValidKey(deviceID)) {
//                    fbDevices.put(deviceID, fbDevice);
//                }
//
//            }
//
////            final int maxWaitQuery = this.maxWaitTimeForQueryCompletion;
////            this.asyncService.run(new Runnable() {
////                
////                private final Object lock = new Object();
////                
////                @Override
////                public void run() {
//            Logger.getLogger(this.getClass().getName()).log(Level.INFO, " - devices: {0}", fbDevices);
////                    
//
//            db.updateChildren(fbDevices, new DumbFirebaseCompletionListener("Successfully update ATM information!"));
//
////            ticketsDb.updateChildren(fbDevices, new DatabaseReference.CompletionListener() {
////                @Override
////                public void onComplete(DatabaseError de, DatabaseReference dr) {
////                    if (de != null) {
////                        Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, de);
////                    } else {
////                        Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Successfully update ATM information!");
////                    }
//////                            synchronized(lock) {
//////                                lock.notifyAll();
//////                            }
////                }
////            });
////                    synchronized(lock) {
////                        try {
////                            lock.wait(maxWaitQuery);
////                        } catch (InterruptedException ex) {
////                        }
////                    }
////                }
////                
////            });
//        }

    }

    Map<String, Object> constructFbTickets(List<TransferTicketDto> tickets) {
        Map<String, Object> fbTickets = new HashMap<>();

        for (TransferTicketDto ticket : tickets) {
            FbTicketDto fbTicket = new FbTicketDto();
            RestTicketDto rest = RestTicketDto.convert(ticket.getTicketMap());
//            fbTicket.setData(rest);
            FbTicketDto.setData(fbTicket, rest);
            fbTicket.setLastupdated(ticket.getLastupdated());
//            fbTicket.setPreviousNotes(rest.getNote());
            fbTicket.setEngineer_id(null); // MUST BE NULL! will be set to non null by vendor app
            fbTicket.setTicketId(ticket.getTicketId());
            fbTickets.put(fbTicket.getTicketId(), fbTicket);
        }

        return fbTickets;
    }
    
//    private void setEngineerIdInAtmFirebase(DatabaseReference ref, String keyID, String key, String assigned) {
//        if (FirebaseUtil.isValidKey(keyID) && FirebaseUtil.isValidKey(key)) {
//            ref.child(keyID).child(key).setValue(assigned, 
//                    new DatabaseReference.CompletionListener() {
//                @Override
//                public void onComplete(DatabaseError de, DatabaseReference dr) {
//                    if (de != null) {
//                    Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, de);
//                }
//                }
//            });
//        } else {
//            Logger.getLogger(this.getClass().getName()).log(Level.WARNING, 
//                    "Invalid Key {0} or {1} is found. Please only use alphanumeric characters", 
//                    new Object[]{keyID, key});
//        }
//    }
    
//    private static class TicketPair {
//        private String ticketId;
//        private long timestamp;
//
//        public TicketPair(String ticketId, long timestamp) {
//            this.ticketId = ticketId;
//            this.timestamp = timestamp;
//        }
//
//        public String getTicketId() {
//            return ticketId;
//        }
//
//        public void setTicketId(String ticketId) {
//            this.ticketId = ticketId;
//        }
//
//        public long getTimestamp() {
//            return timestamp;
//        }
//
//        public void setTimestamp(long timestamp) {
//            this.timestamp = timestamp;
//        }
//    }
    
    

    @Override
    public List<RemoteMessagingResult> sendTickets(final List<TransferTicketDto> tickets) throws RemoteRepositoryException {
        if (tickets == null || tickets.isEmpty()) {
            return Collections.EMPTY_LIST;
        }
        
        Logger.getLogger(FirebaseRemoteDataRepositoryServiceImpl.class.getName()).log(Level.INFO, 
                " - processing tickets: {0}", tickets.size());
        
        List<Future<RemoteMessagingResult>> asyncResults = new ArrayList<>();
        for (TransferTicketDto rawTicket : tickets) {
            Logger.getLogger(FirebaseRemoteDataRepositoryServiceImpl.class.getName()).log(Level.INFO, 
                    " - processing ticket: {0}", rawTicket.getTicketId());
            
            try {
                RestTicketDto rest = RestTicketDto.convert(rawTicket.getTicketMap());

                // read context
                String context = rawTicket.getContext();
                List<SlmUserVo> contacts;

                // if dispatch prefix, send to assigned user
                if (context.startsWith(JmsMsgConstants.DISPATCH_PREFIX)) {
                    contacts = this.pvimSlmUserRepository.query(new GetPvimUserByLoginNameSpecification(rest.getAssignee()));
                    context = context.substring(JmsMsgConstants.DISPATCH_PREFIX.length());
                    
                } else {

                    contacts = new ArrayList<>();
                    // if not dispatch prefix, send to list of accounts
                    for (String account : rawTicket.getAccountList()) {
                        List<SlmUserVo> at = this.pvimSlmUserRepository.query(new GetPvimUserByMobileSpecification(account));
                        contacts.addAll(at);
                    }
                }
                
                Logger.getLogger(FirebaseRemoteDataRepositoryServiceImpl.class.getName()).log(Level.INFO, 
                    " - context: {0}", context);
                
                Logger.getLogger(FirebaseRemoteDataRepositoryServiceImpl.class.getName()).log(Level.INFO, 
                    " - processing contacts: {0}", contacts.size());

                // contents: context w/o prefix
                for (SlmUserVo contact : contacts) {
                    Logger.getLogger(FirebaseRemoteDataRepositoryServiceImpl.class.getName()).log(Level.INFO, 
                            " - processing contact: {0}", contact.getLoginName());
                    
                    List<SlmUserTokenVo> tokenList = this.userTokenRepo.query(
                            new GetPvimUserTokenByIdSpecification(contact.getUserID()));
                    
                    Logger.getLogger(FirebaseRemoteDataRepositoryServiceImpl.class.getName()).log(Level.INFO, 
                            " - processing FCM Tokens: {0}", tokenList.size());
                    
                    for (SlmUserTokenVo token : tokenList) {
                        Logger.getLogger(FirebaseRemoteDataRepositoryServiceImpl.class.getName()).log(Level.INFO, 
                                " - processing token: {0}", token.getMessageToken());
                        
                        String to = token.getMessageToken();
                        FcmMessageJson msg = new FcmMessageJson();
                        msg.setTo(to);
                        msg.setPriority(notifPrio);
                        
                        FcmMessageNotificationJson notif = new FcmMessageNotificationJson();
                        notif.setTitle(notifTitle);
                        notif.setBody(context);
                        msg.setNotification(notif);
                        
                        final SlmUserVo cc = contact;
                        final String cx = context;
                        
                        Future<RemoteMessagingResult> doAsync = this.async.doAsync(new Callable<RemoteMessagingResult>() {
                            @Override
                            public RemoteMessagingResult call() throws Exception {
                                try {
                                    FcmMessageDownstreamResponseJson sendMessage = fcmService.sendMessage(msg);
                                    if (sendMessage.getSuccess() == 1) {
                                        return new RemoteMessagingResult(cc.getMobile(), 
                                                sendMessage.getMulticast_id(), cx, JmsMsgConstants.S_OK, "FCM");
                                    } else {
                                        return new RemoteMessagingResult(cc.getMobile(), 
                                                sendMessage.getMulticast_id(), cx, JmsMsgConstants.E_ERR, "FCM");
                                    }
                                } catch (Exception ex) {
                                    Logger.getLogger(FirebaseRemoteDataRepositoryServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
                                    return new RemoteMessagingResult(cc.getMobile(), 0, cx, JmsMsgConstants.E_ERR, "FCM");
                                }
                            }
                        });
                        asyncResults.add(doAsync);
                    }
                    
                }
                
                List<String> realPhones = new ArrayList<>();
                
                for (SlmUserVo contact : contacts) {
                    if (contact != null && !Commons.isEmptyStrIgnoreSpaces(contact.getMobile())) {
                        realPhones.add(contact.getMobile());
                    }
                }
                
                Logger.getLogger(FirebaseRemoteDataRepositoryServiceImpl.class.getName()).log(Level.INFO, 
                    " - processing Telegram subscribers phones: {0}", realPhones);
                
                String[] phones = new String[realPhones.size()];
                for (int i=0; i<phones.length; ++i) {
                    phones[i] = realPhones.get(i);
                }
                
                List<TelegramSubscriberVo> subs = this.telegramRepos.query(new TelegramSubscribersListOfPhonesSpec(phones));
                
                Logger.getLogger(FirebaseRemoteDataRepositoryServiceImpl.class.getName()).log(Level.INFO, 
                    " - processing Telegram subscribers: {0}", subs.size());
                
                final String fContext = context;
                for (TelegramSubscriberVo sub : subs) {
                    final TelegramSubscriberVo fsub = sub;
                    
                    Logger.getLogger(FirebaseRemoteDataRepositoryServiceImpl.class.getName()).log(Level.INFO, 
                            " - processing Telegram subscriber: {0}", sub);
                    
                    Future<RemoteMessagingResult> doAsync = this.async.doAsync(new Callable<RemoteMessagingResult>() {
                        @Override
                        public RemoteMessagingResult call() throws Exception {
                            long chatID = fsub.getChat_id();
                            String telegramContent = fContext;
                            TelegramSubscriberVo finalSubs = fsub;
                            
                            try {
                            
                                boolean res = MessageSender.sendMessageAndSwallowLogs(fsub.getChat_id(), fContext);
                                if (res) {
                                    Logger.getLogger(FirebaseRemoteDataRepositoryServiceImpl.class.getName()).log(Level.INFO,
                                            "Success sending message to chatID: {0} for message: {1} mobile: {2}", 
                                            new Object[]{chatID, telegramContent, finalSubs.getPhone_num()});
                                    return (new RemoteMessagingResult(finalSubs.getPhone_num(), chatID, telegramContent, JmsMsgConstants.S_OK, "telegram"));
                                } else {
                                    Logger.getLogger(FirebaseRemoteDataRepositoryServiceImpl.class.getName()).log(Level.WARNING,
                                            "Error, cannot send message to chatID: {0} for message: {1} mobile: {2}", 
                                            new Object[]{chatID, telegramContent, finalSubs.getPhone_num()});
                                    return (new RemoteMessagingResult(finalSubs.getPhone_num(), chatID, telegramContent, JmsMsgConstants.E_ERR, "telegram"));
                                }
                            } catch (Exception ex) {
                                Logger.getLogger(FirebaseRemoteDataRepositoryServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
                                    return (new RemoteMessagingResult(finalSubs.getPhone_num(), chatID, telegramContent, JmsMsgConstants.E_ERR, "telegram"));
                            }
                        }
                    });
                    asyncResults.add(doAsync);
                }
                
                
                
            } catch (PvExtPersistenceException ex) {
                Logger.getLogger(FirebaseRemoteDataRepositoryServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
                
            } 
            
        }
        
        List<RemoteMessagingResult> fres = new ArrayList<>();
        for (Future<RemoteMessagingResult> as : asyncResults) {
            try {
                RemoteMessagingResult res = as.get();
                if (res != null) {
                    fres.add(res);
                } else {
                    Logger.getLogger(FirebaseRemoteDataRepositoryServiceImpl.class.getName()).log(Level.WARNING, 
                            " - found remote messaging result null. It means, some push notifications are not delivered to client");
                }
            } catch (InterruptedException | ExecutionException ex) {
                Logger.getLogger(FirebaseRemoteDataRepositoryServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        return fres;
        
        /*
        //final DatabaseReference ticketsDb = this.firebaseDB.getDatabaseReference(this.ticketsFirebaseDBPath);
        //final DatabaseReference atmDb = this.firebaseDB.getDatabaseReference(this.machineStatusFirebaseDBPath);
//        final DatabaseReference ticketNotifDb = this.firebaseDB.getDatabaseReference(this.ticketsNotifFirebaseDBPath);
//        final DatabaseReference ticketChangedDb = this.firebaseDB.getDatabaseReference(this.ticketChangedFirebaseDBPath);
        
//        final Map<String, Object> fbTickets = constructFbTickets(tickets);
        
        
//        Logger.getLogger(this.getClass().getName()).log(Level.INFO, " - sendTickets, fbTickets: {0}", fbTickets);

        Map<String, Object> fbListAtmTicketsMap = new HashMap<>();
        Map<String, List<TicketPair>> ticketsChanged = new HashMap<>();
        
        
        // onComplete is called after all onChildListener hooks have completed.
        // This includes those in FirebaseDBListener service.
        // so, move these here outside onComplete because they will be overriden in onChild listener callbacks!
        for (TransferTicketDto ticket : tickets) {
            RestTicketDto rest = RestTicketDto.convert(ticket.getTicketMap());
//            List<SlmUserVo> usersVo;
            String loginName = rest.getAssignee();
//            try {
//                usersVo = this.pvimSlmUserRepository.query(new GetPvimUserByLoginNameSpecification(loginName));
//            } catch (PvExtPersistenceException ex) {
//                Logger.getLogger(FirebaseRemoteDataRepositoryServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
//                continue;
//            }
            
//            if (usersVo == null || usersVo.isEmpty() || usersVo.size() > 1) {
//                Logger.getLogger(FirebaseRemoteDataRepositoryServiceImpl.class.getName()).log(Level.SEVERE,
//                        "There are more than one user with login name or no login name available: [{0}] Ignoring update ticket", loginName);
//                continue;
//            }
            
            String atmKey = rest.getMachineNumber().trim();
//            setEngineerIdInAtmFirebase(atmDb, atmKey, "engineer_id", loginName);
//            setEngineerIdInAtmFirebase(atmDb, atmKey, "engineerID", loginName);
            
            Map<String, Long> fbTicketPerAtm;
//            SlmUserVo userVo = usersVo.get(0);
//            String userID = userVo.getUserID();
            String userID = loginName;
            if (fbListAtmTicketsMap.containsKey(userID)) {
                fbTicketPerAtm = (Map<String, Long>) fbListAtmTicketsMap.get(userID);
            } else {
                fbTicketPerAtm = new HashMap<>();
                if (FirebaseUtil.isValidKey(userID)) {
                    fbListAtmTicketsMap.put(userID, fbTicketPerAtm);
                } else {
                    Logger.getLogger(this.getClass().getName()).log(Level.WARNING, 
                            "Invalid login name {0} is found. Please only use alphanumeric characters", 
                            new Object[]{userID});
                }
            }
            long sysTime = System.currentTimeMillis();
            fbTicketPerAtm.put(ticket.getTicketId(), sysTime);
            
            List<String> accountList = ticket.getAccountList();
            if (accountList == null) {
                accountList = Collections.EMPTY_LIST;
            }
            for (String account : accountList) {
                try {
                    List<SlmUserVo> userVoList = this.pvimSlmUserRepository.query(
                            new GetPvimUserByMobileSpecification(account));
                    for (SlmUserVo userVo : userVoList) {
                        String userVoName = userVo.getLoginName();
                        List<TicketPair> p;
                        if (ticketsChanged.containsKey(userVoName)) {
                            p = ticketsChanged.get(userVoName);
                        } else {
                            p = new ArrayList<>();
                            ticketsChanged.put(userVoName, p);
                        }
                        p.add(new TicketPair(ticket.getTicketId(), sysTime));
                    }
                } catch (PvExtPersistenceException ex) {
                    Logger.getLogger(FirebaseRemoteDataRepositoryServiceImpl.class.getName())
                            .log(Level.SEVERE, null, ex);
                    
                }
            }
            
            
            
//            PvimTicketVo pvimTicket = new PvimTicketVo();
//
//            // set to true because PVIM already successfully executed the code
//            // so, the problem will be only in firebase side.
//            pvimTicket.setSuccessfullyUpdated(true);
////            pvimTicket.setTicketNumber(rest.getTicketNumber());
//            pvimTicket.setTicketId(ticket.getTicketId());
//            try {
//                pvimTicketMonitorRepository.updateTicket(pvimTicket);
//            } catch (PvExtPersistenceException ex) {
//                Logger.getLogger(FirebaseRemoteDataRepositoryServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
//            }
        }
        
//        ticketsDb.updateChildren(fbTickets, new DumbFirebaseCompletionListener());
        ticketNotifDb.updateChildren(fbListAtmTicketsMap, new DumbFirebaseCompletionListener());
        
        
        

        Map<String, Object> fbTicketsChanged = new HashMap<>();
        for (Map.Entry<String, List<TicketPair>> e : ticketsChanged.entrySet()) {
            String loginName = e.getKey();
            if (FirebaseUtil.isValidKey(loginName)) {
                List<TicketPair> tc = e.getValue();

                Map<String, Long> fbTcMap = new HashMap<>();
                for (TicketPair tp : tc) {
                    fbTcMap.put(tp.getTicketId(), tp.getTimestamp());
                }
                fbTicketsChanged.put(loginName, fbTcMap);
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.WARNING, 
                        "Invalid login name {0} is found. Please only use alphanumeric characters", 
                        new Object[]{loginName});
            }
        }
        
        ticketChangedDb.updateChildren(fbTicketsChanged, new DumbFirebaseCompletionListener());
        
        */
    }
    
    @Override
    @Scheduled(fixedRateString = "${PvimSlmUser.firebase.update.timestamp.interval}")
    public void periodicSendPvimSlmUserData() {
        Logger.getLogger(this.getClass().getName()).log(Level.FINE, ">> periodicSendPvimSlmUserData()");
        try {
//            final DatabaseReference db = this.firebaseDB.getDatabaseReference(this.pvimSlmUserFirebaseDBPath);
//            final FirebaseAuth auth = FirebaseAuth.getInstance(FirebaseApp.getInstance());
            List<SlmUserVo> usersVo = this.pvimSlmUserRepository.query(new GetAllPvimUsersSpecification());
//            Map<String, Object> fbUsers = new HashMap<>();
            for (SlmUserVo userVo : usersVo) {
//                FbPvimSlmUserJson fbUser = new FbPvimSlmUserJson();
//                fbUser.setEmail(userVo.getEmail());
//                fbUser.setLoginName(userVo.getLoginName());
//                fbUser.setMobile(userVo.getMobile());
//                fbUser.setUserId(userVo.getUserID());
//                fbUser.setUserType(userVo.getUserType());
//                fbUser.setLocked(userVo.getLocked());
//                String usLoginName = fbUser.getLoginName();
                
                if (!Commons.isEmptyStrIgnoreSpaces(userVo.getEmail())) {
                    FbAuthUserJson userJson = new FbAuthUserJson();
                    userJson.setEmail(userVo.getEmail());
                    userJson.setPassword("myjalinadmin");
                    userJson.setUsername(userVo.getLoginName());
                    try {
                        this.fcmService.createUser(userJson);
                    } catch (RemoteWsException ex) {
                        Logger.getLogger(FirebaseRemoteDataRepositoryServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
                    }
//                    
//                    UserRecord.CreateRequest cr = new UserRecord.CreateRequest();
//                    cr.setEmail(userVo.getEmail());
//                    cr.setPassword("myjalinadmin");
//                    try {
//                        auth.createUser(cr);
//                    } catch (FirebaseAuthException ex) {
//                        // error is logged when user exists, too much log!
////                        Logger.getLogger(FirebaseRemoteDataRepositoryServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
//                    }
                } else {
                    Logger.getLogger(this.getClass().getName()).log(Level.WARNING, 
                            "unable to send user: {0} because its email is not valid / empty!", 
                            userVo.getLoginName());
                }
                
                
//                if (FirebaseUtil.isValidKey(usLoginName)) {
//                    fbUsers.put(fbUser.getLoginName(), fbUser); // Deal, that login name will be used.
//                } else {
//                    Logger.getLogger(this.getClass().getName()).log(Level.WARNING, 
//                            "unable to send user: {0} because it contains illegal characters! Please only use alphanumeric!", 
//                            usLoginName);
//                }
//                fbUsers.put(fbUser.getUserId(), fbUser); // don't use login name
                                                         // because login name can have dot (.) character
                                                         // which is forbidden by firebase.
                                                         // userid is autogenerated and guaranteed alphanumeric ONLY
            }
//            Logger.getLogger(this.getClass().getName()).log(Level.INFO, " - reading users from db: {0}", fbUsers);
            /*db.updateChildren(fbUsers, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError de, DatabaseReference dr) {
                    if (de != null) {
                        Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, 
                                null, de);
                    }
                }
            });*/
            
        } catch (PvExtPersistenceException ex) {
            Logger.getLogger(FirebaseRemoteDataRepositoryServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
            
        } finally {
            Logger.getLogger(this.getClass().getName()).log(Level.FINE, "<< periodicSendPvimSlmUserData()");
            
        }
    }

}
