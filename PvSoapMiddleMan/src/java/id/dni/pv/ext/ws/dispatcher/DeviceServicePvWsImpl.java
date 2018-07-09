/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.dni.pv.ext.ws.dispatcher;

import id.dni.pvim.ext.dto.PvWsDefaultResponse;
import id.dni.pvim.ext.dto.PvWsCassette;
import id.dni.pvim.ext.web.in.Commons;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author darryl.sulistyan
 */
public class DeviceServicePvWsImpl implements IPvWs {

   
    private void setCassetteInfo(String parent, Map<String, String> refMap, PvWsCassette cassette, 
            String param, Object value) throws NoSuchMethodException, 
            IllegalAccessException, IllegalArgumentException, 
            InvocationTargetException {
        
        String properCased = param.substring(0, 1).toUpperCase() + param.substring(1);
        Method m = cassette.getClass().getDeclaredMethod("set" + properCased, String.class);
        Map<String, Object> ref = (Map<String, Object>) value;
        String content = (String) ref.get(parent + "." + param + ".content");
        String href = (String) ref.get(parent + "." + param + ".href");
        
        if (Commons.isEmptyStrIgnoreSpaces(content)) {
            if (Commons.isEmptyStrIgnoreSpaces(href)) {
                content = null;
            } else {
                content = refMap.get(href.substring(1)); // ignore the '#' character!
            }
        }
        
        m.invoke(cassette, content);
        
    }
    
    private PvWsCassette map2Cassette(Map<String, Object> dick, String parentTag, Map<String, String> refMap) {
        PvWsCassette cassette = new PvWsCassette();
        // cassette object
        for (Map.Entry<String, Object> e : dick.entrySet()) {
            String key = e.getKey();
            String paramId = key.split("\\.")[1];
            if (!"id".equals(paramId) && !"content".equals(paramId)) {
                try {
                    setCassetteInfo(parentTag, refMap, cassette, paramId, e.getValue());
                } catch (NoSuchMethodException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
                    Logger.getLogger(DeviceServicePvWsImpl.class.getName()).log(Level.SEVERE, null, ex);
                    // ignore if exception!
                }
            }

        }
        return cassette;
    }
    
    @Override
    public void handleRequest(String operationName, HttpServletRequest request, HttpServletResponse response) throws IOException {
        
        final Map<String, String> refMap = new HashMap<>();
        final Map<String, PvWsCassette> cassetteMap = new HashMap<>();
        final List<PvWsCassette> cassetteList = new ArrayList<>();
        
        new PvWsWithPostProcessorImpl(new PvWsWithPostProcessorImpl.PvWsPostProcessor() {
            @Override
            public PvWsDefaultResponse handle(PvWsDefaultResponse obj) {
                
                if (obj.getErr() != null) {
                    return obj;
                }
                
                Map<String, Object> data = (Map<String, Object>) obj.getResponse();
                Logger.getLogger(this.getClass().getName()).log(Level.FINE, " - Accept {0}", data);
                
                List multiref = (List) data.get("multiref");
                List resp = (List) data.get("resp");
                
                for (Object o : multiref) {
                    Map<String, Object> dick = (Map<String, Object>) o;
                    String multiref_id = (String) dick.get("multiref.id");
                    String multiref_content = (String) dick.get("multiref.content");
                    
                    if (!Commons.isEmptyStrIgnoreSpaces(multiref_id) && !Commons.isEmptyStrIgnoreSpaces(multiref_content)) {
                        Logger.getLogger(this.getClass().getName()).log(Level.FINE, " -- read id={0} content={1}", 
                                new Object[]{multiref_id, multiref_content});
                        
                        // a complete reference
                        refMap.put(multiref_id, multiref_content);
                    }
                }
                
                for (Object o : multiref) {
                    Map<String, Object> dick = (Map<String, Object>) o;
                    String multiref_id = (String) dick.get("multiref.id");
                    String multiref_content = (String) dick.get("multiref.content");
                    
                    if (!Commons.isEmptyStrIgnoreSpaces(multiref_id) && Commons.isEmptyStrIgnoreSpaces(multiref_content)) {
                        
                        PvWsCassette cassette = map2Cassette(dick, "multiref", refMap);
                        cassetteMap.put(multiref_id, cassette);
                        
                    }
                }
                
                for (Object o : resp) {
                    
                    Map<String, Object> shit = (Map<String, Object>) o;
                    String resp_href = (String) shit.get("resp.href");
                    
                    if (!Commons.isEmptyStrIgnoreSpaces(resp_href)) {
                        cassetteList.add(cassetteMap.get(resp_href.substring(1)));
                    } else {
                        // assume cassette data is here!
                        PvWsCassette cassette = map2Cassette(shit, "resp", refMap);
                        cassetteList.add(cassette);
                    }
                    
                }
                
                obj.setResponse(cassetteList);
                
                return obj;
            }
        }).handleRequest(operationName, request, response);
        
    }
    
}
