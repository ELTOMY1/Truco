package modelo;

import java.util.List;

public interface IJugador {
    String getNombre();
    int getPuntos();
    int getEquipo();
    List<ICarta> getManoCartas(); // Retorna lista de interfaces
}