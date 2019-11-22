package com.vava33.d2dplot.auxi;

import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.Locale;

import javax.swing.JCheckBox;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.ProgressMonitor;
import javax.swing.SwingWorker;

import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.util.FastMath;

import com.vava33.d2dplot.D2Dplot_global;
import com.vava33.d2dplot.MainFrame;
import com.vava33.d2dplot.PeakSearch;
import com.vava33.d2dplot.auxi.ImgFileUtils.EdfHeaderPatt2D;
import com.vava33.d2dplot.auxi.ImgFileUtils.SupportedWriteExtensions;
import com.vava33.d2dplot.auxi.PDDatabase.SearchDBWorker;
import com.vava33.d2dplot.auxi.Pattern1D.PointPatt1D;
import com.vava33.jutils.FileUtils;
import com.vava33.jutils.VavaLogger;

/*
 * Operacions sobre imatges (sostracció fons, correccions, etc...)
 */
public final class ImgOps {

    private static int bkgIter;

    static ProgressMonitor pm = null;
    static SumImagesFileWorker sumwk = null;
    static Pattern2D pattSum = null;

    private static final String className = "ImgOps";
    private static VavaLogger log = D2Dplot_global.getVavaLogger(className);

    public static Pattern2D firstBkgPass(Pattern2D dataIn) {

        final Pattern2D dataOut = new Pattern2D(dataIn, false);

        bkgIter = 0; //inicialitzem iter
        int Imin = Integer.MAX_VALUE;
        int Imean = 0;
        long Iacum = 0;
        int countPoints = 0;
        for (int j = 0; j < dataOut.getDimY(); j++) {
            for (int i = 0; i < dataOut.getDimX(); i++) {
                if (dataIn.isExcluded(i, j))
                    continue;
                if (dataIn.getInten(i, j) > 0) {   //TODO: afegit Dec2018
                    if (dataIn.getInten(i, j) < Imin) {
                        Imin = dataIn.getInten(i, j);
                    }
                }
                Iacum = Iacum + dataIn.getInten(i, j);
                countPoints++;
            }
        }
        Iacum = FastMath.round(Iacum / countPoints);
        Imean = (int) Iacum;

        //si la intensitat mitjana es zero, abortem el firstBkgPass
        if (Imean < 0)
            return dataOut;

        //corregim els punts (els mascara ja es mantenen a -1)
        for (int j = 0; j < dataOut.getDimY(); j++) {
            for (int i = 0; i < dataOut.getDimX(); i++) {
                if (dataIn.getInten(i, j) > (Imean + 2 * (Imean - Imin))) {
                    dataOut.setInten(i, j, (Imean + 2 * (Imean - Imin)), false);
                } else {
                    dataOut.setInten(i, j, dataIn.getInten(i, j), false);
                }
            }
        }

        return dataOut;
    }

    public static Pattern2D firstBkgPassStronger(Pattern2D dataIn) {
        bkgIter = 0;
        float t2 = 0f;
        //	        float tstep = dataIn.calcMinStepsizeBy2Theta4Directions();
        final float tstep = dataIn.calcMinStepsizeEstimateWithPixSizeAndDistMD();
        final float t2max = dataIn.getMax2Tdeg();
        int n = FastMath.round((t2max - t2) / tstep);
        log.writeNameNumPairs("config", true, "n", n);
        final float[] t2list = new float[n + 1];
        int i = 0;
        while (t2 < t2max) {
            t2list[i] = t2;
            t2 = t2 + tstep;
            i = i + 1;
        }

        final float[] intens = radialIntegrationVarious2th(dataIn, t2list, tstep, false, false, true, null);
        log.debug(Arrays.toString(intens));

        final Pattern2D dataOut = new Pattern2D(dataIn, false);

        //corregim els punts (els mascara ja es mantenen a -1)
        for (int j = 0; j < dataOut.getDimY(); j++) {
            for (i = 0; i < dataOut.getDimX(); i++) {
                if (dataIn.isExcluded(i, j))
                    continue;
                //posicio del vector intens
                final double t2i = dataIn.calc2T(i, j, true);
                if (t2i > t2max) {
                    log.debug("t2i>t2max");
                }
                n = (int) FastMath.round(t2i / tstep);
                final float Imean = intens[n];
                if (dataIn.getInten(i, j) > (Imean + 1.5 * (FastMath.sqrt(Imean)))) {
                    dataOut.setInten(i, j, (int) (Imean), false);
                } else {
                    dataOut.setInten(i, j, dataIn.getInten(i, j), false);
                }
            }
        }

        return dataOut;
    }

    public static Pattern2D calcIterAvsq(Pattern2D dataIn, int N, JProgressBar progress) throws InterruptedException {
        final long startTime = System.currentTimeMillis();
        //PROGRESS BAR:
        //		if(txtOut!=null){
        //			txtOut.addtxt(false, true, "");
        //		}
        bkgIter = bkgIter + 1;
        final Pattern2D dataOut = new Pattern2D(dataIn, false);
        for (int j = 0; j < dataOut.getDimY(); j++) {
            for (int i = 0; i < dataOut.getDimX(); i++) {
                //comprovem si el punt es una mascara, i si es aixi el saltem donant-li
                //el valor de la iteracio anterior (suposadament -1)
                if (dataIn.isExcluded(i, j)) {
                    dataOut.setInten(i, j, dataIn.getInten(i, j), false);
                    dataOut.getPixel(i, j).setExcluded(true); //tot i que ja ho deu ser perque hem copiat exz de dataIn a dataOut
                    continue;
                }
                int sumI = 0;
                int nMaskP = 0;
                //el quadrat al voltant del punt
                for (int k = j - N; k <= j + N; k++) { //k files al voltant del punt
                    for (int l = i - N; l <= i + N; l++) { //L columnes al voltant del punt
                        if ((k >= 0) && (k < dataIn.getDimY()) && (l >= 0) && (l < dataIn.getDimX())) {
                            //estem dins la imatge
                            //el propi punt no el considerem
                            if ((k == j) && (l == i))
                                continue;
                            //comprovem si el punt es mascara, si es aix� el "restem" (no el
                            //tenim en compte) i saltem al seguent
                            //							if(dataIn.getInten(l, k)<0){
                            if (dataIn.isExcluded(l, k)) {
                                nMaskP++;
                                continue;
                            }
                            //CAS NORMAL: punt al "centre" que no xoca amb limits (ja hem comprovat que no sigui
                            //(un punt mascara al principi)
                            sumI = sumI + dataIn.getInten(l, k);
                        } else {
                            //ESTEM FORA LA IMAGE
                            //			                  !Agafarem el punt mes proper al punt en questi�, sempre i quant no sigui mascara
                            //			                  !calcularem els nous indexs l,k
                            //			                  !EN TOTS ELS CASOS QUE SOBRESURT HEM DE VIGILAR AMB ELS MARGES DE LA IMATGE
                            //			                  !SI ES QUE N'HI HA, �s a dir:
                            //			                    ! - (0,0) sera (it0%margin, it0%margin)
                            //			                    ! - (nxmx,nymx) sera (nxmx-it0%margin-1, nymx-it0%margin-1)
                            //			                  !valors newL,newK inicials l,k
                            int newL = l;
                            int newK = k;
                            //			                  !si el quadrat sobresurt de les x (columnes) per l'esquerra s'agafa la 1a columna
                            if (l <= 0)
                                newL = dataIn.getExz_margin();
                            //	                          !si el quadrat sobresurt de les x (columnes) per la dreta s'agafa la darrera columna
                            if (l >= dataIn.getDimX())
                                newL = dataIn.getDimX() - dataIn.getExz_margin() - 1;
                            //	                          !si el quadrat sobresurt de les y (files) per dalt s'agafa la 1a fila
                            if (k <= 0)
                                newK = dataIn.getExz_margin();
                            //	                          !si el quadrat sobresurt de les y (files) per baix s'agafa la darrera fila
                            if (k >= dataIn.getDimY())
                                newK = dataIn.getDimY() - dataIn.getExz_margin() - 1;
                            //	                          !comprovem que newL,newK no sigui mascara, si es mascara no el considerem (el restem)
                            //	                        if(dataIn.getInten(newL,newK)<0){
                            if (dataIn.isExcluded(newL, newK)) {
                                nMaskP++;
                                continue;
                            }
                            //si es un pixel normal el sumem
                            sumI = sumI + dataIn.getInten(newL, newK);
                        }
                    }
                }
                int Inew = 0;
                if (((2 * N + 1) * (2 * N + 1) - 1 - nMaskP) > 0) {
                    Inew = sumI / ((2 * N + 1) * (2 * N + 1) - 1 - nMaskP); //restem els nMaskP ja que NO han contribuit
                }
                //				int Inew= sumI/((2*N+1)*(2*N+1)-1-nMaskP); //restem els nMaskP ja que NO han contribuit
                //si el valor de Inew es menor del que hi havia actualitzem, sino el deixem
                if (Inew < dataIn.getInten(i, j)) {
                    dataOut.setInten(i, j, Inew, false);
                } else {
                    dataOut.setInten(i, j, dataIn.getInten(i, j), false);
                }
            }//dimX
             //PROGRESS BAR:
            if (progress != null) {
                final long elapTime = (System.currentTimeMillis() - startTime) / 1000;
                final float percent = ((float) j / (float) dataOut.getDimY()) * 100;
                final float estTime = (((100 - percent) * elapTime) / percent) / 60; //en minuts
                progress.setString(String.format(Locale.ENGLISH, "Iter. %d -> %6.2f %% (est. time %6.2f min.)", bkgIter,
                        percent, estTime));
            }
            //per si s'ha aturat
            if (Thread.interrupted()) {
                throw new InterruptedException();
            }
        }//dimY

        final long endTime = System.currentTimeMillis();
        final float totalTime = (endTime - startTime) / 1000;
        if (progress != null) {
            progress.setString("Iteration finished!");
        }
        log.info("fi iter. " + bkgIter + " (time: " + totalTime + " s)");
        return dataOut;
    }

    public static Pattern2D subtractBKG(Pattern2D dataIn, Pattern2D dataToSubtract) {
        final Pattern2D dataSub = new Pattern2D(dataIn, false);
        dataSub.setMaxI(0);
        dataSub.setMinI(9999999);
        for (int j = 0; j < dataSub.getDimY(); j++) {
            for (int i = 0; i < dataSub.getDimX(); i++) {
                //si és exclos mantenim valor
                if (dataIn.isExcluded(i, j)) {
                    dataSub.setInten(i, j, dataIn.getInten(i, j), false);
                    dataSub.getPixel(i, j).setExcluded(true);
                    continue;
                }
                //si la intensitat es menor a LA FEM ZERO
                if (dataIn.getInten(i, j) < dataToSubtract.getInten(i, j)) {
                    dataSub.setInten(i, j, 0, false);
                    continue;
                }
                dataSub.setInten(i, j, (dataIn.getInten(i, j) - dataToSubtract.getInten(i, j)), false);
                //propaguem error (suma dels dos)
                dataSub.getPixel(i, j).setErr(dataIn.getPixel(i, j).getErr() + dataToSubtract.getPixel(i, j).getErr());
                if (dataSub.getInten(i, j) > dataSub.getMaxI())
                    dataSub.setMaxI(dataSub.getInten(i, j));
                if (dataSub.getInten(i, j) < dataSub.getMinI())
                    dataSub.setMinI(dataSub.getInten(i, j));
            }
        }
        return dataSub;
    }

    //return [0] dataSubtracted and [1] dataMask
    public static Pattern2D[] subtractBKG_v2(Pattern2D dataIn, Pattern2D dataToSubtract, float factor) {
        final Pattern2D[] dataSub = { new Pattern2D(dataIn, false), new Pattern2D(dataIn, false) };
        dataSub[0].setMaxI(0);
        dataSub[1].setMaxI(0);
        dataSub[0].setMinI(9999999);
        dataSub[1].setMinI(9999999);
        int nover = 0;
        for (int j = 0; j < dataSub[0].getDimY(); j++) {
            for (int i = 0; i < dataSub[0].getDimX(); i++) {
                //dataMask comen�a amb el pixel a zero
                dataSub[1].setInten(i, j, 0, true);
                //si és exclos mantenim valor
                if (dataIn.isExcluded(i, j)) { //TODO: POTSER HAURIEM DE POSAR A ZERO LA INTENSITAT DE LES ZONES EXCLOSES?
                    dataSub[0].setInten(i, j, dataIn.getInten(i, j), false);
                    dataSub[0].getPixel(i, j).setExcluded(true);
                    continue;
                }
                //si la intensitat es menor a LA FEM ZERO
                if (dataIn.getInten(i, j) < (dataToSubtract.getInten(i, j) * factor)) {
                    dataSub[0].setInten(i, j, 0, false);
                    dataSub[1].setInten(i, j, FastMath.abs(dataIn.getInten(i, j) - dataToSubtract.getInten(i, j)),
                            false); //deixem err a zero, ens es una mica igual aquesta imatge mascara
                    nover = nover + 1;
                    continue;
                }
                dataSub[0].setInten(i, j, (int) (dataIn.getInten(i, j) - (dataToSubtract.getInten(i, j) * factor)),
                        false);
                //propaguem error (suma dels dos)
                dataSub[0].getPixel(i, j)
                        .setErr(dataIn.getPixel(i, j).getErr() + dataToSubtract.getPixel(i, j).getErr() * factor);

                //debug
                //                if ((i==1075) && (j==982)){
                //                	log.writeNameNumPairs("config", true, "pX,pY,err", i,j,dataSub[0].getPixel(i, j).getErr());
                //                }
                //				
                if (dataSub[0].getInten(i, j) > dataSub[0].getMaxI())
                    dataSub[0].setMaxI(dataSub[0].getInten(i, j));
                if (dataSub[0].getInten(i, j) < dataSub[0].getMinI())
                    dataSub[0].setMinI(dataSub[0].getInten(i, j));
            }
        }
        String linia = String.format(Locale.ENGLISH, "  No. of pixels with Ybkg>Ydata = %d (%.1f%%)", nover,
                ((float) (nover) / (float) (dataSub[0].getDimX() * dataSub[0].getDimY()) * 100));
        log.info(linia);
        linia = String.format(Locale.ENGLISH, "  Factor = %.5f", factor);
        log.info(linia);
        return dataSub;
    }

    public static float calcGlassScale(Pattern2D data, Pattern2D glass) {
        float scale = 10000000;
        final float tol = 0.025f;
        for (int j = 0; j < data.getDimY(); j++) {
            for (int i = 0; i < data.getDimX(); i++) {
                //no considerem I=0 o mascara
                if (data.isExcluded(i, j))
                    continue;
                if (glass.isExcluded(i, j))
                    continue;
                final float sc = (float) ((data.getInten(i, j)) - tol * FastMath.sqrt(data.getInten(i, j)))
                        / (glass.getInten(i, j));
                if (sc < scale && sc != 0)
                    scale = sc;
            }
        }
        return scale;
    }

