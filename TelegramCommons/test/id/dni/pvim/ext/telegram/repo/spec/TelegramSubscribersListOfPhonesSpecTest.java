/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.dni.pvim.ext.telegram.repo.spec;

import id.dni.pvim.ext.telegram.repo.db.vo.TelegramSubscriberVo;
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
public class TelegramSubscribersListOfPhonesSpecTest {
    
    public TelegramSubscribersListOfPhonesSpecTest() {
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
     * Test of toParameterizedSqlQuery method, of class TelegramSubscribersListOfPhonesSpec.
     */
    @Test
    public void testToParameterizedSqlQuery() {
        System.out.println("toParameterizedSqlQuery");
        TelegramSubscribersListOfPhonesSpec instance = new TelegramSubscribersListOfPhonesSpec(new String[] {"12345", "67890"});
        String expResult = new StringBuilder()
                    .append("SELECT ").append(TelegramSubscriberVo.FIELD_SUBS_ID).append(", ")
                                      .append(TelegramSubscriberVo.FIELD_CHAT_ID).append(", ")
                                      .append(TelegramSubscriberVo.FIELD_PHONE_NUM).append(", ")
                                      .append(TelegramSubscriberVo.FIELD_LASTUPDATE).append(", ")
                                      .append(TelegramSubscriberVo.FIELD_PASSKEY).append(" ")
                    .append("FROM ")  .append(TelegramSubscriberVo.TABLE_NAME).append(" ")
                    .append("WHERE ") .append(TelegramSubscriberVo.FIELD_PHONE_NUM).append(" in ")
                    .append("(?,?)")
            .toString();
        
        String result = instance.toParameterizedSqlQuery();
        assertEquals(expResult, result);
    }
    
}
