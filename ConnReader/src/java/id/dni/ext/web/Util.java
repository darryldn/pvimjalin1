/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.dni.ext.web;

import com.google.gson.Gson;
import id.dni.pvim.ext.service.json.ProviewLoginRequest;
import id.dni.pvim.ext.service.json.ProviewLoginResponse;
import id.dni.pvim.ext.web.in.PVIMAuthToken;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import springstuff.exceptions.RemoteWsException;
import springstuff.service.LoginService;

/**
 *
 * @author darryl.sulistyan
 */
public class Util {
    
    public static ResponseEntity<String> returnJson(Object what) {
        Gson gson = new Gson();
        return returnJsonStr(gson.toJson(what));
    }
    
    public static ResponseEntity<String> returnJsonStr(String jsonStr) {
        final HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        return new ResponseEntity<>(jsonStr, httpHeaders, HttpStatus.OK);
    }
    
    public static ProviewLoginResponse login(LoginService loginService, 
            PVIMAuthToken auth, String loginUrl, int timeout) throws RemoteWsException {
        
        ProviewLoginRequest pvl = new ProviewLoginRequest();
        pvl.setPassword(auth.getPassword());
        pvl.setUsername(auth.getUsername());
        pvl.setUrl(loginUrl);
        pvl.setTimeout(timeout);
        ProviewLoginResponse pvr = loginService.login(pvl);
        return pvr;
        
    }
    
}
