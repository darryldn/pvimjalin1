/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package springstuff.model;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author darryl.sulistyan
 */
public class Cache {
    
    private final Map<String, Object> cache;
    private int maxElement;
    
    public Cache() {
        cache = new ConcurrentHashMap<>();
        maxElement = 100000;
    }
    
    public Object get(String key) {
        return cache.get(key);
    }
    
    public void set(String key, Object value) {
        if (cache.size() >= maxElement) {
            cache.clear();
        }
        cache.put(key, value);
    }
    
}
