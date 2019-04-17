/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package viridiengine;

import java.awt.Color;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Jonnelafin
 */
public class levelLoader {
    private int dotC = 0;
    private boolean inSentence = false;
    int x = 0;
    int y = 0;
    int mass = 1;
    String tag;
    String appereance;
    Color c;
    int id;
    String filePath = new File("").getAbsolutePath();
    public levelLoader(String file, objectManager oM){
        try {
            Scanner in = new Scanner(new FileReader(filePath.concat(file)));
            String text = "";
            while (in.hasNextLine()) {
                String line = in.nextLine();
                text = text + line;
            }
            in.close();
            fetch(text, oM);
            
//            fetch(in.toString(), oM);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(levelLoader.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public void fetch(String i, objectManager oM){
        String tmp = "";
        for(char x : i.toCharArray()){
            
            if(x == ':'){
                gameObject tm = new gameObject(this.x, this.y, this.tag, this.appereance, this.mass, this.c, this.id);
                oM.addObject(tm);
                System.out.println(tm.getTag());
                dotC = 0;
                tmp = "";
            }
            else if(x == '.'){
//                System.out.println(tmp);
                if(dotC == 0){
                    this.x = toInt(tmp);
                }
                if(dotC == 1){
                    this.y = toInt(tmp);
                }
                if(dotC == 2){
                    this.tag = tmp;
                }
                if(dotC == 3){
                    this.appereance = tmp;
                }
                if(dotC == 4){
                    this.mass = toInt(tmp);
                }
                if(dotC == 5){
                    this.c = getColorByName(tmp);
                }
                if(dotC == 6){
                    this.id = toInt(tmp);
                }
                dotC++;
                tmp = "";
            }
            else{
                tmp = tmp + x;
            }
            
            
        }
    }
    public int toInt(String som){
        String result = "";
        for (int i = 0; i < som.length(); i++) {
            Character character = som.charAt(i);
            if (Character.isDigit(character)) {
                result = result + character;
            }
        }
        return Integer.parseInt(result);
    }
    public static Color getColorByName(String name) {
    try {
        return (Color)Color.class.getField(name.toUpperCase()).get(null);
    } catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
        e.printStackTrace();
        return null;
    }
}
    
}
