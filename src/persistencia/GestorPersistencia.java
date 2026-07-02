package persistencia;
import modelo.JuegoModelo;
import java.io.*;

public class GestorPersistencia {
    public static void guardarPartida(JuegoModelo modelo, String rutaArchivo) throws Exception {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(rutaArchivo))) {
            out.writeObject(modelo);
        }
    }

    public static JuegoModelo cargarPartida(String rutaArchivo) throws Exception {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(rutaArchivo))) {
            return (JuegoModelo) in.readObject();
        }
    }
}