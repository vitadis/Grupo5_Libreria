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

        // Una sola instancia para mantener la lista en memoria
        PrestamoController pc = PrestamoController.getInstance();

        String menu
                = "=======GESTION DE LIBRERIA=======\n"
                + "\t0. Salir\n"
                + "\t1. Registrar un libro\n"
                + "\t2. Registrar un usuario\n"
                + "\t3. Realizar el prestamo\n"
                + "\t4. Devolver un libro\n"
                + "\t5. Consultar libros disponible\n"
                + "\t6. Ver historial\n"
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
                    realizarPrestamo(pc);
                case 4 ->
                    devolverLibro(pc);
                case 5 ->
                    LibroController.mostrarLibroDispo();
                case 6 ->
                    verHistorial(pc);
                default ->
                    System.out.println("Agrega una opcion valida");
            }
        }
    }

    public static void realizarPrestamo(PrestamoController pc) {
        System.out.println("======= LIBROS DISPONIBLES =======");
        LibroController.mostrarLibroDispo();
        int numLibros = Util.leerInt("Cuantos libros son?");

        pc.hacerPrestamo(numLibros);
    }

    // Ver historial, por libro y por usuario
    public static void verHistorial(PrestamoController pc) {
        String menuHistorial
                = "======= HISTORIAL =======\n"
                + "\t1. Por libro\n"
                + "\t2. Por usuario\n"
                + "Seleccionna una opcion: ";

        int opcion = Util.leerInt(menuHistorial);
        switch (opcion) {
            case 1 -> {
                int idLibro = Util.leerInt("Escribe el id del libro: ");
                pc.mostrarHistorialLibro(idLibro);
            }
            case 2 -> {
                int idUsuario = Util.leerInt("Escribe el id del usuario: ");
                pc.mostrarHistorialUsuario(idUsuario);
            }
            default ->
                System.out.println("Agrega una opcion valida");
        }
    }

    //METODO PARA REGISTRAS UN NUEVO LIBRO
    public static void registrarLibro() {

        LibroController controlador = new LibroController();

        String titulo = Util.introducirCadena("Introduce el titulo del libro: ");
        String autor = Util.introducirCadena("Introduce el nombre del autor: ");
        Genero genero = Util.leerGenero();

        controlador.registrarLibro(titulo, autor, genero, true);
    }

    // Metodo para devolver un libro
    private static void devolverLibro(PrestamoController pc) {
        System.out.println("======= DEVOLVER LIBRO =======");
        int id = Util.leerInt("Introduce el id del libro");
        pc.devolverLibroPorIdLibro(id);
    }

    // REGISTRAR USUARIO 
    public static void registrarUsuario() {
        UsuarioController controlador = new UsuarioController();

        String nombre = Util.introducirCadena("Nombre: ");
        String email = Util.leerEmail("Email: ");
        String telefono = Util.leerTelefono("Telefono: ");

        controlador.registrarUsuario(nombre, email, telefono);
    }

}
