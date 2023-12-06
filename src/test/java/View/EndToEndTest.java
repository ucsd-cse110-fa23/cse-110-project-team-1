package View;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.io.IOException;
import Controller.*;
import Model.*;
import java.io.File;

@TestMethodOrder(MethodOrderer.MethodName.class)
public class EndToEndTest {

    
    @BeforeEach
    void setup() throws IOException {
        File rec = new File("recording.wav");
        rec.createNewFile();
    }

    
    @Test
    void testEndToEndScenario_SBST1_MS2() throws IOException {
        // Initialize mock server and request handler
        MockRecipeServer mockServer = new MockRecipeServer();
        RequestHandler req = new RequestHandler();
    
        //reused strings
        String serverUrl = "http://localhost:8100/";
        String username = "username";
        String password = "password";
        String wrongPassword = "wrongpassword";
        String recordingFile = "recording.wav";

        User user1 = new User(username, password);
    
        // Start and load 
        mockServer.startServer();
        mockServer.loadServer();
    
        // Try to login with incorrect credentials
        boolean loginSuccessful = req.performLogin(serverUrl, new User(username, wrongPassword));
        assertFalse(loginSuccessful);
    
        // Create a new account
        boolean accountCreated = req.performAccountCreation(serverUrl, user1);
        assertTrue(accountCreated);
    
        // Login with correct credentials
        loginSuccessful = req.performLogin(serverUrl, user1);
        assertTrue(loginSuccessful);
    
        // Generate a new recipe for dinner with potatoes and butter
        String newRecipeResponse = req.performPOST(serverUrl, new File(recordingFile), "dinneringredients", "dinner", user1);
        JSONObject newRecipe = new JSONObject(newRecipeResponse);
        assertEquals("Buttery Potato Dinner", newRecipe.getString("recipeTitle"));
    
        // Save the recipe
        Integer recipeID = req.performPUT(serverUrl, newRecipe.getInt("recipeID"), newRecipe.getString("recipeTitle"), newRecipe.getString("recipeText"), "dinner", newRecipe.getString("base64Image"), user1);
        assertEquals(1, recipeID);
    
        // Generate a new recipe for lunch with noodles and soy sauce
        newRecipeResponse = req.performPOST(serverUrl, new File(recordingFile), "lunchingredients", "lunch", user1);
        newRecipe = new JSONObject(newRecipeResponse);
        assertEquals("Noodle Stir Fry", newRecipe.getString("recipeTitle"));
    
        // Save the recipe
        recipeID = req.performPUT(serverUrl, newRecipe.getInt("recipeID"), newRecipe.getString("recipeTitle"), newRecipe.getString("recipeText"), "lunch", newRecipe.getString("base64Image"), user1);
        assertEquals(2, recipeID);
    
        // Generate a new recipe for breakfast with crackers and jam
        newRecipeResponse = req.performPOST(serverUrl, new File(recordingFile), "breakfastingredients", "breakfast", user1);
        newRecipe = new JSONObject(newRecipeResponse);
        assertEquals("Cracker and Jam Snack", newRecipe.getString("recipeTitle"));
    
        // Save the recipe
        recipeID = req.performPUT(serverUrl, newRecipe.getInt("recipeID"), newRecipe.getString("recipeTitle"), newRecipe.getString("recipeText"), "breakfast", newRecipe.getString("base64Image"), user1);
        assertEquals(3, recipeID);
    
        // Verify that the recipes are saved
        String content = req.performGET(serverUrl + "?all", user1);
        JSONArray jsonArray = new JSONArray(content);
        assertEquals(3, jsonArray.length());
    
        // Stop the server
        mockServer.stopServer();
    
        // Restart the server and load data
        mockServer.startServer();
        mockServer.loadServer();
    
        // Verify that the recipes are still saved
        String contentLoaded = req.performGET(serverUrl + "?all", user1);
        JSONArray jsonArrayLoaded = new JSONArray(contentLoaded);
        assertEquals(3, jsonArrayLoaded.length());
    
        // Stop the server again
        mockServer.stopServer();
    }

