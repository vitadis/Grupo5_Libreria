/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package main;

import exceptions.AccesoDatosException;
import java.util.List;
import java.util.Scanner;
import model.Genero;
import model.Libro;
import repository.AccesoLibro;
    

/**
 *
 * @author Hodei.Torres
 */
public class Patata {

    public static void Patata(String[] args) {
        Scanner sc = new Scanner(System.in);
        AccesoLibro dao = AccesoLibro.getInstance();
        boolean salir = false;

        while (!salir) {
            System.out.println("\n===== MENÚ LIBRERÍA =====");
            System.out.println("1. Insertar un nuevo libro");
            System.out.println("2. Ver libros disponibles");
            System.out.println("3. Salir");
            System.out.print("Elige una opción: ");

            String opcion = sc.nextLine().trim();

            switch (opcion) {
                case "1":
                    insertarLibro(sc, dao);
                    break;
                case "2":
                    verLibrosDisponibles(dao);
                    break;
                case "3":
                    salir = true;
                    System.out.println("Saliendo...");
                    break;
                default:
                    System.out.println("Opción no válida. Inténtalo de nuevo.");
            }
        }

        sc.close();
    }

    private static void insertarLibro(Scanner sc, AccesoLibro dao) {
        System.out.println("\n--- Nuevo libro ---");

        System.out.print("Título: ");
        String titulo = sc.nextLine().trim();

        System.out.print("Autor: ");
        String autor = sc.nextLine().trim();

        Genero genero = elegirGenero(sc);

        System.out.print("¿Disponible? (s/n): ");
        boolean disponible = sc.nextLine().trim().equalsIgnoreCase("s");

        System.out.print("Ruta del archivo (o déjalo en blanco): ");
        String ruta = sc.nextLine().trim();

        // el id se pasa a 0 porque lo genera la base de datos (AUTO_INCREMENT)
        Libro libro = new Libro(0, titulo, autor, genero, disponible, ruta);

        try {
            dao.insertar(libro);
            System.out.println("Libro insertado correctamente.");
        } catch (AccesoDatosException e) {
            System.out.println("Error al insertar el libro: " + e.getMessage());
        }
    }

    private static Genero elegirGenero(Scanner sc) {
        Genero[] generos = Genero.values();

        while (true) {
            System.out.println("Elige un género:");
            for (int i = 0; i < generos.length; i++) {
                System.out.println((i + 1) + ". " + generos[i]);
            }
            System.out.print("Opción: ");

            String entrada = sc.nextLine().trim();
            try {
                int indice = Integer.parseInt(entrada) - 1;
                if (indice >= 0 && indice < generos.length) {
                    return generos[indice];
                }
            } catch (NumberFormatException e) {
                // se ignora y se vuelve a pedir
            }
            System.out.println("Opción no válida, inténtalo de nuevo.");
        }
    }

    private static void verLibrosDisponibles(AccesoLibro dao) {
        System.out.println("\n--- Libros disponibles ---");

        try {
            List<Libro> libros = dao.obtenerTodosDispo();

            if (libros.isEmpty()) {
                System.out.println("No hay libros disponibles.");
            } else {
                for (Libro libro : libros) {
                    System.out.println(libro);
                }
            }

        } catch (AccesoDatosException e) {
            System.out.println("Error al obtener los libros disponibles: " + e.getMessage());
        }
    }
}
