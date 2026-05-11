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
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

public class AdminAppointmentsController {

    @FXML private TableView<Appointment> tableAppointments;
    @FXML private TableColumn<Appointment, Integer> colId;
    @FXML private TableColumn<Appointment, String> colPatientName;
    @FXML private TableColumn<Appointment, String> colDoctorName;
    @FXML private TableColumn<Appointment, String> colDate;
    @FXML private TableColumn<Appointment, String> colTime;
    @FXML private TableColumn<Appointment, String> colStatus;
    @FXML private TextField txtSearch;

    private final AppointmentDAO appointmentDAO = new AppointmentDAO();
    private final ObservableList<Appointment> appointmentList = FXCollections.observableArrayList();
    private FilteredList<Appointment> filteredList;

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

        filteredList = new FilteredList<>(appointmentList, b -> true);
        tableAppointments.setItems(filteredList);

        txtSearch.textProperty().addListener((observable, oldValue, newValue) -> {
            String keyword = newValue == null ? "" : newValue.toLowerCase().trim();

            filteredList.setPredicate(appointment -> {
                if (keyword.isBlank()) {
                    return true;
                }

                return String.valueOf(appointment.getId()).contains(keyword)
                        || appointment.getPatientName().toLowerCase().contains(keyword)
                        || appointment.getDoctorName().toLowerCase().contains(keyword)
                        || appointment.getDate().toLowerCase().contains(keyword)
                        || appointment.getTime().toLowerCase().contains(keyword)
                        || appointment.getStatus().toLowerCase().contains(keyword);
            });
        });

        loadAppointments();
    }

    private void loadAppointments() {
        appointmentList.setAll(appointmentDAO.getAllAppointments());
    }

    @FXML
    private void handleRefresh(ActionEvent event) {
        txtSearch.clear();
        loadAppointments();
    }

    @FXML
    private void handleBack(ActionEvent event) {
        SceneSwitcher.switchTo(event, "/fxml/adminDashboard.fxml", "Admin Dashboard");
    }
}