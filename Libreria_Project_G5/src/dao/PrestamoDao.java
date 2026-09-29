package dao;

import exceptions.AccesoDatosException;
import java.util.List;
import model.Prestamo;

/**
 *
 * Interface PrestamoDao
 *
 * @author Joel
 */
public interface PrestamoDao {

    // Crea el .dat vacio si no existe; true si lo creo
    boolean creardb();

    List<Prestamo> cargar() throws AccesoDatosException;

    // Guarda la lista completa con backup previo
    void guardar(List<Prestamo> prestamos) throws AccesoDatosException;

    void agregar(Prestamo prestamo) throws AccesoDatosException;

    // false si el id no existe
    boolean modificar(Prestamo prestamo) throws AccesoDatosException;

    boolean restaurarBackup();
}
