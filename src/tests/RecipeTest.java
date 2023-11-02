package tests;
import org.junit.jupiter.api.Test;

import main.Recipe;

import org.junit.jupiter.api.BeforeEach;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.beans.Transient;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class RecipeTest { //temporary tests until JUnit is in place\
    private Recipe recipe;

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
