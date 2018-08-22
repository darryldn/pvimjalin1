/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package springstuff.controller;

import com.google.gson.Gson;
import id.dni.ext.firebase.cloud.msg.json.FcmMessageDownstreamResponseJson;
import id.dni.ext.firebase.cloud.msg.json.FcmMessageJson;
import id.dni.ext.web.Util;
import id.dni.pvim.ext.err.PVIMErrorCodes;
import id.dni.pvim.ext.web.in.OperationError;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import springstuff.exceptions.RemoteWsException;
import springstuff.service.firebase.FirebaseCloudMessagingService;

/**
 * Only for testing. This controller should only be put in internal network.
 * @author darryl.sulistyan
 */
@Controller
public class FirebaseController {
    
    private FirebaseCloudMessagingService fcmService;
    
    @Autowired
    public void setFirebaseCloudMessagingService(FirebaseCloudMessagingService s) {
        this.fcmService = s;
    }
    
    @RequestMapping(value = "/fcm/send",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<String> send2FirebaseMessaging(@RequestBody String requestJson) {
        Gson gson = new Gson();
        
        FcmMessageJson msg = gson.fromJson(requestJson, FcmMessageJson.class);
        Map<String, Object> retObj = new HashMap<>();
        
        try {
            FcmMessageDownstreamResponseJson sendMessage = this.fcmService.sendMessage(msg);
            retObj.put("result", sendMessage);
            
        } catch (RemoteWsException ex) {
            Logger.getLogger(FirebaseController.class.getName()).log(Level.SEVERE, null, ex);
            OperationError err = new OperationError();
            err.setErrCode("" + PVIMErrorCodes.E_UNKNOWN_ERROR);
            err.setErrMsg(ex.getMessage());
            retObj.put("err", err);
        }
        
        return Util.returnJson(retObj);
    }
    
}
