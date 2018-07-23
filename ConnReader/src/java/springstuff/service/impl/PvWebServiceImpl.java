/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package springstuff.service.impl;

import com.google.gson.Gson;
import id.dni.pvim.ext.dto.PvWsCassette;
import id.dni.pvim.ext.dto.PvWsCassetteResponse;
import id.dni.pvim.ext.dto.PvWsDefaultRequest;
import id.dni.pvim.ext.dto.PvWsDefaultResponse;
import id.dni.pvim.ext.web.in.Commons;
import id.dni.pvim.ext.web.in.PVAuthToken;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import springstuff.exceptions.CassetteBalanceServiceException;
import springstuff.exceptions.RemoteWsException;
import springstuff.service.IPvWebService;

/**
 *
 * @author darryl.sulistyan
 */
@Service
public class PvWebServiceImpl implements IPvWebService {

    private String casetteCounterServiceUrl;
    private int timeout;
    private String defaultUsername;
    private String defaultPassword;
    
    @Value("${CasetteCounterService.url}")
    public void setCasetteCounterServiceUrl(String url) {
        this.casetteCounterServiceUrl = url;
    }
    
    @Value("${CasetteCounterService.timeout}")
    public void setTimeout(String timeout) {
        this.timeout = Integer.parseInt(timeout);
    }
    
    @Value("${CasetteCounterService.defaultusername}")
    public void setCasetteCounterServiceDefaultUsername(String username) {
        this.defaultUsername = username;
    }
    
    @Value("${CasetteCounterService.defaultpassword}")
    public void setCasetteCounterServiceDefaultPassword(String password) {
        this.defaultPassword = password;
    }
    
    @Override
    public List<PvWsCassette> getCassetteBalance(String machineId) throws RemoteWsException {
        
        PvWsDefaultRequest ccService = new PvWsDefaultRequest();
        PVAuthToken def = new PVAuthToken();
        def.setUsername(defaultUsername);
        def.setPassword(defaultPassword);
        ccService.setAuth(def);
        Map<String, String> input = new HashMap<>();
        input.put("terminalID", machineId);
        ccService.setData(input);
        
        Gson gson = new Gson();
        String jsonRequest = gson.toJson(ccService);
        
        Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Requesting cassette balance with data: " + jsonRequest);
        
        String jsonResponse;
        try {
            jsonResponse = Commons.postJsonRequest(this.casetteCounterServiceUrl, timeout, jsonRequest);
        } catch (IOException ex) {
            throw new RemoteWsException(ex);
        }
        
        Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Obtained response: " + jsonResponse);
        
        PvWsCassetteResponse ccResponse = gson.fromJson(jsonResponse, PvWsCassetteResponse.class);
        if (ccResponse.getErr() != null) {
            throw new RemoteWsException(new CassetteBalanceServiceException(ccResponse.getErr()));
        }
        
        List<PvWsCassette> cassetteList = (List<PvWsCassette>) ccResponse.getResponse();
        return cassetteList;
        
    }
    
}
