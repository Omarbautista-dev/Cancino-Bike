package org.example.models;

import org.example.database.ConexionBD;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class ProveedorModel {

    // =========================
    // LISTAR
    // =========================

    public List<Proveedor> listarProveedores() {

        List<Proveedor> lista = new ArrayList<>();

        String sql = """
            SELECT *
            FROM proveedores
            WHERE estado = 1
            ORDER BY nombre_empresa
            """;

        try (Connection con = ConexionBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                lista.add(new Proveedor(
                        rs.getInt("id_proveedor"),
                        rs.getString("nombre_empresa"),
                        rs.getString("contacto"),
                        rs.getString("telefono"),
                        rs.getString("email"),
                        rs.getString("rfc"),
                        rs.getString("direccion")
                ));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return lista;
    }

    // =========================
    // BUSCAR
    // =========================

    public List<Proveedor> buscarProveedor(String filtro) {

        List<Proveedor> lista = new ArrayList<>();

        String sql = """
            SELECT *
            FROM proveedores
            WHERE estado = 1
            AND (
                nombre_empresa LIKE ?
                OR contacto LIKE ?
                OR telefono LIKE ?
                OR rfc LIKE ?
            )
            ORDER BY nombre_empresa
            """;

        try (Connection con = ConexionBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            String busqueda = "%" + filtro + "%";

            ps.setString(1, busqueda);
            ps.setString(2, busqueda);
            ps.setString(3, busqueda);
            ps.setString(4, busqueda);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                lista.add(new Proveedor(
                        rs.getInt("id_proveedor"),
                        rs.getString("nombre_empresa"),
                        rs.getString("contacto"),
                        rs.getString("telefono"),
                        rs.getString("email"),
                        rs.getString("rfc"),
                        rs.getString("direccion")
                ));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return lista;
    }

    // =========================
    // INSERTAR
    // =========================

    public boolean insertarProveedor(
            String empresa,
            String contacto,
            String telefono,
            String email,
            String rfc,
            String direccion
    ) {

        String sql = """
            INSERT INTO proveedores
            (nombre_empresa, contacto, telefono, email, rfc, direccion)
            VALUES (?, ?, ?, ?, ?, ?)
            """;

        try (Connection con = ConexionBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, empresa);
            ps.setString(2, contacto);
            ps.setString(3, telefono);
            ps.setString(4, email);
            ps.setString(5, rfc);
            ps.setString(6, direccion);

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    // =========================
    // ACTUALIZAR
    // =========================

    public boolean actualizarProveedor(
            int idProveedor,
            String empresa,
            String contacto,
            String telefono,
            String email,
            String rfc,
            String direccion
    ) {

        String sql = """
            UPDATE proveedores
            SET nombre_empresa = ?,
                contacto = ?,
                telefono = ?,
                email = ?,
                rfc = ?,
                direccion = ?
            WHERE id_proveedor = ?
            """;

        try (Connection con = ConexionBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, empresa);
            ps.setString(2, contacto);
            ps.setString(3, telefono);
            ps.setString(4, email);
            ps.setString(5, rfc);
            ps.setString(6, direccion);

            ps.setInt(7, idProveedor);

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    // =========================
    // ELIMINAR LÓGICO
    // =========================

    public boolean eliminarProveedor(int idProveedor) {

        String sql = """
            UPDATE proveedores
            SET estado = 0
            WHERE id_proveedor = ?
            """;

        try (Connection con = ConexionBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idProveedor);

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean existeEmpresa(String empresa) {
        String sql = """
        SELECT COUNT(*) AS total
        FROM proveedores
        WHERE nombre_empresa = ?
        AND estado = 1
        """;

        try (Connection con = ConexionBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, empresa);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("total") > 0;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }
}