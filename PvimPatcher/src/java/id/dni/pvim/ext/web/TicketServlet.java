/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.dni.pvim.ext.web;

import com.google.gson.Gson;
import id.dni.pvim.ext.web.in.Util;
import id.dni.pvim.ext.web.rest.PVIMGetTicketByNumberRequest;
import id.dni.pvim.ext.web.rest.PVIMGetTicketByNumberResponse;
import id.dni.pvim.ext.web.rest.PVIMUpdateTicketRequest;
import id.dni.pvim.ext.web.rest.PVIMUpdateTicketResponse;
import id.dni.pvim.ext.web.rest.TicketOperation;
import java.io.IOException;
import java.io.InputStream;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author darryl.sulistyan
 */
@WebServlet(name = "TicketServlet", urlPatterns = {"/ticket/*"})
public class TicketServlet extends HttpServlet {

    
    
    private static Gson GSON = new Gson();
    
    
    /**
     * Only handles POST messages!
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String requestPath = request.getPathInfo();
        if (requestPath == null || requestPath.equals("/")) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        String[] requestSplits = requestPath.split("/");
        if (requestSplits.length < 2) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        String requestOperation = requestSplits[1];
        if (null == requestOperation) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            
        } else {
            // only do this ONCE!
            InputStream in = request.getInputStream();
            String input = Util.inputStreamToString(in);
            TicketOperation oper = new TicketOperation();
            
            switch (requestOperation) {
                case "getTicketByNumber": {
                    PVIMGetTicketByNumberRequest requestData =
                            GSON.fromJson(input, PVIMGetTicketByNumberRequest.class);
                    PVIMGetTicketByNumberResponse responseTicket = oper.findTicket(requestData);
                    Util.sendAsJson(response, responseTicket);

                } break;
                case "updateTicket": {
                    PVIMUpdateTicketRequest requestData = 
                            GSON.fromJson(input, PVIMUpdateTicketRequest.class);
                    PVIMUpdateTicketResponse responseTicket = oper.updateTicket(requestData);
                    Util.sendAsJson(response, responseTicket);

                } break;
                case "rejectWorking": {
                    PVIMUpdateTicketRequest requestData = 
                            GSON.fromJson(input, PVIMUpdateTicketRequest.class);
                    PVIMUpdateTicketResponse responseTicket = oper.updateTicketReject(requestData);
                    Util.sendAsJson(response, responseTicket);
                    
                } break;
                case "startWorking" : {
                    PVIMUpdateTicketRequest requestData = 
                            GSON.fromJson(input, PVIMUpdateTicketRequest.class);
                    PVIMUpdateTicketResponse responseTicket = oper.updateTicketStartWorking(requestData);
                    Util.sendAsJson(response, responseTicket);
                    
                } break;
                case "fixWorking" : {
                    PVIMUpdateTicketRequest requestData = 
                            GSON.fromJson(input, PVIMUpdateTicketRequest.class);
                    PVIMUpdateTicketResponse responseTicket = oper.updateTicketFixed(requestData);
                    Util.sendAsJson(response, responseTicket);
                    
                } break;
                case "giveupWorking" : {
                    PVIMUpdateTicketRequest requestData = 
                            GSON.fromJson(input, PVIMUpdateTicketRequest.class);
                    PVIMUpdateTicketResponse responseTicket = oper.updateTicketGaveUp(requestData);
                    Util.sendAsJson(response, responseTicket);
                    
                } break;
                default: {
                    response.sendError(HttpServletResponse.SC_NOT_FOUND);
                } break;
            }
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        //processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

//    private TicketDto getTicketByNumber(java.lang.String in0) throws PvimWSException {
//        // Note that the injected javax.xml.ws.Service reference as well as port objects are not thread safe.
//        // If the calling of port operations may lead to race condition some synchronization is required.
//        com.wn.econnect.inbound.wsi.ticket.ITicketWebServicePortType port = service.getITicketWebServiceHttpPort();
//        Map<String, Object> config = new HashMap<>();
//        config.put(BindingProviderUtil.WS_URL, "http://localhost:8080/tasman_ws/services/TicketService");
//        config.put(BindingProviderUtil.WS_CONNECT_TIMEOUT, 10000);
//        config.put(BindingProviderUtil.WS_REQUEST_TIMEOUT, 10000);
//        BindingProviderUtil.configureBindingProvider((BindingProvider)port, config);
//        
//        List<Handler> handlerChain = ((BindingProvider)port).getBinding().getHandlerChain();
//        handlerChain.add(new PVIMSoapHeaderHandler("admin11", "password1"));
//        ((BindingProvider)port).getBinding().setHandlerChain(handlerChain);
//        
//        return port.getTicketByNumber(in0);
//    }

}
