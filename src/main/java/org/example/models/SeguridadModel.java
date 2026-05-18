package org.example.models;

import org.example.database.ConexionBD;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class SeguridadModel {

    public boolean validarPasswordActual(String usuario, String passwordActual) {
        String sql = """
            SELECT id_usuario
            FROM usuarios
            WHERE usuario = ?
            AND password = ?
            AND estado = 1
        """;

        try (Connection con = ConexionBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, usuario);
            ps.setString(2, passwordActual);

            ResultSet rs = ps.executeQuery();

            return rs.next();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean cambiarPassword(String usuario, String nuevaPassword) {
        String sql = """
            UPDATE usuarios
            SET password = ?
            WHERE usuario = ?
            AND estado = 1
        """;

        try (Connection con = ConexionBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, nuevaPassword);
            ps.setString(2, usuario);

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }
}