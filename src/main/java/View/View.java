package View;

import Controller.*;
import javafx.geometry.Insets;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.HBox;

public class View {

	public BorderPane root;
    private AudioRecorder audioRecorder;
	private ViewModel viewModel;

	private TextField recipeQuery = new TextField();

	private Button addNewRecipeButton = new Button("Generate Recipe");
	private Button editSavedRecipeButton = new Button("Test Edit Recipe");
	private Button deleteSavedRecipeButton = new Button("Delete Recipe");
	private Button newlyGeneratedRecipeSaveButton = new Button("Save Recipe");

    private Button startRecording = new Button("Start Recording");
    private Button stopRecordingMealType = new Button("Stop Mealtype Recording");
    private Button stopRecordingIngredients = new Button("Stop Ingredient Recording");
    private Button generateNewRecipe = new Button("Generate New Recipe");

	private Button backToHome = new Button("Back");

	private VBox recipeTitleListleftVbox;

	private VBox homePageVbox;
	private VBox savedRecipeDetailVbox; 
	private VBox newlyGeneratedRecipeDisplayVbox;
	private VBox recordMealTypeVbox;
	private VBox recordIngredientsVbox;

	//homepage labels
	private Label homePageText;
	private Label savedRecipeDescription;

	//record mealtype labels
	private Label recordMealTypeText;

	//record ingredient labels
	private Label recordIngredientsText;

	//newly generated receipe labels
	//private Label newlyGeneratedRecipeLabel;

	private int currentSelectedRecipeID;
	private RecipeNode newlyGeneratedRecipe;

	
	public View(ViewModel viewModel) {

		this.root = new BorderPane();
		this.viewModel = viewModel;
		this.root.setPadding(new Insets(10));
		//Left ListView with placeholders and a + button
		this.recipeQuery.setPromptText("Enter Recipe Query...");
		this.addNewRecipeButton.setOnAction(e -> this.onGenerateRequest());
		this.editSavedRecipeButton.setOnAction(e -> this.onEditRequest());
		this.deleteSavedRecipeButton.setOnAction(e -> this.onDeleteRequest());
        this.startRecording.setOnAction(e -> this.onRecordRequest());
		this.backToHome.setOnAction(e -> this.displayHomePage());
		this.generateNewRecipe.setOnAction(e -> this.buildRecordMealType());
        this.stopRecordingMealType.setOnAction(e -> {
			
			if (this.onStopRecordRequest("mealType")){
				buildRecordIngredients();
			}else{
				recordMealTypeText.setText("Click start recording and then say breakfast, lunch, or dinner to select a meal type.\nClick stop when you are done \n Imvalid Meal Type. Please try again.");
			}
		});
			
        this.stopRecordingIngredients.setOnAction(e -> {
			if(this.onStopRecordRequest("ingredients")){
				buildNewlyGeneratedRecipeDisplay();
			}
		});
		this.newlyGeneratedRecipeSaveButton.setOnAction(e -> {
			onSaveNewlyGeneratedRecipeRequest();
		});

		// left side
		currentSelectedRecipeID = -1;
		savedRecipeDescription = new Label("Recipe Description: ...");

		buildHomePage();
		buidlDetailPage();
		buildRecordMealType();

		displayHomePage();

        this.audioRecorder = new AudioRecorder();
	}

	public BorderPane getRoot() {
		return this.root;
	}
	
