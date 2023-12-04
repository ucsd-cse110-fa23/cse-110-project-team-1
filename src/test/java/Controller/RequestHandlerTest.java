package Controller;

import View.*;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;

public class RequestHandlerTest {
    @Test
    public void testPerformLogin_ServerOffline() {
        String invalidUrl = "invalidurl";
        User testUser = new User("testUser", "testPassword");

        RequestHandler requestHandler = new RequestHandler();
        boolean exceptionThrown = false;

        try {
            requestHandler.performLogin(invalidUrl, testUser);
        } catch (IOException e) {
            exceptionThrown = true;
        }
        assertTrue(exceptionThrown);
    }
    @Test
    public void testPerformGET_ServerOffline() {
        String invalidUrl = "invalidurl";
        User testUser = new User("testUser", "testPassword");
        RequestHandler requestHandler = new RequestHandler();
    
        boolean exceptionThrown = false;
        try {
            requestHandler.performGET(invalidUrl, testUser);
        } catch (IOException e) {
            exceptionThrown = true;
        }
    
        assertTrue(exceptionThrown);
    }
    
    @Test
    public void testPerformPOST_ServerOffline() {
        String invalidUrl = "invalidurl";
        User testUser = new User("testUser", "testPassword");
        File testFile = new File("testFile");
        String audioType = "audioType";
        String mealType = "mealType";
        RequestHandler requestHandler = new RequestHandler();
    
        boolean exceptionThrown = false;
        try {
            requestHandler.performPOST(invalidUrl, testFile, audioType, mealType, testUser);
        } catch (IOException e) {
            exceptionThrown = true;
        }
    
        assertTrue(exceptionThrown);
    }
    
    @Test
    public void testPerformPUT_ServerOffline() {
        String invalidUrl = "invalidurl";
        User testUser = new User("testUser", "testPassword");
        int recipeID = 1;
        String recipeTitle = "recipeTitle";
        String recipeText = "recipeText";
        String mealType = "mealType";
        RequestHandler requestHandler = new RequestHandler();
    
        boolean exceptionThrown = false;
        try {
            requestHandler.performPUT(invalidUrl, recipeID, recipeTitle, recipeText, mealType,null, testUser);
        } catch (IOException e) {
            exceptionThrown = true;
        }
    
        assertTrue(exceptionThrown);
    }
    
    @Test
    public void testPerformDELETE_ServerOffline() {
        String invalidUrl = "invalidurl";
        User testUser = new User("testUser", "testPassword");
        int recipeID = 1;
        RequestHandler requestHandler = new RequestHandler();
    
        boolean exceptionThrown = false;
        try {
            requestHandler.performDELETE(invalidUrl, recipeID, testUser);
        } catch (IOException e) {
            exceptionThrown = true;
        }
    
        assertTrue(exceptionThrown);
    }
    
    @Test
    public void testPerformAccountCreation_ServerOffline() {
        String invalidUrl = "invalidurl";
        User testUser = new User("testUser", "testPassword");
        RequestHandler requestHandler = new RequestHandler();
    
        boolean exceptionThrown = false;
        try {
            requestHandler.performAccountCreation(invalidUrl, testUser);
        } catch (IOException e) {
            exceptionThrown = true;
        }
    
        assertTrue(exceptionThrown);
    }
}