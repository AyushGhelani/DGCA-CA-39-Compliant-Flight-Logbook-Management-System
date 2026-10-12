package com.dgca.flightlogbook.ui.controller;

import com.dgca.flightlogbook.ui.SceneNavigator;
import com.dgca.flightlogbook.ui.Session;
import com.dgca.flightlogbook.ui.api.ApiClient;
import com.dgca.flightlogbook.ui.api.ApiException;
import com.dgca.flightlogbook.ui.model.FlightLog;
import com.fasterxml.jackson.core.type.TypeReference;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class CfiController {

    @FXML private Label instructorNameLabel;
    @FXML private Button certifyButton;
    @FXML private Button rejectButton;

    @FXML private TableView<FlightLog> pendingTable;
    @FXML private TableColumn<FlightLog, Long> colId;
    @FXML private TableColumn<FlightLog, String> colDate;
    @FXML private TableColumn<FlightLog, String> colStudent;
    @FXML private TableColumn<FlightLog, String> colRoute;
    @FXML private TableColumn<FlightLog, String> colAircraft;
    @FXML private TableColumn<FlightLog, Integer> colTotalTime;
    @FXML private TableColumn<FlightLog, Integer> colPicTime;
    @FXML private TableColumn<FlightLog, Integer> colDualTime;
    @FXML private TableColumn<FlightLog, Integer> colDayTime;
    @FXML private TableColumn<FlightLog, Integer> colNightTime;
    @FXML private TableColumn<FlightLog, Integer> colInstrumentTime;
    @FXML private TableColumn<FlightLog, String> colRemarks;

    @FXML
    public void initialize() {
        instructorNameLabel.setText(Session.getFullName() != null ? Session.getFullName() : "Capt. Instructor");

        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colDate.setCellValueFactory(data -> new SimpleStringProperty(
                data.getValue().getDateUtc() != null ? data.getValue().getDateUtc().toString() : ""));
        colStudent.setCellValueFactory(data -> new SimpleStringProperty(
                data.getValue().getStudentName() != null ? data.getValue().getStudentName() : ("Student #" + data.getValue().getStudentId())));
        colRoute.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getRoute()));
        colAircraft.setCellValueFactory(data -> new SimpleStringProperty(
                data.getValue().getAircraftRegistration() != null
                        ? data.getValue().getAircraftRegistration() + " (" + data.getValue().getAircraftType() + ")"
                        : ""));
        colTotalTime.setCellValueFactory(new PropertyValueFactory<>("totalFlightTime"));
        colPicTime.setCellValueFactory(new PropertyValueFactory<>("picTime"));
        colDualTime.setCellValueFactory(new PropertyValueFactory<>("dualTime"));
        colDayTime.setCellValueFactory(new PropertyValueFactory<>("dayTime"));
        colNightTime.setCellValueFactory(new PropertyValueFactory<>("nightTime"));
        colInstrumentTime.setCellValueFactory(new PropertyValueFactory<>("instrumentTime"));
        colRemarks.setCellValueFactory(new PropertyValueFactory<>("remarks"));

        pendingTable.getSelectionModel().selectedItemProperty().addListener((obs, oldV, newV) -> {
            boolean hasSelection = newV != null;
            certifyButton.setDisable(!hasSelection);
            rejectButton.setDisable(!hasSelection);
        });

        loadPendingLogs();
    }

    @FXML
    public void loadPendingLogs() {
        Long cfiId = Session.getUserId();
        Task<List<FlightLog>> task = new Task<>() {
            @Override
            protected List<FlightLog> call() throws Exception {
                return ApiClient.get("/api/certification/pending?instructorId=" + cfiId,
                        new TypeReference<List<FlightLog>>() {});
            }
        };

        task.setOnSucceeded(e -> pendingTable.setItems(FXCollections.observableArrayList(task.getValue())));
        task.setOnFailed(e -> SceneNavigator.showAlert(Alert.AlertType.ERROR, "Queue Load Failed",
                task.getException().getMessage()));

        new Thread(task).start();
    }

    @FXML
    public void handleCertify() {
        FlightLog selected = pendingTable.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        Long logId = selected.getId();
        Long cfiId = Session.getUserId();

        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                ApiClient.post("/api/certification/" + logId + "/certify?instructorId=" + cfiId,
                        null, FlightLog.class);
                return null;
            }
        };

        task.setOnSucceeded(e -> {
            SceneNavigator.showAlert(Alert.AlertType.INFORMATION, "Certified",
                    "Flight log #" + logId + " successfully certified.");
            loadPendingLogs();
        });

        task.setOnFailed(e -> SceneNavigator.showAlert(Alert.AlertType.ERROR, "Certification Failed",
                task.getException().getMessage()));

        new Thread(task).start();
    }

    @FXML
    public void handleReject() {
        FlightLog selected = pendingTable.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        TextInputDialog dialog = new TextInputDialog("Discrepancy in flight hours");
        dialog.setTitle("Reject Flight Log");
        dialog.setHeaderText("Rejecting Flight Log #" + selected.getId());
        dialog.setContentText("Enter rejection reason:");
        dialog.initOwner(SceneNavigator.getPrimaryStage());

        Optional<String> result = dialog.showAndWait();
        if (result.isEmpty()) return;

        String reason = result.get().trim();
        if (reason.isEmpty()) {
            SceneNavigator.showAlert(Alert.AlertType.WARNING, "Validation", "Rejection reason cannot be blank.");
            return;
        }

        Long logId = selected.getId();
        Long cfiId = Session.getUserId();

        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                ApiClient.post("/api/certification/" + logId + "/reject?instructorId=" + cfiId,
                        Map.of("reason", reason), FlightLog.class);
                return null;
            }
        };

        task.setOnSucceeded(e -> {
            SceneNavigator.showAlert(Alert.AlertType.INFORMATION, "Rejected",
                    "Flight log #" + logId + " rejected with note: " + reason);
            loadPendingLogs();
        });

        task.setOnFailed(e -> SceneNavigator.showAlert(Alert.AlertType.ERROR, "Rejection Failed",
                task.getException().getMessage()));

        new Thread(task).start();
    }

    @FXML
    public void handleLogout() {
        SceneNavigator.logout();
    }
}
