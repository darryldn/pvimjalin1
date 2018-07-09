/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package springstuff.service;

/**
 *
 * @author darryl.sulistyan
 */
public interface FirebaseDatabaseObjSynchronizerService {
    
    /**
     * 
     * @param obj
     * @return true to delete the object from Firebase store, false otherwise
     */
    public boolean deleteObj(Object obj);
    
    /**
     * Obtains firebase root path. The root path must contain the list of interested objects
     * to be sent to deleteObj above as direct children.
     * @return 
     */
    public String getRootPath();
    
    /**
     * Gets the class of Object sent to deleteObj above.
     * @return 
     */
    public Class<?> getClassObj();
    
}
