package vava33.d2dplot.auxi;

import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

import org.apache.commons.math3.util.FastMath;

import vava33.d2dplot.D2Dplot_global;

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
    
    private static VavaLogger log = D2Dplot_global.getVavaLogger(ArcExZone.class.getName());

    private HashSet<Point> listPixelsZone;
    int maxX;
    int minX;
    int maxY;
    int minY;
    float t2max;
    float t2min;
    
    public ArcExZone(int cx, int xy, int halfRadWpx, float halfAzimApDeg, Pattern2D whereIbelong){
        this.px=cx;
        this.py=xy;
        this.halfRadialWthPx=halfRadWpx;
        this.halfAzimApertureDeg=halfAzimApDeg;
        this.patt2D = whereIbelong;
        this.setNclicks(3);
        calcRadialWth2t();
        calcListPixelsZone();
        
    }
    //empty zone (to do by clicking)
    public ArcExZone(Pattern2D whereIbelong){
        this.px=0;
        this.py=0;
        this.halfRadialWthPx=0;
        this.halfAzimApertureDeg=0;
        this.patt2D = whereIbelong;
        this.setNclicks(0);
        this.clickPix = new Point2D.Float[3];
    }
    
    private void incClick(){
        this.nclicks++;
    }
    
    //RETURNS TRUE WHEN REACHING 3 CLICKS
    public boolean addClickPoint(Point2D.Float pix){
        if (this.getNclicks()<=2){
            this.clickPix[this.getNclicks()]=pix;    
        }else{
            log.debug("clickNum>2, too many clicks");
        }
        this.incClick();
        if (this.nclicks>2){
            //sha acabat, calculem i retornem
            this.px=(int)this.clickPix[0].x;
            this.py=(int)this.clickPix[0].y;
            
            int pxr=(int)this.clickPix[1].x;
            int pyr=(int)this.clickPix[1].y;
            //modul del vector entre els dos
            float vx = px - pxr;
            float vy = py - pyr;
            this.halfRadialWthPx= (int) FastMath.sqrt(vx*vx+vy*vy);
            this.calcRadialWth2t();
            
            float azimCen = patt2D.getAzimAngle(px, py, true);
            float azimClick = patt2D.getAzimAngle((int)this.clickPix[2].x, (int)this.clickPix[2].y, true);
            
            //EL CLICK DE L'AZIMUT EL FORCEM A CLOCKWISE
            if (azimClick<azimCen)azimClick = azimClick + 360;
            this.halfAzimApertureDeg=(azimClick-azimCen);
            calcListPixelsZone();
            return true;
        }
        return false;
    }
    
    
    private void calcRadialWth2t(){
        //CALCUL radialwth2t
        //vector centre-pixel
        float vPCx=px-patt2D.getCentrX();
        float vPCy=patt2D.getCentrY()-py;
        
        //vector unitari
        float modul = (float) FastMath.sqrt(vPCx*vPCx + vPCy*vPCy);
        vPCx = vPCx/modul;
        vPCy = vPCy/modul;
        
        float pixelInX = vPCx * (modul-halfRadialWthPx); //in=intern
        float pixelInY = vPCy * (modul-halfRadialWthPx);
        float pixelExX = vPCx * (modul+halfRadialWthPx); //ex=extern
        float pixelExY = vPCy * (modul+halfRadialWthPx);
        
        pixelInX = patt2D.getCentrX() + pixelInX;
        pixelInY = patt2D.getCentrY() - pixelInY;
        pixelExX = patt2D.getCentrX() + pixelExX;
        pixelExY = patt2D.getCentrY() - pixelExY;
        
        this.radialWth2t = (float) (patt2D.calc2T(pixelExX, pixelExY, true) - patt2D.calc2T(pixelInX, pixelInY, true));
    }
    
    public void calcListPixelsZone(){

        this.listPixelsZone=new HashSet<Point>();
        
        EllipsePars elli = ImgOps.getElliPars(patt2D, new Point2D.Float(px,py));
        float azimAngle = patt2D.getAzimAngle(px, py, true); //azimut des del zero
        float azimMax = azimAngle + halfAzimApertureDeg;
        float azimMin = azimAngle - halfAzimApertureDeg;

        boolean fullRing = false;
        if (halfAzimApertureDeg >= 180.f){
            fullRing = true;
            halfAzimApertureDeg = 180.f;
            azimMin=0;
            azimMax=360;
        }
        
        log.writeNameNumPairs("config",true, "azimAngle,azimMax,azimMin", azimAngle,azimMax,azimMin);

        //COMPROVACIO DE PASSAR PEL "ZERO":
        if (azimMin<0){ 
            azimMin = azimMin +360;
        }
        if (azimMax<azimMin){
            azimMax = azimMax + 360;
        }
        
        log.writeNameNumPairs("config",true, "azimAngle,azimMax,azimMin", azimAngle,azimMax,azimMin);
        
        ArrayList<Point2D.Float> pixelsArc = elli.getEllipsePoints(azimMin, azimMax, 0.5f);
        log.debug("pixelsArc.size="+pixelsArc.size());
        float t2p = (float) patt2D.calc2T(px, py, true);
        t2max = (float) FastMath.min(t2p + radialWth2t/2.,patt2D.getMax2TdegCircle());
        t2min = (float) FastMath.max(0.1, t2p - radialWth2t/2.);
        
        //busquem el maxX, minX, maxY, minY de l'arc
        float[] xs = new float[pixelsArc.size()];
        float[] ys = new float[pixelsArc.size()];
        Iterator<Point2D.Float> itrp = pixelsArc.iterator();
        int n = 0;
        while (itrp.hasNext()){
            Point2D.Float p = itrp.next();
            xs[n] = p.x;
            ys[n] = p.y;
            n = n+1;
        }
        maxX = FastMath.round(ImgOps.findMax(xs)+halfRadialWthPx);
        minX = FastMath.round(ImgOps.findMin(xs)-halfRadialWthPx);
        maxY = FastMath.round(ImgOps.findMax(ys)+halfRadialWthPx);
        minY = FastMath.round(ImgOps.findMin(ys)-halfRadialWthPx);
        maxX = FastMath.min(maxX,patt2D.dimX-1);
        maxY = FastMath.min(maxY,patt2D.dimY-1);
        minX = FastMath.max(minX,0);
        minY = FastMath.max(minY,0);
        
        log.writeNameNumPairs("config", true, "maxX,minX,maxY,minY", maxX,minX,maxY,minY);
        
        //ara ja tenim el quadrat on hem de buscar
        for (int j=minY;j<=maxY;j++){
           for (int i=minX;i<=maxX;i++){
               //si esta fora la imatge o es mascara el saltem
               if(!patt2D.isInside(i, j))continue;
               t2p = (float)patt2D.calc2T(i, j, true);
               if((t2p<t2min)||(t2p>t2max))continue;
               if (fullRing){
                   this.listPixelsZone.add(new Point(i,j)); //l'afegim, vol dir que donem tota la volta! 180º
                   continue;
               }
               azimAngle = patt2D.getAzimAngle(i, j, true);
               if (azimMax>360){
                   if((azimAngle<=azimMin)&&((azimAngle+360)>=azimMax))continue;
               }else{//cas normal
                   if((azimAngle>=azimMax)||(azimAngle<=azimMin))continue;    
               }
               this.listPixelsZone.add(new Point(i,j));
           }
        }
    }
    
    public boolean contains(int pixX, int pixY){
        //comprovacions previes per tal d'speed up
        if (pixX>maxX)return false;
        if (pixX<minX)return false;
        if (pixY>maxY)return false;
        if (pixY<minY)return false;

        if (listPixelsZone.contains(new Point(pixX,pixY)))return true;
        return false;
    }
    
    public int getNclicks() {
        return nclicks;
    }
    public void setNclicks(int nclicks) {
        this.nclicks = nclicks;
    }
    
    public String toString(){
        return String.format("(x,y)= %d %d, halfRadWthPx= %d, halfAzimDeg= %.1f",px,py,halfRadialWthPx,halfAzimApertureDeg);
    }
    public int getPx() {
        return px;
    }
    public void setPx(int px) {
        this.px = px;
    }
    public int getPy() {
        return py;
    }
    public void setPy(int py) {
        this.py = py;
    }
    public float getRadialWth2t() {
        return radialWth2t;
    }
    public void setRadialWth2t(float radialWth2t) {
        this.radialWth2t = radialWth2t;
    }
    public int getHalfRadialWthPx() {
        return halfRadialWthPx;
    }
    public void setHalfRadialWthPx(int halfRadialWthPx) {
        this.halfRadialWthPx = halfRadialWthPx;
    }
    public float getHalfAzimApertureDeg() {
        return halfAzimApertureDeg;
    }
    public void setHalfAzimApertureDeg(float halfAzimApertureDeg) {
        this.halfAzimApertureDeg = halfAzimApertureDeg;
    }
}

