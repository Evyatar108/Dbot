package com.mycompany.discordbot;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Random;
import java.util.Scanner;
import java.util.logging.Level;

/**
 *
 * @author Evyatar
 */
public class Resources {

    static String help() {
        return "\n**`!hm`**\n    the hangman game \n**`!ttt`**\n    TicTacToe game \n**`!quote`**\n    some insight \n**`!sa`**\n    Information about sa and cbt\n**`!image`** `@mention`\n    full profile image of the mentioned user";//Each command has a 10min Cooldown to prevent cursoriness and spamming

    }

    public static String quotes() {; //motivation sentence
        return getRandomLine("C:\\Bot\\Resources\\Quotes.txt");

    }

    public static String love(String name) {; //motivation sentence
        String line = getRandomLine("C:\\Bot\\Resources\\love.txt");
        line = line.replaceAll("[@]", name);
        return line;

    }

    public static String sa() {; //facts about sa symptoms,causes,biological causes, mental causes
        return getRandomLine("C:\\Bot\\Resources\\SA.txt");
    }

    public static String depression() {
        return getRandomLine("C:\\Bot\\Resources\\Depression.txt");
    }

    private static String getRandomLine(String fileLocation) {
        try {
            File f = new File(fileLocation);
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
            Dbot.logger.log(Level.WARNING, "problem with resource file: " + fileLocation + " error: " + exc);
            return ("Error?");
        }
    }

}