    //subrutina que ens busca i corregeix pics espuris en una imatge de fons regular com el vidre
    public static Pattern2D correctGlass(Pattern2D glass) {
        final float t2ini = 0;
        final float t2fin = 80;
        final float stepsize = 0.05f;
        final float percent = 0.25f;
        float t2p, criteri;
        int pos;

        //		if (!glass.checkIfDistMD()){
        //		    Param_dialog p = new Param_dialog(null,glass);
        //		    p.setVisible(true);
        //		    while (p.isVisible()){
        //		        try {
        //                    Thread.sleep(2000);
        //                } catch (InterruptedException e) {
        //                    if(D2Dplot_global.isDebug())e.printStackTrace();
        //                    log.warning("Error in correct glass");
        //                }
        //		    }
        //		}

        if (!glass.checkIfDistMD()) {
            log.warning("Missing instrumental parameters in glass file, skipping corrections");
            return glass;
        }

        //primer integrem la imatge
        final Pattern1D intrad = radialIntegration(glass, t2ini, t2fin, stepsize, -1, -1, false, false, 0.f);

        //per cada pixel mirarem la intensitat mitjana a l'anell que es troba i si aquesta intensitat es
        //mes gran que ymean+2*desv el considerem pic espuri
        for (int j = 0; j < glass.getDimY(); j++) {
            for (int i = 0; i < glass.getDimX(); i++) {
                //s'haurien d'haver aplicat les zones excloses al vidre
                if (glass.isExcluded(i, j))
                    continue;
                //2Theta pixel imatge per determinar amplada i angle
                t2p = (float) glass.calc2T(i, j, true);
                //ara hem de mirar a quina posicio del vector desv es troba aquesta 2t
                pos = FastMath.round(t2p / stepsize) - FastMath.round(t2ini / stepsize);
                //ara hem de decidir si es pic espuri o no
                //dos opcions, amb la mitjana+% o amb la fact*desviacio
                final float ysum = intrad.getPoint(pos).getCounts();
                final float npix = intrad.getPoint(pos).getNpix();
                criteri = ysum / npix + (ysum / npix) * percent; //factDesv*desv(p)
                //criteri=(real(ysum(p))/real(npix(p)))+factDesv*desv(p)		
                if (glass.getInten(i, j) > criteri)
                    glass.setInten(i, j, FastMath.round(ysum / npix), false);
            }
        }
        return glass;
    }

    //    !calcula una nova iteració del fons a partir de l'anterior
    //    !utilitza integracio RADIAL
    //    !parametres: -dataIn: iteracio anterior de referencia
    //    !            -stepsize: ...
    //    !return: nova iteracio
    public static Pattern2D calcIterAvcirc(Pattern2D dataIn, float stepsize, JProgressBar progress)
            throws InterruptedException {
        final long startTime = System.currentTimeMillis();
        //PROGRESS BAR:
        bkgIter = bkgIter + 1;
        final Pattern2D dataOut = new Pattern2D(dataIn, false);

        //        !primer fem la integracio radial de la iteracio anterior
        //        !calcul del 2T maxim (pixel extrem: 0,0 per exemple)
        //        !calcul angle 2teta en graus:
        final float t2fin = (float) dataIn.calc2T(0, 0, true);
        final Pattern1D intrad = radialIntegration(dataIn, 0.0f, t2fin, stepsize, -1, -1, false, false, 0.f); //realment aqui en el promig hauriem de NO considerar pics

        float t2p, ysum, npix;
        int pos;
        //dataOut sera el promig de la integracio radial de dataIn
        for (int j = 0; j < dataOut.getDimY(); j++) {
            for (int i = 0; i < dataOut.getDimX(); i++) {
                if (dataIn.isExcluded(i, j)) {
                    dataOut.setInten(i, j, dataIn.getInten(i, j), false);
                    dataOut.getPixel(i, j).setExcluded(true);
                    continue;
                }
                t2p = (float) dataOut.calc2T(i, j, true);
                //pos al vector
                pos = FastMath.round(t2p / stepsize) - FastMath.round(0.0f / stepsize);
                //ara hem de decidir si es pic espuri o no
                //dos opcions, amb la mitjana+% o amb la fact*desviacio
                ysum = intrad.getPoint(pos).getCounts();
                npix = intrad.getPoint(pos).getNpix();
                dataOut.setInten(i, j, (int) (ysum / npix), true);
            }

            //PROGRESS BAR:
            if (progress != null) {
                final long elapTime = (System.currentTimeMillis() - startTime) / 1000;
                final float percent = ((float) j / (float) dataOut.getDimY()) * 100;
                final float estTime = (((100 - percent) * elapTime) / percent) / 60; //en minuts
                progress.setString(String.format(Locale.ENGLISH, "Iter. %d -> %6.2f %% (est. time %6.2f min.)", bkgIter,
                        percent, estTime));
            }
            //per si s'ha aturat
            if (Thread.interrupted()) {
                throw new InterruptedException();
            }

        }
        final long endTime = System.currentTimeMillis();
        final float totalTime = (endTime - startTime) / 1000;
        if (progress != null) {
            progress.setString("Iteration finished!");
        }
        log.info("fi iter. " + bkgIter + " (time: " + totalTime + " s)");
        return dataOut;
    }

    //    !calcula una nova iteració del fons a partir de l'anterior
    //    !parametres: -it0: iteracio anterior de referencia
    //    !            -it1: nova iteracio
    //    !            -N: pixels de la regio rectangular
    //    !els valors de -1 (mascara) els deixarem tal qual (no calcularem fons) i
    //    !tambe farem que no contribueixin a promitjar el fons d'altres punts. Per
    //    !això emmagatzemarem a una variable int (nMaskP) el nombre de punts d'aquests
    //    !que haguéssin contribuit i ho restarem al fer el promig
    public static Pattern2D calcIterAvarc(Pattern2D dataIn, float ampladaArc, float oberturaArc, JProgressBar progress)
            throws InterruptedException {
        final long startTime = System.currentTimeMillis();

        bkgIter = bkgIter + 1;
        final Pattern2D dataOut = new Pattern2D(dataIn, false);

        //ara a cada pixel de la imatge calcularem el promig dels del voltant de l'arc
        for (int j = 0; j < dataOut.getDimY(); j++) {
            for (int i = 0; i < dataOut.getDimX(); i++) {
                if (dataIn.isExcluded(i, j)) {
                    dataOut.setInten(i, j, dataIn.getInten(i, j), false);
                    dataOut.getPixel(i, j).setExcluded(true);
                    continue;
                }
                //per cada pixel mirem el promig (iteracio anterior)
                final float[] fact = dataIn.getFactAngleAmplada(i, j);
                final float obertura = oberturaArc * fact[0];
                final float amplada = ampladaArc * fact[1];
                final Patt2Dzone arc = yArcTilt(dataIn, i, j, amplada, obertura, false, 0, false);

                //   	        assignem al pixel la intensitat mitjana (fitxer de fons que despres restarem)
                //   	        nomes si la intensitat anterior es superior
                //TODO: aqui es podria introduir la intensitat del fons calculada a Yarc en comptes de Ymean     
                if (dataIn.getInten(i, j) > arc.getYmean()) {
                    dataOut.setInten(i, j, (int) arc.getYmean(), true);
                } else {
                    dataOut.setInten(i, j, dataIn.getInten(i, j), false);
                }
            }

            //PROGRESS BAR:
            if ((progress != null)) {
                final long elapTime = (System.currentTimeMillis() - startTime) / 1000;
                final float percent = ((float) j / (float) dataOut.getDimY()) * 100;
                final float estTime = (((100 - percent) * elapTime) / percent) / 60; //en minuts
                progress.setString(String.format(Locale.ENGLISH, "Iter. %d -> %6.2f %% (est. time %6.2f min.)", bkgIter,
                        percent, estTime));
            }
            //per si s'ha aturat
            if (Thread.interrupted()) {
                throw new InterruptedException();
            }
        }

        final long endTime = System.currentTimeMillis();
        final float totalTime = (endTime - startTime) / 1000;
        if (progress != null) {
            progress.setString("Iteration finished!");
        }
        log.info("fi iter. " + bkgIter + " (time: " + totalTime + " s)");

        return dataOut;
    }

    //metode flip
    public static Pattern2D bkgMin(Pattern2D dataIn, int fhor, int fver, int fhorver, int aresta, float oberturaArc,
            float ampladaArc, boolean minarc, JProgressBar progress) throws InterruptedException {
        final long startTime = System.currentTimeMillis();

        final boolean hor = (fhor == 1) ? true : false;
        final boolean ver = (fver == 1) ? true : false;
        final boolean horver = (fhorver == 1) ? true : false;

        final Pattern2D dataOut = new Pattern2D(dataIn, false);

        float v0, v1, v2, v3; //valor pixel,valor flip hor, valor flip vert, valor fliphor-flipvert
        int newj, newi;
        Patt2Dzone zone;

        //ara a cada pixel de la imatge calcularem el promig dels del voltant de l'arc
        for (int j = 0; j < dataOut.getDimY(); j++) {
            for (int i = 0; i < dataOut.getDimX(); i++) {
                //exzone
                if (dataIn.isExcluded(i, j)) {
                    dataOut.setInten(i, j, dataIn.getInten(i, j), false);
                    dataOut.getPixel(i, j).setExcluded(true);
                    continue;
                }
                //punt central (normalment mascara)
                if (i == (int) (dataIn.getCentrX()) && j == (int) (dataIn.getCentrY())) {
                    dataOut.setInten(i, j, dataIn.getInten(i, j), false);
                    continue;
                }
                //calcul de v0,v1,v2,v3
                float obertura = 1, amplada = 1;
                if (minarc) {
                    final float[] fact = dataIn.getFactAngleAmplada(i, j);
                    obertura = oberturaArc * fact[0];
                    amplada = ampladaArc * fact[1];
                    zone = yArcTilt(dataIn, i, j, amplada, obertura, true, 0, false);
                    v0 = FastMath.round(zone.getYmean());
                } else {
                    v0 = dataIn.calcIntSquare(i, j, aresta, true);
                }
                //inicialitzem v1,v2 i v3 igual a v0 en cas que no s'assignin despres perque queden fora
                v1 = v0;
                v2 = v0;
                v3 = v0;
                newi = (int) (dataIn.getCentrX() + (dataIn.getCentrX() - i));
                newj = (int) (dataIn.getCentrY() + (dataIn.getCentrY() - j));

                if (minarc) {
                    //prova amb integracio arc
                    if (hor) {
                        zone = yArcTilt(dataIn, newi, j, amplada, obertura, true, 0, false);
                        if (zone.getNpix() > 0)
                            v1 = FastMath.round(zone.getYmean());
                    }
                    if (ver) {
                        zone = yArcTilt(dataIn, i, newj, amplada, obertura, true, 0, false);
                        if (zone.getNpix() > 0)
                            v1 = FastMath.round(zone.getYmean());
                    }
                    if (horver) {
                        zone = yArcTilt(dataIn, newi, newj, amplada, obertura, true, 0, false);
                        if (zone.getNpix() > 0)
                            v1 = FastMath.round(zone.getYmean());
                    }
                } else {
                    if (hor) {
                        v1 = dataIn.calcIntSquare(newi, j, aresta, true);
                    }
                    if (ver) {
                        v2 = dataIn.calcIntSquare(i, newj, aresta, true);
                    }
                    if (horver) {
                        v3 = dataIn.calcIntSquare(newi, newj, aresta, true);
                    }
                }

                //comprovem que no hi hagi mascares
                if (v1 < 0)
                    v1 = v0;
                if (v2 < 0)
                    v2 = v0;
                if (v3 < 0)
                    v3 = v0;

                //en cas que tot sigui igual avisem (de moment no el posem a zero...)
                if (v0 == v1 && v0 == v2 && v0 == v3) {
                    //TODO cal fer algo?
                }

                //cas normal agafem el minim
                dataOut.setInten(i, j, (int) ImgOps.findMin(v0, v1, v2, v3), true);
            }
            //PROGRESS BAR:
            if (progress != null) {
                final long elapTime = (System.currentTimeMillis() - startTime) / 1000;
                final float percent = ((float) j / (float) dataOut.getDimY()) * 100;
                final float estTime = (((100 - percent) * elapTime) / percent) / 60; //en minuts
                progress.setString(
                        String.format(Locale.ENGLISH, "Bkg sub. -> %6.2f %% (est. time %6.2f min.)", percent, estTime));
            }
            //per si s'ha aturat
            if (Thread.interrupted()) {
                throw new InterruptedException();
            }

        }
        final long endTime = System.currentTimeMillis();
        final float totalTime = (endTime - startTime) / 1000;
        if (progress != null) {
            progress.setString("Bkg subtraction finished!");
        }
        log.info("Bkg subtraction finished (time: " + totalTime + " s)");
        return dataOut;
    }

    //iosc es l'eix de tilt, 1=horitzontal, 2=vertical
    //ilor oscil=1 powder=2,
    //ipol syn=1 lab=2

    //valors ilor ipol
    //  ilor=chckbxLorOscil.isSelected()?1:0;
    //    ilor=chckbxLorPow.isSelected()?2:ilor;
    //    ipol=chckbxPolSyn.isSelected()?1:0;
    //    ipol=chckbxPolLab.isSelected()?2:ipol;
    //PER FER LA INTEGRACIO RADIAL ILOR=2, IPOL=1 -- seran els valors per defecte
    //RETURNS A VECTOR: [INTENSITY CORRECTED FOR LP, Lfactor, Pfactor] --- son factors, vol dir que s'han de multiplicar
    public static double[] corrLP(Pattern2D dataIn, int pX, int pY, int iPol, int iLor, int iosc, boolean debug) {

        final double pixsA = dataIn.getPixSx() * 10000000;
        final double distA = dataIn.getDistMD() * 10000000;
        final double fact = pixsA / (1 + dataIn.getWavel() * distA); //cal convertir wave ang to mm
        if (iosc == -1)
            iosc = 2; //EIX D'OSCILACIO 1=horitzontal, 2=Vertical
        if (iLor == -1)
            iLor = 2;
        if (iPol == -1)
            iPol = 1;

        //      per cada pixel s'ha de calcular l'angle azimutal (entre la normal
        //      (al pla de polaritzacio *i* el vector del centre de la imatge (xc,yc)
        //      al pixel (x,y) en questi�). Tamb� s'ha de calcular l'angle 2T del pixel

        final double[] nothingdone = { dataIn.getInten(pX, pY), 1, 1 };
        //zona exclosa saltem
        if (dataIn.isExcluded(pX, pY))
            return nothingdone;
        //el punt central el saltem
        if ((pY == (int) (dataIn.getCentrY())) && (pX == (int) (dataIn.getCentrX())))
            return nothingdone;

        //debug: tots els punts amb intensitat 12500 (per veure efectes)
        if (debug) {
            dataIn.setInten(pX, pY, 12500, false);
            dataIn.setMaxI(12500);
        }

        final double vecCPx = (pX - dataIn.getCentrX()) * dataIn.getPixSx(); //en mm
        final double vecCPy = (dataIn.getCentrY() - pY) * dataIn.getPixSy(); //en mm
        final double t2 = dataIn.calc2T(pX, pY, false);

        //lorentz
        double rloren = 1.0f;
        if (iLor == 1) {
            final double xkug = fact * (pX - dataIn.getCentrX()) * FastMath.cos(t2);
            final double ykug = fact * (pY - dataIn.getCentrY()) * FastMath.cos(t2);
            final double zkug = 2.0 / dataIn.getWavel() * FastMath.pow(FastMath.sin(t2 / 2.0), 2); //he canviat waveMM per wavel per igualar amb el jordi PERO no se què esta be...
            final double dkug = FastMath.sqrt(xkug * xkug + ykug * ykug + zkug * zkug);

            double phi = 1;
            if (iosc == 1)
                phi = FastMath.acos(xkug / dkug);
            if (iosc == 2)
                phi = FastMath.acos(ykug / dkug);
            final double arg = FastMath.pow(FastMath.sin(phi), 2) - FastMath.pow(FastMath.sin(t2 / 2.0), 2);
            log.writeNameNums("FINE", true, "pX,pY,xkug,ykug,zkug,dkug,fact,t2,phi,arg", pX, pY, xkug, ykug, zkug, dkug,
                    fact, FastMath.toDegrees(t2), phi, arg);
            rloren = 0;
            if (arg > 0) {
                rloren = 2 * FastMath.sin(t2 / 2.0) * FastMath.sqrt(arg);
                //                log.debug("hello");
            }

        }
        if (iLor == 2) {
            rloren = 1.0 / (FastMath.cos(t2 / 2) * FastMath.pow(FastMath.sin(t2 / 2), 2)); //igual que el DAJUST
        }

        //polaritzacio
        double pol = 1.0f;
        if (iPol == 1) {
            final double xdist2 = vecCPx * vecCPx;
            final double ydist2 = vecCPy * vecCPy;
            final double sepOD2 = dataIn.getDistMD() * dataIn.getDistMD();
            if (iosc == 1)
                pol = (sepOD2 + xdist2) / (sepOD2 + xdist2 + ydist2);
            if (iosc == 2)
                pol = (sepOD2 + ydist2) / (sepOD2 + xdist2 + ydist2);
        }

        if (iPol == 2) {
            pol = 0.5 + 0.5 * (FastMath.cos(t2) * FastMath.cos(t2));
        }

        //cas sense oscil·lacio
        if (iosc == 0) {
            rloren = 1.0f;
            pol = 1.0f;
        }

        final double[] result = { (dataIn.getInten(pX, pY) * rloren) / pol, rloren, 1 / pol };
        return result;
    }

