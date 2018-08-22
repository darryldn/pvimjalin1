/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.dni.pvim.ext.service;

import id.dni.pvim.ext.service.json.ProviewLoginRequest;
import id.dni.pvim.ext.service.json.ProviewLoginResponse;
import id.dni.pvim.ext.web.in.Commons;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author darryl.sulistyan
 */
public class PVIMLoginService {
    
    public ProviewLoginResponse login(ProviewLoginRequest request) {
        
        ProviewLoginResponse response = new ProviewLoginResponse();
        
        try {
        
            String soapXml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
                    "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:tic=\"http://ticket.wsi.inbound.econnect.wn.com\">\n" +
                    "   <soapenv:Header>\n" +
                    "<wn:AuthenticationToken xmlns:wn=\"http://wsi.inbound.econnect.wn.com\">\n" +
                    "<wn:Accountname><![CDATA[" + request.getUsername() + "]]></wn:Accountname>\n" +
                    "<wn:Password><![CDATA[" + request.getPassword() + "]]></wn:Password>\n" +
                    "</wn:AuthenticationToken>\n" +
                    "</soapenv:Header>\n" +
                    "   <soapenv:Body>\n" +
                    "      <tic:getTicketByNumber>\n" +
                    "         <tic:in0>SABCDEFGHIJKLMNOPQ</tic:in0>\n" + // SABCD... will always be invalid anyway
                    "      </tic:getTicketByNumber>\n" +
                    "   </soapenv:Body>\n" +
                    "</soapenv:Envelope>";

            URL url = new URL(request.getUrl());
            HttpURLConnection  conn =  (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "text/xml; charset=utf-8");
            conn.setRequestProperty("SOAPAction", ""); // yes, it is empty in Soap ui as well.
            conn.setConnectTimeout(request.getTimeout());
            conn.setReadTimeout(request.getTimeout());
            
            try ( // Send the request XML
                    OutputStream outputStream = conn.getOutputStream()) {
                outputStream.write(soapXml.getBytes());
            }

            String xmlReturn;
            
            try {
                try ( // Read the response XML
                        InputStream inputStream = conn.getInputStream()) {
                    xmlReturn = Commons.inputStreamToString(inputStream);
                }
                
            } catch (IOException e) {
                
                try (InputStream errStream = conn.getErrorStream()) {
                    xmlReturn = Commons.inputStreamToString(errStream);
                }
                
            }
            
            Logger.getLogger(this.getClass().getName()).log(Level.INFO, "read xml from server: {0}", xmlReturn);
            
            // if using account Name not existing and such, 
            // error caused by network connection will also not contain them.
            // in this scenario, better use white list instead.
            response.setSuccess(
                    xmlReturn.contains("<errorCode xmlns=\"http://wsi.inbound.econnect.wn.com\">90022</errorCode>") ||
                    xmlReturn.contains("<errorMsg xmlns=\"http://wsi.inbound.econnect.wn.com\">Invalid ticket number</errorMsg>")
            );
            
//            response.setSuccess(
//                    !(xmlReturn.contains("Account name not existing") || 
//                            xmlReturn.contains("<errorCode xmlns=\"http://wsi.inbound.econnect.wn.com\">99002</errorCode>") ||
//                            xmlReturn.contains("Account name and password not matching") ||
//                            xmlReturn.contains("<errorCode xmlns=\"http://wsi.inbound.econnect.wn.com\">99001</errorCode>")
//                     )
//            
//            );
            
        } catch (IOException e) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, e.getMessage(), e);
            response.setSuccess(false);
            
        }
        
        return response;
    }
    
}
