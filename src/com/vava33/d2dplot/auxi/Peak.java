package com.vava33.d2dplot.auxi;

import java.awt.geom.Point2D;

import org.apache.commons.math3.util.FastMath;

public class Peak implements Comparable<Peak> {

    private Point2D.Float pixelCentre;
    private int nSatur; //saturats en el pic
    private int nVeinsEixam; //pics veins
    private boolean nearMask;

    //aixo s'estableix a la integracio
    private float radi, ymax, fh2, sfh2, p, dsp, intRad2th, azimAper, Ymean, Ybkg, YbkgSD;
    private int npix, nbkgpix, intRadPx;
    private Patt2Dzone zona;
    private boolean integrated; //sera true quan zona no sigui null, es a dir, s'hagi integrat

    //per escriure fitxers
    public static final String pcs_header = " NPEAK     XPIXEL     YPIXEL     RO_VAL         YMAX          FH2   sigma(FH2)        p  CODI INTRAD      D";
    public static final String all_header = " Npeak     Xpix     Ypix  Radi(px)      Ymax      Fh2   s(Fh2)      Ymean     Npix     Ybkg  s(Ybkg) nBkgPx RadWthPx RadWth2t AzimDeg      dsp  Nbour Nsatur NearMsk      p";
    public static final String out_header = " NPEAK     XPIXEL    YPIXEL    RO_VAL        YMAX         FH      sigma(FH)    p    CODI INTRAD   D";

    public Peak(Point2D.Float pix, int neixam, int nSatur, boolean nearMask) {
        this.pixelCentre = pix;
        this.nVeinsEixam = neixam;
        this.nSatur = nSatur;
        this.nearMask = nearMask;
        this.zona = null;
        this.integrated = false;
    }

    public Peak(Point2D.Float pix) {
        this(pix, 0, 0, false);
    }

    public Peak(float x, float y) {
        this(new Point2D.Float(x, y), 0, 0, false);
    }

    public Point2D.Float getPixelCentre() {
        return this.pixelCentre;
    }

    public void setPixelCentre(Point2D.Float pixelCentre) {
        this.pixelCentre = pixelCentre;
    }

    public int getnSatur() {
        return this.nSatur;
    }

    public void setnSatur(int nSatur) {
        this.nSatur = nSatur;
    }

    public int getnVeinsEixam() {
        return this.nVeinsEixam;
    }

    public void setnVeinsEixam(int nVeinsEixam) {
        this.nVeinsEixam = nVeinsEixam;
    }

    public boolean isNearMask() {
        return this.nearMask;
    }

    public void setNearMask(boolean nearMask) {
        this.nearMask = nearMask;
    }

    public Patt2Dzone getZona() {
        return this.zona;
    }

    public void setZona(Patt2Dzone zona) {
        this.zona = zona;
        if (this.zona == null) {
            this.setIntegrated(false);
            return;
        }
        this.setIntegrated(true);
    }

