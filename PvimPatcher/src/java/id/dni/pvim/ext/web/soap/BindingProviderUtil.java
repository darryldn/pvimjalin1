/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.dni.pvim.ext.web.soap;

import java.util.Map;
import javax.xml.ws.BindingProvider;

/**
 *
 * @author darryl.sulistyan
 */
public class BindingProviderUtil {
    private static final BindingProviderUtil ME = new BindingProviderUtil();
    
    public static final String WS_URL = "WS_URL";
    public static final String WS_CONNECT_TIMEOUT = "WS_CONNECT_TIMEOUT";
    public static final String WS_REQUEST_TIMEOUT = "WS_REQUEST_TIMEOUT";
    
    // taken from decompiled jar
    private static final String 
            CONNECT_TIMEOUT = "com.sun.xml.internal.ws.connect.timeout",
            REQUEST_TIMEOUT = "com.sun.xml.internal.ws.request.timeout";
    
    protected BindingProviderUtil() {
        
    }
    
    public static void configureBindingProvider(BindingProvider wsPort, Map<String, Object> info) {
        ME._configure(wsPort, info);
    }
    
    private void _putWsPortInt(BindingProvider wsPort, String key, Object val, Object defVal) {
        if (val instanceof Integer) {
            wsPort.getRequestContext().put(key, val);
        } else if (val instanceof String) {
            try {
                wsPort.getRequestContext().put(key, Integer.parseInt((String)val));
            } catch (NumberFormatException e) {
                wsPort.getRequestContext().put(key, defVal);
            }
        }
    }
    
    private void _configure(BindingProvider wsPort, Map<String, Object> info) {
        wsPort.getRequestContext().put(
                BindingProvider.ENDPOINT_ADDRESS_PROPERTY, 
                info.get(WS_URL));
        
        _putWsPortInt(wsPort, CONNECT_TIMEOUT, info.get(WS_CONNECT_TIMEOUT), 5000);
        _putWsPortInt(wsPort, REQUEST_TIMEOUT, info.get(WS_REQUEST_TIMEOUT), 5000);
        
    }
}
