/*
 * The MIT License
 *
 * Copyright 2019 Elias Eskelinen.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package PBEngine.Editor2;

import PBEngine.gameObjects.gameObject;
import JFUtils.point.Point2D;
import PBEngine.*;
import PBEngine.Rendering.Renderer;
import PBEngine.Rendering.extra.ImagePanel;
import filedrop.FileDrop;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.PrintWriter;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.border.BevelBorder;

/**
 *
 * @author Elias Eskelinen (Jonnelafin)
 */
public class Editor extends JFUtils.InputActivated{
    public String lastFile = null;
    
    public JPanel propertiesPanel;
    public boolean bake = false;
    public boolean saved = false;
    public PBEngine.Supervisor k;
    public int mode = 0;
    JComboBox select;
    JRadioButton calcLights;
    
    @Override
    public void tog(){
        System.out.println("TOG!");
    }
    
    @SuppressWarnings("unchecked")
    public Editor(){
        //Input ourInput = new Input(this);
        String[] argss = new String[2];
        argss[0] = "nodemo";
        Camera cam = new Camera(0, 0, null);
        k = new Supervisor(0, false, new Point2D(0, 0), 0);
        //k.customInput = ourInput;
        k.timerType = 1;
        
        k.disableVSRAD = true;
        k.features_confFile = "editorConfig.txt";
        
        System.out.println("Params set, starting PBEngine and waiting for the initialization to finish...");
        Thread A = new Thread(k);
        A.start();
        while(!k.ready){
            
            try {
                Thread.sleep(10);
            } catch (InterruptedException ex) {
                Logger.getLogger(Editor.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
            }
        }
        while(!k.Logic.ready){
            try {
                Thread.sleep(10);
            } catch (InterruptedException ex) {
                Logger.getLogger(Editor.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
            }
        }
        
        //Move enginewindows to one, unified one
        k.Logic.window.setVisible(false);
        Renderer v = k.Logic.Vrenderer;
        //k.Logic.window.remove(k.Logic.window.Vrenderer);
        
        Component devcont = k.kit.cont;
        k.kit.remove(devcont);
        k.kit.setVisible(false);
        
        k.grapher.setVisible(false);
        Component grapher = k.grapher.area;
        k.grapher.remove(grapher);
        
        EditorWindow w = new EditorWindow();
        
        JPanel editorPane = new JPanel(new FlowLayout());
        //k.Logic.window.overrideSize = editorPane.getSize();
        k.Logic.w = editorPane.getWidth();
        k.Logic.h = editorPane.getHeight();
        k.Logic.window.w = editorPane.getWidth();
        k.Logic.window.h = editorPane.getHeight();
        
        editorPane.add(k.Logic.window.content);
        editorPane.requestFocusInWindow();
        editorPane.addKeyListener(k.Logic.window.input);
        editorPane.addMouseMotionListener(k.Logic.window.input);
        
        w.container.add(editorPane, BorderLayout.CENTER);
        w.container.add(((devkit)k.kit).cont, BorderLayout.LINE_END);
        JPanel grapherPanel = new JPanel();
        grapherPanel.setBackground(Color.black);
        grapherPanel.add(k.grapher.area);
        w.container.add(grapherPanel, BorderLayout.PAGE_END);
        
        
        propertiesPanel = new JPanel(new BorderLayout());
        
        k.Logic.window.setTitle("PointBreakEngine (Editor2)");
        k.Logic.Vrenderer.factor = 20;
        //k.Logic.input = ourInput;
        
        JPanel editorPanel = new JPanel();
        JPanel editorPanel2 = new JPanel();
        JPanel container = new JPanel();
        container.setLayout(new BorderLayout(45, 0));
        JPanel drop = new JPanel();
        
        JLabel info = new JLabel("\n\nDrag-and-Drop a levelfile (.pblevel) here to load it\n\n");
        drop.add(info);
        drop.setBackground(Color.BLACK);
        drop.setForeground(Color.WHITE);
        info.setForeground(Color.WHITE);
        drop.setBorder(new BevelBorder(1));
        
        new FileDrop( drop, new FileDrop.Listener()
        {   public void  filesDropped( java.io.File[] files )
            {   
                // handle file drop
                load(files[0]);
            }   // end filesDropped
        }); // end FileDrop.Listener
        
        JButton saveB = new JButton("Save");saveB.addActionListener(new BListener(1, this));
        JButton loadB = new JButton("Load");loadB.addActionListener(new BListener(2, this));
        JButton testB = new JButton("Run");testB.addActionListener(new BListener(3, this));
        calcLights = new JRadioButton("calcLights", false);calcLights.addActionListener(new BListener(5, this));
        String[] options = {"Wall","Light","?"};
        select = new JComboBox(options);select.addActionListener(new BListener(4, this));
        select.setEditable(true);
        
        editorPanel.add(saveB);
        editorPanel.add(loadB);
        editorPanel.add(testB);
        editorPanel.add(calcLights);
        propertiesPanel.add(select, BorderLayout.PAGE_START);
        
        container.add(editorPanel, BorderLayout.NORTH);
        container.add(editorPanel2, BorderLayout.CENTER);
        container.add(drop, BorderLayout.SOUTH);
        w.container.add(container, BorderLayout.PAGE_START);
        w.container.add(propertiesPanel, BorderLayout.LINE_START);
        
        k.Logic.abright = true;
        k.Logic.Vrenderer.camMode = 0;
        k.Logic.Vrenderer.drawGrid = true;
        k.Logic.Vrenderer.gridColor = new Color(0, 90, 20);
        k.Logic.overrideRayBG = Color.GRAY;
        
        /*Input cursorInput = k.Logic.input;
        //new Input(k);
        Input kbr = (Input) k.Logic.getKeyListeners()[0];
        //k.Logic.addKeyListener(kbr);*/
        
        cursor = new Cursor(0, 0, 1, "newcursor", "C", 1, Color.white, 0, k, this, k.Logic.input);
        k.Logic.input.verbodose = true;
        //quickTools.alert(k.Logic.input.toString());
        //quickTools.alert(ourInput.toString());
        cursor.onlyColor = true;
        cursor.imageName = "";
        k.objectManager.addObject(cursor);
        System.out.println("Editor initialization finished");
        System.out.println(k.Logic.running);
    }
    Cursor cursor;
    @SuppressWarnings("unchecked")
    public void runLevel(){
        HashMap params = new HashMap();
        String location = save();
        params.put("loadLevel", location);
        params.put("noplayer", "");
        int lightC = k.Logic.oM.getFlattenedObjects().size();
        System.out.println( lightC + " lights present in the scene.");
        if(lightC == 0 || lightC < 0){
            params.put("nodefaultlight", "");
        }
        Supervisor supervisor = new Supervisor(3, !bake, new Point2D(0, 0), 0, params);
        Thread A = new Thread(supervisor);
        //SwingUtilities.invokeLater(A);
        A.start();
        k.Logic.targetSpeed = 60;
        while(!supervisor.ready){
            try {
                Thread.sleep(100);
            } catch (InterruptedException ex) {
                Logger.getLogger(Editor.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
            }
        }
        //quickTools.alert("Engine supervisor ready!");
        
        //quickTools.alert("Have a good day!");
        k.Logic.targetSpeed = 500;
    }
    public String save(){
            String out = "";
            System.out.println("Save...");
            JFileChooser jfc = new JFileChooser(lastFile);
            int returnValue = jfc.showSaveDialog(null);
            if (returnValue == JFileChooser.APPROVE_OPTION) {
			File selectedFile = jfc.getSelectedFile();
			System.out.println(selectedFile.getAbsolutePath());
                try {
                    PrintWriter writer = new PrintWriter(selectedFile.getAbsolutePath(), "UTF-8");
                    writer.print("");
                    writer.close();
                    new LevelLoader("null", k.objectManager, k).write(k.objectManager.getFlattenedObjects(), selectedFile.getAbsolutePath(), "");
                } catch (Exception ex) {
                    Logger.getLogger(BListener.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
                }
                out = selectedFile.getAbsolutePath();
            }
        lastFile = out;
        return out;
    }
    public void load(File file){
        try {
            LinkedList<gameObject> old = k.Logic.loadLevel_lightsAsObjects(file.getAbsolutePath(), "", Color.BLUE, Color.GREEN);
            lastFile = file.getAbsolutePath();
        } catch (URISyntaxException ex) {
            Logger.getLogger(Editor.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
    }
    public String load(){
        String out = "";
        JFileChooser jfc = new JFileChooser(lastFile);
            int returnValue = jfc.showOpenDialog(null);
            if (returnValue == JFileChooser.APPROVE_OPTION) {
			File selectedFile = jfc.getSelectedFile();
                        out = selectedFile.getAbsolutePath();
			System.out.println(selectedFile.getAbsolutePath());
                try {
                    LinkedList<gameObject> old = k.Logic.loadLevel_lightsAsObjects(selectedFile.getAbsolutePath(), "", Color.BLUE, Color.GREEN);
                    
                } catch (URISyntaxException ex) {
                    Logger.getLogger(BListener.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
                }
            }
        lastFile = out;
        return out;
    }
}
class BListener implements ActionListener{
    boolean abright = false;
    int type;
    PBEngine.Editor2.Editor editor;
    public BListener(int t, PBEngine.Editor2.Editor d){
        this.type = t;
        this.editor = d;
    }
    @Override
    public void actionPerformed(ActionEvent ae) {
        if(type == 1){
            System.out.println("Save...");
            editor.save();
        }
        if(type == 2){
            System.out.println("Load");
            editor.load();
        }
        if(type == 3){
            editor.runLevel();
        }
        if(type == 4){
            int action = editor.select.getSelectedIndex();
            System.out.println(action);
            editor.mode = action;
            /*if(action.equals("Wall")){
            editor.mode = 0;
            }
            if(action.equals("Light")){
            editor.mode = 1;
            }*/
            
        }
        if(type == 5){
            editor.bake = editor.calcLights.isSelected();
        }
    }
    
}
