/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.dni.pvim.ext.web.soap;

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
public class PVSoapHeaderHandler implements SOAPHandler<SOAPMessageContext> {

    private static final String 
               PV_NAMESPACE_WSSE = "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd",
               PV_NAMESPACE_WSU = "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd",
               PV_NAMESPACE_SOAPENV = "http://schemas.xmlsoap.org/soap/envelope/",
               PV_NAMESPACE_XSD = "http://www.w3.org/2001/XMLSchema",
               PV_NAMESPACE_XSI = "http://www.w3.org/2001/XMLSchema-instance",
               PV_NAMESPACE_RES = "http://soap.wincor-nixdorf.com/itmp/ResourceService";
    
    private final String username;
    private final String password;

    public PVSoapHeaderHandler(String username, String password) {
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
                    
                SOAPElement securityElem =
                        factory.createElement("Security", "wsse", PV_NAMESPACE_WSSE);
                securityElem.setAttributeNS(PV_NAMESPACE_SOAPENV, "mustUnderstand", "1");
                {
                    SOAPElement usernameTokenElem =
                            factory.createElement("UsernameToken", "wsse", PV_NAMESPACE_WSSE);
                    usernameTokenElem.setAttributeNS(PV_NAMESPACE_WSU, "Id", "UsernameToken-14050342");
                    {
                        SOAPElement usernameElem =
                                factory.createElement("Username", "wsse", PV_NAMESPACE_WSSE);
                        usernameElem.addTextNode(this.username);

                        SOAPElement passwordElem = 
                                factory.createElement("Password", "wsse", PV_NAMESPACE_WSSE);
                        passwordElem.setAttributeNS(PV_NAMESPACE_WSSE, "Type",
                            "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-" +
                                    "username-token-profile-1.0#PasswordText");
                        passwordElem.addTextNode(this.password);

                        usernameTokenElem.addChildElement(usernameElem);
                        usernameTokenElem.addChildElement(passwordElem);
                    }

                    securityElem.addChildElement(usernameTokenElem);
                }

                if (envelope.getHeader() != null) {
                envelope.getHeader().detachNode();
                }

                SOAPHeader header = envelope.addHeader();
                header.addChildElement(securityElem);
                
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
