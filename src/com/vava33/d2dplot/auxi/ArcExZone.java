package com.vava33.d2dplot.auxi;

import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

import org.apache.commons.math3.util.FastMath;

import com.vava33.d2dplot.D2Dplot_global;
import com.vava33.jutils.VavaLogger;

public class ArcExZone {

    private int px; //pixel central zona
    private int py;

    private int halfRadialWthPx; //radial width in pixels of the zone
    private float radialWth2t; //in 2theta

    private float halfAzimApertureDeg; //azimutal aperture in degrees
    private Pattern2D patt2D;

    private int nclicks;
    private Point2D.Float[] clickPix;

    private static final String className = "ArcExZone";
    private static VavaLogger log = D2Dplot_global.getVavaLogger(className);

    private HashSet<Point> listPixelsZone;
    int maxX;
    int minX;
    int maxY;
    int minY;
    float t2max;
    float t2min;

    public ArcExZone(int cx, int xy, int halfRadWpx, float halfAzimApDeg, Pattern2D whereIbelong) {
        this.px = cx;
        this.py = xy;
        this.halfRadialWthPx = halfRadWpx;
        this.halfAzimApertureDeg = halfAzimApDeg;
        this.patt2D = whereIbelong;
        this.setNclicks(3);
        if (this.patt2D != null) {
            this.calcRadialWth2t();
            this.calcListPixelsZone();
        }
    }

    public void recalcZones() {
        this.calcRadialWth2t();
        this.calcListPixelsZone();
    }

    //empty zone (to do by clicking)
    public ArcExZone(Pattern2D whereIbelong) {
        this.px = 0;
        this.py = 0;
        this.halfRadialWthPx = 0;
        this.halfAzimApertureDeg = 0;
        this.patt2D = whereIbelong;
        this.setNclicks(0);
        this.clickPix = new Point2D.Float[3];
    }

    private void incClick() {
        this.nclicks++;
    }

    //RETURNS TRUE WHEN REACHING 3 CLICKS
    public boolean addClickPoint(Point2D.Float pix) {
        if (this.getNclicks() <= 2) {
            this.clickPix[this.getNclicks()] = pix;
        } else {
            log.debug("clickNum>2, too many clicks");
        }
        this.incClick();
        if (this.nclicks > 2) {
            //sha acabat, calculem i retornem
            this.px = (int) this.clickPix[0].x;
            this.py = (int) this.clickPix[0].y;

            final int pxr = (int) this.clickPix[1].x;
            final int pyr = (int) this.clickPix[1].y;
            //modul del vector entre els dos
            final float vx = this.px - pxr;
            final float vy = this.py - pyr;
            this.halfRadialWthPx = (int) FastMath.sqrt(vx * vx + vy * vy);
            this.calcRadialWth2t();

            final float azimCen = this.patt2D.getAzimAngle(this.px, this.py, true);
            float azimClick = this.patt2D.getAzimAngle((int) this.clickPix[2].x, (int) this.clickPix[2].y, true);

            //EL CLICK DE L'AZIMUT EL FORCEM A CLOCKWISE
            if (azimClick < azimCen)
                azimClick = azimClick + 360;
            this.halfAzimApertureDeg = (azimClick - azimCen);
            this.calcListPixelsZone();
            return true;
        }
        return false;
    }

    private void calcRadialWth2t() {
        //CALCUL radialwth2t
        //vector centre-pixel
        float vPCx = this.px - this.patt2D.getCentrX();
        float vPCy = this.patt2D.getCentrY() - this.py;

        //vector unitari
        final float modul = (float) FastMath.sqrt(vPCx * vPCx + vPCy * vPCy);
        vPCx = vPCx / modul;
        vPCy = vPCy / modul;

        float pixelInX = vPCx * (modul - this.halfRadialWthPx); //in=intern
        float pixelInY = vPCy * (modul - this.halfRadialWthPx);
        float pixelExX = vPCx * (modul + this.halfRadialWthPx); //ex=extern
        float pixelExY = vPCy * (modul + this.halfRadialWthPx);

        pixelInX = this.patt2D.getCentrX() + pixelInX;
        pixelInY = this.patt2D.getCentrY() - pixelInY;
        pixelExX = this.patt2D.getCentrX() + pixelExX;
        pixelExY = this.patt2D.getCentrY() - pixelExY;

        this.radialWth2t = (float) (this.patt2D.calc2T(pixelExX, pixelExY, true)
                - this.patt2D.calc2T(pixelInX, pixelInY, true));
    }

