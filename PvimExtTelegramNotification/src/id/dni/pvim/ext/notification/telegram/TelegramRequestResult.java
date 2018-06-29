/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.dni.pvim.ext.notification.telegram;

/**
 *
 * @author darryl.sulistyan
 */
public class TelegramRequestResult {
    
    private final String mobile;
    private final long chatId;
    private final String message;
    private final int status;
    private final long date;
    private String receiverName;

    public TelegramRequestResult(String mobile, long chatId, String message, int status) {
        this.mobile = mobile;
        this.chatId = chatId;
        this.message = message;
        this.status = status;
        this.date = System.currentTimeMillis();
    }

    public String getReceiverName() {
        return receiverName;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }

    public long getDate() {
        return date;
    }
    
    public String getMobile() {
        return mobile;
    }

    public long getChatId() {
        return chatId;
    }

    public String getMessage() {
        return message;
    }

    public int getStatus() {
        return status;
    }
    
}
