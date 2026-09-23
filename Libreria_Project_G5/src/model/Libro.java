
package model;

/**
 *
 * @author Hodei.Torres
 */
public class Libro {
    int id;
    String titulo;
    String autor;
    Genero genero;
    boolean disponible = false;
    String ruta;

    
    public Libro(int id, String titulo, String autor, Genero genero, boolean disponible, String ruta) {
        this.id = id;
        this.titulo = titulo;
        this.autor = autor;
        this.genero = genero;
        this.disponible = disponible;
        this.ruta = ruta;
    }

    public Libro() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
    
    @Override
    public String toString() {
        return "libro [id=" + id + ", titulo=" + titulo + ", autor=" + autor + ", genero=" + genero + ", disponible="
                + disponible + ", ruta=" + ruta + "]";
    }

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getTitulo() {
        return titulo;
    }
    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }
    public String getAutor() {
        return autor;
    }
    public void setAutor(String autor) {
        this.autor = autor;
    }
    public Genero getGenero() {
        return genero;
    }
    public void setGenero(Genero genero) {
        this.genero = genero;
    }
    public boolean isDisponible() {
        return disponible;
    }
    public void setDisponible(boolean disponible) {
        this.disponible = disponible;
    }
    public String getRuta() {
        return ruta;
    }
    public void setRuta(String ruta) {
        this.ruta = ruta;
    }

    
}
