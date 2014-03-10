package vava33.plot2d.auxi;

import java.awt.Polygon;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Rectangle2D.Float;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;

import com.vava33.jutils.FileUtils;

import vava33.plot2d.MainFrame;
import vava33.plot2d.auxi.Pattern1D.PointPatt1D;

public class Pattern2D {
    
    // PARAMETRES IMATGE:
    short[][] intensityB2; // guardem [columna][fila] atencio: columnes=coordX,files=cordY
    int[][] intensityB4; // per si es volgués ampliar a utilitzar enters de moment no es pot
    boolean B4intensities;
    int dimX, dimY;
    int maxI, minI, meanI, nSatur;
    float sdevI;
    float centrX, centrY;
    float distMD, pixSx, pixSy, wavel;
    float scale;
    ArrayList<ExZone> exZones;
    int margin = 0; //per salvar el bin (zones excloses)
    int y0toMask = 0; //per salvar el bin (zones excloses)
    int pixCount; // pixels totals de la imatge

    // Variables relacionades amb el fitxer
    private File imgfile; //file from which the data has been read (if any)
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

        this.centrX=-1;
        this.centrY=-1;
        this.maxI=0;
        this.minI=99999999;
        this.scale=1.0f;
        
        // parametres instrumentals per defecte (-1)
        this.distMD = -1;
        this.pixSx = -1;
        this.pixSy = -1;
        this.wavel = -1;

        // inicialitzem arraylist
        this.exZones = new ArrayList<ExZone>();
        this.margin=0;
        
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

    // Genera un pattern 2D amb els mateixos paràmetres que un altre (amb zones excloses pero sense intensitats)
    public Pattern2D(Pattern2D dataIn){
		this(dataIn.getDimX(), dataIn.getDimY(), dataIn.getCentrX(),dataIn.getCentrY(),dataIn.getMaxI(),dataIn.getMinI(),dataIn.getScale(),false);
		this.setExpParam(dataIn.getPixSx(), dataIn.getPixSy(), dataIn.getDistMD(), dataIn.getWavel());
		this.setExZones(dataIn.getExZones());
		this.setMargin(dataIn.getMargin());
    }
    
    // Aquest metode comprova que s'hagin entrat els parametres
    // instrumentals/experimentals necessaris per certs càlculs
    public boolean checkExpParam() {
        if (this.distMD <= 0 || this.pixSx <= 0 || this.pixSy <= 0) {
            return false;
        } else {
            return true;
        }
    }

    // metode que indica si un pixel (x,y) es troba dins d'una zona exclosa
    public boolean isInExZone(int x, int y) {
    	// primer mirem que la Y sigui positiva
    	if(this.getIntenB2(x, y)<0) return true;
        // primer comprovem el marge
        if (x <= margin || x >= dimX - margin)
            return true;
        if (y <= margin || y >= dimY - margin)
            return true;
        // ara les zones
        Iterator<ExZone> it = exZones.iterator();
        while (it.hasNext()) {
            ExZone r = it.next();
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
                if(this.getIntenB2(j, i)>this.getMaxI())this.setMaxI(this.getIntenB2(j, i));
                if(this.getIntenB2(j, i)<this.getMinI())this.setMinI(this.getIntenB2(j, i));
            }
        }
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

