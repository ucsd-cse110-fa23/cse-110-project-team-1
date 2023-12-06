package Model;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;

public class RecipeTest { //temporary tests until JUnit is in place\
    //private Recipe recipe;

    private final String base64Placeholder = "data:image/jpeg;base64,..."; // Replace with actual Base64 string

    @BeforeEach
    void setUp(){

    }
    @Test
    void testRecipeConstruction(){
       int newRecipeID = 123;
        String newRecipeTitle = "Beans";
        String newRecipeText = "Beans Recipe\n Ingredients:\n1 Can of Beans\n\nInstructions:\nStep 1: Put beans on plate\n";

        Recipe newRecipe = new Recipe(newRecipeID,newRecipeTitle,newRecipeText, "lunch",1,base64Placeholder);

        assertEquals(newRecipeID, newRecipe.getRecipeID());
        assertEquals(newRecipeText, newRecipe.getRecipeText());
        assertEquals(newRecipeTitle, newRecipe.getRecipeTitle());

    }
    @Test
    void testRecipeListConstruction(){
        
        String newRecipeTitle = "Souffle";
        String newRecipeText = "Souffle Recipe\n Ingredients:\n2 Jars of eggs\n\nInstructions:\nStep 1: Put egg in ramen\n";

        RecipeList newRecipeList = new RecipeList("src/test/test");

        newRecipeList.addRecipe(newRecipeTitle, newRecipeText, "lunch",1,base64Placeholder);

        assertEquals(1, newRecipeList.getRecipe(1).getRecipeID());
        assertEquals(newRecipeText, newRecipeList.getRecipe(1).getRecipeText());
        assertEquals(newRecipeTitle, newRecipeList.getRecipe(1).getRecipeTitle());

        File file = new File("src/test/test.list");
        file.delete(); // remove test list
        

    }
    @Test
    void testAddingMultipleRecipes() {
        RecipeList recipeList = new RecipeList("src/test/testAddingMultipleRecipes");
        recipeList.addRecipe("Recipe1", "RecipeText1", "lunch", 1,base64Placeholder);
        recipeList.addRecipe("Recipe2", "RecipeText2", "lunch", 1,base64Placeholder);
        recipeList.addRecipe("Recipe3", "RecipeText3", "lunch", 1,base64Placeholder);

        // Check if the most recent recipe is the last one added
        Recipe mostRecent = recipeList.getMostRecent();
        assertEquals("Recipe3", mostRecent.getRecipeTitle());
        assertEquals("RecipeText3", mostRecent.getRecipeText());

        File file = new File("src/test/testAddingMultipleRecipes.list");
        file.delete(); // remove test list
    }

    @Test
    void testSaveAndLoadRecipeList() {

        RecipeList recipeList = new RecipeList("src/test/testSaveAndLoadRecipeList");
        recipeList.setShareDirectory("src/test/shared/");

        recipeList.addRecipe("Recipe1", "RecipeText1", "lunch", 1,base64Placeholder);
        recipeList.addRecipe("Recipe2", "RecipeText2", "lunch", 1,base64Placeholder);
        recipeList.addRecipe("Recipe3", "RecipeText3", "lunch", 1,base64Placeholder);

        recipeList.shareRecipe(1);
        recipeList.shareRecipe(3);

        recipeList.saveToDisk();

        RecipeList loadedList = new RecipeList("src/test/testSaveAndLoadRecipeList");
        loadedList.setShareDirectory("src/test/shared/");

        loadedList.loadFromDisk();

        assertEquals(recipeList.getRecipe(1).getRecipeTitle(), loadedList.getRecipe(1).getRecipeTitle());
        assertEquals(recipeList.getRecipe(1).getRecipeText(), loadedList.getRecipe(1).getRecipeText());
        assertEquals(loadedList.getRecipe(1).getShared(), true);
    
        assertEquals(recipeList.getRecipe(2).getRecipeTitle(), loadedList.getRecipe(2).getRecipeTitle());
        assertEquals(recipeList.getRecipe(2).getRecipeText(), loadedList.getRecipe(2).getRecipeText());
        assertEquals(loadedList.getRecipe(2).getShared(), false);
    
        assertEquals(recipeList.getRecipe(3).getRecipeTitle(), loadedList.getRecipe(3).getRecipeTitle());
        assertEquals(recipeList.getRecipe(3).getRecipeText(), loadedList.getRecipe(3).getRecipeText());
        assertEquals(loadedList.getRecipe(3).getShared(), true);
        
        
        
        assertTrue(recipeList.getRecipe(1).equals(loadedList.getRecipe(1)));
        assertTrue(recipeList.getRecipe(2).equals(loadedList.getRecipe(2)));
        assertTrue(recipeList.getRecipe(3).equals(loadedList.getRecipe(3)));
        

        File file = new File("src/test/testSaveAndLoadRecipeList.list");
        file.delete(); // remove test list
    }

