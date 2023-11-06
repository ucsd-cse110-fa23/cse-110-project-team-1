package Model;
import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.BeforeEach;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;

public class RecipeTest { //temporary tests until JUnit is in place\
    //private Recipe recipe;

    @BeforeEach
    void setUp(){

    }
    @Test
    void testRecipeConstruction(){
       int newRecipeID = 123;
        String newRecipeTitle = "Beans";
        String newRecipeText = "Beans Recipe\n Ingredients:\n1 Can of Beans\n\nInstructions:\nStep 1: Put beans on plate\n";

        Recipe newRecipe = new Recipe(newRecipeID,newRecipeTitle,newRecipeText);

        assertEquals(newRecipeID, newRecipe.getRecipeID());
        assertEquals(newRecipeText, newRecipe.getRecipeText());
        assertEquals(newRecipeTitle, newRecipe.getRecipeTitle());

    }
    @Test
    void testRecipeListConstruction(){
        
        String newRecipeTitle = "Souffle";
        String newRecipeText = "Souffle Recipe\n Ingredients:\n2 Jars of eggs\n\nInstructions:\nStep 1: Put egg in ramen\n";

        RecipeList newRecipeList = new RecipeList("src/test/test");

        newRecipeList.addRecipe(newRecipeTitle, newRecipeText);

        assertEquals(1, newRecipeList.getRecipe(1).getRecipeID());
        assertEquals(newRecipeText, newRecipeList.getRecipe(1).getRecipeText());
        assertEquals(newRecipeTitle, newRecipeList.getRecipe(1).getRecipeTitle());

        File file = new File("src/test/test.list");
        file.delete(); // remove test list
        

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
