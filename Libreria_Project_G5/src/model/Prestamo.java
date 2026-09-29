package model;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Map;

/**
 *
 * @author Joel
 */
public class Prestamo implements Serializable {

    private static final long serialVersionUID = 1L;

    private int id;
    private LocalDate fechaIni;
    private Map<Integer, LocalDate> libros; // idLibro,fechaFin
    private int idUsuario;

    public Prestamo() {
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

    // true si el libro ya fue devuelto (tiene fechaFin)
    public boolean libroDisponible(int idLibro) {
        return libros != null && libros.get(idLibro) != null;
    }
}
