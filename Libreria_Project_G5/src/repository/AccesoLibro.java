package repository;

import dao.DaoLibro;
import exceptions.AccesoDatosException;
import exceptions.LibroNoEncontradoException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import model.Genero;
import model.Libro;

/**
 *
 * @author Hodei.Torres
 */
public class AccesoLibro extends AccesoDataBase implements DaoLibro {

    // Constructor + siglenton
    private static AccesoLibro instancia;

    private AccesoLibro() {
        super();
    }

    public static AccesoLibro getInstance() {
        if (instancia == null) {
            instancia = new AccesoLibro();
        }
        return instancia;
    }

    // sentencias de base de datos
    private final String SQLLIBRONUEVO = Sentencias.LIBRO_NUEVO;
    private final String SQLLIBROPORID = Sentencias.LIBRO_POR_ID;
    private final String SQLLIBROSDISPO = Sentencias.LIBROS_DISPONIBLES;

    /**
     *
     * @param objeto
     * @throws exceptions.AccesoDatosException
     */
    @Override
    public void insertar(Libro objeto) throws AccesoDatosException {
        try (Connection con = getConnection(); 
            PreparedStatement ps = con.prepareStatement(SQLLIBRONUEVO)) {

            ps.setString(1, objeto.getTitulo());
            ps.setString(2, objeto.getAutor());
            ps.setString(3, objeto.getGenero().name());
            ps.setBoolean(4, objeto.isDisponible());
            ps.executeUpdate();
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
        try (Connection con = getConnection(); 
            PreparedStatement ps = con.prepareStatement(SQLLIBROPORID)) {

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
                rs.getInt("ID_LIBRO"),
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
        try (Connection con = getConnection(); 
            PreparedStatement ps = con.prepareStatement(SQLLIBROSDISPO); 
            ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                libros.add(mapLibro(rs));
            }

        } catch (SQLException ex) {
            throw new AccesoDatosException("Error al buscar los libros disponibles: " + ex.getMessage(), ex);
        }
        return libros;
    }
}
