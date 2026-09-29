package utilidades;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

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

}