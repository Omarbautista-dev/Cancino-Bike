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

import org.example.models.Producto;
import org.example.models.ProductoModel;
import org.example.models.ProveedorItem;

import java.util.Objects;

public class InventarioController {

    @FXML private ImageView logoImage;
    @FXML private TableView<Producto> tablaInventario;

    @FXML private TableColumn<Producto, Integer> colId;
    @FXML private TableColumn<Producto, String> colCodigo;
    @FXML private TableColumn<Producto, String> colModelo;
    @FXML private TableColumn<Producto, String> colProducto;
    @FXML private TableColumn<Producto, String> colDescripcion;
    @FXML private TableColumn<Producto, Double> colCompra;
    @FXML private TableColumn<Producto, Double> colMenudeo;
    @FXML private TableColumn<Producto, Double> colMayoreo;
    @FXML private TableColumn<Producto, Integer> colStock;
    @FXML private TableColumn<Producto, Integer> colMinimo;
    @FXML private TableColumn<Producto, String> colProveedor;

    @FXML private Label lblCantidadProductos;
    @FXML private Label lblCostoInventario;

    @FXML private TextField txtCodigo;
    @FXML private TextField txtModelo;
    @FXML private TextField txtNombre;
    @FXML private TextField txtDescripcion;
    @FXML private TextField txtCompra;
    @FXML private TextField txtMenudeo;
    @FXML private TextField txtMayoreo;
    @FXML private TextField txtStock;
    @FXML private TextField txtMinimo;
    @FXML private ComboBox<ProveedorItem> cbProveedor;

    private Producto productoSeleccionado;

    private final ProductoModel productoModel = new ProductoModel();

    private enum ModoOperacion {
        NUEVO,
        STOCK,
        PRECIOS,
        AJUSTE,
        GENERAL
    }

    private ModoOperacion modoActual = ModoOperacion.NUEVO;

