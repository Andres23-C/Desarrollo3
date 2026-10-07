package battle;

import model.Pokemon;

import java.util.List;
import java.util.Locale;
import java.util.Random;

// Lógica del combate por turnos. No conoce Swing: solo calcula y avisa por BattleListener.

public class Battle {

    private static final double PROB_CRITICO = 0.10;   // 10% de probabilidad
    private static final double MULT_CRITICO = 1.5;
    private static final double MULT_VENTAJA = 1.3;
    private static final double MULT_DESVENTAJA = 0.7;
    private static final double MULT_NEUTRO = 1.0;
    private static final long PAUSA_MS = 800;          // pausa entre turnos

    private final Pokemon p1;
    private final Pokemon p2;
    private final BattleListener listener;
    private final Random random = new Random();
    private boolean iniciada = false;

    public Battle(Pokemon p1, Pokemon p2, BattleListener listener) {
        if (p1 == null || p2 == null || listener == null) {
            throw new IllegalArgumentException("Battle necesita dos Pokémon y un listener");
        }
        this.p1 = p1;
        this.p2 = p2;
        this.listener = listener;
    }

    // Arranca el combate en un hilo aparte para no congelar la ventana. */
    public synchronized void iniciar() {
        if (iniciada) {
            throw new IllegalStateException("El combate ya fue iniciado");
        }
        iniciada = true;
        Thread hilo = new Thread(this::ejecutar, "battle-thread");
        hilo.setDaemon(true); // si se cierra la ventana, el hilo no mantiene vivo el programa
        hilo.start();
    }

    //Bucle principal (corre en el hilo de fondo)

    private void ejecutar() {
        try {
            // Caso borde: alguno ya llegó derrotado
            if (p1.estaDerrotado() || p2.estaDerrotado()) {
                listener.onBattleEnded(p1.estaDerrotado() ? p2.getNombre() : p1.getNombre());
                return;
            }

            Pokemon atacante = decideFirst();
            Pokemon defensor = (atacante == p1) ? p2 : p1;

            while (true) {
                ejecutarTurno(atacante, defensor);

                if (defensor.estaDerrotado()) {
                    listener.onBattleEnded(atacante.getNombre());
                    return;
                }

                Thread.sleep(PAUSA_MS);

                // Se intercambian los roles para el siguiente turno
                Pokemon temp = atacante;
                atacante = defensor;
                defensor = temp;
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // se respeta la interrupción y se termina
        }
    }

    private void ejecutarTurno(Pokemon atacante, Pokemon defensor) {
        double modificador = calcularEfectividad(atacante, defensor);
        boolean critico = random.nextDouble() < PROB_CRITICO;
        int danio = calcularDanio(atacante, defensor, modificador, critico);

        defensor.recibirDanio(danio); // el modelo impide que el HP baje de 0

        listener.onTurn(atacante.getNombre(), defensor.getNombre(), danio, critico, modificador);
        listener.onHpChanged(defensor.getNombre(), defensor.getHpActual());
    }

    //Reglas

    /** Mayor Speed inicia; si empatan, decide el azar. */
    private Pokemon decideFirst() {
        if (p1.getSpeed() > p2.getSpeed()) {
            return p1;
        }
        if (p2.getSpeed() > p1.getSpeed()) {
            return p2;
        }
        return random.nextBoolean() ? p1 : p2;
    }


     // Fórmula de daño:
     // base   = ATK_atacante * 0.5 - DEF_defensor * 0.25
     // daño   = base * efectividad * (1.5 si es crítico, 1.0 si no)
     // El resultado se redondea y nunca baja de 1, así todo ataque hace al menos 1 de daño.

    private int calcularDanio(Pokemon atacante, Pokemon defensor, double modificador, boolean critico) {
        double base = atacante.getAttack() * 0.5 - defensor.getDefense() * 0.25;
        double multiplicador = modificador * (critico ? MULT_CRITICO : 1.0);
        int danio = (int) Math.round(base * multiplicador);
        return Math.max(1, danio);
    }

    // Efectividad según el primer tipo: Agua > Fuego > Planta > Agua.
    private double calcularEfectividad(Pokemon atacante, Pokemon defensor) {
        String tipoAtacante = primerTipo(atacante);
        String tipoDefensor = primerTipo(defensor);

        if (ganaContra(tipoAtacante, tipoDefensor)) {
            return MULT_VENTAJA;
        }
        if (ganaContra(tipoDefensor, tipoAtacante)) {
            return MULT_DESVENTAJA;
        }
        return MULT_NEUTRO;
    }

    private boolean ganaContra(String a, String b) {
        return (a.equals("water") && b.equals("fire"))
                || (a.equals("fire") && b.equals("grass"))
                || (a.equals("grass") && b.equals("water"));
    }

    private String primerTipo(Pokemon p) {
        List<String> tipos = p.getTipos();
        if (tipos == null || tipos.isEmpty() || tipos.get(0) == null) {
            return "";
        }
        return tipos.get(0).toLowerCase(Locale.ROOT);
    }
}