    //iosc es l'eix de tilt, 1=horitzontal, 2=vertical, 0=noOscil must be setted before
    public void calculate(boolean lpcorr) {

        if (!this.isIntegrated())
            return;

        final int ipX = (int) (this.getPixelCentre().x);
        final int ipY = (int) (this.getPixelCentre().y);

        double[] lpfac = { 1, 1, 1 };
        if (lpcorr) {
            lpfac = ImgOps.corrLP(this.getZona().getPatt2d(), ipX, ipY, 1, 1, this.getZona().getPatt2d().getIscan(),
                    false);
        }

        final float inten = this.getZona().getYsum() - (this.getZona().getNpix() * this.getZona().getYbkg());
        float esdinten = 0;
        this.p = 0;
        this.fh2 = 0;
        this.sfh2 = 0;
        if (inten > 0) {
            if (lpfac[1] > 0) {
                //esdinten = (float)FastMath.sqrt(this.getZona().getYsum()+2*this.getZona().getYbkgdesv())/inten;
                esdinten = (float) FastMath.sqrt(inten) / inten;
                this.fh2 = (float) (inten * lpfac[1] * lpfac[2]);
                this.sfh2 = esdinten * this.fh2;
            } else {
                esdinten = 0.f;
                this.fh2 = 0;
                this.sfh2 = 0;
                this.nearMask = true; //posem flag near mask perque esta en zona lp erronia
            }
            this.p = (float) (FastMath.PI * FastMath.pow(this.getZona().getYmax() / inten, 2.d / 3.d));
        } else {
            esdinten = 0.f;
            this.fh2 = 0;
            this.sfh2 = 0;
            this.p = 0.f;
        }

        final float vPCx = this.getPixelCentre().x - this.getZona().getPatt2d().getCentrX();//vector centre-pixel
        final float vPCy = this.getZona().getPatt2d().getCentrY() - this.getPixelCentre().y;
        this.radi = (float) FastMath.sqrt((vPCx * vPCx) + (vPCy * vPCy));
        this.dsp = (float) this.getZona().getPatt2d()
                .calcDsp(this.getZona().getPatt2d().calc2T(this.getPixelCentre(), false));

        this.ymax = this.zona.getYmax() - this.zona.getYbkg();
        this.Ymean = this.zona.getYmean();
        this.npix = this.zona.getNpix();
        this.Ybkg = this.zona.getYbkg();
        this.YbkgSD = this.zona.getYbkgdesv();
        this.nbkgpix = this.zona.getBkgpt();
        this.intRadPx = this.zona.getIntradPix();
        this.azimAper = this.zona.getAzimAngle();
        this.intRad2th = ImgOps.getTol2TFromIntRad(this.getZona().getPatt2d(), this.getPixelCentre().x,
                this.getPixelCentre().y, this.zona.getIntradPix());

        //        log.writeNameNums("CONFIG", true, "ymax, inten, FastMath.pow(pz.getYmax()/inten,2/3)", this.getZona().getYmax(), inten, FastMath.pow(this.getZona().getYmax()/inten,2/3));
    }

    public boolean isIntegrated() {
        return this.integrated;
    }

    public void setIntegrated(boolean integrated) {
        this.integrated = integrated;
    }

    public float getRadi() {
        return this.radi;
    }

    public void setRadi(float radi) {
        this.radi = radi;
    }

    public float getYmax() {
        return this.ymax;
    }

    public void setYmax(float ymax) {
        this.ymax = ymax;
    }

    public float getFh2() {
        return this.fh2;
    }

    public void setFh2(float fh2) {
        this.fh2 = fh2;
    }

    public float getSfh2() {
        return this.sfh2;
    }

    public void setSfh2(float sfh2) {
        this.sfh2 = sfh2;
    }

    public float getP() {
        return this.p;
    }

    public void setP(float p) {
        this.p = p;
    }

    public float getDsp() {
        return this.dsp;
    }

    public void setDsp(float dsp) {
        this.dsp = dsp;
    }

    public float getIntRad2th() {
        return this.intRad2th;
    }

    public void setIntRad2th(float intRad2th) {
        this.intRad2th = intRad2th;
    }

    public float getAzimAper() {
        return this.azimAper;
    }

    public void setAzimAper(float azimAper) {
        this.azimAper = azimAper;
    }

    public float getYmean() {
        return this.Ymean;
    }

    public void setYmean(float ymean) {
        this.Ymean = ymean;
    }

    public float getYbkg() {
        return this.Ybkg;
    }

    public void setYbkg(float ybkg) {
        this.Ybkg = ybkg;
    }

    public float getYbkgSD() {
        return this.YbkgSD;
    }

    public void setYbkgSD(float ybkgSD) {
        this.YbkgSD = ybkgSD;
    }

    public int getNpix() {
        return this.npix;
    }

    public void setNpix(int npix) {
        this.npix = npix;
    }

    public int getNbkgpix() {
        return this.nbkgpix;
    }

    public void setNbkgpix(int nbkgpix) {
        this.nbkgpix = nbkgpix;
    }

    public int getIntRadPx() {
        return this.intRadPx;
    }

    public void setIntRadPx(int intRadPx) {
        this.intRadPx = intRadPx;
    }

    public static String getPcsHeader() {
        return pcs_header;
    }

    public static String getAllHeader() {
        return all_header;
    }

