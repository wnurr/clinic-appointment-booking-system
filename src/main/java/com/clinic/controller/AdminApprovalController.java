package com.clinic.controller;

import com.clinic.dao.AppointmentDAO;
import com.clinic.model.Appointment;
import com.clinic.util.SceneSwitcher;
import com.clinic.util.Session;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.TableCell;

public class AdminApprovalController {

    @FXML private TableView<Appointment> tablePendingAppointments;
    @FXML private TableColumn<Appointment, Integer> colId;
    @FXML private TableColumn<Appointment, String> colPatientName;
    @FXML private TableColumn<Appointment, String> colDoctorName;
    @FXML private TableColumn<Appointment, String> colDate;
    @FXML private TableColumn<Appointment, String> colTime;
    @FXML private TableColumn<Appointment, String> colStatus;
    @FXML private TextField txtSearch;

    private final AppointmentDAO appointmentDAO = new AppointmentDAO();
    private ObservableList<Appointment> pendingList;

    @FXML
    public void initialize() {
        if (!Session.isAdmin()) {
            return;
        }

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
        colPatientName.setCellValueFactory(new PropertyValueFactory<>("patientName"));
        colDoctorName.setCellValueFactory(new PropertyValueFactory<>("doctorName"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        colTime.setCellValueFactory(new PropertyValueFactory<>("time"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

        loadPendingAppointments();
        setupSearch();
    }

    private void loadPendingAppointments() {
        pendingList = FXCollections.observableArrayList(appointmentDAO.getPendingAppointments());
        tablePendingAppointments.setItems(pendingList);
    }

    private void setupSearch() {
        FilteredList<Appointment> filteredList = new FilteredList<>(pendingList, b -> true);

        txtSearch.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredList.setPredicate(appointment -> {
                if (newValue == null || newValue.isBlank()) {
                    return true;
                }

                String keyword = newValue.toLowerCase();

                return String.valueOf(appointment.getId()).contains(keyword)
                        || appointment.getPatientName().toLowerCase().contains(keyword)
                        || appointment.getDoctorName().toLowerCase().contains(keyword)
                        || appointment.getDate().toLowerCase().contains(keyword)
                        || appointment.getTime().toLowerCase().contains(keyword)
                        || appointment.getStatus().toLowerCase().contains(keyword);
            });

            tablePendingAppointments.setItems(filteredList);
        });
    }

    @FXML
    private void handleApprove(ActionEvent event) {
        Appointment selected = tablePendingAppointments.getSelectionModel().getSelectedItem();

        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Warning", "Please select a pending appointment.");
            return;
        }

        boolean success = appointmentDAO.updateAppointmentStatus(selected.getId(), "Approved");

        if (success) {
            showAlert(Alert.AlertType.INFORMATION, "Success", "Appointment approved successfully.");
            handleRefresh(event);
        } else {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to approve appointment.");
        }
    }

    @FXML
    private void handleReject(ActionEvent event) {
        Appointment selected = tablePendingAppointments.getSelectionModel().getSelectedItem();

        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Warning", "Please select a pending appointment.");
            return;
        }

        boolean success = appointmentDAO.updateAppointmentStatus(selected.getId(), "Rejected");

        if (success) {
            showAlert(Alert.AlertType.INFORMATION, "Success", "Appointment rejected successfully.");
            handleRefresh(event);
        } else {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to reject appointment.");
        }
    }

    @FXML
    private void handleRefresh(ActionEvent event) {
        loadPendingAppointments();
        setupSearch();
        txtSearch.clear();
    }

    @FXML
    private void handleBack(ActionEvent event) {
        SceneSwitcher.switchTo(event, "/fxml/adminDashboard.fxml", "Admin Dashboard");
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}