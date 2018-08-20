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
import com.google.firebase.database.ValueEventListener;
import id.dni.ext.test.obj.GeofireLocationJson;
import id.dni.ext.web.ws.obj.RestTicketDto;
import id.dni.ext.web.ws.obj.TicketUtil;
import id.dni.ext.web.ws.obj.firebase.FbDeviceJson;
import id.dni.ext.web.ws.obj.firebase.FbPvimSlmUserJson;
import id.dni.pvim.ext.net.TransferTicketDto;
import id.dni.pvim.ext.repo.ISpecification;
import id.dni.pvim.ext.repo.db.ISlmUserRepository;
import id.dni.pvim.ext.repo.db.vo.SlmUserVo;
import id.dni.pvim.ext.repo.exceptions.PvExtPersistenceException;
import id.dni.pvim.ext.web.in.PVIMAuthToken;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import springstuff.dao.impl.RemoteTicketInMemoryRepositoryImpl;
import springstuff.exceptions.RemoteRepositoryException;
import springstuff.exceptions.RemoteWsException;
import springstuff.json.ComponentStateJson;
import springstuff.json.DeviceComponentStateJson;
import springstuff.json.MachineGpsJson;
import springstuff.model.PvimTicketVo;
import springstuff.service.IPvimTicketWebService;

/**
 *
 * @author darryl.sulistyan
 */
public class FirebaseRemoteDataRepositoryServiceImplTest {
    
    public FirebaseRemoteDataRepositoryServiceImplTest() {
    }
    
    static FirebaseDatabaseReferenceServiceImpl fdb;
    
    @BeforeClass
    public static void setUpClass() {
        try {
        fdb = new FirebaseDatabaseReferenceServiceImpl();
        fdb.setFirebaseDatabaseUrl("https://vynamic-operation-7b1bc.firebaseio.com/");
        fdb.setFirebaseRootPath("/");
        fdb.setFirebaseServiceAuthJsonFile("/vynamic-operation-7b1bc-firebase-adminsdk-g3v7y-5d9b7040bc.json");
        fdb.setFirebaseTimeout("10000");
        fdb.init();
        } catch(Exception ex) {
            
        }
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }
    
