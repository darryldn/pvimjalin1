/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.dni.pvim.ext.telegram.pojo;

/**
 *{
			"message_id": 15,
			"from": see TelegramMessageFromPOJO,
			"chat": see TelegramMessageChatPOJO,
			"date": 1528607274,
			"text": "hello mai bot"
		}
 * @author darryl
 */
public class TelegramMessageContentPOJO {
    
    private long message_id;
    private TelegramMessageFromPOJO from;
    private TelegramMessageChatPOJO chat;
    private long date;
    private String text;

    public long getMessage_id() {
        return message_id;
    }

    public void setMessage_id(long message_id) {
        this.message_id = message_id;
    }

    public TelegramMessageFromPOJO getFrom() {
        return from;
    }

    public void setFrom(TelegramMessageFromPOJO from) {
        this.from = from;
    }

    public TelegramMessageChatPOJO getChat() {
        return chat;
    }

    public void setChat(TelegramMessageChatPOJO chat) {
        this.chat = chat;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return "TelegramMessageContentPOJO{" + "message_id=" + message_id + ", from=" + from + ", chat=" + chat + ", date=" + date + ", text=" + text + '}';
    }
    
    
    
}
