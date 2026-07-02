package modelo;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Mazo implements Serializable {
    private List<Carta> cartas;

    public Mazo() {
        cartas = new ArrayList<>();
        String[] palos = {"Espada", "Basto", "Oro", "Copa"};
        for (String palo : palos) {
            for (int i = 1; i <= 12; i++) {
                if (i != 8 && i != 9) cartas.add(new Carta(i, palo));
            }
        }
    }

    public void barajar() { Collections.shuffle(cartas); }
    public Carta darCarta() {
        if (cartas.isEmpty()) return null;
        return cartas.remove(0);
    }
}