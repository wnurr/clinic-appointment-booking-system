package com.clinic.controller;

import com.clinic.dao.UserDAO;
import com.clinic.model.User;
import com.clinic.util.SceneSwitcher;
import com.clinic.util.Session;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class LoginController {

    @FXML private TextField txtUsername;
    @FXML private PasswordField txtPassword;
    @FXML private TextField txtPasswordVisible;
    @FXML private Button iconTogglePassword;

    private final UserDAO userDAO = new UserDAO();
    private boolean passwordVisible = false;

    @FXML
    public void initialize() {
        txtPasswordVisible.textProperty().bindBidirectional(txtPassword.textProperty());
        txtPasswordVisible.setVisible(false);
        txtPasswordVisible.setManaged(false);
    }

    @FXML
public void handleLogin(ActionEvent event) {
    String username = txtUsername.getText().trim();
    String password = txtPassword.getText().trim();

    if (username.isBlank() || password.isBlank()) {
        showAlert(Alert.AlertType.ERROR, "Validation Error", "Please enter username and password.");
        return;
    }

    User user = userDAO.loginUser(username, password);

    if (user != null) {
        Session.setSession(user.getId(), user.getUsername(), user.getFullName(), user.getRole());

        if ("admin".equalsIgnoreCase(user.getRole())) {
            SceneSwitcher.switchTo(event, "/fxml/adminDashboard.fxml", "Admin Dashboard");
        } else {
            SceneSwitcher.switchTo(event, "/fxml/dashboard.fxml", "Dashboard");
        }
    } else {
        showAlert(Alert.AlertType.ERROR, "Login Failed", "Invalid username or password.");
    }
}

    @FXML
    public void handleGoRegister(ActionEvent event) {
        SceneSwitcher.switchTo(event, "/fxml/register.fxml", "Register");
    }

    @FXML
    public void handleTogglePassword() {
        passwordVisible = !passwordVisible;

        txtPasswordVisible.setVisible(passwordVisible);
        txtPasswordVisible.setManaged(passwordVisible);

        txtPassword.setVisible(!passwordVisible);
        txtPassword.setManaged(!passwordVisible);

        iconTogglePassword.setText(passwordVisible ? "Hide" : "Show");
    }

    private void showAlert(Alert.AlertType type, String title, String msg) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}