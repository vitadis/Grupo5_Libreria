/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package repositorio;

import dao.PrestamoDao;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import model.Prestamo;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author Joel
 */
public class AccesoPrestamo implements PrestamoDao {

    private String ruta;

    private static AccesoPrestamo instance;

    private AccesoPrestamo() {
        ruta = "prestamos.json";
        crearJSONInicial();
    }

    // PATRON SINGLETON
    public static AccesoPrestamo getInstance() {
        if (instance == null) {
            instance = new AccesoPrestamo();
        }
        return instance;
    }

    public String getRuta() {
        return ruta;
    }

    public void setRuta(String ruta) {
        this.ruta = ruta;
    }

    // =======================================================================
    // ============================= METODOS DAO =============================
    // =======================================================================
    /**
     * CARGAR JSON -> JSONArray
     *
     * FLUJO: Creo objeto JSONArray -> try (almaceno el contenido dentro del
     * objeto JSONArray) catch (excepcion).
     *
     * @return JSONArray
     */
    @Override
    public JSONArray cargar() {

        JSONArray prestamos = new JSONArray();
        try (BufferedReader br = new BufferedReader(new FileReader(ruta))) {

            StringBuilder contenido = new StringBuilder();

            String linea;

            while ((linea = br.readLine()) != null) {
                contenido.append(linea);
            }

            prestamos = new JSONArray(contenido.toString());

        } catch (IOException e) {

            System.out.println("Error al cargar el JSON :(: " + e.getMessage());
        }

        return prestamos;
    }

    /**
     * GUARDAR PRESTAMOS:
     *
     * FLUJO: try (escribe el jsonArray), catch(excepcion).
     *
     * @param prestamos
     */
    @Override
    public void guardar(JSONArray prestamos) {

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(ruta))) {

            bw.write(prestamos.toString(4));

        } catch (IOException e) {

            System.out.println("Error al guardar el JSON :(: " + e.getMessage());

        }
    }

    /**
     * AGREGAR:
     *
     * FLUJO: Cargo la lista, agrego el nuevo prestamo y llamo al metodo
     * guardar.
     *
     * @param prestamo
     */
    @Override
    public void agregar(Prestamo prestamo) {

        // Cargamos los prestamos que ya existen
        JSONArray prestamos = cargar();

        // Añadimos el nuevo
        prestamos.put(convertirAJson(prestamo));

        // Guardamos la lista completa
        guardar(prestamos);
    }

    // =======================================================================
    // ======================== METODOS AUXILIARES ===========================
    // =======================================================================
    /**
     * DE PRESTAMO A JSONOBJECT:
     *
     * FLUJO: agrego cada campo dentro de mi json -> para el map<int,boolean>
     * recorro en un foreach del map, y agrego todo dentro de librosJSON (su
     * contenido seria un map de id_Libro : boolean) -> finalmente lo agrego
     * dentro del json con la clave de libros.
     */
    private JSONObject convertirAJson(Prestamo prestamo) {

        JSONObject json = new JSONObject();

        json.put("id", prestamo.getId());
        json.put("fechaIni", prestamo.getFechaIni().toString());
        json.put("fechaFin", prestamo.getFechaFin().toString());
        json.put("idUsuario", prestamo.getIdUsuario());

        JSONObject librosJson = new JSONObject();

        for (Integer idLibro : prestamo.getLibros().keySet()) {
            boolean estado = prestamo.getLibros().get(idLibro);

            librosJson.put(String.valueOf(idLibro), estado);
        }

        json.put("libros", librosJson);

        return json;
    }

    /**
     * CREAR JSON INICIAL:
     * 
     * FLUJO: try (si existe: return -> try bw) catch(excepcion).
     *
     */
    private void crearJSONInicial() {

        try {
            java.io.File archivo = new java.io.File(ruta);

            if (archivo.exists()) {
                return;
            }

            try (BufferedWriter bw = new BufferedWriter(new FileWriter(ruta))) {

                bw.write("[]");

            }
        } catch (IOException e) {

            System.out.println("Error al crear el JSON inicial :(: " + e.getMessage());

        }
    }

}
