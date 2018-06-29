/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package springstuff.service.impl;

import java.util.ArrayList;
import java.util.List;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import springstuff.json.ComponentStateJson;
import springstuff.json.DeviceComponentStateJson;
import springstuff.json.MachineGpsJson;

/**
 *
 * @author darryl.sulistyan
 */
public class FirebaseRemoteDataRepositoryServiceImplTest {
    
    public FirebaseRemoteDataRepositoryServiceImplTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
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

    /**
     * Test of send method, of class FirebaseRemoteDataRepositoryServiceImpl.
     */
    @Test
    public void testSend() throws Exception {
        System.out.println("send");
        
        FirebaseDatabaseReferenceServiceImpl fdb = new FirebaseDatabaseReferenceServiceImpl();
        fdb.setFirebaseDatabaseUrl("https://vynamic-operation-7b1bc.firebaseio.com/");
        fdb.setFirebaseRootPath("/");
        fdb.setFirebaseServiceAuthJsonFile("/vynamic-operation-7b1bc-firebase-adminsdk-g3v7y-5d9b7040bc.json");
        fdb.setFirebaseTimeout("10000");
        fdb.init();
        
        List<DeviceComponentStateJson> devices = new ArrayList<>();
        FirebaseRemoteDataRepositoryServiceImpl instance = new FirebaseRemoteDataRepositoryServiceImpl();
        instance.setGpsFirebaseDBPath("/location");
        instance.setMachineStatusFirebaseDBPath("/ATMs");
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
        
        instance.setFirebaseDB(fdb);
        instance.send(devices);
        
        Thread.sleep(30000); // arbitrary
        
        fail("The test case is a prototype.");
    }
    
}
