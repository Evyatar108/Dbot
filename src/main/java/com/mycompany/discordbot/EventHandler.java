/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.discordbot;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.DiscordDisconnectedEvent;
import sx.blah.discord.handle.impl.events.MessageReceivedEvent;
import sx.blah.discord.handle.impl.events.ReadyEvent;
import sx.blah.discord.handle.obj.IPrivateChannel;

/**
 *
 * @author Evyatar M
 */
public class EventHandler {

    Map<String, Instant> lastTimeUsed = new HashMap<String, Instant>();
    int cooldown = 10;
    int cooldownLeft;
    int cooldownSeconds;
    int cooldownMinutes;

    @EventSubscriber
    public void onDiscordDisconnectEvents(DiscordDisconnectedEvent event) {
        Dbot.login();
    }

    @EventSubscriber
    public void onReadyEvent(ReadyEvent event) {

        Dbot.logger.log(Level.INFO, "The bot is now ready");

        if (!Dbot.checkInitializedFrame()) {
            Dbot.setFrameClient();

        }
        //  event.getClient().changeUsername("Junior");

        //   doSomething();
    }

    public String[] extractCommands(MessageReceivedEvent event) {
        String args[] = event.getMessage().getContent().trim().split(" ");
        for (int i = 0; i < args.length; i++) {
            args[i] = args[i].toLowerCase();
        }
        return args;
    }

    @EventSubscriber
    public void onMessageEvent(MessageReceivedEvent event) throws Exception {
        String message = ("Channel: " + event.getMessage().getChannel().getName() + "/n Message: " + event.getMessage().getAuthor().getName() + ": " + event.getMessage().getContent());
        System.out.println(message);
        //adding the message to the interface

        Dbot.addTextFrame(event.getMessage().getChannel().getID(), event.getMessage().getAuthor().getName() + ": " + event.getMessage().getContent());
        //check for mentions and then commands
        if (isCommand(event)) {
            if (!(event.getMessage().getChannel() instanceof IPrivateChannel)) {
                String[] args = extractCommands(event);
                if (args.length > 0) {
                    handleCommand(event, args);
                }
            }

        }
    }

    public static boolean isCommand(MessageReceivedEvent event) {
        if (event.getMessage().getContent().charAt(0) == '!') {
            return true;
        }
        return false;
    }

    private boolean hasCooldown(String command) {
        if ((!lastTimeUsed.containsKey(command)) || (lastTimeUsed.get(command).plusSeconds(cooldown).isBefore(Instant.now()))) {
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
                case "!hm": {
                    if (handleHangmanCommand(event, commands)) {
                        return true;
                    }
                }
                case "!ttt": {
                    if (handleHangmanCommand(event, commands)) {
                        return true;
                    }
                }
                default:
                    if (handleSimpleCommand(event, commands)) {
                        return true;
                    }
            }

            event.getMessage().reply("\nUnrecognized command,\nuse !help to see the list of commands");

        } catch (Exception exc) {
            Dbot.logger.log(Level.INFO, "problem with command: " + commands[0] + " error: " + exc);
        }

