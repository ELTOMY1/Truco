package vista;

import modelo.*;
import controlador.ControladorGrafico;
import javax.swing.*;
import java.awt.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class VentanaJugador extends JFrame implements Observador {
    private JuegoModelo modelo;
    private Jugador miJugador;
    private ControladorGrafico controlador;

    private JPanel panelCartas;
    private JPanel panelAcciones;
    private JPanel panelMesa;
    private AnotadorPuntos anotadorNosotros;
    private AnotadorPuntos anotadorEllos;
    private Image imagenFondo;

    public VentanaJugador(JuegoModelo modelo, Jugador miJugador, ControladorGrafico controlador) {
        this.modelo = modelo;
        this.miJugador = miJugador;
        this.controlador = controlador;
        this.modelo.agregarObservador(this);

        cargarRecursos();
        configurarVentana();
        actualizar();
    }

    private void cargarRecursos() {
        // Importante: Si 'resources' es Source Root, la ruta debe ser sin la barra inicial
        imagenFondo = cargarImagen("mesa.jpg");
    }

    private Image cargarImagen(String nombre) {
        URL url = getClass().getClassLoader().getResource(nombre);
        return (url != null) ? new ImageIcon(url).getImage() : null;
    }

    private void configurarVentana() {
        setTitle("Truco Argentino - " + miJugador.getNombre());
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panelFondo = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (imagenFondo != null) {
                    g.drawImage(imagenFondo, 0, 0, getWidth(), getHeight(), this);
                } else {
                    g.setColor(new Color(0, 100, 30));
                    g.fillRect(0, 0, getWidth(), getHeight());
                }
            }
        };

        panelCartas = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        panelCartas.setOpaque(false);

        panelAcciones = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        panelAcciones.setOpaque(false);

        JPanel panelInferior = new JPanel();
        panelInferior.setLayout(new BoxLayout(panelInferior, BoxLayout.Y_AXIS));
        panelInferior.setOpaque(false);
        panelInferior.add(panelCartas);
        panelInferior.add(panelAcciones);

        panelMesa = new JPanel(new GridBagLayout());
        panelMesa.setOpaque(false);

        anotadorNosotros = new AnotadorPuntos("Nosotros");
        anotadorEllos = new AnotadorPuntos("Ellos");

        panelFondo.add(panelMesa, BorderLayout.CENTER);
        panelFondo.add(panelInferior, BorderLayout.SOUTH);
        panelFondo.add(anotadorNosotros, BorderLayout.WEST);
        panelFondo.add(anotadorEllos, BorderLayout.EAST);

        setContentPane(panelFondo);
    }

    @Override
    public void actualizar() {
        panelCartas.removeAll();
        panelAcciones.removeAll();
        panelMesa.removeAll();

        // 1. Dibujar Mesa
        List<Carta> cartasEnMesa = modelo.getRondaActual().getMesa().getCartasEnMesa();
        List<Jugador> jugadoresEnMesa = modelo.getRondaActual().getMesa().getJugadores();

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        for (int i = 0; i < cartasEnMesa.size(); i++) {
            gbc.gridy = (modelo.getEquipoDeJugador(jugadoresEnMesa.get(i)) == modelo.getEquipoDeJugador(miJugador)) ? 1 : 0;
            gbc.gridx = i;
            panelMesa.add(crearLabelCarta(cartasEnMesa.get(i)), gbc);
        }

        // 2. Dibujar mi mano
        boolean esMiTurno = modelo.getJugadorActual().equals(miJugador) && !modelo.getRondaActual().seTerminoLaMano();
        for (Carta c : miJugador.getManoCartas()) {
            panelCartas.add(crearBotonCarta(c, esMiTurno));
        }

        // 3. Puntos y botones
        anotadorNosotros.setPuntos(modelo.getEquipo1().getPuntos());
        anotadorEllos.setPuntos(modelo.getEquipo2().getPuntos());
        if (esMiTurno) configurarBotonesAccion();

        revalidate();
        repaint();
    }

    private JLabel crearLabelCarta(Carta c) {
        String nombre = c.getNumero() + "_" + c.getPalo() + ".png";
        URL url = getClass().getClassLoader().getResource(nombre);
        if (url != null) {
            return new JLabel(new ImageIcon(new ImageIcon(url).getImage().getScaledInstance(85, 125, Image.SCALE_SMOOTH)));
        }
        return new JLabel("[" + c.getNumero() + " " + c.getPalo() + "]");
    }

    private JButton crearBotonCarta(Carta c, boolean habilitado) {
        String nombre = c.getNumero() + "_" + c.getPalo() + ".png";
        URL url = getClass().getClassLoader().getResource(nombre);

        JButton btn;
        if (url != null) {
            btn = new JButton(new ImageIcon(new ImageIcon(url).getImage().getScaledInstance(90, 135, Image.SCALE_SMOOTH)));
            btn.setContentAreaFilled(false);
            btn.setBorderPainted(false);
        } else {
            btn = new JButton(c.getNumero() + " de " + c.getPalo());
        }
        btn.setEnabled(habilitado);
        btn.addActionListener(e -> controlador.jugarCarta(miJugador, c));
        return btn;
    }

    private void configurarBotonesAccion() {
        if (modelo.puedeCantarTruco(miJugador)) {
            JButton btn = new JButton("TRUCO");
            btn.addActionListener(e -> controlador.cantarTruco(miJugador));
            panelAcciones.add(btn);
        }
        if (modelo.getRondaActual().sePuedeCantarEnvido(miJugador)) {
            JButton btn = new JButton("ENVIDO");
            btn.addActionListener(e -> controlador.cantarEnvido(miJugador, 2));
            panelAcciones.add(btn);
        }
    }
}