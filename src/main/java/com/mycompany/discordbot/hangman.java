/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.discordbot;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;

/**
 *
 * @author Evyatar M
 */
public class hangman {

    private static Map<String, Integer> leaderboard;
    private static Set<Character> letters;
    private static boolean state = false;
    private static String word = "";
    private static Character[] concealed;
    private static int lives = 10;
    private static String concealedWord = "";
    private static IChannel channel;
    private static Logger logger = Logger.getLogger("hangman");

    public static String help() {
        return "\n**`!hm`** `start`\n    Starts a new game if no game is active, with a random word\n**`!hm`** `info`\n    Gives you info about the current game \n**`!hm`** `*letter*`\n    (for example \"!hm a\")  \n**`!hm`** `*word*`\n    try the \\*word\\*\n**`!hm`** `leaderboard`\n    to see the leaderboard";
    }

    public static boolean getState() {
        return state;
    }

    public static String start(IChannel channel) {
        try {
            logger.log(Level.INFO, "hm start command - handling");
            state = true;
            hangman.channel = channel;
            letters = new HashSet<Character>();
            concealedWord = "";
            lives = 10;
            word = getRandWord();
            concealed = new Character[word.length()];
            for (int i = 0; i < concealed.length; i++) {
                concealed[i] = '⌴';

                concealedWord += '⌴';
            }

            return ("Game started!\n" + hangman.info());
        } catch (Exception e) {
            // <editor-fold defaultstate="collapsed" desc="Stack trace frame">
            StringBuilder sb = new StringBuilder(e.toString());
            for (StackTraceElement ste : e.getStackTrace()) {
                sb.append("\n\tat ");
                sb.append(ste);
            }
            String trace = sb.toString();
            logger.log(Level.WARNING, "hangman Exception - start \n " + trace);
            // </editor-fold>
        }
        return "failed";
    }

    public static String info() {
        logger.log(Level.INFO, "hm info command - handling");
        return ("\nChannel: " + channel.getName() + "\nWord: " + concealedWord + "\nLives: " + lives + "\nUsed Letters: " + hangman.getUsedLetters());
    }

