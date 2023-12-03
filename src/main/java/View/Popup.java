package View;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class Popup {
	
	// Reference from 
	// https://www.unrepo.com/javafx/concurrency-and-multithreading-in-javafx-tutorial
	// https://www.geeksforgeeks.org/javafx-alert-with-examples/
	
	private static void _showPopup(AlertType type, String message) {
		Alert alert = new Alert(type);

		switch (type) {
			case ERROR: {
				alert.setTitle("Error Dialog");
				break;
			}
			case INFORMATION: {
				alert.setTitle("Notification");
				break;
			}
			case WARNING: {
				alert.setTitle("Warning Dialog");
				break;
			}
			case CONFIRMATION: {
				alert.setTitle("Confirmation Dialog");
				break;
			}
			default: {
				break;
			}
		}

		alert.setHeaderText(null);
		alert.setContentText(message);

		Platform.runLater(() -> {
			alert.showAndWait();
		});
	}

	public static void showError(String message) {
		Popup._showPopup(AlertType.ERROR, message);
	}

	public static void showInformation(String message) {
		Popup._showPopup(AlertType.INFORMATION, message);
	}

	public static void showWarning(String message) {
		Popup._showPopup(AlertType.WARNING, message);
	}

	public static void showConfirmation(String message) {
		Popup._showPopup(AlertType.CONFIRMATION, message);
	}
}