package org.example.models;

public class HistorialCompraProveedor {

    private int idCompra;
    private String fechaCompra;
    private String producto;
    private int cantidad;
    private double precioCompra;
    private double subtotal;
    private double totalCompra;

    public HistorialCompraProveedor(int idCompra, String fechaCompra, String producto,
                                    int cantidad, double precioCompra,
                                    double subtotal, double totalCompra) {
        this.idCompra = idCompra;
        this.fechaCompra = fechaCompra;
        this.producto = producto;
        this.cantidad = cantidad;
        this.precioCompra = precioCompra;
        this.subtotal = subtotal;
        this.totalCompra = totalCompra;
    }

    public int getIdCompra() { return idCompra; }
    public String getFechaCompra() { return fechaCompra; }
    public String getProducto() { return producto; }
    public int getCantidad() { return cantidad; }
    public double getPrecioCompra() { return precioCompra; }
    public double getSubtotal() { return subtotal; }
    public double getTotalCompra() { return totalCompra; }
}