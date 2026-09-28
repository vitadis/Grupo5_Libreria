/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package utilidades;

/**
 *
 * @author Hodei.Torres
 */
public class Sentencias {

    // INSERTAR NUEVO LIBRO
    public static final String LIBRO_NUEVO = "INSERT INTO LIBRO (TITULO, AUTOR, GENERO, DISPONIBLE) VALUES (?, ?, ?, ?)";
    // SELECCIONAR LIBRO POR ID
    public static String LIBRO_POR_ID = "SELECT * FROM LIBRO WHERE ID_LIBRO = ?";
    // CONSULTAR LIBROS DISPONIBLES
    public static String LIBROS_DISPONIBLES = "SELECT * FROM LIBRO WHERE DISPONIBLE = TRUE";
    // INSERTAR NUEVO USUARIO
    public static final String USUARIO_NUEVO = "INSERT INTO USUARIO (NOMBRE, EMAIL, TELEFONO) VALUES (?, ?, ?)";
    // SELECCIONAR USUARIO POR ID
    public static final String USUARIO_POR_ID = "SELECT * FROM USUARIO WHERE ID_USUARIO = ?";
}
