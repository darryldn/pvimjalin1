/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.dni.pvim.ext.web.soap;

import com.wn.econnect.inbound.wsi.ticket.ITicketWebService;
import java.util.HashMap;
import java.util.Map;
import javax.xml.ws.Service;

/**
 *
 * @author darryl.sulistyan
 */
public class PVIMWSServiceRegistry {
    
    private static final PVIMWSServiceRegistry INSTANCE = new PVIMWSServiceRegistry();
    private final Map<String, Service> serviceMap = new HashMap<>();
    
    private PVIMWSServiceRegistry() {
        
    }
    
    public static PVIMWSServiceRegistry getInstance() {
        return INSTANCE;
    }
    
    public Service getService(String id) {
        return serviceMap.get(id);
    }
    
    public ITicketWebService getTicketWebService() {
        ITicketWebService service = (ITicketWebService) serviceMap.get(ITicketWebService.class.getName());
//        if (service == null) {
//            try {
//                URL baseUrl;
//                baseUrl = PVIMWSServiceRegistry.class.getResource("/");
//                URL url = new URL(baseUrl, "../wsdl/TicketService.wsdl");
//                
//                // locates the wsdl on ourselves!
//                service = new ITicketWebService(url);
//                putService(service);
//            } catch (MalformedURLException ex) {
//                Logger.getLogger(PVIMWSServiceRegistry.class.getName()).log(Level.SEVERE, null, ex);
//            }            
//        }
//        
        return service;
    }
    
    public void putService(String id, Service service) {
        serviceMap.put(id, service);
    }
    
    /**
     * Puts service in registry. Key is service classname, automatically
     * @param service 
     */
    public void putService(Service service) {
        putService(service.getClass().getName(), service);
    }
    
}
