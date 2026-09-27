/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package repository;

import dao.PrestamoDao;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import model.Prestamo;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author Joel
 */
public class AccesoPrestamo implements PrestamoDao {

    private String ruta;
    private String rutaBackup;

    private static AccesoPrestamo instance;

    private AccesoPrestamo() {
        ruta = "./src/dataBase/prestamos.json";
        rutaBackup = "./src/dataBase/prestamos_backup.json";
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

    public String getRutaBackup() {
        return rutaBackup;
    }

    public void setRutaBackup(String rutaBackup) {
        this.rutaBackup = rutaBackup;
    }

    // =======================================================================
    // ============================= METODOS DAO =============================
    // =======================================================================
    /**
     * CARGAR JSON -> JSONArray
     *
     * FLUJO: Creo objeto JSONArray -> try (leo el contenido del fichero y lo
     * parseo a JSONArray) -> catch (IOException o JSONException: informo el
     * error y relanzo una excepcion sin comprobar, para que la capa superior
     * (controller) decida si restaurar el backup, en vez de continuar
     * silenciosamente con datos vacios o corruptos).
     *
     * @return JSONArray
     */
    @Override
    public JSONArray cargar() {

        JSONArray prestamos;
        try (BufferedReader br = new BufferedReader(new FileReader(ruta))) {

            StringBuilder contenido = new StringBuilder();

            String linea;

            while ((linea = br.readLine()) != null) {
                contenido.append(linea);
            }

            prestamos = new JSONArray(contenido.toString());

        } catch (IOException | org.json.JSONException e) {

            System.out.println("Error al cargar el JSON :(: " + e.getMessage());
            throw new RuntimeException("No se pudo cargar el fichero de prestamos", e);
        }

        return prestamos;
    }

    /**
     * GUARDAR PRESTAMOS:
     *
     * FLUJO: Realizo un backup del json actual (por si la escritura falla a
     * mitad de camino) -> try (escribe el jsonArray) -> catch(excepcion:
     * restauro el backup para dejar el archivo en un estado consistente).
     *
     * @param prestamos
     */
    @Override
    public void guardar(JSONArray prestamos) {

        crearBackup();

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(ruta))) {

            bw.write(prestamos.toString(4));

        } catch (IOException e) {

            System.out.println("Error al guardar el JSON :(: " + e.getMessage());

            restaurarBackup();
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

        JSONArray prestamos = cargar();

        prestamos.put(convertirAJson(prestamo));

        guardar(prestamos);
    }

    /**
     * MODIFICAR PRESTAMO:
     *
     * FLUJO: compruebo que el jsonObject traiga id (si no, return false) ->
     * intento cargar la lista actual (si falla, return false, ya que no tengo
     * forma segura de saber si el id existe) -> recorro la lista buscando un
     * prestamo con ese mismo id -> si lo encuentro, lo reemplazo por el nuevo
     * jsonObject y guardo, return true -> si no existe ningun prestamo con esa
     * id, return false (controlado por condicional, no por excepcion).
     *
     * @param prestamo
     * @return boolean
     */
    @Override
    public boolean modificar(JSONObject prestamo) {

        if (!prestamo.has("id")) {
            return false;
        }

        int id = prestamo.getInt("id");

        JSONArray prestamos;
        try {
            prestamos = cargar();
        } catch (RuntimeException e) {
            return false;
        }

        boolean encontrado = false;

        for (int i = 0; i < prestamos.length(); i++) {
            JSONObject actual = prestamos.getJSONObject(i);

            if (actual.getInt("id") == id) {
                prestamos.put(i, prestamo);
                encontrado = true;
                break;
            }
        }

        if (!encontrado) {
            return false;
        }

        guardar(prestamos);

        return true;
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
        json.put("idUsuario", prestamo.getIdUsuario());

        JSONObject librosJson = new JSONObject();

        for (Integer idLibro : prestamo.getLibros().keySet()) {
            LocalDate fechaFin = prestamo.getLibros().get(idLibro);

            if (fechaFin != null) {
                librosJson.put(String.valueOf(idLibro), fechaFin.toString());
            } else {
                librosJson.put(String.valueOf(idLibro), JSONObject.NULL);
            }
        }

        json.put("libros", librosJson);

        return json;
    }

    /**
     * CREAR JSON INICIAL:
     *
     * FLUJO: try (si existe: return -> try bw) catch(excepcion).
     *
     * @return boolean
     */
    @Override
    public boolean creardb() {
        try {
            java.io.File archivo = new java.io.File(ruta);
            if (archivo.exists()) {
                return false;
            }
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(ruta))) {

                bw.write("[]");
            }
        } catch (IOException e) {
            return false;
        }
        return true;
    }

    /**
     * CREAR BACKUP:
     *
     * FLUJO: try (si el archivo original no existe: return false, no hay nada
     * que respaldar -> copio el archivo original hacia la ruta de backup,
     * reemplazando el backup anterior si existe -> return true) catch
     * (excepcion: return false).
     *
     * @return boolean
     */
    public boolean crearBackup() {
        try {
            File original = new File(ruta);

            if (!original.exists()) {
                return false;
            }

            Path origen = Paths.get(ruta);
            Path destino = Paths.get(rutaBackup);

            Files.copy(origen, destino, StandardCopyOption.REPLACE_EXISTING);

        } catch (IOException e) {
            System.out.println("Error al crear el backup :(: " + e.getMessage());
            return false;
        }
        return true;
    }

    /**
     * RESTAURAR BACKUP:
     *
     * FLUJO: try (si el backup no existe: return false, no hay nada que
     * restaurar -> copio el backup hacia la ruta original, reemplazando el
     * archivo que quedo a medio escribir -> return true) catch (excepcion:
     * return false).
     *
     * @return boolean
     */
    public boolean restaurarBackup() {
        try {
            File backup = new File(rutaBackup);

            if (!backup.exists()) {
                return false;
            }

            Path origen = Paths.get(rutaBackup);
            Path destino = Paths.get(ruta);

            Files.copy(origen, destino, StandardCopyOption.REPLACE_EXISTING);

        } catch (IOException e) {
            System.out.println("Error al restaurar el backup :(: " + e.getMessage());
            return false;
        }
        return true;
    }

}
