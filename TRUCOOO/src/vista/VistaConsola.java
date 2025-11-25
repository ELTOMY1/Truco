package vista;

import java.util.List;
import java.util.Scanner;

public class VistaConsola {
    private Scanner sc = new Scanner(System.in);

    public void mostrarEvento(String evento) {
        if (evento != null && !evento.isEmpty()) System.out.println("\n*** " + evento + " ***");
    }

    public void mostrarTexto(String s) {
        System.out.println(s);
    }

    public void mostrarMarcador(List<String> marcador) {
        System.out.println("\n--- MARCADOR ---");
        for (String m : marcador) System.out.println(m);
        System.out.println("----------------\n");
    }

    public void mostrarPantalla(String mensajeAccion, String nombreTurno, List<String> cartasTurno, List<String> opciones) {
        if (mensajeAccion != null && !mensajeAccion.isEmpty()) {
            System.out.println("\n--------------------------------------------------");
            System.out.println(" INFO: " + mensajeAccion);
            System.out.println("--------------------------------------------------");
        }
        if (nombreTurno != null) {
            System.out.println(">>> Turno de " + nombreTurno);
            if (cartasTurno != null && !cartasTurno.isEmpty()) {
                System.out.println("Cartas en mano:");
                for (int i = 0; i < cartasTurno.size(); i++) {
                    System.out.println(" " + i + ") " + cartasTurno.get(i));
                }
            }
        }

        if (opciones != null && !opciones.isEmpty()) {
            System.out.println("\nOpciones:");
            for (int i = 0; i < opciones.size(); i++) {
                System.out.println("(" + (i + 1) + ") " + opciones.get(i));
            }
            System.out.print("Elija opción: ");
        }
    }

    public int obtenerEntradaInt(int min, int max) {
        while (true) {
            try {
                String s = sc.nextLine();
                int val = Integer.parseInt(s);
                if (val >= min && val <= max) return val;
                System.out.print("Opción inválida ("+min+"-"+max+"): ");
            } catch (Exception e) { System.out.print("Entrada inválida: "); }
        }
    }

    public String pedirTexto(String msg) {
        System.out.print(msg);
        return sc.nextLine();
    }

    public int elegirCarta(String nombre, List<String> cartas) {
        System.out.println("Elige carta para tirar:");
        for(int i=0; i<cartas.size(); i++) System.out.println("(" + i + ") " + cartas.get(i));
        System.out.print("Índice: ");
        return obtenerEntradaInt(0, cartas.size() - 1);
    }
}