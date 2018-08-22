/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.dni.ext.dmz.service.impl;

import id.dni.ext.dmz.exception.RemoteServiceException;
import id.dni.ext.dmz.service.LoginService;
import id.dni.pvim.ext.service.PVIMLoginService;
import id.dni.pvim.ext.service.json.ProviewLoginRequest;
import id.dni.pvim.ext.service.json.ProviewLoginResponse;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 *
 * @author darryl.sulistyan
 */
@Service
@Qualifier("PVIMLoginServiceImpl")
public class LoginServiceImpl implements LoginService {

    private PVIMLoginService loginService;
    
    private String loginUrl;
    private int timeout;
    
    @Value("${Login.url}")
    public void setLoginUrl(String l) {
        this.loginUrl = l;
    }

    @Value("${Login.timeout}")
    public void setLoginTimeout(String t) {
        try {
            timeout = Integer.parseInt(t);
        } catch (NumberFormatException ex) {
            timeout = 3000;
        }
    }
    
    @PostConstruct
    public void init() {
        loginService = new PVIMLoginService();
    }
    
    @Override
    public ProviewLoginResponse login(ProviewLoginRequest request) throws RemoteServiceException {
        request.setUrl(loginUrl);
        request.setTimeout(timeout);
        return loginService.login(request);
    }
    
}
