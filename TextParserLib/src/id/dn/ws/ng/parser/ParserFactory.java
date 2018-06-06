/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.dn.ws.ng.parser;

import id.dn.ws.ng.parser.gen.TrNop;
import id.dn.ws.ng.parser.json.TrJsonTree;
import id.dn.ws.ng.parser.xml.TrXMLTree;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author darryl.sulistyan
 */
public class ParserFactory {
    
    private static final ParserFactory ME = new ParserFactory();
    
    private final Map<String, ITrTreeFactory> parser;
    private final ITrTree NOP_TREE = new TrNop();
    
    private ParserFactory() {
        parser = new HashMap<>();
        
        // default implementations
        ITrTreeFactory xmlParser = new ITrTreeFactory() {
            @Override
            public ITrTree create() {
                return new TrXMLTree();
            }
        };
        
        // default implementations
        ITrTreeFactory jsonParser = new ITrTreeFactory() {
            @Override
            public ITrTree create() {
                return new TrJsonTree();
            }
        };
        
        parser.put("application/xml", xmlParser);
        parser.put("text/xml", xmlParser);
        parser.put("application/json", jsonParser);
        
    }
    
    public static ParserFactory getInstance() {
        return ME;
    }
    
    /**
     * do this during app init.
     * @param mimeType, taken from ContentType class from apache httpclient
     * @param clazz 
     */
    public synchronized void setParserFactory(String mimeType, ITrTreeFactory clazz) {
        parser.put(mimeType, clazz);
    }
    
    
    public synchronized ITrTreeFactory getParserFactory(String mimeType) {
        return parser.get(mimeType);
    }
    
    public ITrTree createParser(String mimeType) {
        ITrTreeFactory cls = this.getParserFactory(mimeType);
        if (cls != null) {
            return cls.create();
        }
        return null;
    }
    
}
