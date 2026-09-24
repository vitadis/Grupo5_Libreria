/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package exceptions;
 
/**
 * 
 *
 * @author Hodei.Torres
 */

// Excepciones para controlar los errores producidos en el acceso a la base de datos.
public class AccesoDatosException extends Exception {
 
    public AccesoDatosException(String mensaje) {
        super(mensaje);
    }
 
    public AccesoDatosException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