    public void calcListPixelsZone() {

        this.listPixelsZone = new HashSet<Point>();

        final EllipsePars elli = ImgOps.getElliPars(this.patt2D, new Point2D.Float(this.px, this.py));
        float azimAngle = this.patt2D.getAzimAngle(this.px, this.py, true); //azimut des del zero
        float azimMax = azimAngle + this.halfAzimApertureDeg;
        float azimMin = azimAngle - this.halfAzimApertureDeg;

        boolean fullRing = false;
        if (this.halfAzimApertureDeg >= 180.f) {
            fullRing = true;
            this.halfAzimApertureDeg = 180.f;
            azimMin = 0;
            azimMax = 360;
        }

        log.writeNameNumPairs("config", true, "azimAngle,azimMax,azimMin", azimAngle, azimMax, azimMin);

        //COMPROVACIO DE PASSAR PEL "ZERO":
        if (azimMin < 0) {
            azimMin = azimMin + 360;
        }
        if (azimMax < azimMin) {
            azimMax = azimMax + 360;
        }

        log.writeNameNumPairs("config", true, "azimAngle,azimMax,azimMin", azimAngle, azimMax, azimMin);

        final ArrayList<Point2D.Float> pixelsArc = elli.getEllipsePoints(azimMin, azimMax, 0.5f);
        log.debug("pixelsArc.size=" + pixelsArc.size());
        float t2p = (float) this.patt2D.calc2T(this.px, this.py, true);
        this.t2max = (float) FastMath.min(t2p + this.radialWth2t / 2., this.patt2D.getMax2TdegCircle());
        this.t2min = (float) FastMath.max(0.1, t2p - this.radialWth2t / 2.);

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
        this.maxX = FastMath.round(ImgOps.findMax(xs) + this.halfRadialWthPx);
        this.minX = FastMath.round(ImgOps.findMin(xs) - this.halfRadialWthPx);
        this.maxY = FastMath.round(ImgOps.findMax(ys) + this.halfRadialWthPx);
        this.minY = FastMath.round(ImgOps.findMin(ys) - this.halfRadialWthPx);
        this.maxX = FastMath.min(this.maxX, this.patt2D.getDimX() - 1);
        this.maxY = FastMath.min(this.maxY, this.patt2D.getDimY() - 1);
        this.minX = FastMath.max(this.minX, 0);
        this.minY = FastMath.max(this.minY, 0);

        log.writeNameNumPairs("config", true, "maxX,minX,maxY,minY", this.maxX, this.minX, this.maxY, this.minY);

        //ara ja tenim el quadrat on hem de buscar
        for (int j = this.minY; j <= this.maxY; j++) {
            for (int i = this.minX; i <= this.maxX; i++) {
                //si esta fora la imatge o es mascara el saltem
                if (!this.patt2D.isInside(i, j))
                    continue;
                t2p = (float) this.patt2D.calc2T(i, j, true);
                if ((t2p < this.t2min) || (t2p > this.t2max))
                    continue;
                if (fullRing) {
                    this.listPixelsZone.add(new Point(i, j)); //l'afegim, vol dir que donem tota la volta! 180º
                    continue;
                }
                azimAngle = this.patt2D.getAzimAngle(i, j, true);
                if (azimMax > 360) {
                    if ((azimAngle <= azimMin) && ((azimAngle + 360) >= azimMax))
                        continue;
                } else {//cas normal
                    if ((azimAngle >= azimMax) || (azimAngle <= azimMin))
                        continue;
                }
                this.listPixelsZone.add(new Point(i, j));
            }
        }
    }

    public boolean contains(int pixX, int pixY) {
        //comprovacions previes per tal d'speed up
        if (pixX > this.maxX)
            return false;
        if (pixX < this.minX)
            return false;
        if (pixY > this.maxY)
            return false;
        if (pixY < this.minY)
            return false;

        if (this.listPixelsZone.contains(new Point(pixX, pixY)))
            return true;
        return false;
    }

    public int getNclicks() {
        return this.nclicks;
    }

    public void setNclicks(int nclicks) {
        this.nclicks = nclicks;
    }

    @Override
    public String toString() {
        return String.format("(x,y)= %d %d, halfRadWthPx= %d, halfAzimDeg= %.1f", this.px, this.py,
                this.halfRadialWthPx, this.halfAzimApertureDeg);
    }

    public int getPx() {
        return this.px;
    }

    public void setPx(int px) {
        this.px = px;
    }

    public int getPy() {
        return this.py;
    }

    public void setPy(int py) {
        this.py = py;
    }

    public float getRadialWth2t() {
        return this.radialWth2t;
    }

    public void setRadialWth2t(float radialWth2t) {
        this.radialWth2t = radialWth2t;
    }

    public int getHalfRadialWthPx() {
        return this.halfRadialWthPx;
    }

    public void setHalfRadialWthPx(int halfRadialWthPx) {
        this.halfRadialWthPx = halfRadialWthPx;
    }

    public float getHalfAzimApertureDeg() {
        return this.halfAzimApertureDeg;
    }

    public void setHalfAzimApertureDeg(float halfAzimApertureDeg) {
        this.halfAzimApertureDeg = halfAzimApertureDeg;
    }

    public Pattern2D getPatt2D() {
        return this.patt2D;
    }

    public void setPatt2D(Pattern2D patt2d) {
        this.patt2D = patt2d;
    }
}
