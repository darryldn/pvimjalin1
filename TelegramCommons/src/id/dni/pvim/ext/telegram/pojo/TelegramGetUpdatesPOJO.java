/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.dni.pvim.ext.telegram.pojo;

import java.util.List;

/**
 *{
	"ok": true,
	"result": [{
          @see TelegramUpdateObjPOJO class
        }]
}
 * @author darryl
 */
public class TelegramGetUpdatesPOJO {
    
    private boolean ok;
    private List<TelegramUpdateObjPOJO> result;
    
    public boolean getOk() {
        return ok;
    }

    public void setOk(boolean ok) {
        this.ok = ok;
    }

    public List<TelegramUpdateObjPOJO> getResult() {
        return result;
    }

    public void setResult(List<TelegramUpdateObjPOJO> result) {
        this.result = result;
    }

    @Override
    public String toString() {
        return "TelegramGetUpdatesPOJO{" + "ok=" + ok + ", result=" + result + '}';
    }
    
}
