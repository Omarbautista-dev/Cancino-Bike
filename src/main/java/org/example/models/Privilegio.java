package org.example.models;

public class Privilegio {

    private int idPrivilegio;
    private String modulo;
    private String accion;
    private boolean asignado;

    public Privilegio(int idPrivilegio, String modulo, String accion, boolean asignado) {
        this.idPrivilegio = idPrivilegio;
        this.modulo = modulo;
        this.accion = accion;
        this.asignado = asignado;
    }

    public int getIdPrivilegio() { return idPrivilegio; }
    public String getModulo() { return modulo; }
    public String getAccion() { return accion; }
    public boolean isAsignado() { return asignado; }

    public void setAsignado(boolean asignado) {
        this.asignado = asignado;
    }
}