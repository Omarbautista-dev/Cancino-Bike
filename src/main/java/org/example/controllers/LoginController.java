package org.example.controllers;

import org.example.models.UsuarioModel;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class LoginController {

    @FXML
    private TextField txtUsuario;

    @FXML
    private PasswordField txtPassword;

    private final UsuarioModel usuarioModel = new UsuarioModel();

    @FXML
    private void iniciarSesion() {
        String usuario = txtUsuario.getText().trim();
        String password = txtPassword.getText().trim();

        if (usuario.isEmpty() || password.isEmpty()) {
            mostrarAlerta("Campos vacíos", "Ingresa usuario y contraseña.");
            return;
        }

        boolean acceso = usuarioModel.validarLogin(usuario, password);

        if (acceso) {
            abrirDashboard();
        } else {
            mostrarAlerta("Acceso denegado", "Usuario o contraseña incorrectos.");
            txtPassword.clear();
        }
    }

    private void abrirDashboard() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/org/example/views/dashboard.fxml")
            );

            Scene scene = new Scene(loader.load(), 1100, 780);

            scene.getStylesheets().add(
                    getClass().getResource("/org/example/css/global.css").toExternalForm()
            );

            scene.getStylesheets().add(
                    getClass().getResource("/org/example/css/dashboard.css").toExternalForm()
            );

            Stage stage = (Stage) txtUsuario.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("SIG-CB - Dashboard");
            stage.centerOnScreen();

        } catch (Exception e) {
            mostrarAlerta("Error", "No se pudo abrir el dashboard: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}