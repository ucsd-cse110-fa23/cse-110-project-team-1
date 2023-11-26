package View;

import java.io.File;
import java.io.IOException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import Controller.RequestHandler;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;


public class ViewModel {
	private static final String DINNER = "dinner";
    private static final String LUNCH = "lunch";
    private static final String BREAKFAST = "breakfast";
	
	private String newlyValidatedMealType;
	private String server_url;
	private RequestHandler req;
	private AudioRecorder audioRecorder;


	/*
	* Pulls recipies from server url passed in
	* Returns a Listview of Recipes in order from newest to oldest
	*/
	public ViewModel(RequestHandler req, String server_url, AudioRecorder audioRecorder){
		this.req = req;
		this.server_url = server_url;
		this.audioRecorder = audioRecorder;
		newlyValidatedMealType = "";
	}
	
	public ListView<HBox> pullRecipes(User user){
		try {
			String allRecipes;
			allRecipes = req.performGET(server_url+"?all", user);
			JSONArray allRec = new JSONArray(allRecipes);
			return createRecipeListView(allRec);
		} catch (IOException e) {
			ErrorAlert.showError("Unable to contact server to get recipes");
			e.printStackTrace();
		}
		return new ListView<HBox>();
    }
	
	/*
	* Returns if mealtype recorded is valid
	*/
	public boolean validateMealType(String mealType){
		mealType = mealType.toLowerCase().trim();
		if(mealType.contains(BREAKFAST) || mealType.contains(LUNCH) || mealType.contains(DINNER)){
			System.out.println("Valid Mealtype: " + mealType);
			return true;
		}
		return false;
	}

	public RecipeNode requestNewRecipe(User user) {
		String response = "";
		try {
			response = req.performPOST(server_url, new File("recording.wav"), "ingredients", newlyValidatedMealType, user);
		} catch (IOException e) {
			ErrorAlert.showError("Unable to contact server to generate new recipe");
			e.printStackTrace();
		}
		try {
			response = response.replaceAll("\r\n?", "\n");
			return RecipeNode.jsonToRecipeNode(new JSONObject(response));
		} catch (JSONException e) {
			ErrorAlert.showError("Invalid JSON response from server, try again");
			e.printStackTrace();
			return null;
		} catch (NullPointerException e){
			ErrorAlert.showError("Server response null, try again.");
			e.printStackTrace();
			return null;
		}
	}
	
	
	public boolean requestMealTypeCheck(User user) {
		String response = "";
        try {
			response = req.performPOST(server_url, new File("recording.wav"), "mealType", "none", user);
			if(validateMealType(response)){
				newlyValidatedMealType = getMealTypeFromResponse(response);
				return true;
			}
        } catch (IOException e) {
			ErrorAlert.showError("Unable to contact server to validate meal type");
            return false;
        }
		
        return false;
    }

	public void performPutRequest(RecipeNode recipe, User user){
        try {
			req.performPUT(server_url, recipe.getRecipeID(), recipe.getRecipeTitle(), recipe.getRecipeText(), recipe.getMealType(), user);
		} catch (IOException e) {
			ErrorAlert.showError("Unable to contact server to save recipe");
			e.printStackTrace();
		}
    }

    public void performDeleteRequest(int recipeId, User user){
        try {
			req.performDELETE(server_url, recipeId, user);
		} catch (IOException e) {
			ErrorAlert.showError("Unable to contact server to delete recipe");
			e.printStackTrace();
		}
    }
	
	private String getMealTypeFromResponse(String response) {
		String lowerCaseResponse = response.toLowerCase();
        if(lowerCaseResponse.contains(DINNER)) return DINNER;
        if(lowerCaseResponse.contains(LUNCH)) return LUNCH;
        if(lowerCaseResponse.contains(BREAKFAST)) return BREAKFAST;
        return "";
    }
	
	
	private ListView<HBox> createRecipeListView(JSONArray allRec) {
		ListView<HBox> listView = new ListView<>();
		for (int i = 0; i < allRec.length(); i++) {
			JSONObject r = allRec.getJSONObject(i);
			RecipeNode newRecipe = RecipeNode.jsonToRecipeNode(r);
			newRecipe.getChildren().addAll(new Label(newRecipe.getRecipeTitle()));
			listView.getItems().add(0,newRecipe);
		}
		return listView;
	}

	public void startRecording() {
		this.audioRecorder.startRecording();
		System.out.println("Started recording.");
	}
	
	public void stopRecording() {
		this.audioRecorder.stopRecording();
		System.out.println("Stopped recording.");
	}

}