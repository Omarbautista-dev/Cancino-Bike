package org.example.models;

public class ProveedorItem {

    private int idProveedor;
    private String nombreEmpresa;

    public ProveedorItem(int idProveedor, String nombreEmpresa) {
        this.idProveedor = idProveedor;
        this.nombreEmpresa = nombreEmpresa;
    }

    public int getIdProveedor() {
        return idProveedor;
    }

    @Override
    public String toString() {
        return nombreEmpresa;
    }
}