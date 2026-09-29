package controller;

import dao.LibroDao;
import dao.PrestamoDao;
import dao.UsuarioDao;
import exceptions.AccesoDatosException;
import exceptions.LibroNoEncontradoException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import model.Libro;
import model.Prestamo;
import model.Usuario;
import repository.AccesoLibro;
import repository.AccesoPrestamo;
import repository.AccesoUsuario;
import utilidades.Util;

public class PrestamoController {

    private static final int MAX_INTENTOS_USUARIO = 3;
    private static final String NO_ENTREGADO = "Todavia no entregado";

    private static PrestamoController instance;

    private final PrestamoDao daoPrestamo = AccesoPrestamo.getInstance();
    private final LibroDao daoLibro = AccesoLibro.getInstance();
    private final UsuarioDao daoUsuario = AccesoUsuario.getInstance();

    // Lista en memoria; null = hay que recargar del fichero
    private List<Prestamo> prestamos;

    /**
     * INICIALIZAR CONTROLADOR:
     *
     * FLUJO: inicializa la base de datos de préstamos mediante el DAO.
     */
    public PrestamoController() {
        daoPrestamo.creardb();
    }

    // INSTANCIA
    public static synchronized PrestamoController getInstance() {
        if (instance == null) {
            instance = new PrestamoController();
        }
        return instance;
    }

    /**
     * REALIZAR PRÉSTAMO:
     *
     * FLUJO: valida la cantidad de libros -> carga los préstamos -> solicita un
     * usuario válido -> reserva los libros disponibles -> crea el préstamo con
     * su ID, fecha y usuario -> guarda el préstamo y actualiza la lista en
     * memoria. Si falla el guardado, invalida la lista y libera los libros.
     */
    public void hacerPrestamo(int numLibros) {
        if (numLibros <= 0) {
            System.out.println("El numero de libros debe ser mayor que 0.");
            return;
        }
        List<Prestamo> lista;
        try {
            lista = obtenerLista();
        } catch (AccesoDatosException e) {
            System.out.println("No se pudo acceder a los prestamos. Operacion cancelada.");
            return;
        }
        Usuario usuario = pedirUsuario();
        if (usuario == null) {
            return;
        }
        Map<Integer, LocalDate> libros = reservarLibros(numLibros);
        if (libros.isEmpty()) {
            System.out.println("Ningun libro valido. No se ha registrado el prestamo.");
            return;
        }
        Prestamo prestamo = new Prestamo();
        prestamo.setId(siguienteId(lista));
        prestamo.setFechaIni(LocalDate.now());
        prestamo.setLibros(libros);
        prestamo.setIdUsuario(usuario.getId());
        try {
            daoPrestamo.agregar(prestamo);
            lista.add(prestamo);
            System.out.println("Prestamo " + prestamo.getId() + " registrado para "
                    + usuario.getNombre() + " con " + libros.size() + " libro(s).");
        } catch (AccesoDatosException e) {
            // Fuerza recarga y libera los libros reservados
            prestamos = null;
            liberarLibros(libros);
            System.out.println("No se pudo guardar el prestamo. Operacion cancelada.");
        }
    }

    /**
     * DEVOLVER LIBRO:
     *
     * FLUJO: carga los préstamos -> busca el préstamo activo del libro ->
     * registra la fecha de devolución -> guarda los cambios -> marca el libro
     * como disponible. Si falla el guardado, invalida la lista en memoria.
     *
     * @param idLibro
     */
    public void devolverLibroPorIdLibro(int idLibro) {
        List<Prestamo> lista;
        try {
            lista = obtenerLista();
        } catch (AccesoDatosException e) {
            System.out.println("No se pudo acceder a los prestamos. Operacion cancelada.");
            return;
        }
        Prestamo activo = buscarActivo(lista, idLibro);
        if (activo == null) {
            System.out.println("El libro " + idLibro + " no esta prestado actualmente.");
            return;
        }
        activo.getLibros().put(idLibro, LocalDate.now());
        try {
            if (!daoPrestamo.modificar(activo)) {
                activo.getLibros().put(idLibro, null);
                System.out.println("No se encontro el prestamo. Devolucion cancelada.");
                return;
            }
        } catch (AccesoDatosException e) {
            prestamos = null;
            System.out.println("No se pudo guardar la devolucion. Operacion cancelada.");
            return;
        }
        marcarDisponible(idLibro);
    }

