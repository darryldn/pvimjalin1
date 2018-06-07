/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.dni.pvim.ext.web.rest;

import com.wn.econnect.inbound.wsi.ticket.ArrayOfTicketDto;
import id.dni.pvim.ext.web.in.OperationError;
import id.dni.pvim.ext.web.in.PVIMAuthToken;
import com.wn.econnect.inbound.wsi.ticket.ITicketWebService;
import com.wn.econnect.inbound.wsi.ticket.ITicketWebServicePortType;
import com.wn.econnect.inbound.wsi.ticket.ObjectFactory;
import com.wn.econnect.inbound.wsi.ticket.PvimWSException;
import com.wn.econnect.inbound.wsi.ticket.TicketDto;
import id.dni.pvim.ext.conf.PatcherConfig;
import id.dni.pvim.ext.db.dao.DBTicketNotesDao;
import id.dni.pvim.ext.db.dao.ITicketNotesDao;
import id.dni.pvim.ext.db.dao.DaoFactory;
import id.dni.pvim.ext.db.exception.PvExtPersistenceException;
import id.dni.pvim.ext.err.PVIMErrorCodes;
import id.dni.pvim.ext.web.in.Commons;
import id.dni.pvim.ext.web.in.Util;
import id.dni.pvim.ext.web.soap.BindingProviderUtil;
import id.dni.pvim.ext.web.soap.PVIMSoapHeaderHandler;
import id.dni.pvim.ext.web.soap.PVIMTicketState;
import id.dni.pvim.ext.web.soap.PVIMWSServiceRegistry;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXBElement;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.handler.Handler;

/**
 *
 * @author darryl.sulistyan
 */
public class TicketOperation {
    
    private OperationError readException(PvimWSException ex) {
        OperationError err = new OperationError();
        String errCode = ex.getFaultInfo().getErrorCode().getValue();
        String errMsg = ex.getFaultInfo().getErrorMsg().getValue();
        err.setErrCode(errCode);
        err.setErrMsg(errMsg);
        return err;
    }
    
    private PVIMUpdateTicketResponse errMandatoryNotes() {
        PVIMUpdateTicketResponse resp = new PVIMUpdateTicketResponse();
        OperationError err = new OperationError();
        err.setErrCode("" + PVIMErrorCodes.E_INPUT_NO_NOTES);
        err.setErrMsg("Note is mandatory input!");
        resp.setErr(err);
        return resp;
    }
    
    public TicketOperation() {
        
    }
    
    private PVIMUpdateTicketResponse updateTicketWithStateAndNoteMandatory(
            PVIMUpdateTicketRequest request, String pvimState) {
        
        RestTicketDto restTicketDto = request.getTicket();
        restTicketDto.setTicketState(pvimState);
        
        // Note is required!
        if (Util.isEmptyStrIgnoreSpaces(restTicketDto.getNote())) {
            return errMandatoryNotes();
        }
        
        request.setTicket(restTicketDto);
        return updateTicket(request);
        
    }
    
    private PVIMUpdateTicketResponse updateTicketWithState(
            PVIMUpdateTicketRequest request, String pvimState) {
        RestTicketDto restTicketDto = request.getTicket();
        restTicketDto.setTicketState(pvimState);
        request.setTicket(restTicketDto);
        return updateTicket(request);
    }
    
    public PVIMUpdateTicketResponse updateTicketStartWorking(PVIMUpdateTicketRequest request) {
        return updateTicketWithState(request, PVIMTicketState.WORKING);
    }
    
    public PVIMUpdateTicketResponse updateTicketSuspend(PVIMUpdateTicketRequest request) {
        return updateTicketWithStateAndNoteMandatory(request, PVIMTicketState.SUSPENDED);
    }
    
    public PVIMUpdateTicketResponse updateTicketReject(PVIMUpdateTicketRequest request) {
        return updateTicketSuspend(request);
    }
    
    public PVIMUpdateTicketResponse updateTicketGaveUp(PVIMUpdateTicketRequest request) {
        return updateTicketSuspend(request);
    }
    
    public PVIMUpdateTicketResponse updateTicketFixed(PVIMUpdateTicketRequest request) {
        return updateTicketWithStateAndNoteMandatory(request, PVIMTicketState.FIXED);
    }
    
