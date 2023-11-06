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
    /*
     * Gets json data from single line from url
     * buffered reader and url connection referenced from
     * https://www.javatpoint.com/java-get-data-from-url
     */
    public String getURLData(String url) throws IOException {
        URL urlObj = new URL(url); // creating a url object
        System.out.println("Opened Mock Server");
        URLConnection urlConnection = urlObj.openConnection(); // creating a urlconnection object

        // wrapping the urlconnection in a bufferedreader
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
        String content;
        // reading from the urlconnection using the bufferedreader
        content = bufferedReader.readLine();
        bufferedReader.close();
        return content;
    }

    @Test
    void testServerReadsExistingListWithItems() throws IOException {
        RecipeServerInterface server = new MockRecipeServer();
        try {
            server.startServer();
            server.renameServer("src/test/lists/testServerReadsExistingList");
            server.loadServer();
            //RequestHandler req = new RequestHandler();
            String content = getURLData("http://localhost:8100/?all");
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
            String content = getURLData("http://localhost:8100/?all");
    
            assertEquals("{}",content);
            server.stopServer();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
    }
}
