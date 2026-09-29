/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package main;

import controller.LibroController;
import controller.PrestamoController;
import controller.UsuarioController;
import model.Genero;
import utilidades.Util;

/**
 *
 * @author Christian, Joel, Hodei.Torres, An
 */
public class Main {

    // CONTROLADOR
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // main menu
        mainMenu();
    }

    public static void mainMenu() {
        final PrestamoController CONTROLLER_PRESTAMO = new PrestamoController();

        String menu
                = "=======GESTION DE LIBRERIA=======\n"
                + "\t0. Salir\n"
                + "\t1. Registrar un libro\n"
                + "\t2. Registrar un usuario\n"
                + "\t3. Realizar el prestamo\n"
                + "\t4. Devolver un libro\n"
                + "\t5. Consultar libros disponible\n"
                + "\t6. Consultar prestamos de usuario\n"
                + "\t7. Ver historial\n"
                + "Seleccionna una opcion: ";

        while (true) {
            int opcion = Util.leerInt(menu);
            switch (opcion) {
                case 0 -> {
                    System.out.println("Adios, cerrando programa");
                    System.exit(0);
                }
                case 1 ->
                    registrarLibro();
                case 2 ->
                    registrarUsuario();
                case 3 ->
                    realizarPrestamo(CONTROLLER_PRESTAMO);
                case 4 ->
                    devolverLibro(CONTROLLER_PRESTAMO);
                case 5 ->
                    LibroController.mostrarLibroDispo();
                case 6 ->
                    buscarPrestamosDeUsuario();
                case 7 ->
                    verHistorial(CONTROLLER_PRESTAMO);
                default ->
                    System.out.println("Agrega una opcion valida");
            }
        }
    }

    public static void realizarPrestamo(PrestamoController pc) {
        System.out.println("======= LIBROS DISPONIBLES =======");
        // IMPORTANTE: llamar a los libros disponibles
        int numLibros = Util.leerInt("Cuantos libros son?");

        pc.hacerPrestamo(numLibros);
    }

    // HISTORIAL DE PRESTAMO DE UN LIBRO
    public static void verHistorial(PrestamoController pc) {
        int idLibro = Util.leerInt("Escribe el id del libro: ");
        pc.mostrarHistorialLibro(idLibro);
    }

    //METODO PARA REGISTRAS UN NUEVO LIBRO
    public static void registrarLibro() {

        LibroController controlador = new LibroController();

        String titulo = Util.introducirCadena("Introduce el titulo del libro: ");
        String autor = Util.introducirCadena("Introduce el nombre del autor: ");
        Genero genero = Util.leerGenero();

        controlador.registrarLibro(titulo, autor, genero, true);
    }
    
    //METODO PARA REGISTRAR UN NUEVO USUARIO
    public static void registrarUsuario(){
        UsuarioController controlador = new UsuarioController();
        
        String nombre = Util.introducirCadena("Introduce el nombre del usuario: ");
        String email = Util.introducirCadena("Introduce el email del usuario: ");
        String telefono = Util.introducirCadena("Introduce el telefono dle usuario: ");
        
        controlador.registrarUsuario(nombre, email, telefono);
    }
    
    //METODO PARA BUSCAR PRESTAMOS DE UN USUARIO
    public static void buscarPrestamosDeUsuario(){
        PrestamoController controlador = new PrestamoController();
        
        int id = Util.leerInt("Introduce el id del usuario:");
        
        controlador.mostrarHistorialUsuario(id);
    }

    //DEVOLVER LIBRO
    private static void devolverLibro(PrestamoController pc) {
        System.out.println("======= DEVOLVER LIBRO =======");
        int id = Util.leerInt("Introduce el id del libro");
        pc.devolverLibroPorIdLibro(id);
    }

}
