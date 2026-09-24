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
    
    public void devolverLibroPorId(int idLibro);
    
    public Libro buscarLibroPorNombre(String nombre);
    
    public String buscarLibroPorId(int id);
}

