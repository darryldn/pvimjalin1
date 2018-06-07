/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.dni.pvim.ext.web;

import com.google.gson.Gson;
import id.dni.pvim.ext.web.in.Commons;
import id.dni.pvim.ext.web.in.Util;
import id.dni.pvim.ext.web.rest.PVLoginRequest;
import id.dni.pvim.ext.web.rest.PVLoginResponse;
import id.dni.pvim.ext.web.rest.UserOperation;
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
@WebServlet(name = "UserServlet", urlPatterns = {"/user/*"})
public class UserServlet extends HttpServlet {

    private static Gson GSON = new Gson();
    
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
        
        String[] requestSplits = Commons.getRequestPath(request.getPathInfo(), 2);
        if (requestSplits == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
//        
//        String requestPath = request.getPathInfo();
//        if (requestPath == null || requestPath.equals("/")) {
//            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
//            return;
//        }
//
//        String[] requestSplits = requestPath.split("/");
//        if (requestSplits.length < 2) {
//            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
//            return;
//        }

        String requestOperation = requestSplits[1];
        if ("login".equals(requestOperation)) {
            // only do this ONCE!
            InputStream in = request.getInputStream();
            String input = Util.inputStreamToString(in);
            UserOperation oper = new UserOperation();
            PVLoginRequest requestData = GSON.fromJson(input, PVLoginRequest.class);
            PVLoginResponse responseData = oper.login(requestData);
            Util.sendAsJson(response, responseData);
            
        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
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
        processRequest(request, response);
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

}
