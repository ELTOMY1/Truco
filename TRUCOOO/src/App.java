import controlador.ControladorJuego;
import modelo.JuegoModelo;
import vista.VistaConsola;

public class App {
    public static void main(String[] args) {
        JuegoModelo modelo = new JuegoModelo();
        VistaConsola vista = new VistaConsola();
        ControladorJuego controlador = new ControladorJuego(modelo, vista);
        controlador.iniciarJuego();
    }
}