    /**
     * MOSTRAR HISTORIAL DE UN LIBRO:
     *
     * FLUJO: carga los préstamos -> consulta el título del libro -> filtra los
     * préstamos que contienen su ID -> muestra el usuario, la fecha de inicio y
     * la fecha de devolución. Si la fecha de devolución es null, sigue sin
     * entregarse.
     * @param idLibro
     */
    public void mostrarHistorialLibro(int idLibro) {
        List<Prestamo> lista;
        try {
            lista = obtenerLista();
        } catch (AccesoDatosException e) {
            System.out.println("No se pudo acceder a los prestamos. Operacion cancelada.");
            return;
        }
        Libro libro;
        try {
            libro = daoLibro.obtenerPorId(idLibro);
        } catch (LibroNoEncontradoException e) {
            System.out.println("El libro " + idLibro + " no existe.");
            return;
        } catch (AccesoDatosException e) {
            System.out.println("No se pudo consultar el libro. Operacion cancelada.");
            return;
        }
        List<Prestamo> historial = lista.stream()
                .filter(p -> p.getLibros() != null && p.getLibros().containsKey(idLibro))
                .toList();
        System.out.println("Historial del libro '" + libro.getTitulo() + "':");
        Util.verPortada(libro.getRuta());
        if (historial.isEmpty()) {
            System.out.println("  Sin prestamos.");
        }
        for (Prestamo p : historial) {
            System.out.println("  Prestamo " + p.getId() + " | Usuario " + p.getIdUsuario()
                    + " | Inicio: " + p.getFechaIni()
                    + " | Fin: " + formatoFecha(p.getLibros().get(idLibro)));
        }
    }

    /**
     * MOSTRAR HISTORIAL DE UN USUARIO:
     *
     * FLUJO: carga los préstamos -> comprueba que el usuario exista -> filtra
     * sus préstamos -> muestra cada préstamo con sus libros y fechas. Utiliza
     * una caché local para evitar consultar varias veces el mismo título.
     */
    public void mostrarHistorialUsuario(int idUsuario) {
        List<Prestamo> lista;
        try {
            lista = obtenerLista();
        } catch (AccesoDatosException e) {
            System.out.println("No se pudo acceder a los prestamos. Operacion cancelada.");
            return;
        }
        Usuario usuario;
        try {
            usuario = daoUsuario.obtenerUsuarioPorId(idUsuario);
        } catch (AccesoDatosException e) {
            System.out.println("No se pudo consultar el usuario. Operacion cancelada.");
            return;
        }
        if (usuario == null) {
            System.out.println("El usuario " + idUsuario + " no existe.");
            return;
        }
        List<Prestamo> historial = lista.stream()
                .filter(p -> p.getIdUsuario() == idUsuario && p.getLibros() != null)
                .toList();
        System.out.println("Historial del usuario " + usuario.getNombre() + ":");
        if (historial.isEmpty()) {
            System.out.println("  Sin prestamos.");
        }
        // Cada titulo se consulta una sola vez
        Map<Integer, String> titulos = new HashMap<>();
        for (Prestamo p : historial) {
            System.out.println("  Prestamo " + p.getId() + " | Inicio: " + p.getFechaIni());
            for (Map.Entry<Integer, LocalDate> e : p.getLibros().entrySet()) {
                System.out.println("    - " + titulo(e.getKey(), titulos)
                        + " | Fin: " + formatoFecha(e.getValue()));
            }
        }
    }

    // Devuelve la lista en memoria; si no esta, la carga (con backup y un reintento)
    private List<Prestamo> obtenerLista() throws AccesoDatosException {
        if (prestamos != null) {
            return prestamos;
        }
        try {
            prestamos = daoPrestamo.cargar();
        } catch (AccesoDatosException e) {
            daoPrestamo.restaurarBackup();
            prestamos = daoPrestamo.cargar();
        }
        return prestamos;
    }

