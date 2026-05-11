package com.clinic.controller;

import javafx.scene.control.Button;

import com.clinic.dao.UserDAO;
import com.clinic.model.User;
import com.clinic.util.SceneSwitcher;

import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class RegisterController {

    @FXML private TextField txtUsername;
    @FXML private TextField txtFullName;
    @FXML private TextField txtPhone;
    @FXML private TextField txtNric;

    @FXML private PasswordField txtPassword;
    @FXML private TextField txtPasswordVisible;

    @FXML private PasswordField txtConfirmPassword;
    @FXML private TextField txtConfirmPasswordVisible;

    @FXML private Button iconPassword;
    @FXML private Button iconConfirmPassword;

    @FXML private Label lblPasswordStrength;
    @FXML private Label lblPasswordMatch;
    @FXML private CheckBox chkAgree;

    private final UserDAO userDAO = new UserDAO();

    private boolean passwordVisible = false;
    private boolean confirmPasswordVisible = false;

    @FXML
    public void initialize() {
        txtPasswordVisible.textProperty().bindBidirectional(txtPassword.textProperty());
        txtConfirmPasswordVisible.textProperty().bindBidirectional(txtConfirmPassword.textProperty());

        ChangeListener<String> passwordListener = (obs, oldVal, newVal) -> {
            updatePasswordStrength();
            updatePasswordMatch();
        };

        ChangeListener<String> confirmListener = (obs, oldVal, newVal) -> updatePasswordMatch();

        txtPassword.textProperty().addListener(passwordListener);
        txtConfirmPassword.textProperty().addListener(confirmListener);
    }

    @FXML
    public void handleRegister(ActionEvent event) {
        String username = txtUsername.getText().trim();
        String fullName = txtFullName.getText().trim();
        String phone = txtPhone.getText().trim();
        String nric = txtNric.getText().trim();
        String password = txtPassword.getText().trim();
        String confirmPassword = txtConfirmPassword.getText().trim();

        if (username.isBlank() || nric.isBlank() || fullName.isBlank() || phone.isBlank() || password.isBlank() || confirmPassword.isBlank()) {
            showAlert(Alert.AlertType.ERROR, "Error", "Please fill in all fields.");
            return;
        }

        if (!nric.matches("\\d{6}-\\d{2}-\\d{4}")) {
            showAlert(Alert.AlertType.ERROR, "Invalid NRIC", "Format must be XXXXXX-XX-XXXX");
            return;
        }

        if (username.length() < 4) {
            showAlert(Alert.AlertType.ERROR, "Invalid Username", "Username must be at least 4 characters.");
            return;
        }

        if (!phone.matches("\\d{10,11}")) {
            showAlert(Alert.AlertType.ERROR, "Invalid Phone", "Phone number must contain 10 or 11 digits.");
            return;
        }

        if (!isStrongPassword(password)) {
            showAlert(Alert.AlertType.ERROR, "Weak Password",
                    "Password must be at least 8 characters and include uppercase, lowercase, number, and special character.");
            return;
        }

        if (!password.equals(confirmPassword)) {
            showAlert(Alert.AlertType.ERROR, "Password Error", "Password and confirm password do not match.");
            return;
        }

        if (!chkAgree.isSelected()) {
            showAlert(Alert.AlertType.ERROR, "Error", "You must agree to the terms and conditions.");
            return;
        }

        if (userDAO.isUsernameExists(username)) {
            showAlert(Alert.AlertType.ERROR, "Error", "Username already exists.");
            return;
        }

        User user = new User(username, password, fullName, phone, nric, "user");
        boolean success = userDAO.registerUser(user);

        if (success) {
            showAlert(Alert.AlertType.INFORMATION, "Success", "Registration successful. Please log in.");

            txtUsername.clear();
            txtFullName.clear();
            txtPhone.clear();
            txtNric.clear();
            txtPassword.clear();
            txtConfirmPassword.clear();
            chkAgree.setSelected(false);
            lblPasswordStrength.setText("Password strength: ");
            lblPasswordMatch.setText("");

            SceneSwitcher.switchTo(event, "/fxml/login.fxml", "Login");
        } else {
            showAlert(Alert.AlertType.ERROR, "Error", "Registration failed. Please try again.");
        }
    }

    @FXML
    public void handleBack(ActionEvent event) {
        SceneSwitcher.switchTo(event, "/fxml/login.fxml", "Login");
    }

    @FXML
    public void handleTogglePassword() {
        passwordVisible = !passwordVisible;

        txtPasswordVisible.setVisible(passwordVisible);
        txtPasswordVisible.setManaged(passwordVisible);

        txtPassword.setVisible(!passwordVisible);
        txtPassword.setManaged(!passwordVisible);

        iconPassword.setText(passwordVisible ? "Hide" : "Show");
    }

    @FXML
    public void handleToggleConfirmPassword() {
        confirmPasswordVisible = !confirmPasswordVisible;

        txtConfirmPasswordVisible.setVisible(confirmPasswordVisible);
        txtConfirmPasswordVisible.setManaged(confirmPasswordVisible);

        txtConfirmPassword.setVisible(!confirmPasswordVisible);
        txtConfirmPassword.setManaged(!confirmPasswordVisible);

        iconConfirmPassword.setText(confirmPasswordVisible ? "Hide" : "Show");
    }

    private void updatePasswordStrength() {
        String password = txtPassword.getText();

        if (password.isBlank()) {
            lblPasswordStrength.setText("Password strength: ");
            lblPasswordStrength.setStyle("-fx-font-size: 11px;");
            return;
        }

        int score = 0;

        if (password.length() >= 8) score++;
        if (password.matches(".*[A-Z].*")) score++;
        if (password.matches(".*[a-z].*")) score++;
        if (password.matches(".*\\d.*")) score++;
        if (password.matches(".*[^a-zA-Z0-9].*")) score++;

        if (score <= 2) {
            lblPasswordStrength.setText("Password strength: Weak");
            lblPasswordStrength.setStyle("-fx-text-fill: red; -fx-font-size: 11px;");
        } else if (score <= 4) {
            lblPasswordStrength.setText("Password strength: Medium");
            lblPasswordStrength.setStyle("-fx-text-fill: orange; -fx-font-size: 11px;");
        } else {
            lblPasswordStrength.setText("Password strength: Strong");
            lblPasswordStrength.setStyle("-fx-text-fill: green; -fx-font-size: 11px;");
        }
    }

    private void updatePasswordMatch() {
        String password = txtPassword.getText();
        String confirmPassword = txtConfirmPassword.getText();

        if (confirmPassword.isBlank()) {
            lblPasswordMatch.setText("");
            return;
        }

        if (password.equals(confirmPassword)) {
            lblPasswordMatch.setText("Passwords match");
            lblPasswordMatch.setStyle("-fx-text-fill: green; -fx-font-size: 11px;");
        } else {
            lblPasswordMatch.setText("Passwords do not match");
            lblPasswordMatch.setStyle("-fx-text-fill: red; -fx-font-size: 11px;");
        }
    }

    private boolean isStrongPassword(String password) {
        return password.length() >= 8
                && password.matches(".*[A-Z].*")
                && password.matches(".*[a-z].*")
                && password.matches(".*\\d.*")
                && password.matches(".*[^a-zA-Z0-9].*");
    }

    private void showAlert(Alert.AlertType type, String title, String msg) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}