	public void updateRecipes() {
		System.out.println("Updating Recipes");
		ListView<HBox> daStuff = viewModel.pullRecipes();
		this.recipeTitleListleftVbox = new VBox(10, daStuff, this.recipeQuery, this.addNewRecipeButton);
		VBox.setVgrow(daStuff, Priority.ALWAYS);
		this.root.setLeft(this.recipeTitleListleftVbox);
		//Update detail view
		daStuff.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue != null) {
				String recipeText = ((RecipeNode)daStuff.getSelectionModel().getSelectedItem()).getRecipeText();
				updateSelectedRecipeDetails(recipeText,((RecipeNode)daStuff.getSelectionModel().getSelectedItem()).getRecipeID());
				System.out.println("Selected: " + ((RecipeNode)daStuff.getSelectionModel().getSelectedItem()).getRecipeTitle());
			}
			buidlDetailPage();
		});
		
	}


	private void buidlDetailPage(){
		savedRecipeDescription.setWrapText(true);
		//Right side
		this.savedRecipeDetailVbox = new VBox(10);
		this.savedRecipeDetailVbox.getChildren().addAll(savedRecipeDescription, editSavedRecipeButton, deleteSavedRecipeButton, backToHome);
		this.savedRecipeDetailVbox.setAlignment(javafx.geometry.Pos.TOP_LEFT);
		this.savedRecipeDetailVbox.setPadding(new Insets(0,10,5,10));
		displayRecipeDetails();
	}
	private void displayRecipeDetails(){
		System.out.println("Display Detail Page");
		this.root.setCenter(this.savedRecipeDetailVbox);
	}



	private void buildHomePage(){
		updateRecipes();
		homePageText = new Label("Welcome to Pantry Pal\n To add a recipe click record meal type");
		homePageText.setWrapText(true);
		//Right side
		this.homePageVbox = new VBox(10);
		this.homePageVbox.getChildren().addAll(homePageText, generateNewRecipe);
		this.homePageVbox.setAlignment(javafx.geometry.Pos.TOP_LEFT);
		this.homePageVbox.setPadding(new Insets(0,10,5,10));
		displayHomePage();
	}
	private void displayHomePage(){
		System.out.println("Displaying Home Page");
		this.root.setCenter(this.homePageVbox);
	}

	private void buildRecordMealType(){
		recordMealTypeText = new Label("Click start recording and then say breakfast, lunch, or dinner to select a meal type.\nClick stop when you are done");
		recordMealTypeText.setWrapText(true);
		this.recordMealTypeVbox = new VBox(10);
		//add buttons to record and stop recording
		this.recordMealTypeVbox.getChildren().addAll(recordMealTypeText, startRecording,stopRecordingMealType);
		//this.recordMealTypeVbox.setAlignment(javafx.geometry.Pos.TOP_LEFT);
		this.recordMealTypeVbox.setPadding(new Insets(0,10,5,10));
		displayRecordMealType();
	}
	private void displayRecordMealType(){
		System.out.println("Displaying Meal Type Page");
		this.root.setCenter(this.recordMealTypeVbox);
	}

	private void buildRecordIngredients(){
		recordIngredientsText = new Label("Click start recording and speak your ingredients to generate a recipe.\nClick stop when you are done");
		recordIngredientsText.setWrapText(true);
		//add buttons to record and stop recording
		this.recordIngredientsVbox = new VBox(10);
		this.recordIngredientsVbox.getChildren().addAll(recordIngredientsText, startRecording, stopRecordingIngredients);
		this.recordIngredientsVbox.setAlignment(javafx.geometry.Pos.TOP_LEFT);
		this.recordIngredientsVbox.setPadding(new Insets(0,10,5,10));
		displayRecordIngredients();
	}
	private void displayRecordIngredients(){
		System.out.println("Displaying Ingredients Page");
		this.root.setCenter(this.recordIngredientsVbox);
	}

	private void buildNewlyGeneratedRecipeDisplay(){
		savedRecipeDescription = new Label(newlyGeneratedRecipe.getRecipeText());
		savedRecipeDescription.setWrapText(true);
		//Right side
		this.newlyGeneratedRecipeDisplayVbox = new VBox(10);
		this.newlyGeneratedRecipeDisplayVbox.getChildren().addAll(savedRecipeDescription, newlyGeneratedRecipeSaveButton, backToHome);
		this.newlyGeneratedRecipeDisplayVbox.setAlignment(javafx.geometry.Pos.TOP_LEFT);
		this.newlyGeneratedRecipeDisplayVbox.setPadding(new Insets(0,10,5,10));
		displayNewlyGeneratedRecipeDisplay();
		
	}
	private void displayNewlyGeneratedRecipeDisplay(){
		System.out.println("Displaying Newly Generated Recipe Page");
		this.root.setCenter(this.newlyGeneratedRecipeDisplayVbox);
	}

	private void onGenerateRequest() {
		//System.out.println("onGenerateRequest Query: " + this.recipeQuery.getText());

		RequestHandler req = new RequestHandler();
		req.performPOST("http://localhost:8100/", this.recipeQuery.getText());
		this.recipeQuery.clear();
		this.updateRecipes();
	}

	private void onSaveNewlyGeneratedRecipeRequest(){
		//RequestHandler req = new RequestHandler();
		//Integer newRecipeID = req.performPUT("http://localhost:8100/", newlyGeneratedRecipe.getRecipeID(), 
		//												   newlyGeneratedRecipe.getRecipeTitle(), 
		//												   newlyGeneratedRecipe.getRecipeText());
		displayHomePage();
		this.updateRecipes();
	}

	private void onEditRequest() {
		//System.out.println("onGenerateRequest Query: " + this.recipeQuery.getText());
		System.out.println("Client Delete Recipe:"+currentSelectedRecipeID);
		
		RequestHandler req = new RequestHandler();
		req.performPUT("http://localhost:8100/", currentSelectedRecipeID, "Test Edit Title\n", "Test Edit Text\n");
		this.recipeQuery.clear();
		this.updateRecipes();
	}

	private void onDeleteRequest() {
		//System.out.println("onGenerateRequest Query: " + this.recipeQuery.getText());
		System.out.println("Client Delete Recipe: "+currentSelectedRecipeID);
	
		RequestHandler req = new RequestHandler();
		req.performDELETE("http://localhost:8100/", currentSelectedRecipeID);
		this.recipeQuery.clear();
		this.updateRecipes();
		displayHomePage();
	}

    private void onRecordRequest() {
        System.out.println("Recording...");
        this.audioRecorder.startRecording();
    }

	private boolean onStopRecordRequest(String requestType) {
		//Stop Recording
        this.audioRecorder.stopRecording();
        System.out.println("Stopped recording.");
		//Send Request
		if(requestType.equals("mealType")){
			return viewModel.requestMealTypeCheck();
		}else if(requestType.equals("ingredients")){
			newlyGeneratedRecipe = viewModel.requestNewRecipe();
			return true;
		}
       /* RequestHandler req = new RequestHandler();
		try {
			if(requestType.equals("mealType")){
				String response = req.performPOST("http://localhost:8100/", new File("recording.wav"), requestType, "none");
				System.out.println("Response from server: " + response);
				boolean validMealType = ViewModel.validateMealType(response);
				if(validMealType){
					newlyValidatedMealType = response;
				}
				return validMealType;
			}else if(requestType.equals("ingredients")){
				String response = req.performPOST("http://localhost:8100/", new File("recording.wav"), requestType, newlyValidatedMealType);
				System.out.println("Response from server: " + response);
				newlyGeneratedRecipe = RecipeNode.jsonToRecipeNode(new JSONObject(response));
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
		*/
		//If valid request, move on, else dont
		return false;
    }
	private void updateSelectedRecipeDetails(String recipeText, Integer curID){
		savedRecipeDescription.setText(recipeText);
		currentSelectedRecipeID = curID;
	}
	
}