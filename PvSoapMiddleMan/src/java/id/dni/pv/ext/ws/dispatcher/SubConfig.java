/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.dni.pv.ext.ws.dispatcher;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author darryl.sulistyan
 */
class DefaultWsConfig {
    
    private final String operationName;
    private final int connectTimeout;
    private final int requestTimeout;
    private final String url;
    private final String contentFileName;
    
    private static final String WSCONF_SUFFIX_URL = ".url";
    private static final String WSCONF_SUFFIX_TEMPLATE_FILE = ".template_file";
    private static final String WSCONF_SUFFIX_CONNECT_TIMEOUT = ".connect_timeout";
    private static final String WSCONF_SUFFIX_REQUEST_TIMEOUT = ".request_timeout";
    
    private static final String WSCONF_SUFFIX_OUTPUT = ".output";
    private static final String WSCONF_SUFFIX_ERROR = ".err";
    private static final String WSCONF_SUFFIX_OUTPUT_N = WSCONF_SUFFIX_OUTPUT + ".N";
    private static final String WSCONF_SUFFIX_ERROR_N = WSCONF_SUFFIX_ERROR + ".N";

    private final List<String> outputConfigList;
    private final List<String> errConfigList;
    
    public DefaultWsConfig(String operationName) {
        this.operationName = operationName;
        contentFileName = WsConfig.getInstance().get(operationName + WSCONF_SUFFIX_TEMPLATE_FILE);
        
        int tv;
        try {
            tv = Integer.parseInt(WsConfig.getInstance().get(operationName + WSCONF_SUFFIX_CONNECT_TIMEOUT));
        } catch (NumberFormatException ex) {
            tv = 4000;
        }
        this.connectTimeout = tv;
        
        try {
            tv = Integer.parseInt(WsConfig.getInstance().get(operationName + WSCONF_SUFFIX_REQUEST_TIMEOUT));
        } catch (NumberFormatException ex) {
            tv = 4000;
        }
        this.requestTimeout = tv;
        
        url = WsConfig.getInstance().get(operationName + WSCONF_SUFFIX_URL);
        //int nl;
        
        outputConfigList = new ArrayList<>();
        readConfigList(outputConfigList, operationName, WSCONF_SUFFIX_OUTPUT, WSCONF_SUFFIX_OUTPUT_N);
        
        errConfigList = new ArrayList<>();
        readConfigList(errConfigList, operationName, WSCONF_SUFFIX_ERROR, WSCONF_SUFFIX_ERROR_N);
        
    }
    
    private void readConfigList(List<String> out, String operationName, String suffix, String suffixN) {
        int nl;
        try {
            nl = Integer.parseInt(WsConfig.getInstance().get(operationName + suffixN));
        } catch (NumberFormatException ex) {
            nl = 0;
        }
        for (int i=0; i<nl; ++i) {
            String configKey = String.format("%s%s.%d", operationName, suffix, i);
            String config = WsConfig.getInstance().get(configKey);
            
            out.add(config);
        }
    }
    
    public int getConnectTimeout() {
        return connectTimeout;
    }

    public int getRequestTimeout() {
        return requestTimeout;
    }

    public String getUrl() {
        return url;
    }

    public String getContentFileName() {
        return contentFileName;
    }

    public String getOperationName() {
        return operationName;
    }

    public List<String> getOutputConfigList() {
        return outputConfigList;
    }

    public List<String> getErrConfigList() {
        return errConfigList;
    }
    
    
    
    
    
    
}
