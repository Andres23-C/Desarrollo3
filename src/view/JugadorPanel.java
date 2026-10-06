package view;

import api.PokeApiClient;
import model.Pokemon;

import javax.swing.*;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.Random;
import java.util.concurrent.ExecutionException;

public class JugadorPanel extends JPanel {

    private final PokeApiClient cliente = new PokeApiClient();
    private final JTextField campoNombre = new JTextField(10);
    private final JButton btnLoad = new JButton("Load");
    private final JButton btnRandom = new JButton("Random");
    private final JLabel lblError = new JLabel(" ");
    private final PokemonPanel panelPokemon;

    private Pokemon pokemon;      // el Pokémon cargado (null si no hay)
    private Runnable alCargar;    // aviso a la ventana cuando se carga uno

    public JugadorPanel(String titulo) {
        panelPokemon = new PokemonPanel(titulo);
        setLayout(new BorderLayout());

        JPanel controles = new JPanel(new FlowLayout());
        controles.add(campoNombre);
        controles.add(btnLoad);
        controles.add(btnRandom);

        add(controles, BorderLayout.NORTH);
        add(panelPokemon, BorderLayout.CENTER);
        add(lblError, BorderLayout.SOUTH);

        // ActionListener de cada botón
        btnLoad.addActionListener(e -> cargar(campoNombre.getText()));
        btnRandom.addActionListener(e ->
                cargar(String.valueOf(1 + new Random().nextInt(1025))));
    }

    private void cargar(String nombreOId) {
        btnLoad.setEnabled(false);
        btnRandom.setEnabled(false);
        lblError.setText("Cargando...");

        new SwingWorker<Pokemon, Void>() {
            @Override
            protected Pokemon doInBackground() throws Exception {
                return cliente.obtenerPokemon(nombreOId); // hilo de fondo
            }

            @Override
            protected void done() {
                try {
                    pokemon = get();
                    panelPokemon.mostrarPokemon(pokemon);
                    lblError.setText(" ");
                    if (alCargar != null) alCargar.run();
                } catch (ExecutionException e) {
                    // aquí llega la excepción de PokeApiClient
                    lblError.setText("Error: " + e.getCause().getMessage());
                } catch (InterruptedException e) {
                    lblError.setText("Carga interrumpida");
                }
                btnLoad.setEnabled(true);
                btnRandom.setEnabled(true);
            }
        }.execute();
    }

    public Pokemon getPokemon() { return pokemon; }
    public PokemonPanel getPanelPokemon() { return panelPokemon; }
    public void setAlCargar(Runnable alCargar) { this.alCargar = alCargar; }
}
