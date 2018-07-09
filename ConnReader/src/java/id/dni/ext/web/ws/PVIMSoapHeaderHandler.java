/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.dni.ext.web.ws;

import java.util.Set;
import java.util.TreeSet;

import javax.xml.namespace.QName;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPFactory;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPMessage;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;

/**
 *
 * @author darryl.sulistyan
 */
public class PVIMSoapHeaderHandler implements SOAPHandler<SOAPMessageContext> {

    private static final String PVIM_NAMESPACE = "http://wsi.inbound.econnect.wn.com";
    
    private final String username;
    private final String password;

    public PVIMSoapHeaderHandler(String username, String password) {
        this.username = username;
        this.password = password;
    }

    @Override
    public boolean handleMessage(SOAPMessageContext context) {
        Boolean outboundProperty =
                (Boolean) context.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);
        if (outboundProperty.booleanValue()) {
            
            SOAPMessage msg = context.getMessage();
            
            try {
                SOAPEnvelope envelope = context.getMessage().getSOAPPart().getEnvelope();
                SOAPFactory factory = SOAPFactory.newInstance();
                
                String prefix = "dni";
                String uri = PVIM_NAMESPACE;
                
                SOAPElement securityElem =
                        factory.createElement("AuthenticationToken", prefix, uri);
                
                SOAPElement usernameElem = 
                        factory.createElement("Accountname", prefix, uri);
                usernameElem.addTextNode(this.username);
                
                SOAPElement passwordElem = 
                        factory.createElement("Password", prefix, uri);
                passwordElem.addTextNode(this.password);
                
                securityElem.addChildElement(usernameElem);
                securityElem.addChildElement(passwordElem);
                
                if (envelope.getHeader() != null) {
                    envelope.getHeader().detachNode();
                }
                
                SOAPHeader header = envelope.addHeader();
                header.addChildElement(securityElem);
                
                msg.writeTo(System.out);
                
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            // inbound
        }
        return true;
    }

    @Override
    public Set<QName> getHeaders() {
        return new TreeSet();
    }

    @Override
    public boolean handleFault(SOAPMessageContext context) {
        return true;
    }

    @Override
    public void close(MessageContext context) {
        //
    }
}