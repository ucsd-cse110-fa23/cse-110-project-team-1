package Model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ChatGPTTest {
    private GPTModel chatGPT;

    @BeforeEach
    void setUp() {
        chatGPT = new MockGPT();
    }

    @Test
    void testValidGPT() {
        String mealType = "Dinner";
        String ingredients = "I have potatoes, butter and cheese.";

        try {
            String response = chatGPT.getResponse(mealType, ingredients);
            assertTrue(response.contains("Buttery Potato Dinner"));
        } catch (Exception e) {
            System.err.println(e);
        }
    }

    @Test
    void testInvalidGPT_mealType() {
        String expected = "No recipe.\n";
        String mealType = "Lunch";
        String ingredients = "I have potatoes, butter and cheese.";

        try {
            String response = chatGPT.getResponse(mealType, ingredients);
            assertEquals(expected, response);
        } catch (Exception e) {
            System.err.println(e);
        }
    }

    @Test
    void testInvalidGPT_ingredients() {
        String expected = "No recipe.\n";
        String mealType = "Dinner";
        String ingredients = "I have potatoes, bacon and cheese.";

        try {
            String response = chatGPT.getResponse(mealType, ingredients);
            assertEquals(expected, response);
        } catch (Exception e) {
            System.err.println(e);
        }
    }
    
    @Test
    void testFilterNonAscii() {
        String input = "Hello, World! 350┬░F";
        String expected = "Hello, World! 350F";
        
        ChatGPT chatGPT = new ChatGPT();
        String output = chatGPT.filterNonAscii(input);
        
        assertEquals(expected, output);
    }
}
