/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package springstuff.json;

/**
 *
 * @author darryl.sulistyan
 */
public class ComponentStateJson {
    
    private String component;
    private String state;

    public ComponentStateJson(String component, String state) {
        this.component = component;
        this.state = state;
    }

    public String getComponent() {
        return component;
    }

    public void setComponent(String component) {
        this.component = component;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
    
    
    
}
