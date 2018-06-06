/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.dni.pv.ext.ws.dispatcher;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author darryl.sulistyan
 */
public class PvWsFactory {
    
    private static final PvWsFactory INSTANCE = new PvWsFactory();
    private final Map<String, IPvWsHandlerFactory> handlerMap;
    
    private PvWsFactory() {
        handlerMap = new HashMap<>();
        handlerMap.put("DeviceService/getCassetteCounters", new IPvWsHandlerFactory() {
            @Override
            public IPvWs create() {
                return new DeviceServicePvWsImpl();
            }
        });
    }
    
    public static PvWsFactory getInstance() {
        return INSTANCE;
    }
    
    public IPvWs getHandler(String ws) {
        IPvWsHandlerFactory factory = handlerMap.get(ws);
        if (factory == null) {
            return new DefaultPvWsImpl();
        } else {
            return factory.create();
        }
    }
    
}
