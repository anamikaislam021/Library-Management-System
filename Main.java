package library;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.Parent;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage stage) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("login.fxml"));
            Scene scene = new Scene(root);
            stage.setTitle("Library Management System");
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();  // This will give you a stack trace if it still fails
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
