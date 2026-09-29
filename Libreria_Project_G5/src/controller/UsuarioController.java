/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;

import dao.UsuarioDao;
import exceptions.AccesoDatosException;
import exceptions.EmailInvalidoException;
import exceptions.TelefonoInvalidoException;
import model.Usuario;
import repository.AccesoUsuario;

/**
 *
 * @author Christian
 */
public class UsuarioController {
    private static final UsuarioDao daoUsuario = AccesoUsuario.getInstance();
    
    public void registrarUsuario(String nombre, String email, String telefono){
        Usuario user = new Usuario();
        user.setNombre(nombre);
        try {
            user.setEmail(email);
            user.setTelefono(telefono);
            daoUsuario.alta(user);
        } catch (EmailInvalidoException | TelefonoInvalidoException | AccesoDatosException ex) {
            System.out.println("Ha surgido un error al registrar el usuario: "+ex.getMessage());
        }
    }
}