    //idem pero corregint tot el pattern
    public static Pattern2D corrLP(Pattern2D dataIn, int iPol, int iLor, int iosc, boolean debug) {
        final Pattern2D dataOut = new Pattern2D(dataIn, false);
        int maxVal = 0;
        if (iosc == -1)
            iosc = 2;

        //      per cada pixel s'ha de calcular l'angle azimutal (entre la normal
        //      (al pla de polaritzacio *i* el vector del centre de la imatge (xc,yc)
        //      al pixel (x,y) en questi�). Tamb� s'ha de calcular l'angle 2T del pixel
        for (int j = 0; j < dataIn.getDimY(); j++) {
            for (int i = 0; i < dataIn.getDimX(); i++) {

                final double[] lpfac = corrLP(dataIn, i, j, iPol, iLor, iosc, false);

                //debug: tots els punts amb intensitat 12500 (per veure efectes)
                if (debug) {
                    dataOut.setInten(i, j, FastMath.round(12500 * (float) lpfac[1] * (float) lpfac[2]), false);
                } else {
                    dataOut.setInten(i, j, FastMath.round((float) lpfac[0]), false);
                }
                //mirem si superem els limits (per escalar despres)
                if (dataOut.getInten(i, j) > maxVal)
                    maxVal = dataOut.getInten(i, j);
            }
        }

        //si ens hem passat del maxim calculem el factor d'escala i escalem
        float fscale = 1.0f;
        if (maxVal > dataOut.getSaturValue()) {
            fscale = (float) (dataOut.getSaturValue()) / (float) maxVal;
        }
        for (int j = 0; j < dataOut.getDimY(); j++) {
            for (int i = 0; i < dataOut.getDimX(); i++) {
                //mascara el deixem tal qual
                if (dataIn.isExcluded(i, j)) {
                    dataOut.setInten(i, j, dataOut.getInten(i, j), false);
                    dataOut.getPixel(i, j).setExcluded(true);
                    continue;
                }
                dataOut.setInten(i, j, FastMath.round(dataOut.getInten(i, j) * fscale), true);
            }
        }
        return dataOut;
    }

    //retorna un array amb {Intensitat corregida, factor aplicat}
    public static float[] corrIncidentAngle(Pattern2D dataIn, int pX, int pY) {

        final float azim = dataIn.getAzimAngle(pX, pY, false);
        final float tthRad = (float) dataIn.calc2T(pX, pY, false);

        //angle incident effectiu:
        final float tiltRad = (float) FastMath.toRadians(dataIn.getTiltDeg());
        final float rotRad = (float) FastMath.toRadians(dataIn.getRotDeg());
        final float tiltRadEff = (float) (tiltRad * (FastMath.sin(azim - rotRad)));
        final float incAngEff = tthRad - tiltRadEff;

        //la correccio Icorr = Iobs/K on K= cos3 incAngEff
        final float K = (float) (FastMath.cos(incAngEff) * FastMath.cos(incAngEff) * FastMath.cos(incAngEff));
        final float[] result = { dataIn.getInten(pX, pY) / K, 1 / K };
        return result;
    }

    //when we click to a point we add the peak but calculating the things (saturated, nearmaks, etc..) as in FindPeaks
    //neixam no ho considerem perque l'afegim apart
    public static Peak addPeakFromCoordinates(Pattern2D patt2d, Point2D.Float pixel, int zoneRadius) {
        final Ellipse2D.Float elli = new Ellipse2D.Float(pixel.x - zoneRadius, pixel.y - zoneRadius, zoneRadius * 2,
                zoneRadius * 2);
        final int ix = (int) (pixel.x);
        final int iy = (int) (pixel.y);

        final Peak pic = new Peak(pixel);
        int sumaX = 0;
        int sumaY = 0;
        int sumaInt = 0;

        //mirem nearmask, saturats i determinem millor el centre
        for (int j = iy - zoneRadius; j < iy + (zoneRadius * 2); j++) {
            for (int i = ix - zoneRadius; i < ix + (zoneRadius * 2); i++) {
                if (elli.contains(i, j)) {
                    if (!patt2d.isInside(i, j)) {
                        pic.setNearMask(true);
                        continue;
                    }
                    if (patt2d.isExcluded(i, j)) {
                        pic.setNearMask(true);
                        continue;
                    }
                    if (patt2d.getInten(i, j) >= (patt2d.getSaturValue() - 1)) {
                        pic.setnSatur(pic.getnSatur() + 1);
                    }
                    sumaX = sumaX + patt2d.getInten(i, j) * i;
                    sumaY = sumaY + patt2d.getInten(i, j) * j;
                    sumaInt = sumaInt + patt2d.getInten(i, j);
                }
            }
            final float xpond = (float) sumaX / (float) sumaInt;
            final float ypond = (float) sumaY / (float) sumaInt;
            pic.getPixelCentre().x = xpond;
            pic.getPixelCentre().y = ypond;
        }
        return pic;
    }

