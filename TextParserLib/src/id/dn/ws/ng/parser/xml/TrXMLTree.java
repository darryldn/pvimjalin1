/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.dn.ws.ng.parser.xml;

import id.dn.ws.ng.TextParserException;
import id.dn.ws.ng.parser.ITrNode;
import id.dn.ws.ng.parser.TrTreeTextParser;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 *
 * @author darryl.sulistyan
 */
public class TrXMLTree extends TrTreeTextParser {
    
    @Override
    public Map<String, Object> parseDoc(ITrNode rootNode, String xmlDoc, Charset charset) throws TextParserException {
        
        Logger.getLogger(this.getClass().getName()).log(Level.FINE, ">> TrXMLTree::parseDoc()");
        
        try {
            //TrNode rootNode = TrTreeBuilder.createBuilder().setEntries(entries).build();
            
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            InputStream stream = new ByteArrayInputStream(xmlDoc.getBytes());
            InputStreamReader reader = new InputStreamReader(stream, charset);
            InputSource src = new InputSource(reader);
            src.setEncoding(charset.toString());
            
            Document doc = builder.parse(src);
            XPathFactory xPathfactory = XPathFactory.newInstance();
            XPath xpath = xPathfactory.newXPath();

            Logger.getLogger(this.getClass().getName()).log(Level.FINE, "root={0}", rootNode);
            
            Map<String, Object> rt = new HashMap<>();
            for (ITrNode ch : rootNode.getChildren()) {
                Logger.getLogger(this.getClass().getName()).log(Level.FINE, " - extracting TrNode ch={0}", ch);
                Object r = _parseDoc(ch, doc, xpath);
                
                Logger.getLogger(this.getClass().getName()).log(Level.FINE, " - obtained object r={0}", r);
                rt.put(ch.getName(), r);
            }

            //String ret = GSON.toJson(rt);
            //return ret;
            return rt;
            //_dumpDoc(rt);
            
        } catch (IOException | ParserConfigurationException | XPathExpressionException | SAXException e) {
            throw new TextParserException(e);
            
        } finally {
            Logger.getLogger(this.getClass().getName()).log(Level.FINE, "<< TrXMLTree::parseDoc()");
            
        }
    }
    
    private Object _parseDoc(ITrNode node, Node xmlNode, XPath xpath) throws XPathExpressionException {
        
        String xpathStr = node.getPath();
        XPathExpression expr2;
        try {
            expr2 = xpath.compile(xpathStr);
        } catch (XPathExpressionException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, ex.getMessage(), ex);
            throw ex;
        }
        
        if (null == node.getType()) {
            return "";
            
        } else switch (node.getType()) {
            case "Literal": {
                // no children, assume literal
                
                String gv = (String) expr2.evaluate(xmlNode, XPathConstants.STRING);
                //gv = xmlNode.getNodeValue();
                return gv;
                //eturn xmlNode.getNodeValue();
            }
                
            case "Record":
            {
                NodeList nl = (NodeList) expr2.evaluate(xmlNode, XPathConstants.NODESET);
                int nodeLen = nl.getLength();
                Node xmlChildNode = null;
                Map<String, Object> chd = new HashMap<>();
                for (int i=0; i<nodeLen; ++i) { // just take first entry
                    xmlChildNode = nl.item(i);
                    break;
                }
                if (xmlChildNode != null) {
                    for (ITrNode child : node.getChildren()) {
                        Object pp = _parseDoc(child, xmlChildNode, xpath);
                        //String gv = (String) expr2.evaluate(xmlChildNode, XPathConstants.STRING);
                        chd.put(child.getName(), pp);
                    }
                }
                return chd;
            }
            
            case "Collection":
            {
                NodeList nl = (NodeList) expr2.evaluate(xmlNode, XPathConstants.NODESET);
                int nodeLen = nl.getLength();
                //Map<String, Object> mo = new HashMap<>();
                List<Object> list = new ArrayList<>();
                for (int i=0; i<nodeLen; ++i) {
                    
                    Map<String, Object> wow = new HashMap<>();
                    for (ITrNode ch : node.getChildren()) {
                        Object pp = (_parseDoc(ch, nl.item(i), xpath));
                        wow.put(ch.getName(), pp);
                    }
                    list.add(wow);
                }
                //mo.put(node.getName(), list);
                return list;
            }
            default:
                return "";
        }
        
    }
    
}
