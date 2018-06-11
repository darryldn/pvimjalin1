/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.dni.pvim.ext.telegram.pojo;

/**
 *{
				"id": 610404514,
				"is_bot": false,
				"first_name": "darryl yunus",
				"last_name": "sulistyan",
				"language_code": "en-us"
			}
 * @author darryl
 */
public class TelegramMessageFromPOJO {
    
    private long id;
    private boolean is_bot;
    private String first_name;
    private String last_name;
    private String language_code;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public boolean isIs_bot() {
        return is_bot;
    }

    public void setIs_bot(boolean is_bot) {
        this.is_bot = is_bot;
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

    public String getLanguage_code() {
        return language_code;
    }

    public void setLanguage_code(String language_code) {
        this.language_code = language_code;
    }

    @Override
    public String toString() {
        return "TelegramMessageFromPOJO{" + "id=" + id + ", is_bot=" + is_bot + ", first_name=" + first_name + ", last_name=" + last_name + ", language_code=" + language_code + '}';
    }
    
    
    
}
