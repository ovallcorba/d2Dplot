package vava33.d2dplot.auxi;

import java.awt.Color;
import java.awt.geom.Ellipse2D;
import java.util.Locale;

import org.apache.commons.math3.util.FastMath;

public class PuntSolucio {

    private static int dincoSolPointSize = 2; //mida defecte, attribut de CLASSE
    private static boolean dincoSolPointSizeByFc = false;
    
    Color colorPunt; // no el faig estatic perque ho podriem assignar segons intensitat...
    float coordX, coordY;
    float fc;
    int h, k, l;
    int midaPunt; //AQUESTA ES LA MIDA PER INSTANCIA!!! (factor estructura!)
    float oscil; // valor d'oscilacio
    int refNumID; //el num que es llegeix del fitxer
    int seqNumber; //el num sequencial de la solucio a la llista de solucions (per si es borren, etc...)
    boolean manuallyAdded;
    
    public PuntSolucio(int refnum, float cx, float cy, int ih, int ik, int il, float festructura, float foscil) {

        if (dincoSolPointSizeByFc){
            midaPunt = FastMath.round((festructura * festructura) / 500.f);
            if (midaPunt <= 2)
                midaPunt = 2;            
        }else{
            midaPunt = dincoSolPointSize;
        }

        manuallyAdded = false; //default false
        
        // dades solucio
        this.coordX = cx;
        this.coordY = cy;
        this.fc = festructura;
        this.h = ih;
        this.k = ik;
        this.l = il;
        this.oscil = foscil;
        this.refNumID = refnum;
        this.seqNumber = refnum;
        this.colorPunt = Color.green;
    }

    public Ellipse2D.Float getEllipseAsDrawingPoint(){
        return new Ellipse2D.Float(this.getCoordX() - midaPunt/2,this.getCoordY() - midaPunt/2,midaPunt,midaPunt);
    }
    
	public PuntSolucio(int refnum, float cx, float cy, int ih, int ik, int il, float festructura, float foscil, Color col) {
        this(refnum, cx, cy, ih, ik, il, festructura, foscil);
        this.colorPunt = col;
    }
	
    public float getCoordX() {
		return coordX;
	}

	public void setCoordX(float coordX) {
		this.coordX = coordX;
	}

	public float getCoordY() {
		return coordY;
	}

	public void setCoordY(float coordY) {
		this.coordY = coordY;
	}

    public Color getColorPunt() {
        return this.colorPunt;
    }

    public String getHKL() {
        return this.h + "," + this.k + "," + this.l;
    }
    
    public String getHKLspaces(){
    	String hkl = String.format(Locale.ENGLISH, "%3d %3d %3d", this.h,this.k,this.l);
    	return hkl;
    }
    
    public int getH() {
        return h;
    }

    public void setH(int h) {
        this.h = h;
    }

    public int getK() {
        return k;
    }

    public void setK(int k) {
        this.k = k;
    }

    public int getL() {
        return l;
    }

    public void setL(int l) {
        this.l = l;
    }

    public float getOscil() {
        return oscil;
    }

    public float getFc() {
        return fc;
    }

    public void setColorPunt(Color col) {
        this.colorPunt = col;
    }

    public void setOscil(float oscil) {
        this.oscil = oscil;
    }

	public int getMidaPunt() {
		return midaPunt;
	}

	public void setMidaPunt(int midaPunt) {
		this.midaPunt = midaPunt;
	}
	
	public int getSeqNumber() {
	    return seqNumber;
	}

	public void setSeqNumber(int number) {
	    this.seqNumber = number;
	}

    public int getRefNumID() {
        return refNumID;
    }

    public void setRefNumID(int refNumID) {
        this.refNumID = refNumID;
    }

    public boolean isManuallyAdded() {
        return manuallyAdded;
    }

    public void setManuallyAdded(boolean manuallyAdded) {
        this.manuallyAdded = manuallyAdded;
    }

    public static int getDincoSolPointSize() {
        return dincoSolPointSize;
    }

    public static void setDincoSolPointSize(int dincoSolPointSize) {
        PuntSolucio.dincoSolPointSize = dincoSolPointSize;
    }

    public static boolean isDincoSolPointSizeByFc() {
        return dincoSolPointSizeByFc;
    }

    public static void setDincoSolPointSizeByFc(boolean dincoSolPointSizeByFc) {
        PuntSolucio.dincoSolPointSizeByFc = dincoSolPointSizeByFc;
    }

    @Override
    public String toString(){
        //s'ha de mostrar això:
        //        78      1954       773         0    7    8        0.10      0.00
        return String.format(" %9d %9d %9d      %4d %4d %4d %11.2f %9.2f", this.getSeqNumber(),FastMath.round(this.getCoordX()),FastMath.round(this.getCoordY()),this.getH(),this.getK(),this.getL(),this.getFc(),this.getOscil());

    }
    
}
