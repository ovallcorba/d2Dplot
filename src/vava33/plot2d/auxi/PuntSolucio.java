package vava33.plot2d.auxi;

import java.awt.Color;
import java.awt.geom.Ellipse2D;
import java.util.Locale;

import org.apache.commons.math3.util.FastMath;

public class PuntSolucio extends Ellipse2D.Float {

    private static final long serialVersionUID = 5416537118064675342L;
    Color colorPunt; // no el faig estatic perque ho podriem assignar segons intensitat...
    float coordX, coordY;
    float coordXclic, coordYclic; //les que s'assignen en mode indexHKL al clicar a la imatge
    float intenClic; //intensitat en el punt del click
    float fc;
    int h, k, l;
    int midaPunt;
    float oscil; // valor d'oscilacio

    public PuntSolucio(float cx, float cy, int ih, int ik, int il, float festructura, float foscil) {
        super();

        midaPunt = FastMath.round((festructura * festructura) / 500.f);
        if (midaPunt <= 2)
            midaPunt = 2;

        // dades solucio
        this.coordX = cx;
        this.coordY = cy;
        this.fc = festructura;
        this.h = ih;
        this.k = ik;
        this.l = il;
        this.oscil = foscil;

        // dades per l'esfera a pintar
        this.x = cx - midaPunt / 2;
        this.y = cy - midaPunt / 2;
        this.width = midaPunt;
        this.height = midaPunt;

        this.colorPunt = Color.green;
        
        // dades indexacio per click (-1 perque no s'ha fet)
        this.coordXclic=-1;
        this.coordYclic=-1;
        this.intenClic=-1;
    }

	public PuntSolucio(float cx, float cy, int ih, int ik, int il, float festructura, float foscil, Color col) {
        this(cx, cy, ih, ik, il, festructura, foscil);
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
    

    public float getOscil() {
        return oscil;
    }

    public void setColorPunt(Color col) {
        this.colorPunt = col;
    }

    public void setOscil(float oscil) {
        this.oscil = oscil;
    }

	public float getCoordXclic() {
		return coordXclic;
	}

	public void setCoordXclic(float coordXclic) {
		this.coordXclic = coordXclic;
	}

	public float getCoordYclic() {
		return coordYclic;
	}

	public void setCoordYclic(float coordYclic) {
		this.coordYclic = coordYclic;
	}

	public float getIntenClic() {
		return intenClic;
	}

	public void setIntenClic(float intenClic) {
		this.intenClic = intenClic;
	}

	public int getMidaPunt() {
		return midaPunt;
	}

	public void setMidaPunt(int midaPunt) {
		this.midaPunt = midaPunt;
	}

}
