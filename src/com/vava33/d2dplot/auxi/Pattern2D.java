package com.vava33.d2dplot.auxi;

import java.awt.Point;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

import org.apache.commons.math3.util.FastMath;

import com.vava33.d2dplot.D2Dplot_global;
import com.vava33.d2dplot.PeakSearch;
import com.vava33.jutils.FileUtils;
import com.vava33.jutils.VavaLogger;

public class Pattern2D {

    private static final String className = "Patt2D";
    private static VavaLogger log = D2Dplot_global.getVavaLogger(className);
    private static float t2tolDegSelectedPoints = 0.05f;

    // PARAMETRES IMATGE:
    private ArrayList<Pixel> pixs;
    private int dimX, dimY, maxI, minI, nSatur, nPkSatur, saturValue;
    private float meanI, sdevI, scale, centrX, centrY, distMD, pixSx, pixSy, wavel;
    private float tiltDeg, rotDeg; //tilt and rot of the detector in degrees
    private float costilt, sintilt, cosrot, sinrot;
    private float omeIni, omeFin, acqTime;
    private ImgFileUtils.SupportedReadExtensions fileFormat;

    private int pixCount; // pixels totals de la imatge
    private int iscan;   // ISCAN=1 (GIR AL VOLTANT DE i); =2 (AL VOLTANT DE j) (POT VALER ZERO), EIX VERTICAL = j, es a dir per MSPD, ISCAN=2

    private final ArrayList<OrientSolucio> solucions; // contindra les solucions
    private final ArrayList<PuntClick> puntsCercles;
    private ArrayList<Peak> pkSearchResult; //de la cerca de pics
    private Pattern2D fonsPerCercaPk;
    private final int NbkgForPksearch = 3;

    //zones excloses
    private ArrayList<PolyExZone> polyExZones;
    private ArrayList<ArcExZone> arcExZones;
    private ArrayList<Point> ExZpaintPixels;
    private int exz_margin = 0; //per salvar el bin (zones excloses)
    private int exz_threshold = 0; //per salvar el bin (zones excloses)
    private int exz_detcircle = 0; //cercle del detector

    // Variables relacionades amb el fitxer
    private File imgfile; //file from which the data has been read (if any)
    private float millis; // temps que s'ha tardat a llegir la imatge

    // Crea un pattern amb unes mides concretes X Y (pixels) i parametres inicialitzats per defecte
    public Pattern2D(int columnes_X, int files_Y) {
        this.dimX = columnes_X;
        this.dimY = files_Y;
        this.pixCount = this.dimX * this.dimY;

        this.centrX = -1;
        this.centrY = -1;
        this.maxI = 0;
        this.minI = Integer.MAX_VALUE;
        this.meanI = -1;
        this.scale = 1.0f;

        //parametres adquisicio per defecte
        this.omeIni = 0;
        this.omeFin = 0;
        this.acqTime = -1;

        // parametres instrumentals per defecte (-1)
        this.distMD = -1;
        this.pixSx = -1;
        this.pixSy = -1;
        this.wavel = -1;
        this.iscan = 2;
        this.saturValue = D2Dplot_global.satur65;

        //tilt defecte
        this.setTiltDeg(0);
        this.setRotDeg(0);

        //init Inten array
        this.initIntenArray();

        // inicialitzem arraylists
        this.polyExZones = new ArrayList<PolyExZone>();
        this.arcExZones = new ArrayList<ArcExZone>();
        this.ExZpaintPixels = new ArrayList<Point>();
        this.solucions = new ArrayList<OrientSolucio>();
        this.puntsCercles = new ArrayList<PuntClick>();
        this.exz_margin = 0;
        this.exz_threshold = 0;
        this.exz_detcircle = 0;
    }

    // COMPLERT
    public Pattern2D(int columnes_X, int files_Y, float centreX, float centreY, int maxInt, int minInt, float escala) {
        this(columnes_X, files_Y);
        this.centrX = centreX;
        this.centrY = centreY;
        this.maxI = maxInt;
        this.minI = minInt;
        this.scale = escala;
    }

    public Pattern2D(Pattern2D dataIn, boolean copyIntensities) {
        //copiem tots els parametres de l'altre imatge
        this(dataIn.getDimX(), dataIn.getDimY(), dataIn.getCentrX(), dataIn.getCentrY(), dataIn.getMaxI(),
                dataIn.getMinI(), dataIn.getScale());
        this.setExpParam(dataIn.getPixSx(), dataIn.getPixSy(), dataIn.getDistMD(), dataIn.getWavel(),
                dataIn.getTiltDeg(), dataIn.getRotDeg());
        this.setAcqTime(dataIn.getAcqTime());
        this.setOmeFin(dataIn.getOmeFin());
        this.setOmeIni(dataIn.getOmeIni());
        for (int j = 0; j < this.getDimY(); j++) { // per cada fila (Y)
            for (int i = 0; i < this.getDimX(); i++) { // per cada columna (X)
                if (copyIntensities) {
                    this.setInten(i, j, dataIn.getInten(i, j), true);
                } else {
                    this.setInten(i, j, 0, true);
                }
                this.getPixel(i, j).setExcluded(dataIn.getPixel(i, j).isExcluded());
            }
        }
        if (!copyIntensities) {
            this.setMaxI(0);
            this.setMinI(0);
            this.setMeanI(0);
            this.setSdevI(0);
        } else {
            this.calcMeanI();
            //        	this.recalcMaxMinI(); //aixo ja ho fa copyExZonesFromImage
        }

        //mantenim zones excloses
        this.copyExZonesFromImage(dataIn);
    }

