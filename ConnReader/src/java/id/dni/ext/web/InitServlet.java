/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.dni.ext.web;

import com.wn.econnect.inbound.wsi.ticket.ITicketWebService;
import id.dni.ext.web.ws.ServiceRegistry;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.xml.ws.WebServiceRef;

/**
 *
 * @author darryl.sulistyan
 */
@WebServlet(name = "InitServlet", loadOnStartup = 1)
public class InitServlet extends HttpServlet {

    @WebServiceRef(wsdlLocation = "WEB-INF/wsdl/TicketService.wsdl")
    private ITicketWebService service;

    @Override
    public void init() {
        ServiceRegistry.getInstance().setService(ITicketWebService.class, service);
    }

}
