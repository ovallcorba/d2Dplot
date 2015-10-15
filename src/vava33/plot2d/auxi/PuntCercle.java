package vava33.plot2d.auxi;

import java.awt.Color;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.util.Locale;

import org.apache.commons.math3.util.FastMath;

public class PuntCercle implements Comparable<PuntCercle>{

    private static Color colorCercle = Color.red;
    private static Color colorPunt = Color.green;
    private static int midaPunt = 8;

    public static Color getColorCercle() {return colorCercle;}
    public static Color getColorPunt() {return colorPunt;}
    public static void setColorCercle(Color colorCercle) {PuntCercle.colorCercle = colorCercle;}
    public static void setColorPunt(Color colorPunt) {PuntCercle.colorPunt = colorPunt;}
    
    private Ellipse2D.Float cercle;
    int intensity;
    private Ellipse2D.Float punt;
    private EllipsePars ellipse;
    double t2; // angle 2 theta en RADIANTS
    float x, y; // coordenades del pixel

    // punt angle intensitat
    public PuntCercle(Point2D.Float p, double angle, int inten) {
        this.t2 = angle;
        this.x = (float) p.getX();
        this.y = (float) p.getY();
        this.setPunt(new Ellipse2D.Float(x - midaPunt / 2, y - midaPunt / 2, midaPunt, midaPunt));
        this.intensity = inten;
    }

    // es crea un punt i el cercle que el cont� (donat el radi)
    public PuntCercle(Point2D.Float p, float xCentre, float yCentre) {
        // p es el punt clicat ja passat a pixels, afegirem un "punt" (centrant
        // una esfera petita d'aresta Xpx)
        // la mida punt es pot fer interactiva mes endavant si es vol
        x = (float) p.getX();
        y = (float) p.getY();
        setPunt(new Ellipse2D.Float(x - midaPunt / 2, y - midaPunt / 2, midaPunt, midaPunt));
        float radi = FastMath.round(FastMath.sqrt(FastMath.pow(x - xCentre, 2) + FastMath.pow(y - yCentre, 2)));
        setCercle(new Ellipse2D.Float(xCentre - radi, yCentre - radi, 2 * radi, 2 * radi));
    }

    // tota la informacio junta
    public PuntCercle(Point2D.Float p, float xCentre, float yCentre, double angle, int inten) {
        // p es el punt clicat ja passat a pixels, afegirem un "punt" (centrant
        // una esfera petita d'aresta Xpx)
        // la mida punt es pot fer interactiva mes endavant si es vol
        this.x = (float) p.getX();
        this.y = (float) p.getY();
        setPunt(new Ellipse2D.Float(x - midaPunt / 2, y - midaPunt / 2, midaPunt, midaPunt));
        float radi = FastMath.round(FastMath.sqrt(FastMath.pow(x - xCentre, 2) + FastMath.pow(y - yCentre, 2)));
        setCercle(new Ellipse2D.Float(xCentre - radi, yCentre - radi, 2 * radi, 2 * radi));
        this.t2 = angle;
        this.intensity = inten;
    }
    
    // tota la informacio junta
    public PuntCercle(Point2D.Float p, Pattern2D patt2D, float angle, int inten) {
        // p es el punt clicat ja passat a pixels, afegirem un "punt" (centrant
        // una esfera petita d'aresta Xpx)
        // la mida punt es pot fer interactiva mes endavant si es vol
        this.x = (float) p.getX();
        this.y = (float) p.getY();
        setPunt(new Ellipse2D.Float(x - midaPunt / 2, y - midaPunt / 2, midaPunt, midaPunt));
        setEllipse(this.getElliPars(patt2D, p));
        this.t2 = angle;
        this.intensity = inten;
    }

    public Ellipse2D.Float getCercle() {
        return cercle;
    }

