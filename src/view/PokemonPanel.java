package view;

import model.Pokemon;

import javax.swing.*;
import java.awt.Component;
import java.awt.Image;
import java.net.URL;

public class PokemonPanel extends JPanel {

    private JLabel lblSprite = new JLabel("", SwingConstants.CENTER);
    private JLabel lblNombre = new JLabel("-");
    private JLabel lblTipos = new JLabel("Tipos: -");
    private JLabel lblStats = new JLabel("ATK - | DEF - | SPD -");
    private JProgressBar barraHp = new JProgressBar();

    public PokemonPanel(String titulo) {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createTitledBorder(titulo));

        barraHp.setStringPainted(true); // muestra el texto sobre la barra
        barraHp.setString("- / -");

        // En BoxLayout, cada componente se centra con esta línea
        lblSprite.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblNombre.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblTipos.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblStats.setAlignmentX(Component.CENTER_ALIGNMENT);
        barraHp.setAlignmentX(Component.CENTER_ALIGNMENT);

        add(lblSprite);
        add(lblNombre);
        add(lblTipos);
        add(lblStats);
        add(barraHp);
    }

    // Muestra los datos de un Pokémon en el panel
    public void mostrarPokemon(Pokemon p) {
        lblNombre.setText(p.getNombre());
        lblTipos.setText("Tipos: " + String.join(", ", p.getTipos()));
        lblStats.setText("ATK " + p.getAttack()
                + " | DEF " + p.getDefense()
                + " | SPD " + p.getSpeed());

        barraHp.setMinimum(0);
        barraHp.setMaximum(p.getHp());
        actualizarHp(p.getHpActual());

        cargarSprite(p.getSpriteUrl());
    }

    // Lo llamará la ventana cuando el BattleListener avise un cambio de vida
    public void actualizarHp(int hpActual) {
        barraHp.setValue(hpActual);
        barraHp.setString(hpActual + " / " + barraHp.getMaximum());
    }

    // Descarga la imagen en un hilo de fondo para no congelar la ventana
    private void cargarSprite(String url) {
        if (url == null || url.isEmpty()) {
            lblSprite.setIcon(null);
            lblSprite.setText("Sin imagen");
            return;
        }

        lblSprite.setIcon(null);
        lblSprite.setText("Cargando...");

        new SwingWorker<ImageIcon, Void>() {
            @Override
            protected ImageIcon doInBackground() throws Exception {
                // Hilo de fondo: la descarga puede tardar
                Image img = new ImageIcon(new URL(url)).getImage();
                return new ImageIcon(img.getScaledInstance(120, 120, Image.SCALE_SMOOTH));
            }

            @Override
            protected void done() {
                // De vuelta en el hilo de la interfaz: aquí sí se puede tocar la pantalla
                try {
                    lblSprite.setText("");
                    lblSprite.setIcon(get());
                } catch (Exception e) {
                    lblSprite.setIcon(null);
                    lblSprite.setText("Sin imagen");
                }
            }
        }.execute();
    }
}