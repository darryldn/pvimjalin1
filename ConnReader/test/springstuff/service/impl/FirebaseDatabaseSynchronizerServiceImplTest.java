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
import id.dni.ext.web.ws.obj.firebase.FbAtmCassetteBalanceWrapperJson;
import id.dni.ext.web.ws.obj.firebase.FbDeviceJson;
import id.dni.ext.web.ws.obj.firebase.FbPvimSlmUserJson;
import id.dni.ext.web.ws.obj.firebase.FbTicketDto;
import id.dni.ext.web.ws.obj.firebase.internal.FbCassetteBalanceJson;
import id.dni.pvim.ext.dto.PvWsCassette;
import id.dni.pvim.ext.repo.ISpecification;
import id.dni.pvim.ext.repo.db.IDeviceRepository;
import id.dni.pvim.ext.repo.db.ISlmUserRepository;
import id.dni.pvim.ext.repo.db.ITicketRepository;
import id.dni.pvim.ext.repo.db.spec.ISqlSpecification;
import id.dni.pvim.ext.repo.db.vo.DeviceVo;
import id.dni.pvim.ext.repo.db.vo.SlmUserVo;
import id.dni.pvim.ext.repo.db.vo.TicketVo;
import id.dni.pvim.ext.repo.exceptions.PvExtPersistenceException;
import java.util.ArrayList;
import java.util.Collection;
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
import springstuff.service.FirebaseDatabaseObjSynchronizerService;
import springstuff.service.firebase.FirebaseDatabaseReferenceService;
import springstuff.service.impl.sync.firebaseobj.FbSyncATMsCassetteBalanceService;
import springstuff.service.impl.sync.firebaseobj.FbSyncATMsService;
import springstuff.service.impl.sync.firebaseobj.FbSyncSlmUserService;
import springstuff.service.impl.sync.firebaseobj.FbSyncTicketService;

/**
 *
 * @author darryl.sulistyan
 */
public class FirebaseDatabaseSynchronizerServiceImplTest {
    
    public FirebaseDatabaseSynchronizerServiceImplTest() {
    }
    
    static FirebaseDatabaseReferenceServiceImpl fdb;
    
    @BeforeClass
    public static void setUpClass() {
        fdb = new FirebaseDatabaseReferenceServiceImpl();
        fdb.setFirebaseDatabaseUrl("https://vynamic-operation-7b1bc.firebaseio.com/");
        fdb.setFirebaseRootPath("/");
        fdb.setFirebaseServiceAuthJsonFile("/vynamic-operation-7b1bc-firebase-adminsdk-g3v7y-5d9b7040bc.json");
        fdb.setFirebaseTimeout("10000");
        fdb.init();
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

    static class Holder {
        int cnt;
        boolean completed;
    }
    
    @Test
    public void testSynchronizeSyncAtmNormalCase() throws Exception {
        Logger.getLogger(this.getClass().getName()).log(Level.INFO, " >> testSynchronizeSyncAtmNormalCase()");
        
        FbSyncATMsService atmService = new FbSyncATMsService();
        atmService.setDeviceRepo(new IDeviceRepository() {
            
            Map<String, DeviceVo> map = new HashMap<>();
            {
                addDevice("1234561");
                addDevice("1234562");
                addDevice("1234563");
                addDevice("1234564");
                addDevice("1234565");
                addDevice("1234566");
                addDevice("1234567");
                addDevice("1234568");
                addDevice("1234569");
                addDevice("ATM00000005");
                addDevice("ATM00000006");
                addDevice("ATM00000007");
                Logger.getLogger(this.getClass().getName()).log(Level.INFO, "::map read: " + map);
            }
            
            private void addDevice(String id) throws PvExtPersistenceException {
                Map<String, Object> f = new HashMap<>();
                f.put(DeviceVo.FIELD_DEVICEID, id);
                DeviceVo dev = new DeviceVo();
                dev.fillDataFromMap(f);
                map.put(id, dev);
            }
            
            @Override
            public boolean isDeviceExist(String deviceId) throws PvExtPersistenceException {
                Logger.getLogger(this.getClass().getName()).log(Level.INFO, " - read deviceId: " + deviceId);
                return this.map.containsKey(deviceId);
            }

            @Override
            public List<DeviceVo> query(ISpecification specification) throws PvExtPersistenceException {
                throw new PvExtPersistenceException("query is done!");
            }
        });
        atmService.setPath("/ATMs");
        
        FirebaseDatabaseSynchronizerServiceImpl impl = new FirebaseDatabaseSynchronizerServiceImpl();
        impl.setFirebaseSyncAtmService(atmService);
        impl.setFirebaseDatabaseReferenceService(fdb);
        
        final DatabaseReference ref = fdb.getDatabaseReference("/ATMs");
        final Holder h = new Holder(); h.cnt = 0;
        
        for (int i=0; i<5; ++i) {
            
            FbDeviceJson js = new FbDeviceJson();
            js.setDeviceID("Test00000000000" + i);
            js.setName("Test_name_00000" + i);
            js.setTimestamp(System.currentTimeMillis());
            ref.child(js.getDeviceID()).setValue(js, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError de, DatabaseReference dr) {
                    if (de == null) {
                        synchronized(h) {
                            h.cnt++;
                            if (h.cnt == 5) {
                                h.notifyAll();
                            }
                        }
                    }
                }
            });
        }
        
