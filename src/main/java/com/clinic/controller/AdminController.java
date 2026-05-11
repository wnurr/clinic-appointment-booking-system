package com.clinic.controller;

import com.clinic.dao.AppointmentDAO;
import com.clinic.dao.UserDAO;
import com.clinic.model.Appointment;
import com.clinic.model.User;
import com.clinic.util.SceneSwitcher;
import com.clinic.util.Session;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

public class AdminController {

    @FXML private TableView<User> tableUsers;
    @FXML private TableColumn<User, String> colFullName;
    @FXML private TableColumn<User, String> colPhone;
    @FXML private TableColumn<User, String> colNric;

    @FXML private TableView<Appointment> tableAppointments;
    @FXML private TableColumn<Appointment, Integer> colAppointmentId;
    @FXML private TableColumn<Appointment, String> colPatientName;
    @FXML private TableColumn<Appointment, String> colDoctorName;
    @FXML private TableColumn<Appointment, String> colDate;
    @FXML private TableColumn<Appointment, String> colTime;
    @FXML private TableColumn<Appointment, String> colStatus;

    private final UserDAO userDAO = new UserDAO();
    private final AppointmentDAO appointmentDAO = new AppointmentDAO();

    @FXML
    public void initialize() {
        if (!Session.isAdmin()) {
            return;
        }

        colFullName.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        colPhone.setCellValueFactory(new PropertyValueFactory<>("phone"));
        colNric.setCellValueFactory(new PropertyValueFactory<>("nric"));

        colAppointmentId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colPatientName.setCellValueFactory(new PropertyValueFactory<>("patientName"));
        colDoctorName.setCellValueFactory(new PropertyValueFactory<>("doctorName"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        colTime.setCellValueFactory(new PropertyValueFactory<>("time"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

        loadUsers();
        loadAppointments();
    }

    private void loadUsers() {
        ObservableList<User> users = FXCollections.observableArrayList(userDAO.getAllUsers());
        tableUsers.setItems(users);
    }

    private void loadAppointments() {
        ObservableList<Appointment> appointments = FXCollections.observableArrayList(appointmentDAO.getAllAppointments());
        tableAppointments.setItems(appointments);
    }

    @FXML
    private void handleApprove(ActionEvent event) {
        Appointment selected = tableAppointments.getSelectionModel().getSelectedItem();

        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Warning", "Please select an appointment.");
            return;
        }

        if (!selected.getStatus().equalsIgnoreCase("Pending")) {
            showAlert(Alert.AlertType.WARNING, "Warning", "Only pending appointments can be approved.");
            return;
        }

        boolean success = appointmentDAO.updateAppointmentStatus(selected.getId(), "Approved");

        if (success) {
            showAlert(Alert.AlertType.INFORMATION, "Success", "Appointment approved.");
            loadAppointments();
        } else {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to approve appointment.");
        }
    }

    @FXML
    private void handleReject(ActionEvent event) {
        Appointment selected = tableAppointments.getSelectionModel().getSelectedItem();

        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Warning", "Please select an appointment.");
            return;
        }

        if (!selected.getStatus().equalsIgnoreCase("Pending")) {
            showAlert(Alert.AlertType.WARNING, "Warning", "Only pending appointments can be rejected.");
            return;
        }

        boolean success = appointmentDAO.updateAppointmentStatus(selected.getId(), "Rejected");

        if (success) {
            showAlert(Alert.AlertType.INFORMATION, "Success", "Appointment rejected.");
            loadAppointments();
        } else {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to reject appointment.");
        }
    }

    @FXML
    private void handleRefresh(ActionEvent event) {
        loadUsers();
        loadAppointments();
    }

    @FXML
    private void handleLogout(ActionEvent event) {
        Session.clear();
        SceneSwitcher.switchTo(event, "/fxml/login.fxml", "Login");
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}