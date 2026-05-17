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
            IFNULL(p.id_proveedor, 0) AS id_proveedor,
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
                        rs.getInt("id_proveedor"),
                        rs.getString("proveedor")
                ));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return lista;
    }

    //METODOS PARA EL CRUD
    public boolean insertarProducto(String codigo, String modelo, String nombre, String descripcion,
                                    double compra, double menudeo, double mayoreo,
                                    int stock, int minimo, int idProveedor) {

        String sql = """
            INSERT INTO productos
            (codigo_barras, modelo, nombre_producto, descripcion,
             precio_compra, precio_menudeo, precio_mayoreo,
             stock, stock_minimo, id_proveedor)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;

        try (Connection con = ConexionBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, codigo);
            ps.setString(2, modelo);
            ps.setString(3, nombre);
            ps.setString(4, descripcion);
            ps.setDouble(5, compra);
            ps.setDouble(6, menudeo);
            ps.setDouble(7, mayoreo);
            ps.setInt(8, stock);
            ps.setInt(9, minimo);
            ps.setInt(10, idProveedor);

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Producto> buscarProductos(String filtro) {
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
        AND (
            p.codigo_barras LIKE ?
            OR p.modelo LIKE ?
            OR p.nombre_producto LIKE ?
        )
        ORDER BY p.id_producto DESC
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

    public boolean actualizarProducto(int idProducto, String codigo, String modelo, String nombre,
                                      String descripcion, double compra, double menudeo,
                                      double mayoreo, int stock, int minimo, int idProveedor) {

        String sql = """
            UPDATE productos SET
                codigo_barras = ?,
                modelo = ?,
                nombre_producto = ?,
                descripcion = ?,
                precio_compra = ?,
                precio_menudeo = ?,
                precio_mayoreo = ?,
                stock = ?,
                stock_minimo = ?,
                id_proveedor = ?
            WHERE id_producto = ?
            """;

        try (Connection con = ConexionBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, codigo);
            ps.setString(2, modelo);
            ps.setString(3, nombre);
            ps.setString(4, descripcion);
            ps.setDouble(5, compra);
            ps.setDouble(6, menudeo);
            ps.setDouble(7, mayoreo);
            ps.setInt(8, stock);
            ps.setInt(9, minimo);
            ps.setInt(10, idProveedor);
            ps.setInt(11, idProducto);

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean eliminarProducto(int idProducto) {
        String sql = "UPDATE productos SET estado = 0 WHERE id_producto = ?";

        try (Connection con = ConexionBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idProducto);
            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<ProveedorItem> listarProveedores() {
        List<ProveedorItem> lista = new ArrayList<>();

        String sql = """
            SELECT id_proveedor, nombre_empresa
            FROM proveedores
            WHERE estado = 1
            ORDER BY nombre_empresa
            """;

        try (Connection con = ConexionBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(new ProveedorItem(
                        rs.getInt("id_proveedor"),
                        rs.getString("nombre_empresa")
                ));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return lista;
    }

}