package dao;

import exceptions.*;
import java.util.List;
import model.Libro;
/**
 *
 * @author Hodei.Torres
 */
public interface DaoLibro {

    public void insertar(Libro objeto) throws AccesoDatosException;     
        
    public Libro obtenerPorId(int id) throws AccesoDatosException,LibroNoEncontradoException;
        
    public List<Libro> obtenerTodosDispo() throws AccesoDatosException;
}

