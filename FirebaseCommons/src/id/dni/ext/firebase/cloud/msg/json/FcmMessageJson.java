/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.dni.ext.firebase.cloud.msg.json;

/**
 *
 * @author darryl.sulistyan
 */
public class FcmMessageJson {
    
    private String to;
    private String priority;
    private FcmMessageNotificationJson notification;

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public FcmMessageNotificationJson getNotification() {
        return notification;
    }

    public void setNotification(FcmMessageNotificationJson notification) {
        this.notification = notification;
    }
    
    
    
}
