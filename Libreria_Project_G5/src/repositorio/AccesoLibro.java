package repositorio;

import dao.DaoLibro;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
import model.Libro;

public class AccesoLibro implements DaoLibro {

    private final String urlDB;
    private final String userDB;
    private final String passwordDB;

    // Singleton 
    private static AccesoLibro instance;

    // Consultas SQL
    private static final String ACTUALIZAR_DISPONIBILIDAD_POR_ID = 
            "UPDATE LIBRO SET DISPONIBLE = ? WHERE ID_LIBRO = ?";
    private static final String BUSCAR_LIBRO_POR_NOMBRE = 
            "SELECT * FROM LIBRO WHERE TITULO = ?";
    private static final String BUSCAR_NOMBRE_POR_ID = 
            "SELECT TITULO FROM LIBRO WHERE ID_LIBRO = ?";

    private AccesoLibro() {
        ResourceBundle configFile = ResourceBundle.getBundle("utilidades.configFile");
        this.urlDB = configFile.getString("Conn");
        this.userDB = configFile.getString("DBUser");
        this.passwordDB = configFile.getString("DBPass");
    }

    public static AccesoLibro getInstance() {
        if (instance == null) {
            instance = new AccesoLibro();
        }
        return instance;
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(this.urlDB, this.userDB, this.passwordDB);
    }

    @Override
    public void devolverLibroPorId(int idLibro) {
        try (Connection con = getConnection();
             PreparedStatement stmt = con.prepareStatement(ACTUALIZAR_DISPONIBILIDAD_POR_ID)) {
            stmt.setBoolean(1, true); 
            stmt.setInt(2, idLibro);
            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Libro buscarLibroPorNombre(String nombre) {
        Libro libro = null;

        try (Connection con = getConnection();
             PreparedStatement stmt = con.prepareStatement(BUSCAR_LIBRO_POR_NOMBRE)) {

            stmt.setString(1, nombre);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    libro = new Libro();
                    libro.setId(rs.getInt("ID_LIBRO"));
                    libro.setTitulo(rs.getString("NOMBRE"));
                    libro.setDisponible(rs.getBoolean("DISPONIBLE"));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return libro;
    }

    @Override
    public String buscarLibroPorId(int id) {
        String nombreLibro = null;

        try (Connection con = getConnection();
             PreparedStatement stmt = con.prepareStatement(BUSCAR_NOMBRE_POR_ID)) {

            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    nombreLibro = rs.getString("NOMBRE");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return nombreLibro;
    }
}