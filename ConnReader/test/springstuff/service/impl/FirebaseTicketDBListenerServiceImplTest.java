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
import com.wn.econnect.inbound.wsi.ObjectFactory;
import com.wn.econnect.inbound.wsi.ticket.PvimWSException;
import id.dni.ext.web.ws.obj.RestTicketDto;
import id.dni.ext.web.ws.obj.TicketUtil;
import id.dni.ext.web.ws.obj.firebase.FbTicketDto;
import id.dni.pvim.ext.net.TransferTicketDto;
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
import springstuff.model.PvimTicketVo;
import springstuff.service.IPvimTicketWebService;

/**
 *
 * @author darryl.sulistyan
 */
public class FirebaseTicketDBListenerServiceImplTest {
    
    public FirebaseTicketDBListenerServiceImplTest() {
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

    @Test
    public void testExecuteListener() throws PvExtPersistenceException, InterruptedException {
        Logger.getLogger(this.getClass().getName()).log(Level.INFO, " >> testExecuteListener()");
        
        final Object lock = new Object();
        
        FirebaseTicketDBListenerServiceImpl impl = new FirebaseTicketDBListenerServiceImpl();
        impl.setFirebaseDatabaseReferenceService(fdb);
        
        impl.setTicketsFirebaseDBPath("/tickets");
        
        RemoteTicketInMemoryRepositoryImpl inmem = new RemoteTicketInMemoryRepositoryImpl();
        inmem.init();
        impl.setSyncTicketRepo(inmem);
        
        impl.setTicketWebService(new IPvimTicketWebService() {
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
        impl.init();
        
        DatabaseReference ref = fdb.getDatabaseReference("/tickets");
        
        FirebaseRemoteDataRepositoryServiceImpl serv = new FirebaseRemoteDataRepositoryServiceImpl();
//        serv.setFirebaseDB(fdb);
//        serv.setRemoteTicketRepository(inmem);
//        serv.setTicketsFirebaseDBPath("/tickets");
//        serv.setMachineStatusFirebaseDBPath("/ATMs");
        
        List<TransferTicketDto> tickets = new ArrayList<>();
        TransferTicketDto tic = new TransferTicketDto("testExecuteListener");
        tic.setLastupdated(System.currentTimeMillis());
        RestTicketDto dto = new RestTicketDto();
        dto.setTicketNumber("SAMPLE_TICKET_0");
        dto.setNote("abcdefg\r\nasdfasdfasdf\r\nasdfasrfrfr\r\n");
        dto.setTicketState("11");
        dto.setMachineNumber("ATM00000001");
        Map<String, Object> tm = TicketUtil.convert(dto);
        tic.setTicketMap(tm);
        tickets.add(tic);
        try {
            serv.sendTickets(tickets);
        } catch (RemoteRepositoryException ex) {
            fail(ex.getMessage());
        }
        
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ex) {
        }
        
        Map<String, Object> um = new HashMap<>();
        um.put("engineer_id", "54543223");
        ref.child(tic.getTicketId()).updateChildren(um, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError de, DatabaseReference dr) {
                if (de != null) {
                    System.out.println("DatabaseError!!!" + de);
                    fail("database error calling firebase");
                } else {
                    synchronized(lock) {
                        lock.notifyAll();
                    }
                }
            }
        });
        
        synchronized(lock) {
            lock.wait();
        }
        
        Thread.sleep(1000);
        
        PvimTicketVo pvimticket = inmem.getTicket(tic.getTicketId());
        assertNotNull(pvimticket);
        assertTrue(pvimticket.isSuccessfullyUpdated());
        assertEquals(pvimticket.getTicketId(), tic.getTicketId());
        
        Logger.getLogger(this.getClass().getName()).log(Level.INFO, " << testExecuteListener()");
    }
    
    volatile boolean engineerIdReset;
    
