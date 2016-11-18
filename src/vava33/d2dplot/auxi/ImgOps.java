package vava33.d2dplot.auxi;

import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Locale;

import javax.swing.JCheckBox;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.ProgressMonitor;
import javax.swing.SwingWorker;

import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.util.FastMath;

import vava33.d2dplot.D2Dplot_global;
import vava33.d2dplot.PKsearch_frame;
import vava33.d2dplot.Param_dialog;
import vava33.d2dplot.auxi.PDDatabase.searchDBWorker;
import vava33.d2dplot.auxi.Pattern1D.PointPatt1D;

import com.vava33.jutils.FileUtils;
import com.vava33.jutils.LogJTextArea;
import com.vava33.jutils.VavaLogger;

/*
 * Operacions sobre imatges (sostracci� fons, correccions, etc...)
 */
public final class ImgOps {
	
	private static int bkgIter;
    
	static ProgressMonitor pm = null;
    static sumImagesFileWorker sumwk =  null;
    static Pattern2D pattSum = null;
    
	private static VavaLogger log = D2Dplot_global.getVavaLogger(ImgOps.class.getName());

	public static Pattern2D firstBkgPass(Pattern2D dataIn){
		
		Pattern2D dataOut = new Pattern2D(dataIn,false);
		bkgIter=0; //inicialitzem iter
		int Imin=Integer.MAX_VALUE;
		int Imean=0;
		long Iacum=0;
		int countPoints=0;
		for(int i=0; i<dataOut.getDimY();i++){
			for(int j=0; j<dataOut.getDimX();j++){
				if(dataIn.getInten(j, i)<0)continue;
				if(dataIn.getInten(j, i)<Imin)Imin=dataIn.getInten(j, i);
				Iacum=Iacum+dataIn.getInten(j, i);
				countPoints++;
			}
		}
		Iacum=FastMath.round(Iacum/countPoints);
		Imean = (int) Iacum;
		
		//si la intensitat mitjana es zero, abortem el firstBkgPass
		if(Imean<0)return dataOut;
		
		//corregim els punts (els mascara ja es mantenen a -1)
		for(int i=0; i<dataOut.getDimY();i++){
			for(int j=0; j<dataOut.getDimX();j++){
				if(dataIn.getInten(j, i)>(Imean+2*(Imean-Imin))){
					dataOut.setInten(j, i, (Imean+2*(Imean-Imin)));
				}else{
					dataOut.setInten(j, i, dataIn.getInten(j, i));
				}
			}
		}
		
		return dataOut;
	}
	
	public static Pattern2D calcIterAvsq(Pattern2D dataIn,int N,LogJTextArea txtOut,JProgressBar progress) throws InterruptedException{
		long startTime = System.currentTimeMillis();
		//PROGRESS BAR:
//		if(txtOut!=null){
//			txtOut.addtxt(false, true, "");
//		}
		bkgIter=bkgIter+1;
		Pattern2D dataOut = new Pattern2D(dataIn,false);
		for(int i=0; i<dataOut.getDimY();i++){
			for(int j=0; j<dataOut.getDimX();j++){
	            //comprovem si el punt es una mascara, i si es aixi el saltem donant-li
	            //el valor de la iteracio anterior (suposadament -1)
				if(dataIn.getInten(j, i)<0){
					dataOut.setInten(j, i, dataIn.getInten(j, i));
					continue;
				}
				int sumI=0;
				int nMaskP=0;
				//el quadrat al voltant del punt
				for(int k=i-N;k<=i+N;k++){ //k files al voltant del punt
					for(int l=j-N;l<=j+N;l++){ //L columnes al voltant del punt
						if((k>=0)&&(k<dataIn.getDimY())&&(l>=0)&&(l<dataIn.getDimX())){
							//estem dins la imatge
							//el propi punt no el considerem
							if((k==i)&&(l==j))continue;
							//comprovem si el punt es mascara, si es aix� el "restem" (no el
							//tenim en compte) i saltem al seguent
							if(dataIn.getInten(l, k)<0){
								nMaskP++;
								continue;
							}
							//CAS NORMAL: punt al "centre" que no xoca amb limits (ja hem comprovat que no sigui
			                //(un punt mascara al principi)
							sumI=sumI+dataIn.getInten(l, k);
						}else{
							//ESTEM FORA LA IMAGE
//			                  !Agafarem el punt mes proper al punt en questi�, sempre i quant no sigui mascara
//			                  !calcularem els nous indexs l,k
//			                  !EN TOTS ELS CASOS QUE SOBRESURT HEM DE VIGILAR AMB ELS MARGES DE LA IMATGE
//			                  !SI ES QUE N'HI HA, �s a dir:
//			                    ! - (0,0) sera (it0%margin, it0%margin)
//			                    ! - (nxmx,nymx) sera (nxmx-it0%margin-1, nymx-it0%margin-1)
//			                  !valors newL,newK inicials l,k
							int newL=l;
							int newK=k;
//			                  !si el quadrat sobresurt de les x (columnes) per l'esquerra s'agafa la 1a columna
			                if(l<=0)newL=dataIn.getExz_margin();
//	                          !si el quadrat sobresurt de les x (columnes) per la dreta s'agafa la darrera columna
	                        if(l>=dataIn.getDimX())newL=dataIn.getDimX()-dataIn.getExz_margin()-1;
//	                          !si el quadrat sobresurt de les y (files) per dalt s'agafa la 1a fila
	                        if(k<=0)newK=dataIn.getExz_margin();
//	                          !si el quadrat sobresurt de les y (files) per baix s'agafa la darrera fila
	                        if(k>=dataIn.getDimY())newK=dataIn.getDimY()-dataIn.getExz_margin()-1;
//	                          !comprovem que newL,newK no sigui mascara, si es mascara no el considerem (el restem)
	                        if(dataIn.getInten(newL,newK)<0){
	                            nMaskP++;
	                            continue;
	                        }
	                        //si es un pixel normal el sumem
	                        sumI=sumI+dataIn.getInten(newL, newK);
						}
					}
				}
				int Inew= sumI/((2*N+1)*(2*N+1)-1-nMaskP); //restem els nMaskP ja que NO han contribuit
				//si el valor de Inew es menor del que hi havia actualitzem, sino el deixem
	            if(Inew<dataIn.getInten(j,i)){
	                dataOut.setInten(j,i, Inew);
	            }else{
	                dataOut.setInten(j,i,dataIn.getInten(j, i));
	            }
			}//dimX
			//PROGRESS BAR:
			if(progress!=null){
				long elapTime = (System.currentTimeMillis() - startTime)/1000;
				float percent = ((float)i/(float)dataOut.getDimY())*100;
				float estTime = (((100-percent)*elapTime)/percent)/60; //en minuts
				progress.setString(String.format(Locale.ENGLISH, "Iter. %d -> %6.2f %% (est. time %6.2f min.)",bkgIter,percent,estTime));
			}
			//per si s'ha aturat
			if (Thread.interrupted()) {
			    throw new InterruptedException();
			}
		}//dimY
		
		long endTime   = System.currentTimeMillis();
		float totalTime = (endTime - startTime)/1000;
		if(progress!=null){
			progress.setString("Iteration finished!");
		}
		if(txtOut!=null){
			txtOut.addtxt(true, true, "fi iter. "+bkgIter+" (time: "+totalTime+" s)");
		}
		return dataOut;
	}
	
	public static Pattern2D subtractBKG(Pattern2D dataIn, Pattern2D dataToSubtract){
		Pattern2D dataSub = new Pattern2D(dataIn,false);
		dataSub.setMaxI(0);
		dataSub.setMinI(9999999);
		for(int i=0; i<dataSub.getDimY();i++){
			for(int j=0; j<dataSub.getDimX();j++){
				//si la intensitat �s zero saltem
				if(dataIn.getInten(j, i)<0){
					dataSub.setInten(j, i, dataIn.getInten(j, i));
					continue;
				}
				//si la intensitat es menor a LA FEM ZERO
				if(dataIn.getInten(j, i)<dataToSubtract.getInten(j, i)){
					dataSub.setInten(j, i, 0);
					continue;
				}
				dataSub.setInten(j, i, (dataIn.getInten(j, i)-dataToSubtract.getInten(j, i)));
				if(dataSub.getInten(j, i)>dataSub.getMaxI())dataSub.setMaxI(dataSub.getInten(j, i));
				if(dataSub.getInten(j, i)<dataSub.getMinI())dataSub.setMinI(dataSub.getInten(j, i));
			}
		}
		return dataSub;
	}
	
	//return [0] dataSubtracted and [1] dataMask
	public static Pattern2D[] subtractBKG_v2(Pattern2D dataIn, Pattern2D dataToSubtract,float factor,LogJTextArea out){
		Pattern2D[] dataSub = {new Pattern2D(dataIn,false),new Pattern2D(dataIn,false)};
		dataSub[0].setMaxI(0);
		dataSub[1].setMaxI(0);
		dataSub[0].setMinI(9999999);
		dataSub[1].setMinI(9999999);
		int nover = 0;
		for(int i=0; i<dataSub[0].getDimY();i++){
			for(int j=0; j<dataSub[0].getDimX();j++){
				//dataMask comen�a amb el pixel a zero
				dataSub[1].setInten(j, i, 0);
				//si la intensitat �s zero saltem
				if(dataIn.getInten(j, i)<0){
					dataSub[0].setInten(j, i, dataIn.getInten(j, i));
					continue;
				}
				//si la intensitat es menor a LA FEM ZERO
				if(dataIn.getInten(j, i)<(dataToSubtract.getInten(j, i)*factor)){
					dataSub[0].setInten(j, i, 0);
					dataSub[1].setInten(j, i, FastMath.abs(dataIn.getInten(j, i)-dataToSubtract.getInten(j, i)));
					nover=nover+1;
					continue;
				}
				dataSub[0].setInten(j, i, (int) (dataIn.getInten(j, i) - (dataToSubtract.getInten(j, i)*factor)));
				if(dataSub[0].getInten(j, i)>dataSub[0].getMaxI())dataSub[0].setMaxI(dataSub[0].getInten(j, i));
				if(dataSub[0].getInten(j, i)<dataSub[0].getMinI())dataSub[0].setMinI(dataSub[0].getInten(j, i));
			}
		}
		if(out!=null){
            String linia = String.format(Locale.ENGLISH, "  No. of pixels with Ybkg>Ydata = %d (%.1f%%)",
            		nover,((float)(nover)/(float)(dataSub[0].getDimX()*dataSub[0].getDimY())*100));
			out.ln(linia);
            linia = String.format(Locale.ENGLISH, "  Factor = %.5f",factor);
			out.ln(linia);
		}
		return dataSub;
	}
	
	public static float calcGlassScale(Pattern2D data, Pattern2D glass){
		float scale = 10000000;
		float tol = 0.025f;
		for(int i=0; i<data.getDimY();i++){
			for(int j=0; j<data.getDimX();j++){
				//no considerem I=0 o mascara
				if (data.getInten(j, i)<0)continue;
				if (glass.getInten(j, i)<0)continue;
			    float sc = (float) ((data.getInten(j, i))-(float)tol*FastMath.sqrt((float)data.getInten(j, i)))/(float)(glass.getInten(j, i));
				if (sc<scale&&sc!=0)scale=sc;
			}
		}
		return scale;
	}
	
