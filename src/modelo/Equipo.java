package modelo;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Equipo implements Serializable {
    private String nombre;
    private List<Jugador> integrantes;
    private int puntos;

    public Equipo(String nombre) {
        this.nombre = nombre;
        this.integrantes = new ArrayList<>();
        this.puntos = 0;
    }

    public void agregarJugador(Jugador j) { integrantes.add(j); }
    public List<Jugador> getIntegrantes() { return integrantes; }
    public String getNombre() { return nombre; }
    public int getPuntos() { return puntos; }

    public void sumarPuntos(int cantidad, int limite) {
        this.puntos += cantidad;
        if (this.puntos > limite) this.puntos = limite;
    }

    public int obtenerMejorEnvido() {
        int max = 0;
        for (Jugador j : integrantes) if (j.getTantosEnvido() > max) max = j.getTantosEnvido();
        return max;
    }

    public Jugador obtenerJugadorMejorEnvido() {
        Jugador mejor = null;
        for (Jugador j : integrantes) {
            if (mejor == null || j.getTantosEnvido() > mejor.getTantosEnvido()) mejor = j;
        }
        return mejor;
    }

    public boolean tieneFlor() {
        for (Jugador j : integrantes) if (j.tieneFlor()) return true;
        return false;
    }

    public int obtenerMejorFlor() {
        int max = 0;
        for (Jugador j : integrantes) if (j.tieneFlor() && j.getTantosFlor() > max) max = j.getTantosFlor();
        return max;
    }
}