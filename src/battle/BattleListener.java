package battle;

// Canal por el que Battle avisa lo que ocurre en el combate.
//La interfaz gráfica implementa esta interfaz y se actualiza solo desde estos eventos.

public interface BattleListener {

    // Se llama después de cada ataque. modifier es la efectividad (1.3, 0.7 o 1.0). */
    void onTurn(String attacker, String defender, int damage, boolean critical, double modifier);

    // Se llama cuando cambia la vida de un Pokémon. hpActual nunca es negativo. */
    void onHpChanged(String pokemon, int hpActual);

    // Se llama una sola vez, cuando un Pokémon queda derrotado. */
    void onBattleEnded(String winner);
}