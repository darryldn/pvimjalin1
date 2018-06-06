/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.dn.ws.ng.parser;

import id.dn.ws.ng.TextParserException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author darryl.sulistyan
 */
class Util {
    public static String convertStreamToString(InputStream stream, Charset responseCharset) throws TextParserException {
        //Charset responseCharset = contentType.getCharset();
        BufferedReader rd = null;
        StringBuilder result = new StringBuilder();
        
        try {
            if (responseCharset == null) {
                responseCharset = Charset.defaultCharset();
            }
            rd = new BufferedReader(new InputStreamReader(stream, responseCharset));    
            String line;
            while ((line = rd.readLine()) != null) {
                result.append(line);
            }
        } catch (IOException ex) {
            Logger.getLogger(Util.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
            throw new TextParserException(ex);

        } finally {
            if (rd != null) {
                try {
                    rd.close();
                } catch (IOException ex) {
                }
            }
        }
        
        return result.toString();
    }
}
