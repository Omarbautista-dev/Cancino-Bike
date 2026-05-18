package org.example.controllers;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import org.example.models.Proveedor;
import org.example.models.ProveedorModel;

import java.util.Objects;

public class ProveedoresController {

    @FXML private ImageView logoImage;

    @FXML private TextField txtBuscar;

    @FXML private TextField txtEmpresa;
    @FXML private TextField txtContacto;
    @FXML private TextField txtTelefono;
    @FXML private TextField txtEmail;
    @FXML private TextField txtRfc;
    @FXML private TextField txtDireccion;

    @FXML private TableView<Proveedor> tablaProveedores;

    @FXML private TableColumn<Proveedor, Integer> colId;
    @FXML private TableColumn<Proveedor, String> colEmpresa;
    @FXML private TableColumn<Proveedor, String> colContacto;
    @FXML private TableColumn<Proveedor, String> colTelefono;
    @FXML private TableColumn<Proveedor, String> colEmail;
    @FXML private TableColumn<Proveedor, String> colRfc;
    @FXML private TableColumn<Proveedor, String> colDireccion;

    private final ProveedorModel proveedorModel =
            new ProveedorModel();

    private Proveedor proveedorSeleccionado;

    @FXML
    public void initialize() {

        cargarLogo();

        configurarTabla();

        cargarProveedores();

        configurarBusqueda();

        detectarSeleccionTabla();
    }

    private void cargarLogo() {

        try {

            Image logo = new Image(
                    Objects.requireNonNull(
                            getClass().getResourceAsStream(
                                    "/org/example/img/logo.jpeg"
                            )
                    )
            );

            logoImage.setImage(logo);

        } catch (Exception e) {
            System.out.println("No se pudo cargar logo");
        }
    }

    private void configurarTabla() {

        colId.setCellValueFactory(
                new PropertyValueFactory<>("idProveedor")
        );

        colEmpresa.setCellValueFactory(
                new PropertyValueFactory<>("nombreEmpresa")
        );

        colContacto.setCellValueFactory(
                new PropertyValueFactory<>("contacto")
        );

        colTelefono.setCellValueFactory(
                new PropertyValueFactory<>("telefono")
        );

        colEmail.setCellValueFactory(
                new PropertyValueFactory<>("email")
        );

        colRfc.setCellValueFactory(
                new PropertyValueFactory<>("rfc")
        );

        colDireccion.setCellValueFactory(
                new PropertyValueFactory<>("direccion")
        );
    }

    private void cargarProveedores() {

        tablaProveedores.setItems(
                FXCollections.observableArrayList(
                        proveedorModel.listarProveedores()
                )
        );
    }

    private void configurarBusqueda() {

        txtBuscar.textProperty().addListener(
                (obs, oldValue, newValue) -> {

                    tablaProveedores.setItems(
                            FXCollections.observableArrayList(
                                    proveedorModel.buscarProveedor(newValue)
                            )
                    );
                }
        );
    }

    private void detectarSeleccionTabla() {

        tablaProveedores.getSelectionModel()
                .selectedItemProperty()
                .addListener((obs, oldSelection, proveedor) -> {

                    if (proveedor != null) {

                        proveedorSeleccionado = proveedor;

                        txtEmpresa.setText(
                                proveedor.getNombreEmpresa()
                        );

                        txtContacto.setText(
                                proveedor.getContacto()
                        );

                        txtTelefono.setText(
                                proveedor.getTelefono()
                        );

                        txtEmail.setText(
                                proveedor.getEmail()
                        );

                        txtRfc.setText(
                                proveedor.getRfc()
                        );

                        txtDireccion.setText(
                                proveedor.getDireccion()
                        );
                    }
                });
    }

    @FXML
    private void guardarProveedor() {

        if (!validarCampos()) return;

        boolean ok = proveedorModel.insertarProveedor(
                txtEmpresa.getText().trim(),
                txtContacto.getText().trim(),
                txtTelefono.getText().trim(),
                txtEmail.getText().trim(),
                txtRfc.getText().trim(),
                txtDireccion.getText().trim()
        );

        if (ok) {

            mostrarMensaje("Proveedor guardado.");

            cargarProveedores();

            limpiarFormulario();
        }
    }

    @FXML
    private void actualizarProveedor() {

        if (proveedorSeleccionado == null) {

            mostrarMensaje(
                    "Selecciona un proveedor."
            );

            return;
        }

        if (!validarCampos()) return;

        boolean ok = proveedorModel.actualizarProveedor(
                proveedorSeleccionado.getIdProveedor(),
                txtEmpresa.getText().trim(),
                txtContacto.getText().trim(),
                txtTelefono.getText().trim(),
                txtEmail.getText().trim(),
                txtRfc.getText().trim(),
                txtDireccion.getText().trim()
        );

        if (ok) {

            mostrarMensaje(
                    "Proveedor actualizado."
            );

            cargarProveedores();

            limpiarFormulario();
        }
    }

    @FXML
    private void eliminarProveedor() {

        if (proveedorSeleccionado == null) {

            mostrarMensaje(
                    "Selecciona un proveedor."
            );

            return;
        }

        Alert confirmacion = new Alert(
                Alert.AlertType.CONFIRMATION
        );

        confirmacion.setTitle(
                "Confirmar"
        );

        confirmacion.setHeaderText(null);

        confirmacion.setContentText(
                "¿Eliminar proveedor?"
        );

        ButtonType respuesta =
                confirmacion.showAndWait()
                        .orElse(ButtonType.CANCEL);

        if (respuesta != ButtonType.OK) return;

        boolean ok = proveedorModel.eliminarProveedor(
                proveedorSeleccionado.getIdProveedor()
        );

        if (ok) {

            mostrarMensaje(
                    "Proveedor eliminado."
            );

            cargarProveedores();

            limpiarFormulario();
        }
    }

    @FXML
    private void limpiarFormulario() {

        proveedorSeleccionado = null;

        txtEmpresa.clear();
        txtContacto.clear();
        txtTelefono.clear();
        txtEmail.clear();
        txtRfc.clear();
        txtDireccion.clear();

        tablaProveedores.getSelectionModel()
                .clearSelection();
    }

    private boolean validarCampos() {

        if (txtEmpresa.getText().trim().isEmpty()) {

            mostrarMensaje(
                    "Ingresa nombre empresa."
            );

            return false;
        }

        return true;
    }

    @FXML
    private void abrirDashboard() {

        cambiarVentana(
                "/org/example/views/dashboard.fxml",
                "dashboard.css",
                "SIG-CB - Dashboard"
        );
    }

    private void cambiarVentana(
            String fxml,
            String cssModulo,
            String titulo
    ) {

        try {

            FXMLLoader loader =
                    new FXMLLoader(
                            Objects.requireNonNull(
                                    getClass().getResource(fxml)
                            )
                    );

            Scene scene =
                    new Scene(
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

            Stage stage =
                    (Stage) tablaProveedores
                            .getScene()
                            .getWindow();

            stage.setScene(scene);

            stage.setTitle(titulo);

            stage.centerOnScreen();

        } catch (Exception e) {

            mostrarMensaje(
                    "No se pudo abrir ventana."
            );

            e.printStackTrace();
        }
    }

    private void mostrarMensaje(String mensaje) {

        Alert alert = new Alert(
                Alert.AlertType.INFORMATION
        );

        alert.setTitle("Proveedores");

        alert.setHeaderText(null);

        alert.setContentText(mensaje);

        alert.showAndWait();
    }
}