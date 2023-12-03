package View;

import Model.*;

import java.io.*;

import Controller.RequestHandler;
import javafx.application.Application;
import javafx.stage.Stage;

import javafx.scene.Scene;
import javafx.scene.image.Image;


public class App extends Application {

    public final String appName = new String("PantryPal");
	public final int dimX = 1000, dimY = 600;
    public static RecipeServer server; 

	public static void main(String[] args) {
		launch(args);
	}
	
	@Override
	public void start(Stage primaryStage) throws IOException {

		//GenerateDemoLists.generateAndSaveDemoList(); enable this to make demo recipe.list
		server = new RecipeServer();
		server.startServer();

		AudioRecorder audioRecorder = new AudioRecorder();
		ViewModel viewModel = new ViewModel(new RequestHandler(), "http://localhost:8100/", audioRecorder,"savedLogin.csv");
		View view = new View(viewModel);

		primaryStage.setTitle(this.appName);
		primaryStage.getIcons().add(new Image("file:./src/main/java/View/images/icon.png"));
		primaryStage.setScene(new Scene(view.getRoot(), dimX, dimY));
		primaryStage.show();
	}
}