    //aplica un factor d'escala a la imatge (I/fscale)
    public void scaleImage(float fscale){
        for (int i = 0; i < this.getDimY(); i++) { // per cada fila (Y)
            for (int j = 0; j < this.getDimX(); j++) { // per cada columna (X)
            	this.setIntenB2(j, i, (short) Math.round((float)this.getIntenB2(j, i)/fscale));
            }
        }
        //correccio maxI minI scale
        this.setMaxI((int) ((float)this.getMaxI()/fscale));
        this.setMinI((int) ((float)this.getMinI()/fscale));
        this.setScale(fscale);
    }
    
//    !Aquesta subrutina sumarà les intensitats dels píxels en una zona en forma d'arc al voltant
//    !d'un pixel determinat.
//    ! 1)calculem (utilitzant amplada i angle) la 2Tmin i 2Tmax de l'arc, així com els 4 vertexs 
//    !   de l'arc
//    ! 2)trobarem el quadrat minim que conté l'arc
//    ! 3)per cada pixel del cuadrat:
//    !   -mirarem si està entre 2Tmin i 2Tmax
//    !   -mirarem si l'angle entre el vector reflexio-centre i el vector pixel-centre es menor a 
//    !    "angle"
//    !   -si es compleixen les dues condicions anteriors en sumarem la intensitat
//    !parametres d'entrada:
//    !  - patt2d: la imatge de treball (CONSIDEREM QUE S'HAN ASSIGNAT EL CENTRE, DISTOD i PIXSIZE)
//    !  - px, py: Coordenades x,y del píxel
//    !  - amplada: amplada de l'arc (gruix)
//    !  - angle: obertura de l'arc
//    !  - self: si es considera o no el propi pixel
//    !  - bkgpt: num de punts de menor intensitat a considerar per calcular el fons
//    	 - debug: per "pintar" de -1 l'arc, si no es per comprovacions ha de valer false
//    !parametres de sortida: objecte patt2dzone
//    !  - ysum: intensitat suma de la zona
//    !  - npix: nombre de pixels que han contribuit a la suma
//    !  - ymean: intensitat mitjana de la zona
//    !  - ymeandesv: desviacio estandard de la mitjana (ysum/npix)
//    !  - ymax: intensitat maxima del pixel
//    !  - ybkg: intensitat del fons (mitjana dels 20 punts de menor intensitat)
//    !  - ybkgdesv: desviacio de la intensitat del fons (entre els 20 punts)
    public Patt2Dzone Yarc(int px, int py, float amplada, float angle, boolean self, int bkgpt, boolean debug){
    	
//      vars calculs extraccio (quadrat,etc..)
    	float RcX,RcY,RcXmax,RcYmax,RcXmin,RcYmin;
    	float RcXcw,RcYcw,RcXcwMax,RcYcwMax,RcXcwMin,RcYcwMin;
    	float RcXacw,RcYacw,RcXacwMax,RcYacwMax,RcXacwMin,RcYacwMin;
    	float vSupX,vSupY,vInfX,vInfY;
        float t2min, t2max,x2,y2,radi,t2p;
        float vPCx,vPCy;
        int bkgsum;
        float pesc, modvPC, modvRc, angleB;
        ArrayList<Short> intensitats = new ArrayList<Short>(); //vector on guardarem les intensitats per calcular desv.estd
        float xmean,sumdesv;
        ArrayList<Short> minint = new ArrayList<Short>(bkgpt); //vector amb les bkgpt intensitats menors
    	
        int npix=0;
        int ysum=0;
        int ymax=0;
        float ymean=0;
        float ybkg=0;
        float ymeandesv=0;
        float ybkgdesv=0;
        for(int i=0;i<bkgpt;i++){
        	minint.add(Short.MAX_VALUE);
        }
        
        
        //calcul vector centre-pixelReflexio (Rc) amb l'origen al centre de la imatge (per fer rotacions)
        RcX=px-this.getCentrX();
        RcY=this.getCentrY()-py;
        //Amplada de l'arc (es podria fer RcX-Amplada*nxmx i RcY-Amp*nymx si la imatge no fos quadrada)
        //l'amplada es en "pixels"
        RcXmax=RcX*(1+amplada);
        RcYmax=RcY*(1+amplada);
        RcXmin=RcX*(1-amplada);
        RcYmin=RcY*(1-amplada);
        
        //ara fem rotar el vector Rc +angle/2 i -angle/2 per trobar els limits de l'arc, trobem els vectors
        //RcCWMax i Min (clockwise) i RcACWMax i min (anticlockwise) i tindrem els vertexs de l'arc
        RcXcw=(float) (RcX*Math.cos(angle)+RcY*Math.sin(angle));
        RcYcw=(float) (-RcX*Math.sin(angle)+RcY*Math.cos(angle));
        RcXcwMax=RcXcw*(1+amplada);
        RcYcwMax=RcYcw*(1+amplada);
        RcXcwMin=RcXcw*(1-amplada);
        RcYcwMin=RcYcw*(1-amplada);
        
        RcXacw=(float) (RcX*Math.cos(angle)-RcY*Math.sin(angle));
        RcYacw=(float) (RcX*Math.sin(angle)+RcY*Math.cos(angle));
        RcXacwMax=RcXacw*(1+amplada);
        RcYacwMax=RcYacw*(1+amplada);
        RcXacwMin=RcXacw*(1-amplada);
        RcYacwMin=RcYacw*(1-amplada);
        
        //ara ja tenim els vertexs, per definir el quadrat mirem el superior esquerra i l'inferior dret,
        //i li sumem el centre per tenir els pixels en la imatge real
        
        vSupX=ImgOps.findMin(RcXcwMax,RcXcwMin,RcXacwMax,RcXacwMin);
        vSupY=ImgOps.findMax(RcYcwMax,RcYcwMin,RcYacwMax,RcYacwMin);
        vInfX=ImgOps.findMax(RcXcwMax,RcXcwMin,RcXacwMax,RcXacwMin);
        vInfY=ImgOps.findMin(RcYcwMax,RcYcwMin,RcYacwMax,RcYacwMin);

        //tornem a referenciar l'origen (0,0) al vertex superior? esquerra
        vSupX=vSupX+this.getCentrX();
        vSupY=this.getCentrY()-vSupY;
        vInfX=vInfX+this.getCentrX();
        vInfY=this.getCentrY()-vInfY;
        
        //calculem els angles t2min i t2max de l'arc (sepMD pixL en micres=> radi micres)
        x2 = (RcXmax*this.getPixSx())*(RcXmax*this.getPixSx());
        y2 = (RcYmax*this.getPixSy())*(RcYmax*this.getPixSy());
        radi = (float) Math.sqrt(x2+y2);
        t2max=(float) Math.atan(radi/(this.getDistMD()));
        
        x2 = (RcXmin*this.getPixSx())*(RcXmin*this.getPixSx());
        y2 = (RcYmin*this.getPixSy())*(RcYmin*this.getPixSy());
        radi = (float) Math.sqrt(x2+y2);
        t2min= (float) Math.atan(radi/(this.getDistMD()));

//        !Ara ja toca la part:
//        !3)per cada pixel del cuadrat:
//        !   -mirarem si està entre 2Tmin i 2Tmax
//        !   -mirarem si l'angle entre el vector reflexio-centre i el vector pixel-centre es menor a "angle"
//        !   -si es compleixen les dues condicions anteriors en sumarem la intensitat
        int ivSupY = Math.round(vSupY);
        int ivInfY = Math.round(vInfY);
        int ivSupX = Math.round(vSupX);
        int ivInfX = Math.round(vInfX);
        for (int j=ivSupY;j<=ivInfY;j++){
        	for (int i=ivSupX;i<=ivInfX;i++){
        		//si esta fora la imatge o es mascara el saltem
        		if(!this.isInside(i, j))continue;
        		if(this.isInExZone(i, j))continue;
        		//ell mateix?
        		if ((i==px)&&(j==py)&&(!self))continue;
        		
        		//calculem el vector pixel-centre (corregit d'origen, centre 0,0 i necessari pels calculs seguents)
                vPCx=i-this.getCentrX();
                vPCy=this.getCentrY()-j;
                        
                //mirem si esta entre 2tmin i 2tmax, sino saltem (cal calcular t2p)
                x2 = (vPCx*this.getPixSx())*(vPCx*this.getPixSx());
                y2 = (vPCy*this.getPixSy())*(vPCy*this.getPixSy());
                radi = (float) Math.sqrt(x2+y2);
                t2p = (float) Math.atan(radi/(this.getDistMD()));
                if((t2p<t2min)||(t2p>t2max))continue;
                
                //angle entre vPC i Rc (prod. escalar)
                pesc = vPCx*RcX + vPCy*RcY;
                modvRc = (float) Math.sqrt(RcX*RcX+RcY*RcY);
                modvPC = (float) Math.sqrt(vPCx*vPCx+vPCy*vPCy);
                if((pesc/(modvRc*modvPC))>1){
                  angleB=(float) Math.acos(1.0);
                }else{
                  angleB=(float) Math.acos(pesc/(modvRc*modvPC));
                }
                //si angleB es major a angle -> saltem
                if(angleB>angle)continue;
                
                //si hem arribat aquí es que hem se sumar la intensitat del pixel
                ysum=ysum+this.getIntenB2(i, j);
                if(ymax<this.getIntenB2(i, j))ymax=this.getIntenB2(i, j);
                for(int k=0;k<bkgpt;k++){
                	//si val zero no considerem (s'hauria de reconsiderar...)
                	//if(this.getIntenB2(i, j)==0)break;
                	if(this.getIntenB2(i, j)<minint.get(k)){
                		minint.set(k, this.getIntenB2(i, j));
                		break; //sortim del for, ja hem utilitzat la intensitat
                	}
                }

                npix=npix+1;
                intensitats.add(this.getIntenB2(i, j));
                
                //debug
                if(debug)this.setIntenB2(i, j, (short) -1);
        	}
        }
        
        if(npix>0){
        	//calcul desviacio estandar sqrt((sum(xi-xmean)^2)/N-1)
            ymean=(float)(ysum)/(float)(npix);
            sumdesv=0;
            Iterator<Short> it = intensitats.iterator();
            while (it.hasNext()){
            	short inten = it.next();
            	sumdesv=sumdesv + ((float)(inten)-ymean)*((float)(inten)-ymean);
            }
            if(npix<2)npix=2;
            ymeandesv=(float) Math.sqrt(sumdesv/(float)(npix-1));
            //calcul del valor de fons i la desviacio
            bkgsum=0;
            int nbkgpt=Math.min(bkgpt, npix);
            for (int i=0; i<nbkgpt;i++){
            	bkgsum = bkgsum + minint.get(i);
            }
            ybkg = (float)(bkgsum)/(float)(nbkgpt);
            sumdesv=0;
            for (int i=0; i<nbkgpt;i++){
            	sumdesv = sumdesv + ((float)(minint.get(i))-ybkg)*((float)(minint.get(i))-ybkg);
            }
            ybkgdesv=(float) Math.sqrt(sumdesv/(float)(nbkgpt-1));
            //systemout? per debug
        }

		return new Patt2Dzone(npix, ysum, ymax, ymean, ymeandesv, ybkg, ybkgdesv);
        	
    }