    @Test
    public void testListenerPvimResetEngineerID() throws InterruptedException, PvExtPersistenceException {
        Logger.getLogger(this.getClass().getName()).log(Level.INFO, " >> testListenerPvimResetEngineerID()");
        
        final Object lock = new Object();
        engineerIdReset = false;
        
        FirebaseTicketDBListenerServiceImpl impl = new FirebaseTicketDBListenerServiceImpl();
        impl.setFirebaseDatabaseReferenceService(fdb);
        impl.setResetEngineerIdCallback(new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError de, DatabaseReference dr) {
                if (de != null) {
                    fail("Error FirebaseTicketDBListenerServiceImpl");
                } else {
                    engineerIdReset = true;
                }
            }
        });
        
        impl.setTicketsFirebaseDBPath("/tickets");
        
        RemoteTicketInMemoryRepositoryImpl inmem = new RemoteTicketInMemoryRepositoryImpl();
        inmem.init();
        impl.setSyncTicketRepo(inmem);
        
        impl.setTicketWebService(new IPvimTicketWebService() {
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
        impl.init();
        
        DatabaseReference ref = fdb.getDatabaseReference("/tickets");
        
        FirebaseRemoteDataRepositoryServiceImpl serv = new FirebaseRemoteDataRepositoryServiceImpl();
//        serv.setFirebaseDB(fdb);
//        serv.setRemoteTicketRepository(inmem);
//        serv.setTicketsFirebaseDBPath("/tickets");
//        serv.setMachineStatusFirebaseDBPath("/ATMs");
        
        List<TransferTicketDto> tickets = new ArrayList<>();
        TransferTicketDto tic = new TransferTicketDto("testListenerPvimResetEngineerID");
        tic.setLastupdated(System.currentTimeMillis());
        RestTicketDto dto = new RestTicketDto();
        dto.setTicketNumber("RESET_ENGINEER_ID_TEST");
        dto.setNote("abcdefg\r\nasdfasdfasdf\r\nasdfasrfrfr\r\n");
        dto.setTicketState("11");
        dto.setMachineNumber("ATM00000002");
        Map<String, Object> tm = TicketUtil.convert(dto);
        tic.setTicketMap(tm);
        tickets.add(tic);
        try {
            serv.sendTickets(tickets);
        } catch (RemoteRepositoryException ex) {
            fail(ex.getMessage());
        }
        
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ex) {
        }
        
        Map<String, Object> um = new HashMap<>();
        um.put("engineer_id", "ABCDEFGABCDEFGABCDEFG");
        ref.child(tic.getTicketId()).updateChildren(um, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError de, DatabaseReference dr) {
                if (de != null) {
                    System.out.println("DatabaseError!!!" + de);
                    fail("database error calling firebase");
                } else {
                    synchronized(lock) {
                        lock.notifyAll();
                    }
                }
            }
        });
        
        synchronized(lock) {
            lock.wait();
        }
        
        Thread.sleep(1000);
        
        PvimTicketVo pvimticket = inmem.getTicket(tic.getTicketId());
        assertNotNull(pvimticket);
        assertTrue(pvimticket.isSuccessfullyUpdated());
        assertEquals(pvimticket.getTicketId(), tic.getTicketId());
        
        Thread.sleep(4000);
        
        assertTrue(engineerIdReset);
        Logger.getLogger(this.getClass().getName()).log(Level.INFO, " << testListenerPvimResetEngineerID()");
    }
    
    @Test
    public void testListenerPvimThrowException() throws PvExtPersistenceException, InterruptedException {
        Logger.getLogger(this.getClass().getName()).log(Level.INFO, " >> testListenerPvimThrowException()");
        
        final Object lock = new Object();
        
        FirebaseTicketDBListenerServiceImpl impl = new FirebaseTicketDBListenerServiceImpl();
        impl.setFirebaseDatabaseReferenceService(fdb);
        
        impl.setTicketsFirebaseDBPath("/tickets");
        
        RemoteTicketInMemoryRepositoryImpl inmem = new RemoteTicketInMemoryRepositoryImpl();
        inmem.init();
        impl.setSyncTicketRepo(inmem);
        
        impl.setTicketWebService(new IPvimTicketWebService() {
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
                Logger.getLogger(this.getClass().getName()).log(Level.INFO, "updateTicket called");
                synchronized(lock) {
                    lock.notifyAll();
                }
                throw new RemoteWsException("Test throw exception, pvim fail");
            }
        });
        
        impl.init();
        
        DatabaseReference ref = fdb.getDatabaseReference("/tickets");
        
        FirebaseRemoteDataRepositoryServiceImpl serv = new FirebaseRemoteDataRepositoryServiceImpl();