    public static String showLeaderboard(IDiscordClient client) {
        logger.log(Level.INFO, "hm showLeaderboard command - handling");
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

    public static void addPoint(String id, int points) {
        if (leaderboard == null) {
            loadLB();
        }
        if (leaderboard.containsKey(id)) {
            leaderboard.replace(id, leaderboard.get(id) + points);
        } else {
            leaderboard.put(id, points);
        }
    }

    public static String endGame(IUser user, boolean win) {
        logger.log(Level.INFO, "hm endgame - handling");
        String result = "";
        try {
            if (win) {
                addPoint(user.getID(), 1);
                saveLB();
                result = (user.mention() + " is the winner!");
            } else {
                result = ("Game Over \nThe word was: " + word);
            }

        } catch (Exception exc) {
            logger.log(Level.WARNING, "error sending message");
        }

        state = false;
        word = "";
        concealed = null;
        letters = null;
        concealedWord = "";
        return result;
    }

    public static String tryLetter(Character letter, IMessage message) {
        logger.log(Level.INFO, "hm tryLetter - handling");
        String result = "";
        try {
            if (!(letters.add(letter))) {
                result = (letter + " is already used");
            } else if (word.indexOf(letter) == -1) {
                lives--;
                result += ("The word does not contain " + letter + "\nLives: " + lives);
                if (lives == 0) {
                    result += "\n" + (hangman.endGame(message.getAuthor(), false));
                }
            } else {

                int previndex = 0;
                int index;
                while (true) {
                    index = word.indexOf(letter, previndex);
                    if (index == -1) {
                        break;
                    }
                    concealed[index] = letter;
                    previndex = index + 1;
                    if (previndex >= concealed.length) {
                        break;
                    }
                }
                concealedWord = "";
                for (int i = 0; i < concealed.length; i++) {
                    concealedWord += concealed[i];
                }
                result += ("The word contains " + letter + "\n" + hangman.info());
                if (word.equals(concealedWord)) {
                    result += "\n" + hangman.endGame(message.getAuthor(), true);
                }

            }

        } catch (Exception e) {
            // <editor-fold defaultstate="collapsed" desc="Stack trace frame">
            StringBuilder sb = new StringBuilder(e.toString());
            for (StackTraceElement ste : e.getStackTrace()) {
                sb.append("\n\tat ");
                sb.append(ste);
            }
            String trace = sb.toString();
            logger.log(Level.WARNING, "hangman Exception - tryLetter \n " + trace);
            // </editor-fold>
        }
        return result;
    }

    public static boolean isWord(String tryword) {
        logger.log(Level.INFO, "hm isWord - handling");
        if (word.equals(tryword.toLowerCase())) {
            return true;
        }
        return false;
    }

    public static IChannel getChannel() {
        return channel;
    }

    private static void saveLB() {
        logger.log(Level.INFO, "hm saveLB - handling");
        File file = new File(System.getProperty("user.home") + "/Documents/Dbot/hangmanLeaderboard.txt");
        try {
            if (!file.exists()) {
                logger.log(Level.INFO, "hm  saveLB - file doesnt exist");
                file.createNewFile();
                logger.log(Level.INFO, "hm saveLB - file created");
            }
            logger.log(Level.INFO, "hm saveLB - saving to file");
            FileOutputStream saveFile = new FileOutputStream(System.getProperty("user.home") + "/Documents/Dbot/hangmanLeaderboard.txt");
            ObjectOutputStream save = new ObjectOutputStream(saveFile);
            save.writeObject(leaderboard);
            save.close();
            saveFile.close();
            logger.log(Level.INFO, "hm saveLB - saved to file");
        } catch (Exception e) {
            // <editor-fold defaultstate="collapsed" desc="Stack trace frame">
            StringBuilder sb = new StringBuilder(e.toString());
            for (StackTraceElement ste : e.getStackTrace()) {
                sb.append("\n\tat ");
                sb.append(ste);
            }
            String trace = sb.toString();
            logger.log(Level.WARNING, "hangman Exception - saveLB \n " + trace);
            // </editor-fold>
        }
    }

    private static void loadLB() {
        logger.log(Level.INFO, "hm loadLB - handling");
        File file = new File(System.getProperty("user.home") + "/Documents/Dbot/hangmanLeaderboard.txt");
        try {
            if (file.exists()) {
                logger.log(Level.INFO, "hm loadLB - loading file");
                FileInputStream saveFile = new FileInputStream(System.getProperty("user.home") + "/Documents/Dbot/hangmanLeaderboard.txt");
                ObjectInputStream save = new ObjectInputStream(saveFile);
                leaderboard = (HashMap) save.readObject();
                save.close();
                saveFile.close();
                logger.log(Level.INFO, "hm loadLB - loaded file");
            } else {
                logger.log(Level.INFO, "hm loadLB - file doesnt exist");
                leaderboard = new HashMap<String, Integer>();
                saveLB();
            }
        } catch (Exception e) {
            // <editor-fold defaultstate="collapsed" desc="Stack trace frame">
            StringBuilder sb = new StringBuilder(e.toString());
            for (StackTraceElement ste : e.getStackTrace()) {
                sb.append("\n\tat ");
                sb.append(ste);
            }
            String trace = sb.toString();
            logger.log(Level.WARNING, "hangman Exception - loadLB \n " + trace);
            // </editor-fold>
        }
    }

    public static String getUsedLetters() {
        logger.log(Level.INFO, "hm getUsedLetters - handling");
        String temp = "";
        if (!letters.isEmpty()) {
            for (Character ch : letters) {
                temp += ch + ", ";
            }
            return temp.substring(0, temp.length() - 2);
        }
        return temp;
    }

    private static String getRandWord() {
        String result = "";
        logger.log(Level.INFO, "hm getRandWord - handling");
        try {
            File f = new File(System.getProperty("user.home") + "/Documents/Dbot/wordlist.txt");
            Random rand = new Random();
            int n = 0;
            for (Scanner sc = new Scanner(f); sc.hasNext();) {
                ++n;
                String line = sc.nextLine();
                if (rand.nextInt(n) == 0) {
                    result = line;
                }
            }
            logger.log(Level.INFO, "hm getRandWord - got random word");
        } catch (FileNotFoundException e) {
            // <editor-fold defaultstate="collapsed" desc="Stack trace frame">
            StringBuilder sb = new StringBuilder(e.toString());
            for (StackTraceElement ste : e.getStackTrace()) {
                sb.append("\n\tat ");
                sb.append(ste);
            }
            String trace = sb.toString();
            logger.log(Level.WARNING, "hangman Exception - getRandWord \n " + trace);
            // </editor-fold>
        }
        return result;
    }

}
