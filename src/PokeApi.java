import org.json.JSONArray;
import org.json.JSONObject;

import javax.swing.*;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class PokeApi
{
    public void consultarPokemon()
    {
        try
        {
            //Solicita el nombre del pokemon
            String nombrePokemon = JOptionPane.showInputDialog("Ingrese el nombre del pokemon");

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

                System.out.println("ID Pokemon: "+json.getInt("id"));
                System.out.println("nombre: "+json.getString("name"));
                System.out.println("peso: "+json.getInt("weight"));
                System.out.println("altura: "+json.getInt("height"));

                //Vamos a obtener las habilidades pero estas estan contenidas en un Array
                System.out.println("\nHabilidades:");

                json.getJSONArray("abilities").forEach(habilidad ->
                {
                    //Accedemos al objeto
                    JSONObject abilityJson = (JSONObject) habilidad;

                    JSONObject abilityName = abilityJson.getJSONObject("ability");

                    System.out.println(abilityName.getString("name"));

                });

                System.out.println("\nEstadisticas:");

                json.getJSONArray("stats").forEach(estadistica ->
                {
                    //Accedemos al objeto
                    JSONObject estadisticaJson = (JSONObject) estadistica;

                    JSONObject estadisticaName = estadisticaJson.getJSONObject("stat");

                    System.out.println(estadisticaName.getString("name")+": "+estadisticaJson.getInt("base_stat"));

                });

                System.out.println("\nImagen:");
                JSONObject foto = json.getJSONObject("sprites");
                System.out.println(foto.getString("front_default"));

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

    //psvm
    static void main() {
        PokeApi pokeApi = new PokeApi();
        pokeApi.consultarPokemon();
    }
}
