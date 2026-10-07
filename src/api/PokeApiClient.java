package api;

import model.Pokemon;
import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

public class PokeApiClient {

    private static final String URL_BASE = "https://pokeapi.co/api/v2/pokemon/";

    public Pokemon obtenerPokemon(String nombre) throws Exception {
        // 1. Hacer la petición
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(URL_BASE + nombre.trim().toLowerCase()))
                .build();
        HttpResponse<String> response =
                client.send(request, HttpResponse.BodyHandlers.ofString());

        // 2. Revisar el código de respuesta
        if (response.statusCode() != 200) {
            throw new Exception("Pokémon no encontrado");
        }

        // 3. Leer el JSON y armar el Pokemon
        JSONObject json = new JSONObject(response.body());
        String nombrePokemon = json.getString("name");

        // Sprite (puede no existir, por eso optString)
        String sprite = json.getJSONObject("sprites").optString("front_default", "");

        // Tipos
        List<String> tipos = new ArrayList<>();
        JSONArray arrayTipos = json.getJSONArray("types");
        for (int i = 0; i < arrayTipos.length(); i++) {
            JSONObject tipo = arrayTipos.getJSONObject(i).getJSONObject("type");
            tipos.add(tipo.getString("name"));
        }

        // Stats
        int hp = 0, attack = 0, defense = 0, speed = 0;
        JSONArray arrayStats = json.getJSONArray("stats");
        for (int i = 0; i < arrayStats.length(); i++) {
            JSONObject s = arrayStats.getJSONObject(i);
            String nombreStat = s.getJSONObject("stat").getString("name");
            int valor = s.getInt("base_stat");

            if (nombreStat.equals("hp")) {
                hp = valor;
            } else if (nombreStat.equals("attack")) {
                attack = valor;
            } else if (nombreStat.equals("defense")) {
                defense = valor;
            } else if (nombreStat.equals("speed")) {
                speed = valor;
            }
        }

        return new Pokemon(nombrePokemon, tipos, sprite, hp, attack, defense, speed);
    }
}