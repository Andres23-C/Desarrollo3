## PokeApi

Aplicación de escritorio en Java Swing que consulta la PokeAPI y simula un combate por turnos entre dos Pokémon.

Autores: Marlon Andres Cuellar y Nicol Vanessa Peña Jimenez

## Características

- Carga de Pokémon por nombre o ID, o de forma aleatoria, desde la PokeAPI (sprite, tipos y stats).
- Combate por turnos: inicia el Pokémon con mayor Speed (en empate, al azar).
- Daño con fórmula simple, golpe crítico (10 % de probabilidad, x1.5) y efectividad por el primer tipo.
- Barras de HP y registro del combate que se actualizan en vivo, sin congelar la ventana.

## Requisitos

- JDK 26
- IntelliJ IDEA (o cualquier IDE que abra proyectos IntelliJ)
- Conexión a internet (para consultar la PokeAPI y descargar los sprites)
- Librería `lib/json-20230227.jar` (ya incluida en el repositorio)

## Instrucciones de ejecución

1. Clonar el repositorio:
   ```
   git clone https://github.com/Andres23-C/Desarrollo3.git
   ```
2. Abrir la carpeta del proyecto en IntelliJ con el JDK 26.
3. Agregar la librería JSON: **File → Project Structure → Libraries → + → Java** y seleccionar `lib/json-20230227.jar`.
4. Ejecutar la clase `view.MainWindow`.
5. En cada panel de jugador, escribir el nombre de un Pokémon (por ejemplo `pikachu`) y pulsar **Load**, o pulsar **Random**.
6. Cuando los dos Pokémon estén cargados, pulsar **Fight!**.

## Estructura del proyecto

```
src/
├── api/      PokeApiClient: consulta la PokeAPI y construye el Pokemon
├── model/    Pokemon: datos y estado (HP actual) de un Pokémon
├── battle/   BattleListener y Battle: lógica del combate
└── view/     MainWindow, JugadorPanel, PokemonPanel: interfaz Swing
lib/          json-20230227.jar
```

## Reglas del combate

| Regla | Detalle |
|---|---|
| Quién inicia | El de mayor `Speed`; si empatan, se decide al azar |
| Daño base | `ATK del atacante * 0.5 - DEF del defensor * 0.25` |
| Daño final | `base * efectividad * (1.5 si es crítico)`, redondeado y con mínimo 1 |
| Crítico | 10 % de probabilidad, multiplica el daño x1.5 |
| Efectividad | Se calcula con el **primer tipo** de cada Pokémon |
| Agua > Fuego > Planta > Agua | x1.3 a favor, x0.7 en contra |
| Cualquier otra combinación | x1.0 |
| HP | Nunca baja de 0 (lo garantiza `Pokemon.recibirDanio`) |
| Fin del combate | Cuando un Pokémon queda derrotado, gana el otro |

## Diseño

La lógica del combate está en el paquete `battle`, separada por completo de la interfaz. La clase `Battle` calcula cada turno (quién inicia según Speed, daño, crítico y efectividad) y notifica lo ocurrido a través de la interfaz `BattleListener`, siguiendo el patrón Observer. Así la lógica se puede probar sin ventana, y la interfaz solo se actualiza reaccionando a los eventos `onTurn`, `onHpChanged` y `onBattleEnded`.

Para no bloquear la ventana, las peticiones a la PokeAPI y la descarga de sprites se hacen con `SwingWorker`, y `Battle` ejecuta el bucle del combate en un hilo aparte con una pausa de 800 ms entre turnos. La interfaz traslada cada evento del combate al hilo de Swing con `SwingUtilities.invokeLater`. El modelo `Pokemon` garantiza que el HP nunca sea negativo y la fórmula de daño garantiza un mínimo de 1 por ataque, de modo que todo combate termina.

## Tecnologías

- Java 26 y Swing
- `java.net.http.HttpClient` para las peticiones HTTP
- `org.json` para leer las respuestas JSON
- PokeAPI: https://pokeapi.co/
