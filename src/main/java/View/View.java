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

	private TextField recipeQuery = new TextField();

	private Button addNewRecipeButton = new Button("Generate Recipe");
	private Button editSavedRecipeButton = new Button("Test Edit Recipe");
	private Button deleteSavedRecipeButton = new Button("Delete Recipe");

    private Button startRecording = new Button("Start Recording");
    private Button stopRecordingMealType = new Button("Stop Mealtype Recording");
    private Button stopRecordingIngredients = new Button("Stop Ingredient Recording");
    private Button generateNewRecipe = new Button("Generate New Recipe");

	private Button backToHome = new Button("Back");

	private VBox recipeTitleListleftVbox;

	private VBox homePageVbox;
	private VBox savedRecipeDetailVbox; 
	private VBox newlyGeneratedRecipeVbox;
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
	private Label newlyGeneratedRecipeLabel;

	private int currentSelectedRecipeID;
	
	public View() {

		this.root = new BorderPane();
		this.root.setPadding(new Insets(10));
		//Left ListView with placeholders and a + button
		this.recipeQuery.setPromptText("Enter Recipe Query...");
		this.addNewRecipeButton.setOnAction(e -> this.onGenerateRequest());
		this.editSavedRecipeButton.setOnAction(e -> this.onEditRequest());
		this.deleteSavedRecipeButton.setOnAction(e -> this.onDeleteRequest());
        this.startRecording.setOnAction(e -> this.onRecordRequest());
        this.stopRecordingMealType.setOnAction(e -> this.onStopRecordRequest("mealType"));
        this.stopRecordingIngredients.setOnAction(e -> this.onStopRecordRequest("ingredients"));
		this.backToHome.setOnAction(e -> this.displayHomePage());
		this.generateNewRecipe.setOnAction(e -> this.displayRecordMealType());

		// left side
		this.updateRecipes();
		currentSelectedRecipeID = -1;

		buildHomePage();
		buidlDetailPage();
		buildRecordMealType();
		buildRecordIngredients();
		displayHomePage();

        this.audioRecorder = new AudioRecorder();
	}

	public BorderPane getRoot() {
		return this.root;
	}
	
	public void updateRecipes() {
		
		ListView<HBox> daStuff = ViewModel.pullRecipes();
		this.recipeTitleListleftVbox = new VBox(10, daStuff, this.recipeQuery, this.addNewRecipeButton);
		VBox.setVgrow(daStuff, Priority.ALWAYS);
		this.root.setLeft(this.recipeTitleListleftVbox);
		//Update detail view
		daStuff.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
			displayRecipeDetails();
			if (newValue != null) {
				String recipeText = ((RecipeNode)daStuff.getSelectionModel().getSelectedItem()).getRecipeText();
				savedRecipeDescription.setText(recipeText);
				currentSelectedRecipeID = ((RecipeNode)daStuff.getSelectionModel().getSelectedItem()).getRecipeID();
			}
		});
		
	}


	private void buidlDetailPage(){
		System.out.println("printing the muhfuckin' list viwew");

		savedRecipeDescription = new Label("Recipe Description: ...");
		savedRecipeDescription.setWrapText(true);
		//Right side
		this.savedRecipeDetailVbox = new VBox(10);
		this.savedRecipeDetailVbox.getChildren().addAll(savedRecipeDescription, editSavedRecipeButton, deleteSavedRecipeButton, backToHome);
		this.savedRecipeDetailVbox.setAlignment(javafx.geometry.Pos.TOP_LEFT);
		this.savedRecipeDetailVbox.setPadding(new Insets(0,10,5,10));
		displayRecipeDetails();
	}
	private void displayRecipeDetails(){
		this.root.setCenter(this.savedRecipeDetailVbox);
	}



	private void buildHomePage(){
		System.out.println("Displaying Home Page");
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
		this.root.setCenter(this.homePageVbox);
	}

	private void buildRecordMealType(){
		recordMealTypeText = new Label("Click start recording and then say breakfast, lunch, or dinner to select a meal type.\nClick stop when you are done");
		recordMealTypeText.setWrapText(true);
		this.recordMealTypeVbox = new VBox(10);
		//add buttons to record and stop recording
		this.recordMealTypeVbox.getChildren().addAll(startRecording,stopRecordingMealType);
	}
	private void displayRecordMealType(){
		this.root.setCenter(this.recordMealTypeVbox);
	}

	private void buildRecordIngredients(){
		System.out.println("Displaying Ingredients Page");
		recordIngredientsText = new Label("Click start recording and speak your ingredients to generate a recipe.\nClick stop when you are done");
		recordIngredientsText.setWrapText(true);
		
		this.recordIngredientsVbox = new VBox(10);
		this.recordIngredientsVbox.getChildren().addAll(recordIngredientsText);
		this.recordIngredientsVbox.setAlignment(javafx.geometry.Pos.TOP_LEFT);
		this.recordIngredientsVbox.setPadding(new Insets(0,10,5,10));
	}
	private void displayRecordIngredients(){
		this.root.setCenter(this.recordIngredientsVbox);
	}

	private void onGenerateRequest() {
		//System.out.println("onGenerateRequest Query: " + this.recipeQuery.getText());

		RequestHandler req = new RequestHandler();
		req.performPOST("http://localhost:8100/", this.recipeQuery.getText());
		this.recipeQuery.clear();
		this.updateRecipes();
	}

	private void onEditRequest() {
		//System.out.println("onGenerateRequest Query: " + this.recipeQuery.getText());
		System.out.println("edi curRecipe:"+currentSelectedRecipeID);
		
		RequestHandler req = new RequestHandler();
		req.performPUT("http://localhost:8100/", currentSelectedRecipeID, "Test Edit Title\n", "Test Edit Text\n");
		this.recipeQuery.clear();
		this.updateRecipes();
	}

	private void onDeleteRequest() {
		//System.out.println("onGenerateRequest Query: " + this.recipeQuery.getText());
		System.out.println("del curRecipe:"+currentSelectedRecipeID);
	
		RequestHandler req = new RequestHandler();
		req.performDELETE("http://localhost:8100/", currentSelectedRecipeID);
		this.recipeQuery.clear();
		this.updateRecipes();
	}

    private void onRecordRequest() {
        System.out.println("Recording...");
        this.audioRecorder.startRecording();
    }

	private void onStopRecordRequest(String type) {
        this.audioRecorder.stopRecording();
        System.out.println("Stopped recording.");

        RequestHandler req = new RequestHandler();
		//req.performPOST("http://localhost:8100/", new File("recording.wav"), type);
		this.updateRecipes();
    }
}