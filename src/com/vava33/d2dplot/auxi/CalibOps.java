package com.vava33.d2dplot.auxi;

import java.awt.geom.Point2D;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.commons.math3.fitting.GaussianCurveFitter;
import org.apache.commons.math3.fitting.WeightedObservedPoints;
import org.apache.commons.math3.stat.regression.SimpleRegression;
import org.apache.commons.math3.util.FastMath;

import com.vava33.d2dplot.D2Dplot_global;
import com.vava33.jutils.VavaLogger;

public final class CalibOps {
    private static final String className = "CalibOps";
    private static VavaLogger log = D2Dplot_global.getVavaLogger(className);
    
    
    public static ArrayList<Calibrant> calibrants = new ArrayList<Calibrant>();
    
    public static ArrayList<Calibrant> getCalibrants() {
        return calibrants;
    }


    public static void setCalibrants(ArrayList<Calibrant> calibrants) {
        CalibOps.calibrants = calibrants;
    }

    public static Point2D.Float centerEstimationGrid(Pattern2D patt2d) {

        int securityMarginPx = 100;
        int halfndiv = 5;

        int rangeMirror = 200; // +-75 pixels

        // dividim grid horitzontal
        int div = ((patt2d.getDimY() / 2) - securityMarginPx) / halfndiv;
        // int ydiv = patt2d.getDimY()/2 + div;
        int ydiv = securityMarginPx + div;

        ArrayList<Point2D.Float> xcent = new ArrayList<Point2D.Float>(); // resultatX
        ArrayList<Point2D.Float> ycent = new ArrayList<Point2D.Float>(); // resultatY

        while (ydiv < patt2d.getDimY()) {
            ArrayList<Integer> d1integr = new ArrayList<Integer>();
            for (int i = 0; i < patt2d.getDimX(); i++) {
                d1integr.add(patt2d.getInten(i, ydiv));
                // log.config("d1integr "+String.valueOf(d1integr.get(i)));
            }

            // REPRESENTEM PER DEBUG =====================================:

            Pattern1D d1integrPatt = new Pattern1D(0, d1integr.size(), 1);
            Iterator<Integer> itrInteg = d1integr.iterator();
            int pos = 0;
            while (itrInteg.hasNext()) {
                Integer n = itrInteg.next();
                // if (n==null){
                // pos = pos +1;
                // continue;
                // }
                d1integrPatt.sumPoint(pos, n.intValue(), 1);
                pos = pos + 1;
            }
            d1integrPatt.writeXYnorm(
                    new File(D2Dplot_global.getWorkdir()
                            + D2Dplot_global.separator + "d1integr_ydiv" + ydiv
                            + ".xy"), "d1integr_ydiv" + ydiv);
            // =============================================================

            // ara ja ho tenim integrat, busquem el centre (MIRROR)

            int centre = d1integr.size() / 2;
            centre = centre - rangeMirror;
            // float[] mirror = new float[rangeMirror*2];
            ArrayList<Integer> mirror = new ArrayList<Integer>();
            for (int i = 0; i < d1integr.size(); i++) {
                mirror.add(i, Integer.MAX_VALUE);
            }
            // log.debug("d1integr size="+d1integr.size()+"mirror size="+mirror.size());
            // int index = 0;
            // int npunts = 0;
            for (int i = centre; i < (centre + 2 * rangeMirror); i++) {
                // i es el pixel central que estem avaluant com a possible
                // mirror
                boolean end = false;
                int sumDiff = 0;
                int offset = 1;
                while (!end) {
                    if ((i - offset > 0) && i + offset < (d1integr.size() - 2)) {
                        // sumDiff = sumDiff +
                        // FastMath.abs(d1integr.get(i-offset)-d1integr.get(i+offset));
                        // //AL QUADRAT PROVO A VEURE SI ES MES BÈSTIA
                        sumDiff = (int) (sumDiff + FastMath.pow(
                                d1integr.get(i - offset)
                                        - d1integr.get(i + offset), 2));
                        offset = offset + 1;
                        // npunts = npunts+1;
                    } else {
                        end = true;
                    }
                }
                // mirror[index]=
                // index = index + 1;
                // mirror.set(i, sumDiff/npunts);
                mirror.set(i, sumDiff / (offset - 1)); // LA DIVISIO L'HE AFEGIT
                                                       // JO, IGUAL QUE EL
                                                       // FACTOR QUADRATIC
                // log.config("mirror "+String.valueOf(mirror.get(i)));
            }

            // REPRESENTEM PER DEBUG =====================================:

            Pattern1D mirrorPatt = new Pattern1D(0, mirror.size(), 1);
            Iterator<Integer> itrMPatt = mirror.iterator();
            pos = 0;
            while (itrMPatt.hasNext()) {
                Integer n = itrMPatt.next();
                if ((n == null) || (n.intValue() == Integer.MAX_VALUE)) {
                    pos = pos + 1;
                    continue;
                }
                mirrorPatt.sumPoint(pos, n.intValue(), 1);
                pos = pos + 1;
            }
            mirrorPatt.writeXYnorm(new File(D2Dplot_global.getWorkdir()
                    + D2Dplot_global.separator + "mirror_ydiv" + ydiv + ".xy"),
                    "mirror_ydiv" + ydiv);

            // =============================================================

            // ARA BUSQUEM EL PUNT MINIM DE MIRROR ---- S'HA DE FER FENT FIT, NO
            // FUNCIONA NOMES AGAFANT EL MIN.
            // Integer min = Collections.min(mirror);
            // int minPixelX = mirror.indexOf(min); //AQUEST ES EL BO!
            // Provem de fer el fit (cal invertir)
            WeightedObservedPoints obs = new WeightedObservedPoints();
            centre = mirror.size() / 2; // fitegem amb mig rang
            for (int i = centre - rangeMirror; i < centre + rangeMirror; i++) {
                if (mirror.get(i).intValue() == Integer.MAX_VALUE) continue;
                if (mirror.get(i).intValue() == 0) continue;
                obs.add(i, 1.d / (double) mirror.get(i));
            }
            double[] guess = new GaussianCurveFitter.ParameterGuesser(
                    obs.toList()).guess();
            log.writeNameNums("CONFIG", true, "guess", guess);

            double[] parameters = GaussianCurveFitter.create()
                    .withStartPoint(guess).fit(obs.toList());
            log.writeNameNums("CONFIG", true, "parameters", parameters);

            // xcent.add(new Point2D.Float(minPixelX,ydiv));
            // log.writeNameNums("CONFIG", true, "xcent Point added=",
            // minPixelX,ydiv);
            xcent.add(new Point2D.Float((float) parameters[1], ydiv));
            log.writeNameNums("CONFIG", true, "xcent Point added=",
                    (float) parameters[1], ydiv);
            ydiv = ydiv + div;

        }

        // ARA LES Y
        div = ((patt2d.getDimX() / 2) - securityMarginPx) / halfndiv;
        int xdiv = securityMarginPx + div;

        while (xdiv < patt2d.getDimY()) {
            ArrayList<Integer> d1integr = new ArrayList<Integer>();
            for (int i = 0; i < patt2d.getDimY(); i++) {
                d1integr.add(patt2d.getInten(xdiv, i));
            }

            // REPRESENTEM PER DEBUG =====================================:

            Pattern1D d1integrPatt = new Pattern1D(0, d1integr.size(), 1);
            Iterator<Integer> itrInteg = d1integr.iterator();
            int pos = 0;
            while (itrInteg.hasNext()) {
                Integer n = itrInteg.next();
                if (n == null) {
                    pos = pos + 1;
                    continue;
                }
                d1integrPatt.sumPoint(pos, n.intValue(), 1);
                pos = pos + 1;
            }
            d1integrPatt.writeXYnorm(
                    new File(D2Dplot_global.getWorkdir()
                            + D2Dplot_global.separator + "d1integr_xdiv" + xdiv
                            + ".xy"), "d1integr_xdiv" + xdiv);
            // =============================================================

            // ara ja ho tenim integrat, busquem el centre (MIRROR)

            int centre = d1integr.size() / 2;
            centre = centre - rangeMirror;
            // float[] mirror = new float[rangeMirror*2];
            ArrayList<Integer> mirror = new ArrayList<Integer>();
            for (int i = 0; i < d1integr.size(); i++) {
                mirror.add(i, Integer.MAX_VALUE);
            }
            // int index = 0;
            for (int i = centre; i < (centre + 2 * rangeMirror); i++) {
                // i es el pixel central que estem avaluant com a possible
                // mirror
                boolean end = false;
                int sumDiff = 0;
                int offset = 1;
                while (!end) {
                    if ((i - offset > 0) && i + offset < (d1integr.size() - 2)) {
                        sumDiff = (int) (sumDiff + FastMath.pow(
                                d1integr.get(i - offset)
                                        - d1integr.get(i + offset), 2));
                        offset = offset + 1;
                        // npunts = npunts+1;
                    } else {
                        end = true;
                    }
                }
                // mirror[index]=
                // index = index + 1;
                // mirror.set(i, sumDiff/npunts);
                mirror.set(i, sumDiff / (offset - 1));
                // log.config("mirror "+String.valueOf(mirror.get(i)));
            }

            // REPRESENTEM PER DEBUG =====================================:

            Pattern1D mirrorPatt = new Pattern1D(0, mirror.size(), 1);
            Iterator<Integer> itrMPatt = mirror.iterator();
            pos = 0;
            while (itrMPatt.hasNext()) {
                Integer n = itrMPatt.next();
                if ((n == null) || (n.intValue() == Integer.MAX_VALUE)) {
                    pos = pos + 1;
                    continue;
                }
                mirrorPatt.sumPoint(pos, n.intValue(), 1);
                pos = pos + 1;
            }
            mirrorPatt.writeXYnorm(new File(D2Dplot_global.getWorkdir()
                    + D2Dplot_global.separator + "mirror_xdiv" + xdiv + ".xy"),
                    "mirror_xdiv" + xdiv);

            // =============================================================

            // ARA BUSQUEM EL PUNT MINIM DE MIRROR
            // Integer min = Collections.min(mirror);
            // int minPixelY = mirror.indexOf(min); //AQUEST ES EL BO!
            // ycent.add(new Point2D.Float(xdiv,minPixelY));
            // log.writeNameNums("CONFIG", true, "ycent Point added=",
            // xdiv,minPixelY);

            WeightedObservedPoints obs = new WeightedObservedPoints();
            centre = mirror.size() / 2; // fitegem amb mig rang
            for (int i = centre - rangeMirror; i < centre + rangeMirror; i++) {
                if (mirror.get(i).intValue() == Integer.MAX_VALUE) continue;
                if (mirror.get(i).intValue() == 0) continue;
                obs.add(i, 1.d / (double) mirror.get(i));
            }
            double[] guess = new GaussianCurveFitter.ParameterGuesser(
                    obs.toList()).guess();
            log.writeNameNums("CONFIG", true, "guess", guess);

            double[] parameters = GaussianCurveFitter.create()
                    .withStartPoint(guess).fit(obs.toList());
            log.writeNameNums("CONFIG", true, "parameters", parameters);

            ycent.add(new Point2D.Float(xdiv, (float) parameters[1]));
            log.writeNameNums("CONFIG", true, "ycent Point added=", xdiv,
                    (float) parameters[1]);
            xdiv = xdiv + div;
        }

        // fer recta xcent i ycent i buscar la itersecció.
        SimpleRegression sr = new SimpleRegression();
        Iterator<Point2D.Float> itrx = xcent.iterator();
        while (itrx.hasNext()) {
            Point2D.Float e = itrx.next();
            sr.addData(e.getX(), e.getY());
        }
        // recta:
        double slope1 = 0;
        double intercept1 = 0;
        double rsqr = 0;
        try {
            slope1 = sr.getSlope();
            intercept1 = sr.getIntercept();
            rsqr = sr.getRSquare();
            log.writeNameNumPairs("CONFIG", true,
                    "slope1 intercept1 rsqr npoints", slope1, intercept1, rsqr,
                    sr.getN());
        } catch (Exception ex) {
            if (D2Dplot_global.isDebug()) ex.printStackTrace();
            log.warning("Error in regression");
            return null;
        }

        sr = new SimpleRegression();
        itrx = ycent.iterator();
        while (itrx.hasNext()) {
            Point2D.Float e = itrx.next();
            sr.addData(e.getX(), e.getY());
        }
        // recta:
        double slope2 = 0;
        double intercept2 = 0;
        rsqr = 0;
        try {
            slope2 = sr.getSlope();
            intercept2 = sr.getIntercept();
            rsqr = sr.getRSquare();
            log.writeNameNumPairs("CONFIG", true,
                    "slope2 intercept2 rsqr npoints", slope2, intercept2, rsqr,
                    sr.getN());
        } catch (Exception ex) {
            if (D2Dplot_global.isDebug()) ex.printStackTrace();
            log.warning("Error in regression");
            return null;
        }

        // ara busquem la intersecció
        float xinters = (float) ((intercept2 - intercept1) / (slope1 - slope2));
        float yinters = (float) (slope1 * xinters + intercept1);

        return new Point2D.Float(xinters, yinters);
    }

    
    public static float minimize2Theta(Pattern2D patt2d, double xcen, double ycen,
            double distMDmm, double tiltD, double rotD,ArrayList<EllipsePars> solutions, float[] cal_d){
        float oldxcen = patt2d.getCentrX();
        float oldycen = patt2d.getCentrY();
        float oldtilt = patt2d.getTiltDeg();
        float oldrot = patt2d.getRotDeg();
        float olddist = patt2d.getDistMD();

        patt2d.setCentrX((float) xcen);
        patt2d.setCentrY((float) ycen);
        patt2d.setTiltDeg((float) tiltD);
        patt2d.setRotDeg((float) rotD);
        patt2d.setDistMD((float) distMDmm);
        
        Iterator<EllipsePars> itr = solutions.iterator();
        float residual = 0;
//        float angstep = 2.5f;
        while (itr.hasNext()){
            EllipsePars e = itr.next();
            float lab6dsp = cal_d[e.getLab6ring()-1];
            ArrayList<Point2D.Float> punts = e.getEstimPoints();
//            ArrayList<Point2D.Float> punts = e.getEllipsePoints(0, 360, angstep);
//            angstep=angstep/2.f;
            Iterator<Point2D.Float> itrp = punts.iterator();
            while (itrp.hasNext()){
                Point2D.Float p = itrp.next();
                float pdsp = (float) patt2d.calcDsp(patt2d.calc2T(p, false));
                residual = residual + FastMath.abs(lab6dsp-pdsp);
            }
        }
        
        // RECUPEREM VALORS
        patt2d.setCentrX(oldxcen);
        patt2d.setCentrY(oldycen);
        patt2d.setTiltDeg(oldtilt);
        patt2d.setRotDeg(oldrot);
        patt2d.setDistMD(olddist);
        
        return residual;
        
    }
    
