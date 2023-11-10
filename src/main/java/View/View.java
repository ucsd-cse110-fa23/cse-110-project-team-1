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
	private TextField recipeQuery = new TextField();
	private Button generateButton = new Button("Generate Recipe");
	private Button editButton = new Button("Test Edit Recipe");
	private Button deleteButton = new Button("Delete Recipe");
    private Button startRecording = new Button("Start Recording");
    private Button stopRecordingMealType = new Button("Stop Recording");
    private Button stopRecordingIngredients = new Button("Stop Recording");
	private VBox leftVBox;
	private VBox rightVBox;
	private int currentSelectedRecipeID;
    private AudioRecorder audioRecorder;

	public View() {
		this.root = new BorderPane();
		this.root.setPadding(new Insets(10));
		//Left ListView with placeholders and a + button

		this.recipeQuery.setPromptText("Enter Recipe Query...");
		this.generateButton.setOnAction(e -> this.onGenerateRequest());
		this.editButton.setOnAction(e -> this.onEditRequest());
		this.deleteButton.setOnAction(e -> this.onDeleteRequest());
        this.startRecording.setOnAction(e -> this.onRecordRequest());
        this.stopRecordingMealType.setOnAction(e -> this.onStopRecordRequest("mealType"));
        this.stopRecordingIngredients.setOnAction(e -> this.onStopRecordRequest("ingredients"));

		// left side
		this.updateRecipes();
		currentSelectedRecipeID = -1;

        this.audioRecorder = new AudioRecorder();
	}

	public BorderPane getRoot() {
		return this.root;
	}
	
	public void updateRecipes() {
		System.out.println("printing the muhfuckin' list viwew");

		Label recipeDescription = new Label("Recipe Description: ...");
		recipeDescription.setWrapText(true);
		ListView<HBox> daStuff = ViewModel.pullRecipes();
		this.leftVBox = new VBox(10, daStuff, new Button("+"), this.recipeQuery, this.generateButton);
		VBox.setVgrow(daStuff, Priority.ALWAYS);
		this.root.setLeft(this.leftVBox);
		//Update detail view
		daStuff.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue != null) {
				String recipeText = ((RecipeNode)daStuff.getSelectionModel().getSelectedItem()).getRecipeText();
				recipeDescription.setText(recipeText);
				currentSelectedRecipeID = ((RecipeNode)daStuff.getSelectionModel().getSelectedItem()).getRecipeID();
			}
		});

		//Right side
		this.rightVBox = new VBox(10);
		this.rightVBox.getChildren().addAll(recipeDescription, startRecording, stopRecordingMealType, stopRecordingIngredients, editButton, deleteButton);
		this.rightVBox.setAlignment(javafx.geometry.Pos.TOP_LEFT);
		this.rightVBox.setPadding(new Insets(0,10,5,10));
		this.root.setCenter(this.rightVBox);
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