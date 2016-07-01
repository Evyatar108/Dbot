/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.discordbot;

import static com.mycompany.discordbot.Dbot.logger;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

/**
 *
 * @author Evyatar M
 */
public class bank {

    static Map<String, Integer> bank;

    public static void addMoney(String ID, Integer sum) {
        logger.log(Level.INFO, "Dbot add money ");
        if (bank == null) {
            loadBank();
        }
        if (bank.containsKey(ID)) {
            bank.replace(ID, bank.get(ID) + sum);

        } else {
            bank.put(ID, sum);
        }
        saveLB();
    }

    public static boolean takeMoney(String ID, Integer sum) {
        logger.log(Level.INFO, "Dbot take money ");
        if (bank == null) {
            loadBank();
        }

        if (bank.containsKey(ID)) {
            if ((bank.get(ID) - sum) > 0) {
                bank.replace(ID, bank.get(ID) - sum);
                saveLB();
                return true;
            } else {
                return false;
            }

        } else {
            bank.put(ID, 0);
            saveLB();
            return false;
        }
    }

    public static Integer getMoney(String ID) {
        logger.log(Level.INFO, "Dbot take money ");
        if (bank == null) {
            loadBank();
        }

        if (bank.containsKey(ID)) {
            return bank.get(ID);
        } else {
            bank.put(ID, 0);
            saveLB();
            return 0;
        }
    }

    private static void saveLB() {
        logger.log(Level.INFO, "Dbot saveLB - handling");
        File file = new File("C:\\Bot\\Resources\\hangmanLeaderboard.txt");
        try {
            if (!file.exists()) {
                logger.log(Level.INFO, "Dbot  saveLB - file doesnt exist");
                file.createNewFile();
                logger.log(Level.INFO, "Dbot saveLB - file created");
            }
            logger.log(Level.INFO, "Dbot saveLB - saving to file");
            FileOutputStream saveFile = new FileOutputStream("C:\\Bot\\Resources\\hangmanLeaderboard.txt");
            ObjectOutputStream save = new ObjectOutputStream(saveFile);
            save.writeObject(bank);
            save.close();
            saveFile.close();
            logger.log(Level.INFO, "Dbot saveLB - saved to file");
        } catch (Exception exc) {
            logger.log(Level.WARNING, "Dbot Exception - saveLB " + exc);
        }
    }

    public static void loadBank() {
        logger.log(Level.INFO, "Dbot loadLB - handling");
        File file = new File("C:\\Bot\\Resources\\hangmanLeaderboard.txt");
        try {
            if (file.exists()) {
                logger.log(Level.INFO, "Dbot loadLB - loading file");
                FileInputStream saveFile = new FileInputStream("C:\\Bot\\Resources\\hangmanLeaderboard.txt");
                ObjectInputStream save = new ObjectInputStream(saveFile);
                bank = (HashMap) save.readObject();
                save.close();
                saveFile.close();
                logger.log(Level.INFO, "Dbot loadLB - loaded file");
            } else {
                logger.log(Level.INFO, "Dbot loadLB - file doesnt exist");
                bank = new HashMap<String, Integer>();
                saveLB();
            }
        } catch (Exception exc) {
            logger.log(Level.WARNING, "Dbot Exception - loadLB " + exc);
        }
    }

}
