package View;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import Controller.RequestHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.OverrunStyle;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;

import java.io.*;


public class ViewModel {
	private static final String DINNER = "dinner";
    private static final String LUNCH = "lunch";
    private static final String BREAKFAST = "breakfast";
	
	private String newlyValidatedMealType;
	private String server_url;
	private RequestHandler req;
	private AudioRecorder audioRecorder;
	private String savedLoginFileName;


	/*
	* Pulls recipies from server url passed in
	* Returns a Listview of Recipes in order from newest to oldest
	*/
	public ViewModel(RequestHandler req, String server_url, AudioRecorder audioRecorder, String savedLoginFileName){
		this.req = req;
		this.server_url = server_url;
		this.audioRecorder = audioRecorder;
		newlyValidatedMealType = "";
		this.savedLoginFileName = savedLoginFileName;
	}
	
	public ListView<HBox> pullRecipes(User user) throws Exception{
		try {
			String allRecipes;
			allRecipes = req.performGET(server_url+"?all", user);
			if(allRecipes.contains("Invalid username")){
				Popup.showError("Invalid username or password when pulling recipes");
				throw new Exception("Invalid username or password");
			}
			JSONArray allRec = new JSONArray(allRecipes);
			return createRecipeListView(allRec);
		} catch (IOException e) {
			Popup.showError("Unable to contact server to get recipes");
			throw e;		
		}
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

	public RecipeNode requestNewRecipe(User user) throws IOException {
		String response = "";
		try {
			response = req.performPOST(server_url, new File("recording.wav"), "ingredients", newlyValidatedMealType, user);
			checkIfAPIError(response);
		} catch (IOException e) {
			Popup.showError("Unable to contact server to generate new recipe");
			throw e;		
		}
		try {
			response = response.replaceAll("\r\n?", "\n");
			return RecipeNode.jsonToRecipeNode(new JSONObject(response));
		} catch (JSONException e) {
			Popup.showError("Invalid JSON response from server, try again");
			e.printStackTrace();
			return null;
		} catch (NullPointerException e){
			Popup.showError("Server response null, try again.");
			e.printStackTrace();
			return null;
		}
	}
	
	
	public boolean requestMealTypeCheck(User user) throws IOException {
		String response = "";
        try {
			response = req.performPOST(server_url, new File("recording.wav"), "mealType", "none", user);
			checkIfAPIError(response);
			if(validateMealType(response)){
				newlyValidatedMealType = getMealTypeFromResponse(response);
				return true;
			}
        } catch (IOException e) {
			Popup.showError("Unable to contact server to validate meal type");
            throw e;
        }
            		
		
        return false;
    }

	public void performPutRequest(RecipeNode recipe, User user) throws IOException{
        try {
			req.performPUT(server_url, recipe.getRecipeID(), recipe.getRecipeTitle(), recipe.getRecipeText(), recipe.getMealType(), recipe.getBase64Image(), user);
		} catch (IOException e) {
			Popup.showError("Unable to contact server to save recipe");
			throw e;
		}
    }

    public boolean performDeleteRequest(int recipeId, User user) throws IOException{
		try {
            boolean deleted = req.performDELETE(server_url, recipeId, user);
			return deleted;
        } catch (IOException e) {
			Popup.showError("Server Offline: Unable to contact server to delete recipe");
			throw e;	
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
			newRecipe.setMaxWidth(230);
			
// Setup title and mealtype labels for sidebar
			Label recipeTitle = new Label(newRecipe.getRecipeTitle());
			recipeTitle.setTextOverrun(OverrunStyle.ELLIPSIS);
			Label mealType = new Label(newRecipe.getMealType());
			BackgroundFill background_fill = new BackgroundFill(getMealTypeColor(mealType.getText()), CornerRadii.EMPTY, Insets.EMPTY);
			Background background = new Background(background_fill);
			mealType.setBackground(background);
			// Setting padding width and alignment
			mealType.setPadding(new Insets(0,3,0,3));
			recipeTitle.setPadding(new Insets(0,0,0,10));
			mealType.setMinWidth(55);
			mealType.setAlignment(Pos.CENTER);

			newRecipe.getChildren().addAll(mealType, recipeTitle);
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

	private Color getMealTypeColor(String mealType) {
		switch (mealType.toLowerCase()) {
			case BREAKFAST:
				return Color.rgb(230, 237, 138); // yellowish
			case LUNCH:
				return Color.rgb(145, 237, 138); // greenish
			case DINNER:
				return Color.rgb(237, 138, 146); // redish
			default:
				return Color.TRANSPARENT;
		}
	}
	
	public User getSavedUser() {
		String csvFile = savedLoginFileName;
		File file = new File(csvFile);
		if (!file.exists()) {
			return null;
		}
	
		String line = "";
		String cvsSplitBy = ",";
	
		try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
			while ((line = br.readLine()) != null) {
				// Check if line is not empty and contains a comma
				if (!line.isEmpty() && line.contains(cvsSplitBy)) {
					// use comma as separator
					String[] userInfo = line.split(cvsSplitBy);
	
					String username = userInfo[0];
					String password = userInfo[1];
					if(!isValidUserInfo(username,password)){
						return null;
					}
					System.out.println("Read Saved User Login");
					return new User(username, password);
				}
			}
		} catch (IOException e) {
			if (!file.exists()) {
				file.delete();
			}
		}
	
		return null;
	}

	public void saveUserLogin(String username, String password) {
		if (!isValidUserInfo(username, password)) {
			return;
		}
		String csvFile = savedLoginFileName;
		try {
			FileWriter writer = new FileWriter(csvFile);
			writer.append(username);
			writer.append(",");
			writer.append(password);
			writer.append("\n");
			writer.flush();
			writer.close();
			System.out.println("Saved User Login");
		} catch (IOException e) {
			System.out.println("Error writing to file: " + e.getMessage());
		}
	}

	public boolean handleLogin(String username, String password) throws IOException  {
		if (!isValidUserInfo(username, password)) {
			Popup.showError("Invalid username or password");
			return false;
		}
		User user = new User(username, password);
		System.out.println("Sending Login Request");
		boolean loggedIn;
		try {
			loggedIn = req.performLogin(server_url, user);
			if(loggedIn){
				return true;
			}
			Popup.showError("Invalid username or password");
			return false;	
		} catch (IOException e) {
			Popup.showError("Server Offline: Unable to contact server to log in");
			throw e;
		}	
	}
	
	public boolean createAccount(String username, String password) throws IOException {
		if (!isValidUserInfo(username, password)) {
			Popup.showError("Invalid username or password");
			return false;
		}
		User newUser = new User(username, password);

		System.out.println("Sending Create Account Request");
		try {
			boolean accountCreated =  req.performAccountCreation(server_url, newUser);
			if(accountCreated){
				return true;
			}
			Popup.showError("Username Already Taken");
            return false;
			
        } catch (IOException e) {
			Popup.showError("Server Offline: Unable to contact server to create account");
			throw e;
		}

	}

	public void handleLogout() {
		String csvFile = savedLoginFileName;
		File file = new File(csvFile);
		if (file.exists()) {
			System.out.println("Logged Out ");
			file.delete();
		}
	}

	
	public boolean handleShare(int recipeId, User user){
		
        try {
			req.performShare(server_url, recipeId, user);
		} catch (IOException e) {
			Popup.showError("Unable to contact server to share recipe");
			e.printStackTrace();
			return false;
		}
		return true;
    }

	private boolean isValidUserInfo(String username, String password) {
		//System.out.println("Checking user info ["+ username + ":" + password + "] " + !(username.isEmpty() || password.isEmpty()));
		return !(username.isEmpty() || password.isEmpty());
	}
	private void checkIfAPIError(String response) throws IOException {
		if(response.contains("Incorrect API key provided")){
		    Popup.showError("Incorrect API key provided \n You can find your API key at https://platform.openai.com/account/api-keys.");
		    throw new IOException("Incorrect API key provided");
		}
	}

	public String getServerURL(){
		return this.server_url;
	}
}