    public static ArrayList<Peak> findPeaks(Pattern2D patt2d, float delsig, int zoneRadius, int minpix,
            boolean roundToInt, boolean estimBKG, boolean pond) {

        final int maxPeakCandidates = 2500;

        //1r busquem candidats
        final ArrayList<Peak> pkCandidates = patt2d.findPeakCandidates(delsig, minpix, estimBKG);
        log.debug("====> N initial candidates=" + pkCandidates.size());
        if (pkCandidates.size() <= 0)
            return null;
        if (pkCandidates.size() > maxPeakCandidates) {
            final boolean yes = FileUtils.YesNoDialog(null,
                    "Too many candidates, it will take long unless you increase the ESD factor, continue anyway?");
            if (!yes)
                return null;
        }

        //        if (pkCandidates.size()>0)return pkCandidates;

        //2n colapsem els pics on hi ha intensitat igual en pixels consecutius (i identifiquem els saturats?) i identifiquem veïns
        //(REALMENT LA SEGONA PASSADA ES PODRIA FER DUES VEGADES SI ES VEU QUE FALTA RANG...)

        //ordenem la llista de pics segons intensitat
        //        for (int i=0;i<pkCandidates.size();i++){
        //            System.out.println(String.format("%d x=%.1f y=%.1f ymax=%.2f",i, pkCandidates.get(i).getPixelCentre().x,pkCandidates.get(i).getPixelCentre().y,pkCandidates.get(i).getYmax()));    
        //        }
        Collections.sort(pkCandidates);
        //        for (int i=0;i<pkCandidates.size();i++){
        //            System.out.println(String.format("%d x=%.1f y=%.1f ymax=%.2f",i, pkCandidates.get(i).getPixelCentre().x,pkCandidates.get(i).getPixelCentre().y,pkCandidates.get(i).getYmax()));    
        //        }

        Iterator<Peak> itrpk = pkCandidates.iterator();
        while (itrpk.hasNext()) {
            log.fine(itrpk.next().toString());
        }
        ArrayList<Peak> toRemove = new ArrayList<Peak>();

        //now we deal with the saturated if any (estaran a dalt de tot de la llista)
        boolean finished = false;
        int nsatur = 0;
        int processedpks = 0; // afegit perque si nomes hi ha saturats a la llista es queda amb bucle infinit
        while (!finished) {
            final int pkinten = (int) pkCandidates.get(0).getYmax();
            if ((pkinten < patt2d.getSaturValue()) || (processedpks > pkCandidates.size())) {
                finished = true;
                continue;
            }
            //SI ESTEM AQUI ES QUE ES SATURAT, no cal mes comprovacio
            nsatur = nsatur + 1;

            final int x = (int) (pkCandidates.get(0).getPixelCentre().x);
            final int y = (int) (pkCandidates.get(0).getPixelCentre().y);

            if (D2Dplot_global.isDebug())
                log.fine("saturated peak " + x + " " + y);
            //busquem el centre de la saturacio dins un quadrat força gran 5*zoneRadius horitzontal 15*zoneRadius vertical...
            //millor busquem els limits nosaltres!!

            //cerca limit superior agafant uns zoneRadius * 5 pixels d'amplada
            final int amplada = zoneRadius * 5;
            boolean fin = false;
            int ncicles = 0;
            while (!fin) {
                fin = true;
                for (int i = x - amplada; i < x + amplada; i++) {
                    if (!patt2d.isInside(i, y - ncicles)) {//negatiu perque positiu es avall!!
                        fin = true;
                        continue;
                    }
                    final int intensity = patt2d.getInten(i, y - ncicles);
                    if (intensity >= patt2d.getSaturValue()) {
                        fin = false;
                    }
                }
                ncicles = ncicles + 1;
            }
            final int topLim = y - ncicles;
            //cerca limit inferior agafant uns zoneRadius * 5 pixels d'amplada
            fin = false;
            ncicles = 0;
            while (!fin) {
                fin = true;
                for (int i = x - amplada; i < x + amplada; i++) {
                    if (!patt2d.isInside(i, y + ncicles)) {
                        fin = true;
                        continue;
                    }
                    final int intensity = patt2d.getInten(i, y + ncicles);
                    if (intensity >= patt2d.getSaturValue()) {
                        fin = false;
                    }
                }
                ncicles = ncicles + 1;
            }
            final int botLim = y + ncicles;

            //cerca limit esquerra agafant uns zoneRadius * 5 pixels d'amplada
            fin = false;
            ncicles = 0;
            while (!fin) {
                fin = true;
                for (int i = y - amplada; i < y + amplada; i++) {
                    if (!patt2d.isInside(i, x - ncicles)) {
                        fin = true;
                        continue;
                    }
                    final int intensity = patt2d.getInten(x - ncicles, i);
                    if (intensity >= patt2d.getSaturValue()) {
                        fin = false;
                    }
                }
                ncicles = ncicles + 1;
            }
            final int leftLim = x - ncicles;

            fin = false;
            ncicles = 0;
            while (!fin) {  //&& ncicles<100
                fin = true;
                for (int i = y - amplada; i < y + amplada; i++) {
                    if (!patt2d.isInside(i, x + ncicles)) {
                        fin = true;
                        continue;
                    }
                    final int intensity = patt2d.getInten(x + ncicles, i);
                    if (intensity >= patt2d.getSaturValue()) {
                        fin = false;
                    }
                }
                ncicles = ncicles + 1;
            }
            final int rightLim = x + ncicles;

            //ara tots els de dins el rectangle els eliminem i corregim l'actual amb les coordenades centrades
            //            Rectangle2D.Float rect = new Rectangle2D.Float(leftLim, topLim, rightLim-leftLim, botLim-topLim);
            //PROVO d'Incrementar-ho perque tecnicament hauriem de considerar zoneRadius... i aixi treure espuris
            final Rectangle2D.Float rect = new Rectangle2D.Float(leftLim - zoneRadius, topLim - zoneRadius,
                    rightLim - leftLim + 2 * zoneRadius, botLim - topLim + 2 * zoneRadius);
            if (D2Dplot_global.isDebug())
                log.fine("rectangle " + leftLim + " " + topLim + " " + (rightLim - leftLim) + " " + (botLim - topLim));

            itrpk = pkCandidates.iterator();
            while (itrpk.hasNext()) {
                if (rect.contains(itrpk.next().getPixelCentre())) {
                    itrpk.remove();
                    //                    log.debug("pkCandidates="+pkCandidates.size());
                }
            }
            final Peak newPk = new Peak((float) rect.getCenterX(), (float) rect.getCenterY());
            newPk.setYmax(patt2d.getSaturValue()); //el torno a posar saturat pero com que estara al final de la llista no afectara
            if (D2Dplot_global.isDebug())
                log.fine("substituted by " + (float) rect.getCenterX() + " " + (float) rect.getCenterY());

            processedpks = processedpks + 1;
            pkCandidates.add(newPk);
        }

        log.debug("nsatur condensed peaks=" + nsatur);

        //tornem a ordenar, m'interessa els mes intensos a dalt (i ara els satur estan a sota de tot)
        //        for (int i=0;i<pkCandidates.size();i++){
        //            System.out.println(String.format("%d x=%.1f y=%.1f ymax=%.2f",i, pkCandidates.get(i).getPixelCentre().x,pkCandidates.get(i).getPixelCentre().y,pkCandidates.get(i).getYmax()));    
        //        }
        Collections.sort(pkCandidates);
        //        for (int i=0;i<pkCandidates.size();i++){
        //            System.out.println(String.format("%d x=%.1f y=%.1f ymax=%.2f",i, pkCandidates.get(i).getPixelCentre().x,pkCandidates.get(i).getPixelCentre().y,pkCandidates.get(i).getYmax()));    
        //        }

        log.debug("====> N candidates after saturated processing=" + pkCandidates.size());

        //ara ja no tractarem amb saturats sino que anirem de mes intens a menys intens per ordre
        toRemove = new ArrayList<Peak>();
        final ArrayList<Peak> toAdd = new ArrayList<Peak>();
        finished = false;
        while (!finished) {
            if (pkCandidates.isEmpty()) {
                finished = true;
                continue;
            }
            final Peak pk = pkCandidates.get(0);
            final int x = (int) (pk.getPixelCentre().x);
            final int y = (int) (pk.getPixelCentre().y);
            int pkinten = patt2d.getInten(x, y);
            int mxinten = pkinten;
            float mxX = pk.getPixelCentre().x;
            float mxY = pk.getPixelCentre().y;

            //NOU 170616 ponderacio
            float sumX = pk.getPixelCentre().x * pkinten;
            float sumY = pk.getPixelCentre().y * pkinten;

            pkCandidates.remove(pk); //l'eliminem

            final Ellipse2D.Float elli = new Ellipse2D.Float(x - zoneRadius, y - zoneRadius, zoneRadius * 2,
                    zoneRadius * 2);
            int niguals = 1;

            for (int i = 0; i < pkCandidates.size(); i++) { //que passa amb l'ultim pic? s'ha d'afegir tal cual
                final Peak pk2 = pkCandidates.get(i);
                if (pk.equals(pk2))
                    continue; //no hauria de passar ja que l'he eliminat...
                final int pk2inten = patt2d.getInten(Math.round(pk2.getPixelCentre().x),
                        Math.round(pk2.getPixelCentre().y));
                if (elli.contains(pk2.getPixelCentre())) {
                    //ponderem posició
                    sumX = sumX + (pk2.getPixelCentre().x * pk2inten);
                    sumY = sumY + (pk2.getPixelCentre().y * pk2inten);
                    if (pk2inten > mxinten) {
                        mxinten = pk2inten;
                        mxX = pk2.getPixelCentre().x;
                        mxY = pk2.getPixelCentre().y;
                    }
                    pkinten = pkinten + pk2inten;
                    niguals = niguals + 1;
                    toRemove.add(pk2); //l'eliminarem al sortir del bucle
                    if (D2Dplot_global.isDebug())
                        log.writeNameNums("fine", true, "removed candidate (x,y)=", pk2.getPixelCentre().x,
                                pk2.getPixelCentre().y);
                    continue;
                }
            }

            pkCandidates.removeAll(toRemove); //eliminem els que ja hem considerat

            if (niguals > 1) {//hi ha hagut merging
                //afegim el pic "suma"
                //                float xf = sumX/niguals;
                //                float yf = sumY/niguals;
                float xf = mxX;
                float yf = mxY;
                if (pond) {
                    xf = sumX / pkinten;
                    yf = sumY / pkinten;
                }
                if (roundToInt) {
                    xf = FastMath.round(xf);
                    yf = FastMath.round(yf);
                }
                toAdd.add(new Peak(new Point2D.Float(xf, yf)));
                if (D2Dplot_global.isDebug()) {
                    log.writeNameNums("fine", true, "removed candidate (x,y)=", pk.getPixelCentre().x,
                            pk.getPixelCentre().y);
                    log.writeNameNums("fine", true, "added new candidate (from merging) (x,y)=", xf, yf);
                }
            } else {
                //hem de tornar a afegir el pic que hem eliminat!
                toAdd.add(new Peak(new Point2D.Float(pk.getPixelCentre().x, pk.getPixelCentre().y)));
            }

        }
        if (!toAdd.isEmpty())
            pkCandidates.addAll(toAdd);

        log.debug("====> N candidates after peak merge zone=" + pkCandidates.size());

        //        if (pkCandidates.size()>0)return pkCandidates;

        //---- tercera passada busquem pics aprop dels altres (tambe col·lapsem) pero considerem eixam i ens quedem amb el major maxim.
        //tambe identifiquem pics propers a mascara
        final ArrayList<Peak> realPeaks = new ArrayList<Peak>();
        finished = false;
        final float llindarGeneral = patt2d.getMeanI() + (delsig / 2) * patt2d.getSdevI();
        log.debug("llindarGeneral=" + llindarGeneral);
        while (!finished) {
            if (pkCandidates.isEmpty()) {
                finished = true;
                continue;
            }
            final Point2D.Float pk = pkCandidates.get(0).getPixelCentre();
            int x = (int) (pk.x);
            int y = (int) (pk.y);

            float bestX = pk.x;
            float bestY = pk.y;
            int maxInten = patt2d.getInten(x, y);
            int nEixam = 1;

            pkCandidates.remove(pkCandidates.get(0)); //l'eliminem
            toRemove = new ArrayList<Peak>();

            final Ellipse2D.Float elli = new Ellipse2D.Float(x - zoneRadius, y - zoneRadius, zoneRadius * 2,
                    zoneRadius * 2);
            //PROVEM DE FER-HO AMB ZONA ARC
            //tenim tolerancia radial en pixels
            final int intRadPixels = zoneRadius; //ho passarem a tol2t
            //i la tolerancia de l'arc (longitudinal)
            final int intLonPixels = zoneRadius * 2;  //ho passarem a angdeg

            //vector centre-pixel
            float vPCx = x - patt2d.getCentrX();
            float vPCy = patt2d.getCentrY() - y;

            //vector unitari
            final float modul = (float) FastMath.sqrt(vPCx * vPCx + vPCy * vPCy);
            vPCx = vPCx / modul;
            vPCy = vPCy / modul;

            float pixelInX = vPCx * (modul - intRadPixels / 2.f); //in=intern
            float pixelInY = vPCy * (modul - intRadPixels / 2.f);
            float pixelExX = vPCx * (modul + intRadPixels / 2.f); //ex=extern
            float pixelExY = vPCy * (modul + intRadPixels / 2.f);

            pixelInX = patt2d.getCentrX() + pixelInX;
            pixelInY = patt2d.getCentrY() - pixelInY;
            pixelExX = patt2d.getCentrX() + pixelExX;
            pixelExY = patt2d.getCentrY() - pixelExY;

            final float tol2t = (float) (patt2d.calc2T(pixelExX, pixelExY, true)
                    - patt2d.calc2T(pixelInX, pixelInY, true));

            float angleDeg = (float) FastMath.atan(intLonPixels / modul);
            angleDeg = (float) FastMath.toDegrees(angleDeg);

            //limits tth i azim
            final EllipsePars ellip = getElliPars(patt2d, new Point2D.Float(x, y));
            final float azimAngle = patt2d.getAzimAngle(x, y, true); //azimut des del zero
            float azimMax = azimAngle + angleDeg / 2;
            final float azimMin = azimAngle - angleDeg / 2;
            if (azimMax < azimMin) {
                //vol dir que passem pel zero
                azimMax = azimMax + 360;
            }
            //calclulem be la pixelTolerance, quants pixels hi ha en aquest increment de 2theta (no cal que sigui exacte) i realment s'hauria de fer servir la meitat
            final float step = patt2d.calcMinStepsizeBy2Theta4Directions();
            final float pixelTolerance = tol2t / (step / 1.5f); //no faig servir la meitat per precaucio

            final ArrayList<Point2D.Float> pixelsArc = ellip.getEllipsePoints(azimMin, azimMax, 1);
            float t2p = (float) patt2d.calc2T(x, y, true);
            final float t2max = (float) FastMath.min(t2p + tol2t / 2., patt2d.getMax2TdegCircle());
            final float t2min = (float) FastMath.max(0.1, t2p - tol2t / 2.);

            //busquem el maxX, minX, maxY, minY de l'arc
            final float[] xs = new float[pixelsArc.size()];
            final float[] ys = new float[pixelsArc.size()];
            final Iterator<Point2D.Float> itrp = pixelsArc.iterator();
            int n = 0;
            while (itrp.hasNext()) {
                final Point2D.Float p = itrp.next();
                xs[n] = p.x;
                ys[n] = p.y;
                n = n + 1;
            }
            int maxX = FastMath.round(findMax(xs) + pixelTolerance);
            int minX = FastMath.round(findMin(xs) - pixelTolerance);
            int maxY = FastMath.round(findMax(ys) + pixelTolerance);
            int minY = FastMath.round(findMin(ys) - pixelTolerance);
            maxX = FastMath.min(maxX, patt2d.getDimX() - 1);
            maxY = FastMath.min(maxY, patt2d.getDimY() - 1);
            minX = FastMath.max(minX, 0);
            minY = FastMath.max(minY, 0);

            for (int i = 0; i < pkCandidates.size(); i++) { //que passa amb l'ultim pic? s'ha d'afegir tal cual
                final Peak pk2 = pkCandidates.get(i);
                if (pk.equals(pk2.getPixelCentre()))
                    continue; //no hauria de passar ja que l'he eliminat...
                if (!(pk2.getPixelCentre().x > minX && pk2.getPixelCentre().x < maxX && pk2.getPixelCentre().y > minY
                        && pk2.getPixelCentre().y < maxY)) {
                    continue;
                }
                final int pk2inten = patt2d.getInten(Math.round(pk2.getPixelCentre().x),
                        Math.round(pk2.getPixelCentre().y));

                //el busquem a dins de la zona
                //ara ja tenim el quadrat on hem de buscar
                //                int npix = 0;
                boolean trobat = false;
                for (int j = minY; j <= maxY; j++) {
                    for (int k = minX; k <= maxX; k++) {
                        //si esta fora la imatge o es mascara el saltem
                        if (!patt2d.isInside(k, j))
                            continue;
                        if (patt2d.isExcluded(k, j))
                            continue;
                        t2p = (float) patt2d.calc2T(k, j, true);
                        if ((t2p < t2min) || (t2p > t2max))
                            continue;
                        if (azimMax > 360) {
                            if ((azimAngle < azimMin) && ((azimAngle + 360) > azimMax))
                                continue;
                        } else {//cas normal
                            if ((azimAngle > azimMax) || (azimAngle < azimMin))
                                continue;
                        }
                        //siarribem aqui estem dins la zona arc, mirem si coincideix el pkCandidates
                        if ((int) pk2.getPixelCentre().x == k && (int) pk2.getPixelCentre().y == j) {
                            if (pk2inten > maxInten) {
                                //aquest es mes intens, ens el quedem
                                bestX = pk2.getPixelCentre().x;
                                bestY = pk2.getPixelCentre().y;
                                maxInten = pk2inten;

                                nEixam = nEixam + 1;
                                toRemove.add(pkCandidates.get(i)); //l'eliminarem al sortir del bucle
                                if (D2Dplot_global.isDebug())
                                    log.fine("eixam found");
                                trobat = true;
                                break;
                            }

                            //si el pic trobat aprop es molt intens ens el quedem apart

                            if (pk2inten > llindarGeneral) { //llindar general
                                trobat = true;
                                break;
                            } else { //l'afegim com a eixam
                                nEixam = nEixam + 1;
                                toRemove.add(pkCandidates.get(i)); //l'eliminarem al sortir del bucle
                                if (D2Dplot_global.isDebug())
                                    log.fine("eixam found");
                                trobat = true;
                                break;
                            }
                        }
                    }
                    if (trobat)
                        break;
                }
            }

            pkCandidates.removeAll(toRemove); //eliminem els que ja hem considerat

            //afegim el pic
            final Peak pic = new Peak(bestX, bestY);
            pic.setnVeinsEixam(nEixam);

            //mirem ara els saturats i si està prop d'una mascara
            x = (int) (pic.getPixelCentre().x);
            y = (int) (pic.getPixelCentre().y);

            for (int j = y - zoneRadius; j < y + (zoneRadius * 2); j++) {
                for (int i = x - zoneRadius; i < x + (zoneRadius * 2); i++) {
                    if (elli.contains(i, j)) {
                        if (!patt2d.isInside(i, j)) {
                            pic.setNearMask(true);
                            continue;
                        }
                        if (patt2d.isExcluded(i, j)) {
                            pic.setNearMask(true);
                            continue;
                        }
                        if (patt2d.getInten(i, j) >= (patt2d.getSaturValue() - 1)) {
                            pic.setnSatur(pic.getnSatur() + 1);
                        }
                    }
                }
            }

            //TEST de determinar una mica millor el màxim amb els 8 veins ---> FUNCIONA PERO HO PROVARE AMB MES VEINS PER TAMBE MIRAR EL FONS ... no, ho fare a l'integrar millor...
            int sumaX = 0;
            int sumaY = 0;
            int sumaInt = 0;
            for (int ii = y - 1; ii < y + 2; ii++) {
                for (int jj = x - 1; jj < x + 2; jj++) {
                    if (patt2d.isExcluded(jj, ii))
                        continue;
                    sumaX = sumaX + patt2d.getInten(jj, ii) * jj;
                    sumaY = sumaY + patt2d.getInten(jj, ii) * ii;
                    sumaInt = sumaInt + patt2d.getInten(jj, ii);
                }
            }
            final float xpond = (float) sumaX / (float) sumaInt;
            final float ypond = (float) sumaY / (float) sumaInt;
            pic.getPixelCentre().x = xpond;
            pic.getPixelCentre().y = ypond;

            //ja hem acabat!!

            realPeaks.add(pic);
            log.writeNameNums("CONFIG", true, "peak added (NUM,x,y,inten,neixam,nsatur)", realPeaks.size() - 1,
                    pic.getPixelCentre().x, pic.getPixelCentre().y, patt2d.getInten(x, y), pic.getnVeinsEixam(),
                    pic.getnSatur());
        }

        log.debug("====> N final peaks=" + realPeaks.size());

        return realPeaks;

    }

    //roundToInt fa que es guardin fent un round
    //HA d'eliminar els diamant i també els que estan a PROP dels diamant
    public static int removeDiamondPeaks(ArrayList<Peak> pks) {
        final ArrayList<Peak> toRemove = new ArrayList<Peak>();
        final ArrayList<Peak> dPeaks = new ArrayList<Peak>();
        Iterator<Peak> itrPk = pks.iterator();
        while (itrPk.hasNext()) {
            final Peak pk = itrPk.next();
            if (pk.isDiamond()) {
                dPeaks.add(pk);
            }
        }

        //borrem pics diamant si n'hi ha
        if (!dPeaks.isEmpty()) {
            pks.removeAll(dPeaks);
        } else {
            return 0;
        }

        //ara busquem els propers
        itrPk = pks.iterator();
        while (itrPk.hasNext()) {
            final Peak pk = itrPk.next();
            final Iterator<Peak> itrDpk = dPeaks.iterator();
            while (itrDpk.hasNext()) {
                final Peak dpeak = itrDpk.next();
                //                double dist = pk.getPixelCentre().distance(dpeak.getPixelCentre()); //in pixels
                //ARA HEM DE MIRAR SI la llista de pixels dpeak i pk tenen algun en comú.
                boolean trobat = false;
                for (final Pixel p1 : pk.getZona().getPixelList()) { //TODO:revisar si funciona, canvi Point2Dfloat per Pixel
                    if (dpeak.getZona().getPixelList().contains(p1)) {
                        trobat = true;
                        break;
                    }
                }
                if (trobat) {
                    toRemove.add(pk);
                    break;
                }
            }
        }

        if (!toRemove.isEmpty()) {
            pks.removeAll(toRemove);
            return toRemove.size() + dPeaks.size();
        }
        return dPeaks.size();
    }

    public static int removeSaturPeaks(ArrayList<Peak> pks) {
        final ArrayList<Peak> toRemove = new ArrayList<Peak>();
        final Iterator<Peak> itrPk = pks.iterator();
        while (itrPk.hasNext()) {
            final Peak pk = itrPk.next();
            if (pk.isSatur()) {
                toRemove.add(pk);
            }
        }
        if (!toRemove.isEmpty()) {
            pks.removeAll(toRemove);
            return toRemove.size();
        }
        return 0;
    }

    public static float findMax(float... vals) {
        float max = -99999999;

        for (final float d : vals) {
            if (d > max)
                max = d;
        }
        return max;
    }

    public static float findMin(float... vals) {
        float min = 99999999;

        for (final float d : vals) {
            if (d < min)
                min = d;
        }
        return min;
    }

    public static double findMin(double... vals) {
        double min = 99999999;

        for (final double d : vals) {
            if (d < min)
                min = d;
        }
        return min;
    }