    // funció que donats un parametres calcula un valor a maximitzar.
    // ÉS A DIR:
    // param: xcent, ycent, tilt, rot
    // anirà sumant pixels dels anells de LaB6
    // el valor maxim serà aquell on la coincidència serà màxima
    public static int calcSum(Pattern2D patt2d, float xcen, float ycen,
            float distMDmm, float tiltD, float rotD, float[] cal_d) {

        float oldxcen = patt2d.getCentrX();
        float oldycen = patt2d.getCentrY();
        float oldtilt = patt2d.getTiltDeg();
        float oldrot = patt2d.getRotDeg();
        float olddist = patt2d.getDistMD();

        patt2d.setCentrX(xcen);
        patt2d.setCentrY(ycen);
        patt2d.setTiltDeg(tiltD);
        patt2d.setRotDeg(rotD);
        patt2d.setDistMD(distMDmm);

        int suma = 0;

        for (int i = 0; i < cal_d.length; i++) {
            double tth = 2 * FastMath.asin(patt2d.getWavel() / (2 * cal_d[i]));
            double weight = 1 / cal_d[i];
            EllipsePars p = ImgOps.getElliPars(patt2d, tth);
            ArrayList<Point2D.Float> punts = p.getEllipsePoints(0, 360, 5);
            Iterator<Point2D.Float> itrp = punts.iterator();
            while (itrp.hasNext()) {
                Point2D.Float pix = itrp.next();
                int ix = (int)(pix.x);
                int iy = (int)(pix.y);
                // log.debug("calc SUM ring="+i+"pix="+ix+" "+iy);
                if (!patt2d.isInside(ix, iy)) continue;
                if (patt2d.isExcluded(ix, iy)) continue;
                suma = (int) (suma + weight * patt2d.getInten(ix, iy));
            }
        }

        // RECUPEREM VALORS
        patt2d.setCentrX(oldxcen);
        patt2d.setCentrY(oldycen);
        patt2d.setTiltDeg(oldtilt);
        patt2d.setRotDeg(oldrot);
        patt2d.setDistMD(olddist);

        return suma;
    }

