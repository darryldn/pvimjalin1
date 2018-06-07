/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.dni.pv.ext.web.servlet;

import id.dni.pv.ext.ws.dispatcher.IPvWs;
import id.dni.pv.ext.ws.dispatcher.PvWsFactory;
import id.dni.pvim.ext.web.in.Commons;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author darryl.sulistyan
 */
@WebServlet(name = "GatewayServlet", urlPatterns = {"/gateway/*"})
public class GatewayServlet extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String[] requestSplits = Commons.getRequestPath(request.getPathInfo(), 3);
        if (requestSplits == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        
//        String requestPath = request.getPathInfo();
//        if (requestPath == null || requestPath.equals("/")) {
//            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
//            return;
//        }
//
//        String[] requestSplits = requestPath.split("/");
//        if (requestSplits.length < 3) {
//            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
//            return;
//        }

        String requestService = requestSplits[1];
        String requestOperation = requestSplits[2];
        
        if (Commons.isEmptyStrIgnoreSpaces(requestService) || 
                Commons.isEmptyStrIgnoreSpaces(requestOperation)) {
            
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            
        } else {
            
            String clRequestService = requestService.trim();
            String clRequestOperation = requestOperation.trim();
            String operationName = String.format("%s/%s", clRequestService, clRequestOperation);
            
            IPvWs wsHandler = PvWsFactory.getInstance().getHandler(operationName);
            if (wsHandler != null) {
                wsHandler.handleRequest(operationName, request, response);
            }
            
//            InputStream in = request.getInputStream();
//            String input = Commons.inputStreamToString(in);
            
        }
        
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
    }

}
