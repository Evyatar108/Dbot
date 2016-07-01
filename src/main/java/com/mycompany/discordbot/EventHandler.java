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
import sx.blah.discord.handle.impl.events.MentionEvent;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IPrivateChannel;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.MessageBuilder;
import sx.blah.discord.util.MissingPermissionsException;
import sx.blah.discord.util.RequestBuffer;

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
    public void onMentionEvent(MentionEvent event) {
        if (event.getMessage().getAuthor().getID().equals("132466660870193153")) {
            sendMessage(event.getMessage().getChannel(), Resources.love(event.getMessage().getAuthor().getName()));
        }
    }

    @EventSubscriber
    public void onMessageEvent(MessageReceivedEvent event) throws Exception {
        String message = ("Channel: " + event.getMessage().getChannel().getName() + "/n Message: " + event.getMessage().getAuthor().getName() + ": " + event.getMessage().getContent());
        System.out.println(message);
        //adding the message to the interface

        Dbot.addTextFrame(event.getMessage().getChannel().getID(), event.getMessage().getAuthor().getName() + ": " + event.getMessage().getContent());
        //check for mentions and then commands
        //    if(mentionMe(event)){
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
        switch (commands[0]) {
            case "!hm": {
                if (handleHangmanCommand(event, commands)) {
                    return true;
                }
            }
            case "!ttt": {
                if (handleTicTacToeCommand(event, commands)) {
                    return true;
                }
            }
            default:
                if (handleSimpleCommand(event, commands)) {
                    return true;
                }
        }
        sendReply(event.getMessage(), "\nUnrecognized command,\nuse !help to see the list of commands");

        return false;
    }

    private boolean handleSimpleCommand(MessageReceivedEvent event, String[] commands) {
        switch (commands[0]) {
            case "!help": {
                //if 1. The command wasnt used before Or 2. 10 minutes passed
                if (!hasCooldown(commands[0])) {
                    sendMessage(event.getMessage().getChannel(), Resources.help());
                    lastTimeUsed.put(commands[0], Instant.now());
                    return true;
                } else { //or tell the user how much cooldown is left
                    sendReply(event.getMessage(), cooldownReply(commands[0]));
                    return true;
                }

            }

            case "!quote": {
                if (!hasCooldown(commands[0])) {
                    sendMessage(event.getMessage().getChannel(), Resources.quotes());
                    lastTimeUsed.put(commands[0], Instant.now());
                    return true;
                } else { //or tell the user how much cooldown is left
                    sendMessage(event.getMessage().getChannel(), cooldownReply(commands[0]));
                    return true;
                }
            }

            case "!sa": {

                if (!hasCooldown(commands[0])) {
                    sendMessage(event.getMessage().getChannel(), Resources.sa());
                    lastTimeUsed.put(commands[0], Instant.now());
                    return true;
                } else { //or tell the user how much cooldown is left
                    sendMessage(event.getMessage().getChannel(), cooldownReply(commands[0]));
                    return true;
                }
            }
        }
        return false;
    }

    private boolean handleHangmanCommand(MessageReceivedEvent event, String[] commands) {
        if ((commands.length < 2) || (commands[1].equals("help"))) {
            if (!hasCooldown(commands[0])) {
                sendMessage(event.getMessage().getChannel(), hangman.help());
                lastTimeUsed.put(commands[0], Instant.now());
                return true;
            } else {
                sendReply(event.getMessage(), cooldownReply(commands[0]));
                return true;
            }
        } else if (commands[1].equals("start")) {
            if (hangman.getState()) {
                sendReply(event.getMessage(), "Game is already in progress \n" + hangman.info());
                return true;
            } else {
                sendMessage(event.getMessage().getChannel(), hangman.start(event.getMessage().getChannel()));
                return true;
            }
        } else if ((commands[1].matches("[a-z]")) && (commands[1].length() == 1)) {
            if (hangman.getState()) {
                if (event.getMessage().getChannel().equals(hangman.getChannel())) {
                    sendMessage(event.getMessage().getChannel(), hangman.tryLetter(commands[1].toCharArray()[0], event.getMessage()));
                    return true;
                } else {
                    sendReply(event.getMessage(), "The game is in channel " + hangman.getChannel().getName());
                }
            } else {
                sendReply(event.getMessage(), "Start a game first!");

                return true;
            }
        } else if (commands[1].equals("info")) {
            sendMessage(event.getMessage().getChannel(), hangman.info());
            return true;

        } else if (commands[1].equals("leaderboard")) {
            sendMessage(event.getMessage().getChannel(), hangman.showLeaderboard(event.getClient()));
            return true;

        } else if (hangman.getState()) {
            if ((commands.length > 1) && (hangman.isWord(commands[1]))) {
                sendMessage(event.getMessage().getChannel(), hangman.endGame(event.getMessage().getAuthor(), true));
                return true;
            } else {
                sendReply(event.getMessage(), "That's not the word");
                return true;
            }
        }
        return false;
    }

    private boolean handleTicTacToeCommand(MessageReceivedEvent event, String[] commands) {
        if ((commands.length < 2) || (commands[1].equals("help"))) {
            if (!hasCooldown(commands[0])) {
                sendMessage(event.getMessage().getChannel(), ticTacToe.help());
                lastTimeUsed.put(commands[0], Instant.now());
                return true;
            } else {
                sendReply(event.getMessage(), cooldownReply(commands[0]));
                return true;
            }
        } else if (commands[1].equals("start")) {
            if (ticTacToe.getState()) {

                sendReply(event.getMessage(), "\nGame is already in progress \n" + ticTacToe.info());
                return true;
            } else if ((!event.getMessage().getMentions().isEmpty()) && (commands.length == 3)) {
                if (!event.getMessage().getAuthor().equals(event.getMessage().getMentions().get(0))) {
                    ticTacToe.start(event.getMessage().getAuthor(), event.getMessage().getMentions().get(0), event.getMessage().getChannel());
                    sendMessage(event.getMessage().getChannel(), "Game started!\n" + ticTacToe.info());
                    return true;
                } else {
                    sendReply(event.getMessage(), "You are not allowed to play with yourself");
                    return true;
                }
            } else {
                sendReply(event.getMessage(), "Wrong parameters (example- \"!ttt start @mentionFriend\"");
                return true;
            }
        } else if (commands[1].equals("info")) {

            sendMessage(event.getMessage().getChannel(), ticTacToe.info());
            return true;

        } else if (commands[1].equals("abort")) {
            sendMessage(event.getMessage().getChannel(), ticTacToe.abort());
            return true;

        } else if (commands[1].equals("leaderboard")) {
            sendMessage(event.getMessage().getChannel(), ticTacToe.showLeaderboard(event.getClient()));
        } else if (ticTacToe.getState()) {
            if (event.getMessage().getChannel().equals(ticTacToe.getChannel())) {
                if ((commands.length == 2) && (commands[1].equals("giveup"))) {
                    if (event.getMessage().getAuthor().equals(ticTacToe.getCurrentUser())) {
                        sendMessage(event.getMessage().getChannel(), ticTacToe.giveUp());
                        return true;
                    } else if (event.getMessage().getAuthor().equals(ticTacToe.getOtherUser())) {
                        sendMessage(event.getMessage().getChannel(), ticTacToe.giveUpOther());
                        return true;
                    }
                } else if (event.getMessage().getAuthor().equals(ticTacToe.getCurrentUser())) {

                    if (commands.length == 3) {
                        sendMessage(event.getMessage().getChannel(), ticTacToe.turn(commands[1], commands[2]));
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private static void sendMessage(IChannel channel, String message) {
        RequestBuffer.request(() -> {
            try {
                channel.sendMessage(message);
            } catch (DiscordException e) { //| MissingPermissionsException e) {
                e.printStackTrace();
                sendMessage(channel, message);
            } catch (MissingPermissionsException e) {
                e.printStackTrace();
            }
            return null;
        });

    }

    private static void sendReply(IMessage recipent, String message) {
        RequestBuffer.request(() -> {
            try {
                recipent.reply(message);
            } catch (DiscordException e) { //| MissingPermissionsException e) {
                e.printStackTrace();
                sendReply(recipent, message);
            } catch (MissingPermissionsException e) {
                e.printStackTrace();
            }
            return null;
        });
    }
}
