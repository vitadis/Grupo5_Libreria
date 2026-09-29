package repository;

import utilidades.Sentencias;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import dao.UsuarioDao;
import exceptions.AccesoDatosException;
import java.sql.ResultSet;
import model.Usuario;

/**
 *
 * @author Christian
 */
public class AccesoUsuario extends AccesoDataBase implements UsuarioDao {

    private static AccesoUsuario instancia;

    public static AccesoUsuario getInstance() {
        if (instancia == null) {
            instancia = new AccesoUsuario();
        }
        return instancia;
    }

    @Override
    public void alta(Usuario user) throws AccesoDatosException {
        try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(Sentencias.USUARIO_NUEVO)) {

            ps.setString(1, user.getNombre());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getTelefono());
            ps.executeUpdate(); // CAMBIO: executeQuery() -> executeUpdate()
        } catch (SQLException e) {
            throw new AccesoDatosException("Error al añadir un nuevo USUARIO:" + e.getMessage(), e);
        }
    }

    @Override
    public Usuario obtenerUsuarioPorId(int id) throws AccesoDatosException {
        try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(Sentencias.USUARIO_POR_ID)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapUsuario(rs);
                }
            }
        } catch (SQLException e) {
            throw new AccesoDatosException("Error al buscar un el USUARIO:" + e.getMessage(), e);
        }
        return null;
    }

    private Usuario mapUsuario(ResultSet rs) throws SQLException {
        return new Usuario(
                rs.getInt("ID_USUARIO"),
                rs.getString("NOMBRE"),
                rs.getString("EMAIL"),
                rs.getString("TELEFONO")
        );
    }
}
