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
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IMessage;

/**
 *
 * @author Evyatar M
 */
public class Dbot extends JFrame {

    private static IDiscordClient client;
    private static Logger logger = Logger.getLogger("Junior's Log");
    private static botFrame frame;
    private static boolean loggedOut = false;

    public static void setLoggedOut(boolean set) {
        loggedOut = set;
    }

    public static boolean getLoggedOut() {
        return loggedOut;
    }

    public static void loadStartMessages() {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
        frame.loadStartMessages();
            }});
    }

    public static void main(String[] args) {
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
        logger.setLevel(Level.ALL);
        login(getToken());
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                frame = new botFrame(client);
                frame.setVisible(true);
            }
        });
    }

    private static String getToken() {
        String token = "";
        try {
            logger.setLevel(Level.ALL);
            System.getProperty("user.home");
            Scanner sc = new Scanner(new File(System.getProperty("user.home") + "/Documents/Dbot/Token.txt"));
            token = sc.nextLine();
            sc.close();
        } catch (Exception e) {// <editor-fold defaultstate="collapsed" desc="Stack trace frame">
            StringBuilder sb = new StringBuilder(e.toString());
            for (StackTraceElement ste : e.getStackTrace()) {
                sb.append("\n\tat ");
                sb.append(ste);
            }
            String trace = sb.toString();
            logger.log(Level.SEVERE, "Dbot Exception - getToken \n" + trace);
            // </editor-fold>}
        }
        return token;
    }

    private static void login(String token) {
        try {
            logger.info("Dbot - login");
            client = new ClientBuilder().withToken(token).login(false);
            client.getDispatcher().registerListener(new EventHandler());
            //client.getDispatcher().registerListener(new ReadyEventListener());
        } catch (Exception e) {
            // <editor-fold defaultstate="collapsed" desc="Stack trace frame">
            StringBuilder sb = new StringBuilder(e.toString());
            for (StackTraceElement ste : e.getStackTrace()) {
                sb.append("\n\tat ");
                sb.append(ste);
            }
            String trace = sb.toString();
            logger.log(Level.SEVERE, "Dbot Exception - login \n" + trace);
            // </editor-fold>

        }

    }

    public static void loadMessagesOnChannel(IChannel ch) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
        frame.loadMessages(ch);
    }});
    }

    public static void reLogin() {
        try {
            client.login(false);
            reloadMessages();
        } catch (Exception e) {// <editor-fold defaultstate="collapsed" desc="Stack trace frame">
            StringBuilder sb = new StringBuilder(e.toString());
            for (StackTraceElement ste : e.getStackTrace()) {
                sb.append("\n\tat ");
                sb.append(ste);
            }
            String trace = sb.toString();
            logger.log(Level.SEVERE, "Dbot Exception - reLogin \n" + trace);
            // </editor-fold>}
        }
    }

    public static void reloadMessages() {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
        frame.reloadMessages();
            }});
    }

    public static boolean checkInitializedFrame() {
        logger.log(Level.INFO, "Dbot checkInitializedFrame - handling");
        if (frame != null) {
            return true;
        }
        return false;
    }

    public static void addTextFrame(String channelID, IMessage msg) {
        logger.log(Level.INFO, "Dbot addTextFrame - handling");
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
        if (checkInitializedFrame()) {
            frame.addMessage(channelID, msg);
        }
            }});
    }

    public static void initiate() {

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                frame.initiateTabs();
            }

        });
    }

}
