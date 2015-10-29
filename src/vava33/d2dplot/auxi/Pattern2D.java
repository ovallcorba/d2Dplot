package vava33.d2dplot.auxi;

import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;
import org.apache.commons.math3.util.FastMath;

import com.vava33.jutils.FileUtils;
import com.vava33.jutils.VavaLogger;

import vava33.d2dplot.D2Dplot_global;
import vava33.d2dplot.MainFrame;

public class Pattern2D {

    private static VavaLogger log = D2Dplot_global.log;
    private static final float t2tolDegSelectedPoints = 0.05f;  
    
    // PARAMETRES IMATGE:
    short[][] intensityB2; // guardem [columna][fila] atencio: columnes=coordX,files=cordY
    int[][] intensityB4; // [columna][fila] atencio: columnes=coordX,files=cordY
    int dimX, dimY;
    int maxI, minI, meanI, nSatur;
    float sdevI;
    float centrX, centrY;
    float distMD, pixSx, pixSy, wavel;
    float tiltDeg, rotDeg; //tilt and rot of the detector in degrees
    float omeIni,omeFin,acqTime;
    /*
     * rot es considerant el 0 a les 12h d'un rellotge amb clockwise positiu
     * tilt es la rotació d'aquest eix de forma que "entra" o "surt" del pla del detector.
     * Dit d'altre forma, es com si rotessim des d'un altre eix perpendicular a aquest.
     * Aleshores sembla que (per un rot 0), un tilt>0 fa que el detector s'inclini "mirant"
     * cap amunt. 
     */
    float scale;
    int pixCount; // pixels totals de la imatge
    
    private ArrayList<OrientSolucio> solucions; // contindra les solucions
    private ArrayList<PuntCercle> puntsCercles;

    //zones excloses
    private ArrayList<PolyExZone> polyExZones;
    int exz_margin = 0; //per salvar el bin (zones excloses)
    int exz_threshold = 0; //per salvar el bin (zones excloses)

    // Variables relacionades amb el fitxer
    private File imgfile; //file from which the data has been read (if any)
    float millis; // temps que s'ha tardat a llegir la imatge
    public boolean oldBIN=false; //variable temporal que indica si �s old o new BIN
    private boolean useB4inten = true;  //by default use B4 intensities

    // Crea un pattern amb unes mides concretes X Y (pixels)
    public Pattern2D(int columnes_X, int files_Y, boolean b4int) {
        this.dimX = columnes_X;
        this.dimY = files_Y;
        this.setB4inten(b4int);
        
        if (!isB4inten()) {
            intensityB2 = new short[dimX][dimY];
        } else {
            intensityB4 = new int[dimX][dimY];
        }

        this.centrX=-1;
        this.centrY=-1;
        this.maxI=0;
        this.minI=99999999;
        this.meanI=-1;
        this.scale=1.0f;
        
        //parametres adquisicio per defecte
        this.omeIni=0;
        this.omeFin=0;
        this.acqTime=-1;
        
        // parametres instrumentals per defecte (-1)
        this.distMD = -1;
        this.pixSx = -1;
        this.pixSy = -1;
        this.wavel = -1;
        
        //tilt defecte
        this.tiltDeg = 0;
        this.rotDeg = 0;

        // inicialitzem arraylist
        this.polyExZones = new ArrayList<PolyExZone>();
        this.solucions = new ArrayList<OrientSolucio>();
        this.puntsCercles = new ArrayList<PuntCercle>();
        this.exz_margin=0;
        
        this.oldBIN=false;
    }

    //si no s'especifica res s'assumeix use of I4
    public Pattern2D(int columnes_X, int files_Y){
        this(columnes_X, files_Y,true);
    }
    
    // COMPLERT
    public Pattern2D(int columnes_X, int files_Y, float centreX, float centreY, int maxInt, int minInt, float escala, boolean i4) {
        this(columnes_X, files_Y, i4);
        this.centrX = centreX;
        this.centrY = centreY;
        this.maxI = maxInt;
        this.minI = minInt;
        this.scale = escala;
    }

