package com.vava33.d2dplot.auxi;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Locale;

import org.apache.commons.math3.util.FastMath;

import com.vava33.d2dplot.D2Dplot_global;
import com.vava33.jutils.VavaLogger;

public class Pattern1D {
    double t2i, t2f;
    double step; //stepsize
    ArrayList<PointPatt1D> points;
    private String comment;
    private static final String className = "Patt1D";
    private static VavaLogger log = D2Dplot_global.getVavaLogger(className);

    public class PointPatt1D {
        int counts;
        int npix;
        double desv;
        double t2;
        double intensity; //INTENSITY (means divided by npix and corrected for LP)

        public PointPatt1D() {
            this.counts = 0;
            this.npix = 0;
            this.intensity = 0;
            this.desv = 0;
        }

        public PointPatt1D(double t2) {
            this();
            this.t2 = t2;
        }

        public int getCounts() {
            return this.counts;
        }

        public void setCounts(int counts) {
            this.counts = counts;
        }

        public void addCounts(int counts) {
            this.counts = this.counts + counts;
        }

        public int getNpix() {
            return this.npix;
        }

        public void setNpix(int npix) {
            this.npix = npix;
        }

        public void addNpix(int npix) {
            this.npix = this.npix + npix;
        }

        public double getDesv() {
            return this.desv;
        }

        public void setDesv(double desv) {
            this.desv = desv;
        }

        public void addDesv(double desv) {
            this.desv = this.desv + desv;
        }

        public double getT2() {
            return this.t2;
        }

        public void setT2(double t2) {
            this.t2 = t2;
        }

        public double getIntensity() {
            return this.intensity;
        }

        public void setIntensity(double inten) {
            this.intensity = inten;
        }
    }

    public Pattern1D(double t2ini, double t2fin, double stepsize) {
        this.t2i = t2ini;
        this.t2f = t2fin;
        this.step = stepsize;
        this.comment = "";
        final int npoints = (int) (FastMath.round((t2fin - t2ini) / stepsize) + 1); //+1 perque volem incloure t2ini i t2fin
        this.points = new ArrayList<PointPatt1D>(npoints);
        //inicialitzem a zero els punts
        double t2p = t2ini;
        int toRemove = 0;
        for (int i = 0; i <= npoints; i++) {
            if (t2p > t2fin)
                toRemove = toRemove + 1;
            this.points.add(new PointPatt1D(t2p));
            t2p = t2p + stepsize;
        }
        //borrem els "extra"
        for (int i = 0; i < toRemove; i++) {
            this.points.remove(this.points.size() - 1);
        }

    }

    //	public Pattern1D() {
    //	    //new pattern1D empty
    //	    this.points = new ArrayList<PointPatt1D>();
    //	}
    //	
    //	public addPoint()

    //suma en un punt existent del vector punts
    public void sumPoint(int posicio, int counts, int npix, double desv) {
        this.getPoints().get(posicio).addCounts(counts);
        this.getPoints().get(posicio).addNpix(npix);
        this.getPoints().get(posicio).addDesv(desv);
    }

    public PointPatt1D getPoint(int pos) {
        return this.getPoints().get(pos);
    }

    //escriu fitxer dat
    //afegeix comentari i els simbols son #
    public void writeDAT(File fileout, String title) {
        if (fileout == null)
            return;
        log.debug("locale=" + Locale.getDefault().toString());
        //Escriure fitxer format dat
        try {
            final PrintWriter output = new PrintWriter(new BufferedWriter(new FileWriter(fileout)));
            // ESCRIBIM AL FITXER:
            String linia = String.format("# %s", title);
            output.println(linia);
            linia = String.format("# %s", this.getComment());
            output.println(linia);
            final Iterator<PointPatt1D> itp = this.getPoints().iterator();
            while (itp.hasNext()) {
                final PointPatt1D p = itp.next();
                final long t2 = FastMath.round(p.getT2() * 10000);
                final double t2p = t2 / 10000.0;
                if (p.getNpix() == 0) {
                    p.setNpix(1);
                }
                linia = String.format("  %.7E  %.7E  %.7E", t2p, p.getIntensity(), p.getDesv());
                output.println(linia);
            }
            output.close();

        } catch (final IOException e) {
            if (D2Dplot_global.isDebug())
                e.printStackTrace();
            log.warning("Error writting dat file");
        }
    }

    public double getDesvForA2Trange(double t2ini, double t2fin) {
        final Iterator<PointPatt1D> itp = this.getPoints().iterator();
        int npoints = 0;
        double sumSD = 0;
        while (itp.hasNext()) {
            final PointPatt1D p = itp.next();
            if ((p.t2 >= t2ini) && (p.t2 <= t2fin)) {
                if (p.npix <= 0)
                    continue;
                sumSD = sumSD + (p.desv / p.npix);
                npoints = npoints + 1;
            }
        }
        if (npoints > 0) {
            return sumSD / npoints;
        } else {
            return 0;
        }
    }

    public double getT2i() {
        return this.t2i;
    }

    public void setT2i(double t2i) {
        this.t2i = t2i;
    }

    public double getT2f() {
        return this.t2f;
    }

    public void setT2f(double t2f) {
        this.t2f = t2f;
    }

    public double getStep() {
        return this.step;
    }

    public void setStep(double step) {
        this.step = step;
    }

    public ArrayList<PointPatt1D> getPoints() {
        return this.points;
    }

    public void setPoints(ArrayList<PointPatt1D> points) {
        this.points = points;
    }

    public String getComment() {
        return this.comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

}
