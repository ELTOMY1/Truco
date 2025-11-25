package controlador;

import modelo.JuegoModelo;
import modelo.Observador;
import vista.VistaConsola;
import modelo.IJugador;
import modelo.ICarta;
import java.util.ArrayList;
import java.util.List;

public class ControladorJuego implements Observador {
    private JuegoModelo modelo;
    private VistaConsola vista;

    public ControladorJuego(JuegoModelo modelo, VistaConsola vista) {
        this.modelo = modelo;
        this.vista = vista;
        this.modelo.agregarObservador(this);
    }

    public void iniciarJuego() {
        vista.mostrarTexto("=== BIENVENIDO AL TRUCO ARGENTINO ===");
        vista.mostrarTexto("Modo: (1) 15 pts (2) 30 pts");
        int puntosMax = (vista.obtenerEntradaInt(1, 2) == 1) ? 15 : 30;
        vista.mostrarTexto("¿Con Flor? (1) Sí (2) No");
        boolean conFlor = (vista.obtenerEntradaInt(1, 2) == 1);
        String n1 = vista.pedirTexto("Jugador 1: ");
        String n2 = vista.pedirTexto("Jugador 2: ");

        modelo.iniciarPartida(n1, n2, conFlor, puntosMax);

        while (!modelo.isJuegoTerminado()) {
            IJugador actual = modelo.getJugadorActual();
            List<String> opciones = modelo.getOpcionesDisponibles();
            String msgEstado = modelo.getEstadoMesaInfo();

            List<String> cartasStr = new ArrayList<>();
            for(ICarta c : actual.getManoCartas()) cartasStr.add(c.toString());

            vista.mostrarTexto(msgEstado);

            vista.mostrarPantalla(null, actual.getNombre(), cartasStr, opciones);

            int seleccion = vista.obtenerEntradaInt(1, opciones.size());
            String accion = opciones.get(seleccion - 1);
            int idxCarta = -1;
            if (accion.contains("TIRAR") || accion.contains("SEGUIR")) {
                idxCarta = vista.elegirCarta(actual.getNombre(), cartasStr);
            }
            modelo.procesarAccion(accion, idxCarta);
        }

        vista.mostrarTexto("--- FIN --- Ganador: " + modelo.getNombreGanador());
    }

    @Override
    public void actualizar() {
        String evento = modelo.getMensajeUltimaAccion();
        if (evento != null && !evento.isEmpty()) {
            vista.mostrarEvento(evento);

            if (evento.contains("NUEVA MANO") || evento.contains("JUEGO TERMINADO") || evento.contains("Mano terminada")) {
                List<String> marcadorStr = new ArrayList<>();
                for(IJugador j : modelo.getJugadores()) {
                    marcadorStr.add(j.getNombre() + " (Eq " + j.getEquipo() + "): " + j.getPuntos() + " pts");
                }
                vista.mostrarMarcador(marcadorStr);
            }
        }
    }
}