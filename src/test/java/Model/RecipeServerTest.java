package Model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import java.io.IOException;
import Controller.*;
import View.User;
import java.io.File;


public class RecipeServerTest{
    

    @Test
    void testServerReadsExistingListWithItems() throws IOException {
        RecipeServerInterface server = new MockRecipeServer();
        try {
            AccountManager AccountManager = new AccountManager("test.csv");
            int userID = AccountManager.addUser("username", "password");
            String base64Placeholder = "data:image/jpeg;base64,..."; // Replace with actual Base64 string

            RecipeList recipeList = new RecipeList("src/test/lists/testServerReadsExistingList");
            recipeList.addRecipe("Recipe1", "RecipeText1", "lunch", 1, base64Placeholder);

            server.startServer();
            server.renameServer("src/test/lists/testServerReadsExistingList");
            server.loadServer();
            RequestHandler req = new RequestHandler();
            String content = req.performGET("http://localhost:8100/?all", new User("username", "password"));
            String expectedJson = String.format("[{\"recipeTitle\":\"Recipe1\",\"recipeText\":\"RecipeText1\",\"mealType\":\"lunch\",\"ownerID\":1,\"recipeID\":1, \"base64Image\":\"%s\"}]", base64Placeholder);
            assertEquals(expectedJson,content);
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
            int userID = AccountManager.addUser("username", "password");

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
