/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.dni.pvim.ext.web;

import com.wn.econnect.inbound.wsi.ticket.ITicketWebService;
import id.dni.pvim.ext.conf.PatcherConfig;
import id.dni.pvim.ext.web.soap.PVIMWSServiceRegistry;
import javax.servlet.http.HttpServlet;
import javax.xml.ws.WebServiceRef;

/**
 *
 * @author darryl.sulistyan
 */
public class InitServlet extends HttpServlet {

    @WebServiceRef(wsdlLocation = "WEB-INF/wsdl/TicketService.wsdl")
    private ITicketWebService service;
    
    @Override
    public void init() {
        PVIMWSServiceRegistry.getInstance().putService(service);
        PatcherConfig.getInstance().init();
    }
    
}