    public Ellipse2D.Float getPunt() {
        return punt;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    // recalcula el cercle (en cas que es canvii el centre)
    public void recalcular(float xCentre, float yCentre, double angle) {
        float radi = FastMath.round(FastMath.sqrt(FastMath.pow(x - xCentre, 2) + FastMath.pow(y - yCentre, 2)));
        setCercle(new Ellipse2D.Float(xCentre - radi, yCentre - radi, 2 * radi, 2 * radi));
        this.t2 = angle;
    }

    public void setCercle(Ellipse2D.Float cercle) {
        this.cercle = cercle;
    }

    public void setPunt(Ellipse2D.Float punt) {
        this.punt = punt;
    }

    public void setX(float x) {
        this.x = x;
    }

    public void setY(float y) {
        this.y = y;
    }

    public double getT2() {
        return t2;
    }
    public int getIntensity() {
        return intensity;
    }
    public void setIntensity(int intensity) {
        this.intensity = intensity;
    }
    @Override
    public String toString() {
        String linia = String.format(Locale.ENGLISH, "%8.2f" + " " + "%8.2f" + " " + "%8.3f" + " " + "%6d", x, y,
                FastMath.toDegrees(this.t2), this.intensity);
        return linia;
    }
    @Override
    public int compareTo(PuntCercle o) {
        // compareTo should return < 0 if this is supposed to be
        // less than other, > 0 if this is supposed to be greater than 
        // other and 0 if they are supposed to be equal
        if (this.getT2()>=o.getT2()){
            return 1;
        }
        return -1;
    }
    
    //pattern contains calibration info
    private EllipsePars getElliPars(Pattern2D patt2D, Point2D.Float pixel){
        
        double twothRad = patt2D.calcT2new(pixel, false);
//        double phiRad = patt2D.getAzimAngle(FastMath.round(pixel.x), FastMath.round(pixel.y), false);
        double phiRad = FastMath.toRadians(patt2D.getRotDeg());
        
        
        double tantth = FastMath.tan(twothRad);
        double sintth = FastMath.sin(twothRad);
        double costth = FastMath.cos(twothRad);
        double tiltrad = FastMath.toRadians(patt2D.getTiltDeg());
        double tanTilt = FastMath.tan(tiltrad);
        double sinTilt = FastMath.sin(tiltrad);
        double cosTilt = FastMath.cos(tiltrad);
        double tmenys = FastMath.tan((twothRad-tiltrad)/2);
        double tmes = FastMath.tan((twothRad+tiltrad)/2);
        double distPix = patt2D.getDistMD()/patt2D.getPixSx();
        
        double fmes = distPix*tanTilt*sintth/(cosTilt+sintth);
        double fmenys = distPix*tanTilt*sintth/(cosTilt-sintth);
        
        double vmes = distPix*(tanTilt+(1+tmenys)/(1-tmenys))*sintth/(cosTilt+sintth);
        double vmenys = distPix*(tanTilt+(1-tmes)/(1+tmes))*sintth/(cosTilt-sintth);
        
        double rMaj = (vmes+vmenys)/2;
        double rMen = FastMath.sqrt((vmes+vmenys)*(vmes+vmenys) - (fmes+fmenys)*(fmes+fmenys))/2;
        double zdis = (fmes-fmenys)/2;
//      #NB: zdis is || to major axis & phi is rotation of minor axis
//      #thus shift from beam to ellipse center is [Z*sin(phi),-Z*cos(phi)]
//          elcent = [cent[0]+zdis*sind(phi),cent[1]-zdis*cosd(phi)]
//          return elcent,phi,radii
        double ellicentX = patt2D.getCentrX()+zdis * FastMath.sin(phiRad);
        double ellicentY = patt2D.getCentrY()-zdis * FastMath.cos(phiRad);
        
        return new EllipsePars(rMaj, rMen, ellicentX, ellicentY,phiRad);
        
    }
    public EllipsePars getEllipse() {
        return ellipse;
    }
    public void setEllipse(EllipsePars ellipse) {
        this.ellipse = ellipse;
    }
    
    
}
