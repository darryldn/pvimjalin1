/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.dni.pvim.ext.sim.telegram;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class Chat {
    private long chatID;
    private String text;

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
    
    
}

/**
 *
 * @author darryl.sulistyan
 */
class ChatRepository {
 
    private static final ChatRepository ME = new ChatRepository();
    private final List<Chat> repos;
    
    private ChatRepository() {
        repos = new ArrayList<>();
    }
    
    public static ChatRepository getInstance() {
        return ME;
    }
    
    public synchronized void addChat(long chatID, String text) {
        repos.add(new Chat(chatID, text));
    }
    
    public List<Chat> getChats() {
        return repos;
    }
    
    public void clearChat() {
        repos.clear();
    }
    
}
