/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.dni.pv.ext.ws.dispatcher;

import id.dni.pvim.ext.web.in.Commons;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author darryl.sulistyan
 */
public class InputTemplateCache {
    
    private static final InputTemplateCache ME = new InputTemplateCache();
    private final Map<String, String> contentCache;
    
    private InputTemplateCache() {
        contentCache = new HashMap<>();
    }
    
    public void init() {
        Map<String, String> config = WsConfig.getInstance().getProperties();
        
        for (Map.Entry<String, String> entry : config.entrySet()) {
            String key = entry.getKey();
            if (key.endsWith(".template_file")) {
                loadFile(entry.getValue());
            }
        }
        
//        loadFile("/getCassetteCounters.input.xml");
//        loadFile("/TerminalApplicationService_getStates.input.xml");
    }
    
    private void loadFile(String fileName) {
        Logger.getLogger(this.getClass().getName()).log(Level.FINE, ">> loadFile({0})", fileName);
        InputStream in = InputTemplateCache.class.getResourceAsStream(fileName);
        try {
            String content = Commons.inputStreamToString(in);
            contentCache.put(fileName, content);
            
        } catch (IOException ex) {
            Logger.getLogger(InputTemplateCache.class.getName()).log(Level.SEVERE, null, ex);
            
        } finally {
            try {
                in.close();
            } catch (IOException ex) {
            }
            
        }
        Logger.getLogger(this.getClass().getName()).log(Level.FINE, "<< loadFile()");
    }
    
    public static InputTemplateCache getInstance() {
        return ME;
    }
    
    public String getContent(String fileName) {
        return contentCache.get(fileName);
    }
    
}
