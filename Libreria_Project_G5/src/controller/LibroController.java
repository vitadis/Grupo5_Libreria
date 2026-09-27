/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;

import dao.DaoLibro;
import exceptions.AccesoDatosException;
import exceptions.LibroNoEncontradoException;
import model.Libro;
import repository.AccesoLibro;

/**
 *
 * @author anazk
 */
public class LibroController {

    private static LibroController instance;
    private final DaoLibro daoLibro;

    public LibroController() {
        this.daoLibro = AccesoLibro.getInstance();
    }

    public static LibroController getInstance() {
        if (instance == null) {
            instance = new LibroController();
        }
        return instance;
    }
    
    public Libro buscarLibroPorTitulo(String titulo) throws LibroNoEncontradoException, AccesoDatosException {
        return daoLibro.buscarLibroPorTitulo(titulo);
    }
}