    // Pide el usuario (maximo 3 intentos); null si se cancela
    private Usuario pedirUsuario() {
        for (int intento = 1; intento <= MAX_INTENTOS_USUARIO; intento++) {
            int id = Util.leerInt("Id del usuario: ");
            try {
                Usuario usuario = daoUsuario.obtenerUsuarioPorId(id);
                if (usuario != null) {
                    return usuario;
                }
                System.out.println("El usuario no existe (intento " + intento + " de " + MAX_INTENTOS_USUARIO + ").");
            } catch (AccesoDatosException e) {
                System.out.println("No se pudo consultar el usuario. Operacion cancelada.");
                return null;
            }
        }
        System.out.println("Demasiados intentos fallidos. Operacion cancelada.");
        return null;
    }

    // Pide los libros y marca como no disponibles los validos
    private Map<Integer, LocalDate> reservarLibros(int numLibros) {
        Map<Integer, LocalDate> libros = new HashMap<>();
        for (int i = 1; i <= numLibros; i++) {
            int idLibro = Util.leerInt("Id del libro " + i + " de " + numLibros + ": ");
            if (libros.containsKey(idLibro)) {
                System.out.println("El libro " + idLibro + " ya esta en este prestamo. Se omite.");
                continue;
            }
            try {
                Libro libro = daoLibro.obtenerPorId(idLibro);
                if (!libro.isDisponible()) {
                    System.out.println("El libro '" + libro.getTitulo() + "' no esta disponible. Se omite.");
                    continue;
                }
                libro.setDisponible(false);
                daoLibro.modificar(libro);
                libros.put(idLibro, null);
            } catch (LibroNoEncontradoException e) {
                System.out.println("El libro " + idLibro + " no existe. Se omite.");
            } catch (AccesoDatosException e) {
                System.out.println("No se pudo reservar el libro " + idLibro + ". Se omite.");
            }
        }
        return libros;
    }

    // Deshace la reserva de los libros si falla el guardado
    private void liberarLibros(Map<Integer, LocalDate> libros) {
        for (Integer idLibro : libros.keySet()) {
            try {
                Libro libro = daoLibro.obtenerPorId(idLibro);
                libro.setDisponible(true);
                daoLibro.modificar(libro);
            } catch (LibroNoEncontradoException | AccesoDatosException e) {
                System.out.println("No se pudo liberar el libro " + idLibro + ". Revise su disponibilidad.");
            }
        }
    }

    private void marcarDisponible(int idLibro) {
        try {
            Libro libro = daoLibro.obtenerPorId(idLibro);
            libro.setDisponible(true);
            daoLibro.modificar(libro);
            System.out.println("Libro '" + libro.getTitulo() + "' devuelto correctamente el " + LocalDate.now() + ".");
        } catch (LibroNoEncontradoException e) {
            System.out.println("Devolucion registrada, pero el libro " + idLibro + " no existe en el catalogo.");
        } catch (AccesoDatosException e) {
            System.out.println("Devolucion registrada, pero no se pudo marcar el libro " + idLibro + " como disponible.");
        }
    }

    // Prestamo activo del libro (fechaFin == null)
    private Prestamo buscarActivo(List<Prestamo> lista, int idLibro) {
        return lista.stream()
                .filter(p -> p.getLibros() != null && p.getLibros().containsKey(idLibro)
                        && !p.libroDisponible(idLibro))
                .findFirst()
                .orElse(null);
    }

    private int siguienteId(List<Prestamo> lista) {
        return lista.stream().mapToInt(Prestamo::getId).max().orElse(0) + 1;
    }

    // Titulo con cache local para no repetir consultas
    private String titulo(int idLibro, Map<Integer, String> cache) {
        String titulo = cache.get(idLibro);
        if (titulo != null) {
            return titulo;
        }
        try {
            titulo = daoLibro.obtenerPorId(idLibro).getTitulo();
        } catch (LibroNoEncontradoException e) {
            titulo = "Libro " + idLibro + " (no existe en el catalogo)";
        } catch (AccesoDatosException e) {
            titulo = "Libro " + idLibro + " (titulo no disponible)";
        }
        cache.put(idLibro, titulo);
        return titulo;
    }

    private String formatoFecha(LocalDate fecha) {
        return fecha == null ? NO_ENTREGADO : fecha.toString();
    }
}
