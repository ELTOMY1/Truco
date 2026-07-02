package controlador;

import modelo.JuegoModelo;
import modelo.Jugador;
import modelo.Carta;
import modelo.Equipo;
import vista.VistaConsola;
import java.util.ArrayList;
import java.util.List;

public class ControladorJuego {
    private JuegoModelo modelo;
    private VistaConsola vista;
    private boolean seJuegaConFlor;

    public ControladorJuego(JuegoModelo modelo, VistaConsola vista) {
        this.modelo = modelo;
        this.vista = vista;
    }

    public void iniciarJuego() {
        vista.mostrarBienvenida();
        this.seJuegaConFlor = vista.preguntarFlor();
        vista.mostrarMensaje("\n=== COMIENZA EL PARTIDO A " + modelo.getPuntosObjetivo() + " PUNTOS ===");

        while (true) {
            jugarRonda();
            if (verificarGanadorPartido()) break;
            modelo.rotarMano();
        }
    }

    private void jugarRonda() {
        modelo.repartirCartas();
        vista.mostrarMensaje("\n--- Nueva Mano (Barajando...) ---");

        boolean finRonda = false;
        while (!finRonda) {
            Jugador actual = modelo.getJugadorActual();
            vista.mostrarMesa(actual, modelo.getEquipoDeJugador(actual),
                    modelo.getRondaActual().getNivelTruco(),
                    modelo.getEquipo1().getPuntos(), modelo.getEquipo2().getPuntos());

            boolean puedeTruco = modelo.puedeCantarTruco(actual);
            VistaConsola.OpcionJugador opcion = vista.pedirAccion(
                    actual,
                    modelo.getRondaActual().sePuedeCantarEnvido(actual),
                    puedeTruco,
                    seJuegaConFlor && actual.tieneFlor() && !modelo.getRondaActual().florCantada
            );

            switch (opcion.tipo) {
                case "CARTA":
                    Carta c = actual.getManoCartas().get(opcion.valor);
                    modelo.jugadaTirarCarta(c);
                    vista.mostrarMensaje(">> " + actual.getNombre() + " tiró " + c);
                    break;
                case "ENVIDO": iniciarCicloEnvido(actual, opcion.valor); break;
                case "FLOR": iniciarCicloFlor(actual); break;
                case "TRUCO": finRonda = iniciarCicloTruco(actual); break;
                case "MAZO":
                    modelo.jugadorSeVaAlMazo(actual);
                    vista.mostrarMensaje(">> " + actual.getNombre() + " se fue al mazo.");
                    break;
            }

            if (modelo.isRondaFinalizada()) {
                finRonda = true;
                if (modelo.getGanadorRonda() != null) {
                    vista.mostrarMensaje("\n*** GANÓ LA RONDA: " + modelo.getGanadorRonda().getNombre() + " ***");
                }
            }
        }
    }

