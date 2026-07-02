package controlador;

import modelo.*;
import persistencia.GestorPersistencia;
import javax.swing.JOptionPane;
import java.util.ArrayList;
import java.util.List;

public class ControladorGrafico {
    private JuegoModelo modelo;
    private boolean seJuegaConFlor;

    public ControladorGrafico(JuegoModelo modelo, boolean seJuegaConFlor) {
        this.modelo = modelo;
        this.seJuegaConFlor = seJuegaConFlor;
    }

    public boolean isSeJuegaConFlor() { return seJuegaConFlor; }

    public void jugarCarta(Jugador j, Carta c) {
        if (modelo.getJugadorActual().equals(j)) {
            modelo.jugadaTirarCarta(c);
            if (!verificarFinPartido()) {
                verificarFinRonda();
            }
        }
    }

    public void cantarEnvido(Jugador iniciador, int codigoInicial) {
        int estado = codigoInicial;
        int acumulado = (estado == 99) ? calcularFaltaEnvido() : estado;
        int siNo = 1;
        Equipo resp = obtenerRival(iniciador);
        boolean envidoDosVeces = false;

        while(true) {
            List<String> ops = new ArrayList<>();
            ops.add("Quiero"); ops.add("No Quiero");
            if (estado == 2 && !envidoDosVeces) ops.add("Envido");
            if (estado <= 2) ops.add("Real Envido");
            if (estado <= 3) ops.add("Falta Envido");

            String[] opsArray = ops.toArray(new String[0]);

            String msg = "¡" + iniciador.getNombre() + " cantó " + nombreEnvido(estado) + "!\n\n" +
                    "Responde el Equipo " + resp.getNombre() + "\n" + infoCartas(resp) + "\n¿Qué deciden?";

            int r = JOptionPane.showOptionDialog(null, msg, "Apuesta Envido", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, opsArray, opsArray[0]);

            if (r == -1) r = 1;
            String seleccion = opsArray[r];

            if (seleccion.equals("Quiero")) {
                Jugador j1 = modelo.getEquipo1().obtenerJugadorMejorEnvido();
                Jugador j2 = modelo.getEquipo2().obtenerJugadorMejorEnvido();
                Jugador ganadorJugador;

                if (j1.getTantosEnvido() > j2.getTantosEnvido()) ganadorJugador = j1;
                else if (j2.getTantosEnvido() > j1.getTantosEnvido()) ganadorJugador = j2;
                else ganadorJugador = modelo.getEquipoDeJugador(modelo.getJugadorMano()) == modelo.getEquipo1() ? j1 : j2;

                Equipo g = modelo.resolverEnvido(acumulado);

                modelo.getRondaActual().setMensajeApuestas("¡" + ganadorJugador.getNombre() + " ganó el envido con " + ganadorJugador.getTantosEnvido() + "!");
                verificarFinPartido();
                break;
            } else if (seleccion.equals("No Quiero")) {
                Equipo ganaRechazo = obtenerRival(resp.getIntegrantes().get(0));
                ganaRechazo.sumarPuntos(siNo, modelo.getPuntosObjetivo());
                modelo.getRondaActual().envidoCantado = true;

                modelo.getRondaActual().setMensajeApuestas("Envido no querido. " + siNo + " pts para " + obtenerNombresEquipo(ganaRechazo));
                verificarFinPartido();
                break;
            } else {
                siNo = acumulado;
                if(seleccion.equals("Envido")) { envidoDosVeces=true; estado=2; acumulado+=2; }
                if(seleccion.equals("Real Envido")) { estado=3; acumulado+=3; }
                if(seleccion.equals("Falta Envido")) { estado=99; acumulado=calcularFaltaEnvido(); }

                iniciador = resp.getIntegrantes().get(0);
                resp = obtenerRival(iniciador);
            }
        }
        modelo.notificarObservadores();
    }

