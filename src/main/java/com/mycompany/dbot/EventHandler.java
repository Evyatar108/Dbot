/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.dbot;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.MessageReceivedEvent;
import sx.blah.discord.handle.impl.events.ReadyEvent;
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
            Bot.logger.log(Level.WARNING, "Failed to change nick: "+ exc);
        }
        //   doSomething();
    }

    @EventSubscriber
    public void onMessageEvent(MessageReceivedEvent event) throws Exception {
        System.out.println("Guild: " + event.getMessage().getGuild() + " Channel: " + event.getMessage().getChannel() + " Message: " + event.getMessage().getAuthor().getName() + ": " + event.getMessage().getContent());

        if (mentionMe(event)) {
            if (!(event.getMessage().getChannel() instanceof IPrivateChannel)) {

                String args[] = removeMentions(event.getMessage().getContent()).trim().split(" ");
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
                            hangman.endGame(event.getMessage().getAuthor(), true);
                        }
                    }
                }

                case "hangman": {
                    if ((commands.length < 2) || (commands[1].equals("help"))) {
                        if (!hasCooldown(commands[0])) {
                            event.getMessage().reply(hangman.hangmanHelp());
                            lastTimeUsed.put(commands[0], Instant.now());
                            break;
                        } else {
                            event.getMessage().reply(cooldownReply(commands[0]));
                            break;
                        }
                    } else if (commands[1].equals("start")) {
                        if (hangman.getState()) {
                            event.getMessage().reply("Game is already in progress \n" + hangman.info());
                            break;
                        } else {
                            hangman.start(event.getMessage().getChannel());
                            break;
                        }

                    } else if ((commands[1].matches("[a-z]")) && (commands[1].length() == 1)) {
                        if (hangman.getState()) {
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
                    event.getMessage().reply("\nUnrecognized command,\nuse help to see the list of commands");

            }
        } catch (Exception exc) {
            Bot.logger.log(Level.INFO, "problem with command: " + commands[0] + " error: " + exc);
        }
        return false;
    }
}
