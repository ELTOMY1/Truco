package modelo;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class JuegoModelo extends Observable implements Serializable {
    private List<Jugador> jugadores;
    private Equipo equipo1, equipo2;
    private Ronda rondaActual;
    private int turnoActual;
    private int indiceMano;
    private Jugador jugadorQueIniciaMano;
    private int puntosObjetivo;
    private boolean rondaFinalizada;
    private Equipo ganadorRonda;
    private Equipo equipoCantoTruco;

    public JuegoModelo(int cantidad, int puntosMax, String n1, String n2, String n3, String n4) {
        this.puntosObjetivo = puntosMax;
        jugadores = new ArrayList<>();
        equipo1 = new Equipo("Nosotros");
        equipo2 = new Equipo("Ellos");

        if (cantidad == 2) {
            crearJugador(n1, equipo1); crearJugador(n2, equipo2);
        } else {
            crearJugador(n1, equipo1); crearJugador(n2, equipo2);
            crearJugador(n3, equipo1); crearJugador(n4, equipo2);
        }
        this.indiceMano = 0;
        this.turnoActual = 0;
    }

    private void crearJugador(String nombre, Equipo e) {
        Jugador j = new Jugador(nombre);
        e.agregarJugador(j);
        jugadores.add(j);
    }

    public void repartirCartas() {
        Mazo mazo = new Mazo();
        mazo.barajar();
        this.rondaActual = new Ronda(jugadores);
        this.rondaFinalizada = false;
        this.ganadorRonda = null;
        this.equipoCantoTruco = null;

        for (Jugador j : jugadores) {
            j.limpiarMano();
            for(int i=0; i<3; i++) j.recibirCarta(mazo.darCarta());
            j.evaluarMano();
        }
        this.turnoActual = indiceMano;
        this.jugadorQueIniciaMano = jugadores.get(indiceMano);
        notificarObservadores();
    }

    public void jugadaTirarCarta(Carta c) {
        Jugador actual = getJugadorActual();

        // 1. Quitamos la carta de la mano y la ponemos en la mesa
        actual.getManoCartas().remove(c);
        rondaActual.jugarCarta(c, actual);

        // AVISAMOS A LA VISTA AQUÍ MISMO PARA QUE VEA LA CARTA ANTES DE QUE SE LIMPIE LA MESA
        notificarObservadores();

        // 2. Verificamos si con esta carta se completa la mano
        if (rondaActual.seTerminoLaMano()) {
            rondaActual.cerrarMano(equipo1, equipo2);
            Equipo ganadorDeRonda = rondaActual.determinarGanadorDeRonda(getEquipoDeJugador(getJugadorMano()));

            if (ganadorDeRonda != null) {
                this.ganadorRonda = ganadorDeRonda;
                this.rondaFinalizada = true;
                ganadorDeRonda.sumarPuntos(rondaActual.nivelTruco, puntosObjetivo);
            } else {
                Jugador jGana = rondaActual.getJugadorGanadorUltimaMano();
                if (jGana != null) {
                    this.turnoActual = jugadores.indexOf(jGana);
                    this.jugadorQueIniciaMano = jGana;
                } else {
                    this.turnoActual = jugadores.indexOf(this.jugadorQueIniciaMano);
                }
            }
        } else {
            siguienteTurno();
        }

        // 3. Notificamos de nuevo por los cambios de turno y puntos
        notificarObservadores();
    }

    public Equipo resolverEnvido(int puntosApostados) {
        int p1 = equipo1.obtenerMejorEnvido();
        int p2 = equipo2.obtenerMejorEnvido();
        Equipo ganador = (p1 > p2) ? equipo1 : (p2 > p1) ? equipo2 : getEquipoDeJugador(getJugadorMano());

        ganador.sumarPuntos(puntosApostados, puntosObjetivo);
        rondaActual.envidoCantado = true;
        return ganador;
    }

    public void resolverFlor(Equipo ganador, int puntos) {
        ganador.sumarPuntos(puntos, puntosObjetivo);
        rondaActual.florCantada = true;
        rondaActual.envidoCantado = true;
    }

    public void jugadorSeVaAlMazo(Jugador j) {
        Equipo rival = (getEquipoDeJugador(j) == equipo1) ? equipo2 : equipo1;
        int pts = rondaActual.nivelTruco;
        if (j.getManoCartas().size() == 3 && !rondaActual.envidoCantado) pts++;

        rival.sumarPuntos(pts, puntosObjetivo);
        this.rondaFinalizada = true;
        this.ganadorRonda = rival;
    }

    public void subirTruco(Jugador quienCanta) {
        rondaActual.trucoCantado = true;
        this.equipoCantoTruco = getEquipoDeJugador(quienCanta);
    }

    public void confirmarSubida() {
        if (rondaActual.nivelTruco < 4) {
            rondaActual.nivelTruco++;
        }
    }

    public boolean puedeCantarTruco(Jugador j) {
        if (rondaActual.nivelTruco == 4) return false;
        if (!rondaActual.trucoCantado) return true;
        return getEquipoDeJugador(j) != equipoCantoTruco;
    }

    public void noQuieroTruco(Jugador quienDijoNo) {
        Equipo rival = (getEquipoDeJugador(quienDijoNo) == equipo1) ? equipo2 : equipo1;
        rival.sumarPuntos(rondaActual.nivelTruco - 1, puntosObjetivo);
        this.rondaFinalizada = true;
        this.ganadorRonda = rival;
    }

    public void siguienteTurno() { turnoActual = (turnoActual + 1) % jugadores.size(); }
    public void rotarMano() { indiceMano = (indiceMano + 1) % jugadores.size(); }
    public Jugador getJugadorActual() { return jugadores.get(turnoActual); }
    public Equipo getEquipoDeJugador(Jugador j) { return equipo1.getIntegrantes().contains(j) ? equipo1 : equipo2; }
    public Jugador getJugadorMano() { return jugadores.get(indiceMano); }
    public Equipo getEquipo1() { return equipo1; }
    public Equipo getEquipo2() { return equipo2; }
    public boolean isRondaFinalizada() { return rondaFinalizada; }
    public Equipo getGanadorRonda() { return ganadorRonda; }
    public Ronda getRondaActual() { return rondaActual; }
    public int getPuntosObjetivo() { return puntosObjetivo; }
    public List<Jugador> getJugadores() {
        return this.jugadores;
    }
}