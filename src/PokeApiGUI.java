import org.json.JSONObject;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class PokeApiGUI {
    private JPanel mainPanel;
    private JTextField campoId;
    private JTextField campoNombre;
    private JTextField campoPeso;
    private JTextField campoAltura;
    private JTextField campoHp;
    private JTextField campoAtk;
    private JTextField campoDef;
    private JTextField campoAtkEsp;
    private JTextField campoDefEsp;
    private JTextArea areaHabilidad;
    private JTextField campoVelocidad;
    private JLabel textoImagen;

    public PokeApiGUI()
    {
        campoNombre.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                consultarPokemon();
            }
        });
    }

    public void consultarPokemon()
    {
        try
        {
            //Solicita el nombre del pokemon
            String nombrePokemon = campoNombre.getText();

            //Crea un cliente http el cual se encarga de hacer las peticiones
            HttpClient client = HttpClient.newHttpClient();
            //Crea una peticion al servidor de la PokeApi
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://pokeapi.co/api/v2/pokemon/"+nombrePokemon))
                    .build();
            //Ejecutamos la solicitud
            HttpResponse<String> response = client.send(request,HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200)
            {
                JSONObject json = new JSONObject(response.body());

                campoId.setText(String.valueOf(json.getInt("id")));
                campoPeso.setText(String.valueOf(json.getInt("weight")));
                campoAltura.setText(String.valueOf(json.getInt("height")));

                //Vamos a obtener las habilidades pero estas estan contenidas en un Array
                json.getJSONArray("abilities").forEach(habilidad ->
                {
                    //Accedemos al objeto
                    JSONObject abilityJson = (JSONObject) habilidad;
                    JSONObject abilityName = abilityJson.getJSONObject("ability");

                    areaHabilidad.append(abilityName.getString("name")+"\n");

                });

                System.out.println("\nEstadisticas:");

                json.getJSONArray("stats").forEach(estadistica ->
                {
                    //Accedemos al objeto
                    JSONObject estadisticaJson = (JSONObject) estadistica;

                    JSONObject estadisticaName = estadisticaJson.getJSONObject("stat");

                    String nombre = estadisticaName.getString("name");
                    int valor = estadisticaJson.getInt("base_stat");

                    if (nombre.equals("hp"))
                        campoHp.setText(String.valueOf(valor));
                    else if (nombre.equals("attack"))
                        campoAtk.setText(String.valueOf(valor));
                    else if (nombre.equals("defense"))
                        campoDef.setText(String.valueOf(valor));
                    else if (nombre.equals("special-attack"))
                        campoAtkEsp.setText(String.valueOf(valor));
                    else if (nombre.equals("special-defense"))
                        campoDefEsp.setText(String.valueOf(valor));
                    else if (nombre.equals("speed"))
                        campoVelocidad.setText(String.valueOf(valor));

                });

                JSONObject foto = json.getJSONObject("sprites");
                try
                {
                    java.net.URL urlImagen = new java.net.URL(foto.getString("front_default"));
                    ImageIcon icono = new ImageIcon(urlImagen);
                    Image image = icono.getImage().getScaledInstance(100, 100, Image.SCALE_DEFAULT);
                    textoImagen.setText("");
                    textoImagen.setIcon(new ImageIcon(image));
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                    textoImagen.setText("No se pudo cargar la imagen");
                }

                System.out.println("\nSonido:");
                System.out.println(json.getJSONObject("cries").getString("latest"));

            }
            else
            {
                JOptionPane.showMessageDialog(null,"El pokemon no existe");
            }
        }
        catch (IOException | InterruptedException e)
        {
            e.printStackTrace();
        }
    }

    static void main() {
        JFrame frame = new JFrame("PokeApi");
        frame.setContentPane(new PokeApiGUI().mainPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
        frame.setLocationRelativeTo(null);
        frame.setResizable(true);

    }
}
