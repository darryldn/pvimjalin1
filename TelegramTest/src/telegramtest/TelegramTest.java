/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package telegramtest;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

/**
 *
 * @author darryl.sulistyan
 */
public class TelegramTest {

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
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws MalformedURLException, InterruptedException, IOException {
        String base = "https://api.telegram.org/bot580773855:AAE5vF2wyBH7juU4qHQzUtf2jMLlC6Q8lLk";
        
        for (int i=0; i<1000; ++i) {
            URL url = new URL(base + "/sendMessage?chat_id=580773855&text=message-" + i);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            String returnData;
            try ( // Read the response XML
                    InputStream inputStream = conn.getInputStream()) {
                returnData = inputStreamToString(inputStream);
            }
            
            System.out.println(returnData);
            
            Thread.sleep(50);
        }
    }
    
}
