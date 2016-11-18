package vava33.d2dplot.auxi;

import java.awt.geom.Point2D;

import org.apache.commons.math3.util.FastMath;

public class Peak implements Comparable<Peak> {

    private Point2D.Float pixelCentre;
    private int nSatur; //saturats en el pic
    private int nVeinsEixam; //pics veins
    private boolean nearMask;

    //aixo s'estableix a la integracio
    private float radi,ymax,fh2,sfh2,p,dsp,intRad2th,azimAper,Ymean,Ybkg,YbkgSD;    
    private int npix, nbkgpix,intRadPx;
    private Patt2Dzone zona;
    private boolean integrated; //sera true quan zona no sigui null, es a dir, s'hagi integrat
    
    //per escriure fitxers
    public static final String pcs_header = " NPEAK     XPIXEL     YPIXEL     RO_VAL         YMAX          FH2   sigma(FH2)        p  CODI INTRAD      D";
    public static final String all_header = " Npeak     Xpix     Ypix  Radi(px)      Ymax      Fh2   s(Fh2)      Ymean     Npix     Ybkg  s(Ybkg) nBkgPx RadWthPx RadWth2t AzimDeg      dsp  Nbour Nsatur NearMsk      p";
    
    public Peak(Point2D.Float pix,int neixam, int nSatur, boolean nearMask){
        this.pixelCentre=pix;
        this.nVeinsEixam=neixam;
        this.nSatur=nSatur;
        this.nearMask=nearMask;
        this.zona = null;
        this.integrated=false;
    }

    public Peak(Point2D.Float pix){
        this(pix,0,0,false);
    }
    
    public Peak(float x, float y){
        this(new Point2D.Float(x,y),0,0,false);
    }
    
    public Point2D.Float getPixelCentre() {
        return pixelCentre;
    }

    public void setPixelCentre(Point2D.Float pixelCentre) {
        this.pixelCentre = pixelCentre;
    }

    public int getnSatur() {
        return nSatur;
    }

    public void setnSatur(int nSatur) {
        this.nSatur = nSatur;
    }

    public int getnVeinsEixam() {
        return nVeinsEixam;
    }

    public void setnVeinsEixam(int nVeinsEixam) {
        this.nVeinsEixam = nVeinsEixam;
    }

    public boolean isNearMask() {
        return nearMask;
    }

    public void setNearMask(boolean nearMask) {
        this.nearMask = nearMask;
    }

    public Patt2Dzone getZona() {
        return zona;
    }

    public void setZona(Patt2Dzone zona) {
        this.zona = zona;
        if (this.zona==null){
            this.setIntegrated(false);
            return;
        }
        this.setIntegrated(true);
    }

    //iosc es l'eix de tilt, 1=horitzontal, 2=vertical, 0=noOscil must be setted before
    public void calculate(boolean lpcorr){
        
        if (!isIntegrated())return;
        
        int ipX = (int)(this.getPixelCentre().x);
        int ipY = (int)(this.getPixelCentre().y);
        
        double[] lpfac = {1,1,1};
        if (lpcorr){
            lpfac = ImgOps.corrLP(this.getZona().getPatt2d(), ipX, ipY, 1, 1, this.getZona().getPatt2d().getIscan(), false);
        }
        
        float inten = this.getZona().getYsum()-(this.getZona().getNpix()*this.getZona().getYbkg());
        float esdinten = 0;
        this.p = 0;
        this.fh2 = 0;
        this.sfh2 = 0;
        if (inten > 0){
            if(lpfac[1]>0){
                esdinten = (float)FastMath.sqrt(this.getZona().getYsum()+2*this.getZona().getYbkgdesv())/inten;
                this.fh2 = (float)FastMath.sqrt(inten*lpfac[1]*lpfac[2]);
                this.sfh2 = (float)FastMath.sqrt(esdinten*(fh2*fh2));                
            }else{
                esdinten=-1.f;
                this.fh2 = -1;
                this.sfh2 = -1;
                this.nearMask=true; //posem flag near mask perque esta en zona lp erronia
            }
            this.p = (float) (FastMath.PI*FastMath.pow(this.getZona().getYmax()/inten,2.d/3.d));
        }else{
            esdinten=-1.f;
            this.fh2 = -1;
            this.sfh2 = -1;
            this.p=-1.f;
        }
                    
        float vPCx=this.getPixelCentre().x-this.getZona().getPatt2d().getCentrX();//vector centre-pixel
        float vPCy=this.getZona().getPatt2d().getCentrY()-this.getPixelCentre().y;
        this.radi = (float) FastMath.sqrt((vPCx*vPCx)+(vPCy*vPCy));
        this.dsp = (float) this.getZona().getPatt2d().calcDsp(this.getZona().getPatt2d().calc2T(this.getPixelCentre(), false));
        
        this.ymax = this.zona.getYmax()-this.zona.getYbkg();
        this.Ymean = this.zona.getYmean();
        this.npix = this.zona.getNpix();
        this.Ybkg = this.zona.getYbkg();
        this.YbkgSD = this.zona.getYbkgdesv();
        this.nbkgpix = this.zona.getBkgpt();
        this.intRadPx = this.zona.getIntradPix();
        this.azimAper = this.zona.getAzimAngle();
        this.intRad2th = ImgOps.getTol2TFromIntRad(this.getZona().getPatt2d(), this.getPixelCentre().x,this.getPixelCentre().y,this.zona.getIntradPix());

//        log.writeNameNums("CONFIG", true, "ymax, inten, FastMath.pow(pz.getYmax()/inten,2/3)", this.getZona().getYmax(), inten, FastMath.pow(this.getZona().getYmax()/inten,2/3));
    }
    
