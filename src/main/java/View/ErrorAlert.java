package View;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class ErrorAlert {
    
    // Reference from 
    // https://www.unrepo.com/javafx/concurrency-and-multithreading-in-javafx-tutorial
    // https://www.geeksforgeeks.org/javafx-alert-with-examples/

    public static void showError(String errorMessage) {
        Platform.runLater(() -> {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Error Dialog");
            alert.setHeaderText(null);
            alert.setContentText(errorMessage);
            alert.showAndWait();
        });
    }
}