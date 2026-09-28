package dao;

import java.util.List;

import exceptions.AccesoDatosException;
import model.Usuario;

public interface UsuarioDao {
    public void alta(Usuario user) throws AccesoDatosException;
    public Usuario obtenerUsuarioPorId(int id) throws AccesoDatosException;
}
