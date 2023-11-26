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
            server.startServer();
            server.renameServer("src/test/lists/testServerReadsExistingList");
            server.loadServer();
            RequestHandler req = new RequestHandler();
            String content = req.performGET("http://localhost:8100/?all", new User(1, "username", "password"));
            assertEquals("[{\"recipeTitle\":\"Beans\",\"recipeText\":\"Beans\\n Ingredients:\\n1 Can of Beans\\n\\nInstructions:\\nStep 1: Put beans on plate\\n\",\"recipeID\":1}]",content);
            server.stopServer();
        } catch (IOException e) {
            e.printStackTrace();
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
            String content = req.performGET("http://localhost:8100/?all", new User(1, "username", "password"));
    
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