        synchronized(h) {
            h.wait(20000);
        }
        
        if (h.cnt != 5) {
            fail("Fail test, not all callbacks completed");
        }
        
        impl.synchronize();
        
        Thread.sleep(30000);
        
        Holder suc = new Holder();
        suc.cnt = 0;
        suc.completed = false;
        
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot ds) {
                for (DataSnapshot childDs : ds.getChildren()) {
                    FbDeviceJson js = childDs.getValue(FbDeviceJson.class);
                    if (js != null) {
                        if (js.getDeviceID().startsWith("Test00000000000")) {
                            synchronized(suc) {
                                suc.cnt++;
                            }
                        }
                    }
                }
                synchronized(suc) {
                    suc.completed = true;
                    suc.notifyAll();
                }
            }

            @Override
            public void onCancelled(DatabaseError de) {
            }
        });
        
        synchronized(suc) {
            suc.wait(30000);
        }
        synchronized(suc) {
            if (!suc.completed) {
                fail("fail complete all callbacks after downloading data from server check after synchronizing!");
            }

            assertEquals(0, suc.cnt);
        }
        Logger.getLogger(this.getClass().getName()).log(Level.INFO, " << testSynchronizeSyncAtmNormalCase()");
    }
    
    
    @Test
    public void testSynchronizeSyncAtmCassetteBalanceNormalCase() throws Exception {
        Logger.getLogger(this.getClass().getName()).log(Level.INFO, " >> testSynchronizeSyncAtmCassetteBalanceNormalCase()");
        
        FbSyncATMsCassetteBalanceService acass = new FbSyncATMsCassetteBalanceService();
        acass.setDeviceRepo(new IDeviceRepository() {
            
            Map<String, DeviceVo> map = new HashMap<>();
            {
                addDevice("456789");
                addDevice("1234561");
                addDevice("TE9900903");
//                addDevice("1234562");
//                addDevice("1234563");
//                addDevice("1234564");
//                addDevice("1234565");
//                addDevice("1234566");
//                addDevice("1234567");
//                addDevice("1234568");
//                addDevice("1234569");
//                addDevice("ATM00000005");
//                addDevice("ATM00000006");
//                addDevice("ATM00000007");
                Logger.getLogger(this.getClass().getName()).log(Level.INFO, "::map read: " + map);
            }
            
            private void addDevice(String id) throws PvExtPersistenceException {
                Map<String, Object> f = new HashMap<>();
                f.put(DeviceVo.FIELD_DEVICEID, id);
                DeviceVo dev = new DeviceVo();
                dev.fillDataFromMap(f);
                map.put(id, dev);
            }
            
            @Override
            public boolean isDeviceExist(String deviceId) throws PvExtPersistenceException {
                Logger.getLogger(this.getClass().getName()).log(Level.INFO, " - read deviceId: " + deviceId);
                return this.map.containsKey(deviceId);
            }

            @Override
            public List<DeviceVo> query(ISpecification specification) throws PvExtPersistenceException {
                throw new PvExtPersistenceException("query is done!");
            }
        });
        acass.setPath("/ATM_cassette_balance");
        
        FirebaseDatabaseSynchronizerServiceImpl impl = new FirebaseDatabaseSynchronizerServiceImpl();
        impl.setFirebaseCassetteBalanceService(acass);
        impl.setFirebaseDatabaseReferenceService(fdb);
        
        final DatabaseReference ref = fdb.getDatabaseReference("/ATM_cassette_balance");
        final Holder h = new Holder(); h.cnt = 0;
        
        for (int i=0; i<5; ++i) {
            
            FbAtmCassetteBalanceWrapperJson js = new FbAtmCassetteBalanceWrapperJson();
            js.setDeviceId("Test10000000000" + i);
            
            FbCassetteBalanceJson cass = new FbCassetteBalanceJson();
            cass.setTimestamp(System.currentTimeMillis());
            Map<String, PvWsCassette> cs = new HashMap<>();
            for (int j=0; j<4; ++j) {
                PvWsCassette icass = new PvWsCassette();
                icass.setCassetteId("WHOOOHOOO" + j);
                icass.setCassetteType("TEST CASSETTE" + j);
                icass.setStatus("0");
                cs.put(icass.getCassetteId(), icass);
            }
            cass.setCassettes(cs);
            
            ref.child(js.getDeviceId()).setValue(js, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError de, DatabaseReference dr) {
                    if (de == null) {
                        synchronized(h) {
                            h.cnt++;
                            if (h.cnt == 5) {
                                h.notifyAll();
                            }
                        }
                    }
                }
            });
        }
        
        synchronized(h) {
            h.wait(20000);
        }
        
        if (h.cnt != 5) {
            fail("Fail test, not all callbacks completed");
        }
        
        impl.synchronize();
        
        Thread.sleep(30000);
        
        Holder suc = new Holder();
        suc.cnt = 0;
        suc.completed = false;
        
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot ds) {
                for (DataSnapshot childDs : ds.getChildren()) {
                    FbAtmCassetteBalanceWrapperJson js = childDs.getValue(FbAtmCassetteBalanceWrapperJson.class);
                    if (js != null) {
                        if (js.getDeviceId().startsWith("Test10000000000")) {
                            synchronized(suc) {
                                suc.cnt++;
                            }
                        }
                    }
                }
                synchronized(suc) {
                    suc.completed = true;
                    suc.notifyAll();
                }
            }

            @Override
            public void onCancelled(DatabaseError de) {
            }
        });
        
        synchronized(suc) {
            suc.wait(30000);
        }
        synchronized(suc) {
            if (!suc.completed) {
                fail("fail complete all callbacks after downloading data from server check after synchronizing!");
            }

            assertEquals(0, suc.cnt);
        }
        Logger.getLogger(this.getClass().getName()).log(Level.INFO, " << testSynchronizeSyncAtmCassetteBalanceNormalCase()");
    }
    
    
    @Test
    public void testSynchronizeSyncSlmUsersNormalCase() throws Exception {
        Logger.getLogger(this.getClass().getName()).log(Level.INFO, " >> testSynchronizeSyncSlmUsersNormalCase()");
        
        FbSyncSlmUserService acass = new FbSyncSlmUserService();
        acass.setSlmUserRepo(new ISlmUserRepository() {
            @Override
            public boolean isMobileExist(String mobile) throws PvExtPersistenceException {
                return false;
            }

            @Override
            public List<SlmUserVo> query(ISpecification specification) throws PvExtPersistenceException {
                ISqlSpecification sq = (ISqlSpecification) specification;
                String id = (String) sq.getSqlParams()[0];
                List<SlmUserVo> l = new ArrayList<>();
                switch (id) {
                    case "0":
                    case "1":
                    case "2":
                    case "3":
                    case "4":
                    case "5":
                    case "6":
                    case "7":
                    case "8":
                    case "9":
                    {
                        SlmUserVo u = new SlmUserVo();
                        Map<String, Object> f = new HashMap<>();
                        f.put(SlmUserVo.FIELD_USER_ID, id);
                        u.fillDataFromMap(f);
                        l.add(u);
                    }
                        break;
                        
                    default:
                        break;
                }
                return l;
            }
        });
        acass.setPath("/users/pvim/slm");
        
        FirebaseDatabaseSynchronizerServiceImpl impl = new FirebaseDatabaseSynchronizerServiceImpl();
        impl.setSlmUserService(acass);
        impl.setFirebaseDatabaseReferenceService(fdb);
        
        final DatabaseReference ref = fdb.getDatabaseReference("/users/pvim/slm");
        final Holder h = new Holder(); h.cnt = 0;
        
        for (int i=0; i<5; ++i) {
            
            FbPvimSlmUserJson js = new FbPvimSlmUserJson();
            js.setUserId("Test20000000000" + i);
            js.setUserType("SLM");
            js.setEmail("Testemail.com");
            js.setLoginName("test.email.test");
            js.setMobile("5555555555555");
            
            ref.child(js.getUserId()).setValue(js, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError de, DatabaseReference dr) {
                    if (de == null) {
                        synchronized(h) {
                            h.cnt++;
                            if (h.cnt == 5) {
                                h.notifyAll();
                            }
                        }
                    }
                }
            });
        }
        
        synchronized(h) {
            h.wait(20000);
        }
        
        if (h.cnt != 5) {
            fail("Fail test, not all callbacks completed");
        }
        
        impl.synchronize();
        
        Thread.sleep(30000);
        
        Holder suc = new Holder();
        suc.cnt = 0;
        suc.completed = false;
        
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot ds) {
                for (DataSnapshot childDs : ds.getChildren()) {
                    FbPvimSlmUserJson js = childDs.getValue(FbPvimSlmUserJson.class);
                    if (js != null) {
                        if (js.getUserId().startsWith("Test20000000000")) {
                            synchronized(suc) {
                                suc.cnt++;
                            }
                        }
                    }
                }
                synchronized(suc) {
                    suc.completed = true;
                    suc.notifyAll();
                }
            }

            @Override
            public void onCancelled(DatabaseError de) {
            }
        });
        
        synchronized(suc) {
            suc.wait(30000);
        }
        synchronized(suc) {
            if (!suc.completed) {
                fail("fail complete all callbacks after downloading data from server check after synchronizing!");
            }

            assertEquals(0, suc.cnt);
        }
        Logger.getLogger(this.getClass().getName()).log(Level.INFO, " << testSynchronizeSyncSlmUsersNormalCase()");
    }
    
    
    @Test
    public void testSynchronizeSyncTicketNormalCase() throws Exception {
        Logger.getLogger(this.getClass().getName()).log(Level.INFO, " >> testSynchronizeSyncTicketNormalCase()");
        
        FbSyncTicketService acass = new FbSyncTicketService();
        acass.setTicketRepo(new ITicketRepository() {
            @Override
            public List<TicketVo> query(ISpecification specification) throws PvExtPersistenceException {
                String ticketNumber = (String) ((ISqlSpecification) specification).getSqlParams()[0];
                
                if (ticketNumber != null && ticketNumber.startsWith("RemoveMe1")) {
                    return Collections.EMPTY_LIST;
                } else {
                    TicketVo t = new TicketVo();
                    Map<String, Object> f = new HashMap<>();
                    f.put(TicketVo.FIELD_TICKETNUMBER, ticketNumber);
                    t.fillDataFromMap(f);
                    List<TicketVo> z = new ArrayList<>();
                    z.add(t);
                    return z;
                }
            }
        });
        acass.setPath("/tickets");
        
        FirebaseDatabaseSynchronizerServiceImpl impl = new FirebaseDatabaseSynchronizerServiceImpl();
        impl.setTicketService(acass);
        impl.setFirebaseDatabaseReferenceService(fdb);
        
        final DatabaseReference ref = fdb.getDatabaseReference("/tickets");
        final Holder h = new Holder(); h.cnt = 0;
        
        for (int i=0; i<5; ++i) {
            
            FbTicketDto js = new FbTicketDto();
            js.setTicketId("ticket_id_test_" + i);
            js.setTicketNumber("RemoveMe1" + i);
            js.setTicketState("15");
            js.setAssignee("jichael.mackson");
            js.setNote("test database delete synchronizer");
            
            ref.child(js.getTicketId()).setValue(js, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError de, DatabaseReference dr) {
                    if (de == null) {
                        synchronized(h) {
                            h.cnt++;
                            if (h.cnt == 5) {
                                h.notifyAll();
                            }
                        }
                    }
                }
            });
        }
        
        synchronized(h) {
            h.wait(20000);
        }
        
        if (h.cnt != 5) {
            fail("Fail test, not all callbacks completed");
        }
        
        impl.synchronize();
        
        Thread.sleep(30000);
        
        Holder suc = new Holder();
        suc.cnt = 0;
        suc.completed = false;
        
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot ds) {
                for (DataSnapshot childDs : ds.getChildren()) {
                    FbTicketDto js = childDs.getValue(FbTicketDto.class);
                    if (js != null) {
                        if (js.getTicketNumber().startsWith("RemoveMe1")) {
                            synchronized(suc) {
                                suc.cnt++;
                            }
                        }
                    }
                }
                synchronized(suc) {
                    suc.completed = true;
                    suc.notifyAll();
                }
            }

            @Override
            public void onCancelled(DatabaseError de) {
            }
        });
        
        synchronized(suc) {
            suc.wait(30000);
        }
        synchronized(suc) {
            if (!suc.completed) {
                fail("fail complete all callbacks after downloading data from server check after synchronizing!");
            }

            assertEquals(0, suc.cnt);
        }
        Logger.getLogger(this.getClass().getName()).log(Level.INFO, " << testSynchronizeSyncTicketNormalCase()");
    }
    
}
