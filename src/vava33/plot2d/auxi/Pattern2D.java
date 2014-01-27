package vava33.plot2d.auxi;

import java.awt.Polygon;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Iterator;

import vava33.plot2d.MainFrame;

public class Pattern2D {
    
    // PARAMETRES IMATGE:
    short[][] intensityB2; // guardem [columna][fila] atencio: columnes=coordX,files=cordY
    int[][] intensityB4; // per si es volgués ampliar a utilitzar enters de moment no es pot
    int maxI, minI, meanI, nSatur;
    float sdevI;
    boolean B4intensities;
    float centrX, centrY;
    int dimX, dimY;
    private static int MAX_ZONES = 20;
    private ArrayList<MyPolygon4> exZones;
    private int margin = 0; //per salvar el bin (zones excloses)
    private int y0toMask = 0; //per salvar el bin (zones excloses)

    // PARAMETRES INSTRUMENTALS:
    float distMD, pixSx, pixSy, wavel;
    
    // altres variables
    int pixCount; // pixels totals de la imatge
    float scale;
    float millis; // temps que s'ha tardat a llegir la imatge
    public boolean oldBIN=false; //variable temporal que indica si és old o new BIN

    // Crea un pattern amb unes mides concretes X Y (pixels)
    public Pattern2D(int columnes_X, int files_Y, boolean useB4inten) {
        this.dimX = columnes_X;
        this.dimY = files_Y;
        if (!useB4inten) {
            intensityB2 = new short[dimX][dimY];
        } else {
            intensityB4 = new int[dimX][dimY];
        }
        this.B4intensities = useB4inten;

        // parametres instrumentals per defecte (-1)
        this.distMD = -1;
        this.pixSx = -1;
        this.pixSy = -1;
        this.wavel = -1;

        // inicialitzem arraylist
        this.exZones = new ArrayList<MyPolygon4>(MAX_ZONES);
        
        this.oldBIN=false;
    }

    // COMPLERT
    public Pattern2D(int columnes_X, int files_Y, float centreX, float centreY, int maxInt, int minInt, float escala,
            boolean useB4inten) {
        this(columnes_X, files_Y, useB4inten);
        this.centrX = centreX;
        this.centrY = centreY;
        this.maxI = maxInt;
        this.minI = minInt;
        this.scale = escala;
    }

    // Aquest metode comprova que s'hagin entrat els parametres
    // instrumentals/experimentals necessaris per
    // certs càlculs
    public boolean checkExpParam() {
        if (this.distMD <= 0 || this.pixSx <= 0 || this.pixSy <= 0) {
            return false;
        } else {
            return true;
        }
    }

    public float getCentrX() {
        return centrX;
    }

    public float getCentrY() {
        return centrY;
    }

    public int getDimX() {
        return dimX;
    }

    public int getDimY() {
        return dimY;
    }

    public float getDistMD() {
        return distMD;
    }

    public ArrayList<MyPolygon4> getExZones() {
        return exZones;
    }

    public String getInfo() {
        
//        String info = "  NX(cols)= " + dimX + " NY(rows)= " + dimY + "\n" + "  minI=" + minI + " maxI=" + maxI
//                + "  (Scale factor= " + FileUtils.dfX_3.format(1/scale) +")\n" + "  Xcent=" + FileUtils.dfX_3.format(centrX)
//                + " Ycent=" + FileUtils.dfX_3.format(centrY) + "\n" + "  Distance Sample-Detector (mm)=" 
//                + FileUtils.dfX_3.format(distMD) + "\n" + "  Pixel Size X Y (mm)= " + FileUtils.dfX_4.format(pixSx) + " "
//                + FileUtils.dfX_4.format(pixSy);
        String info = "  NX(cols)= " + dimX + " NY(rows)= " + dimY + "\n" + 
                "  minI=" + minI + " maxI=" + maxI + "  (Scale factor= " + FileUtils.dfX_3.format(1/scale) +"; Saturated= "+nSatur+")\n" + 
                "  Xcent=" + FileUtils.dfX_3.format(centrX) + " Ycent=" + FileUtils.dfX_3.format(centrY) + "\n" + 
                "  Distance Sample-Detector (mm)=" + FileUtils.dfX_3.format(distMD) + "\n" + 
                "  Pixel Size X Y (mm)= " + FileUtils.dfX_4.format(pixSx) + " " + FileUtils.dfX_4.format(pixSy);
        return info;
    }

