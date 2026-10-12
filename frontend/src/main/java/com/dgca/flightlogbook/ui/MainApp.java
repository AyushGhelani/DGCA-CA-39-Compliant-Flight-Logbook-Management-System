package com.dgca.flightlogbook.ui;

import javafx.application.Application;
import javafx.stage.Stage;

public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("DGCA Flight Logbook");
        SceneNavigator.setPrimaryStage(primaryStage);
        SceneNavigator.switchScene("login.fxml");
    }

    public static void main(String[] args) {
        launch(args);
    }
}
