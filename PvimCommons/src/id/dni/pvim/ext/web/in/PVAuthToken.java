/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.dni.pvim.ext.web.in;

/**
 *
 * Authentication token sent for ProView (PV) Services
 * 
 * @author darryl.sulistyan
 */
public class PVAuthToken {
    
    private String username;
    private String password;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "PVAuthToken{" + "username=" + username + ", password=" + password + '}';
    }
    
    
    
}
