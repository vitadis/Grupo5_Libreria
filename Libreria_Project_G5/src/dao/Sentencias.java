/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

/**
 *
 * @author Hodei.Torres, An Azkona
 */
public class Sentencias {

    // SENTENCIAS PARA METER NUEVOS LIBROS EN LA BASE DE DATOS
    public static String LIBRO_NUEVO = "INSERT INTO LIBRO (TITULO, AUTOR, GENERO, DISPONIBLE)";
    // SENTENCIA PARA OBTENER UN LIBRO POR SU ID
    public static String LIBRO_POR_ID = "SELECT ID, TITULO, AUTOR, GENERO, DISPONIBLE, RUTA FROM LIBRO WHERE ID = ?";
    // SENTENCIA PARA OBTENER TODOS LOS LIBROS DISPONIBLES
    public static String LIBROS_DISPONIBLES = "SELECT ID, TITULO, AUTOR, GENERO, DISPONIBLE, RUTA FROM LIBRO WHERE DISPONIBLE = TRUE";
    //SENTENCIA PARA ACTUALIZAR LA DISPONIBILIDAD DE UN LIBRO MEDIANTE SU ID (An)
    public static final String ACTUALIZAR_DISPONIBILIDAD_POR_ID = "UPDATE LIBRO SET DISPONIBLE = ? WHERE ID_LIBRO = ?";
    
}