    public static int getBkgIter() {
        return bkgIter;
    }

    public static void setBkgIter(int bkgIter) {
        ImgOps.bkgIter = bkgIter;
    }

    public static float getDspacingFromHKL(int h, int k, int l, float a, float b, float c, float alfaDeg, float betaDeg,
            float gammaDeg) {

        final double cosal = FastMath.cos(FastMath.toRadians(alfaDeg));
        final double cosbe = FastMath.cos(FastMath.toRadians(betaDeg));
        final double cosga = FastMath.cos(FastMath.toRadians(gammaDeg));
        final double sinal = FastMath.sin(FastMath.toRadians(alfaDeg));
        final double sinbe = FastMath.sin(FastMath.toRadians(betaDeg));
        final double singa = FastMath.sin(FastMath.toRadians(gammaDeg));

        final double s11 = b * b * c * c * sinal * sinal;
        final double s22 = a * a * c * c * sinbe * sinbe;
        final double s33 = a * a * b * b * sinbe * singa;
        final double s12 = a * b * c * c * (cosal * cosbe - cosga);
        final double s23 = a * a * b * c * (cosbe * cosga - cosal);
        final double s13 = a * b * b * c * (cosga * cosal - cosbe);

        final double insqrt = 1 - cosal * cosal - cosbe * cosbe - cosga * cosga + 2 * cosal * cosbe * cosga;
        final double vol = a * b * c * (FastMath.sqrt(insqrt)); //Ang3

        final double fact = s11 * h * h + s22 * k * k + s33 * l * l + 2 * s12 * h * k + 2 * s23 * k * l
                + 2 * s13 * h * l;
        final double invdsp2 = fact * (1 / (vol * vol));
        final double dsp = FastMath.sqrt(1 / invdsp2);

        return (float) dsp;
    }

    public static float getTol2TFromIntRad(Pattern2D patt2d, float px, float py, int intRadPixels) {

        //vector centre-pixel
        float vPCx = px - patt2d.getCentrX();
        float vPCy = patt2d.getCentrY() - py;

        //vector unitari
        final float modul = (float) FastMath.sqrt(vPCx * vPCx + vPCy * vPCy);
        vPCx = vPCx / modul;
        vPCy = vPCy / modul;

        float pixelInX = vPCx * (modul - intRadPixels / 2.f); //in=intern
        float pixelInY = vPCy * (modul - intRadPixels / 2.f);
        float pixelExX = vPCx * (modul + intRadPixels / 2.f); //ex=extern
        float pixelExY = vPCy * (modul + intRadPixels / 2.f);

        pixelInX = patt2d.getCentrX() + pixelInX;
        pixelInY = patt2d.getCentrY() - pixelInY;
        pixelExX = patt2d.getCentrX() + pixelExX;
        pixelExY = patt2d.getCentrY() - pixelExY;

        return (float) (patt2d.calc2T(pixelExX, pixelExY, true) - patt2d.calc2T(pixelInX, pixelInY, true));
    }

    //igual que el de sota pero donant la IntRad en pixels i no 2theta
    public static Patt2Dzone yArcTilt(Pattern2D patt2D, int px, int py, int intRadPixels, float azimApertureDeg,
            boolean self, int bkgpt, boolean debug) {

        final float tol2t = getTol2TFromIntRad(patt2D, px, py, intRadPixels);
        return YarcTilt(patt2D, px, py, tol2t, intRadPixels, azimApertureDeg, self, bkgpt, debug);
    }

    public static Patt2Dzone yArcTilt(Pattern2D patt2D, int px, int py, float tol2tDeg, float azimApertureDeg,
            boolean self, int bkgpt, boolean debug) {

        //calculem intRadPixels a partr de la 2theta per tenir-ne el valor
        final float t2i = (float) patt2D.calc2T(px, py, true);
        final float azim = patt2D.getAzimAngle(px, py, true);
        final Point2D.Float extern = patt2D.getPixelFromAzimutAnd2T(azim, t2i + (tol2tDeg / 2));
        final Point2D.Float intern = patt2D.getPixelFromAzimutAnd2T(azim, t2i - (tol2tDeg / 2));
        float sumX = 0.0f;
        float sumY = 0.0f;
        if ((extern != null) && (intern != null)) {
            sumX = extern.x - intern.x;
            sumY = extern.y - intern.y;
        } else {
            if (extern == null) {
                sumX = patt2D.getDimX() - intern.x;
                sumY = patt2D.getDimY() - intern.y;
            } else { //intern is null
                sumX = extern.x;
                sumY = extern.y;
            }
        }
        final int intRadPixels = FastMath.round((float) FastMath.sqrt(sumX * sumX + sumY * sumY));

        return YarcTilt(patt2D, px, py, tol2tDeg, intRadPixels, azimApertureDeg, self, bkgpt, debug);

    }

    //tol2theta = quina tolerancia en 2theta volem integrar, es el rang TOTAL (es fara tol2t/2 en cada direccio)
    //angle = angle TOTAL d'obertura de l'arc a integrar en GRAUS
    //consideraElTIlt
    private static Patt2Dzone YarcTilt(Pattern2D patt2D, int px, int py, float tol2tDeg, int intRadPixels,
            float azimApertureDeg, boolean self, int bkgpt, boolean debug) {
        //limits tth i azim
        final EllipsePars elli = getElliPars(patt2D, new Point2D.Float(px, py));
        float azimAngle = patt2D.getAzimAngle(px, py, true); //azimut des del zero
        float azimMax = azimAngle + azimApertureDeg / 2;
        final float azimMin = azimAngle - azimApertureDeg / 2;
        if (azimMax < azimMin) {
            //vol dir que passem pel zero
            azimMax = azimMax + 360;
        }
        //calclulem be la pixelTolerance, quants pixels hi ha en aquest increment de 2theta (no cal que sigui exacte) i realment s'hauria de fer servir la meitat
        final float step = patt2D.calcMinStepsizeBy2Theta4Directions();
        final float pixelTolerance = tol2tDeg / (step / 1.5f); //no faig servir la meitat per precaucio

        final ArrayList<Point2D.Float> pixelsArc = elli.getEllipsePoints(azimMin, azimMax, 1);
        float t2p = (float) patt2D.calc2T(px, py, true);
        final float t2max = (float) FastMath.min(t2p + tol2tDeg / 2., patt2D.getMax2TdegCircle());
        final float t2min = (float) FastMath.max(0.1, t2p - tol2tDeg / 2.);
        if (D2Dplot_global.isDebug()) {
            log.writeNameNumPairs("FINE", true, "t2p,t2max,t2min,pixelTolerance,step", t2p, t2max, t2min,
                    pixelTolerance, step);
            log.writeNameNumPairs("FINE", true, "azimAngle,azimMax,azimMin", azimAngle, azimMax, azimMin);
        }
        //busquem el maxX, minX, maxY, minY de l'arc
        final float[] xs = new float[pixelsArc.size()];
        final float[] ys = new float[pixelsArc.size()];
        final Iterator<Point2D.Float> itrp = pixelsArc.iterator();
        int n = 0;
        while (itrp.hasNext()) {
            final Point2D.Float p = itrp.next();
            xs[n] = p.x;
            ys[n] = p.y;
            n = n + 1;
        }
        int maxX = FastMath.round(findMax(xs) + pixelTolerance);
        int minX = FastMath.round(findMin(xs) - pixelTolerance);
        int maxY = FastMath.round(findMax(ys) + pixelTolerance);
        int minY = FastMath.round(findMin(ys) - pixelTolerance);
        maxX = FastMath.min(maxX, patt2D.getDimX() - 1);
        maxY = FastMath.min(maxY, patt2D.getDimY() - 1);
        minX = FastMath.max(minX, 0);
        minY = FastMath.max(minY, 0);
        if (D2Dplot_global.isDebug())
            log.writeNameNumPairs("FINE", true, "minX,maxX,minY,maxY", minX, maxX, minY, maxY);

        //iniciacions valors
        int npix = 0;
        int ysum = 0;
        int ymax = 0;
        int ymin = Integer.MAX_VALUE;
        final ArrayList<Integer> minint = new ArrayList<Integer>(bkgpt); //vector amb les bkgpt intensitats menors
        final ArrayList<Integer> intensitats = new ArrayList<Integer>(); //vector on guardarem les intensitats per calcular desv.estd
        float ymean = 0;
        float ybkg = 0;
        float ymeandesv = 0;
        float ybkgdesv = 0;
        float sumdesv = 0;
        int bkgsum = 0;
        for (int i = 0; i < bkgpt; i++) {
            minint.add(patt2D.getSaturValue());
        }

        final ArrayList<Pixel> pixelList = new ArrayList<Pixel>(); //TODO canvi point2D.float per Pixel

        //ara ja tenim el quadrat on hem de buscar
        for (int j = minY; j <= maxY; j++) {
            for (int i = minX; i <= maxX; i++) {
                //si esta fora la imatge o es mascara el saltem
                if (!patt2D.isInside(i, j))
                    continue;
                if (patt2D.isExcluded(i, j))
                    continue;
                //ell mateix?
                if ((i == px) && (j == py) && (!self))
                    continue;

                //comprovacions
                t2p = (float) patt2D.calc2T(i, j, true);
                azimAngle = patt2D.getAzimAngle(i, j, true); //azimut des del zero

                if ((t2p < t2min) || (t2p > t2max))
                    continue;
                if (azimMax > 360) {
                    if ((azimAngle < azimMin) && ((azimAngle + 360) > azimMax))
                        continue;
                } else {//cas normal
                    if ((azimAngle > azimMax) || (azimAngle < azimMin))
                        continue;
                }

                //si hem arribat aqu� es que hem se sumar la intensitat del pixel
                int inten = patt2D.getInten(i, j);
                ysum = ysum + inten;
                if (ymax < inten)
                    ymax = inten;
                if (ymin > inten)
                    ymin = inten;
                for (int k = 0; k < bkgpt; k++) {
                    //TODO:si val zero no considerem (s'hauria de reconsiderar...)
                    //if(patt2D.getIntenB2(i, j)==0)break;
                    if (inten < minint.get(k)) {
                        minint.set(k, inten);
                        break; //sortim del for, ja hem utilitzat la intensitat
                    }
                }

                npix = npix + 1;
                intensitats.add(inten);

                //afegit el 25/4/2017
                pixelList.add(patt2D.getPixel(i, j));

                //debug
                if (debug)
//                    patt2D.setInten(i, j, -1, false);
                    patt2D.getPixel(i, j).setExcluded(true);
            }
        }

        if (npix > 0) {
            //calcul desviacio estandar sqrt((sum(xi-xmean)^2)/N-1)
            ymean = (float) (ysum) / (float) (npix);
            sumdesv = 0;
            final Iterator<Integer> it = intensitats.iterator();
            while (it.hasNext()) {
                final int inten = it.next();
                sumdesv = sumdesv + ((inten) - ymean) * ((inten) - ymean);
            }
            if (npix < 2)
                npix = 2;
            ymeandesv = (float) FastMath.sqrt(sumdesv / (npix - 1));
            //calcul del valor de fons i la desviacio
            bkgsum = 0;
            final int nbkgpt = FastMath.min(bkgpt, npix);
            int neffectiveBkgPt = 0; //afegit 160920
            for (int i = 0; i < nbkgpt; i++) {
                if (minint.get(i) > ymean)
                    continue;
                bkgsum = bkgsum + minint.get(i);
                neffectiveBkgPt = neffectiveBkgPt + 1;
            }
            ybkg = (float) (bkgsum) / (float) (neffectiveBkgPt);
            sumdesv = 0;
            for (int i = 0; i < nbkgpt; i++) {
                if (minint.get(i) > ymean)
                    continue;
                sumdesv = sumdesv + ((float) (minint.get(i)) - ybkg) * ((float) (minint.get(i)) - ybkg);
            }
            ybkgdesv = (float) FastMath.sqrt(sumdesv / (neffectiveBkgPt - 1));
        }

        final Patt2Dzone pz = new Patt2Dzone(npix, ysum, ymax, ymean, ymeandesv, ybkg, ybkgdesv);
        pz.setYmin(ymin);//afegit Abril 2019 per això no està al constructor
        pz.setIntradPix(intRadPixels);
        pz.setAzimAngle(azimApertureDeg);
        pz.setBkgpt(bkgpt);
        pz.setCentralPoint(new Point2D.Float(px, py));
        pz.setPatt2d(patt2D);
        pz.setPixelList(pixelList);
        return pz;
    }

    public static int arcNPix(Pattern2D patt2D, int px, int py, int intRadPixels, float angleDeg) {
        //vector centre-pixel
        float vPCx = px - patt2D.getCentrX();
        float vPCy = patt2D.getCentrY() - py;

        //vector unitari
        final float modul = (float) FastMath.sqrt(vPCx * vPCx + vPCy * vPCy);
        vPCx = vPCx / modul;
        vPCy = vPCy / modul;

        float pixelInX = vPCx * (modul - intRadPixels / 2.f); //in=intern
        float pixelInY = vPCy * (modul - intRadPixels / 2.f);
        float pixelExX = vPCx * (modul + intRadPixels / 2.f); //ex=extern
        float pixelExY = vPCy * (modul + intRadPixels / 2.f);

        pixelInX = patt2D.getCentrX() + pixelInX;
        pixelInY = patt2D.getCentrY() - pixelInY;
        pixelExX = patt2D.getCentrX() + pixelExX;
        pixelExY = patt2D.getCentrY() - pixelExY;

        final float tol2t = (float) (patt2D.calc2T(pixelExX, pixelExY, true) - patt2D.calc2T(pixelInX, pixelInY, true));

        return arcNPix(patt2D, px, py, tol2t, angleDeg);

    }

