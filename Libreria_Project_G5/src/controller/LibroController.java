package controller;

import java.util.List;

import dao.DaoLibro;
import exceptions.AccesoDatosException;
import model.Genero;
import model.Libro;
import repository.AccesoLibro;

/**
 *
 * @author Hodei
 */

public class LibroController {
    private static final DaoLibro daoLibro = AccesoLibro.getInstance();

    public void registrarLibro(String titulo, String autor, Genero genero, Boolean dispo) {
        Libro libro = new Libro();
        libro.setTitulo(titulo);
        libro.setAutor(autor);
        libro.setGenero(genero);
        libro.setDisponible(dispo);

        try {
            daoLibro.insertar(libro);
            System.out.println("Libro registrado correctamente.");
        } catch (AccesoDatosException e) {
            System.out.println("Error al registras el libro: " + e.getMessage());
        }
    }

    //METODO PARA MOSTRAS LOS LIBROS DISPONIBLES
    public static void mostrarLibroDispo() {
        try {
            List<Libro> libros = daoLibro.obtenerTodosDispo();
            if(libros.isEmpty()){
                System.out.println("No hay libros disponibles.");
            } else {
                System.out.println("Libros disponibles:");
                for (Libro libro : libros) {
                    System.out.println(libro);
                }
            }
        } catch (AccesoDatosException e) {
            System.out.println("Error al obtener los libros disponibles: " + e.getMessage());
        }
    }


}