    public static int calcSum(Pattern2D patt2d, double d, double e, double f,
            double g, double h) {
        return calcSum(patt2d, (float) d, (float) e, (float) f, (float) g,
                (float) h);
    }

    public static Pattern2D createLaB6Img(float xcen, float ycen,
            float distMDmm, float tiltD, float rotD, float wavel, float pixsizeMM, float[] cal_d) {

        Pattern2D lab6 = new Pattern2D(2048, 2048, xcen, ycen, 5000, 0, 1);
        lab6.setWavel(wavel);
        lab6.setDistMD(distMDmm);
        lab6.setTiltDeg(tiltD);
        lab6.setRotDeg(rotD);
        lab6.setPixSx(pixsizeMM);
        lab6.setPixSy(pixsizeMM);
        lab6.zeroIntensities();

        // hem de posar intensitat als anells amb uncert fwhm
//        float fwhmPx = 5;
        int maxInten = 10000;
        for (int i = 0; i < cal_d.length; i++) {
            double tth = 2 * FastMath.asin(lab6.getWavel() / (2 * cal_d[i])); //aqui ja anem des de cal_d i=zero...
            EllipsePars p = ImgOps.getElliPars(lab6, tth);
            log.config("lab6 ring"+i);
            p.logElliPars("CONFIG");
            ArrayList<Point2D.Float> punts = p.getEllipsePoints(0, 360, -1);
            Iterator<Point2D.Float> itrp = punts.iterator();
            while (itrp.hasNext()) {
                Point2D.Float pix = itrp.next();
                int ix = (int)(pix.x);
                int iy = (int)(pix.y);
                // log.debug("ring="+i+"pix="+ix+" "+iy);
                if (!lab6.isInside(ix, iy)) continue;
                if (lab6.isExcluded(ix, iy)) continue;
                lab6.setInten(ix, iy, maxInten);
                Point2D.Float extr = getNeighbourRadialDir(lab6,new Point2D.Float(pix.x, pix.y), true);
                Point2D.Float intr = getNeighbourRadialDir(lab6,new Point2D.Float(pix.x, pix.y), false);
                if (lab6.isInside((int)(extr.x),(int)(extr.y))){
                    lab6.setInten((int)(extr.x),(int)(extr.y), maxInten / 2);    
                }
                if (lab6.isInside((int)(intr.x),(int)(intr.y))){
                    lab6.setInten((int)(intr.x), (int)(intr.y), maxInten / 2);    
                }
                
            }
        }
        return lab6;
    }

