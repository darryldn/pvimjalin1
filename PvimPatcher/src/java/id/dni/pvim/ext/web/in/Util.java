/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.dni.pvim.ext.web.in;

import com.google.gson.Gson;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author darryl.sulistyan
 */
public class Util {
    
    private static final Gson GSON = new Gson();
    
    public static boolean isEmptyStr(String s) {
        return s == null || s.equals("");
    }

    public static boolean isEmptyStrIgnoreSpaces(String s) {
        return s == null || isEmptyStr(s.trim());
    }
    
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
        HttpServletResponse response, 
        Object obj) throws IOException {

        response.setContentType("application/json;charset=UTF-8");

        String res = GSON.toJson(obj);

        try (PrintWriter out = response.getWriter()) {
            out.print(res);
            out.flush();
        }
    }
//    
//    /**
//     * Convert ProView XML Error stream to OperationError object.
//     * Don't use this for ProView IM XML Error String! Different object!
//     * @param xmlError
//     * @return 
//     */
//    public static OperationError pvXmlErrStrToOperationError(String xmlError) {
//        OperationError err = new OperationError();
//        // Parse xml string from xml error string for ProView service here!
//        return err;
//    }
}
