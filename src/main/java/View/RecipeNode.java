package View;

import org.json.JSONObject;

import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

public class RecipeNode extends HBox{

    private int recipeID; //index of recipe to be used for ordering and adressing
    private Label recipeTitle; 
    private String recipeText;
    private String mealType;
   
    public RecipeNode(int recipeID, String recipeTitle, String recipeText, String mealType){
        this.recipeID = recipeID;
        this.recipeTitle = new Label(recipeTitle);
        this.recipeText = recipeText;
        this.mealType = mealType;
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

    public String getMealType(){
        return mealType;
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
        recipe.put("mealType", getMealType());
        return recipe;
    }
    public static RecipeNode jsonToRecipeNode(JSONObject json){
        RecipeNode newRecipe = new RecipeNode(json.getInt("recipeID"), json.getString("recipeTitle").replaceAll("\\r?", "")
        , json.getString("recipeText"), json.getString("mealType"));
        return newRecipe;
    }

}

