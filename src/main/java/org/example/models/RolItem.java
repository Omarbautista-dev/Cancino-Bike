package org.example.models;

public class RolItem {

    private int idRol;
    private String nombreRol;

    public RolItem(int idRol, String nombreRol) {
        this.idRol = idRol;
        this.nombreRol = nombreRol;
    }

    public int getIdRol() {
        return idRol;
    }

    public String getNombreRol() {
        return nombreRol;
    }

    @Override
    public String toString() {
        return nombreRol;
    }
}