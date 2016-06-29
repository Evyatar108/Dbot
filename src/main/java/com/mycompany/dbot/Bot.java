/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.dbot;

import java.io.File;
import java.util.Scanner;
import java.util.logging.Logger;
import sx.blah.discord.api.ClientBuilder;
import sx.blah.discord.api.IDiscordClient;

/**
 *
 * @author Evyatar M
 */
public class Bot {

    static Scanner sc;
    public static IDiscordClient client;
    static String token;
    static Logger logger = Logger.getLogger("Junior's Log");

    public static void main(String[] args) {
        try {

            sc = new Scanner(new File("C:\\Bot\\Resources\\Token.txt"));
            token = sc.nextLine();
            System.out.println("token is: " + token);
            client = new ClientBuilder().withToken(token).login();
            client.getDispatcher().registerListener(new EventHandler());
            client.getDispatcher().registerListener(new ReadyEventListener());

        } catch (Exception exc) {
            System.out.println(exc);
        }

    }

}
