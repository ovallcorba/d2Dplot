package com.vava33.d2dplot.tts;

import java.io.File;
import java.util.Scanner;

import com.vava33.d2dplot.D2Dplot_global;
import com.vava33.jutils.VavaLogger;

public class TSDfile {
    
    private String pathToFile;
    
    private String title;
    private float a,b,c,alfa,beta,gamma;
    private char lattice;
    private int laue;
    private boolean centro;
    private String[] symmetryMatt;
    //TODO Contents
    
    //CONTROL:
    private float swing, gruix, dsfou, alon, alat, aspin;
    private int multdom, ioff, nsol;
    
    private int nfiles;
    private int[] fnum;
    private float[] fnumAngOff;
    
    private boolean successfulRead = false;
    
    private static VavaLogger log = D2Dplot_global.getVavaLogger(TSDfile.class.getName());

    //creates a TSD file via reading it
    public TSDfile(String pathToFile){
        this.pathToFile=pathToFile;
        this.multdom=0;
        this.alon=0;
        this.alat=0;
        this.aspin=0;
        this.ioff=2;
        this.nsol=10;
        this.swing=7.5f;
        this.successfulRead = this.readTSD();
    }
    
    private boolean readTSD(){
        File f = new File(pathToFile);
        if (!f.exists())return false;
        boolean finished = false;
        String line;
        try {
            Scanner scTSDfile = new Scanner(f);
            //1a linia titol
            title = scTSDfile.nextLine();
            while (scTSDfile.hasNextLine()){
                if (finished)break;
                line = scTSDfile.nextLine();
                log.debug(line);
                
                if (line.contains("CELL")){
                    String[] spars = scTSDfile.nextLine().trim().split("\\s+");
                    try{
                        a = Float.parseFloat(spars[0]);
                        b = Float.parseFloat(spars[1]);
                        c = Float.parseFloat(spars[2]);
                        alfa = Float.parseFloat(spars[3]);
                        beta = Float.parseFloat(spars[4]);
                        gamma = Float.parseFloat(spars[5]);
                    }catch(Exception e){
                        e.printStackTrace();
                        log.debug("error reading cell parameters");
                    }
                    continue;
                }
                
                if (line.contains("LATTICE")){
                    String latt = scTSDfile.nextLine();
                    if (latt.trim().endsWith("-")){
                        this.centro=true;
                    }else{
                        this.centro=false;
                    }
                    lattice = latt.charAt(0);
                    log.debug("lattice="+lattice);
                    continue;
                }
                
                if (line.contains("LAUE")){
                    try{
                        laue = Integer.parseInt(scTSDfile.nextLine().trim());    
                    }catch(Exception e){
                        e.printStackTrace();
                        log.debug("error reading laue");
                    }
                    continue;
                }
                
                if (line.contains("SYMMETRY")){
                    //TODO
                    continue;
                }
                
                if (line.contains("CONTENTS")){
                    //TODO
                    continue;
                }
                
                if (line.contains("CONTROL")){
                    StringBuilder sbcontrol = new StringBuilder();
                    boolean endcontrol = false;
                    while (!endcontrol){
                        String line2 = scTSDfile.nextLine();
                        sbcontrol = sbcontrol.append(line2.trim());
                        if (line2.contains("/")) endcontrol=true;
                    }
//                    String control = scTSDfile.next("/");
                    String control = sbcontrol.substring(0, sbcontrol.length()-1);
                    log.debug("control="+control);
                    String[] items = control.trim().split(",");
                    for (int i=0;i<items.length;i++){
                        log.debug("items["+i+"]="+items[i]);
                        String[] item2 = items[i].split("=");
                        try{
                            if (item2[0].equalsIgnoreCase("SWING"))this.swing=Float.parseFloat(item2[1]);
                            if (item2[0].equalsIgnoreCase("GRUIX"))this.gruix=Float.parseFloat(item2[1]);
                            if (item2[0].equalsIgnoreCase("DSFOU"))this.dsfou=Float.parseFloat(item2[1]);
                            if (item2[0].equalsIgnoreCase("MULTDOM"))this.multdom=Integer.parseInt(item2[1]);
                            if (item2[0].equalsIgnoreCase("IOFF"))this.ioff=Integer.parseInt(item2[1]);
                            if (item2[0].equalsIgnoreCase("NSOL"))this.nsol=Integer.parseInt(item2[1]);
                            if (item2[0].equalsIgnoreCase("ALON"))this.alon=Float.parseFloat(item2[1]);
                            if (item2[0].equalsIgnoreCase("ALAT"))this.alat=Float.parseFloat(item2[1]);
                            if (item2[0].equalsIgnoreCase("SPIN"))this.aspin=Float.parseFloat(item2[1]);
                        }catch(Exception e){
                            e.printStackTrace();
                            log.debug("error getting item "+item2[0]);
                        }
                        continue;
                    }
                    continue;
                }
                
                if (line.contains("MODEL")){
                    //TODO
                    continue;
                }
                
                if ((line.contains("PCS"))||(line.contains("HKL"))){
                    this.nfiles = Integer.parseInt(scTSDfile.nextLine().trim());
                    if (this.multdom!=1) {
                        this.fnum = new int[nfiles];
                        this.fnumAngOff = new float[nfiles];
                        for (int i=0; i<this.nfiles; i++){
//                            String[] items = scTSDfile.nextLine().trim().split("\\s+");
                            String[] items = scTSDfile.nextLine().trim().split(",");
                            this.fnum[i]=Integer.parseInt(items[0]);
                            this.fnumAngOff[i]=Float.parseFloat(items[1]);
                        }
                    }
                    finished = true;
                    continue;
                }
            }
            scTSDfile.close();
        }catch(Exception e){
            e.printStackTrace();
            log.debug("error reading TSD file");
            return false;
        }
        return true;
    }

