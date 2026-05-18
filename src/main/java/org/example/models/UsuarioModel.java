package org.example.models;

import org.example.database.ConexionBD;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class UsuarioModel {

    public boolean validarLogin(String usuario, String password) {
        String sql = """
                SELECT id_usuario
                FROM usuarios
                WHERE usuario = ?
                AND password = ?
                AND estado = 1
                """;

        try (Connection conn = ConexionBD.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, usuario);
            stmt.setString(2, password);

            ResultSet rs = stmt.executeQuery();
            return rs.next();

        } catch (Exception e) {
            System.out.println("Error en login: " + e.getMessage());
            return false;
        }
    }
    public int obtenerIdUsuario(String usuario) {
        String sql = """
        SELECT id_usuario
        FROM usuarios
        WHERE usuario = ?
    """;

        try (Connection con = ConexionBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, usuario);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt("id_usuario");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }

    public void registrarAcceso(int idUsuario, String estado) {
        String sql = """
        INSERT INTO accesos
        (id_usuario, estado_acceso)
        VALUES (?, ?)
    """;

        try (Connection con = ConexionBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idUsuario);
            ps.setString(2, estado);
            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}