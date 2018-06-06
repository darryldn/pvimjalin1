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
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * Base abstract class for text parsers.
 * 
 * This class is used if the responseData is human readable text, like xml and json.
 * So, this class assumes that the InputStream can be parsed using a character-based stream
 * 
 * This class implements the parseDoc from InputStream and exposes a new abstract function
 * to be implemented with a String parameter and charset as source data.
 * 
 * @author darryl.sulistyan
 */
public abstract class TrTreeTextParser implements ITrTree {
    
    
    /**
     * Parses InputStream to a String object, encoded in contentType
     * 
     * @param stream
     * @return
     */
    @Override
    public Map<String, Object> parseDoc(ITrNode rootNode, InputStream stream, String mimeType, Charset charset) 
            throws TextParserException {
        Charset responseCharset = charset;
        String result = Util.convertStreamToString(stream, charset);
        Logger.getLogger(this.getClass().getName()).log(Level.FINE, result);
        return this.parseDoc(rootNode, result, responseCharset);
    }
    
    /**
     * Calls parseDoc with default machine charset.
     * 
     * @param source
     * @return
     * @throws WSException 
     */
    //public abstract Map<String, Object> parseDoc(String source) throws WSException;
    
    /**
     * Parses the source string into Map object.
     * 
     * @param rootNode
     * @param source, response data from server read from response body
     * @param charset, given from Content-Type header entry
     * @return
     * @throws id.dn.ws.ng.TextParserException
     */
    public abstract Map<String, Object> parseDoc(ITrNode rootNode, String source, Charset charset) throws TextParserException;
    
    
    
    
}
