package com.dgca.flightlogbook.ui.controller;

import com.dgca.flightlogbook.ui.SceneNavigator;
import com.dgca.flightlogbook.ui.Session;
import com.dgca.flightlogbook.ui.api.ApiClient;
import com.dgca.flightlogbook.ui.api.ApiException;
import com.dgca.flightlogbook.ui.model.Aircraft;
import com.fasterxml.jackson.core.type.TypeReference;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdminController {

    @FXML private Label adminNameLabel;
    @FXML private TextField regTailField;
    @FXML private TextField typeDesignatorField;
    @FXML private ComboBox<String> engineTypeComboBox;
    @FXML private Button addAircraftButton;
    @FXML private Label formStatusLabel;

    @FXML private TableView<Aircraft> aircraftTable;
    @FXML private TableColumn<Aircraft, Long> colId;
    @FXML private TableColumn<Aircraft, String> colTail;
    @FXML private TableColumn<Aircraft, String> colType;
    @FXML private TableColumn<Aircraft, String> colEngine;

    @FXML
    public void initialize() {
        adminNameLabel.setText(Session.getFullName() != null ? Session.getFullName() : "Administrator");

        engineTypeComboBox.setItems(FXCollections.observableArrayList("SINGLE", "MULTI"));
        engineTypeComboBox.setValue("SINGLE");

        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colTail.setCellValueFactory(new PropertyValueFactory<>("registrationTail"));
        colType.setCellValueFactory(new PropertyValueFactory<>("typeDesignator"));
        colEngine.setCellValueFactory(new PropertyValueFactory<>("engineType"));

        loadAircraftList();
    }

    @FXML
    public void loadAircraftList() {
        Task<List<Aircraft>> task = new Task<>() {
            @Override
            protected List<Aircraft> call() throws Exception {
                return ApiClient.get("/api/aircraft", new TypeReference<List<Aircraft>>() {});
            }
        };

        task.setOnSucceeded(e -> aircraftTable.setItems(FXCollections.observableArrayList(task.getValue())));
        task.setOnFailed(e -> SceneNavigator.showAlert(Alert.AlertType.ERROR, "Fleet Load Failed",
                task.getException().getMessage()));

        new Thread(task).start();
    }

    @FXML
    public void handleAddAircraft() {
        formStatusLabel.setVisible(false);

        String tail = regTailField.getText().trim().toUpperCase();
        String type = typeDesignatorField.getText().trim().toUpperCase();
        String engine = engineTypeComboBox.getValue();

        if (tail.isEmpty() || type.isEmpty() || engine == null) {
            showError("All aircraft fields (tail registration, type designator, engine type) are required.");
            return;
        }

        Map<String, String> payload = new HashMap<>();
        payload.put("registrationTail", tail);
        payload.put("typeDesignator", type);
        payload.put("engineType", engine);

        addAircraftButton.setDisable(true);
        Task<Aircraft> task = new Task<>() {
            @Override
            protected Aircraft call() throws Exception {
                return ApiClient.post("/api/aircraft", payload, Aircraft.class);
            }
        };

        task.setOnSucceeded(e -> {
            addAircraftButton.setDisable(false);
            SceneNavigator.showAlert(Alert.AlertType.INFORMATION, "Success",
                    "Aircraft " + tail + " registered successfully!");
            regTailField.clear();
            typeDesignatorField.clear();
            loadAircraftList();
        });

        task.setOnFailed(e -> {
            addAircraftButton.setDisable(false);
            Throwable ex = task.getException();
            showError(ex instanceof ApiException ? ex.getMessage() : "Error: " + ex.getMessage());
        });

        new Thread(task).start();
    }

    private void showError(String msg) {
        formStatusLabel.setText(msg);
        formStatusLabel.setStyle("-fx-text-fill: #e53e3e;");
        formStatusLabel.setVisible(true);
    }

    @FXML
    public void handleLogout() {
        SceneNavigator.logout();
    }
}
