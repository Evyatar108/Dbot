/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.dbot;

import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.MessageReceivedEvent;
import sx.blah.discord.handle.impl.events.ReadyEvent;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IPrivateChannel;

/**
 *
 * @author Evyatar M
 */
public class EventHandler {

    Map<String, Instant> lastTimeUsed = new HashMap<String, Instant>();
    int cooldown = 600;
    int cooldownLeft;
    int cooldownSeconds;
    int cooldownMinutes;

    @EventSubscriber
    public void onReadyEvent(ReadyEvent event) {

        Bot.logger.log(Level.INFO, "The bot is now ready");
        try {
            event.getClient().changeUsername("Junior");
        } catch (Exception exc) {
            Bot.logger.log(Level.WARNING, "Failed to change nick: " + exc);
        }
        //   doSomething();
    }

    @EventSubscriber
    public void onMessageEvent(MessageReceivedEvent event) throws Exception {
        System.out.println(event.getMessage().getAuthor().getName() + ": " + event.getMessage().getContent());

        if (mentionMe(event)) {
            if (!(event.getMessage().getChannel() instanceof IPrivateChannel)) {

                String args[] = removeMentions(event.getMessage().getContent()).trim().split(" ");
                System.out.println(args[0]);
                for (int i = 0; i < args.length; i++) {
                    args[i] = args[i].toLowerCase();
                }
                if (args.length > 0) {
                    handleCommand(event, args);

                }

            }
        }
    }

    private boolean hasCooldown(String command) {
        if ((!lastTimeUsed.containsKey(command)) || (lastTimeUsed.get(command).plusSeconds(600).isBefore(Instant.now()))) {
            return false;
        }
        return true;

    }

    private String cooldownReply(String command) {
        cooldownLeft = (int) (cooldown - (Instant.now().getEpochSecond() - lastTimeUsed.get(command).getEpochSecond()));
        cooldownMinutes = cooldownLeft / 60;
        cooldownSeconds = cooldownLeft % 60;
        return "There are still " + cooldownMinutes + " minutes and " + cooldownSeconds + " seconds left";
    }

    private boolean mentionMe(MessageReceivedEvent message) {
        if (!message.getMessage().getMentions().isEmpty()) {
            return message.getMessage().getMentions().get(0).getID().equals(message.getClient().getOurUser().getID());
        }
        return false;
    }

    private String removeMentions(String message) {
        return message.replaceAll("<@[0-9]{18}>", "");
    }

    private boolean handleCommand(MessageReceivedEvent event, String[] commands) {
        try {
            for(int i=0;i<commands.length;i++)
                System.out.println("Variable "+i+" : "+commands[i]);
            switch (commands[0]) {
                case "help": {
                    //if 1. The command wasnt used before Or 2. 10 minutes passed
                    if (!hasCooldown(commands[0])) {
                        event.getMessage().reply(Resources.help());
                        lastTimeUsed.put(commands[0], Instant.now());
                        return true;
                    } else { //or tell the user how much cooldown is left
                        event.getMessage().reply(cooldownReply(commands[0]));
                    }
                    break;
                }

                case "quote": {
                    if (!hasCooldown(commands[0])) {
                        event.getMessage().reply(Resources.quotes());
                        lastTimeUsed.put(commands[0], Instant.now());
                        return true;
                    } else { //or tell the user how much cooldown is left
                        event.getMessage().reply(cooldownReply(commands[0]));
                    }
                    break;
                }

                case "sa": {

                    if (!hasCooldown(commands[0])) {
                        System.out.println("0");
                        event.getMessage().reply(Resources.sa());
                        lastTimeUsed.put(commands[0], Instant.now());
                        return true;
                    } else { //or tell the user how much cooldown is left
                        event.getMessage().reply(cooldownReply(commands[0]));
                    }
                    break;
                }

                case "hangmanis": {
                    if (hangman.getState()) {
                        if ((commands.length > 1) && (hangman.isWord(commands[1]))) {
                            hangman.end(event.getMessage().getAuthor(), true);
                        }
                    }
                }

                case "hangman": {
                    System.out.println("1");
                    if ((commands.length < 2) || (commands[1].equals("help"))) {
                        System.out.println("2");
                        if (!hasCooldown(commands[0])) {
                            System.out.println("3");
                            event.getMessage().reply(hangman.hangmanHelp());
                            lastTimeUsed.put(commands[0], Instant.now());
                            break;
                        } else {
                            System.out.println("4");
                            event.getMessage().reply(cooldownReply(commands[0]));
                            break;
                        }
                    } else if (commands[1].equals("start")) {
                        System.out.println("5");
                        if (hangman.getState()) {
                            System.out.println("6");
                            event.getMessage().reply("Game is already in progress \n"+hangman.info());
                            break;
                        } else {
                            System.out.println("7");
                            hangman.start(event.getMessage().getChannel());
                            break;
                        }
                        

                    } else if ((commands[1].matches("[a-z]")) && (commands[1].length() == 1)) {
                        System.out.println("8");
                        if (hangman.getState()) {
                            System.out.println("9");
                            hangman.tryLetter(commands[1].toCharArray()[0], event.getMessage());
                            break;
                        } else {
                            event.getMessage().reply("Start a game first!");
                            break;
                        }

                    } else if (commands[1].equals("info")) {
                        event.getMessage().reply(hangman.info());
                        break;

                    }

                }
                default:
                    event.getMessage().reply("Unrecognized command");

            }
        } catch (Exception exc) {
            Bot.logger.log(Level.INFO, "problem with command: " + commands[0] + " error: " + exc);
        }
        return false;
    }
}
