package com.clinic.controller;

import com.clinic.util.SceneSwitcher;
import com.clinic.util.Session;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class DashboardController {

    @FXML
    private Label welcomeLabel;

    @FXML
public void initialize() {
    String username = Session.getUsername();

    if (username != null && !username.isBlank()) {

        String formattedUsername = formatName(username);

        welcomeLabel.setText("Welcome " + formattedUsername + " !");
    } else {
        welcomeLabel.setText("Welcome !");
    }
}

private String formatName(String name) {
    String[] words = name.trim().toLowerCase().split("\\s+");

    StringBuilder formatted = new StringBuilder();

    for (String word : words) {
        if (!word.isEmpty()) {
            formatted.append(Character.toUpperCase(word.charAt(0)))
                     .append(word.substring(1))
                     .append(" ");
        }
    }

    return formatted.toString().trim();
}

    @FXML
    public void handleBookAppointment(ActionEvent event) {
        SceneSwitcher.switchTo(event, "/fxml/bookAppointment.fxml", "Book Appointment");
    }

    @FXML
    public void handleMyAppointments(ActionEvent event) {
        SceneSwitcher.switchTo(event, "/fxml/myAppointments.fxml", "My Appointments");
    }

    @FXML
    public void handleLogout(ActionEvent event) {
        Session.clear();
        SceneSwitcher.switchTo(event, "/fxml/login.fxml", "Login");
    }

    @FXML
    public void handleViewProfile(ActionEvent event) {
        SceneSwitcher.switchTo(event, "/fxml/profile.fxml", "My Profile");
    }
}