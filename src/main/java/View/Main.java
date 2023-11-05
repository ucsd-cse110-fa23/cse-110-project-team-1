package View;

import Model.RecipeServer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class Main extends Application {
	public final String appName = new String("Recipe App");
	public final int dimX = 800, dimY = 600;
    public static RecipeServer server;

    public Main() {
        server = new RecipeServer();
    }

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) {
        try {
            server.startServer();
        } catch (Exception e) {
            System.err.println(e);
        }

		primaryStage.setTitle(this.appName);
		primaryStage.setScene(new Scene(new StackPane(), dimX, dimY));
		primaryStage.show();
	}

    @Override
    public void stop() {
        try {
            server.stopServer();
        } catch (Exception e) {
            System.err.println(e);
        }
    }
    
}