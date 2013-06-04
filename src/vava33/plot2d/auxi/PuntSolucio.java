package vava33.plot2d.auxi;

import java.awt.Color;
import java.awt.geom.Ellipse2D;

public class PuntSolucio extends Ellipse2D.Float {

    private static final long serialVersionUID = 5416537118064675342L;
    Color colorPunt; // no el faig estatic perque ho podriem assignar segons intensitat...
    float coordX, coordY;
    float fc;
    int h, k, l;
    int midaPunt;
    float oscil; // valor d'oscilacio

    public PuntSolucio(float cx, float cy, int ih, int ik, int il, float festructura, float foscil) {
        super();

        midaPunt = Math.round((festructura * festructura) / 500.f);
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
    }

    public PuntSolucio(float cx, float cy, int ih, int ik, int il, float festructura, float foscil, Color col) {
        this(cx, cy, ih, ik, il, festructura, foscil);
        this.colorPunt = col;
    }

    public Color getColorPunt() {
        return this.colorPunt;
    }

    public String getHKL() {
        return this.h + "," + this.k + "," + this.l;
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

}