    public void cantarTruco(Jugador iniciador) {
        // 1. El modelo marca quién gritó, pero el nivel NO sube todavía
        modelo.subirTruco(iniciador);
        Equipo resp = obtenerRival(iniciador);

        while (true) {
            // Determinamos qué es lo que se está gritando ahora (el siguiente nivel)
            int nivelPropuesto = modelo.getRondaActual().getNivelTruco() + 1;
            String gritoActual = nombreTruco(nivelPropuesto);

            List<String> ops = new ArrayList<>();
            ops.add("Quiero");
            ops.add("No Quiero");
            if (nivelPropuesto < 4) ops.add("Subir Apuesta");

            // Verificamos si pueden responder con Envido (solo en primera mano y si no se cantó antes)
            boolean puedeEnvido = false;
            Jugador jRespondeEnvido = resp.getIntegrantes().get(0);
            for (Jugador rival : resp.getIntegrantes()) {
                if (modelo.getRondaActual().sePuedeCantarEnvido(rival)) {
                    puedeEnvido = true;
                    jRespondeEnvido = rival;
                    break;
                }
            }
            if (puedeEnvido) {
                ops.add("Envido");
                ops.add("Real Envido");
                ops.add("Falta Envido");
            }

            String[] opsArray = ops.toArray(new String[0]);
            String msg = "¡" + iniciador.getNombre() + " gritó " + gritoActual + "!\n\n" +
                    "Responde el Equipo " + resp.getNombre() + "\n" + infoCartas(resp) + "\n¿Qué deciden?";

            int r = JOptionPane.showOptionDialog(null, msg, "Apuesta de Truco",
                    JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE, null, opsArray, opsArray[0]);

            if (r == -1) r = 1; // Si cierran la ventana, es un "No Quiero"
            String seleccion = opsArray[r];

            if (seleccion.equals("Quiero")) {
                modelo.confirmarSubida(); // RECIÉN ACÁ el Truco vale más
                break;
            } else if (seleccion.equals("No Quiero")) {
                modelo.noQuieroTruco(resp.getIntegrantes().get(0));
                if (!verificarFinPartido()) verificarFinRonda();
                break;
            } else if (seleccion.equals("Subir Apuesta")) {
                modelo.confirmarSubida(); // Acepta el actual (ej. Truco)
                modelo.subirTruco(resp.getIntegrantes().get(0)); // Y grita el que sigue (ej. Retruco)
                iniciador = resp.getIntegrantes().get(0);
                resp = obtenerRival(iniciador);
                // El while(true) hace que ahora se pregunte por el Retruco
            } else {
                // CASO ENVIDO: Se interrumpe el Truco para cantar los puntos
                int cod = seleccion.equals("Envido") ? 2 : seleccion.equals("Real Envido") ? 3 : 99;
                cantarEnvido(jRespondeEnvido, cod);
                // Al terminar cantarEnvido, el bucle while vuelve a empezar.
                // Como el Envido ya se cantó, la función sePuedeCantarEnvido() devolverá FALSE
                // y los botones de envido ya no aparecerán, quedando solo los de Truco.
            }
        }
        modelo.notificarObservadores();
    }


    public void cantarFlor(Jugador iniciador) {
        Equipo equipoCanta = modelo.getEquipoDeJugador(iniciador);
        Equipo rival = obtenerRival(iniciador);

        if (rival.tieneFlor()) {
            String[] ops = {"Con Flor Quiero", "Contra Flor al Resto", "Me achico"};
            String msg = "¡" + iniciador.getNombre() + " cantó FLOR!\nEquipo " + rival.getNombre() + ", ustedes también tienen Flor.\n" + infoCartas(rival) + "\n¿Qué hacen?";

            int r = JOptionPane.showOptionDialog(null, msg, "Duelo de Flores", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, ops, ops[0]);

            if (r == 0) {
                int f1 = equipoCanta.obtenerMejorFlor();
                int f2 = rival.obtenerMejorFlor();
                Equipo g = (f1 >= f2) ? equipoCanta : rival;
                modelo.resolverFlor(g, 6);
                modelo.getRondaActual().setMensajeApuestas("¡" + obtenerNombresEquipo(g) + " ganó la Flor!");
                verificarFinPartido();
            } else if (r == 1) {
                int f1 = equipoCanta.obtenerMejorFlor();
                int f2 = rival.obtenerMejorFlor();
                Equipo g = (f1 >= f2) ? equipoCanta : rival;
                modelo.resolverFlor(g, modelo.getPuntosObjetivo());
                modelo.getRondaActual().setMensajeApuestas("¡" + obtenerNombresEquipo(g) + " ganó la Contraflor al Resto!");
                verificarFinPartido();
            } else {
                modelo.resolverFlor(equipoCanta, 3);
                modelo.getRondaActual().setMensajeApuestas(obtenerNombresEquipo(rival) + " se achicó en Flor.");
                verificarFinPartido();
            }
        } else {
            modelo.resolverFlor(equipoCanta, 3);
            modelo.getRondaActual().setMensajeApuestas("¡" + iniciador.getNombre() + " cantó FLOR! (Rival no tiene)");
            verificarFinPartido();
        }
        modelo.notificarObservadores();
    }

