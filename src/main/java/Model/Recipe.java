package Model;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;

import org.json.JSONObject;

public class Recipe implements Serializable{

    private int recipeID; //index of recipe to be used for ordering and adressing
    private String recipeTitle; 
    private String recipeText;
    private String mealType;
    private int ownerID; // The ID of the user who owns the recipe
    private boolean shared;
    private String base64Image;
    private static final long serialVersionUID = -3203943774493510754L;

    public Recipe(int recipeID, String recipeTitle, String recipeText, String mealType, int ownerID, String base64Image){
        this.recipeID = recipeID;
        this.recipeTitle = recipeTitle;
        this.recipeText = recipeText;
        this.mealType = mealType;
        this.ownerID = ownerID;
        this.shared = false;
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
        if(shared)
            generateHTML();
    }

    public void setRecipeText(String newRecipe){
        recipeText = newRecipe;
        if(shared)
            generateHTML();
    }

    public void setBase64Image(String base64Image) {
        this.base64Image = base64Image;
    }

    public String toString(){
        return recipeTitle + ": "+ recipeText;
    }

    public void share(){
        shared = true;
        generateHTML();
    }

    public boolean getShared(){
        return shared;
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

    //https://www.baeldung.com/java-write-to-file
    public void generateHTML(){
        try{
            String str = "<html>\r\n" + //
                    "    <style>\r\n" + //
                    "        body {\r\n" + //
                    "            background-color: rgb(192, 192, 192);\r\n" + //
                    "        }\r\n" + //
                    "    </style>\r\n" + //
                    "\r\n" + //
                    "    <head>\r\n" + //
                    "        <meta charset=\"UTF-8\" />\r\n" + //
                    "    </head>\r\n" + //
                    "    <h2>\r\n" + //
                    "        "+ this.recipeTitle +"\r\n" + //
                    "    </h2>\r\n" + //
                    "\r\n" + //
                    "    <body>\r\n" + //
                    "        <title>\r\n" + //
                    "            "+ this.recipeTitle +"\r\n" + //
                    "        </title>\r\n" + //
                    "        "+ this.recipeText.replaceAll("\n", "<br>") +"\r\n" + //
                    "    </body>\r\n" + //
                    "\r\n" + //
                    "</html>";
            
            
            // create shared directory if we don't have one
            File sharedDir = new File("shared/");
            if (!sharedDir.exists()){
                sharedDir.mkdirs();
            }

            FileOutputStream outputStream = new FileOutputStream("shared/recipe"+recipeID+".html");
            byte[] strToBytes = str.getBytes();
            outputStream.write(strToBytes);
            outputStream.close();
        }catch(Exception e){

        }
    }

    public boolean equals(Recipe rhs) {
        return (this.recipeID == rhs.recipeID) &&
            (this.recipeTitle.equals(recipeTitle)) &&
            (this.recipeText.equals(rhs.recipeText)) &&
            (this.mealType.equals(rhs.mealType)) &&
            (this.ownerID == rhs.ownerID) &&
            (this.shared == rhs.shared);
    }
}

