/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package springstuff.service;

import java.util.Properties;

/**
 *
 * @author darryl.sulistyan
 */
public interface CacheService {
    
    public void init(String name, Properties config);
    
    public Object get(String name, String key);
    
    public void set(String name, String key, Object value);
    
}
