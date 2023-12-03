package View;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class Popup {
    
    // Reference from 
    // https://www.unrepo.com/javafx/concurrency-and-multithreading-in-javafx-tutorial
    // https://www.geeksforgeeks.org/javafx-alert-with-examples/

    public static void showError(String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Error Dialog");
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }

    public static void showInformation(String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Notification");
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }

    public static void showWarning(String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(AlertType.WARNING);
            alert.setTitle("Warning Dialog");
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }

    public static void showConfirmation(String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(AlertType.CONFIRMATION);
            alert.setTitle("Confirmation Dialog");
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }
}