	//subrutina que ens busca i corregeix pics espuris en una imatge de fons regular com el vidre
	public static Pattern2D correctGlass(Pattern2D glass){
		float t2ini=0;
		float t2fin=80;
		float stepsize=0.05f;
		float percent=0.25f;
		float t2p, criteri;
		int pos;
		
		if (!glass.checkIfDistMD()){
		    Param_dialog p = new Param_dialog(null,glass);
		    p.setVisible(true);
		    while (p.isVisible()){
		        try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    if(D2Dplot_global.isDebug())e.printStackTrace();
                    log.warning("Error in correct glass");
                }
		    }
		}
		
		if (!glass.checkIfDistMD()){
		    log.warning("Missing instrumental parameters, aborting");
		    return null;
		}
		
		//primer integrem la imatge
		Pattern1D intrad = radialIntegration(glass, t2ini, t2fin, stepsize, -1,  -1, false, false,0.f);

		//per cada pixel mirarem la intensitat mitjana a l'anell que es troba i si aquesta intensitat es
        //mes gran que ymean+2*desv el considerem pic espuri
		for(int i=0; i<glass.getDimY();i++){
			for(int j=0; j<glass.getDimX();j++){
				//s'haurien d'haver aplicat les zones excloses al vidre
				if(glass.isInExZone(j, i))continue;
				//2Theta pixel imatge per determinar amplada i angle
				t2p = (float) glass.calc2T(j, i, true);
				//ara hem de mirar a quina posicio del vector desv es troba aquesta 2t
				pos = FastMath.round(t2p/stepsize)-FastMath.round(t2ini/stepsize);
	            //ara hem de decidir si es pic espuri o no
	            //dos opcions, amb la mitjana+% o amb la fact*desviacio
				float ysum = (float)intrad.getPoint(pos).getCounts();
				float npix = (float)intrad.getPoint(pos).getNpix();
				criteri= ysum/npix + (ysum/npix)*percent; //factDesv*desv(p)
				//criteri=(real(ysum(p))/real(npix(p)))+factDesv*desv(p)		
	            if(glass.getInten(j,i)>criteri)glass.setInten(j,i, FastMath.round(ysum/npix));
			}
		}
		return glass;
	}
	
//    !pixel a pixel mira la diferencia d'intensitats entre dades i fons
//    !retorna la minima diferencia i el pixel en questio
	//es pot canviar per retornar int[3] si es necessari amb el pixel
	@SuppressWarnings("unused")
    public static int minDif(Pattern2D dataIn, Pattern2D dataBkg){
		int col_X,row_Y,minDiff,diff;
		minDiff=99999999;
		for(int i=0; i<dataIn.getDimY();i++){
			for(int j=0; j<dataIn.getDimX();j++){
				if(dataIn.isInExZone(j, i))continue;
				diff=dataIn.getInten(j, i)-dataBkg.getInten(j, i);
				if(diff<minDiff){
					minDiff=diff;
					col_X=j;
					row_Y=i;
				}
			}
		}
		return minDiff;
	}
	
//    !calcula una nova iteraci� del fons a partir de l'anterior
//    !utilitza integracio RADIAL
//    !parametres: -dataIn: iteracio anterior de referencia
//    !            -stepsize: ...
//    !return: nova iteracio
    public static Pattern2D calcIterAvcirc(Pattern2D dataIn, float stepsize,LogJTextArea txtOut,JProgressBar progress) throws InterruptedException{
  		long startTime = System.currentTimeMillis();
   		//PROGRESS BAR:
   		bkgIter=bkgIter+1;
   		Pattern2D dataOut = new Pattern2D(dataIn,false);
   		
//        !primer fem la integracio radial de la iteracio anterior
//        !calcul del 2T maxim (pixel extrem: 0,0 per exemple)
//        !calcul angle 2teta en graus:
   		float t2fin = (float) dataIn.calc2T(0, 0, true);
   		Pattern1D intrad = radialIntegration(dataIn,0.0f, t2fin, stepsize, -1, -1, false, false, 0.f); //realment aqui en el promig hauriem de NO considerar pics

   		float t2p,ysum,npix;
   		int pos;
   		//dataOut sera el promig de la integracio radial de dataIn
   		for(int i=0; i<dataOut.getDimY();i++){
   			for(int j=0; j<dataOut.getDimX();j++){
   				if(dataIn.isInExZone(j, i)){
   					dataOut.setInten(j, i, dataIn.getInten(j, i));
   					continue;
   				}
   				t2p = (float) dataOut.calc2T(j, i, true);
   				//pos al vector
				pos = FastMath.round(t2p/stepsize)-FastMath.round(0.0f/stepsize);
	            //ara hem de decidir si es pic espuri o no
	            //dos opcions, amb la mitjana+% o amb la fact*desviacio
				ysum = (float)intrad.getPoint(pos).getCounts();
				npix = (float)intrad.getPoint(pos).getNpix();
				dataOut.setInten(j, i, (int) (ysum/npix));
   			}
   			
			//PROGRESS BAR:
			if(progress!=null){
				long elapTime = (System.currentTimeMillis() - startTime)/1000;
				float percent = ((float)i/(float)dataOut.getDimY())*100;
				float estTime = (((100-percent)*elapTime)/percent)/60; //en minuts
				progress.setString(String.format(Locale.ENGLISH, "Iter. %d -> %6.2f %% (est. time %6.2f min.)",bkgIter,percent,estTime));
			}
			//per si s'ha aturat
			if (Thread.interrupted()) {
			    throw new InterruptedException();
			}
   			
   		}
		long endTime   = System.currentTimeMillis();
		float totalTime = (endTime - startTime)/1000;
		if(progress!=null){
			progress.setString("Iteration finished!");
		}
		if(txtOut!=null){
			txtOut.addtxt(true, true, "fi iter. "+bkgIter+" (time: "+totalTime+" s)");
		}
		return dataOut;
    }
    
//    !calcula una nova iteraci� del fons a partir de l'anterior
//    !parametres: -it0: iteracio anterior de referencia
//    !            -it1: nova iteracio
//    !            -N: pixels de la regio rectangular
//    !els valors de -1 (mascara) els deixarem tal qual (no calcularem fons) i
//    !tambe farem que no contribueixin a promitjar el fons d'altres punts. Per
//    !aix� emmagatzemarem a una variable int (nMaskP) el nombre de punts d'aquests
//    !que hagu�ssin contribuit i ho restarem al fer el promig
    public static Pattern2D calcIterAvarc(Pattern2D dataIn,float ampladaArc, float oberturaArc, LogJTextArea txtOut,JProgressBar progress) throws InterruptedException{
  		long startTime = System.currentTimeMillis();

   		bkgIter=bkgIter+1;
   		Pattern2D dataOut = new Pattern2D(dataIn,false);
   		
   		//ara a cada pixel de la imatge calcularem el promig dels del voltant de l'arc
   		for(int i=0; i<dataOut.getDimY();i++){
   			for(int j=0; j<dataOut.getDimX();j++){
   				if(dataIn.isInExZone(j, i)){
   					dataOut.setInten(j, i, dataIn.getInten(j, i));
   					continue;
   				}
   				//per cada pixel mirem el promig (iteracio anterior)
   				float[] fact = dataIn.getFactAngleAmplada(j, i);
   				float obertura = oberturaArc * fact[0];
   				float amplada = ampladaArc * fact[1];
   				Patt2Dzone arc = YarcTilt(dataIn, j, i, amplada, obertura, false, 0, false);
   				
//   	        assignem al pixel la intensitat mitjana (fitxer de fons que despres restarem)
//   	        nomes si la intensitat anterior es superior
   				//TODO: aqui es podria introduir la intensitat del fons calculada a Yarc en comptes de Ymean     
   				if(dataIn.getInten(j, i)>arc.getYmean()){
   					dataOut.setInten(j, i, (int) arc.getYmean());
   				}else{
   					dataOut.setInten(j, i, dataIn.getInten(j, i));
   				}
   			}
   			
			//PROGRESS BAR:
			if((progress!=null)){
				long elapTime = (System.currentTimeMillis() - startTime)/1000;
				float percent = ((float)i/(float)dataOut.getDimY())*100;
				float estTime = (((100-percent)*elapTime)/percent)/60; //en minuts
				progress.setString(String.format(Locale.ENGLISH, "Iter. %d -> %6.2f %% (est. time %6.2f min.)",bkgIter,percent,estTime));
			}
			//per si s'ha aturat
			if (Thread.interrupted()) {
			    throw new InterruptedException();
			}
   		}
   		

		long endTime   = System.currentTimeMillis();
		float totalTime = (endTime - startTime)/1000;
		if(progress!=null){
			progress.setString("Iteration finished!");
		}
		if(txtOut!=null){
			txtOut.addtxt(true, true, "fi iter. "+bkgIter+" (time: "+totalTime+" s)");
		}
		return dataOut;   		
    }
    
    //metode flip
    public static Pattern2D bkgMin(Pattern2D dataIn, int fhor, int fver, int fhorver, int aresta,float oberturaArc, float ampladaArc,boolean minarc,LogJTextArea txtOut,JProgressBar progress) throws InterruptedException{
  		long startTime = System.currentTimeMillis();

    	boolean hor = (fhor == 1) ? true:false;
    	boolean ver = (fver == 1) ? true:false;
    	boolean horver = (fhorver == 1) ? true:false;
    	
   		Pattern2D dataOut = new Pattern2D(dataIn,false);
   		
   		int v0,v1,v2,v3; //valor pixel,valor flip hor, valor flip vert, valor fliphor-flipvert
   		int newj,newi;
   		Patt2Dzone zone;

   		//ara a cada pixel de la imatge calcularem el promig dels del voltant de l'arc
   		for(int i=0; i<dataOut.getDimY();i++){
   			for(int j=0; j<dataOut.getDimX();j++){
   				//exzone
   				if(dataIn.isInExZone(j, i)){
   					dataOut.setInten(j, i, dataIn.getInten(j, i));
   					continue;
   				}
   				//punt central (normalment mascara)
   				if(i==(int)(dataIn.getCentrX())&&j==(int)(dataIn.getCentrY())){
   					dataOut.setInten(j, i, dataIn.getInten(j, i));
   					continue;
   				}
   				//calcul de v0,v1,v2,v3
   				float obertura = 1,amplada = 1;
   				if(minarc){
   	   				float[] fact = dataIn.getFactAngleAmplada(j, i);
   	   				obertura = oberturaArc * fact[0];
   	   				amplada = ampladaArc * fact[1];
   					zone = YarcTilt(dataIn, j, i, amplada, obertura, true, 0, false);
   					v0 = FastMath.round(zone.getYmean());
   				}else{
   					v0 = dataIn.calcIntSquare(j, i, aresta, true);
   				}
   				//inicialitzem v1,v2 i v3 igual a v0 en cas que no s'assignin despres perque queden fora
   				v1=v0;
   				v2=v0;
   				v3=v0;
   				newj=(int)(dataIn.getCentrX()+(dataIn.getCentrX()-j));
   				newi=(int)(dataIn.getCentrY()+(dataIn.getCentrY()-i));
   				
   				if (minarc){
   					//prova amb integracio arc
   					if(hor){
   						zone = YarcTilt(dataIn, newj, i, amplada, obertura, true, 0, false);
   						if(zone.getNpix()>0)v1=FastMath.round(zone.getYmean());
   					}
   					if(ver){
   						zone = YarcTilt(dataIn, j, newi, amplada, obertura, true, 0, false);
   						if(zone.getNpix()>0)v1=FastMath.round(zone.getYmean());
   					}
   					if(horver){
   						zone = YarcTilt(dataIn,newj, newi, amplada, obertura, true, 0, false);
   						if(zone.getNpix()>0)v1=FastMath.round(zone.getYmean());
   					}
   				}else{
   					if(hor){
   						v1 = dataIn.calcIntSquare(newj, i, aresta, true);
   					}
   					if(ver){
   						v2 = dataIn.calcIntSquare(j, newi, aresta, true);
   					}
   					if(horver){
   						v3 = dataIn.calcIntSquare(newj, newi, aresta, true);
   					}
   				}
   				
   				//comprovem que no hi hagi mascares
   				if(v1<0)v1=v0;
   				if(v2<0)v2=v0;
   				if(v3<0)v3=v0;
   				
   				//en cas que tot sigui igual avisem (de moment no el posem a zero...)
   				if(v0==v1&&v0==v2&&v0==v3){
   					//TODO cal fer algo?
   				}
   				
   				//cas normal agafem el minim
   				dataOut.setInten(j, i, (int) ImgOps.findMin(v0,v1,v2,v3));
   			}
			//PROGRESS BAR:
			if(progress!=null){
				long elapTime = (System.currentTimeMillis() - startTime)/1000;
				float percent = ((float)i/(float)dataOut.getDimY())*100;
				float estTime = (((100-percent)*elapTime)/percent)/60; //en minuts
				progress.setString(String.format(Locale.ENGLISH, "Bkg sub. -> %6.2f %% (est. time %6.2f min.)",percent,estTime));
			}
			//per si s'ha aturat
			if (Thread.interrupted()) {
			    throw new InterruptedException();
			}
   			
   		}
		long endTime   = System.currentTimeMillis();
		float totalTime = (endTime - startTime)/1000;
		if(progress!=null){
			progress.setString("Bkg subtraction finished!");
		}
		if(txtOut!=null){
			txtOut.addtxt(true, true, "Bkg subtraction finished (time: "+totalTime+" s)");
		}
		return dataOut; 
    }
    
    //iosc es l'eix de tilt, 1=horitzontal, 2=vertical
    //ilor oscil=1 powder=2, 
    //ipol syn=1 lab=2
    
    //valors ilor ipol
