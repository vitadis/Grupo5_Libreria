package utilidades;

import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import model.Genero;

public class Util {

    public static int leerInt(String mensaje) {
        int num = 0;
        boolean error;
        do {
            error = false;
            try {
                System.out.println(mensaje);
                num = Integer.parseInt(introducirCadena());
            } catch (NumberFormatException e) {
                System.out.println("Valor no numérico. Introduce de nuevo:");
                error = true;
            }
        } while (error);
        return num;
    }

    public static int leerInt(String message, int min, int max) {
        int num = 0;
        boolean error;
        System.out.println(message);
        do {
            error = false;
            try {
                num = Integer.parseInt(introducirCadena());

            } catch (NumberFormatException e) {
                System.out.println("Valor no numérico. Introduce de nuevo:");
                error = true;
                num = min;
            }
            if (num < min || num > max) {
                System.out.println("Número fuera de rango, introduce número entre " + min + " y " + max + ": ");
                error = true;
            }
        } while (error);
        return num;
    }

    public static String introducirCadena() {
        String cadena = "";
        boolean error;
        InputStreamReader entrada = new InputStreamReader(System.in);
        BufferedReader teclado = new BufferedReader(entrada);
        do {
            error = false;
            try {
                cadena = teclado.readLine();
            } catch (IOException e) {
                System.out.println("Error en la entrada de datos");
                error = true;
            }
        } while (error);
        return cadena;
    }

    public static String introducirCadena(String mensaje) {
        String cadena = "";
        boolean error;
        InputStreamReader entrada = new InputStreamReader(System.in);
        BufferedReader teclado = new BufferedReader(entrada);
        do {
            error = false;
            try {
                System.out.println(mensaje);
                cadena = teclado.readLine();
            } catch (IOException e) {
                System.out.println("Error en la entrada de datos");
                error = true;
            }
        } while (error);
        return cadena;
    }

    public static Genero leerGenero() {
        Genero[] generosLista = generos();
        int opcion = Util.leerInt("Introduce el numero de opcion: ", 1, generosLista.length);

        return generosLista[opcion - 1];
    }

    private static Genero[] generos() {
        Genero[] generos = Genero.values();
        System.out.println("Elige un genero: ");
        for (int i = 0; i < generos.length; i++) {
            System.out.println((i + 1) + ". " + generos[i]);
        }

        return generos;
    }

    // Abrir imagen
    public static void verPortada(String ruta) {
        File img = new File(ruta);
        if (!img.isFile()) {
            System.err.println("La imagen no existe: " + img.getPath());
            return;
        }

        Desktop desktop = Desktop.getDesktop();
        try {
            desktop.open(img);
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }

    }

    // formato del email
    public static String leerEmail(String mensaje) {
        String email;
        boolean error;

        do {
            error = false;
            System.out.println(mensaje);
            email = introducirCadena();

            if (!email.matches("^[\\w.+-]+@[\\w.-]+\\.[a-zA-Z]{2,}$")) {
                System.out.println("Email no válido. Introduce un correo correcto.");
                error = true;
            }

        } while (error);

        return email;
    }

    public static String leerTelefono(String mensaje) {
        String telefono;
        boolean error;

        do {
            error = false;
            telefono = introducirCadena(mensaje);

            if (!telefono.matches("^\\d{9}$")) {
                System.out.println("Formato telefono incorrecto, son 9 digitos");
                error = true;
            }

        } while (error);

        return telefono;
    }
}
