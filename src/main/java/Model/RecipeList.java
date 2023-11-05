package Model;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.FileOutputStream; 
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;  
import java.util.Iterator; 

//Referenced this for serialization and deserialization code
//https://www.geeksforgeeks.org/how-to-serialize-hashmap-in-java/

public class RecipeList {
    
    private int highestIndex;  //recipeID of the most recently created recipe
    private String listName;
    
    private HashMap<Integer, Recipe> recipeList;

    public RecipeList(String listName){
        highestIndex = 0;
        recipeList = new HashMap<Integer, Recipe>();
        this.listName = listName; //default
    }
    
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

    // Suppress Type Safety for objectInput, since we are sure of <Int, Recipe> type
    @SuppressWarnings("unchecked")
    public void loadFromDisk(){
        HashMap<Integer, Recipe> newHashMap = null; 
  
        try { 
            FileInputStream fileInput = new FileInputStream( 
                listName + ".list"); //create pointer to file 
  
            ObjectInputStream objectInput 
                = new ObjectInputStream(fileInput); //attach pointer to object input stream
  
            newHashMap = (HashMap<Integer, Recipe>)objectInput.readObject(); //take object in from stream
  
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
  
        // Iterate through the hashmap and find largest entry to update highestIndex
        Set<Entry<Integer, Recipe>> set = newHashMap.entrySet(); 
        Iterator<Entry<Integer, Recipe>> iterator = set.iterator(); 
  
        while (iterator.hasNext()) { 
            Map.Entry<Integer, Recipe> entry = (Map.Entry<Integer, Recipe>)iterator.next(); 
            if((Integer)entry.getKey() > highestIndex){
                highestIndex = (Integer)entry.getKey();
            }
        }

        recipeList = newHashMap; //update recipeList
    }

    public Recipe getRecipe(int recipeID){
        return recipeList.get(recipeID);
    }
    public void addRecipe(String recipeTitle, String recipeText){
        int recipeID = ++highestIndex; //increment highestIndex with every new recipe creataed
        Recipe r = new Recipe(recipeID,recipeTitle, recipeText);
        recipeList.put(recipeID,r);
        saveToDisk();
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
  
        while (iterator.hasNext()) { 
            Map.Entry<Integer, Recipe> entry = (Map.Entry<Integer, Recipe>)iterator.next(); 
            Recipe r = ((Recipe) entry.getValue());
            JSONObject recipe = new JSONObject();
            recipe.put("recipeText", r.getRecipeText());
            recipe.put("recipeTitle", r.getRecipeTitle());
            recipe.put("recipeID", r.getRecipeID());
            allRecipes.put(Integer.toString(r.getRecipeID()), recipe);  
        }
        return allRecipes;
    }
    public void changeListName(String listName){
        this.listName = listName;
    }

}

