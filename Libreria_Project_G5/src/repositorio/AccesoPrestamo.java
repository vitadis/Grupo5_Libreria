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
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import model.Prestamo;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author Joel
 */
public class AccesoPrestamo implements PrestamoDao {
    // Agregar nuevamente el patron sigleton

    private String ruta;

    private static AccesoPrestamo instance;

    private AccesoPrestamo() {
        ruta = "prestamos.json";
    }

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
     * CARGAR JSON -> LIST
     *
     * FLUJO: tenemos una list -> abrimos el fichero mediante un br -> agregamos
     * el contenido dentro sb-contenido -> si el fichero esta vacio devolvemos
     * vacio, si no continuamos -> Creo un JsonArray, para agregar todo el
     * contenido -> recorremos todo el contenido del json y lo convertimos a un
     * Prestamo y guardamos dentro de la list -> finalmente retornamos la list.
     *
     * @return List<Prestamo.>
     */
    @Override
    public List<Prestamo> cargar() {

        List<Prestamo> prestamos = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(ruta))) {

            StringBuilder contenido = new StringBuilder();

            String linea;

            while ((linea = br.readLine()) != null) {
                contenido.append(linea);
            }

            if (contenido.length() == 0) {
                return prestamos;
            }

            JSONArray array = new JSONArray(contenido.toString());

            for (int i = 0; i < array.length(); i++) {

                JSONObject json = array.getJSONObject(i);

                Prestamo prestamo = convertirAPrestamo(json);

                prestamos.add(prestamo);
            }
        } catch (IOException e) {

            System.out.println("Error al cargar el JSON:(: " + e.getMessage());
        }
        return prestamos;
    }

    /**
     * GUARDAR PRESTAMOS:
     *
     * FLUJO: Creamos un JsonArray, recorremos dentro de la lista y lo guardamos
     * dentro de nuestro JsonObject, el JsOb lo guardamos dentro del JsonArray
     * -> finalmente abrimos un bw y escribimos el contenido del array.
     *
     * @param prestamos
     */
    @Override
    public void guardar(List<Prestamo> prestamos) {

        JSONArray array = new JSONArray();

        for (Prestamo prestamo : prestamos) {

            JSONObject json = convertirAJson(prestamo);

            array.put(json);
        }

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(ruta))) {

            bw.write(array.toString(4));

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
        List<Prestamo> prestamos = cargar();

        // Añadimos el nuevo
        prestamos.add(prestamo);

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
     * DE JSONOBJECT A PRESTAMO:
     *
     * FLUJO: Guardo todo en las variables, segun su key del objeto json. -> el
     * map lo guardo dentro del jsonObject, y creo otra variable map ->
     * finalmente recorro el jsonobject, y agrego los valores dentro del map que
     * tengo.
     */
    private Prestamo convertirAPrestamo(JSONObject json) {

        int id = json.getInt("id");

        LocalDate fechaIni = LocalDate.parse(json.getString("fechaIni"));

        LocalDate fechaFin = LocalDate.parse(json.getString("fechaFin"));

        int idUsuario = json.getInt("idUsuario");

        Map<Integer, Boolean> libros = new HashMap<>();

        JSONObject librosJson = json.getJSONObject("libros");

        for (String key : librosJson.keySet()) {

            int idLibro = Integer.parseInt(key);

            boolean estado = librosJson.getBoolean(key);
            libros.put(idLibro, estado);
        }
        return new Prestamo(id, fechaIni, fechaFin, libros, idUsuario);
    }

}