    //RETORNA EL NOMBRE DE PICS EN UN ARC
    //tol2theta = quina tolerancia en 2theta volem integrar, es el rang TOTAL (es fara tol2t/2 en cada direccio)
    //angle = angle TOTAL d'obertura de l'arc a integrar en GRAUS
    //consideraElTIlt
    public static int arcNPix(Pattern2D patt2D, int px, int py, float tol2t, float angleDeg) {
        //limits tth i azim
        final EllipsePars elli = getElliPars(patt2D, new Point2D.Float(px, py));
        final float azimAngle = patt2D.getAzimAngle(px, py, true); //azimut des del zero
        float azimMax = azimAngle + angleDeg / 2;
        final float azimMin = azimAngle - angleDeg / 2;
        if (azimMax < azimMin) {
            //vol dir que passem pel zero
            azimMax = azimMax + 360;
        }
        //calclulem be la pixelTolerance, quants pixels hi ha en aquest increment de 2theta (no cal que sigui exacte) i realment s'hauria de fer servir la meitat
        final float step = patt2D.calcMinStepsizeBy2Theta4Directions();
        final float pixelTolerance = tol2t / (step / 1.5f); //no faig servir la meitat per precaucio

        final ArrayList<Point2D.Float> pixelsArc = elli.getEllipsePoints(azimMin, azimMax, 1);
        float t2p = (float) patt2D.calc2T(px, py, true);
        final float t2max = (float) FastMath.min(t2p + tol2t / 2., patt2D.getMax2TdegCircle());
        final float t2min = (float) FastMath.max(0.1, t2p - tol2t / 2.);

        //busquem el maxX, minX, maxY, minY de l'arc
        final float[] xs = new float[pixelsArc.size()];
        final float[] ys = new float[pixelsArc.size()];
        final Iterator<Point2D.Float> itrp = pixelsArc.iterator();
        int n = 0;
        while (itrp.hasNext()) {
            final Point2D.Float p = itrp.next();
            xs[n] = p.x;
            ys[n] = p.y;
            n = n + 1;
        }
        int maxX = FastMath.round(findMax(xs) + pixelTolerance);
        int minX = FastMath.round(findMin(xs) - pixelTolerance);
        int maxY = FastMath.round(findMax(ys) + pixelTolerance);
        int minY = FastMath.round(findMin(ys) - pixelTolerance);
        maxX = FastMath.min(maxX, patt2D.getDimX() - 1);
        maxY = FastMath.min(maxY, patt2D.getDimY() - 1);
        minX = FastMath.max(minX, 0);
        minY = FastMath.max(minY, 0);

        //ara ja tenim el quadrat on hem de buscar
        int npix = 0;
        for (int j = minY; j <= maxY; j++) {
            for (int i = minX; i <= maxX; i++) {
                //si esta fora la imatge o es mascara el saltem
                if (!patt2D.isInside(i, j))
                    continue;
                if (patt2D.isExcluded(i, j))
                    continue;
                t2p = (float) patt2D.calc2T(i, j, true);
                if ((t2p < t2min) || (t2p > t2max))
                    continue;
                if (azimMax > 360) {
                    if ((azimAngle < azimMin) && ((azimAngle + 360) > azimMax))
                        continue;
                } else {//cas normal
                    if ((azimAngle > azimMax) || (azimAngle < azimMin))
                        continue;
                }
                //siarribem aqui el sumem
                npix = npix + 1;
            }
        }
        return npix;
    }

    public static float azimAngleOfAPeak(Pattern2D patt2D, float px, float py) {
        final int ipx = (int) (px);
        final int ipy = (int) (py);
        final EllipsePars elli = getElliPars(patt2D, new Point2D.Float(px, py));
        final float azimAngle = patt2D.getAzimAngle(ipx, ipy, true); //azimut des del zero
        final Point2D.Float central = elli.getEllipsePoint(azimAngle);

        if (D2Dplot_global.isDebug())
            log.fine(String.format("haurien de coincidir px,py= %f,%f entrats amb CentralSegonsAzimut %f,%f", px, py,
                    central.x, central.y));

        //farem com amb intrad
        int maxPixels = 150;
        float halfleft = 0;
        float halfright = 0;

        //ara anirem mirant pixel per pixel en el radi la desviacio de la intensitat
        //primer cap a l'interior:
        final int centralPixelIntensity = patt2D.getInten(ipx, ipy);
        int previousIntensity = centralPixelIntensity;
        int minI = centralPixelIntensity;
        int previousX = ipx;
        int previousY = ipy;
        int pixcount = 1;
        int countdown = 0;
        final float azimStep = 0.02f; //revisar el valor o calcular perque sigui l'equivalent a aprox. 1pixel en angles alts
        float currentAzimut = azimAngle;
        boolean finished = false;
        while (!finished) {
            if (D2Dplot_global.isDebug())
                log.fine("countLeft=" + pixcount + " currentAzim=" + currentAzimut);

            currentAzimut = currentAzimut + azimStep;
            final Point2D.Float newPoint = elli.getEllipsePoint(currentAzimut);

            final int npx = (int) (newPoint.x);//new pixel x
            final int npy = (int) (newPoint.y);

            if ((npx == previousX) && (npy == previousY)) {
                continue;
            }

            final int inten = patt2D.getInten(npx, npy);

            if (inten >= patt2D.getSaturValue()) {
                continue;
            }

            if (inten < previousIntensity) {
                if (inten < minI) {
                    //anem baixant encara
                    minI = inten;
                } else {
                    countdown = countdown + 1; //penalitzem
                }
            } else {//intensitat es major a l'anterior
                countdown = countdown + 1; //AIXO POTSER HO HAURIA DE DESACTIVAR PEL TEMA DELS MOSAICS
            }

            previousIntensity = inten;
            pixcount = pixcount + 1;
            maxPixels = maxPixels - 1;
            previousX = npx;
            previousY = npy;

            if (maxPixels < 0)
                break;

            if (countdown >= 3) {
                finished = true;
            }
        }
        halfleft = currentAzimut - azimAngle;

        //ara cap a l'exterior:
        previousIntensity = centralPixelIntensity;
        minI = centralPixelIntensity;
        previousX = ipx;
        previousY = ipy;
        pixcount = 1;
        countdown = 0;
        currentAzimut = azimAngle;
        finished = false;
        while (!finished) {
            if (D2Dplot_global.isDebug())
                log.fine("countRight=" + pixcount + " currentAzim=" + currentAzimut);

            currentAzimut = currentAzimut - azimStep;
            final Point2D.Float newPoint = elli.getEllipsePoint(currentAzimut);

            final int npx = (int) (newPoint.x);//new pixel x
            final int npy = (int) (newPoint.y);

            if ((npx == previousX) && (npy == previousY)) {
                continue;
            }

            final int inten = patt2D.getInten(npx, npy);

            if (inten >= patt2D.getSaturValue()) {
                continue;
            }

            if (inten < previousIntensity) {
                if (inten < minI) {
                    //anem baixant encara
                    minI = inten;
                } else {
                    countdown = countdown + 1; //penalitzem
                }
            } else {//intensitat es major a l'anterior
                countdown = countdown + 1; //AIXO POTSER HO HAURIA DE DESACTIVAR PEL TEMA DELS MOSAICS
            }

            previousIntensity = inten;
            pixcount = pixcount + 1;
            maxPixels = maxPixels - 1;
            previousX = npx;
            previousY = npy;

            if (maxPixels < 0)
                break;

            if (countdown >= 3) {
                finished = true;
            }
        }
        halfright = azimAngle - currentAzimut;

        return 2 * FastMath.max(halfleft, halfright);
    }

    public static int intRadPixelsOfAPeak(Pattern2D patt2D, float px, float py) {
        //vector centre-pixel
        float vPCx = px - patt2D.getCentrX();
        float vPCy = patt2D.getCentrY() - py;

        //vector unitari
        final float modul = (float) FastMath.sqrt(vPCx * vPCx + vPCy * vPCy);
        vPCx = vPCx / modul;
        vPCy = vPCy / modul;

        int maxPixels = 30;
        int halfinterna = 0;
        int halfexterna = 0;

        //ara anirem mirant pixel per pixel en el radi la desviacio de la intensitat
        //primer cap a l'interior:
        int centralPixelIntensity = patt2D.getInten((int) (px), (int) (py));
        int previousIntensity = centralPixelIntensity;
        int minI = centralPixelIntensity;
        int previousX = (int) (px);
        int previousY = (int) (py);
        int pixcount = 1;
        int passades = 1;
        int countdown = 0;
        boolean finished = false;
        while (!finished) {
            if (D2Dplot_global.isDebug())
                log.fine("count=" + pixcount + " passades=" + passades);
            final float fpx = (int) (vPCx * (modul - passades)); //new pixel x float
            final float fpy = (int) (vPCy * (modul - passades));

            final int npx = (int) (patt2D.getCentrX() + fpx);//new pixel x
            final int npy = (int) (patt2D.getCentrY() - fpy);

            if ((npx == previousX) && (npy == previousY)) {
                passades = passades + 1;
                continue;
            }

            final int inten = patt2D.getInten(npx, npy);

            if (inten >= patt2D.getSaturValue()) {
                passades = passades + 1;
                continue;
            } //els saturats no els considerem

            if (inten < previousIntensity) {
                if (inten < minI) {
                    //anem baixant encara
                    minI = inten;
                } else {
                    countdown = countdown + 1; //penalitzem
                }
            } else {//intensitat es major a l'anterior
                countdown = countdown + 1;
            }

            previousIntensity = inten;
            pixcount = pixcount + 1;
            passades = passades + 1;
            maxPixels = maxPixels - 1;
            previousX = npx;
            previousY = npy;

            if (maxPixels < 0)
                break;

            if (countdown >= 3) {
                finished = true;
            }
        }
        halfinterna = pixcount;

        //ara cap a l'exterior:
        centralPixelIntensity = patt2D.getInten((int) (px), (int) (py));
        previousIntensity = centralPixelIntensity;
        minI = centralPixelIntensity;
        previousX = (int) (px);
        previousY = (int) (py);
        pixcount = 1;
        passades = 1;
        countdown = 0;
        finished = false;
        while (!finished) {
            final float fpx = (int) (vPCx * (modul - passades)); //new pixel x float
            final float fpy = (int) (vPCy * (modul - passades));

            final int npx = (int) (patt2D.getCentrX() + fpx);//new pixel x
            final int npy = (int) (patt2D.getCentrY() - fpy);

            if ((npx == previousX) && (npy == previousY)) {
                passades = passades + 1;
                continue;
            }

            final int inten = patt2D.getInten(npx, npy);

            if (inten >= patt2D.getSaturValue()) {
                passades = passades + 1;
                continue;
            }

            if (inten < previousIntensity) {
                if (inten < minI) {
                    //anem baixant encara
                    minI = inten;
                } else {
                    countdown = countdown + 1; //penalitzem
                }
            } else {//intensitat es major a l'anterior
                countdown = countdown + 1;
            }

            previousIntensity = inten;
            pixcount = pixcount + 1;
            passades = passades + 1;
            maxPixels = maxPixels - 1;
            previousX = npx;
            previousY = npy;

            if (maxPixels < 0)
                break;

            if (countdown >= 3) {
                finished = true;
            }
        }
        halfexterna = pixcount;

        return 2 * FastMath.max(halfinterna, halfexterna);
    }

    //Using the full image
    public static Pattern1D radialIntegration(Pattern2D patt2D, double t2ini, double t2fin, double step, double cakeIn,
            double cakeOut, boolean corrLP, boolean corrIAng, double subadu) {
        return ImgOps.radialIntegration(patt2D, 0, patt2D.getDimY(), 0, patt2D.getDimX(), t2ini, t2fin, step, cakeIn,
                cakeOut, corrLP, corrIAng, subadu);
    }

    //FULL SUBROUTINE
    private static Pattern1D radialIntegration(Pattern2D patt2D, int rowIni, int rowFin, int colIni, int colFin,
            double t2ini, double t2fin, double step, double cakeIn, double cakeOut, boolean corrLP, boolean corrIAng,
            double subadu) {
        //comprovacions previes
        if (step < 0) {
            step = patt2D.calcMinStepsizeBy2Theta4Directions();
        }
        if (cakeIn < 0) {
            cakeIn = 0.0;
        }
        if (cakeOut < 0) {
            cakeOut = 360.0;
        }
        boolean fullCake = false;
        if (cakeIn == 0.0 && cakeOut == 360.0) {
            fullCake = true;
        }

        t2ini = FastMath.max(0, t2ini - step / 2.);
        log.debug("step =" + step + " t2ini=" + t2ini + " t2fin=" + t2fin);
        final Pattern1D out = new Pattern1D(t2ini, t2fin, step);

        //ara anirem pixel a pixel i mirarem la 2theta a la qual correspon
        for (int j = rowIni; j < rowFin; j++) { // per cada fila (Y)
            for (int i = colIni; i < colFin; i++) { // per cada columna (X)
                //mask o zero el descartem
                if (patt2D.isExcluded(i, j))
                    continue;

                //HEM DE MIRAR SI EL VECTOR ESTA DINTRE EL CAKE
                if (!fullCake) {
                    final float azim = patt2D.getAzimAngle(i, j, true);
                    //debug
                    //	                if(i==patt2D.getCentrXI() && j==patt2D.getCentrYI()-100)log.fine("x,y,azim="+i+","+j+","+azim);
                    //	                if(i==patt2D.getCentrXI() && j==patt2D.getCentrYI()+100)log.fine("x,y,azim="+i+","+j+","+azim);
                    //	                if(i==patt2D.getCentrXI()+100 && j==patt2D.getCentrYI())log.fine("x,y,azim="+i+","+j+","+azim);
                    //	                if(i==patt2D.getCentrXI()-100 && j==patt2D.getCentrYI())log.fine("x,y,azim="+i+","+j+","+azim);

                    if (cakeOut < cakeIn) {
                        //va al reves
                        if ((azim < cakeIn) && (azim > cakeOut))
                            continue;
                    } else {
                        if ((azim < cakeIn) || (azim > cakeOut))
                            continue;
                    }
                }
                //2theta del pixel en la imatge
                final double t2p = patt2D.calc2T(new Point2D.Float(i, j), true);

                if (t2p < t2ini || t2p > t2fin)
                    continue;

                //position to the vector
                final int p = (int) (t2p / step - t2ini / step);
                float facIAng = 1;
                if (corrIAng)
                    facIAng = ImgOps.corrIncidentAngle(patt2D, i, j)[1];
                final float inten = (float) (patt2D.getInten(i, j) * patt2D.getScale() * facIAng + subadu);
                out.sumPoint(p, FastMath.round(inten), 1, patt2D.getPixel(i, j).getErr());
            }
        }

        //Calculs finals
        final Iterator<PointPatt1D> it = out.getPoints().iterator();
        while (it.hasNext()) {
            final PointPatt1D punt = it.next();
            //correction of t2, e.g. 0 it is really 0+(step/2) to be at the center of the bin
            punt.setT2(punt.getT2() + (out.step / 2f));
            if (punt.getNpix() <= 0) {
                punt.setIntensity(0);
                continue;
            }
            //lorentz correction
            if (corrLP) {
                double lorfact = FastMath.pow(FastMath.cos(FastMath.toRadians(punt.getT2())), 3);
                lorfact = 1 / lorfact;
                punt.setIntensity((punt.getCounts() * lorfact) / punt.getNpix());
            } else {
                punt.setIntensity((float) punt.getCounts() / punt.getNpix());
            }
            //esd
            punt.setDesv(FastMath.sqrt(punt.getDesv() / punt.getNpix())); //si no faig l'arrel es gran en relacio a la intensitat normalitzada
        }
        return out;
    }

    //Retorna el promig d'intensitat d'un anell centrat en t2 i considerant una amplada tol2t
    //considera tilt/rot
    public static float radialIntegrationSingle2th(Pattern2D patt2D, float t2, float tol2tdeg, boolean corrLP,
            boolean corrIAng) {
        if (tol2tdeg < 0) { //default value
            tol2tdeg = 0.1f;
        }
        final float step = patt2D.calcMinStepsizeBy2Theta4Directions();
        float sum = 0; //valor de intensitat suma
        int npix = 0;

        if (step > tol2tdeg) {
            return -1;
        }
        final float t2ini = t2 - tol2tdeg;
        final float t2fin = t2 + tol2tdeg;

        //ara anirem pixel a pixel i mirarem la 2theta a la qual correspon
        for (int j = 0; j < patt2D.getDimY(); j++) { // per cada fila (Y)
            for (int i = 0; i < patt2D.getDimX(); i++) { // per cada columna (X)
                //mask o zero el descartem
                if (patt2D.isExcluded(i, j))
                    continue;
                final double t2p = patt2D.calc2T(new Point2D.Float(i, j), true);
                if (t2p < t2ini || t2p > t2fin)
                    continue;
                double facLor = 1;
                double facPol = 1;
                float facIAng = 1;
                if (corrLP) {
                    final double[] facLP = ImgOps.corrLP(patt2D, i, j, 1, 2, 2, false);
                    facLor = facLP[1];
                    facPol = facLP[2];
                }
                if (corrIAng)
                    facIAng = ImgOps.corrIncidentAngle(patt2D, i, j)[1];
                final float inten = (float) (patt2D.getInten(i, j) * patt2D.getScale() * facLor * facPol * facIAng);
                sum = sum + inten;
                npix = npix + 1;
            }
        }
        //normalitzem pel nombre de pixels
        sum = sum / npix;
        return sum;
    }

