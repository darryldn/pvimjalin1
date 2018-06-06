/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.dn.ws.ng.parser.json;

/*
Issue: 
1. JSON string is not parsed correctly to their good data types, especially
numbers. GSON will automatically convert any number into Double data type.
Must specify hints, but since hints are impossible for this scenario, must use
some other library, or some other method, like JsonParser, also from GSON, but
more low level

*/

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import id.dn.ws.ng.TextParserException;
import id.dn.ws.ng.parser.ITrNode;
import id.dn.ws.ng.parser.TrTreeTextParser;
import id.dn.ws.ng.parser.xml.TrXMLTree;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Implements json parser.
 * 
 * Basically by converting json to xml document, and analyse it by xml using XPath.
 * 
 * Root node is named <root> when converting json to XML
 * json = {
 *  "test1" : "xxxssss",
 * "test2" : "eeeee",
 * "array" : [
 * {
 * "obj1" : "rrrr"
 * },
 * {
 * "obj1" : "ssss"
 * }
 * ]
 * }
 * 
 * becomes:
 * <root>
 * <test1>xxxssss</test1>
 * <test2>eeeee</test2>
 * <array>
 * <obj1>rrrr</obj1>
 * </array>
 *  <array>
 * <obj1>ssss</obj1>
 * </array>
 * </root>
 * 
 * if the json is a JSON Array, two new elements are created: root0 is hardcoded. It won't become root1, 2, or whatever
 * 
 * <root0>
 *  <root>
 *      ...
 *  </root>
 * <root>
 *      ...
 *  </root>
 * ...
 * <root>
 *      ...
 *  </root>
 * </root0>
 * 
 * No other new elements are created.
 * 
 * @author darryl.sulistyan
 */
public class TrJsonTree extends TrTreeTextParser {

    //private final TrJsonUsingXMLTree jsonViaXmlTree;
    private final TrXMLTree xmlTree;
    
    public TrJsonTree() {
        xmlTree = new TrXMLTree();
        //jsonViaXmlTree = new TrJsonUsingXMLTree();
    }
    
    private String json2xml(String jsonDoc, Charset charset) throws TextParserException {
        //GsonBuilder gsonBuilder = new GsonBuilder();
        //gsonBuilder.setLongSerializationPolicy( LongSerializationPolicy.STRING );
        //Gson gson = gsonBuilder.create();
        
        JsonParser parser = new JsonParser();
        JsonElement asdf = parser.parse(jsonDoc);
        
        //Object asdf = gson.fromJson(jsonDoc, Object.class);
        

        //Random r = new Random();
        String rootTag = "root";
        String root0 = rootTag + "0";

        String topXml = "<?xml version=\"1.0\" encoding=\"" + charset.toString() + "\"?>";

        // Add a wrapper element root0 to wrap the rootTag element array
        // if JSON object is an array, it is nameless to begin with. User will have to
        // find out how.
        if (asdf.isJsonArray()) {
            return topXml + "\n<" + root0 + ">" + _json2xml(rootTag, asdf) + "</" + root0 + ">";

        } else if (asdf.isJsonObject()) {
            return topXml + "\n" + _json2xml(rootTag, asdf);

        } else {
            return topXml + "\n" + _json2xml(rootTag, asdf);

        }
    }

    private String _json2xml(String elem, JsonElement whats) throws TextParserException {

        if (whats == null || whats.isJsonNull()) {
            StringBuilder sb = new StringBuilder();
            sb.append("<").append(elem).append("/>");
            return sb.toString();
        }
        
        if (whats.isJsonObject()) {
            //Map mw  = (Map) whats;
            JsonObject mw = (JsonObject) whats;
            StringBuilder sb = new StringBuilder();

            sb.append("<").append(elem).append(">");
            for (Map.Entry<String, JsonElement> o : mw.entrySet()) {
                Map.Entry<String, JsonElement> e = (Map.Entry<String, JsonElement>)o;
                String k = (String)e.getKey();
                JsonElement v = e.getValue();
                sb.append(_json2xml(k, v));
            }
            sb.append("</").append(elem).append(">");

            return sb.toString();

        } else if (whats.isJsonArray()) {
            //List lw = (List) whats;
            JsonArray lw = (JsonArray) whats;
            StringBuilder sb = new StringBuilder();
            for (JsonElement o : lw) {
                sb.append(_json2xml(elem, o));
            }
            return sb.toString();

        } else if (whats.isJsonPrimitive()) {
            // assume literal
            StringBuilder sb = new StringBuilder();
            
            sb.append("<").append(elem).append(">").append("<![CDATA[");
            sb.append(whats.getAsString());
            sb.append("]]>").append("</").append(elem).append(">");

            return sb.toString();
            
        } else {
            // unknown type.
            throw new TextParserException(new UnsupportedOperationException("Unable to parse json: " + whats));
            
        }

    }
    
    @Override
    public Map<String, Object> parseDoc(ITrNode rootNode, String jsonDoc, Charset charset) throws TextParserException {

        Logger.getLogger(this.getClass().getName()).log(Level.FINE, ">> parseDoc({0})", jsonDoc);
        Logger.getLogger(this.getClass().getName()).log(Level.FINE, " - Using charset {0}: ", charset);

        String xmlDoc = json2xml(jsonDoc, charset);

        Logger.getLogger(this.getClass().getName()).log(Level.FINE, " - json to xml:\n{0}", xmlDoc);

        Map m = xmlTree.parseDoc(rootNode, xmlDoc, charset);
        Logger.getLogger(this.getClass().getName()).log(Level.FINE, "<< parseDoc(): returns:\n{0}", m);

        return m;

    }
    
//    @Override
//    public Map<String, Object> parseDoc(String jsonDoc) throws TextParserException {
//        return this.parseDoc(jsonDoc, Charset.defaultCharset());
//    }

//    @Override
//    public void construct(List<TrEntry> entries) {
//        xmlTree.construct(entries);
//    }

    //@Override
//    public Map<String, Object> parseDoc(String jsonDoc, Charset charset) throws TextParserException {
//        TRACER.TrcWriteStr(C.Trace.TRC_BIT_FUNCENTRY, ">> parseDoc(" + jsonDoc + ")");
//        TRACER.TrcWriteStr(C.Trace.TRC_BIT_INFO, " - Using charset: " + charset);
//        
//        String xmlDoc = json2xml(jsonDoc, charset);
//        
//        TRACER.TrcWriteStr(C.Trace.TRC_BIT_INFO, " - json to xml:\n" + xmlDoc);
//        
//        Map m = xmlTree.parseDoc(xmlDoc, charset);
//        TRACER.TrcWriteStr(C.Trace.TRC_BIT_FUNCENTRY, "<< parseDoc(): returns:\n" + m);
//        
//        return m;
//    }
    
}
