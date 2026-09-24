/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;

import dao.PrestamoDao;
import org.json.JSONArray;
import org.json.JSONObject;
import repository.AccesoPrestamo;

/**
 *
 * @author Joel
 */
public class PrestamoController {

    // objeto para interactura con el dao
    private final PrestamoDao dao = AccesoPrestamo.getInstance();

    // cargar el fichero solo cuando se modifique
    private boolean seModifico = false;

    private JSONArray cargarDatos;

    private JSONArray getCargarDatos() {
        if (seModifico || cargarDatos == null) {
            cargarDatos = dao.cargar();
            seModifico = false;
        }
        return cargarDatos;
    }

    // ============================================================================
    // ============================ METODOS CONTROLLER ============================
    // ============================================================================
    public void mostrarHistorialLibro(int id) {
        JSONArray prestamos = getCargarDatos();

        for (int i = 0; i < prestamos.length(); i++) {

        }

        prestamos.forEach( o -> {
            JSONObject prestamo = (JSONObject) o;
            JSONObject libros = prestamo.getJSONObject("libros");
            prestamo.remove("libros");

            for (String key : libros.keySet()) {
                int idLibro = Integer.parseInt(key);

                if (idLibro == id) {
                    // obtener los valores y enseñar
                    // IMPORTANTE: enseñar el titulo del libro, haciendo una consulta en la base de datos.
                    
                    StringBuilder sb = new StringBuilder();
                    sb.append("---------------------------------------------\n");
                    sb.append(prestamo.getInt("id")+"\n");
                    sb.append(prestamo.getString("fechaIni")+"\n");
                    sb.append(libros.getString(idLibro+"")+"\n");
                    sb.append(prestamo.getInt("idUsuario")+"\n");
                    sb.append("---------------------------------------------");

                    System.out.println(sb.toString());
                }

            }

            /*
            json.put("id", prestamo.getId());
            json.put("fechaIni", prestamo.getFechaIni().toString());
            json.put("fechaFin", prestamo.getFechaFin().toString());
            json.put("idUsuario", prestamo.getIdUsuario());
            
            
            
             */
        }
        );
        
    }
}
