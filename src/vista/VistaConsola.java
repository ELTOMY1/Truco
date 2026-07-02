package vista;

import modelo.Jugador;
import modelo.Equipo;
import modelo.Carta;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

public class VistaConsola {
    private Map<String, VentanaConsolaGrafica> ventanas;

    public VistaConsola(Map<String, VentanaConsolaGrafica> ventanas) {
        this.ventanas = ventanas;
    }

    public void mostrarBienvenida() {
        mostrarMensaje("\n=========================================");
        mostrarMensaje("       TRUCO ARGENTINO - SIMULADOR");
        mostrarMensaje("=========================================");
    }

    public boolean preguntarFlor() {
        if (ventanas.isEmpty()) return false;
        // Tomamos una ventana cualquiera para la configuración inicial
        VentanaConsolaGrafica v = ventanas.values().iterator().next();
        v.escribir("\n¿Jugar con Flor?");
        v.escribir("1) Sí");
        v.escribir("2) No");
        return leerEntero(v, 1, 2) == 1;
    }

    public void mostrarMesa(Jugador j, Equipo e, int nivelTruco, int p1, int p2) {
        VentanaConsolaGrafica v = ventanas.get(j.getNombre());
        if (v != null) {
            v.escribir("\n-------------------------------------------");
            v.escribir("MARCADOR: NOSOTROS [" + p1 + "] - ELLOS [" + p2 + "]");
            v.escribir("TURNO: " + j.getNombre() + " (" + e.getNombre() + ")");
            v.escribir("VALOR ACTUAL: " + nombreTruco(nivelTruco));
            v.escribir("-------------------------------------------");
            v.escribir("TUS CARTAS:");
            for (Carta c : j.getManoCartas()) {
                v.escribir(" [" + c + "]");
            }
        }
    }

    public OpcionJugador pedirAccion(Jugador j, boolean env, boolean puedeTruco, boolean flor) {
        VentanaConsolaGrafica v = ventanas.get(j.getNombre());
        if (v == null) return new OpcionJugador("MAZO", 0);

        List<OpcionJugador> ops = new ArrayList<>();
        int idx = 1;

        v.escribir("\n¿QUÉ DESEAS HACER?");
        for (int i = 0; i < j.getManoCartas().size(); i++) {
            v.escribir(idx++ + ") Tirar " + j.getManoCartas().get(i));
            ops.add(new OpcionJugador("CARTA", i));
        }

        if (flor) { v.escribir(idx++ + ") CANTAR FLOR"); ops.add(new OpcionJugador("FLOR", 0)); }
        if (env) {
            v.escribir(idx++ + ") ENVIDO"); ops.add(new OpcionJugador("ENVIDO", 2));
            v.escribir(idx++ + ") REAL ENVIDO"); ops.add(new OpcionJugador("ENVIDO", 3));
            v.escribir(idx++ + ") FALTA ENVIDO"); ops.add(new OpcionJugador("ENVIDO", 99));
        }
        if (puedeTruco) {
            v.escribir(idx++ + ") TRUCO / SUBIR"); ops.add(new OpcionJugador("TRUCO", 0));
        }

        v.escribir(idx + ") IRSE AL MAZO");
        ops.add(new OpcionJugador("MAZO", 0));

        v.escribir(">> Elige opción: ");
        return ops.get(leerEntero(v, 1, ops.size()) - 1);
    }

    public int responderApuesta(Equipo e, String msg, String[] ops) {
        // Notificar a todo el equipo
        for (Jugador j : e.getIntegrantes()) {
            VentanaConsolaGrafica v = ventanas.get(j.getNombre());
            if (v != null) {
                v.escribir("\n>>> " + msg);
                for (int i = 0; i < ops.length; i++) {
                    v.escribir((i + 1) + ") " + ops[i]);
                }
            }
        }

        // Asumimos que el primer integrante responde por el equipo
        Jugador responde = e.getIntegrantes().get(0);
        VentanaConsolaGrafica vResponde = ventanas.get(responde.getNombre());

        if (vResponde != null) {
            vResponde.escribir("> Tu respuesta: ");
            return leerEntero(vResponde, 1, ops.length);
        }
        return 2; // Default "No quiero" si algo falla
    }

    public void mostrarMensaje(String m) {
        for (VentanaConsolaGrafica v : ventanas.values()) {
            v.escribir(m);
        }
    }

    private int leerEntero(VentanaConsolaGrafica v, int min, int max) {
        while (true) {
            try {
                // Aquí el hilo se bloquea hasta que el usuario presiona ENTER en la GUI
                String input = v.leerComando();
                int val = Integer.parseInt(input.trim());
                if (val >= min && val <= max) return val;
                v.escribir("Opción fuera de rango.");
            } catch (Exception e) {
                v.escribir("Entrada inválida. Intenta nuevamente:");
            }
        }
    }

    private String nombreTruco(int n) {
        switch (n) {
            case 2: return "Truco (2 pts)";
            case 3: return "Retruco (3 pts)";
            case 4: return "Vale 4 (4 pts)";
            default: return "Nada (1 pto)";
        }
    }

    public static class OpcionJugador {
        public String tipo;
        public int valor;
        public OpcionJugador(String t, int v) { this.tipo = t; this.valor = v; }
    }
}