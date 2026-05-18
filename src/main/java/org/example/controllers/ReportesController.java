package org.example.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import org.example.models.ReporteModel;

import java.time.LocalDate;
import java.util.Objects;

public class ReportesController {

    @FXML private ImageView logoImage;

    @FXML private DatePicker dpFecha;

    @FXML private Label lblTotalVentas;
    @FXML private Label lblTotalContado;
    @FXML private Label lblTotalCredito;
    @FXML private Label lblUtilidad;
    @FXML private Label lblNumeroVentas;
    @FXML private Label lblValorInventario;

    @FXML private TextArea txtResumen;

    private final ReporteModel reporteModel = new ReporteModel();

    private double totalVentasActual;
    private double totalContadoActual;
    private double totalCreditoActual;
    private double utilidadActual;

    @FXML
    public void initialize() {
        cargarLogo();
        dpFecha.setValue(LocalDate.now());
        generarReporte();
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
    private void generarReporte() {
        LocalDate fecha = dpFecha.getValue();

        if (fecha == null) {
            mostrarMensaje("Selecciona una fecha.");
            return;
        }

        totalVentasActual = reporteModel.totalVentas(fecha);
        totalContadoActual = reporteModel.totalContado(fecha);
        totalCreditoActual = reporteModel.totalCredito(fecha);
        utilidadActual = reporteModel.utilidad(fecha);

        int numeroVentas = reporteModel.numeroVentas(fecha);
        double valorInventario = reporteModel.valorInventario();
        int stockBajo = reporteModel.productosStockBajo();

        lblTotalVentas.setText(String.format("$%.2f", totalVentasActual));
        lblTotalContado.setText(String.format("$%.2f", totalContadoActual));
        lblTotalCredito.setText(String.format("$%.2f", totalCreditoActual));
        lblUtilidad.setText(String.format("$%.2f", utilidadActual));
        lblNumeroVentas.setText(String.valueOf(numeroVentas));
        lblValorInventario.setText(String.format("$%.2f", valorInventario));

        txtResumen.setText(
                "REPORTE DEL DÍA: " + fecha + "\n\n" +
                        "Total de ventas: $" + String.format("%.2f", totalVentasActual) + "\n" +
                        "Ventas de contado: $" + String.format("%.2f", totalContadoActual) + "\n" +
                        "Ventas a crédito: $" + String.format("%.2f", totalCreditoActual) + "\n" +
                        "Número de ventas: " + numeroVentas + "\n" +
                        "Utilidad estimada: $" + String.format("%.2f", utilidadActual) + "\n\n" +
                        "Valor actual del inventario: $" + String.format("%.2f", valorInventario) + "\n" +
                        "Productos con stock bajo: " + stockBajo
        );
    }

    @FXML
    private void mostrarReporteVentas() {
        generarReporte();
        txtResumen.appendText("\n\nVista: Reporte de ventas.");
    }

    @FXML
    private void mostrarReporteInventario() {
        double valorInventario = reporteModel.valorInventario();
        int stockBajo = reporteModel.productosStockBajo();

        lblValorInventario.setText(String.format("$%.2f", valorInventario));

        txtResumen.setText(
                "REPORTE DE INVENTARIO\n\n" +
                        "Valor actual del inventario: $" + String.format("%.2f", valorInventario) + "\n" +
                        "Productos con stock bajo: " + stockBajo
        );
    }

    @FXML
    private void mostrarReporteUtilidad() {
        LocalDate fecha = dpFecha.getValue();

        if (fecha == null) {
            mostrarMensaje("Selecciona una fecha.");
            return;
        }

        utilidadActual = reporteModel.utilidad(fecha);

        lblUtilidad.setText(String.format("$%.2f", utilidadActual));

        txtResumen.setText(
                "REPORTE DE UTILIDAD\n\n" +
                        "Fecha: " + fecha + "\n" +
                        "Utilidad estimada: $" + String.format("%.2f", utilidadActual)
        );
    }

    @FXML
    private void guardarCorteCaja() {
        LocalDate fecha = dpFecha.getValue();

        if (fecha == null) {
            mostrarMensaje("Selecciona una fecha.");
            return;
        }

        generarReporte();

        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Guardar corte");
        confirmacion.setHeaderText(null);
        confirmacion.setContentText("¿Deseas guardar el corte de caja del día?");

        ButtonType respuesta = confirmacion.showAndWait().orElse(ButtonType.CANCEL);

        if (respuesta != ButtonType.OK) {
            return;
        }

        int idUsuario = 1;

        boolean ok = reporteModel.guardarCorteCaja(
                idUsuario,
                totalVentasActual,
                totalContadoActual,
                totalCreditoActual,
                utilidadActual
        );

        if (ok) {
            mostrarMensaje("Corte de caja guardado correctamente.");
        } else {
            mostrarMensaje("No se pudo guardar el corte de caja.");
        }
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

            Stage stage = (Stage) dpFecha.getScene().getWindow();

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
        alert.setTitle("Reportes");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}