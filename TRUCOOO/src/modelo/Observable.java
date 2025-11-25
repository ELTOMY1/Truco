package modelo;

public interface Observable {
    void agregarObservador(Observador observador);
    void quitarObservador(Observador observador);
    void notificarObservadores();
}

