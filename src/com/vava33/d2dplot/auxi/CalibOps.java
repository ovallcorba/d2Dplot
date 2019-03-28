package com.vava33.d2dplot.auxi;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Iterator;

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

    public static float minimize2Theta(Pattern2D patt2d, double xcen, double ycen, double distMDmm, double tiltD,
            double rotD, ArrayList<EllipsePars> solutions, float[] cal_d) {
        final float oldxcen = patt2d.getCentrX();
        final float oldycen = patt2d.getCentrY();
        final float oldtilt = patt2d.getTiltDeg();
        final float oldrot = patt2d.getRotDeg();
        final float olddist = patt2d.getDistMD();

        patt2d.setCentrX((float) xcen);
        patt2d.setCentrY((float) ycen);
        patt2d.setTiltDeg((float) tiltD);
        patt2d.setRotDeg((float) rotD);
        patt2d.setDistMD((float) distMDmm);

        final Iterator<EllipsePars> itr = solutions.iterator();
        float residual = 0;
        //        float angstep = 2.5f;
        while (itr.hasNext()) {
            final EllipsePars e = itr.next();
            final float lab6dsp = cal_d[e.getLab6ring() - 1];
            final ArrayList<Point2D.Float> punts = e.getEstimPoints();
            //            ArrayList<Point2D.Float> punts = e.getEllipsePoints(0, 360, angstep);
            //            angstep=angstep/2.f;
            final Iterator<Point2D.Float> itrp = punts.iterator();
            while (itrp.hasNext()) {
                final Point2D.Float p = itrp.next();
                final float pdsp = (float) patt2d.calcDsp(patt2d.calc2T(p, false));
                residual = residual + FastMath.abs(lab6dsp - pdsp);
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
    public static int calcSum(Pattern2D patt2d, float xcen, float ycen, float distMDmm, float tiltD, float rotD,
            float[] cal_d) {

        final float oldxcen = patt2d.getCentrX();
        final float oldycen = patt2d.getCentrY();
        final float oldtilt = patt2d.getTiltDeg();
        final float oldrot = patt2d.getRotDeg();
        final float olddist = patt2d.getDistMD();

        patt2d.setCentrX(xcen);
        patt2d.setCentrY(ycen);
        patt2d.setTiltDeg(tiltD);
        patt2d.setRotDeg(rotD);
        patt2d.setDistMD(distMDmm);

        int suma = 0;

        for (final float element : cal_d) {
            final double tth = 2 * FastMath.asin(patt2d.getWavel() / (2 * element));
            final double weight = 1 / element;
            final EllipsePars p = ImgOps.getElliPars(patt2d, tth);
            final ArrayList<Point2D.Float> punts = p.getEllipsePoints(0, 360, 5);
            final Iterator<Point2D.Float> itrp = punts.iterator();
            while (itrp.hasNext()) {
                final Point2D.Float pix = itrp.next();
                final int ix = (int) (pix.x);
                final int iy = (int) (pix.y);
                // log.debug("calc SUM ring="+i+"pix="+ix+" "+iy);
                if (!patt2d.isInside(ix, iy))
                    continue;
                if (patt2d.isExcluded(ix, iy))
                    continue;
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

    //    public static int calcSum(Pattern2D patt2d, double d, double e, double f,
    //            double g, double h) {
    //        return calcSum(patt2d, (float) d, (float) e, (float) f, (float) g,
    //                (float) h);
    //    }

    public static Pattern2D createLaB6Img(float xcen, float ycen, float distMDmm, float tiltD, float rotD, float wavel,
            float pixsizeMM, float[] cal_d) {

        final Pattern2D lab6 = new Pattern2D(2048, 2048, xcen, ycen, 5000, 0, 1);
        lab6.setWavel(wavel);
        lab6.setDistMD(distMDmm);
        lab6.setTiltDeg(tiltD);
        lab6.setRotDeg(rotD);
        lab6.setPixSx(pixsizeMM);
        lab6.setPixSy(pixsizeMM);
        lab6.zeroIntensities();

        // hem de posar intensitat als anells amb uncert fwhm
        //        float fwhmPx = 5;
        final int maxInten = 10000;
        for (int i = 0; i < cal_d.length; i++) {
            final double tth = 2 * FastMath.asin(lab6.getWavel() / (2 * cal_d[i])); //aqui ja anem des de cal_d i=zero...
            final EllipsePars p = ImgOps.getElliPars(lab6, tth);
            log.config("lab6 ring" + i);
            p.logElliPars("CONFIG");
            final ArrayList<Point2D.Float> punts = p.getEllipsePoints(0, 360, -1);
            final Iterator<Point2D.Float> itrp = punts.iterator();
            while (itrp.hasNext()) {
                final Point2D.Float pix = itrp.next();
                final int ix = (int) (pix.x);
                final int iy = (int) (pix.y);
                // log.debug("ring="+i+"pix="+ix+" "+iy);
                if (!lab6.isInside(ix, iy))
                    continue;
                if (lab6.isExcluded(ix, iy))
                    continue;
                lab6.setInten(ix, iy, maxInten, false);
                final Point2D.Float extr = getNeighbourRadialDir(lab6, new Point2D.Float(pix.x, pix.y), true);
                final Point2D.Float intr = getNeighbourRadialDir(lab6, new Point2D.Float(pix.x, pix.y), false);
                if (lab6.isInside((int) (extr.x), (int) (extr.y))) {
                    lab6.setInten((int) (extr.x), (int) (extr.y), maxInten / 2, false);
                }
                if (lab6.isInside((int) (intr.x), (int) (intr.y))) {
                    lab6.setInten((int) (intr.x), (int) (intr.y), maxInten / 2, false);
                }

            }
        }
        return lab6;
    }

    public static Point2D.Float getNeighbourRadialDir(Pattern2D patt2d, Point2D.Float pixel, boolean positiveDir) {
        // vector centre-pixel
        float vPCx = pixel.x - patt2d.getCentrX();
        float vPCy = patt2d.getCentrY() - pixel.y;

        // vector unitari
        final float modul = (float) FastMath.sqrt(vPCx * vPCx + vPCy * vPCy);
        vPCx = vPCx / modul;
        vPCy = vPCy / modul;
        float pixelExX = pixel.x;
        float pixelExY = pixel.y;
        if (positiveDir) {
            while (((int) (pixelExX) == (int) (pixel.x)) && ((int) (pixelExY) == (int) (pixel.y))) {
                pixelExX = vPCx * (modul + 0.5f); // ex=extern
                pixelExY = vPCy * (modul + 0.5f);
            }
            // if
            // ((FastMath.round(pixelExX)==FastMath.round(pixel.x))&&(FastMath.round(pixelExY)==FastMath.round(pixel.y))){
            // //encara es el mateix pixel
            // }
        } else { // negativa
            while (((int) (pixelExX) == (int) (pixel.x)) && ((int) (pixelExY) == (int) (pixel.y))) {
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
