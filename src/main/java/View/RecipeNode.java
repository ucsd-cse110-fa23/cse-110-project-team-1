package View;

import org.json.JSONObject;

import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

public class RecipeNode extends HBox{

    private int recipeID; //index of recipe to be used for ordering and adressing
    private Label recipeTitle; 
    private String recipeText;
   
    public RecipeNode(int recipeID, String recipeTitle, String recipeText){
        this.recipeID = recipeID;
        this.recipeTitle = new Label(recipeTitle);
        this.recipeText = recipeText;
    }

    public int getRecipeID(){
        return recipeID;
    }

    public String getRecipeTitle(){
        return recipeTitle.getText();
    }

    public String getRecipeText(){
        return recipeText;
    }

    public void setRecipeTitle(String newTitle){
        recipeTitle.setText(newTitle);
    }

    public void setRecipeText(String newRecipe){
        recipeText = newRecipe;
    }

    public String toString(){
        return recipeTitle.getText() + ": "+ recipeText;
    }

    public JSONObject toJson(){
        JSONObject recipe = new JSONObject();
        recipe.put("recipeText", getRecipeText());
        recipe.put("recipeTitle", getRecipeTitle());
        recipe.put("recipeID", getRecipeID());
        return recipe;
    }

}

