/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.dni.pvim.ext.sim.telegram;

import com.google.gson.Gson;
import id.dni.pvim.ext.telegram.pojo.TelegramGetUpdatesPOJO;
import id.dni.pvim.ext.telegram.pojo.TelegramMessageChatPOJO;
import id.dni.pvim.ext.telegram.pojo.TelegramMessageContentPOJO;
import id.dni.pvim.ext.telegram.pojo.TelegramMessageFromPOJO;
import id.dni.pvim.ext.telegram.pojo.TelegramUpdateObjPOJO;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author darryl.sulistyan
 */
@WebServlet(name = "GetUpdatesServlet", urlPatterns = {"/getUpdates/*"})
public class GetUpdatesServlet extends HttpServlet {

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
        response.setContentType("application/json;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            
            long offset;
            try {
                offset = Long.parseLong(request.getParameter("offset"));
            } catch (Exception ex) {
                offset = 0;
            }
            
            List<Chat> chats = ChatRepository.getInstance().getChats();
            List<Chat> newChat = new ArrayList<>();
            
            TelegramGetUpdatesPOJO getUpdates = new TelegramGetUpdatesPOJO();
            getUpdates.setOk(true);
            getUpdates.setResult(new ArrayList<>());
            
            for (Chat chat : chats) {
                long updateID = chat.getUpdateID();
                if (updateID >= offset) {
                    TelegramUpdateObjPOJO m = new TelegramUpdateObjPOJO();
                    m.setUpdate_id(chat.getUpdateID());

                    TelegramMessageContentPOJO c = new TelegramMessageContentPOJO();
                    c.setMessage_id(60);
                    c.setDate(chat.getDate());
                    c.setText(chat.getText());

                    TelegramMessageChatPOJO pc = new TelegramMessageChatPOJO();
                    pc.setFirst_name("whatever");
                    pc.setLast_name("whatever 2");
                    pc.setType("private");
                    pc.setId(chat.getChatID());

                    TelegramMessageFromPOJO mf = new TelegramMessageFromPOJO();
                    mf.setFirst_name("whatever");
                    mf.setLast_name("whatever 2");
                    mf.setId(chat.getChatID());
                    mf.setIs_bot(false);
                    mf.setLanguage_code("en-us");

                    c.setChat(pc);
                    c.setFrom(mf);

                    m.setMessage(c);

                    getUpdates.getResult().add(m);
                    newChat.add(chat);
                }
            }
            
            ChatRepository.getInstance().getChats().clear();
            ChatRepository.getInstance().getChats().addAll(newChat);
            
            Gson gson = new Gson();
            String json = gson.toJson(getUpdates);
            out.write(json);
            out.flush();
            
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
