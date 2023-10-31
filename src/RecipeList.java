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
    
    private int highestIndex;
    
    private HashMap<Integer, Recipe> recipeList;

    public RecipeList(){
        highestIndex = 0;
        recipeList = new HashMap<Integer, Recipe>();
    }
    
    public void saveToDisk(){
        try { 
            FileOutputStream myFileOutStream 
                = new FileOutputStream( 
                    "recipeList.txt"); 
  
            ObjectOutputStream myObjectOutStream 
                = new ObjectOutputStream(myFileOutStream); 
  
            myObjectOutStream.writeObject(recipeList); 
  
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
                "recipeList.txt"); 
  
            ObjectInputStream objectInput 
                = new ObjectInputStream(fileInput); 
  
            newHashMap = (HashMap)objectInput.readObject(); 
  
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
  
        System.out.println("Deserializing  HashMap.."); 
  
        // Displaying content in "newHashMap.txt" using 
        // Iterator 
        Set set = newHashMap.entrySet(); 
        Iterator iterator = set.iterator(); 
  
        while (iterator.hasNext()) { 
            Map.Entry entry = (Map.Entry)iterator.next(); 
  
            // System.out.print("key : " + entry.getKey() 
            //                  + " & Value : "); 
            // System.out.println(entry.getValue()); 
            if((Integer)entry.getKey() > highestIndex){
                highestIndex = (Integer)entry.getKey();
            }
        }

        recipeList = newHashMap;
    }

    public Recipe getRecipe(int recipeID){
        return recipeList.get(recipeID);
    }
    public void newRecipe(String title, String recipeText){
        int index = ++highestIndex;
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