    //Retorna els promitjos d'intensitat d'anells centrats en diverses t2[] i considerant una amplada tol2t
    //considera tilt/rot. Aixo es per evitar fer moltes passades si es volen varies t2.
    public static float[] radialIntegrationVarious2th(Pattern2D patt2D, float[] t2, float tol2tdeg, boolean corrLP,
            boolean corrIAng, boolean normNpix, SearchDBWorker sw) {
        if (tol2tdeg < 0) { //default value
            tol2tdeg = 0.1f;
        }
        final float step = patt2D.calcMinStepsizeBy2Theta4Directions();
        if (step > tol2tdeg) {
            return null;
        }

        final float[] sum = new float[t2.length]; //valor de intensitat suma
        final int[] npix = new int[t2.length];
        final float[] t2ini = new float[t2.length];
        final float[] t2fin = new float[t2.length];

        //establim 2tini/fin i inicialitzem sum, npix
        for (int i = 0; i < t2.length; i++) {
            t2ini[i] = t2[i] - tol2tdeg;
            t2fin[i] = t2[i] + tol2tdeg;
            sum[i] = 0;
            npix[i] = 0;
        }

        //ara anirem pixel a pixel i mirarem la 2theta a la qual correspon
        for (int j = 0; j < patt2D.getDimY(); j++) { // per cada fila (Y)
            for (int i = 0; i < patt2D.getDimX(); i++) { // per cada columna (X)
                //mask o zero el descartem
                if (patt2D.isExcluded(i, j))
                    continue;
                final double t2p = patt2D.calc2T(new Point2D.Float(i, j), true);

                //mirem si està dins d'algun rang dels buscats
                int npeak = -1;
                for (int k = 0; k < t2.length; k++) {
                    if (t2p > t2ini[k] && t2p < t2fin[k]) {
                        npeak = k;
                    }
                }
                if (npeak < 0)
                    continue; //no esta a cap rang

                double facLor = 1;
                double facPol = 1;
                float facIAng = 1;
                if (corrLP) {
                    final double[] facLP = ImgOps.corrLP(patt2D, i, j, 1, 2, 2, false);
                    facLor = facLP[1];
                    facPol = facLP[2];
                }
                if (corrIAng)
                    facIAng = ImgOps.corrIncidentAngle(patt2D, i, j)[1];
                final float inten = (float) (patt2D.getInten(i, j) * patt2D.getScale() * facLor * facPol * facIAng);
                sum[npeak] = sum[npeak] + inten;
                npix[npeak] = npix[npeak] + 1;

                //en cas que hi hagi un progres
                if (sw != null) {
                    if ((j % 100) == 0) {
                        final float percent = ((float) j / (float) patt2D.getDimY()) * 100.f;
                        sw.mySetProgress((int) percent);
                        log.debug(String.valueOf(percent));
                    }
                }
            }
        }
        if (normNpix) {
            //normalitzem pel nombre de pixels
            for (int i = 0; i < t2.length; i++) {
                if (sum[i] == 0)
                    continue;
                if (npix[i] == 0)
                    continue;
                sum[i] = sum[i] / npix[i];
            }
        }
        return sum;
    }

    //string is X, Y or Z
    //retorna la matriu de rotacio respecte un eix donat
    public static RealMatrix rotMatrix(double angleRad, String axis) {

        final double[][] mat = new double[3][3];

        //	    VavaLogger.writeNameNums("CONFIG", true, "anglerad", angleRad);
        final double cos = FastMath.cos(angleRad);
        final double sin = FastMath.sin(angleRad);

        if (axis.equalsIgnoreCase("X")) {
            mat[0][0] = 1.0;
            mat[0][1] = 0.0;
            mat[0][2] = 0.0;
            mat[1][0] = 0.0;
            mat[1][1] = cos;
            mat[1][2] = -sin;
            mat[2][0] = 0.0;
            mat[2][1] = sin;
            mat[2][2] = cos;
        }
        if (axis.equalsIgnoreCase("Y")) {
            mat[0][0] = cos;
            mat[0][1] = 0.0;
            mat[0][2] = sin;
            mat[1][0] = 0.0;
            mat[1][1] = 1.0;
            mat[1][2] = 0.0;
            mat[2][0] = -sin;
            mat[2][1] = 0.0;
            mat[2][2] = cos;
        }
        if (axis.equalsIgnoreCase("Z")) {
            mat[0][0] = cos;
            mat[0][1] = -sin;
            mat[0][2] = 0;
            mat[1][0] = sin;
            mat[1][1] = cos;
            mat[1][2] = 0.0;
            mat[2][0] = 0.0;
            mat[2][1] = 0.0;
            mat[2][2] = 1.0;
        }

        final RealMatrix m = new Array2DRowRealMatrix(mat);

        return m;
    }

    //from pixel or twoteta calculate the ellipse considering pattern calibratino
    public static EllipsePars getElliPars(Pattern2D patt2D, Point2D.Float pixel) {
        final double twothRad = patt2D.calc2T(pixel, false);
        log.fine(String.format("TWO THETA CLICK = %f", FastMath.toDegrees(twothRad)));
        return getElliPars(patt2D, twothRad);
    }

    public static EllipsePars getElliPars(Pattern2D patt2D, double twothRad) {

        final double distPix = patt2D.getDistMD() / patt2D.getPixSx();
        final double tiltrad = -FastMath.toRadians(patt2D.getTiltDeg());
        final double cosTilt = FastMath.cos(tiltrad);
        final double tanTilt = FastMath.tan(tiltrad);
        final double rotRad = FastMath.toRadians(patt2D.getRotDeg()); //amb negativa funcionava
        final double sintth = FastMath.sin(twothRad);

        final double tmenys = FastMath.tan((twothRad - tiltrad) / 2);
        final double tmes = FastMath.tan((twothRad + tiltrad) / 2);
        final double fmes = distPix * tanTilt * sintth / (cosTilt + sintth);
        final double fmenys = distPix * tanTilt * sintth / (cosTilt - sintth);

        final double vmes = distPix * (tanTilt + (1 + tmenys) / (1 - tmenys)) * sintth / (cosTilt + sintth);
        final double vmenys = distPix * (tanTilt + (1 - tmes) / (1 + tmes)) * sintth / (cosTilt - sintth);

        final double rMaj = (vmes + vmenys) / 2;
        final double rMen = FastMath.sqrt((vmes + vmenys) * (vmes + vmenys) - (fmes + fmenys) * (fmes + fmenys)) / 2;
        final double zdis = (fmes - fmenys) / 2;

        final double ellicentX = patt2D.getCentrX() + zdis * FastMath.cos(rotRad);
        final double ellicentY = patt2D.getCentrY() + zdis * FastMath.sin(rotRad); //cal considerar que direccio Y està "invertida"?? (+ cap avall)

        return new EllipsePars(rMaj, rMen, ellicentX, ellicentY, rotRad);
    }

    public static class SumImagesFileWorker extends SwingWorker<Integer, Integer> {

        private final File[] files;
        private final boolean stop;
        Pattern2D pattsum;
        boolean doSubtract = false;
        Pattern2D pattFons;
        float aini, afin, ctime;

        public SumImagesFileWorker(File[] files) {//, Pattern2D patt) {
            this.files = files;
            this.stop = false;
            this.aini = 0;
            this.afin = 0;
            this.ctime = 0;
        }

        public SumImagesFileWorker(File[] files, float ain, float afi, float ctim) {//, Pattern2D patt) {
            this(files);
            this.aini = ain;
            this.afin = afi;
            this.ctime = ctim;
        }

        public Pattern2D getpattSum() {
            return this.pattsum;
        }

        public void setSubtract(boolean doSub) {
            this.doSubtract = doSub;
        }

        public void setPattFons(Pattern2D pfons) {
            this.pattFons = pfons;
        }

        @Override
        protected Integer doInBackground() throws Exception {

            final int totalfiles = this.files.length;

            Pattern2D patt = ImgFileUtils.readPatternFile(this.files[0], true);
            final int dimY = patt.getDimY();
            final int dimX = patt.getDimX();

            this.pattsum = new Pattern2D(patt, false); //copiem dades instr de les originals
            final Pattern2D dataI4temp = new Pattern2D(dimX, dimY);
            //inicialitzem a zero les dades SUMA
            for (int i = 0; i < dimY; i++) {
                for (int j = 0; j < dimX; j++) {
                    dataI4temp.setInten(j, i, 0, true);
                }
            }

            //            int maxVal=0;

            //sumem
            for (int k = 0; k < this.files.length; k++) {
                //obrim el pattern i sumem
                patt = ImgFileUtils.readPatternFile(this.files[k], false);
                if (patt == null) {
                    log.warning("Error reading " + this.files[k].getName() + " ... skipping");
                    continue;
                }
                if (this.doSubtract) {
                    if (this.pattFons != null) {
                        patt = ImgOps.subtractBKG(patt, this.pattFons);
                    }
                }

                for (int j = 0; j < dimY; j++) {
                    for (int i = 0; i < dimX; i++) {
                        //zona exclosa saltem
                        if (patt.isExcluded(i, j))
                            continue;
                        final int s = dataI4temp.getInten(i, j) + patt.getInten(i, j);
                        dataI4temp.setInten(i, j, s, false); //sobretot no hem d'actualitzar error aqui si volem propagar correctament
                        final float err = dataI4temp.getPixel(i, j).getErr() + patt.getPixel(i, j).getErr(); //propagem error
                        dataI4temp.getPixel(i, j).setErr(err);
                    }
                }
                final float percent = ((float) k / (float) totalfiles) * 100.f;
                this.setProgress((int) percent);
                log.fine(String.valueOf(percent));
                log.info("File added: " + this.files[k].getName());
                if (this.stop)
                    break;
            }

            //antigament escalava les intensitats, ara no ho faig, es fa al guardar en un format concret
            for (int j = 0; j < dimY; j++) {
                for (int i = 0; i < dimX; i++) {
                    this.pattsum.setInten(i, j, dataI4temp.getInten(i, j), false);
                    this.pattsum.getPixel(i, j).setErr(dataI4temp.getPixel(i, j).getErr());

                    //debug
                    //                    if ((i==1075) && (j==982)){
                    //                    	log.writeNameNumPairs("config", true, "pX,pY,err", i,j,pattsum.getPixel(i,j).getErr());
                    //                    }
                }
            }
            this.setProgress(100);
            this.pattsum.setScale(1.0f);
            log.writeNameNumPairs("config", true, "aini(swk), afin(swk)", this.aini, this.afin);
            this.pattsum.setScanParameters(this.aini, this.afin, this.ctime);
            return 0;

        }
    }

    public static class PkIntegrateFileWorker extends SwingWorker<Integer, Integer> {

        private final File[] flist;
        float delsig, angDeg;
        boolean autoBkgPt, autoTol2T, autoAngD, lorCorr, estimbkg, pond;
        int zoneR, minPix, bkgpt, iosc, tol2tpix;
        MainFrame d2Dmain;

        // distMD & wavel -1 to take the ones from the original image,
        // exzfile=null for the same.
        public PkIntegrateFileWorker(File[] files, float delsig, int zoneR, int minPix, int bkgpt, boolean autoBkgPt,
                int tol2tpix, boolean autoTol2t, float angDeg, boolean autoAngDeg, boolean lorCorr, int iosc,
                boolean estimbkg, boolean pond, MainFrame d2Dmain) {
            this.flist = files;
            this.delsig = delsig;
            this.zoneR = zoneR;
            this.minPix = minPix;
            this.bkgpt = bkgpt;
            this.autoBkgPt = autoBkgPt;
            this.tol2tpix = tol2tpix;
            this.autoTol2T = autoTol2t;
            this.angDeg = angDeg;
            this.autoAngD = autoAngDeg;
            this.lorCorr = lorCorr;
            this.iosc = iosc;
            this.estimbkg = estimbkg;
            this.pond = pond;
            this.d2Dmain = d2Dmain;
        }

        @Override
        protected Integer doInBackground() throws Exception {

            final int totalfiles = this.flist.length;

            //ara anem imatge per imatge a guardar-la (mateix nom, diferent extensio, preguntarem si overwrite)
            boolean applyAll = false;
            boolean owrite = false;
            for (int i = 0; i < this.flist.length; i++) {

                final float percent = ((float) i / (float) totalfiles) * 100.f;
                this.setProgress((int) percent);

                log.info(String.format("Reading image: %s", this.flist[i].getName()));
                final Pattern2D in = ImgFileUtils.readPatternFile(this.flist[i], false);
                if (in == null) {
                    log.warning("Error reading file " + this.flist[i].getName() + " ...skipping");
                    continue;
                }
                if (this.d2Dmain != null) {
                    this.d2Dmain.updatePatt2D(in, false, true);
                }

                log.info("   Finding peaks...");
                in.setPkSearchResult(
                        ImgOps.findPeaks(in, this.delsig, this.zoneR, this.minPix, false, this.estimbkg, this.pond));
                final Iterator<Peak> itrpks = in.getPkSearchResult().iterator();
                if (this.d2Dmain != null) {
                    if (this.d2Dmain.getpksearchframe() != null) {
                        this.d2Dmain.getpksearchframe().updateTable();
                    }
                }
                log.info("   Integrating...");
                while (itrpks.hasNext()) {
                    final Peak pk = itrpks.next();
                    final Point2D.Float pxc = pk.getPixelCentre();
                    if (this.autoBkgPt) {
                        final int npix = ImgOps.arcNPix(in, (int) (pxc.x), (int) (pxc.y), this.tol2tpix, this.angDeg);
                        //agafem un 5% dels pixels com a fons
                        this.bkgpt = FastMath.round(npix * PeakSearch.def_bkgPxAutoPercent);
                        if (this.bkgpt < PeakSearch.def_minbkgPx)
                            this.bkgpt = PeakSearch.def_minbkgPx;
                        log.debug("bkgPT=" + this.bkgpt);
                    }
                    if (this.autoTol2T) {
                        this.tol2tpix = ImgOps.intRadPixelsOfAPeak(in, pxc.x, pxc.y);
                        //TODO: posem limits?
                    }
                    if (this.autoAngD) {
                        this.angDeg = ImgOps.azimAngleOfAPeak(in, pxc.x, pxc.y);
                        log.writeNameNums("CONFIG", true, "x,y,angDeg", pxc.x, pxc.y, this.angDeg);
                        //TODO
                    }
                    final Patt2Dzone pz = ImgOps.yArcTilt(in, (int) (pxc.x), (int) (pxc.y), this.tol2tpix, this.angDeg,
                            true, this.bkgpt, false);

                    pk.setZona(pz);
                    in.setIscan(this.iosc);
                    pk.calculate(this.lorCorr);
                }

                ImgOps.removeDiamondPeaks(in.getPkSearchResult());

                //Escribim PCS
                final File out = FileUtils.canviExtensio(this.flist[i], "PCS");
                if (out.exists()) {
                    if (!applyAll) {
                        final JCheckBox checkbox = new JCheckBox("Apply to all");
                        final String message = "Overwrite " + out.getName() + "?";
                        final Object[] params = { message, checkbox };
                        final int n = JOptionPane.showConfirmDialog(null, params, "Overwrite existing file",
                                JOptionPane.YES_NO_OPTION);
                        applyAll = checkbox.isSelected();
                        if (n == JOptionPane.YES_OPTION) {
                            owrite = true;
                        } else {
                            owrite = false;
                        }
                    }
                    if (!owrite)
                        continue;
                }

                final File f = ImgFileUtils.writePCS(in, out, this.delsig, this.angDeg, this.autoTol2T, this.zoneR,
                        this.minPix, this.bkgpt, this.autoBkgPt, this.autoAngD, false);

                if (f != null) {
                    log.info(f.toString() + " written!");
                } else {
                    log.warning("Error writting " + out.toString());
                }

            }
            this.setProgress(100);
            return 0;
        }
    }