    public short getIntenB2(int x_col, int y_row) {
        return intensityB2[x_col][y_row];
    }

    public int getIntenB4(int x_col, int y_row) {
        return intensityB4[x_col][y_row];
    }

    public int getMargin() {
        return margin;
    }

    public int getMaxI() {
        return maxI;
    }

    public float getMillis() {
        return millis;
    }

    public int getMinI() {
        return minI;
    }

    public int getPixCount() {
        return pixCount;
    }

    public float getPixSx() {
        return pixSx;
    }

    public float getPixSy() {
        return pixSy;
    }

    public float getScale() {
        return scale;
    }

    public float getWavel() {
        return wavel;
    }

    public boolean isB4intensities() {
        return B4intensities;
    }

    // metode que indica si un pixel (x,y) es troba dins d'una zona exclosa
    public boolean isInExZone(int x, int y) {
        Iterator<MyPolygon4> it = exZones.iterator();
        // primer comprovem el marge
        if (x <= margin || x >= dimX - margin)
            return true;
        if (y <= margin || y >= dimY - margin)
            return true;
        // ara les zones
        while (it.hasNext()) {
            MyPolygon4 r = it.next();
            if (r.contains(new Point2D.Float(x, y)))
                return true;
        }

        return false;
    }

    public void setB4intensities(boolean b4intensities) {
        B4intensities = b4intensities;
    }

    public void setCentrX(float centrX) {
        this.centrX = centrX;
    }

    public void setCentrY(float centrY) {
        this.centrY = centrY;
    }

    public void setDimX(int dimX) {
        this.dimX = dimX;
    }

    public void setDimY(int dimY) {
        this.dimY = dimY;
    }

    public void setDistMD(float distMD) {
        this.distMD = distMD;
    }

    public void setExpParam(float pixlx, float pixly, float sepod, float wl) {
        this.setPixSx(pixlx);
        this.setPixSy(pixly);
        this.setDistMD(sepod);
        this.setWavel(wl);
    }

    public void setIntenB2(int x_col, int y_row, short inten) {
        this.intensityB2[x_col][y_row] = inten;
    }

    public void setIntenB4(int x_col, int y_row, int inten) {
        this.intensityB4[x_col][y_row] = inten;
    }

    public void setMargin(int margin) {
        this.margin = margin;
    }

    public void setMaxI(int maxI) {
        this.maxI = maxI;
    }

    public void setMillis(float millis) {
        this.millis = millis;
    }

    public void setMinI(int minI) {
        this.minI = minI;
    }

    public void setPixCount(int pixCount) {
        this.pixCount = pixCount;
    }

    public void setPixSx(float pixSx) {
        this.pixSx = pixSx;
    }

    public void setPixSy(float pixSy) {
        this.pixSy = pixSy;
    }

    public void setScale(float scale) {
        this.scale = scale;
    }

    public void setWavel(float wavel) {
        this.wavel = wavel;
    }

