package modelo;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Mesa implements Serializable {
    private List<Carta> cartasEnMesa;
    private List<Jugador> jugadoresQueTiraron;

    public Mesa() {
        this.cartasEnMesa = new ArrayList<>();
        this.jugadoresQueTiraron = new ArrayList<>();
    }

    public void recibirCarta(Carta carta, Jugador jugador) {
        cartasEnMesa.add(carta);
        jugadoresQueTiraron.add(jugador);
    }

    public List<Carta> getCartasEnMesa() { return cartasEnMesa; }

    public List<Jugador> getJugadores() { return jugadoresQueTiraron; }

    public boolean estaCompleta(int cantidadJugadoresTotal) {
        return cartasEnMesa.size() == cantidadJugadoresTotal;
    }

    public void limpiarMesa() {
        cartasEnMesa.clear();
        jugadoresQueTiraron.clear();
    }

    public Jugador determinarJugadorGanador(Equipo e1, Equipo e2) {
        if (cartasEnMesa.isEmpty()) return null;
        int maxValor = -1;
        for (Carta c : cartasEnMesa) if (c.getValorTruco() > maxValor) maxValor = c.getValorTruco();

        List<Jugador> posiblesGanadores = new ArrayList<>();
        for (int i = 0; i < cartasEnMesa.size(); i++) {
            if (cartasEnMesa.get(i).getValorTruco() == maxValor) {
                posiblesGanadores.add(jugadoresQueTiraron.get(i));
            }
        }

        boolean e1Gana = false;
        boolean e2Gana = false;
        for (Jugador j : posiblesGanadores) {
            if (e1.getIntegrantes().contains(j)) e1Gana = true;
            else e2Gana = true;
        }

        if (e1Gana && e2Gana) return null;
        return posiblesGanadores.get(0);
    }
}