/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package springstuff.controller;

import com.google.gson.Gson;
import com.wn.econnect.inbound.wsi.ticket.PvimWSException;
import id.dni.ext.web.Util;
import id.dni.ext.web.ws.obj.RestTicketDto;
import id.dni.ext.web.ws.obj.SendTicketRemoteResponse;
import id.dni.pvim.ext.err.PVIMErrorCodes;
import id.dni.pvim.ext.net.RemoteMessagingResult;
import id.dni.pvim.ext.net.SendTicketRemoteResponseJson;
import id.dni.pvim.ext.net.TransferTicketDto;
import id.dni.pvim.ext.web.in.OperationError;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import springstuff.exceptions.RemoteRepositoryException;
import springstuff.exceptions.RemoteWsException;
import springstuff.json.PVIMUpdateTicketRequest;
import springstuff.service.IPvimTicketWebService;
import springstuff.service.RemoteDataRepositoryService;

/**
 *
 * @author darryl.sulistyan
 */
@Controller
public class TicketController {
    
    private RemoteDataRepositoryService remoteDataRepositoryService;
    
    @Autowired
//    @Qualifier("simpleurlRemoteDataRepositoryService")
    @Qualifier("firebaseRemoteDataRepositoryService")
    public void setRemoteDataRepositoryService(RemoteDataRepositoryService service) {
        this.remoteDataRepositoryService = service;
    }
    
    private IPvimTicketWebService ws;
    
    @Autowired
    public void setPvimWS(IPvimTicketWebService ws) {
        this.ws = ws;
    }
    
    @RequestMapping(value = "/ticket/update", method = RequestMethod.POST, 
            produces = MediaType.APPLICATION_JSON_VALUE, 
            consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<String> sendTicketRemote(@RequestBody String ticketJson) {
        Logger.getLogger(this.getClass().getName()).log(Level.INFO, ">> sendTicketRemote");
        
        Gson gson = new Gson();
        
        TransferTicketDto transferDto = gson.fromJson(ticketJson, TransferTicketDto.class);
        Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Obtain ticket: {0}", transferDto);
        
        List<TransferTicketDto> tickets = new ArrayList<>();
        tickets.add(transferDto);
        
//        SendTicketRemoteResponse resp = new SendTicketRemoteResponse();
        SendTicketRemoteResponseJson respNew = new SendTicketRemoteResponseJson();
        
        try {
            List<RemoteMessagingResult> sendTickets = this.remoteDataRepositoryService.sendTickets(tickets);
            respNew.setResult(sendTickets);
            
//            resp.setTicket(RestTicketDto.convert(transferDto.getTicketMap()));
        } catch (RemoteRepositoryException ex) {
            Logger.getLogger(TicketController.class.getName()).log(Level.SEVERE, null, ex);
            OperationError err = new OperationError();
            err.setErrCode("" + PVIMErrorCodes.E_UNKNOWN_ERROR);
            err.setErrMsg("Unable to send request to Remote server");
//            resp.setErr(err);
            respNew.setErr(err);
        }
        
        String jsonret = null;
        try {
            jsonret = gson.toJson(respNew);
            return Util.returnJsonStr(jsonret);
        } finally {
            Logger.getLogger(this.getClass().getName()).log(Level.INFO, "<< sendTicketRemote {0}", jsonret);
        }
    }
    
    @RequestMapping(value = "/ticket/remove", method = RequestMethod.POST, 
            produces = MediaType.APPLICATION_JSON_VALUE, 
            consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<String> removeTicketRemote(@RequestBody String ticketJson) {
        Logger.getLogger(this.getClass().getName()).log(Level.INFO, ">> removeTicketRemote");
        
        Gson gson = new Gson();
        
        TransferTicketDto transferDto = gson.fromJson(ticketJson, TransferTicketDto.class);
        Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Obtain ticket: {0}", transferDto);
        
        List<TransferTicketDto> tickets = new ArrayList<>();
        tickets.add(transferDto);
        
        SendTicketRemoteResponse resp = new SendTicketRemoteResponse();
        try {
            this.remoteDataRepositoryService.removeTickets(tickets);
            resp.setTicket(RestTicketDto.convert(transferDto.getTicketMap()));
        } catch (RemoteRepositoryException ex) {
            Logger.getLogger(TicketController.class.getName()).log(Level.SEVERE, null, ex);
            OperationError err = new OperationError();
            err.setErrCode("-20000");
            err.setErrMsg("Unable to send request to Remote server");
            resp.setErr(err);
        }
        
        String jsonret = null;
        try {
            jsonret = gson.toJson(resp);
            return Util.returnJsonStr(jsonret);
        } finally {
            Logger.getLogger(this.getClass().getName()).log(Level.INFO, "<< removeTicketRemote {0}", jsonret);
        }
    }
    
    @RequestMapping(value = "/ticket/pvim/update", method = RequestMethod.POST, 
            produces = MediaType.APPLICATION_JSON_VALUE, 
            consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<String> pvimUpdateTicket(@RequestBody String ticketJson) {
        Gson gson = new Gson();
        PVIMUpdateTicketRequest req = gson.fromJson(ticketJson, PVIMUpdateTicketRequest.class);
        SendTicketRemoteResponse resp = new SendTicketRemoteResponse();
        try {
            RestTicketDto ers = this.ws.updateTicket(req.getTicket(), req.getAuth());
            resp.setTicket(ers);
        } catch (RemoteWsException ex) {
            Logger.getLogger(TicketController.class.getName()).log(Level.SEVERE, null, ex);
            OperationError err = new OperationError();
            err.setErrCode(((PvimWSException) ex.getCause()).getFaultInfo().getErrorCode().getValue());
            err.setErrMsg(((PvimWSException) ex.getCause()).getFaultInfo().getErrorMsg().getValue());
            resp.setErr(err);
        }
        return Util.returnJson(resp);
//        return gson.toJson(resp);
    }
    
}
