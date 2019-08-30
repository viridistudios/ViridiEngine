/*
 * The MIT License
 *
 * Copyright 2019 Elias.
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

package PBEngine;

import java.awt.Color;
import java.util.LinkedList;

/**
 *
 * @author Jonnelafin
 */
public class VSRadManager{
    public kick masterParent;
    public int blurStrenght = 0; //Disabled by default
    public Color[][] colors;
    public LinkedList<VSRad> VSRad = new LinkedList<>();
    private VSRad director;
    private int w, h;
    private objectManager oM;
    private radiosity demo;
    private dVector[] directions;
    private float[][] last;
    public VSRadManager(int w, int h,objectManager oM, kick k){
        this.masterParent = k;
        this.w = w;
        this.h = h;
        this.oM = oM;
        last = new float[w][h];
        //director = new VSRad(this.oM, Color.BLACK, 1999);
        //director.init(this.w, this.h, 1, id);
        id++;
        //this.directions = director.directions;
        this.colors = new Color[w][h];
        quickEffects.zero(colors);
        for(Color [] lane : colors){
            for (Color c : lane){
                c = Color.black;
            }
        }
    }
    int id = 0;
    public void add(int x, int y, float s, Color color, int type, boolean recalculate){
        VSRad tmp = new VSRad(oM, color, type);        
        tmp.id = id;
        System.out.println("ADDING A RAY");
        System.out.println("RAY ID "+id);
        tmp.init(w, h, 0, id);
        //tmp.directions = directions;
        tmp.calculate(new dVector(x,y), s, "null");
        System.out.println(tmp.sum);
        this.VSRad.add(tmp);
        if(recalculate){masterParent.Logic.red = this.read(999999);}
        id++;
    }
    public void recalculateParent(){
        
        masterParent.Logic.red = this.read(999999);
    }
    int lasthash = 0;
    public float[][] read(int type){
        float[][] sum = new float[w][h];
        int xp = 0, yp = 0;
        LinkedList<VSRad> list = VSRad;
        for(VSRad ray : list){
            if(ray.type != type){
                for(float[] line : ray.grid){
                    for(float i : line){
                        if(i > 0){
                            sum[xp][yp] = sum[xp][yp] + i;
                            //System.out.println(colors[xp][yp] + " " + ray.color.getRGB());
                            float r,g,b;
                            try{r = colors[xp][yp].getRed() + (ray.color.getRed() * ray.grid[xp][yp]);}catch(Exception e){/*r = ray.color.getRed() * ray.grid[xp][yp];*/ r=0;}
                            try{g = colors[xp][yp].getGreen()+ (ray.color.getGreen() * ray.grid[xp][yp]);}catch(Exception e){/*g = ray.color.getGreen() * ray.grid[xp][yp];*/g=0;}
                            try{b = colors[xp][yp].getBlue()+ (ray.color.getBlue() * ray.grid[xp][yp]);}catch(Exception e){/*b = ray.color.getBlue() * ray.grid[xp][yp];*/b=0;}
                            //r = r / 2;g = g / 2;b = b / 2;
                            if(r > 255){r = 255;}
                            if(g > 255){g = 255;}
                            if(b > 255){b = 255;}
                            colors[xp][yp] = new Color((int)r,(int)g,(int)b);
                        }
                        yp++;
                    }
                    xp++;
                    yp = 0;
                }
            }
            xp = 0;
        }
        if(sum.hashCode() == lasthash){
            System.out.println("no change");
        }
        lasthash = sum.hashCode();
        last = sum;
        return(sum);
    }
    public Color readColor(int x, int y){
        
        //x = x -1;
        //y = y -1;
        //if(x > 49){x = 49;}
        //if(x < 0){x = 0;}
        //if(y > 49){y = 49;}
        //if(y < 0){y = 0;}
        Color sum = new Color(0,0,0);
        int r = 0, g = 0, b = 0;
        for(VSRad ray : VSRad){
            r = Math.round(sum.getRed() + (ray.grid[x][y] * ray.color.getRed()));if(r > 255){r = 255;}
            g = Math.round(sum.getGreen() + (ray.grid[x][y] * ray.color.getGreen()));if(g > 255){g = 255;}
            b = Math.round(sum.getBlue() + (ray.grid[x][y] * ray.color.getBlue()));if(b > 255){b = 255;}
            sum = new Color(r,g,b);
        }
        //sum = getBlurred()[x][y];
        return(sum);
    }
    public void removeA(){
        for(int i : new Range(VSRad.size())){
            VSRad.remove(0);
        }
    }
    public Color[][] getBlurred(){
        Color[][] out = new Color[w][h];
        out = quickEffects.zero(out);
        float[][] r = getR(w,h);
        float[][] g = getG(w,h);
        float[][] b = getB(w,h);
        LinkedList<float[][]> rgb = new LinkedList<>();
        r = new quickEffects().blur(r, w, h, blurStrenght);
        g = new quickEffects().blur(g, w, h, blurStrenght);
        b = new quickEffects().blur(b, w, h, blurStrenght);
        rgb.add(r);rgb.add(g);rgb.add(b);
        int xp = 0, yp = 0;
        for(Color[] lane : out){
            for(Color i : lane){
                int ri = (int) r[xp][yp];
                int gi = (int) g[xp][yp];
                int bi = (int) b[xp][yp];
                i = new Color(ri,gi,bi);
                yp++;
            }
            xp++;
            yp = 0;
        }
        return out;
    }
    public void recalculate(String ignore, int type){
        for(VSRad i :VSRad){
            if(i.type == type){
                continue;
            }
            i.fill(0);
            this.colors = new Color[w][h];
            i.calculate(i.from, i.lastS, ignore);
        }
        //float[][] r = quickEffects.blur(quickEffects.separateRGB(colors, w, h).get(0), w, h, 3);
        //float[][] g = quickEffects.blur(quickEffects.separateRGB(colors, w, h).get(1), w, h, 3);
        //float[][] b = quickEffects.blur(quickEffects.separateRGB(colors, w, h).get(2), w, h, 3);
        //this.colors = quickEffects.parseColor(w, h, r, g, b);
    }
    public float[][] getR(int xd, int yd){
        float[][] out = new float[xd][yd];
        for(int x : new Range(xd)){
            for(int y : new Range(yd)){
                out[x][y] = 0;
                try{out[x][y] = colors[x][y].getRed();}catch(Exception e){out[x][y] = 0;}
            }
        }
        return out;
    }
    public float[][] getG(int xd, int yd){
        float[][] out = new float[xd][yd];
        for(int x : new Range(xd)){
            for(int y : new Range(yd)){
                out[x][y] = 0;
                try{out[x][y] = colors[x][y].getGreen();}catch(Exception e){out[x][y] = 0;}
            }
        }
        return out;
    }
    public float[][] getB(int xd, int yd){
        float[][] out = new float[xd][yd];
        for(int x : new Range(xd)){
            for(int y : new Range(yd)){
                out[x][y] = 0;
                try{out[x][y] = colors[x][y].getBlue();}catch(Exception e){out[x][y] = 0;}
            }
        }
        return out;
    }
}