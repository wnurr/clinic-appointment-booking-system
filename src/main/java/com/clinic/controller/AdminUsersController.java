package com.clinic.controller;

import com.clinic.dao.UserDAO;
import com.clinic.model.User;
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

public class AdminUsersController {

    @FXML private TableView<User> tableUsers;
    @FXML private TableColumn<User, Integer> colId;
    @FXML private TableColumn<User, String> colFullName;
    @FXML private TableColumn<User, String> colPhone;
    @FXML private TableColumn<User, String> colNric;
    @FXML private TextField txtSearch;

    private final UserDAO userDAO = new UserDAO();
    private final ObservableList<User> userList = FXCollections.observableArrayList();
    private FilteredList<User> filteredList;

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
        colFullName.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        colPhone.setCellValueFactory(new PropertyValueFactory<>("phone"));
        colNric.setCellValueFactory(new PropertyValueFactory<>("nric"));

        filteredList = new FilteredList<>(userList, b -> true);
        tableUsers.setItems(filteredList);

        txtSearch.textProperty().addListener((observable, oldValue, newValue) -> {
            String keyword = newValue == null ? "" : newValue.toLowerCase().trim();

            filteredList.setPredicate(user -> {
                if (keyword.isBlank()) {
                    return true;
                }

                return user.getFullName().toLowerCase().contains(keyword)
                        || user.getPhone().toLowerCase().contains(keyword)
                        || user.getNric().toLowerCase().contains(keyword);
            });
        });

        loadUsers();
    }

    private void loadUsers() {
        userList.setAll(userDAO.getAllUsers());
    }

    @FXML
    private void handleRefresh(ActionEvent event) {
        txtSearch.clear();
        loadUsers();
    }

    @FXML
    private void handleBack(ActionEvent event) {
        SceneSwitcher.switchTo(event, "/fxml/adminDashboard.fxml", "Admin Dashboard");
    }
}