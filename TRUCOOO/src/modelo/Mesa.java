package modelo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Mesa {

    private final List<Jugador> jugadores = new ArrayList<>();
    private final List<Carta> mazo = new ArrayList<>();
    private int puntosMax;
    private int manoQueEmpieza = 0;

    public Mesa(int p) {
        this.puntosMax = p;
    }

    public void setPuntosMax(int p) {
        this.puntosMax = p;
    }

    public void agregarJugador(Jugador j) {
        jugadores.add(j);
    }

    public List<Jugador> getJugadores() {
        return jugadores;
    }

    public int getPuntosMax() {
        return puntosMax;
    }

    public boolean estamosEnBuenas() {
        int mitad = puntosMax / 2;
        int maxPuntaje = 0;
        for (Jugador j : jugadores) {
            maxPuntaje = Math.max(maxPuntaje, j.getPuntos());
        }
        return maxPuntaje >= mitad;
    }

    public int puntosParaTerminar() {
        int maxPuntaje = 0;
        for (Jugador j : jugadores) {
            maxPuntaje = Math.max(maxPuntaje, j.getPuntos());
        }
        return Math.max(1, puntosMax - maxPuntaje);
    }

    public boolean hayGanador() {
        for (Jugador j : jugadores) {
            if (j.getPuntos() >= puntosMax) return true;
        }
        return false;
    }

    public int getManoQueEmpieza() {
        return manoQueEmpieza;
    }

    public void setManoQueEmpieza(int m) {
        if (!jugadores.isEmpty()) {
            manoQueEmpieza = m % jugadores.size();
        }
    }

    public void avanzarManoQueEmpieza(int n) {
        if (!jugadores.isEmpty()) {
            manoQueEmpieza = (manoQueEmpieza + n) % jugadores.size();
        }
    }

    public Jugador getJugador(int i) {
        return jugadores.get(i);
    }

    public int indexOf(Jugador j) {
        return jugadores.indexOf(j);
    }

    public Jugador siguienteJugador(Jugador actual) {
        int idx = indexOf(actual);
        return getJugador((idx + 1) % jugadores.size());
    }

    public void crearMazo() {
        mazo.clear();
        String[] palos = {"Oros", "Copas", "Espadas", "Bastos"};
        int[] vals = {1, 2, 3, 4, 5, 6, 7, 10, 11, 12};
        for (String p : palos) {
            for (int v : vals) {
                mazo.add(new Carta(v, p));
            }
        }
    }

    public void barajar() {
        Collections.shuffle(mazo);
    }

    public void repartir() {
        for (Jugador j : jugadores) j.limpiarMano();
        for (int i = 0; i < 3; i++) {
            for (Jugador j : jugadores) {
                if (!mazo.isEmpty()) {
                    j.agregarCarta(mazo.remove(mazo.size() - 1));
                }
            }
        }
    }
    public Jugador getJugadorGanador() {
        for (Jugador j : jugadores) if (j.getPuntos() >= puntosMax) return j;
        return null;
    }
}