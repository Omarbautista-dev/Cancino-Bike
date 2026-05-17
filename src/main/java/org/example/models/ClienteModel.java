package org.example.models;

import org.example.database.ConexionBD;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class ClienteModel {

    public List<ClienteItem> listarClientesActivos() {
        List<ClienteItem> lista = new ArrayList<>();

        String sql = """
            SELECT id_cliente, nombre_cliente
            FROM clientes
            WHERE estado = 1
            ORDER BY nombre_cliente
            """;

        try (Connection con = ConexionBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(new ClienteItem(
                        rs.getInt("id_cliente"),
                        rs.getString("nombre_cliente")
                ));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return lista;
    }
}