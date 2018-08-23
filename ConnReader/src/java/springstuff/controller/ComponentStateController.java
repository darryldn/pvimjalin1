/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package springstuff.controller;

import com.google.gson.Gson;
import id.dni.ext.web.Util;
import id.dni.pvim.ext.err.PVIMErrorCodes;
import id.dni.pvim.ext.repo.exceptions.PvExtPersistenceException;
import id.dni.pvim.ext.service.json.ProviewLoginRequest;
import id.dni.pvim.ext.service.json.ProviewLoginResponse;
import id.dni.pvim.ext.web.in.OperationError;
import id.dni.pvim.ext.web.in.PVIMAuthToken;
import id.dni.pvim.ext.web.in.PaginationRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import springstuff.exceptions.RemoteWsException;
import springstuff.json.ComponentStateJson;
import springstuff.json.DeviceComponentRequestJson;
import springstuff.json.DeviceComponentResponseJson;
import springstuff.json.DeviceComponentStateJson;
import springstuff.json.MachineGpsJson;
import springstuff.model.ComponentStateVo;
import springstuff.service.DeviceComponentService;
import springstuff.service.LoginService;

/**
 *
 * @author darryl.sulistyan
 */
@Controller
public class ComponentStateController {

    private DeviceComponentService deviceComponentService;
    private LoginService loginService;
//    private String loginUrl;
//    private int timeout;

    @Autowired
    public void setDeviceComponentService(DeviceComponentService d) {
        this.deviceComponentService = d;
    }

    @Autowired
    public void setLoginService(LoginService l) {
        this.loginService = l;
    }

    // @Value does not get evaluated in Controller. Dunno why but no time to explain
    // Just move these to LoginServiceImpl instead
//    @Value("${Login.url}")
//    public void setLoginUrl(String l) {
//        this.loginUrl = l;
//    }
//
//    @Value("${Login.timeout}")
//    public void setLoginTimeout(String t) {
//        try {
//            timeout = Integer.parseInt(t);
//        } catch (NumberFormatException ex) {
//            timeout = 3000;
//        }
//    }

//    @RequestMapping(value = "/device", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
//    @ResponseBody
//    public ResponseEntity<String> getDeviceComponents(
//            @RequestParam(value = "id", required = false) String deviceID,
//            @RequestParam(value = "pageSize", required = false) Integer oPageSize,
//            @RequestParam(value = "pageNum", required = false) Integer oPageNum
//    ) {
//        DeviceComponentResponseJson response = new DeviceComponentResponseJson();
//        try {
//            Map<String, List<ComponentStateVo>> result;
//            if (deviceID != null) {
//                result = new HashMap<>();
//                result.put(deviceID, deviceComponentService.getDeviceComponentState(deviceID));
//            } else {
//                int pageSize = oPageSize == null ? -1 : oPageSize;
//                int pageNum = oPageNum == null ? -1 : oPageNum;
//
//                result = this.deviceComponentService.getAllDevices(pageSize, pageNum);
//            }
//            List<DeviceComponentStateJson> list = new ArrayList<>();
//
//            for (Map.Entry<String, List<ComponentStateVo>> entry : result.entrySet()) {
//                DeviceComponentStateJson ds = new DeviceComponentStateJson();
//                ds.setComponents(new ArrayList<>());
//                ds.setDeviceid(entry.getKey());
//
//                List<ComponentStateVo> entryComponents = entry.getValue();
//                ComponentStateVo ev = null;
//                for (ComponentStateVo c : entryComponents) {
//                    ds.getComponents().add(new ComponentStateJson(c.getComponent(), c.getComponentState()));
//                    ev = c;
//                }
//
//                if (ev != null) {
//                    String sLat = ev.getLatitude();
//                    String sLon = ev.getLongitude();
//
//                    if (sLat != null && sLon != null) {
//                        try {
//                            double lat = Double.parseDouble(sLat);
//                            double lon = Double.parseDouble(sLon);
//                            ds.setLocation(new MachineGpsJson(lat, lon));
//                        } catch (NumberFormatException ex) {
//
//                        }
//                    }
//                    if (ev.getDeviceDescr() != null) {
//                        ds.setDeviceType(ev.getDeviceDescr().trim());
//                    }
//                }
//
//                list.add(ds);
//            }
//            response.setDevices(list);
//
//        } catch (PvExtPersistenceException ex) {
//            Logger.getLogger(ComponentStateController.class.getName()).log(Level.SEVERE, null, ex);
//            OperationError err = new OperationError();
//            err.setErrCode("-15001");
//            err.setErrMsg(ex.getMessage());
//            response.setErr(err);
//
//        }
//
//        return Util.returnJson(response);
//
////        Gson gson = new Gson();
////        
////        final HttpHeaders httpHeaders = new HttpHeaders();
////        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
////        return new ResponseEntity<>(gson.toJson(response), httpHeaders, HttpStatus.OK);
//        //return gson.toJson(response);
//    }