    private Ellipse2D.Float getEllipseFromCenter(float x, float y, float width, float height)
    {
        float newX = x - width / 2.0f;
        float newY = y - height / 2.0f;

        Ellipse2D.Float ellipse = new Ellipse2D.Float(newX, newY, width, height);

        return ellipse;
    }
    
    //fix yarc, que perdia pixels de fora el quadrat si l'arc era ample o estava a l'eix vertical/horitzontal
    //es pot fer manual pero es una matada:
//    //ara hem de mirar si hem creuat algun eix (vertical o horitzontal) per tal d'evitar definir un
//    //quadrat insuficient
//    if(RcXacw<0&&RcXcw>0){
//    	//estem creuant la vertical per l'hemisferi superior
//    	//caldrà considerar com a vSupY el vector sobre aquesta vertical (0,y)
//    	
//    	
//    }
//    if(RcXacw>0&&RcXcw<0){
//    	//estem creuant la vertical per l'hemisferi inferior
//    	//caldrà considerar com a vInfY el vector sobre aquesta vertical (0,-y)
//    }
//    
    //ho provare de fer amb ellipse2D de java
    public Patt2Dzone Yarc2(int px, int py, float amplada, float angle, boolean self, int bkgpt, boolean debug){
    	
    	//debug time
  		//long startTime = System.currentTimeMillis();
    	
//      vars calculs extraccio (quadrat,etc..)
    	float RcX,RcY,RcXmax,RcYmax,RcXmin,RcYmin;
    	float RcXcw,RcYcw,RcXcwMax,RcYcwMax,RcXcwMin,RcYcwMin;
    	float RcXacw,RcYacw,RcXacwMax,RcYacwMax,RcXacwMin,RcYacwMin;
    	float vSupX,vSupY,vInfX,vInfY;
        float t2min, t2max,x2,y2,radi,t2p;
        float vPCx,vPCy;
        int bkgsum;
        float pesc, modvPC, modvRc, angleB;
        ArrayList<Short> intensitats = new ArrayList<Short>(); //vector on guardarem les intensitats per calcular desv.estd
        float xmean,sumdesv;
        ArrayList<Short> minint = new ArrayList<Short>(bkgpt); //vector amb les bkgpt intensitats menors
    	
        int npix=0;
        int ysum=0;
        int ymax=0;
        float ymean=0;
        float ybkg=0;
        float ymeandesv=0;
        float ybkgdesv=0;
        for(int i=0;i<bkgpt;i++){
        	minint.add(Short.MAX_VALUE);
        }
        
        //calcul vector centre-pixelReflexio (Rc) amb l'origen al centre de la imatge (per fer rotacions)
        RcX=px-this.getCentrX();
        RcY=this.getCentrY()-py;
        //System.out.println("Rc = "+RcX+" "+RcY);
        float modR = (float) Math.sqrt(RcX*RcX+RcY*RcY); //modul, per calcular l'unitari i els vectors d'amplada de l'arc
        float RcUx = RcX/modR;
        float RcUy = RcY/modR;
        RcXmax=RcUx*(modR+amplada/2);
        RcYmax=RcUy*(modR+amplada/2);
        RcXmin=RcUx*(modR-amplada/2);
        RcYmin=RcUy*(modR-amplada/2);
        
        //Amplada de l'arc (es podria fer RcX-Amplada*nxmx i RcY-Amp*nymx si la imatge no fos quadrada)
        //l'amplada es en "pixels"
//        RcXmax=RcX*(1+amplada);
//        RcYmax=RcY*(1+amplada);
//        RcXmin=RcX*(1-amplada);
//        RcYmin=RcY*(1-amplada);
        //System.out.println("RcMax;RcMin = "+RcXmax+" "+RcYmax+"; "+RcXmin+" "+RcYmin);

        float radiExt = (float) Math.sqrt(RcXmax*RcXmax+RcYmax*RcYmax);
        float radiInt = (float) Math.sqrt(RcXmin*RcXmin+RcYmin*RcYmin);
        //System.out.println("radiExt;radiInt = "+radiExt+" "+radiInt);

        //ara podem calcular les ellipses exterior i interior (considerem CERCLES)
        Ellipse2D.Float elext = this.getEllipseFromCenter(this.getCentrX(), this.getCentrY(), radiExt*2, radiExt*2);
        Ellipse2D.Float elint = this.getEllipseFromCenter(this.getCentrX(), this.getCentrY(), radiInt*2, radiInt*2);
//        Ellipse2D.Float elext = this.getEllipseFromCenter(0, 0, radiExt*2, radiExt*2);
//        Ellipse2D.Float elint = this.getEllipseFromCenter(0, 0, radiInt*2, radiInt*2);
        
//        //ara fem rotar el vector Rc - angle/2 per trobar el vector per calcular l'angle on ha de començar l'arc
//        RcXacw=(float) (RcX*Math.cos(angle)-RcY*Math.sin(angle));
//        RcYacw=(float) (RcX*Math.sin(angle)+RcY*Math.cos(angle));
//        //System.out.println("RcACW = "+RcXacw+" "+RcYacw);

        //calculem l'angle entre el vector (1,0) i Rc (ARC2D agafa angle 0 a l'eix X positiu i dibuixa ACW.)
        //prod escalar
        pesc = RcX * 1 + RcY * 0;
        float arcAngle = (float) Math.toDegrees(Math.acos(pesc/(modR)));
        //hem de determinar l'hemisferi
        if(RcY<0){
        	arcAngle = 360 - arcAngle;	
        }
        //System.out.println("pesc;modR;arcAngle = "+pesc+"; "+modR+"; "+arcAngle);
        
        //System.out.println("elext bounds = "+elext.getBounds2D().toString());
        
        //ara construirem l'arc desitjat i en mirarem els limits del quadrat delimitador
//        Arc2D.Float arcExt = new Arc2D.Float(elext.getBounds2D(), arcAngle, (float) Math.toDegrees(angle), Arc2D.OPEN);
        Arc2D.Float arcExt = new Arc2D.Float(elext.getBounds2D(), arcAngle-(angle/2), angle, Arc2D.OPEN);
        Arc2D.Float arcInt = new Arc2D.Float(elint.getBounds2D(), arcAngle-(angle/2), angle, Arc2D.OPEN);
        Rectangle2D.Float rextern = (Float) arcExt.getBounds2D();
        Rectangle2D.Float rintern = (Float) arcInt.getBounds2D();
        
        //System.out.println("arcext bounds = "+arcExt.getBounds2D().toString());
        //System.out.println("angle = "+angle);
        
//        vSupX = (float) Math.min(rextern.getMinX(), rintern.getMinX());
//        vSupY = (float) Math.max(rextern.getMaxY(), rintern.getMaxY());
//        vInfX = (float) Math.max(rextern.getMaxX(), rintern.getMaxX());
//        vInfY = (float) Math.min(rextern.getMinY(), rintern.getMinY());
        //COORD IMATGE
        vSupX = (float) Math.min(rextern.getMinX(), rintern.getMinX());
        vSupY = (float) Math.min(rextern.getMinY(), rintern.getMinY());
        vInfX = (float) Math.max(rextern.getMaxX(), rintern.getMaxX());
        vInfY = (float) Math.max(rextern.getMaxY(), rintern.getMaxY());
        //System.out.println("Vsup;Vinf = "+vSupX+" "+vSupY+"; "+vInfX+" "+vInfY);

//       
//        vSupX=ImgOps.findMin(RcXcwMax,RcXcwMin,RcXacwMax,RcXacwMin);
//        vSupY=ImgOps.findMax(RcYcwMax,RcYcwMin,RcYacwMax,RcYacwMin);
//        vInfX=ImgOps.findMax(RcXcwMax,RcXcwMin,RcXacwMax,RcXacwMin);
//        vInfY=ImgOps.findMin(RcYcwMax,RcYcwMin,RcYacwMax,RcYacwMin);

        //tornem a referenciar l'origen (0,0) al vertex superior? esquerra
//        vSupX=vSupX+this.getCentrX();
//        vSupY=this.getCentrY()-vSupY;
//        vInfX=vInfX+this.getCentrX();
//        vInfY=this.getCentrY()-vInfY;
        //NO CAL, HEM TREBALLAT AMB ELLIPSES AMB COORD D'IMATGE
        
        //calculem els angles t2min i t2max de l'arc (sepMD pixL en micres=> radi micres)
        x2 = (RcXmax*this.getPixSx())*(RcXmax*this.getPixSx());
        y2 = (RcYmax*this.getPixSy())*(RcYmax*this.getPixSy());
        radi = (float) Math.sqrt(x2+y2);
        t2max=(float) Math.atan(radi/(this.getDistMD()));
        
        x2 = (RcXmin*this.getPixSx())*(RcXmin*this.getPixSx());
        y2 = (RcYmin*this.getPixSy())*(RcYmin*this.getPixSy());
        radi = (float) Math.sqrt(x2+y2);
        t2min= (float) Math.atan(radi/(this.getDistMD()));

//        !Ara ja toca la part:
//        !3)per cada pixel del cuadrat:
//        !   -mirarem si està entre 2Tmin i 2Tmax
//        !   -mirarem si l'angle entre el vector reflexio-centre i el vector pixel-centre es menor a "angle"
//        !   -si es compleixen les dues condicions anteriors en sumarem la intensitat
        int ivSupY = Math.round(vSupY);
        int ivInfY = Math.round(vInfY);
        int ivSupX = Math.round(vSupX);
        int ivInfX = Math.round(vInfX);
        
        //debug
		//long checkpoint1  = System.currentTimeMillis();
		//float check1time = (checkpoint1 - startTime);
        
        for (int j=ivSupY;j<=ivInfY;j++){
        	for (int i=ivSupX;i<=ivInfX;i++){
        		//si esta fora la imatge o es mascara el saltem
        		if(!this.isInside(i, j))continue;
        		if(this.isInExZone(i, j))continue;
        		//ell mateix?
        		if ((i==px)&&(j==py)&&(!self))continue;
        		
        		//calculem el vector pixel-centre (corregit d'origen, centre 0,0 i necessari pels calculs seguents)
                vPCx=i-this.getCentrX();
                vPCy=this.getCentrY()-j;
                        
                //mirem si esta entre 2tmin i 2tmax, sino saltem (cal calcular t2p)
                x2 = (vPCx*this.getPixSx())*(vPCx*this.getPixSx());
                y2 = (vPCy*this.getPixSy())*(vPCy*this.getPixSy());
                radi = (float) Math.sqrt(x2+y2);
                t2p = (float) Math.atan(radi/(this.getDistMD()));
                if((t2p<t2min)||(t2p>t2max))continue;
                
                //angle entre vPC i Rc (prod. escalar)
                pesc = vPCx*RcX + vPCy*RcY;
                modvRc = (float) Math.sqrt(RcX*RcX+RcY*RcY);
                modvPC = (float) Math.sqrt(vPCx*vPCx+vPCy*vPCy);
                if((pesc/(modvRc*modvPC))>1){
                  angleB=(float) Math.acos(1.0);
                }else{
                  angleB=(float) Math.acos(pesc/(modvRc*modvPC));
                }
                //si angleB es major a angle -> saltem
                if(Math.toDegrees(angleB)>(angle/2))continue;
                
                //si hem arribat aquí es que hem se sumar la intensitat del pixel
                ysum=ysum+this.getIntenB2(i, j);
                if(ymax<this.getIntenB2(i, j))ymax=this.getIntenB2(i, j);
                for(int k=0;k<bkgpt;k++){
                	//si val zero no considerem (s'hauria de reconsiderar...)
                	//if(this.getIntenB2(i, j)==0)break;
                	if(this.getIntenB2(i, j)<minint.get(k)){
                		minint.set(k, this.getIntenB2(i, j));
                		break; //sortim del for, ja hem utilitzat la intensitat
                	}
                }

                npix=npix+1;
                intensitats.add(this.getIntenB2(i, j));
                
                //debug
                if(debug)this.setIntenB2(i, j, (short) -1);
        	}
        }
        
        if(npix>0){
        	//calcul desviacio estandar sqrt((sum(xi-xmean)^2)/N-1)
            ymean=(float)(ysum)/(float)(npix);
            sumdesv=0;
            Iterator<Short> it = intensitats.iterator();
            while (it.hasNext()){
            	short inten = it.next();
            	sumdesv=sumdesv + ((float)(inten)-ymean)*((float)(inten)-ymean);
            }
            if(npix<2)npix=2;
            ymeandesv=(float) Math.sqrt(sumdesv/(float)(npix-1));
            //calcul del valor de fons i la desviacio
            bkgsum=0;
            int nbkgpt=Math.min(bkgpt, npix);
            for (int i=0; i<nbkgpt;i++){
            	bkgsum = bkgsum + minint.get(i);
            }
            ybkg = (float)(bkgsum)/(float)(nbkgpt);
            sumdesv=0;
            for (int i=0; i<nbkgpt;i++){
            	sumdesv = sumdesv + ((float)(minint.get(i))-ybkg)*((float)(minint.get(i))-ybkg);
            }
            ybkgdesv=(float) Math.sqrt(sumdesv/(float)(nbkgpt-1));
            //systemout? per debug
        }

		//long endTime   = System.currentTimeMillis();
		//float totalTime = (endTime - startTime);
		
		//System.out.println("Yarc took "+totalTime+"ms "+"(checkpoint at "+check1time+"ms, npix= "+npix+", zona="+ivSupX+","+ivSupY+";"+ivInfX+","+ivInfY+")");

		return new Patt2Dzone(npix, ysum, ymax, ymean, ymeandesv, ybkg, ybkgdesv);
        	
    }
    
    
//    !Aquesta subrutina fa la integració radial de la imatge
//    !parametres d'entrada:
//    !  - patt2d: la imatge de treball (CONSIDEREM QUE S'HAN ASSIGNAT EL CENTRE, DISTOD i PIXSIZE)
//    !  - t2ini, t2fin
//    !  - stepsize: pas del "diagrama" a generar == gruix dels anells
//    !parametres de sortida:
//    !  - ysum: vector d'intensitats
//    !  - npix: vector de numero de pixels que han contribuit a cada punt
//    !  - desv: vector de desviacions estandard de ysum en cada punt
    public Pattern1D intRad(float t2ini, float t2fin, float stepsize, boolean factorN){
    	Pattern1D out = new Pattern1D(t2ini,t2fin,stepsize);
    	float xmean,sumdesv,vPCx,vPCy,x2,y2,radi,t2p;
    	int npoints,p;
        float factN=1.0f;
        
    	npoints = Math.round((t2fin-t2ini)/stepsize)+1; //+1 perque volem incloure t2ini i t2fin
    	
        for (int i = 0; i < this.getDimY(); i++) { // per cada fila (Y)
            for (int j = 0; j < this.getDimX(); j++) { // per cada columna (X)
	        	//mask o zero el descartem
	        	if(this.isInExZone(j, i))continue;
	            //mirem la 2T del pixel en la imatge per determinar amplada i angle
	            //1)vector centre pixel:
	            vPCx=j-this.getCentrX();
	            vPCy=this.getCentrY()-i;
	            //2)calcul angle 2teta en graus
	            x2 = (vPCx*this.getPixSx())*(vPCx*this.getPixSx());
	            y2 = (vPCy*this.getPixSy())*(vPCy*this.getPixSy());
	            radi = (float) Math.sqrt(x2+y2);
	            t2p=(float) Math.toDegrees(Math.atan(radi/(this.getDistMD())));
	            if(t2p<t2ini)continue;
	            //mirem a quina posicio del vector (diagrama pols) ha d'anar
	            p=Math.round(t2p/stepsize)-Math.round(t2ini/stepsize);
	            if(factorN){
		            factN= (float) (((2*Math.PI*(this.getDistMD()))/(this.getPixSx()*this.getPixSy()))  * Math.tan(Math.toRadians(t2p)));	            	
	            }
	            //i sumem
	            out.sumPoint(p, Math.round(this.getIntenB2(j,i)*this.getScale()*(1.0f/factN)), 1);
            }
        }
    	
        //ara ja hauriem de tenir al vector el diagrama de pols.
        
        //tornem a fer una passada per calcular la desviacio estandar sqrt((sum(xi-xmean)^2)/N-1)
        for (int i = 0; i < this.getDimY(); i++) { // per cada fila (Y)
            for (int j = 0; j < this.getDimX(); j++) { // per cada columna (X)
            	//mask o zero el descartem
            	if(this.isInExZone(j, i))continue;
                //mirem la 2T del pixel en la imatge per determinar amplada i angle
                //1)vector centre pixel:
                vPCx=j-this.getCentrX();
                vPCy=this.getCentrY()-i;
                //2)calcul angle 2teta en graus
                x2 = (vPCx*this.getPixSx())*(vPCx*this.getPixSx());
                y2 = (vPCy*this.getPixSy())*(vPCy*this.getPixSy());
                radi = (float) Math.sqrt(x2+y2);
                t2p=(float) Math.toDegrees(Math.atan(radi/(this.getDistMD())));
                if(t2p<t2ini)continue;
                //mirem a quina posicio del vector (diagrama pols) ha d'anar
                p=Math.round(t2p/stepsize)-Math.round(t2ini/stepsize);
                //i ara acumulem la desv
                xmean=(float)(out.getPoint(p).getCounts())/(float)(out.getPoint(p).getNpix());
                float des = (this.getIntenB2(j, i)*this.getScale()-xmean)*(this.getIntenB2(j, i)*this.getScale()-xmean);
                out.getPoint(p).addDesv(des);
            }
        }
        //Calcul final desviacio
        Iterator<PointPatt1D> it = out.getPoints().iterator();
        while(it.hasNext()){
        	PointPatt1D punt = it.next();
        	if (punt.getNpix()<2){
        		punt.setDesv(0);
        		continue;
        	}
        	float des = (float) Math.sqrt(punt.getDesv()/(float)(punt.getNpix()-1));
        	punt.setDesv(des);
        }
        
        return out;
    }
    
//    !passa els pixels mascara d'una imatge a una altra. És una forma de
//    !copiar les zones excloses. També recalcularem maxI i minI ja que es
//    !poden veure afectades.
    public void copyMaskPixelsFromImage(Pattern2D im){
        this.setMaxI(0);
        this.setMinI(9999999);
        for (int i = 0; i < this.getDimY(); i++) { // per cada fila (Y)
            for (int j = 0; j < this.getDimX(); j++) { // per cada columna (X)
            	if(im.isInExZone(j, i))this.setIntenB2(j, i, im.getIntenB2(j, i));
                if(this.isInExZone(j, i))continue;
                if(this.getIntenB2(j, i)>this.getMaxI())this.setMaxI(this.getIntenB2(j, i));
                if(this.getIntenB2(j, i)<this.getMinI())this.setMinI(this.getIntenB2(j, i));
            }
        }
    }
    
