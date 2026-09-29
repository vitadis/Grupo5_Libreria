package controller;

import dao.UsuarioDao;
import exceptions.AccesoDatosException;
import model.Usuario;
import repository.AccesoUsuario;

/**
 *
 * @author Christian
 */
public class UsuarioController {

    private static final UsuarioDao daoUsuario = AccesoUsuario.getInstance();

    public void registrarUsuario(String nombre, String email, String telefono) {
        Usuario user = new Usuario();
        user.setNombre(nombre);

        try {
            user.setEmail(email);
            user.setTelefono(telefono);
            daoUsuario.alta(user);

        } catch (AccesoDatosException ex) {
            System.out.println("Ha surgido un error al registrar el usuario: " + ex.getMessage());
        }
    }
}
