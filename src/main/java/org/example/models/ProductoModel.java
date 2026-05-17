package org.example.models;

import org.example.database.ConexionBD;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class ProductoModel {

    public List<Producto> listarProductos() {
        List<Producto> lista = new ArrayList<>();

        String sql = """
                SELECT 
                    p.id_producto,
                    p.codigo_barras,
                    p.modelo,
                    p.nombre_producto,
                    p.descripcion,
                    p.precio_compra,
                    p.precio_menudeo,
                    p.precio_mayoreo,
                    p.stock,
                    p.stock_minimo,
                    IFNULL(pr.nombre_empresa, 'Sin proveedor') AS proveedor
                FROM productos p
                LEFT JOIN proveedores pr ON p.id_proveedor = pr.id_proveedor
                WHERE p.estado = 1
                ORDER BY p.id_producto DESC
                """;

        try (Connection con = ConexionBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(new Producto(
                        rs.getInt("id_producto"),
                        rs.getString("codigo_barras"),
                        rs.getString("modelo"),
                        rs.getString("nombre_producto"),
                        rs.getString("descripcion"),
                        rs.getDouble("precio_compra"),
                        rs.getDouble("precio_menudeo"),
                        rs.getDouble("precio_mayoreo"),
                        rs.getInt("stock"),
                        rs.getInt("stock_minimo"),
                        rs.getString("proveedor")
                ));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return lista;
    }
}