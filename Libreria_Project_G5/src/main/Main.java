/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package main;

import controller.LibroController;
import controller.PrestamoController;
import exceptions.AccesoDatosException;
import exceptions.LibroNoEncontradoException;
import model.Libro;
import utilidades.Util;

/**
 *
 * @author Christian, An, Hodei, Joel
 */
public class Main {

    // CONTROLADORES
    private static final PrestamoController CONTROLLER_PRESTAMO = new PrestamoController();
    private static final LibroController CONTROLLER_LIBRO = new LibroController();

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // main menu
        mainMenu();
    }

    public static void mainMenu() {
        String menu
                = "=======GESTION DE LIBRERIA=======\n"
                + "\t0. Salir\n"
                + "\t1. Registrar un libro\n"
                + "\t2. Registrar un usuario\n"
                + "\t3. Realizar el prestamo\n"
                + "\t4. Devolver un libro\n"
                + "\t5. Consultar libros disponible\n"
                + "\t6. Consultar prestamos\n"
                + "\t7. Ver historial\n"
                + "Seleccionna una opcion: ";

        while (true) {
            int opcion = Util.leerInt(menu);
            switch (opcion) {
                case 0 ->{
                    System.out.println("Adios, cerrando programa");
                    System.exit(0);
                }
                case 3 ->
                    realizarPrestamo();
                    case 4 -> 
                    devolverLibro();
                case 7 ->
                    verHistorial();
                default ->
                    System.out.println("Agrega una opcion valida");
            }
        }
    }

    public static void realizarPrestamo() {
        System.out.println("======= LIBROS DISPONIBLES =======");
        // IMPORTANTE: llamar a los libros disponibles
        int numLibros = Util.leerInt("Cuantos libros son?");
        
        CONTROLLER_PRESTAMO.hacerPrestamo(numLibros);
    }

    // HISTORIAL DE PRESTAMO DE UN LIBRO
    public static void verHistorial() {
        int idLibro = Util.leerInt("Escribe el id del libro: ");
        CONTROLLER_PRESTAMO.mostrarHistorialLibro(idLibro);
    }

    private static void devolverLibro() {
         System.out.println("======= DEVOLVER LIBRO =======");
         String titulo = Util.introducirCadena("Introduce el nombre del libro");
         try {
            Libro libro = CONTROLLER_LIBRO.buscarLibroPorTitulo(titulo);

            if (libro.isDisponible()) {
                System.out.println("El libro \"" + libro.getTitulo() + "\" ya esta disponible.");
                return;
            }

            CONTROLLER_PRESTAMO.devolverLibroPorIdLibro(libro.getId());

        } catch (LibroNoEncontradoException e) {
            System.out.println("No se encontro ningun libro con ese titulo.");
        } catch (AccesoDatosException e) {
            System.out.println("Error de acceso a datos: " + e.getMessage());
        }
    }

}
