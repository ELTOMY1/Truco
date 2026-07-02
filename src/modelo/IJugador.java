package modelo;
import java.util.List;
public interface IJugador {
    void recibirCarta(Carta c);
    List<Carta> getManoCartas();
    String getNombre();
    void limpiarMano();
}