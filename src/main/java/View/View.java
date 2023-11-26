package View;

import Controller.*;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.HBox;

public class View {

	public BorderPane root;
	private AudioRecorder audioRecorder;
	private ViewModel viewModel;

	private TextField recipeQuery = new TextField();
	private TextArea recipeEditArea;

	private Button addNewRecipeButton = new Button("Generate Recipe");
	private Button editSavedRecipeButton = new Button("Edit Recipe");
	private Button deleteSavedRecipeButton = new Button("Delete Recipe");
	private Button newlyGeneratedRecipeSaveButton = new Button("Save Recipe");
	private Button editedRecipeSaveButton = new Button("Save Recipe");

	private Button startRecording = new Button("Start Recording");
	private Button stopRecordingMealType = new Button("Stop Meal Type Recording");
	private Button stopRecordingIngredients = new Button("Stop Ingredient Recording");
	private Button generateNewRecipe = new Button("Generate New Recipe");

    private Button filterButton = new Button("Filter");

	private Button backToHome = new Button("Back to Home");

	private VBox recipeTitleListleftVbox;

	private VBox homePageVbox;
	private VBox savedRecipeDetailVbox; 
	private VBox newlyGeneratedRecipeDisplayVbox;
	private VBox recordMealTypeVbox;
	private VBox recordIngredientsVbox;
	private VBox editRecipesVbox;


	//homepage labels
	private Label homePageTextHeader;
	private Label homePageTextSubheader;
	private Label savedRecipeDescription;

	private ComboBox<String> filterDropdown;
	private String[] filters;

	//record mealtype labels
	private Label recordMealTypeText;

	//record ingredient labels
	private Label recordIngredientsText;

	//newly generated receipe labels
	//private Label newlyGeneratedRecipeLabel;

	private int currentSelectedRecipeID;
	private RecipeNode newlyGeneratedRecipe;
	private RecipeNode currentlyEditingRecipe;

	
	public View(ViewModel viewModel) {
		this.filters = new String[] {"All", "Breakfast", "Lunch", "Dinner"};
		this.filterDropdown = new ComboBox<String>(FXCollections.observableArrayList(this.filters));
		this.filterDropdown.setValue(this.filters[0]);

		this.root = new BorderPane();
		this.viewModel = viewModel;
		this.root.setPadding(new Insets(10));
		//Left ListView with placeholders and a + button
		this.recipeQuery.setPromptText("Enter Recipe Query...");
		this.addNewRecipeButton.setOnAction(e -> this.onGenerateRequest());
		this.deleteSavedRecipeButton.setOnAction(e -> this.onDeleteRequest());
		this.startRecording.setOnAction(e -> this.onRecordRequest());
		this.backToHome.setOnAction(e -> this.displayHomePage());
		this.generateNewRecipe.setOnAction(e -> this.buildRecordMealType());
		this.editSavedRecipeButton.setOnAction(e -> this.buildEditPage(currentlyEditingRecipe));
		this.stopRecordingMealType.setOnAction(e -> {
			
			if (this.onStopRecordRequest("mealType")){
				buildRecordIngredients();
			}else{
				this.recordMealTypeText.setText(this.recordMealTypeText.getText() + "\nInvalid meal type, please try again.");
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
		this.editedRecipeSaveButton.setOnAction(e -> {
			String newRecipeTitle = recipeEditArea.getText().trim().substring(0, recipeEditArea.getText().trim().indexOf("\n"));
			currentlyEditingRecipe.setRecipeText(recipeEditArea.getText());
			currentlyEditingRecipe.setRecipeTitle(newRecipeTitle);
			onSaveEditedRecipe(currentlyEditingRecipe);
		});
        this.filterButton.setOnAction(e -> {
            this.updateRecipes();
        });


		// left side
		currentSelectedRecipeID = -1;
		savedRecipeDescription = new Label("Recipe Description: ...");

		this.buildHomePage();
		this.buildDetailPage();
		this.buildRecordMealType();
		this.buildRecordIngredients();
		this.displayHomePage();
		this.updateRecipes();

		this.audioRecorder = new AudioRecorder();
	}

	public BorderPane getRoot() {
		return this.root;
	}

	public void updateRecipes() {
		System.out.println("Updating Recipes");
		ListView<HBox> daStuff = viewModel.pullRecipes();
        
        // Implement filtering here. Should simply remove filtered Recipes from list.

        // for(HBox h : daStuff.getItems()) {
        //     // If recipe's meal type is not what the filter is set as,
        //     // do not show it / remove it from the sidebar.
        //     if(!((RecipeNode)h).toJson().getString("mealType").equalsIgnoreCase(this.getCurrentFilter())) {
        //         daStuff.getItems().remove(h);
        //     }
        // }
		this.recipeTitleListleftVbox = new VBox(10, daStuff);
        HBox filterContainer = new HBox(10, this.filterDropdown, this.filterButton);
        this.recipeTitleListleftVbox.getChildren().add(0, filterContainer);
		VBox.setVgrow(daStuff, Priority.ALWAYS);
		this.root.setLeft(this.recipeTitleListleftVbox);
		//Update detail view
		daStuff.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue != null) {
				RecipeNode selected = (RecipeNode)daStuff.getSelectionModel().getSelectedItem();
				String recipeText = selected.getRecipeText();
				updateSelectedRecipeDetails(recipeText,(selected.getRecipeID()));
				currentlyEditingRecipe = selected;
				System.out.println("Selected: " + selected.getRecipeTitle());
			}
			buildDetailPage();
		});

	}


