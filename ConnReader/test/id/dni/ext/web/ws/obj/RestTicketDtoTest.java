/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.dni.ext.web.ws.obj;

import com.wn.econnect.inbound.wsi.ticket.TicketDto;
import java.util.Map;
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
public class RestTicketDtoTest {
    
    public RestTicketDtoTest() {
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

    
    @Test
    public void testConvert() {
        RestTicketDto ticketDto = new RestTicketDto();
        Map<String, Object> mapped = TicketUtil.convert(ticketDto);
        RestTicketDto another = RestTicketDto.convert(mapped);
        assertEquals(ticketDto, another);
    }
    
}