//        serv.setFirebaseDB(fdb);
//        serv.setRemoteTicketRepository(inmem);
//        serv.setTicketsFirebaseDBPath("/tickets");
//        serv.setMachineStatusFirebaseDBPath("/ATMs");
        
        List<TransferTicketDto> tickets = new ArrayList<>();
        TransferTicketDto tic = new TransferTicketDto("testListenerPvimThrowException");
        tic.setLastupdated(System.currentTimeMillis());
        RestTicketDto dto = new RestTicketDto();
        dto.setTicketNumber("SAMPLE_TICKET_1");
        dto.setNote("abcdefg\r\nasdfasdfasdf\r\nasdfasrfrfr\r\n");
        dto.setTicketState("15");
        dto.setMachineNumber("ATM00000003");
        Map<String, Object> tm = TicketUtil.convert(dto);
        tic.setTicketMap(tm);
        tickets.add(tic);
        try {
            serv.sendTickets(tickets);
        } catch (RemoteRepositoryException ex) {
            fail(ex.getMessage());
        }
        
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ex) {
        }
        
        Map<String, Object> um = new HashMap<>();
        um.put("engineer_id", "95858858585858");
        ref.child(tic.getTicketId()).updateChildren(um, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError de, DatabaseReference dr) {
                if (de != null) {
                    Logger.getLogger(this.getClass().getName()).log(Level.INFO, "DatabaseError!!!" + de);
                    fail("database error calling firebase");
                }
            }
        });
        
        synchronized(lock) {
            lock.wait();
        }
        
        Thread.sleep(1000);
        
        PvimTicketVo pvimticket = inmem.getTicket(tic.getTicketId());
        Logger.getLogger(this.getClass().getName()).log(Level.INFO, "received: " + pvimticket);
        assertNotNull(pvimticket);
        assertFalse(pvimticket.isSuccessfullyUpdated());
        assertEquals(pvimticket.getTicketId(), tic.getTicketId());
        
        Logger.getLogger(this.getClass().getName()).log(Level.INFO, " << testListenerPvimThrowException()");
    }
    
    
    
    volatile boolean updateTicketCalled = false;
    
    @Test
    public void testListenerPvimNoEngineerID() throws PvExtPersistenceException, InterruptedException {
        Logger.getLogger(this.getClass().getName()).log(Level.INFO, " >> testListenerPvimNoEngineerID()");
        
        final Object lock = new Object();
        
        FirebaseTicketDBListenerServiceImpl impl = new FirebaseTicketDBListenerServiceImpl();
        impl.setFirebaseDatabaseReferenceService(fdb);
        
        impl.setTicketsFirebaseDBPath("/tickets");
        
        RemoteTicketInMemoryRepositoryImpl inmem = new RemoteTicketInMemoryRepositoryImpl();
        inmem.init();
        impl.setSyncTicketRepo(inmem);
        
        impl.setTicketWebService(new IPvimTicketWebService() {
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
                updateTicketCalled = true;
                throw new RemoteWsException("Test throw exception, pvim fail");
            }
        });
        
        impl.init();
        
        DatabaseReference ref = fdb.getDatabaseReference("/tickets");
        
        FirebaseRemoteDataRepositoryServiceImpl serv = new FirebaseRemoteDataRepositoryServiceImpl();
