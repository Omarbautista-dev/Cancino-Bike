package org.example.models;

public class Usuario {

    private int idUsuario;
    private String usuario;
    private String nombreCompleto;
    private int idRol;
    private String rol;
    private int estado;
    private String fechaRegistro;

    public Usuario(int idUsuario, String usuario, String nombreCompleto,
                   int idRol, String rol, int estado, String fechaRegistro) {
        this.idUsuario = idUsuario;
        this.usuario = usuario;
        this.nombreCompleto = nombreCompleto;
        this.idRol = idRol;
        this.rol = rol;
        this.estado = estado;
        this.fechaRegistro = fechaRegistro;
    }

    public int getIdUsuario() { return idUsuario; }
    public String getUsuario() { return usuario; }
    public String getNombreCompleto() { return nombreCompleto; }
    public int getIdRol() { return idRol; }
    public String getRol() { return rol; }
    public int getEstado() { return estado; }
    public String getFechaRegistro() { return fechaRegistro; }
}