package org.example.controllers;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Label;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import org.example.models.Producto;
import org.example.models.ProductoModel;

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

    private final ProductoModel productoModel = new ProductoModel();

    @FXML
    public void initialize() {
        configurarTabla();
        cargarProductos();
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

    @FXML
    private void abrirDashboard() {
        cambiarVentana("/org/example/views/dashboard.fxml", "dashboard.css", "SIG-CB - Dashboard");
    }

    @FXML
    private void abrirVentas() {}

    @FXML
    private void abrirInventario() {
        cargarProductos();
    }

    @FXML
    private void abrirProveedores() {}

    @FXML
    private void abrirReportes() {}

    @FXML
    private void abrirSeguridad() {}

    @FXML
    private void cerrarSesion() {
        cambiarVentana("/org/example/views/login.fxml", "login.css", "SIG-CB - Login");
    }

    private void cambiarVentana(String fxml, String cssModulo, String titulo) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
            Scene scene = new Scene(loader.load(), 1100, 780);

            scene.getStylesheets().add(
                    getClass().getResource("/org/example/css/global.css").toExternalForm()
            );

            scene.getStylesheets().add(
                    getClass().getResource("/org/example/css/" + cssModulo).toExternalForm()
            );

            Stage stage = (Stage) tablaInventario.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle(titulo);
            stage.centerOnScreen();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}