    public static Point2D.Float getNeighbourRadialDir(Pattern2D patt2d,
            Point2D.Float pixel, boolean positiveDir) {
        // vector centre-pixel
        float vPCx = pixel.x - patt2d.getCentrX();
        float vPCy = patt2d.getCentrY() - pixel.y;

        // vector unitari
        float modul = (float) FastMath.sqrt(vPCx * vPCx + vPCy * vPCy);
        vPCx = vPCx / modul;
        vPCy = vPCy / modul;
        float pixelExX = pixel.x;
        float pixelExY = pixel.y;
        if (positiveDir) {
            while (((int)(pixelExX) == (int)(pixel.x))
                    && ((int)(pixelExY) == (int)(pixel.y))) {
                pixelExX = vPCx * (modul + 0.5f); // ex=extern
                pixelExY = vPCy * (modul + 0.5f);
            }
            // if
            // ((FastMath.round(pixelExX)==FastMath.round(pixel.x))&&(FastMath.round(pixelExY)==FastMath.round(pixel.y))){
            // //encara es el mateix pixel
            // }
        } else { // negativa
            while (((int)(pixelExX) == (int)(pixel.x))
                    && ((int)(pixelExY) == (int)(pixel.y))) {
                pixelExX = vPCx * (modul - 0.5f); // ex=extern
                pixelExY = vPCy * (modul - 0.5f);
            }
        }
        pixelExX = patt2d.getCentrX() + pixelExX;
        pixelExY = patt2d.getCentrY() - pixelExY;
        // log.debug("pixEx="+pixelExX+" "+pixelExY);
        return new Point2D.Float(pixelExX, pixelExY);
    }
}
