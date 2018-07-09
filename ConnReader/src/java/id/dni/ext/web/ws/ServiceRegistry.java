/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.dni.ext.web.ws;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author darryl.sulistyan
 */
public class ServiceRegistry {
    
    private static final ServiceRegistry INSTANCE = new ServiceRegistry();
    private final Map<String, Object> what;
    
    private ServiceRegistry() {
        what = new HashMap<>();
    }
    
    public static ServiceRegistry getInstance() {
        return INSTANCE;
    }
    
    public void setService(Class clazz, Object instance) {
        what.put(clazz.getName(), instance);
    }
    
    public Object getService(Class clazz) {
        return what.get(clazz.getName());
    }
    
}
