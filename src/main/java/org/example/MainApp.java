package org.example;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp extends Application {

    @Override
    public void start(Stage stage) throws Exception {

        FXMLLoader loader = new FXMLLoader(
                MainApp.class.getResource("/org/example/views/login.fxml")
        );

        Scene scene = new Scene(loader.load());

        scene.getStylesheets().add(
                MainApp.class.getResource("/org/example/css/styles.css").toExternalForm()
        );

        stage.setTitle("SIG-CB - Login");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.centerOnScreen();
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}