    @Test
    public void testRemoveTicketsNormalCase() throws InterruptedException, PvExtPersistenceException {
        
        RemoteTicketInMemoryRepositoryImpl inmem = new RemoteTicketInMemoryRepositoryImpl();
        inmem.init();
        
        FirebaseRemoteDataRepositoryServiceImpl impl = new FirebaseRemoteDataRepositoryServiceImpl();
//        impl.setFirebaseDB(fdb);
//        impl.setTicketsFirebaseDBPath("/tickets");
//        impl.setMachineStatusFirebaseDBPath("/ATMs");
//        impl.setTicketsNotifFirebaseDBPath("/list_atm_tickets");
//        impl.setRemoteTicketRepository(inmem);
        
        FirebaseTicketDBListenerServiceImpl dblistener = new FirebaseTicketDBListenerServiceImpl();
        dblistener.setFirebaseDatabaseReferenceService(fdb);
        dblistener.setSyncTicketRepo(inmem);
        dblistener.setTicketsFirebaseDBPath("/tickets");
        dblistener.setTicketWebService(new IPvimTicketWebService() {
            @Override
            public RestTicketDto createTicket(RestTicketDto ticket, PVIMAuthToken auth) throws RemoteWsException {
                return ticket;
            }

            @Override
            public List<RestTicketDto> getOpenTickets(String machineNo, PVIMAuthToken auth) throws RemoteWsException {
                return Collections.EMPTY_LIST;
            }

            @Override
            public RestTicketDto getTicketByNumber(String ticketNumber, PVIMAuthToken auth) throws RemoteWsException {
                return null;
            }

            @Override
            public RestTicketDto updateTicket(RestTicketDto ticket, PVIMAuthToken auth) throws RemoteWsException {
                return ticket;
            }
        });
        dblistener.init();
        
        DatabaseReference ref = fdb.getDatabaseReference("/tickets");
        String TICKET_ID = "TICKET_ID_XX_";
        for (int i=0; i<10; ++i) {
            ref.child(TICKET_ID+i).removeValue(new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError de, DatabaseReference dr) {
                }
            });
        }
        
        Thread.sleep(2000);
        
        
        List<TransferTicketDto> tickets = new ArrayList<>();
        for (int i=0; i<10; ++i) {
            TransferTicketDto tic = new TransferTicketDto(TICKET_ID+i);
            tic.setLastupdated(System.currentTimeMillis());
            RestTicketDto dto = new RestTicketDto();
            dto.setTicketNumber("TEST_REMOTE_" + i);
            dto.setNote("abcdefg\r\nasdfasdfasdf\r\nasdfasrfrfr\r\nPPPEPPEPEPEPEPPEPPEPEP\r\n");
            dto.setTicketState("11");
            dto.setMachineNumber("MACHINE_" + i);
            Map<String, Object> tm = TicketUtil.convert(dto);
            tic.setTicketMap(tm);
            tickets.add(tic);
        }
        try {
            impl.sendTickets(tickets);
        } catch (RemoteRepositoryException ex) {
            fail(ex.getMessage());
        }
        
        Thread.sleep(10000);
        
        List<PvimTicketVo> ll = inmem.getAllTickets();
        assertEquals(ll.size(), tickets.size());
        
        for (int i=0; i<10; ++i) {
            TransferTicketDto mp = tickets.get(i);
            RestTicketDto dto = RestTicketDto.convert(mp.getTicketMap());
            PvimTicketVo tvo = inmem.getTicket(mp.getTicketId());
            assertNotNull(tvo);
            assertEquals(tvo.getTicketId(), mp.getTicketId());
            assertTrue(tvo.isSuccessfullyUpdated());
        }
        
        List<TransferTicketDto> removed = new ArrayList<>();
        for (int i=0; i<5; ++i) {
            removed.add(tickets.get(i));
        }
        
        try {
            impl.removeTickets(removed);
        } catch (RemoteRepositoryException ex) {
            fail(ex.getMessage());
        }
        
        Thread.sleep(10000);
        
        for (int i=0; i<5; ++i) {
            TransferTicketDto mp = tickets.get(i);
            RestTicketDto dto = RestTicketDto.convert(mp.getTicketMap());
            PvimTicketVo tvo = inmem.getTicket(mp.getTicketId());
            assertNull(tvo);
        }
        
        for (int i=5; i<10; ++i) {
            TransferTicketDto mp = tickets.get(i);
            RestTicketDto dto = RestTicketDto.convert(mp.getTicketMap());
            PvimTicketVo tvo = inmem.getTicket(mp.getTicketId());
            assertNotNull(tvo);
            assertEquals(tvo.getTicketId(), mp.getTicketId());
            assertTrue(tvo.isSuccessfullyUpdated());
        }
        
    }
    
    
    @Test
    public void testPeriodicSendTimestampAll() throws PvExtPersistenceException, InterruptedException {
        RemoteTicketInMemoryRepositoryImpl inmem = new RemoteTicketInMemoryRepositoryImpl();
        inmem.init();
        
        FirebaseRemoteDataRepositoryServiceImpl impl = new FirebaseRemoteDataRepositoryServiceImpl();
//        impl.setFirebaseDB(fdb);
//        impl.setTicketsFirebaseDBPath("/tickets");
//        impl.setRemoteTicketRepository(inmem);
        
        DatabaseReference ref = fdb.getDatabaseReference("/tickets");
        
        long savedTime = System.currentTimeMillis();
        for (int i=0; i<10; ++i) {
            PvimTicketVo t = new PvimTicketVo();
            t.setSuccessfullyUpdated(true);
            t.setTicketId("TICKET_XX_" + i);
            t.setLastupdated(savedTime);
            inmem.updateTicket(t);
        }
        
        Thread.sleep(1000);
        
        impl.periodicUpdateLastupdate();
        
        for (int i=0; i<10; ++i) {
            String tcn = "TICKET_XX_" + i;
            
            PvimTicketVo tv = inmem.getTicket(tcn);
            assertNotNull(tv);
            assertEquals(tv.getLastupdated() > savedTime, true);
        }
        
    }
    
    @Test
    public void testPeriodicSendTimestampHalf() throws PvExtPersistenceException, InterruptedException {
        RemoteTicketInMemoryRepositoryImpl inmem = new RemoteTicketInMemoryRepositoryImpl();
        inmem.init();
        
        FirebaseRemoteDataRepositoryServiceImpl impl = new FirebaseRemoteDataRepositoryServiceImpl();
//        impl.setFirebaseDB(fdb);
//        impl.setTicketsFirebaseDBPath("/tickets");
//        impl.setRemoteTicketRepository(inmem);
        
        DatabaseReference ref = fdb.getDatabaseReference("/tickets");
        
        long savedTime = System.currentTimeMillis();
        for (int i=0; i<10; ++i) {
            PvimTicketVo t = new PvimTicketVo();
            t.setSuccessfullyUpdated(i < 5);
            t.setTicketId("TICKET_XX_" + i);
            t.setLastupdated(savedTime);
            inmem.updateTicket(t);
        }
        
        Thread.sleep(1000);
        
        impl.periodicUpdateLastupdate();
        
        for (int i=0; i<5; ++i) {
            String tcn = "TICKET_XX_" + i;
            
            PvimTicketVo tv = inmem.getTicket(tcn);
            assertNotNull(tv);
            assertEquals(tv.getLastupdated() > savedTime, true);
        }
        
        for (int i=5; i<10; ++i) {
            String tcn = "TICKET_XX_" + i;
            
            PvimTicketVo tv = inmem.getTicket(tcn);
            assertNotNull(tv);
            assertEquals(tv.getLastupdated() == savedTime, true);
        }
        
    }
    
    @Test
    public void testPeriodicallySendUserData() throws Exception {
        FirebaseRemoteDataRepositoryServiceImpl impl = new FirebaseRemoteDataRepositoryServiceImpl();
//        impl.setFirebaseDB(fdb);
//        impl.setPvimSlmUserFirebaseDBPath("/users/pvim/slm");
        final List<SlmUserVo> list = new ArrayList<>();
        final Object lock = new Object();
        for (int i=0; i<10; ++i) {
            Map<String, Object> data = new HashMap<>();
            data.put(SlmUserVo.FIELD_EMAIL, "test@user.com");
            data.put(SlmUserVo.FIELD_USER_ID, "-KSye27ce9ic9=23ie-oei3" + i);
            data.put(SlmUserVo.FIELD_LOGIN_NAME, "userlogin" + i);
            data.put(SlmUserVo.FIELD_USER_TYPE, i%3==0 ? "SLM" : "CSR");
            data.put(SlmUserVo.FIELD_MOBILE, "01234567890");
            SlmUserVo user = new SlmUserVo();
            user.fillDataFromMap(data);
            list.add(user);
        }
        impl.setPvimSlmUserRepository(new ISlmUserRepository() {
            @Override
            public boolean isMobileExist(String mobile) throws PvExtPersistenceException {
                return true;
            }

            @Override
            public List<SlmUserVo> query(ISpecification specification) throws PvExtPersistenceException {
                return list;
            }
        });
        DatabaseReference ref = fdb.getDatabaseReference("/users/pvim/slm");
        ref.removeValue(new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError de, DatabaseReference dr) {
            }
        });
        final Map<String, FbPvimSlmUserJson> result = new HashMap<>();
        ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot ds, String string) {
                FbPvimSlmUserJson js = ds.getValue(FbPvimSlmUserJson.class);
                if (js != null) {
                    String key = ds.getKey();
                    result.put(key, js);
                }
            }

            @Override
            public void onChildChanged(DataSnapshot ds, String string) {
                FbPvimSlmUserJson js = ds.getValue(FbPvimSlmUserJson.class);
                if (js != null) {
                    String key = ds.getKey();
                    result.put(key, js);
                }
            }

            @Override
            public void onChildRemoved(DataSnapshot ds) {
                FbPvimSlmUserJson js = ds.getValue(FbPvimSlmUserJson.class);
                if (js != null) {
                    String key = ds.getKey();
                    result.remove(key);
                }
            }

            @Override
            public void onChildMoved(DataSnapshot ds, String string) {
            }

            @Override
            public void onCancelled(DatabaseError de) {
            }
        });
        impl.periodicSendPvimSlmUserData();
        
        synchronized(lock) {
            lock.wait(30000);
        }
        
        assertEquals(result.size(), list.size());
        for (SlmUserVo us : list) {
            FbPvimSlmUserJson js = result.get(us.getUserID());
            assertNotNull(js);
            assertEquals(js.getEmail(), us.getEmail());
            assertEquals(js.getLoginName(), us.getLoginName());
            assertEquals(js.getMobile(), us.getMobile());
            assertEquals(js.getUserType(), us.getUserType());
        }
    }
    
    @Test
    public void testPeriodicallySendUserDataWithNumericID() throws Exception {
        FirebaseRemoteDataRepositoryServiceImpl impl = new FirebaseRemoteDataRepositoryServiceImpl();
//        impl.setFirebaseDB(fdb);
//        impl.setPvimSlmUserFirebaseDBPath("/users/pvim/slm");
        final List<SlmUserVo> list = new ArrayList<>();
        final Object lock = new Object();
        for (int i=0; i<10; ++i) {
            Map<String, Object> data = new HashMap<>();
            data.put(SlmUserVo.FIELD_EMAIL, "test@yahoo.co.pz");
            data.put(SlmUserVo.FIELD_USER_ID, "" + i);
            data.put(SlmUserVo.FIELD_LOGIN_NAME, "Numeric" + i);
            data.put(SlmUserVo.FIELD_USER_TYPE, i%3==0 ? "SLM" : "CSR");
            data.put(SlmUserVo.FIELD_MOBILE, "565564545455656");
            SlmUserVo user = new SlmUserVo();
            user.fillDataFromMap(data);
            list.add(user);
        }
        impl.setPvimSlmUserRepository(new ISlmUserRepository() {
            @Override
            public boolean isMobileExist(String mobile) throws PvExtPersistenceException {
                return true;
            }

            @Override
            public List<SlmUserVo> query(ISpecification specification) throws PvExtPersistenceException {
                return list;
            }
        });
        DatabaseReference ref = fdb.getDatabaseReference("/users/pvim/slm");
        ref.removeValue(new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError de, DatabaseReference dr) {
            }
        });
        final Map<String, FbPvimSlmUserJson> result = new HashMap<>();
        ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot ds, String string) {
                FbPvimSlmUserJson js = ds.getValue(FbPvimSlmUserJson.class);
                if (js != null) {
                    String key = ds.getKey();
                    result.put(key, js);
                }
            }

            @Override
            public void onChildChanged(DataSnapshot ds, String string) {
                FbPvimSlmUserJson js = ds.getValue(FbPvimSlmUserJson.class);
                if (js != null) {
                    String key = ds.getKey();
                    result.put(key, js);
                }
            }

            @Override
            public void onChildRemoved(DataSnapshot ds) {
                FbPvimSlmUserJson js = ds.getValue(FbPvimSlmUserJson.class);
                if (js != null) {
                    String key = ds.getKey();
                    result.remove(key);
                }
            }

            @Override
            public void onChildMoved(DataSnapshot ds, String string) {
            }

            @Override
            public void onCancelled(DatabaseError de) {
            }
        });
        impl.periodicSendPvimSlmUserData();
        
        synchronized(lock) {
            lock.wait(30000);
        }
        
        assertEquals(result.size(), list.size());
        for (SlmUserVo us : list) {
            FbPvimSlmUserJson js = result.get(us.getUserID());
            assertNotNull(js);
            assertEquals(js.getEmail(), us.getEmail());
            assertEquals(js.getLoginName(), us.getLoginName());
            assertEquals(js.getMobile(), us.getMobile());
            assertEquals(js.getUserType(), us.getUserType());
        }
    }
    
    static class Holder {
        boolean flag;
        int ctr;
    }
    
    
    /**
     * Test of send method, of class FirebaseRemoteDataRepositoryServiceImpl.
     */
    @Test
    public void testSendNormalCase() throws Exception {
        System.out.println("send");
        
        String locationPath = "/location";
        String atmPath = "/ATMs";
        
        final DatabaseReference locref = fdb.getDatabaseReference(locationPath);
        final DatabaseReference atmref = fdb.getDatabaseReference(atmPath);
        
        List<DeviceComponentStateJson> devices = new ArrayList<>();
        FirebaseRemoteDataRepositoryServiceImpl instance = new FirebaseRemoteDataRepositoryServiceImpl();
//        instance.setGpsFirebaseDBPath(locationPath);
//        instance.setMachineStatusFirebaseDBPath(atmPath);
//        instance.setAsyncRunnerService(new AsyncRunnerService() {
//            @Override
//            public void run(Runnable job) {
//                job.run();
//            }
//        });        

        for (int i=0; i<10; ++i) {
            DeviceComponentStateJson device = new DeviceComponentStateJson();
            device.setDeviceid("DEVICE_" + i + "X000");
            device.setDeviceType("Diebold");
            device.setLocation(new MachineGpsJson(-6.5 + i/100.0, 100.234 + i/100.0));
            
            List<ComponentStateJson> comp = new ArrayList<>();
            for (int j=0; j<5; ++j) {
                comp.add(new ComponentStateJson("component-" + (j+10), "FAIL"));
            }
            device.setComponents(comp);
            devices.add(device);
        }
        
        Holder h = new Holder();
        h.flag = true;
        h.ctr = 0;
        
        // remove first
        for (DeviceComponentStateJson device : devices) {
            locref.child(device.getDeviceid()).removeValue(new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError de, DatabaseReference dr) {
                    if (de != null) {
                        h.flag = false;
                    }
                    
                    synchronized(h) {
                        h.ctr++;
                        if (h.ctr == devices.size()*2) {
                            h.notifyAll();
                        }
                    }
                }
            });
            atmref.child(device.getDeviceid()).removeValue(new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError de, DatabaseReference dr) {
                    if (de != null) {
                        h.flag = false;
                    }
                    
                    synchronized(h) {
                        h.ctr++;
                        if (h.ctr == devices.size()*2) {
                            h.notifyAll();
                        }
                    }
                }
            });
        }
        
        synchronized(h) {
            h.wait(10000);
        }
        
        if (!h.flag) {
            fail("Error, not managed to delete all leftover ATMs and locations");
        }
        
