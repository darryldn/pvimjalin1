/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.dni.pv.ext.ws.dispatcher;

import com.google.gson.Gson;
import id.dni.pv.ext.exception.PvWsException;
import id.dni.pvim.ext.dto.PvWsDefaultRequest;
import id.dni.pvim.ext.dto.PvWsDefaultResponse;
import id.dni.pvim.ext.web.in.Commons;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author darryl.sulistyan
 */
public class PvWsWithPostProcessorImpl implements IPvWs {
    
    public static interface PvWsPostProcessor {
        public PvWsDefaultResponse handle(PvWsDefaultResponse obj);
    }
    
    private final PvWsPostProcessor postProcessor;
    
    public PvWsWithPostProcessorImpl(PvWsPostProcessor postProcessor) {
        this.postProcessor = postProcessor;
    }
    
    public PvWsWithPostProcessorImpl() {
        this.postProcessor = null;
    }
    
    @Override
    public void handleRequest(String operationName, HttpServletRequest request, 
            HttpServletResponse response) throws IOException {
        
//        String requestPath = request.getPathInfo();
//        String[] requestSplits = requestPath.split("/");
//        String requestOperation = requestSplits[1];
        
        response.setContentType("application/json;charset=UTF-8");

        InputStream in = request.getInputStream();
        String input = Commons.inputStreamToString(in);
        Gson gson = new Gson();
        
        PvWsDefaultRequest requestObj = gson.fromJson(input, PvWsDefaultRequest.class);
        PvWsProcessorInternal processor = new PvWsProcessorInternal();
        PvWsDefaultResponse responseWs;
        try {
            responseWs = processor.process(operationName, requestObj);
            if (this.postProcessor != null) {
                responseWs = this.postProcessor.handle(responseWs);
            }
            try (PrintWriter out = response.getWriter()) {
                Commons.sendAsJson(out, responseWs);
                out.flush();
            }
            return;
        } catch (PvWsException ex) {
            Logger.getLogger(DefaultPvWsImpl.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
        }
        response.sendError(HttpServletResponse.SC_BAD_REQUEST);
    }
    
}
