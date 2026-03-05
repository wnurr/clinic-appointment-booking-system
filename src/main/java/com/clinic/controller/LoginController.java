package com.clinic.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.event.ActionEvent;

public class LoginController {

    @FXML private TextField txtUsername;
    @FXML private PasswordField txtPassword;

    @FXML
    private void handleLogin(ActionEvent event) {
        String username = txtUsername.getText();
        String password = txtPassword.getText();

        if (username == null || username.isBlank() || password == null || password.isBlank()) {
            showAlert("Validation Error", "Please enter username and password.");
            return;
        }

        // NEXT STEP: call UserDAO.login(username, password)
        showAlert("Info", "Login button works. Next: connect to database login.");
    }

    @FXML
private void handleGoRegister(ActionEvent event) {
    com.clinic.util.SceneSwitcher.switchTo(event, "/fxml/register.fxml", "Register");
}

    private void showAlert(String title, String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}