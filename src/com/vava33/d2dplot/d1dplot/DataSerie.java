package com.vava33.d2dplot.d1dplot;

import java.awt.Color;
import java.util.ArrayList;

import org.apache.commons.math3.util.FastMath;

import com.vava33.d2dplot.D2Dplot_global;
import com.vava33.jutils.VavaLogger;

public class DataSerie {
    private static float def_markerSize = 3;
    private static float def_lineWidth = 1;

    public enum Xunits {
        tth("2Theta"), dsp("d-spacing"), dspInv("1/dsp"), Q("Q"), G("G(r)"); //Q is 4pi(sinT)/lambda
        private String name;

        private Xunits(String s) {
            this.name = s;
        }

        public String getName() {
            return this.name;
        }

        public Xunits getEnum(String n) {
            if (this.getName().equalsIgnoreCase(n))
                return this;
            return null;
        }
    }

    private ArrayList<DataPoint> seriePoints;

    private double wavelength = -1;
    private float markerSize;
    private float lineWidth;
    private boolean showErrBars = false;
    private double t2i, t2f, step;
    private Color color;
    private Pattern1D Patt1D;
    private Xunits xUnits;
    private static VavaLogger log = D2Dplot_global.getVavaLogger(DataSerie.class.getName());
    private String serieName;

    //empty dataserie
    public DataSerie() {
        this.setSeriePoints(new ArrayList<DataPoint>());
        this.setPatt1D(null);
        this.setT2i(-999);
        this.setT2f(-999);
        this.setStep(-999);
        this.setWavelength(-1);
        this.setxUnits(Xunits.tth);
        this.setColor(Color.BLACK);
        this.setMarkerSize(def_markerSize);
        this.setLineWidth(def_lineWidth);
        this.setSerieName("");
    }

    public DataPoint getPoint(int arrayPosition) {
        final DataPoint dp = this.seriePoints.get(arrayPosition);
        return dp;
    }

    public int getIndexOfDP(DataPoint dp) {
        return this.seriePoints.indexOf(dp);
    }

    public void addPoint(DataPoint dp) {
        this.seriePoints.add(dp);
    }

    public void removePoint(DataPoint dp) {
        this.logdebug("index of the point to remove=" + this.seriePoints.indexOf(dp));
        final boolean removed = this.seriePoints.remove(dp);
        this.logdebug(Boolean.toString(removed));
    }

    public void removePoint(int index) {
        this.seriePoints.remove(index);
    }

    //pels dos casos
    public int getNpoints() {
        return this.seriePoints.size();
    }

    public void setSeriePoints(ArrayList<DataPoint> seriePoints) {
        this.seriePoints = seriePoints;
    }

    public float getMarkerSize() {
        return this.markerSize;
    }

    public void setMarkerSize(float markerSize) {
        this.markerSize = markerSize;
    }

    public double[] getPuntsMaxXMinXMaxYMinY() {
        if (this.seriePoints != null) {
            double minX = Double.MAX_VALUE;
            double minY = Double.MAX_VALUE;
            double maxX = Double.MIN_VALUE;
            double maxY = Double.MIN_VALUE;
            for (int i = 0; i < this.seriePoints.size(); i++) {
                final DataPoint punt = this.getPoint(i);
                if (punt.getX() < minX) {
                    minX = punt.getX();
                }
                if (punt.getX() > maxX) {
                    maxX = punt.getX();
                }
                if (punt.getY() < minY) {
                    minY = punt.getY();
                }
                if (punt.getY() > maxY) {
                    maxY = punt.getY();
                }
            }
            return new double[] { maxX, minX, maxY, minY };
        } else {
            return null;
        }
    }

    public float getLineWidth() {
        return this.lineWidth;
    }

    public void setLineWidth(float lineWidth) {
        this.lineWidth = lineWidth;
    }

    public boolean isShowErrBars() {
        return this.showErrBars;
    }

    public void setShowErrBars(boolean showErrBars) {
        this.showErrBars = showErrBars;
    }

