package modelo;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Observable implements Serializable {
    // transient evita que Java intente guardar las ventanas gráficas en el disco duro
    private transient List<Observador> observadores = new ArrayList<>();

    public void agregarObservador(Observador o) {
        if (observadores == null) observadores = new ArrayList<>();
        observadores.add(o);
    }

    public void notificarObservadores() {
        if (observadores != null) {
            for (Observador o : observadores) o.actualizar();
        }
    }
}