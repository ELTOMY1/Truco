import controlador.ControladorGrafico;
import modelo.JuegoModelo;
import modelo.Jugador;
import persistencia.GestorPersistencia;
import vista.VentanaJugador;
import javax.swing.JOptionPane;
import java.io.File;

public class AppGrafica {
    public static void main(String[] args) {
        JuegoModelo modelo = null;
        boolean juegaConFlor = false;

        File archivo = new File("truco_save.dat");
        if (archivo.exists()) {
            int cargar = JOptionPane.showConfirmDialog(null, "Se encontró una partida guardada. ¿Deseas cargarla?", "Cargar Juego", JOptionPane.YES_NO_OPTION);
            if (cargar == JOptionPane.YES_OPTION) {
                try {
                    modelo = GestorPersistencia.cargarPartida("truco_save.dat");
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(null, "Error al cargar: " + e.getMessage());
                }
            }
        }

        if (modelo == null) {
            String[] opcionesModo = {"1 vs 1", "2 vs 2"};
            int modo = JOptionPane.showOptionDialog(null, "¿Cuántos jugadores?", "Modo de Juego", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, opcionesModo, opcionesModo[1]);
            int cantidad = (modo == 0) ? 2 : 4;

            String[] opcionesPuntos = {"15 puntos", "30 puntos"};
            int pts = JOptionPane.showOptionDialog(null, "¿A cuántos puntos?", "Puntos", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, opcionesPuntos, opcionesPuntos[1]);
            int puntosMax = (pts == 0) ? 15 : 30;

            int flor = JOptionPane.showConfirmDialog(null, "¿Jugar con Flor?", "Reglas", JOptionPane.YES_NO_OPTION);
            juegaConFlor = (flor == JOptionPane.YES_OPTION);

            // --- NUEVO: PEDIR NOMBRES INDIVIDUALMENTE ---
            if (cantidad == 2) {
                String n1 = pedirNombre("Jugador 1 (Equipo Nosotros)", "J1");
                String n2 = pedirNombre("Jugador 2 (Equipo Ellos)", "J2");
                modelo = new JuegoModelo(2, puntosMax, n1, n2, null, null);
            } else {
                String n1 = pedirNombre("Jugador 1 (Equipo Nosotros)", "J1");
                String n2 = pedirNombre("Jugador 2 (Equipo Ellos)", "J2");
                String n3 = pedirNombre("Jugador 3 (Equipo Nosotros)", "J3");
                String n4 = pedirNombre("Jugador 4 (Equipo Ellos)", "J4");
                modelo = new JuegoModelo(4, puntosMax, n1, n2, n3, n4);
            }
            modelo.repartirCartas();
        }

        ControladorGrafico controlador = new ControladorGrafico(modelo, juegaConFlor);

        int cant = modelo.getEquipo1().getIntegrantes().size() * 2;

        Jugador j1 = modelo.getEquipo1().getIntegrantes().get(0);
        Jugador j2 = modelo.getEquipo2().getIntegrantes().get(0);

        VentanaJugador v1 = new VentanaJugador(modelo, j1, controlador);
        VentanaJugador v2 = new VentanaJugador(modelo, j2, controlador);
        v1.setLocation(0, 0);
        v2.setLocation(500, 0);
        v1.setVisible(true);
        v2.setVisible(true);

        if (cant == 4) {
            Jugador j3 = modelo.getEquipo1().getIntegrantes().get(1);
            Jugador j4 = modelo.getEquipo2().getIntegrantes().get(1);
            VentanaJugador v3 = new VentanaJugador(modelo, j3, controlador);
            VentanaJugador v4 = new VentanaJugador(modelo, j4, controlador);
            v3.setLocation(0, 400);
            v4.setLocation(500, 400);
            v3.setVisible(true);
            v4.setVisible(true);
        }
    }

    // Método auxiliar para pedir nombres y evitar que dejen el campo vacío
    private static String pedirNombre(String mensaje, String nombrePorDefecto) {
        String nombre = JOptionPane.showInputDialog(null, "Ingrese nombre para " + mensaje + ":", nombrePorDefecto);
        if (nombre == null || nombre.trim().isEmpty()) {
            return nombrePorDefecto;
        }
        return nombre.trim();
    }
}