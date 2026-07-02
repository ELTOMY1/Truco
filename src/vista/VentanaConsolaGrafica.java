package vista;

import javax.swing.*;
import java.awt.*;
import java.util.concurrent.LinkedBlockingQueue;

public class VentanaConsolaGrafica extends JFrame {
    private JTextArea areaTexto;
    private JTextField campoComando;
    private LinkedBlockingQueue<String> colaEntrada = new LinkedBlockingQueue<>();

    public VentanaConsolaGrafica(String nombreJugador, int x, int y) {
        setTitle("TRUCO - Jugador: " + nombreJugador);
        setSize(600, 700);
        setLocation(x, y);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        areaTexto = new JTextArea();
        areaTexto.setEditable(false);
        areaTexto.setBackground(Color.BLACK);
        areaTexto.setForeground(Color.WHITE);
        areaTexto.setFont(new Font("Consolas", Font.BOLD, 16));
        areaTexto.setMargin(new Insets(15, 15, 15, 15));

        JScrollPane scroll = new JScrollPane(areaTexto);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(50, 50, 50), 2));
        add(scroll, BorderLayout.CENTER);

        JPanel panelInput = new JPanel(new BorderLayout());
        panelInput.setBackground(new Color(30, 30, 30));
        panelInput.setBorder(BorderFactory.createTitledBorder(null, " COMANDOS ", 0, 0, null, Color.GREEN));

        campoComando = new JTextField();
        campoComando.setBackground(Color.BLACK);
        campoComando.setForeground(Color.GREEN);
        campoComando.setFont(new Font("Consolas", Font.BOLD, 18));
        campoComando.setCaretColor(Color.WHITE);
        campoComando.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        campoComando.addActionListener(e -> {
            String txt = campoComando.getText().trim();
            if (!txt.isEmpty()) {
                colaEntrada.offer(txt);
                campoComando.setText("");
            }
        });

        panelInput.add(campoComando, BorderLayout.CENTER);
        add(panelInput, BorderLayout.SOUTH);
    }

    public void escribir(String msg) {
        areaTexto.append(msg + "\n");
        areaTexto.setCaretPosition(areaTexto.getDocument().getLength());
    }

    public String leerComando() {
        try {
            // Vaciamos la cola antes de pedir un nuevo comando
            // Esto evita que los "ENTER" acumulados respondan automáticamente
            colaEntrada.clear();
            return colaEntrada.take();
        } catch (InterruptedException e) {
            return "";
        }
    }
}