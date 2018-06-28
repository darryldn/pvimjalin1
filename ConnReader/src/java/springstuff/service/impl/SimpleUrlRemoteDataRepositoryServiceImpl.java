/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package springstuff.service.impl;

import com.google.gson.Gson;
import id.dni.pvim.ext.web.in.Commons;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import springstuff.exceptions.RemoteRepositoryException;
import springstuff.json.DeviceComponentStateJson;
import springstuff.service.RemoteDataRepositoryService;

/**
 *
 * @author darryl.sulistyan
 */
@Service
@Qualifier("simpleurlRemoteDataRepositoryService")
public class SimpleUrlRemoteDataRepositoryServiceImpl implements RemoteDataRepositoryService {

    private String parseServerUrl;
    private String parseServerTimeout;

    @Value("${device.component.parse_server_url}")
    public void setParseServerUrl(String parseServerUrl) {
        this.parseServerUrl = parseServerUrl;
    }

    @Value("${device.component.parse_server_url.timeout}")
    public void setParseServerTimeout(String parseServerTimeout) {
        this.parseServerTimeout = parseServerTimeout;
    }

    @Override
    public void send(List<DeviceComponentStateJson> devices) throws RemoteRepositoryException {
        try {
            Gson gson = new Gson();
            String json = gson.toJson(devices);
            
            Logger.getLogger(this.getClass().getName()).log(Level.INFO, " - Send request to parse url: {0}", parseServerUrl);
            Logger.getLogger(this.getClass().getName()).log(Level.INFO, " - Sending request body: {0}", json);
            
            URL url = new URL(parseServerUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "application/json; charset=utf-8");
            int timeout;
            try {
                timeout = Integer.parseInt(parseServerTimeout, 10);
            } catch (NumberFormatException ex) {
                timeout = 3000;
            }
            conn.setConnectTimeout(timeout);
            conn.setReadTimeout(timeout);
            conn.setRequestMethod("POST");
            try (BufferedWriter out = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream()))) {
                out.write(json);
            }
            
            String returnData;
            try ( // Read the response
                    InputStream inputStream = conn.getInputStream()) {
                returnData = Commons.inputStreamToString(inputStream);
            }
            
            Logger.getLogger(this.getClass().getName()).log(Level.INFO,
                    " - obtain request from parse server: {0}", returnData);
        } catch (MalformedURLException ex) {
            throw new RemoteRepositoryException(ex);
        } catch (IOException ex) {
            throw new RemoteRepositoryException(ex);
        }
    }

}