//        serv.setFirebaseDB(fdb);
//        serv.setRemoteTicketRepository(inmem);
//        serv.setTicketsFirebaseDBPath("/tickets");
//        serv.setMachineStatusFirebaseDBPath("/ATMs");
        
        List<TransferTicketDto> tickets = new ArrayList<>();
        TransferTicketDto tic = new TransferTicketDto("testListenerPvimNoEngineerID");
        tic.setLastupdated(System.currentTimeMillis());
        RestTicketDto dto = new RestTicketDto();
        dto.setMachineNumber("ATM00000004");
        dto.setTicketNumber("SAMPLE_TICKET_2");
        dto.setNote("abcdefg\r\nasdfasdfasdf\r\nasdfasrfrfr\r\n");
        dto.setTicketState("15");
        Map<String, Object> tm = TicketUtil.convert(dto);
        tic.setTicketMap(tm);
        tickets.add(tic);
        try {
            serv.sendTickets(tickets);
        } catch (RemoteRepositoryException ex) {
            fail(ex.getMessage());
        }
        
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ex) {
        }
        
        Map<String, Object> um = new HashMap<>();
        um.put("engineer_id", "");
        ref.child(tic.getTicketId()).updateChildren(um, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError de, DatabaseReference dr) {
                if (de != null) {
                    Logger.getLogger(this.getClass().getName()).log(Level.INFO, "DatabaseError!!!" + de);
                    fail("database error calling firebase");
                }
            }
        });
        
        synchronized(lock) {
            lock.wait(5000);
        }
        
        Thread.sleep(1000);
        
        PvimTicketVo pvimticket = inmem.getTicket(tic.getTicketId());
        Logger.getLogger(this.getClass().getName()).log(Level.INFO, "received: " + pvimticket);
        assertNotNull(pvimticket);
        assertTrue(pvimticket.isSuccessfullyUpdated());
        assertFalse(updateTicketCalled);
        assertEquals(pvimticket.getTicketId(), tic.getTicketId());
        
        Logger.getLogger(this.getClass().getName()).log(Level.INFO, " << testListenerPvimNoEngineerID()");
    }
    
    volatile boolean isWaitNotNotified;
    @Test
    public void testNormalUpdateReupdateTicketInFirebase() throws InterruptedException {
        Logger.getLogger(this.getClass().getName()).log(Level.INFO, " >> testNormalUpdateReupdateTicketInFirebase()");
        
        final Object lock = new Object();
        
        FirebaseTicketDBListenerServiceImpl impl = new FirebaseTicketDBListenerServiceImpl();
        impl.setFirebaseDatabaseReferenceService(fdb);
        
        impl.setTicketsFirebaseDBPath("/tickets");
        
        RemoteTicketInMemoryRepositoryImpl inmem = new RemoteTicketInMemoryRepositoryImpl();
        inmem.init();
        impl.setSyncTicketRepo(inmem);
        
        RestTicketDto expectedInFirebase = new RestTicketDto();
        String NEW_UPDATED_NOTE = "NEW UPDATED NOTE";
        String NEW_ETA = System.currentTimeMillis() + "";
        expectedInFirebase.setNote(NEW_UPDATED_NOTE);
        expectedInFirebase.setETA(NEW_ETA);
        
        impl.setTicketWebService(new IPvimTicketWebService() {
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
                return expectedInFirebase;
            }
        });
        
        impl.init();
        
        DatabaseReference ref = fdb.getDatabaseReference("/tickets");
        
        FirebaseRemoteDataRepositoryServiceImpl serv = new FirebaseRemoteDataRepositoryServiceImpl();
