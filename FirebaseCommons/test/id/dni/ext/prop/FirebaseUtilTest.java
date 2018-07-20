/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.dni.ext.prop;

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
public class FirebaseUtilTest {
    
    public FirebaseUtilTest() {
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
     * Test of isValidKey method, of class FirebaseUtil.
     */
    @Test
    public void testIsValidKeyExpectTrue() {
        System.out.println("isValidKey");
        String key = "abcdef";
        boolean expResult = true;
        boolean result = FirebaseUtil.isValidKey(key);
        assertEquals(expResult, result);
    }
    
    @Test
    public void testIsValidKeyExpectFalse() {
        System.out.println("isValidKey");
        String key = "abcdef.";
        boolean expResult = false;
        boolean result = FirebaseUtil.isValidKey(key);
        assertEquals(expResult, result);
    }
    
    @Test
    public void testIsValidKeyExpectFalse2() {
        System.out.println("isValidKey");
        String key = "abcdef$";
        boolean expResult = false;
        boolean result = FirebaseUtil.isValidKey(key);
        assertEquals(expResult, result);
    }
    
    @Test
    public void testIsValidKeyExpectFalse3() {
        System.out.println("isValidKey");
        String key = "abcdef/";
        boolean expResult = false;
        boolean result = FirebaseUtil.isValidKey(key);
        assertEquals(expResult, result);
    }
    
    @Test
    public void testIsValidKeyExpectFalse4() {
        System.out.println("isValidKey");
        String key = "abcdef[";
        boolean expResult = false;
        boolean result = FirebaseUtil.isValidKey(key);
        assertEquals(expResult, result);
    }
    
    @Test
    public void testIsValidKeyExpectFalse5() {
        System.out.println("isValidKey");
        String key = "abcdef]";
        boolean expResult = false;
        boolean result = FirebaseUtil.isValidKey(key);
        assertEquals(expResult, result);
    }
    
    @Test
    public void testIsValidKeyExpectFalse6() {
        System.out.println("isValidKey");
        String key = "ab/cdef.";
        boolean expResult = false;
        boolean result = FirebaseUtil.isValidKey(key);
        assertEquals(expResult, result);
    }
    
    @Test
    public void testIsValidKeyEmptyExpectTrue() {
        System.out.println("isValidKey");
        String key = "";
        boolean expResult = true;
        boolean result = FirebaseUtil.isValidKey(key);
        assertEquals(expResult, result);
    }
    
    @Test
    public void testIsValidKeyNullExpectTrue() {
        System.out.println("isValidKey");
        String key = null;
        boolean expResult = true;
        boolean result = FirebaseUtil.isValidKey(key);
        assertEquals(expResult, result);
    }
    
}
