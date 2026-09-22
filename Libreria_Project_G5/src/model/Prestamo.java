/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.time.LocalDate;
import java.util.Map;

/**
 *
 * @author Joel
 */
public class Prestamo {
    private int id;
    private LocalDate fechaIni;
    private LocalDate fechaFin;
    private Map<Integer,Boolean> libros; // idLibro,boolean
    private int idUsuario;

    public Prestamo(int id, LocalDate fechaIni, LocalDate fechaFin, Map<Integer, Boolean> libros, int idUsuario) {
        this.id = id;
        this.fechaIni = fechaIni;
        this.fechaFin = fechaFin;
        this.libros = libros;
        this.idUsuario = idUsuario;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LocalDate getFechaIni() {
        return fechaIni;
    }

    public void setFechaIni(LocalDate fechaIni) {
        this.fechaIni = fechaIni;
    }

    public LocalDate getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(LocalDate fechaFin) {
        this.fechaFin = fechaFin;
    }

    public Map<Integer, Boolean> getLibros() {
        return libros;
    }

    public void setLibros(Map<Integer, Boolean> libros) {
        this.libros = libros;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }
    
    
    
}
