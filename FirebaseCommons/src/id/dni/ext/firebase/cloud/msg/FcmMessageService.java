/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.dni.ext.firebase.cloud.msg;

import com.google.gson.Gson;
import id.dni.ext.firebase.cloud.msg.json.FcmMessageDownstreamResponseJson;
import id.dni.ext.firebase.cloud.msg.json.FcmMessageJson;
import id.dni.ext.firebase.cloud.msg.project.FcmProjectToken;
import id.dni.pvim.ext.web.in.Commons;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 *
 * @author darryl.sulistyan
 */
public class FcmMessageService {
    
    public FcmMessageDownstreamResponseJson sendSimpleNotification(
            FcmProjectToken projectToken, FcmMessageJson message, int timeout) 
            throws IOException {
        
        Gson gson = new Gson();
        
        URL url = new URL(projectToken.getFcmUrl());
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setDoOutput(true);
        conn.setRequestProperty("Content-Type", "application/json; charset=utf-8");
        conn.setRequestProperty("Authorization", "key=" + projectToken.getAuthTokenStr());
        conn.setConnectTimeout(timeout);
        conn.setReadTimeout(timeout);
        conn.setRequestMethod("POST");
        
        String jsonData = gson.toJson(message);

        try (BufferedWriter out = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream()))) {
            out.write(jsonData);
        }

        String returnData;
        try ( // Read the response
                InputStream inputStream = conn.getInputStream()) {
            returnData = Commons.inputStreamToString(inputStream);
        }

        return gson.fromJson(returnData, FcmMessageDownstreamResponseJson.class);
        
    }
    
}
