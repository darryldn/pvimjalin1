/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.dn.ws.ng.parser.gen;

import id.dn.ws.ng.TextParserException;
import id.dn.ws.ng.parser.ITrNode;
import id.dn.ws.ng.parser.ITrTree;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.Map;

/**
 *
 * @author darryl.sulistyan
 */
public class TrNop implements ITrTree {

    @Override
    public Map<String, Object> parseDoc(ITrNode rootNode, InputStream stream, 
            String mimeType, Charset charset) throws TextParserException {
        return Collections.EMPTY_MAP;
    }
    
}
