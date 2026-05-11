package com.clinic.controller;

import com.clinic.dao.AppointmentDAO;
import com.clinic.model.Appointment;
import com.clinic.util.SceneSwitcher;
import com.clinic.util.Session;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.List;

public class MyAppointmentsController {

    @FXML private TextField txtSearch;
    @FXML private TableView<Appointment> tableAppointments;

    @FXML private TableColumn<Appointment, Integer> colId;
    @FXML private TableColumn<Appointment, String> colDoctorName;
    @FXML private TableColumn<Appointment, String> colDate;
    @FXML private TableColumn<Appointment, String> colTime;
    @FXML private TableColumn<Appointment, String> colStatus;

    private final AppointmentDAO appointmentDAO = new AppointmentDAO();
    private ObservableList<Appointment> appointmentList = FXCollections.observableArrayList();

@FXML
public void initialize() {
    colId.setCellFactory(column -> new TableCell<>() {
        @Override
        protected void updateItem(Integer item, boolean empty) {
            super.updateItem(item, empty);

            if (empty) {
                setText(null);
            } else {
                setText(String.valueOf(getIndex() + 1));
            }
        }
    });

    colId.setStyle("-fx-alignment: CENTER;");
    colDate.setStyle("-fx-alignment: CENTER;");
    colTime.setStyle("-fx-alignment: CENTER;");
    colStatus.setStyle("-fx-alignment: CENTER;");

    colDoctorName.setCellValueFactory(new PropertyValueFactory<>("doctorName"));
    colDate.setCellValueFactory(new PropertyValueFactory<>("date"));
    colTime.setCellValueFactory(new PropertyValueFactory<>("time"));
    colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

    loadAppointments();
}

    

    private void loadAppointments() {
        int userId = Session.getUserId();

        List<Appointment> list = appointmentDAO.getAppointmentsByUser(userId);

        appointmentList = FXCollections.observableArrayList(list);
        tableAppointments.setItems(appointmentList);
    }

@FXML
public void handleSearch(ActionEvent event) {
    String keyword = txtSearch.getText().toLowerCase();

    if (keyword.isBlank()) {
        tableAppointments.setItems(appointmentList);
        return;
    }

    ObservableList<Appointment> filteredList = FXCollections.observableArrayList();

    for (Appointment appointment : appointmentList) {
        if (appointment.getDoctorName().toLowerCase().contains(keyword)) {
            filteredList.add(appointment);
        }
    }

    tableAppointments.setItems(filteredList);
}

    @FXML
    public void handleReset(ActionEvent event) {
        txtSearch.clear();
        loadAppointments();
    }

@FXML
public void handleCancelAppointment(ActionEvent event) {
    Appointment selected = tableAppointments.getSelectionModel().getSelectedItem();

    if (selected.getStatus().equalsIgnoreCase("Cancelled")) {
    showAlert(Alert.AlertType.WARNING, "Warning", "This appointment is already cancelled.");
    return;
}

if (selected.getStatus().equalsIgnoreCase("Rejected")) {
    showAlert(Alert.AlertType.WARNING, "Warning", "This appointment was already rejected.");
    return;
}

    Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
    confirmAlert.setTitle("Confirm Cancellation");
    confirmAlert.setContentText(
        """
        Are you sure you want to cancel this appointment ?

        Doctor : %s
        Date : %s
        Time : %s""".formatted(selected.getDoctorName(), selected.getDate(), selected.getTime())
    );

    ButtonType yesButton = new ButtonType("Yes, Cancel");
    ButtonType noButton = new ButtonType("No, Keep It", ButtonBar.ButtonData.CANCEL_CLOSE);

    confirmAlert.getButtonTypes().setAll(yesButton, noButton);

    java.util.Optional<ButtonType> result = confirmAlert.showAndWait();

    if (result.isPresent() && result.get() == yesButton) {
        boolean success = appointmentDAO.cancelAppointment(selected.getId());

        if (success) {
            showAlert(Alert.AlertType.INFORMATION, "Success", "Appointment cancelled successfully.");
            loadAppointments();
        } else {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to cancel appointment.");
        }
    }
}

    @FXML
    public void handleBack(ActionEvent event) {
        SceneSwitcher.switchTo(event, "/fxml/dashboard.fxml", "Dashboard");
    }

    private void showAlert(Alert.AlertType type, String title, String msg) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}