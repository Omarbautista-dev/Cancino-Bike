package org.example.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import org.example.models.SeguridadModel;

import java.util.Objects;

public class SeguridadController {

    @FXML private ImageView logoImage;

    @FXML private TextField txtUsuario;
    @FXML private PasswordField txtPasswordActual;
    @FXML private PasswordField txtPasswordNuevo;
    @FXML private PasswordField txtPasswordConfirmar;

    private final SeguridadModel seguridadModel = new SeguridadModel();

    @FXML
    public void initialize() {
        cargarLogo();
    }

    private void cargarLogo() {
        try {
            Image logo = new Image(
                    Objects.requireNonNull(
                            getClass().getResourceAsStream("/org/example/img/logo.jpeg")
                    )
            );
            logoImage.setImage(logo);
        } catch (Exception e) {
            System.out.println("No se pudo cargar el logo");
        }
    }

    @FXML
    private void cambiarPassword() {
        String usuario = txtUsuario.getText().trim();
        String actual = txtPasswordActual.getText().trim();
        String nuevo = txtPasswordNuevo.getText().trim();
        String confirmar = txtPasswordConfirmar.getText().trim();

        if (usuario.isEmpty() || actual.isEmpty() || nuevo.isEmpty() || confirmar.isEmpty()) {
            mostrarMensaje("Completa todos los campos.");
            return;
        }

        if (nuevo.length() < 4) {
            mostrarMensaje("El nuevo password debe tener al menos 4 caracteres.");
            return;
        }

        if (!nuevo.equals(confirmar)) {
            mostrarMensaje("El nuevo password y la confirmación no coinciden.");
            return;
        }

        if (actual.equals(nuevo)) {
            mostrarMensaje("El nuevo password no puede ser igual al actual.");
            return;
        }

        boolean passwordCorrecto = seguridadModel.validarPasswordActual(usuario, actual);

        if (!passwordCorrecto) {
            mostrarMensaje("Usuario o password actual incorrecto.");
            return;
        }

        boolean ok = seguridadModel.cambiarPassword(usuario, nuevo);

        if (ok) {
            mostrarMensaje("Password actualizado correctamente.");
            limpiarFormulario();
        } else {
            mostrarMensaje("No se pudo actualizar el password.");
        }
    }

    @FXML
    private void limpiarFormulario() {
        txtUsuario.clear();
        txtPasswordActual.clear();
        txtPasswordNuevo.clear();
        txtPasswordConfirmar.clear();
    }

    @FXML
    private void mostrarCambiarPassword() {
        mostrarMensaje("Estás en Cambio de Password.");
    }

    @FXML
    private void mostrarUsuarios() {
        mostrarMensaje("Usuarios en construcción.");
    }

    @FXML
    private void mostrarPrivilegios() {
        mostrarMensaje("Privilegios en construcción.");
    }

    @FXML
    private void mostrarAccesos() {
        mostrarMensaje("Accesos en construcción.");
    }

    @FXML
    private void abrirDashboard() {
        cambiarVentana(
                "/org/example/views/dashboard.fxml",
                "dashboard.css",
                "SIG-CB - Dashboard"
        );
    }

    private void cambiarVentana(String fxml, String cssModulo, String titulo) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    Objects.requireNonNull(getClass().getResource(fxml))
            );

            Scene scene = new Scene(loader.load(), 1100, 780);

            scene.getStylesheets().add(
                    Objects.requireNonNull(
                            getClass().getResource("/org/example/css/global.css")
                    ).toExternalForm()
            );

            scene.getStylesheets().add(
                    Objects.requireNonNull(
                            getClass().getResource("/org/example/css/" + cssModulo)
                    ).toExternalForm()
            );

            Stage stage = (Stage) txtUsuario.getScene().getWindow();

            stage.setScene(scene);
            stage.setTitle(titulo);
            stage.centerOnScreen();

        } catch (Exception e) {
            mostrarMensaje("No se pudo abrir la ventana.");
            e.printStackTrace();
        }
    }

    private void mostrarMensaje(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Seguridad");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}