    @RequestMapping(value = "/devicepost",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<String> getDeviceComponentsPost(@RequestBody String deviceRequestJson) {
        Gson gson = new Gson();
        DeviceComponentRequestJson request = gson.fromJson(deviceRequestJson, DeviceComponentRequestJson.class);
        DeviceComponentResponseJson response = new DeviceComponentResponseJson();
        String deviceID = request.getId();
        PaginationRequest page = request.getPage();
        PVIMAuthToken auth = request.getAuth();

        if (auth == null) {
            OperationError err = new OperationError();
            err.setErrCode("" + PVIMErrorCodes.E_INPUT_ERROR);
            err.setErrMsg("No authentication token given");
            response.setErr(err);
            return Util.returnJson(response);
        }
        
        try {
            ProviewLoginRequest pvl = new ProviewLoginRequest();
            pvl.setPassword(auth.getPassword());
            pvl.setUsername(auth.getUsername());
//            pvl.setUrl(loginUrl);
//            pvl.setTimeout(timeout);
            ProviewLoginResponse pvr = this.loginService.login(pvl);
            
            if (pvr == null || !pvr.isSuccess()) {
                OperationError err = new OperationError();
                err.setErrCode("" + PVIMErrorCodes.E_INPUT_ERROR);
                err.setErrMsg("Username / password wrong");
                response.setErr(err);

            } else {

                Map<String, List<ComponentStateVo>> result;
                if (deviceID != null) {
                    result = new HashMap<>();
                    result.put(deviceID, deviceComponentService.getDeviceComponentState(deviceID));
                } else {
                    int pageSize = page == null ? -1 : page.getPageSize();
                    int pageNum = page == null ? -1 : page.getPageNum();

                    result = this.deviceComponentService.getAllDevices(pageSize, pageNum);
                }
                List<DeviceComponentStateJson> list = new ArrayList<>();

                for (Map.Entry<String, List<ComponentStateVo>> entry : result.entrySet()) {
                    DeviceComponentStateJson ds = new DeviceComponentStateJson();
                    ds.setComponents(new ArrayList<>());
                    ds.setDeviceid(entry.getKey());

                    List<ComponentStateVo> entryComponents = entry.getValue();
                    ComponentStateVo ev = null;
                    for (ComponentStateVo c : entryComponents) {
                        ds.getComponents().add(new ComponentStateJson(c.getComponent(), c.getComponentState()));
                        ev = c;
                    }

                    if (ev != null) {
                        String sLat = ev.getLatitude();
                        String sLon = ev.getLongitude();

                        if (sLat != null && sLon != null) {
                            try {
                                double lat = Double.parseDouble(sLat);
                                double lon = Double.parseDouble(sLon);
                                ds.setLocation(new MachineGpsJson(lat, lon));
                            } catch (NumberFormatException ex) {

                            }
                        }
                        if (ev.getDeviceDescr() != null) {
                            ds.setDeviceType(ev.getDeviceDescr().trim());
                        }
                    }

                    list.add(ds);
                }
                response.setDevices(list);
            }

        } catch (PvExtPersistenceException | RemoteWsException ex) {
            Logger.getLogger(ComponentStateController.class.getName()).log(Level.SEVERE, null, ex);
            OperationError err = new OperationError();
            err.setErrCode("" + PVIMErrorCodes.E_DATABASE_ERROR);
            err.setErrMsg(ex.getMessage());
            response.setErr(err);

        }

        return Util.returnJson(response);

//        Gson gson = new Gson();
//        
//        final HttpHeaders httpHeaders = new HttpHeaders();
//        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
//        return new ResponseEntity<>(gson.toJson(response), httpHeaders, HttpStatus.OK);
        //return gson.toJson(response);
    }

}
