package com.clinic.controller;

import com.clinic.util.SceneSwitcher;
import com.clinic.util.Session;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class AdminDashboardController {

    @FXML
    private Label welcomeLabel;

    @FXML
    public void initialize() {
        String username = Session.getUsername();

        if (username != null && !username.isBlank()) {
            String capitalized = username.substring(0, 1).toUpperCase() + username.substring(1).toLowerCase();
            welcomeLabel.setText("Welcome " + capitalized + " !");
        } else {
            welcomeLabel.setText("Welcome Admin !");
        }
    }

    @FXML
    private void handleViewUsers(ActionEvent event) {
        SceneSwitcher.switchTo(event, "/fxml/adminUsers.fxml", "View Users");
    }

    @FXML
    private void handleViewAppointments(ActionEvent event) {
        SceneSwitcher.switchTo(event, "/fxml/adminAppointments.fxml", "View Appointments");
    }

    @FXML
    private void handleApproval(ActionEvent event) {
        SceneSwitcher.switchTo(event, "/fxml/adminApproval.fxml", "Approve Booking");
    }

    @FXML
    private void handleLogout(ActionEvent event) {
        Session.clear();
        SceneSwitcher.switchTo(event, "/fxml/login.fxml", "Login");
    }
}