/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.dni.pvim.ext.telegram.getupdates.bean;

import java.io.Serializable;

/**
 *
 * @author darryl
 */
public class TelegramTimerInfo implements Serializable {
    
    private String timerName;
    private int key;

    public String getTimerName() {
        return timerName;
    }

    public void setTimerName(String timerName) {
        this.timerName = timerName;
    }

    public int getKey() {
        return key;
    }

    public void setKey(int key) {
        this.key = key;
    }

    @Override
    public String toString() {
        return "TelegramTimerInfo{" + "timerName=" + timerName + ", key=" + key + '}';
    }

    
    
    
}
