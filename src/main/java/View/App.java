package View;

import Model.RecipeServer;

import java.io.*;

import javafx.application.Application;
import javafx.stage.Stage;

import javafx.scene.Scene;


public class App extends Application {

    public final String appName = new String("PantryPal");
	public final int dimX = 1000, dimY = 600;
    public static RecipeServer server; 

	public static void main(String[] args) {
		launch(args);
	}
	
	@Override
	public void start(Stage primaryStage) throws IOException {
		server = new RecipeServer();
		server.startServer();

		View view = new View();

		primaryStage.setTitle(this.appName);
		primaryStage.setScene(new Scene(view.getRoot(), dimX, dimY));
		primaryStage.show();
	}
}