/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package springstuff.service.impl;

import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import springstuff.model.Cache;
import springstuff.service.CacheService;

/**
 *
 * @author darryl.sulistyan
 */
@Service
@Qualifier("SimpleCacheService")
public class SimpleCacheServiceImpl implements CacheService {

    private Map<String, Cache> cache;
    
    @PostConstruct
    public void init() {
        cache = new ConcurrentHashMap<>();
    }
    
    @Override
    public void init(String name, Properties config) {
        
    }
    
    @Override
    public Object get(String name, String key) {
        Cache ce = cache.get(name);
        if (ce != null) {
            return ce.get(key);
        }
        return null;
    }

    @Override
    public void set(String name, String key, Object value) {
        Cache ce = cache.get(name);
        if (ce == null) {
            ce = new Cache();
            cache.put(name, ce);
        }
        ce.set(key, value);
    }
    
}