	private void buildEditPage(RecipeNode selectedRecipe){
		this.recipeEditArea = new TextArea(selectedRecipe.getRecipeText());
		this.recipeEditArea.setMinHeight(400);
		this.editRecipesVbox = new VBox(10);
		this.editRecipesVbox.getChildren().addAll(recipeEditArea, new HBox(backToHome, editedRecipeSaveButton));
		this.editRecipesVbox.setAlignment(Pos.TOP_LEFT);
		this.editRecipesVbox.setPadding(new Insets(0,10,5,10));
		displayEditPage();
	}
	private void displayEditPage(){
		this.root.setCenter(this.editRecipesVbox);
	}

	private void buildDetailPage(){
		savedRecipeDescription.setWrapText(true);
		//Right side
		this.savedRecipeDetailVbox = new VBox(10);
		this.savedRecipeDetailVbox.getChildren().addAll(savedRecipeDescription, new HBox(backToHome, editSavedRecipeButton, deleteSavedRecipeButton));
		this.savedRecipeDetailVbox.setAlignment(Pos.TOP_LEFT);
		this.savedRecipeDetailVbox.setPadding(new Insets(0,10,5,10));
		displayRecipeDetails();
	}
	private void displayRecipeDetails(){
		System.out.println("Display Detail Page");
		this.root.setCenter(this.savedRecipeDetailVbox);
	}

	private void buildHomePage() {
		System.out.println("Displaying Home Page");
		this.homePageTextHeader = new Label("Welcome to Pantry Pal");
		this.homePageTextHeader.setWrapText(true);

		this.homePageTextSubheader = new Label("To add a new recipe, click \"" + this.addNewRecipeButton.getText() + "\"");
		this.homePageTextSubheader.setWrapText(true);

		// we need this to center the content
		StackPane centeringPane = new StackPane();

		//Right side
		this.homePageVbox = new VBox(10);
		this.homePageVbox.getChildren().addAll(this.homePageTextHeader, this.homePageTextSubheader, this.generateNewRecipe);
		this.homePageVbox.setAlignment(Pos.CENTER);

		VBox.setVgrow(this.homePageVbox, Priority.ALWAYS);
		centeringPane.getChildren().add(this.homePageVbox);
		StackPane.setAlignment(this.homePageVbox, Pos.CENTER);
		StackPane.setMargin(this.homePageVbox, new Insets(10));

		this.root.setCenter(centeringPane);

		this.displayHomePage();
	}
	private void displayHomePage(){
		this.updateRecipes();
		System.out.println("Displaying Home Page");
		this.root.setCenter(this.homePageVbox);
	}