//        serv.setFirebaseDB(fdb);
//        serv.setRemoteTicketRepository(inmem);
//        serv.setTicketsFirebaseDBPath("/tickets");
//        serv.setMachineStatusFirebaseDBPath("/ATMs");
        
        List<TransferTicketDto> tickets = new ArrayList<>();
        TransferTicketDto tic = new TransferTicketDto("testNormalUpdateReupdateTicketInFirebase");
        tic.setLastupdated(System.currentTimeMillis());
        RestTicketDto dto = new RestTicketDto();
        dto.setTicketNumber("SAMPLE_TICKET_3");
        dto.setNote("abcdefg\r\nasdfasdfasdf\r\nasdfasrfrfr\r\n");
        dto.setTicketState("15");
        dto.setMachineNumber("ATM00000005");
        dto.setAssignee("monty.python");
        Map<String, Object> tm = TicketUtil.convert(dto);
        tic.setTicketMap(tm);
        tickets.add(tic);
        try {
            serv.sendTickets(tickets);
        } catch (RemoteRepositoryException ex) {
            fail(ex.getMessage());
        }
        
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ex) {
        }
        
        Map<String, Object> um = new HashMap<>();
        um.put("engineer_id", "random engineer_id");
        ref.child(tic.getTicketId()).updateChildren(um, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError de, DatabaseReference dr) {
                if (de != null) {
                    Logger.getLogger(this.getClass().getName()).log(Level.INFO, "DatabaseError!!!" + de);
                    fail("database error calling firebase");
                }
            }
        });
        
        synchronized(lock) {
            lock.wait(5000);
        }
        
        Thread.sleep(1000);
        
        isWaitNotNotified = true;
        ref.child(tic.getTicketId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot ds) {
                Logger.getLogger(this.getClass().getName()).log(Level.INFO, " - AddListenerForSingleValueEvent call1");
                FbTicketDto fbDto = ds.getValue(FbTicketDto.class);
                assertNotNull(fbDto);
                assertNull(fbDto.getErr());
                assertEquals(fbDto.getEngineer_id(), "");
                assertEquals(tic.getLastupdated() < fbDto.getLastupdated(), true);
                RestTicketDto pvimTicketDto = FbTicketDto.getData(fbDto);
                assertEquals(expectedInFirebase, pvimTicketDto);
                synchronized(lock) {
                    isWaitNotNotified = false;
                    lock.notifyAll();
                }
            }

            @Override
            public void onCancelled(DatabaseError de) {
            }
        });
        
        synchronized(lock) {
            lock.wait(10000);
        }
        
        if (isWaitNotNotified) {
            fail("Not enough time until listener answered from firebase. Try checking your network connection!");
        }
        
        Logger.getLogger(this.getClass().getName()).log(Level.INFO, " << testNormalUpdateReupdateInFirebase()");
    }
    
    volatile boolean isWaitNotNotified2;
    @Test
    public void testThrowExceptionErrUpdateInFirebase() throws InterruptedException {
        Logger.getLogger(this.getClass().getName()).log(Level.INFO, " >> testThrowExceptionErrUpdateInFirebase()");
        
        final Object lock = new Object();
        
        FirebaseTicketDBListenerServiceImpl impl = new FirebaseTicketDBListenerServiceImpl();
        impl.setFirebaseDatabaseReferenceService(fdb);
        
        impl.setTicketsFirebaseDBPath("/tickets");
        
        RemoteTicketInMemoryRepositoryImpl inmem = new RemoteTicketInMemoryRepositoryImpl();
        inmem.init();
        impl.setSyncTicketRepo(inmem);
        
        impl.setTicketWebService(new IPvimTicketWebService() {
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
                com.wn.econnect.inbound.wsi.PvimWSException content = new com.wn.econnect.inbound.wsi.PvimWSException();
                ObjectFactory of = new ObjectFactory();
                content.setErrorCode(of.createPvimWSExceptionErrorCode("86"));
                content.setErrorMsg(of.createPvimWSExceptionErrorMsg("Test error message thrown when update"));
                throw new RemoteWsException(new PvimWSException("Test message thrown", content));
            }
        });
        
        impl.init();
        
        DatabaseReference ref = fdb.getDatabaseReference("/tickets");
        
        FirebaseRemoteDataRepositoryServiceImpl serv = new FirebaseRemoteDataRepositoryServiceImpl();
