package RecipeApp;

import java.io.*;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class Main extends Application {

	public final String appName = new String("Recipe App");
	public final int dimX = 800, dimY = 600;

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) {
		primaryStage.setTitle(this.appName);
		primaryStage.setScene(new Scene(new StackPane(), dimX, dimY));
		primaryStage.show();
	}
    
}