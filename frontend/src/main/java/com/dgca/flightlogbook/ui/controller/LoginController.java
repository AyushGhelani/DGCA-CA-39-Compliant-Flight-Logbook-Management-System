package com.dgca.flightlogbook.ui.controller;

import com.dgca.flightlogbook.ui.SceneNavigator;
import com.dgca.flightlogbook.ui.Session;
import com.dgca.flightlogbook.ui.api.ApiClient;
import com.dgca.flightlogbook.ui.api.ApiException;
import com.dgca.flightlogbook.ui.model.AuthResponse;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.util.HashMap;
import java.util.Map;

public class LoginController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private CheckBox registerToggle;
    @FXML private VBox registerBox;
    @FXML private TextField fullNameField;
    @FXML private TextField emailField;
    @FXML private TextField licenseNumberField;
    @FXML private ComboBox<String> roleComboBox;
    @FXML private Label errorLabel;
    @FXML private Button actionButton;
    @FXML private ProgressIndicator loadingIndicator;

    @FXML
    public void initialize() {
        roleComboBox.setItems(FXCollections.observableArrayList("STUDENT", "CFI", "ADMIN"));
        roleComboBox.setValue("STUDENT");
    }

    @FXML
    public void handleToggleRegister() {
        boolean isRegister = registerToggle.isSelected();
        registerBox.setVisible(isRegister);
        registerBox.setManaged(isRegister);
        actionButton.setText(isRegister ? "Register & Log In" : "Log In");
        hideError();
    }

    @FXML
    public void handleSubmit() {
        hideError();
        String username = usernameField.getText().trim();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            showError("Username and password are required.");
            return;
        }

        boolean isRegister = registerToggle.isSelected();
        if (isRegister) {
            String fullName = fullNameField.getText().trim();
            String email = emailField.getText().trim();
            String license = licenseNumberField.getText().trim();
            String role = roleComboBox.getValue();

            if (fullName.isEmpty() || email.isEmpty() || role == null) {
                showError("Full Name, Email, and Role are required for registration.");
                return;
            }

            setLoading(true);
            Task<AuthResponse> registerAndLoginTask = new Task<>() {
                @Override
                protected AuthResponse call() throws Exception {
                    Map<String, Object> regReq = new HashMap<>();
                    regReq.put("username", username);
                    regReq.put("password", password);
                    regReq.put("fullName", fullName);
                    regReq.put("email", email);
                    regReq.put("licenseNumber", license.isEmpty() ? null : license);
                    regReq.put("role", role);

                    // 1. Call register
                    ApiClient.post("/api/auth/register", regReq, Map.class);

                    // 2. Call login to obtain token
                    Map<String, String> loginReq = Map.of("username", username, "password", password);
                    return ApiClient.post("/api/auth/login", loginReq, AuthResponse.class);
                }
            };

            registerAndLoginTask.setOnSucceeded(e -> {
                setLoading(false);
                handleAuthSuccess(registerAndLoginTask.getValue());
            });

            registerAndLoginTask.setOnFailed(e -> {
                setLoading(false);
                Throwable ex = registerAndLoginTask.getException();
                showError(ex instanceof ApiException ? ex.getMessage() : "Registration failed: " + ex.getMessage());
            });

            new Thread(registerAndLoginTask).start();

        } else {
            // Login flow
            setLoading(true);
            Task<AuthResponse> loginTask = new Task<>() {
                @Override
                protected AuthResponse call() throws Exception {
                    Map<String, String> loginReq = Map.of("username", username, "password", password);
                    return ApiClient.post("/api/auth/login", loginReq, AuthResponse.class);
                }
            };

            loginTask.setOnSucceeded(e -> {
                setLoading(false);
                handleAuthSuccess(loginTask.getValue());
            });

            loginTask.setOnFailed(e -> {
                setLoading(false);
                Throwable ex = loginTask.getException();
                showError(ex instanceof ApiException ? ex.getMessage() : "Login failed: " + ex.getMessage());
            });

            new Thread(loginTask).start();
        }
    }

    private void handleAuthSuccess(AuthResponse auth) {
        if (auth == null) {
            showError("Authentication returned no response.");
            return;
        }

        Session.set(auth.getToken(), auth.getUserId(), auth.getFullName(), auth.getRole());

        String role = auth.getRole() != null ? auth.getRole().toUpperCase() : "";
        switch (role) {
            case "STUDENT":
                SceneNavigator.switchScene("student.fxml");
                break;
            case "CFI":
                SceneNavigator.switchScene("cfi.fxml");
                break;
            case "ADMIN":
                SceneNavigator.switchScene("admin.fxml");
                break;
            default:
                showError("Unknown user role: " + role);
                break;
        }
    }

    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
    }

    private void hideError() {
        errorLabel.setText("");
        errorLabel.setVisible(false);
    }

    private void setLoading(boolean loading) {
        loadingIndicator.setVisible(loading);
        loadingIndicator.setManaged(loading);
        actionButton.setDisable(loading);
    }
}
