/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import java.util.List;
import model.Prestamo;

/**
 *
 * @author Joel
 */
public interface PrestamoDao {

    public List<Prestamo> cargar();
    public void guardar(List<Prestamo> prestamos);
    public void agregar(Prestamo prestamo); 
    
}
