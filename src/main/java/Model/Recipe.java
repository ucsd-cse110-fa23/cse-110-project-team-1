package Model;

import java.io.Serializable;

import org.json.JSONObject;

public class Recipe implements Serializable{

    private int recipeID; //index of recipe to be used for ordering and adressing
    private String recipeTitle; 
    private String recipeText;
    private String mealType;
    private int ownerID; // The ID of the user who owns the recipe
    private String base64Image;
    private static final long serialVersionUID = -3203943774493510754L;

    public Recipe(int recipeID, String recipeTitle, String recipeText, String mealType, int ownerID, String base64Image){
        this.recipeID = recipeID;
        this.recipeTitle = recipeTitle;
        this.recipeText = recipeText;
        this.mealType = mealType;
        this.ownerID = ownerID;
        this.base64Image = base64Image;
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

    public String getMealType() {
        return mealType;
    }

    public int getOwnerID() {
        return ownerID;
    }

    public String getBase64Image() {
        return base64Image;
    }

    public void setRecipeTitle(String newTitle){
        recipeTitle = newTitle;
    }

    public void setRecipeText(String newRecipe){
        recipeText = newRecipe;
    }

    public void setBase64Image(String base64Image) {
        this.base64Image = base64Image;
    }

    public String toString(){
        return recipeTitle + ": "+ recipeText;
    }

    public JSONObject toJson(){
        JSONObject recipe = new JSONObject();
        recipe.put("recipeText", getRecipeText());
        recipe.put("recipeTitle", getRecipeTitle());
        recipe.put("recipeID", getRecipeID());
        recipe.put("mealType", getMealType());
        recipe.put("ownerID", getOwnerID());
        recipe.put("base64Image", getBase64Image());
        return recipe;
    }

}