	private void buildRecordMealType(){
		this.recordMealTypeText = new Label(
			"Click \"" +
			this.startRecording.getText() +
			"\" and then say \"breakfast\", \"lunch\", or \"dinner\" to select a meal type. Click \"" +
			this.stopRecordingMealType.getText() +
			"\" when you are done.");

		this.recordMealTypeText.setWrapText(true);
		this.recordMealTypeText.setMaxWidth(480.0);

		this.recordMealTypeVbox = new VBox(10);
		this.recordMealTypeVbox.setAlignment(Pos.CENTER);
	
		//add buttons to record and stop recording
		this.recordMealTypeVbox.getChildren().addAll(this.recordMealTypeText, this.startRecording, this.stopRecordingMealType);
		//this.recordMealTypeVbox.setAlignment(javafx.geometry.Pos.TOP_LEFT);
		this.recordMealTypeVbox.setPadding(new Insets(0,10,5,10));
		displayRecordMealType();
	}
	private void displayRecordMealType() {
		System.out.println("Displaying Record Meal Type Page");

		BorderPane recordLayout = new BorderPane();

		recordLayout.setCenter(this.recordMealTypeVbox);

		HBox topContainer = new HBox();
		topContainer.setAlignment(Pos.TOP_RIGHT);
		topContainer.getChildren().add(this.backToHome);
		topContainer.setPadding(new Insets(10));

		recordLayout.setTop(topContainer);

		this.root.setCenter(recordLayout);
	}

	private void buildRecordIngredients(){
		this.recordIngredientsText = new Label(
			"Click \"" +
			this.startRecording.getText() + 
			"\" and speak your ingredients to generate a recipe. Click \"" +
			this.stopRecordingIngredients.getText() +
			"\" when you are done.");

		this.recordIngredientsText.setWrapText(true);
		this.recordIngredientsText.setMaxWidth(480.0);

		//add buttons to record and stop recording
		this.recordIngredientsVbox = new VBox(10);
		this.recordIngredientsVbox.setAlignment(Pos.CENTER);
		this.recordIngredientsVbox.getChildren().addAll(recordIngredientsText, startRecording, stopRecordingIngredients);
		this.recordIngredientsVbox.setPadding(new Insets(0,10,5,10));
		displayRecordIngredients();
	}
	private void displayRecordIngredients(){
		System.out.println("Displaying Ingredients Page");

		BorderPane recordIngredientsLayout = new BorderPane();
		recordIngredientsLayout.setCenter(this.recordIngredientsVbox);

		HBox topContainer = new HBox();
		topContainer.setAlignment(Pos.TOP_RIGHT);
		topContainer.getChildren().add(this.backToHome);
		topContainer.setPadding(new Insets(10));

		recordIngredientsLayout.setTop(topContainer);

		this.root.setCenter(recordIngredientsLayout);
	}

	private void buildNewlyGeneratedRecipeDisplay(){
		savedRecipeDescription = new Label(newlyGeneratedRecipe.getRecipeText());
		savedRecipeDescription.setWrapText(true);
		//Right side
		this.newlyGeneratedRecipeDisplayVbox = new VBox(10);
		this.newlyGeneratedRecipeDisplayVbox.getChildren().addAll(savedRecipeDescription, new HBox(backToHome, newlyGeneratedRecipeSaveButton));
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
		RequestHandler req = new RequestHandler();
		req.performPUT("http://localhost:8100/", newlyGeneratedRecipe.getRecipeID(), 
														   newlyGeneratedRecipe.getRecipeTitle(), 
														   newlyGeneratedRecipe.getRecipeText(),
                                                           newlyGeneratedRecipe.getMealType());
		displayHomePage();
		this.updateRecipes();
	}

	private void onSaveEditedRecipe(RecipeNode editRecipeNode){
		RequestHandler req = new RequestHandler();
		req.performPUT("http://localhost:8100/", editRecipeNode.getRecipeID(), 
														   editRecipeNode.getRecipeTitle(), 
														   editRecipeNode.getRecipeText(),
                                                           editRecipeNode.getMealType());
		
		this.updateRecipes();
		displayHomePage();
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
		//If valid request, move on, else dont
		return false;
	}

	private void updateSelectedRecipeDetails(String recipeText, Integer curID){
		savedRecipeDescription.setText(recipeText);
		currentSelectedRecipeID = curID;
	}
	
	public String getCurrentFilter() {
		return this.filterDropdown.getValue();
	}
}