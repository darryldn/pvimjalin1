/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.dni.pvim.ext.telegram.pojo;

/**
 *{
		"update_id": 728393104,
		"message": { see TelegramMessageContentPOJO }
	}
 * @author darryl
 */
public class TelegramMessagePOJO {
    
    private long update_id;
    private TelegramMessageContentPOJO message;

    public long getUpdate_id() {
        return update_id;
    }

    public void setUpdate_id(long update_id) {
        this.update_id = update_id;
    }

    public TelegramMessageContentPOJO getMessage() {
        return message;
    }

    public void setMessage(TelegramMessageContentPOJO message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "TelegramMessagePOJO{" + "update_id=" + update_id + ", message=" + message + '}';
    }
    
    
    
}
