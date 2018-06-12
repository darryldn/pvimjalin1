/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.dni.pvim.ext.repo.db.vo;

import id.dni.pvim.ext.repo.exceptions.PvExtPersistenceException;
import java.util.Map;

/**
 *
 * @author darryl.sulistyan
 */
public abstract class GenericVo implements ITableDescriptorVo {

    @Override
    public void fillDataFromMap(Map<String, Object> fromDB) throws PvExtPersistenceException {
        for (Map.Entry<String, Object> k : fromDB.entrySet()) {
            this.getFieldDescriptor().get(k.getKey()).setValue(k.getValue());
        }
    }
    
    
    
}
