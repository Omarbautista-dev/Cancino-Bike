package org.example.models;

public class Producto {

    private int idProducto;
    private String codigoBarras;
    private String modelo;
    private String nombreProducto;
    private String descripcion;
    private double precioCompra;
    private double precioMenudeo;
    private double precioMayoreo;
    private int stock;
    private int stockMinimo;
    private int idProveedor;
    private String proveedor;

    public Producto(int idProducto, String codigoBarras, String modelo, String nombreProducto,
                    String descripcion, double precioCompra, double precioMenudeo,
                    double precioMayoreo, int stock, int stockMinimo,
                    int idProveedor, String proveedor) {

        this.idProducto = idProducto;
        this.codigoBarras = codigoBarras;
        this.modelo = modelo;
        this.nombreProducto = nombreProducto;
        this.descripcion = descripcion;
        this.precioCompra = precioCompra;
        this.precioMenudeo = precioMenudeo;
        this.precioMayoreo = precioMayoreo;
        this.stock = stock;
        this.stockMinimo = stockMinimo;
        this.idProveedor = idProveedor;
        this.proveedor = proveedor;
    }
    @Override
    public String toString() {
        return nombreProducto;
    }
    public int getIdProducto() { return idProducto; }
    public String getCodigoBarras() { return codigoBarras; }
    public String getModelo() { return modelo; }
    public String getNombreProducto() { return nombreProducto; }
    public String getDescripcion() { return descripcion; }
    public double getPrecioCompra() { return precioCompra; }
    public double getPrecioMenudeo() { return precioMenudeo; }
    public double getPrecioMayoreo() { return precioMayoreo; }
    public int getStock() { return stock; }
    public int getStockMinimo() { return stockMinimo; }
    public int getIdProveedor() { return idProveedor; }
    public String getProveedor() { return proveedor; }
}