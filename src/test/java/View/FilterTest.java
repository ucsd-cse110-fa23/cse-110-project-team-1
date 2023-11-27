package View;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import Controller.RequestHandler;
import Model.GenerateDemoLists;
import Model.MockRecipeServer;
import Model.RecipeServerInterface;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.application.Platform;


public class FilterTest {
    
    @BeforeEach
    void setUp(){
        Platform.startup(() -> {});
        GenerateDemoLists.generateAndSaveDemoList();
    }

    @Test 
    void testBreakfast(){
        RecipeServerInterface server = new MockRecipeServer();
        try {
            server.startServer();
            server.renameServer("demo1");
            server.loadServer();
            ListView<HBox> filtered = new ListView<HBox>();
            RequestHandler req = new RequestHandler();
            ViewModel viewModel = new ViewModel(req, "http://localhost:8100/?all", null);
            ListView<HBox> allRecipes = viewModel.pullRecipes();
            String filter = "Breakfast";
            for(HBox h : allRecipes.getItems()) {
                if(filter.equalsIgnoreCase("all") || ((RecipeNode)h).toJson().getString("mealType").equalsIgnoreCase(filter)) {
                    filtered.getItems().add(h);
                }
            }
            for(HBox h : filtered.getItems()) {
                assertTrue(((RecipeNode)h).toJson().getString("mealType").equalsIgnoreCase(filter));
            }
            server.stopServer();
            File demoFile = new File("demo1.list");
            demoFile.delete();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test 
    void testAll(){
        RecipeServerInterface server = new MockRecipeServer();
        try {
            server.startServer();
            server.renameServer("demo1");
            server.loadServer();
            ListView<HBox> filtered = new ListView<HBox>();
            RequestHandler req = new RequestHandler();
            ViewModel viewModel = new ViewModel(req, "http://localhost:8100/?all", null);
            ListView<HBox> allRecipes = viewModel.pullRecipes();
            String filter = "All";
            int recipeCounter = 0;
            for(HBox h : allRecipes.getItems()) {
                if(filter.equalsIgnoreCase("all") || ((RecipeNode)h).toJson().getString("mealType").equalsIgnoreCase(filter)) {
                    filtered.getItems().add(h);
                }
            }
            for(HBox h : filtered.getItems()) {
                recipeCounter++;
            }
            assertEquals(12, recipeCounter);
            server.stopServer();
            File demoFile = new File("demo1.list");
            demoFile.delete();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