    public boolean isIntegrated() {
        return integrated;
    }

    public void setIntegrated(boolean integrated) {
        this.integrated = integrated;
    }
    
    
    public float getRadi() {
        return radi;
    }

    public void setRadi(float radi) {
        this.radi = radi;
    }

    public float getYmax() {
        return ymax;
    }

    public void setYmax(float ymax) {
        this.ymax = ymax;
    }

    public float getFh2() {
        return fh2;
    }

    public void setFh2(float fh2) {
        this.fh2 = fh2;
    }

    public float getSfh2() {
        return sfh2;
    }

    public void setSfh2(float sfh2) {
        this.sfh2 = sfh2;
    }

    public float getP() {
        return p;
    }

    public void setP(float p) {
        this.p = p;
    }

    public float getDsp() {
        return dsp;
    }

    public void setDsp(float dsp) {
        this.dsp = dsp;
    }

    public float getIntRad2th() {
        return intRad2th;
    }

    public void setIntRad2th(float intRad2th) {
        this.intRad2th = intRad2th;
    }

    public float getAzimAper() {
        return azimAper;
    }

    public void setAzimAper(float azimAper) {
        this.azimAper = azimAper;
    }

    public float getYmean() {
        return Ymean;
    }

    public void setYmean(float ymean) {
        Ymean = ymean;
    }

    public float getYbkg() {
        return Ybkg;
    }

    public void setYbkg(float ybkg) {
        Ybkg = ybkg;
    }

    public float getYbkgSD() {
        return YbkgSD;
    }

    public void setYbkgSD(float ybkgSD) {
        YbkgSD = ybkgSD;
    }

    public int getNpix() {
        return npix;
    }

    public void setNpix(int npix) {
        this.npix = npix;
    }

    public int getNbkgpix() {
        return nbkgpix;
    }

    public void setNbkgpix(int nbkgpix) {
        this.nbkgpix = nbkgpix;
    }

    public int getIntRadPx() {
        return intRadPx;
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

    public String getFormmattedStringPCS(){
        int flag = 1;
        if (nVeinsEixam>1) flag = nVeinsEixam;
        if (nearMask) flag = -1;
        if (nSatur>0) flag = nSatur*(-1);
        return String.format("%10.2f %10.2f %10.2f %12.2f %12.2f %12.2f %8.3f %5d %4d %9.4f",
                pixelCentre.x,pixelCentre.y,radi,ymax,fh2,sfh2,p,flag,intRadPx,dsp);
    }
    
    public String getFormmattedStringAll(){
        String msk = "No";
        if (nearMask) msk = "Yes";
        return String.format("%8.2f %8.2f %8.2f %10.2f %8.2f %8.2f %10.2f %8d %8.2f %8.2f %6d %8d %8.2f %7.2f %8.4f %6d %6d %7s %6.3f",
                pixelCentre.x,pixelCentre.y,radi,ymax,fh2,sfh2,Ymean,npix,Ybkg,YbkgSD,nbkgpix,intRadPx,intRad2th,azimAper,dsp,nVeinsEixam,nSatur,msk,p);
    }

    @Override
    public int compareTo(Peak arg0) {
        if (this.ymax>arg0.ymax){
            return 0;
        }else{
            return 1;    
        }
    }
}
