package com.clinic.controller;

import com.clinic.dao.AppointmentDAO;
import com.clinic.util.SceneSwitcher;
import com.clinic.util.Session;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToggleGroup;

import java.time.LocalDate;
import java.util.List;

public class BookAppointmentController {

    @FXML private DatePicker dpAppointmentDate;
    @FXML private ComboBox<String> cbAppointmentTime;
    @FXML private TextArea txtNotes;
    @FXML private Label lblAvailabilityInfo;

    @FXML private RadioButton rbDrAli;
    @FXML private RadioButton rbDrSarah;
    @FXML private RadioButton rbDrJohn;

    private ToggleGroup doctorGroup;
    private final AppointmentDAO appointmentDAO = new AppointmentDAO();

    @FXML
    public void initialize() {
        doctorGroup = new ToggleGroup();
        rbDrAli.setToggleGroup(doctorGroup);
        rbDrSarah.setToggleGroup(doctorGroup);
        rbDrJohn.setToggleGroup(doctorGroup);

        dpAppointmentDate.setDayCellFactory(datePicker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                setDisable(empty || date.isBefore(LocalDate.now()));
            }
        });

        doctorGroup.selectedToggleProperty().addListener((obs, oldVal, newVal) -> loadAvailableSlots());
        dpAppointmentDate.valueProperty().addListener((obs, oldVal, newVal) -> loadAvailableSlots());
    }

    private void loadAvailableSlots() {
        cbAppointmentTime.getItems().clear();
        cbAppointmentTime.setValue(null);
        cbAppointmentTime.setDisable(true);

        if (doctorGroup.getSelectedToggle() == null || dpAppointmentDate.getValue() == null) {
            lblAvailabilityInfo.setText("Choose doctor and date to view available slots");
            return;
        }

        RadioButton selectedDoctor = (RadioButton) doctorGroup.getSelectedToggle();
        String doctorName = selectedDoctor.getText();
        LocalDate selectedDate = dpAppointmentDate.getValue();

        List<String> slots = appointmentDAO.getAvailableSlots(doctorName, selectedDate);

        if (slots.isEmpty()) {
            lblAvailabilityInfo.setText("No available slots for " + doctorName + " on " + selectedDate);
        } else {
            cbAppointmentTime.setItems(FXCollections.observableArrayList(slots));
            cbAppointmentTime.setDisable(false);
            lblAvailabilityInfo.setText("Available slots for " + doctorName + " on " + selectedDate);
        }
    }

    @FXML
    public void handleBookAppointment(ActionEvent event) {

        if (dpAppointmentDate.getValue() == null
                || doctorGroup.getSelectedToggle() == null
                || cbAppointmentTime.getValue() == null) {

            showAlert(Alert.AlertType.ERROR, "Validation Error",
                    "Please fill in all required fields.");
            return;
        }

        int userId = Session.getUserId();

        if (userId == 0) {
            showAlert(Alert.AlertType.ERROR, "Session Error",
                    "Please login again.");
            return;
        }

        RadioButton selectedDoctor = (RadioButton) doctorGroup.getSelectedToggle();
        String doctorName = selectedDoctor.getText();
        LocalDate appointmentDate = dpAppointmentDate.getValue();
        String appointmentTime = cbAppointmentTime.getValue();
        String notes = txtNotes.getText();

        boolean available = appointmentDAO.isSlotStillAvailable(
                doctorName, appointmentDate, appointmentTime
        );

        if (!available) {
            showAlert(Alert.AlertType.WARNING, "Slot Taken",
                    "This slot has already been booked.");
            loadAvailableSlots();
            return;
        }

        boolean success = appointmentDAO.bookAppointment(
                userId,
                doctorName,
                appointmentDate,
                appointmentTime,
                notes
        );

        if (success) {
    showAlert(Alert.AlertType.INFORMATION, "Success",
            """
            Appointment submitted successfully !

            Patient : %s
            Doctor : %s
            Date : %s
            Time : %s
            Status : Pending
            """.formatted(
                    Session.getFullName(),
                    doctorName,
                    appointmentDate,
                    appointmentTime
            ));

    handleReset();
} else {
            showAlert(Alert.AlertType.ERROR, "Error",
                    "Failed to book appointment.");
        }
    }

    @FXML
    public void handleBack(ActionEvent event) {
        SceneSwitcher.switchTo(event, "/fxml/dashboard.fxml", "Dashboard");
    }

    @FXML
    private void handleReset() {
        dpAppointmentDate.setValue(null);
        cbAppointmentTime.getItems().clear();
        cbAppointmentTime.setValue(null);
        cbAppointmentTime.setDisable(true);
        txtNotes.clear();
        doctorGroup.selectToggle(null);
        lblAvailabilityInfo.setText("Choose doctor and date to view available slots");
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}