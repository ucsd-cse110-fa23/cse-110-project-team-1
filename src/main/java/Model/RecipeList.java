package Model;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream; 
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;  
import java.util.Iterator; 

//Referenced this for serialization and deserialization code
//https://www.geeksforgeeks.org/how-to-serialize-hashmap-in-java/

/* 
 * Contains:
 * HashMap for Recipe objects
 * Integer highestIndex for tracking latest recipe
 * String listName for the name of the recipe list
 * 
 * Functions:
 * Manages recipes (add, delete, edit)
 * Saves and loads list from disk
 * Maintains unique IDs for recipes with highestIndex
 */
public class RecipeList {
    
    private int highestIndex;  //recipeID of the most recently created recipe
    private String listName;
    
    private HashMap<Integer, Recipe> recipeList;

    public RecipeList(String listName){
        highestIndex = 0;
        recipeList = new HashMap<Integer, Recipe>();
        this.listName = listName; //default
    }
    
    /*
     * Input: None
     * Output: None
     * Operation: Saves the current state of recipeList to a file on disk. 
     * The file name is derived from listName. 
     * If an error occurs during the process, it prints the stack trace of the exception.
     */
    public void saveToDisk(){
        try { 
            FileOutputStream myFileOutStream 
                = new FileOutputStream( 
                    listName + ".list"); //create outputstream for file
  
            ObjectOutputStream myObjectOutStream 
                = new ObjectOutputStream(myFileOutStream); //attaches the fileobject stream to object
  
            myObjectOutStream.writeObject(recipeList); //write the objecct to the output object stream
  
            // closing FileOutputStream and 
            // ObjectOutputStream 
            myObjectOutStream.close(); 
            myFileOutStream.close(); 
        } 
        catch (IOException e) { 
            e.printStackTrace(); 
        }
    }

    /* 
     * Input: None 
     * Output: None 
     * Operation: Loads the recipeList from a file on disk. 
     * The file name is derived from listName. If the file does not exist or 
     * an error occurs during the process, it handles the exception accordingly. 
     */
    // Suppress Type Safety for objectInput, since we are sure of <Int, Recipe> type
    @SuppressWarnings("unchecked")
    public void loadFromDisk(){
        HashMap<Integer, Recipe> newHashMap = null; 
        String filePath = listName + ".list";
        File file = new File(filePath);

        if (!file.exists()) {
            saveToDisk();
            return;
        }
        try { 
            FileInputStream fileInput = new FileInputStream(filePath); 
            ObjectInputStream objectInput = new ObjectInputStream(fileInput); 
            newHashMap = (HashMap<Integer, Recipe>)objectInput.readObject(); 
            objectInput.close(); 
            fileInput.close(); 
        } 
  
        catch (IOException obj1) { 
            obj1.printStackTrace(); 
            return; 
        } 
  
        catch (ClassNotFoundException obj2) { 
            System.out.println("Class not found"); 
            obj2.printStackTrace(); 
            return; 
        } 
  
        updateHighestIndex(newHashMap);

        recipeList = newHashMap; //update recipeList
    }
    /*
     * Input: newHashMap - A HashMap containing recipe IDs as keys and Recipe objects as values.
     * Output: None
     * Operation: Updates the highestIndex field with the highest key value from the input HashMap.
     */
    private void updateHighestIndex(HashMap<Integer,Recipe> newHashMap) {
        // Iterate through the hashmap and find largest entry to update highestIndex
        Set<Entry<Integer, Recipe>> set = newHashMap.entrySet(); 
        Iterator<Entry<Integer, Recipe>> iterator = set.iterator(); 
  
        while (iterator.hasNext()) { 
            Map.Entry<Integer, Recipe> entry = (Map.Entry<Integer, Recipe>)iterator.next(); 
            if((Integer)entry.getKey() > highestIndex){
                highestIndex = (Integer)entry.getKey();
            }
        }
    }

    public Recipe getRecipe(int recipeID){
        return recipeList.get(recipeID);
    }

    public Recipe getMostRecent(){
        return recipeList.get(highestIndex);
    }
    /*
     * Input: recipeTitle - The title of the new recipe, recipeText - The text of the new recipe.
     * Operation: Adds a new recipe to the recipeList and saves the updated list to disk.
     * Output: recipeID - The ID of the newly added recipe.
     */
    public int addRecipe(String recipeTitle, String recipeText){
        int recipeID = ++highestIndex; //increment highestIndex with every new recipe creataed
        Recipe r = new Recipe(recipeID,recipeTitle, recipeText);
        recipeList.put(recipeID,r);
        saveToDisk();
        return recipeID;
    }
    /*
     * Input: recipeID - The ID of the recipe to be deleted.
     * Output: Boolean - Returns true if the recipe was successfully deleted, false otherwise.
     * Operation: Deletes a recipe from the recipeList based on the input ID and saves the updated list to disk.
     */
    public boolean deleteRecipe(int recipeID){
        if(recipeList.get(recipeID) != null){
            recipeList.remove(recipeID);
            saveToDisk();
            return true;
        }
        return false;
    }
    /*
     * Input: recipeID - The ID of the recipe to be edited, newRecipeTitle - The new title for the recipe, newRecipeText - The new text for the recipe.
     * Output: Boolean - Returns true if the recipe was successfully edited, false otherwise.
     * Operation: Edits a recipe in the recipeList based on the input ID and saves the updated list to disk.
     */
    public boolean editRecipe(int recipeID, String newRecipeTitle, String newRecipeText){
        if(recipeList.get(recipeID) != null){
            recipeList.get(recipeID).setRecipeText(newRecipeText);
            recipeList.get(recipeID).setRecipeTitle(newRecipeTitle);
            saveToDisk();
            return true;
        }
        return false;
    }
    
    public String toString(){
        String s = "";
        Set<Entry<Integer, Recipe>> set = recipeList.entrySet(); 
        Iterator<Entry<Integer, Recipe>> iterator = set.iterator(); 
  
        while (iterator.hasNext()) { 
            Map.Entry<Integer, Recipe> entry = (Map.Entry<Integer, Recipe>)iterator.next(); 
  
            s += "key : " + entry.getKey() 
                             + " & Value : "+entry.getValue()+"\n";
            
        }
        return s;
    }
    public JSONObject toJSONObject(){
        JSONObject allRecipes = new JSONObject();
        Set<Entry<Integer, Recipe>> set = recipeList.entrySet(); 
        Iterator<Entry<Integer, Recipe>> iterator = set.iterator(); 
        int recipeIndex = 0;
  
        while (iterator.hasNext()) { 
            Map.Entry<Integer, Recipe> entry = (Map.Entry<Integer, Recipe>)iterator.next(); 
            Recipe r = ((Recipe) entry.getValue());
            JSONObject recipe = new JSONObject();
            recipe.put("recipeText", r.getRecipeText());
            recipe.put("recipeTitle", r.getRecipeTitle());
            recipe.put("recipeID", r.getRecipeID());
            allRecipes.put(Integer.toString(recipeIndex++), recipe);  
        }
        return allRecipes;
    }
    public void changeListName(String listName){
        this.listName = listName;
    }
     public boolean isEmpty(){
        return recipeList.isEmpty();
     }
}