//        serv.setFirebaseDB(fdb);
//        serv.setRemoteTicketRepository(inmem);
//        serv.setTicketsFirebaseDBPath("/tickets");
//        serv.setMachineStatusFirebaseDBPath("/ATMs");
        
        List<TransferTicketDto> tickets = new ArrayList<>();
        TransferTicketDto tic = new TransferTicketDto("testThrowExceptionErrUpdateInFirebase");
        tic.setLastupdated(System.currentTimeMillis());
        RestTicketDto dto = new RestTicketDto();
        dto.setTicketNumber("SAMPLE_TICKET_4");
        dto.setNote("abcdefg\r\nasdfasdfasdf\r\nasdfasrfrfr\r\n");
        dto.setTicketState("15");
        dto.setMachineNumber("ATM00000006");
        dto.setAssignee("monty.python");
        RestTicketDto expectedInFirebase = dto;
        Map<String, Object> tm = TicketUtil.convert(dto);
        tic.setTicketMap(tm);
        tickets.add(tic);
        try {
            serv.sendTickets(tickets);
        } catch (RemoteRepositoryException ex) {
            fail(ex.getMessage());
        }
        
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ex) {
        }
        
        Map<String, Object> um = new HashMap<>();
        um.put("engineer_id", "random engineer_id");
        ref.child(tic.getTicketId()).updateChildren(um, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError de, DatabaseReference dr) {
                if (de != null) {
                    Logger.getLogger(this.getClass().getName()).log(Level.INFO, "DatabaseError!!!" + de);
                    fail("database error calling firebase");
                }
            }
        });
        
        synchronized(lock) {
            lock.wait(5000);
        }
        
        Thread.sleep(1000);
        
        isWaitNotNotified2 = true;
        ref.child(tic.getTicketId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot ds) {
                Logger.getLogger(this.getClass().getName()).log(Level.INFO, " - AddListenerForSingleValueEvent call21");
                FbTicketDto fbDto = ds.getValue(FbTicketDto.class);
                Logger.getLogger(this.getClass().getName()).log(Level.INFO, " - AddListenerForSingleValueEvent call211");
                assertNotNull(fbDto);
                assertNotNull(fbDto.getErr()); // original : assertNull, which is fail, as expected
                                                // because of that, the following lines don't get executed!
                                                // but the failed assert in callback thread does not get reported to main thread!
                assertEquals(fbDto.getEngineer_id(), "");
                assertEquals(tic.getLastupdated() == fbDto.getLastupdated(), true);
                RestTicketDto pvimTicketDto = FbTicketDto.getData(fbDto);
                assertEquals(expectedInFirebase, pvimTicketDto);
                Logger.getLogger(this.getClass().getName()).log(Level.INFO, " - AddListenerForSingleValueEvent call212");
                synchronized(lock) {
                    isWaitNotNotified2 = false;
                    lock.notifyAll();
                }
                Logger.getLogger(this.getClass().getName()).log(Level.INFO, " - AddListenerForSingleValueEvent call213");
            }

            @Override
            public void onCancelled(DatabaseError de) {
            }
        });
        
        synchronized(lock) {
            lock.wait(10000);
        }
        
        if (isWaitNotNotified2) {
            fail("Not enough time until listener answered from firebase. Try checking your network connection! "
                    + "Check also the asserts in callback!"
                    + "If they fail, they will stop the callback thread, silently, without reporting to main thread!!");
        }
        
        DatabaseReference atmref = fdb.getDatabaseReference("/ATMs");
        isWaitNotNotified2 = true;
        atmref.child(dto.getMachineNumber()).child("engineer_id").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot ds) {
                Logger.getLogger(this.getClass().getName()).log(Level.INFO, " - AddListenerForSingleValueEvent call onDataChange checking ATMs/machnum/engineer_id");
                Object o = ds.getValue();
                assertTrue(o instanceof String);
                Logger.getLogger(this.getClass().getName()).log(Level.INFO, " - AddListenerForSingleValueEvent check o instanceof String");
                String j = (String) o;
                assertEquals(dto.getAssignee(), j);
                Logger.getLogger(this.getClass().getName()).log(Level.INFO, " - AddListenerForSingleValueEvent check assignee == o");
                synchronized (lock) {
                    isWaitNotNotified2 = false;
                    lock.notifyAll();
                }
            }

            @Override
            public void onCancelled(DatabaseError de) {
            }
        });
        
        synchronized(lock) {
            lock.wait(10000);
        }
        
        Logger.getLogger(this.getClass().getName()).log(Level.INFO, " << testThrowExceptionErrUpdateInFirebase()");
        if (isWaitNotNotified2) {
            fail("Not enough time until listener answered from firebase. Try checking your network connection! "
                    + "Check also the asserts in callback!"
                    + "If they fail, they will stop the callback thread, silently, without reporting to main thread!!");
        }
    }
    
    @Test
    public void testUpdateErrNextSuccessNoErrInFirebase() throws InterruptedException {
        final Object lock = new Object();
        
        FirebaseTicketDBListenerServiceImpl impl = new FirebaseTicketDBListenerServiceImpl();
        impl.setFirebaseDatabaseReferenceService(fdb);
        
        impl.setTicketsFirebaseDBPath("/tickets");
        
        RemoteTicketInMemoryRepositoryImpl inmem = new RemoteTicketInMemoryRepositoryImpl();
        inmem.init();
        impl.setSyncTicketRepo(inmem);
        
        impl.setTicketWebService(new IPvimTicketWebService() {
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
                com.wn.econnect.inbound.wsi.PvimWSException content = new com.wn.econnect.inbound.wsi.PvimWSException();
                ObjectFactory of = new ObjectFactory();
                content.setErrorCode(of.createPvimWSExceptionErrorCode("96"));
                content.setErrorMsg(of.createPvimWSExceptionErrorMsg("Error message thrown when update"));
                throw new RemoteWsException(new PvimWSException("Test message thrown 2", content));
            }
        });
        
        impl.init();
        
        DatabaseReference ref = fdb.getDatabaseReference("/tickets");
        
        FirebaseRemoteDataRepositoryServiceImpl serv = new FirebaseRemoteDataRepositoryServiceImpl();