    @FXML
    public void initialize() {
        cargarLogo();
        configurarTabla();
        cargarProveedores();
        cargarProductos();
        detectarSeleccionTabla();
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

    private void configurarTabla() {
        colId.setCellValueFactory(new PropertyValueFactory<>("idProducto"));
        colCodigo.setCellValueFactory(new PropertyValueFactory<>("codigoBarras"));
        colModelo.setCellValueFactory(new PropertyValueFactory<>("modelo"));
        colProducto.setCellValueFactory(new PropertyValueFactory<>("nombreProducto"));
        colDescripcion.setCellValueFactory(new PropertyValueFactory<>("descripcion"));
        colCompra.setCellValueFactory(new PropertyValueFactory<>("precioCompra"));
        colMenudeo.setCellValueFactory(new PropertyValueFactory<>("precioMenudeo"));
        colMayoreo.setCellValueFactory(new PropertyValueFactory<>("precioMayoreo"));
        colStock.setCellValueFactory(new PropertyValueFactory<>("stock"));
        colMinimo.setCellValueFactory(new PropertyValueFactory<>("stockMinimo"));
        colProveedor.setCellValueFactory(new PropertyValueFactory<>("proveedor"));
    }

    private void cargarProductos() {
        var productos = productoModel.listarProductos();

        tablaInventario.setItems(FXCollections.observableArrayList(productos));

        lblCantidadProductos.setText("Cantidad de productos: " + productos.size());

        double costoTotal = productos.stream()
                .mapToDouble(p -> p.getPrecioCompra() * p.getStock())
                .sum();

        lblCostoInventario.setText(String.format("Costo de inventario: $%.2f", costoTotal));
    }

    private void cargarProveedores() {
        cbProveedor.setItems(
                FXCollections.observableArrayList(productoModel.listarProveedores())
        );
    }

    private void detectarSeleccionTabla() {
        tablaInventario.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSelection, producto) -> {
                    if (producto != null) {
                        productoSeleccionado = producto;
                        modoActual = ModoOperacion.GENERAL;

                        txtCodigo.setText(producto.getCodigoBarras());
                        txtModelo.setText(producto.getModelo());
                        txtNombre.setText(producto.getNombreProducto());
                        txtDescripcion.setText(producto.getDescripcion());
                        txtCompra.setText(String.valueOf(producto.getPrecioCompra()));
                        txtMenudeo.setText(String.valueOf(producto.getPrecioMenudeo()));
                        txtMayoreo.setText(String.valueOf(producto.getPrecioMayoreo()));
                        txtStock.setText(String.valueOf(producto.getStock()));
                        txtMinimo.setText(String.valueOf(producto.getStockMinimo()));

                        seleccionarProveedor(producto.getIdProveedor());

                        habilitarFormulario();
                    }
                }
        );
    }

    private void seleccionarProveedor(int idProveedor) {
        for (ProveedorItem proveedor : cbProveedor.getItems()) {
            if (proveedor.getIdProveedor() == idProveedor) {
                cbProveedor.setValue(proveedor);
                break;
            }
        }
    }

    @FXML
    private void guardarProducto() {
        try {
            if (!validarCampos()) return;

            ProveedorItem proveedor = cbProveedor.getValue();

            boolean ok = productoModel.insertarProducto(
                    txtCodigo.getText().trim(),
                    txtModelo.getText().trim(),
                    txtNombre.getText().trim(),
                    txtDescripcion.getText().trim(),
                    Double.parseDouble(txtCompra.getText().trim()),
                    Double.parseDouble(txtMenudeo.getText().trim()),
                    Double.parseDouble(txtMayoreo.getText().trim()),
                    Integer.parseInt(txtStock.getText().trim()),
                    Integer.parseInt(txtMinimo.getText().trim()),
                    proveedor.getIdProveedor()
            );

            if (ok) {
                mostrarMensaje("Producto guardado correctamente.");
                cargarProductos();
                limpiarFormulario();
            }

        } catch (Exception e) {
            mostrarMensaje("Error al guardar producto.");
            e.printStackTrace();
        }
    }

    @FXML
    private void actualizarProducto() {
        if (productoSeleccionado == null) {
            mostrarMensaje("Selecciona un producto de la tabla.");
            return;
        }

        try {
            int idProveedor = productoSeleccionado.getIdProveedor();

            if (cbProveedor.getValue() != null) {
                idProveedor = cbProveedor.getValue().getIdProveedor();
            }

            boolean ok;

            switch (modoActual) {
                case STOCK -> {
                    if (txtStock.getText().trim().isEmpty() || txtMinimo.getText().trim().isEmpty()) {
                        mostrarMensaje("Ingresa stock y stock mínimo.");
                        return;
                    }

                    ok = productoModel.actualizarProducto(
                            productoSeleccionado.getIdProducto(),
                            productoSeleccionado.getCodigoBarras(),
                            productoSeleccionado.getModelo(),
                            productoSeleccionado.getNombreProducto(),
                            productoSeleccionado.getDescripcion(),
                            productoSeleccionado.getPrecioCompra(),
                            productoSeleccionado.getPrecioMenudeo(),
                            productoSeleccionado.getPrecioMayoreo(),
                            Integer.parseInt(txtStock.getText().trim()),
                            Integer.parseInt(txtMinimo.getText().trim()),
                            idProveedor
                    );

                    if (ok) mostrarMensaje("Stock actualizado correctamente.");
                }

                case PRECIOS -> {
                    if (txtCompra.getText().trim().isEmpty()
                            || txtMenudeo.getText().trim().isEmpty()
                            || txtMayoreo.getText().trim().isEmpty()) {
                        mostrarMensaje("Ingresa los precios.");
                        return;
                    }

                    ok = productoModel.actualizarProducto(
                            productoSeleccionado.getIdProducto(),
                            productoSeleccionado.getCodigoBarras(),
                            productoSeleccionado.getModelo(),
                            productoSeleccionado.getNombreProducto(),
                            productoSeleccionado.getDescripcion(),
                            Double.parseDouble(txtCompra.getText().trim()),
                            Double.parseDouble(txtMenudeo.getText().trim()),
                            Double.parseDouble(txtMayoreo.getText().trim()),
                            productoSeleccionado.getStock(),
                            productoSeleccionado.getStockMinimo(),
                            idProveedor
                    );

                    if (ok) mostrarMensaje("Precios actualizados correctamente.");
                }

                case AJUSTE -> {
                    if (txtStock.getText().trim().isEmpty()) {
                        mostrarMensaje("Ingresa el stock real contado físicamente.");
                        return;
                    }

                    int stockReal = Integer.parseInt(txtStock.getText().trim());

                    ok = productoModel.actualizarProducto(
                            productoSeleccionado.getIdProducto(),
                            productoSeleccionado.getCodigoBarras(),
                            productoSeleccionado.getModelo(),
                            productoSeleccionado.getNombreProducto(),
                            productoSeleccionado.getDescripcion(),
                            productoSeleccionado.getPrecioCompra(),
                            productoSeleccionado.getPrecioMenudeo(),
                            productoSeleccionado.getPrecioMayoreo(),
                            stockReal,
                            productoSeleccionado.getStockMinimo(),
                            idProveedor
                    );

                    if (ok) {
                        mostrarMensaje("Inventario ajustado correctamente.");
                    }
                }

                default -> {
                    if (!validarCampos()) return;

                    ok = productoModel.actualizarProducto(
                            productoSeleccionado.getIdProducto(),
                            txtCodigo.getText().trim(),
                            txtModelo.getText().trim(),
                            txtNombre.getText().trim(),
                            txtDescripcion.getText().trim(),
                            Double.parseDouble(txtCompra.getText().trim()),
                            Double.parseDouble(txtMenudeo.getText().trim()),
                            Double.parseDouble(txtMayoreo.getText().trim()),
                            Integer.parseInt(txtStock.getText().trim()),
                            Integer.parseInt(txtMinimo.getText().trim()),
                            idProveedor
                    );

                    if (ok) mostrarMensaje("Producto actualizado correctamente.");
                }
            }

            cargarProductos();
            limpiarFormulario();

        } catch (NumberFormatException e) {
            mostrarMensaje("Precios y stock deben ser valores numéricos.");
        } catch (Exception e) {
            mostrarMensaje("Error al actualizar producto.");
            e.printStackTrace();
        }
    }

    @FXML
    private void eliminarProducto() {
        if (productoSeleccionado == null) {
            mostrarMensaje("Selecciona un producto de la tabla.");
            return;
        }

        boolean ok = productoModel.eliminarProducto(productoSeleccionado.getIdProducto());

        if (ok) {
            mostrarMensaje("Producto eliminado correctamente.");
            cargarProductos();
            limpiarFormulario();
        }
    }

    @FXML
    private void limpiarFormulario() {
        modoActual = ModoOperacion.NUEVO;
        productoSeleccionado = null;

        habilitarFormulario();

        txtCodigo.clear();
        txtModelo.clear();
        txtNombre.clear();
        txtDescripcion.clear();
        txtCompra.clear();
        txtMenudeo.clear();
        txtMayoreo.clear();
        txtStock.clear();
        txtMinimo.clear();

        cbProveedor.getSelectionModel().clearSelection();
        tablaInventario.getSelectionModel().clearSelection();
    }

    @FXML
    private void modoActualizarStock() {
        if (productoSeleccionado == null) {
            mostrarMensaje("Selecciona un producto de la tabla.");
            return;
        }

        modoActual = ModoOperacion.STOCK;

        deshabilitarTodo();
        txtStock.setDisable(false);
        txtMinimo.setDisable(false);

        mostrarMensaje("Modo actualizar stock: modifica Stock o Stock mínimo y presiona Actualizar.");
    }

    @FXML
    private void modoActualizarPrecios() {
        if (productoSeleccionado == null) {
            mostrarMensaje("Selecciona un producto de la tabla.");
            return;
        }

        modoActual = ModoOperacion.PRECIOS;

        deshabilitarTodo();
        txtCompra.setDisable(false);
        txtMenudeo.setDisable(false);
        txtMayoreo.setDisable(false);

        mostrarMensaje("Modo actualizar precios: modifica los precios y presiona Actualizar.");
    }

    @FXML
    private void modoProductos() {

        modoActual = ModoOperacion.GENERAL;

        habilitarFormulario();

        mostrarMensaje(
                "Modo productos:\n" +
                        "Puedes registrar, editar o eliminar productos."
        );
    }

    @FXML
    private void modoControlLotes() {
        mostrarMensaje("Control de lotes se implementará con la tabla lotes.");
    }

    @FXML
    private void modoAjusteInventario() {
        if (productoSeleccionado == null) {
            mostrarMensaje("Selecciona un producto de la tabla.");
            return;
        }

        modoActual = ModoOperacion.AJUSTE;

        deshabilitarTodo();
        txtStock.setDisable(false);

        mostrarMensaje("Modo ajuste de inventario: escribe el stock real contado físicamente y presiona Actualizar.");
    }

    private void deshabilitarTodo() {
        txtCodigo.setDisable(true);
        txtModelo.setDisable(true);
        txtNombre.setDisable(true);
        txtDescripcion.setDisable(true);
        txtCompra.setDisable(true);
        txtMenudeo.setDisable(true);
        txtMayoreo.setDisable(true);
        txtStock.setDisable(true);
        txtMinimo.setDisable(true);
        cbProveedor.setDisable(true);
    }

    private void habilitarFormulario() {
        txtCodigo.setDisable(false);
        txtModelo.setDisable(false);
        txtNombre.setDisable(false);
        txtDescripcion.setDisable(false);
        txtCompra.setDisable(false);
        txtMenudeo.setDisable(false);
        txtMayoreo.setDisable(false);
        txtStock.setDisable(false);
        txtMinimo.setDisable(false);
        cbProveedor.setDisable(false);
    }

    private boolean validarCampos() {
        if (txtCodigo.getText().trim().isEmpty()
                || txtModelo.getText().trim().isEmpty()
                || txtNombre.getText().trim().isEmpty()
                || txtCompra.getText().trim().isEmpty()
                || txtMenudeo.getText().trim().isEmpty()
                || txtMayoreo.getText().trim().isEmpty()
                || txtStock.getText().trim().isEmpty()
                || txtMinimo.getText().trim().isEmpty()
                || cbProveedor.getValue() == null) {

            mostrarMensaje("Completa los campos obligatorios.");
            return false;
        }

        try {
            Double.parseDouble(txtCompra.getText().trim());
            Double.parseDouble(txtMenudeo.getText().trim());
            Double.parseDouble(txtMayoreo.getText().trim());
            Integer.parseInt(txtStock.getText().trim());
            Integer.parseInt(txtMinimo.getText().trim());
        } catch (NumberFormatException e) {
            mostrarMensaje("Precios y stock deben ser valores numéricos.");
            return false;
        }

        return true;
    }

    private void mostrarMensaje(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Inventario");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    @FXML
    private void abrirDashboard() {
        cambiarVentana("/org/example/views/dashboard.fxml", "dashboard.css", "SIG-CB - Dashboard");
    }

    @FXML
    private void abrirVentas() {
        cambiarVentana("/org/example/views/ventas.fxml", "ventas.css", "SIG-CB - Ventas");
    }

    @FXML
    private void abrirInventario() {
        cargarProductos();
    }

    @FXML
    private void abrirProveedores() {
        cambiarVentana("/org/example/views/proveedores.fxml", "proveedores.css", "SIG-CB - Proveedores");
    }

    @FXML
    private void abrirReportes() {
        cambiarVentana("/org/example/views/reportes.fxml", "reportes.css", "SIG-CB - Reportes");
    }

    @FXML
    private void abrirSeguridad() {
        cambiarVentana("/org/example/views/seguridad.fxml", "seguridad.css", "SIG-CB - Seguridad");
    }

    @FXML
    private void cerrarSesion() {
        cambiarVentana("/org/example/views/login.fxml", "styles.css", "SIG-CB - Login");
    }

    private void cambiarVentana(String fxml, String cssModulo, String titulo) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    Objects.requireNonNull(getClass().getResource(fxml))
            );

            Scene scene;

            if (fxml.contains("login.fxml")) {
                scene = new Scene(loader.load(), 710, 610);
                scene.getStylesheets().add(
                        Objects.requireNonNull(
                                getClass().getResource("/org/example/css/" + cssModulo)
                        ).toExternalForm()
                );
            } else {
                scene = new Scene(loader.load(), 1100, 780);

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
            }

            Stage stage = (Stage) tablaInventario.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle(titulo);
            stage.centerOnScreen();

        } catch (Exception e) {
            mostrarMensaje("No se pudo abrir la ventana: " + titulo);
            e.printStackTrace();
        }
    }
}