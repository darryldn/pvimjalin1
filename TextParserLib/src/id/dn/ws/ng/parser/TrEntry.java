/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.dn.ws.ng.parser;

/**
 * Representation class of the RESPONSE parameter in registry
 * 
 * The RESPONSE parameters have the following format:
 * NAME;PATH;TYPE;PARENT
 * 
 * TYPE is one of Literal, Record, or Collection
 * Literal is default
 * Record means a map object, or an object that contains several map-value mapping
 * Collection means an array of objects: Literal or Record or even another Collection
 * 
 * The setup is mimicking kony fabric configuration for integration service
 * 
 * 
 * @author darryl.sulistyan
 */
public class TrEntry {
    private String name;
    private String path;
    private String type;
    private String parent;

    /**
     * 
     * 
     * @param name
     * @param path
     * @param type, if null or empty, Literal is assumed
     * @param parent, if null or empty, parent is equal to root TrEntry. The root TrEntry is made automatically
     */
    public TrEntry(String name, String path, String type, String parent) {
        this.name = name;
        this.path = path;
        
        if (type == null || "".equals(type.trim())) {
            this.type = "Literal"; // Literal, Record / Collection
        } else {
            this.type = type;
        }
        
        if (parent == null || "".equals(parent.trim())) {
            this.parent = "root";
        } else {
            this.parent = parent;
        }
        
        
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    /**
     * 
     * @return Literal, Record or Collection
     */
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getParent() {
        return parent;
    }

    public void setParent(String parent) {
        this.parent = parent;
    }
}
