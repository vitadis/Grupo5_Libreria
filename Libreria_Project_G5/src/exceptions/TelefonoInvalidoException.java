
package exceptions;

/**
 *
 * @author Hodei.Torres
 */
public class TelefonoInvalidoException extends Exception {
    public TelefonoInvalidoException() {
        super("El teléfono debe tener exactamente 9 dígitos.");
    }

    public TelefonoInvalidoException(String mensaje) {
        super(mensaje);
    }
}

