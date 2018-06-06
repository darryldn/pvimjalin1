/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.dn.ws.ng.parser;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author darryl.sulistyan
 */
public class TrTreeBuilder {
    
    private final TrNode root;
    private final Map<String, TrNode> map;
    private List<TrEntry> entries;
    
    private TrTreeBuilder() {
        root = new TrNode(null, "root", "/", "Record");
        map = new HashMap<>();
        map.put("root", root);
    }
    
    public static TrTreeBuilder createBuilder() {
        return new TrTreeBuilder();
    }
    
    public TrTreeBuilder setEntries(List<TrEntry> entries) {
        this.entries = entries;
        return this;
    }
    
    private ITrNode getRoot() {
        return this.root;
    }
    
    /**
     * Builds tree and get root
     * @return 
     */
    public ITrNode build() {
        Logger.getLogger(this.getClass().getName()).log(Level.FINE, ">> construct({0})", entries);
        
        for (TrEntry entry : entries) {
            
            TrNode en = map.get(entry.getName());
            if (en == null) {
                
                String parentName;
                if (entry.getParent() == null || "".equals(entry.getParent().trim())) {
                    parentName = "root";
                } else {
                    parentName = entry.getParent();
                }
                TrNode parent = map.get(parentName);
                en = new TrNode(parent, entry.getName(), entry.getPath(), entry.getType());
                
                map.put(en.getName(), en);
            }
            
            TrNode parentNode = (TrNode)en.getParent();
            parentNode.getMutableChildren().add(en);
            
        }
        
        for (ITrNode n : this.getRoot().getChildren()) {
             Logger.getLogger(this.getClass().getName()).log(Level.FINE, "child of root: {0}", n);
        }
        
        Logger.getLogger(this.getClass().getName()).log(Level.FINE, "<< construct()");
        
        return this.getRoot();
    }
    
}