    public void initIntenArray() {
        /*
         * la questió aquí es que posem que tenim una imatge de 5x3, 5 columnes i 3 files (X=5, Y=3), arraylist de 15
         *
         * 1a fila... indexs 0 to 4
         * 2a fila... indexs 5 to 9
         * 3a fila... indexs 10 to 14
         *
         * per accedir al pixel (x,y)=(3,2) cal anar a la fila 2 columna 3, considerant indexs començant a zero (resto 1
         * a x,y):
         * *
         * serà l'element (y-1)*X+(x-1), es a dir (2-1)*5+(3-1) = 7... que és realment la posició correcta
         *
         * SI considerem sempre el zero, que ÉS EL CAS, el pixel (3,2) es realment el (2,1), no cal restar 1:
         *
         * y*X+x = 1*5+2 = 7.
         * 
         */
        this.pixs = new ArrayList<Pixel>(this.dimY * this.dimX);
        final long start = System.nanoTime();
        for (int j = 0; j < this.getDimY(); j++) { // per cada fila (Y)
            for (int i = 0; i < this.getDimX(); i++) { // per cada columna (X)
                this.pixs.add(new Pixel(i, j, 0));
            }
        }
        log.debug("pixs size=" + this.pixs.size());
        final long end = System.nanoTime();
        final float milis = (float) ((end - start) / 1000000d);
        log.debug("init time arraylist= " + (int) milis + " ms)");
    }

    public int[][] getIntenAsIntArray() {
        final int[][] inten = new int[this.dimY][this.dimX];
        for (int j = 0; j < this.getDimY(); j++) { // per cada fila (Y)
            for (int i = 0; i < this.getDimX(); i++) { // per cada columna (X)
                inten[j][i] = this.getInten(i, j);
            }
        }
        return inten;
    }

    public void copyInstrParamFromOtherPatt(Pattern2D in) { //es podria afegir centrX i centreY...
        this.setExpParam(in.getPixSx(), in.getPixSy(), in.getDistMD(), in.getWavel(), in.getTiltDeg(), in.getRotDeg());
    }

    public void copyExZonesFromImage(Pattern2D im) {
        this.setArcExZones(im.getArcExZones());
        this.setExz_detcircle(im.getExz_detcircle());
        this.setExz_margin(im.getExz_margin());
        this.setExz_threshold(im.getExz_threshold());
        this.setExZpaintPixels(im.getExZpaintPixels());
        this.setPolyExZones(im.getPolyExZones());
        this.recalcExcludedPixels();
        this.recalcMaxMinI();
    }

    public void zeroIntensities() {
        for (int j = 0; j < this.getDimY(); j++) { // per cada fila (Y)
            for (int i = 0; i < this.getDimX(); i++) { // per cada columna (X)
                this.setInten(i, j, 0, true);
            }
        }
        this.setMaxI(0);
        this.setMinI(0);
        this.setMeanI(0);
        this.setSdevI(0);
    }

    public boolean esIgualA(Pattern2D altreDiag) { //RENOMBRAR A ESIGUALINTENSITATA
        for (int j = 0; j < this.getDimY(); j++) { // per cada fila (Y)
            for (int i = 0; i < this.getDimX(); i++) { // per cada columna (X)
                if (this.getInten(i, j) != altreDiag.getInten(i, j)) {
                    return false;
                }
            }
        }
        return true;
    }

    public String getInfo() {
        final String info = "  NX(cols)= " + this.dimX + " NY(rows)= " + this.dimY + "\n" + "  minI=" + this.minI
                + " maxI=" + this.maxI + "  (Scale factor= " + FileUtils.dfX_3.format(1 / this.scale) + "; Saturated= "
                + this.nSatur + ")\n" + "  Xcent=" + FileUtils.dfX_3.format(this.centrX) + " Ycent="
                + FileUtils.dfX_3.format(this.centrY) + "\n" + "  Distance Sample-Detector (mm)="
                + FileUtils.dfX_3.format(this.distMD) + "\n" + "  Pixel Size X Y (mm)= "
                + FileUtils.dfX_4.format(this.pixSx) + " " + FileUtils.dfX_4.format(this.pixSy);
        return info;
    }

    //important methods to get pixel and its intensity from x_col y_row
    public Pixel getPixel(int x_col, int y_row) {
        return this.pixs.get(y_row * this.getDimX() + x_col);
    }

    public int getInten(int x_col, int y_row) {
        return this.getPixel(x_col, y_row).getIntensity();
    }

    public void setInten(int x_col, int y_row, int inten, boolean updateErr) {
        this.getPixel(x_col, y_row).setIntensity(inten, updateErr);
    }

    public double calc2T(float col_px, float row_py, boolean degrees) {

        final double distMDpix = (this.getDistMD() / this.getPixSx()) / this.costilt;
        final float vCPx = col_px - this.getCentrX();
        final float vCPy = this.getCentrY() - row_py;

        //El tema es que ara el zero es a les X... al fer servir conveni fit2D, el eix de rot horitzontal es per ROT=90. El eix ROT=0 es el vertical, es a dir el que era abans les Y ara son les X
        final double testRot = FastMath.toRadians(this.rotDeg);
        final double distprima = (vCPx * FastMath.cos(testRot) - vCPy * FastMath.sin(testRot)) * this.sintilt; //acw rotation
        double t2p = (vCPx * vCPx) + (vCPy * vCPy) - (distprima * distprima);
        t2p = FastMath.sqrt(t2p);
        t2p = t2p / (distMDpix + distprima); //EL SIGNE VA INCORPORAT A DISTPRIMA
        t2p = FastMath.atan(t2p);
        if (degrees) {
            t2p = FastMath.toDegrees(t2p);
        }
        return t2p;
    }

    public double calc2T(Point2D.Float pixel, boolean degrees) {
        return this.calc2T(pixel.x, pixel.y, degrees);
    }

    //t2 en radiants
    public double calcDsp(double t2rad) {
        final float wl = this.getWavel();
        if (wl > 0) {
            final double d = wl / (2 * FastMath.sin(t2rad / 2.));
            return d;
        }
        return -1.d;
    }

    //t2 en radiants!
    public double dspToT2(double dsp, boolean degrees) {
        final float wl = this.getWavel();
        if (wl > 0) {
            final double t2 = 2 * FastMath.asin(wl / (2 * dsp));
            if (degrees) {
                return FastMath.toDegrees(t2);
            } else {
                return t2;
            }
        }
        return -1.d;
    }

