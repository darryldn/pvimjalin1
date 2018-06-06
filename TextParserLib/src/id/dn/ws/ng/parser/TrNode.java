/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.dn.ws.ng.parser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author darryl.sulistyan
 */
class TrNode implements ITrNode {
    
    private ITrNode parent;
    private String name;
    private String path;
    private String type;
    private final List<ITrNode> children;
    
    public TrNode(ITrNode parent, String name, String path, String type) {
        this.parent = parent;
        this.name = name;
        this.path = path;
        this.type = type;
        children = new ArrayList<>();
    }

    @Override
    public List<ITrNode> getChildren() {
        return Collections.unmodifiableList(children);
    }
    
    List<ITrNode> getMutableChildren() {
        return children;
    }
    
    @Override
    public ITrNode getParent() {
        return parent;
    }

    /*public*/ void setParent(TrNode parent) {
        this.parent = parent;
    }

    @Override
    public String getName() {
        return name;
    }

    /*public*/ void setName(String name) {
        this.name = name;
    }

    @Override
    public String getPath() {
        return path;
    }

    /*public*/ void setPath(String path) {
        this.path = path;
    }

    @Override
    public String getType() {
        return type;
    }

    /*public*/ void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "TrNode{" + "parent=" + parent + ", name=" + name + ", path=" + path + ", type=" + type + '}';
    }
    
    
    
}