//        instance.setFirebaseDB(fdb);
        instance.send(devices);
        
        Thread.sleep(10000); // arbitrary
        
        h.ctr = 0;
        
        for (DeviceComponentStateJson device : devices) {
            locref.child(device.getDeviceid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot ds) {
                    Logger.getLogger(this.getClass().getName()).log(Level.INFO, " - check gps device: " + device.getDeviceid());
                    
                    try {
                        Double objLat = null, objLon = null;
                        for (DataSnapshot ads : ds.child("l").getChildren()) {
                            if (objLat == null) {
                                objLat = ads.getValue(Double.class);
                            } else {
                                objLon = ads.getValue(Double.class);
                            }
                        }
                        assertNotNull(objLat);
                        Logger.getLogger(this.getClass().getName()).log(Level.INFO, " - check gps device: objLat not null");
                        
                        assertNotNull(objLon);
                        Logger.getLogger(this.getClass().getName()).log(Level.INFO, " - check gps device: objLon not null");
                        
                        double obsLat = objLat;
                        double obsLon = objLon;
                        
                        double expLat = device.getLocation().getLatitude();
                        double expLon = device.getLocation().getLongitude();

                        Logger.getLogger(this.getClass().getName()).log(Level.INFO, " - check gps device: obsLat=" + obsLat + " expLat=" + expLat);
                        assertEquals(Math.abs(obsLat - expLat) <= 0.0001, true);
                        Logger.getLogger(this.getClass().getName()).log(Level.INFO, " - check gps device: lat ok");

                        Logger.getLogger(this.getClass().getName()).log(Level.INFO, " - check gps device: obsLon=" + obsLon + " expLon=" + expLon);
                        assertEquals(Math.abs(obsLon - expLon) <= 0.0001, true);
                        Logger.getLogger(this.getClass().getName()).log(Level.INFO, " - check gps device: lon ok");

                        synchronized(h) {
                            h.ctr++;
                            if (h.ctr == devices.size()) {
                                h.notifyAll();
                            }
                        }
                    } catch (Exception ex) {
                        Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
                    }
                    
                    
                }

                @Override
                public void onCancelled(DatabaseError de) {
                }
            });
        }
        
        synchronized(h) {
            h.wait(10000);
        }
        
        if (h.ctr != devices.size()) {
            fail("Not all cases are successful in comparing location and geofire sent");
            
        }
        
        
        h.ctr = 0;
        
        for (DeviceComponentStateJson device : devices) {
            atmref.child(device.getDeviceid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot ds) {
                    Logger.getLogger(this.getClass().getName()).log(Level.INFO, " - device: " + device.getDeviceid());
                    FbDeviceJson js = ds.getValue(FbDeviceJson.class);
                    assertNotNull(js);
                    Logger.getLogger(this.getClass().getName()).log(Level.INFO, " - js is not null, ok");
                    assertEquals(device.getDeviceid(), js.getDeviceID());
                    Logger.getLogger(this.getClass().getName()).log(Level.INFO, " - js deviceID, ok");
                    assertEquals(device.getDeviceType(), js.getType());
                    Logger.getLogger(this.getClass().getName()).log(Level.INFO, " - js type, ok");
                    assertNotNull(js.getStatus());
                    Logger.getLogger(this.getClass().getName()).log(Level.INFO, " - js status is not null, ok");
                    assertEquals(js.getStatus().size(), device.getComponents().size());
                    Logger.getLogger(this.getClass().getName()).log(Level.INFO, " - js status has same size, ok");
                    for (ComponentStateJson cjs : device.getComponents()) {
                        if (cjs.getComponent() == null) {
                            assertNull(js.getStatus().get(cjs.getComponent()));
                            Logger.getLogger(this.getClass().getName()).log(Level.INFO, " - js null component is null as well, ok");
                        } else {
                            Object o = js.getStatus().get(cjs.getComponent());
                            assertNotNull(o);
                            Logger.getLogger(this.getClass().getName()).log(Level.INFO, " - js not null component also not null, ok");
                            assertEquals(cjs.getState(), o);
                            Logger.getLogger(this.getClass().getName()).log(Level.INFO, " - js state == device state, ok");
                        }
                    }
                    
                    synchronized(h) {
                        h.ctr++;
                        if (h.ctr == devices.size()) {
                            h.notifyAll();
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError de) {
                }
            });
        }
        
        synchronized(h) {
            h.wait(10000);
        }
        
        if (h.ctr != devices.size()) {
            fail("Not all cases are successful in comparing FbDevicesJson and Component sent");
        }
        
    }
    
    /**
     * Test of send method, of class FirebaseRemoteDataRepositoryServiceImpl.
     */
    @Test
    public void testSendOneGpsLocationError() throws Exception {
        System.out.println("send");
        
        String locationPath = "/location";
        String atmPath = "/ATMs";
        
        final DatabaseReference locref = fdb.getDatabaseReference(locationPath);
        final DatabaseReference atmref = fdb.getDatabaseReference(atmPath);
        
        List<DeviceComponentStateJson> devices = new ArrayList<>();
        FirebaseRemoteDataRepositoryServiceImpl instance = new FirebaseRemoteDataRepositoryServiceImpl();
//        instance.setGpsFirebaseDBPath(locationPath);
//        instance.setMachineStatusFirebaseDBPath(atmPath);
//        instance.setAsyncRunnerService(new AsyncRunnerService() {
//            @Override
//            public void run(Runnable job) {
//                job.run();
//            }
//        });        

        String FAULT_DEVICE_ID = "DEVICE_5X000";
        for (int i=0; i<10; ++i) {
            DeviceComponentStateJson device = new DeviceComponentStateJson();
            device.setDeviceid("DEVICE_" + i + "X000");
            device.setDeviceType("Diebold");
            if (FAULT_DEVICE_ID.equals(device.getDeviceid())) {
                device.setLocation(new MachineGpsJson(74000.0, 25000.0)); // error here
            } else {
                device.setLocation(new MachineGpsJson(-6.5 + i/100.0, 100.234 + i/100.0));
            }
            
            List<ComponentStateJson> comp = new ArrayList<>();
            for (int j=0; j<5; ++j) {
                comp.add(new ComponentStateJson("component-" + (j+10), "FAIL"));
            }
            device.setComponents(comp);
            devices.add(device);
        }
        
        Holder h = new Holder();
        h.flag = true;
        h.ctr = 0;
        
        // remove first
        for (DeviceComponentStateJson device : devices) {
            locref.child(device.getDeviceid()).removeValue(new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError de, DatabaseReference dr) {
                    if (de != null) {
                        h.flag = false;
                    }
                    
                    synchronized(h) {
                        h.ctr++;
                        if (h.ctr == devices.size()*2) {
                            h.notifyAll();
                        }
                    }
                }
            });
            atmref.child(device.getDeviceid()).removeValue(new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError de, DatabaseReference dr) {
                    if (de != null) {
                        h.flag = false;
                    }
                    
                    synchronized(h) {
                        h.ctr++;
                        if (h.ctr == devices.size()*2) {
                            h.notifyAll();
                        }
                    }
                }
            });
        }
        
        synchronized(h) {
            h.wait(10000);
        }
        
        if (!h.flag) {
            fail("Error, not managed to delete all leftover ATMs and locations");
        }
        
