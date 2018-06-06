/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.dn.ws.ng.parser;

import java.util.List;

/**
 *
 * @author darryl.sulistyan
 */
public interface ITrNode {
    
    /**
     * Returns child of this node.
     * If it has no child, it returns empty list
     * @return Unmodifiable list of children
     */
    public List<ITrNode> getChildren();
    
    public ITrNode getParent();
    
    public String getName();
    
    public String getPath();
    
    /**
     * Returns type of node
     * @return Literal, Record or Collection
     */
    public String getType();
    
}
