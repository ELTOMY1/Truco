package modelo;

import java.util.List;

public class GestorFlor {

    private Mesa mesa;

    public GestorFlor(Mesa mesa) {
        this.mesa = mesa;
    }

    public int calcularPuntosNoQuiero(String ultimoCanto) {
        switch (ultimoCanto) {
            case "CONTRA FLOR": return 4;
            case "CONTRA FLOR AL RESTO": return 6;
            default: return 3; // "Con flor me achico" base
        }
    }

    public int calcularPuntosQuiero(String ultimoCanto) {
        switch (ultimoCanto) {
            case "FLOR": return 3; // Flor simple sin disputa
            case "CONTRA FLOR": return 6;
            case "CONTRA FLOR AL RESTO": return mesa.puntosParaTerminar(); // Igual que falta envido
            default: return 4;
        }
    }

    public Jugador determinarGanadorFlor(Jugador j1, Jugador j2) {
        int v1 = ReglasDelTruco.valorFlor(j1.getMano());
        int v2 = ReglasDelTruco.valorFlor(j2.getMano());
        return (v1 >= v2) ? j1 : j2; // En empate gana el mano (j1)
    }
}