    private void iniciarCicloEnvido(Jugador iniciador, int codigoInicial) {
        vista.mostrarMensaje("!!! " + iniciador.getNombre() + " cantó " + nombreEnvido(codigoInicial) + " !!!");
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
            int r = vista.responderApuesta(resp, "Equipo " + resp.getNombre() + ":", opsArray);
            String seleccion = opsArray[r - 1];

            if (seleccion.equals("Quiero")) {
                Equipo g = modelo.resolverEnvido(acumulado);
                vista.mostrarMensaje("Ganó " + g.getNombre() + " (" + g.obtenerMejorEnvido() + " tantos)");
                return;
            } else if (seleccion.equals("No Quiero")) {
                vista.mostrarMensaje("No quisieron. " + siNo + " puntos para " + obtenerRival(resp.getIntegrantes().get(0)).getNombre());
                obtenerRival(resp.getIntegrantes().get(0)).sumarPuntos(siNo, modelo.getPuntosObjetivo());
                modelo.getRondaActual().envidoCantado = true;
                return;
            } else {
                siNo = acumulado;
                if(seleccion.equals("Envido")) { envidoDosVeces=true; estado=2; acumulado+=2; }
                if(seleccion.equals("Real Envido")) { estado=3; acumulado+=3; }
                if(seleccion.equals("Falta Envido")) { estado=99; acumulado=calcularFaltaEnvido(); }
                vista.mostrarMensaje("!!! " + resp.getNombre() + " cantó " + seleccion.toUpperCase() + " !!!");
                resp = obtenerRival(resp.getIntegrantes().get(0));
            }
        }
    }

    private void iniciarCicloFlor(Jugador iniciador) {
        vista.mostrarMensaje("✿ " + iniciador.getNombre() + " cantó FLOR ✿");
        Equipo equipoCanta = modelo.getEquipoDeJugador(iniciador);
        Equipo rival = obtenerRival(iniciador);

        if (rival.tieneFlor()) {
            int r = vista.responderApuesta(rival, "Equipo " + rival.getNombre() + ", también tienen Flor:",
                    new String[]{"Con Flor Quiero", "Contra Flor al Resto", "Me achico"});

            if (r == 1) {
                int f1 = equipoCanta.obtenerMejorFlor();
                int f2 = rival.obtenerMejorFlor();
                vista.mostrarMensaje("Flor Eq1: " + f1 + " vs Flor Eq2: " + f2);
                if (f1 >= f2) modelo.resolverFlor(equipoCanta, 6); else modelo.resolverFlor(rival, 6);
            } else if (r == 2) {
                int f1 = equipoCanta.obtenerMejorFlor();
                int f2 = rival.obtenerMejorFlor();
                Equipo g = (f1 >= f2) ? equipoCanta : rival;
                modelo.resolverFlor(g, modelo.getPuntosObjetivo());
                vista.mostrarMensaje("Ganó la flor al resto: " + g.getNombre());
            } else {
                modelo.resolverFlor(equipoCanta, 3);
            }
        } else {
            modelo.resolverFlor(equipoCanta, 3);
        }
    }

    private boolean iniciarCicloTruco(Jugador iniciador) {
        modelo.subirTruco(iniciador);
        // SOLUCIÓN BUG 2: Calculamos el nivel que se está proponiendo para la impresión
        int nivelPropuesto = modelo.getRondaActual().getNivelTruco() + 1;
        vista.mostrarMensaje("!!! " + iniciador.getNombre() + " cantó " + nombreTruco(nivelPropuesto) + " !!!");
        Equipo resp = obtenerRival(iniciador);

        while(true) {
            List<String> ops = new ArrayList<>();
            ops.add("Quiero"); ops.add("No Quiero");
            if(nivelPropuesto < 4) ops.add("Subir Apuesta");

            boolean puedeEnvidoEquipo = false;
            Jugador quienCantaEnvido = null;
            for (Jugador j : resp.getIntegrantes()) {
                if (modelo.getRondaActual().sePuedeCantarEnvido(j)) {
                    puedeEnvidoEquipo = true;
                    quienCantaEnvido = j;
                    break;
                }
            }

            if (puedeEnvidoEquipo) {
                ops.add("Envido");
                ops.add("Real Envido");
                ops.add("Falta Envido");
            }

            int r = vista.responderApuesta(resp, "Equipo " + resp.getNombre() + ":", ops.toArray(new String[0]));
            String seleccion = ops.get(r - 1);

            if (seleccion.equals("Quiero")) {
                modelo.getRondaActual().envidoAnulado = true;
                modelo.confirmarSubida(); // Confirmamos el aumento del truco en el modelo
                vista.mostrarMensaje("¡Quisieron!"); return false;
            } else if (seleccion.equals("No Quiero")) {
                modelo.confirmarSubida(); // Subimos el nivel para que la resta dé los puntos exactos
                modelo.noQuieroTruco(resp.getIntegrantes().get(0));
                vista.mostrarMensaje("No quisieron."); return true;
            } else if (seleccion.equals("Subir Apuesta")) {
                modelo.getRondaActual().envidoAnulado = true;
                modelo.confirmarSubida(); // Confirma el canto anterior
                modelo.subirTruco(resp.getIntegrantes().get(0)); // Propone el siguiente canto
                nivelPropuesto = modelo.getRondaActual().getNivelTruco() + 1; // Actualizamos
                vista.mostrarMensaje("!!! " + resp.getNombre() + " SUBIÓ A " + nombreTruco(nivelPropuesto) + " !!!");
                resp = obtenerRival(resp.getIntegrantes().get(0));
            } else if (seleccion.equals("Envido")) {
                iniciarCicloEnvido(quienCantaEnvido, 2);
                vista.mostrarMensaje("\n>>> Volviendo a responder el " + nombreTruco(nivelPropuesto) + "...");
            } else if (seleccion.equals("Real Envido")) {
                iniciarCicloEnvido(quienCantaEnvido, 3);
                vista.mostrarMensaje("\n>>> Volviendo a responder el " + nombreTruco(nivelPropuesto) + "...");
            } else if (seleccion.equals("Falta Envido")) {
                iniciarCicloEnvido(quienCantaEnvido, 99);
                vista.mostrarMensaje("\n>>> Volviendo a responder el " + nombreTruco(nivelPropuesto) + "...");
            }
        }
    }

    private String nombreEnvido(int c) { return c==99?"FALTA ENVIDO": c==3?"REAL ENVIDO":"ENVIDO"; }

    // SOLUCIÓN BUG 2: Arreglado el método para que devuelva los nombres correctos
    private String nombreTruco(int n) {
        if(n==1) return "NADA";
        if(n==2) return "TRUCO";
        if(n==3) return "RETRUCO";
        return "VALE CUATRO";
    }

    private int calcularFaltaEnvido() {
        int limite = modelo.getPuntosObjetivo();
        int maxPuntos = Math.max(modelo.getEquipo1().getPuntos(), modelo.getEquipo2().getPuntos());
        return (maxPuntos >= (limite / 2)) ? (limite - maxPuntos) : limite;
    }

    private Equipo obtenerRival(Jugador j) {
        return (modelo.getEquipoDeJugador(j) == modelo.getEquipo1()) ? modelo.getEquipo2() : modelo.getEquipo1();
    }

    private boolean verificarGanadorPartido() {
        if (modelo.getEquipo1().getPuntos() >= modelo.getPuntosObjetivo()) {
            vista.mostrarMensaje("\n🏆 ¡GANARON " + modelo.getEquipo1().getNombre().toUpperCase() + "! 🏆");
            return true;
        }
        if (modelo.getEquipo2().getPuntos() >= modelo.getPuntosObjetivo()) {
            vista.mostrarMensaje("\n🏆 ¡GANARON " + modelo.getEquipo2().getNombre().toUpperCase() + "! 🏆");
            return true;
        }
        return false;
    }
}