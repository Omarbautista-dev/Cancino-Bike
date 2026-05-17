package org.example.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.util.Objects;

public class DashboardController {

    @FXML
    private AnchorPane contentPane;

    @FXML
    private ImageView logoImage;

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

    // =========================
    // VENTAS
    // =========================

    @FXML
    private void abrirVentas() {
        cambiarVentana(
                "/org/example/views/ventas.fxml",
                "ventas.css",
                "SIG-CB - Ventas"
        );
    }

    // =========================
    // INVENTARIO
    // =========================

    @FXML
    private void abrirInventario() {
        cambiarVentana(
                "/org/example/views/inventario.fxml",
                "inventario.css",
                "SIG-CB - Inventario"
        );
    }

    // =========================
    // PROVEEDORES
    // =========================

    @FXML
    private void abrirProveedores() {
        cambiarVentana(
                "/org/example/views/proveedores.fxml",
                "proveedores.css",
                "SIG-CB - Proveedores"
        );
    }

    // =========================
    // REPORTES
    // =========================

    @FXML
    private void abrirReportes() {
        cambiarVentana(
                "/org/example/views/reportes.fxml",
                "reportes.css",
                "SIG-CB - Reportes"
        );
    }

    // =========================
    // SEGURIDAD
    // =========================

    @FXML
    private void abrirSeguridad() {
        cambiarVentana(
                "/org/example/views/seguridad.fxml",
                "seguridad.css",
                "SIG-CB - Seguridad"
        );
    }

    // =========================
    // CERRAR SESIÓN
    // =========================

    @FXML
    private void cerrarSesion() {

        try {

            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/org/example/views/login.fxml")
            );

            Scene scene = new Scene(loader.load(), 710, 610);

            scene.getStylesheets().add(
                    Objects.requireNonNull(
                            getClass().getResource("/org/example/css/styles.css")
                    ).toExternalForm()
            );

            Stage stage = (Stage) contentPane.getScene().getWindow();

            stage.setScene(scene);

            stage.setTitle("SIG-CB - Login");

            stage.centerOnScreen();

        } catch (Exception e) {

            mostrarMensaje(
                    "Error al cerrar sesión:\n" + e.getMessage()
            );

            e.printStackTrace();
        }
    }

    // =========================
    // CAMBIAR VENTANA
    // =========================

    private void cambiarVentana(
            String fxml,
            String cssModulo,
            String titulo
    ) {

        try {

            FXMLLoader loader = new FXMLLoader(
                    Objects.requireNonNull(
                            getClass().getResource(fxml)
                    )
            );

            Scene scene = new Scene(
                    loader.load(),
                    1100,
                    780
            );

            scene.getStylesheets().add(
                    Objects.requireNonNull(
                            getClass().getResource(
                                    "/org/example/css/global.css"
                            )
                    ).toExternalForm()
            );

            scene.getStylesheets().add(
                    Objects.requireNonNull(
                            getClass().getResource(
                                    "/org/example/css/" + cssModulo
                            )
                    ).toExternalForm()
            );

            Stage stage = (Stage) contentPane
                    .getScene()
                    .getWindow();

            stage.setScene(scene);

            stage.setTitle(titulo);

            stage.centerOnScreen();

        } catch (Exception e) {

            mostrarMensaje(
                    "No se pudo abrir la ventana:\n" + titulo
            );

            e.printStackTrace();
        }
    }

    // =========================
    // ALERTAS
    // =========================

    private void mostrarMensaje(String mensaje) {

        Alert alert = new Alert(
                Alert.AlertType.INFORMATION
        );

        alert.setTitle("SIG-CB");

        alert.setHeaderText(null);

        alert.setContentText(mensaje);

        alert.showAndWait();
    }
}