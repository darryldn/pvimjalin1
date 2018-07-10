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
import id.dni.ext.web.ws.obj.firebase.internal.FbCassetteBalanceJson;
import id.dni.pvim.ext.dto.PvWsCassette;
import id.dni.pvim.ext.web.in.OperationError;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
import springstuff.exceptions.CassetteBalanceServiceException;
import springstuff.exceptions.RemoteWsException;
import springstuff.service.IPvWebService;

/**
 *
 * @author darryl.sulistyan
 */
public class FirebaseCassetteBalanceListenerServiceImplTest {
    
    public FirebaseCassetteBalanceListenerServiceImplTest() {
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
        boolean flag;
    }
    
    @Test
    public void testCassetteBalanceListenerThrowCustomException() throws Exception {
        String rootpath = "/ATM_cassette_balance";
        OperationError expectedErr = new OperationError();
        expectedErr.setErrCode("-999999");
        expectedErr.setErrMsg("Sample error message test from custom exception");
        
        FirebaseCassetteBalanceListenerServiceImpl impl = new FirebaseCassetteBalanceListenerServiceImpl();
        impl.setAtmDevicesFirebaseDBPath(rootpath);
        impl.setFirebaseDatabaseReferenceService(fdb);
        impl.setPvWebService(new IPvWebService() {
            @Override
            public List<PvWsCassette> getCassetteBalance(String machineId) throws RemoteWsException {
                throw new RemoteWsException(new CassetteBalanceServiceException(expectedErr));
            }
        });
        impl.init();
        
        final Object lock = new Object();
        Thread.sleep(1000);
        
        // now, go to firebase console and remove the cassete_balance under /ATM_cassette_balance/deviceId
        // the expected behavior is after the branch is deleted, it is recreated again automatically
        // the content should be:
        // "err": {
        //   "errCode": "-999999",
        //   "errMsg": "Sample error message test from custom exception"
        //}
        
        String NEW_DEVICE_ID = "TEST_CASSETTE_ID_0001";
        DatabaseReference ref = fdb.getDatabaseReference(rootpath);
        FbAtmCassetteBalanceWrapperJson cassetteBalanceWrapper = new FbAtmCassetteBalanceWrapperJson();
        cassetteBalanceWrapper.setDeviceId(NEW_DEVICE_ID);
        Holder h = new Holder();
        h.flag = false;
        
        ref.child(NEW_DEVICE_ID).setValue(cassetteBalanceWrapper, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError de, DatabaseReference dr) {
                if (de == null) {
                    h.flag = true;
                    synchronized(lock) {
                        lock.notifyAll();
                    }
                }
            }
        });
        
        synchronized(lock) {
            lock.wait(10000);
        }
        
        if (!h.flag) {
            fail("Failed setting cassette balance for " + NEW_DEVICE_ID);
        }
        
        synchronized(lock) {
            lock.wait(10000);
        }
        // wait until the child handler in impl created the cassette infos, because
        // they are not given
        // in here, it should be err because that's what we give.
        
        h.flag = false;
        
        ref.child(NEW_DEVICE_ID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot ds) {
                FbAtmCassetteBalanceWrapperJson fb = ds.getValue(FbAtmCassetteBalanceWrapperJson.class);
                if (fb != null) {
                    assertNotNull(fb.getCassette_balance());
                    Logger.getLogger(this.getClass().getName()).log(Level.INFO, " - cassette balance json object is not null, ok");
                    assertEquals(fb.getDeviceId(), NEW_DEVICE_ID);
                    Logger.getLogger(this.getClass().getName()).log(Level.INFO, " - device id ok");
                    assertNull(fb.getCassette_balance().getCassettes());
                    Logger.getLogger(this.getClass().getName()).log(Level.INFO, " - cassettes info is null, ok");
                    assertNotNull(fb.getCassette_balance().getErr());
                    Logger.getLogger(this.getClass().getName()).log(Level.INFO, " - cassettes error info is not null");
                    assertNotNull(fb.getCassette_balance().getTimestamp());
                    Logger.getLogger(this.getClass().getName()).log(Level.INFO, " - timestamp exists");
                    assertEquals(fb.getCassette_balance().getErr().getErrCode(), expectedErr.getErrCode());
                    Logger.getLogger(this.getClass().getName()).log(Level.INFO, " - errCode equals as expected");
                    assertEquals(fb.getCassette_balance().getErr().getErrMsg(), expectedErr.getErrMsg());
                    Logger.getLogger(this.getClass().getName()).log(Level.INFO, " - errMsg equals as expected");
                    synchronized(lock) {
                        h.flag = true;
                        lock.notifyAll();
                    }
                }
                
            }

            @Override
            public void onCancelled(DatabaseError de) {
            }
        });
        
        synchronized(lock) {
            lock.wait(10000);
        }
        
        if (!h.flag) {
            fail("Object in firebase =/= expected");
        }
        
    }

    @Test
    public void testCassetteBalanceListenerThrowIOException() throws Exception {
        String rootpath = "/ATM_cassette_balance";
        OperationError expectedErr = new OperationError();
        expectedErr.setErrCode("-20000");
        expectedErr.setErrMsg("Cannot obtain cassete balance information.");
        
        FirebaseCassetteBalanceListenerServiceImpl impl = new FirebaseCassetteBalanceListenerServiceImpl();
        impl.setAtmDevicesFirebaseDBPath(rootpath);
        impl.setFirebaseDatabaseReferenceService(fdb);
        impl.setPvWebService(new IPvWebService() {
            @Override
            public List<PvWsCassette> getCassetteBalance(String machineId) throws RemoteWsException {
                throw new RemoteWsException(new IOException("Test throw IOException"));
            }
        });
        impl.init();
        
        final Object lock = new Object();
        Thread.sleep(1000);
        
        // now, go to firebase console and remove the cassete_balance under /ATM_cassette_balance/deviceId
        // the expected behavior is after the branch is deleted, it is recreated again automatically
        // the content should be:
        // "err": {
        //   "errCode": "-999999",
        //   "errMsg": "Sample error message test from custom exception"
        //}
        
        String NEW_DEVICE_ID = "TEST_CASSETTE_ID_0002";
        DatabaseReference ref = fdb.getDatabaseReference(rootpath);
        FbAtmCassetteBalanceWrapperJson cassetteBalanceWrapper = new FbAtmCassetteBalanceWrapperJson();
        cassetteBalanceWrapper.setDeviceId(NEW_DEVICE_ID);
        Holder h = new Holder();
        h.flag = false;
        
        ref.child(NEW_DEVICE_ID).setValue(cassetteBalanceWrapper, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError de, DatabaseReference dr) {
                if (de == null) {
                    h.flag = true;
                    synchronized(lock) {
                        lock.notifyAll();
                    }
                }
            }
        });
        
        synchronized(lock) {
            lock.wait(10000);
        }
        
        if (!h.flag) {
            fail("Failed setting cassette balance for " + NEW_DEVICE_ID);
        }
        
        synchronized(lock) {
            lock.wait(10000);
        }
        // wait until the child handler in impl created the cassette infos, because
        // they are not given
        // in here, it should be err because that's what we give.
        
        h.flag = false;
        
        ref.child(NEW_DEVICE_ID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot ds) {
                FbAtmCassetteBalanceWrapperJson fb = ds.getValue(FbAtmCassetteBalanceWrapperJson.class);
                if (fb != null) {
                    assertNotNull(fb.getCassette_balance());
                    Logger.getLogger(this.getClass().getName()).log(Level.INFO, " - cassette balance json object is not null, ok");
                    assertEquals(fb.getDeviceId(), NEW_DEVICE_ID);
                    Logger.getLogger(this.getClass().getName()).log(Level.INFO, " - device id ok");
                    assertNull(fb.getCassette_balance().getCassettes());
                    Logger.getLogger(this.getClass().getName()).log(Level.INFO, " - cassettes info is null, ok");
                    assertNotNull(fb.getCassette_balance().getErr());
                    Logger.getLogger(this.getClass().getName()).log(Level.INFO, " - cassettes error info is not null");
                    assertNotNull(fb.getCassette_balance().getTimestamp());
                    Logger.getLogger(this.getClass().getName()).log(Level.INFO, " - timestamp exists");
                    assertEquals(fb.getCassette_balance().getErr().getErrCode(), expectedErr.getErrCode());
                    Logger.getLogger(this.getClass().getName()).log(Level.INFO, " - errCode equals as expected");
                    assertEquals(fb.getCassette_balance().getErr().getErrMsg(), expectedErr.getErrMsg());
                    Logger.getLogger(this.getClass().getName()).log(Level.INFO, " - errMsg equals as expected");
                    synchronized(lock) {
                        h.flag = true;
                        lock.notifyAll();
                    }
                }
                
            }

            @Override
            public void onCancelled(DatabaseError de) {
            }
        });
        
        synchronized(lock) {
            lock.wait(10000);
        }
        
        if (!h.flag) {
            fail("Object in firebase =/= expected");
        }
        
    }
    
    @Test
    public void testCassetteBalanceListenerNormal() throws Exception {
        String rootpath = "/ATM_cassette_balance";
        List<PvWsCassette> expectedCassetteList;
        {
            List<PvWsCassette> s = new ArrayList<>();

            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
            for (int i=0; i<5; ++i) {
                PvWsCassette c = new PvWsCassette();
                c.setCassetteExchanged(df.format(new Date()));
                c.setCassetteId("1234" + i);
                c.setCassetteType("0");
                c.setCurrency("IDR");
                c.setDenomination("50000");
                c.setDeposited("125");
                c.setDispensed("164");
                c.setFilling("167");
                c.setRejected("6");
                c.setRemainingTotal("" + (167 * 50000));
                c.setStart("4567");
                c.setStatus("0");
                s.add(c);
            }
            expectedCassetteList = s;
        }
        
        FirebaseCassetteBalanceListenerServiceImpl impl = new FirebaseCassetteBalanceListenerServiceImpl();
        impl.setAtmDevicesFirebaseDBPath(rootpath);
        impl.setFirebaseDatabaseReferenceService(fdb);
        impl.setPvWebService(new IPvWebService() {
            @Override
            public List<PvWsCassette> getCassetteBalance(String machineId) throws RemoteWsException {
                return expectedCassetteList;
            }
        });
        impl.init();
        
        final Object lock = new Object();
        synchronized(lock) {
            lock.wait(1000);
        }
        
        // now, go to firebase console and remove the cassete_balance under /ATM_cassette_balance/deviceId
        // the expected behavior is after the branch is deleted, it is recreated again automatically
        
        String NEW_DEVICE_ID = "TEST_CASSETTE_ID_0003";
        DatabaseReference ref = fdb.getDatabaseReference(rootpath);
        FbAtmCassetteBalanceWrapperJson cassetteBalanceWrapper = new FbAtmCassetteBalanceWrapperJson();
        cassetteBalanceWrapper.setDeviceId(NEW_DEVICE_ID);
        Holder h = new Holder();
        h.flag = false;
        
        ref.child(NEW_DEVICE_ID).setValue(cassetteBalanceWrapper, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError de, DatabaseReference dr) {
                if (de == null) {
                    h.flag = true;
                    synchronized(lock) {
                        lock.notifyAll();
                    }
                }
            }
        });
        
        synchronized(lock) {
            lock.wait(10000);
        }
        
        if (!h.flag) {
            fail("Failed setting cassette balance for " + NEW_DEVICE_ID);
        }
        
        synchronized(lock) {
            lock.wait(10000);
        }
        // wait until the child handler in impl created the cassette infos, because
        // they are not given
        // in here, it should be err because that's what we give.
        
        h.flag = false;
        
        ref.child(NEW_DEVICE_ID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot ds) {
                FbAtmCassetteBalanceWrapperJson fb = ds.getValue(FbAtmCassetteBalanceWrapperJson.class);
                if (fb != null) {
                    assertNotNull(fb.getCassette_balance());
                    Logger.getLogger(this.getClass().getName()).log(Level.INFO, " - cassette balance json object is not null, ok");
                    assertEquals(fb.getDeviceId(), NEW_DEVICE_ID);
                    Logger.getLogger(this.getClass().getName()).log(Level.INFO, " - device id ok");
                    assertNotNull(fb.getCassette_balance().getCassettes());
                    Logger.getLogger(this.getClass().getName()).log(Level.INFO, " - cassettes info is not null, ok");
                    assertNull(fb.getCassette_balance().getErr());
                    Logger.getLogger(this.getClass().getName()).log(Level.INFO, " - cassettes error info is null");
                    assertNotNull(fb.getCassette_balance().getTimestamp());
                    Logger.getLogger(this.getClass().getName()).log(Level.INFO, " - timestamp exists");
                    
                    Map<String, PvWsCassette> cassetteMap = fb.getCassette_balance().getCassettes();
                    assertEquals(cassetteMap.size(), expectedCassetteList.size());
                    Logger.getLogger(this.getClass().getName()).log(Level.INFO, " - cassetteMap size == expected size");
                    
                    for (PvWsCassette expected : expectedCassetteList) {
                        assertNotNull(cassetteMap.get(expected.getCassetteId()));
                        Logger.getLogger(this.getClass().getName()).log(Level.INFO, " - cassetteId: {0} is found", expected.getCassetteId());
                        assertEquals(cassetteMap.get(expected.getCassetteId()), expected);
                        Logger.getLogger(this.getClass().getName()).log(Level.INFO, " - cassette in firebase == expected");
                    }
                    
                    synchronized(lock) {
                        h.flag = true;
                        lock.notifyAll();
                    }
                }
                
            }

            @Override
            public void onCancelled(DatabaseError de) {
            }
        });
        
        synchronized(lock) {
            lock.wait(10000);
        }
        
        if (!h.flag) {
            fail("Object in firebase =/= expected");
        }
        
    }


    @Test
    public void testCassetteBalanceListenerSetCassettesZeroTimestamp() throws Exception {
        String rootpath = "/ATM_cassette_balance";
        List<PvWsCassette> expectedCassetteList;
        {
            List<PvWsCassette> s = new ArrayList<>();

            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
            for (int i=0; i<5; ++i) {
                PvWsCassette c = new PvWsCassette();
                c.setCassetteExchanged(df.format(new Date()));
                c.setCassetteId("1234" + i);
                c.setCassetteType("0");
                c.setCurrency("IDR");
                c.setDenomination("50000");
                c.setDeposited("125");
                c.setDispensed("164");
                c.setFilling("167");
                c.setRejected("6");
                c.setRemainingTotal("" + (167 * 50000));
                c.setStart("4567");
                c.setStatus("0");
                s.add(c);
            }
            expectedCassetteList = s;
        }
        
        FirebaseCassetteBalanceListenerServiceImpl impl = new FirebaseCassetteBalanceListenerServiceImpl();
        impl.setAtmDevicesFirebaseDBPath(rootpath);
        impl.setFirebaseDatabaseReferenceService(fdb);
        impl.setPvWebService(new IPvWebService() {
            @Override
            public List<PvWsCassette> getCassetteBalance(String machineId) throws RemoteWsException {
                return expectedCassetteList;
            }
        });
        impl.init();
        
        final Object lock = new Object();
        synchronized(lock) {
            lock.wait(1000);
        }
        
        // now, go to firebase console and remove the cassete_balance under /ATM_cassette_balance/deviceId
        // the expected behavior is after the branch is deleted, it is recreated again automatically
        
        String NEW_DEVICE_ID = "TEST_CASSETTE_ID_0004";
        DatabaseReference ref = fdb.getDatabaseReference(rootpath);
        FbAtmCassetteBalanceWrapperJson cassetteBalanceWrapper = new FbAtmCassetteBalanceWrapperJson();
        cassetteBalanceWrapper.setDeviceId(NEW_DEVICE_ID);
        FbCassetteBalanceJson cassettes = new FbCassetteBalanceJson();
        cassettes.setTimestamp((long)0);
        Holder h = new Holder();
        h.flag = false;
        
        ref.child(NEW_DEVICE_ID).setValue(cassetteBalanceWrapper, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError de, DatabaseReference dr) {
                if (de == null) {
                    h.flag = true;
                    synchronized(lock) {
                        lock.notifyAll();
                    }
                }
            }
        });
        
        synchronized(lock) {
            lock.wait(10000);
        }
        
        if (!h.flag) {
            fail("Failed setting cassette balance for " + NEW_DEVICE_ID);
        }
        
        synchronized(lock) {
            lock.wait(10000);
        }
        // wait until the child handler in impl created the cassette infos, because
        // they are not given
        // in here, it should be err because that's what we give.
        
        h.flag = false;
        
        ref.child(NEW_DEVICE_ID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot ds) {
                FbAtmCassetteBalanceWrapperJson fb = ds.getValue(FbAtmCassetteBalanceWrapperJson.class);
                if (fb != null) {
                    assertNotNull(fb.getCassette_balance());
                    Logger.getLogger(this.getClass().getName()).log(Level.INFO, " - cassette balance json object is not null, ok");
                    assertEquals(fb.getDeviceId(), NEW_DEVICE_ID);
                    Logger.getLogger(this.getClass().getName()).log(Level.INFO, " - device id ok");
                    assertNotNull(fb.getCassette_balance().getCassettes());
                    Logger.getLogger(this.getClass().getName()).log(Level.INFO, " - cassettes info is not null, ok");
                    assertNull(fb.getCassette_balance().getErr());
                    Logger.getLogger(this.getClass().getName()).log(Level.INFO, " - cassettes error info is null");
                    assertNotNull(fb.getCassette_balance().getTimestamp());
                    Logger.getLogger(this.getClass().getName()).log(Level.INFO, " - timestamp exists");
                    
                    Map<String, PvWsCassette> cassetteMap = fb.getCassette_balance().getCassettes();
                    assertEquals(cassetteMap.size(), expectedCassetteList.size());
                    Logger.getLogger(this.getClass().getName()).log(Level.INFO, " - cassetteMap size == expected size");
                    
                    for (PvWsCassette expected : expectedCassetteList) {
                        assertNotNull(cassetteMap.get(expected.getCassetteId()));
                        Logger.getLogger(this.getClass().getName()).log(Level.INFO, " - cassetteId: {0} is found", expected.getCassetteId());
                        assertEquals(cassetteMap.get(expected.getCassetteId()), expected);
                        Logger.getLogger(this.getClass().getName()).log(Level.INFO, " - cassette in firebase == expected");
                    }
                    
                    synchronized(lock) {
                        h.flag = true;
                        lock.notifyAll();
                    }
                }
                
            }

            @Override
            public void onCancelled(DatabaseError de) {
            }
        });
        
        synchronized(lock) {
            lock.wait(10000);
        }
        
        if (!h.flag) {
            fail("Object in firebase =/= expected");
        }
        
    }
    
    @Test
    public void testCassetteBalanceListenerSetCassettesNullTimestamp() throws Exception {
        String rootpath = "/ATM_cassette_balance";
        List<PvWsCassette> expectedCassetteList;
        {
            List<PvWsCassette> s = new ArrayList<>();

            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
            for (int i=0; i<5; ++i) {
                PvWsCassette c = new PvWsCassette();
                c.setCassetteExchanged(df.format(new Date()));
                c.setCassetteId("1234" + i);
                c.setCassetteType("0");
                c.setCurrency("IDR");
                c.setDenomination("50000");
                c.setDeposited("125");
                c.setDispensed("164");
                c.setFilling("167");
                c.setRejected("6");
                c.setRemainingTotal("" + (167 * 50000));
                c.setStart("4567");
                c.setStatus("0");
                s.add(c);
            }
            expectedCassetteList = s;
        }
        
        FirebaseCassetteBalanceListenerServiceImpl impl = new FirebaseCassetteBalanceListenerServiceImpl();
        impl.setAtmDevicesFirebaseDBPath(rootpath);
        impl.setFirebaseDatabaseReferenceService(fdb);
        impl.setPvWebService(new IPvWebService() {
            @Override
            public List<PvWsCassette> getCassetteBalance(String machineId) throws RemoteWsException {
                return expectedCassetteList;
            }
        });
        impl.init();
        
        final Object lock = new Object();
        synchronized(lock) {
            lock.wait(1000);
        }
        
        // now, go to firebase console and remove the cassete_balance under /ATM_cassette_balance/deviceId
        // the expected behavior is after the branch is deleted, it is recreated again automatically
        
        String NEW_DEVICE_ID = "TEST_CASSETTE_ID_0005";
        DatabaseReference ref = fdb.getDatabaseReference(rootpath);
        FbAtmCassetteBalanceWrapperJson cassetteBalanceWrapper = new FbAtmCassetteBalanceWrapperJson();
        cassetteBalanceWrapper.setDeviceId(NEW_DEVICE_ID);
        FbCassetteBalanceJson cassettes = new FbCassetteBalanceJson();
        
        Map<String, PvWsCassette> tempCassetes = new HashMap<>();
        PvWsCassette cs = new PvWsCassette();
        cs.setCassetteId("000000x");
        tempCassetes.put(cs.getCassetteId(), cs);
        cassettes.setCassettes(tempCassetes);
        
        cassetteBalanceWrapper.setCassette_balance(cassettes);
        
        Holder h = new Holder();
        h.flag = false;
        
        ref.child(NEW_DEVICE_ID).setValue(cassetteBalanceWrapper, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError de, DatabaseReference dr) {
                if (de == null) {
                    h.flag = true;
                    synchronized(lock) {
                        lock.notifyAll();
                    }
                }
            }
        });
        
        synchronized(lock) {
            lock.wait(10000);
        }
        
        if (!h.flag) {
            fail("Failed setting cassette balance for " + NEW_DEVICE_ID);
        }
        
        synchronized(lock) {
            lock.wait(10000);
        }
        // wait until the child handler in impl created the cassette infos, because
        // they are not given
        // in here, it should be err because that's what we give.
        
        h.flag = false;
        
        ref.child(NEW_DEVICE_ID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot ds) {
                FbAtmCassetteBalanceWrapperJson fb = ds.getValue(FbAtmCassetteBalanceWrapperJson.class);
                if (fb != null) {
                    assertNotNull(fb.getCassette_balance());
                    Logger.getLogger(this.getClass().getName()).log(Level.INFO, " - cassette balance json object is not null, ok");
                    assertEquals(fb.getDeviceId(), NEW_DEVICE_ID);
                    Logger.getLogger(this.getClass().getName()).log(Level.INFO, " - device id ok");
                    assertNotNull(fb.getCassette_balance().getCassettes());
                    Logger.getLogger(this.getClass().getName()).log(Level.INFO, " - cassettes info is not null, ok");
                    assertNull(fb.getCassette_balance().getErr());
                    Logger.getLogger(this.getClass().getName()).log(Level.INFO, " - cassettes error info is null");
                    assertNotNull(fb.getCassette_balance().getTimestamp());
                    Logger.getLogger(this.getClass().getName()).log(Level.INFO, " - timestamp exists");
                    
                    Map<String, PvWsCassette> cassetteMap = fb.getCassette_balance().getCassettes();
                    assertEquals(cassetteMap.size(), expectedCassetteList.size());
                    Logger.getLogger(this.getClass().getName()).log(Level.INFO, " - cassetteMap size == expected size");
                    
                    for (PvWsCassette expected : expectedCassetteList) {
                        assertNotNull(cassetteMap.get(expected.getCassetteId()));
                        Logger.getLogger(this.getClass().getName()).log(Level.INFO, " - cassetteId: {0} is found", expected.getCassetteId());
                        assertEquals(cassetteMap.get(expected.getCassetteId()), expected);
                        Logger.getLogger(this.getClass().getName()).log(Level.INFO, " - cassette in firebase == expected");
                    }
                    
                    synchronized(lock) {
                        h.flag = true;
                        lock.notifyAll();
                    }
                }
                
            }

            @Override
            public void onCancelled(DatabaseError de) {
            }
        });
        
        synchronized(lock) {
            lock.wait(10000);
        }
        
        if (!h.flag) {
            fail("Object in firebase =/= expected");
        }
        
    }
    
}
