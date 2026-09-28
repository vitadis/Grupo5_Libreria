package repository;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ResourceBundle;

/**
 * Clase base para los accesos a la base de datos. Carga la configuración y
 * proporciona conexiones a las clases hijas.
 *
 * @author Joel
 */
public abstract class AccesoDataBase {

    private static final String CONFIG_BUNDLE = "utilidades.configGlobal";

    private final String urlBD;
    private final String userBD;
    private final String passwordBD;

    protected AccesoDataBase() {
        ResourceBundle config = ResourceBundle.getBundle(CONFIG_BUNDLE);
        this.urlBD = config.getString("Conn");
        this.userBD = config.getString("DBUser");
        this.passwordBD = config.getString("DBPass");
    }

    protected Connection getConnection() throws SQLException {
        return DriverManager.getConnection(urlBD, userBD, passwordBD);
    }
}
