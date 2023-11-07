package Controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;

public class RequestHandler {
    public void performPOST(String body){
        try {
            String urlString = "http://localhost:8100/";
            URL url = new URI(urlString).toURL();
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            OutputStreamWriter out = new OutputStreamWriter(conn.getOutputStream());
            out.write(body);
            out.flush();
            out.close(); 

            
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String response = in.readLine();
            in.close();
            System.out.println(response);
        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    /*
     * Gets json data from single line from url
     * buffered reader and url connection referenced from
     * https://www.javatpoint.com/java-get-data-from-url
     */
    public String performGET(String url){
        String content = "Invalid";
        try {
            URL urlObj = new URL(url); // creating a url object
            URLConnection urlConnection = urlObj.openConnection(); // creating a urlconnection object
            
            // wrapping the urlconnection in a bufferedreader
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            // reading from the urlconnection using the bufferedreader
            content = bufferedReader.readLine();
            bufferedReader.close();
            return content;
        } catch (Exception e) {
            // TODO: handle exception
        }
        
        return content;
    }

}
