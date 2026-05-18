package org.example.models;

public class Acceso {

    private int idAcceso;
    private String usuario;
    private String nombreCompleto;
    private String fechaAcceso;
    private String estadoAcceso;

    public Acceso(int idAcceso, String usuario, String nombreCompleto,
                  String fechaAcceso, String estadoAcceso) {
        this.idAcceso = idAcceso;
        this.usuario = usuario;
        this.nombreCompleto = nombreCompleto;
        this.fechaAcceso = fechaAcceso;
        this.estadoAcceso = estadoAcceso;
    }

    public int getIdAcceso() { return idAcceso; }
    public String getUsuario() { return usuario; }
    public String getNombreCompleto() { return nombreCompleto; }
    public String getFechaAcceso() { return fechaAcceso; }
    public String getEstadoAcceso() { return estadoAcceso; }
}