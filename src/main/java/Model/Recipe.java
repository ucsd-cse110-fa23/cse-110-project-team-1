package Model;

import java.io.Serializable;

import org.json.JSONObject;

public class Recipe implements Serializable{

    private int recipeID; //index of recipe to be used for ordering and adressing
    private String recipeTitle; 
    private String recipeText;
    private static final long serialVersionUID = -3203943774493510754L;

    public Recipe(int recipeID, String recipeTitle, String recipeText){
        this.recipeID = recipeID;
        this.recipeTitle = recipeTitle;
        this.recipeText = recipeText;
    }

    public int getRecipeID(){
        return recipeID;
    }

    public String getRecipeTitle(){
        return recipeTitle;
    }

    public String getRecipeText(){
        return recipeText;
    }

    public void setRecipeTitle(String newTitle){
        recipeTitle = newTitle;
    }

    public void setRecipeText(String newRecipe){
        recipeText = newRecipe;
    }

    public String toString(){
        return recipeTitle + ": "+ recipeText;
    }

    public JSONObject toJson(){
        JSONObject recipe = new JSONObject();
        recipe.put("recipeText", getRecipeText());
        recipe.put("recipeTitle", getRecipeTitle());
        recipe.put("recipeID", getRecipeID());
        return recipe;
    }

}

