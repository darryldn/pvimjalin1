/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package springstuff.service.firebase;

import com.google.firebase.database.DatabaseReference;

/**
 *
 * @author darryl.sulistyan
 */
public interface FirebaseDatabaseReferenceService {
    
    public DatabaseReference getDatabaseReference();
    
    public DatabaseReference getDatabaseReference(String rootPath);
    
}
