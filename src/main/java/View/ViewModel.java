package View;

import java.io.File;
import java.io.IOException;

import org.json.JSONArray;
import org.json.JSONObject;

import Controller.RequestHandler;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;


public class ViewModel {
	private String newlyValidatedMealType;
	RequestHandler req;
	/*
	 * Pulls recipies from server http://localhost:8100/
	 * Returns a Listview of Recipes in order from newest to oldest
	 */
	public ViewModel(){
		req = new RequestHandler();
		newlyValidatedMealType = "";
	}
    public ListView<HBox> pullRecipes(){
		String allRecipes = req.performGET("http://localhost:8100/?all");
		JSONArray allRec = new JSONArray(allRecipes);
		ListView<HBox> listView = new ListView<>();
		for (int i = 0; i < allRec.length(); i++) {
			JSONObject r = allRec.getJSONObject(i);
            //System.out.println(allRec.getJSONObject(key));
            //System.out.println(r.getString("recipeTitle"));
			
			RecipeNode newRecipe = new RecipeNode(r.getInt("recipeID"),
												  r.getString("recipeTitle"),
												  r.getString("recipeText"));
												  
			newRecipe.getChildren().add(new Label(newRecipe.getRecipeTitle()));
            //System.out.println(newRecipe.toString());
			listView.getItems().add(0,newRecipe);
			//System.out.println("Added " + newRecipe.getRecipeID() + " to list");

			}
		return listView;
		//System.out.println(allRec.toString());
	}

	/*
	 * Returns if mealtype recorded is valid
	 */
	public boolean validateMealType(String mealType){
		mealType= mealType.toLowerCase().trim();
		if(mealType.contains("breakfast") || mealType.contains("lunch") || mealType.contains("dinner")){
			System.out.println("Valid Mealtype: " + mealType);
			return true;
		}
		return false;
	}

    public boolean requestMealTypeCheck() {
		RequestHandler req = new RequestHandler();
		String response = "";
		try {
			response = req.performPOST("http://localhost:8100/", new File("recording.wav"), "mealType", "none");
		} catch (IOException e) {
			e.printStackTrace();
		}
		//System.out.println("Response from server: " + response);
		if(validateMealType(response)){
			if(response.toLowerCase().contains("dinner")){newlyValidatedMealType = "dinner";}
			if(response.toLowerCase().contains("lunch")){newlyValidatedMealType = "lunch";}
			if(response.toLowerCase().contains("breakfast")){newlyValidatedMealType = "breakfast";}
			return true;
		}
		return false;
    }

    public RecipeNode requestNewRecipe() {
	
		String response;
		try {
			response = req.performPOST("http://localhost:8100/", new File("recording.wav"), "ingredients", newlyValidatedMealType);
			//System.out.println("Response from server: " + response);
			response = response.replaceAll("\\r\\n?", "\n");

			return RecipeNode.jsonToRecipeNode(new JSONObject(response));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
    }

}
