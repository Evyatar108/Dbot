/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.discordbot;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IUser;

/**
 *
 * @author Evyatar M
 */
public class ticTacToe {

    private static String[][] board;
    private static IUser user, otherUser;
    private static boolean state = false;
    private static IChannel channel;
    private static Instant startTime;
    private static String currentSymbol = "X";
    private static String otherSymbol = "O";
    private static int cooldown = 600;
    private static Map<String, Integer> leaderboard;
    private static Logger tLogger;

    private static void saveLB() {
        tLogger.log(Level.INFO, "ttt saveLB - handling");
        File file = new File("C:\\Bot\\Resources\\TicTacToeLeaderboard.txt");
        try {
            if (!file.exists()) {
                file.createNewFile();
                tLogger.log(Level.INFO, "ttt saveLB - created file");
            }
            tLogger.log(Level.INFO, "ttt saveLB - savig to file");
            FileOutputStream saveFile = new FileOutputStream("C:\\Bot\\Resources\\TicTacToeLeaderboard.txt");
            ObjectOutputStream save = new ObjectOutputStream(saveFile);
            save.writeObject(leaderboard);
            save.close();
            saveFile.close();
            tLogger.log(Level.INFO, "ttt saveLB - saved to file");
        } catch (IOException exc) {
            tLogger.log(Level.WARNING, "Exception - ttt saveLB " + exc);
        }
    }

    private static void loadLB() {
        tLogger.log(Level.INFO, "ttt loadLB - handling");
        File file = new File("C:\\Bot\\Resources\\TicTacToeLeaderboard.txt");
        try {
            if (file.exists()) {
                tLogger.log(Level.INFO, "ttt loadLB - loading from file");
                FileInputStream saveFile = new FileInputStream("C:\\Bot\\Resources\\TicTacToeLeaderboard.txt");
                ObjectInputStream save = new ObjectInputStream(saveFile);
                leaderboard = (HashMap) save.readObject();
                save.close();
                saveFile.close();
                tLogger.log(Level.INFO, "ttt loadLB - loaded from file");
            } else {
                tLogger.log(Level.INFO, "ttt loadLB - file doesnt exist");
                leaderboard = new HashMap<String, Integer>();
                saveLB();
            }
        } catch (Exception exc) {
            tLogger.log(Level.WARNING, "Exception - ttt loadLB " + exc);
        }
    }

