/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.dni.pvim.ext.sim.telegram;

import java.util.ArrayList;
import java.util.List;

class Chat {
    private long chatID;
    private String text;
    private long updateID;
    private long date;

    public Chat(long chatID, String text) {
        this.chatID = chatID;
        this.text = text;
    }
    
    public long getChatID() {
        return chatID;
    }

    public void setChatID(long chatID) {
        this.chatID = chatID;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public long getUpdateID() {
        return updateID;
    }

    public void setUpdateID(long updateID) {
        this.updateID = updateID;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }
    
    
    
}

/**
 *
 * @author darryl.sulistyan
 */
class ChatRepository {
 
    private static final ChatRepository ME = new ChatRepository();
    private long lastChatUpdateID;
    private final List<Chat> repos;
    
    private ChatRepository() {
        repos = new ArrayList<>();
        lastChatUpdateID = 1;
    }
    
    public static ChatRepository getInstance() {
        return ME;
    }
    
    public synchronized void addChat(long chatID, String text) {
        Chat n = new Chat(chatID, text);
        n.setUpdateID(lastChatUpdateID++);
        n.setDate(System.currentTimeMillis());
        repos.add(n);
    }
    
    public List<Chat> getChats() {
        return repos;
    }
    
    public void clearChat() {
        repos.clear();
    }
    
}
