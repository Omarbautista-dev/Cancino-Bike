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

    @FXML
    private void abrirVentas() {
        cambiarVentana("/org/example/views/ventas.fxml", "ventas.css", "SIG-CB - Ventas");
    }

    @FXML
    private void abrirClientes() {
        cambiarVentana("/org/example/views/clientes.fxml", "clientes.css", "SIG-CB - Clientes");
    }

    @FXML
    private void abrirProductos() {
        cambiarVentana("/org/example/views/productos.fxml", "productos.css", "SIG-CB - Productos");
    }

    @FXML
    private void abrirInventario() {
        cambiarVentana("/org/example/views/inventario.fxml", "inventario.css", "SIG-CB - Inventario");
    }

    @FXML
    private void abrirFacturas() {
        cambiarVentana("/org/example/views/facturas.fxml", "facturas.css", "SIG-CB - Facturas");
    }

    @FXML
    private void abrirCatalogos() {
        cambiarVentana("/org/example/views/catalogos.fxml", "catalogos.css", "SIG-CB - Catálogos");
    }

    @FXML
    private void abrirDatosPersonales() {
        cambiarVentana("/org/example/views/datos_personales.fxml", "datos_personales.css", "SIG-CB - Datos Personales");
    }

    @FXML
    private void abrirUsuarios() {
        cambiarVentana("/org/example/views/usuarios.fxml", "usuarios.css", "SIG-CB - Usuarios");
    }

    @FXML
    private void abrirCambiarPassword() {
        cambiarVentana("/org/example/views/cambiar_password.fxml", "cambiar_password.css", "SIG-CB - Cambiar Password");
    }

    @FXML
    private void cerrarSesion() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/org/example/views/login.fxml")
            );

            Scene scene = new Scene(loader.load(), 710, 610);

            scene.getStylesheets().add(
                    getClass().getResource("/org/example/css/styles.css").toExternalForm()
            );

            Stage stage = (Stage) contentPane.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("SIG-CB - Login");
            stage.centerOnScreen();

        } catch (Exception e) {
            mostrarMensaje("Error al cerrar sesión: " + e.getMessage());
            e.printStackTrace();
        }
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

            Stage stage = (Stage) contentPane.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle(titulo);
            stage.centerOnScreen();

        } catch (Exception e) {
            mostrarMensaje("No se pudo abrir la ventana: " + titulo + "\n" + e.getMessage());
            e.printStackTrace();
        }
    }

    private void mostrarMensaje(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Cancino Bike");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}