    public static void start(IUser userA, IUser userB, IChannel newChannel) {
        tLogger.log(Level.INFO, "ttt start command - handling");
        channel = newChannel;
        otherUser = userA;
        user = userB;
        board = new String[3][3];
        currentSymbol = "X";
        otherSymbol = "O";
        startTime = Instant.now();
        state = true;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                board[i][j] = "  ";
            }
        }
    }

    public static String showLeaderboard(IDiscordClient client) {
        tLogger.log(Level.INFO, "ttt show leaderboard command - handling");
        String leaderboardlist = "";
        if (leaderboard == null) {
            loadLB();
        }
        if (leaderboard.isEmpty()) {
            return ("No scores yet");
        }
        for (Map.Entry<String, Integer> entry : leaderboard.entrySet()) {
            String userName = client.getUserByID(entry.getKey()).getName();
            Integer score = entry.getValue();
            leaderboardlist += userName + " " + score + " points\n";

        }
        return leaderboardlist;
    }

    private static String checkVictory() {
        tLogger.log(Level.INFO, "ttt checking victory conditions");
        boolean victory = false;
        int counter = 0;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (board[i][j].equals(otherSymbol)) {
                    counter++;
                }
            }
            if (counter == 3) {
                victory = true;
                break;
            }
            counter = 0;
        }

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (board[j][i].equals(otherSymbol)) {
                    counter++;
                }
            }
            if (counter == 3) {
                victory = true;
                break;
            }
            counter = 0;
        }

        if (((board[0][0].equals(otherSymbol)) && (board[1][1].equals(otherSymbol))) && (board[2][2].equals(otherSymbol))) {
            victory = true;
        }

        if (((board[2][0].equals(otherSymbol)) && (board[1][1].equals(otherSymbol))) && (board[0][2].equals(otherSymbol))) {
            victory = true;
        }

        if (victory) {
            endGame();
            addPoint(otherUser);
            return ("\n" + otherUser.getName() + " is the winner!");

        }
        return "";
    }

    private static void addPoint(IUser winner) {
        tLogger.log(Level.INFO, "ttt addpoint to the winner");
        if (leaderboard == null) {
            loadLB();
        }
        if (leaderboard.containsKey(winner.getID())) {
            leaderboard.replace(winner.getID(), leaderboard.get(winner.getID()) + 1);

        } else {
            leaderboard.put(winner.getID(), 1);
        }
        saveLB();
    }

    public static IChannel getChannel() {
        return channel;
    }

    public static boolean getState() {
        return state;
    }

    public static IUser getCurrentUser() {
        return user;
    }

    public static IUser getOtherUser() {
        return otherUser;
    }

    public static String info() {
        tLogger.log(Level.INFO, "ttt info request");
        if (state) {
            return ("Game is in channel: " + channel.getName() + "\nIts " + user.getName() + "'s turn\nThe other player is " + otherUser.getName() + "\n\n" + board());
        }
        return ("Start a game first");
    }

    public static String help() {
        return ("\n**`!ttt`** `start` `@mention`\n    Starts a new game if no game is active, with the mentioned user\n**`!ttt`** `info`\n    Gives you info about the current game \n**`!ttt`** `*height*` `*width*`\n    height = top/middle/bottom \n    width = left/middle/right \n**`!ttt`** `abort`\n    try to abort the game if 10 minutes passed since it's begining\n**`!ttt`** `giveup`\n    giving up!\n**`!ttt`** `leaderboard`\n    to see the leaderboard");
    }

    public static String turn(String height, String width) {
        tLogger.log(Level.INFO, "ttt turn command");
        int a = -1, b = -1;
        boolean foundHeight = true, foundWidth = true;
        if (height.equals("top")) {
            a = 0;
        } else if (height.equals("middle")) {
            a = 1;
        } else if (height.equals("bottom")) {
            a = 2;
        }

        if (width.equals("left")) {
            b = 0;
        } else if (width.equals("middle")) {
            b = 1;
        }
        if (width.equals("right")) {
            b = 2;
        }

        if ((a == -1) || (b == -1)) {
            if (width.equals("top")) {
                a = 0;
            } else if (width.equals("middle")) {
                a = 1;
            } else if (width.equals("bottom")) {
                a = 2;
            }

            if (height.equals("left")) {
                b = 0;
            } else if (height.equals("middle")) {
                b = 1;
            }
            if (height.equals("right")) {
                b = 2;
            }

        }

        if ((a == -1) || (b == -1)) {
            return "failed to find height/width";
        }

        if (!board[a][b].equals("  ")) {
            return ("This location is already taken");
        } else {
            board[a][b] = currentSymbol;

            IUser tempUser = user;
            user = otherUser;
            otherUser = tempUser;

            String tempSymbol = currentSymbol;
            currentSymbol = otherSymbol;
            otherSymbol = tempSymbol;

            String info = info();
            String check = checkVictory();
            if ((check.equals("")) && (isTie())) {
                endGame();
                check = "\nIts a tie";
            }
            return (info + "\n" + check);
        }
    }

    private static boolean isTie() {
        tLogger.log(Level.INFO, "ttt checking if tie");
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (board[i][j].equals("  ")) {
                    return false;
                }
            }
        }
        return true;
    }

    public static String abort() {
        tLogger.log(Level.INFO, "ttt abort command - handling");
        if (startTime.plusSeconds(cooldown).isBefore(Instant.now())) {
            endGame();
            return ("Game - aborted");
        }
        int cooldownLeft = (int) (cooldown - (Instant.now().getEpochSecond() - startTime.getEpochSecond()));
        int cooldownMinutes = cooldownLeft / 60;
        int cooldownSeconds = cooldownLeft % 60;
        return ("There are still " + cooldownMinutes + " minutes and " + cooldownSeconds + " seconds left");
    }

    public static String giveUp() {
        tLogger.log(Level.INFO, "ttt give up command - handling");
        endGame();
        addPoint(otherUser);
        return (user.getName() + " gave up, " + otherUser.getName() + " wins!");
    }

    public static String giveUpOther() {
        tLogger.log(Level.INFO, "ttt give up other command - handling");
        endGame();
        addPoint(user);
        return (otherUser.getName() + " gave up, " + user.getName() + " wins!");
    }

    private static void endGame() {
        tLogger.log(Level.INFO, "ttt end game - handling");
        state = false;

    }

    public static String board() {
        return ("  " + board[0][0] + "  ⎹⎸  " + board[0][1] + "  ⎹⎸  " + board[0][2] + "  \n———————\n  " + board[1][0] + "  ⎹⎸  " + board[1][1] + "  ⎹⎸  " + board[1][2] + "  \n———————\n  " + board[2][0] + "  ⎹⎸  " + board[2][1] + "  ⎹⎸  " + board[2][2] + "  ");
    }

}
//
