package org.example.models;

import org.example.database.ConexionBD;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;

public class ReporteModel {

    public double totalVentas(LocalDate fecha) {
        String sql = """
            SELECT IFNULL(SUM(total), 0) AS total
            FROM ventas
            WHERE DATE(fecha_venta) = ?
            AND estado <> 'CANCELADA'
        """;
        return obtenerDouble(sql, fecha);
    }

    public double totalContado(LocalDate fecha) {
        String sql = """
            SELECT IFNULL(SUM(total), 0) AS total
            FROM ventas
            WHERE DATE(fecha_venta) = ?
            AND tipo_venta = 'CONTADO'
            AND estado <> 'CANCELADA'
        """;
        return obtenerDouble(sql, fecha);
    }

    public double totalCredito(LocalDate fecha) {
        String sql = """
            SELECT IFNULL(SUM(total), 0) AS total
            FROM ventas
            WHERE DATE(fecha_venta) = ?
            AND tipo_venta = 'CREDITO'
            AND estado <> 'CANCELADA'
        """;
        return obtenerDouble(sql, fecha);
    }

    public int numeroVentas(LocalDate fecha) {
        String sql = """
            SELECT COUNT(*) AS total
            FROM ventas
            WHERE DATE(fecha_venta) = ?
            AND estado <> 'CANCELADA'
        """;

        try (Connection con = ConexionBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setDate(1, java.sql.Date.valueOf(fecha));
            ResultSet rs = ps.executeQuery();

            if (rs.next()) return rs.getInt("total");

        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }

    public double utilidad(LocalDate fecha) {
        String sql = """
            SELECT IFNULL(SUM((dv.precio_unitario - p.precio_compra) * dv.cantidad), 0) AS total
            FROM detalle_ventas dv
            INNER JOIN ventas v ON dv.id_venta = v.id_venta
            INNER JOIN productos p ON dv.id_producto = p.id_producto
            WHERE DATE(v.fecha_venta) = ?
            AND v.estado <> 'CANCELADA'
        """;
        return obtenerDouble(sql, fecha);
    }

    public double valorInventario() {
        String sql = """
            SELECT IFNULL(SUM(precio_compra * stock), 0) AS total
            FROM productos
            WHERE estado = 1
        """;

        try (Connection con = ConexionBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) return rs.getDouble("total");

        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }

    public int productosStockBajo() {
        String sql = """
            SELECT COUNT(*) AS total
            FROM productos
            WHERE estado = 1
            AND stock <= stock_minimo
        """;

        try (Connection con = ConexionBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) return rs.getInt("total");

        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }

    public boolean guardarCorteCaja(int idUsuario,
                                    double totalVentas,
                                    double totalContado,
                                    double totalCredito,
                                    double utilidad) {

        String sql = """
            INSERT INTO cortes_caja
            (id_usuario, total_ventas, total_contado, total_credito, utilidad)
            VALUES (?, ?, ?, ?, ?)
        """;

        try (Connection con = ConexionBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idUsuario);
            ps.setDouble(2, totalVentas);
            ps.setDouble(3, totalContado);
            ps.setDouble(4, totalCredito);
            ps.setDouble(5, utilidad);

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    private double obtenerDouble(String sql, LocalDate fecha) {
        try (Connection con = ConexionBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setDate(1, java.sql.Date.valueOf(fecha));
            ResultSet rs = ps.executeQuery();

            if (rs.next()) return rs.getDouble("total");

        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }
}