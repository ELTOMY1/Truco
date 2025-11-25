package modelo;

import java.util.*;

public class Jugador implements IJugador {
    private String nombre;
    private List<Carta> mano = new ArrayList<>();
    private List<Carta> cartasParaEnvido = new ArrayList<>(); // Nueva lista que no pierde cartas para cantar el tanto
    private int puntos;
    private int equipo;

    public Jugador(String nombre, int equipo) {
        this.nombre = nombre;
        this.equipo = equipo;
    }

    @Override
    public String getNombre(){ return nombre; }

    public List<Carta> getMano(){ return mano; }

    public List<Carta> getCartasParaEnvido() {
        return new ArrayList<>(cartasParaEnvido);
    }

    @Override
    public List<ICarta> getManoCartas() {
        return new ArrayList<>(mano);
    }

    @Override
    public int getPuntos(){ return puntos; }

    @Override
    public int getEquipo(){ return equipo; }

    public void sumarPuntos(int c){ puntos += c; }

    public void agregarCarta(Carta c){
        mano.add(c);
        cartasParaEnvido.add(c);
    }

    public Carta tirarCarta(int i){
        return mano.remove(i);
    }

    public void setEquipo(int e){ equipo = e; }

    public void limpiarMano(){
        mano.clear();
        cartasParaEnvido.clear(); // manos clear
    }

    @Override
    public String toString(){ return nombre + " (Equipo " + equipo + ", Pts:" + puntos + ")"; }
}