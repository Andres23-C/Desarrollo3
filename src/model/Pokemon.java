package model;

import java.util.List;


public class Pokemon {

    private String nombre;
    private List<String> tipos;
    private String spriteUrl;
    private int hp;
    private int attack;
    private int defense;
    private int speed;
    private int hpActual;

    public Pokemon(String nombre, List<String> tipos, String spriteUrl, int hp, int attack, int defense, int speed) {
        this.nombre = nombre;
        this.tipos = tipos;
        this.spriteUrl = spriteUrl;
        this.hp = hp;
        this.attack = attack;
        this.defense = defense;
        this.speed = speed;
        this.hpActual = hp;
    }

    public String getNombre() {
        return nombre;
    }

    public List<String> getTipos() {
        return tipos;
    }

    public String getSpriteUrl() {
        return spriteUrl;
    }

    public int getHp() {
        return hp;
    }
    public int getAttack() {
        return attack;
    }
    public int getDefense() {
        return defense;
    }
    public int getSpeed() {
        return speed;
    }
    public int getHpActual() {
        return hpActual;
    }
    public void recibirDanio(int danio) {
        hpActual = Math.max(0, hpActual - danio);
    }

    public boolean estaDerrotado() {
        return hpActual == 0;

    }






}
