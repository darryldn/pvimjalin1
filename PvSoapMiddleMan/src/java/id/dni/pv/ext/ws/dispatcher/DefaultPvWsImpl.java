/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.dni.pv.ext.ws.dispatcher;

import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author darryl.sulistyan
 */
public class DefaultPvWsImpl implements IPvWs {
    
    public DefaultPvWsImpl() {
        
    }
    
    @Override
    public void handleRequest(String operationName, HttpServletRequest request, 
            HttpServletResponse response) throws IOException {
        
        new PvWsWithPostProcessorImpl().handleRequest(operationName, request, response);
    }
    
}
