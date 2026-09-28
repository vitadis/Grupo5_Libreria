package repository;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;

import dao.Sentencias;
import dao.UsuarioDao;
import exceptions.AccesoDatosException;
import java.sql.ResultSet;
import model.Usuario;

public class AccesoUsuario implements UsuarioDao {
    private ResourceBundle configFile;
    private String urlBD;
    private String userBD;
    private String passwordBD;
    private static AccesoUsuario instancia;
    
    private final String SQLUSUARIONUEVO = Sentencias.USUARIO_NUEVO;
    private final String SQLUSUARIOPORID = Sentencias.USUARIO_POR_ID;   

    private AccesoUsuario(){
        this.configFile = ResourceBundle.getBundle("configGlobal");
        this.urlBD = this.configFile.getString("Conn");
        this.userBD = this.configFile.getString("DBUser");
        this.passwordBD = this.configFile.getString("DBPass");
    }

    public static AccesoUsuario getInstance(){
        if(instancia == null){
            instancia = new AccesoUsuario(); 
        }
        return instancia;
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(this.urlBD, this.userBD, this.passwordBD);
    }

    @Override
    public void alta(Usuario user) throws AccesoDatosException {
        try(Connection con = DriverManager.getConnection(urlBD, userBD, passwordBD); 
            PreparedStatement ps = con.prepareStatement(SQLUSUARIONUEVO)) {
            
            ps.setString(1, user.getNombre());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getTelefono());
            ps.executeQuery();
        } catch (SQLException e) {
            throw new AccesoDatosException("Error al añadir un nuevo USUARIO:" + e.getMessage(), e);
        }
    }

    @Override
    public Usuario obtenerUsuarioPorId(int id) throws AccesoDatosException {
        try(Connection con = DriverManager.getConnection(urlBD, userBD, passwordBD); 
            PreparedStatement ps = con.prepareStatement(SQLUSUARIOPORID)){
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()){
                if(rs.next()){
                    return mapUsuario(rs);
                }
            }
        }catch(SQLException e){
            throw new AccesoDatosException("Error al buscar un el USUARIO:" + e.getMessage(), e);
        }
        return null;
    }

    private Usuario mapUsuario(ResultSet rs) throws SQLException {
        return new Usuario(
                rs.getInt("ID"),
                rs.getString("NOMBRE"),
                rs.getString("EMAIL"),
                rs.getString("TELEFONO")
        );
    }
}
