/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package springstuff.service.impl;

import com.wn.econnect.inbound.wsi.ticket.ArrayOfTicketDto;
import com.wn.econnect.inbound.wsi.ticket.ITicketWebService;
import com.wn.econnect.inbound.wsi.ticket.ITicketWebServicePortType;
import com.wn.econnect.inbound.wsi.ticket.PvimWSException;
import com.wn.econnect.inbound.wsi.ticket.TicketDto;
import id.dni.ext.web.ws.BindingProviderUtil;
import id.dni.ext.web.ws.PVIMSoapHeaderHandler;
import id.dni.ext.web.ws.ServiceRegistry;
import id.dni.ext.web.ws.obj.RestTicketDto;
import id.dni.pvim.ext.web.in.PVIMAuthToken;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.handler.Handler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import springstuff.exceptions.RemoteWsException;
import springstuff.service.IPvimTicketWebService;

/**
 *
 * @author darryl.sulistyan
 */
@Service
public class PvimTicketWebServiceImpl implements IPvimTicketWebService {
    
    private String wsurl;
    private int wsTimeout;
    private String wsDefaultUsername;
    private String wsDefaultPassword;
    
    @Value("${TicketService.url}")
    public void setUrl(String path) {
        this.wsurl = path;
    }
    
    @Value("${TicketService.timeout}")
    public void setTimeout(String path) {
        this.wsTimeout = Integer.parseInt(path);
    }
    
    @Value("${TicketService.defaultusername}")
    public void setDefaultUsername(String username) {
        this.wsDefaultUsername = username;
    }
    
    @Value("${TicketService.defaultpassword}")
    public void setDefaultPassword(String pass) {
        this.wsDefaultPassword = pass;
    }
    
    private static ITicketWebService getWS() {
        return (ITicketWebService) ServiceRegistry.getInstance().getService(ITicketWebService.class);
    }
    
    private PVIMAuthToken createToken(PVIMAuthToken auth) {
        if (auth != null) {
            return auth;
        }
        PVIMAuthToken token = new PVIMAuthToken();
        token.setPassword(this.wsDefaultPassword);
        token.setUsername(this.wsDefaultUsername);
        return token;
    }
    
    @Override
    public RestTicketDto createTicket(RestTicketDto ticket, PVIMAuthToken auth) throws RemoteWsException {
        try {
            TicketDto ticketWs = this.getWebServicePort(createToken(auth)).createTicket(ticket.convert());
            return ticketWs != null ? new RestTicketDto(ticketWs) : null;
        } catch (PvimWSException ex) {
            throw new RemoteWsException(ex);
        }
    }

    @Override
    public List<RestTicketDto> getOpenTickets(String machineNo, PVIMAuthToken auth) throws RemoteWsException {
        try {
            ArrayOfTicketDto ticketDtoList = this.getWebServicePort(createToken(auth)).getOpenTickets(machineNo);
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
            return rstc;
        } catch (PvimWSException ex) {
            throw new RemoteWsException(ex);
        }
    }

    @Override
    public RestTicketDto getTicketByNumber(String ticketNumber, PVIMAuthToken auth) throws RemoteWsException {
        try {
            TicketDto ticket = this.getWebServicePort(createToken(auth)).getTicketByNumber(ticketNumber);
            if (ticket != null) {
                return new RestTicketDto(ticket);
            } else {
                return null;
            }
        } catch (PvimWSException ex) {
            throw new RemoteWsException(ex);
        }
    }

    @Override
    public RestTicketDto updateTicket(RestTicketDto ticket, PVIMAuthToken auth) throws RemoteWsException {
        try {
            TicketDto ticketWs = this.getWebServicePort(createToken(auth)).updateTicket(ticket.convert());
            return ticketWs != null ? new RestTicketDto(ticketWs) : null;
        } catch (PvimWSException ex) {
            throw new RemoteWsException(ex);
        }
    }
    
    private void modifyPortService(BindingProvider port, PVIMAuthToken auth) {
        Map<String, Object> config = new HashMap<>();
        
        config.put(BindingProviderUtil.WS_URL, this.wsurl);
        config.put(BindingProviderUtil.WS_CONNECT_TIMEOUT, this.wsTimeout);
        config.put(BindingProviderUtil.WS_REQUEST_TIMEOUT, this.wsTimeout);
        BindingProviderUtil.configureBindingProvider(port, config);
        
        List<Handler> handlerChain = (port).getBinding().getHandlerChain();
        handlerChain.add(new PVIMSoapHeaderHandler(auth.getUsername(), auth.getPassword()));
        ((BindingProvider)port).getBinding().setHandlerChain(handlerChain);
    }
    
    private ITicketWebServicePortType getWebServicePort(PVIMAuthToken auth) {
        ITicketWebService service = getWS();
        ITicketWebServicePortType port = service.getITicketWebServiceHttpPort();
        modifyPortService((BindingProvider)port, auth);
        return port;
    }
    
}
