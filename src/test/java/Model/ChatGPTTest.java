package Model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import Model.ChatGPT;

public class ChatGPTTest {
    private ChatGPT chatGPT;

    @BeforeEach
    void setUp() {
        chatGPT = new ChatGPT();
    }

    @Test
    void testValidChatGPT() {
        String expected = "this is wrong, write a mock ChatGPT class later so this is doable";
        String mealType = "Dinner";
        String ingredients = "I have eggs, bacon, cheese.";

        try {
            String response = chatGPT.getResponse(mealType, ingredients);
            assertEquals(expected, response);
        } catch (Exception e) {
            System.err.println(e);
        }
        
    }
}