        return false;
    }

    private boolean handleSimpleCommand(MessageReceivedEvent event, String[] commands) throws Exception {
        switch (commands[0]) {
            case "!help": {
                //if 1. The command wasnt used before Or 2. 10 minutes passed
                if (!hasCooldown(commands[0])) {
                    event.getMessage().getChannel().sendMessage(Resources.help());
                    lastTimeUsed.put(commands[0], Instant.now());
                    return true;
                } else { //or tell the user how much cooldown is left
                    event.getMessage().reply(cooldownReply(commands[0]));
                    return true;
                }

            }

            case "!quote": {
                if (!hasCooldown(commands[0])) {
                    event.getMessage().getChannel().sendMessage(Resources.quotes());
                    lastTimeUsed.put(commands[0], Instant.now());
                    return true;
                } else { //or tell the user how much cooldown is left
                    event.getMessage().getChannel().sendMessage(cooldownReply(commands[0])); //  ;
                    return true;
                }
            }

            case "!sa": {

                if (!hasCooldown(commands[0])) {
                    event.getMessage().getChannel().sendMessage(Resources.sa());
                    lastTimeUsed.put(commands[0], Instant.now());
                    return true;
                } else { //or tell the user how much cooldown is left
                    event.getMessage().getChannel().sendMessage(cooldownReply(commands[0]));
                    return true;
                }

            }

            case "!board": {

                if (!hasCooldown(commands[0])) {
                    event.getMessage().getChannel().sendMessage(ticTacToe.board());
                    lastTimeUsed.put(commands[0], Instant.now());
                    return true;
                } else { //or tell the user how much cooldown is left
                    event.getMessage().getChannel().sendMessage(cooldownReply(commands[0]));
                    return true;
                }

            }
        }
        return false;
    }

    private boolean handleHangmanCommand(MessageReceivedEvent event, String[] commands) throws Exception {
        if ((commands.length < 2) || (commands[1].equals("help"))) {
            if (!hasCooldown(commands[0])) {
                event.getMessage().getChannel().sendMessage(hangman.help());
                lastTimeUsed.put(commands[0], Instant.now());
                return true;
            } else {
                event.getMessage().reply(cooldownReply(commands[0]));
                return true;
            }
        } else if (commands[1].equals("start")) {
            if (hangman.getState()) {
                event.getMessage().reply("Game is already in progress \n" + hangman.info());
                return true;
            } else {
                event.getMessage().getChannel().sendMessage(hangman.start(event.getMessage().getChannel()));
                return true;
            }
        } else if ((commands[1].matches("[a-z]")) && (commands[1].length() == 1)) {
            if (hangman.getState()) {
                if (event.getMessage().getChannel().equals(hangman.getChannel())) {
                    event.getMessage().getChannel().sendMessage(hangman.tryLetter(commands[1].toCharArray()[0], event.getMessage()));
                    return true;
                } else {
                    event.getMessage().reply("The game is in channel " + hangman.getChannel().getName());
                }
            } else {
                event.getMessage().reply("Start a game first!");
                return true;
            }
        } else if (commands[1].equals("info")) {
            event.getMessage().getChannel().sendMessage(hangman.info());
            return true;

        } else if (commands[1].equals("leaderboard")) {
            event.getMessage().getChannel().sendMessage(hangman.showLeaderboard(event.getClient()));
            return true;

        } else if (hangman.getState()) {
            if ((commands.length > 1) && (hangman.isWord(commands[1]))) {
                event.getMessage().getChannel().sendMessage(hangman.endGame(event.getMessage().getAuthor(), true));
                return true;
            } else {
                event.getMessage().reply("That's not the word");
                return true;
            }
        }
        return false;
    }

    private boolean handleTicTacToeCommand(MessageReceivedEvent event, String[] commands) throws Exception {
        if ((commands.length < 2) || (commands[1].equals("help"))) {
            if (!hasCooldown(commands[0])) {
                event.getMessage().getChannel().sendMessage(ticTacToe.help());
                lastTimeUsed.put(commands[0], Instant.now());
                return true;
            } else {
                event.getMessage().reply(cooldownReply(commands[0]));
                return true;
            }
        } else if (commands[1].equals("start")) {
            if (ticTacToe.getState()) {
                event.getMessage().reply("\nGame is already in progress \n" + ticTacToe.info());
                return true;
            } else if ((!event.getMessage().getMentions().isEmpty()) && (commands.length == 3)) {
                if (!event.getMessage().getAuthor().equals(event.getMessage().getMentions().get(0))) {
                    ticTacToe.start(event.getMessage().getAuthor(), event.getMessage().getMentions().get(0), event.getMessage().getChannel());
                    event.getMessage().getChannel().sendMessage("Game started!\n" + ticTacToe.info());
                    return true;
                } else {
                    event.getMessage().reply("You cant play with yourself");
                    return true;
                }
            } else {
                event.getMessage().reply("Wrong parameters (example- \"!ttt start @mentionFriend\"");
                return true;
            }
        } else if (commands[1].equals("info")) {
            event.getMessage().getChannel().sendMessage(ticTacToe.info());
            return true;

        } else if (commands[1].equals("abort")) {
            event.getMessage().getChannel().sendMessage(ticTacToe.abort());
            return true;

        } else if (commands[1].equals("leaderboard")) {
            event.getMessage().getChannel().sendMessage(ticTacToe.showLeaderboard(event.getClient()));
            return true;
        } else if (ticTacToe.getState()) {
            if (event.getMessage().getChannel().equals(ticTacToe.getChannel())) {
                if ((commands.length == 2) && (commands[1].equals("giveup"))) {
                    if (event.getMessage().getAuthor().equals(ticTacToe.getCurrentUser())) {
                        event.getMessage().getChannel().sendMessage(ticTacToe.giveUp());
                        return true;
                    } else if (event.getMessage().getAuthor().equals(ticTacToe.getOtherUser())) {
                        event.getMessage().getChannel().sendMessage(ticTacToe.giveUpOther());
                        return true;
                    }
                } else if (event.getMessage().getAuthor().equals(ticTacToe.getCurrentUser())) {

                    if (commands.length == 3) {
                        event.getMessage().getChannel().sendMessage(ticTacToe.turn(commands[1], commands[2]));
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
