package modelo;

public class GestorTruco {
    private Mesa mesa;
    private int puntosTruco;
    private boolean trucoActivo;
    private Jugador ultimoQueSubio;

    public GestorTruco(Mesa mesa) { this.mesa = mesa; reset(); }

    public void reset() { puntosTruco = 1; trucoActivo = false; ultimoQueSubio = null; }
    public int getPuntosTruco() { return puntosTruco; }
    public boolean isTrucoActivo() { return trucoActivo; }
    public boolean puedeSubir(Jugador j) { return ultimoQueSubio != null && ultimoQueSubio == j; }

    public void cantarTruco(Jugador j) { puntosTruco = 2; ultimoQueSubio = j; }
    public void cantarRetruco(Jugador j) { puntosTruco = 3; ultimoQueSubio = j; }
    public void cantarValeCuatro(Jugador j) { puntosTruco = 4; ultimoQueSubio = j; }
    public void cantarQuiero(Jugador j) { trucoActivo = true; ultimoQueSubio = j; }

    public int puntosSiNoQuiero() {
        if (puntosTruco == 2) return 1; // Truco rechazado
        if (puntosTruco == 3) return 2; // Retruco rechazado
        if (puntosTruco == 4) return 3; // Vale 4 rechazado
        return 1;
    }

    public void irseAlMazo(Jugador j) {
        int eq = (j.getEquipo()==1 ? 2 : 1);
        sumarPuntosEq(eq, puntosTruco);
    }

    private void sumarPuntosEq(int eq, int pts){
        for(Jugador j:mesa.getJugadores()) if(j.getEquipo()==eq) j.sumarPuntos(pts);
    }
}