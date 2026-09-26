/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;

import dao.DaoLibro;
import dao.PrestamoDao;
import exceptions.AccesoDatosException;
import exceptions.LibroNoEncontradoException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import model.Libro;
import model.Prestamo;
import org.json.JSONArray;
import org.json.JSONObject;
import repository.AccesoLibro;
import repository.AccesoPrestamo;

/**
 *
 * @author Joel
 */
public class PrestamoController {

    // Singleton Controller
    private static PrestamoController instance;

    // objeto para interactura con el dao
    private final PrestamoDao dao = AccesoPrestamo.getInstance();
    private final DaoLibro daoLibro = AccesoLibro.getInstance();

    // referencia al repositorio concreto, solo para poder invocar
    // crearBackup()/restaurarBackup(), que no forman parte del contrato
    // generico de PrestamoDao
    private final AccesoPrestamo repositorio = (AccesoPrestamo) dao;

    // cargar el fichero solo cuando se modifique
    private boolean seModifico = false;

    private JSONArray prestamos;

    // Constructor
    public PrestamoController() {
        init();
    }

    public static PrestamoController getInstance() {
        if (instance == null) {
            instance = new PrestamoController();
        }
        return instance;
    }

    /**
     * CARGAR DATOS:
     *
     * FLUJO: si ya estaban cargados y no hubo cambios -> no hago nada, return
     * true (evita relecturas innecesarias) -> try (cargo desde el dao) catch
     * (RuntimeException: el fichero esta corrupto o no se pudo leer, intento
     * restaurar el ultimo backup -> si se restaura, reintento la carga una
     * unica vez) -> si nada de esto funciona, dejo prestamos como una lista
     * vacia y devuelvo false, sin mostrarle al usuario detalles tecnicos.
     *
     * @return boolean si los datos quedaron disponibles para trabajar
     */
    private boolean cargarDatosMethod() {
        if (!seModifico && prestamos != null) {
            return true;
        }

        try {
            prestamos = dao.cargar();
            seModifico = false;
            return true;

        } catch (RuntimeException e) {

            if (repositorio.restaurarBackup()) {
                try {
                    prestamos = dao.cargar();
                    seModifico = false;
                    System.out.println("Se detecto un problema con los datos y se restauro la ultima copia de seguridad.");
                    return true;
                } catch (RuntimeException ex) {

                }
            }
        }

        prestamos = new JSONArray();
        System.out.println("No se pudieron recuperar los prestamos en este momento. Intentelo mas tarde.");
        return false;
    }

    // ============================================================================
    // ============================ METODOS CONTROLLER ============================
    // ============================================================================
    /**
     * MODIFICAR HISTORIAL LIBRO: FLUJO: ASEGURO QUE LOS DATOS ESTEN CARGADOS
     * (SI NO, AVISO Y TERMINO) -> OBTENGO LOS PRESTAMOS -> FOR DE LOS PRESTAMOS
     * -> FOREACH DEL LIBRO POR LA KEY -> CONDICION SI ES EL ID_LIBRO (AGREGO
     * LOS OBJETOS EN STRINGBUILDER, LO IMPRIMO)
     *
     * @param idLibro
     */
    public void mostrarHistorialLibro(int idLibro) {

        if (!cargarDatosMethod()) {
            return;
        }

        prestamos.forEach(o -> {
            JSONObject prestamo = (JSONObject) o;
            JSONObject libros = prestamo.getJSONObject("libros");

            for (String key : libros.keySet()) {
                int idL = Integer.parseInt(key);

                if (idL == idLibro) {

                    Libro libro;

                    try {
                        libro = daoLibro.obtenerPorId(idLibro);
                    } catch (LibroNoEncontradoException e) {
                        System.out.println("No se encontró el libro con id " + idLibro);
                        return;
                    } catch (AccesoDatosException e) {
                        System.out.println("Error al acceder a los datos: " + e.getMessage());
                        return;
                    }

                    System.out.println("========== " + libro.getTitulo() + " ==========");

                    StringBuilder sb = new StringBuilder();
                    sb.append("---------------------------------------------\n");
                    sb.append(prestamo.getInt("id")).append("\n");
                    sb.append(prestamo.getString("fechaIni")).append("\n");
                    sb.append(JSONObject.NULL.equals(libros.opt(idLibro + "")) ? "Todavia no entregado" : libros.opt(idLibro + "")).append("\n");
                    sb.append(prestamo.getInt("idUsuario")).append("\n");
                    sb.append("---------------------------------------------");

                    System.out.println(sb.toString());
                }
            }
        }
        );
    }