    //es zona exclosa si el pixel te la info de excluded o te intensitat negativa
    public boolean isExcluded(int x_col, int y_row) {
        final Pixel p = this.getPixel(x_col, y_row);
        if (p.isExcluded())
            return true;
        if (p.getIntensity() < 0)
            return true;
        return false;
    }

    public boolean isInPolyExZone(int x, int y) {
        final Iterator<PolyExZone> it = this.polyExZones.iterator();
        while (it.hasNext()) {
            final PolyExZone r = it.next();
            if (r.contains(new Point2D.Float(x, y)))
                return true;
        }
        return false;
    }

    public boolean isInArcExZone(int x, int y) {
        final Iterator<ArcExZone> it2 = this.arcExZones.iterator();
        while (it2.hasNext()) {
            final ArcExZone a = it2.next();
            if (a.contains(x, y))
                return true;
        }
        return false;
    }

    public boolean isMouseFreeEzZone(int x, int y) {
        final Iterator<Point> it2 = this.ExZpaintPixels.iterator();
        final Point p = new Point(x, y);
        while (it2.hasNext()) {
            final Point a = it2.next();
            if (a.equals(p))
                return true;
        }
        return false;
    }

    public boolean isExcludedByThresholds(int x, int y) {
        //be, si es menys 1 directament tornemtrue
        if (this.getInten(x, y) < 0)
            return true;

        // primer mirem que la Y ESTIGUI PER SOBRE EL THRESHOLD
        if (this.getInten(x, y) < this.exz_threshold)
            return true;

        // després comprovem el marge
        if (x <= this.exz_margin || x >= this.dimX - this.exz_margin)
            return true;
        if (y <= this.exz_margin || y >= this.dimY - this.exz_margin)
            return true;

        //ara el cercle del detector
        if (this.exz_detcircle > 0) {
            final int xc = (int) (this.dimX / 2.f) - this.exz_detcircle;
            final int yc = (int) (this.dimY / 2.f) - this.exz_detcircle;
            final Ellipse2D cercle = new Ellipse2D.Float(xc, yc, this.exz_detcircle * 2, this.exz_detcircle * 2);
            if (!cercle.contains(x, y))
                return true;
        }
        return false;
    }

    // metode que comprova si un pixel (x,y) es troba dins d'una de les zones excloses definides
    // caldrà cridar-lo despres de llegir una EXZ
    public boolean isInExZoneFullCheck(int x, int y) {
        if (this.isExcludedByThresholds(x, y))
            return true;
        if (this.isInPolyExZone(x, y))
            return true;
        if (this.isInArcExZone(x, y))
            return true;
        if (this.isMouseFreeEzZone(x, y))
            return true;
        return false;
    }

    public void recalcExcludedPixels() {
        for (int j = 0; j < this.getDimY(); j++) { // per cada fila (Y)
            for (int i = 0; i < this.getDimX(); i++) { // per cada columna (X)
                if (this.isInExZoneFullCheck(i, j)) {
                    this.getPixel(i, j).setExcluded(true);
                } else {
                    this.getPixel(i, j).setExcluded(false);
                }
            }
        }
    }


    //retorna true si el pixel esta dins
    public boolean isInside(int col_X, int row_Y) {
        if ((row_Y >= 0) && (row_Y < this.getDimY()) && (col_X >= 0) && (col_X < this.getDimX())) {
            return true;
        }
        return false;
    }

    //calcula i estableix els valors de meanI i sdevI de tota la imatge excepte mascares. Tambe calcula els nombre de pixels saturats
    public void calcMeanI() {
        long yacum = 0;
        int npix = 0;
        int satur = 0;

        final ArrayList<Pixel> satPixels = new ArrayList<Pixel>();

        for (int j = 0; j < this.getDimY(); j++) { // per cada fila (Y)
            for (int i = 0; i < this.getDimX(); i++) { // per cada columna (X)
                if (this.isExcluded(i, j))
                    continue;
                yacum = yacum + this.getInten(i, j);
                npix = npix + 1;
                if (this.getInten(i, j) >= this.getSaturValue()) {
                    satur = satur + 1;
                    satPixels.add(new Pixel(i, j, this.getSaturValue()));
                }
            }
        }
        this.nSatur = satur;
        this.meanI = (float) yacum / (float) npix;
        //desviacio
        double sd = 0.0;

        for (int j = 0; j < this.getDimY(); j++) { // per cada fila (Y)
            for (int i = 0; i < this.getDimX(); i++) { // per cada columna (X)
                if (this.isExcluded(i, j))
                    continue;
                final long a = (long) (this.getInten(i, j) - this.meanI);
                final long b = a * a;
                final double c = b / (float) (npix - 1);
                sd = sd + c;
            }
        }
        this.sdevI = (float) FastMath.sqrt(FastMath.abs((float) sd));

        //afegit el 160906  calcul pics saturats (a partir dels pixels)
        //REVISAR HP:     Centre del pic (HP(J)) amb fons substret (BACKJ)
        final int valorPixelsConsiderarMateixPic = PeakSearch.def_zoneR;
        final int nsatur = this.getnSatur();
        final int[] idpk = new int[nsatur];
        final float[] xp = new float[nsatur];
        final float[] yp = new float[nsatur];
        final float[] hp = new float[nsatur];
        for (int i = 0; i < nsatur; i++) {
            idpk[i] = 0;
        }
        int nsat = -1;
        for (int i = 0; i < nsatur; i++) {
            if (idpk[i] == 0) {
                nsat = nsat + 1;
                xp[nsat] = 0.0f;
                yp[nsat] = 0.0f;
                hp[nsat] = 0.0f;
                int nequiv = 0;
                for (int j = i; j < nsatur; j++) {
                    final int arg = (satPixels.get(j).getX() - satPixels.get(i).getX())
                            * (satPixels.get(j).getX() - satPixels.get(i).getX())
                            + (satPixels.get(j).getY() - satPixels.get(i).getY())
                                    * (satPixels.get(j).getY() - satPixels.get(i).getY());
                    final float dist = (float) FastMath.sqrt(arg);
                    if (dist <= valorPixelsConsiderarMateixPic) {
                        idpk[j] = i;
                        xp[nsat] = xp[nsat] + satPixels.get(j).getX();
                        yp[nsat] = yp[nsat] + satPixels.get(j).getY();
                        hp[nsat] = hp[nsat] + this.getInten(satPixels.get(j).getX(), satPixels.get(j).getY());
                        nequiv = nequiv + 1;
                    }
                }
                xp[nsat] = xp[nsat] / nequiv;
                yp[nsat] = yp[nsat] / nequiv;
                hp[nsat] = hp[nsat] / nequiv;
            }
        }
        this.nPkSatur = nsat;
        log.printmsg("DEBUG", String.format("nombre de pics saturats=%d", this.nPkSatur));

    }

