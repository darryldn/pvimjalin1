/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package springstuff.controller;

/**
 * Only for testing. This controller should only be put in internal network.
 * @author darryl.sulistyan
 */
//@Controller
public class FirebaseController {
    
//    private FirebaseCloudMessagingService fcmService;
//    
//    @Autowired
//    public void setFirebaseCloudMessagingService(FirebaseCloudMessagingService s) {
//        this.fcmService = s;
//    }
//    
//    @RequestMapping(value = "/fcm/send",
//            method = RequestMethod.POST,
//            produces = MediaType.APPLICATION_JSON_VALUE,
//            consumes = MediaType.APPLICATION_JSON_VALUE)
//    @ResponseBody
//    public ResponseEntity<String> send2FirebaseMessaging(@RequestBody String requestJson) {
//        Gson gson = new Gson();
//        
//        FcmMessageJson msg = gson.fromJson(requestJson, FcmMessageJson.class);
//        Map<String, Object> retObj = new HashMap<>();
//        
//        try {
//            FcmMessageDownstreamResponseJson sendMessage = this.fcmService.sendMessage(msg);
//            retObj.put("result", sendMessage);
//            
//        } catch (RemoteWsException ex) {
//            Logger.getLogger(FirebaseController.class.getName()).log(Level.SEVERE, null, ex);
//            OperationError err = new OperationError();
//            err.setErrCode("" + PVIMErrorCodes.E_UNKNOWN_ERROR);
//            err.setErrMsg(ex.getMessage());
//            retObj.put("err", err);
//        }
//        
//        return Util.returnJson(retObj);
//    }
    
}
