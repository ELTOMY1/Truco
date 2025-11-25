package modelo;

public class Carta implements ICarta {
    private String palo;
    private int valor;

    public Carta(int valor, String palo) {
        this.valor = valor;
        this.palo = palo;
    }

    @Override
    public int getValor() { return valor; }

    @Override
    public String getPalo() { return palo; }

    @Override
    public String toString() { return valor + " de " + palo; }

    @Override
    public int valorEnvido() {
        if(valor >= 10) return 0;
        return valor;
    }
}