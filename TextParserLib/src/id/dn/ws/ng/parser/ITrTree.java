/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.dn.ws.ng.parser;

import id.dn.ws.ng.TextParserException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Map;

/**
 * Interface to describe specification of the parser to parse data from server
 * 
 * @author darryl.sulistyan
 */
public interface ITrTree {
    
    /**
     * Initialization step. The system will read all RESPONSE parameters from registry, put in a list
     * and call this function.
     * 
     * @param entries, registry entries under [wsName\\RESPONSE] key registry entry.
     */
    //public void construct(List<TrEntry> entries);
    
    /**
     * 
     * @param rootNode
     * @param stream, InputStream that exposes the responseData from server, as stream
     * @param mimeType
     * @param charset
     * 
     * @return
     * @throws id.dn.ws.ng.TextParserException
     */
    public Map<String, Object> parseDoc(ITrNode rootNode, InputStream stream, 
            String mimeType, Charset charset) throws TextParserException;
    
    /**
     * Calls parseDoc with default machine charset.
     * 
     * @param source
     * @return
     * @throws WSException 
     */
    //public Map<String, Object> parseDoc(String source) throws WSException;
    
    /**
     * Parses the source string into Map object.
     * 
     * @param source, response data from server read from response body
     * @param charset, given from Content-Type header entry
     * @return
     * @throws WSException 
     */
    //public Map<String, Object> parseDoc(String source, Charset charset) throws WSException;
    
}
