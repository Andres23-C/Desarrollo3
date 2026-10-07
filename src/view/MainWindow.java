package view;

import battle.Battle;
import battle.BattleListener;

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

        btnFight.addActionListener(e -> iniciarCombate());

        pack();
        setLocationRelativeTo(null);
    }

    // crea el combate y lo conecta con la pantalla mediante el listener
    private void iniciarCombate() {
        btnFight.setEnabled(false); // evita lanzar dos combates a la vez
        log.setText("");

        BattleListener listener = new BattleListener() {
            @Override
            public void onTurn(String attacker, String defender, int damage, boolean critical, double modifier) {
                // Llega desde el hilo del combate: Swing solo se toca con invokeLater
                SwingUtilities.invokeLater(() -> {
                    String texto = attacker + " ataca a " + defender + ": " + damage + " de daño";
                    if (critical) texto += " ¡CRÍTICO!";
                    if (modifier > 1.0) texto += " (súper efectivo)";
                    else if (modifier < 1.0) texto += " (poco efectivo)";
                    log.append(texto + "\n");
                });
            }

            @Override
            public void onHpChanged(String pokemon, int hpActual) {
                SwingUtilities.invokeLater(() -> {
                    if (pokemon.equals(jugador1.getPokemon().getNombre())) {
                        jugador1.getPanelPokemon().actualizarHp(hpActual);
                    } else if (pokemon.equals(jugador2.getPokemon().getNombre())) {
                        jugador2.getPanelPokemon().actualizarHp(hpActual);
                    }
                });
            }

            @Override
            public void onBattleEnded(String winner) {
                SwingUtilities.invokeLater(() -> log.append("¡Ganó " + winner + "!\n"));
            }
        };

        new Battle(jugador1.getPokemon(), jugador2.getPokemon(), listener).iniciar();
    }

    public static void main(String[] args) {
        // La interfaz siempre se crea en el hilo de Swing
        SwingUtilities.invokeLater(() -> new MainWindow().setVisible(true));
    }
}