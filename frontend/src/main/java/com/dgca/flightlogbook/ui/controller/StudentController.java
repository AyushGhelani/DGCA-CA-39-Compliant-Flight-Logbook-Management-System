package com.dgca.flightlogbook.ui.controller;

import com.dgca.flightlogbook.ui.SceneNavigator;
import com.dgca.flightlogbook.ui.Session;
import com.dgca.flightlogbook.ui.api.ApiClient;
import com.dgca.flightlogbook.ui.api.ApiException;
import com.dgca.flightlogbook.ui.model.Aircraft;
import com.dgca.flightlogbook.ui.model.CA39Report;
import com.dgca.flightlogbook.ui.model.FlightLog;
import com.dgca.flightlogbook.ui.model.Instructor;
import com.fasterxml.jackson.core.type.TypeReference;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StudentController {

    // Top Bar
    @FXML private Label userNameLabel;
    @FXML private TabPane tabPane;
    @FXML private Tab addEditTab;

    // TAB 1: My Logs
    @FXML private ComboBox<String> statusFilterComboBox;
    @FXML private Button editLogButton;
    @FXML private TableView<FlightLog> logsTable;
    @FXML private TableColumn<FlightLog, String> colDate;
    @FXML private TableColumn<FlightLog, String> colRoute;
    @FXML private TableColumn<FlightLog, String> colAircraft;
    @FXML private TableColumn<FlightLog, String> colInstructor;
    @FXML private TableColumn<FlightLog, Integer> colTotalTime;
    @FXML private TableColumn<FlightLog, Integer> colPicTime;
    @FXML private TableColumn<FlightLog, Integer> colDualTime;
    @FXML private TableColumn<FlightLog, Integer> colDayTime;
    @FXML private TableColumn<FlightLog, Integer> colNightTime;
    @FXML private TableColumn<FlightLog, String> colStatus;
    @FXML private TableColumn<FlightLog, String> colRemarks;

    // TAB 2: Add / Edit Log
    @FXML private Label formTitleLabel;
    @FXML private Button cancelEditButton;
    @FXML private DatePicker datePicker;
    @FXML private ComboBox<Aircraft> aircraftComboBox;
    @FXML private TextField depAerodromeField;
    @FXML private TextField arrAerodromeField;
    @FXML private TextField blockOffField;
    @FXML private TextField blockOnField;
    @FXML private ComboBox<Instructor> instructorComboBox;
    @FXML private Label calcTotalTimeLabel;
    @FXML private TextField picTimeField;
    @FXML private TextField sicTimeField;
    @FXML private TextField dualTimeField;
    @FXML private TextField dayTimeField;
    @FXML private TextField nightTimeField;
    @FXML private TextField instrumentTimeField;
    @FXML private TextField remarksField;
    @FXML private Label formErrorLabel;
    @FXML private Button saveLogButton;

    // TAB 3: CA-39 Report
    @FXML private Label reportStudentName;
    @FXML private Label reportLicenseNo;
    @FXML private Label reportTotalTime;
    @FXML private Label reportPicDualTime;
    @FXML private Label reportDayNightTime;
    @FXML private Label reportInstrumentTime;
    @FXML private TableView<CA39Report.AircraftTypeSummary> ca39Table;
    @FXML private TableColumn<CA39Report.AircraftTypeSummary, String> colRepAircraftType;
    @FXML private TableColumn<CA39Report.AircraftTypeSummary, String> colRepEngineType;
    @FXML private TableColumn<CA39Report.AircraftTypeSummary, Integer> colRepTotalMin;
    @FXML private TableColumn<CA39Report.AircraftTypeSummary, Integer> colRepPic;
    @FXML private TableColumn<CA39Report.AircraftTypeSummary, Integer> colRepSic;
    @FXML private TableColumn<CA39Report.AircraftTypeSummary, Integer> colRepDual;
    @FXML private TableColumn<CA39Report.AircraftTypeSummary, Integer> colRepDay;
    @FXML private TableColumn<CA39Report.AircraftTypeSummary, Integer> colRepNight;
    @FXML private TableColumn<CA39Report.AircraftTypeSummary, Integer> colRepInstrument;
    @FXML private TableColumn<CA39Report.AircraftTypeSummary, Integer> colRepFlights;

    // State
    private Long editingLogId = null;
    private int calculatedTotalMinutes = 0;
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    @FXML
    public void initialize() {
        userNameLabel.setText(Session.getFullName() != null ? Session.getFullName() : "Pilot");

        setupLogsTable();
        setupCA39Table();
        setupFormListeners();

        statusFilterComboBox.setItems(FXCollections.observableArrayList("ALL", "PENDING", "CERTIFIED", "REJECTED"));
        statusFilterComboBox.setValue("ALL");
        statusFilterComboBox.valueProperty().addListener((obs, oldV, newV) -> loadLogs());

        // Prepopulate with default dummy data so dropdowns are immediately available
        aircraftComboBox.setItems(FXCollections.observableArrayList(getDefaultAircraftList()));
        if (!aircraftComboBox.getItems().isEmpty()) {
            aircraftComboBox.setValue(aircraftComboBox.getItems().get(0));
        }
        instructorComboBox.setItems(FXCollections.observableArrayList(getDefaultInstructorList()));
        if (!instructorComboBox.getItems().isEmpty()) {
            instructorComboBox.setValue(instructorComboBox.getItems().get(0));
        }

        loadAircraftList();
        loadInstructorList();
        loadLogs();
        loadCA39Report();

        tabPane.getSelectionModel().selectedItemProperty().addListener((obs, oldTab, newTab) -> {
            if (newTab != null && "CA-39 Report".equalsIgnoreCase(newTab.getText())) {
                loadCA39Report();
            }
        });
    }

    private void setupLogsTable() {
        colDate.setCellValueFactory(data -> new SimpleStringProperty(
                data.getValue().getDateUtc() != null ? data.getValue().getDateUtc().toString() : ""));
        colRoute.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getRoute()));
        colAircraft.setCellValueFactory(data -> new SimpleStringProperty(
                data.getValue().getAircraftRegistration() != null
                        ? data.getValue().getAircraftRegistration() + " (" + data.getValue().getAircraftType() + ")"
                        : ""));
        colInstructor.setCellValueFactory(data -> new SimpleStringProperty(
                data.getValue().getInstructorName() != null ? data.getValue().getInstructorName() : "-"));
        colTotalTime.setCellValueFactory(new PropertyValueFactory<>("totalFlightTime"));
        colPicTime.setCellValueFactory(new PropertyValueFactory<>("picTime"));
        colDualTime.setCellValueFactory(new PropertyValueFactory<>("dualTime"));
        colDayTime.setCellValueFactory(new PropertyValueFactory<>("dayTime"));
        colNightTime.setCellValueFactory(new PropertyValueFactory<>("nightTime"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        colRemarks.setCellValueFactory(new PropertyValueFactory<>("remarks"));

        logsTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            boolean canEdit = newSel != null && "PENDING".equalsIgnoreCase(newSel.getStatus());
            editLogButton.setDisable(!canEdit);
        });
    }

    private void setupCA39Table() {
        colRepAircraftType.setCellValueFactory(new PropertyValueFactory<>("aircraftType"));
        colRepEngineType.setCellValueFactory(new PropertyValueFactory<>("engineType"));
        colRepTotalMin.setCellValueFactory(new PropertyValueFactory<>("totalMinutes"));
        colRepPic.setCellValueFactory(new PropertyValueFactory<>("picMinutes"));
        colRepSic.setCellValueFactory(new PropertyValueFactory<>("sicMinutes"));
        colRepDual.setCellValueFactory(new PropertyValueFactory<>("dualMinutes"));
        colRepDay.setCellValueFactory(new PropertyValueFactory<>("dayMinutes"));
        colRepNight.setCellValueFactory(new PropertyValueFactory<>("nightMinutes"));
        colRepInstrument.setCellValueFactory(new PropertyValueFactory<>("instrumentMinutes"));
        colRepFlights.setCellValueFactory(new PropertyValueFactory<>("numberOfFlights"));
    }

    private void setupFormListeners() {
        blockOffField.textProperty().addListener((obs, oldV, newV) -> recalculateBlockTime());
        blockOnField.textProperty().addListener((obs, oldV, newV) -> recalculateBlockTime());
        datePicker.setValue(LocalDate.now());
    }

    private void recalculateBlockTime() {
        String offStr = blockOffField.getText().trim();
        String onStr = blockOnField.getText().trim();

        if (offStr.length() == 5 && onStr.length() == 5) {
            try {
                LocalTime off = LocalTime.parse(offStr, TIME_FORMATTER);
                LocalTime on = LocalTime.parse(onStr, TIME_FORMATTER);

                long mins;
                if (!on.isBefore(off)) {
                    mins = Duration.between(off, on).toMinutes();
                } else {
                    // Midnight crossing (+1440 mins)
                    mins = Duration.between(off, on.plusHours(24)).toMinutes();
                }
                calculatedTotalMinutes = (int) mins;
                int hrs = calculatedTotalMinutes / 60;
                int remMins = calculatedTotalMinutes % 60;
                calcTotalTimeLabel.setText(calculatedTotalMinutes + " min (" + hrs + "h " + remMins + "m)");
                calcTotalTimeLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #2b6cb0;");
                return;
            } catch (DateTimeParseException ignored) {
            }
        }
        calculatedTotalMinutes = 0;
        calcTotalTimeLabel.setText("Invalid time format (use HH:mm)");
        calcTotalTimeLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #e53e3e;");
    }

    // ──────────────────────────── DATA LOADING ────────────────────────────

    @FXML
    public void loadLogs() {
        Long studentId = Session.getUserId();
        String statusFilter = statusFilterComboBox.getValue();
        String endpoint = "/api/logs/student/" + studentId;
        if (statusFilter != null && !"ALL".equalsIgnoreCase(statusFilter)) {
            endpoint += "?status=" + statusFilter;
        }

        final String url = endpoint;
        Task<List<FlightLog>> task = new Task<>() {
            @Override
            protected List<FlightLog> call() throws Exception {
                return ApiClient.get(url, new TypeReference<List<FlightLog>>() {});
            }
        };

        task.setOnSucceeded(e -> logsTable.setItems(FXCollections.observableArrayList(task.getValue())));
        task.setOnFailed(e -> SceneNavigator.showAlert(Alert.AlertType.ERROR, "Error Loading Logs",
                task.getException().getMessage()));

        new Thread(task).start();
    }

    private void loadAircraftList() {
        Task<List<Aircraft>> task = new Task<>() {
            @Override
            protected List<Aircraft> call() throws Exception {
                return ApiClient.get("/api/aircraft", new TypeReference<List<Aircraft>>() {});
            }
        };
        task.setOnSucceeded(e -> {
            List<Aircraft> fetched = task.getValue();
            if (fetched != null && !fetched.isEmpty()) {
                aircraftComboBox.setItems(FXCollections.observableArrayList(fetched));
                aircraftComboBox.setValue(fetched.get(0));
            } else {
                aircraftComboBox.setItems(FXCollections.observableArrayList(getDefaultAircraftList()));
                aircraftComboBox.setValue(aircraftComboBox.getItems().get(0));
            }
        });
        task.setOnFailed(e -> {
            if (aircraftComboBox.getItems().isEmpty()) {
                List<Aircraft> defaults = getDefaultAircraftList();
                aircraftComboBox.setItems(FXCollections.observableArrayList(defaults));
                aircraftComboBox.setValue(defaults.get(0));
            }
        });
        new Thread(task).start();
    }

    private void loadInstructorList() {
        Task<List<Instructor>> task = new Task<>() {
            @Override
            protected List<Instructor> call() throws Exception {
                return ApiClient.get("/api/users/instructors", new TypeReference<List<Instructor>>() {});
            }
        };
        task.setOnSucceeded(e -> {
            List<Instructor> fetched = task.getValue();
            if (fetched != null && !fetched.isEmpty()) {
                instructorComboBox.setItems(FXCollections.observableArrayList(fetched));
                instructorComboBox.setValue(fetched.get(0));
            } else {
                instructorComboBox.setItems(FXCollections.observableArrayList(getDefaultInstructorList()));
                instructorComboBox.setValue(instructorComboBox.getItems().get(0));
            }
        });
        task.setOnFailed(e -> {
            if (instructorComboBox.getItems().isEmpty()) {
                List<Instructor> defaults = getDefaultInstructorList();
                instructorComboBox.setItems(FXCollections.observableArrayList(defaults));
                instructorComboBox.setValue(defaults.get(0));
            }
        });
        new Thread(task).start();
    }

    private List<Aircraft> getDefaultAircraftList() {
        return List.of(
                new Aircraft(1L, "VT-C150", "C150", "SINGLE"),
                new Aircraft(2L, "VT-A320", "A320", "MULTI"),
                new Aircraft(3L, "VT-A350", "A350", "MULTI"),
                new Aircraft(4L, "VT-P32",  "P32",  "SINGLE"),
                new Aircraft(5L, "VT-C172", "C172", "SINGLE"),
                new Aircraft(6L, "VT-PA28", "PA28", "SINGLE"),
                new Aircraft(7L, "VT-B737", "B737", "MULTI")
        );
    }

    private List<Instructor> getDefaultInstructorList() {
        return List.of(
                new Instructor(2L, "Capt. Anil Sharma (CFI)"),
                new Instructor(4L, "Capt. Rajesh Khanna (CFI)"),
                new Instructor(5L, "Capt. Priya Patel (CFI)"),
                new Instructor(6L, "Capt. Vikram Malhotra (CFI)"),
                new Instructor(7L, "Capt. Meera Sen (CFI)")
        );
    }

    @FXML
    public void loadCA39Report() {
        Long studentId = Session.getUserId();
        Task<CA39Report> task = new Task<>() {
            @Override
            protected CA39Report call() throws Exception {
                return ApiClient.get("/api/reports/ca39-summary/" + studentId, CA39Report.class);
            }
        };

        task.setOnSucceeded(e -> {
            CA39Report report = task.getValue();
            if (report != null) {
                reportStudentName.setText(report.getStudentName() != null ? report.getStudentName() : "-");
                reportLicenseNo.setText(report.getLicenseNumber() != null ? report.getLicenseNumber() : "-");

                int totalMins = report.getTotalFlightMinutes() != null ? report.getTotalFlightMinutes() : 0;
                double hrs = totalMins / 60.0;
                reportTotalTime.setText(String.format("%d min (%.1f hrs)", totalMins, hrs));

                int pic = report.getTotalPicMinutes() != null ? report.getTotalPicMinutes() : 0;
                int dual = report.getTotalDualMinutes() != null ? report.getTotalDualMinutes() : 0;
                reportPicDualTime.setText(pic + " / " + dual + " min");

                int day = report.getTotalDayMinutes() != null ? report.getTotalDayMinutes() : 0;
                int night = report.getTotalNightMinutes() != null ? report.getTotalNightMinutes() : 0;
                reportDayNightTime.setText(day + " / " + night + " min");

                int inst = report.getTotalInstrumentMinutes() != null ? report.getTotalInstrumentMinutes() : 0;
                reportInstrumentTime.setText(inst + " min");

                if (report.getByAircraftType() != null) {
                    ca39Table.setItems(FXCollections.observableArrayList(report.getByAircraftType()));
                }
            }
        });

        task.setOnFailed(e -> SceneNavigator.showAlert(Alert.AlertType.ERROR, "Report Error",
                task.getException().getMessage()));

        new Thread(task).start();
    }

    // ──────────────────────────── ADD / EDIT LOG ────────────────────────────

    @FXML
    public void handleEditSelectedLog() {
        FlightLog selected = logsTable.getSelectionModel().getSelectedItem();
        if (selected == null || !"PENDING".equalsIgnoreCase(selected.getStatus())) {
            return;
        }

        editingLogId = selected.getId();
        formTitleLabel.setText("Edit Flight Log (ID #" + editingLogId + ")");
        saveLogButton.setText("Update Flight Log");
        cancelEditButton.setVisible(true);
        cancelEditButton.setManaged(true);

        datePicker.setValue(selected.getDateUtc());
        depAerodromeField.setText(selected.getDepartureAerodrome());
        arrAerodromeField.setText(selected.getArrivalAerodrome());
        blockOffField.setText(selected.getBlockOffUtc() != null ? selected.getBlockOffUtc().format(TIME_FORMATTER) : "");
        blockOnField.setText(selected.getBlockOnUtc() != null ? selected.getBlockOnUtc().format(TIME_FORMATTER) : "");

        for (Aircraft ac : aircraftComboBox.getItems()) {
            if (ac.getId().equals(selected.getAircraftId())) {
                aircraftComboBox.setValue(ac);
                break;
            }
        }

        if (selected.getInstructorId() != null) {
            for (Instructor ins : instructorComboBox.getItems()) {
                if (ins.getId().equals(selected.getInstructorId())) {
                    instructorComboBox.setValue(ins);
                    break;
                }
            }
        } else {
            instructorComboBox.setValue(null);
        }

        picTimeField.setText(String.valueOf(selected.getPicTime() != null ? selected.getPicTime() : 0));
        sicTimeField.setText(String.valueOf(selected.getSicTime() != null ? selected.getSicTime() : 0));
        dualTimeField.setText(String.valueOf(selected.getDualTime() != null ? selected.getDualTime() : 0));
        dayTimeField.setText(String.valueOf(selected.getDayTime() != null ? selected.getDayTime() : 0));
        nightTimeField.setText(String.valueOf(selected.getNightTime() != null ? selected.getNightTime() : 0));
        instrumentTimeField.setText(String.valueOf(selected.getInstrumentTime() != null ? selected.getInstrumentTime() : 0));
        remarksField.setText(selected.getRemarks() != null ? selected.getRemarks() : "");

        recalculateBlockTime();
        tabPane.getSelectionModel().select(addEditTab);
    }

    @FXML
    public void handleResetForm() {
        editingLogId = null;
        formTitleLabel.setText("Add New Flight Log");
        saveLogButton.setText("Submit Flight Log");
        cancelEditButton.setVisible(false);
        cancelEditButton.setManaged(false);

        datePicker.setValue(LocalDate.now());
        depAerodromeField.clear();
        arrAerodromeField.clear();
        blockOffField.clear();
        blockOnField.clear();
        instructorComboBox.setValue(null);
        picTimeField.setText("0");
        sicTimeField.setText("0");
        dualTimeField.setText("0");
        dayTimeField.setText("0");
        nightTimeField.setText("0");
        instrumentTimeField.setText("0");
        remarksField.clear();
        formErrorLabel.setVisible(false);
        recalculateBlockTime();
    }

    @FXML
    public void handleSubmitLog() {
        formErrorLabel.setVisible(false);

        // Validations
        LocalDate date = datePicker.getValue();
        if (date == null) {
            showFormError("Flight date is required.");
            return;
        }

        Aircraft aircraft = aircraftComboBox.getValue();
        if (aircraft == null) {
            showFormError("Please select an aircraft.");
            return;
        }

        String dep = depAerodromeField.getText().trim().toUpperCase();
        String arr = arrAerodromeField.getText().trim().toUpperCase();
        if (dep.length() != 4 || arr.length() != 4) {
            showFormError("Aerodromes must be 4-character ICAO codes (e.g. VIDP, VABB).");
            return;
        }

        recalculateBlockTime();
        if (calculatedTotalMinutes <= 0) {
            showFormError("Block times must result in a positive flight duration.");
            return;
        }

        int pic, sic, dual, day, night, inst;
        try {
            pic = Integer.parseInt(picTimeField.getText().trim());
            sic = Integer.parseInt(sicTimeField.getText().trim());
            dual = Integer.parseInt(dualTimeField.getText().trim());
            day = Integer.parseInt(dayTimeField.getText().trim());
            night = Integer.parseInt(nightTimeField.getText().trim());
            inst = Integer.parseInt(instrumentTimeField.getText().trim());
        } catch (NumberFormatException e) {
            showFormError("Time fields must be valid numbers in minutes.");
            return;
        }

        if (pic < 0 || sic < 0 || dual < 0 || day < 0 || night < 0 || inst < 0) {
            showFormError("Times cannot be negative.");
            return;
        }

        // DGCA Client-side validations
        if (pic + sic + dual != calculatedTotalMinutes) {
            showFormError("PIC (" + pic + ") + SIC (" + sic + ") + Dual (" + dual
                    + ") must equal total block time (" + calculatedTotalMinutes + " min).");
            return;
        }

        if (day + night != calculatedTotalMinutes) {
            showFormError("Day (" + day + ") + Night (" + night
                    + ") must equal total block time (" + calculatedTotalMinutes + " min).");
            return;
        }

        if (inst > calculatedTotalMinutes) {
            showFormError("Instrument time (" + inst
                    + ") cannot exceed total flight time (" + calculatedTotalMinutes + " min).");
            return;
        }

        // Build payload
        Map<String, Object> payload = new HashMap<>();
        payload.put("dateUtc", date.toString());
        payload.put("departureAerodrome", dep);
        payload.put("arrivalAerodrome", arr);
        payload.put("blockOffUtc", blockOffField.getText().trim());
        payload.put("blockOnUtc", blockOnField.getText().trim());
        payload.put("aircraftId", aircraft.getId());
        payload.put("studentId", Session.getUserId());

        Instructor instructor = instructorComboBox.getValue();
        if (instructor != null) {
            payload.put("instructorId", instructor.getId());
        } else {
            payload.put("instructorId", null);
        }

        payload.put("picTime", pic);
        payload.put("sicTime", sic);
        payload.put("dualTime", dual);
        payload.put("dayTime", day);
        payload.put("nightTime", night);
        payload.put("instrumentTime", inst);
        payload.put("remarks", remarksField.getText().trim());

        saveLogButton.setDisable(true);
        final boolean isUpdate = (editingLogId != null);
        final Long logId = editingLogId;

        Task<Void> saveTask = new Task<>() {
            @Override
            protected Void call() throws Exception {
                if (isUpdate) {
                    ApiClient.put("/api/logs/" + logId, payload, FlightLog.class);
                } else {
                    ApiClient.post("/api/logs", payload, FlightLog.class);
                }
                return null;
            }
        };

        saveTask.setOnSucceeded(e -> {
            saveLogButton.setDisable(false);
            SceneNavigator.showAlert(Alert.AlertType.INFORMATION, "Success",
                    isUpdate ? "Flight log updated successfully!" : "Flight log added successfully!");
            handleResetForm();
            loadLogs();
            loadCA39Report();
            tabPane.getSelectionModel().select(0);
        });

        saveTask.setOnFailed(e -> {
            saveLogButton.setDisable(false);
            Throwable ex = saveTask.getException();
            showFormError(ex instanceof ApiException ? ex.getMessage() : "Error: " + ex.getMessage());
        });

        new Thread(saveTask).start();
    }

    private void showFormError(String msg) {
        formErrorLabel.setText(msg);
        formErrorLabel.setVisible(true);
    }

    @FXML
    public void handleLogout() {
        SceneNavigator.logout();
    }
}
