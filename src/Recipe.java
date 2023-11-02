import java.io.Serializable;

public class Recipe implements Serializable{

    private int recipeID; //index of recipe to be used for ordering and adressing
    private String recipeTitle; 
    private String recipeText;

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

}

