/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import model.Libro;

/**
 *
 * @author anazk
 */
public interface DaoLibro {
    
    public void devolverLibroPorId(int idLibro);
    public Libro buscarLibroPorNombre(String nombre);
    public String buscarLibroPorId(int id);
    
}
