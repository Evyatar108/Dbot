/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.discordbot.RPG;

import org.apache.commons.lang3.text.WordUtils;
import org.eclipse.jetty.util.StringUtil;

/**
 *
 * @author Evyatar M
 */
public enum Stat {
    //LEVEL,
    //EXP,
    HP,
    MP,
    STRENTH,
    SPEED,
    STAMINA,
    MAGIC,
    ATTACK,
    DEFENCE,
    EVASION,
    MAGIC_DEFENCE,
    MAGIC_EVASION;

    @Override
    public String toString() {
        return WordUtils.capitalize(name()).replace('_', ' ');
    }
}
