package com.vava33.d2dplot.tts;

import java.io.File;
import java.util.Scanner;

import com.vava33.d2dplot.D2Dplot_global;
import com.vava33.jutils.VavaLogger;

public class TSDfile {

    private String pathToFile;

    private String title;
    private float a, b, c, alfa, beta, gamma;
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

    private static final String className = "TSDfile";
    private static VavaLogger log = D2Dplot_global.getVavaLogger(className);

    //creates a TSD file via reading it
    public TSDfile(String pathToFile) {
        this.pathToFile = pathToFile;
        this.multdom = 0;
        this.alon = 0;
        this.alat = 0;
        this.aspin = 0;
        this.ioff = 2;
        this.nsol = 10;
        this.swing = 7.5f;
        this.successfulRead = this.readTSD();
    }

    private boolean readTSD() {
        final File f = new File(this.pathToFile);
        if (!f.exists())
            return false;
        boolean finished = false;
        String line;
        try {
            final Scanner scTSDfile = new Scanner(f);
            //1a linia titol
            this.title = scTSDfile.nextLine();
            while (scTSDfile.hasNextLine()) {
                if (finished)
                    break;
                line = scTSDfile.nextLine();
                log.debug(line);

                if (line.contains("CELL")) {
                    final String[] spars = scTSDfile.nextLine().trim().split("\\s+");
                    try {
                        this.a = Float.parseFloat(spars[0]);
                        this.b = Float.parseFloat(spars[1]);
                        this.c = Float.parseFloat(spars[2]);
                        this.alfa = Float.parseFloat(spars[3]);
                        this.beta = Float.parseFloat(spars[4]);
                        this.gamma = Float.parseFloat(spars[5]);
                    } catch (final Exception e) {
                        e.printStackTrace();
                        log.warning("Error reading cell parameters");
                    }
                    continue;
                }

                if (line.contains("LATTICE")) {
                    final String latt = scTSDfile.nextLine();
                    if (latt.trim().endsWith("-")) {
                        this.centro = true;
                    } else {
                        this.centro = false;
                    }
                    this.lattice = latt.charAt(0);
                    log.debug("lattice=" + this.lattice);
                    continue;
                }

                if (line.contains("LAUE")) {
                    try {
                        this.laue = Integer.parseInt(scTSDfile.nextLine().trim());
                    } catch (final Exception e) {
                        e.printStackTrace();
                        log.warning("Error reading LAUE");
                    }
                    continue;
                }

                if (line.contains("SYMMETRY")) {
                    //TODO
                    continue;
                }

                if (line.contains("CONTENTS")) {
                    //TODO
                    continue;
                }

                if (line.contains("CONTROL")) {
                    StringBuilder sbcontrol = new StringBuilder();
                    boolean endcontrol = false;
                    while (!endcontrol) {
                        final String line2 = scTSDfile.nextLine();
                        sbcontrol = sbcontrol.append(line2.trim());
                        if (line2.contains("/"))
                            endcontrol = true;
                    }
                    //                    String control = scTSDfile.next("/");
                    final String control = sbcontrol.substring(0, sbcontrol.length() - 1);
                    log.debug("control=" + control);
                    final String[] items = control.trim().split(",");
                    for (int i = 0; i < items.length; i++) {
                        log.debug("items[" + i + "]=" + items[i]);
                        final String[] item2 = items[i].split("=");
                        try {
                            if (item2[0].equalsIgnoreCase("SWING"))
                                this.swing = Float.parseFloat(item2[1]);
                            if (item2[0].equalsIgnoreCase("GRUIX"))
                                this.gruix = Float.parseFloat(item2[1]);
                            if (item2[0].equalsIgnoreCase("DSFOU"))
                                this.dsfou = Float.parseFloat(item2[1]);
                            if (item2[0].equalsIgnoreCase("MULTDOM"))
                                this.multdom = Integer.parseInt(item2[1]);
                            if (item2[0].equalsIgnoreCase("IOFF"))
                                this.ioff = Integer.parseInt(item2[1]);
                            if (item2[0].equalsIgnoreCase("NSOL"))
                                this.nsol = Integer.parseInt(item2[1]);
                            if (item2[0].equalsIgnoreCase("ALON"))
                                this.alon = Float.parseFloat(item2[1]);
                            if (item2[0].equalsIgnoreCase("ALAT"))
                                this.alat = Float.parseFloat(item2[1]);
                            if (item2[0].equalsIgnoreCase("SPIN"))
                                this.aspin = Float.parseFloat(item2[1]);
                        } catch (final Exception e) {
                            e.printStackTrace();
                            log.warning("Error getting item " + item2[0]);
                        }
                        continue;
                    }
                    continue;
                }

                if (line.contains("MODEL")) {
                    //TODO
                    continue;
                }

                if ((line.contains("PCS")) || (line.contains("HKL"))) {
                    this.nfiles = Integer.parseInt(scTSDfile.nextLine().trim());
                    if (this.multdom != 1) {
                        this.fnum = new int[this.nfiles];
                        this.fnumAngOff = new float[this.nfiles];
                        for (int i = 0; i < this.nfiles; i++) {
                            //                            String[] items = scTSDfile.nextLine().trim().split("\\s+");
                            final String[] items = scTSDfile.nextLine().trim().split(",");
                            this.fnum[i] = Integer.parseInt(items[0]);
                            this.fnumAngOff[i] = Float.parseFloat(items[1]);
                        }
                    }
                    finished = true;
                    continue;
                }
            }
            scTSDfile.close();
        } catch (final Exception e) {
            e.printStackTrace();
            log.warning("Error reading TSD file");
            return false;
        }
        return true;
    }

