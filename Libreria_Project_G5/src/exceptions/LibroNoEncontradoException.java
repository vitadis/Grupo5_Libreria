/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package exceptions;

/**
 *
 * @author Hodei.Torres
 */
public class LibroNoEncontradoException extends Exception {
 
    public LibroNoEncontradoException(String mensaje) {
        super(mensaje);
    }
 
    public LibroNoEncontradoException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