    /**
     * HACE UN PRESTAMO:
     *
     * FLUJO: (si el id es <= 0) -> terminar (aseguro que los datos esten
     * cargados, si no, aviso y termino, para no generar un prestamo con datos
     * vacios o desactualizados) (for de prestamos -> comprobar que existe el
     * libro, y que no este pillado -> al momento de guardar en el map,
     * modificar el getDisponible del libro en la base de datos) (genero el id
     * de forma segura, si falla, aviso y termino sin guardar).
     *
     * @param numLibros
     */
    public void hacerPrestamo(int numLibros) {
        if (numLibros <= 0) {
            System.out.println("No se realizo ningun libro");
            return;
        }

        if (!cargarDatosMethod()) {
            System.out.println("No se puede realizar el prestamo en este momento.");
            return;
        }

        Prestamo prestamo = new Prestamo();

        Map<Integer, LocalDate> libros = new HashMap<>();

        for (int i = 0; i < numLibros; i++) {
            int idLibro = utilidades.Util.leerInt("Id Libro: ");

            Libro libro = null;

            try {
                libro = daoLibro.obtenerPorId(idLibro);
            } catch (LibroNoEncontradoException e) {
                System.out.println("No se encontró el libro con id " + idLibro);
                continue;
            } catch (AccesoDatosException e) {
                System.out.println("Error al acceder a los datos: " + e.getMessage());
            }

            
            if (!libro.isDisponible()) {
                System.out.println("Libro no disponible");
                continue;
            }
            libros.put(idLibro, null); //IMPORTANTE: agregar la fecha, si es null no se realizo la baja
        }
        // si esta vacio no guardo el cambio
        if (libros.isEmpty()) {
            System.out.println("No se realizo ningun pretamo de ningún libro");
            return;
        }

        int idPrestamo = generarIdPrestamo();
        if (idPrestamo == -1) {
            System.out.println("No se pudo generar el prestamo, intentelo mas tarde.");
            return;
        }

        prestamo.setId(idPrestamo);
        prestamo.setFechaIni(LocalDate.now());

        int idUsuario;
        do {
            idUsuario = utilidades.Util.leerInt("Id Usuario: ");
            /*
        if(existe usuario){
            System.out.print("No existe el usuario en la base de datos");
            // mejor que tenga 3 intentos. al 3ro no añade nada
        }*/
        } while (false); // mientras no exista

        prestamo.setIdUsuario(numLibros);
        prestamo.setLibros(libros);

        // una vez creado cargo el objeto
        dao.agregar(prestamo);
        seModifico = true;
    }

    /**
     * DEVOLVER LIBRO:
     *
     * FLUJO: aseguro que los datos esten cargados (si falla, aviso y termino)
     * -> busco en la lista en memoria el prestamo con ese id -> si no existe,
     * aviso al usuario y termino -> obtengo el objeto libros -> si ese idLibro
     * no pertenece al prestamo, aviso y termino -> si ya tiene fecha (no es
     * null), no hago nada y termino -> si esta en null, le pongo la fecha de
     * hoy -> llamo a modificar() del repositorio con el jsonObject actualizado
     * -> segun el resultado, informo al usuario si se guardo o no.
     *
     * @param idPrestamo
     * @param idLibro
     */
    public void devolverLibro(int idPrestamo, int idLibro) {

        if (!cargarDatosMethod()) {
            System.out.println("No se puede procesar la devolucion en este momento.");
            return;
        }

        JSONObject prestamoEncontrado = null;

        for (int i = 0; i < prestamos.length(); i++) {
            JSONObject actual = prestamos.getJSONObject(i);

            if (actual.getInt("id") == idPrestamo) {
                prestamoEncontrado = actual;
                break;
            }
        }

        if (prestamoEncontrado == null) {
            System.out.println("No existe ningun prestamo con ese id.");
            return;
        }

        JSONObject libros = prestamoEncontrado.getJSONObject("libros");
        String key = String.valueOf(idLibro);

        if (!libros.has(key)) {
            System.out.println("Ese libro no pertenece a este prestamo.");
            return;
        }

        if (!libros.isNull(key)) {
            return;
        }

        libros.put(key, LocalDate.now().toString());

        if (repositorio.modificar(prestamoEncontrado)) {
            seModifico = true;
            System.out.println("Libro devuelto correctamente.");
        } else {
            System.out.println("No se pudo registrar la devolucion.");
        }
    }

    // ============================================================================
    // ============================ METODOS AUXILIARES ============================
    // ============================================================================
    /**
     * GENERAR ID PRESTAMO:
     *
     * FLUJO: aseguro que los datos esten cargados (si falla, devuelvo -1 como
     * valor centinela de error, en vez de asumir que la lista esta vacia) -> si
     * esta vacia, el primer id es 1 -> si no, tomo el ultimo prestamo y le sumo
     * 1.
     *
     * @return int el nuevo id, o -1 si no se pudo generar
     */
    private int generarIdPrestamo() {
        if (!cargarDatosMethod()) {
            return -1;
        }
        if (prestamos.isEmpty()) {
            return 1;
        }

        JSONObject jo = prestamos.getJSONObject(prestamos.length() - 1);

        return jo.getInt("id") + 1;
    }

    /**
     * INIT:
     *
     * FLUJO: intento crear la base de datos (true = fichero nuevo, false = ya
     * existia, esto NO es un error) -> cargo los datos con cargarDatosMethod(),
     * que ya gestiona internamente el backup ante cualquier problema y solo
     * informa al usuario si de verdad no se pudo recuperar la informacion.
     */
    private void init() {
        boolean creado = dao.creardb();

        if (creado) {
            System.out.println("Fichero de prestamos creado correctamente.");
        }

        cargarDatosMethod();
    }

}
