package com.vava33.d2dplot.auxi;

import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

import org.apache.commons.math3.util.FastMath;

import com.vava33.d2dplot.D2Dplot_global;
import com.vava33.jutils.FileUtils;
import com.vava33.jutils.VavaLogger;

public class Pattern2D {

    private static VavaLogger log = D2Dplot_global.getVavaLogger(Pattern2D.class.getName());
    private static float t2tolDegSelectedPoints = 0.05f;  
    
    // PARAMETRES IMATGE:
    short[][] intensityB2; // guardem [columna][fila] atencio: columnes=coordX,files=cordY
    int[][] intensityB4; // [columna][fila] atencio: columnes=coordX,files=cordY
    int dimX, dimY;
    int maxI, minI, meanI, nSatur,nPkSatur;
    float sdevI;
    float centrX, centrY;
    float distMD, pixSx, pixSy, wavel;
    float tiltDeg, rotDeg; //tilt and rot of the detector in degrees
    float costilt, sintilt, cosrot, sinrot;
    float omeIni,omeFin,acqTime;
    ImgFileUtils.SupportedReadExtensions fileFormat;
    
    /*
     * rot es considerant el 0 a les 12h d'un rellotge amb clockwise positiu
     * tilt es la rotació d'aquest eix de forma que "entra" o "surt" del pla del detector.
     * Dit d'altre forma, es com si rotessim des d'un altre eix perpendicular a aquest.
     * Aleshores sembla que (per un rot 0), un tilt>0 fa que el detector s'inclini "mirant"
     * cap amunt. 
     */
    float scale;
    int pixCount; // pixels totals de la imatge
    int iscan;   // ISCAN=1 (GIR AL VOLTANT DE i); =2 (AL VOLTANT DE j) (POT VALER ZERO), EIX VERTICAL = j, es a dir per MSPD, ISCAN=2
    
    private ArrayList<OrientSolucio> solucions; // contindra les solucions
    private ArrayList<PuntClick> puntsCercles;
    private ArrayList<Peak> pkSearchResult; //de la cerca de pics
    private Pattern2D fonsPerCercaPk;
    
    //zones excloses
    private ArrayList<PolyExZone> polyExZones;
    private ArrayList<ArcExZone> arcExZones;
    int exz_margin = 0; //per salvar el bin (zones excloses)
    int exz_threshold = 0; //per salvar el bin (zones excloses)
    int exz_detcircle = 0; //cercle del detector
//    private HashSet<Point> listPixelsInExZones;

    // Variables relacionades amb el fitxer
    private File imgfile; //file from which the data has been read (if any)
    float millis; // temps que s'ha tardat a llegir la imatge
    public boolean oldBIN=false; //variable temporal que indica si �s old o new BIN
    private boolean useB4inten = true;  //by default use B4 intensities

    int NbkgForPksearch =3;
    
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
        this.iscan = 2;
        
        //tilt defecte
        this.setTiltDeg(0);
        this.setRotDeg(0);

