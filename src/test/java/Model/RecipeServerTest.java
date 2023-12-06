package Model;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import static org.junit.jupiter.api.Assertions.assertEquals;
import java.io.IOException;
import Controller.*;
import View.User;
import java.io.File;

@TestMethodOrder(MethodOrderer.MethodName.class)
public class RecipeServerTest{
    
    @Test
    void testServerReadsExistingListWithItems() throws IOException {
        RecipeServerInterface server = new MockRecipeServer();
        try {
            AccountManager AccountManager = new AccountManager("test.csv");
            AccountManager.addUser("username", "password");
            String base64Placeholder = "data:image/jpeg;base64,..."; // Replace with actual Base64 string

            RecipeList recipeList = new RecipeList("src/test/lists/testServerReadsExistingList");
            recipeList.addRecipe("Recipe1", "RecipeText1", "lunch", 1, base64Placeholder);

            server.startServer();
            server.renameServer("src/test/lists/testServerReadsExistingList");
            server.loadServer();
            RequestHandler req = new RequestHandler();
            String content = req.performGET("http://localhost:8100/?all", new User("username", "password"));

            JSONArray jsonArray = new JSONArray(content);
            JSONObject jsonObject = jsonArray.getJSONObject(0);

            assertEquals("Recipe1", jsonObject.getString("recipeTitle"));
            assertEquals("RecipeText1", jsonObject.getString("recipeText"));
            assertEquals("lunch", jsonObject.getString("mealType"));
            assertEquals(1, jsonObject.getInt("ownerID"));
            assertEquals(1, jsonObject.getInt("recipeID"));

            server.stopServer();
        } catch (IOException e) {
            e.printStackTrace();
        }
        File csvFile = new File("test.csv");
        if (csvFile.exists()) {
            csvFile.delete();
        }
        File recipes = new File("src/test/lists/testServerReadsExistingList.list");
        if (recipes.exists()) {
            recipes.delete();
        }
    }
    
    @Test
    void testServerReadsEmptyExistingList() throws IOException {
        RecipeServerInterface server = new MockRecipeServer();
        try {
            AccountManager AccountManager = new AccountManager("test.csv");
        
            // Add a user and then delete it
            AccountManager.addUser("username", "password");

            server.startServer();
            server.renameServer("src/test/lists/empty");
            server.loadServer();
            RequestHandler req = new RequestHandler();
            String content = req.performGET("http://localhost:8100/?all", new User("username", "password"));
    
            assertEquals("[]",content);
            server.stopServer();
        } catch (IOException e) {
            e.printStackTrace();
        }
        File csvFile = new File("test.csv");
        if (csvFile.exists()) {
            csvFile.delete();
        }
        
    }
}
