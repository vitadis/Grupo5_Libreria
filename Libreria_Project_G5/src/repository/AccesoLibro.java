package repository;

import utilidades.Sentencias;
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
import dao.LibroDao;

/**
 *
 * @author Hodei.Torres
 */
public class AccesoLibro extends AccesoDataBase implements LibroDao {

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
    
    /**
     *
     * @param objeto
     * @throws exceptions.AccesoDatosException
     */
    @Override
    public void insertar(Libro objeto) throws AccesoDatosException {
        try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(Sentencias.LIBRO_NUEVO)) {

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
        try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(Sentencias.LIBRO_POR_ID)) {

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
        try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(Sentencias.LIBROS_DISPONIBLES); ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                libros.add(mapLibro(rs));
            }

        } catch (SQLException ex) {
            throw new AccesoDatosException("Error al buscar los libros disponibles: " + ex.getMessage(), ex);
        }
        return libros;
    }

    /**
     * @param libro
     * @throws LibroNoEncontradoException
     * @throws AccesoDatosException
     * 
     * @author Joel
     * 
     */
    @Override
    public void modificar(Libro libro) throws LibroNoEncontradoException, AccesoDatosException {
        if (libro.getGenero() == null) {
            throw new AccesoDatosException("El género del libro es obligatorio");
        }

        try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(Sentencias.LIBRO_MODIFICAR)) {

            ps.setString(1, libro.getTitulo());
            ps.setString(2, libro.getAutor());
            ps.setString(3, libro.getGenero().name());
            ps.setBoolean(4, libro.isDisponible());
            ps.setString(5, libro.getRuta());
            ps.setInt(6, libro.getId());

            int filas = ps.executeUpdate();
            if (filas == 0) {
                throw new LibroNoEncontradoException("No existe ningún libro con el ID " + libro.getId());
            }

        } catch (SQLException e) {
            throw new AccesoDatosException("Error al modificar el libro: " + e.getMessage(), e);
        }
    }
}