    //generarà un fitxer OUT
    public static class PkSCIntegrateFileWorker extends SwingWorker<Integer, Integer> {

        private final File[] flist;
        float delsig, angDeg;
        boolean autoBkgPt, autoTol2T, autoAngD, lorCorr, estimbkg, pond;
        int zoneR, minPix, bkgpt, iosc, tol2tpix;

        // distMD & wavel -1 to take the ones from the original image,
        // exzfile=null for the same.
        public PkSCIntegrateFileWorker(File[] files, float delsig, int zoneR, int minPix, int bkgpt, boolean autoBkgPt,
                int tol2tpix, boolean autoTol2t, float angDeg, boolean autoAngDeg, boolean lorCorr, int iosc,
                boolean estimbkg, boolean pond) {
            this.flist = files;
            this.delsig = delsig;
            this.zoneR = zoneR;
            this.minPix = minPix;
            this.bkgpt = bkgpt;
            this.autoBkgPt = autoBkgPt;
            this.tol2tpix = tol2tpix;
            this.autoTol2T = autoTol2t;
            this.angDeg = angDeg;
            this.autoAngD = autoAngDeg;
            this.lorCorr = lorCorr;
            this.iosc = iosc;
            this.estimbkg = estimbkg;
            this.pond = pond;
        }

        @Override
        protected Integer doInBackground() throws Exception {

            final int totalfiles = this.flist.length;

            //ara anem imatge per imatge i anirem escribint a un fitxer OUT
            //primer generem fitxer OUT
            PrintWriter output;
            final File fout = FileUtils.fchooserSaveAsk(null, new File(D2Dplot_global.getWorkdir()), null, "OUT");
            int totalNpks = 0;
            if (fout == null)
                return null;
            try {
                //                fout = FileUtils.canviExtensio(fout, "OUT");
                output = new PrintWriter(new BufferedWriter(new FileWriter(fout)));
                final String eqLine = "=============================================================================================================";
                final String minLine = "-------------------------------------------------------------------------------------------------------------";
                Pattern2D first = null;
                for (int i = 0; i < this.flist.length; i++) {

                    final float percent = ((float) i / (float) totalfiles) * 100.f;
                    this.setProgress((int) percent);

                    log.info(String.format("Reading image: %s", this.flist[i].getName()));
                    final Pattern2D in = ImgFileUtils.readPatternFile(this.flist[i], false);
                    if (in == null) {
                        log.warning("Error reading file " + this.flist[i].getName() + " ...skipping");
                        continue;
                    }

                    //capçalera fitxer out per la 1a imatge
                    if (i == 0) {
                        first = in;
                        output.println(eqLine);
                        output.println("        d2Dplot peak integration for CERCAPICS");
                        output.println(eqLine);
                        final String fname = FileUtils.getFNameNoExt(in.getImgfile());
                        output.println("Image File= " + fname.substring(0, fname.length() - 4) + "XXXX");
                        output.println(String.format("dimX= %d, dimY= %d, centX= %.2f, centY= %.2f", in.getDimX(),
                                in.getDimY(), in.getCentrX(), in.getCentrY()));
                        output.println(String.format("pixSize(micron)= %.3f, dist(mm)= %.3f, wave(A)= %.5f",
                                in.getPixSx() * 1000, in.getDistMD(), in.getWavel()));
                        switch (in.getIscan()) {
                        case 1:
                            output.println("HORIZONTAL rotation axis");
                            break;
                        case 2:
                            output.println("VERTICAL rotation axis");
                            break;
                        default:
                            output.println("NO rotation axis");
                            break;
                        }
                        output.println(String.format("ESD factor = %.2f", this.delsig));
                        if (this.autoAngD) {
                            output.println(String.format("Automatic azim aperture of the integration for each peak"));
                        } else {
                            output.println(String.format("Azim aperture of the integration (º)= %.2f", this.angDeg));
                        }
                        if (this.autoTol2T) {
                            output.println(String.format("Automatic radial width of the integration for each peak"));
                        } else {
                            output.println(String.format("Radial width of the integration (pix)= %d", this.tol2tpix));
                        }
                        output.println(String.format("Peak merge zone radius (pixels)= %d", this.zoneR));
                        output.println(String.format("Min pixels for a peak= %d", this.minPix));
                        if (this.autoBkgPt) {
                            output.println(String.format("Background pixels determined automatically"));
                        } else {
                            output.println(String.format("Background pixels= %d", this.bkgpt));
                        }
                        if (this.estimbkg) {
                            output.println(String.format("Using background estimation for local thresholds"));
                        } else {
                            output.println(String.format("No bakground estimation, general threshold"));
                        }
                        output.println(minLine);
                        output.println(minLine);
                    } else {
                        in.copyInstrParamFromOtherPatt(first);
                    }

                    log.info("   Finding peaks...");
                    in.setPkSearchResult(ImgOps.findPeaks(in, this.delsig, this.zoneR, this.minPix, false,
                            this.estimbkg, this.pond));
                    if (in.getPkSearchResult() == null)
                        continue;
                    final Iterator<Peak> itrpks = in.getPkSearchResult().iterator();
                    log.info("   Integrating...");
                    while (itrpks.hasNext()) {
                        final Peak pk = itrpks.next();
                        final Point2D.Float pxc = pk.getPixelCentre();
                        if (this.autoBkgPt) {
                            final int npix = ImgOps.arcNPix(in, (int) (pxc.x), (int) (pxc.y), this.tol2tpix,
                                    this.angDeg);
                            //agafem un 5% dels pixels com a fons
                            this.bkgpt = FastMath.round(npix * PeakSearch.def_bkgPxAutoPercent);
                            if (this.bkgpt < PeakSearch.def_minbkgPx)
                                this.bkgpt = PeakSearch.def_minbkgPx;
                            log.debug("bkgPT=" + this.bkgpt);
                        }
                        if (this.autoTol2T) {
                            this.tol2tpix = ImgOps.intRadPixelsOfAPeak(in, pxc.x, pxc.y);
                            //TODO: posem limits?
                        }
                        if (this.autoAngD) {
                            this.angDeg = ImgOps.azimAngleOfAPeak(in, pxc.x, pxc.y);
                            log.writeNameNums("CONFIG", true, "x,y,angDeg", pxc.x, pxc.y, this.angDeg);
                            //TODO
                        }
                        final Patt2Dzone pz = ImgOps.yArcTilt(in, (int) (pxc.x), (int) (pxc.y), this.tol2tpix,
                                this.angDeg, true, this.bkgpt, false);

                        pk.setZona(pz);
                        in.setIscan(this.iosc);
                        pk.calculate(this.lorCorr);
                    }

                    ImgOps.removeDiamondPeaks(in.getPkSearchResult());

                    output.println(Peak.out_header);
                    output.println(String.format("%10d%10d", in.getFileNameNumber(), in.getPkSearchResult().size()));

                    //ordenem segons Ymax
                    //                    System.out.println(in.getPkSearchResult().get(0).getFormmattedStringOUT_singleCryst());
                    in.sortPkSearchResultYmax();
                    //                    System.out.println(in.getPkSearchResult().get(0).getFormmattedStringOUT_singleCryst());

                    for (int j = 1; j <= in.getPkSearchResult().size(); j++) {
                        output.println(String.format("%7d%s", j,
                                in.getPkSearchResult().get(j - 1).getFormmattedStringOUT_singleCryst()));
                        totalNpks++;
                    }
                    output.println(minLine);
                }
                output.println("NOMBRE TOTAL DE PICS 2D = " + totalNpks);
                output.close();
            } catch (final Exception e) {
                log.warning("Error writting OUT file");
                if (D2Dplot_global.isDebug())
                    e.printStackTrace();
                this.setProgress(100);
            }
            log.info(String.format("OUT file written! [%s]", fout.toString()));
            this.setProgress(100);
            return 0;
        }
    }

    public static class SumImagesIncoFileWorker extends SwingWorker<Integer, Integer> {

        private final File[] files;
        private final boolean stop;
        //        Pattern2D pattsum;
        boolean doSubtract = false;
        Pattern2D pattFons = null;
        float inco_aini, inco_afin, inco_aacq, inco_aincr;
        float sc_aini, sc_astep, bkgScale;
        String base_fname;
        File outdir;

        public SumImagesIncoFileWorker(File[] files, File bkgfile, float bkgscale, float scAngleIni, float scAngleStep,
                float incoAngleIni, float incoAngleFin, float incoAngleAcq, float incoAngleIncr, boolean removeBkg,
                String baseFilename, File outDir) {//, Pattern2D patt) {
            this.files = files;
            this.stop = false;
            this.sc_aini = scAngleIni;
            this.sc_astep = scAngleStep;
            this.inco_aini = incoAngleIni;
            this.inco_afin = incoAngleFin;
            this.inco_aacq = incoAngleAcq;
            this.inco_aincr = incoAngleIncr;
            this.doSubtract = removeBkg;
            this.base_fname = baseFilename;
            this.outdir = outDir;
            this.bkgScale = bkgscale;
            if (bkgfile != null) {
                try {
                    this.pattFons = ImgFileUtils.readPatternFile(bkgfile, false);
                } catch (final Exception ex) {
                    log.warning("background file not found");
                    this.pattFons = null;
                }

            }
        }

        private void generatePatternInco(float aini, float afin, File outf) {
            final int n = FastMath.round((aini - this.sc_aini) / this.sc_astep);
            final int nfin = FastMath.round((afin - this.sc_aini) / this.sc_astep) - 1;
            log.debug(String.format("images from %d to %d comprises angles %.3f to %.3f", n, nfin, aini, afin));

            //llegeixo primera imatge header pels parametres
            final EdfHeaderPatt2D pattH = ImgFileUtils.readEDFheaderOnly(this.files[n]); //TODO: canvi per EdfHeaderPatt2D
            final int dimY = pattH.getPatt2D().getDimY();
            final int dimX = pattH.getPatt2D().getDimX();
            final Pattern2D pattsum = new Pattern2D(pattH.getPatt2D(), false);
            final Pattern2D dataI4temp = new Pattern2D(dimX, dimY);
            //inicialitzem a zero les dades SUMA
            for (int i = 0; i < dimY; i++) {
                for (int j = 0; j < dimX; j++) {
                    dataI4temp.setInten(j, i, 0, true);
                }
            }

            //sumem
            for (int k = n; k < nfin; k++) {
                //obrim el pattern i sumem
                Pattern2D patt = ImgFileUtils.readPatternFile(this.files[k], false);
                if (patt == null) {
                    log.warning("Error reading " + this.files[k].getName() + " ... skipping");
                    continue;
                }
                if (this.doSubtract) {
                    if (this.pattFons != null) {
                        if (this.bkgScale < 0) {
                            this.bkgScale = ImgOps.calcGlassScale(patt, this.pattFons);
                        }
                        log.fine("bkgScale=" + this.bkgScale);
                        patt = ImgOps.subtractBKG_v2(patt, this.pattFons, this.bkgScale)[0];
                    }
                }

                for (int i = 0; i < dimY; i++) {
                    for (int j = 0; j < dimX; j++) {
                        //zona exclosa saltem
                        if (patt.isExcluded(j, i))
                            continue;
                        final int s = dataI4temp.getInten(j, i) + patt.getInten(j, i);
                        dataI4temp.setInten(j, i, s, false);
                        final float err = dataI4temp.getPixel(i, j).getErr() + patt.getPixel(i, j).getErr(); //propagem error
                        dataI4temp.getPixel(i, j).setErr(err);
                    }
                }
                final float percent = ((float) k / (float) this.files.length) * 100.f; //files is totalfiles
                this.setProgress((int) percent);
                log.fine(String.valueOf(percent));
                log.info("File added: " + this.files[k].getName());
                if (this.stop)
                    break;
            }

            //l'escala la farà el write edf, no cal fer-la aquí
            for (int i = 0; i < dimY; i++) {
                for (int j = 0; j < dimX; j++) {
                    pattsum.setInten(j, i, dataI4temp.getInten(j, i), false);
                    pattsum.getPixel(i, j).setErr(dataI4temp.getPixel(i, j).getErr());
                }
            }
            //            this.setProgress(100);
            pattsum.setScale(1.0f);
            log.writeNameNumPairs("fine", true, "aini(swk), afin(swk)", aini, afin);
            pattsum.setScanParameters(aini, afin, 0);
            log.writeNameNumPairs("fine", true, "pattsum.omeini, omefin", pattsum.getOmeIni(), pattsum.getOmeFin());
            //escribim pattsum
            ImgFileUtils.writePatternFile(outf, pattsum, false); //ja porta el format de la seleccio anterior 
        }

        @Override
        protected Integer doInBackground() throws Exception {

            //PREGUNTEM EL FORMAT DE SORTIDA (aixo pot anar aqui o a maniframe)
            //this line returns the FORMAT in the ENUM or NULL
            final SupportedWriteExtensions[] possibilities = SupportedWriteExtensions.values();
            final SupportedWriteExtensions format = (SupportedWriteExtensions) JOptionPane.showInputDialog(null,
                    "Output format:", "Save Files", JOptionPane.PLAIN_MESSAGE, null, possibilities, possibilities[0]);
            if (format == null) {
                return -1;
            }

            float currOme = this.inco_aini;
            int index = 0;

            while ((currOme + this.inco_aacq) <= this.inco_afin) {
                String outfname = this.outdir.getAbsolutePath() + D2Dplot_global.separator + this.base_fname + "_";
                //num
                outfname = String.format("%s%04d", outfname, index);
                outfname = outfname + "." + format.toString().toLowerCase();
                log.fine("outfname=" + outfname);
                log.fine("currome=" + currOme + " inco_aacq=" + this.inco_aacq + " inco_aincr=" + this.inco_aincr
                        + " inco_afin=" + this.inco_afin);

                //GENERATE PATTERN
                this.generatePatternInco(currOme, currOme + this.inco_aacq, new File(outfname));
                //                int n = FastMath.round((currOme - sc_aini)/sc_astep);
                //                int nfin = FastMath.round(((currOme+inco_aacq) -sc_aini)/sc_astep)-1;
                //                log.info(String.format("images from %d to %d comprises angles %.3f to %.3f",n,nfin,currOme,currOme+inco_aacq));
                currOme = currOme + this.inco_aincr;
                index = index + 1;
            }
            this.setProgress(100);
            return 0;
        }
    }
}
