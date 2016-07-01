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
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;
import java.util.logging.Level;
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

    public static String help() {
        return "\n**`!hm`** `start`\n    Starts a new game if no game is active, with a random word\n**`!hm`** `info`\n    Gives you info about the current game \n**`!hm`** `*letter*`\n    (for example \"!hm a\")  \n**`!hm`** `*word*`\n    try the \\*word\\*\n**`!hm`** `leaderboard`\n    to see the leaderboard";
    }

    public static boolean getState() {
        return state;
    }

    public static String start(IChannel channel) {
        try {
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
        } catch (Exception exc) {
            Dbot.logger.log(Level.WARNING, "error: " + exc);
        }
        return "failed";
    }

    public static String info() {
        return ("\nChannel: " + channel.getName() + "\nWord: " + concealedWord + "\nLives: " + lives + "\nUsed Letters: " + hangman.getUsedLetters());
    }

    public static String showLeaderboard(IDiscordClient client) {
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

    public static String endGame(IUser user, boolean win) {
        String result = "";
        try {
            if (win) {
                if (leaderboard == null) {
                    loadLB();
                }
                if (leaderboard.containsKey(user.getID())) {
                    leaderboard.replace(user.getID(), leaderboard.get(user.getID()) + 1);

                } else {
                    leaderboard.put(user.getID(), 1);
                }
                saveLB();
                result = (user.mention() + " is the winner!");
            } else {
                result = ("Game Over \nThe word was: " + word);
            }

        } catch (Exception exc) {
            Dbot.logger.log(Level.WARNING, "error sending message");
        }

        state = false;
        word = "";
        concealed = null;
        letters = null;
        concealedWord = "";
        return result;
    }

    public static String tryLetter(Character letter, IMessage message) {
        String result = "";
        try {
            if (!(letters.add(letter))) {
                message.reply(letter + " is already used");
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

        } catch (Exception exc) {
            Dbot.logger.log(Level.WARNING, "error: " + exc);
        }
        return result;
    }

    public static boolean isWord(String tryword) {
        if (word.equals(tryword.toLowerCase())) {
            return true;
        }
        return false;
    }

    public static IChannel getChannel() {
        return channel;
    }

    private static void saveLB() {
        File file = new File("C:\\Bot\\Resources\\hangmanLeaderboard.txt");
        try {
            if (!file.exists()) {
                file.createNewFile();
            }

            FileOutputStream saveFile = new FileOutputStream("C:\\Bot\\Resources\\hangmanLeaderboard.txt");
            ObjectOutputStream save = new ObjectOutputStream(saveFile);
            save.writeObject(leaderboard);
            save.close();
            saveFile.close();
        } catch (IOException ioex) {

        }
    }

    private static void loadLB() {
        File file = new File("C:\\Bot\\Resources\\hangmanLeaderboard.txt");
        try {
            if (file.exists()) {
                FileInputStream saveFile = new FileInputStream("C:\\Bot\\Resources\\hangmanLeaderboard.txt");
                ObjectInputStream save = new ObjectInputStream(saveFile);
                leaderboard = (HashMap) save.readObject();
                save.close();
                saveFile.close();
            } else {
                leaderboard = new HashMap<String, Integer>();
                saveLB();
            }
        } catch (IOException ioexc) {

        } catch (ClassNotFoundException cnfexc) {

        }
    }

    public static String getUsedLetters() {
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
        try {
            File f = new File("C:\\Bot\\Resources\\wordlist.txt");
            String result = null;
            Random rand = new Random();
            int n = 0;
            for (Scanner sc = new Scanner(f); sc.hasNext();) {
                ++n;
                String line = sc.nextLine();
                if (rand.nextInt(n) == 0) {
                    result = line;
                }
            }

            return result;
        } catch (FileNotFoundException exc) {
            Dbot.logger.log(Level.WARNING, "problem with hangman resource file: " + exc);
            return ("Error?");
        }
    }

}
