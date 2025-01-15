/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.gestorfinanciero;

/**
 *
 * @author JIANG XIAO QI
 */

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;

public class SQLiteDatabaseExample {

    public static void main(String[] args) {
        SQLiteDatabaseExample example = new SQLiteDatabaseExample();
        example.createTables();
        //example.insertDefaultCategories();

        /* Ejemplos de operaciones CRUD para categor�as -> TO DELETE
        example.insertCategoria("NuevaCategoria", false);
        example.updateCategoria(1, "CategoriaActualizada", true);
        example.listCategorias();
        example.getCategoriaById(1);
        example.deleteCategoria(1);*/

        /* Ejemplos de operaciones CRUD para gastos -> TO DELETE
        example.insertGasto(1, "NuevoGasto", 50.0, 30.0);
        example.updateGasto(1, "GastoActualizado", 60.0, 40.0);
        example.listGastos();
        example.getGastoById(1);
        example.deleteGasto(1);*/
    }

    public Connection connect() {
        String url = "jdbc:sqlite:mi_base_de_datos.db";
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return conn;
    }

    public void eliminarBaseDeDatos() {
        String eliminarBaseDeDatosSQL = "DROP DATABASE mi_base_de_datos.db";
        try (Connection conn = connect();
             Statement stmt = conn.createStatement()) {
            stmt.execute(eliminarBaseDeDatosSQL);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public void createTables() {
        String createCategoriasTableSQL = "CREATE TABLE IF NOT EXISTS categorias ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "nombre VARCHAR(20) NOT NULL,"
                + "balance BOOLEAN NOT NULL)";

        String createTransaccionesTableSQL = "CREATE TABLE IF NOT EXISTS transacciones ("
        + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
        + "categoria_id INTEGER NOT NULL,"
        + "descripcion TEXT,"
        + "importe DOUBLE NOT NULL,"
        + "fecha DATE NOT NULL,"
        + "es_gasto BOOLEAN NOT NULL,"
        + "FOREIGN KEY (categoria_id) REFERENCES categorias(id))";

        String createPresupuestosTableSQL = "CREATE TABLE IF NOT EXISTS presupuestos ("
                + "categoria_id INTEGER NOT NULL,"
                + "cantidad DOUBLE NOT NULL,"
                + "FOREIGN KEY (categoria_id) REFERENCES categorias(id))";

        try (Connection conn = connect();
            Statement stmt = conn.createStatement()) {
            stmt.execute(createCategoriasTableSQL);
            stmt.execute(createTransaccionesTableSQL);
            stmt.execute(createPresupuestosTableSQL);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void insertDefaultCategories() {
        insertCategoria("salario", true);
        insertCategoria("inversiones", false);
        insertCategoria("comida", false);
        insertCategoria("entretenimiento", false);
        insertCategoria("salud", false);
        insertCategoria("viajes", false);
        insertCategoria("finanzas", false);
        insertCategoria("otros", false);
    }

    public int obtenerCategoriaId(String nombreCategoria) throws SQLException {
        String sql = "SELECT id FROM categorias WHERE nombre = ?";
        
        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, nombreCategoria);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt("id");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        
        return -1; // Retorna -1 si no se encuentra la categoría
    }

    public ArrayList<String> obtenerNombresCategorias() {
        ArrayList<String> nombresCategorias = new ArrayList<>();
        String sql = "SELECT nombre FROM categorias";
        
        try (Connection conn = this.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                nombresCategorias.add(rs.getString("nombre"));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        
        return nombresCategorias;
    }

    public void insertarPresupuesto(int categoriaId, double cantidad) throws SQLException {
        String sql = "INSERT INTO presupuestos (categoria_id, cantidad) VALUES (?, ?)";
        
        try (Connection conn = this.connect();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
                pstmt.setInt(1, categoriaId);
                pstmt.setDouble(2, cantidad);
                pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public double obtenerPresupuestoPorCategoria(int categoriaId) throws SQLException {
        String sql = "SELECT cantidad FROM presupuestos WHERE categoria_id = ?";
        
        try (Connection conn = this.connect();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, categoriaId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getDouble("cantidad");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        
        return 0.0; // Retorna 0.0 si no se encuentra el presupuesto
    }

    public double obtenerTotalTransaccionesPorCategoria(int categoriaId, boolean esGasto) {
        String sql = "SELECT SUM(importe) as total FROM transacciones "
                  + "WHERE categoria_id = ? AND es_gasto = ?";
        
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, categoriaId);
            pstmt.setBoolean(2, esGasto);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getDouble("total");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0.0;
    }
    
    public ArrayList<TransaccionDTO> obtenerTransaccionesPorMes(int mes, int año) {
        ArrayList<TransaccionDTO> transacciones = new ArrayList<>();
        String sql = "SELECT t.*, c.nombre as categoria_nombre "
                  + "FROM transacciones t "
                  + "JOIN categorias c ON t.categoria_id = c.id "
                  + "WHERE strftime('%m', t.fecha) = ? AND strftime('%Y', t.fecha) = ? "
                  + "ORDER BY t.fecha DESC";
        
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, String.format("%02d", mes));
            pstmt.setString(2, String.valueOf(año));
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                TransaccionDTO transaccion = new TransaccionDTO(
                    rs.getInt("id"),
                    rs.getString("categoria_nombre"),
                    rs.getString("descripcion"),
                    rs.getDouble("importe"),
                    rs.getDate("fecha"),
                    rs.getBoolean("es_gasto")
                );
                transacciones.add(transaccion);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return transacciones;
    }

    public double obtenerBalanceTotal() {
        String sql = "SELECT SUM(CASE WHEN es_gasto = 0 THEN importe ELSE -importe END) as balance "
                  + "FROM transacciones";
        
        try (Connection conn = connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getDouble("balance");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0.0;
    }

    // Operaciones CRUD para Categor�as

    public void insertCategoria(String nombre, boolean balance) {
        String insertCategoriaSQL = "INSERT INTO categorias (nombre, balance) VALUES (?, ?)";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(insertCategoriaSQL)) {
            pstmt.setString(1, nombre);
            pstmt.setBoolean(2, balance);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateCategoria(int id, String nombre, boolean balance) {
        String updateCategoriaSQL = "UPDATE categorias SET nombre = ?, balance = ? WHERE id = ?";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(updateCategoriaSQL)) {
            pstmt.setString(1, nombre);
            pstmt.setBoolean(2, balance);
            pstmt.setInt(3, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateCategoriaName(String nombreViejo, String nombreNuevo) {
        String updateCategoriaNameSQL = "UPDATE categorias SET nombre = ? WHERE nombre = ?";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(updateCategoriaNameSQL)) {
            pstmt.setString(1, nombreNuevo);
            pstmt.setString(2, nombreViejo);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<Categoria> listCategorias() {
        ArrayList<Categoria> categorias = new ArrayList<>();
        String selectCategoriasSQL = "SELECT * FROM categorias";

        try (Connection conn = connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(selectCategoriasSQL)) {
            while (rs.next()) {
                System.out.println("ID: " + rs.getInt("id") +
                        ", Nombre: " + rs.getString("nombre") +
                        ", Balance: " + rs.getBoolean("balance"));
                String nombre = rs.getString("nombre");
                boolean balance = rs.getBoolean("balance");
                categorias.add(new Categoria(nombre, balance));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return categorias;
    }

    public void getCategoriaById(int id) {
        String selectCategoriaByIdSQL = "SELECT * FROM categorias WHERE id = ?";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(selectCategoriaByIdSQL)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    System.out.println("ID: " + rs.getInt("id") +
                            ", Nombre: " + rs.getString("nombre") +
                            ", Balance: " + rs.getBoolean("balance"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public String getCategoriabyName(String nombre) {
        String selectCategoriaByNameSQL = "SELECT * FROM categorias WHERE nombre = ?";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(selectCategoriaByNameSQL)) {
            pstmt.setString(1, nombre);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    System.out.println("ID: " + rs.getInt("id") +
                            ", Nombre: " + rs.getString("nombre") +
                            ", Balance: " + rs.getBoolean("balance"));
                    return rs.getString("nombre");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Categoria getCategoriaByName(String nombre) {
        String selectCategoriaByNameSQL = "SELECT * FROM categorias WHERE nombre = ?";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(selectCategoriaByNameSQL)) {
            pstmt.setString(1, nombre);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    System.out.println("ID: " + rs.getInt("id") +
                            ", Nombre: " + rs.getString("nombre") +
                            ", Balance: " + rs.getBoolean("balance"));
                    Categoria categoria = new Categoria(rs.getString("nombre"), rs.getBoolean("balance"));
                    System.out.println(categoria);
                    return categoria;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getCategoriaByNameString(String nombre) {
        String selectCategoriaByNameSQL = "SELECT * FROM categorias WHERE nombre = ?";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(selectCategoriaByNameSQL)) {
            pstmt.setString(1, nombre);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    System.out.println("ID: " + rs.getInt("id") +
                            ", Nombre: " + rs.getString("nombre") +
                            ", Balance: " + rs.getBoolean("balance"));
                    String categoria = rs.getString("nombre");
                    System.out.println(categoria);
                    return categoria;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "No se encontró la categoría";
    }

    public void deleteCategoria(int id) {
        String deleteCategoriaSQL = "DELETE FROM categorias WHERE id = ?";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(deleteCategoriaSQL)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteCategoriaByName(String nombre) {
        String deleteCategoriaByNameSQL = "DELETE FROM categorias WHERE nombre = ?";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(deleteCategoriaByNameSQL)) {
            pstmt.setString(1, nombre);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Operaciones CRUD para Transacciones

    public void insertTransaccion(int categoriaId, String descripcion, double importe, 
                                Date fecha, boolean esGasto) {
        String sql = "INSERT INTO transacciones (categoria_id, descripcion, importe, fecha, es_gasto) "
                  + "VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, categoriaId);
            pstmt.setString(2, descripcion);
            pstmt.setDouble(3, importe);
            pstmt.setDate(4, new java.sql.Date(fecha.getTime()));
            pstmt.setBoolean(5, esGasto);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void insertGasto(int categoriaId, String nombre, double importe, double capital) {
        String insertGastoSQL = "INSERT INTO gastos (categoria_id, nombre, importe, capital) VALUES (?, ?, ?, ?)";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(insertGastoSQL)) {
            pstmt.setInt(1, categoriaId);
            pstmt.setString(2, nombre);
            pstmt.setDouble(3, importe);
            pstmt.setDouble(4, capital);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateGasto(int id, String nombre, double importe, double capital) {
        String updateGastoSQL = "UPDATE gastos SET nombre = ?, importe = ?, capital = ? WHERE id = ?";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(updateGastoSQL)) {
            pstmt.setString(1, nombre);
            pstmt.setDouble(2, importe);
            pstmt.setDouble(3, capital);
            pstmt.setInt(4, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateGastoCapital(int id, double capital) {
        String updateGastoCapitalSQL = "UPDATE gastos SET capital = ? WHERE id = ?";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(updateGastoCapitalSQL)) {
            pstmt.setDouble(1, capital);
            pstmt.setInt(2, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateGastoCapitalPorNombre(String nombre, double capital) {
        String updateGastoCapitalSQL = "UPDATE gastos SET capital = ? WHERE nombre = ?";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(updateGastoCapitalSQL)) {
            pstmt.setDouble(1, capital);
            pstmt.setString(2, nombre);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void listGastos() {
        String selectGastosSQL = "SELECT * FROM gastos";
        try (Connection conn = connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(selectGastosSQL)) {
            while (rs.next()) {
                System.out.println("ID: " + rs.getInt("id") +
                        ", Categor�a ID: " + rs.getInt("categoria_id") +
                        ", Nombre: " + rs.getString("nombre") +
                        ", Importe: " + rs.getDouble("importe") +
                        ", Capital: " + rs.getDouble("capital"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<Gastos> listarGastos() {
        ArrayList<Gastos> gastos = new ArrayList<>();
        String selectGastosSQL = "SELECT * FROM gastos";
        try (Connection conn = connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(selectGastosSQL)) {
            while (rs.next()) {
                System.out.println("ID: " + rs.getInt("id") +
                        ", Categor�a ID: " + rs.getInt("categoria_id") +
                        ", Nombre: " + rs.getString("nombre") +
                        ", Importe: " + rs.getDouble("importe") +
                        ", Capital: " + rs.getDouble("capital"));
                String nombre = rs.getString("nombre");
                String importe = rs.getString("importe");
                double capital = rs.getDouble("capital");
                gastos.add(new Gastos(nombre, importe, capital));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return gastos;
    }

    public void getGastoById(int id) {
        String selectGastoByIdSQL = "SELECT * FROM gastos WHERE id = ?";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(selectGastoByIdSQL)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    System.out.println("ID: " + rs.getInt("id") +
                            ", Categor�a ID: " + rs.getInt("categoria_id") +
                            ", Nombre: " + rs.getString("nombre") +
                            ", Importe: " + rs.getDouble("importe") +
                            ", Capital: " + rs.getDouble("capital"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteGasto(int id) {
        String deleteGastoSQL = "DELETE FROM gastos WHERE id = ?";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(deleteGastoSQL)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //Obtener el ultimo capital O CUANTO NOS QUEDA 
    public double obtenerUltimoCapital() {
        String selectUltimoCapitalSQL = "SELECT capital FROM gastos ORDER BY id DESC LIMIT 1";
        try (Connection conn = connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(selectUltimoCapitalSQL)) {
            if (rs.next()) {
                return rs.getDouble("capital");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0.0; // Valor predeterminado si no se encuentra ninguna entrada en la tabla de gastos
    }

    //Obtener gastos de una categoria
    public double obtenerImporteTotalPorCategoria(int categoriaId) {
        String selectImporteTotalSQL = "SELECT SUM(importe) AS importe_total FROM gastos WHERE categoria_id = ?";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(selectImporteTotalSQL)) {
            pstmt.setInt(1, categoriaId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble("importe_total");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0.0; // Valor predeterminado si no se encuentra ninguna entrada para la categor�a dada
    }
    
    //Listar gastos por categoria (todas)
    public double obtenerImporteTotalTodasCategorias() {
        String selectImporteTotalSQL = "SELECT SUM(importe) AS importe_total FROM gastos GROUP BY categoria_id";
        try (Connection conn = connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(selectImporteTotalSQL)) {
            if (rs.next()) {
                return rs.getDouble("importe_total");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0.0; // Valor predeterminado si no se encuentra ninguna entrada en la tabla de gastos
    }

    //Listar solo gastos por categoria (balance false)
    public ArrayList<Gastos> listarGastosPorCategorias() {
        ArrayList<Gastos> gastos = new ArrayList<>();
        //String selectGastosPorCategoriasSQL = "SELECT nombre, importe FROM gastos WHERE balance = false";

        String selectGastosPorCategoriasSinBalanceSQL =
                "SELECT g.id, g.categoria_id, g.nombre, g.importe, g.capital, c.nombre as categoria_nombre " +
                        "FROM gastos g " +
                        "JOIN categorias c ON g.categoria_id = c.id " +
                        "WHERE c.balance = false";

        try (Connection conn = connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(selectGastosPorCategoriasSinBalanceSQL)) {

            while (rs.next()) {
                System.out.println("ID Gasto: " + rs.getInt("id") +
                        ", Categor�a ID: " + rs.getInt("categoria_id") +
                        ", Categor�a Nombre: " + rs.getString("categoria_nombre") +
                        ", Nombre Gasto: " + rs.getString("nombre") +
                        ", Importe: " + rs.getDouble("importe") +
                        ", Capital: " + rs.getDouble("capital"));
                
                String nombre = rs.getString("nombre");
                String categoria = rs.getString("categoria");
                double importe = rs.getDouble("importe");
                gastos.add(new Gastos(nombre, categoria, importe));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return gastos;
    }

    //listar ingresos por categorias (balance true)
    public ArrayList<Ingresos> listarIngresosPorCategorias() {
        ArrayList<Ingresos> ingresos = new ArrayList<>();
        String selectIngresosSQL = "SELECT nombre, importe FROM gastos WHERE balance = true";

        try (Connection conn = connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(selectIngresosSQL)) {

            while (rs.next()) {
                String nombre = rs.getString("nombre");
                String categoria = rs.getString("categoria");
                double importe = rs.getDouble("importe");
                ingresos.add(new Ingresos(nombre, categoria, importe));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return ingresos;
    }

    //Obtener el balance de una categoria por id
    public boolean obtenerBalancePorId(int categoriaId) {
        String selectBalanceSQL = "SELECT balance FROM categorias WHERE id = ?";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(selectBalanceSQL)) {
            pstmt.setInt(1, categoriaId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getBoolean("balance");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false; // Valor predeterminado si no se encuentra ninguna entrada para la categor�a dada
    }
    //Obtener el nombre de una categoria por id
    public String obtenerNombrePorId(int categoriaId) {
        String selectNombreSQL = "SELECT nombre FROM categorias WHERE id = ?";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(selectNombreSQL)) {
            pstmt.setInt(1, categoriaId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("nombre");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ""; // Valor predeterminado si no se encuentra ninguna entrada para la categoría dada
    }

    //obtener balance de una categoria por nombre
    public boolean obtenerBalancePorNombre(String nombreCategoria) {
        String selectBalanceSQL = "SELECT balance FROM categorias WHERE nombre = ?";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(selectBalanceSQL)) {
            pstmt.setString(1, nombreCategoria);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getBoolean("balance");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false; // Valor predeterminado si no se encuentra ninguna entrada para la categor�a dada
    }

    //borrar todos los gastos e ingresos
    public void borrarTodosLosGastosIngresos() {
        String deleteTodosLosGastosSQL = "DELETE FROM gastos";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(deleteTodosLosGastosSQL)) {
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //borrar todos los gastos (balance false)
    public void borrarTodosLosGastos() {
        String selectCategoriasSinBalanceSQL = "SELECT id FROM categorias WHERE balance = false";
        String deleteGastosPorCategoriaSQL = "DELETE FROM gastos WHERE categoria_id = ?";

        try (Connection conn = connect();
             Statement stmt = conn.createStatement();
             PreparedStatement pstmt = conn.prepareStatement(deleteGastosPorCategoriaSQL)) {

            try (ResultSet rs = stmt.executeQuery(selectCategoriasSinBalanceSQL)) {
                while (rs.next()) {
                    int categoriaId = rs.getInt("id");
                    pstmt.setInt(1, categoriaId);
                    pstmt.executeUpdate();
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
