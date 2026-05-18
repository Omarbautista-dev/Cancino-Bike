package org.example.models;

public class OrdenCompraItem {

    private int idProducto;
    private String producto;
    private int cantidad;
    private double precioEstimado;
    private double subtotal;

    public OrdenCompraItem(int idProducto, String producto, int cantidad, double precioEstimado) {
        this.idProducto = idProducto;
        this.producto = producto;
        this.cantidad = cantidad;
        this.precioEstimado = precioEstimado;
        this.subtotal = cantidad * precioEstimado;
    }

    public int getIdProducto() { return idProducto; }
    public String getProducto() { return producto; }
    public int getCantidad() { return cantidad; }
    public double getPrecioEstimado() { return precioEstimado; }
    public double getSubtotal() { return subtotal; }
}