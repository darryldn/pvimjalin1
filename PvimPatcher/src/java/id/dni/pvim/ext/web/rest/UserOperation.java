/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.dni.pvim.ext.web.rest;

import id.dni.pvim.ext.conf.PatcherConfig;
import id.dni.pvim.ext.web.in.Util;
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
public class UserOperation {
    
    public PVLoginResponse login(PVLoginRequest request) {
        
        PVLoginResponse response = new PVLoginResponse();
        
        try {
        
            String soapXml = 
                    "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                    "<soapenv:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ter=\"http://soap.wincor-nixdorf.com/itmp/TerminalCounterService\">\n" +
                    "   <soapenv:Header>\n" +
                    "   	<n0:Security xmlns:n0=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd\" soapenv:mustUnderstand=\"1\">\n" +
                    "      <n0:UsernameToken xmlns:n1=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd\" n1:Id=\"UsernameToken-14050342\">\n" +
                    "        <n0:Username><![CDATA[" + request.getUsername() + "]]></n0:Username>\n" +
                    "        <n0:Password n0:Type=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-username-token-profile-1.0#PasswordText\"><![CDATA[" + request.getPassword() + "]]></n0:Password>\n" +
                    "      </n0:UsernameToken>\n" +
                    "    </n0:Security>\n" +
                    "   </soapenv:Header>\n" +
                    "   <soapenv:Body>\n" +
                    "      <ter:getCassetteCounters soapenv:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\">\n" +
                    "         <terminalID xsi:type=\"xsd:string\">blahblahblahblah</terminalID>\n" +
                    "      </ter:getCassetteCounters>\n" +
                    "   </soapenv:Body>\n" +
                    "</soapenv:Envelope>";

            URL url = new URL(PatcherConfig.getInstance().get("LoginService.url"));
            HttpURLConnection  conn =  (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "text/xml; charset=utf-8");
            conn.setRequestProperty("SOAPAction", ""); // yes, it is empty in Soap ui as well.
            conn.setConnectTimeout(Integer.parseInt(PatcherConfig.getInstance().get("LoginService.connect_timeout", "3000")));
            conn.setReadTimeout(Integer.parseInt(PatcherConfig.getInstance().get("LoginService.request_timeout", "3000")));
            
            try ( // Send the request XML
                    OutputStream outputStream = conn.getOutputStream()) {
                outputStream.write(soapXml.getBytes());
            }

            String xmlReturn;
            
            try {
                try ( // Read the response XML
                        InputStream inputStream = conn.getInputStream()) {
                    xmlReturn = Util.inputStreamToString(inputStream);
                }
                
            } catch (IOException e) {
                
                try (InputStream errStream = conn.getErrorStream()) {
                    xmlReturn = Util.inputStreamToString(errStream);
                }
                
            }
            
            Logger.getLogger(this.getClass().getName()).log(Level.INFO, "read xml from server: {0}", xmlReturn);
            
            response.setIsLoginSuccess(
                    !(xmlReturn.contains("CMDSTAT_USER_LOGIN_FAILED") || 
                            xmlReturn.contains("CMDSTAT_USER_INVALID")));
            
        } catch (IOException e) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, e.getMessage(), e);
            response.setIsLoginSuccess(false);
            
        }
        
        return response;
    }
    
}