//        instance.setFirebaseDB(fdb);
        instance.send(devices);
        
        Thread.sleep(10000); // arbitrary
        
        h.ctr = 0;
        
        for (DeviceComponentStateJson device : devices) {
            locref.child(device.getDeviceid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot ds) {
                    Logger.getLogger(this.getClass().getName()).log(Level.INFO, " - check gps device: " + device.getDeviceid());
                    
                    try {
                        if (FAULT_DEVICE_ID.equals(device.getDeviceid())) {
                            Object o = ds.getValue();
                            assertNull(o);
                            Logger.getLogger(this.getClass().getName()).log(Level.INFO, " - check gps device: ds is null for faulty entry");
                            
                        } else {
                            Logger.getLogger(this.getClass().getName()).log(Level.INFO, " - check gps device: initialize");
                            
                            Double objLat = null, objLon = null;
                            Logger.getLogger(this.getClass().getName()).log(Level.INFO, " - check gps device: obj Lat created");
                            
                            
                            
                            try {
                                Logger.getLogger(this.getClass().getName()).log(Level.INFO, " - is ds null?" + (ds == null));
                                
                                for (DataSnapshot ads : ds.child("l").getChildren()) {
                                    Object z = ads.getValue();
                                    if (z == null) {
                                        Logger.getLogger(this.getClass().getName()).log(Level.INFO, " - check gps device: z null");
                                    } else {
                                        Logger.getLogger(this.getClass().getName()).log(Level.INFO, " - check gps device: z: " + z.toString());
                                    }

                                    if (objLat == null) {
                                        objLat = ads.getValue(Double.class);
                                    } else {
                                        objLon = ads.getValue(Double.class);
                                    }
                                }
                            } catch (Throwable t) {
                                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, t);
                            }
                            
                            assertNotNull(objLat);
                            Logger.getLogger(this.getClass().getName()).log(Level.INFO, " - check gps device: objLat not null");

                            assertNotNull(objLon);
                            Logger.getLogger(this.getClass().getName()).log(Level.INFO, " - check gps device: objLon not null");
                            
                            double obsLat = objLat;
                            double obsLon = objLon;

                            double expLat = device.getLocation().getLatitude();
                            double expLon = device.getLocation().getLongitude();

                            Logger.getLogger(this.getClass().getName()).log(Level.INFO, " - check gps device: obsLat=" + obsLat + " expLat=" + expLat);
                            assertEquals(Math.abs(obsLat - expLat) <= 0.0001, true);
                            Logger.getLogger(this.getClass().getName()).log(Level.INFO, " - check gps device: lat ok");

                            Logger.getLogger(this.getClass().getName()).log(Level.INFO, " - check gps device: obsLon=" + obsLon + " expLon=" + expLon);
                            assertEquals(Math.abs(obsLon - expLon) <= 0.0001, true);
                            Logger.getLogger(this.getClass().getName()).log(Level.INFO, " - check gps device: lon ok");
                        }
                        
                        synchronized(h) {
                            h.ctr++;
                            if (h.ctr == devices.size()) {
                                h.notifyAll();
                            }
                        }
                    } catch (Exception ex) {
                        Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
                    }
                    
                    
                }

                @Override
                public void onCancelled(DatabaseError de) {
                }
            });
        }
        
        synchronized(h) {
            h.wait(10000);
        }
        
        if (h.ctr != devices.size()) {
            fail("Not all cases are successful in comparing location and geofire sent");
            
        }
        
        
        h.ctr = 0;
        
        for (DeviceComponentStateJson device : devices) {
            atmref.child(device.getDeviceid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot ds) {
                    Logger.getLogger(this.getClass().getName()).log(Level.INFO, " - device: " + device.getDeviceid());
                    FbDeviceJson js = ds.getValue(FbDeviceJson.class);
                    assertNotNull(js);
                    Logger.getLogger(this.getClass().getName()).log(Level.INFO, " - js is not null, ok");
                    assertEquals(device.getDeviceid(), js.getDeviceID());
                    Logger.getLogger(this.getClass().getName()).log(Level.INFO, " - js deviceID, ok");
                    assertEquals(device.getDeviceType(), js.getType());
                    Logger.getLogger(this.getClass().getName()).log(Level.INFO, " - js type, ok");
                    assertNotNull(js.getStatus());
                    Logger.getLogger(this.getClass().getName()).log(Level.INFO, " - js status is not null, ok");
                    assertEquals(js.getStatus().size(), device.getComponents().size());
                    Logger.getLogger(this.getClass().getName()).log(Level.INFO, " - js status has same size, ok");
                    for (ComponentStateJson cjs : device.getComponents()) {
                        if (cjs.getComponent() == null) {
                            assertNull(js.getStatus().get(cjs.getComponent()));
                            Logger.getLogger(this.getClass().getName()).log(Level.INFO, " - js null component is null as well, ok");
                        } else {
                            Object o = js.getStatus().get(cjs.getComponent());
                            assertNotNull(o);
                            Logger.getLogger(this.getClass().getName()).log(Level.INFO, " - js not null component also not null, ok");
                            assertEquals(cjs.getState(), o);
                            Logger.getLogger(this.getClass().getName()).log(Level.INFO, " - js state == device state, ok");
                        }
                    }
                    
                    synchronized(h) {
                        h.ctr++;
                        if (h.ctr == devices.size()) {
                            h.notifyAll();
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError de) {
                }
            });
        }
        
        synchronized(h) {
            h.wait(10000);
        }
        
        if (h.ctr != devices.size()) {
            fail("Not all cases are successful in comparing FbDevicesJson and Component sent");
        }
        
    }
    
}
