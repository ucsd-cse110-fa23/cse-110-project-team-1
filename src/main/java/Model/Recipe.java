package Model;

import java.io.File;
import java.io.FileOutputStream;
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

    public void setRecipeTitle(String newTitle, String shareDirectory){
        recipeTitle = newTitle;
        if(shared)
            generateHTML(shareDirectory);
    }

    public void setRecipeText(String newRecipe, String shareDirectory){
        recipeText = newRecipe;
        if(shared)
            generateHTML(shareDirectory);
    }

    public void setBase64Image(String base64Image) {
        this.base64Image = base64Image;
    }

    public String toString(){
        return recipeTitle + ": "+ recipeText;
    }

    public void share(String shareDirectory){
        shared = true;
        generateHTML(shareDirectory);
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
    public String generateHTML(String saveDirectory){
        try{
            String str = "<html>\r\n" + 
                    "    <head>\r\n" + 
                    "        <meta charset=\"UTF-8\" />\r\n" + 
                    "        <title>" + this.recipeTitle + "</title>\r\n" + 
                    "        <style>\r\n" + 
                    "            body {\r\n" + 
                    "                font-family: Arial, sans-serif;\r\n" + 
                    "                margin: 0;\r\n" + 
                    "                padding: 0;\r\n" + 
                    "                background-color: #f4f4f4;\r\n" + 
                    "            }\r\n" + 
                    "            .recipe {\r\n" + 
                    "                width: 80%;\r\n" + 
                    "                margin: auto;\r\n" + 
                    "                padding: 20px;\r\n" + 
                    "                background-color: #fff;\r\n" + 
                    "                box-shadow: 0 0 10px rgba(0,0,0,0.1);\r\n" + 
                    "            }\r\n" + 
                    "            .recipe h2 {\r\n" + 
                    "                color: #333;\r\n" + 
                    "            }\r\n" + 
                    "            .recipe p {\r\n" + 
                    "                font-size: 16px;\r\n" + 
                    "                color: #666;\r\n" + 
                    "            }\r\n" + 
                    "            .recipe img {\r\n" + 
                    "                width: 256px;\r\n" + 
                    "                height: 256px;\r\n" + 
                    "            }\r\n" + 
                    "        </style>\r\n" + 
                    "    </head>\r\n" + 
                    "    <body>\r\n" + 
                    "        <div class=\"recipe\">\r\n" + 
                    "            <h2>" + this.recipeTitle + "</h2>\r\n" + 
                    "            <p>" + this.recipeText.replace(this.recipeTitle+"\n", "").replaceAll("\n", "<br>") + "</p>\r\n" + 
                    "            <img src=\"" + this.base64Image + "\" />\r\n" + 
                    "        </div>\r\n" + 
                    "    </body>\r\n" + 
                    "</html>";
            
            
            // create shared directory if we don't have one"shared/"
            File sharedDir = new File(saveDirectory);
            if (!sharedDir.exists()){
                sharedDir.mkdirs();
            }

            FileOutputStream outputStream = new FileOutputStream(saveDirectory + "recipe"+recipeID+".html");
            byte[] strToBytes = str.getBytes();
            outputStream.write(strToBytes);
            outputStream.close();
            return str;
        }catch(Exception e){
            return "";
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

