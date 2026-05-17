package org.example.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.scene.input.MouseButton;
import org.example.models.DetalleVentaItem;
import org.example.models.Producto;
import org.example.models.VentaModel;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class VentasController {

    @FXML private ImageView logoImage;

    @FXML private TextField txtBuscarProducto;
    @FXML private TextField txtCantidad;
    @FXML private TextField txtDescuento;

    @FXML private ComboBox<String> cbTipoPrecio;
    @FXML private ComboBox<String> cbTipoVenta;

    @FXML private TableView<Producto> tablaProductos;
    @FXML private TableColumn<Producto, String> colCodigo;
    @FXML private TableColumn<Producto, String> colModelo;
    @FXML private TableColumn<Producto, String> colProducto;
    @FXML private TableColumn<Producto, Integer> colStock;
    @FXML private TableColumn<Producto, Double> colMenudeo;
    @FXML private TableColumn<Producto, Double> colMayoreo;

    @FXML private TableView<DetalleVentaItem> tablaCarrito;
    @FXML private TableColumn<DetalleVentaItem, String> colCarCodigo;
    @FXML private TableColumn<DetalleVentaItem, String> colCarProducto;
    @FXML private TableColumn<DetalleVentaItem, Integer> colCarCantidad;
    @FXML private TableColumn<DetalleVentaItem, Double> colCarPrecio;
    @FXML private TableColumn<DetalleVentaItem, Double> colCarSubtotal;

    @FXML private Label lblSubtotal;
    @FXML private Label lblDescuento;
    @FXML private Label lblTotal;

    private final VentaModel ventaModel = new VentaModel();

    private final ObservableList<DetalleVentaItem> carrito =
            FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        cargarLogo();
        configurarCombos();
        configurarTablaProductos();
        configurarTablaCarrito();
        configurarBusqueda();
        configurarDescuento();
        configurarDobleClickCarrito();
        editarCantidadCarrito();
        buscarProductos("");
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

    private void configurarDobleClickCarrito() {
        tablaCarrito.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY
                    && event.getClickCount() == 2) {

                DetalleVentaItem item = tablaCarrito
                        .getSelectionModel()
                        .getSelectedItem();

                if (item != null) {
                    carrito.remove(item);
                    calcularTotales();
                }
            }
        });
    }

    private void editarCantidadCarrito() {
        tablaCarrito.setEditable(true);

        colCarCantidad.setCellFactory(column -> new TableCell<DetalleVentaItem, Integer>() {

            private final TextField textField = new TextField();

            {
                textField.setOnAction(event -> guardarCambio());
                textField.focusedProperty().addListener((obs, oldValue, newValue) -> {
                    if (!newValue) {
                        guardarCambio();
                    }
                });
            }

            private void guardarCambio() {
                DetalleVentaItem item = getTableView().getItems().get(getIndex());

                try {
                    int nuevaCantidad = Integer.parseInt(textField.getText().trim());

                    if (nuevaCantidad <= 0) {
                        mostrarMensaje("La cantidad debe ser mayor a cero.");
                        updateItem(item.getCantidad(), false);
                        return;
                    }

                    Producto productoOriginal = tablaProductos.getItems()
                            .stream()
                            .filter(p -> p.getIdProducto() == item.getIdProducto())
                            .findFirst()
                            .orElse(null);

                    if (productoOriginal == null) {
                        mostrarMensaje("No se encontró el producto.");
                        return;
                    }

                    if (nuevaCantidad > productoOriginal.getStock()) {
                        mostrarMensaje(
                                "La cantidad supera el stock disponible.\n" +
                                        "Stock actual: " + productoOriginal.getStock()
                        );

                        textField.setText(String.valueOf(item.getCantidad()));
                        return;
                    }

                    item.setCantidad(nuevaCantidad);
                    tablaCarrito.refresh();
                    calcularTotales();

                } catch (NumberFormatException e) {
                    mostrarMensaje("La cantidad debe ser numérica.");
                    updateItem(item.getCantidad(), false);
                }
            }

            @Override
            protected void updateItem(Integer cantidad, boolean empty) {
                super.updateItem(cantidad, empty);

                if (empty || cantidad == null) {
                    setGraphic(null);
                } else {
                    textField.setText(String.valueOf(cantidad));
                    setGraphic(textField);
                }
            }
        });
    }

    private void configurarCombos() {
        cbTipoPrecio.setItems(FXCollections.observableArrayList("MENUDEO", "MAYOREO"));
        cbTipoPrecio.setValue("MENUDEO");

        cbTipoVenta.setItems(FXCollections.observableArrayList("CONTADO", "CREDITO"));
        cbTipoVenta.setValue("CONTADO");
    }

    private void configurarTablaProductos() {
        colCodigo.setCellValueFactory(new PropertyValueFactory<>("codigoBarras"));
        colModelo.setCellValueFactory(new PropertyValueFactory<>("modelo"));
        colProducto.setCellValueFactory(new PropertyValueFactory<>("nombreProducto"));
        colStock.setCellValueFactory(new PropertyValueFactory<>("stock"));
        colMenudeo.setCellValueFactory(new PropertyValueFactory<>("precioMenudeo"));
        colMayoreo.setCellValueFactory(new PropertyValueFactory<>("precioMayoreo"));
    }

    private void configurarTablaCarrito() {
        colCarCodigo.setCellValueFactory(new PropertyValueFactory<>("codigo"));
        colCarProducto.setCellValueFactory(new PropertyValueFactory<>("producto"));
        colCarCantidad.setCellValueFactory(new PropertyValueFactory<>("cantidad"));
        colCarPrecio.setCellValueFactory(new PropertyValueFactory<>("precioUnitario"));
        colCarSubtotal.setCellValueFactory(new PropertyValueFactory<>("subtotal"));

        tablaCarrito.setItems(carrito);
    }

    private void configurarBusqueda() {
        txtBuscarProducto.textProperty().addListener((obs, oldValue, newValue) -> {
            buscarProductos(newValue);
        });
    }

    private void configurarDescuento() {
        txtDescuento.textProperty().addListener((obs, oldValue, newValue) -> {
            calcularTotales();
        });
    }

    private void buscarProductos(String filtro) {
        tablaProductos.setItems(
                FXCollections.observableArrayList(
                        ventaModel.buscarProductosVenta(filtro)
                )
        );
    }

    @FXML
    private void agregarAlCarrito() {
        Producto producto = tablaProductos.getSelectionModel().getSelectedItem();

        if (producto == null) {
            mostrarMensaje("Selecciona un producto.");
            return;
        }

        int cantidad;

        try {
            cantidad = Integer.parseInt(txtCantidad.getText().trim());
        } catch (NumberFormatException e) {
            mostrarMensaje("La cantidad debe ser numérica.");
            return;
        }

        if (cantidad <= 0) {
            mostrarMensaje("La cantidad debe ser mayor a cero.");
            return;
        }

        if (cantidad > producto.getStock()) {
            mostrarMensaje("No hay stock suficiente.");
            return;
        }

        int cantidadActualEnCarrito = carrito.stream()
                .filter(item -> item.getIdProducto() == producto.getIdProducto())
                .mapToInt(DetalleVentaItem::getCantidad)
                .sum();

        if (cantidadActualEnCarrito + cantidad > producto.getStock()) {
            mostrarMensaje("La cantidad total en carrito supera el stock disponible.");
            return;
        }

        double precio = cbTipoPrecio.getValue().equals("MAYOREO")
                ? producto.getPrecioMayoreo()
                : producto.getPrecioMenudeo();

        for (DetalleVentaItem item : carrito) {
            if (item.getIdProducto() == producto.getIdProducto()
                    && item.getPrecioUnitario() == precio) {

                item.aumentarCantidad(cantidad);
                tablaCarrito.refresh();
                calcularTotales();
                return;
            }
        }

        carrito.add(new DetalleVentaItem(
                producto.getIdProducto(),
                producto.getCodigoBarras(),
                producto.getNombreProducto(),
                cantidad,
                precio
        ));

        calcularTotales();
    }

    @FXML
    private void quitarProductoCarrito() {
        DetalleVentaItem item = tablaCarrito.getSelectionModel().getSelectedItem();

        if (item == null) {
            mostrarMensaje("Selecciona un producto del carrito.");
            return;
        }

        carrito.remove(item);
        calcularTotales();
    }

    @FXML
    private void cancelarVenta() {
        carrito.clear();
        txtCantidad.setText("1");
        txtDescuento.setText("0");
        calcularTotales();
    }

    @FXML
    private void registrarVenta() {
        if (carrito.isEmpty()) {
            mostrarMensaje("Agrega productos al carrito.");
            return;
        }

        double subtotal = obtenerSubtotal();
        double descuento = obtenerDescuento();

        if (descuento < 0) {
            mostrarMensaje("El descuento no puede ser negativo.");
            return;
        }

        if (descuento > subtotal) {
            mostrarMensaje("El descuento no puede ser mayor al subtotal.");
            return;
        }

        double total = subtotal - descuento;

        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar venta");
        confirmacion.setHeaderText(null);
        confirmacion.setContentText(
                "¿Deseas registrar la venta por $" + String.format("%.2f", total) + "?"
        );

        ButtonType respuesta = confirmacion.showAndWait().orElse(ButtonType.CANCEL);

        if (respuesta != ButtonType.OK) {
            return;
        }

        String folio = generarFolio();

        int idUsuario = 1;

        boolean ok = ventaModel.registrarVenta(
                folio,
                idUsuario,
                cbTipoVenta.getValue(),
                cbTipoPrecio.getValue(),
                subtotal,
                descuento,
                total,
                carrito
        );

        if (ok) {
            mostrarMensaje("Venta registrada correctamente.\nFolio: " + folio);
            cancelarVenta();
            buscarProductos(txtBuscarProducto.getText().trim());
        } else {
            mostrarMensaje("No se pudo registrar la venta. Revisa stock o conexión.");
        }
    }

    private String generarFolio() {
        return "V" + LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
    }

    private double obtenerSubtotal() {
        return carrito.stream()
                .mapToDouble(DetalleVentaItem::getSubtotal)
                .sum();
    }

    private double obtenerDescuento() {
        try {
            return Double.parseDouble(txtDescuento.getText().trim());
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private void calcularTotales() {
        double subtotal = obtenerSubtotal();
        double descuento = obtenerDescuento();

        if (descuento < 0) descuento = 0;
        if (descuento > subtotal) descuento = subtotal;

        double total = subtotal - descuento;

        lblSubtotal.setText(String.format("Subtotal: $%.2f", subtotal));
        lblDescuento.setText(String.format("Descuento: $%.2f", descuento));
        lblTotal.setText(String.format("Total: $%.2f", total));
    }

    @FXML
    private void enfocarBusqueda() {
        txtBuscarProducto.requestFocus();
    }

    @FXML
    private void mostrarInfoTicket() {
        mostrarMensaje("El ticket digital se genera automáticamente al registrar la venta.");
    }

    @FXML
    private void abrirDashboard() {
        cambiarVentana("/org/example/views/dashboard.fxml", "dashboard.css", "SIG-CB - Dashboard");
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

            Stage stage = (Stage) txtBuscarProducto.getScene().getWindow();
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
        alert.setTitle("Ventas");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}