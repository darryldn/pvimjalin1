/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wn.tasman.id.dni.pvim.ext.notification.telegram;

import com.wn.tasman.id.dni.pvim.ext.notification.telegram.TelegramNotificationMessageListener;
import com.wn.tasman.basedata.SystemVariableManager;
import com.wn.tasman.notification.dao.NotificationManagerDAO;
import com.wn.tasman.notification.ext.dni.services.AsyncSender;
import com.wn.tasman.ticket.TicketManager;
import com.wn.tasman.ticket.domain.Ticket;
import id.dni.pvim.ext.telegram.repo.ITelegramSuscribersRepository;
import id.dni.pvim.ext.telegram.repo.db.vo.TelegramSubscriberVo;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import javax.jms.Message;
import org.hibernate.dialect.H2Dialect;
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
public class TelegramNotificationMessageListenerTest {
    
    public TelegramNotificationMessageListenerTest() {
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
    public void testJoinNoTrailling() {
        List<String> s = new ArrayList<>();
        s.add("ASDFG");
        s.add("BCDEF");
        s.add("GHIJK");
        String res = TelegramNotificationMessageListener.joinStr(s, ",");
        assertEquals("ASDFG,BCDEF,GHIJK", res);
    }
    
    @Test
    public void testJoinEmptyListNoTrailling() {
        List<String> s = new ArrayList<>();
        String res = TelegramNotificationMessageListener.joinStr(s, ",");
        assertEquals("", res);
    }
    
    @Test
    public void testJoinNULLListNoTrailling() {
        String res = TelegramNotificationMessageListener.joinStr(null, ",");
        assertEquals("", res);
    }
    
    @Test
    public void testExtractAccountsNoBlacklist() {
        List<String> res = TelegramNotificationMessageListener.extractAccounts("1,2,3,4,5", Collections.EMPTY_SET);
        for (int i=1; i<=5; ++i) {
            assertEquals("" + i, res.get(i-1));
        }
    }
    
    @Test
    public void testExtractAccountsWithBlacklist() {
        Set<String> blacklist = new HashSet<>();
        blacklist.add("1");
        blacklist.add("2");
        List<String> res = TelegramNotificationMessageListener.extractAccounts("1,2,3,4,5", blacklist);
        for (int i=3; i<=5; ++i) {
            assertEquals("" + i, res.get(i-3));
        }
    }

//    @Test
//    public void testDispatchTicketToSubscribersNoSubs() {
//        TelegramNotificationMessageListener listener = new TelegramNotificationMessageListener();
//        List<TelegramSubscriberVo> subs = new ArrayList<>();
////        List<TelegramRequestResult> ret = listener.dispatchTicketToSubscribers(subs, "");
////        assertEquals(ret.isEmpty(), true);
//    }
//    
//    @Test
//    public void testDispatchTicketToSubscribersAllThrows() {
//        TelegramNotificationMessageListener listener = new TelegramNotificationMessageListener();
//        listener.setAsyncSender(new AsyncSender() {
//            @Override
//            public <T> Future<T> doAsync(Callable<T> callable) {
//                ExecutorService es = null;
//                try {
//                    es = Executors.newFixedThreadPool(1);
//                    return es.submit(new Callable<T>() {
//                        @Override
//                        public T call() throws Exception {
//                            Thread.sleep(2000);
//                            return null; // simulate return null
//                        }
//                    });
//                } finally {
//                    if (es != null) {
//                        es.shutdown();
//                    }
//                }
//            }
//        });
//        
//        List<TelegramSubscriberVo> subs = new ArrayList<>();
//        for (int i=0; i<5; ++i) {
//            TelegramSubscriberVo s = new TelegramSubscriberVo();
//            s.setChat_id(i);
//            s.setPhone_num("phone-" + i);
//            subs.add(s);
//        }
//        
//        String message = "SEND THIS MESSAGE!";
//        
////        List<TelegramRequestResult> obtained = listener.dispatchTicketToSubscribers(subs, message);
////        assertEquals(obtained.size(), subs.size());
////        for (int i=0; i<subs.size(); ++i) {
////            TelegramRequestResult res = obtained.get(i);
////            assertEquals(res.getStatus(), -1);
////            assertEquals(res.getChatId(), subs.get(i).getChat_id());
////            assertEquals(res.getMessage(), message);
////            assertEquals(res.getMobile(), subs.get(i).getPhone_num());
////        }
//        
//        
//    }
    
}