    @Test
    void testEndToEndScenario_SBST2_MS2() throws IOException {
        // Initialize mock server and request handler
        MockRecipeServer mockServer = new MockRecipeServer();
        RequestHandler req = new RequestHandler();
    
        //reused strings
        String serverUrl = "http://localhost:8100/";
        String username = "username";
        String password = "password";
        String recordingFile = "recording.wav";
        User user1 = new User(username, password);
    
        //// Setup Server for test
        
        // Start and load 
        mockServer.startServer();
        mockServer.loadServer();
    
        // Setup Account
        boolean accountCreated = req.performAccountCreation(serverUrl, user1);
        assertTrue(accountCreated);
    
        // Generate recipe to be edited 
        String newRecipeResponse = req.performPOST(serverUrl, new File(recordingFile), "breakfastingredients", "breakfast", user1);
        JSONObject newRecipe = new JSONObject(newRecipeResponse);
        assertEquals("Cracker and Jam Snack", newRecipe.getString("recipeTitle"));
    
        // Save the recipe
        Integer recipeID = req.performPUT(serverUrl, newRecipe.getInt("recipeID"), newRecipe.getString("recipeTitle"), newRecipe.getString("recipeText"), "breakfast", newRecipe.getString("base64Image"), user1);
        assertEquals(1, recipeID);
    
        // Share the recipe
        req.performShare(serverUrl, recipeID, user1);
    
        // Edit the recipe
        String editedRecipeTitle = "Edited Cracker and Jam Snack";
        String editedRecipeText = "Edited recipe text";
        req.performPUT(serverUrl, recipeID, editedRecipeTitle, editedRecipeText, "breakfast", newRecipe.getString("base64Image"), user1);
    
        // Delete the recipe
        boolean deleteSuccessful = req.performDELETE(serverUrl, recipeID, user1);
        assertTrue(deleteSuccessful);
    
        // Stop the server
        mockServer.stopServer();
    }

    @Test
    void testEndToEndScenario_InvalidScenarios() throws IOException {
        // Initialize mock server and request handler
        MockRecipeServer mockServer = new MockRecipeServer();
        RequestHandler req = new RequestHandler();
    
        //reused strings
        String serverUrl = "http://localhost:8100/";
        String username = "username";
        String password = "password";
        String invalidMealType = "invalidMealType";
        User user1 = new User(username, password);
    
        // Start and load 
        mockServer.startServer();
        mockServer.loadServer();
    
        // Create a new account
        boolean accountCreated = req.performAccountCreation(serverUrl, user1);
        assertTrue(accountCreated);
    
        // Try to create a new account with the same username
        boolean duplicateAccountCreated = req.performAccountCreation(serverUrl, user1);
        assertFalse(duplicateAccountCreated);
    
        // Send a meal type request with an invalid meal type(mocking the audio containing invalid so using the test to check instead)
        
        String invalidMealTypeResponse = req.performPOST(serverUrl, new File("recording.wav"), invalidMealType, "none", user1);
        assertTrue(invalidMealTypeResponse.contains("Invalid POST request"));
    
        // Try to delete a non existent recipe
        boolean deleteSuccessful = req.performDELETE(serverUrl, 9999, user1);
        assertFalse(deleteSuccessful);
    
        // Stop the server
        mockServer.stopServer();
    }

    @AfterEach
    void cleanup() {
        // Delete any files that were created during the tests
        File file = new File("test.csv");
        if (file.exists()) {
            file.delete();
        }
        file = new File("mock.list");
        if (file.exists()) {
            file.delete();
        }
        File dinnerFile = new File("src/main/ReceivedMedia/dinneringredients.wav");
        if (dinnerFile.exists()) {
            dinnerFile.delete();
        }
        File lunchFile = new File("src/main/ReceivedMedia/lunchingredients.wav");
        if (lunchFile.exists()) {
            lunchFile.delete();
        }
        File breakfastFile = new File("src/main/ReceivedMedia/breakfastingredients.wav");
        if (breakfastFile.exists()) {
            breakfastFile.delete();
        }
    }

}