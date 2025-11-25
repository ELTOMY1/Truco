package modelo;

import java.util.List;

public class ResolutorRondas {

    public int contarRondasGanadas(List<Integer> ganadoresPorRonda, int equipo) {
        int t = 0;
        for (int g : ganadoresPorRonda) if (g == equipo) t++;
        return t;
    }

    public int ganadorDeMano(List<Integer> ganadoresPorRonda) {
        int c1 = contarRondasGanadas(ganadoresPorRonda, 1);
        int c2 = contarRondasGanadas(ganadoresPorRonda, 2);
        if (c1 >= 2) return 1;
        if (c2 >= 2) return 2;
        return -1;
    }
}