//        serv.setFirebaseDB(fdb);
//        serv.setRemoteTicketRepository(inmem);
//        serv.setTicketsFirebaseDBPath("/tickets");
//        serv.setMachineStatusFirebaseDBPath("/ATMs");
        
        List<TransferTicketDto> tickets = new ArrayList<>();
        TransferTicketDto tic = new TransferTicketDto("testUpdateErrNextSuccessNoErrInFirebase");
        tic.setLastupdated(System.currentTimeMillis());
        RestTicketDto dto = new RestTicketDto();
        dto.setTicketNumber("SAMPLE_TICKET_5");
        dto.setNote("abcdefg\r\nasdfasdfasdf\r\nasdfasrfrfr\r\n");
        dto.setTicketState("15");
        dto.setMachineNumber("ATM00000007");
        dto.setAssignee("adam.sandler");
        RestTicketDto expectedInFirebase = dto;
        Map<String, Object> tm = TicketUtil.convert(dto);
        tic.setTicketMap(tm);
        tickets.add(tic);
        try {
            serv.sendTickets(tickets);
        } catch (RemoteRepositoryException ex) {
            fail(ex.getMessage());
        }
        
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ex) {
        }
        
        Map<String, Object> um = new HashMap<>();
        um.put("engineer_id", "random engineer_id");
        ref.child(tic.getTicketId()).updateChildren(um, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError de, DatabaseReference dr) {
                if (de != null) {
                    Logger.getLogger(this.getClass().getName()).log(Level.INFO, "DatabaseError!!!" + de);
                    fail("database error calling firebase");
                }
            }
        });
        
        synchronized(lock) {
            lock.wait(5000);
        }
        
        Thread.sleep(1000);
        
        isWaitNotNotified2 = true;
        ref.child(tic.getTicketId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot ds) {
                Logger.getLogger(this.getClass().getName()).log(Level.INFO, " - AddListenerForSingleValueEvent call21");
                FbTicketDto fbDto = ds.getValue(FbTicketDto.class);
                Logger.getLogger(this.getClass().getName()).log(Level.INFO, " - AddListenerForSingleValueEvent call211");
                assertNotNull(fbDto);
                assertNotNull(fbDto.getErr()); // original : assertNull, which is fail, as expected
                                                // because of that, the following lines don't get executed!
                                                // but the failed assert in callback thread does not get reported to main thread!
                assertEquals(fbDto.getEngineer_id(), "");
                assertEquals(tic.getLastupdated() == fbDto.getLastupdated(), true);
                RestTicketDto pvimTicketDto = FbTicketDto.getData(fbDto);
                assertEquals(expectedInFirebase, pvimTicketDto);
                Logger.getLogger(this.getClass().getName()).log(Level.INFO, " - AddListenerForSingleValueEvent call212");
                synchronized(lock) {
                    isWaitNotNotified2 = false;
                    lock.notifyAll();
                }
                Logger.getLogger(this.getClass().getName()).log(Level.INFO, " - AddListenerForSingleValueEvent call213");
            }

            @Override
            public void onCancelled(DatabaseError de) {
            }
        });
        
        synchronized(lock) {
            lock.wait(10000);
        }
        
        if (isWaitNotNotified2) {
            fail("Not enough time until listener answered from firebase. Try checking your network connection! "
                    + "Check also the asserts in callback!"
                    + "If they fail, they will stop the callback thread, silently, without reporting to main thread!!");
        }
        
        RestTicketDto newTicketFromPvim = new RestTicketDto();
        newTicketFromPvim.setETA(System.currentTimeMillis() + "");
        newTicketFromPvim.setDescription("New Description");
        newTicketFromPvim.setTicketNumber(dto.getTicketNumber());
        impl.setTicketWebService(new IPvimTicketWebService() {
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
                return newTicketFromPvim;
            }
        });
        
        um.put("engineer_id", "random engineer_id 2");
        ref.child(tic.getTicketId()).updateChildren(um, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError de, DatabaseReference dr) {
                if (de != null) {
                    Logger.getLogger(this.getClass().getName()).log(Level.INFO, "DatabaseError!!!" + de);
                    fail("database error calling firebase");
                }
            }
        });
        
        synchronized(lock) {
            lock.wait(5000);
        }
        
        Thread.sleep(1000);
        
        isWaitNotNotified2 = true;
        ref.child(tic.getTicketId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot ds) {
                Logger.getLogger(this.getClass().getName()).log(Level.INFO, " - AddListenerForSingleValueEvent onDataChange begin");
                FbTicketDto fbDto = ds.getValue(FbTicketDto.class);
                assertNotNull(fbDto);
                Logger.getLogger(this.getClass().getName()).log(Level.INFO, " - AddListenerForSingleValueEvent fbDto != null");
                assertNull(fbDto.getErr());
                Logger.getLogger(this.getClass().getName()).log(Level.INFO, " - AddListenerForSingleValueEvent err is null");
                assertEquals(fbDto.getEngineer_id(), "");
                Logger.getLogger(this.getClass().getName()).log(Level.INFO, " - AddListenerForSingleValueEvent engineer_id is empty");
                assertEquals(tic.getLastupdated() < fbDto.getLastupdated(), true);
                Logger.getLogger(this.getClass().getName()).log(Level.INFO, " - AddListenerForSingleValueEvent timestamp updated");
                RestTicketDto pvimTicketDto = FbTicketDto.getData(fbDto);
                assertEquals(newTicketFromPvim, pvimTicketDto);
                Logger.getLogger(this.getClass().getName()).log(Level.INFO, " - AddListenerForSingleValueEvent ticket in firebase == ticket here");
                synchronized(lock) {
                    isWaitNotNotified2 = false;
                    lock.notifyAll();
                }
                Logger.getLogger(this.getClass().getName()).log(Level.INFO, " - AddListenerForSingleValueEvent END");
            }

            @Override
            public void onCancelled(DatabaseError de) {
            }
        });
        
        synchronized(lock) {
            lock.wait(10000);
        }
        
        if (isWaitNotNotified2) {
            fail("Not enough time until listener answered from firebase. Try checking your network connection! "
                    + "Check also the asserts in callback!"
                    + "If they fail, they will stop the callback thread, silently, without reporting to main thread!!");
        }
    }
    
    
}
