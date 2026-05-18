package org.example.models;

import org.example.database.ConexionBD;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class UsuarioSeguridadModel {

    public List<Usuario> listarUsuarios() {
        List<Usuario> lista = new ArrayList<>();

        String sql = """
            SELECT 
                u.id_usuario,
                u.usuario,
                u.nombre_completo,
                u.id_rol,
                r.nombre_rol,
                u.estado,
                DATE_FORMAT(u.fecha_registro, '%Y-%m-%d %H:%i') AS fecha_registro
            FROM usuarios u
            INNER JOIN roles r ON u.id_rol = r.id_rol
            ORDER BY u.id_usuario DESC
        """;

        try (Connection con = ConexionBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(new Usuario(
                        rs.getInt("id_usuario"),
                        rs.getString("usuario"),
                        rs.getString("nombre_completo"),
                        rs.getInt("id_rol"),
                        rs.getString("nombre_rol"),
                        rs.getInt("estado"),
                        rs.getString("fecha_registro")
                ));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return lista;
    }

    public List<Usuario> buscarUsuarios(String filtro) {
        List<Usuario> lista = new ArrayList<>();

        String sql = """
            SELECT 
                u.id_usuario,
                u.usuario,
                u.nombre_completo,
                u.id_rol,
                r.nombre_rol,
                u.estado,
                DATE_FORMAT(u.fecha_registro, '%Y-%m-%d %H:%i') AS fecha_registro
            FROM usuarios u
            INNER JOIN roles r ON u.id_rol = r.id_rol
            WHERE u.usuario LIKE ?
               OR u.nombre_completo LIKE ?
               OR r.nombre_rol LIKE ?
            ORDER BY u.id_usuario DESC
        """;

        try (Connection con = ConexionBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            String busqueda = "%" + filtro + "%";

            ps.setString(1, busqueda);
            ps.setString(2, busqueda);
            ps.setString(3, busqueda);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                lista.add(new Usuario(
                        rs.getInt("id_usuario"),
                        rs.getString("usuario"),
                        rs.getString("nombre_completo"),
                        rs.getInt("id_rol"),
                        rs.getString("nombre_rol"),
                        rs.getInt("estado"),
                        rs.getString("fecha_registro")
                ));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return lista;
    }

    public List<RolItem> listarRoles() {
        List<RolItem> lista = new ArrayList<>();

        String sql = """
            SELECT id_rol, nombre_rol
            FROM roles
            ORDER BY nombre_rol
        """;

        try (Connection con = ConexionBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(new RolItem(
                        rs.getInt("id_rol"),
                        rs.getString("nombre_rol")
                ));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return lista;
    }

    public boolean existeUsuario(String usuario) {
        String sql = """
            SELECT COUNT(*) AS total
            FROM usuarios
            WHERE usuario = ?
        """;

        try (Connection con = ConexionBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, usuario);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt("total") > 0;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean insertarUsuario(String usuario,
                                   String password,
                                   String nombreCompleto,
                                   int idRol) {
        String sql = """
            INSERT INTO usuarios
            (usuario, password, nombre_completo, id_rol, estado)
            VALUES (?, ?, ?, ?, 1)
        """;

        try (Connection con = ConexionBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, usuario);
            ps.setString(2, password);
            ps.setString(3, nombreCompleto);
            ps.setInt(4, idRol);

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean actualizarUsuario(int idUsuario,
                                     String usuario,
                                     String nombreCompleto,
                                     int idRol,
                                     int estado) {
        String sql = """
            UPDATE usuarios
            SET usuario = ?,
                nombre_completo = ?,
                id_rol = ?,
                estado = ?
            WHERE id_usuario = ?
        """;

        try (Connection con = ConexionBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, usuario);
            ps.setString(2, nombreCompleto);
            ps.setInt(3, idRol);
            ps.setInt(4, estado);
            ps.setInt(5, idUsuario);

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean cambiarEstadoUsuario(int idUsuario, int estado) {
        String sql = """
            UPDATE usuarios
            SET estado = ?
            WHERE id_usuario = ?
        """;

        try (Connection con = ConexionBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, estado);
            ps.setInt(2, idUsuario);

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }
    public List<Acceso> listarAccesos() {
        List<Acceso> lista = new ArrayList<>();

        String sql = """
        SELECT 
            a.id_acceso,
            u.usuario,
            u.nombre_completo,
            DATE_FORMAT(a.fecha_acceso, '%Y-%m-%d %H:%i') AS fecha_acceso,
            a.estado_acceso
        FROM accesos a
        INNER JOIN usuarios u ON a.id_usuario = u.id_usuario
        ORDER BY a.fecha_acceso DESC
    """;

        try (Connection con = ConexionBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(new Acceso(
                        rs.getInt("id_acceso"),
                        rs.getString("usuario"),
                        rs.getString("nombre_completo"),
                        rs.getString("fecha_acceso"),
                        rs.getString("estado_acceso")
                ));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return lista;
    }

    public List<Acceso> buscarAccesos(String filtro) {
        List<Acceso> lista = new ArrayList<>();

        String sql = """
        SELECT 
            a.id_acceso,
            u.usuario,
            u.nombre_completo,
            DATE_FORMAT(a.fecha_acceso, '%Y-%m-%d %H:%i') AS fecha_acceso,
            a.estado_acceso
        FROM accesos a
        INNER JOIN usuarios u ON a.id_usuario = u.id_usuario
        WHERE u.usuario LIKE ?
           OR u.nombre_completo LIKE ?
           OR a.estado_acceso LIKE ?
        ORDER BY a.fecha_acceso DESC
    """;

        try (Connection con = ConexionBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            String busqueda = "%" + filtro + "%";

            ps.setString(1, busqueda);
            ps.setString(2, busqueda);
            ps.setString(3, busqueda);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                lista.add(new Acceso(
                        rs.getInt("id_acceso"),
                        rs.getString("usuario"),
                        rs.getString("nombre_completo"),
                        rs.getString("fecha_acceso"),
                        rs.getString("estado_acceso")
                ));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return lista;
    }
}