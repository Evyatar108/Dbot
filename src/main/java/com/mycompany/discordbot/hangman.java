/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.discordbot;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashSet;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;
import java.util.logging.Level;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;

/**
 *
 * @author Evyatar M
 */
public class hangman {

    private static Set<Character> letters;
    private static boolean state = false;
    private static String word = "";
    private static Character[] concealed;
    private static int lives = 10;
    private static String concealedWord = "";
    private static IChannel channel;

    public static String hangmanHelp() {
        return "\nhangman commands- \n__hangman start__- starts a new game if no game is active, with a random word\n__hangman info__- gives you info about the current game \n__hangman \\*letter\\*__ (for example \"hangman a\")  \n__hangmanis \\*word\\*__- to try and guess if \\*word\\* is the solution";
    }

    public static boolean getState() {
        return state;
    }

    public static void start(IChannel channel) {
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

            channel.sendMessage("Game started!\n" + hangman.info());
        } catch (Exception exc) {
            Dbot.logger.log(Level.WARNING, "error: " + exc);
        }
    }

    public static String info() {
        return ("\nChannel: " + channel.getName() + "\nWord: " + concealedWord + "\nLives: " + lives + "\nUsed Letters: " + hangman.getUsedLetters());
    }

    public static void endGame(IUser user, boolean win) {

        try {
            if (win) {
                channel.sendMessage(user.mention() + " is the winner!");

            } else {
                channel.sendMessage("Game Over \nThe word was: " + word);
            }

        } catch (Exception exc) {
            Dbot.logger.log(Level.WARNING, "error sending message");
        }

        state = false;
        word = "";
        concealed = null;
        letters = null;
        concealedWord = "";
    }

    public static void tryLetter(Character letter, IMessage message) {
        try {
            if (!(letters.add(letter))) {
                message.reply(letter + " is already used");
            } else if (word.indexOf(letter) == -1) {
                lives--;
                hangman.channel.sendMessage("The word does not contain " + letter + "\nLives: " + lives);
                if (lives == 0) {
                    hangman.endGame(message.getAuthor(), false);
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
                hangman.channel.sendMessage("The word contains " + letter + "\n" + hangman.info());
                if (word.equals(concealedWord)) {
                    hangman.endGame(message.getAuthor(), true);
                }
            }

        } catch (Exception exc) {
            Dbot.logger.log(Level.WARNING, "error: " + exc);
        }

    }

    public static boolean isWord(String tryword) {
        if (word.equals(tryword.toLowerCase())) {
            return true;
        }
        return false;
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
