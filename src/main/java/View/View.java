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
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.HBox;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class View {

    public BorderPane root;
	public ListView<HBox> listView;
    public View(){
    root = new BorderPane();
	root.setPadding(new Insets(10));
		//Left ListView with placeholders and a + button

		RequestHandler req = new RequestHandler();
		req.performPOST("D,ingredients=butter\npotato");
		
		ListView<HBox> listView = ViewModel.pullRecipes();
		//int recipeNum = 5;
		//for (int i = 0; i < recipeNum; i++) {
			//Placeholder image
			//ImageView imageView = new ImageView(new Image("file:src/main/java/View/images/" + (i + 1) + ".jpg"));
			//imageView.setFitHeight(50);
			//imageView.setFitWidth(50);
			//HBox hBox = new HBox(10, imageView, new Label("Recipe " + (i + 1)));
			//listView.getItems().add(hBox);
		//}
		VBox leftVBox = new VBox(10, listView, new Button("+"));
		VBox.setVgrow(listView, Priority.ALWAYS);
		root.setLeft(leftVBox);

		//Right side
		VBox rightVBox = new VBox(10);
		ImageView detailImage = new ImageView(new Image("file:./src/RecipeApp/images/1.jpg"));
		detailImage.setFitHeight(400);
		detailImage.setFitWidth(300);
		Label recipeDescription = new Label("Recipe Description: ...");
		rightVBox.getChildren().addAll(detailImage, recipeDescription, new Button("Rectangle Button?"));
		rightVBox.setAlignment(javafx.geometry.Pos.TOP_LEFT);
		root.setRight(rightVBox);


		//Update detail view

		listView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue != null) {
				int selectedIndex = listView.getSelectionModel().getSelectedIndex();
				detailImage.setImage(new Image("file:./src/RecipeApp/images/" + (selectedIndex + 1) + ".jpg"));

				String recipeText = ((RecipeNode)listView.getSelectionModel().getSelectedItem()).getRecipeText();
				recipeDescription.setText(recipeText);
			}
		});

    }
    public BorderPane getRoot(){
        return root;
    }
	public void updateRecipes(){
		listView = ViewModel.pullRecipes();
	}

}