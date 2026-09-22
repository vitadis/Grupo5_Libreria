/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package repositorio;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import org.json.JSONObject;
import dao.PrestamoDao;

/**
 *
 * @author Joel
 */
public class AccesoPrestamo implements PrestamoDao {

    private static AccesoPrestamo instance;
    
    private String ruta;

    private AccesoPrestamo(String ruta) {
        this.ruta = ruta;
    }

    public String getRuta() {
        return ruta;
    }

    public void setRuta(String ruta) {
        this.ruta = ruta;
    }
    
    //======================================================
    //================== METODO DE INSTACIA ================
    //======================================================
    public static AccesoPrestamo getInstance(String ruta){
        if(instance == null)
            instance = new AccesoPrestamo(ruta);
        return instance;
    }
    
    
    
    

    //======================================================
    //===================== METODOS DAO ====================
    //======================================================
    /**
     * CARGAR JSON: Cargamos con el formato json y retornamos un JsonObject del
     * fichero json.
     *
     * FLUJO: try donde habrimos el buffer --> creamos dos variable,
     * String-Linea y un Stringbuilder, para no modificar el formato json que
     * tenemos. --> while, mientras linea no sea null. --> cogemos todos los
     * datos en Stringbuilder y lo guardamos en un jsonobject --> Al final
     * retornamos nuestro objeto.
     *
     * Si ocurre la excepción al momento de leer el archivo, mostramos el
     * mensaje "se gestionará en gestión de excepciones" --> al final retornara
     * un objeto vacio.
     *
     *
     * @return JSONObject
     */
    @Override
    public JSONObject cargar() {

        try (BufferedReader br = new BufferedReader(new FileReader(ruta))) {

            String linea;
            StringBuilder contenido = new StringBuilder();

            while ((linea = br.readLine()) != null) {
                contenido.append(linea);
            }

            return new JSONObject(contenido.toString());

        } catch (IOException e) {
            System.out.println("Error al leer el JSON :( :" + e.getMessage());
        }

        return new JSONObject();
    }

    /**
     * GUARDAR JSON: Agrega el parametro del json que quieres que modifique.
     *
     * FLUJO: try de bw --> escribo todo el objeto json, dentro del fichero.
     * IMPORTANTE: el parametro que agrego en el toString del objeto es la
     * identación (los espacios).
     *
     * Si ocurre una excepcion al agregar --> mensaje de error sin mas :(
     *
     * @param json
     */
    @Override
    public void guardar(JSONObject json) {

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(ruta))) {

            bw.write(json.toString(4));

        } catch (IOException e) {

            System.out.println("Error al guardar el JSON :((: " + e.getMessage());

        }
    }

}