    //retorna la mitjana de les intensitats de la imatge per sobre un llindar d'intensitat -- Nov18 passat a float
    public float calcMeanI(int minItoConsider) {
        long yacum = 0;
        int npix = 0;
        for (int j = 0; j < this.getDimY(); j++) { // per cada fila (Y)
            for (int i = 0; i < this.getDimX(); i++) { // per cada columna (X)
                if (this.isExcluded(i, j))
                    continue;
                final int iB4 = this.getInten(i, j);
                if (iB4 < minItoConsider)
                    continue;
                yacum = yacum + iB4;
                npix = npix + 1;
            }
        }
        log.debug("Imean(llindar)= " + (float) yacum / (float) npix);
        return (float) yacum / (float) npix;
    }

    public void recalcMaxMinI() {
        this.setMaxI(0);
        this.setMinI(9999999);
        for (int j = 0; j < this.getDimY(); j++) { // per cada fila (Y)
            for (int i = 0; i < this.getDimX(); i++) { // per cada columna (X)
                if (this.isExcluded(i, j))
                    continue;
                if (this.getInten(i, j) > this.getMaxI())
                    this.setMaxI(this.getInten(i, j));
                if (this.getInten(i, j) < this.getMinI())
                    this.setMinI(this.getInten(i, j));
            }
        }
    }

    //donat un saturVal es calcula l'escala (caldra dividir les intensitats per aquest valor de retorn)
    public float calcScale(int saturVal) {
        int maxi = 0;
        //dnomes una passada per determinar l'escala
        for (int j = 0; j < this.getDimY(); j++) { // per cada fila (Y)
            for (int i = 0; i < this.getDimX(); i++) { // per cada columna (X)
                if (this.isExcluded(i, j))
                    continue;
                final int inten = this.getInten(i, j);
                if (inten > maxi)
                    maxi = inten;
            }
        }
        return FastMath.max((float) maxi / (float) (saturVal - 1), 1.000f); //-1 per assegurar...
    }

    public float[] getFactAngleAmplada(int x_col, int y_row) {
        final float[] factors = new float[2];
        final float maxAngle = (float) this.calc2T(0, 0, true);
        //aplicarem un factor lineal de 0.5 a 1.5? per 2T=0 2T=max (angle) o al reves (amplada)
        final float angle = (float) this.calc2T(x_col, y_row, true);
        //interpolem sobre les rectes
        //ANGLE
        final float x1 = 0.0f;
        final float y1 = 3.0f;
        final float x2 = maxAngle;
        final float y2 = 0.8f;
        factors[0] = ((y2 - y1) / (x2 - x1)) * angle + y1 - ((y2 - y1) / (x2 - x1)) * x1;
        //AMPLADA
        factors[1] = 1.f;
        return factors;
    }

    //donat un pixel i una aresta calcula la intensitat mitjana del quadrat centrat a aquest pixel TODO:passat a float
    public float calcIntSquare(int col_X, int row_Y, int aresta, boolean self) {
        final int hA = (int) FastMath.round((aresta) / 2.);
        int npix = 0;
        float meanInten = -1;

        for (int j = row_Y - hA; j <= row_Y + hA; j++) {
            for (int i = col_X - hA; i <= col_X + hA; i++) {
                //fora imatge o mascara o propi ciclem
                if (!this.isInside(i, j))
                    continue;
                if (this.isExcluded(i, j))
                    continue;
                if ((!self) && (i == col_X) && (j == row_Y))
                    continue;
                //afegim
                meanInten = meanInten + this.getInten(i, j);
                npix = npix + 1;
            }
        }
        if (npix > 0) {
            meanInten = (meanInten + 1) / (npix); //recuperem el -1 inicial
        }
        return meanInten;
    }

    public float getMax2Tdeg() {
        final float f1 = (float) this.calc2T(this.getDimX() - 1, this.getDimY() - 1, true);
        final float f2 = (float) this.calc2T(0, 0, true);
        final float f3 = (float) this.calc2T(this.getDimX() - 1, 0, true);
        final float f4 = (float) this.calc2T(0, this.getDimY() - 1, true);
        return FastMath.max(FastMath.max(f1, f2), FastMath.max(f3, f4));
    }

    public float getMax2TdegCircle() {
        final float f1 = (float) this.calc2T(this.getCentrX(), 0.f, true);
        final float f2 = (float) this.calc2T(0.f, this.getCentrY(), true);
        final float f3 = (float) this.calc2T(this.getCentrX(), this.getDimY() - 1, true);
        final float f4 = (float) this.calc2T(this.getDimX() - 1, this.getCentrY(), true);
        return FastMath.min(FastMath.min(f1, f2), FastMath.min(f3, f4));
    }

    public void addSolucio(OrientSolucio s) {
        this.solucions.add(s);
        //recontem i reassingem colors
        OrientSolucio.setNumSolucions(this.getSolucions().size());
        final Iterator<OrientSolucio> itros = this.getSolucions().iterator();
        while (itros.hasNext()) {
            final OrientSolucio os = itros.next();
            os.setNumSolucio(this.getSolucions().indexOf(os));
            os.assignaColorSol();
        }

    }