    // Genera un pattern 2D amb els mateixos par�metres que un altre (amb zones excloses pero sense intensitats)
    //si es vol una COPIA exacta posar true a copyIntensities
    public Pattern2D(Pattern2D dataIn, boolean copyIntensities){
        this(dataIn.getDimX(), dataIn.getDimY(), dataIn.getCentrX(),dataIn.getCentrY(),dataIn.getMaxI(),dataIn.getMinI(),dataIn.getScale(),dataIn.isB4inten());
        this.setExpParam(dataIn.getPixSx(), dataIn.getPixSy(), dataIn.getDistMD(), dataIn.getWavel());
        this.setExZones(dataIn.getExZones());
        this.setMargin(dataIn.getMargin());
        this.setTiltDeg(dataIn.getTiltDeg());
        this.setRotDeg(dataIn.getRotDeg());
        if(copyIntensities){
            if (dataIn.isB4inten()){
                this.setIntenB4Array(dataIn.getIntenB4Array());
            }else{
                this.setIntenB2Array(dataIn.getIntenB2Array());
            }
        }
    }
    
    // Aquest metode comprova que s'hagin entrat els parametres
    // instrumentals/experimentals necessaris per certs c�lculs
    public boolean checkIfDistMD() {
        if (this.distMD <= 0 || this.pixSx <= 0 || this.pixSy <= 0) {
            return false;
        } else {
            return true;
        }
    }
    
    // Aquest metode comprova que s'hagin entrat els parametres
    // instrumentals/experimentals necessaris per certs c�lculs
    public boolean checkIfWavel() {
        if (this.wavel <= 0) {
            return false;
        } else {
            return true;
        }
    }
    
    public boolean checkIfPixSize() {
        if ((this.pixSx <= 0)||(this.pixSy<=0)) {
            return false;
        } else {
            return true;
        }
    }
    
    // metode que indica si un pixel (x,y) es troba dins d'una zona exclosa
    public boolean isInExZone(int x, int y) {
        // primer mirem que la Y ESTIGUI PER SOBRE EL THRESHOLD
        if(this.getInten(x, y)<exz_threshold) return true;
        // primer comprovem el marge
        if (x <= exz_margin || x >= dimX - exz_margin)
            return true;
        if (y <= exz_margin || y >= dimY - exz_margin)
            return true;
        // ara les zones
        Iterator<PolyExZone> it = polyExZones.iterator();
        while (it.hasNext()) {
            PolyExZone r = it.next();
            if (r.contains(new Point2D.Float(x, y)))
                return true;
        }
        return false;
    }
    
    public void recalcMaxMinI(){
        this.setMaxI(0);
        this.setMinI(9999999);
        for (int i = 0; i < this.getDimY(); i++) { // per cada fila (Y)
            for (int j = 0; j < this.getDimX(); j++) { // per cada columna (X)
                if(this.isInExZone(j, i))continue;
                if(this.getInten(j, i)>this.getMaxI())this.setMaxI(this.getInten(j, i));
                if(this.getInten(j, i)<this.getMinI())this.setMinI(this.getInten(j, i));
            }
        }
    }
    