    public void irseAlMazo(Jugador j) {
        if (modelo.getJugadorActual().equals(j)) {
            modelo.jugadorSeVaAlMazo(j);
            if (!verificarFinPartido()) {
                verificarFinRonda();
                modelo.notificarObservadores();
            }
        }
    }

    private String obtenerNombresEquipo(Equipo e) {
        if (e == null) return "";
        List<String> nombres = new ArrayList<>();
        for (Jugador j : e.getIntegrantes()) {
            nombres.add(j.getNombre());
        }
        return String.join(" y ", nombres);
    }

    private boolean verificarFinPartido() {
        int limite = modelo.getPuntosObjetivo();
        if (modelo.getEquipo1().getPuntos() >= limite || modelo.getEquipo2().getPuntos() >= limite) {
            modelo.notificarObservadores();
            Equipo campeon = (modelo.getEquipo1().getPuntos() >= limite) ? modelo.getEquipo1() : modelo.getEquipo2();

            String nombresCampeones = obtenerNombresEquipo(campeon);
            JOptionPane.showMessageDialog(null, "🏆 ¡LLEGARON A " + limite + " PUNTOS! 🏆\n\n¡" + nombresCampeones.toUpperCase() + " GANARON EL PARTIDO!");
            System.exit(0);
            return true;
        }
        return false;
    }

    private void verificarFinRonda() {
        if (modelo.isRondaFinalizada()) {
            modelo.notificarObservadores();

            String msgGanador = "Empate / Mazo";
            if (modelo.getGanadorRonda() != null) {
                Equipo ganador = modelo.getGanadorRonda();
                msgGanador = obtenerNombresEquipo(ganador) + " (" + ganador.getNombre() + ")";
            }

            JOptionPane.showMessageDialog(null, "🏆 Ganó la ronda: " + msgGanador + "\n\nPresiona OK para la siguiente ronda.", "Fin de Ronda", JOptionPane.INFORMATION_MESSAGE);

            modelo.rotarMano();
            modelo.repartirCartas();
        }
    }

    private int calcularFaltaEnvido() {
        int limite = modelo.getPuntosObjetivo();
        int mitad = limite / 2;
        Equipo win = (modelo.getEquipo1().getPuntos() > modelo.getEquipo2().getPuntos()) ? modelo.getEquipo1() : modelo.getEquipo2();
        return (win.getPuntos() >= mitad) ? (limite - win.getPuntos()) : limite;
    }

    private Equipo obtenerRival(Jugador j) {
        return (modelo.getEquipoDeJugador(j) == modelo.getEquipo1()) ? modelo.getEquipo2() : modelo.getEquipo1();
    }

    private String nombreEnvido(int c) { return c==99?"FALTA ENVIDO": c==3?"REAL ENVIDO":"ENVIDO"; }

    private String nombreTruco(int n) { return n==2?"TRUCO": n==3?"RETRUCO":"VALE CUATRO"; }

    private String infoCartas(Equipo e) {
        StringBuilder sb = new StringBuilder("SUS CARTAS:\n");
        for(Jugador j : e.getIntegrantes()) {
            sb.append("- ").append(j.getNombre()).append(": ");
            for(Carta c : j.getManoCartas()) sb.append("[").append(c).append("] ");
            sb.append(" (Envido: ").append(j.getTantosEnvido()).append(")\n");
        }
        return sb.toString();
    }

    public void guardarJuego() {
        try {
            GestorPersistencia.guardarPartida(modelo, "truco_save.dat");
            JOptionPane.showMessageDialog(null, "¡Partida guardada exitosamente!");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error al guardar: " + e.getMessage());
        }
    }
}