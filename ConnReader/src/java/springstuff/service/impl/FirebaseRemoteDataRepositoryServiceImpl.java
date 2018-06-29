/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package springstuff.service.impl;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import springstuff.exceptions.RemoteRepositoryException;
import springstuff.json.ComponentStateJson;
import springstuff.json.DeviceComponentStateJson;
import springstuff.json.firebase.FbDeviceJson;
import springstuff.service.RemoteDataRepositoryService;
import springstuff.service.firebase.FirebaseDatabaseReferenceService;

/**
 *
 * @author darryl.sulistyan
 */
@Service
@Qualifier("firebaseRemoteDataRepositoryService")
public class FirebaseRemoteDataRepositoryServiceImpl implements RemoteDataRepositoryService {

    private FirebaseDatabaseReferenceService firebaseDB;

    private String gpsFirebaseDBPath;
    private String machineStatusFirebaseDBPath;

//    private AsyncRunnerService asyncService;

    private int maxWaitTimeForQueryCompletion;

    @Value("${firebase.database.geofire.root}")
    public void setGpsFirebaseDBPath(String path) {
        this.gpsFirebaseDBPath = path;
    }

    @Value("${firebase.database.machinestatus.root}")
    public void setMachineStatusFirebaseDBPath(String path) {
        this.machineStatusFirebaseDBPath = path;
    }

    @Autowired
    public void setFirebaseDB(FirebaseDatabaseReferenceService db) {
        this.firebaseDB = db;
    }

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

    @Override
    public void send(List<DeviceComponentStateJson> devices) throws RemoteRepositoryException {

        if (this.gpsFirebaseDBPath != null && !"".equals(this.gpsFirebaseDBPath.trim())) {

            GeoFire geoFire = new GeoFire(this.firebaseDB.getDatabaseReference(this.gpsFirebaseDBPath));
            for (DeviceComponentStateJson device : devices) {

//                // although geoFire.setLocation is async as it is, it is important
//                // to run this via asyncService to limit the number of parallel
//                // threads within a manageable number, via executor pool.
                // No need to do that. In the background, Firebase database already limits
                // the number of threads in the background.
//                final int maxWaitQuery = this.maxWaitTimeForQueryCompletion;
//                this.asyncService.run(new Runnable() {
//                    
//                    private final Object lock = new Object();
//                    
//                    @Override
//                    public void run() {
//                        // this is async call
//                        // geofire does not support bulk uploads, unfortunatelly
//                        // because it is not meant to be used this way!!
                geoFire.setLocation(device.getDeviceid(),
                        new GeoLocation(device.getLocation().getLatitude(),
                                device.getLocation().getLongitude()),
                        new GeoFire.CompletionListener() {
                    @Override
                    public void onComplete(String string, DatabaseError de) {
                        if (de != null) {
                            Logger.getLogger(this.getClass().getName()).log(Level.WARNING,
                                    "Failed send data on device: {0} with error: {1}",
                                    new Object[]{device.getDeviceid(), de});
                        } else {
                            Logger.getLogger(this.getClass().getName()).log(Level.INFO,
                                    "Device {0} is saved successfully!",
                                    new Object[]{device.getDeviceid()});
                        }
//                                synchronized(lock) {
//                                    lock.notifyAll();
//                                }
                    }
                });
//                        
//                        // method must block here.
//                        // if this does not block, the guard of number of threads
//                        // from asyncService will be useless.
//                        synchronized(lock) {
//                            try {
//                                lock.wait(maxWaitQuery);
//                            } catch (InterruptedException ex) {
//                            }
//                        }
//                    }
//                });

            }

        }

        if (this.machineStatusFirebaseDBPath != null && !"".equals(this.machineStatusFirebaseDBPath.trim())) {
            final DatabaseReference db = this.firebaseDB.getDatabaseReference(this.machineStatusFirebaseDBPath);
            final Map<String, Object> fbDevices = new HashMap<>();

            for (DeviceComponentStateJson device : devices) {
                FbDeviceJson fbDevice = new FbDeviceJson();

                fbDevice.setDeviceID(device.getDeviceid());
                fbDevice.setType(device.getDeviceType());
                fbDevice.setTimestamp(System.currentTimeMillis());

                Map<String, Object> deviceStatus = new HashMap<>();
                List<ComponentStateJson> deviceComponents = device.getComponents();
                for (ComponentStateJson component : deviceComponents) {
                    if (component.getComponent() != null) {
                        deviceStatus.put(component.getComponent(), component.getState());
                    }
                }
                fbDevice.setStatus(deviceStatus);

                fbDevices.put(fbDevice.getDeviceID(), fbDevice);

            }

//            final int maxWaitQuery = this.maxWaitTimeForQueryCompletion;
//            this.asyncService.run(new Runnable() {
//                
//                private final Object lock = new Object();
//                
//                @Override
//                public void run() {
            Logger.getLogger(this.getClass().getName()).log(Level.INFO, " - devices: {0}", fbDevices);
//                    
            db.updateChildren(fbDevices, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError de, DatabaseReference dr) {
                    if (de != null) {
                        Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, de);
                    } else {
                        Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Successfully update ATM information!");
                    }
//                            synchronized(lock) {
//                                lock.notifyAll();
//                            }
                }
            });
//                    synchronized(lock) {
//                        try {
//                            lock.wait(maxWaitQuery);
//                        } catch (InterruptedException ex) {
//                        }
//                    }
//                }
//                
//            });
        }

    }

}
