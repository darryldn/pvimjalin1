/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package springstuff.service;

import id.dni.pvim.ext.service.json.ProviewLoginRequest;
import id.dni.pvim.ext.service.json.ProviewLoginResponse;
import springstuff.exceptions.RemoteWsException;

/**
 *
 * @author darryl.sulistyan
 */
public interface LoginService {
    
    public ProviewLoginResponse login(ProviewLoginRequest request) throws RemoteWsException;
    
}
