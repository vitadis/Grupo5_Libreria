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
    private Map<Integer, LocalDate> libros; // idLibro,fechaFin
    private int idUsuario;

    public Prestamo(){
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

    public Map<Integer, LocalDate> getLibros() {
        return libros;
    }

    public void setLibros(Map<Integer, LocalDate> libros) {
        this.libros = libros;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public boolean libroDisponible(int idLibro) {
        return libros.get(id) != null;
    }
}
