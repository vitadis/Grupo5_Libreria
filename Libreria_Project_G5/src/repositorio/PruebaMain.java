/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package repositorio;

/**
 *
 * @author Lidia
 */
public class PruebaMain {
     public static void main(String[] args){
          AccesoPrestamo objeto = AccesoPrestamo.getInstance("Joel");
          AccesoPrestamo objeto2 = AccesoPrestamo.getInstance("Hola");
          
          
          System.out.println(objeto.getRuta());
          System.out.println(objeto2.getRuta());
    }
}
