
import com.mycompany.dbot.Bot;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Random;
import java.util.Scanner;
import java.util.logging.Level;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Evyatar M
 */
public class NewClass {
    public static void main(String[] args){
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
            System.out.println(result);
           
        } catch (FileNotFoundException exc) {
         
        }
    }
}
    
