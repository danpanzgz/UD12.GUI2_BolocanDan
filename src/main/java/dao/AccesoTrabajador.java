package dao;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import config.ConfigMySQL;
import exceptions.BDException;
import modelo.Trabajador;

public class AccesoTrabajador {

    public static boolean insertar(Trabajador t) throws BDException {
        return altaTrabajador(t);
    }

    public static boolean altaTrabajador(Trabajador t) throws BDException {
        String sql = "INSERT INTO empleados (dni, nombre, apellido, direccion, telefono, puesto) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = ConfigMySQL.abrirConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, t.getDni());
            pstmt.setString(2, t.getNombre());
            pstmt.setString(3, t.getApellidos());
            pstmt.setString(4, t.getDireccion());
            pstmt.setString(5, t.getTelefono());
            pstmt.setString(6, t.getPuesto());

            pstmt.executeUpdate();
            return true;

        } catch (SQLException e) {
            if (e.getErrorCode() == 1062) {
                return false;
            }
            throw new BDException("Error al insertar trabajador: " + e.getMessage());
        }
    }

    public static void insertar(ArrayList<Trabajador> trabajadores) throws BDException {
        for (Trabajador t : trabajadores) {
            boolean insertado = altaTrabajador(t);

            if (!insertado) {
                actualizarPorDni(t);
            }
        }
    }

    public static boolean actualizarPorDni(Trabajador t) throws BDException {
        String sql = "UPDATE empleados SET nombre=?, apellido=?, direccion=?, telefono=?, puesto=? WHERE dni=?";

        try (Connection conn = ConfigMySQL.abrirConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, t.getNombre());
            pstmt.setString(2, t.getApellidos());
            pstmt.setString(3, t.getDireccion());
            pstmt.setString(4, t.getTelefono());
            pstmt.setString(5, t.getPuesto());
            pstmt.setString(6, t.getDni());

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new BDException("Error al actualizar trabajador: " + e.getMessage());
        }
    }

    public static boolean bajaTrabajador(int id) throws BDException {
        String sql = "DELETE FROM empleados WHERE id = ?";

        try (Connection conn = ConfigMySQL.abrirConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new BDException("Error al eliminar trabajador: " + e.getMessage());
        }
    }

    public static boolean modificaTrabajador(Trabajador t) throws BDException {
        String sql = "UPDATE empleados SET dni=?, nombre=?, apellido=?, direccion=?, telefono=?, puesto=? WHERE id=?";

        try (Connection conn = ConfigMySQL.abrirConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, t.getDni());
            pstmt.setString(2, t.getNombre());
            pstmt.setString(3, t.getApellidos());
            pstmt.setString(4, t.getDireccion());
            pstmt.setString(5, t.getTelefono());
            pstmt.setString(6, t.getPuesto());
            pstmt.setInt(7, t.getIdentificador());

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new BDException("Error al actualizar trabajador: " + e.getMessage());
        }
    }

    public static String[][] listarTrabajadores() throws BDException {
        List<String[]> lista = new ArrayList<>();
        String sql = "SELECT * FROM empleados";

        try (Connection conn = ConfigMySQL.abrirConexion();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String[] fila = new String[7];
                fila[0] = String.valueOf(rs.getInt("id"));
                fila[1] = rs.getString("dni");
                fila[2] = rs.getString("nombre");
                fila[3] = rs.getString("apellido");
                fila[4] = rs.getString("direccion");
                fila[5] = rs.getString("telefono");
                fila[6] = rs.getString("puesto");
                lista.add(fila);
            }

        } catch (SQLException e) {
            throw new BDException("Error al listar trabajadores: " + e.getMessage());
        }

        return lista.toArray(new String[0][0]);
    }

    public static Trabajador buscarTrabajadorPorId(int id) throws BDException {
        String sql = "SELECT * FROM empleados WHERE id = ?";

        try (Connection conn = ConfigMySQL.abrirConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }
                return new Trabajador(
                        rs.getInt("id"),
                        rs.getString("dni"),
                        rs.getString("nombre"),
                        rs.getString("apellido"),
                        rs.getString("direccion"),
                        rs.getString("telefono"),
                        rs.getString("puesto")
                );
            }

        } catch (SQLException e) {
            throw new BDException("Error al buscar trabajador por id: " + e.getMessage());
        }
    }

    public static Trabajador buscarTrabajadorPorDni(String dni) throws BDException {
        String sql = "SELECT * FROM empleados WHERE dni = ?";

        try (Connection conn = ConfigMySQL.abrirConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, dni);
            try (ResultSet rs = stmt.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }
                return new Trabajador(
                        rs.getInt("id"),
                        rs.getString("dni"),
                        rs.getString("nombre"),
                        rs.getString("apellido"),
                        rs.getString("direccion"),
                        rs.getString("telefono"),
                        rs.getString("puesto")
                );
            }

        } catch (SQLException e) {
            throw new BDException("Error al buscar trabajador por DNI: " + e.getMessage());
        }
    }

    public static List<String> obtenerPuestos() throws BDException {
        List<String> puestos = new ArrayList<>();
        String sql = "SELECT DISTINCT puesto FROM empleados ORDER BY puesto";

        try (Connection conn = ConfigMySQL.abrirConexion();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                puestos.add(rs.getString("puesto"));
            }
            return puestos;

        } catch (SQLException e) {
            throw new BDException("Error al obtener puestos: " + e.getMessage());
        }
    }

    public static boolean validarDNI(String dni) {
        if (dni == null || dni.length() != 9) {
            return false;
        }

        String numeros = dni.substring(0, 8);
        char letra = Character.toUpperCase(dni.charAt(8));

        if (!numeros.matches("\\d{8}")) {
            return false;
        }

        String letrasValidas = "TRWAGMYFPDXBNJZSQVHLCKE";
        int numero = Integer.parseInt(numeros);
        char letraCorrecta = letrasValidas.charAt(numero % 23);

        return letra == letraCorrecta;
    }

    public static boolean validarTelefono(String telefono) {
        if (telefono == null) {
            return false;
        }

        telefono = telefono.replaceAll("[\\s-]", "");
        return telefono.matches("^(\\+34)?[6789]\\d{8}$");
    }

    public static void exportarACSV(String rutaArchivo) throws BDException {
        List<String[]> listaTrabajadores = new ArrayList<>();
        String sql = "SELECT * FROM empleados";

        try (Connection conn = ConfigMySQL.abrirConexion();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String[] fila = new String[7];
                fila[0] = String.valueOf(rs.getInt("id"));
                fila[1] = rs.getString("dni");
                fila[2] = rs.getString("nombre");
                fila[3] = rs.getString("apellido");
                fila[4] = rs.getString("direccion");
                fila[5] = rs.getString("telefono");
                fila[6] = rs.getString("puesto");
                listaTrabajadores.add(fila);
            }

        } catch (SQLException e) {
            throw new BDException("Error al listar trabajadores: " + e.getMessage());
        }

        try (FileWriter writer = new FileWriter(rutaArchivo)) {
            writer.append("Identificador,DNI,Nombre,Apellidos,Direccion,Telefono,Puesto\n");

            for (String[] trabajador : listaTrabajadores) {
                for (int i = 0; i < trabajador.length; i++) {
                    writer.append(trabajador[i]);
                    if (i < trabajador.length - 1) {
                        writer.append(",");
                    }
                }
                writer.append("\n");
            }

        } catch (IOException e) {
            throw new BDException("Error al escribir el archivo CSV: " + e.getMessage());
        }
    }

    public static void exportarTrabajadoresAJSON(String rutaArchivo) throws BDException {
        List<String[]> listaTrabajadores = new ArrayList<>();
        String sql = "SELECT * FROM empleados";

        try (Connection conn = ConfigMySQL.abrirConexion();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String[] fila = new String[7];
                fila[0] = String.valueOf(rs.getInt("id"));
                fila[1] = rs.getString("dni");
                fila[2] = rs.getString("nombre");
                fila[3] = rs.getString("apellido");
                fila[4] = rs.getString("direccion");
                fila[5] = rs.getString("telefono");
                fila[6] = rs.getString("puesto");
                listaTrabajadores.add(fila);
            }

        } catch (SQLException e) {
            throw new BDException("Error al listar trabajadores: " + e.getMessage());
        }

        StringBuilder json = new StringBuilder();
        json.append("[\n");

        for (int i = 0; i < listaTrabajadores.size(); i++) {
            String[] fila = listaTrabajadores.get(i);

            json.append("  {\n");
            json.append("    \"id\": ").append(fila[0]).append(",\n");
            json.append("    \"dni\": \"").append(fila[1]).append("\",\n");
            json.append("    \"nombre\": \"").append(fila[2]).append("\",\n");
            json.append("    \"apellido\": \"").append(fila[3]).append("\",\n");
            json.append("    \"direccion\": \"").append(fila[4]).append("\",\n");
            json.append("    \"telefono\": \"").append(fila[5]).append("\",\n");
            json.append("    \"puesto\": \"").append(fila[6]).append("\"\n");
            json.append("  }");

            if (i < listaTrabajadores.size() - 1) {
                json.append(",");
            }

            json.append("\n");
        }

        json.append("]");

        try (FileWriter writer = new FileWriter(rutaArchivo)) {
            writer.write(json.toString());

        } catch (IOException e) {
            throw new BDException("Error al exportar trabajadores a JSON: " + e.getMessage());
        }
    }
}