    public void removeSolucio(OrientSolucio s) {
        this.solucions.remove(s);
        //recontem i repintem
        OrientSolucio.setNumSolucions(this.getSolucions().size());
        final Iterator<OrientSolucio> itros = this.getSolucions().iterator();
        while (itros.hasNext()) {
            final OrientSolucio os = itros.next();
            os.setNumSolucio(this.getSolucions().indexOf(os));
            os.assignaColorSol();
        }

    }

    //minim stepsize (2theta entre 2 pixels consecutius)
    public float calcMinStepsizeEstimateWithPixSizeAndDistMD() {
        final float picSize = FastMath.min(this.getPixSx(), this.getPixSy()); //en mm
        final float minstep = (float) FastMath.atan(picSize / this.getDistMD());
        return (float) FastMath.toDegrees(minstep);
    }

    //minim stepsize (2theta entre 2 pixels consecutius)
    public float calcMinStepsizeBy2Theta4Directions() {
        final double[] steps = new double[4];

        //faig al final a les 4 direccions pero al mig, JAN2019, ho fare a l'extrem, realment sera el minim
        final float cX = this.getCentrX();
        final float cY = this.getCentrY();
        //        float quartDimX = this.getDimX()/4;
        //        float quartDimY = this.getDimY()/4;

        //x+
        double t2a = this.calc2T(this.getDimX() - 1, cY, true);
        double t2b = this.calc2T(this.getDimX() - 2, cY, true);
        steps[0] = FastMath.abs(t2a - t2b);
        //x-
        t2a = this.calc2T(0, cY, true);
        t2b = this.calc2T(1, cY, true);
        steps[1] = FastMath.abs(t2a - t2b);

        //y+
        t2a = this.calc2T(cX, this.getDimY() - 1, true);
        t2b = this.calc2T(cX, this.getDimY() - 2, true);
        steps[2] = FastMath.abs(t2a - t2b);
        //y-
        t2a = this.calc2T(cX, 0, true);
        t2b = this.calc2T(cX, 1, true);
        steps[3] = FastMath.abs(t2a - t2b);

        return (float) ImgOps.findMin(steps);
    }

    //minim stepsize (2theta entre 2 pixels consecutius)
    public float calcMinStepsizeBy2Theta4DirectionsOLD() {
        final double[] steps = new double[4];

        //faig al final a les 4 direccions pero al mig
        final float cX = this.getCentrX();
        final float cY = this.getCentrY();
        final float quartDimX = this.getDimX() / 4.f;
        final float quartDimY = this.getDimY() / 4.f;

        //x+
        double t2a = this.calc2T(cX + quartDimX, cY, true);
        double t2b = this.calc2T(cX + quartDimX + 1, cY, true);
        steps[0] = FastMath.abs(t2a - t2b);
        //x-
        t2a = this.calc2T(cX - quartDimX, cY, true);
        t2b = this.calc2T(cX - quartDimX - 1, cY, true);
        steps[1] = FastMath.abs(t2a - t2b);

        //y+
        t2a = this.calc2T(cX, cY + quartDimY, true);
        t2b = this.calc2T(cX, cY + quartDimY + 1, true);
        steps[2] = FastMath.abs(t2a - t2b);
        //y-
        t2a = this.calc2T(cX, cY - quartDimY, true);
        t2b = this.calc2T(cX, cY - quartDimY - 1, true);
        steps[3] = FastMath.abs(t2a - t2b);

        return (float) ImgOps.findMin(steps);
    }

    public int getFileNameNumber() {
        final String fnameCurrent = FileUtils.getFNameNoExt(this.getImgfile());
        int imgNum = -1;
        try {
            log.debug("substring " + fnameCurrent.substring(fnameCurrent.length() - 4, fnameCurrent.length()));
            imgNum = Integer.parseInt(fnameCurrent.substring(fnameCurrent.length() - 4, fnameCurrent.length()));
        } catch (final Exception e) {
            log.debug("trying to get the file numbering");
            final int indexGuio = fnameCurrent.lastIndexOf("_");
            if (indexGuio > 0) {
                log.debug("index guio=" + indexGuio);
                imgNum = Integer.parseInt(fnameCurrent.substring(indexGuio + 1, fnameCurrent.length()));
            }
        }
        return imgNum;
    }

    //returns the "azimuth" angle of the pixel, the "clockwise" angle between the vertical and the vector centre-pixel.
    public float getAzimAngle(int pX, int pY, boolean degrees) {
        //el centre l'obviem
        if ((pY == (int) (this.getCentrY())) && (pX == (int) (this.getCentrX())))
            return 0;

        //vector centre-pixel
        final double vPCx = pX - this.getCentrX();
        final double vPCy = this.getCentrY() - pY;
        final double verX = 0.0;
        final double verY = 1.0;

        //angle entre vector centre-pixel i la vertical, pero l'angle que volem es sempre el clockwise
        final double dot = vPCx * verX + vPCy * verY;
        final double det = vPCx * verY + vPCy * verX;
        double azim = FastMath.atan2(det, dot);

        if (azim < 0)
            azim = azim + 2 * FastMath.PI;

        if (degrees) {
            azim = FastMath.toDegrees(azim);
        }
        return (float) azim;
    }

