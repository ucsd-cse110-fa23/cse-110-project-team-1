package Controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;

import org.json.JSONObject;

import Model.Recipe;

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

    /**
     * This method sends a POST request to the server with a file and a specific audio type.
     * The file is sent as an octet-stream and the audio type is sent as a header.
     *
     * @param urlString The URL of the server to which the POST request will be sent.
     * @param file The file to be sent to the server.
     * @param audioType The type of audio in the file, which can be either 'mealType' or 'ingredients'.
     *
     * @throws MalformedURLException If the provided urlString is not a valid URL.
     * @throws FileNotFoundException If the provided file does not exist.
     * @throws IOException If an I/O error occurs with the connection.
     */
    public void performPOST(String urlString, File file, String audioType) throws MalformedURLException, FileNotFoundException, IOException {
        try {
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "application/octet-stream");
            conn.setRequestProperty("Content-Length", String.valueOf(file.length()));
            conn.setRequestProperty("Audio-Type", audioType);
    
            OutputStream out = conn.getOutputStream();
            Files.copy(file.toPath(), out);
    
            out.flush();
            out.close();
    
            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String response = in.readLine();
                in.close();
                System.out.println(response);
            } else {
                System.out.println("Server returned non-OK code: " + responseCode);
            }
        } catch (MalformedURLException e) {
            System.out.println("Malformed URL: " + urlString);
        } catch (FileNotFoundException e) {
            System.out.println("File not found: " + file.getPath());
        } catch (IOException e) {
            System.out.println("Error communicating with server: " + e.getMessage());
        }
    }


    /**
     * This method sends a PUT request to the server with a recipe's details. If the recipe already exists on the server, it will be updated; 
     * if it does not exist, it will be created.
     *
     * @param urlString The URL of the server to which the PUT request will be sent.
     * @param recipeID The ID of the recipe to be updated or created.
     * @param recipeTitle The title of the recipe to be updated or created.
     * @param recipeText The text of the recipe to be updated or created.
     *
     */
    public void performPUT(String urlString, int recipeID, String recipeTitle, String recipeText) {
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
    
            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String response = in.readLine();
                in.close();
                System.out.println("Put response " + response);
            } else {
                System.out.println("Server returned non-OK code: " + responseCode);
            }
        } catch (MalformedURLException e) {
            System.out.println("Malformed URL: " + urlString);
        } catch (IOException e) {
            System.out.println("Error communicating with server: " + e.getMessage());
        }
    }


    /**
     * This method sends a DELETE request to the server to delete a specific recipe identified by its ID.
     * 
     * @param urlString The URL of the server to which the DELETE request will be sent
     * @param recipeID The ID of the recipe to be deleted.
     * 
     */
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
    
            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String response = in.readLine();
                in.close();
                System.out.println("Delete response " + response);
            } else {
                System.out.println("Server returned non-OK code: " + responseCode);
            }
        } catch (MalformedURLException e) {
            System.out.println("Malformed URL: " + urlString);
        } catch (IOException e) {
            System.out.println("Error communicating with server: " + e.getMessage());
        }
    }

    /**
     * This method sends a GET request to the server and retrieves all recipes as a JSON string.
     * It reads the JSON data from a single line from the URL.
     * The method uses a BufferedReader and a URLConnection to read data from the URL.
     * The approach for reading data from the URL is referenced from https://www.javatpoint.com/java-get-data-from-url.
     *
     * @param url The URL of the server from which the data will be retrieved.
     *
     * @return A JSON string containing all recipes. If an error occurs, it returns the string "Invalid".
     *
     */
    public String performGET(String url) {
        String content = "Invalid";
        try {
            URL urlObj = new URL(url); // creating a url object
            URLConnection urlConnection = urlObj.openConnection(); // creating a urlconnection object
            
            // wrapping the urlconnection in a bufferedreader
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            // reading from the urlconnection using the bufferedreader
            content = bufferedReader.readLine();
            bufferedReader.close();
        } catch (MalformedURLException e) {
            System.out.println("Malformed URL: " + url);
        } catch (IOException e) {
            System.out.println("Error communicating with server: " + e.getMessage());
        }
        
        return content;
    }

}
