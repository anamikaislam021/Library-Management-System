/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package library;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.StackPane;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
import javafx.event.ActionEvent;

public class DashboardController {

    @FXML
    private StackPane contentPane;

    @FXML
    public void goToBooks(ActionEvent event) {
        loadUI("Book.fxml");
    }

    @FXML
    public void goToMembers(ActionEvent event) {
        loadUI("Member.fxml");
    }

    @FXML
    public void goToIssueReturn(ActionEvent event) {
        loadUI("IssueReturn.fxml");
    }

    @FXML
    public void logout(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("Login.fxml"));
            Stage stage = (Stage) contentPane.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    

    private void loadUI(String fxml) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(fxml));
            contentPane.getChildren().setAll(root);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

