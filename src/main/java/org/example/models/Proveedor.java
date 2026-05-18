package org.example.models;

public class Proveedor {

    private int idProveedor;
    private String nombreEmpresa;
    private String contacto;
    private String telefono;
    private String email;
    private String rfc;
    private String direccion;

    public Proveedor(int idProveedor, String nombreEmpresa, String contacto,
                     String telefono, String email, String rfc, String direccion) {
        this.idProveedor = idProveedor;
        this.nombreEmpresa = nombreEmpresa;
        this.contacto = contacto;
        this.telefono = telefono;
        this.email = email;
        this.rfc = rfc;
        this.direccion = direccion;
    }

    public int getIdProveedor() { return idProveedor; }
    public String getNombreEmpresa() { return nombreEmpresa; }
    public String getContacto() { return contacto; }
    public String getTelefono() { return telefono; }
    public String getEmail() { return email; }
    public String getRfc() { return rfc; }
    public String getDireccion() { return direccion; }
}