    public Point2D.Float getPixelFromAzimutAnd2T(float azimDegrees, float t2deg) {
        //mirarem primer vectors verticals des del centre (azim=0) per mirar la t2 i després aplicarem la rotació...
        // o millor per tema de tilt...
        // primer unitari apliquem rotació i l'anem allargant fins a trobar la t2,
        //(la tolerancia sera el minstepsize?)... o millor quan ens passem agafem l'anterior (aixi no falla mai)

        //vector cap amunt (0,1)
        final float verX = 0.f;
        final float verY = 1.f;

        //com que hem definit azim com rotacio CLOCKWISE desde la vertical, hem d'aplicar angle negatiu
        final double azimRad = (FastMath.toRadians(azimDegrees) * -1);

        final float newX = (float) (verX * (FastMath.cos(azimRad)) - verY * (FastMath.sin(azimRad)));
        final float newY = (float) (verX * (FastMath.sin(azimRad)) + verY * (FastMath.cos(azimRad)));

        //ara ja el tenim "orientat", ara l'hem d'anar allargant
        //establim vector amb coordenades pixels
        float vPCx = this.getCentrX() + newX;
        float vPCy = this.getCentrY() - newY;

        log.writeNameNums("CONFIG", true, "azimDeg,azimRad,t2deg,newX,newY,vPCx,vPCy", azimDegrees, azimRad, t2deg,
                newX, newY, vPCx, vPCy);

        boolean found = false;
        while (!found) {
            final double currT2 = this.calc2T(vPCx, vPCy, true);
            if (currT2 > t2deg) {
                found = true;
            }
            vPCx = vPCx + newX;
            vPCy = vPCy - newY;
            if (!this.isInside((int) (vPCx), (int) (vPCy))) {
                break;
            }
        }
        //agafem el punt anterior
        if (found) {
            vPCx = vPCx - newX;
            vPCy = vPCy + newY;
            log.writeNameNums("CONFIG", true, "FOUND vPCx,vPCy", vPCx, vPCy);
            return new Point2D.Float(vPCx, vPCy);
        } else {
            log.debug("pixel from azimut and 2theta not found");
            ;
            return null;
        }

    }

    public ArrayList<PuntClick> getPuntsCercles() {
        return this.puntsCercles;
    }

    public void clearPuntsCercles() {
        this.puntsCercles.clear();
    }

    public void addPuntCercle(PuntClick s) {
        this.puntsCercles.add(s);
    }

    // donat un pixel i una intensitat afegim el puntCercle si hi ha els parametres
    public void addPuntCercle(Point2D.Float pixel, int inten) {
        if (!this.checkIfDistMD()) {
            return;
        }
        final double t2 = this.calc2T(pixel, false);
        this.addPuntCercle(new PuntClick(pixel, this, (float) t2, inten));
    }

    public void removePuntCercle(PuntClick s) {
        this.puntsCercles.remove(s);
    }

    // donat un punt clicat mirarem si hi ha cercle a aquest pixel i en cas que aixi sigui el borrarem
    //EDIT: al passar a treballar amb ellipses, ens fixarem en la 2theta i una tolerancia
    public void removePuntCercle(Point2D.Float pixel) {
        final Iterator<PuntClick> itrPC = this.getPuntsCercles().iterator();
        int i = 0;
        int indexTrobat = -1;
        final double t2pix = this.calc2T(pixel, true);
        while (itrPC.hasNext()) {
            final EllipsePars e = itrPC.next().getEllipse();
            final double t2elli = this.calc2T(e.getEllipsePoint(0), true);
            //mirem si corresponen amb una certa tolerancia
            if ((t2pix < (t2elli + t2tolDegSelectedPoints)) && (t2pix > (t2elli - t2tolDegSelectedPoints))) {
                //correspon a un puntcercle existent
                indexTrobat = i;
            }
            i++;
        }
        if (indexTrobat >= 0) {
            this.getPuntsCercles().remove(indexTrobat);
        }
    }

    public void recalcularCercles() {
        final Iterator<PuntClick> itrPC = this.puntsCercles.iterator();
        while (itrPC.hasNext()) {
            final PuntClick pc = itrPC.next();
            pc.recalcularEllipse();
        }
    }

    public Peak getPeakFromCoordinates(Point2D.Float px) {
        final Iterator<Peak> itrpk = this.getPkSearchResult().iterator();
        Peak pk = null;
        while (itrpk.hasNext()) {
            pk = itrpk.next();
            if (pk.getPixelCentre().equals(px))
                break;
        }
        return pk;
    }

    public Peak removePeak(Peak pk) {
        final int index = this.getPkSearchResult().indexOf(pk);
        log.config("removePeak at index=" + index);
        if (index < 0)
            return null;
        return this.getPkSearchResult().remove(index);
    }

    public Peak findNearestPeak(Point2D.Float pixel, float maxDistToConsider) {
        final Iterator<Peak> itrp = this.getPkSearchResult().iterator();
        Peak closest = null;
        double minDist = maxDistToConsider + 1;
        while (itrp.hasNext()) {
            final Peak pk = itrp.next();
            final Point2D.Float centre = pk.getPixelCentre();
            final double dist = centre.distance(pixel);
            log.writeNameNums("FINE", true, "p(list) pixel", centre.x, centre.y, pixel.x, pixel.y);
            log.fine("dist=" + Double.toString(dist));
            if ((dist < maxDistToConsider)) {
                if (dist < minDist) {
                    minDist = dist;
                    closest = pk;
                }
            }
        }
        return closest;
    }

