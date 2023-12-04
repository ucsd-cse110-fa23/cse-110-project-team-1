package Model;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Base64;
import org.json.JSONObject;

interface ImageModel {
    // Take a recipe description and return an image in Base64 format
    String generateImageBase64(String recipeDescription) throws Exception;
}

public class Dalle implements ImageModel {
    private static final String API_ENDPOINT = "https://api.openai.com/v1/images/generations";
    private static String API_KEY;
    private static final String MODEL = "dall-e-2"; // Or the specific model used for DALL-E

    public Dalle() {
        try {
            FileInputStream fs = new FileInputStream("key.txt");
            API_KEY = new String(fs.readAllBytes());
            fs.close();
        } catch (Exception e) {
            System.err.println(e);
        }
    }

    public String generateImageBase64(String recipeDescription) throws Exception {
        // Constructing the prompt for DALL-E API
        JSONObject requestBody = new JSONObject();
        requestBody.put("model", MODEL);
        requestBody.put("prompt", recipeDescription);
        requestBody.put("n", 1); // Generate one image
        requestBody.put("size", "256x256");

        // Create the HTTP Client
        HttpClient client = HttpClient.newHttpClient();

        // Create the request object
        HttpRequest request = HttpRequest.newBuilder()
            .uri(new URI(API_ENDPOINT))
            .header("Content-Type", "application/json")
            .header("Authorization", "Bearer " + API_KEY)
            .POST(HttpRequest.BodyPublishers.ofString(requestBody.toString()))
            .build();

        // Send the request and receive the response
        HttpResponse<String> response = client.send(
            request,
            HttpResponse.BodyHandlers.ofString()
        );

        // Parse the response to get the image URL
        String responseBody = response.body();
        System.out.println("Response Body: \n" + responseBody);
        JSONObject responseJson = new JSONObject(responseBody);
        String imageURL = responseJson.getJSONArray("data").getJSONObject(0).getString("url");
        
        // Convert the image URL to Base64
        String base64Image = "data:image/png;base64," + convertToBase64(imageURL);
        
        return base64Image;
    }

    private String convertToBase64(String imageURL) throws Exception {
        URL url = new URL(imageURL);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        InputStream inputStream = connection.getInputStream();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[4096];
        int bytesRead;
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, bytesRead);
        }
        byte[] imageBytes = outputStream.toByteArray();
        inputStream.close();
        outputStream.close();
        return Base64.getEncoder().encodeToString(imageBytes);
    }
}

class MockDalle implements ImageModel {
    // Return a fixed Base64 string representing a small transparent image for testing purposes
    private static final String MOCK_IMAGE_BASE64 = "iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAQAAAC1HAwCAAAAC0lEQVR42mP8/wcAAtMB9VMJ+ysAAAAASUVORK5CYII=";

    public String generateImageBase64(String recipeDescription) throws Exception {
        return MOCK_IMAGE_BASE64;
    }
}
