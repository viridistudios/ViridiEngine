
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.PointBreakStudios.PointBreakEngine;

import java.awt.Color;
import java.net.URISyntaxException;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingUtilities;

/**
 *
 * @author Elias
 */
public class main {

    /**
     * @param args the command line arguments
     */
    static LinkedList<Object> list;
    static kick k;
    engine main;
    Editor editor;
    boolean running;
    public static void main(String[] args) {
        // TODO code application logic here
        k = new kick(3);
        
        
    }
    public void start(){
        running = true;
        main = k.wM;
        editor = k.ea;
        float c = 0F;
        k = new kick(0);
        while(!main.ready){
            System.out.println("initializing main... " + c);
            c++;
        }
        try {
            main.loadLevel("out.txt");
        } catch (URISyntaxException ex) {
            Logger.getLogger(main.class.getName()).log(Level.SEVERE, null, ex);
        }
        main.oM.addObject(new Player(5, 5, "player1", "█", 1F, Color.black, 1, main.k));
    }
    public void stop(){
        k.stop();
    }
    public void continu(){
        k.continu();
    }
}
