/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;

import dao.DaoLibro;
import dao.PrestamoDao;
import dao.UsuarioDao;
import exceptions.AccesoDatosException;
import exceptions.LibroNoEncontradoException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import model.Libro;
import model.Prestamo;
import model.Usuario;

import org.json.JSONArray;
import org.json.JSONObject;
import repository.AccesoLibro;
import repository.AccesoPrestamo;
import repository.AccesoUsuario;

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
    private final UsuarioDao daoUsuario = AccesoUsuario.getInstance();

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

    public void mostrarHistorialUsuario(int idUsuario) {

        if (!cargarDatosMethod()) {
            return;
        }

        prestamos.forEach(o -> {
            JSONObject prestamo = (JSONObject) o;
            JSONObject usuarios = prestamo.getJSONObject("usuarios");

            for (String key : usuarios.keySet()) {
                int idU = Integer.parseInt(key);

                if (idU == idUsuario) {

                    Usuario usuario;

                    try {
                        usuario = daoUsuario.obtenerUsuarioPorId(idUsuario);
                    } catch (AccesoDatosException e) {
                        System.out.println("Error al acceder a los datos: " + e.getMessage());
                        return;
                    }

                    System.out.println("========== " + usuario.getNombre() + " ==========");
                    StringBuilder sb = new StringBuilder();
                    sb.append("---------------------------------------------\n");
                    sb.append(prestamo.getInt("id")).append("\n");
                    sb.append(prestamo.getString("fechaIni")).append("\n");
                    sb.append(prestamo.getInt("idLibro")).append("\n");
                    sb.append(JSONObject.NULL.equals(usuarios.opt(idUsuario + "")) ? "Todavia no entregado" : usuarios.opt(idUsuario + "")).append("\n");
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
     * FLUJO: (si numLibros es <= 0) -> aviso y termino (compruebo que los datos
     * esten cargados, si no, aviso y termino, para no generar un prestamo con
     * datos vacios o desactualizados) (pido el id del usuario y compruebo que
     * existe en la base de datos, con un maximo de 3 intentos; si se agotan,
     * aviso de que se cancela el prestamo por id de usuario inexistente y
     * termino; si hay un error de acceso a datos, aviso y termino sin
     * reintentar) (for de libros -> pido el id, compruebo que existe el libro y
     * que este disponible, si no, aviso y paso al siguiente -> al momento de
     * guardar en el map, modifico el disponible del libro a false en la base de
     * datos) (si el map de libros queda vacio, aviso y termino sin guardar)
     * (genero el id del prestamo de forma segura, si falla, aviso y termino sin
     * guardar) (asigno fecha inicial, id de usuario y libros, y agrego el
     * prestamo).
     *
     * @param numLibros numero de libros que se van a prestar; si es <= 0 no se
     * realiza ningun prestamo
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

        // Comprobacion de la existencia del usuario
        final int MAX_INTENTOS = 3;
        int intentos = 0;
        int idUsuario;
        boolean usuarioValido = false;

        do {
            idUsuario = utilidades.Util.leerInt("Id Usuario: ");
            intentos++;

            try {
                Usuario usuario = AccesoUsuario.getInstance().obtenerUsuarioPorId(idUsuario);

                if (usuario != null) {
                    usuarioValido = true;
                } else {
                    System.out.println("No existe el usuario en la base de datos. Intento "
                            + intentos + " de " + MAX_INTENTOS);
                }

            } catch (AccesoDatosException e) {
                System.out.println("Error de acceso a datos: " + e.getMessage());
                return;
            }

        } while (!usuarioValido && intentos < MAX_INTENTOS);

        if (!usuarioValido) {
            System.out.println("Se cancelo el prestamo, por id de usuario inexistente.");
            return;
        }

        // Prestamos mediante un foreach
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

            try {
                libro.setDisponible(false);
                daoLibro.modificar(libro);
                System.out.println("Libro agregado correctamente.");

            } catch (LibroNoEncontradoException e) {
                System.out.println("No se pudo modificar: " + e.getMessage());

            } catch (AccesoDatosException e) {
                System.out.println("Error de acceso a datos: " + e.getMessage());
            }

            libros.put(idLibro, null);
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

        // si todo esta bien agrego el idUsuario
        prestamo.setIdUsuario(idUsuario);
        prestamo.setLibros(libros);

        // una vez creado cargo el objeto
        dao.agregar(prestamo);
        seModifico = true;
    }

    /**
     * DEVOLVER LIBRO POR ID DE LIBRO: Busca en el JSON el préstamo activo donde
     * figura el libro (valor null), le asigna la fecha de hoy, guarda el JSON
     * y actualiza la BD.
     */
    public void devolverLibroPorIdLibro(int idLibro) {
        if (!cargarDatosMethod()) {
            System.out.println("No se puede procesar la devolucion en este momento.");
            return;
        }

        JSONObject prestamoEncontrado = null;
        String key = String.valueOf(idLibro);

        for (int i = 0; i < prestamos.length(); i++) {
            JSONObject p = prestamos.getJSONObject(i);
            JSONObject libros = p.getJSONObject("libros");

            if (libros.has(key) && libros.isNull(key)) {
                prestamoEncontrado = p;
                break;
            }
        }

        if (prestamoEncontrado == null) {
            System.out.println("No se encontro ningun prestamo activo para el libro con ID " + idLibro);
            return;
        }
        JSONObject libros = prestamoEncontrado.getJSONObject("libros");
        libros.put(key, LocalDate.now().toString());

        if (repositorio.modificar(prestamoEncontrado)) {
            seModifico = true;
            
            // Si se modifico, modificar dentro de prestamos
            Libro libro = null;
            try {
                libro = daoLibro.obtenerPorId(idLibro);
            } catch (LibroNoEncontradoException e) {
                System.out.println("No se encontró el libro con id " + idLibro);
            } catch (AccesoDatosException e) {
                System.out.println("Error al acceder a los datos: " + e.getMessage());
            }
            // modifico la visualizacion del libro
            try {
                libro.setDisponible(true);
                daoLibro.modificar(libro);
                System.out.println("Libro agregado correctamente.");

            } catch (LibroNoEncontradoException e) {
                System.out.println("No se pudo modificar: " + e.getMessage());

            } catch (AccesoDatosException e) {
                System.out.println("Error de acceso a datos: " + e.getMessage());
            }
            
        } else {
            System.out.println("No se pudo guardar la devolucion en el fichero.");
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
