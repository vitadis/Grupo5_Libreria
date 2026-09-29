package repository;

import dao.PrestamoDao;
import exceptions.AccesoDatosException;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InvalidClassException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import model.Prestamo;

/**
 * Acceso a los prestamos guardados en un fichero .dat.
 *
 * @author Joel
 */
public class AccesoPrestamo implements PrestamoDao {

    // Instancia unica
    private static AccesoPrestamo instance;

    // Rutas configurables
    private Path ruta = Paths.get("./src/dataBase/prestamos.dat");
    private Path rutaBackup = Paths.get("./src/dataBase/prestamos_backup.dat");
    private Path rutaTmp = Paths.get("./src/dataBase/prestamos.tmp");

    private AccesoPrestamo() {
    }

    public static synchronized AccesoPrestamo getInstance() {
        if (instance == null) {
            instance = new AccesoPrestamo();
        }
        return instance;
    }

    public Path getRuta() {
        return ruta;
    }

    public void setRuta(Path ruta) {
        this.ruta = ruta;
    }

    public Path getRutaBackup() {
        return rutaBackup;
    }

    public void setRutaBackup(Path rutaBackup) {
        this.rutaBackup = rutaBackup;
    }

    public Path getRutaTmp() {
        return rutaTmp;
    }

    public void setRutaTmp(Path rutaTmp) {
        this.rutaTmp = rutaTmp;
    }

    @Override
    public synchronized boolean creardb() {
        try {
            if (Files.exists(ruta)) {
                return false;
            }
            Path padre = ruta.getParent();
            if (padre != null) {
                Files.createDirectories(padre);
            }
            // Lista vacia para que cargar() funcione
            escribirYMover(new ArrayList<>());
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    @Override
    public synchronized List<Prestamo> cargar() throws AccesoDatosException {
        if (!Files.exists(ruta)) {
            return new ArrayList<>();
        }
        try (ObjectInputStream in = new ObjectInputStream(
                new BufferedInputStream(new FileInputStream(ruta.toFile())))) {
            Object leido = in.readObject();
            if (!(leido instanceof List<?> lista)) {
                throw new AccesoDatosException("El fichero de prestamos tiene un formato no valido.");
            }
            List<Prestamo> resultado = new ArrayList<>();
            for (Object o : lista) {
                if (!(o instanceof Prestamo p)) {
                    throw new AccesoDatosException("El fichero de prestamos contiene datos no validos.");
                }
                resultado.add(p);
            }
            return resultado;
        } catch (InvalidClassException e) {
            throw new AccesoDatosException("Version de clase incompatible en el fichero de prestamos.", e);
        } catch (ClassNotFoundException e) {
            throw new AccesoDatosException("Clase no encontrada al leer el fichero de prestamos.", e);
        } catch (IOException e) {
            throw new AccesoDatosException("No se pudo leer el fichero de prestamos (posible corrupcion).", e);
        }
    }

    @Override
    public synchronized void guardar(List<Prestamo> prestamos) throws AccesoDatosException {
        if (prestamos == null) {
            throw new AccesoDatosException("La lista de prestamos no puede ser nula.");
        }
        // Backup previo; si falla, el fichero principal sigue intacto
        try {
            if (Files.exists(ruta)) {
                Files.copy(ruta, rutaBackup, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException e) {
            throw new AccesoDatosException("No se pudo crear la copia de seguridad de prestamos.", e);
        }
        try {
            escribirYMover(prestamos);
        } catch (IOException e) {
            // Limpia el temporal y restaura el backup
            try {
                Files.deleteIfExists(rutaTmp);
            } catch (IOException ex) {
                e.addSuppressed(ex);
            }
            restaurarBackup();
            throw new AccesoDatosException("No se pudo guardar el fichero de prestamos.", e);
        }
    }

    @Override
    public synchronized void agregar(Prestamo prestamo) throws AccesoDatosException {
        if (prestamo == null) {
            throw new AccesoDatosException("El prestamo a agregar no puede ser nulo.");
        }
        List<Prestamo> lista = cargar();
        for (Prestamo p : lista) {
            if (p.getId() == prestamo.getId()) {
                throw new AccesoDatosException("Ya existe un prestamo con id " + prestamo.getId() + ".");
            }
        }
        lista.add(prestamo);
        guardar(lista);
    }

    @Override
    public synchronized boolean modificar(Prestamo prestamo) throws AccesoDatosException {
        if (prestamo == null) {
            throw new AccesoDatosException("El prestamo a modificar no puede ser nulo.");
        }
        List<Prestamo> lista = cargar();
        for (int i = 0; i < lista.size(); i++) {
            if (lista.get(i).getId() == prestamo.getId()) {
                lista.set(i, prestamo);
                guardar(lista);
                return true;
            }
        }
        return false;
    }

    @Override
    public synchronized boolean restaurarBackup() {
        try {
            if (!Files.exists(rutaBackup)) {
                return false;
            }
            Files.copy(rutaBackup, ruta, StandardCopyOption.REPLACE_EXISTING);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    // Escribe en el temporal y lo mueve al fichero principal
    private void escribirYMover(List<Prestamo> prestamos) throws IOException {
        try (ObjectOutputStream out = new ObjectOutputStream(
                new BufferedOutputStream(new FileOutputStream(rutaTmp.toFile())))) {
            out.writeObject(new ArrayList<>(prestamos));
        }
        try {
            Files.move(rutaTmp, ruta, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
        } catch (IOException e) {
            // Sin soporte atomico: movimiento normal
            Files.move(rutaTmp, ruta, StandardCopyOption.REPLACE_EXISTING);
        }
    }
}
