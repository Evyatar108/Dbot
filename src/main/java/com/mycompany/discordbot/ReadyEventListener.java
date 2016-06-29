/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.discordbot;

import sx.blah.discord.api.events.IListener;
import sx.blah.discord.handle.impl.events.ReadyEvent;

/**
 *
 * @author Evyatar M
 */
public class ReadyEventListener implements IListener<ReadyEvent>
{
    public void handle(ReadyEvent event)
    {
        //System.out.println("The bot is now ready2");
        //doSomething();
    }
}
