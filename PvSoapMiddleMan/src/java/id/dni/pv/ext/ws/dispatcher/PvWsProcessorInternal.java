/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.dni.pv.ext.ws.dispatcher;

import id.dn.ws.ng.TextParserException;
import id.dn.ws.ng.parser.ITrNode;
import id.dn.ws.ng.parser.ITrTree;
import id.dn.ws.ng.parser.ParserFactory;
import id.dn.ws.ng.parser.TrEntry;
import id.dn.ws.ng.parser.TrTreeBuilder;
import id.dni.pv.ext.exception.PvWsException;
import id.dni.pv.ext.web.servlet.in.PvWsDefaultRequest;
import id.dni.pv.ext.web.servlet.in.PvWsDefaultResponse;
import id.dni.pvim.ext.web.in.Commons;
import id.dni.pvim.ext.web.in.OperationError;
import id.dni.pvim.ext.web.in.PVAuthToken;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author darryl.sulistyan
 */
class PvWsProcessorInternal {
    
    public PvWsProcessorInternal() {
        
    }
    
    private static final String XML_AUTH_USERNAME = "auth.USERNAME";
    private static final String XML_AUTH_PASSWORD = "auth.PASSWORD";
    
    private static List<TrEntry> convertStr2TrEntry(List<String> configList) {
        int cz = configList.size();
        List<TrEntry> entry = new ArrayList<>(cz);
        for (int i=0; i<cz; ++i) {
            String resp = configList.get(i);
            String c = resp + ";;;;M";

            String[] dc = c.split(";");
            String name = dc[0];
            String path = dc[1];
            String type = dc[2];
            String parent = dc[3];
            entry.add(new TrEntry(name, path, type, parent));
        }
        return entry;
    }
    
    private PvWsDefaultResponse sendRequest(String content, DefaultWsConfig wsConfig) 
            throws IOException {
        
        URL url = new URL(wsConfig.getUrl());
        HttpURLConnection  conn =  (HttpURLConnection) url.openConnection();
        conn.setDoOutput(true);
        
        // ProView services uses text/xml encoding.
        conn.setRequestProperty("Content-Type", "text/xml; charset=utf-8");
        conn.setRequestProperty("SOAPAction", ""); // yes, it is empty in Soap ui as well.
        
        conn.setConnectTimeout(wsConfig.getConnectTimeout());
        conn.setReadTimeout(wsConfig.getRequestTimeout());

        try ( // Send the request XML
                OutputStream outputStream = conn.getOutputStream()) {
            outputStream.write(content.getBytes());
        }

        PvWsDefaultResponse responseWs = new PvWsDefaultResponse();
        Map<String, Object> data;
        
        try {
            try ( // Read the response XML
                    InputStream inputStream = conn.getInputStream()) {
                List<String> configList = wsConfig.getOutputConfigList();
                List<TrEntry> entryList = convertStr2TrEntry(configList);
                ITrNode rootNode = TrTreeBuilder.createBuilder().setEntries(entryList).build();
                
                ITrTree responseParser = ParserFactory.getInstance().createParser("text/xml");
                data = responseParser.parseDoc(rootNode, inputStream, 
                        "text/xml", StandardCharsets.UTF_8);
                responseWs.setResponse(data);
                
            } catch (TextParserException ex) {
                throw new IOException(ex);
                
            }

        } catch (IOException e) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, e.getMessage(), e);

            // read the error stream in case of fault.
            try (InputStream errStream = conn.getErrorStream()) {
                List<String> errList = wsConfig.getErrConfigList();
                List<TrEntry> entryList = convertStr2TrEntry(errList);
                ITrNode rootNode = TrTreeBuilder.createBuilder().setEntries(entryList).build();
                
                ITrTree responseParser = ParserFactory.getInstance().createParser("text/xml");
                data = responseParser.parseDoc(rootNode, errStream, 
                        "text/xml", StandardCharsets.UTF_8);
                OperationError err = new OperationError();
                err.setErrCode((String) data.get("errCode"));
                err.setErrMsg((String) data.get("errMsg"));
                responseWs.setErr(err);
                
            } catch (TextParserException ex) {
                throw new IOException(ex);
                
            }
            
            //response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
        
        return responseWs;
    }
    
    public PvWsDefaultResponse process(String operationName, final PvWsDefaultRequest requestObj) throws PvWsException {
        PVAuthToken auth = requestObj.getAuth();
        
        if (auth == null) {
            throw new PvWsException();
        }
        
        DefaultWsConfig wsConfig = new DefaultWsConfig(operationName);
        
        String content = InputTemplateCache.getInstance().getContent(wsConfig.getContentFileName());
        if (content == null ) {
            throw new PvWsException();
        }
        
        String replacedContent = Commons.replaceFWVars(content, new Commons.IReplaceFWVarCallback() {
            @Override
            public String replace(String val) {
                if (val == null) {
                    return "";
                } else {
                    switch (val) {
                        case XML_AUTH_PASSWORD:
                            return requestObj.getAuth().getPassword();
                            
                        case XML_AUTH_USERNAME:
                            return requestObj.getAuth().getUsername();
                            
                        default: {
                            return requestObj.getData().get(val);
                            
                        }
                    }
                }
            }
        });
        
        try {
            PvWsDefaultResponse responseWs = sendRequest(replacedContent, wsConfig);
            return responseWs;
        } catch (IOException ex) {
            throw new PvWsException(ex);
        }
    }
    
}