    //donat un pixel en calcula la 2t (suposem que tenim totes les dades) en radiants o graus (inDeg=true)
    public float calc2T(int x_col, int y_row, boolean inDeg){
        float vPCx,vPCy,x2,y2,radi;
        vPCx=(float)(x_col)-this.getCentrX();
        vPCy=this.getCentrY()-(float)(y_row);
        x2 = (vPCx*this.getPixSx())*(vPCx*this.getPixSx());
        y2 = (vPCy*this.getPixSy())*(vPCy*this.getPixSy());
        radi = (float) Math.sqrt(x2+y2);
        if(!inDeg){
        	return (float) Math.atan(radi/(this.getDistMD()));	
        }
        return (float) Math.toDegrees(Math.atan(radi/(this.getDistMD())));
    }
    
//    //torna un factor per multiplicar a l'angle d'obertura entrat a Yarc, per fer-lo més petit
//    //a angles baixos i més gran a angles alts. A la 2T "central" serà 1.
//    public float getFactAngle(int x_col, int y_row){
//    	float maxAngle = this.calc2T(0,0,true);
//    	//aplicarem un factor lineal de 0.5 a 1.5 per 2T=0 2T=max
//    	float angle = this.calc2T(x_col, y_row, true);
//    	// el valor d'angle s'interpolara sobre la recta (0,0.5) a (max,1.5)
//        //interpolem
//        float x1 = 0.0f;
//        float y1 = 0.5f;
//        float x2 = maxAngle;
//        float y2 = 1.5f;
//        return ((y2 - y1) / (x2 - x1)) * angle + y1 - ((y2 - y1) / (x2 - x1)) * x1;
//    }
//    
//    //torna un factor per multiplicar a l'amplada de l'arc per entrar a Yarc, per fer-lo més ample
//    //a angles baixos i més estret a angles alts. A la 2T "central" serà 1.
//    public float getFactAmplada(int x_col, int y_row){
//    	float maxAngle = this.calc2T(0,0,true);
//    	//aplicarem un factor lineal de 0.5 a 1.5 per 2T=0 2T=max
//    	float angle = this.calc2T(x_col, y_row, true);
//    	// el valor d'angle s'interpolara sobre la recta (0,1.5) a (max,0.5)
//        //interpolem
//        float x1 = 0.0f;
//        float y1 = 1.5f;
//        float x2 = maxAngle;
//        float y2 = 0.5f;
//        return ((y2 - y1) / (x2 - x1)) * angle + y1 - ((y2 - y1) / (x2 - x1)) * x1;
//    }
//AJUNTEM ELS DOS METODES
    //primer havia posat rectes de 0.5 a 1.5 però és exigerat, canvio a +-0.2
    //EII, canvio la direcció d'angle! igual que amplada per alleugar efecte angle
    //NO FER CAS ALS COMENTARIS, HE CANVIAT COSES
	  public float[] getFactAngleAmplada(int x_col, int y_row){
		float[] factors = new float[2];
//		float maxAngle = this.calc2T(0,(int)this.getCentrY(),true);
		float maxAngle = this.calc2T(0,0,true);
		//aplicarem un factor lineal de 0.5 a 1.5? per 2T=0 2T=max (angle) o al reves (amplada)
		float angle = this.calc2T(x_col, y_row, true);
	    //interpolem sobre les rectes
		//ANGLE
	    float x1 = 0.0f;
	    float y1 = 3.0f;
	    float x2 = maxAngle;
	    float y2 = 0.8f;
	    factors[0] = ((y2 - y1) / (x2 - x1)) * angle + y1 - ((y2 - y1) / (x2 - x1)) * x1;
	    //AMPLADA
//	    x1 = 0.0f;
//      	y1 = 1.2f;
//      	x2 = maxAngle;
//      	y2 = 0.8f;
//      	factors[1] = ((y2 - y1) / (x2 - x1)) * angle + y1 - ((y2 - y1) / (x2 - x1)) * x1;
	    factors[1]=1.f;
      	return factors;
	}
    
