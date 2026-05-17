package org.example.models;

public class DetalleVentaItem {

    private int idProducto;
    private String codigo;
    private String producto;
    private int cantidad;
    private double precioUnitario;
    private double subtotal;

    public DetalleVentaItem(int idProducto, String codigo, String producto,
                            int cantidad, double precioUnitario) {
        this.idProducto = idProducto;
        this.codigo = codigo;
        this.producto = producto;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
        this.subtotal = cantidad * precioUnitario;
    }

    public int getIdProducto() { return idProducto; }
    public String getCodigo() { return codigo; }
    public String getProducto() { return producto; }
    public int getCantidad() { return cantidad; }
    public double getPrecioUnitario() { return precioUnitario; }
    public double getSubtotal() { return subtotal; }

    public void aumentarCantidad(int cantidadExtra) {
        this.cantidad += cantidadExtra;
        this.subtotal = this.cantidad * this.precioUnitario;
    }
}