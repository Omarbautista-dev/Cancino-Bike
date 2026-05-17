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
}