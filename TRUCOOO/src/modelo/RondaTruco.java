package modelo;

import java.util.List;

public class RondaTruco {

    public int determinarGanadorBaza(List<Carta> cartas, List<Jugador> ordenJugadores) {
        int mejorValor = -1;
        int indexGanador = -1;
        boolean empate = false;

        for (int i = 0; i < cartas.size(); i++) {
            int val = ReglasDelTruco.valorTruco(cartas.get(i));

            if (val > mejorValor) {
                mejorValor = val;
                indexGanador = i;
                empate = false;
            } else if (val == mejorValor) {
                empate = true;
            }
        }

        if (empate) return -1; // -1 significa (Empate)

        // Retornamos el equipo del jugador que tiró la carta ganadora
        return ordenJugadores.get(indexGanador).getEquipo();
    }

    public int determinarGanadorMano(int[] g, int equipoMano) {
        int g1 = g[0]; // Ganador 1ra baza
        int g2 = g[1]; // Ganador 2da baza
        int g3 = g[2]; // Ganador 3ra baza

        // Si nadie ganó la primera o segunda aún, sigue el juego
        if (g1 == 0 && g2 == 0) return 0;

        if (g2 != 0) { // Ya se jugó la segunda
            if (g1 != -1 && g1 == g2) return g1; // Alguien ganó 1ra y 2da -> Gana
            if (g1 == -1 && g2 != -1) return g2; // Parda en 1ra, gana quien gana 2da
            if (g1 != -1 && g2 == -1) return g1; // Gana 1ra, Parda en 2da -> Gana el de 1ra
            if (g1 != -1 && g2 != -1 && g1 != g2) {
                // Van 1 a 1, se define en la 3ra.
                if (g3 == 0) return 0; // Falta jugar 3ra
            }
        }

        if (g3 != 0) { // Ya se jugó la tercera
            if (g3 != -1) return g3; // Gana quien gana la 3ra (si iban 1-1)

            if (g1 != -1) return g1; // Si hubo ganador en 1ra (y perdió 2da), gana el de 1ra

            // Triple Parda -> Gana el Mano
            return equipoMano;
        }

        return 0; // Sin ganador
    }

    public Jugador obtenerJugadorGanadorBaza(List<Carta> cartas, List<Jugador> ordenJugadores) {
        int mejorValor = -1;
        int indexGanador = -1;
        boolean empate = false;

        for (int i = 0; i < cartas.size(); i++) {
            int val = ReglasDelTruco.valorTruco(cartas.get(i));
            if (val > mejorValor) {
                mejorValor = val;
                indexGanador = i;
                empate = false;
            } else if (val == mejorValor) {
                empate = true;
            }
        }

        if (empate) return null;
        return ordenJugadores.get(indexGanador);
    }
}