    public Color getColor() {
        return this.color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public Pattern1D getPatt1D() {
        return this.Patt1D;
    }

    public void setPatt1D(Pattern1D patt1d) {
        this.Patt1D = patt1d;
        if (patt1d != null) {
            if (this.Patt1D.getOriginal_wavelength() < 0) {
                this.Patt1D.setOriginal_wavelength(this.wavelength);
            }
            if (patt1d.indexOfSerie(this) < 0) { //ONLY IF IT IS NOT IN THE LIST!!!
                patt1d.addDataSerie(this);
            }
        }
    }

    @Override
    public String toString() {
        return this.getPatt1D().getFile().getName();
    }

    public double getStep() {
        return this.step;
    }

    public void setStep(double step) {
        this.step = step;
    }

    public void setT2f(double t2f) {
        this.t2f = t2f;
    }

    public void setT2i(double t2i) {
        this.t2i = t2i;
    }

    public double calcStep() {
        return (this.t2f - this.t2i) / this.getNpoints();
    }

    public double getWavelength() {
        return this.wavelength;
    }

    public void setWavelength(double wavelength) {
        this.wavelength = wavelength;
    }

    public Xunits getxUnits() {
        return this.xUnits;
    }

    public void setxUnits(Xunits xUnits) {
        this.xUnits = xUnits;
    }

    public void clearDataPoints() {
        this.seriePoints.clear();
    }

    public double[] calcYmeanYDesvYmaxYmin() {
        final int puntIni = 0;
        final int puntFin = this.getNpoints() - 1;
        return this.calcYmeanYDesvYmaxYmin(puntIni, puntFin);
    }

    //punt Fin està inclos
    public double[] calcYmeanYDesvYmaxYmin(int puntIni, int puntFin) {
        final double[] ymean_ydesv_ymax_ymin = new double[4];
        ymean_ydesv_ymax_ymin[2] = Double.MIN_VALUE;
        ymean_ydesv_ymax_ymin[3] = Double.MAX_VALUE;
        final int npoints = FastMath.abs(puntFin - puntIni + 1);
        double sumY = 0;
        for (int i = puntIni; i <= puntFin; i++) {
            final DataPoint dp = this.getPoint(i);
            sumY = sumY + dp.getY();
            if (dp.getY() < ymean_ydesv_ymax_ymin[3])
                ymean_ydesv_ymax_ymin[3] = dp.getY();
            if (dp.getY() > ymean_ydesv_ymax_ymin[2])
                ymean_ydesv_ymax_ymin[2] = dp.getY();
        }
        ymean_ydesv_ymax_ymin[0] = sumY / npoints;
        //ara desviacio
        sumY = 0;
        for (int i = puntIni; i <= puntFin; i++) {
            final DataPoint dp = this.getPoint(i);
            sumY = sumY + (dp.getY() - ymean_ydesv_ymax_ymin[0]) * (dp.getY() - ymean_ydesv_ymax_ymin[0]);
        }
        ymean_ydesv_ymax_ymin[1] = FastMath.sqrt(sumY / (npoints - 1));
        return ymean_ydesv_ymax_ymin;
    }

    public static float getDef_markerSize() {
        return def_markerSize;
    }

    public static void setDef_markerSize(float def_markerSize) {
        DataSerie.def_markerSize = def_markerSize;
    }

    public static float getDef_lineWidth() {
        return def_lineWidth;
    }

    public static void setDef_lineWidth(float def_lineWidth) {
        DataSerie.def_lineWidth = def_lineWidth;
    }

    //returns the closest DP to the one entered (usually by clicking)
    public DataPoint getClosestDP(DataPoint click, double tolX, double tolY) {
        if (tolX < 0)
            tolX = 1.0;
        if (tolY < 0)
            tolY = 5000;
        DataPoint closest = null;
        double minDiffX = Double.MAX_VALUE / 2.5;
        double minDiffY = Double.MAX_VALUE / 2.5;
        for (int i = 0; i < this.getNpoints(); i++) {
            final DataPoint dp = this.getPoint(i);
            final double diffX = FastMath.abs(dp.getX() - click.getX());
            final double diffY = FastMath.abs(dp.getY() - click.getY());
            if ((diffX < tolX) && (diffY < tolY)) {
                if ((diffX + diffY) < (minDiffX + minDiffY)) {
                    minDiffX = diffX;
                    minDiffY = diffY;
                    closest = dp;
                    log.fine("index of the closest in loop (i)= " + i);
                    log.fine("index of the closest in loop (indexof dp)= " + this.seriePoints.indexOf(dp));
                }
            }
        }
        this.logdebug("index of the closest=" + this.seriePoints.indexOf(closest));
        return closest;
    }

    /**
     * @return the serieName
     */
    public String getSerieName() {
        return this.serieName;
    }

    /**
     * @param serieName the serieName to set
     */
    public void setSerieName(String serieName) {
        this.serieName = serieName;
    }

    private void logdebug(String s) {
        if (D2Dplot_global.isDebug())
            log.debug(s);
    }

}
