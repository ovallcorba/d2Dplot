package com.vava33.d2dplot.auxi;

import java.awt.Color;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.util.Locale;

import org.apache.commons.math3.util.FastMath;

public class PuntClick implements Comparable<PuntClick> {

    private static Color colorCercle = Color.red;
    private static Color colorPunt = Color.green;
    private static int midaPunt = 6;

    public static Color getColorCercle() {
        return colorCercle;
    }

    public static Color getColorPunt() {
        return colorPunt;
    }

    public static void setColorCercle(Color colorCercle) {
        PuntClick.colorCercle = colorCercle;
    }

    public static void setColorPunt(Color colorPunt) {
        PuntClick.colorPunt = colorPunt;
    }

    public static int getMidaPunt() {
        return midaPunt;
    }

    public static void setMidaPunt(int midaPunt) {
        PuntClick.midaPunt = midaPunt;
    }

    int intensity;
    private Ellipse2D.Float punt;
    private EllipsePars ellipse;
    double t2rad; // angle 2 theta en RADIANTS
    float px, py; // coordenades del pixel
    private final Pattern2D whereIBelong;

    // tota la informacio junta
    public PuntClick(Point2D.Float p, Pattern2D patt2D, float t2rad, int inten) {
        // p es el punt clicat ja passat a pixels, afegirem un "punt" (centrant
        // una esfera petita d'aresta Xpx)
        // la mida punt es pot fer interactiva mes endavant si es vol
        this.px = (float) p.getX();
        this.py = (float) p.getY();
        this.setPunt(new Ellipse2D.Float(this.px - midaPunt / 2.f, this.py - midaPunt / 2.f, midaPunt, midaPunt));
        this.setEllipse(ImgOps.getElliPars(patt2D, p));
        //        System.out.println("puntClick");
        this.getEllipse().logElliPars("CONFIG");
        this.t2rad = t2rad;
        this.intensity = inten;
        this.whereIBelong = patt2D;
    }

    public Ellipse2D.Float getPunt() {
        return this.punt;
    }

    public float getX() {
        return this.px;
    }

    public float getY() {
        return this.py;
    }

    // recalcula el cercle (en cas que es canvii el centre)
    public void recalcularEllipse() {
        this.setEllipse(ImgOps.getElliPars(this.whereIBelong, new Point2D.Float(this.px, this.py)));
    }

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
        return this.t2rad;
    }

    public int getIntensity() {
        return this.intensity;
    }

    public void setIntensity(int intensity) {
        this.intensity = intensity;
    }

    @Override
    public String toString() {
        final String linia = String.format(Locale.ENGLISH, "%8.2f" + " " + "%8.2f" + " " + "%8.3f" + " " + "%6d",
                this.px, this.py, FastMath.toDegrees(this.t2rad), this.intensity);
        return linia;
    }

    @Override
    public int compareTo(PuntClick o) {
        // compareTo should return < 0 if this is supposed to be
        // less than other, > 0 if this is supposed to be greater than
        // other and 0 if they are supposed to be equal
        if (this.getT2rad() >= o.getT2rad()) {
            return 1;
        }
        return -1;
    }

    public EllipsePars getEllipse() {
        return this.ellipse;
    }

    public void setEllipse(EllipsePars ellipse) {
        this.ellipse = ellipse;
    }
}
