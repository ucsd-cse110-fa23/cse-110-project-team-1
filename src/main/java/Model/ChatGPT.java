package Model;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import org.json.JSONArray;
import org.json.JSONObject;

public class ChatGPT {
    private static final String API_ENDPOINT = "https://api.openai.com/v1/completions";
    private static String API_KEY;
    private static final String MODEL = "text-davinci-003";
    private static final int MAXTOKENS = 400;

    public ChatGPT() {
        try {
            FileInputStream fs = new FileInputStream("../../key.txt");
            API_KEY = new String(fs.readAllBytes());
            System.out.println(API_KEY);
            fs.close();
        } catch(Exception e) {
            System.err.println(e);
        }
    }

    public String getResponse(String mealType, String ingredients) throws Exception, IOException, InterruptedException, URISyntaxException {
        String prompt = "I'm looking to make " + mealType + ". " + ingredients;

        // Create a request body which you will pass into request object
        JSONObject requestBody = new JSONObject();
        requestBody.put("model", MODEL);
        requestBody.put("prompt", prompt);
        requestBody.put("max_tokens", MAXTOKENS);
        requestBody.put("temperature", 1.0);

            // Create the HTTP Client


        HttpClient client = HttpClient.newHttpClient();


        // Create the request object
        HttpRequest request = HttpRequest
        .newBuilder()
        .uri(new URI(API_ENDPOINT))
        .header("Content-Type", "application/json")
        .header("Authorization", String.format("Bearer %s", API_KEY))
        .POST(HttpRequest.BodyPublishers.ofString(requestBody.toString()))
        .build();


        // Send the request and receive the response
        HttpResponse<String> response = client.send(
        request,
        HttpResponse.BodyHandlers.ofString()
        );
        // System.out.println(response.body());

        String responseBody = response.body();
        System.out.println(responseBody);
        JSONObject responseJson = new JSONObject(responseBody);


        JSONArray choices = responseJson.getJSONArray("choices");
        String generatedText = choices.getJSONObject(0).getString("text");


        // System.out.println(generatedText);
        return generatedText;
        
        
    }




}