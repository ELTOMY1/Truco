package modelo;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Jugador implements IJugador, Serializable {
    private String nombre;
    private List<Carta> mano;
    private int tantosEnvido;
    private int tantosFlor;
    private boolean tieneFlor;

    public Jugador(String nombre) {
        this.nombre = nombre;
        this.mano = new ArrayList<>();
    }

    @Override public void recibirCarta(Carta c) { mano.add(c); }
    @Override public List<Carta> getManoCartas() { return mano; }
    @Override public String getNombre() { return nombre; }
    @Override public void limpiarMano() { mano.clear(); }

    public void evaluarMano() {
        this.tantosEnvido = calcularEnvido();
        this.tieneFlor = verificarFlor();
        this.tantosFlor = tieneFlor ? calcularFlor() : 0;
    }

    private boolean verificarFlor() {
        if (mano.size() < 3) return false;
        return mano.get(0).getPalo().equals(mano.get(1).getPalo()) &&
                mano.get(0).getPalo().equals(mano.get(2).getPalo());
    }

    private int calcularFlor() {
        int suma = 20;
        for (Carta c : mano) suma += c.getValorEnvido();
        return suma;
    }

    private int calcularEnvido() {
        int max = 0;
        boolean bandera = false;
        for (int i = 0; i < mano.size(); i++) {
            for (int j = i + 1; j < mano.size(); j++) {
                if ((mano.get(i).getPalo().equals(mano.get(j).getPalo())) && (mano.get(i).getValorEnvido() + mano.get(j).getValorEnvido() + 20) > max) {
                    max = mano.get(i).getValorEnvido() + mano.get(j).getValorEnvido() + 20;
                    bandera = true;
                }
            }
        }
        if(bandera) return max;
        for(Carta c : mano) if(c.getValorEnvido() > max) max = c.getValorEnvido();
        return max;
    }

    public int getTantosEnvido() { return tantosEnvido; }
    public boolean tieneFlor() { return tieneFlor; }
    public int getTantosFlor() { return tantosFlor; }
}