    public String getPathToFile() {
        return pathToFile;
    }

    public void setPathToFile(String pathToFile) {
        this.pathToFile = pathToFile;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public float getA() {
        return a;
    }

    public void setA(float a) {
        this.a = a;
    }

    public float getB() {
        return b;
    }

    public void setB(float b) {
        this.b = b;
    }

    public float getC() {
        return c;
    }

    public void setC(float c) {
        this.c = c;
    }

    public float getAlfa() {
        return alfa;
    }

    public void setAlfa(float alfa) {
        this.alfa = alfa;
    }

    public float getBeta() {
        return beta;
    }

    public void setBeta(float beta) {
        this.beta = beta;
    }

    public float getGamma() {
        return gamma;
    }

    public void setGamma(float gamma) {
        this.gamma = gamma;
    }

    public char getLattice() {
        return lattice;
    }

    public void setLattice(char lattice) {
        this.lattice = lattice;
    }

    public int getLaue() {
        return laue;
    }

    public void setLaue(int laue) {
        this.laue = laue;
    }

    public boolean isCentro() {
        return centro;
    }

    public void setCentro(boolean centro) {
        this.centro = centro;
    }

    public String[] getSymmetryMatt() {
        return symmetryMatt;
    }

    public void setSymmetryMatt(String[] symmetryMatt) {
        this.symmetryMatt = symmetryMatt;
    }

    public float getSwing() {
        return swing;
    }

    public void setSwing(float swing) {
        this.swing = swing;
    }

    public float getGruix() {
        return gruix;
    }

    public void setGruix(float gruix) {
        this.gruix = gruix;
    }

    public float getDsfou() {
        return dsfou;
    }

    public void setDsfou(float dsfou) {
        this.dsfou = dsfou;
    }

    public float getAlon() {
        return alon;
    }

    public void setAlon(float alon) {
        this.alon = alon;
    }

    public float getAlat() {
        return alat;
    }

    public void setAlat(float alat) {
        this.alat = alat;
    }

    public float getAspin() {
        return aspin;
    }

    public void setAspin(float aspin) {
        this.aspin = aspin;
    }

    public int getMultdom() {
        return multdom;
    }

    public void setMultdom(int multdom) {
        this.multdom = multdom;
    }

    public int getIoff() {
        return ioff;
    }

    public void setIoff(int ioff) {
        this.ioff = ioff;
    }

    public int getNsol() {
        return nsol;
    }

    public void setNsol(int nsol) {
        this.nsol = nsol;
    }

    public int getNfiles() {
        return nfiles;
    }

    public void setNfiles(int nfiles) {
        this.nfiles = nfiles;
    }

    public int[] getFnum() {
        return fnum;
    }

    public void setFnum(int[] fnum) {
        this.fnum = fnum;
    }

    public float[] getFnumAngOff() {
        return fnumAngOff;
    }

    public void setFnumAngOff(float[] fnumAngOff) {
        this.fnumAngOff = fnumAngOff;
    }

    public boolean isSuccessfulRead() {
        return successfulRead;
    }

    public void setSuccessfulRead(boolean successfulRead) {
        this.successfulRead = successfulRead;
    }
    
}
