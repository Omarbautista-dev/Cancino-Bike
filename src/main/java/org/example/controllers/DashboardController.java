package org.example.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.util.Objects;

public class DashboardController {

    @FXML
    private AnchorPane contentPane;

    @FXML
    private ImageView logoImage;

    @FXML
    public void initialize() {
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
        mostrarMensaje("Módulo Ventas en construcción");
    }

    @FXML
    private void abrirClientes() {
        cargarVista("/org/example/views/clientes.fxml");
    }

    @FXML
    private void abrirProductos() {
        cargarVista("/org/example/views/productos.fxml");
    }

    @FXML
    private void abrirInventario() {
        cargarVista("/org/example/views/inventario.fxml");
    }

    @FXML
    private void abrirFacturas() {
        cargarVista("/org/example/views/facturas.fxml");
    }

    @FXML
    private void abrirCatalogos() {
        cargarVista("/org/example/views/catalogos.fxml");
    }

    @FXML
    private void abrirDatosPersonales() {
        cargarVista("/org/example/views/datos_personales.fxml");
    }

    @FXML
    private void abrirUsuarios() {
        cargarVista("/org/example/views/usuarios.fxml");
    }

    @FXML
    private void abrirCambiarPassword() {
        cargarVista("/org/example/views/cambiar_password.fxml");
    }

    @FXML
    private void cerrarSesion() {
        try {
            Parent login = FXMLLoader.load(
                    Objects.requireNonNull(
                            getClass().getResource("/org/example/views/login.fxml")
                    )
            );

            contentPane.getScene().setRoot(login);

        } catch (IOException e) {
            mostrarMensaje("Error al cerrar sesión");
        }
    }

    private void cargarVista(String ruta) {
        try {
            Parent vista = FXMLLoader.load(
                    Objects.requireNonNull(getClass().getResource(ruta))
            );

            contentPane.getChildren().clear();
            contentPane.getChildren().add(vista);

            AnchorPane.setTopAnchor(vista, 0.0);
            AnchorPane.setBottomAnchor(vista, 0.0);
            AnchorPane.setLeftAnchor(vista, 0.0);
            AnchorPane.setRightAnchor(vista, 0.0);

        } catch (IOException | NullPointerException e) {
            mostrarMensaje("No se encontró la vista: " + ruta);
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