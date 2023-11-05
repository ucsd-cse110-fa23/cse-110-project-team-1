package Model;

import org.junit.jupiter.api.Test;

import Model.Recipe;
import Model.RecipeList;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.beans.Transient;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
        } catch (IOException e) {
            e.printStackTrace();
        }
        server.renameServer("src/test/lists/testServerReadsExistingList");
        server.loadServer();
        String content = getURLData("http://localhost:8100/?all");
        assertEquals("{\"1\":{\"recipeTitle\":\"Pizza\",\"recipeText\":\"Pizza\\n" + //
                "Ingredients:\\n" + //
                "Cheese\\n" + //
                "Dough\\n" + //
                "Tomato Sauce\",\"recipeID\":1}}",content);
        server.stopServer();
    }
    
    @Test
    void testServerReadsEmptyExistingList() throws IOException {
        RecipeServerInterface server = new MockRecipeServer();
        try {
            server.startServer();
        } catch (IOException e) {
            e.printStackTrace();
        }
        server.renameServer("src/test/lists/empty");
        server.loadServer();
        String content = getURLData("http://localhost:8100/?all");

        assertEquals("{}",content);
        server.stopServer();
        
    }
}
