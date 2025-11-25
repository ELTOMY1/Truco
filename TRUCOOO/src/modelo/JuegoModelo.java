package modelo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class JuegoModelo implements Observable {
    private List<Observador> observadores = new ArrayList<>();

    private Mesa mesa;
    private GestorTruco gestorTruco;
    private GestorEnvido gestorEnvido;
    private GestorFlor gestorFlor;
    private RondaTruco rondaLogica;

    private boolean juegoTerminado;
    private String mensajeUltimaAccion;
    private boolean conFlor;
    private String nombreGanador;

    private int turnoActualIndex;
    private int nroBaza;
    private List<Carta> cartasEnMesa;
    private int[] ganadoresBazas;

    private enum EstadoJuego { JUGANDO, DISPUTA_ENVIDO, DISPUTA_TRUCO, DISPUTA_FLOR }
    private EstadoJuego estadoActual;
    private boolean trucoPendienteDePrenvido;

    private List<String> cadenaCanto;
    private Jugador jugadorQueCanto;

    private boolean envidoCantado;
    private boolean florCantada;
    private boolean florEnJuego;

    public JuegoModelo() {
        this.mesa = new Mesa(15);
        this.gestorTruco = new GestorTruco(mesa);
        this.gestorEnvido = new GestorEnvido(mesa);
        this.gestorFlor = new GestorFlor(mesa);
        this.rondaLogica = new RondaTruco();
        this.cartasEnMesa = new ArrayList<>();
        this.ganadoresBazas = new int[]{0, 0, 0};
        this.cadenaCanto = new ArrayList<>();
    }

    public void iniciarPartida(String n1, String n2, boolean conFlor, int pts) {
        mesa.setPuntosMax(pts);
        this.conFlor = conFlor;
        mesa.agregarJugador(new Jugador(n1, 1));
        mesa.agregarJugador(new Jugador(n2, 2));
        nuevaMano("¡Partida iniciada!");
    }

    private void nuevaMano(String msg) {
        if (mesa.hayGanador()) {
            juegoTerminado = true;
            nombreGanador = mesa.getJugadorGanador().getNombre();
            mensajeUltimaAccion = msg + "\n¡JUEGO TERMINADO!";
            notificarObservadores();
            return;
        }
        mesa.crearMazo(); mesa.barajar(); mesa.repartir();
        gestorTruco.reset();
        nroBaza = 1;
        cartasEnMesa.clear();
        ganadoresBazas = new int[]{0, 0, 0};
        envidoCantado = false; florCantada = false; florEnJuego = false;
        estadoActual = EstadoJuego.JUGANDO;
        trucoPendienteDePrenvido = false;
        cadenaCanto.clear();
        turnoActualIndex = mesa.getManoQueEmpieza();
        mensajeUltimaAccion = msg + "\n[NUEVA MANO]";
        notificarObservadores();
    }

    public void procesarAccion(String accion, int idxCarta) {
        Jugador actual = getJugadorActual();

        if (accion.contains("TIRAR") || accion.contains("SEGUIR")) {
            Carta c = actual.tirarCarta(idxCarta);
            cartasEnMesa.add(c);
            // Guardamos el mensaje base: "Jugador tiró Carta"
            mensajeUltimaAccion = actual.getNombre() + " tiró " + c;
            avanzarTurnoBaza();
            return;
        }

        if (accion.equals("MAZO")) {
            int pts = gestorTruco.getPuntosTruco();
            if (actual.getMano().size() == 3) pts = 2;
            sumarPuntosRival(actual, pts);
            nuevaMano(actual.getNombre() + " se fue al mazo. Rival +" + pts);
            return;
        }

        if (accion.contains("FLOR") || accion.contains("CONTRA FLOR")) {
            if (accion.equals("FLOR") && estadoActual == EstadoJuego.DISPUTA_TRUCO) {
                trucoPendienteDePrenvido = true;
            }
            procesarCantoFlor(actual, accion);
            return;
        }

        if (accion.contains("ENVIDO")) {
            if (estadoActual == EstadoJuego.DISPUTA_ENVIDO) {
                cadenaCanto.add(accion);
                jugadorQueCanto = actual;
                mensajeUltimaAccion = actual.getNombre() + " cantó " + accion;
                cambiarTurno();
                notificarObservadores();
                return;
            }
            if (estadoActual == EstadoJuego.DISPUTA_TRUCO) {
                trucoPendienteDePrenvido = true;
                mensajeUltimaAccion = actual.getNombre() + " cantó " + accion + " (¡Prenvido!)";
            } else {
                mensajeUltimaAccion = actual.getNombre() + " cantó " + accion;
            }
            iniciarDisputa(actual, accion, EstadoJuego.DISPUTA_ENVIDO);
            return;
        }

        if (accion.contains("TRUCO") || accion.contains("VALE")) {
            mensajeUltimaAccion = actual.getNombre() + " cantó " + accion;
            if (accion.contains("VALE")) gestorTruco.cantarValeCuatro(actual);
            else if (accion.contains("RETRUCO")) gestorTruco.cantarRetruco(actual);
            else gestorTruco.cantarTruco(actual);
            estadoActual = EstadoJuego.DISPUTA_TRUCO;
            cambiarTurno();
            notificarObservadores();
            return;
        }

        if (accion.equals("QUIERO")) procesarQuiero(actual);
        else if (accion.equals("NO QUIERO") || accion.contains("ACHICO")) procesarNoQuiero(actual);
    }

    private void iniciarDisputa(Jugador j, String canto, EstadoJuego nuevoEstado) {
        cadenaCanto.clear();
        cadenaCanto.add(canto);
        jugadorQueCanto = j;
        estadoActual = nuevoEstado;
        if (nuevoEstado == EstadoJuego.DISPUTA_ENVIDO) envidoCantado = true;
        if (nuevoEstado == EstadoJuego.DISPUTA_FLOR) { florCantada = true; florEnJuego = true; }
        cambiarTurno();
        notificarObservadores();
    }

    private void procesarCantoFlor(Jugador j, String canto) {
        if (!florCantada) {
            Jugador rival = mesa.siguienteJugador(j);
            if (ReglasDelTruco.tieneFlor(rival.getCartasParaEnvido())) {
                iniciarDisputa(j, "FLOR", EstadoJuego.DISPUTA_FLOR);
            } else {
                florCantada = true; florEnJuego = true;
                sumarPuntos(j.getEquipo(), 3);
                String msg = j.getNombre() + " tiene FLOR. Rival no tiene. +3 pts.";
                if (trucoPendienteDePrenvido) {
                    trucoPendienteDePrenvido = false;
                    estadoActual = EstadoJuego.DISPUTA_TRUCO;
                    mensajeUltimaAccion = msg + "\n(Ahora responde al Truco...)";
                } else {
                    mensajeUltimaAccion = msg;
                }
                notificarObservadores();
            }
        } else {
            cadenaCanto.add(canto);
            jugadorQueCanto = j;
            mensajeUltimaAccion = j.getNombre() + " cantó " + canto;
            cambiarTurno();
            notificarObservadores();
        }
    }

    private void procesarQuiero(Jugador resp) {
        if (estadoActual == EstadoJuego.DISPUTA_TRUCO) {
            gestorTruco.cantarQuiero(resp);
            estadoActual = EstadoJuego.JUGANDO;
            mensajeUltimaAccion = "QUIERO TRUCO.";
            restaurarTurnoJuego();
        } else if (estadoActual == EstadoJuego.DISPUTA_ENVIDO) {
            Jugador g = gestorEnvido.determinarGanador(resp, jugadorQueCanto);
            int pts = gestorEnvido.calcularPuntosQuiero(cadenaCanto, g.getEquipo());
            sumarPuntos(g.getEquipo(), pts);
            mensajeUltimaAccion = mostrarPuntosEnvido(resp, jugadorQueCanto) + "\nGana Envido " + g.getNombre() + " (" + pts + " pts)";
            finalizarDisputaEnvido();
        } else if (estadoActual == EstadoJuego.DISPUTA_FLOR) {
            int pts = gestorFlor.calcularPuntosQuiero(cadenaCanto.get(cadenaCanto.size()-1));
            Jugador g = gestorFlor.determinarGanadorFlor(resp, jugadorQueCanto);
            sumarPuntos(g.getEquipo(), pts);
            mensajeUltimaAccion = "Flor Querida. Gana " + g.getNombre() + " (" + pts + " pts)";
            finalizarDisputaEnvido();
        }
        notificarObservadores();
    }

    private void procesarNoQuiero(Jugador resp) {
        if (estadoActual == EstadoJuego.DISPUTA_TRUCO) {
            int pts = gestorTruco.puntosSiNoQuiero();
            sumarPuntosRival(resp, pts);
            nuevaMano("NO QUIERO TRUCO. Rival +" + pts);
        } else if (estadoActual == EstadoJuego.DISPUTA_ENVIDO) {
            int pts = gestorEnvido.calcularPuntosNoQuiero(cadenaCanto);
            sumarPuntosRival(resp, pts);
            mensajeUltimaAccion = "NO QUIERO ENVIDO. Rival +" + pts;
            finalizarDisputaEnvido();
            notificarObservadores();
        } else if (estadoActual == EstadoJuego.DISPUTA_FLOR) {
            int pts = gestorFlor.calcularPuntosNoQuiero(cadenaCanto.get(cadenaCanto.size()-1));
            sumarPuntosRival(resp, pts);
            mensajeUltimaAccion = "NO QUIERO FLOR. Rival +" + pts;
            finalizarDisputaEnvido();
            notificarObservadores();
        }
    }

    private void finalizarDisputaEnvido() {
        estadoActual = EstadoJuego.JUGANDO;
        if (trucoPendienteDePrenvido) {
            trucoPendienteDePrenvido = false;
            estadoActual = EstadoJuego.DISPUTA_TRUCO;
            mensajeUltimaAccion += "\n(Ahora responde al Truco...)";
        } else restaurarTurnoJuego();
    }

    // ---BAZA ---
    private void avanzarTurnoBaza() {
        if (cartasEnMesa.size() == 2) {
            // Se completó la baza
            List<Jugador> orden = new ArrayList<>();
            orden.add(mesa.siguienteJugador(getJugadorActual()));
            orden.add(getJugadorActual());

            // Calculamos ganador baza
            int g = rondaLogica.determinarGanadorBaza(cartasEnMesa, orden);
            Jugador ganadorBazaObj = rondaLogica.obtenerJugadorGanadorBaza(cartasEnMesa, orden);

            ganadoresBazas[nroBaza-1] = g;

            int eqMano = mesa.getJugador(mesa.getManoQueEmpieza()).getEquipo();
            int gMano = rondaLogica.determinarGanadorMano(ganadoresBazas, eqMano);

            if (gMano != 0) {
                int pts = gestorTruco.getPuntosTruco();
                sumarPuntos(gMano, pts);
                String txtBaza = (g != -1) ? "Gana baza " + ganadorBazaObj.getNombre() : "Parda la baza";
                nuevaMano(mensajeUltimaAccion + "\n" + txtBaza + ".\n¡MANO TERMINADA! Gana Equipo " + gMano);
            } else {
                // Seguimos a la siguiente baza
                nroBaza++;
                cartasEnMesa.clear();

                // Definimos quién tira primero en la siguiente
                if (g != -1) {
                    for(Jugador j:mesa.getJugadores()) if(j.getEquipo()==g) turnoActualIndex=mesa.indexOf(j);
                } else {
                    cambiarTurno();
                }

                String txtGanador = (g != -1) ? "Gana baza " + ganadorBazaObj.getNombre() : "Parda la baza";
                mensajeUltimaAccion += "\n*** " + txtGanador + ". Comienza Baza " + nroBaza + " ***";

                notificarObservadores();
            }
        } else {
            cambiarTurno();
            notificarObservadores();
        }
    }

    private void restaurarTurnoJuego() {
        if (cartasEnMesa.isEmpty()) {
            if (nroBaza == 1) turnoActualIndex = mesa.getManoQueEmpieza();
            else {
                int gAnt = ganadoresBazas[nroBaza-2];
                if (gAnt != -1) for(Jugador j:mesa.getJugadores()) if(j.getEquipo()==gAnt) turnoActualIndex=mesa.indexOf(j);
                else turnoActualIndex = mesa.getManoQueEmpieza();
            }
        }
    }

    private void cambiarTurno() { turnoActualIndex = mesa.indexOf(mesa.siguienteJugador(getJugadorActual())); }
    private void sumarPuntos(int eq, int pts) { for(Jugador j : mesa.getJugadores()) if(j.getEquipo()==eq) j.sumarPuntos(pts); }
    private void sumarPuntosRival(Jugador j, int pts) { sumarPuntos(j.getEquipo()==1?2:1, pts); }
    private String mostrarPuntosEnvido(Jugador j1, Jugador j2) { return j1.getNombre() + ": " + ReglasDelTruco.valorEnvido(j1.getCartasParaEnvido()) + " | " + j2.getNombre() + ": " + ReglasDelTruco.valorEnvido(j2.getCartasParaEnvido()); }

    public Jugador getJugadorActual() { return mesa.getJugador(turnoActualIndex); }
    public List<Jugador> getJugadores() { return mesa.getJugadores(); }
    public String getMensajeUltimaAccion() { return mensajeUltimaAccion; }
    public boolean isJuegoTerminado() { return juegoTerminado; }
    public String getNombreGanador() { return nombreGanador; }
    public String getEstadoMesaInfo() { return "** Truco = " + gestorTruco.getPuntosTruco() + " pts **"; }

    public List<String> getOpcionesDisponibles() {
        List<String> ops = new ArrayList<>();
        Jugador actual = getJugadorActual();

        if (estadoActual == EstadoJuego.DISPUTA_ENVIDO || estadoActual == EstadoJuego.DISPUTA_FLOR) {
            ops.add("QUIERO"); ops.add("NO QUIERO");
            if (estadoActual == EstadoJuego.DISPUTA_ENVIDO) {
                int cantEnv = Collections.frequency(cadenaCanto, "ENVIDO");
                boolean hayReal = cadenaCanto.contains("REAL ENVIDO");
                boolean hayFalta = cadenaCanto.contains("FALTA ENVIDO");
                if (!hayFalta && !hayReal && cantEnv < 2) ops.add("ENVIDO");
                if (!hayFalta && !hayReal) ops.add("REAL ENVIDO");
                if (!hayFalta) ops.add("FALTA ENVIDO");
            } else {
                String ultimo = cadenaCanto.get(cadenaCanto.size()-1);
                if(ultimo.equals("FLOR")) { ops.add("CONTRA FLOR"); ops.add("CONTRA FLOR AL RESTO"); }
                else if(ultimo.equals("CONTRA FLOR")) ops.add("CONTRA FLOR AL RESTO");
            }
            return ops;
        }

        if (estadoActual == EstadoJuego.DISPUTA_TRUCO) {
            ops.add("QUIERO"); ops.add("NO QUIERO");
            if (gestorTruco.getPuntosTruco() == 2) ops.add("RETRUCO");
            else if (gestorTruco.getPuntosTruco() == 3) ops.add("VALE CUATRO");
            if (nroBaza == 1 && !envidoCantado && !trucoPendienteDePrenvido && !florCantada && gestorTruco.getPuntosTruco() == 2) {
                if (conFlor && ReglasDelTruco.tieneFlor(actual.getCartasParaEnvido())) ops.add("FLOR");
                else { ops.add("ENVIDO"); ops.add("REAL ENVIDO"); ops.add("FALTA ENVIDO"); }
            }
            return ops;
        }

        ops.add("SEGUIR (tirar carta)"); ops.add("MAZO");
        if (!gestorTruco.isTrucoActivo()) ops.add("TRUCO");
        else if (gestorTruco.puedeSubir(actual)) {
            if (gestorTruco.getPuntosTruco() == 2) ops.add("RETRUCO");
            if (gestorTruco.getPuntosTruco() == 3) ops.add("VALE CUATRO");
        }
        if (nroBaza == 1 && !envidoCantado && !gestorTruco.isTrucoActivo()) {
            if (conFlor && ReglasDelTruco.tieneFlor(actual.getCartasParaEnvido()) && !florCantada) ops.add("FLOR");
            if (!florCantada) { ops.add("ENVIDO"); ops.add("REAL ENVIDO"); ops.add("FALTA ENVIDO"); }
        }
        return ops;
    }

    @Override public void agregarObservador(Observador o) { observadores.add(o); }
    @Override public void quitarObservador(Observador o) { observadores.remove(o); }
    @Override public void notificarObservadores() { for(Observador o:observadores) o.actualizar(); }
}