    //donat un pixel i una aresta calcula la intensitat mitjana del quadrat centrat a aquest pixel
    public int calcIntSquare(int col_X, int row_Y, int aresta, boolean self){
        int hA=(int) Math.round((float)(aresta)/2.);
        int npix=0;
        int meanInten =-1;
        
        for (int i=row_Y-hA;i<=row_Y+hA;i++){
        	for (int j=col_X-hA; j<=col_X+hA;j++){
        		//fora imatge o mascara o propi ciclem
        		if(!this.isInside(j, i))continue;
        		if(this.isInExZone(j, i))continue;
        		if((!self)&&(j==col_X)&&(i==row_Y))continue;
        		//afegim
        		meanInten = meanInten + this.getIntenB2(j, i);
        		npix=npix+1;
        	}
        }
        if(npix>0){
        	meanInten = Math.round((float)(meanInten+1)/(float)(npix)); //recuperem el -1 inicial
        }
        return meanInten;
    }
    
    public boolean esIgualA(Pattern2D altreDiag){
        for (int i = 0; i < this.getDimY(); i++) { // per cada fila (Y)
            for (int j = 0; j < this.getDimX(); j++) { // per cada columna (X)
            	if(this.getIntenB2(j, i)!=altreDiag.getIntenB2(j, i)){
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
    
    public void addExZone(ExZone ez){
    	this.getExZones().add(ez);
    }
    
    public void setExpParam(float pixlx, float pixly, float sepod, float wl) {
        this.setPixSx(pixlx);
        this.setPixSy(pixly);
        this.setDistMD(sepod);
        this.setWavel(wl);
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

    public ArrayList<ExZone> getExZones() {
        return exZones;
    }
    
    public void setExZones(ArrayList<ExZone> zones){
    	this.exZones=zones;
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

    public int getY0toMask() {
        return y0toMask;
    }

    public void setY0toMask(int y0toMask) {
        this.y0toMask = y0toMask;
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

	public void setImgfile(File imgfile) {
		this.imgfile = imgfile;
	}
}
