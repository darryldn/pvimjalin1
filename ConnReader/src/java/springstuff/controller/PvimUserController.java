/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package springstuff.controller;

import com.google.gson.Gson;
import id.dni.ext.web.Util;
import id.dni.ext.web.ws.obj.firebase.FbPvimSlmUserJson;
import id.dni.pvim.ext.err.PVIMErrorCodes;
import id.dni.pvim.ext.repo.db.vo.SlmUserTokenVo;
import id.dni.pvim.ext.repo.db.vo.SlmUserVo;
import id.dni.pvim.ext.web.in.OperationError;
import id.dni.pvim.ext.web.in.PVIMAuthToken;
import id.dni.pvim.ext.web.in.PVIMUserTokenRequest;
import id.dni.pvim.ext.web.in.PVIMUserTokenResponse;
import id.dni.pvim.ext.web.in.UserToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import springstuff.json.PVIMGetUserRequest;
import springstuff.json.PVIMGetUserRequestPayload;
import springstuff.json.PVIMGetUserResponse;
import springstuff.service.UserService;

/**
 *
 * @author darryl.sulistyan
 */
@Controller
public class PvimUserController {
    
    private UserService userService;
    
    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }
    
    @RequestMapping(value = "/pvim/user/token/set", method = RequestMethod.POST, 
            produces = MediaType.APPLICATION_JSON_VALUE, 
            consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<String> setUserToken(@RequestBody String userTokenJson) {
        
        Gson gson = new Gson();
        PVIMUserTokenRequest request = gson.fromJson(userTokenJson, PVIMUserTokenRequest.class);
        PVIMUserTokenResponse resp = new PVIMUserTokenResponse();
        
        PVIMAuthToken auth = request.getAuth();
        
        if (auth == null) {
            // DIE!
            OperationError err = new OperationError();
            err.setErrCode("" + PVIMErrorCodes.E_INPUT_ERROR);
            err.setErrMsg("No authentication token given");
            resp.setErr(err);
            return Util.returnJson(resp);
        }
        
        SlmUserVo authUser = userService.checkUser(auth);
        
        if (authUser == null) {
            // DIE!
            OperationError err = new OperationError();
            err.setErrCode("" + PVIMErrorCodes.E_INPUT_ERROR);
            err.setErrMsg("Username / password error");
            resp.setErr(err);
            return Util.returnJson(resp);
        }
        
        UserToken requestToken = request.getToken();
        SlmUserTokenVo dbToken = this.userService.setOrUpdateUserToken(requestToken);
        
        if (dbToken == null) {
            OperationError err = new OperationError();
            err.setErrCode("" + PVIMErrorCodes.E_DATABASE_ERROR);
            err.setErrMsg("Database error, please contact administrator");
            resp.setErr(err);
            return Util.returnJson(resp);
        }
        
        resp.setToken(requestToken);
        return Util.returnJson(resp);
    }
    
    @RequestMapping(value = "/pvim/user/token/delete", method = RequestMethod.POST, 
            produces = MediaType.APPLICATION_JSON_VALUE, 
            consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<String> deleteUserToken(@RequestBody String userTokenJson) {
        
        Gson gson = new Gson();
        PVIMUserTokenRequest request = gson.fromJson(userTokenJson, PVIMUserTokenRequest.class);
        PVIMUserTokenResponse resp = new PVIMUserTokenResponse();
        
        PVIMAuthToken auth = request.getAuth();
        
        if (auth == null) {
            // DIE!
            OperationError err = new OperationError();
            err.setErrCode("" + PVIMErrorCodes.E_INPUT_ERROR);
            err.setErrMsg("No authentication token given");
            resp.setErr(err);
            return Util.returnJson(resp);
        }
        
        SlmUserVo authUser = userService.checkUser(auth);
        
        if (authUser == null) {
            // DIE!
            OperationError err = new OperationError();
            err.setErrCode("" + PVIMErrorCodes.E_INPUT_ERROR);
            err.setErrMsg("Username / password error");
            resp.setErr(err);
            return Util.returnJson(resp);
        }
        
        UserToken requestToken = request.getToken();
        SlmUserTokenVo dbToken = this.userService.deleteUserToken(requestToken);
        
        if (dbToken == null) {
            OperationError err = new OperationError();
            err.setErrCode("" + PVIMErrorCodes.E_DATABASE_ERROR);
            err.setErrMsg("Database error, please contact administrator");
            resp.setErr(err);
            return Util.returnJson(resp);
        }
        
        resp.setToken(requestToken);
        return Util.returnJson(resp);
    }
    
    @RequestMapping(value = "/pvim/user/get", method = RequestMethod.POST, 
            produces = MediaType.APPLICATION_JSON_VALUE, 
            consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<String> getPvimUser(@RequestBody String userJson) {
        Gson gson = new Gson();
        PVIMGetUserRequest request = gson.fromJson(userJson, PVIMGetUserRequest.class);
        PVIMGetUserResponse resp = new PVIMGetUserResponse();
        
        PVIMAuthToken auth = request.getAuth();
        
        if (auth == null) {
            // DIE!
            OperationError err = new OperationError();
            err.setErrCode("" + PVIMErrorCodes.E_INPUT_ERROR);
            err.setErrMsg("No authentication token given");
            resp.setErr(err);
            return Util.returnJson(resp);
        }
        
        SlmUserVo authUser = userService.checkUser(auth);
        
        if (authUser == null) {
            // DIE!
            OperationError err = new OperationError();
            err.setErrCode("" + PVIMErrorCodes.E_INPUT_ERROR);
            err.setErrMsg("Username / password error");
            resp.setErr(err);
            return Util.returnJson(resp);
        }
        
        PVIMGetUserRequestPayload payload = request.getUser();
        
        if (payload == null) {
            OperationError err = new OperationError();
            err.setErrCode("" + PVIMErrorCodes.E_INPUT_ERROR);
            err.setErrMsg("No user data given");
            resp.setErr(err);
            return Util.returnJson(resp);
        }
        
        SlmUserVo targetUser;
        
        targetUser = userService.getUserDataByEmail(payload.getEmail());
        if (targetUser == null) {
            targetUser = userService.getUserDataByUsername(payload.getLoginname());
        }
        
        if (targetUser == null) {
            OperationError err = new OperationError();
            err.setErrCode("" + PVIMErrorCodes.E_CONFIG);
            err.setErrMsg("Payload user account not found");
            resp.setErr(err);
            return Util.returnJson(resp);
        }
        
        FbPvimSlmUserJson fbUserJson = new FbPvimSlmUserJson();
        fbUserJson.setEmail(targetUser.getEmail());
        fbUserJson.setLocked(targetUser.getLocked());
        fbUserJson.setLoginName(targetUser.getLoginName());
        fbUserJson.setMobile(targetUser.getMobile());
        fbUserJson.setUserId(targetUser.getUserID());
        fbUserJson.setUserType(targetUser.getUserType());
        
        resp.setUser(fbUserJson);
        return Util.returnJson(resp);
    }
    
}