    public ArrayList<Peak> findPeakCandidates(float delsig, int minpix, boolean estimbkg) {

        log.debug("************* FIND PEAK CANDIDATES *************");
        final ArrayList<Peak> pkCandidates = new ArrayList<Peak>();
        if (estimbkg) {
            if (this.fonsPerCercaPk == null) {
                //                    int oldThr=this.getExz_threshold();
                //                    this.setExz_threshold(1);
                try {
                    this.fonsPerCercaPk = ImgOps.calcIterAvsq(ImgOps.firstBkgPass(this), this.NbkgForPksearch, null);
                    //                        fonsPerCercaPk = ImgOps.calcIterAvsq(ImgOps.firstBkgPassStronger(this),NbkgForPksearch,null);

                } catch (final InterruptedException e1) {
                    log.warning("Error calculating image background");
                    //                    e1.printStackTrace();
                    return null;
                }
                //                    this.setExz_threshold(oldThr);
                this.fonsPerCercaPk.calcMeanI();
            }
            if (this.meanI <= 0)
                this.calcMeanI();
            if (this.fonsPerCercaPk.meanI <= 0)
                this.fonsPerCercaPk.calcMeanI();
            log.writeNameNumPairs("CONFIG", true, "meanI, sdevI, fonsMeanI,fonsSDevI", this.meanI, this.sdevI,
                    this.fonsPerCercaPk.meanI, this.fonsPerCercaPk.sdevI);
        } else {
            log.writeNameNumPairs("CONFIG", true, "meanI, sdevI", this.meanI, this.sdevI);
        }
        final int nPixAresta_half = 2;
        final int max = (nPixAresta_half * 2 + 1) * (nPixAresta_half * 2 + 1);

        //de 1 a -1 per no agafar els pixels de les vores
        for (int j = 1; j < this.getDimY() - 1; j++) { // per cada fila (Y)
            for (int i = 1; i < this.getDimX() - 1; i++) { // per cada columna (X)

                //                	boolean interestPoint = false;
                //                	if ((i==147)&&(j==865)) {
                //                		log.debug("punt 147,865");
                //                		interestPoint = true;
                //                	}else {
                //                		interestPoint = false;
                //                	}

                if (this.isExcluded(i, j))
                    continue;

                final int pxInten = this.getInten(i, j);
                if (this.meanI <= 0)
                    this.calcMeanI();
                float llindar = this.meanI + delsig * this.sdevI;
                float llindarVeins = this.meanI + this.sdevI;

                if (estimbkg) {
                    final int fonsInten = this.fonsPerCercaPk.getInten(i, j);
                    llindar = fonsInten + delsig * this.fonsPerCercaPk.sdevI;
                    llindarVeins = this.fonsPerCercaPk.meanI;
                }

                if (pxInten < llindar)
                    continue;

                boolean possiblepeak = true;
                //2n comprovem quie la intensitat es superior als 8 veins
                search8neigbors: for (int jj = j - 1; jj < j + 2; jj++) {
                    for (int ii = i - 1; ii < i + 2; ii++) {
                        if (this.isExcluded(ii, jj))
                            continue;
                        if ((ii == i) && (jj == j))
                            continue; //same pixel
                        try {
                            //                                	if (interestPoint)log.writeNameNumPairs("config", true, "ii,jj,inten", ii,jj,this.getInten(ii, jj));
                            if (pxInten < this.getInten(ii, jj)) { //px no es pic
                                possiblepeak = false;
                                break search8neigbors;
                            }
                            ;
                        } catch (final Exception e) {
                            e.printStackTrace();
                        }
                        //si la intensitat es igual o major pot ser pic (igual pot ser saturat...)
                    }
                }

                if (!possiblepeak)
                    continue;
                //                log.writeNameNums("CONFIG", true, "possible peak (x,y,inten,llindar)", j,i,pxInten,llindar);

                boolean hasMinEnough = true;
                if (minpix > 1) {
                    if (minpix > max) {
                        log.debug("minPix max = " + max);
                        minpix = max; //restriccio que poso
                    }
                    int npixbons = 0;
                    for (int jj = j - nPixAresta_half; jj <= j + nPixAresta_half; jj++) {
                        for (int ii = i - nPixAresta_half; ii <= i + nPixAresta_half; ii++) {
                            if (!this.isInside(ii, jj))
                                continue;
                            if (this.isExcluded(ii, jj))
                                continue;

                            if (this.getInten(ii, jj) > (llindarVeins)) { //unicament Ymean pels veins
                                npixbons = npixbons + 1;
                            }
                        }
                    }
                    if (npixbons < minpix) {
                        hasMinEnough = false;
                        log.debug("peak containing less than minpix");
                    }
                }

                //i estem aqui es que pot ser un pic en aquesta primera ronda, el guardem a un arraylist
                if (hasMinEnough) {
                    final Peak pk = new Peak(i, j);
                    pk.setYmax(pxInten);
                    pkCandidates.add(pk);
                }
                //                if (i==testPeakY && j==testPeakX)System.out.println(String.format("hasMinEnough %b", hasMinEnough));
            }
        }

        return pkCandidates;

    }

    private void recalcArcExZones() {
        //ara les zones arc
        final Iterator<ArcExZone> it2 = this.arcExZones.iterator();
        while (it2.hasNext()) {
            final ArcExZone a = it2.next();
            a.setPatt2D(this);
            a.recalcZones();
        }
    }

    public void clearSolutions() {
        this.solucions.clear();
        //recontem
        OrientSolucio.setNumSolucions(0);
    }

    public ArrayList<OrientSolucio> getSolucions() {
        return this.solucions;
    }

    public static float getT2tolDegSelectedPoints() {
        return t2tolDegSelectedPoints;
    }

    public static void setT2tolDegSelectedPoints(float t2tolDegSelectedPoints) {
        Pattern2D.t2tolDegSelectedPoints = t2tolDegSelectedPoints;
    }

    //    public void setExpParam(float pixlx, float pixly, float sepod, float wl, float tilt_deg, float rot_deg, float beamX, float beamY) {
    //        this.setPixSx(pixlx);
    //        this.setPixSy(pixly);
    //        this.setDistMD(sepod);
    //        this.setWavel(wl);
    //        this.setTiltDeg(tilt_deg);
    //        this.setRotDeg(rot_deg);
    //        this.setCentrX(beamX);
    //        this.setCentrY(beamY);
    //    }
    public void setExpParam(float pixlx, float pixly, float sepod, float wl) {
        this.setPixSx(pixlx);
        this.setPixSy(pixly);
        this.setDistMD(sepod);
        this.setWavel(wl);
    }

    public void setExpParam(float pixlx, float pixly, float sepod, float wl, float tilt_deg, float rot_deg) {
        this.setExpParam(pixlx, pixly, sepod, wl);
        this.setTiltDeg(tilt_deg);
        this.setRotDeg(rot_deg);
    }

    public void setScanParameters(float omeInitial, float omeEnd, float acquisitionTime) {
        this.setAcqTime(acquisitionTime);
        this.setOmeFin(omeEnd);
        this.setOmeIni(omeInitial);
    }

    // Aquest metode comprova que s'hagin entrat els parametres instrumentals/experimentals necessaris per certs calculs
    public boolean checkIfDistMD() {
        if (this.distMD <= 0 || this.pixSx <= 0 || this.pixSy <= 0) {
            return false;
        } else {
            return true;
        }
    }

    // Aquest metode comprova que s'hagin entrat els parametres instrumentals/experimentals necessaris per certs calculs
    public boolean checkIfWavel() {
        if (this.wavel <= 0) {
            return false;
        } else {
            return true;
        }
    }

