/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.discordbot.RPG;

import java.util.Map;
import java.lang.Math;
import java.time.Instant;

/**
 *
 * @author Evyatar M
 */
public class Player {

    private String id;
    private Battle currentBattle;
    Map<String, Integer> stats;
    private Instant deathTime;

    public Player(String id) {
        this.id = id;
        addStat("Strength", 10);
        addStat("Speed", 10);
        addStat("Hp", 100);
        addStat("maxHp", 100);
        addStat("Defence", 10);
        addStat("Points", 0);
        //death

    }

    private Integer calcCost(String statName) {

        return (int) (Math.pow(1.414213562374, stats.get(statName) - 9));
    }

    public boolean isDead() {
        if (stats.get("Hp") <= 0) {
            return true;
        }
        return false;
    }

    private void addStat(String statName, Integer statValue) {
        stats.put(statName, statValue);
    }

    public void setCurrentBattle(Battle currentBattle) {
        this.currentBattle = currentBattle;
    }

    public String hit(Player attackingP) {

        return "";

    }

}
