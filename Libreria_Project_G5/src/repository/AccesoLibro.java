package repository;

import dao.DaoLibro;
import dao.Sentencias;
import exceptions.AccesoDatosException;
import exceptions.LibroNoEncontradoException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import model.Genero;
import model.Libro;

/**
 *
 * @author Hodei.Torres , An Azkona
 */
public class AccesoLibro implements DaoLibro {

    // configuracion para el acceso a la base de datos
    private ResourceBundle configFile;
    private String urlBD;
    private String userBD;
    private String passwordBD;
    private static AccesoLibro instancia;

    // sentencias de base de datos
    private final String SQLLIBRONUEVO = Sentencias.LIBRO_NUEVO;
    private final String SQLLIBROPORID = Sentencias.LIBRO_POR_ID;
    private final String SQLLIBROSDISPO = Sentencias.LIBROS_DISPONIBLES;
    private final String ACTUALIZAR_DISPONIBILIDAD_POR_ID = Sentencias.ACTUALIZAR_DISPONIBILIDAD_POR_ID;
    private final String BUSCAR_LIBRO_POR_NOMBRE = Sentencias.BUSCAR_LIBRO_POR_NOMBRE;
    

    private AccesoLibro() {
        this.configFile = ResourceBundle.getBundle("configGlobal");
        this.urlBD = this.configFile.getString("Conn");
        this.userBD = this.configFile.getString("DBUser");
        this.passwordBD = this.configFile.getString("DBPass");
    }

    public static AccesoLibro getInstance() {
        if (instancia == null) {
            instancia = new AccesoLibro();
        }
        return instancia;
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(this.urlBD, this.userBD, this.passwordBD);
    }

    /**
     *
     * @param objeto
     * @throws exceptions.AccesoDatosException
     */
    @Override
    public void insertar(Libro objeto) throws AccesoDatosException {
        try (Connection con = DriverManager.getConnection(urlBD, userBD, passwordBD); PreparedStatement ps = con.prepareStatement(SQLLIBRONUEVO)) {

            ps.setString(1, objeto.getTitulo());
            ps.setString(2, objeto.getAutor());
            ps.setString(3, objeto.getGenero().name());
            ps.setBoolean(4, objeto.isDisponible());
            ps.executeQuery();
        } catch (SQLException e) {
            throw new AccesoDatosException("Error al insertar el libro: " + e.getMessage(), e);
        }
    }

    /**
     *
     * @param id
     * @return
     */
    @Override
    public Libro obtenerPorId(int id) throws LibroNoEncontradoException, AccesoDatosException {
        try (Connection con = DriverManager.getConnection(urlBD, userBD, passwordBD); PreparedStatement ps = con.prepareStatement(SQLLIBROPORID)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapLibro(rs);
                }
            }
            throw new LibroNoEncontradoException("No existe ningun libro con esa ID.");

        } catch (SQLException ex) {
            throw new AccesoDatosException("Error al buscar la id del libro: " + ex.getMessage(), ex);
        }
    }

    private Libro mapLibro(ResultSet rs) throws SQLException {
        return new Libro(
                rs.getInt("ID"),
                rs.getString("TITULO"),
                rs.getString("AUTOR"),
                Genero.valueOf(rs.getString("GENERO")),
                rs.getBoolean("DISPONIBLE"),
                rs.getString("RUTA")
        );
    }

    /**
     *
     * @return @throws AccesoDatosException
     */
    @Override
    public List<Libro> obtenerTodosDispo() throws AccesoDatosException {
        List<Libro> libros = new ArrayList<>();
        try (Connection con = DriverManager.getConnection(urlBD, userBD, passwordBD); PreparedStatement ps = con.prepareStatement(SQLLIBROSDISPO); ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                libros.add(mapLibro(rs));
            }

        } catch (SQLException ex) {
            throw new AccesoDatosException("Error al buscar los libros disponibles: " + ex.getMessage(), ex);
        }
        return libros;

    }

    //An
    @Override
    public void devolverLibroPorId(int idLibro) throws AccesoDatosException {
        try (Connection con = getConnection(); PreparedStatement stmt = con.prepareStatement(ACTUALIZAR_DISPONIBILIDAD_POR_ID)) {
            stmt.setBoolean(1, true);
            stmt.setInt(2, idLibro);
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new AccesoDatosException("Error al devolver el libro: " + e.getMessage(), e);
        }
    }
    //An
    @Override
    public Libro buscarLibroPorTitulo(String nombre) throws AccesoDatosException, LibroNoEncontradoException{

        try (Connection con = getConnection(); PreparedStatement stmt = con.prepareStatement(BUSCAR_LIBRO_POR_NOMBRE)) {

            stmt.setString(1, nombre);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapLibro(rs);
                }
            }
            throw new LibroNoEncontradoException("No se encontró ningún libro con el título: " + nombre);

        } catch (SQLException e) {
            throw new AccesoDatosException("Error al consultar el libro por título: " + e.getMessage(), e);
        }
    }
    /*
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
     */
}
