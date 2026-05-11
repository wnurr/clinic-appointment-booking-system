package com.clinic.controller;

import com.clinic.dao.UserDAO;
import com.clinic.model.User;
import com.clinic.util.SceneSwitcher;
import com.clinic.util.Session;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class ProfileController {

    @FXML private Label lblUsername;
    @FXML private Label lblFullname;
    @FXML private Label lblPhone;
    @FXML private Label lblNric;

    private final UserDAO userDAO = new UserDAO();

    @FXML
    public void initialize() {
        String username = Session.getUsername();

        if (username != null && !username.isBlank()) {
            User user = userDAO.getUserByUsername(username);

            if (user != null) {
                lblUsername.setText("Username : " + user.getUsername());
                lblFullname.setText("Full Name : " + capitalize(user.getFullName()));
                lblPhone.setText("Phone Number : " + user.getPhone());
                lblNric.setText("NRIC : " + user.getNric());
            } else {
                lblUsername.setText("Username : -");
                lblFullname.setText("Full Name : -");
                lblPhone.setText("Phone Number : -");
                lblNric.setText("NRIC : -");
            }
        }
    }

    private String capitalize(String text) {
    if (text == null || text.isEmpty()) return text;

    String[] words = text.toLowerCase().split(" ");
    StringBuilder result = new StringBuilder();

    for (String word : words) {
        if (!word.isEmpty()) {
            result.append(Character.toUpperCase(word.charAt(0)))
                  .append(word.substring(1))
                  .append(" ");
        }
    }

    return result.toString().trim();
}
    @FXML
    private void handleBack(ActionEvent event) {
        SceneSwitcher.switchTo(event, "/fxml/dashboard.fxml", "Dashboard");
    }
}