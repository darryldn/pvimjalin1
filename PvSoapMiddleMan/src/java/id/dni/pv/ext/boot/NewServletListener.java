/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.dni.pv.ext.boot;

import id.dni.pv.ext.ws.dispatcher.InputTemplateCache;
import id.dni.pv.ext.ws.dispatcher.WsConfig;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * Web application lifecycle listener.
 *
 * @author darryl.sulistyan
 */
public class NewServletListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        WsConfig.getInstance().init();
        InputTemplateCache.getInstance().init();
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        // does nothing
    }
}
