package Controller;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import org.json.JSONObject;

public class RequestHandler {

    /*
     * This is used to send the audio to the server
     * 
     */
    public void performPOST(String urlString, String body){
        try {
            URL url = new URL(urlString);
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
     * Sends a recipe to the server which will edit if already existing or create it if not
     * 
     * 
     */
    public void performPUT(String urlString, int recipeID, String recipeTitle, String recipeText){
    
        try {
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("PUT");
            conn.setDoOutput(true);
            OutputStreamWriter out = new OutputStreamWriter(conn.getOutputStream());

            JSONObject requestBody = new JSONObject();
            requestBody.put("newRecipeText", recipeText);
            requestBody.put("newRecipeTitle", recipeTitle);
            requestBody.put("recipeID", recipeID);
            String body = requestBody.toString();
            out.write(body);
            out.flush();
            out.close(); 

            
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String response = in.readLine();
            in.close();
            System.out.println("Put response " + response);
        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    public void performDELETE(String urlString, int recipeID){
    
        try {
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("DELETE");
            conn.setDoOutput(true);
            OutputStreamWriter out = new OutputStreamWriter(conn.getOutputStream());

            String body = "recipeID="+recipeID;
            out.write(body);
            out.flush();
            out.close(); 

            
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String response = in.readLine();
            in.close();
            System.out.println("Delete response " + response);
        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    /*
     * This gets all recipes and returns them as a json string
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