    public String getFormmattedStringPCS() {
        int flag = 1;
        if (this.nVeinsEixam > 1)
            flag = this.nVeinsEixam;
        if (this.nearMask)
            flag = -1;
        if (this.nSatur > 0)
            flag = this.nSatur * (-1);
        //        return String.format("%10.2f %10.2f %10.2f %12.2f %12.2f %12.2f %8.3f %5d %4d %9.4f",
        //                pixelCentre.x,pixelCentre.y,radi,ymax,fh2,sfh2,p,flag,FastMath.round(intRadPx/2.0f),dsp);
        return String.format("%10.2f %10.2f %10.2f %12.2f %12.2f %12.2f %8.3f %5d %4d %9.4f", this.pixelCentre.x,
                this.pixelCentre.y, this.radi, this.ymax, FastMath.sqrt(this.fh2), FastMath.sqrt(this.sfh2), this.p,
                flag, FastMath.round(this.intRadPx / 2.0f), this.dsp);
    }

    // NPEAK     XPIXEL    YPIXEL    RO_VAL        YMAX         FH      sigma(FH)    p    CODI INTRAD   D
    //      1   1031.08   1316.02    292.06       2762.50        9.42        0.78   0.739    1   11    0.1538
    public String getFormmattedStringOUT_singleCryst() {
        int flag = 1;
        if (this.nVeinsEixam > 1)
            flag = this.nVeinsEixam;
        if (this.nearMask)
            flag = -1;
        if (this.nSatur > 0)
            flag = this.nSatur * (-1);
        //        return String.format("%10.2f%10.2f%10.2f%14.2f%12.2f%12.2f%8.3f%5d%5d%10.4f",
        //                pixelCentre.x,pixelCentre.y,radi,ymax,FastMath.sqrt(fh2),FastMath.sqrt(sfh2),p,flag,intRadPx,dsp);
        return String.format("%10.2f%10.2f%10.2f%14.2f%12.2f%12.2f%8.3f%5d%5d%10.4f", this.pixelCentre.x,
                this.pixelCentre.y, this.radi, this.ymax, FastMath.sqrt(this.fh2), FastMath.sqrt(this.sfh2), this.p,
                flag, this.intRadPx, this.getZona().getPatt2d().calc2T(this.getPixelCentre(), false));
    }

    public String getFormmattedStringAll() {
        String msk = "No";
        if (this.nearMask)
            msk = "Yes";
        return String.format(
                "%8.2f %8.2f %8.2f %10.2f %8.2f %8.2f %10.2f %8d %8.2f %8.2f %6d %8d %8.2f %7.2f %8.4f %6d %6d %7s %6.3f",
                this.pixelCentre.x, this.pixelCentre.y, this.radi, this.ymax, this.fh2, this.sfh2, this.Ymean,
                this.npix, this.Ybkg, this.YbkgSD, this.nbkgpix, this.intRadPx, this.intRad2th, this.azimAper, this.dsp,
                this.nVeinsEixam, this.nSatur, msk, this.p);
    }

    @Override
    public String toString() {
        return String.format("%8.2f %8.2f %8.2f", this.pixelCentre.x, this.pixelCentre.y, this.ymax);
    }

    @Override
    public int compareTo(Peak arg0) {
        //        if (this.ymax>arg0.ymax)return -1;
        //        if (this.ymax<arg0.ymax)return 1;
        //        return 0;
        //per mantenir el que tenia ho he de posar al reves TODO març 2019
        return Float.compare(arg0.ymax, this.ymax);
    }
    //    public int compareTo(Peak arg0) {
    //        if (this.ymax>arg0.ymax){
    //            return 0;
    //        }else{
    //            return 1;    
    //        }
    //    }

    public boolean isSatur() {
        if (this.nSatur > 0)
            return true;
        return false;
    }

    public boolean isDiamond() {
        if (this.isSatur() && this.p < 0.25)
            return true; // si la forma del pic es rara i està saturat
        if (this.isSatur()) {
            if (((float) this.nSatur / this.npix) > 0.02)
                return true; //mes del 2% dels pixels saturats
        }
        return false;
    }
}
