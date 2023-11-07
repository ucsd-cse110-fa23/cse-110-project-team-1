package Model;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
        String expected = "Mashed Potatos\n...";
        String mealType = "Dinner";
        String ingredients = "I have potatoes, butter and cheese.";

        try {
            String response = chatGPT.getResponse(mealType, ingredients);
            assertEquals(expected, response);
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
}
