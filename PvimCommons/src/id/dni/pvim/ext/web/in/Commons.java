/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.dni.pvim.ext.web.in;

import com.google.gson.Gson;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Properties;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author darryl.sulistyan
 */
public class Commons {
//    private static final Gson GSON = new Gson();
    
    /**
     * Creates a random UUID with length 32 characters
     * @return 
     */
    public static String randomUUID() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }
    
    public static boolean isEmptyStr(String s) {
        return s == null || s.equals("");
    }
    
    /**
     * Creates ?,?,?,...,?
     * @param n number of question marks
     * @return 
     */
    public static String concatQuestionMarks(int n) {
        return stringJoinMultiply(n, "?", ",");
    }
    
    /**
     * Creates String C,C,C,...,C
     * @param n is the number of C
     * @param C a character to multiply
     * @return if n == 0, empty string.
     */
    public static String stringCharJoinMultiply(int n, String C) {
        return stringJoinMultiply(n, C, ",");
    }
    
    /**
     * Creates String CsepCsepCsep...C
     * @param n number of C
     * @param C 
     * @param sep separator between Cs.
     * @return 
     */
    public static String stringJoinMultiply(int n, String C, String sep) {
        StringBuilder sb = new StringBuilder();
        if (n > 0) {
            sb.append(C);
            for (int i=1; i<n; ++i) {
                sb.append(sep).append(C);
            }
        }
        return sb.toString();
    }

    public static boolean isEmptyStrIgnoreSpaces(String s) {
        return s == null || isEmptyStr(s.trim());
    }
    
    /**
     * Checks request path from request.getPathInfo()
     * @param requestPath from request.getPathInfo()
     * @param length the depth of valid request
     * @return null if the depth is not satisfied. else, requestPath, split into
     * parts divided by "/"
     */
    public static String[] getRequestPath(String requestPath, int length) {
        if (requestPath == null || requestPath.equals("/")) {
            return null;
        }

        String[] requestSplits = requestPath.split("/");
        if (requestSplits.length < length) {
            return null;
        }
        
        return requestSplits;
    }
    
    /**
     * Assumed charset: UTF8
     * @param inputStream
     * @return
     * @throws IOException 
     */
    public static String inputStreamToString(InputStream inputStream) throws IOException {
        try(ByteArrayOutputStream result = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) != -1) {
                result.write(buffer, 0, length);
            }

            return result.toString(StandardCharsets.UTF_8.name());
        }
    }
    
    public static void sendAsJson(
        PrintWriter out, 
        Object obj) throws IOException {
        Gson gson = new Gson();
        String res = gson.toJson(obj);
        out.print(res);
        out.flush();
    }
    
    public static String postJsonRequest(String urlStr, int timeout, String jsonData) throws IOException {
        try {
            
            Logger.getLogger(Commons.class.getName()).log(Level.INFO, " - Send request to url: {0}", urlStr);
            Logger.getLogger(Commons.class.getName()).log(Level.INFO, " - Sending request body: {0}", jsonData);
            
            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "application/json; charset=utf-8");
            conn.setConnectTimeout(timeout);
            conn.setReadTimeout(timeout);
            conn.setRequestMethod("POST");
            try (BufferedWriter out = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream()))) {
                out.write(jsonData);
            }
            
            String returnData;
            try ( // Read the response
                    InputStream inputStream = conn.getInputStream()) {
                returnData = Commons.inputStreamToString(inputStream);
            }
            
            Logger.getLogger(Commons.class.getName()).log(Level.INFO,
                    " - obtain request from parse server: {0}", returnData);
            return returnData;
            
        } catch (MalformedURLException ex) {
            throw ex;
        } catch (IOException ex) {
            throw ex;
        }
    }
    
    public static Properties loadConfig(Class<?> owner, String source) throws IOException {
        InputStream resourceAsStream = owner.getResourceAsStream(source);
        Properties prop = null;
        try {
            prop = new Properties();
            prop.load(resourceAsStream);
            Logger.getLogger(Commons.class.getName()).log(Level.INFO, "Read properties: {0}", prop);
            
        } catch (IOException ex) {
            Logger.getLogger(Commons.class.getName()).log(Level.SEVERE, null, ex);
            throw ex;
            
        } finally {
            try {
                resourceAsStream.close();
            } catch (IOException ex) {
            }
        }
        return prop;
    }
    
    public static interface IReplaceFWVarCallback {
        
        /**
         * Called whenever a replacement event is found in string.
         * @param val
         * @return null if val has no replacement, the original string will be left alone.
         */
        public String replace(String val);
    }
    
    /**
     * Replacing strings of format [#CCVARFW_BLAH#] to the contents of variable
     * CCVARFW_BLAH in CCVarFW. If the variable does not exist, replaced by empty string
     * 
     * This does not support subexpressions. Expressions like
     * [#CCVARFW_[#CCVARFW_DYN#]#] is not supported and will be interpreted as
     * [#CCVARFW_[#CCVARFW_DYN#]
     * 
     * This also only supports variables with maximum content length 4096 bytes
     * 
     * @param str
     * @param callback, when a match str is found
     * @return 
     */
    public static String replaceFWVars(String str, IReplaceFWVarCallback callback) {
        Logger.getLogger(Commons.class.getName()).log(Level.FINE, String.format(">> Util::replaceFWVars(%s)", str));
        
        String strRes = null;
        
        try {
        
            if (isEmptyStr(str)) {
                return strRes = str;
            }
            if (callback == null) {
                throw new IllegalArgumentException("callback may not be NULL!");
            }

            // split in java uses regex. \\. ; \\+ 
            // a;d;v;;;;;;;;;;;;;;;M
            // a, d, v, , , , ,
            String[] sp = str.split("\\[#");
            StringBuilder sb = new StringBuilder();

            for (int i=0; i<sp.length; ++i) {

                Logger.getLogger(Commons.class.getName()).log(Level.FINE, String.format(" - sp[%d] = [%s]", i, sp[i]));

                int ei = sp[i].indexOf("#]");

                //System.out.println("sp[i]=" + sp[i] + " ei=" + ei);

                Logger.getLogger(Commons.class.getName()).log(Level.FINE, String.format(" - ei = %d", ei));

                if (ei >= 0) {
                    String var = sp[i].substring(0, ei);

                    Logger.getLogger(Commons.class.getName()).log(Level.FINE, String.format(" - var = [%s]", var));

                    String repl = callback.replace(var);
                    if (repl == null) {
                        
                        // sp[i] already contains enclosing #];
                        sb.append("[#").append(sp[i]);
                    } else {
                        
                        // bypass the enclosing #] of sp[i] and continue.
                        sb.append(repl).append(sp[i].substring(ei+2));
                    }

                } else {
                    // not found
                    sb.append(sp[i]);

                }
            }

            return strRes = sb.toString();
        
        } finally {
            Logger.getLogger(Commons.class.getName()).log(Level.FINE, String.format("<< Util::replaceFWVars(): returns [%s]", strRes));
            
        }
    }
}
