package vava33.d2dplot.auxi;

import java.awt.Color;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.util.Locale;

import org.apache.commons.math3.util.FastMath;

public class PuntCercle implements Comparable<PuntCercle>{

    private static Color colorCercle = Color.red;
    private static Color colorPunt = Color.green;
    private static int midaPunt = 6;

    public static Color getColorCercle() {return colorCercle;}
    public static Color getColorPunt() {return colorPunt;}
    public static void setColorCercle(Color colorCercle) {PuntCercle.colorCercle = colorCercle;}
    public static void setColorPunt(Color colorPunt) {PuntCercle.colorPunt = colorPunt;}
    
//    private Ellipse2D.Float cercle;
    int intensity;
    private Ellipse2D.Float punt;
    private EllipsePars ellipse;
    double t2rad; // angle 2 theta en RADIANTS
    float px, py; // coordenades del pixel
    
    // tota la informacio junta
    public PuntCercle(Point2D.Float p, Pattern2D patt2D, float t2rad, int inten) {
        // p es el punt clicat ja passat a pixels, afegirem un "punt" (centrant
        // una esfera petita d'aresta Xpx)
        // la mida punt es pot fer interactiva mes endavant si es vol
        this.px = (float) p.getX();
        this.py = (float) p.getY();
        setPunt(new Ellipse2D.Float(px - midaPunt / 2, py - midaPunt / 2, midaPunt, midaPunt));
        setEllipse(ImgOps.getElliPars(patt2D, p));
        this.t2rad = t2rad;
        this.intensity = inten;
    }

//    public Ellipse2D.Float getCercle() {
//        return cercle;
//    }

    public Ellipse2D.Float getPunt() {
        return punt;
    }

    public float getX() {
        return px;
    }

    public float getY() {
        return py;
    }

    // recalcula el cercle (en cas que es canvii el centre)
    public void recalcular(float xCentre, float yCentre, double angle) {


    public void setPunt(Ellipse2D.Float punt) {
        this.punt = punt;
    }

    public void setX(float x) {
        this.px = x;
    }

    public void setY(float y) {
        this.py = y;
    }

    public double getT2rad() {
        return t2rad;
    }
    public int getIntensity() {
        return intensity;
    }
    public void setIntensity(int intensity) {
        this.intensity = intensity;
    }
    @Override
    public String toString() {
        String linia = String.format(Locale.ENGLISH, "%8.2f" + " " + "%8.2f" + " " + "%8.3f" + " " + "%6d", px, py,
                FastMath.toDegrees(this.t2rad), this.intensity);
        return linia;
    }
    @Override
    public int compareTo(PuntCercle o) {
        // compareTo should return < 0 if this is supposed to be
        // less than other, > 0 if this is supposed to be greater than 
        // other and 0 if they are supposed to be equal
        if (this.getT2rad()>=o.getT2rad()){
            return 1;
        }
        return -1;
    }
    

    public EllipsePars getEllipse() {
        return ellipse;
    }
    public void setEllipse(EllipsePars ellipse) {
        this.ellipse = ellipse;
    }
    
    
}