    @Test
    void testDeleteExistingRecipe(){
        RecipeList recipeList = new RecipeList("src/test/testDeleteExistingRecipe");
        int addedRecipeID = recipeList.addRecipe("Recipe1", "RecipeText1", "lunch", 1,base64Placeholder);
        assertFalse(recipeList.isEmpty());
        recipeList.deleteRecipe(addedRecipeID, 1);
        assertTrue(recipeList.isEmpty());

        File file = new File("src/test/testDeleteExistingRecipe.list");
        file.delete(); // remove test list
    }

    @Test
    void testDeleteNonExistingRecipe(){
        RecipeList recipeList = new RecipeList("src/test/testDeleteNonExistingRecipe");
        int addedRecipeID = recipeList.addRecipe("Recipe1", "RecipeText1", "lunch", 1,base64Placeholder);
        assertFalse(recipeList.deleteRecipe(addedRecipeID+1, 1));
        assertFalse(recipeList.isEmpty());
        
        File file = new File("src/test/testDeleteNonExistingRecipe.list");
        file.delete(); // remove test list
    }
    
    
    
    
    @Test 
    void testEditExistingRecipe(){
        RecipeList recipeList = new RecipeList("src/test/testEditExistingRecipe");
    
        recipeList.addRecipe("Recipe1", "RecipeText1", "lunch", 1,base64Placeholder);
        recipeList.addRecipe("Recipe2", "RecipeText2", "lunch", 1,base64Placeholder);

        recipeList.editRecipe(1,"Newer Recipe1", "Newer RecipeText", 1,base64Placeholder);

        assertEquals("Newer Recipe1", recipeList.getRecipe(1).getRecipeTitle());
        assertEquals("Newer RecipeText", recipeList.getRecipe(1).getRecipeText());
    
        assertEquals("Recipe2", recipeList.getRecipe(2).getRecipeTitle());
        assertEquals("RecipeText2", recipeList.getRecipe(2).getRecipeText());

        File file = new File("src/test/testEditExistingRecipe.list");
        file.delete(); // remove test list
    }
    @Test 
    void testEditNonExistingRecipe(){
        RecipeList recipeList = new RecipeList("src/test/testEditNonExistingRecipe");
    
        recipeList.addRecipe("Recipe1", "RecipeText1", "lunch", 1,base64Placeholder);
        recipeList.addRecipe("Recipe2", "RecipeText2", "lunch", 1,base64Placeholder);

        assertFalse(recipeList.editRecipe(3,"Newer Recipe1","Newer RecipeText", 1,base64Placeholder));

        assertEquals("Recipe1", recipeList.getRecipe(1).getRecipeTitle());
        assertEquals("RecipeText1", recipeList.getRecipe(1).getRecipeText());
    
        assertEquals("Recipe2", recipeList.getRecipe(2).getRecipeTitle());
        assertEquals("RecipeText2", recipeList.getRecipe(2).getRecipeText());
        
        File file = new File("src/test/testEditNonExistingRecipe.list");
        file.delete(); // remove test list
    }

    @Test
    void testGenerateHTML(){
       int newRecipeID = 123;
        String newRecipeTitle = "Beans Recipe";
        String newRecipeText = "Beans Recipe\n Ingredients:\n1 Can of Beans\n\nInstructions:\nStep 1: Put beans on plate\n";

        Recipe newRecipe = new Recipe(newRecipeID,newRecipeTitle,newRecipeText, "lunch",1,base64Placeholder);

        String html = newRecipe.generateHTML("src/test/shared/");

        assertTrue(html.contains(newRecipeTitle));
        assertTrue(html.contains("Can of Beans"));

        assertEquals(newRecipeID, newRecipe.getRecipeID());
        assertEquals(newRecipeText, newRecipe.getRecipeText());
        assertEquals(newRecipeTitle, newRecipe.getRecipeTitle());

    }

    @AfterEach
    void cleanup() {
        // Delete any files that were created during the tests
        File sharedDir = new File("src/test/shared");
        if (sharedDir.exists()) {
            for (File file : sharedDir.listFiles()) {
                file.delete();
            }
        }
    }



    /*public static void main(String[] args){
        Recipe L = new Recipe(0, "ball", "two eggs");
        Recipe G = new Recipe(1, "souffle", "three eggs");

        RecipeList list = new RecipeList();

        list.newRecipe("ball","2 egg");
        list.newRecipe("bll","3 egg");

        System.out.println(list);

        list.saveToDisk();


        RecipeList list2 = new RecipeList();
        list2.loadFromDisk();
        System.out.println(list2);

    }*/
}
