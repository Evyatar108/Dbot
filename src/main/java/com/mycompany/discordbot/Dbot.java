/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.discordbot;

import java.io.File;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import sx.blah.discord.api.ClientBuilder;
import sx.blah.discord.api.IDiscordClient;

/**
 *
 * @author Evyatar M
 */
public class Dbot extends JFrame {

    static IDiscordClient client;
    static String token;
    static Logger logger = Logger.getLogger("Junior's Log");
    static botFrame frame;

    public static void main(String[] args) {
        login();

    }

    public static IDiscordClient login() {
        try {
            logger.setLevel(Level.ALL);
            System.getProperty("user.home");
            Scanner sc = new Scanner(new File(System.getProperty("user.home")+"/Documents/Dbot/Token.txt"));
            token = sc.nextLine();
            sc.close();
            client = new ClientBuilder().withToken(token).login();
            client.getDispatcher().registerListener(new EventHandler());
            //       client.getDispatcher().registerListener(new ReadyEventListener());
        } catch (Exception exc) {
            logger.log(Level.SEVERE, "Dbot Exception - login \n" + exc);
        }
        return client;

    }

    public static void setFrameClient() {
        logger.log(Level.INFO, "Dbot setFrameClient - handling");
        run();
    }

    public static boolean checkInitializedFrame() {
        logger.log(Level.INFO, "Dbot checkInitializedFrame - handling");
        if (frame != null) {
            return true;
        }
        return false;
    }

    public static void addTextFrame(String channelID, String message) {
        logger.log(Level.INFO, "Dbot addTextFrame - handling");
        if ((checkInitializedFrame())&&(frame.initializedTabs())) {
            frame.addMessages(channelID, message);
        }

    }

    public static void run() {

        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;

                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(botFrame.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);

        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(botFrame.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);

        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(botFrame.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);

        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(botFrame.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                frame = new botFrame(client);
                frame.setVisible(true);
                // frame.initiateTabs();
            }

        });
    }

}
