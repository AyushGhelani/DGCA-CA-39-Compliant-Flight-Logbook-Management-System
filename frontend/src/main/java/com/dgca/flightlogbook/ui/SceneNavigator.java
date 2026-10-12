package com.dgca.flightlogbook.ui;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public class SceneNavigator {

    private static Stage primaryStage;

    public static void setPrimaryStage(Stage stage) {
        primaryStage = stage;
    }

    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    public static FXMLLoader switchScene(String fxmlName) {
        try {
            String path = "/com/dgca/flightlogbook/ui/" + fxmlName;
            URL resource = SceneNavigator.class.getResource(path);
            if (resource == null) {
                throw new IllegalStateException("FXML file not found: " + path);
            }

            FXMLLoader loader = new FXMLLoader(resource);
            Parent root = loader.load();

            Scene scene = primaryStage.getScene();
            if (scene == null) {
                scene = new Scene(root);
                primaryStage.setScene(scene);
            } else {
                scene.setRoot(root);
            }

            URL cssResource = SceneNavigator.class.getResource("/com/dgca/flightlogbook/ui/style.css");
            if (cssResource != null && !scene.getStylesheets().contains(cssResource.toExternalForm())) {
                scene.getStylesheets().add(cssResource.toExternalForm());
            }

            primaryStage.sizeToScene();
            primaryStage.centerOnScreen();
            primaryStage.show();

            return loader;
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Navigation Error", "Could not load screen: " + fxmlName + "\n" + e.getMessage());
            return null;
        }
    }

    public static void showAlert(Alert.AlertType type, String title, String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(type);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.initOwner(primaryStage);
            alert.showAndWait();
        });
    }

    public static void logout() {
        Session.clear();
        switchScene("login.fxml");
    }
}