        // inicialitzem arraylist
        this.polyExZones = new ArrayList<PolyExZone>();
        this.arcExZones = new ArrayList<ArcExZone>();
        this.solucions = new ArrayList<OrientSolucio>();
        this.puntsCercles = new ArrayList<PuntClick>();
//        this.listPixelsInExZones = new HashSet<Point>();
        this.exz_margin=0;
        this.exz_threshold = 0;
        this.exz_detcircle = 0;
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
        this(dataIn,copyIntensities,false);
    }
    
    public Pattern2D(Pattern2D dataIn, boolean copyIntensities, boolean initIntToZero){
        this(dataIn.getDimX(), dataIn.getDimY(), dataIn.getCentrX(),dataIn.getCentrY(),dataIn.getMaxI(),dataIn.getMinI(),dataIn.getScale(),dataIn.isB4inten());
        this.setExpParam(dataIn.getPixSx(), dataIn.getPixSy(), dataIn.getDistMD(), dataIn.getWavel());
        //mantenim zones excloses... aixo vol dir que si intensitat es -1 s'ha de mantenir (implmentat al initToZero)!!
        //el threshold tambe el posem allà ja que sino al guardar un fitxer mask no es te en compte.
        this.setPolyExZones(dataIn.getPolyExZones());
        this.setArcExZones(dataIn.getArcExZones());
        this.setExz_margin(dataIn.getExz_margin());
        this.setExz_threshold(dataIn.getExz_threshold());
        this.setExz_detcircle(dataIn.getExz_detcircle());
        this.setTiltDeg(dataIn.getTiltDeg());
        this.setRotDeg(dataIn.getRotDeg());
        if(copyIntensities){
            for (int i = 0; i < this.getDimY(); i++) { // per cada fila (Y)
                for (int j = 0; j < this.getDimX(); j++) { // per cada columna (X)
                    this.setInten(j, i, dataIn.getInten(j,i));
                }
            }
        }else{
            if (initIntToZero){
                for (int i = 0; i < this.getDimY(); i++) { // per cada fila (Y)
                    for (int j = 0; j < this.getDimX(); j++) { // per cada columna (X)
                        if (dataIn.isB4inten()){
                            if(dataIn.getIntenB4(j, i)<0 || dataIn.getIntenB4(j, i)<dataIn.getExz_threshold()){
                                this.setIntenB4(j, i, -1);    
                            }else{
                                this.setIntenB4(j, i, 0);
                            }
                        }else{
                            if(dataIn.getIntenB2(j, i)<0 || dataIn.getIntenB2(j, i)<dataIn.getExz_threshold()){
                                this.setIntenB2(j, i, (short)-1);
                            }else{
                                this.setIntenB2(j, i, (short) 0);    
                            }
                        }
                    }
                }
            }
        }
    }
    
    public void copyInstrParamFromOtherPatt(Pattern2D in){
        this.setExpParam(in.getPixSx(), in.getPixSy(), in.getDistMD(), in.getWavel());
        this.setWavel(in.getWavel());
        this.setCentrX(in.getCentrX());
        this.setCentrY(in.getCentrY());
        this.setTiltDeg(in.getTiltDeg());
        this.setRotDeg(in.getRotDeg());
        
    }
    
    public void zeroIntensities(){
        for (int i = 0; i < this.getDimY(); i++) { // per cada fila (Y)
            for (int j = 0; j < this.getDimX(); j++) { // per cada columna (X)
                if (this.isB4inten()){
                    this.setIntenB4(j, i, 0);
                }else{
                    this.setIntenB2(j, i, (short) 0);
                }
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
        //be, si es menys 1 directament tornemtrue
        if(this.getInten(x, y)<0)return true;
        
        // primer mirem que la Y ESTIGUI PER SOBRE EL THRESHOLD
        if(this.getInten(x, y)<exz_threshold) return true;
        
        // després comprovem el marge
        if (x <= exz_margin || x >= dimX - exz_margin)
            return true;
        if (y <= exz_margin || y >= dimY - exz_margin)
            return true;
        
        //ara el cercle del detector
        if (exz_detcircle>0){
              int xc = (int)((float)this.dimX/2.f)-exz_detcircle;
              int yc = (int)((float)this.dimY/2.f)-exz_detcircle;
              Ellipse2D cercle = new Ellipse2D.Float(xc, yc, exz_detcircle*2, exz_detcircle*2);
              if (!cercle.contains(x, y))return true; 
        }
        
        // ara les zones
        Iterator<PolyExZone> it = polyExZones.iterator();
        while (it.hasNext()) {
            PolyExZone r = it.next();
            if (r.contains(new Point2D.Float(x, y)))
                return true;
        }
        
        //ara les zones arc
        Iterator<ArcExZone> it2 = arcExZones.iterator();
        while (it2.hasNext()){
            ArcExZone a = it2.next();
            if (a.contains(x, y))return true;
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
            if (this.getFileFormat()==ImgFileUtils.SupportedReadExtensions.CBF){
                return 1048573; //saturacio pilatus 6M
            }
            if (this.getFileFormat()==ImgFileUtils.SupportedReadExtensions.TIF){
                return 1048573;
            }
            return D2Dplot_global.satur65;
        }else{
            return D2Dplot_global.satur32;
        }
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
    
    //calcula i estableix els valors de meanI i sdevI de tota la imatge excepte mascares
    //tamb� calcula els nombre de pixels saturats
    public void calcMeanI(){
//        BigInteger byacum = BigInteger.valueOf(0);
        long yacum=0;
        int npix=0;
        int satur=0;
        
        class pixel {
            int x;
            int y;
            pixel(int x, int y) {
                this.x = x;
                this.y = y;
            }
        }
        ArrayList<pixel> satPixels=new ArrayList<pixel>();
        
        for (int i = 0; i < this.getDimY(); i++) { // per cada fila (Y)
            for (int j = 0; j < this.getDimX(); j++) { // per cada columna (X)
                if(this.getInten(j, i)<0)continue;
                yacum=yacum+this.getInten(j, i);
                npix=npix+1;

                if(this.getInten(j, i)>=getSaturValue()){
                    satur=satur+1;
                    satPixels.add(new pixel(j,i));
                }
            }
        }
        this.nSatur=satur;
        this.meanI= FastMath.round((float)yacum/(float)npix);
        //desviacio
        double sd=0.0;

        for (int i = 0; i < this.getDimY(); i++) { // per cada fila (Y)
            for (int j = 0; j < this.getDimX(); j++) { // per cada columna (X)
                if(this.getInten(j, i)<0)continue;
                
                long a = this.getInten(j, i)-this.meanI;
                long b = a*a;
                double c = b/(float)npix;
                sd = sd + c; 
                
            }
        }
        this.sdevI=(float) FastMath.sqrt(FastMath.abs((float) sd));
        
        //afegit el 160906  calcul pics saturats (a partir dels pixels)
        //REVISAR HP:     Centre del pic (HP(J)) amb fons substret (BACKJ)
        int valorPixelsConsiderarMateixPic = 15; //TODO PASSAR A CONSTANT I UNIFICAR AMB PKSEARCH
        int nsatur= this.getnSatur();
        int[] idpk = new int[nsatur];
        float[] xp = new float[nsatur];
        float[] yp = new float[nsatur];
        float[] hp = new float[nsatur];
        for (int i=0;i<nsatur;i++){
            idpk[i]=0;
        }
        int nsat=-1;
        for (int i=0;i<nsatur;i++){
            if(idpk[i]==0){
                nsat = nsat +1;
                xp[nsat]=0.0f;
                yp[nsat]=0.0f;
                hp[nsat]=0.0f;
                int nequiv = 0;
                for (int j=i;j<nsatur;j++){
                    int arg = (satPixels.get(j).x-satPixels.get(i).x)*(satPixels.get(j).x-satPixels.get(i).x)
                            +(satPixels.get(j).y-satPixels.get(i).y)*(satPixels.get(j).y-satPixels.get(i).y);
                    float dist = (float) FastMath.sqrt(arg);
                    if (dist<=valorPixelsConsiderarMateixPic){
                        idpk[j]=i;
                        xp[nsat]=xp[nsat]+satPixels.get(j).x;
                        yp[nsat]=yp[nsat]+satPixels.get(j).y;
                        hp[nsat]=hp[nsat]+this.getInten(satPixels.get(j).x, satPixels.get(j).y);
                        nequiv = nequiv +1;
                    }
                }
                xp[nsat]=xp[nsat]/(float)nequiv;
                yp[nsat]=yp[nsat]/(float)nequiv;
                hp[nsat]=hp[nsat]/(float)nequiv;
            }
        }
        this.nPkSatur=nsat;
        log.printmsg("DEBUG", String.format("nombre de pics saturats=%d", this.nPkSatur));
        
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
        this.getPolyExZones().add(ez);
    }
    
    public void addArcExZone(ArcExZone ez){
        this.getArcExZones().add(ez);
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
        return (int)(centrX);
    }

    public int getCentrYI() {
        return (int)(centrY);
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

    public ArrayList<PolyExZone> getPolyExZones() {
        return polyExZones;
    }
    
    public void setPolyExZones(ArrayList<PolyExZone> zones){
        this.polyExZones=zones;
    }

    public ArrayList<ArcExZone> getArcExZones() {
        return arcExZones;
    }

    public void setArcExZones(ArrayList<ArcExZone> arcExZones) {
        this.arcExZones = arcExZones;
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
    
    public int getExz_margin() {
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
        return exz_threshold;
    }

    public void setExz_threshold(int y0toMask) {
        this.exz_threshold = y0toMask;
    }
    public int getExz_detcircle() {
        return exz_detcircle;
    }

    public void setExz_detcircle(int exz_detcircle) {
        this.exz_detcircle = exz_detcircle;
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
        //recontem
        OrientSolucio.setNumSolucions(0);
    }
    
    public ArrayList<OrientSolucio> getSolucions() {
        return this.solucions;
    }
    
    public void addSolucio(OrientSolucio s){
        this.solucions.add(s);
        //recontem i reassingem colors
        OrientSolucio.setNumSolucions(this.getSolucions().size());
        Iterator<OrientSolucio> itros = this.getSolucions().iterator();
        while(itros.hasNext()){
            OrientSolucio os = itros.next();
            os.setNumSolucio(this.getSolucions().indexOf(os));
            os.assignaColorSol();
        }
        
    }
    
    public void removeSolucio(OrientSolucio s){
        this.solucions.remove(s);
        //recontem i repintem
        OrientSolucio.setNumSolucions(this.getSolucions().size());
        Iterator<OrientSolucio> itros = this.getSolucions().iterator();
        while(itros.hasNext()){
            OrientSolucio os = itros.next();
            os.setNumSolucio(this.getSolucions().indexOf(os));
            os.assignaColorSol();
        }
        
    }

    public ArrayList<PuntClick> getPuntsCercles() {
        return puntsCercles;
    }
    
    public void clearPuntsCercles() {
        this.puntsCercles.clear();
    }
    
    public void addPuntCercle(PuntClick s){
        this.puntsCercles.add(s);
    }
    
    // donat un pixel i una intensitat afegim el puntCercle si hi ha els parametres
    public void addPuntCercle(Point2D.Float pixel, int inten) {
        if (!this.checkIfDistMD()) {
                return;
        }
        double t2 = this.calc2T(pixel, false);
        this.addPuntCercle(new PuntClick(pixel,this,(float)t2,inten));
        //DEBUG
//        this.addPuntCercle(new PuntClick(pixel,this,(float)t2,inten));
    }
    
    public void removePuntCercle(PuntClick s){
        this.puntsCercles.remove(s);
    }
    
    // donat un punt clicat mirarem si hi ha cercle a aquest pixel i en cas que aixi sigui el borrarem
    //EDIT: al passar a treballar amb ellipses, ens fixarem en la 2theta i una tolerancia
    public void removePuntCercle(Point2D.Float pixel) {
        Iterator<PuntClick> itrPC = this.getPuntsCercles().iterator();
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
    
    public void recalcularCercles() {
        Iterator<PuntClick> itrPC = puntsCercles.iterator();
        while (itrPC.hasNext()) {
            PuntClick pc = itrPC.next();
            pc.recalcularEllipse();
        }
    }
    
    
    public double calc2T(float col_px, float row_py, boolean degrees) {
        
        double distMDpix = (this.getDistMD()/this.getPixSx())/this.costilt;
        float vCPx=col_px-this.getCentrX();
        float vCPy=this.getCentrY()-row_py;
        
        double testRot = FastMath.toRadians(this.rotDeg);
        double distprima = (vCPx*FastMath.cos(testRot) - vCPy*FastMath.sin(testRot))*this.sintilt; //acw rotation
        double t2p = (vCPx*vCPx) + (vCPy*vCPy) - (distprima*distprima);
        t2p = FastMath.sqrt(t2p);
        t2p = t2p/(distMDpix+distprima); 
        t2p = FastMath.atan(t2p);
        if (degrees) {
            t2p = FastMath.toDegrees(t2p);
        }        
        return t2p;
        
    } 
    
    public double calc2T_FORM(float col_px, float row_py, boolean degrees) {
        
        double distMDpix = (this.getDistMD()/this.getPixSx())/this.costilt;
        float vCPx=col_px-this.getCentrX();
        float vCPy=this.getCentrY()-row_py;
        double rot = FastMath.toRadians(this.rotDeg);
        //apliquem -rot
        double vCPxRot = vCPx*FastMath.cos(rot) - vCPy*FastMath.sin(rot);
        double vCPyRot = vCPx*FastMath.sin(rot) + vCPy*FastMath.cos(rot);
        
        double num = vCPxRot*vCPxRot*this.costilt*this.costilt+vCPyRot*vCPyRot;
        double den = (distMDpix + vCPxRot*this.sintilt)*(distMDpix + vCPxRot*this.sintilt);
                
        double t2 = FastMath.sqrt(num/den);
        t2 = FastMath.atan(t2);
        if (degrees) {
            t2 = FastMath.toDegrees(t2);
        }  
        return t2;
        
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
    public float calcMinStepsizeEstimateWithPixSizeAndDistMD(){
        float picSize = FastMath.min(this.getPixSx(),this.getPixSy()); //en mm
        float minstep = (float) FastMath.atan(picSize / this.getDistMD());
        return (float) FastMath.toDegrees(minstep);
    }
    
    //minim stepsize (2theta entre 2 pixels consecutius) TODO: APROX, s'hauria de fer en varies direccions
    public float calcMinStepsizeBy2Theta4Directions(){
        double[] steps = new double[4];
        
        //faig al final a les 4 direccions pero al mig
        float cX = this.getCentrX();
        float cY = this.getCentrY();
        float quartDimX = this.getDimX()/4;
        float quartDimY = this.getDimY()/4;
        
        //x+
        double t2a = this.calc2T(cX+quartDimX, cY, true);
        double t2b = this.calc2T(cX+quartDimX+1, cY, true);
        steps[0] = FastMath.abs(t2a-t2b);
        //x-
        t2a = this.calc2T(cX-quartDimX, cY, true);
        t2b = this.calc2T(cX-quartDimX-1, cY, true);
        steps[1] = FastMath.abs(t2a-t2b);
        
        //y+
        t2a = this.calc2T(cX, cY+quartDimY, true);
        t2b = this.calc2T(cX, cY+quartDimY+1, true);
        steps[2] = FastMath.abs(t2a-t2b);
        //y-
        t2a = this.calc2T(cX, cY-quartDimY, true);
        t2b = this.calc2T(cX, cY-quartDimY-1, true);
        steps[3] = FastMath.abs(t2a-t2b);
        
        return (float) ImgOps.findMin(steps);
        
    }

    public float getTiltDeg() {
        return tiltDeg;
    }

    public void setTiltDeg(float tiltDeg) {
        this.tiltDeg = tiltDeg;
        this.costilt = (float) FastMath.cos(FastMath.toRadians(tiltDeg));
        this.sintilt = (float) FastMath.sin(FastMath.toRadians(tiltDeg));
    }

    public float getRotDeg() {
        return rotDeg;
    }

    public void setRotDeg(float rotDeg) {
        this.rotDeg = rotDeg;
        this.cosrot = (float) FastMath.cos(FastMath.toRadians(rotDeg));
        this.sinrot = (float) FastMath.sin(FastMath.toRadians(rotDeg));
    }
    
    //returns the "azimuth" angle of the pixel, the "clockwise" angle between the vertical and the vector
    //centre-pixel. TODO: CHECK AND COMPARE WITH calc2T
    public float getAzimAngle(int pX, int pY,boolean degrees){
        //el centre l'obviem
        if ((pY == (int)(this.getCentrY()))&&(pX == (int)(this.getCentrX())))return 0;
        
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
    
    public Point2D.Float getPixelFromAzimutAnd2T(float azimDegrees, float t2deg){
        //mirarem primer vectors verticals des del centre (azim=0) per mirar la t2 i després aplicarem la rotació...
        // o millor per tema de tilt...
        // primer unitari apliquem rotació i l'anem allargant fins a trobar la t2, 
        //(la tolerancia sera el minstepsize?)... o millor quan ens passem agafem l'anterior (aixi no falla mai)
        
        //vector cap amunt (0,1)
        float verX=0.f;
        float verY=1.f;

        //com que hem definit azim com rotacio CLOCKWISE desde la vertical, hem d'aplicar angle negatiu
        double azimRad = (FastMath.toRadians(azimDegrees) * -1);
        
        float newX = (float) (verX*(FastMath.cos(azimRad))-verY*(FastMath.sin(azimRad)));
        float newY = (float) (verX*(FastMath.sin(azimRad))+verY*(FastMath.cos(azimRad)));

        //ara ja el tenim "orientat", ara l'hem d'anar allargant
        //establim vector amb coordenades pixels
        float vPCx= this.getCentrX()+newX;
        float vPCy= this.getCentrY()-newY;        
        
        log.writeNameNums("CONFIG", true, "azimDeg,azimRad,t2deg,newX,newY,vPCx,vPCy",azimDegrees,azimRad,t2deg,newX,newY,vPCx,vPCy);
        
        boolean found = false;
        while (!found){
            double currT2 = this.calc2T(vPCx, vPCy, true);
            if (currT2>t2deg){
                found = true;
            }
            vPCx = vPCx + newX;
            vPCy = vPCy - newY;
            if (!this.isInside((int)(vPCx), (int)(vPCy))){
                break;
            }
        }
        //agafem el punt anterior
        if (found){
            vPCx = vPCx - newX;
            vPCy = vPCy + newY;
            log.writeNameNums("CONFIG", true, "FOUND vPCx,vPCy",vPCx,vPCy);
            return new Point2D.Float(vPCx,vPCy);
        }else{
            log.debug("pixel from azimut and 2theta not found");;
            return null;
        }
        
    }

    //AQUEST NO FUNCIONA BE
    public Point2D.Float getPixelFromAzimutAnd2TOLD(float azimDegrees, float t2deg){
        //mirarem primer vectors verticals des del centre (azim=0) per mirar la t2 i després aplicarem la rotació...
        // o millor per tema de tilt...
        // primer unitari apliquem rotació i l'anem allargant fins a trobar la t2, 
        //(la tolerancia sera el minstepsize?)... o millor quan ens passem agafem l'anterior (aixi no falla mai)
        
        //vector cap amunt (0,1)
        float verX=0.f;
        float verY=1.f;

        //com que hem definit azim com rotacio CLOCKWISE desde la vertical, hem d'aplicar angle negatiu
        double azimRad = (FastMath.toRadians(azimDegrees) * -1);
        
        float newX = (float) (verX*(FastMath.cos(azimRad))-verY*(FastMath.sin(azimRad)));
        float newY = (float) (verX*(FastMath.sin(azimRad))+verY*(FastMath.cos(azimRad)));
        
        //ara ja el tenim "orientat", ara l'hem d'anar allargant
        //establim vector amb coordenades pixels
        float vPCx= this.getCentrX()+newX;
        float vPCy= this.getCentrY()-newY;        
        
        log.writeNameNums("CONFIG", true, "azimDeg,azimRad,t2deg,newX,newY,vPCx,vPCy",azimDegrees,azimRad,t2deg,newX,newY,vPCx,vPCy);
        
        boolean found = false;
        while (!found){
            double currT2 = this.calc2T(vPCx, vPCy, true);
            if (currT2>t2deg){
                found = true;
            }
            vPCx = vPCx + newX;
            vPCy = vPCy + newY;
            if (!this.isInside((int)(vPCx), (int)(vPCy))){
                break;
            }
        }
        //agafem el punt anterior
        if (found){
            vPCx = vPCx - newX;
            vPCy = vPCy - newY;
            log.writeNameNums("CONFIG", true, "FOUND vPCx,vPCy",vPCx,vPCy);
            return new Point2D.Float(vPCx,vPCy);
        }else{
            log.debug("pixel from azimut and 2theta not found");;
            return null;
        }
        
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

    public ArrayList<Peak> getPkSearchResult() {
        return pkSearchResult;
    }
    
    public void sortPkSearchResultYmax() {
        Collections.sort(pkSearchResult);
    }

    public void setPkSearchResult(ArrayList<Peak> pkSearchResult) {
        this.pkSearchResult = pkSearchResult;
    }

    public static float getT2tolDegSelectedPoints() {
        return t2tolDegSelectedPoints;
    }

    public static void setT2tolDegSelectedPoints(float t2tolDegSelectedPoints) {
        Pattern2D.t2tolDegSelectedPoints = t2tolDegSelectedPoints;
    }

    public int getnPkSatur() {
        return nPkSatur;
    }

    public void setnPkSatur(int nPkSatur) {
        this.nPkSatur = nPkSatur;
    }

    public int getIscan() {
        return iscan;
    }

    public void setIscan(int iscan) {
        this.iscan = iscan;
    }
    
    public Peak getPeakFromCoordinates(Point2D.Float px){
        Iterator<Peak> itrpk = this.getPkSearchResult().iterator();
        Peak pk = null;
        while (itrpk.hasNext()){
            pk = itrpk.next();
            if (pk.getPixelCentre().equals(px))break;
        }
        return pk;
    }
    
    public Peak removePeak(Peak pk){
        int index = this.getPkSearchResult().indexOf(pk);
        log.config("removePeak at index="+index);
        if (index<0)return null;
        return this.getPkSearchResult().remove(index);
    }
    
    public Peak findNearestPeak(Point2D.Float pixel, float maxDistToConsider){
        Iterator<Peak> itrp = this.getPkSearchResult().iterator();
        Peak closest = null;
        double minDist = maxDistToConsider +1;
        while (itrp.hasNext()){
            Peak pk = (Peak)itrp.next();
            Point2D.Float centre = pk.getPixelCentre();
            double dist = centre.distance(pixel);
            log.writeNameNums("FINE", true, "p(list) pixel", centre.x,centre.y,pixel.x,pixel.y);
            log.fine("dist="+Double.toString(dist));
            if ((dist<maxDistToConsider)){
                if (dist<minDist){
                    minDist = dist;
                    closest = pk;
                }
            }
        }
        return closest;
    }

    public ImgFileUtils.SupportedReadExtensions getFileFormat() {
        return fileFormat;
    }

    public void setFileFormat(ImgFileUtils.SupportedReadExtensions fileFormat) {
        this.fileFormat = fileFormat;
    }
    
    public int getFileNameNumber(){
        String fnameCurrent = FileUtils.getFNameNoExt(this.getImgfile());
        int imgNum = -1;
        try{
            log.debug("substring "+fnameCurrent.substring(fnameCurrent.length()-4, fnameCurrent.length()));
            imgNum = Integer.parseInt(fnameCurrent.substring(fnameCurrent.length()-4, fnameCurrent.length()));
        }catch(Exception e){
            log.debug("trying to get the file numbering");
            int indexGuio = fnameCurrent.lastIndexOf("_");
            if (indexGuio>0){
                log.debug("index guio="+indexGuio);
                imgNum = Integer.parseInt(fnameCurrent.substring(indexGuio+1, fnameCurrent.length()));
            }
        }
        return imgNum;
    }
        
    public ArrayList<Peak> findPeakCandidates(float delsig, int minpix, boolean estimbkg){
        
        log.debug("************* FIND PEAK CANDIDATES *************");
        ArrayList<Peak> pkCandidates = new ArrayList<Peak>();
        if (estimbkg) {
            if (fonsPerCercaPk == null){
                int oldThr=this.getExz_threshold();
                this.setExz_threshold(1);
                try {
                    fonsPerCercaPk = ImgOps.calcIterAvsq(ImgOps.firstBkgPass(this),NbkgForPksearch,null,null);
                } catch (InterruptedException e1) {
                    log.info("error calculating image background");
//                    e1.printStackTrace();
                    return null;
                }
                this.setExz_threshold(oldThr);
                fonsPerCercaPk.calcMeanI();
            }
            if (this.meanI<=0)this.calcMeanI();
            if (this.fonsPerCercaPk.meanI<=0)fonsPerCercaPk.calcMeanI();
            log.writeNameNumPairs("CONFIG", true, "meanI, sdevI, fonsMeanI,fonsSDevI", meanI, sdevI, fonsPerCercaPk.meanI,fonsPerCercaPk.sdevI);
        }else {
            log.writeNameNumPairs("CONFIG", true, "meanI, sdevI", meanI, sdevI);
        }
        int nPixAresta_half = 2;
        int max = (nPixAresta_half*2+1)*(nPixAresta_half*2+1);
        
        //de 1 a -1 per no agafar els pixels de les vores
        for(int i=1; i<this.getDimY()-1;i++){
            for(int j=1; j<this.getDimX()-1;j++){
                
                if (this.isInExZone(j, i))continue;
                
                int pxInten = this.getInten(j, i);
                if (this.meanI<=0)this.calcMeanI();
                float llindar = this.meanI + delsig*this.sdevI;
                float llindarVeins = this.meanI + this.sdevI;
                
                if (estimbkg) {
                    int fonsInten = fonsPerCercaPk.getInten(j, i);
                    llindar = fonsInten + delsig*fonsPerCercaPk.sdevI; 
                    llindarVeins = fonsPerCercaPk.meanI;
                }
                
                
//                float llindar = fonsInten + delsig*this.sdevI; 
//                float llindarVeins = this.meanI;
                
                if (pxInten<llindar)continue;
                
                boolean possiblepeak = true;
                //2n comprovem quie la intensitat es superior als 8 veins
                search8neigbors:
                    for (int ii=i-1;ii<i+2;ii++){
                        for (int jj=j-1;jj<j+2;jj++){
                            if (this.isInExZone(jj, ii))continue;
                            if ((ii==i)&&(jj==j))continue; //same pixel
                            try{
                                if (pxInten< this.getInten(jj, ii)){ //px no es pic
                                    possiblepeak = false;
                                    break search8neigbors;
                                }; 
                            }catch(Exception e){
                                e.printStackTrace();
                            }
                            //si la intensitat es igual o major pot ser pic (igual pot ser saturat...)
                        }
                    }

                if (!possiblepeak)continue;
//                log.writeNameNums("CONFIG", true, "possible peak (x,y,inten,llindar)", j,i,pxInten,llindar);
                
                boolean hasMinEnough = true;
                if(minpix>1){
                    if (minpix > max) {
                        log.debug("minPix max = "+max);
                        minpix=max; //restriccio que poso
                    }
                    int npixbons = 0;
                    for (int ii=i-nPixAresta_half;ii<=i+nPixAresta_half;ii++){
                        for (int jj=j-nPixAresta_half;jj<=j+nPixAresta_half;jj++){
                            if (!this.isInside(jj, ii))continue;
                            if (this.isInExZone(jj, ii))continue;
                            
                            if (this.getInten(jj, ii)>(llindarVeins)){ //unicament Ymean pels veins
                                npixbons=npixbons+1;
                            }
                        }
                    }
                    if (npixbons<minpix) {
                        hasMinEnough=false;
                        log.debug("peak containing less than minpix");
                    }
                }
                
                //i estem aqui es que pot ser un pic en aquesta primera ronda, el guardem a un arraylist
                if (hasMinEnough) {
                    Peak pk = new Peak(j,i);
                    pk.setYmax(pxInten);
                    pkCandidates.add(pk);
                }
//                if (i==testPeakY && j==testPeakX)System.out.println(String.format("hasMinEnough %b", hasMinEnough));
            }
        }
        return pkCandidates;
        
    }
    
}