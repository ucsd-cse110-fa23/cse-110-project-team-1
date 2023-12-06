package Model;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.json.JSONArray;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Iterator;

//Referenced this for serialization and deserialization code
//https://www.geeksforgeeks.org/how-to-serialize-hashmap-in-java/

/**
 * The RecipeList class represents a list of recipes. Each recipe is stored as a Recipe object in a HashMap,
 * with a unique integer ID as the key. The class provides methods for adding, deleting, and editing recipes,
 * as well as for saving the recipe list to disk and loading it from disk. The class also maintains a highestIndex
 * field, which is used to generate unique IDs for the recipes, and a listName field, which is used as the file name
 * when saving the list to disk.
 */
public class RecipeList {

    private int highestIndex;
    private String listName;
    private HashMap<Integer, Recipe> recipeList;
    private String shareDirectory;

    /**
     * This is the constructor for the RecipeList class. 
     * It initializes the recipeList as a new HashMap
     * Sets the highestIndex to 0, and sets the listName to the input string.
     *
     * @param listName The name of the new recipe list.
     */
    public RecipeList(String listName) {
        highestIndex = 0;
        recipeList = new HashMap<Integer, Recipe>();
        this.listName = listName; // default
        this.shareDirectory = "shared/";

    }

    /**
     * This method saves the current state of recipeList to a file on disk.
     * The file name is derived from listName.
     * If an error occurs during the process, it prints the stack trace of the exception.
     */
    public void saveToDisk() {
        try {
            FileOutputStream myFileOutStream = new FileOutputStream(
                    listName + ".list"); // create outputstream for file

            ObjectOutputStream myObjectOutStream = new ObjectOutputStream(myFileOutStream); // attaches the fileobject
                                                                                            // stream to object

            myObjectOutStream.writeObject(recipeList); // write the objecct to the output object stream

            // closing FileOutputStream and
            // ObjectOutputStream
            myObjectOutStream.close();
            myFileOutStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * This method loads the recipeList from a file on disk.
     * The file name is derived from listName. If the file does not exist or
     * an error occurs during the process, it handles the exception accordingly.
     *
     */
    // Suppress Type Safety for objectInput, since we are sure of <Int, Recipe> type
    @SuppressWarnings("unchecked")
    public void loadFromDisk() {
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
            newHashMap = (HashMap<Integer, Recipe>) objectInput.readObject();
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

        recipeList = newHashMap; // update recipeList
    }

    /**
     * This method updates the highestIndex field with the highest key value from
     * the input HashMap.
     * It iterates through the HashMap and finds the largest entry to update
     * highestIndex.
     *
     * @param newHashMap A HashMap containing recipe IDs as keys and Recipe objects
     *                   as values.
     */
    private void updateHighestIndex(HashMap<Integer, Recipe> newHashMap) {
        // Iterate through the hashmap and find largest entry to update highestIndex
        Set<Entry<Integer, Recipe>> set = newHashMap.entrySet();
        Iterator<Entry<Integer, Recipe>> iterator = set.iterator();

        while (iterator.hasNext()) {
            Map.Entry<Integer, Recipe> entry = (Map.Entry<Integer, Recipe>) iterator.next();
            if ((Integer) entry.getKey() > highestIndex) {
                highestIndex = (Integer) entry.getKey();
            }
        }
    }

    /**
     * This method retrieves a recipe from the recipe list based on the input ID.
     *
     * @param recipeID The ID of the recipe to be retrieved.
     *
     * @return The Recipe object corresponding to the input ID.
     */
    public Recipe getRecipe(int recipeID) {
        return recipeList.get(recipeID);
    }

    /**
     * This method shares a recipe from the recipe list based on the input ID.
     *
     * @param recipeID The ID of the recipe to be shared.
     */
    public boolean shareRecipe(int recipeID) {
        Recipe r = recipeList.get(recipeID);
        if(r != null){
            r.share(shareDirectory);
            return true;
        }
        return false;
    }

    /**
     * This method retrieves the most recently added recipe from the recipe list.
     *
     * @return The Recipe object that was most recently added to the list.
     */
    public Recipe getMostRecent() {
        return recipeList.get(highestIndex);
    }

    /**
     * This method adds a new recipe to the recipe list and saves the updated list
     * to disk.
     * The recipe is identified by a unique ID, which is the incremented value of
     * the highest index.
     *
     * @param recipeTitle The title of the new recipe.
     * @param recipeText  The text of the new recipe.
     * @param mealType    The meal type of the new recipe.
     *
     * @return The ID of the newly added recipe.
     */
    public int addRecipe(String recipeTitle, String recipeText, String mealType, int ownerID, String base64Image) {
        int recipeID = ++highestIndex; // increment highestIndex with every new recipe creataed
        Recipe r = new Recipe(recipeID, recipeTitle, recipeText, mealType, ownerID, base64Image);
        recipeList.put(recipeID, r);
        //System.out.println("Added " + recipeID);
        saveToDisk();
        return recipeID;
    }

    /**
     * This method deletes a recipe from the recipe list based on the input ID and
     * saves the updated list to disk.
     *
     * @param recipeID The ID of the recipe to be deleted.
     *
     * @return Boolean - Returns true if the recipe was successfully deleted, false
     *         otherwise.
     */
    public boolean deleteRecipe(int recipeID, int ownerID) {
        if (recipeList.get(recipeID) != null && recipeList.get(recipeID).getOwnerID() == ownerID) {
            recipeList.remove(recipeID);
            saveToDisk();
            return true;
        }
        return false;
    }

    /**
     * This method edits a recipe in the recipe list based on the input ID and saves
     * the updated list to disk.
     *
     * @param recipeID       The ID of the recipe to be edited.
     * @param newRecipeTitle The new title for the recipe.
     * @param newRecipeText  The new text for the recipe.
     *
     * @return Boolean - Returns true if the recipe was successfully edited, false
     *         otherwise.
     */
    public boolean editRecipe(int recipeID, String newRecipeTitle, String newRecipeText, int ownerID, String newbase64Image) {
        Recipe recipe = recipeList.get(recipeID);
        if (recipe != null && recipe.getOwnerID() == ownerID) {
            recipe.setRecipeText(newRecipeText, shareDirectory);
            recipe.setRecipeTitle(newRecipeTitle, shareDirectory);
            recipe.setBase64Image(newbase64Image);
            saveToDisk();
            return true;
        }
        return false;
    }

    /**
     * This method returns a string representation of the recipe list.
     * The string includes the keys (recipe IDs) and values (Recipe objects) of all
     * entries in the list.
     *
     * @return A string representation of the recipe list.
     */
    public String toString() {
        String s = "";
        Set<Entry<Integer, Recipe>> set = recipeList.entrySet();
        Iterator<Entry<Integer, Recipe>> iterator = set.iterator();

        while (iterator.hasNext()) {
            Map.Entry<Integer, Recipe> entry = (Map.Entry<Integer, Recipe>) iterator.next();

            s += "key : " + entry.getKey()
                    + " & Value : " + entry.getValue() + "\n";

        }
        return s;
    }

    /**
     * This method converts the recipe list to a JSONObject.
     * Each entry in the list is converted to a JSONArray, with the recipe ID,
     * title, and text as properties.
     *
     * @return A JSONArray representing the recipe list.
     */

    public JSONArray toJSONObject() {
        JSONArray allRecipes = new JSONArray();
        Set<Entry<Integer, Recipe>> set = recipeList.entrySet();
        Iterator<Entry<Integer, Recipe>> iterator = set.iterator();

        int recipeIndex = 0;
        while (iterator.hasNext()) {
            Map.Entry<Integer, Recipe> entry = iterator.next();
            Recipe r = ((Recipe) entry.getValue());
            //System.out.println(r.getRecipeID());
            allRecipes.put(recipeIndex++, r.toJson());
        }
        return allRecipes;
    }

    public JSONArray getUserRecipes(Integer userID) {
        JSONArray userRecipes = new JSONArray();
        Set<Entry<Integer, Recipe>> set = recipeList.entrySet();
        Iterator<Entry<Integer, Recipe>> iterator = set.iterator();
    
        int recipeIndex = 0;
        while (iterator.hasNext()) {
            Map.Entry<Integer, Recipe> entry = iterator.next();
            Recipe r = ((Recipe) entry.getValue());
            if (r.getOwnerID() == userID) {
                userRecipes.put(recipeIndex++,r.toJson());
            }
        }
        return userRecipes;
    }

    /**
     * This method changes the name of the recipe list.
     *
     * @param listName The new name for the recipe list.
     */
    public void changeListName(String listName) {
        this.listName = listName;
    }

    /**
     * This method checks if the recipe list is empty.
     *
     * @return Boolean - Returns true if the recipe list is empty, false otherwise.
     */
    public boolean isEmpty() {
        return recipeList.isEmpty();
    }

    public String getShareDirectory(){
        return this.shareDirectory;
    }

    public void setShareDirectory(String shareDirectory){
        this.shareDirectory = shareDirectory;
    }
}
