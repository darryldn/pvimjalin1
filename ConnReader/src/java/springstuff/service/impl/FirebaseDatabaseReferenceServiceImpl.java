/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package springstuff.service.impl;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import springstuff.service.firebase.FirebaseDatabaseReferenceService;

/**
 *
 * @author darryl.sulistyan
 */
@Service
public class FirebaseDatabaseReferenceServiceImpl implements FirebaseDatabaseReferenceService {

    private String firebaseServiceAuth;
    private String firebaseDatabaseUrl;
    private String firebaseRootPath;
    private int firebaseTimeout;

    @Value("${firebase.service_auth}")
    public void setFirebaseServiceAuthJsonFile(String j) {
        this.firebaseServiceAuth = j;
    }

    @Value("${firebase.database.url}")
    public void setFirebaseDatabaseUrl(String url) {
        this.firebaseDatabaseUrl = url;
    }

    @Value("${firebase.database.root}")
    public void setFirebaseRootPath(String path) {
        this.firebaseRootPath = path;
    }

    @Value("${firebase.database.timeout}")
    public void setFirebaseTimeout(String strTimeout) {
        try {
            firebaseTimeout = Integer.parseInt(strTimeout);
        } catch (NumberFormatException ex) {
            firebaseTimeout = 3000;
        }
    }

//    private DatabaseReference ref;

    @PostConstruct
    public void init() {
        try {
            InputStream serviceAuthJson = this.getClass().getResourceAsStream(this.firebaseServiceAuth);
//        System.setProperty("https.proxyHost", "proxy-pdb.wincor-nixdorf.com");
//        System.setProperty("https.proxyPort", "81");
            FirebaseOptions opt = new FirebaseOptions.Builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAuthJson))
                    .setDatabaseUrl(this.firebaseDatabaseUrl)
                    .setConnectTimeout(firebaseTimeout)
                    .setReadTimeout(firebaseTimeout)
                    .build();

            FirebaseApp.initializeApp(opt);
            
        } catch (IOException ex) {
            Logger.getLogger(FirebaseDatabaseReferenceServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
            throw new RuntimeException(ex);
            
        }
    }

    @Override
    public DatabaseReference getDatabaseReference() {
        return FirebaseDatabase.getInstance().getReference();
    }

    @Override
    public DatabaseReference getDatabaseReference(String rootPath) {
        return FirebaseDatabase.getInstance().getReference(rootPath);
    }

}
