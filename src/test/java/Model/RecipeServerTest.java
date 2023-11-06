package Model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import Controller.*;

public class RecipeServerTest{
    

    @Test
    void testServerReadsExistingListWithItems() throws IOException {
        RecipeServerInterface server = new MockRecipeServer();
        try {
            server.startServer();
            server.renameServer("src/test/lists/testServerReadsExistingList");
            server.loadServer();
            RequestHandler req = new RequestHandler();
            String content = req.performGET("http://localhost:8100/?all");
            assertEquals("{\"1\":{\"recipeTitle\":\"Pizza\",\"recipeText\":\"Pizza\\n" + //
                    "Ingredients:\\n" + //
                    "Cheese\\n" + //
                    "Dough\\n" + //
                    "Tomato Sauce\",\"recipeID\":1}}",content);
            server.stopServer();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    @Test
    void testServerReadsEmptyExistingList() throws IOException {
        RecipeServerInterface server = new MockRecipeServer();
        try {
            server.startServer();
            server.renameServer("src/test/lists/empty");
            server.loadServer();
            RequestHandler req = new RequestHandler();
            String content = req.performGET("http://localhost:8100/?all");
    
            assertEquals("{}",content);
            server.stopServer();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
    }
}