    //Retorna l'escala necessaria per passar encabir la imatge a I2
    public float calcScaleI2(){
        int maxi=0;
        //dnomes una passada per determinar l'escala
        for (int i = 0; i < this.getDimY(); i++) { // per cada fila (Y)
            for (int j = 0; j < this.getDimX(); j++) { // per cada columna (X)
                if(this.isInExZone(j, i)){
                    continue;
                }
                if(this.getInten(j, i)<0)continue;
                int inten=this.getInten(j, i);
                if(inten>maxi)maxi=inten;
            }
        }
        return FastMath.max(maxi / D2Dplot_global.satur32, 1.000f);
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
                    this.setInten(j, i, -1);
                    continue;
                }
                if(this.getInten(j, i)<0)continue;
                int inten=FastMath.round(this.getInten(j, i)*this.getScale());
                if(inten>this.getMaxI())this.setMaxI(inten);
                if(inten<this.getMinI())this.setMinI(inten);
            }
        }
        //ara tenim maxI i minI (mxI,mnI) veritables (amb escala aplicada), recalculem factor d'escala
        float oldScale=this.getScale();
        this.setScale(FastMath.max(this.getMaxI() / getSaturValue(), 1.000f));
        
        // ara aplico factor escala i guardo on i com toca
        for (int i = 0; i < this.getDimY(); i++) { // per cada fila (Y)
            for (int j = 0; j < this.getDimX(); j++) { // per cada columna (X)
                if(this.isInExZone(j, i)){
                    this.setInten(j, i, -1);
                    continue;
                }
                if(this.getInten(j, i)<0)continue;
                int inten=FastMath.round(this.getInten(j, i)*oldScale);
                this.setInten(j, i, (int) (inten / this.getScale()));
            }
        }
        // corregim maxI i minI
        this.setMaxI((int) (this.getMaxI() / this.getScale()));
        this.setMinI((int) (this.getMinI() / this.getScale()));
    }
    
    public int getSaturValue(){
        if (this.isB4inten()){
            return D2Dplot_global.satur65;
        }else{
            return D2Dplot_global.satur32;
        }
    }

    //calcula i estableix els valors de meanI i sdevI de tota la imatge excepte mascares
    //tamb� calcula els nombre de pixels saturats
    public void calcMeanI(){
        long yacum=0;
        int npix=0;
        int satur=0;
        for (int i = 0; i < this.getDimY(); i++) { // per cada fila (Y)
            for (int j = 0; j < this.getDimX(); j++) { // per cada columna (X)
                if(this.getInten(j, i)<0)continue;
                yacum=yacum+this.getInten(j, i);
                npix=npix+1;
                if(this.getInten(j, i)>=getSaturValue()){
                    satur=satur+1;
                }
            }
        }
        this.nSatur=satur;
        this.meanI= FastMath.round((float)yacum/(float)npix);
        //desviacio
        long numerador=0;
        for (int i = 0; i < this.getDimY(); i++) { // per cada fila (Y)
            for (int j = 0; j < this.getDimX(); j++) { // per cada columna (X)
                if(this.getInten(j, i)<0)continue;
                numerador=numerador + (this.getInten(j, i)-this.meanI)*(this.getInten(j, i)-this.meanI);
            }
        }
        this.sdevI=(float) FastMath.sqrt((float)numerador/(float)npix);
    }
    
    //retorna la mitjana de les intensitats de la imatge per sobre un llindar d'intensitat
    public int calcMeanI(int llindar){
        long yacum=0;
        int npix=0;
        for (int i = 0; i < this.getDimY(); i++) { // per cada fila (Y)
            for (int j = 0; j < this.getDimX(); j++) { // per cada columna (X)
                int iB4=this.getInten(j, i);
                if(iB4<0)continue;
                if(iB4<llindar)continue;
                yacum=yacum+iB4;
                npix=npix+1;
            }
        }
        log.debug("Imean(llindar)= "+FastMath.round((float)yacum/(float)npix));
        return FastMath.round((float)yacum/(float)npix);
    }

    //aplica un factor d'escala a la imatge (I/fscale)
    public void scaleImage(float fscale){
        for (int i = 0; i < this.getDimY(); i++) { // per cada fila (Y)
            for (int j = 0; j < this.getDimX(); j++) { // per cada columna (X)
                this.setInten(j, i, FastMath.round((float)this.getInten(j, i)/fscale));
            }
        }
        //correccio maxI minI scale
        this.setMaxI((int) ((float)this.getMaxI()/fscale));
        this.setMinI((int) ((float)this.getMinI()/fscale));
        this.setScale(fscale);
    }
    