//  ilor=chckbxLorOscil.isSelected()?1:0;
//    ilor=chckbxLorPow.isSelected()?2:ilor;
//    ipol=chckbxPolSyn.isSelected()?1:0;
//    ipol=chckbxPolLab.isSelected()?2:ipol;
    //PER FER LA INTEGRACIO RADIAL ILOR=2, IPOL=1 -- seran els valors per defecte
    //RETURNS A VECTOR: [INTENSITY CORRECTED FOR LP, Lfactor, Pfactor] --- son factors, vol dir que s'han de multiplicar
    public static double[] corrLP(Pattern2D dataIn, int pX, int pY, int iPol, int iLor, int iosc, boolean debug){
        
        double pixsA = dataIn.pixSx*10000000;
        double distA = dataIn.distMD*10000000;
        double fact = pixsA / (1 + dataIn.wavel * distA); //cal convertir wave ang to mm
        if(iosc==-1) iosc = 2; //EIX D'OSCILACIO 1=horitzontal, 2=Vertical
        if(iLor==-1) iLor = 2;
        if(iPol==-1) iPol = 1;

//      per cada pixel s'ha de calcular l'angle azimutal (entre la normal
//      (al pla de polaritzacio *i* el vector del centre de la imatge (xc,yc)
//      al pixel (x,y) en questi�). Tamb� s'ha de calcular l'angle 2T del pixel
        
        double[] nothingdone = {dataIn.getInten(pX, pY),1,1};
        //zona exclosa saltem
        if (dataIn.isInExZone(pX, pY))return nothingdone;
        //el punt central el saltem
        if ((pY == (int)(dataIn.getCentrY()))&&(pX == (int)(dataIn.getCentrX())))return nothingdone;
        
        //debug: tots els punts amb intensitat 12500 (per veure efectes)
        if(debug){
            dataIn.setInten(pX, pY, 12500);
            dataIn.setMaxI(12500);
        }
        
        double vecCPx = (pX-dataIn.getCentrX())*dataIn.pixSx; //en mm
        double vecCPy = (dataIn.getCentrY()-pY)*dataIn.pixSy; //en mm
        double t2 = dataIn.calc2T(pX, pY, false);

        
        //lorentz
        double rloren = 1.0f;
        if (iLor==1){
            double xkug = fact * (pX-dataIn.centrX) * FastMath.cos(t2);
            double ykug = fact * (pY-dataIn.centrY) * FastMath.cos(t2);
            double zkug = 2.0 / dataIn.wavel * FastMath.pow(FastMath.sin(t2/2.0),2); //he canviat waveMM per wavel per igualar amb el jordi PERO no se què esta be...
            double dkug = FastMath.sqrt(xkug*xkug+ykug*ykug+zkug*zkug);
            
            double phi = 1;
            if (iosc == 1) phi = FastMath.acos(xkug/dkug);
            if (iosc == 2) phi = FastMath.acos(ykug/dkug);
            double arg = FastMath.pow(FastMath.sin(phi),2) - FastMath.pow(FastMath.sin(t2/2.0),2);
            log.writeNameNums("CONFIG", true, "pX,pY,xkug,ykug,zkug,dkug,fact,t2,phi,arg",pX,pY,xkug,ykug,zkug,dkug,fact,FastMath.toDegrees(t2),phi,arg);
            rloren=0;
            if (arg>0){
                rloren = 2 * FastMath.sin(t2/2.0) * FastMath.sqrt(arg);    
            }
            
        }
        if (iLor == 2) {
            rloren=1.0/(FastMath.cos(t2/2)*FastMath.pow(FastMath.sin(t2/2),2)); //igual que el DAJUST
        }
        
        //polaritzacio
        double pol = 1.0f;
        if (iPol==1){
            double xdist2 = vecCPx*vecCPx;
            double ydist2 = vecCPy*vecCPy;
            double sepOD2 = dataIn.distMD*dataIn.distMD;
            if (iosc==1) pol = (sepOD2 + xdist2) / (sepOD2 + xdist2 + ydist2);
            if (iosc==2) pol = (sepOD2 + ydist2) / (sepOD2 + xdist2 + ydist2);
        }
        
        if (iPol==2){
            pol=0.5 + 0.5 * (FastMath.cos(t2)*FastMath.cos(t2));
        }

        //cas sense oscil·lacio
        if(iosc==0){
            rloren=1.0f;
            pol=1.0f;
        }
        
        double[] result = {(dataIn.getInten(pX, pY)*rloren)/pol,rloren,1/pol};
        return result;
    }
    
    
    //idem pero corregint tot el pattern
    public static Pattern2D corrLP(Pattern2D dataIn,int iPol, int iLor, int iosc, boolean debug){
        Pattern2D dataOut = new Pattern2D(dataIn,false);
        int maxVal=0;
        if(iosc==-1) iosc = 2;
        
//      per cada pixel s'ha de calcular l'angle azimutal (entre la normal
//      (al pla de polaritzacio *i* el vector del centre de la imatge (xc,yc)
//      al pixel (x,y) en questi�). Tamb� s'ha de calcular l'angle 2T del pixel
        for(int i=0; i<dataIn.getDimY();i++){
            for(int j=0; j<dataIn.getDimX();j++){

                double[] lpfac = corrLP(dataIn,j,i,iPol,iLor,iosc,false);
                
                //debug: tots els punts amb intensitat 12500 (per veure efectes)
                if(debug){
                    dataOut.setInten(j, i, FastMath.round(12500*(float)lpfac[1]*(float)lpfac[2]));
                }else{
                    dataOut.setInten(j, i, FastMath.round((float)lpfac[0]));    
                }
                //mirem si superem els limits (per escalar despres)
                if (dataOut.getInten(j, i)>maxVal) maxVal = dataOut.getInten(j, i);
            }
        }
        
        //si ens hem passat del maxim calculem el factor d'escala i escalem
        float fscale=1.0f;
        if(maxVal>dataOut.getSaturValue()){
            fscale = (float)(dataOut.getSaturValue())/(float)maxVal;
        }
        for(int i=0; i<dataOut.getDimY();i++){
            for(int j=0; j<dataOut.getDimX();j++){
                //mascara el deixem tal qual
                if(dataIn.isInExZone(j, i)){
                    dataOut.setInten(j, i, dataOut.getInten(j, i));
                    continue;
                }
                dataOut.setInten(j, i, FastMath.round((float)dataOut.getInten(j, i)*fscale));
            }
        }
        return dataOut;
    }
	
    //retorna un array amb {Intensitat corregida, factor aplicat}
    public static float[] corrIncidentAngle(Pattern2D dataIn, int pX, int pY){

        float azim = dataIn.getAzimAngle(pX,pY,false);
        float tthRad = (float) dataIn.calc2T(pX, pY, false);
        
        //angle incident effectiu:
        float tiltRad = (float) FastMath.toRadians(dataIn.getTiltDeg());
        float rotRad = (float) FastMath.toRadians(dataIn.getRotDeg());
        float tiltRadEff = (float) (tiltRad*(FastMath.sin(azim-rotRad)));
        float incAngEff = tthRad - tiltRadEff;
        
        //la correccio Icorr = Iobs/K on K= cos3 incAngEff
        float K = (float) (FastMath.cos(incAngEff)*FastMath.cos(incAngEff)*FastMath.cos(incAngEff));
        float[] result = {dataIn.getInten(pX, pY)/K,1/K};
        return result;
    }
    
    //when we click to a point we add the peak but calculating the things (saturated, nearmaks, etc..) as in FindPeaks
    //neixam no ho considerem perque l'afegim apart
    public static Peak addPeakFromCoordinates(Pattern2D patt2d, Point2D.Float pixel, int zoneRadius){
        Ellipse2D.Float elli = new Ellipse2D.Float(pixel.x-zoneRadius,pixel.y-zoneRadius,zoneRadius*2,zoneRadius*2);
        int ix = (int)(pixel.x);
        int iy = (int)(pixel.y);
        
        Peak pic = new Peak(pixel);
        int sumaX=0;
        int sumaY=0;
        int sumaInt=0;
        
        //mirem nearmask, saturats i determinem millor el centre
        for(int i=iy-zoneRadius; i<iy+(zoneRadius*2);i++){
            for(int j=ix-zoneRadius; j<ix+(zoneRadius*2);j++){
                if (elli.contains(j, i)){
                    if (!patt2d.isInside(j, i)){
                        pic.setNearMask(true);
                        continue;
                    }
                    if (patt2d.isInExZone(j, i)){
                        pic.setNearMask(true);
                        continue;
                    }
                    if (patt2d.getInten(j, i)>=(patt2d.getSaturValue()-1)){
                        pic.setnSatur(pic.getnSatur()+1);
                    }
                    sumaX = sumaX + patt2d.getInten(j, i)*j;
                    sumaY = sumaY + patt2d.getInten(j, i)*i;
                    sumaInt = sumaInt + patt2d.getInten(j, i);
                }
            }
            float xpond = (float)sumaX/(float)sumaInt;
            float ypond = (float)sumaY/(float)sumaInt;
            pic.getPixelCentre().x=xpond;
            pic.getPixelCentre().y=ypond;
        }
        return pic;
    }
    
    //roundToInt fa que es guardin fent un round
    public static ArrayList<Peak> findPeaks(Pattern2D patt2d, float delsig, int zoneRadius, boolean t2dependent, int zones2t, int minpix, boolean roundToInt){      

        ArrayList<Point2D.Float> foundCandidates = new ArrayList<Point2D.Float>();
        
        patt2d.calcMeanI();
        
        //llindar general (despres es pot fer per 2t)
        float llindarGeneral = patt2d.meanI + delsig*patt2d.sdevI;
        
        //llindar per zones (algunes variables fora perque s'utilitzen mes avall)
        int nzones = zones2t; //i.e. caldran nzones+1 valors de px (un valor de 8 va be)
        float[] izone2t = new float[nzones+1]; //llindars de les zones en 2THETA en la direccio horitzontal (X)
        float dbLlin[] = new float[nzones];

        //primer determinem les zones: py=centre, px=divisioPelNombreDeZones
        if (t2dependent){
            float py=patt2d.getCentrY();
            float angdeg = 15f; //es sobreesciura
            
            int lhaperture = 75;//longitudinal Half-aperture in pixels, to determine the angdeg
            
            float div = (patt2d.getDimX()-patt2d.getCentrX())/nzones;
            float[] izonepx = new float[nzones+1]; //llindars de les zones en PIXELS en la direccio horitzontal (X)
            izonepx[0]=patt2d.getCentrX();
            izone2t[0]=0f;
            for (int i=1;i<nzones+1;i++){
                izonepx[i]=izonepx[i-1]+div;
                izone2t[i]=(float) patt2d.calc2T(izonepx[i],py, true);
            }
            log.writeNameNums("CONFIG", true, "izonePX", izonepx[0],izonepx[1],izonepx[2],izonepx[3],izonepx[4],izonepx[5],izonepx[6],izonepx[7],izonepx[8]);
            log.writeNameNums("CONFIG", true, "izone2t", izone2t[0],izone2t[1],izone2t[2],izone2t[3],izone2t[4],izone2t[5],izone2t[6],izone2t[7],izone2t[8]);
            
            //ara a quina tol2t correspon div
            float t2a = (float) patt2d.calc2T(izonepx[nzones/2], py, true);
            float t2b = (float) patt2d.calc2T(izonepx[nzones/2+1], py, true);
            float tol2t = t2b-t2a;
            
            Patt2Dzone[] zones = new Patt2Dzone[nzones];
            
            for (int i=1;i<nzones+1;i++){
                int pxcen = FastMath.round((izonepx[i]+izonepx[i-1])/2);
                angdeg = (float) FastMath.atan(lhaperture/((izonepx[i]+izonepx[i-1])/2));
                angdeg = (float) FastMath.toDegrees(angdeg);
                zones[i-1] = YarcTilt(patt2d,pxcen,patt2d.getCentrYI(),tol2t,angdeg,true,0,false);
                log.debug("angdeg="+angdeg);
            }
            
            //llista dels llindars
            for (int k=0;k<nzones;k++){
                dbLlin[k] = zones[k].getYmean() + delsig*patt2d.sdevI;//delsig*zones[k].getYmeandesv();
            }
            log.writeNameNums("CONFIG", true, "dbLlin", dbLlin[0],dbLlin[1],dbLlin[2],dbLlin[3],dbLlin[4],dbLlin[5],dbLlin[6],dbLlin[7]);
            log.writeNameNums("CONFIG", true, "LlindarGeneral", llindarGeneral);
        }
        
        //de 1 a -1 per no agafar els pixels de les vores
        for(int i=1; i<patt2d.getDimY()-1;i++){
            for(int j=1; j<patt2d.getDimX()-1;j++){
                
                if (patt2d.isInExZone(j, i))continue;
                
                int pxInten = patt2d.getInten(j, i);
                
                float llindar = llindarGeneral;
                
                if (t2dependent){
                    //establim el llindar segons 2th
                    float t2p = (float) patt2d.calc2T(j, i,true);
                    for (int k=0;k<nzones;k++){
                        if ((t2p>=izone2t[k])&&(t2p<=izone2t[k+1])){
                            //esta a la zona
                            llindar = dbLlin[k];
                            break;
                        }
                    }
                }
                
                if (pxInten<llindar)continue;
                
                boolean possiblepeak = true;
                //2n comprovem quie la intensitat es superior als 8 veins
                search8neigbors:
                    for (int ii=i-1;ii<i+2;ii++){
                        for (int jj=j-1;jj<j+2;jj++){
                            if (patt2d.isInExZone(jj, ii))continue;
                            if ((ii==i)&&(jj==j))continue; //same pixel
                            try{
                                if (pxInten< patt2d.getInten(jj, ii)){ //px no es pic
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
                log.writeNameNums("CONFIG", true, "possible peak (x,y,inten,llindar)", j,i,pxInten,llindar);
                
                boolean hasMinEnough = true;
                if(minpix>1){
                    if (minpix > 20) minpix=20; //restriccio que poso
                    int npixbons = 0;
                    for (int ii=i-2;ii<i+3;ii++){
                        for (int jj=j-2;jj<j+3;jj++){
                            if (!patt2d.isInside(jj, ii))continue;
                            if (patt2d.isInExZone(jj, ii))continue;
                            if (patt2d.getInten(jj, ii)>llindar){
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
                if (hasMinEnough) foundCandidates.add(new Point2D.Float(j,i));
            }
        }
        
        if (foundCandidates.size()==0)return null;

        //REALMENT LA SEGONA PASSADA ES PODRIA FER DUES VEGADES SI ES VEU QUE FALTA RANG...
        //segona passada, colapsem els pics on hi ha intensitat igual en pixels consecutius (i identifiquem els saturats?)
        ArrayList<Point2D.Float> toRemove = new ArrayList<Point2D.Float>();
        ArrayList<Point2D.Float> toAdd = new ArrayList<Point2D.Float>();
        
        boolean finished = false;
        while (!finished){
            if (foundCandidates.isEmpty()){
                finished=true;
                continue;
            }
            Point2D.Float pk = foundCandidates.get(0);
            int x = (int)(pk.x);
            int y = (int)(pk.y);

            float sumX = pk.x;
            float sumY = pk.y;
            int pkinten = patt2d.getInten(x, y);
            
            foundCandidates.remove(pk); //l'eliminem
            toRemove = new ArrayList<Point2D.Float>();

            Rectangle2D.Float rect = new Rectangle2D.Float(x-zoneRadius, y-zoneRadius, zoneRadius*2, zoneRadius*2);
            int niguals = 1;
            
            for (int i=0; i<foundCandidates.size();i++){ //que passa amb l'ultim pic? s'ha d'afegir tal cual
                Point2D.Float pk2 = foundCandidates.get(i);
                if (pk.equals(pk2))continue; //no hauria de passar ja que l'he eliminat...
                if (rect.contains(pk2)){
                    int pk2inten = patt2d.getInten(Math.round(pk2.x), Math.round(pk2.y));
                    if (pkinten == pk2inten){
                        sumX = sumX + pk2.x;
                        sumY = sumY + pk2.y;
                        niguals = niguals +1;
                        toRemove.add(pk2); //l'eliminarem al sortir del bucle
                        log.writeNameNums("CONFIG", true, "removed candidate (x,y)=", pk2.x,pk2.y);
                    }
                }
            }
            foundCandidates.removeAll(toRemove); //eliminem els que ja hem considerat

            if (niguals > 1){//hi ha hagut merging
                //afegim el pic "suma"
                float xf = sumX/niguals;
                float yf = sumY/niguals;
                if (roundToInt){
                    xf = FastMath.round(xf);
                    yf = FastMath.round(yf);
                }
                toAdd.add(new Point2D.Float(xf,yf));
                log.writeNameNums("CONFIG", true, "removed candidate (x,y)=", pk.x,pk.y);
                log.writeNameNums("CONFIG", true, "added new candidate (from merging) (x,y)=", xf,yf);
            }else{
                //hem de tornar a afegir el pic que hem eliminat!
                toAdd.add(new Point2D.Float(pk.x,pk.y));
            }
            
        }
        if (!toAdd.isEmpty())foundCandidates.addAll(toAdd);

        
        //tercera passada busquem pics aprop dels altres (tambe col·lapsem) pero considerem eixam i ens quedem amb el major maxim.
        //tambe identifiquem pics propers a mascara
        ArrayList<Peak> realPeaks = new ArrayList<Peak>();
        finished = false;
        while (!finished){
            if (foundCandidates.isEmpty()){
                finished=true;
                continue;
            }
            Point2D.Float pk = foundCandidates.get(0);
            int x = (int)(pk.x);
            int y = (int)(pk.y);

            float bestX = pk.x;
            float bestY = pk.y;
            int maxInten = patt2d.getInten(x, y);
            int nEixam = 1;

            foundCandidates.remove(pk); //l'eliminem
            toRemove = new ArrayList<Point2D.Float>();
            Ellipse2D.Float elli = new Ellipse2D.Float(x-zoneRadius,y-zoneRadius,zoneRadius*2,zoneRadius*2);
            
            for (int i=0; i<foundCandidates.size();i++){ //que passa amb l'ultim pic? s'ha d'afegir tal cual
                Point2D.Float pk2 = foundCandidates.get(i);
                if (pk.equals(pk2))continue; //no hauria de passar ja que l'he eliminat...
                if (elli.contains(pk2)){
                    int pk2inten = patt2d.getInten(Math.round(pk2.x), Math.round(pk2.y));
                    if (pk2inten>maxInten){
                        //aquest es mes intens, ens el quedem
                        bestX = pk2.x;
                        bestY = pk2.y;
                        maxInten = pk2inten;
                    }
                    nEixam = nEixam+1;
                    toRemove.add(pk2); //l'eliminarem al sortir del bucle
                    log.config("eixam found");
                }
            }
            foundCandidates.removeAll(toRemove); //eliminem els que ja hem considerat

            //afegim el pic
            Peak pic = new Peak(bestX,bestY);
            pic.setnVeinsEixam(nEixam);

            //mirem ara els saturats i si està prop d'una mascara
            x = (int)(pic.getPixelCentre().x);
            y = (int)(pic.getPixelCentre().y);
            
            for(int i=y-zoneRadius; i<y+(zoneRadius*2);i++){
                for(int j=x-zoneRadius; j<x+(zoneRadius*2);j++){
                    if (elli.contains(j, i)){
                        if (!patt2d.isInside(j, i)){
                            pic.setNearMask(true);
                            continue;
                        }
                        if (patt2d.isInExZone(j, i)){
                            pic.setNearMask(true);
                            continue;
                        }
                        if (patt2d.getInten(j, i)>=(patt2d.getSaturValue()-1)){
                            pic.setnSatur(pic.getnSatur()+1);
                        }
                    }
                }
            }
            
            //TEST de determinar una mica millor el màxim amb els 8 veins ---> FUNCIONA PERO HO PROVARE AMB MES VEINS PER TAMBE MIRAR EL FONS ... no, ho fare a l'integrar millor...
            int sumaX=0;
            int sumaY=0;
            int sumaInt=0;
            for (int ii=y-1;ii<y+2;ii++){
                for (int jj=x-1;jj<x+2;jj++){
                    if (patt2d.isInExZone(jj, ii))continue;
                    sumaX = sumaX + patt2d.getInten(jj, ii)*jj;
                    sumaY = sumaY + patt2d.getInten(jj, ii)*ii;
                    sumaInt = sumaInt + patt2d.getInten(jj, ii);
                }
            }
            float xpond = (float)sumaX/(float)sumaInt;
            float ypond = (float)sumaY/(float)sumaInt;
            pic.getPixelCentre().x=xpond;
            pic.getPixelCentre().y=ypond;
            
            //ja hem acabat!!
            
            realPeaks.add(pic);
            log.writeNameNums("CONFIG", true, "peak added (x,y,inten,neixam,nsatur)", pic.getPixelCentre().x, pic.getPixelCentre().y,patt2d.getInten(x,y),pic.getnVeinsEixam(),pic.getnSatur());
        }
        
        return realPeaks;
    }
	
	public static float findMax(float... vals) {
		   float max = -99999999;

		   for (float d : vals) {
		      if (d > max) max = d;
		   }
		   return max;
	}
	
	public static float findMin(float... vals) {
		   float min = 99999999;

		   for (float d : vals) {
		      if (d < min) min = d;
		   }
		   return min;
	}
	
	public static double findMin(double... vals) {
	       double min = 99999999;

           for (double d : vals) {
              if (d < min) min = d;
           }
           return min;
    }

	public static int getBkgIter() {
		return bkgIter;
	}

	public static void setBkgIter(int bkgIter) {
		ImgOps.bkgIter = bkgIter;
	}

    public static float getDspacingFromHKL(int h, int k, int l, float a, float b, float c, float alfaDeg, float betaDeg, float gammaDeg){
        
        double cosal = FastMath.cos(FastMath.toRadians(alfaDeg));
        double cosbe = FastMath.cos(FastMath.toRadians(betaDeg));
        double cosga = FastMath.cos(FastMath.toRadians(gammaDeg));
        double sinal = FastMath.sin(FastMath.toRadians(alfaDeg));
        double sinbe = FastMath.sin(FastMath.toRadians(betaDeg));
        double singa = FastMath.sin(FastMath.toRadians(gammaDeg));
        
        double s11 = b*b*c*c*sinal*sinal;
        double s22 = a*a*c*c*sinbe*sinbe;
        double s33 = a*a*b*b*sinbe*singa;
        double s12 = a*b*c*c*(cosal*cosbe-cosga);
        double s23 = a*a*b*c*(cosbe*cosga-cosal);
        double s13 = a*b*b*c*(cosga*cosal-cosbe);
        
        double insqrt = 1 - cosal*cosal - cosbe*cosbe - cosga*cosga + 2*cosal*cosbe*cosga;
        double vol = a*b*c*(FastMath.sqrt(insqrt)); //Ang3
        
        double fact = s11*h*h + s22*k*k + s33*l*l + 2*s12*h*k + 2*s23*k*l + 2*s13*h*l;
        double invdsp2 = fact*(1/(vol*vol));
        double dsp = FastMath.sqrt(1/invdsp2);
        
        return (float)dsp;
    }
    
    public static void getDspacingFromHKL(ArrayList<PDReflection> refs, float a, float b, float c, float alfaDeg, float betaDeg, float gammaDeg){
        
        double cosal = FastMath.cos(FastMath.toRadians(alfaDeg));
        double cosbe = FastMath.cos(FastMath.toRadians(betaDeg));
        double cosga = FastMath.cos(FastMath.toRadians(gammaDeg));
        double sinal = FastMath.sin(FastMath.toRadians(alfaDeg));
        double sinbe = FastMath.sin(FastMath.toRadians(betaDeg));
        double singa = FastMath.sin(FastMath.toRadians(gammaDeg));
        
        double s11 = b*b*c*c*sinal*sinal;
        double s22 = a*a*c*c*sinbe*sinbe;
        double s33 = a*a*b*b*sinbe*singa;
        double s12 = a*b*c*c*(cosal*cosbe-cosga);
        double s23 = a*a*b*c*(cosbe*cosga-cosal);
        double s13 = a*b*b*c*(cosga*cosal-cosbe);
        
        double insqrt = 1 - cosal*cosal - cosbe*cosbe - cosga*cosga + 2*cosal*cosbe*cosga;
        double vol = a*b*c*(FastMath.sqrt(insqrt)); //Ang3
        
        Iterator<PDReflection> itrr = refs.iterator();
        while (itrr.hasNext()){
            PDReflection p = itrr.next();
            int h = p.getH();
            int k = p.getL();
            int l = p.getL();
            double fact = s11*h*h + s22*k*k + s33*l*l + 2*s12*h*k + 2*s23*k*l + 2*s13*h*l;
            double invdsp2 = fact*(1/(vol*vol));
            p.setDsp((float)FastMath.sqrt(1/invdsp2));
        }
    }
    
    public static float getTol2TFromIntRad(Pattern2D patt2d, float px, float py,int intRadPixels){
        
        //vector centre-pixel
        float vPCx=px-patt2d.getCentrX();
        float vPCy=patt2d.getCentrY()-py;
        
        //vector unitari
        float modul = (float) FastMath.sqrt(vPCx*vPCx + vPCy*vPCy);
        vPCx = vPCx/modul;
        vPCy = vPCy/modul;
        
        float pixelInX = vPCx * (modul-intRadPixels/2); //in=intern
        float pixelInY = vPCy * (modul-intRadPixels/2);
        float pixelExX = vPCx * (modul+intRadPixels/2); //ex=extern
        float pixelExY = vPCy * (modul+intRadPixels/2);
        
        pixelInX = patt2d.getCentrX() + pixelInX;
        pixelInY = patt2d.getCentrY() - pixelInY;
        pixelExX = patt2d.getCentrX() + pixelExX;
        pixelExY = patt2d.getCentrY() - pixelExY;
        
        return (float) (patt2d.calc2T(pixelExX, pixelExY, true) - patt2d.calc2T(pixelInX, pixelInY, true));
    }
    
    //igual que el de sota pero donant la IntRad en pixels i no 2theta
    public static Patt2Dzone YarcTilt(Pattern2D patt2D, int px, int py, int intRadPixels, float azimApertureDeg, boolean self, int bkgpt, boolean debug){

        float tol2t = getTol2TFromIntRad(patt2D,px,py,intRadPixels);
        return YarcTilt(patt2D, px, py, tol2t, intRadPixels, azimApertureDeg, self, bkgpt, debug);
    }

    public static Patt2Dzone YarcTilt(Pattern2D patt2D, int px, int py, float tol2tDeg, float azimApertureDeg, boolean self, int bkgpt, boolean debug){
        
            //calculem intRadPixels a partr de la 2theta per tenir-ne el valor
            float t2i = (float)patt2D.calc2T(px, py, true);
            float azim = patt2D.getAzimAngle(px, py, true);
            Point2D.Float extern = patt2D.getPixelFromAzimutAnd2T(azim, t2i+(tol2tDeg/2));
            Point2D.Float intern = patt2D.getPixelFromAzimutAnd2T(azim, t2i-(tol2tDeg/2));
            float sumX=0.0f;
            float sumY=0.0f;
            if ((extern!=null)&&(intern!=null)){
                sumX = extern.x-intern.x;
                sumY = extern.y-intern.y;
            }else{
                if (extern==null){
                    sumX = patt2D.getDimX()-intern.x;
                    sumY = patt2D.getDimY()-intern.y;
                }else{ //intern is null
                    sumX = extern.x;
                    sumY = extern.y;
                }
            }
            int intRadPixels = FastMath.round((float) FastMath.sqrt(sumX*sumX+sumY*sumY));
            
            return YarcTilt(patt2D, px, py, tol2tDeg, intRadPixels, azimApertureDeg, self, bkgpt, debug);

    }
    
    //tol2theta = quina tolerancia en 2theta volem integrar, es el rang TOTAL (es fara tol2t/2 en cada direccio)
    //angle = angle TOTAL d'obertura de l'arc a integrar en GRAUS
    //consideraElTIlt
    private static Patt2Dzone YarcTilt(Pattern2D patt2D, int px, int py, float tol2tDeg, int intRadPixels, float azimApertureDeg, boolean self, int bkgpt, boolean debug){
        //limits tth i azim
        EllipsePars elli = getElliPars(patt2D, new Point2D.Float(px,py));
        float azimAngle = patt2D.getAzimAngle(px, py, true); //azimut des del zero
        float azimMax = azimAngle + azimApertureDeg/2;
        float azimMin = azimAngle - azimApertureDeg/2;
        if (azimMax<azimMin){
            //vol dir que passem pel zero
            azimMax = azimMax + 360;
        }
        //calclulem be la pixelTolerance, quants pixels hi ha en aquest increment de 2theta (no cal que sigui exacte) i realment s'hauria de fer servir la meitat
        float step = patt2D.calcMinStepsizeBy2Theta4Directions();
        float pixelTolerance = tol2tDeg/(step/1.5f); //no faig servir la meitat per precaucio
        
        
        ArrayList<Point2D.Float> pixelsArc = elli.getEllipsePoints(azimMin, azimMax, 1);
        float t2p = (float) patt2D.calc2T(px, py, true);
        float t2max = (float) FastMath.min(t2p + tol2tDeg/2.,patt2D.getMax2TdegCircle());
        float t2min = (float) FastMath.max(0.1, t2p - tol2tDeg/2.);
        log.writeNameNumPairs("FINE", true, "t2p,t2max,t2min,pixelTolerance,step",t2p,t2max,t2min,pixelTolerance,step);
        log.writeNameNumPairs("FINE", true, "azimAngle,azimMax,azimMin", azimAngle,azimMax,azimMin);

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
        int maxX = FastMath.round(findMax(xs)+pixelTolerance);
        int minX = FastMath.round(findMin(xs)-pixelTolerance);
        int maxY = FastMath.round(findMax(ys)+pixelTolerance);
        int minY = FastMath.round(findMin(ys)-pixelTolerance);
        maxX = FastMath.min(maxX,patt2D.dimX-1);
        maxY = FastMath.min(maxY,patt2D.dimY-1);
        minX = FastMath.max(minX,0);
        minY = FastMath.max(minY,0);
        log.writeNameNumPairs("FINE", true, "minX,maxX,minY,maxY", minX,maxX,minY,maxY);

        //iniciacions valors
        int npix=0;
        int ysum=0;
        int ymax=0;
        ArrayList<Integer> minint = new ArrayList<Integer>(bkgpt); //vector amb les bkgpt intensitats menors
        ArrayList<Integer> intensitats = new ArrayList<Integer>(); //vector on guardarem les intensitats per calcular desv.estd
        float ymean=0;
        float ybkg=0;
        float ymeandesv=0;
        float ybkgdesv=0;
        float sumdesv=0;
        int bkgsum=0;
        for(int i=0;i<bkgpt;i++){
            minint.add(patt2D.getSaturValue());
        }
        
        //ara ja tenim el quadrat on hem de buscar
        for (int j=minY;j<=maxY;j++){
           for (int i=minX;i<=maxX;i++){
               //si esta fora la imatge o es mascara el saltem
               if(!patt2D.isInside(i, j))continue;
               if(patt2D.isInExZone(i, j))continue;
               //ell mateix?
               if ((i==px)&&(j==py)&&(!self))continue;             
               
               //comprovacions
               t2p = (float)patt2D.calc2T(i, j, true);
               azimAngle = patt2D.getAzimAngle(i, j, true); //azimut des del zero
               
               if((t2p<t2min)||(t2p>t2max))continue;
               if (azimMax>360){
                   if((azimAngle<azimMin)&&((azimAngle+360)>azimMax))continue;
               }else{//cas normal
                   if((azimAngle>azimMax)||(azimAngle<azimMin))continue;    
               }
                  
               //si hem arribat aqu� es que hem se sumar la intensitat del pixel
               ysum=ysum+patt2D.getInten(i, j);
               if(ymax<patt2D.getInten(i, j))ymax=patt2D.getInten(i, j);
               for(int k=0;k<bkgpt;k++){
                   //TODO:si val zero no considerem (s'hauria de reconsiderar...)
                   //if(patt2D.getIntenB2(i, j)==0)break;
                   if(patt2D.getInten(i, j)<minint.get(k)){
                       minint.set(k, patt2D.getInten(i, j));
                       break; //sortim del for, ja hem utilitzat la intensitat
                   }
               }

               npix=npix+1;
               intensitats.add(patt2D.getInten(i, j));
               
               //debug
               if(debug)patt2D.setInten(i, j, -1);
           }
        }
        
        if(npix>0){
            //calcul desviacio estandar sqrt((sum(xi-xmean)^2)/N-1)
            ymean=(float)(ysum)/(float)(npix);
            sumdesv=0;
            Iterator<Integer> it = intensitats.iterator();
            while (it.hasNext()){
                int inten = it.next();
                sumdesv=sumdesv + ((float)(inten)-ymean)*((float)(inten)-ymean);
            }
            if(npix<2)npix=2;
            ymeandesv=(float) FastMath.sqrt(sumdesv/(float)(npix-1));
            //calcul del valor de fons i la desviacio
            bkgsum=0;
            int nbkgpt=FastMath.min(bkgpt, npix);
            int neffectiveBkgPt = 0; //afegit 160920
            for (int i=0; i<nbkgpt;i++){
                if (minint.get(i)>ymean)continue;
                bkgsum = bkgsum + minint.get(i);
                neffectiveBkgPt = neffectiveBkgPt +1;
            }
            ybkg = (float)(bkgsum)/(float)(neffectiveBkgPt);
            sumdesv=0;
            for (int i=0; i<nbkgpt;i++){
                if (minint.get(i)>ymean)continue;
                sumdesv = sumdesv + ((float)(minint.get(i))-ybkg)*((float)(minint.get(i))-ybkg);
            }
            ybkgdesv=(float) FastMath.sqrt(sumdesv/(float)(neffectiveBkgPt-1));
        }
        
        Patt2Dzone pz = new Patt2Dzone(npix, ysum, ymax, ymean, ymeandesv, ybkg, ybkgdesv);
        pz.setIntradPix(intRadPixels);
        pz.setAzimAngle(azimApertureDeg);
        pz.setBkgpt(bkgpt);
        pz.setCentralPoint(new Point2D.Float(px,py));
        pz.setPatt2d(patt2D);
        return pz;
    }
    
    
    public static int ArcNPix(Pattern2D patt2D, int px, int py, int intRadPixels, float angleDeg){
        //vector centre-pixel
        float vPCx=px-patt2D.getCentrX();
        float vPCy=patt2D.getCentrY()-py;
        
        //vector unitari
        float modul = (float) FastMath.sqrt(vPCx*vPCx + vPCy*vPCy);
        vPCx = vPCx/modul;
        vPCy = vPCy/modul;
        
        float pixelInX = vPCx * (modul-intRadPixels/2); //in=intern
        float pixelInY = vPCy * (modul-intRadPixels/2);
        float pixelExX = vPCx * (modul+intRadPixels/2); //ex=extern
        float pixelExY = vPCy * (modul+intRadPixels/2);
        
        pixelInX = patt2D.getCentrX() + pixelInX;
        pixelInY = patt2D.getCentrY() - pixelInY;
        pixelExX = patt2D.getCentrX() + pixelExX;
        pixelExY = patt2D.getCentrY() - pixelExY;
        
        float tol2t = (float) (patt2D.calc2T(pixelExX, pixelExY, true) - patt2D.calc2T(pixelInX, pixelInY, true));
        
        return ArcNPix(patt2D, px, py, tol2t, angleDeg);

    }

    //RETORNA EL NOMBRE DE PICS EN UN ARC
    //tol2theta = quina tolerancia en 2theta volem integrar, es el rang TOTAL (es fara tol2t/2 en cada direccio)
    //angle = angle TOTAL d'obertura de l'arc a integrar en GRAUS
    //consideraElTIlt
    public static int ArcNPix(Pattern2D patt2D, int px, int py, float tol2t, float angleDeg){
        //limits tth i azim
        EllipsePars elli = getElliPars(patt2D, new Point2D.Float(px,py));
        float azimAngle = patt2D.getAzimAngle(px, py, true); //azimut des del zero
        float azimMax = azimAngle + angleDeg/2;
        float azimMin = azimAngle - angleDeg/2;
        if (azimMax<azimMin){
            //vol dir que passem pel zero
            azimMax = azimMax + 360;
        }
        //calclulem be la pixelTolerance, quants pixels hi ha en aquest increment de 2theta (no cal que sigui exacte) i realment s'hauria de fer servir la meitat
        float step = patt2D.calcMinStepsizeBy2Theta4Directions();
        float pixelTolerance = tol2t/(step/1.5f); //no faig servir la meitat per precaucio
        
        ArrayList<Point2D.Float> pixelsArc = elli.getEllipsePoints(azimMin, azimMax, 1);
        float t2p = (float) patt2D.calc2T(px, py, true);
        float t2max = (float) FastMath.min(t2p + tol2t/2.,patt2D.getMax2TdegCircle());
        float t2min = (float) FastMath.max(0.1, t2p - tol2t/2.);
        
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
        int maxX = FastMath.round(findMax(xs)+pixelTolerance);
        int minX = FastMath.round(findMin(xs)-pixelTolerance);
        int maxY = FastMath.round(findMax(ys)+pixelTolerance);
        int minY = FastMath.round(findMin(ys)-pixelTolerance);
        maxX = FastMath.min(maxX,patt2D.dimX-1);
        maxY = FastMath.min(maxY,patt2D.dimY-1);
        minX = FastMath.max(minX,0);
        minY = FastMath.max(minY,0);
        
        //ara ja tenim el quadrat on hem de buscar
        int npix = 0;
        for (int j=minY;j<=maxY;j++){
           for (int i=minX;i<=maxX;i++){
               //si esta fora la imatge o es mascara el saltem
               if(!patt2D.isInside(i, j))continue;
               if(patt2D.isInExZone(i, j))continue;
               t2p = (float)patt2D.calc2T(i, j, true);
               if((t2p<t2min)||(t2p>t2max))continue;
               if (azimMax>360){
                   if((azimAngle<azimMin)&&((azimAngle+360)>azimMax))continue;
               }else{//cas normal
                   if((azimAngle>azimMax)||(azimAngle<azimMin))continue;    
               }
               //siarribem aqui el sumem
               npix = npix +1;
           }
        }
        return npix;
    }	
	public static float azimAngleOfAPeak(Pattern2D patt2D, float px, float py){
	    int ipx = (int)(px);
	    int ipy = (int)(py);
        EllipsePars elli = getElliPars(patt2D, new Point2D.Float(px,py));
        float azimAngle = patt2D.getAzimAngle(ipx, ipy, true); //azimut des del zero
        Point2D.Float central = elli.getEllipsePoint(azimAngle);
        
        log.debug(String.format("haurien de coincidir px,py= %f,%f entrats amb CentralSegonsAzimut %f,%f",px,py,central.x,central.y));
        
        //farem com amb intrad
        int maxPixels = 150;
        float halfleft = 0;
        float halfright = 0;
        
        //ara anirem mirant pixel per pixel en el radi la desviacio de la intensitat
        //primer cap a l'interior:
        int centralPixelIntensity = patt2D.getInten(ipx,ipy); 
        int previousIntensity = centralPixelIntensity;
        int minI = centralPixelIntensity;
        int previousX = ipx;
        int previousY = ipy;
        int pixcount = 1;
        int countdown = 0;
        float azimStep = 0.02f; //revisar el valor o calcular perque sigui l'equivalent a aprox. 1pixel en angles alts
        float currentAzimut = azimAngle;
        boolean finished = false;
        while (!finished){
            log.debug("countLeft="+pixcount+" currentAzim="+currentAzimut);
            
            currentAzimut = currentAzimut + azimStep;
            Point2D.Float newPoint = elli.getEllipsePoint(currentAzimut);
            
            int npx = (int)(newPoint.x);//new pixel x
            int npy = (int)(newPoint.y);
            
            if ((npx==previousX)&&(npy==previousY)){
                continue;
            }
            
            int inten = patt2D.getInten(npx,npy);
            
            if (inten >= patt2D.getSaturValue()){
                continue;
            }
            
            if (inten<previousIntensity){
                if (inten<minI){
                    //anem baixant encara
                    minI=inten;
                }else{
                    countdown=countdown+1; //penalitzem
                }
            }else{//intensitat es major a l'anterior
                countdown=countdown+1; //AIXO POTSER HO HAURIA DE DESACTIVAR PEL TEMA DELS MOSAICS
            }
            
            previousIntensity = inten;
            pixcount = pixcount +1;
            maxPixels = maxPixels -1;
            previousX = npx;
            previousY = npy;
            
            if (maxPixels < 0)break;

            if (countdown>=3){
                finished=true;
            }
        }
        halfleft = currentAzimut - azimAngle;
        
        //ara cap a l'exterior:
        previousIntensity = centralPixelIntensity;
        minI = centralPixelIntensity;
        previousX = ipx;
        previousY = ipy;
        pixcount = 1;
        countdown = 0;
        currentAzimut = azimAngle;
        finished = false;
        while (!finished){
            log.debug("countRight="+pixcount+" currentAzim="+currentAzimut);
            
            currentAzimut = currentAzimut - azimStep;
            Point2D.Float newPoint = elli.getEllipsePoint(currentAzimut);
            
            int npx = (int)(newPoint.x);//new pixel x
            int npy = (int)(newPoint.y);
            
            if ((npx==previousX)&&(npy==previousY)){
                continue;
            }
            
            int inten = patt2D.getInten(npx,npy);
            
            if (inten >= patt2D.getSaturValue()){
                continue;
            }
            
            if (inten<previousIntensity){
                if (inten<minI){
                    //anem baixant encara
                    minI=inten;
                }else{
                    countdown=countdown+1; //penalitzem
                }
            }else{//intensitat es major a l'anterior
                countdown=countdown+1; //AIXO POTSER HO HAURIA DE DESACTIVAR PEL TEMA DELS MOSAICS
            }
            
            previousIntensity = inten;
            pixcount = pixcount +1;
            maxPixels = maxPixels -1;
            previousX = npx;
            previousY = npy;
            
            if (maxPixels < 0)break;

            if (countdown>=3){
                finished=true;
            }
        }
        halfright = azimAngle - currentAzimut;
        
        return 2*FastMath.max(halfleft, halfright);
	}
	
    public static int intRadPixelsOfAPeak(Pattern2D patt2D, float px, float py){
        //vector centre-pixel
        float vPCx=px-patt2D.getCentrX();
        float vPCy=patt2D.getCentrY()-py;
        
        //vector unitari
        float modul = (float) FastMath.sqrt(vPCx*vPCx + vPCy*vPCy);
        vPCx = vPCx/modul;
        vPCy = vPCy/modul;
        
        int maxPixels = 30;
        int halfinterna = 0;
        int halfexterna = 0;

        //ara anirem mirant pixel per pixel en el radi la desviacio de la intensitat
        //primer cap a l'interior:
        int centralPixelIntensity = patt2D.getInten((int)(px), (int)(py)); 
        int previousIntensity = centralPixelIntensity;
        int minI = centralPixelIntensity;
        int previousX = (int)(px);
        int previousY = (int)(py);
        int pixcount = 1;
        int passades = 1;
        int countdown = 0;
        boolean finished = false;
        while (!finished){
            log.debug("count="+pixcount+" passades="+passades);
            float fpx = (int)(vPCx * (modul-passades)); //new pixel x float
            float fpy = (int)(vPCy * (modul-passades));
            
            int npx = (int)(patt2D.getCentrX() + fpx);//new pixel x
            int npy = (int)(patt2D.getCentrY() - fpy);
            
            if ((npx==previousX)&&(npy==previousY)){
                passades=passades+1;
                continue;
            }
            
            int inten = patt2D.getInten(npx,npy);
            
            if (inten >= patt2D.getSaturValue()){
                passades=passades+1;
                continue;
            } //els saturats no els considerem
            
            if (inten<previousIntensity){
                if (inten<minI){
                    //anem baixant encara
                    minI=inten;
                }else{
                    countdown=countdown+1; //penalitzem
                }
            }else{//intensitat es major a l'anterior
                countdown=countdown+1;
            }
            
            previousIntensity = inten;
            pixcount = pixcount +1;
            passades = passades +1;
            maxPixels = maxPixels -1;
            previousX = npx;
            previousY = npy;
            
            if (maxPixels < 0)break;

            if (countdown>=3){
                finished=true;
            }
        }
        halfinterna = pixcount;
        
        //ara cap a l'exterior:
        centralPixelIntensity = patt2D.getInten((int)(px), (int)(py)); 
        previousIntensity = centralPixelIntensity;
        minI = centralPixelIntensity;
        previousX = (int)(px);
        previousY = (int)(py);
        pixcount = 1;
        passades = 1;
        countdown = 0;
        finished = false;
        while (!finished){
            float fpx = (int)(vPCx * (modul-passades)); //new pixel x float
            float fpy = (int)(vPCy * (modul-passades));
            
            int npx = (int)(patt2D.getCentrX() + fpx);//new pixel x
            int npy = (int)(patt2D.getCentrY() - fpy);
            
            if ((npx==previousX)&&(npy==previousY)){
                passades=passades+1;
                continue;
            }
            
            int inten = patt2D.getInten(npx,npy);
            
            if (inten >= patt2D.getSaturValue()){
                passades=passades+1;
                continue;
            }

            if (inten<previousIntensity){
                if (inten<minI){
                    //anem baixant encara
                    minI=inten;
                }else{
                    countdown=countdown+1; //penalitzem
                }
            }else{//intensitat es major a l'anterior
                countdown=countdown+1;
            }
            
            previousIntensity = inten;
            pixcount = pixcount +1;
            passades = passades +1;
            maxPixels = maxPixels -1;
            previousX = npx;
            previousY = npy;
            
            if (maxPixels < 0)break;

            if (countdown>=3){
                finished=true;
            }
        }
        halfexterna = pixcount;
        
        return 2*FastMath.max(halfinterna, halfexterna);
    }
    
    //Using the full image
    public static Pattern1D radialIntegration(Pattern2D patt2D,float t2ini, float t2fin, float step, float cakeIn, float cakeOut, boolean corrLP, boolean corrIAng,float subadu){
        return ImgOps.radialIntegration(patt2D, 0, patt2D.getDimY(), 0, patt2D.getDimX(), t2ini, t2fin, step, cakeIn, cakeOut, corrLP, corrIAng, subadu);
    }
    
    //FULL SUBROUTINE
	private static Pattern1D radialIntegration(Pattern2D patt2D, int rowIni, int rowFin, int colIni, int colFin, float t2ini, float t2fin, float step, float cakeIn, float cakeOut, boolean corrLP, boolean corrIAng,float subadu){
	    //comprovacions previes
	    if (step < 0) {step = patt2D.calcMinStepsizeBy2Theta4Directions();}
	    if (cakeIn < 0){cakeIn = 0.0f;}
	    if (cakeOut < 0){cakeOut = 360.f;}
	    boolean fullCake = false;
	    if (cakeIn==0.0 && cakeOut==360.0){
	        fullCake = true;
	    }

	    log.debug("step ="+step+" t2ini="+t2ini+" t2fin="+t2fin);
	    Pattern1D out = new Pattern1D(t2ini,t2fin,step);

	    //ara anirem pixel a pixel i mirarem la 2theta a la qual correspon
	    for (int i = rowIni; i < rowFin; i++) { // per cada fila (Y)
	        for (int j = colIni; j < colFin; j++) { // per cada columna (X)
	            //mask o zero el descartem
	            if(patt2D.isInExZone(j, i))continue;

	            //HEM DE MIRAR SI EL VECTOR ESTA DINTRE EL CAKE
	            if (!fullCake){
	                float azim = patt2D.getAzimAngle(j, i, true);
	                //debug
	                if(j==patt2D.getCentrXI() && i==patt2D.getCentrYI()-100)log.fine("x,y,azim="+j+","+i+","+azim);
	                if(j==patt2D.getCentrXI() && i==patt2D.getCentrYI()+100)log.fine("x,y,azim="+j+","+i+","+azim);
	                if(j==patt2D.getCentrXI()+100 && i==patt2D.getCentrYI())log.fine("x,y,azim="+j+","+i+","+azim);
	                if(j==patt2D.getCentrXI()-100 && i==patt2D.getCentrYI())log.fine("x,y,azim="+j+","+i+","+azim);

	                if (cakeOut<cakeIn){
	                    //va al reves
	                    if ((azim < cakeIn) && (azim > cakeOut))continue; 
	                }else{
	                    if ((azim < cakeIn) || (azim > cakeOut))continue;    
	                }
	            }
	            //2theta del pixel en la imatge
	            double t2p = patt2D.calc2T(new Point2D.Float(j,i), true);

	            if(t2p<t2ini||t2p>t2fin)continue;

	            //position to the vector
	            int p=(int) (t2p/step - t2ini/step);
	            float facIAng = 1;
	            if (corrIAng) facIAng = ImgOps.corrIncidentAngle(patt2D, j, i)[1];
	            float inten = (float) (patt2D.getInten(j,i) * patt2D.getScale() * facIAng + subadu);
	            out.sumPoint(p, FastMath.round(inten), 1);
	        }
	    }

	    //Calculs finals
	    Iterator<PointPatt1D> it = out.getPoints().iterator();
	    while(it.hasNext()){
	        PointPatt1D punt = it.next();
   	        //correction of t2, e.g. 0 it is really 0+(step/2) to be at the center of the bin
	        punt.setT2(punt.getT2()+(out.step/2f)); 
	        if (punt.getNpix()<=0){
	            punt.setIntensity(0);
	            continue;
	        }
	        //lorentz correction
	        if (corrLP){
	            float lorfact = (float) FastMath.pow(FastMath.cos(FastMath.toRadians(punt.getT2())),3);
	            lorfact = 1/lorfact;
	            punt.setIntensity((punt.getCounts()*lorfact)/punt.getNpix());	            
	        }else{
	            punt.setIntensity(punt.getCounts()/punt.getNpix());
	        }
	        //esd
	        float des = (float) FastMath.sqrt(punt.getIntensity()/(float)(punt.getNpix()));
	        punt.setDesv(des);
	    }
	    return out;
	}
	
	//Retorna el promig d'intensitat d'un anell centrat en t2 i considerant una amplada tol2t
	//considera tilt/rot
	public static float radialIntegrationSingle2th(Pattern2D patt2D, float t2,float tol2tdeg,boolean corrLP, boolean corrIAng){
	    if (tol2tdeg < 0) { //default value
	        tol2tdeg = 0.1f;
	    }
	    float step = patt2D.calcMinStepsizeBy2Theta4Directions();
	    float sum = 0; //valor de intensitat suma
	    int npix = 0;

	    if (step>tol2tdeg){return -1;}
	    float t2ini = t2 - tol2tdeg;
	    float t2fin = t2 + tol2tdeg;

	    //ara anirem pixel a pixel i mirarem la 2theta a la qual correspon
	    for (int i = 0; i < patt2D.getDimY(); i++) { // per cada fila (Y)
	        for (int j = 0; j < patt2D.getDimX(); j++) { // per cada columna (X)
	            //mask o zero el descartem
	            if(patt2D.isInExZone(j, i))continue;
	            double t2p = patt2D.calc2T(new Point2D.Float(j,i), true);
	            if(t2p<t2ini||t2p>t2fin)continue;
	            double facLor = 1;
	            double facPol = 1;
	            float facIAng = 1;
	            if (corrLP) {
	                double[] facLP = ImgOps.corrLP(patt2D, j, i, 1, 2, 2, false);
	                facLor = facLP[1];
	                facPol = facLP[2];
	            }
	            if (corrIAng) facIAng = ImgOps.corrIncidentAngle(patt2D, j, i)[1];
	            float inten = (float) (patt2D.getInten(j,i) * patt2D.getScale() * facLor * facPol * facIAng);
	            sum = sum + inten;
	            npix = npix +1;
	        }
	    }
	    //normalitzem pel nombre de pixels
	    sum = sum / npix;
	    return sum;
	}

	//Retorna els promitjos d'intensitat d'anells centrats en diverses t2[] i considerant una amplada tol2t
    //considera tilt/rot. Aixo es per evitar fer moltes passades si es volen varies t2.
    public static float[] radialIntegrationVarious2th(Pattern2D patt2D, float[] t2,float tol2tdeg,boolean corrLP, boolean corrIAng,searchDBWorker sw){
        if (tol2tdeg < 0) { //default value
            tol2tdeg = 0.1f;
        }
        float step = patt2D.calcMinStepsizeBy2Theta4Directions();
        if (step>tol2tdeg){return null;}
        
        float[] sum = new float[t2.length]; //valor de intensitat suma
        int[] npix = new int[t2.length];
        float[] t2ini = new float[t2.length];
        float[] t2fin = new float[t2.length];
        
        //establim 2tini/fin i inicialitzem sum, npix
        for (int i=0;i<t2.length;i++){
            t2ini[i] = t2[i] - tol2tdeg;
            t2fin[i] = t2[i] + tol2tdeg;
            sum[i]=0;
            npix[i]=0;
        }

        //ara anirem pixel a pixel i mirarem la 2theta a la qual correspon
        for (int i = 0; i < patt2D.getDimY(); i++) { // per cada fila (Y)
            for (int j = 0; j < patt2D.getDimX(); j++) { // per cada columna (X)
                //mask o zero el descartem
                if(patt2D.isInExZone(j, i))continue;
                double t2p = patt2D.calc2T(new Point2D.Float(j,i), true);
                
                //mirem si està dins d'algun rang dels buscats
                int npeak = -1;
                for (int k=0; k<t2.length; k++){
                    if(t2p>t2ini[k]&&t2p<t2fin[k]){
                        npeak = k;                    
                    }
                }
                if (npeak<0)continue; //no esta a cap rang
                
                double facLor = 1;
                double facPol = 1;
                float facIAng = 1;
                if (corrLP) {
                    double[] facLP = ImgOps.corrLP(patt2D, j, i, 1, 2, 2, false);
                    facLor = facLP[1];
                    facPol = facLP[2];
                }
                if (corrIAng) facIAng = ImgOps.corrIncidentAngle(patt2D, j, i)[1];
                float inten = (float) (patt2D.getInten(j,i) * patt2D.getScale() * facLor * facPol * facIAng);
                sum[npeak] = sum[npeak] + inten;
                npix[npeak] = npix[npeak] +1;
                
                //en cas que hi hagi un progres
                if (sw!=null){
                    if ((i % 100) == 0){
                        float percent = ((float)i/(float)patt2D.getDimY())*100.f;
                        sw.mySetProgress((int) percent);
                        log.debug(String.valueOf(percent));
                    }                    
                }
            }
        }
        //normalitzem pel nombre de pixels
        for (int i=0;i<t2.length;i++){
            sum[i] = sum[i] / npix[i];
        }
        return sum;
    }
	
    //string is X, Y or Z
    //retorna la matriu de rotacio respecte un eix donat
	public static RealMatrix rotMatrix(double angleRad, String axis){
	    
	    double[][] mat = new double[3][3];
	    
//	    VavaLogger.writeNameNums("CONFIG", true, "anglerad", angleRad);
	    double cos = FastMath.cos(angleRad);
	    double sin = FastMath.sin(angleRad);
	    
	    if (axis.equalsIgnoreCase("X")){
	        mat[0][0] = 1.0;
	        mat[0][1] = 0.0;
	        mat[0][2] = 0.0;
	        mat[1][0] = 0.0;
	        mat[1][1] = cos;
	        mat[1][2] = -sin;
	        mat[2][0] = 0.0;
	        mat[2][1] = sin;
	        mat[2][2] = cos;
	    }
        if (axis.equalsIgnoreCase("Y")){
            mat[0][0] = cos;
            mat[0][1] = 0.0;
            mat[0][2] = sin;
            mat[1][0] = 0.0;
            mat[1][1] = 1.0;
            mat[1][2] = 0.0;
            mat[2][0] = -sin;
            mat[2][1] = 0.0;
            mat[2][2] = cos;
        }
        if (axis.equalsIgnoreCase("Z")){
            mat[0][0] = cos;
            mat[0][1] = -sin;
            mat[0][2] = 0;
            mat[1][0] = sin;
            mat[1][1] = cos;
            mat[1][2] = 0.0;
            mat[2][0] = 0.0;
            mat[2][1] = 0.0;
            mat[2][2] = 1.0;
        }
	    
	    RealMatrix m = new Array2DRowRealMatrix(mat);
	    
	    return m;
	}
	
    //from pixel or twoteta calculate the ellipse considering pattern calibratino
    public static EllipsePars getElliPars(Pattern2D patt2D, Point2D.Float pixel){
        double twothRad = patt2D.calc2T(pixel, false);
        log.fine(String.format("TWO THETA CLICK = %f",FastMath.toDegrees(twothRad)));
        return getElliPars(patt2D,twothRad);
    }
    
    //from pixel or twoteta calculate the ellipse considering pattern calibratino
    public static EllipsePars getElliPars(Pattern2D patt2D, double twothRad){

        double rotRad = FastMath.toRadians(patt2D.getRotDeg());
        double sintth = FastMath.sin(twothRad);
        double tiltrad = FastMath.toRadians(patt2D.getTiltDeg());
        double tanTilt = FastMath.tan(tiltrad);
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

        double ellicentX = patt2D.getCentrX()+zdis * FastMath.sin(rotRad);
        double ellicentY = patt2D.getCentrY()+zdis * FastMath.cos(rotRad); //cal considerar que direccio Y està "invertida"?? (+ cap avall)

        return new EllipsePars(rMaj, rMen, ellicentX, ellicentY,rotRad);
    }
    
    public static class sumImagesFileWorker extends SwingWorker<Integer,Integer> {

        private File[] files;
        private boolean stop;
        Pattern2D pattsum;
        LogJTextArea taOut;

        public sumImagesFileWorker(File[] files, LogJTextArea textAreaOut){//, Pattern2D patt) {
            this.files = files;
            this.stop = false;
            this.taOut = textAreaOut;
        }

        public Pattern2D getpattSum(){
            return pattsum;
        }
        
        @Override
        protected Integer doInBackground() throws Exception {
            
            int totalfiles = files.length;
            
            Pattern2D patt = ImgFileUtils.readPatternFile(files[0],true);
            int dimY = patt.getDimY();
            int dimX = patt.getDimX();
            
            pattsum = new Pattern2D(patt,false); //copiem dades instr de les originals
            pattsum.setB4inten(true);//ens assegurem que treballem amb I4
            Pattern2D dataI4temp = new Pattern2D(dimX,dimY,true);
            //inicialitzem a zero les dades SUMA
            for(int i=0; i<dimY;i++){
                for(int j=0; j<dimX;j++){
                    dataI4temp.setInten(j, i, 0);
                }
            }
            
            int maxVal=0;
            
            //sumem
            for(int k=0;k<files.length;k++){
                //obrim el pattern i sumem
                patt = ImgFileUtils.readPatternFile(files[k],false);
                if (patt == null){
                    if (taOut!=null)taOut.stat("Error reading "+files[k].getName()+" ... skipping");
                    log.warning("could not read "+files[k].getName()+" ... skiping");
                    continue;
                }
                for(int i=0; i<dimY;i++){
                    for(int j=0; j<dimX;j++){
                        //zona exclosa saltem
                        if (patt.isInExZone(j, i))continue;
                        int s = dataI4temp.getInten(j, i) + patt.getInten(j, i);
                        dataI4temp.setInten(j, i, s);
                    }
                }
                float percent = ((float)k/(float)totalfiles)*100.f;
                setProgress((int) percent);
                log.debug(String.valueOf(percent));
                if (taOut!=null)taOut.stat("File added: "+files[k].getName());
                if (stop) break;
            }

            //escala
            for(int i=0; i<dimY;i++){
                for(int j=0; j<dimX;j++){
                    //mirem si superem els limits (per escalar despres)
                    if (dataI4temp.getInten(j, i)>maxVal) maxVal=dataI4temp.getInten(j, i);
                }
            }
            
            //si ens hem passat del maxim calculem el factor d'escala i escalem
            float fscale=1.0f;
            log.debug("maxVal= "+maxVal+"  satur="+pattsum.getSaturValue());
            if(maxVal>pattsum.getSaturValue()){
                fscale = (float)maxVal/(float)(pattsum.getSaturValue()-1); // -1 per assegurar-nos que entra
            }
            
            for(int i=0; i<dimY;i++){
                for(int j=0; j<dimX;j++){
                    pattsum.setInten(j, i, FastMath.round((float)dataI4temp.getInten(j, i)/fscale));
                }
            }
            this.setProgress(100);
            pattsum.setScale(fscale);
            return 0;            

        }
    }
    
    public static class PkIntegrateFileWorker extends
    SwingWorker<Integer, Integer> {

        private File[] flist;
        LogJTextArea taOut;
        float delsig, angDeg;
        boolean autoDS, autoBkgPt, autoTol2T, autoAngD, lorCorr;
        int zoneR,minPix,bkgpt,iosc,tol2tpix;

        // distMD & wavel -1 to take the ones from the original image,
        // exzfile=null for the same.
        public PkIntegrateFileWorker(File[] files, LogJTextArea textAreaOutput,
                float delsig, boolean autoDS, int zoneR, int minPix, int bkgpt, 
                boolean autoBkgPt, int tol2tpix, boolean autoTol2t, float angDeg, 
                boolean autoAngDeg, boolean lorCorr, int iosc) {
            this.flist = files;
            this.delsig=delsig;
            this.autoDS=autoDS;
            this.zoneR=zoneR;
            this.minPix=minPix;
            this.bkgpt=bkgpt;
            this.autoBkgPt=autoBkgPt;
            this.tol2tpix=tol2tpix;
            this.autoTol2T=autoTol2t;
            this.angDeg=angDeg;
            this.autoAngD=autoAngDeg;
            this.taOut = textAreaOutput;
            this.lorCorr=lorCorr;
            this.iosc=iosc;
        }

        @Override
        protected Integer doInBackground() throws Exception {

            int totalfiles = flist.length;

            //ara anem imatge per imatge a guardar-la (mateix nom, diferent extensio, preguntarem si overwrite)
            boolean applyAll=false;
            boolean owrite = false;
            for (int i=0; i<flist.length;i++){

                float percent = ((float)i/(float)totalfiles)*100.f;
                setProgress((int) percent);

                taOut.stat(String.format("Reading image: %s",flist[i].getName()));
                Pattern2D in = ImgFileUtils.readPatternFile(flist[i],false);
                if (in==null){
                    if (this.taOut!=null) taOut.stat("Error reading file "+flist[i].getName()+" ...skipping");
                    log.info("Error reading file "+flist[i].getName()+" ...skipping");
                    continue;
                }
                taOut.stat("   Finding peaks...");
                in.setPkSearchResult(ImgOps.findPeaks(in, delsig, zoneR, autoDS, PKsearch_frame.nzonesFindPeaks, minPix, false));
                Iterator<Peak> itrpks = in.getPkSearchResult().iterator();
                taOut.stat("   Integrating...");
                while (itrpks.hasNext()){
                    Peak pk = (Peak)itrpks.next();
                    Point2D.Float pxc = pk.getPixelCentre();
                    if (autoBkgPt){
                        int npix = ImgOps.ArcNPix(in, (int)(pxc.x), (int)(pxc.y), tol2tpix, angDeg);
                        //agafem un 5% dels pixels com a fons
                        bkgpt = FastMath.round(npix*PKsearch_frame.def_bkgPxAutoPercent);
                        if (bkgpt<PKsearch_frame.def_minbkgPx)bkgpt=PKsearch_frame.def_minbkgPx;
                        log.debug("bkgPT="+bkgpt);
                    }
                    if (autoTol2T){
                        tol2tpix = ImgOps.intRadPixelsOfAPeak(in,pxc.x,pxc.y);
                        //TODO: posem limits?
                    }
                    if (autoAngD){
                        angDeg = ImgOps.azimAngleOfAPeak(in,pxc.x,pxc.y);
                        log.writeNameNums("CONFIG", true, "x,y,angDeg", pxc.x,pxc.y,angDeg);
                        //TODO
                    }
                    Patt2Dzone pz = ImgOps.YarcTilt(in, (int)(pxc.x), (int)(pxc.y), tol2tpix, angDeg, true, bkgpt, false);
                    
                    pk.setZona(pz);
                    in.setIscan(iosc);
                    pk.calculate(lorCorr);
                }
                //Escribim PCS
                File out = FileUtils.canviExtensio(flist[i],"PCS");
                if (out.exists()){
                    if (!applyAll){
                        JCheckBox checkbox = new JCheckBox("Apply to all");
                        String message = "Overwrite "+out.getName()+"?";
                        Object[] params = {message, checkbox};
                        int n = JOptionPane.showConfirmDialog(null, params, "Overwrite existing file", JOptionPane.YES_NO_OPTION);
                        applyAll = checkbox.isSelected();
                        if (n == JOptionPane.YES_OPTION) {
                            owrite = true;
                        }else{
                            owrite = false;
                        }
                    }
                    if (!owrite)continue;
                }
                
                File f = ImgFileUtils.writePCS(in,out,delsig,autoDS,angDeg,autoTol2T,zoneR,minPix,bkgpt,autoBkgPt,autoAngD);
                
                if (f != null) {
                    if (this.taOut!=null) taOut.stat("   "+f.toString()+" written!");
                    log.info(f.toString()+" written!");
                }else{
                    if (this.taOut!=null) taOut.stat("   Error writting "+out.toString());
                    log.warning("Error writting "+out.toString());
                }
            }
            this.setProgress(100);
            return 0;
        }
    }
}
