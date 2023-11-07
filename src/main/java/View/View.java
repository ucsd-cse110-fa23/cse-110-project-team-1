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
	public ListView<HBox> listView;
	private TextField recipeQuery = new TextField();
	private Button generateButton = new Button("Generate Recipe");

	public View() {
		this.root = new BorderPane();
		this.root.setPadding(new Insets(10));
		//Left ListView with placeholders and a + button

		this.recipeQuery.setPromptText("Enter Recipe Query...");
		this.listView = ViewModel.pullRecipes();

		this.generateButton.setOnAction(e -> this.onGenerateRequest());

		// left side
		VBox leftVBox = new VBox(10, this.listView, new Button("+"), this.recipeQuery, this.generateButton);
		VBox.setVgrow(this.listView, Priority.ALWAYS);
		this.root.setLeft(leftVBox);

		//Right side
		VBox rightVBox = new VBox(10);
		Label recipeDescription = new Label("Recipe Description: ...");
		rightVBox.getChildren().addAll(recipeDescription, new Button("Rectangle Button?"));
		rightVBox.setAlignment(javafx.geometry.Pos.TOP_LEFT);
		this.root.setRight(rightVBox);


		//Update detail view
		this.listView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue != null) {
				String recipeText = ((RecipeNode)this.listView.getSelectionModel().getSelectedItem()).getRecipeText();
				recipeDescription.setText(recipeText);
			}
		});

	}

	public BorderPane getRoot() {
		return this.root;
	}
	
	public void updateRecipes() {
		this.listView = ViewModel.pullRecipes();
	}

	private void onGenerateRequest() {
		System.out.println("onGenerateRequest");
	}
}