//    !passa els pixels mascara d'una imatge a una altra. �s una forma de
//    !copiar les zones excloses. Tamb� recalcularem maxI i minI ja que es
//    !poden veure afectades.
    public void copyMaskPixelsFromImage(Pattern2D im){
        this.setMaxI(0);
        this.setMinI(9999999);
        for (int i = 0; i < this.getDimY(); i++) { // per cada fila (Y)
            for (int j = 0; j < this.getDimX(); j++) { // per cada columna (X)
                if(im.isInExZone(j, i))this.setInten(j, i, im.getInten(j, i));
                if(this.isInExZone(j, i))continue;
                if(this.getInten(j, i)>this.getMaxI())this.setMaxI(this.getInten(j, i));
                if(this.getInten(j, i)<this.getMinI())this.setMinI(this.getInten(j, i));
            }
        }
    }
    
    //primer havia posat rectes de 0.5 a 1.5 per� �s exigerat, canvio a +-0.2
    //EII, canvio la direcci� d'angle! igual que amplada per alleugar efecte angle
    //NO FER CAS ALS COMENTARIS, HE CANVIAT COSES
      public float[] getFactAngleAmplada(int x_col, int y_row){
        float[] factors = new float[2];
//      float maxAngle = this.calc2T(0,(int)this.getCentrY(),true);
        float maxAngle = (float) this.calc2T(0,0,true);
        //aplicarem un factor lineal de 0.5 a 1.5? per 2T=0 2T=max (angle) o al reves (amplada)
        float angle = (float) this.calc2T(x_col, y_row, true);
        //interpolem sobre les rectes
        //ANGLE
        float x1 = 0.0f;
        float y1 = 3.0f;
        float x2 = maxAngle;
        float y2 = 0.8f;
        factors[0] = ((y2 - y1) / (x2 - x1)) * angle + y1 - ((y2 - y1) / (x2 - x1)) * x1;
        //AMPLADA
//      x1 = 0.0f;
//          y1 = 1.2f;
//          x2 = maxAngle;
//          y2 = 0.8f;
//          factors[1] = ((y2 - y1) / (x2 - x1)) * angle + y1 - ((y2 - y1) / (x2 - x1)) * x1;
        factors[1]=1.f;
        return factors;
    }
    
    //donat un pixel i una aresta calcula la intensitat mitjana del quadrat centrat a aquest pixel
    public int calcIntSquare(int col_X, int row_Y, int aresta, boolean self){
        int hA=(int) FastMath.round((float)(aresta)/2.);
        int npix=0;
        int meanInten =-1;
        
        for (int i=row_Y-hA;i<=row_Y+hA;i++){
            for (int j=col_X-hA; j<=col_X+hA;j++){
                //fora imatge o mascara o propi ciclem
                if(!this.isInside(j, i))continue;
                if(this.isInExZone(j, i))continue;
                if((!self)&&(j==col_X)&&(i==row_Y))continue;
                //afegim
                meanInten = meanInten + this.getInten(j, i);
                npix=npix+1;
            }
        }
        if(npix>0){
            meanInten = FastMath.round((float)(meanInten+1)/(float)(npix)); //recuperem el -1 inicial
        }
        return meanInten;
    }
    
    public boolean esIgualA(Pattern2D altreDiag){
        for (int i = 0; i < this.getDimY(); i++) { // per cada fila (Y)
            for (int j = 0; j < this.getDimX(); j++) { // per cada columna (X)
                if(this.getInten(j, i)!=altreDiag.getInten(j, i)){
                    return false;
                }
            }
        }
        return true;
    }
    
    //retorna true si el pixel esta dins
    public boolean isInside(int col_X, int row_Y){
        if((row_Y>=0)&&(row_Y<this.getDimY())&&(col_X>=0)&&(col_X<this.getDimX())){
            return true;
        }
        return false;
    }
    
    public String getInfo() {
        String info = "  NX(cols)= " + dimX + " NY(rows)= " + dimY + "\n" + 
                "  minI=" + minI + " maxI=" + maxI + "  (Scale factor= " + FileUtils.dfX_3.format(1/scale) +"; Saturated= "+nSatur+")\n" + 
                "  Xcent=" + FileUtils.dfX_3.format(centrX) + " Ycent=" + FileUtils.dfX_3.format(centrY) + "\n" + 
                "  Distance Sample-Detector (mm)=" + FileUtils.dfX_3.format(distMD) + "\n" + 
                "  Pixel Size X Y (mm)= " + FileUtils.dfX_4.format(pixSx) + " " + FileUtils.dfX_4.format(pixSy);
        return info;
    }
    
    public void addExZone(PolyExZone ez){
        this.getExZones().add(ez);
    }
    
    public void setExpParam(float pixlx, float pixly, float sepod, float wl) {
        this.setPixSx(pixlx);
        this.setPixSy(pixly);
        this.setDistMD(sepod);
        this.setWavel(wl);
    }
    
    public float getMax2Tdeg(){
        return (float) this.calc2T(this.getDimX()-1, this.getDimY()-1, true);
    }
    
    public float getMax2TdegCircle(){
        float f1 = (float) this.calc2T(this.getCentrX(), 0.f, true);
        float f2 = (float) this.calc2T(0.f, this.getCentrY(), true);
        float f3 = (float) this.calc2T(this.getCentrX(), this.getDimY()-1, true);
        float f4 = (float) this.calc2T(this.getDimX()-1, this.getCentrY(), true);
        return FastMath.min(FastMath.min(f1,f2),FastMath.min(f3,f4));
    }
    
    public float getCentrX() {
        return centrX;
    }

    public float getCentrY() {
        return centrY;
    }

    public int getCentrXI() {
        return FastMath.round(centrX);
    }

    public int getCentrYI() {
        return FastMath.round(centrY);
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

    public ArrayList<PolyExZone> getExZones() {
        return polyExZones;
    }
    
    public void setExZones(ArrayList<PolyExZone> zones){
        this.polyExZones=zones;
    }

    //IT WILL RETURN A "SHORT" OR INT
    public int getInten(int x_col, int y_row){
        if (this.isB4inten()){
            return this.getIntenB4(x_col, y_row);
        }else{
            return this.getIntenB2(x_col, y_row);
        }
    }

    private short getIntenB2(int x_col, int y_row) {
        return intensityB2[x_col][y_row];
    }

    private int getIntenB4(int x_col, int y_row) {
        return intensityB4[x_col][y_row];
    }

    private short[][] getIntenB2Array(){
        return intensityB2;
    }

    private int[][] getIntenB4Array(){
        return intensityB4;
    }
    
    private void setIntenB2Array(short[][] newIntenB2array){
        this.intensityB2 = newIntenB2array;
    }

    private void setIntenB4Array(int[][] newIntenB4array){
        this.intensityB4 = newIntenB4array;
    }
    
    public int getMargin() {
        return exz_margin;
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

    public boolean isB4inten() {
        return this.useB4inten;
    }

    public void setB4inten(boolean b4inten) {
        this.useB4inten = b4inten;
        if (b4inten && (this.getIntenB4Array()==null)){
            //cal inicialitzarlo
            this.setIntenB2Array(null);
            this.setIntenB4Array(new int[this.getDimX()][this.getDimY()]);
        }
        if (!b4inten && (this.getIntenB2Array()==null)){
            this.setIntenB4Array(null);
            this.setIntenB2Array(new short[this.getDimX()][this.getDimY()]);            
        }
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

    //es pot fer el cast a SHORT
    public void setInten(int x_col, int y_row, int inten){
        if (this.isB4inten()){
            this.setIntenB4(x_col, y_row,inten);
        }else{
            this.setIntenB2(x_col, y_row,(short)inten);
        }
    }
    
    private void setIntenB2(int x_col, int y_row, short inten) {
        this.intensityB2[x_col][y_row] = inten;
    }

    private void setIntenB4(int x_col, int y_row, int inten) {
        this.intensityB4[x_col][y_row] = inten;
    }

    public void setMargin(int margin) {
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

    public int getY0toMask() {
        return exz_threshold;
    }

    public void setY0toMask(int y0toMask) {
        this.exz_threshold = y0toMask;
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

    public File getImgfile() {
        return imgfile;
    }
    
    public String getImgfileString() {
        if (imgfile != null){
            return imgfile.toString();
        }else{
            return "";
        }
    }

    public void setImgfile(File imgfile) {
        this.imgfile = imgfile;
    }

    public int getnSatur() {
        return nSatur;
    }

    public void setnSatur(int nSatur) {
        this.nSatur = nSatur;
    }
    
    public void clearSolutions() {
        this.solucions.clear();
    }
    
    public ArrayList<OrientSolucio> getSolucions() {
        return this.solucions;
    }
    
    public void addSolucio(OrientSolucio s){
        this.solucions.add(s);
    }

    public ArrayList<PuntCercle> getPuntsCercles() {
        return puntsCercles;
    }
    
    public void clearPuntsCercles() {
        this.puntsCercles.clear();
    }
    
    public void addPuntCercle(PuntCercle s){
        this.puntsCercles.add(s);
    }
    
    // donat un pixel i una intensitat afegim el puntCercle si hi ha els parametres
    public void addPuntCercle(Point2D.Float pixel, int inten) {
      //AQUESTA RESPONSABILITAT NO LA TE PATT2D
        if (!this.checkIfDistMD()) {
//            MainFrame.showSetParameters();
//            if (this.getDistMD() <= 0 || this.getPixSx() <= 0 || this.getPixSy() <= 0)
                return;
        }
//        double t2 = this.calc2T(pixel);
//        
//        this.addPuntCercle(new PuntCercle(pixel, this.getCentrX(), this.getCentrY(), t2, inten));
        double t2 = this.calc2T(pixel, false);
        this.addPuntCercle(new PuntCercle(pixel,this,(float)t2,inten));
    }
    
    public void removePuntCercle(PuntCercle s){
        this.puntsCercles.remove(s);
    }
    
    // donat un punt clicat mirarem si hi ha cercle a aquest pixel i en cas que aixi sigui el borrarem
    //EDIT: al passar a treballar amb ellipses, ens fixarem en la 2theta i una tolerancia
    public void removePuntCercle(Point2D.Float pixel) {
        Iterator<PuntCercle> itrPC = this.getPuntsCercles().iterator();
        int i = 0;
        int indexTrobat = -1;
        double t2pix = this.calc2T(pixel, true);
        while (itrPC.hasNext()) {
            EllipsePars e = itrPC.next().getEllipse();
            double t2elli = this.calc2T(e.getEllipsePoint(0),true);
            //mirem si corresponen amb una certa tolerancia
            if ((t2pix < (t2elli+t2tolDegSelectedPoints))&&(t2pix > (t2elli-t2tolDegSelectedPoints))){
                //correspon a un puntcercle existent
                indexTrobat = i;
            }
            i++;
        }
        if (indexTrobat >= 0) {
            this.getPuntsCercles().remove(indexTrobat);
        }
    }
    
    //TODO PASSAR A ELLIPSEPARS
    public void recalcularCercles() {
        Iterator<PuntCercle> itrPC = puntsCercles.iterator();
        while (itrPC.hasNext()) {
            PuntCercle pc = itrPC.next();
            pc.recalcular(this.getCentrX(), this.getCentrY(), calc2T(new Point2D.Float(pc.getX(), pc.getY()),false));
        }
    }
    
    
    public double calc2T(float col_px, float row_py, boolean degrees) {
        double tiltRad = FastMath.toRadians(this.getTiltDeg());
        double rotRad = FastMath.toRadians(this.getRotDeg());
        double cosTilt = FastMath.cos(tiltRad);
        
        double distMDpix = this.getDistMD()/this.getPixSx();
        double dist = distMDpix/cosTilt; //in pixels
        
        //vector centre-pixel
        float vPCx=col_px-this.getCentrX();
        float vPCy=this.getCentrY()-row_py;
        
        double[] vec = new double[3];
        vec[0]=vPCx;
        vec[1]=vPCy;
        vec[2]=0.0;
        RealMatrix vx = new Array2DRowRealMatrix(vec).transpose();
        RealMatrix rotM = ImgOps.rotMatrix(rotRad, "Z");
        RealMatrix tiltM = ImgOps.rotMatrix(tiltRad, "X");

        RealMatrix X = vx.multiply(rotM);
        RealMatrix Z = X.multiply(tiltM);
        double z = Z.getEntry(0, 2);

        double t2p = FastMath.atan(FastMath.sqrt((vPCx*vPCx)+(vPCy*vPCy)-(z*z))/dist-z);
        double DX = dist-z;
        double DY = FastMath.sqrt(vPCx*vPCx+vPCy*vPCy-z*z);
        t2p = FastMath.atan2(DY,DX);

        if (degrees) {
            t2p = FastMath.toDegrees(t2p);
        }
        return t2p;
    }    
    
    public double calc2T(Point2D.Float pixel, boolean degrees) {
        return this.calc2T(pixel.x,pixel.y, degrees);
    }    
    
    
    //t2 en radiants
    public double calcDsp(double t2rad){
        float wl = this.getWavel();
        if (wl > 0){
            double d = wl/(2*FastMath.sin(t2rad/2.));
            return d;
        }
        return -1.d;
    }

    //t2 en radiants!
    public double dspToT2(double dsp, boolean degrees){
        float wl = this.getWavel();
        if (wl > 0){
            double t2 = 2 * FastMath.asin(wl/(2*dsp));
            if (degrees){
                return FastMath.toDegrees(t2);
            }else{
                return t2;    
            }
        }
        return -1.d;
    }
    
    //minim stepsize (2theta entre 2 pixels consecutius)
    public float getMinStepsizeOLD(){
        float picSize = FastMath.min(this.getPixSx(),this.getPixSy()); //en mm
        float minstep = (float) FastMath.atan(picSize / this.getDistMD());
        return minstep;
    }
    
    //minim stepsize (2theta entre 2 pixels consecutius) TODO: APROX, s'hauria de fer en varies direccions
    public float getMinStepsize(){
        double t2a = this.calc2T(1000, 0, true);
        double t2b = this.calc2T(1001, 0, true);
        return (float) FastMath.abs((t2b-t2a));
    }

    public float getTiltDeg() {
        return tiltDeg;
    }

    public void setTiltDeg(float tiltDeg) {
        this.tiltDeg = tiltDeg;
    }

    public float getRotDeg() {
        return rotDeg;
    }

    public void setRotDeg(float rotDeg) {
        this.rotDeg = rotDeg;
    }
    
    //returns the "azimuth" angle of the pixel, the "clockwise" angle between the vertical and the vector
    //centre-pixel. TODO: CHECK AND COMPARE WITH calc2T
    public float getAzimAngle(int pX, int pY,boolean degrees){
        //el centre l'obviem
        if ((pY == FastMath.round(this.getCentrY()))&&(pX == FastMath.round(this.getCentrX())))return 0;
        
        //vector centre-pixel
        float vPCx=pX-this.getCentrX();
        float vPCy=this.getCentrY()-pY;
        float verX=0.f;
        float verY=1.f;
        
        //angle entre vector centre-pixel i la vertical, pero l'angle que volem es sempre el clockwise
        float dot = vPCx*verX + vPCy*verY;
        float det = vPCx*verY + vPCy*verX;
        double azim = FastMath.atan2(det, dot);
        
        if (azim<0)azim = azim + 2*FastMath.PI;
        
        if (degrees){
            azim = FastMath.toDegrees(azim);
        }
        return (float)azim;
    }
    

    public PuntSolucio getNearestPS(float px, float py){
        Iterator<OrientSolucio> itrOS = this.getSolucions().iterator();
        OrientSolucio os = null;
        while (itrOS.hasNext()) {
            os = itrOS.next();
            if (os.isShowSol()) {
                break;
            }
        }
        if(os==null)return null;
        // d'aquesta solucio, es busca el puntSolucio m�s proper al punt entrat
        Iterator<PuntSolucio> itrS = os.getSol().iterator();
        float minModul = Float.MAX_VALUE;
        PuntSolucio nearestPS = null;
        while (itrS.hasNext()) {
            PuntSolucio s = itrS.next();
            float modul = (float) FastMath.sqrt((s.getCoordX()-px)*(s.getCoordX()-px)+(s.getCoordY()-py)*(s.getCoordY()-py));
            if (modul<minModul){
                nearestPS = s;
                minModul=modul;
            }
        }
        return nearestPS;
    }

    public float getOmeIni() {
        return omeIni;
    }

    public void setOmeIni(float omeIni) {
        this.omeIni = omeIni;
    }

    public float getOmeFin() {
        return omeFin;
    }

    public void setOmeFin(float omeFin) {
        this.omeFin = omeFin;
    }

    public float getAcqTime() {
        return acqTime;
    }

    public void setAcqTime(float acqtime) {
        this.acqTime = acqtime;
    }
    
    public void setScanParameters(float omeInitial, float omeEnd, float acquisitionTime){
        this.setAcqTime(acquisitionTime);
        this.setOmeFin(omeEnd);
        this.setOmeIni(omeInitial);
    }
    
}
