package exceptions;

/**
 *
 * @author Hodei.Torres
 */
public class EmailInvalidoException extends Exception {
    public EmailInvalidoException() {
        super("El correo electrónico no es válido.");
    }

    public EmailInvalidoException(String mensaje) {
        super(mensaje);
    }
}