    public String getPathToFile() {
        return this.pathToFile;
    }

    public void setPathToFile(String pathToFile) {
        this.pathToFile = pathToFile;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public float getA() {
        return this.a;
    }

    public void setA(float a) {
        this.a = a;
    }

    public float getB() {
        return this.b;
    }

    public void setB(float b) {
        this.b = b;
    }

    public float getC() {
        return this.c;
    }

    public void setC(float c) {
        this.c = c;
    }

    public float getAlfa() {
        return this.alfa;
    }

    public void setAlfa(float alfa) {
        this.alfa = alfa;
    }

    public float getBeta() {
        return this.beta;
    }

    public void setBeta(float beta) {
        this.beta = beta;
    }

    public float getGamma() {
        return this.gamma;
    }

    public void setGamma(float gamma) {
        this.gamma = gamma;
    }

    public char getLattice() {
        return this.lattice;
    }

    public void setLattice(char lattice) {
        this.lattice = lattice;
    }

    public int getLaue() {
        return this.laue;
    }

    public void setLaue(int laue) {
        this.laue = laue;
    }

    public boolean isCentro() {
        return this.centro;
    }

    public void setCentro(boolean centro) {
        this.centro = centro;
    }

    public String[] getSymmetryMatt() {
        return this.symmetryMatt;
    }

    public void setSymmetryMatt(String[] symmetryMatt) {
        this.symmetryMatt = symmetryMatt;
    }

    public float getSwing() {
        return this.swing;
    }

    public void setSwing(float swing) {
        this.swing = swing;
    }

    public float getGruix() {
        return this.gruix;
    }

    public void setGruix(float gruix) {
        this.gruix = gruix;
    }

    public float getDsfou() {
        return this.dsfou;
    }

    public void setDsfou(float dsfou) {
        this.dsfou = dsfou;
    }

    public float getAlon() {
        return this.alon;
    }

    public void setAlon(float alon) {
        this.alon = alon;
    }

    public float getAlat() {
        return this.alat;
    }

    public void setAlat(float alat) {
        this.alat = alat;
    }

    public float getAspin() {
        return this.aspin;
    }

    public void setAspin(float aspin) {
        this.aspin = aspin;
    }

    public int getMultdom() {
        return this.multdom;
    }

    public void setMultdom(int multdom) {
        this.multdom = multdom;
    }

    public int getIoff() {
        return this.ioff;
    }

    public void setIoff(int ioff) {
        this.ioff = ioff;
    }

    public int getNsol() {
        return this.nsol;
    }

    public void setNsol(int nsol) {
        this.nsol = nsol;
    }

    public int getNfiles() {
        return this.nfiles;
    }

    public void setNfiles(int nfiles) {
        this.nfiles = nfiles;
    }

    public int[] getFnum() {
        return this.fnum;
    }

    public void setFnum(int[] fnum) {
        this.fnum = fnum;
    }

    public float[] getFnumAngOff() {
        return this.fnumAngOff;
    }

    public void setFnumAngOff(float[] fnumAngOff) {
        this.fnumAngOff = fnumAngOff;
    }

    public boolean isSuccessfulRead() {
        return this.successfulRead;
    }

    public void setSuccessfulRead(boolean successfulRead) {
        this.successfulRead = successfulRead;
    }

}
