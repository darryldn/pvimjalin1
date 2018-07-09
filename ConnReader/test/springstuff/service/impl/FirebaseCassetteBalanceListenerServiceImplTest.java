/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package springstuff.service.impl;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import id.dni.ext.web.ws.obj.firebase.FbDeviceJson;
import id.dni.pvim.ext.dto.PvWsCassette;
import id.dni.pvim.ext.web.in.OperationError;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import springstuff.exceptions.CassetteBalanceServiceException;
import springstuff.exceptions.RemoteWsException;
import springstuff.service.IPvWebService;
import springstuff.service.firebase.FirebaseDatabaseReferenceService;

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
    
//    @Test
    public void testCassetteBalanceListenerThrowCustomException() throws Exception {
        FirebaseCassetteBalanceListenerServiceImpl impl = new FirebaseCassetteBalanceListenerServiceImpl();
        impl.setAtmDevicesFirebaseDBPath("/ATM_cassette_balance");
        impl.setFirebaseDatabaseReferenceService(fdb);
        impl.setPvWebService(new IPvWebService() {
            @Override
            public List<PvWsCassette> getCassetteBalance(String machineId) throws RemoteWsException {
                OperationError err = new OperationError();
                err.setErrCode("-999999");
                err.setErrMsg("Sample error message test from custom exception");
                throw new RemoteWsException(new CassetteBalanceServiceException(err));
            }
        });
        impl.init();
        
        final Object lock = new Object();
        synchronized(lock) {
            lock.wait(30000);
        }
        
        // now, go to firebase console and remove the cassete_balance under /ATM_cassette_balance/deviceId
        // the expected behavior is after the branch is deleted, it is recreated again automatically
        // the content should be:
        // "err": {
        //   "errCode": "-999999",
        //   "errMsg": "Sample error message test from custom exception"
        //}
    }

//    @Test
    public void testCassetteBalanceListenerThrowIOException() throws Exception {
        FirebaseCassetteBalanceListenerServiceImpl impl = new FirebaseCassetteBalanceListenerServiceImpl();
        impl.setAtmDevicesFirebaseDBPath("/ATM_cassette_balance");
        impl.setFirebaseDatabaseReferenceService(fdb);
        impl.setPvWebService(new IPvWebService() {
            @Override
            public List<PvWsCassette> getCassetteBalance(String machineId) throws RemoteWsException {
                throw new RemoteWsException(new IOException());
            }
        });
        impl.init();
        
        final Object lock = new Object();
        synchronized(lock) {
            lock.wait(30000);
        }
        
        // now, go to firebase console and remove the cassete_balance under /ATM_cassette_balance/deviceId
        // the expected behavior is after the branch is deleted, it is recreated again automatically
        // content should be:
        // "err": {
        //   "errCode": "-20000",
        //   "errMsg": "<SOME GENERIC ERROR MESSAGE HERE>"
        //}
    }
    
    @Test
    public void testCassetteBalanceListenerNormal() throws Exception {
        FirebaseCassetteBalanceListenerServiceImpl impl = new FirebaseCassetteBalanceListenerServiceImpl();
        impl.setAtmDevicesFirebaseDBPath("/ATM_cassette_balance");
        impl.setFirebaseDatabaseReferenceService(fdb);
        impl.setPvWebService(new IPvWebService() {
            @Override
            public List<PvWsCassette> getCassetteBalance(String machineId) throws RemoteWsException {
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
                
                return s;
            }
        });
        impl.init();
        
        final Object lock = new Object();
        synchronized(lock) {
            lock.wait(150000);
        }
        
        // now, go to firebase console and remove the cassete_balance under /ATM_cassette_balance/deviceId
        // the expected behavior is after the branch is deleted, it is recreated again automatically
    }
    
    
    
}
