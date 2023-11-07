package View;

import Controller.*;
import Model.RecipeServer;

import java.io.*;
import java.util.Iterator;

import org.json.JSONObject;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.HBox;

public class View {

	public BorderPane root;
	private TextField recipeQuery = new TextField();
	private Button generateButton = new Button("Generate Recipe");
	private VBox leftVBox;
	private VBox rightVBox;

	public View() {
		this.root = new BorderPane();
		this.root.setPadding(new Insets(10));
		//Left ListView with placeholders and a + button

		this.recipeQuery.setPromptText("Enter Recipe Query...");
		ListView<HBox> listView = ViewModel.pullRecipes();

		this.generateButton.setOnAction(e -> this.onGenerateRequest());

		// left side
		this.updateRecipes();

		//Right side
		this.rightVBox = new VBox(10);
		Label recipeDescription = new Label("Recipe Description: ...");
		this.rightVBox.getChildren().addAll(recipeDescription, new Button("Rectangle Button?"));
		this.rightVBox.setAlignment(javafx.geometry.Pos.TOP_LEFT);
		this.root.setRight(this.rightVBox);


		//Update detail view
		listView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue != null) {
				String recipeText = ((RecipeNode)listView.getSelectionModel().getSelectedItem()).getRecipeText();
				recipeDescription.setText(recipeText);
			}
		});

	}

	public BorderPane getRoot() {
		return this.root;
	}
	
	public void updateRecipes() {
		System.out.println("printing the muhfuckin list viwew");

		ListView<HBox> daStuff = ViewModel.pullRecipes();
		this.leftVBox = new VBox(10, daStuff, new Button("+"), this.recipeQuery, this.generateButton);
		VBox.setVgrow(daStuff, Priority.ALWAYS);
		this.root.setLeft(this.leftVBox);
	}

	private void onGenerateRequest() {
		//System.out.println("onGenerateRequest Query: " + this.recipeQuery.getText());

		RequestHandler req = new RequestHandler();
		req.performPOST(this.recipeQuery.getText());
		this.recipeQuery.clear();
		this.updateRecipes();
	}
}