    public PVIMUpdateTicketResponse updateTicket(PVIMUpdateTicketRequest request) {
        
        PVIMAuthToken auth = request.getAuth();
        RestTicketDto restTicketDto = request.getTicket();
        PVIMUpdateTicketResponse resp = new PVIMUpdateTicketResponse();
        
        try {
            // modify restTicketDto to support immediate time and GMT stuff...
            // If the time is not set, as in, null, not exist in xml, not just empty,
            // PVIM will calculate them according to server time.
            
            TicketDto ticketSoap = restTicketDto.convert();
            TicketDto returned = pvimUpdateTicket(ticketSoap, auth);
            resp.setTicket(returned);
            
        } catch (PvimWSException ex) {
            Logger.getLogger(TicketOperation.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
            resp.setErr(readException(ex));
            
        }
        
        return resp;
    }
    
    public PVIMGetTicketByNumberResponse findTicket(PVIMGetTicketByNumberRequest request) {
        
        String ticketNumber = request.getTicketNumber();
        PVIMAuthToken auth = request.getAuth();
        PVIMGetTicketByNumberResponse resp = new PVIMGetTicketByNumberResponse();
        
        Logger.getLogger(TicketOperation.class.getName()).log(Level.INFO, "ticketNumber: {0} auth: {1}", 
                new Object[]{ticketNumber, auth});
        
        try {
            TicketDto ticketDto = pvimGetTicketByNumber(ticketNumber, auth);
            if (ticketDto != null) {
                JAXBElement<String> jaxbNotes = ticketDto.getNote();
                if (jaxbNotes == null || Commons.isEmptyStrIgnoreSpaces(jaxbNotes.getValue())) {
                    Logger.getLogger(TicketOperation.class.getName()).log(Level.INFO, "No notes given. Read from database!");
                    
                    // Obtain ticket notes and append it in ticketDto!
                    ITicketNotesDao ticketNotesDao = DaoFactory.getInstance().getTicketNotesDao();
                    List<DBTicketNotesDao> ticketNotes = ticketNotesDao.getTicketNotesWithParent(ticketNumber);
                    StringBuilder sb = new StringBuilder();
                    for (DBTicketNotesDao ticketNote : ticketNotes) {
                        sb.append(ticketNote.getNotes()).append("\n");
                    }
                    Logger.getLogger(TicketOperation.class.getName()).log(Level.INFO, "Obtain notes: [{0}]", sb);
                    
                    ObjectFactory objFactory = new ObjectFactory();
                    JAXBElement<String> xnote = objFactory.createTicketDtoNote(sb.toString()); 
                    ticketDto.setNote(xnote);
                }
            }
            
            resp.setTicket(ticketDto);
            
        } catch (PvimWSException ex) {
            Logger.getLogger(TicketOperation.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
            resp.setErr(readException(ex));
            
        } catch (PvExtPersistenceException ex) {
            Logger.getLogger(TicketOperation.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
            OperationError err = new OperationError();
            err.setErrCode("" + PVIMErrorCodes.E_DATABASE_ERROR);
            err.setErrMsg("Database error, please contact administrator");
            resp.setErr(err);
            
        }
        
        return resp;
    }
    
    public PVIMGetOpenTicketsByMachineNumberResponse getOpenTickets(PVIMGetOpenTicketsByMachineNumberRequest request) {
        String machineNumber = request.getMachineNumber();
        PVIMAuthToken auth = request.getAuth();
        PVIMGetOpenTicketsByMachineNumberResponse resp = new PVIMGetOpenTicketsByMachineNumberResponse();
        
        try {
            ArrayOfTicketDto ticketDtoList = pvimGetOpenTickets(machineNumber, auth);
            List<TicketDto> tc;
            if (ticketDtoList == null) {
                tc = Collections.EMPTY_LIST;
            } else {
                tc = ticketDtoList.getTicketDto();
            }
            List<RestTicketDto> rstc = new ArrayList<>();
            for (TicketDto t : tc) {
                rstc.add(new RestTicketDto(t));
            }
            resp.setTickets(rstc);
            
        } catch (PvimWSException ex) {
            Logger.getLogger(TicketOperation.class.getName()).log(Level.SEVERE, null, ex);
            resp.setErr(readException(ex));
            
        }
        
        return resp;
    }
    
    private void modifyPortService(BindingProvider port, PVIMAuthToken auth) {
        Map<String, Object> config = new HashMap<>();
        
        config.put(BindingProviderUtil.WS_URL, PatcherConfig.getInstance().get("TicketService.url"));
        config.put(BindingProviderUtil.WS_CONNECT_TIMEOUT, PatcherConfig.getInstance().get("TicketService.connect_timeout", "3000"));
        config.put(BindingProviderUtil.WS_REQUEST_TIMEOUT, PatcherConfig.getInstance().get("TicketService.request_timeout", "3000"));
        BindingProviderUtil.configureBindingProvider(port, config);
        
        List<Handler> handlerChain = (port).getBinding().getHandlerChain();
        handlerChain.add(new PVIMSoapHeaderHandler(auth.getUsername(), auth.getPassword()));
        ((BindingProvider)port).getBinding().setHandlerChain(handlerChain);
    }
    
    private ITicketWebServicePortType getWebServicePort(PVIMAuthToken auth) {
        ITicketWebService service = PVIMWSServiceRegistry.getInstance().getTicketWebService();
        ITicketWebServicePortType port = service.getITicketWebServiceHttpPort();
        modifyPortService((BindingProvider)port, auth);
        return port;
    }
    
    private TicketDto pvimUpdateTicket(TicketDto ticket, PVIMAuthToken auth) throws PvimWSException {
        return getWebServicePort(auth).updateTicket(ticket);
//        ITicketWebService service = PVIMWSServiceRegistry.getInstance().getTicketWebService();
//        ITicketWebServicePortType port = service.getITicketWebServiceHttpPort();
//        modifyPortService((BindingProvider)port, auth);
//        return port.updateTicket(ticket);
    }

    private TicketDto pvimGetTicketByNumber(String ticketNumber, PVIMAuthToken auth) throws PvimWSException {
        return getWebServicePort(auth).getTicketByNumber(ticketNumber);
//        ITicketWebService service = PVIMWSServiceRegistry.getInstance().getTicketWebService();
//        ITicketWebServicePortType port = service.getITicketWebServiceHttpPort();
//        modifyPortService((BindingProvider)port, auth);
//        return port.getTicketByNumber(ticketNumber);
    }
    
    private ArrayOfTicketDto pvimGetOpenTickets(String machineNumber, PVIMAuthToken auth) throws PvimWSException {
        return getWebServicePort(auth).getOpenTickets(machineNumber);
    }
    
}
