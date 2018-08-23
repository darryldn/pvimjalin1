/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.dni.ext.dmz.service.impl;

import id.dni.ext.dmz.exception.RemoteServiceException;
import id.dni.ext.firebase.user.msg.FbAuthUserServiceResponse;
import id.dni.ext.firebase.cloud.msg.json.FcmMessageDownstreamResponseJson;
import id.dni.ext.firebase.cloud.msg.json.FcmMessageJson;
import id.dni.ext.firebase.user.msg.FbAuthUserJson;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author darryl.sulistyan
 */
public class FirebaseServiceImplTest {
    
    private static FirebaseServiceImpl fdb;
    
    public FirebaseServiceImplTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
        fdb = new FirebaseServiceImpl();
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

    /**
     * Test of sendMessage method, of class FirebaseServiceImpl.
     */
//    @Test
//    public void testSendMessage() throws Exception {
//        // already tested
//    }

    /**
     * Test of createUser method, of class FirebaseServiceImpl.
     */
    @Test
    public void testDoubleCreatingUserExpectNoException() throws Exception {
        String email = "TEST1_cawabonga12345@gmail.dude";
        String pass = "TEST1_asdfasdfasdfasdfasdfasdf";
        
        FbAuthUserJson js = new FbAuthUserJson();
        js.setEmail(email);
        js.setPassword(pass);
        js.setUsername("bababa2");
        FbAuthUserServiceResponse createUser = fdb.createUser(js);
        assertEquals(createUser.isSuccess(), true);
        
        FbAuthUserServiceResponse createUser2 = fdb.createUser(js);
        assertEquals(createUser2.isSuccess(), true);
    }
    
    @Test
    public void testCreateUserWithEmptyEmailThrowException() throws Exception {
        String email = "";
        String pass = "TEST2_asdfasdfasdfasdfasdfasdf";
        
        FbAuthUserJson js = new FbAuthUserJson();
        js.setEmail(email);
        js.setPassword(pass);
        js.setUsername("bababa2");
        try {
            FbAuthUserServiceResponse createUser = fdb.createUser(js);
            fail("Should fail if email empty");
        } catch (RemoteServiceException ex) {
            
        }
    }
    
    @Test
    public void testCreateUserWithNullEmailThrowException() throws Exception {
        String email = null;
        String pass = "TEST2_asdfasdfasdfasdfasdfasdf";
        
        FbAuthUserJson js = new FbAuthUserJson();
        js.setEmail(email);
        js.setPassword(pass);
        js.setUsername("bababa2");
        try {
            FbAuthUserServiceResponse createUser = fdb.createUser(js);
            fail("Should fail if email null");
        } catch (RemoteServiceException ex) {
            
        }
    }

    /**
     * Test of removeUser method, of class FirebaseServiceImpl.
     */
    @Test
    public void testRemoveUserNotExisting() throws Exception {
        String email = "TEST3_cawabonga12345@gmail.dude";
        String pass = "TEST3_asdfasdfasdfasdfasdfasdf";
        
        FbAuthUserJson js = new FbAuthUserJson();
        js.setEmail(email);
        js.setPassword(pass);
        js.setUsername("bababa2");
        FbAuthUserServiceResponse removeUser = fdb.removeUser(js);
        assertTrue(removeUser.isSuccess());
    }
    
    /**
     * Test of removeUser method, of class FirebaseServiceImpl.
     */
    @Test
    public void testRemoveUser() throws Exception {
        String email = "TEST4_cawabonga12345@gmail.dude";
        String pass = "TEST4_asdfasdfasdfasdfasdfasdf";
        
        FbAuthUserJson js = new FbAuthUserJson();
        js.setEmail(email);
        js.setPassword(pass);
        js.setUsername("bababa2");
        FbAuthUserServiceResponse createUser = fdb.createUser(js);
        assertEquals(createUser.isSuccess(), true);
        
        FbAuthUserServiceResponse removeUser = fdb.removeUser(js);
        assertTrue(removeUser.isSuccess());
    }
    
}
