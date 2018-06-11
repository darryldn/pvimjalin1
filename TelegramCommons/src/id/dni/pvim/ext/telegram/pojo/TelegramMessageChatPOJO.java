/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.dni.pvim.ext.telegram.pojo;

/**
 *{
				"id": 610404514,
				"first_name": "darryl yunus",
				"last_name": "sulistyan",
				"type": "private"
			}
 * @author darryl
 */
public class TelegramMessageChatPOJO {
    
    private long id;
    private String first_name;
    private String last_name;
    private String type;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "TelegramMessageChatPOJO{" + "id=" + id + ", first_name=" + first_name + ", last_name=" + last_name + ", type=" + type + '}';
    }
    
    
    
}
