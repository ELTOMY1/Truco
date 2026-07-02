package vista;

import javax.swing.*;

public class ConfiguradorUI {
    public static int elegirModo() {
        String[] opciones = {"1 vs 1", "2 vs 2"};
        int seleccion = JOptionPane.showOptionDialog(null, "¿Modo de Juego?", "Truco",
                JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, opciones, opciones[0]);
        return (seleccion == 1) ? 4 : 2;
    }

    public static int elegirPuntos() {
        String[] opciones = {"15 Puntos", "30 Puntos"};
        int seleccion = JOptionPane.showOptionDialog(null, "¿A cuánto jugamos?", "Truco",
                JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, opciones, opciones[0]);
        return (seleccion == 0) ? 15 : 30;
    }

    public static boolean elegirFlor() {
        return JOptionPane.showConfirmDialog(null, "¿Jugar con Flor?", "Truco",
                JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
    }
}