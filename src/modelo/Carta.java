package modelo;
import java.io.Serializable;

public class Carta implements ICarta, Serializable {
    private int numero;
    private String palo;

    public Carta(int numero, String palo) {
        this.numero = numero;
        this.palo = palo;
    }

    @Override public int getNumero() { return numero; }
    @Override public String getPalo() { return palo; }

    @Override
    public int getValorTruco() {
        if (numero == 1 && palo.equals("Espada")) return 14;
        if (numero == 1 && palo.equals("Basto")) return 13;
        if (numero == 7 && palo.equals("Espada")) return 12;
        if (numero == 7 && palo.equals("Oro")) return 11;
        if (numero == 3) return 10;
        if (numero == 2) return 9;
        if (numero == 1) return 8;
        if (numero == 12) return 7;
        if (numero == 11) return 6;
        if (numero == 10) return 5;
        if (numero == 7) return 4;
        if (numero == 6) return 3;
        if (numero == 5) return 2;
        if (numero == 4) return 1;
        return 0;
    }

    @Override
    public int getValorEnvido() {
        return (numero >= 10) ? 0 : numero;
    }

    public int getValor() { return getValorTruco(); }
    public int valorEnvido() { return getValorEnvido(); }

    @Override
    public String toString() { return numero + " de " + palo; }
}