package Model;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.io.File;

import org.junit.jupiter.api.AfterEach;

public class RecipeServerMethodsTest {
    private MockRecipeServer mockServer;

    @BeforeEach
    void setUp() {
        mockServer = new MockRecipeServer();
    }

    @Test
    void testStartLoadStopServer() {
        assertDoesNotThrow(() -> mockServer.startServer());
        assertDoesNotThrow(() -> mockServer.loadServer());
        assertDoesNotThrow(() -> mockServer.stopServer());
    }

    @AfterEach
    void cleanup(){
        File test = new File("test.csv");
        if(test.exists()){
            test.delete();
        }
    }

}