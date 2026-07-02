package modelo;

import java.io.Serializable;
import java.util.List;

public class Ronda implements Serializable {
    private Mesa mesa;
    private List<Jugador> ordenJugadores;
    private int manoActual;
    private Equipo[] ganadoresManos;
    private Jugador jugadorGanadorUltimaMano;
    private String[] historialManos;
    private String mensajeApuestas = "";
    public boolean envidoCantado = false;
    public boolean florCantada = false;
    public boolean trucoCantado = false;
    public boolean envidoAnulado = false;
    public int nivelTruco = 1;

    public Ronda(List<Jugador> jugadores) {
        this.ordenJugadores = jugadores;
        this.mesa = new Mesa();
        this.manoActual = 0;
        this.ganadoresManos = new Equipo[3];
        this.historialManos = new String[3];
    }

    public void jugarCarta(Carta c, Jugador j) {
        mesa.recibirCarta(c, j);
    }

    public boolean seTerminoLaMano() {
        return mesa.estaCompleta(ordenJugadores.size());
    }

    //Para que el Controlador sepa quién ganó antes de borrar la mesa
    public Equipo determinarEquipoGanadorMano(Equipo e1, Equipo e2) {
        Jugador jg = mesa.determinarJugadorGanador(e1, e2);
        if (jg == null) return null; // Parda
        return e1.getIntegrantes().contains(jg) ? e1 : e2;
    }

    public Equipo cerrarMano(Equipo eq1, Equipo eq2) {
        Jugador jGanador = mesa.determinarJugadorGanador(eq1, eq2);
        Equipo ganador = null;

        if (jGanador != null) {
            ganador = eq1.getIntegrantes().contains(jGanador) ? eq1 : eq2;
            this.jugadorGanadorUltimaMano = jGanador;
        } else {
            this.jugadorGanadorUltimaMano = null; // Emparde
        }

        if (manoActual < 3) {
            ganadoresManos[manoActual] = ganador;
            historialManos[manoActual] = (ganador == null) ? "Parda" : "Ganó " + ganador.getNombre();
        }

        mesa.limpiarMesa();
        manoActual++;
        return ganador;
    }

    public Equipo determinarGanadorDeRonda(Equipo equipoMano) {
        if (manoActual >= 2) {
            int victoriasEq1 = 0;
            int victoriasEq2 = 0;

            for (int i = 0; i < manoActual; i++) {
                if (ganadoresManos[i] != null) {
                    if (ganadoresManos[i].getNombre().equals("Nosotros")) victoriasEq1++;
                    else victoriasEq2++;
                }
            }

            if (victoriasEq1 >= 2) return (ganadoresManos[0] != null && ganadoresManos[0].getNombre().equals("Nosotros")) ? ganadoresManos[0] : ganadoresManos[1];
            if (victoriasEq2 >= 2) return (ganadoresManos[0] != null && ganadoresManos[0].getNombre().equals("Ellos")) ? ganadoresManos[0] : ganadoresManos[1];

            // Si hay parda en la primera, gana el de la segunda
            if (ganadoresManos[0] == null && manoActual >= 2 && ganadoresManos[1] != null) return ganadoresManos[1];

            // Si se jugaron las 3 y persiste empate técnico, gana el equipo que era mano
            if (manoActual == 3) {
                if (victoriasEq1 > victoriasEq2) return (ganadoresManos[0] != null) ? ganadoresManos[0] : equipoMano;
                if (victoriasEq2 > victoriasEq1) return (ganadoresManos[0] != null) ? ganadoresManos[0] : equipoMano;
                return equipoMano;
            }
        }
        return null;
    }

    public Mesa getMesa() { return mesa; }
    public int getManoActual() { return manoActual; }
    public Jugador getJugadorGanadorUltimaMano() { return jugadorGanadorUltimaMano; }
    public int getNivelTruco() { return nivelTruco; }

    public boolean sePuedeCantarEnvido(Jugador actual) {
        // 1. Si ya se cantó, si se anuló o si ya pasó la primera mano, NO se puede.
        if (envidoAnulado || envidoCantado || manoActual > 0) return false;

        // 2. Si el jugador ya tiró su carta en esta mano, no puede iniciar envido.
        // (Asumimos que empieza con 3 cartas, si tiene menos es que ya jugó).
        if (actual.getManoCartas().size() < 3) return false;

        // 3. Si el Truco ya fue QUERIDO (nivel > 1), el envido ya no existe.
        // el envido solo se puede cantar antes del truco
        // o como respuesta inmediata al canto de truco.
        if (nivelTruco > 1) return false;

        // 4. Lógica para 2 vs 2
        if (ordenJugadores.size() == 4) {
            // Si alguien gritó Truco pero todavía no se aceptó (nivel sigue en 1),
            // se puede responder con Envido.
            if (trucoCantado) return true;

            // Si nadie cantó nada, esperamos a que haya un par de cartas en mesa
            // para darle dinamismo al 2v2 (opcional, según tu gusto).
            return mesa.getCartasEnMesa().size() >= 2;
        }

        // 5. En 1 vs 1, si no se aceptó el truco y es la primera mano, se puede.
        return true;
    }

    public String getResumenRonda() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < manoActual; i++) {
            if (historialManos[i] != null) sb.append(historialManos[i]).append("\n");
        }
        return sb.toString();
    }

    public void setMensajeApuestas(String msj) { this.mensajeApuestas = msj; }
    public String getMensajeApuestas() { return mensajeApuestas; }
}