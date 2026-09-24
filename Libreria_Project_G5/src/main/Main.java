/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package main;

import controller.PrestamoController;
import utilidades.Util;

/**
 *
 * @author Christian
 */
public class Main {

    
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
                case 0 ->
                    System.exit(0);
                case 3 ->
                    realizarPrestamo();
                case 7 ->
                    verHistorial();
                default ->
                    System.out.println("Agrega una opcion valida");
            }
        }
    }

    public static void realizarPrestamo() {

    }

    // HISTORIAL DE PRESTAMO DE UN LIBRO
    public static void verHistorial() {
        PrestamoController controlador = new PrestamoController();

        int idLibro = Util.leerInt("Escribe el id del libro: ");
        controlador.mostrarHistorialLibro(idLibro);
    }

}