    //METODE QUE RECALCULA L'ESCALA, MAXI, MINI (sense escalar per sobre mai)
    public void recalcScale(){
        //TODO
        this.setMaxI(0);
        this.setMinI(9999999);
        //dues passades, una per determinar l'escala i l'altre per aplicar-la
        for (int i = 0; i < this.getDimY(); i++) { // per cada fila (Y)
            for (int j = 0; j < this.getDimX(); j++) { // per cada columna (X)
                if(this.isInExZone(j, i)){
                    this.setIntenB2(j, i, (short)-1);
                    continue;
                }
                if(this.getIntenB2(j, i)<0)continue;
                int inten=Math.round(this.getIntenB2(j, i)*this.getScale());
                if(inten>this.getMaxI())this.setMaxI(inten);
                if(inten<this.getMinI())this.setMinI(inten);
            }
        }
        //ara tenim maxI i minI (mxI,mnI) veritables (amb escala aplicada), recalculem factor d'escala
        float oldScale=this.getScale();
        this.setScale(Math.max(this.getMaxI() / 32767.f, 1.000f));
        
        // ara aplico factor escala i guardo on i com toca
        for (int i = 0; i < this.getDimY(); i++) { // per cada fila (Y)
            for (int j = 0; j < this.getDimX(); j++) { // per cada columna (X)
                if(this.isInExZone(j, i)){
                    this.setIntenB2(j, i, (short)-1);
                    continue;
                }
                if(this.getIntenB2(j, i)<0)continue;
                int inten=Math.round(this.getIntenB2(j, i)*oldScale);
                this.setIntenB2(j, i, (short) (inten / this.getScale()));
            }
        }
        // corregim maxI i minI
        this.setMaxI((int) (this.getMaxI() / this.getScale()));
        this.setMinI((int) (this.getMinI() / this.getScale()));
    }

    public int getY0toMask() {
        return y0toMask;
    }

    public void setY0toMask(int y0toMask) {
        this.y0toMask = y0toMask;
    }
    
    //calcula i estableix els valors de meanI i sdevI de tota la imatge excepte mascares
    //també calcula els nombre de pixels saturats
    public void calcMeanI(){
    	int yacum=0;
    	int npix=0;
    	int satur=0;
        for (int i = 0; i < this.getDimY(); i++) { // per cada fila (Y)
            for (int j = 0; j < this.getDimX(); j++) { // per cada columna (X)
                if(this.getIntenB2(j, i)<0)continue;
                yacum=yacum+this.getIntenB2(j, i);
                npix=npix+1;
                
                if(this.getIntenB2(j, i)>=MainFrame.shortsize){
                	satur=satur+1;
                }
            }
        }
        this.nSatur=satur;
        
        this.meanI= Math.round((float)yacum/(float)npix);
        //desviacio
        int numerador=0;
        for (int i = 0; i < this.getDimY(); i++) { // per cada fila (Y)
            for (int j = 0; j < this.getDimX(); j++) { // per cada columna (X)
                if(this.getIntenB2(j, i)<0)continue;
                numerador=numerador + (this.getIntenB2(j, i)-this.meanI)*(this.getIntenB2(j, i)-this.meanI);
            }
        }
        this.sdevI=(float) Math.sqrt((float)numerador/(float)npix);
    }
    
    //retorna la mitjana de les intensitats de la imatge per sobre un llindar d'intensitat
    public int calcMeanI(int llindar){
    	int yacum=0;
    	int npix=0;
        for (int i = 0; i < this.getDimY(); i++) { // per cada fila (Y)
            for (int j = 0; j < this.getDimX(); j++) { // per cada columna (X)
                short iB2=this.getIntenB2(j, i);
            	if(iB2<0)continue;
            	if(iB2<llindar)continue;
                yacum=yacum+iB2;
                npix=npix+1;
            }
        }
        System.out.println("Imean(llindar)= "+Math.round((float)yacum/(float)npix));
        return Math.round((float)yacum/(float)npix);
    }

	public int getMeanI() {
		return meanI;
	}

	public void setMeanI(int meanI) {
		this.meanI = meanI;
	}

	public float getSdevI() {
		return sdevI;
	}

	public void setSdevI(float sdevI) {
		this.sdevI = sdevI;
	}
    
    // public void printParameters(){
    // this.stat(" Instrumental Parameters:");
    // this.stat("  - Distance Sample-Detector (mm)="+panelImatge.getDistMD());
    // this.stat("  - Pixel Size X Y (mm)= "+panelImatge.getPixSx()+" "+panelImatge.getPixSy());
    // this.stat("  - Beam Centre (pixel)= ("+patt2D.getCentrX()+","+patt2D.getCentrY()+")");
    // }
}
