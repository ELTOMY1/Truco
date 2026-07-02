import vista.*;
import modelo.*;
import controlador.ControladorJuego;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JOptionPane;

public class App {
    public static void main(String[] args) {
        // 1. Configuración con BOTONES
        int modo = ConfiguradorUI.elegirModo();
        int puntos = ConfiguradorUI.elegirPuntos();
        boolean conFlor = ConfiguradorUI.elegirFlor();

        String[] nombres = new String[modo];
        for (int i = 0; i < modo; i++) {
            nombres[i] = JOptionPane.showInputDialog("Nombre del Jugador " + (i + 1));
            if (nombres[i] == null) nombres[i] = "Jugador " + (i + 1);
        }

        // 2. Crear Modelo
        JuegoModelo modelo;
        if (modo == 2) {
            modelo = new JuegoModelo(2, puntos, nombres[0], nombres[1], null, null);
        } else {
            modelo = new JuegoModelo(4, puntos, nombres[0], nombres[1], nombres[2], nombres[3]);
        }

        // 3. Crear Ventanas (Pantalla dividida)
        Map<String, VentanaConsolaGrafica> ventanas = new HashMap<>();
        int anchoPantalla = 600;

        for (int i = 0; i < modelo.getJugadores().size(); i++) {
            Jugador j = modelo.getJugadores().get(i);
            VentanaConsolaGrafica v = new VentanaConsolaGrafica(j.getNombre(), i * anchoPantalla, 50);
            v.setVisible(true);
            ventanas.put(j.getNombre(), v);
            v.escribir("BIENVENIDO " + j.getNombre().toUpperCase());
        }

        // 4. Iniciar Controlador
        // AHORA LE PASAMOS LAS VENTANAS A LA VISTA
        VistaConsola vista = new VistaConsola(ventanas);
        ControladorJuego controlador = new ControladorJuego(modelo, vista);

        controlador.iniciarJuego();
    }
}