/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.discordbot.RPG;

import java.util.Map;

/**
 *
 * @author Evyatar M
 */
public class Player {

    private String id;
    private Battle currentBattle;
    Map<Stat, Integer> stats;

    public Player(String id) {
        this.id = id;

    }

    private void resetStatMap(Map<Stat, Integer> statMap) {
        for (Stat stat : Stat.values()) {
            statMap.put(stat, 0);
        }
    }


    public void setCurrentBattle(Battle currentBattle){
        this.currentBattle = currentBattle;
    }

}
