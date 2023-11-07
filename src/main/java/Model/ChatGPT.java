package Model;

import java.io.FileInputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import org.json.JSONArray;
import org.json.JSONObject;

interface GPTModel {
    // Take in a meal type and an arbitrary list of ingredients
    // and return a recipe formulated from ChatGPT's response
    public String getResponse(String mealType, String ingredients) throws Exception;
}

public class ChatGPT implements GPTModel {
    private static final String API_ENDPOINT = "https://api.openai.com/v1/completions";
    private static String API_KEY;
    private static final String MODEL = "text-davinci-003";
    private static final int MAXTOKENS = 400;

    public ChatGPT() {
        try {
            FileInputStream fs = new FileInputStream("key.txt");
            API_KEY = new String(fs.readAllBytes());
            fs.close();
        } catch(Exception e) {
            System.err.println(e);
        }
    }

    public String getResponse(String mealType, String ingredients) throws Exception {
        String prompt = "I'm looking to make " + mealType + ". Ingredients: " + ingredients;

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

        // Pull response string out of JSON response body
        String responseBody = response.body();
        JSONObject responseJson = new JSONObject(responseBody);
        JSONArray choices = responseJson.getJSONArray("choices");
        String generatedText = choices.getJSONObject(0).getString("text");

        return generatedText;
    }
}

class MockGPT implements GPTModel {
    private static final String API_ENDPOINT = "https://api.openai.com/v1/completions";
    private static String API_KEY;
    private static final String MODEL = "text-davinci-003";
    private static final int MAXTOKENS = 400;

    public MockGPT() {
        try {
            // For mocking purposes, this file does not exist
            FileInputStream fs = new FileInputStream("wrongkey.txt");
            API_KEY = new String(fs.readAllBytes());
            fs.close();
        } catch(Exception e) {
            System.err.println(e);
        }
    }

    public String getResponse(String mealType, String ingredients) throws Exception {
        // Create a request body which you will pass into request object
        JSONObject requestBody = new JSONObject();
        requestBody.put("model", MODEL);
        requestBody.put("prompt", "I'm looking to make " + mealType + ". Ingredients: " + ingredients);
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

        // Send the request and receive an erroneous response
        HttpResponse<String> postResponse = client.send(
            request,
            HttpResponse.BodyHandlers.ofString()
        );

        // Check if anything about HTTP request is strange
        if(postResponse.statusCode() != 401 ||
            postResponse.body() == null ||
            API_KEY != null) {
            return "Error";
        }

        String response;
        String prompt = "{bla:\"foo\", choices:[{text:\"Mashed Potatos\\n...\", test:\"foofoo\"}], blabla:\"bar\"}";

        // Pull response string out of premade JSON prompt string
        String responseBody = prompt;
        JSONObject responseJson = new JSONObject(responseBody);
        JSONArray choices = responseJson.getJSONArray("choices");
        String generatedText = choices.getJSONObject(0).getString("text");

        if((mealType).contains("Dinner") &&
            (ingredients.toLowerCase()).contains("potato") &&
            (ingredients.toLowerCase()).contains("butter")) {
            response = generatedText;
        } else {
            response = "No recipe.\n";
        }

        return response;
    }
}