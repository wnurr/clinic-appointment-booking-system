package com.clinic.controller;

import com.clinic.util.SceneSwitcher;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class RegisterController {

    @FXML private TextField txtUsername;
    @FXML private PasswordField txtPassword;
    @FXML private TextField txtFullName;
    @FXML private TextField txtPhone;
    @FXML private CheckBox chkAgree;

    @FXML
    private void handleRegister(ActionEvent event) {
        if (txtUsername.getText().isBlank() || txtPassword.getText().isBlank()) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "Username and password are required.");
            return;
        }
        if (!chkAgree.isSelected()) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "Please agree to the terms.");
            return;
        }

        // NEXT STEP: insert into DB using UserDAO.register(...)
        showAlert(Alert.AlertType.INFORMATION, "Success", "Register button works. Next: save user to MySQL.");
    }

    @FXML
    private void handleBack(ActionEvent event) {
        SceneSwitcher.switchTo(event, "/fxml/login.fxml", "Login");
    }

    private void showAlert(Alert.AlertType type, String title, String msg) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}