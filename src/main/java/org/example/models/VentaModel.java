package org.example.models;

import org.example.database.ConexionBD;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class VentaModel {

    public List<Producto> buscarProductosVenta(String filtro) {
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
                IFNULL(p.id_proveedor, 0) AS id_proveedor,
                IFNULL(pr.nombre_empresa, 'Sin proveedor') AS proveedor
            FROM productos p
            LEFT JOIN proveedores pr ON p.id_proveedor = pr.id_proveedor
            WHERE p.estado = 1
            AND p.stock > 0
            AND (
                p.codigo_barras LIKE ?
                OR p.modelo LIKE ?
                OR p.nombre_producto LIKE ?
            )
            ORDER BY p.nombre_producto
            """;

        try (Connection con = ConexionBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            String busqueda = "%" + filtro + "%";

            ps.setString(1, busqueda);
            ps.setString(2, busqueda);
            ps.setString(3, busqueda);

            ResultSet rs = ps.executeQuery();

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
                        rs.getInt("id_proveedor"),
                        rs.getString("proveedor")
                ));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return lista;
    }

    public boolean registrarVenta(
            String folio,
            int idUsuario,
            Integer idCliente,
            String fechaLimite,
            String tipoVenta,
            String tipoPrecio,
            double subtotal,
            double descuento,
            double total,
            List<DetalleVentaItem> carrito
    ) {

        String sqlVenta = """
        INSERT INTO ventas
        (folio, id_usuario, id_cliente, tipo_venta, tipo_precio, subtotal, descuento, total, estado)
        VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;

        String sqlDetalle = """
        INSERT INTO detalle_ventas
        (id_venta, id_producto, cantidad, precio_unitario, subtotal)
        VALUES (?, ?, ?, ?, ?)
        """;

        String sqlStock = """
        UPDATE productos
        SET stock = stock - ?
        WHERE id_producto = ?
        AND stock >= ?
        """;

        String sqlTicket = """
        INSERT INTO tickets
        (id_venta, folio_ticket, total)
        VALUES (?, ?, ?)
        """;

        String sqlCredito = """
        INSERT INTO creditos
        (id_venta, id_cliente, monto_total, monto_pagado, saldo_pendiente, estado, fecha_limite)
        VALUES (?, ?, ?, ?, ?, ?, ?)
        """;

        try (Connection con = ConexionBD.conectar()) {

            con.setAutoCommit(false);

            try (PreparedStatement psVenta = con.prepareStatement(sqlVenta, Statement.RETURN_GENERATED_KEYS)) {

                psVenta.setString(1, folio);
                psVenta.setInt(2, idUsuario);

                if (idCliente == null) {
                    psVenta.setNull(3, Types.INTEGER);
                } else {
                    psVenta.setInt(3, idCliente);
                }

                psVenta.setString(4, tipoVenta);
                psVenta.setString(5, tipoPrecio);
                psVenta.setDouble(6, subtotal);
                psVenta.setDouble(7, descuento);
                psVenta.setDouble(8, total);
                psVenta.setString(9, tipoVenta.equals("CREDITO") ? "PENDIENTE" : "PAGADA");

                psVenta.executeUpdate();

                ResultSet keys = psVenta.getGeneratedKeys();

                if (!keys.next()) {
                    con.rollback();
                    return false;
                }

                int idVenta = keys.getInt(1);

                try (PreparedStatement psDetalle = con.prepareStatement(sqlDetalle);
                     PreparedStatement psStock = con.prepareStatement(sqlStock);
                     PreparedStatement psTicket = con.prepareStatement(sqlTicket)) {

                    for (DetalleVentaItem item : carrito) {

                        psStock.setInt(1, item.getCantidad());
                        psStock.setInt(2, item.getIdProducto());
                        psStock.setInt(3, item.getCantidad());

                        int stockActualizado = psStock.executeUpdate();

                        if (stockActualizado == 0) {
                            con.rollback();
                            return false;
                        }

                        psDetalle.setInt(1, idVenta);
                        psDetalle.setInt(2, item.getIdProducto());
                        psDetalle.setInt(3, item.getCantidad());
                        psDetalle.setDouble(4, item.getPrecioUnitario());
                        psDetalle.setDouble(5, item.getSubtotal());
                        psDetalle.addBatch();
                    }

                    psDetalle.executeBatch();

                    psTicket.setInt(1, idVenta);
                    psTicket.setString(2, "TCK-" + folio);
                    psTicket.setDouble(3, total);
                    psTicket.executeUpdate();
                }

                if (tipoVenta.equals("CREDITO")) {

                    if (idCliente == null || fechaLimite == null) {
                        con.rollback();
                        return false;
                    }

                    try (PreparedStatement psCredito = con.prepareStatement(sqlCredito)) {
                        psCredito.setInt(1, idVenta);
                        psCredito.setInt(2, idCliente);
                        psCredito.setDouble(3, total);
                        psCredito.setDouble(4, 0);
                        psCredito.setDouble(5, total);
                        psCredito.setString(6, "PENDIENTE");
                        psCredito.setDate(7, Date.valueOf(fechaLimite));

                        psCredito.executeUpdate();
                    }
                }

                con.commit();
                return true;

            } catch (Exception e) {
                con.rollback();
                e.printStackTrace();
                return false;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}