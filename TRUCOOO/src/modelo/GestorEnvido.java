package modelo;

import java.util.List;
import java.util.Collections;

public class GestorEnvido {

    private Mesa mesa;

    public GestorEnvido(Mesa mesa) {
        this.mesa = mesa;
    }

    // CÁLCULO DE PUNTOS AL RECHAZAR
    public int calcularPuntosNoQuiero(List<String> c) {
        int envidos = Collections.frequency(c, "ENVIDO");
        boolean real = c.contains("REAL ENVIDO");
        boolean falta = c.contains("FALTA ENVIDO");

        // 1. CASOS CON FALTA ENVIDO RECHAZADA
        if (falta) {
            if (real && envidos == 2) return 7; // Envido + Envido + Real + Falta -> No = 7 pts
            if (real && envidos == 1) return 5; // Envido + Real + Falta -> No = 5 pts
            if (real) return 3;                 // Real + Falta -> No = 3 pts
            if (envidos == 2) return 4;         // Envido + Envido + Falta -> No = 4 pts
            if (envidos == 1) return 2;         // Envido + Falta -> No = 2 pts
            return 1;                           // Falta sola -> No = 1 pt
        }

        // 2. CASOS CON REAL ENVIDO RECHAZADO
        if (real) {
            if (envidos == 2) return 4; // Envido + Envido + Real -> No = 4 pts
            if (envidos == 1) return 2; // Envido + Real -> No = 2 pts
            return 1;                   // Real solo -> No = 1 pt
        }

        // 3. CASOS DE SOLO ENVIDO
        if (envidos >= 2) return 2; // Envido + Envido -> No = 2 pts
        return 1;                   // Envido -> No = 1 pt
    }

    // CÁLCULO DE PUNTOS AL ACEPTAR
    public int calcularPuntosQuiero(List<String> c, int equipoGanador) {
        int envidos = Collections.frequency(c, "ENVIDO");
        boolean real = c.contains("REAL ENVIDO");
        boolean falta = c.contains("FALTA ENVIDO");

        if (falta) {
            if (!mesa.estamosEnBuenas()) {
                return mesa.getPuntosMax(); // Gana el partido directo
            } else {
                return mesa.puntosParaTerminar(); // Puntos para terminar
            }
        }

        // Tabla de puntos queridos
        if (real && envidos == 2) return 7;
        if (real && envidos == 1) return 5;
        if (real) return 3;
        if (envidos == 2) return 4;
        if (envidos == 1) return 2;

        return 0;
    }

    public Jugador determinarGanador(Jugador j1, Jugador j2) {
        int v1 = ReglasDelTruco.valorEnvidoMano(j1.getCartasParaEnvido());
        int v2 = ReglasDelTruco.valorEnvidoMano(j2.getCartasParaEnvido());
        // En caso de empate gana el mano.
        return (v1 >= v2) ? j1 : j2;
    }
}