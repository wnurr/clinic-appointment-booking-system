package com.clinic.util;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class SceneSwitcher {

    public static void switchTo(ActionEvent event, String fxmlPath, String title) {
        try {
            Parent root = FXMLLoader.load(SceneSwitcher.class.getResource(fxmlPath));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            Scene scene = new Scene(root, stage.getWidth(), stage.getHeight());
            scene.getStylesheets().add(SceneSwitcher.class.getResource("/css/style.css").toExternalForm()
            );

            stage.setScene(scene);
            stage.setTitle(title);
            stage.setMaximized(true);
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}