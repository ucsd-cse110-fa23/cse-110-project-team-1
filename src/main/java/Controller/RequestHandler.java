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
import java.net.URL;
import java.nio.file.Files;
import View.*;
import org.json.JSONObject;

public class RequestHandler {

    /**
     * Sends a POST request to the specified server with a file and specific audio type.
     * The file is sent as an octet-stream and the audio type is sent as a header.
     *
     * @param urlString The URL of the server where the POST request will be sent.
     * @param file The file to be sent to the server.
     * @param audioType The type of audio in the file, which can be either 'mealType' or 'ingredients'.
     * @param mealType The type of meal to generate, should be "breakfast" "lunch" or "dinner", only use when sending a generate request not a mealType check
     * @return The server's response as a String.
     *
     * @throws MalformedURLException If the provided urlString is not a valid URL.
     * @throws FileNotFoundException If the provided file does not exist.
     * @throws IOException If an I/O error occurs with the connection.
     */
    public String performPOST(String urlString, File file, String audioType, String mealType, User user) throws IOException {
        HttpURLConnection conn = setupConnection(urlString, "POST", user);
        conn.setRequestProperty("Content-Type", "application/octet-stream");
        conn.setRequestProperty("Content-Length", String.valueOf(file.length()));
        conn.setRequestProperty("Audio-Type", audioType);
        conn.setRequestProperty("Meal-Type", mealType);
    
        OutputStream out = conn.getOutputStream();
        Files.copy(file.toPath(), out);
    
        out.flush();
        out.close();
    
        String response = getResponse(conn);
        System.out.println("Server Response: " + response);
        return response;
    }


    /**
     * This method sends a PUT request to the server with a recipe's details. If the recipe already exists on the server, it will be updated; 
     * if it does not exist, it will be created.
     *
     * @param urlString The URL of the server to which the PUT request will be sent.
     * @param recipeID The ID of the recipe to be updated or created.
     * @param recipeTitle The title of the recipe to be updated or created.
     * @param recipeText The text of the recipe to be updated or created.
     * @param mealType The meal type of the recipe to be updated or created.
     * 
     * @return 
     *
     */
    public Integer performPUT(String urlString, int recipeID, String recipeTitle, String recipeText, String mealType, User user) throws IOException {
        HttpURLConnection conn = setupConnection(urlString, "PUT", user);
    
        JSONObject requestBody = new JSONObject();
        requestBody.put("newRecipeText", recipeText);
        requestBody.put("newRecipeTitle", recipeTitle);
        requestBody.put("recipeID", recipeID);
        requestBody.put("mealType", mealType);
        String body = requestBody.toString();
    
        sendRequest(conn, body);
    
        int responseCode = conn.getResponseCode();
        Integer toReturn = -1;
        if (responseCode == HttpURLConnection.HTTP_OK) {
            String response = getResponse(conn);
            toReturn = Integer.parseInt(response); // Get recipeID from server response
        } else {
            System.out.println("Server returned non-OK code: " + responseCode);
        }
        return toReturn;
    }


    /**
     * This method sends a DELETE request to the server to delete a specific recipe identified by its ID.
     * 
     * @param urlString The URL of the server to which the DELETE request will be sent
     * @param recipeID The ID of the recipe to be deleted.
     * 
     */
    public void performDELETE(String urlString, int recipeID, User user) throws IOException {
        HttpURLConnection conn = setupConnection(urlString, "DELETE", user);
    
        String body = "recipeID=" + recipeID;
        sendRequest(conn, body);
    
        int responseCode = conn.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            String response = getResponse(conn);
            System.out.println("Delete response " + response);
        } else {
            System.out.println("Server returned non-OK code: " + responseCode);
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
    public String performGET(String urlString, User user) throws IOException {
        HttpURLConnection conn = setupConnection(urlString, "GET", user);
        String content = getResponse(conn);
        return content;
    }

    private HttpURLConnection setupConnection(String urlString, String method, User user) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod(method);
        conn.setDoOutput(true);
        conn.setRequestProperty("Username", user.getUsername());
        conn.setRequestProperty("Password", user.getPassword());
        return conn;
    }
    
    private void sendRequest(HttpURLConnection conn, String body) throws IOException {
        OutputStreamWriter out = new OutputStreamWriter(conn.getOutputStream());
        out.write(body);
        out.flush();
        out.close();
    }
    
    private String getResponse(HttpURLConnection conn) throws IOException {
        BufferedReader in;
        System.out.println("Response Code: " + conn.getResponseCode());
        System.out.println("Response Message: " + conn.getResponseMessage());
        if (200 <= conn.getResponseCode() && conn.getResponseCode() <= 399) {
            in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        } else {
            in = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
        }
        StringBuilder response = new StringBuilder();
        String inputLine;
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();
        return response.toString();
     }

}
