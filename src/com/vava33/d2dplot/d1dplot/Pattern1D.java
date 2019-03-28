package com.vava33.d2dplot.d1dplot;

import java.awt.Color;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;

import com.vava33.d2dplot.D2Dplot_global;

public class Pattern1D {

    private File file;
    private double original_wavelength = -1f;
    private ArrayList<String> commentLines;
    private ArrayList<DataSerie> series; //usually one, maybe more (eg. PRF files) 
    private static int globalNseries = 0; //TOTAL EN TOTES LES CLASSES

    //prf exclusive
    private boolean isPrf = false;
    private static boolean plotwithbkg = false; //static perque afectara a totes les series!
    private static int hkloff = -10;//pixels
    private static int hklticksize = 6;
    private static boolean prfFullprofColors = false;

    //create empty pattern
    public Pattern1D() {
        this.commentLines = new ArrayList<String>();
        this.setSeries(new ArrayList<DataSerie>());
        this.setOriginal_wavelength(-1f);
    }


    public void addDataSerie(DataSerie ds) {

        globalNseries = globalNseries + 1;
        ds.setColor(getNextColor());
        this.getSeries().add(ds);
        ds.setPatt1D(this); //important que estigui aqui baix
    }

    public void removeDataSerie(DataSerie ds) {
        this.getSeries().remove(ds);
        globalNseries = globalNseries - 1;
    }

    public void removeDataSerie(int index) {
        this.getSeries().remove(index);
        globalNseries = globalNseries - 1;
    }

    public int getNseriesPattern() {
        return this.series.size();
    }

    public void removeAllSeries() {
        globalNseries = globalNseries - this.series.size();
        this.series.clear();
    }

    public static Color getNextColor() {
        //aqui segons el "TEMA" s'assignarà el color
        if (PlotPanel.isLightTheme()) {
            final int ncol = (globalNseries - 1) % D2Dplot_global.lightColors.length;
            return D2Dplot_global.parseColorName(D2Dplot_global.lightColors[ncol]);
        } else {
            final int ncol = (globalNseries - 1) % D2Dplot_global.darkColors.length;
            return D2Dplot_global.parseColorName(D2Dplot_global.darkColors[ncol]);
        }
    }

    public File getFile() {
        return this.file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public ArrayList<String> getCommentLines() {
        return this.commentLines;
    }

    public void setCommentLines(ArrayList<String> commentLines) {
        this.commentLines = commentLines;
    }

    public ArrayList<DataSerie> getSeries() {
        return this.series;
    }

    public DataSerie getSerie(int index) {
        return this.series.get(index);
    }

    public Iterator<DataSerie> getSeriesIterator() {
        return this.series.iterator();
    }

    public int getNseries() {
        return this.series.size();
    }

    public int indexOfSerie(DataSerie ds) {
        return this.series.indexOf(ds);
    }

    public void setSeries(ArrayList<DataSerie> series) {
        this.series = series;
    }

    public double getOriginal_wavelength() {
        return this.original_wavelength;
    }

    public boolean isPrf() {
        return this.isPrf;
    }

    public void setPrf(boolean isPrf) {
        this.isPrf = isPrf;
    }

    public void setOriginal_wavelength(double original_wavelength) {
        this.original_wavelength = original_wavelength;
    }

    public static int getHklticksize() {
        return hklticksize;
    }

    public static void setHklticksize(int hklticksize) {
        Pattern1D.hklticksize = hklticksize;
    }

    public static int getHkloff() {
        return hkloff;
    }

    public static void setHkloff(int hkloff) {
        Pattern1D.hkloff = hkloff;
    }

    public static boolean isPlotwithbkg() {
        return plotwithbkg;
    }

    public static void setPlotwithbkg(boolean plotwithbkg) {
        Pattern1D.plotwithbkg = plotwithbkg;
    }

    public static boolean isPrfFullprofColors() {
        return prfFullprofColors;
    }

    public static void setPrfFullprofColors(boolean prfFullprofColors) {
        Pattern1D.prfFullprofColors = prfFullprofColors;
    }
}
