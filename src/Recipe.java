import java.io.Serializable;

public class Recipe implements Serializable{

    private int recipeID;
    private String title;
    private String recipeText;

    public Recipe(int recipeID, String title, String recipeText){
        this. recipeID = recipeID;
        this.title = title;
        this.recipeText = recipeText;
    }

    public int getRecipeID(){
        return recipeID;
    }

    public String getTitle(){
        
    
        return title;
    }

    public String getRecipeText(){
        return recipeText;
    }

    public void setTitle(String newTitle){
        title = newTitle;
    }

    public void setRecipeText(String newRecipe){
        recipeText = newRecipe;
    }

    public String toString(){
        return title + ": "+ recipeText;
    }

}

