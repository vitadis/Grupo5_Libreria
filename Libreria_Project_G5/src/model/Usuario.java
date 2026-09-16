package model;

import exceptions.*;

/**
 *
 * @author Hodei.Torres
 */
public class Usuario {
    int id; 
    String nombre;
    String email;
    String telefono;


    public Usuario(int id, String nombre, String email, String telefono) {
        this.id = id;
        this.nombre = nombre;
        this.email = email;
        this.telefono = telefono;
    }

    @Override
    public String toString() {
        return "Usuario [id=" + id + ", nombre=" + nombre + ", email=" + email + ", telefono=" + telefono + "]";
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getEmail() {
        return email;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) throws TelefonoInvalidoException {
        if (telefono == null || !telefono.matches("\\d{9}")) {
            throw new TelefonoInvalidoException();
        }
        this.telefono = telefono;
    }

    public void setEmail(String email) throws EmailInvalidoException {
        if (email == null || !email.matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$")) {
            throw new EmailInvalidoException();
        }
        this.email = email;
    }
}

