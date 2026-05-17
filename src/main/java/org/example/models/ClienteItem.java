package org.example.models;

public class ClienteItem {

    private int idCliente;
    private String nombreCliente;

    public ClienteItem(int idCliente, String nombreCliente) {
        this.idCliente = idCliente;
        this.nombreCliente = nombreCliente;
    }

    public int getIdCliente() {
        return idCliente;
    }

    public String getNombreCliente() {
        return nombreCliente;
    }

    @Override
    public String toString() {
        return nombreCliente;
    }
}