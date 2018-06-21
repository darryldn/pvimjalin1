/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.dni.pvim.ext.repo.db.vo;

import id.dni.pvim.ext.repo.exceptions.PvExtPersistenceException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author darryl.sulistyan
 */
public abstract class GenericVo implements ITableDescriptorVo {

    @Override
    public void fillDataFromMap(Map<String, Object> fromDB) throws PvExtPersistenceException {
        if (this.getFieldDescriptor() != null) {
            for (Map.Entry<String, Object> k : fromDB.entrySet()) {
                FieldData fd = this.getFieldDescriptor().get(k.getKey());
                if (fd != null) {
                    fd.setValue(k.getValue());
                } else {
                    Logger.getLogger(this.getClass().getName()).log(Level.WARNING, 
                            " - FieldData associated with key: {0} does not exist in tableDescriptor", k.getKey());
                }
            }
        } else {
            Logger.getLogger(this.getClass().getName()).log(Level.WARNING, " - fillDataFromMap, getFieldDescriptor returns null!");
        }
    }
    
    
    
}
