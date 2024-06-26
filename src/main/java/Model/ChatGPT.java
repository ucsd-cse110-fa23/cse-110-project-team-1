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
        String prompt = "\"Imagine you're a chef and you've been asked to create a unique '" +  mealType + "' recipe. The ingredients you have to work with are '" + ingredients + "'. Your task is to come up with a recipe that incorporates some of these ingredients but not necessarily  all. \n" + //
                "\n" + //
                "The format of the recipe should be as follows:\n" + //
                "\n" + //
                "1. Start with the name of the recipe. This should be a short catchy and descriptive title that gives a sense of what the final dish will be like. This should be the first line of your response.\n" + //
                "\n" + //
                "2. Next, list out all the ingredients required for the recipe. For each ingredient, specify the amount needed. Each ingredient should be on a new line.\n" + //
                "\n" + //
                "3. After listing the ingredients, provide a step-by-step guide on how to prepare and cook the meal. Each step should be numbered and start on a new line. Be as detailed as possible, explaining each process clearly.\n" + //
                "\n" + //
                "Here's an example of how your recipe should be formatted:\n" + //
                "\n" + //
                "Chicken Stir-Fry\n" + //
                "\n" + //
                "- 1 lb of chicken\n" + //
                "- 2 bell peppers\n" + //
                "- 1 onion\n" + //
                "- 2 tomatoes\n" + //
                "\n" + //
                "1. Cut the chicken and vegetables into bite-sized pieces.\n" + //
                "2. Heat oil in a pan and add the chicken. Cook until no longer pink.\n" + //
                "3. Add the vegetables and stir-fry until tender.\n" + //
                "4. Serve hot with rice.\n" + //
                "\n" + //
                "Remember, you can only use the listed ingredients and the response should contain only the recipe and it should strictly follow this format. it should not include any explanation after the last step.\n Do not use any Box-drawing characters or other illustrative characters as they are not able to be understood.";

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
        System.out.println("Response Body: \n" + responseBody);
        JSONObject responseJson = new JSONObject(responseBody);
        //System.out.println("Parsed Json : \n" + responseJson.toString());
        JSONArray choices = responseJson.getJSONArray("choices");
        String generatedText = choices.getJSONObject(0).getString("text");
        //System.out.println("Parsed Json :" + generatedText);

        return filterNonAscii(generatedText);
    }

    String filterNonAscii(String input) {
        return input.replaceAll("[^\\x00-\\x7F]", "");
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
        //String prompt = "{bla:\"foo\", choices:[{text:\"Mashed Potatos\n...\", test:\"foofoo\"}], blabla:\"bar\"}";
        
        // Pull response string out of premade JSON prompt string
        //String responseBody = prompt;
        //JSONObject responseJson = new JSONObject(responseBody);
        //JSONArray choices = responseJson.getJSONArray("choices");
        //String generatedText = choices.getJSONObject(0).getString("text");


        if(mealType.toLowerCase().contains("dinner") && ingredients.contains("potato") && ingredients.contains("butter")) {
            response = "Buttery Potato Dinner\n\nIngredients:\nPotatoes\nButter\n\nInstructions:\nStep 1: Boil the potatoes.\nStep 2: Drain the potatoes and add butter.\nStep 3: Serve hot.\n";
        } else if(mealType.toLowerCase().contains("lunch") && ingredients.contains("noodles") && ingredients.contains("soy sauce")) {
            response = "Noodle Stir Fry\n\nIngredients:\nNoodles\nSoy Sauce\nAvocado oil\nOnion powder\nGarlic powder\n\nInstructions:\nStep 1: Cook the noodles.\nStep 2: Heat the avocado oil in a pan.\nStep 3: Add the cooked noodles, soy sauce, onion powder, and garlic powder.\nStep 4: Stir fry for a few minutes.\n";
        } else if(mealType.toLowerCase().contains("breakfast") && ingredients.contains("crackers") && ingredients.contains("jam")) {
            response = "Cracker and Jam Snack\n\nIngredients:\nCrackers\nJam\n\nInstructions:\nStep 1: Spread jam on the crackers.\n";
        } else if(mealType.toLowerCase().contains("dinner") && ingredients.contains("noodles") && ingredients.contains("soy sauce")) {
            response = "Soy Sauce Noodles Dinner\n\nIngredients:\nNoodles\nSoy Sauce\n\nInstructions:\nStep 1: Cook the noodles.\nStep 2: Drain the noodles and add soy sauce.\nStep 3: Serve hot.\n";
        } else {
            response = "No recipe.\n";
        }
    

        return response;
    }
}