    public boolean checkIfPixSize() {
        if ((this.pixSx <= 0) || (this.pixSy <= 0)) {
            return false;
        } else {
            return true;
        }
    }

    public void addExZone(PolyExZone ez) {
        this.getPolyExZones().add(ez);
    }

    public void addArcExZone(ArcExZone ez) {
        this.getArcExZones().add(ez);
    }

    public int getnPkSatur() {
        return this.nPkSatur;
    }

    public void setnPkSatur(int nPkSatur) {
        this.nPkSatur = nPkSatur;
    }

    public int getIscan() {
        return this.iscan;
    }

    public void setIscan(int iscan) {
        this.iscan = iscan;
    }

    public float getTiltDeg() {
        return this.tiltDeg;
    }

    public void setTiltDeg(float tiltDeg) {
        this.tiltDeg = tiltDeg;
        this.costilt = (float) FastMath.cos(FastMath.toRadians(tiltDeg));
        this.sintilt = (float) FastMath.sin(FastMath.toRadians(tiltDeg));
    }

    public float getRotDeg() {
        return this.rotDeg;
    }

    public void setRotDeg(float rotDeg) {
        this.rotDeg = rotDeg;
        this.setCosrot((float) FastMath.cos(FastMath.toRadians(rotDeg)));
        this.setSinrot((float) FastMath.sin(FastMath.toRadians(rotDeg)));
    }

    public float getOmeIni() {
        return this.omeIni;
    }

    public void setOmeIni(float omeIni) {
        this.omeIni = omeIni;
    }

    public float getOmeFin() {
        return this.omeFin;
    }

    public void setOmeFin(float omeFin) {
        this.omeFin = omeFin;
    }

    public float getAcqTime() {
        return this.acqTime;
    }

    public void setAcqTime(float acqtime) {
        this.acqTime = acqtime;
    }

    public ArrayList<Peak> getPkSearchResult() {
        return this.pkSearchResult;
    }

    public void setPkSearchResult(ArrayList<Peak> pkSearchResult) {
        this.pkSearchResult = pkSearchResult;
    }

    public void sortPkSearchResultYmax() {
        Collections.sort(this.pkSearchResult);
    }

    public ImgFileUtils.SupportedReadExtensions getFileFormat() {
        return this.fileFormat;
    }

    public void setFileFormat(ImgFileUtils.SupportedReadExtensions fileFormat) {
        this.fileFormat = fileFormat;
    }

    public void addPaintedEXZpixel(Point pix) {
        this.ExZpaintPixels.add(pix);
    }

    public void removePaintedEXZpixel(Point pix) {
        this.ExZpaintPixels.remove(pix);
    }

    public void clearPaintedEXZpixel() {
        this.ExZpaintPixels.clear();
    }

    public ArrayList<Point> getExZpaintPixels() {
        return this.ExZpaintPixels;
    }

    public void setExZpaintPixels(ArrayList<Point> exZpaintPixels) {
        this.ExZpaintPixels = exZpaintPixels;
    }

    public int getSaturValue() {
        return this.saturValue;
    }

    public void setSaturValue(int satur) {
        this.saturValue = satur;
    }

    public float getCentrX() {
        return this.centrX;
    }

    public float getCentrY() {
        return this.centrY;
    }

    public int getCentrXI() {
        return (int) (this.centrX);
    }

    public int getCentrYI() {
        return (int) (this.centrY);
    }

    public int getDimX() {
        return this.dimX;
    }

    public int getDimY() {
        return this.dimY;
    }

    public float getDistMD() {
        return this.distMD;
    }

    public ArrayList<PolyExZone> getPolyExZones() {
        return this.polyExZones;
    }

    public void setPolyExZones(ArrayList<PolyExZone> zones) {
        this.polyExZones = zones;
    }

    public ArrayList<ArcExZone> getArcExZones() {
        return this.arcExZones;
    }

    public void setArcExZones(ArrayList<ArcExZone> arcExZones) {
        this.arcExZones = arcExZones;
        this.recalcArcExZones();
    }

    public int getExz_margin() {
        return this.exz_margin;
    }

    public int getMaxI() {
        return this.maxI;
    }

    public float getMillis() {
        return this.millis;
    }

    public int getMinI() {
        return this.minI;
    }

    public int getPixCount() {
        return this.pixCount;
    }

    public float getPixSx() {
        return this.pixSx;
    }

    public float getPixSy() {
        return this.pixSy;
    }

    public float getScale() {
        return this.scale;
    }

    public float getWavel() {
        return this.wavel;
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

    public void setExz_margin(int margin) {
        this.exz_margin = margin;
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

    public int getExz_threshold() {
        return this.exz_threshold;
    }

    public void setExz_threshold(int y0toMask) {
        this.exz_threshold = y0toMask;
    }

    public int getExz_detcircle() {
        return this.exz_detcircle;
    }

    public void setExz_detcircle(int exz_detcircle) {
        this.exz_detcircle = exz_detcircle;
    }

    public float getMeanI() {
        return this.meanI;
    }

    public void setMeanI(float meanI) {
        this.meanI = meanI;
    }

    public float getSdevI() {
        return this.sdevI;
    }

    public void setSdevI(float sdevI) {
        this.sdevI = sdevI;
    }

    public File getImgfile() {
        return this.imgfile;
    }

    public String getImgfileString() {
        if (this.imgfile != null) {
            return this.imgfile.toString();
        } else {
            return "";
        }
    }

    public void setImgfile(File imgfile) {
        this.imgfile = imgfile;
    }

    public int getnSatur() {
        return this.nSatur;
    }

    public void setnSatur(int nSatur) {
        this.nSatur = nSatur;
    }

    public float getCosrot() {
        return this.cosrot;
    }

    public void setCosrot(float cosrot) {
        this.cosrot = cosrot;
    }

    public float getSinrot() {
        return this.sinrot;
    }

    public void setSinrot(float sinrot) {
        this.sinrot = sinrot;
    }

}