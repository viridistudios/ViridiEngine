/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package PBEngine;

import java.awt.Color;
import java.net.URISyntaxException;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingUtilities;

/**
 *
 * @author Jonnelafin
 */
public class kick {
    dVector engine_gravity = new dVector(0D, 0.1D);
    Engine wM;
    Editor ea;
    //radiosity rad;
    VSRadManager rad;
    boolean tog;
    kick ref = this;
    int xd = 50;
    int yd = 25;
        EffectsDemo ED;
    Thread c;
    Thread b;
    Thread a;
    objectManager forwM = new objectManager();
    public kick(int mode){
        devkit kit = new devkit(ref);
        //ED = new EffectsDemo(ref , forwM, 50, 25, rad, engine_gravity);
        b  = new Thread(){
            @Override
            public void run(){
                ea = new Editor(ref);
                SwingUtilities.invokeLater(ea);
                ea.setVisible(false);
            }
        };
        System.out.println("Starting PointBreakEngine on " + System.getProperty("os.name") + "...");
        System.out.println("initializing");
        System.out.println("////////////////");
        b.start();
        a = new Thread(){
                @Override
                public void run(){
        wM = new Engine(ref , forwM, xd, yd, rad, engine_gravity);
        
        
        //rad = new radiosity(ref);
        //SwingUtilities.invokeLater(ED);
        SwingUtilities.invokeLater(wM);
           //Thread a = new Thread(wM, "Thread 1");
           //Thread b = new Thread(ea, "Thread 2");
           //a.start();
           //b.start();
        
        //rad.running = true;
        wM.running = true;
        if(mode == 3){
            try {
                wM.loadLevel("out.txt");
            } catch (URISyntaxException ex) {
                Logger.getLogger(kick.class.getName()).log(Level.SEVERE, null, ex);
            }
            //wM.oM.addObject(new Player(5, 5, "player1", "█", 1F, Color.black, 1, ref));
            gameObject p = new Player(25, 5, "player1", "█", 1F, Color.black, 1, ref);
            int id = 2;
            /*
            for(int x : new Range(10)){
                for(int y : new Range(15)){
                    Player p1 = new Player(x+2, y+2, "player1", "█", 1F, Color.black, id, ref);
                    p1.setParent(p);
                    wM.oM.addObject(p1);
                    id++;
                }
            }
            */
            
            wM.oM.addObject(p);
            //wM.oM.addObject(new Player(5, 6, "player1", "█", 1F, Color.black, 3, ref));
            //wM.oM.addObject(new Player(6, 6, "player1", "█", 1F, Color.black, 4, ref));
            //rad.setTitle("VSRad");
            wM.rads.add(1, 1, 2, new Color(1, 1, 1), 1);
            wM.rads.add(49, 1, 2, new Color(1, 1, 1), 1);
            //wM.rads.add(25, 1, 1, new Color(1, 1, 1), 1);
        }
        //wM.running = true;
                }
        };
        a.start();
        //System.out.println("////////////////");
        //System.out.println("done initializing");
    }
    public void tog(){
        if(tog){
            wM.record();
        }
        wM.setVisible(tog);
        wM.running = tog;
//        rad.setVisible(tog);
//        rad.running = tog;
        ea.setVisible(!tog);
        ea.running = !tog;
        
        tog = !tog;
    }
    public void stop(){
        wM.running = false;
        ea.running = false;
    }
    public void continu(){
        wM.running = tog;
        ea.running = !tog;
    }
}