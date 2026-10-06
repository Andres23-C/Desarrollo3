package view;

import javax.swing.*;
import java.awt.BorderLayout;
import java.awt.GridLayout;

public class MainWindow extends JFrame {

    private final JugadorPanel jugador1 = new JugadorPanel("Jugador 1");
    private final JugadorPanel jugador2 = new JugadorPanel("Jugador 2");
    private final JButton btnFight = new JButton("Fight!");
    private final JTextArea log = new JTextArea(8, 40);

    public MainWindow() {
        super("Pokémon Stadium Lite");
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // Los dos jugadores lado a lado
        JPanel centro = new JPanel(new GridLayout(1, 2));
        centro.add(jugador1);
        centro.add(jugador2);

        // Fight! deshabilitado hasta que haya dos Pokémon
        btnFight.setEnabled(false);
        log.setEditable(false);

        JPanel abajo = new JPanel(new BorderLayout());
        abajo.add(btnFight, BorderLayout.NORTH);
        abajo.add(new JScrollPane(log), BorderLayout.CENTER);

        add(centro, BorderLayout.CENTER);
        add(abajo, BorderLayout.SOUTH);

        // Cada vez que se carga un Pokémon, revisamos si ya están los dos
        Runnable revisar = () -> btnFight.setEnabled(
                jugador1.getPokemon() != null && jugador2.getPokemon() != null);
        jugador1.setAlCargar(revisar);
        jugador2.setAlCargar(revisar);

        pack();
        setLocationRelativeTo(null);
    }

    public static void main(String[] args) {
        // La interfaz siempre se crea en el hilo de Swing
        SwingUtilities.invokeLater(() -> new MainWindow().setVisible(true));
    }
}