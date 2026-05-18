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
import org.example.models.HistorialCompraProveedor;
import org.example.models.Proveedor;
import org.example.models.ProveedorModel;
import javafx.collections.ObservableList;
import org.example.models.Producto;
import org.example.models.ProductoModel;
import org.example.models.OrdenCompraItem;
import java.util.Objects;
import javafx.scene.layout.VBox;
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

    @FXML private TableView<HistorialCompraProveedor> tablaHistorialCompras;

    @FXML private TableColumn<HistorialCompraProveedor, Integer> colHIdCompra;
    @FXML private TableColumn<HistorialCompraProveedor, String> colHFecha;
    @FXML private TableColumn<HistorialCompraProveedor, String> colHProducto;
    @FXML private TableColumn<HistorialCompraProveedor, Integer> colHCantidad;
    @FXML private TableColumn<HistorialCompraProveedor, Double> colHPrecio;
    @FXML private TableColumn<HistorialCompraProveedor, Double> colHSubtotal;
    @FXML private TableColumn<HistorialCompraProveedor, Double> colHTotal;

    @FXML private VBox panelOrdenCompra;

    @FXML private ComboBox<Producto> cbProductoOrden;
    @FXML private TextField txtCantidadOrden;
    @FXML private TextField txtPrecioOrden;
    @FXML private TableView<OrdenCompraItem> tablaOrdenCompra;

    @FXML private TableColumn<OrdenCompraItem, String> colOrdenProducto;
    @FXML private TableColumn<OrdenCompraItem, Integer> colOrdenCantidad;
    @FXML private TableColumn<OrdenCompraItem, Double> colOrdenPrecio;
    @FXML private TableColumn<OrdenCompraItem, Double> colOrdenSubtotal;

    @FXML private Label lblTotalOrden;

    private final ProductoModel productoModel = new ProductoModel();

    private final ObservableList<OrdenCompraItem> itemsOrden =
            FXCollections.observableArrayList();

    private final ProveedorModel proveedorModel =
            new ProveedorModel();

    private Proveedor proveedorSeleccionado;

    @FXML
    public void initialize() {
        cargarLogo();
        configurarTabla();
        configurarTablaHistorial();
        cargarProveedores();
        configurarBusqueda();
        detectarSeleccionTabla();
        configurarTablaOrdenCompra();
        cargarProductosOrden();
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

    private void configurarTablaHistorial() {
        colHIdCompra.setCellValueFactory(new PropertyValueFactory<>("idCompra"));
        colHFecha.setCellValueFactory(new PropertyValueFactory<>("fechaCompra"));
        colHProducto.setCellValueFactory(new PropertyValueFactory<>("producto"));
        colHCantidad.setCellValueFactory(new PropertyValueFactory<>("cantidad"));
        colHPrecio.setCellValueFactory(new PropertyValueFactory<>("precioCompra"));
        colHSubtotal.setCellValueFactory(new PropertyValueFactory<>("subtotal"));
        colHTotal.setCellValueFactory(new PropertyValueFactory<>("totalCompra"));
    }

    @FXML
    private void guardarProveedor() {

        if (!validarCampos()) return;

        if (proveedorModel.existeEmpresa(txtEmpresa.getText().trim())) {
            mostrarMensaje("Ya existe un proveedor con ese nombre de empresa.");
            return;
        }
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
                "¿Seguro que deseas eliminar este proveedor?\n\n" +
                        proveedorSeleccionado.getNombreEmpresa()
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

    private void configurarTablaOrdenCompra() {
        colOrdenProducto.setCellValueFactory(
                new PropertyValueFactory<>("producto")
        );

        colOrdenCantidad.setCellValueFactory(
                new PropertyValueFactory<>("cantidad")
        );

        colOrdenPrecio.setCellValueFactory(
                new PropertyValueFactory<>("precioEstimado")
        );

        colOrdenSubtotal.setCellValueFactory(
                new PropertyValueFactory<>("subtotal")
        );

        tablaOrdenCompra.setItems(itemsOrden);
    }

    private void cargarProductosOrden() {
        cbProductoOrden.setItems(
                FXCollections.observableArrayList(
                        productoModel.listarProductos()
                )
        );
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

        String empresa = txtEmpresa.getText().trim();
        String telefono = txtTelefono.getText().trim();
        String email = txtEmail.getText().trim();
        String rfc = txtRfc.getText().trim();

        if (empresa.isEmpty()) {
            mostrarMensaje("Ingresa el nombre de la empresa.");
            return false;
        }

        if (!telefono.isEmpty() && !telefono.matches("\\d{10}")) {
            mostrarMensaje("El teléfono debe tener 10 dígitos numéricos.");
            return false;
        }

        if (!email.isEmpty() && !email.matches("^[\\w.-]+@[\\w.-]+\\.[A-Za-z]{2,}$")) {
            mostrarMensaje("Ingresa un email válido.");
            return false;
        }

        if (!rfc.isEmpty() && (rfc.length() < 12 || rfc.length() > 13)) {
            mostrarMensaje("El RFC debe tener 12 o 13 caracteres.");
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

    @FXML
    private void mostrarHistorialCompras() {

        if (proveedorSeleccionado == null) {
            mostrarMensaje("Selecciona un proveedor para ver su historial de compras.");
            return;
        }

        panelOrdenCompra.setVisible(false);
        panelOrdenCompra.setManaged(false);

        tablaProveedores.setVisible(false);
        tablaProveedores.setManaged(false);

        tablaHistorialCompras.setVisible(true);
        tablaHistorialCompras.setManaged(true);

        tablaHistorialCompras.setItems(
                FXCollections.observableArrayList(
                        proveedorModel.historialComprasPorProveedor(
                                proveedorSeleccionado.getIdProveedor()
                        )
                )
        );
    }

    @FXML
    private void mostrarDatosProveedores() {

        panelOrdenCompra.setVisible(false);
        panelOrdenCompra.setManaged(false);

        tablaHistorialCompras.setVisible(false);
        tablaHistorialCompras.setManaged(false);

        tablaProveedores.setVisible(true);
        tablaProveedores.setManaged(true);

        cargarProveedores();
    }

    @FXML
    private void mostrarOrdenCompra() {

        if (proveedorSeleccionado == null) {
            mostrarMensaje("Selecciona un proveedor para crear la orden de compra.");
            return;
        }

        tablaProveedores.setVisible(false);
        tablaProveedores.setManaged(false);

        tablaHistorialCompras.setVisible(false);
        tablaHistorialCompras.setManaged(false);

        panelOrdenCompra.setVisible(true);
        panelOrdenCompra.setManaged(true);

        mostrarMensaje("Orden de compra para: " + proveedorSeleccionado.getNombreEmpresa());
    }

    @FXML
    private void agregarProductoOrden() {

        Producto producto = cbProductoOrden.getValue();

        if (producto == null) {
            mostrarMensaje("Selecciona un producto.");
            return;
        }

        int cantidad;
        double precio;

        try {
            cantidad = Integer.parseInt(txtCantidadOrden.getText().trim());
            precio = Double.parseDouble(txtPrecioOrden.getText().trim());
        } catch (NumberFormatException e) {
            mostrarMensaje("Cantidad y precio deben ser numéricos.");
            return;
        }

        if (cantidad <= 0) {
            mostrarMensaje("La cantidad debe ser mayor a cero.");
            return;
        }

        if (precio <= 0) {
            mostrarMensaje("El precio debe ser mayor a cero.");
            return;
        }

        itemsOrden.add(new OrdenCompraItem(
                producto.getIdProducto(),
                producto.getNombreProducto(),
                cantidad,
                precio
        ));

        txtCantidadOrden.clear();
        txtPrecioOrden.clear();
        cbProductoOrden.getSelectionModel().clearSelection();

        actualizarTotalOrden();
    }

    @FXML
    private void quitarProductoOrden() {
        OrdenCompraItem item =
                tablaOrdenCompra.getSelectionModel().getSelectedItem();

        if (item == null) {
            mostrarMensaje("Selecciona un producto de la orden.");
            return;
        }

        itemsOrden.remove(item);
        actualizarTotalOrden();
    }

    @FXML
    private void limpiarOrdenCompra() {
        itemsOrden.clear();
        txtCantidadOrden.clear();
        txtPrecioOrden.clear();
        cbProductoOrden.getSelectionModel().clearSelection();
        actualizarTotalOrden();
    }

    @FXML
    private void guardarOrdenCompra() {

        if (proveedorSeleccionado == null) {
            mostrarMensaje("Selecciona un proveedor.");
            return;
        }

        if (itemsOrden.isEmpty()) {
            mostrarMensaje("Agrega productos a la orden.");
            return;
        }

        double total = obtenerTotalOrden();

        boolean ok = proveedorModel.guardarOrdenCompra(
                proveedorSeleccionado.getIdProveedor(),
                1,
                itemsOrden,
                total
        );

        if (ok) {
            mostrarMensaje("Orden de compra guardada correctamente.");
            limpiarOrdenCompra();
            cargarProductosOrden();
        } else {
            mostrarMensaje("No se pudo guardar la orden.");
        }
    }

    private double obtenerTotalOrden() {
        return itemsOrden.stream()
                .mapToDouble(OrdenCompraItem::getSubtotal)
                .sum();
    }

    private void actualizarTotalOrden() {
        lblTotalOrden.setText(
                String.format("Total estimado: $%.2f", obtenerTotalOrden())
        );
    }
}