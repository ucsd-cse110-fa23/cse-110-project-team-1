import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.io.FileInputStream;
import java.io.FileOutputStream; 
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;  
import java.util.Iterator; 

//Referenced this for serialization and deserialization code
//https://www.geeksforgeeks.org/how-to-serialize-hashmap-in-java/

public class RecipeList {
    
    private int highestIndex;  //index of the most recently created recipe
    
    private HashMap<Integer, Recipe> recipeList;

    public RecipeList(){
        highestIndex = 0;
        recipeList = new HashMap<Integer, Recipe>();
    }
    
    public void saveToDisk(){
        try { 
            FileOutputStream myFileOutStream 
                = new FileOutputStream( 
                    "recipe.list"); //create outputstream for file
  
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

    public void loadFromDisk(){
        HashMap<Integer, Recipe> newHashMap = null; 
  
        try { 
            FileInputStream fileInput = new FileInputStream( 
                "recipe.list"); //create pointer to file 
  
            ObjectInputStream objectInput 
                = new ObjectInputStream(fileInput); //attach pointer to object input stream
  
            newHashMap = (HashMap)objectInput.readObject(); //take object in from stream
  
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
        Set set = newHashMap.entrySet(); 
        Iterator iterator = set.iterator(); 
  
        while (iterator.hasNext()) { 
            Map.Entry entry = (Map.Entry)iterator.next(); 
            if((Integer)entry.getKey() > highestIndex){
                highestIndex = (Integer)entry.getKey();
            }
        }

        recipeList = newHashMap; //update recipeList
    }

    public Recipe getRecipe(int recipeID){
        return recipeList.get(recipeID);
    }
    public void newRecipe(String title, String recipeText){
        int index = ++highestIndex; //increment highestIndex with every new recipe creataed
        Recipe r = new Recipe(index,title, recipeText);
        recipeList.put(index,r);
    }
    
    public String toString(){
        String s = "";
        Set set = recipeList.entrySet(); 
        Iterator iterator = set.iterator(); 
  
        while (iterator.hasNext()) { 
            Map.Entry entry = (Map.Entry)iterator.next(); 
  
            s += "key : " + entry.getKey() 
                             + " & Value : "+entry.getValue()+"\n";
            
        }
        return s;
    }
}

