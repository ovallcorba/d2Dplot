package vava33.d2dplot.auxi;

import java.awt.Toolkit;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Locale;

import javax.swing.JDialog;
import javax.swing.JProgressBar;
import javax.swing.ProgressMonitor;
import javax.swing.SwingWorker;

import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.util.FastMath;

import vava33.d2dplot.D2Dplot_global;
import vava33.d2dplot.MainFrame;
import vava33.d2dplot.auxi.PDDatabase.searchDBWorker;
import vava33.d2dplot.auxi.Pattern1D.PointPatt1D;

import com.vava33.jutils.LogJTextArea;
import com.vava33.jutils.VavaLogger;

/*
 * Operacions sobre imatges (sostracci� fons, correccions, etc...)
 */
public final class ImgOps {
	
	private static int bkgIter;
    private static VavaLogger log = D2Dplot_global.log;

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
			                if(l<=0)newL=dataIn.getMargin();
//	                          !si el quadrat sobresurt de les x (columnes) per la dreta s'agafa la darrera columna
	                        if(l>=dataIn.getDimX())newL=dataIn.getDimX()-dataIn.getMargin()-1;
//	                          !si el quadrat sobresurt de les y (files) per dalt s'agafa la 1a fila
	                        if(k<=0)newK=dataIn.getMargin();
//	                          !si el quadrat sobresurt de les y (files) per baix s'agafa la darrera fila
	                        if(k>=dataIn.getDimY())newK=dataIn.getDimY()-dataIn.getMargin()-1;
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
		float factDesv=2;
		float percent=0.25f;
		float t2p, criteri;
		int pos;
		
		//primer integrem la imatge
//		Pattern1D intrad = intRad(glass, t2ini, t2fin, stepsize, false);
		Pattern1D intrad = radialIntegration(glass, t2ini, t2fin, stepsize, -1,  -1, true, false, false);

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
//   		if(txtOut!=null){
//   			txtOut.addtxt(false, true, "");
//   		}
   		bkgIter=bkgIter+1;
   		Pattern2D dataOut = new Pattern2D(dataIn,false);
   		
//        !primer fem la integracio radial de la iteracio anterior
//        !calcul del 2T maxim (pixel extrem: 0,0 per exemple)
//        !calcul angle 2teta en graus:
   		float t2fin = (float) dataIn.calc2T(0, 0, true);
   		Pattern1D intrad = radialIntegration(dataIn,0.0f, t2fin, stepsize, -1, -1, true, false, false); //realment aqui en el promig hauriem de NO considerar pics

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
   		//PROGRESS BAR:
//   		if(txtOut!=null){
//   			txtOut.addtxt(false, true, "");
//   		}
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
//   		    float angle = (float) (factAngle*FastMath.toRadians(((1-t2p)*1.5))); //TODO:revisar
//   			float amplada = (1/t2p)*factAmplada;
   				float[] fact = dataIn.getFactAngleAmplada(j, i);
   				float obertura = oberturaArc * fact[0];
   				float amplada = ampladaArc * fact[1];
   				Patt2Dzone arc = YarcTilt(dataIn, j, i, amplada, obertura, false, 0, false);
   				
//   	        assignem al pixel la intensitat mitjana (fitxer de fons que despres restarem)
//   	        nomes si la intensitat anterior es superior
   				//TODO: aqui es podria introduir la intensitat del fons calculada a Yarc en comptes
   				//      de Ymean
   				if(dataIn.getInten(j, i)>arc.getYmean()){
   					dataOut.setInten(j, i, (int) arc.getYmean());
   				}else{
   					dataOut.setInten(j, i, dataIn.getInten(j, i));
   				}
   			}
   			
			//PROGRESS BAR:
//			if((progress!=null)&&(i%10==0)){
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
   		
			//DEBUG (comentar els for anteriors): 
//   		int j=300;
//   		int i=300;
//   		dataOut=dataIn;
//   		while(j<2048){
//   			float t2p = dataOut.calc2T(j, i, false);
//   			float angle = (float) (factAngle*FastMath.toRadians(((1-t2p)*1.5)));
//   			float amplada = (1/t2p)*factAmplada;
//   			dataOut.Yarc(j, i, amplada, angle, false, 0, true);
//   			j=j+300;
//   			i=i+300;
//   		}

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
   		//PROGRESS BAR:
//   		if(txtOut!=null){
//   			txtOut.addtxt(false, true, "");
//   		}
    	
    	boolean hor = (fhor == 1) ? true:false;
    	boolean ver = (fver == 1) ? true:false;
    	boolean horver = (fhorver == 1) ? true:false;
    	
//   	bkgIter=bkgIter+1;
   		Pattern2D dataOut = new Pattern2D(dataIn,false);
   		
   		int v0,v1,v2,v3; //valor pixel,valor flip hor, valor flip vert, valor fliphor-flipvert
   		int newj,newi;
//   		boolean outx,outy;
//   		int ysum, npix;
//   		float desv;
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
   				if(i==FastMath.round(dataIn.getCentrX())&&j==FastMath.round(dataIn.getCentrY())){
   					dataOut.setInten(j, i, dataIn.getInten(j, i));
   					continue;
   				}
   				//inicialitzem
//   				outx=false;
//   				outy=false;
   				//calcul de v0,v1,v2,v3
   				float obertura = 1,amplada = 1;
   				if(minarc){
//   					t2p = dataIn.calc2T(j, i, false);
//   					angle = (float) (oberturaArc*FastMath.toRadians(((1-t2p)*1.5))); //TODO: revisar si cal passar a radiants
//   					amplada = (1.f/t2p) * ampladaArc;
   	   				float[] fact = dataIn.getFactAngleAmplada(j, i);
   	   				obertura = oberturaArc * fact[0];
   	   				amplada = ampladaArc * fact[1];
   					zone = YarcTilt(dataIn, j, i, amplada, obertura, true, 0, false);
   					v0 = FastMath.round(zone.getYmean());
//   					txtOut.ln("pix"+j+","+i);
   				}else{
   					v0 = dataIn.calcIntSquare(j, i, aresta, true);
   				}
   				//inicialitzem v1,v2 i v3 igual a v0 en cas que no s'assignin despres perque queden fora
   				v1=v0;
   				v2=v0;
   				v3=v0;
   				newj=FastMath.round(dataIn.getCentrX()+(dataIn.getCentrX()-j));
   				newi=FastMath.round(dataIn.getCentrY()+(dataIn.getCentrY()-i));
   				
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
	public static Pattern2D corrLP(Pattern2D dataIn,int iPol, int iLor, int iosc, boolean debug){
		Pattern2D dataOut = new Pattern2D(dataIn,false);
		Pattern2D dataI4temp = new Pattern2D(dataIn.getDimX(),dataIn.getDimY());
		int maxVal=0;
		int minVal=99999999;
		
		float fact = dataIn.pixSx / (1 + dataIn.wavel * dataIn.distMD);
		if(iosc==-1) iosc = 2;
		
//	    per cada pixel s'ha de calcular l'angle azimutal (entre la normal
//	    (al pla de polaritzacio *i* el vector del centre de la imatge (xc,yc)
//	    al pixel (x,y) en questi�). Tamb� s'ha de calcular l'angle 2T del pixel
		for(int i=0; i<dataIn.getDimY();i++){
			for(int j=0; j<dataIn.getDimX();j++){
				//zona exclosa saltem
				if (dataIn.isInExZone(j, i))continue;
				//el punt central el saltem
				if ((i == dataIn.getCentrY())&&(j == dataIn.getCentrX()))continue;
				
				//debug: tots els punts amb intensitat 12500 (per veure efectes)
				if(debug){
					dataIn.setInten(j, i, 12500);
					dataIn.setMaxI(12500);
				}
				
				float vecCPx = (float)(j-dataIn.getCentrX())*dataIn.pixSx;
				float vecCPy = (float)(dataIn.getCentrY()-i)*dataIn.pixSy;
				float vecCPmod = (float) FastMath.sqrt(vecCPx*vecCPx+vecCPy*vecCPy);
				float t2 = (float) FastMath.atan(vecCPmod/dataIn.getDistMD());

				
				//lorentz
				float rloren = 1.0f;
				if (iLor==1){
				    float xkug = (float) (fact * (j-dataIn.centrX) * FastMath.cos(t2));
				    float ykug = (float) (fact * (i-dataIn.centrY) * FastMath.cos(t2));
				    float zkug = (float) (2.0 / dataIn.wavel * FastMath.pow(FastMath.sin(t2/2.0),2));
				    float dkug = (float) FastMath.sqrt(xkug*xkug+ykug*ykug+zkug*zkug);
				    
				    float phi = 1;
				    if (iosc == 1) phi = (float) FastMath.acos(xkug/dkug);
				    if (iosc == 2) phi = (float) FastMath.acos(ykug/dkug);
				    float arg = (float) (FastMath.pow(FastMath.sin(phi),2) - FastMath.pow(FastMath.sin(t2/2.0),2));
				    rloren = (float) (2 * FastMath.sin(t2/2.0) * FastMath.sqrt(arg));
				}
				if (iLor == 2) {
				    rloren=(float) (1.0/(FastMath.cos(t2/2)*FastMath.pow(FastMath.sin(t2/2),2))); //igual que el DAJUST
				}
				
				//polaritzacio
				float pol = 1.0f;
				if (iPol==1){
		            float xdist2 = vecCPx*vecCPx;
		            float ydist2 = vecCPy*vecCPy;
		            float sepOD2 = dataIn.distMD*dataIn.distMD;
		            if (iosc==1) pol = (sepOD2 + xdist2) / (sepOD2 + xdist2 + ydist2);
		            if (iosc==2) pol = (sepOD2 + ydist2) / (sepOD2 + xdist2 + ydist2);
				}
				
		        if (iPol==2){
		            pol=(float) (0.5 + 0.5 * (FastMath.cos(t2)*FastMath.cos(t2)));
		        }

                dataI4temp.setInten(j, i, FastMath.round((dataIn.getInten(j, i)*rloren)/pol));
                //mirem si superem els limits (per escalar despres)
                if (dataI4temp.getInten(j, i)>maxVal) maxVal=dataI4temp.getInten(j, i);
			}
		}
		
		//si ens hem passat del maxim calculem el factor d'escala i escalem
		float fscale=1.0f;
		if(maxVal>dataIn.getMaxI()){
			fscale = (float)(dataIn.getMaxI()-1)/(float)maxVal; // -1 per assegurar-nos que entra
		}
		for(int i=0; i<dataIn.getDimY();i++){
			for(int j=0; j<dataIn.getDimX();j++){
				//mascara el deixem tal qual
				if(dataIn.isInExZone(j, i)){
					dataOut.setInten(j, i, dataIn.getInten(j, i));
					continue;
				}
				dataOut.setInten(j, i, FastMath.round((float)dataI4temp.getInten(j, i)*fscale));
			}
		}
		
		return dataOut;
	}
	
    //iosc es l'eix de tilt, 1=horitzontal, 2=vertical
	//ilor oscil=1 powder=2, 
	//ipol syn=1 lab=2
	
    //valors ilor ipol
//	ilor=chckbxLorOscil.isSelected()?1:0;
//    ilor=chckbxLorPow.isSelected()?2:ilor;
//    ipol=chckbxPolSyn.isSelected()?1:0;
//    ipol=chckbxPolLab.isSelected()?2:ipol;
	//PER FER LA INTEGRACIO RADIAL ILOR=2, IPOL=1 -- seran els valors per defecte
	//RETURNS A VECTOR: [INTENSITY CORRECTED FOR LP, Lfactor, Pfactor] --- son factors, vol dir que s'han de multiplicar
    public static float[] corrLP(Pattern2D dataIn, int pX, int pY, int iPol, int iLor, int iosc, boolean debug){
        
        float fact = dataIn.pixSx / (1 + dataIn.wavel * dataIn.distMD);
        if(iosc==-1) iosc = 2; //EIX D'OSCILACIO 1=horitzontal, 2=Vertical
        if(iLor==-1) iLor = 2;
        if(iPol==-1) iPol = 1;
        
//      per cada pixel s'ha de calcular l'angle azimutal (entre la normal
//      (al pla de polaritzacio *i* el vector del centre de la imatge (xc,yc)
//      al pixel (x,y) en questi�). Tamb� s'ha de calcular l'angle 2T del pixel
        
        float[] nothingdone = {dataIn.getInten(pX, pY),1,1};
        //zona exclosa saltem
        if (dataIn.isInExZone(pX, pY))return nothingdone;
        //el punt central el saltem
        if ((pY == FastMath.round(dataIn.getCentrY()))&&(pX == FastMath.round(dataIn.getCentrX())))return nothingdone;
        
        //debug: tots els punts amb intensitat 12500 (per veure efectes)
        if(debug){
            dataIn.setInten(pX, pY, 12500);
            dataIn.setMaxI(12500);
        }
        
        float vecCPx = (float)(pX-dataIn.getCentrX())*dataIn.pixSx;
        float vecCPy = (float)(dataIn.getCentrY()-pY)*dataIn.pixSy;
        float vecCPmod = (float) FastMath.sqrt(vecCPx*vecCPx+vecCPy*vecCPy);
        float t2 = (float) FastMath.atan(vecCPmod/dataIn.getDistMD());

        
        //lorentz
        float rloren = 1.0f;
        if (iLor==1){
            float xkug = (float) (fact * (pX-dataIn.centrX) * FastMath.cos(t2));
            float ykug = (float) (fact * (pY-dataIn.centrY) * FastMath.cos(t2));
            float zkug = (float) (2.0 / dataIn.wavel * FastMath.pow(FastMath.sin(t2/2.0),2));
            float dkug = (float) FastMath.sqrt(xkug*xkug+ykug*ykug+zkug*zkug);
            
            float phi = 1;
            if (iosc == 1) phi = (float) FastMath.acos(xkug/dkug);
            if (iosc == 2) phi = (float) FastMath.acos(ykug/dkug);
            float arg = (float) (FastMath.pow(FastMath.sin(phi),2) - FastMath.pow(FastMath.sin(t2/2.0),2));
            rloren = (float) (2 * FastMath.sin(t2/2.0) * FastMath.sqrt(arg));
        }
        if (iLor == 2) {
            rloren=(float) (1.0/(FastMath.cos(t2/2)*FastMath.pow(FastMath.sin(t2/2),2))); //igual que el DAJUST
        }
        
        //polaritzacio
        float pol = 1.0f;
        if (iPol==1){
            float xdist2 = vecCPx*vecCPx;
            float ydist2 = vecCPy*vecCPy;
            float sepOD2 = dataIn.distMD*dataIn.distMD;
            if (iosc==1) pol = (sepOD2 + xdist2) / (sepOD2 + xdist2 + ydist2);
            if (iosc==2) pol = (sepOD2 + ydist2) / (sepOD2 + xdist2 + ydist2);
        }
        
        if (iPol==2){
            pol=(float) (0.5 + 0.5 * (FastMath.cos(t2)*FastMath.cos(t2)));
        }

        float[] result = {(dataIn.getInten(pX, pY)*rloren)/pol,rloren,1/pol};
        return result;
    }
	
    //retorna un array amb {Intensitat corregida, factor aplicat}
    public static float[] corrIncidentAngle(Pattern2D dataIn, int pX, int pY){

        //vector centre-pixel
//        float vecCPx = (float)(pX-dataIn.getCentrX())*dataIn.pixSx;
//        float vecCPy = (float)(dataIn.getCentrY()-pY)*dataIn.pixSy;
//        float vecCPmod = (float) FastMath.sqrt(vecCPx*vecCPx+vecCPy*vecCPy);
//        //vector vertical referenciat al centre sera (0, -1)
//        //eix Y cap amunt es vector (0,-1)
//        float modVer = (float) FastMath.sqrt(1);
//        //angle azimut
//        float azim = (float) FastMath.acos((vecCPx*0+vecCPy*-1)/(vecCPmod*modVer));

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

	public static int getBkgIter() {
		return bkgIter;
	}

	public static void setBkgIter(int bkgIter) {
		ImgOps.bkgIter = bkgIter;
	}

	
    static ProgressMonitor pm = null;
    static sumImagesFileWorker sumwk =  null;
    static Pattern2D pattSum = null;

	
	//tol2theta = quina tolerancia en 2theta volem integrar, es el rang TOTAL (es fara tol2t/2 en cada direccio)
	//angle = angle TOTAL d'obertura de l'arc a integrar en GRAUS
	//consideraElTIlt
	public static Patt2Dzone YarcTilt(Pattern2D patt2D, int px, int py, float tol2t, float angleDeg, boolean self, int bkgpt, boolean debug){
	    
	    //limits tth i azim
	    EllipsePars elli = getElliPars(patt2D, new Point2D.Float(px,py));
	    float azimAngle = patt2D.getAzimAngle(px, py, true); //azimut des del zero
	    float azimMax = azimAngle + angleDeg/2;
	    float azimMin = azimAngle - angleDeg/2;
	    if (azimMax<azimMin){
	        //vol dir que passem pel zero
	        azimMax = azimMax + 360;
	    }
//	    float pixelTolerance = tol2t/(patt2D.getMinStepsize());
	    float pixelTolerance = tol2t/0.02f;
	    ArrayList<Point2D.Float> pixelsArc = elli.getEllipsePoints(azimMin, azimMax, 1);
	    float t2p = (float) patt2D.calc2T(px, py, true);
	    float t2max = (float) FastMath.min(t2p + tol2t/2.,patt2D.getMax2TdegCircle());
	    float t2min = (float) FastMath.max(0.1, t2p - tol2t/2.);
        log.writeNameNumPairs("FINE", true, "t2p,t2max,t2min,pixelTolerance,patt2D.getMinStepsize()",t2p,t2max,t2min,pixelTolerance,patt2D.getMinStepsize());
	    log.writeNameNumPairs("FINE", true, "azimAngle,azimMax,azimMin", azimAngle,azimMax,azimMin);

	    //busquem el maxX, minX, maxY, minY de l'arc
	    float[] xs = new float[pixelsArc.size()];
//	    double[] xsd = new double[pixelsArc.size()];
	    float[] ys = new float[pixelsArc.size()];
	    Iterator<Point2D.Float> itrp = pixelsArc.iterator();
	    int n = 0;
	    while (itrp.hasNext()){
	        Point2D.Float p = itrp.next();
	        xs[n] = p.x;
//	        xsd[n] = p.x;
	        ys[n] = p.y;
	        n = n+1;
	    }
//	    log.writeNameNums("CONFIG", true, "xs", xsd);
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
               
//               log.writeNameNumPairs("CONFIG", true, "t2p,azimAngle", t2p,azimAngle);
               
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
                   //si val zero no considerem (s'hauria de reconsiderar...)
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
	        for (int i=0; i<nbkgpt;i++){
	            bkgsum = bkgsum + minint.get(i);
	        }
	        ybkg = (float)(bkgsum)/(float)(nbkgpt);
	        sumdesv=0;
	        for (int i=0; i<nbkgpt;i++){
	            sumdesv = sumdesv + ((float)(minint.get(i))-ybkg)*((float)(minint.get(i))-ybkg);
	        }
	        ybkgdesv=(float) FastMath.sqrt(sumdesv/(float)(nbkgpt-1));
	    }
	    return new Patt2Dzone(npix, ysum, ymax, ymean, ymeandesv, ybkg, ybkgdesv);
	}
	
	//ATENCIO AQUI ES CALCULA "IN SITU" LA 2THETA DELS PIXELS (repetit de patt2D.calc2T) TODO: VIGILAR SI ES FAN CANVIS, en aquest es pot decidir si fer servir o no tilt.
	//sino sempre es pot fer servir la variant slower, que fa servir Pattern2D
	public static Pattern1D radialIntegration(Pattern2D patt2D,float t2ini, float t2fin, float step, float cakeIn, float cakeOut, boolean useTilt, boolean corrLP, boolean corrIAng){
	    //comprovacions previes
	    if (step < 0) {step = patt2D.getMinStepsize();}
	    if (cakeIn < 0){cakeIn = 0.0f;}
	    if (cakeOut < 0){cakeOut = 360.f;}
	    boolean fullCake = false;
	    if (cakeIn==0.0 && cakeOut==360.0){
	        fullCake = true;
	    }
	    //cas que cakeOut sigui < cakeIn
	    //	      if (cakeOut < cakeIn){
	    //	          cakeOut = cakeOut + 360.f;
	    //	      }

	    log.info("step ="+step+" t2ini="+t2ini+" t2fin="+t2fin);
	    Pattern1D out = new Pattern1D(t2ini,t2fin,step);

	    //	      int pos = 0; //posicio al vector del pattern 1d comença a zero
	    //	      int npoints = FastMath.round((t2fin-t2ini)/step)+1; //+1 perque volem incloure t2ini i t2fin

	    //calculs previs "constants"
	    double tiltRad = 0;
	    double rotRad = 0;
	    if (useTilt){
	        tiltRad = FastMath.toRadians(patt2D.getTiltDeg());
	        rotRad = FastMath.toRadians(patt2D.getRotDeg());
	    }
	    double cosTilt = FastMath.cos(tiltRad);
	    double distMDpix = patt2D.getDistMD()/patt2D.getPixSx();
	    double dist = distMDpix/cosTilt; //in pixels

	    //ara anirem pixel a pixel i mirarem la 2theta a la qual correspon
	    for (int i = 0; i < patt2D.getDimY(); i++) { // per cada fila (Y)
	        for (int j = 0; j < patt2D.getDimX(); j++) { // per cada columna (X)
	            //mask o zero el descartem
	            if(patt2D.isInExZone(j, i))continue;

	            //vector centre-pixel
	            float vPCx=j-patt2D.getCentrX();
	            float vPCy=patt2D.getCentrY()-i;

	            //HEM DE MIRAR SI EL VECTOR ESTA DINTRE EL CAKE
	            if (!fullCake){
	                float azim = patt2D.getAzimAngle(j, i, true);
	                //	                  log.debug("x,y,azim="+j+","+i+","+azim);
	                //debug
	                if(j==patt2D.getCentrXI() && i==patt2D.getCentrYI()-100)log.debug("x,y,azim="+j+","+i+","+azim);
	                if(j==patt2D.getCentrXI() && i==patt2D.getCentrYI()+100)log.debug("x,y,azim="+j+","+i+","+azim);
	                if(j==patt2D.getCentrXI()+100 && i==patt2D.getCentrYI())log.debug("x,y,azim="+j+","+i+","+azim);
	                if(j==patt2D.getCentrXI()-100 && i==patt2D.getCentrYI())log.debug("x,y,azim="+j+","+i+","+azim);

	                if (cakeOut<cakeIn){
	                    //va al reves
	                    if ((azim < cakeIn) && (azim > cakeOut))continue; 
	                }else{
	                    if ((azim < cakeIn) || (azim > cakeOut))continue;    
	                }
	            }

	            //calcul de t2p repetit de Pattern2D.calc2T
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
	            t2p = FastMath.toDegrees(t2p);

	            if(t2p<t2ini||t2p>t2fin)continue;

	            //position to the vector
	            int p=(int) (FastMath.round(t2p/step)-FastMath.round(t2ini/step));
	            float factN=1;
	            float pond=1;
	            float facLor = 1;
	            float facPol = 1;
	            float facIAng = 1;
	            //!!CANVIAR PER PODER APLICAR LES DUES!!
	            if (corrLP) {
	                float[] facLP = ImgOps.corrLP(patt2D, j, i, 1, 2, 2, false);
	                facLor = facLP[1];
	                facPol = facLP[2];
	            }
	            if (corrIAng) facIAng = ImgOps.corrIncidentAngle(patt2D, j, i)[1];
	            float inten = pond * patt2D.getInten(j,i) * patt2D.getScale() * (1.0f/factN) * facLor * facPol * facIAng;
	            out.sumPoint(p, FastMath.round(inten), 1);
	        }
	    }

	    //ara ja hauriem de tenir al vector el diagrama de pols.

	    //tornem a fer una passada per calcular la desviacio estandar sqrt((sum(xi-xmean)^2)/N-1)
	    for (int i = 0; i < patt2D.getDimY(); i++) { // per cada fila (Y)
	        for (int j = 0; j < patt2D.getDimX(); j++) { // per cada columna (X)
	            //mask o zero el descartem
	            if(patt2D.isInExZone(j, i))continue;
	            //vector centre-pixel
	            float vPCx=j-patt2D.getCentrX();
	            float vPCy=patt2D.getCentrY()-i;
	            //mirem la 2T del pixel en la imatge
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
	            t2p = FastMath.toDegrees(t2p);
	            if(t2p<t2ini||t2p>t2fin)continue;
	            //mirem a quina posicio del vector (diagrama pols) ha d'anar
	            int p=(int) (FastMath.round(t2p/step)-FastMath.round(t2ini/step));
	            //i ara acumulem la desv
	            float xmean=(float)(out.getPoint(p).getCounts())/(float)(out.getPoint(p).getNpix());
	            float des = (patt2D.getInten(j, i)*patt2D.getScale()-xmean)*(patt2D.getInten(j, i)*patt2D.getScale()-xmean);
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
	        float des = (float) FastMath.sqrt(punt.getDesv()/(float)(punt.getNpix()-1));
	        punt.setDesv(des);
	    }

	    return out;
	}

	public static Pattern1D radialIntegrationSlower(Pattern2D patt2D,float t2ini, float t2fin, float step, float cakeIn, float cakeOut, boolean corrLP, boolean corrIAng){
	    //comprovacions previes
	    if (step < 0) {step = patt2D.getMinStepsize();}
	    if (cakeIn < 0){cakeIn = 0.0f;}
	    if (cakeOut < 0){cakeOut = 360.f;}
	    boolean fullCake = false;
	    if (cakeIn==0.0 && cakeOut==360.0){
	        fullCake = true;
	    }
	    //cas que cakeOut sigui < cakeIn
	    //      if (cakeOut < cakeIn){
	    //          cakeOut = cakeOut + 360.f;
	    //      }

	    log.info("step ="+step+" t2ini="+t2ini+" t2fin="+t2fin);
	    Pattern1D out = new Pattern1D(t2ini,t2fin,step);

	    //      int pos = 0; //posicio al vector del pattern 1d comença a zero
	    //      int npoints = FastMath.round((t2fin-t2ini)/step)+1; //+1 perque volem incloure t2ini i t2fin


	    //ara anirem pixel a pixel i mirarem la 2theta a la qual correspon
	    for (int i = 0; i < patt2D.getDimY(); i++) { // per cada fila (Y)
	        for (int j = 0; j < patt2D.getDimX(); j++) { // per cada columna (X)
	            //mask o zero el descartem
	            if(patt2D.isInExZone(j, i))continue;

	            //HEM DE MIRAR SI EL VECTOR ESTA DINTRE EL CAKE
	            if (!fullCake){
	                float azim = patt2D.getAzimAngle(j, i, true);
	                //                  log.debug("x,y,azim="+j+","+i+","+azim);
	                //debug
	                if(j==patt2D.getCentrXI() && i==patt2D.getCentrYI()-100)log.debug("x,y,azim="+j+","+i+","+azim);
	                if(j==patt2D.getCentrXI() && i==patt2D.getCentrYI()+100)log.debug("x,y,azim="+j+","+i+","+azim);
	                if(j==patt2D.getCentrXI()+100 && i==patt2D.getCentrYI())log.debug("x,y,azim="+j+","+i+","+azim);
	                if(j==patt2D.getCentrXI()-100 && i==patt2D.getCentrYI())log.debug("x,y,azim="+j+","+i+","+azim);

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

	            //              if ((i==300)||(i==600)||(i==900)||(i==1200)||(i==1500)||(i==1800)){
	            //                  log.debug("t2p="+t2p);    
	            //              }
	            //              if ((j==300)||(j==600)||(j==900)||(j==1200)||(j==1500)||(j==1800)){
	            //                  log.debug("t2p="+t2p);    
	            //              }

	            //position to the vector
	            int p=(int) (FastMath.round(t2p/step)-FastMath.round(t2ini/step));
	            float factN=1;
	            float pond=1;
	            float facLor = 1;
	            float facPol = 1;
	            float facIAng = 1;
	            //!!CANVIAR PER PODER APLICAR LES DUES!!
	            if (corrLP) {
	                float[] facLP = ImgOps.corrLP(patt2D, j, i, 1, 2, 2, false);
	                facLor = facLP[1];
	                facPol = facLP[2];
	            }
	            if (corrIAng) facIAng = ImgOps.corrIncidentAngle(patt2D, j, i)[1];
	            float inten = pond * patt2D.getInten(j,i) * patt2D.getScale() * (1.0f/factN) * facLor * facPol * facIAng;
	            out.sumPoint(p, FastMath.round(inten), 1);
	        }
	    }

	    //ara ja hauriem de tenir al vector el diagrama de pols.

	    //tornem a fer una passada per calcular la desviacio estandar sqrt((sum(xi-xmean)^2)/N-1)
	    for (int i = 0; i < patt2D.getDimY(); i++) { // per cada fila (Y)
	        for (int j = 0; j < patt2D.getDimX(); j++) { // per cada columna (X)
	            //mask o zero el descartem
	            if(patt2D.isInExZone(j, i))continue;
	            double t2p = patt2D.calc2T(new Point2D.Float(j,i), true);
	            if(t2p<t2ini||t2p>t2fin)continue;
	            //mirem a quina posicio del vector (diagrama pols) ha d'anar
	            int p=(int) (FastMath.round(t2p/step)-FastMath.round(t2ini/step));
	            //i ara acumulem la desv
	            float xmean=(float)(out.getPoint(p).getCounts())/(float)(out.getPoint(p).getNpix());
	            float des = (patt2D.getInten(j, i)*patt2D.getScale()-xmean)*(patt2D.getInten(j, i)*patt2D.getScale()-xmean);
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
	        float des = (float) FastMath.sqrt(punt.getDesv()/(float)(punt.getNpix()-1));
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
	    float step = patt2D.getMinStepsize();
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
	            float facLor = 1;
	            float facPol = 1;
	            float facIAng = 1;
	            if (corrLP) {
	                float[] facLP = ImgOps.corrLP(patt2D, j, i, 1, 2, 2, false);
	                facLor = facLP[1];
	                facPol = facLP[2];
	            }
	            if (corrIAng) facIAng = ImgOps.corrIncidentAngle(patt2D, j, i)[1];
	            float inten = patt2D.getInten(j,i) * patt2D.getScale() * facLor * facPol * facIAng;
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
        float step = patt2D.getMinStepsize();
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
                
                float facLor = 1;
                float facPol = 1;
                float facIAng = 1;
                if (corrLP) {
                    float[] facLP = ImgOps.corrLP(patt2D, j, i, 1, 2, 2, false);
                    facLor = facLP[1];
                    facPol = facLP[2];
                }
                if (corrIAng) facIAng = ImgOps.corrIncidentAngle(patt2D, j, i)[1];
                float inten = patt2D.getInten(j,i) * patt2D.getScale() * facLor * facPol * facIAng;
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
        return getElliPars(patt2D,twothRad);
    }
    
    //from pixel or twoteta calculate the ellipse considering pattern calibratino
    public static EllipsePars getElliPars(Pattern2D patt2D, double twothRad){
        //      double phiRad = patt2D.getAzimAngle(FastMath.round(pixel.x), FastMath.round(pixel.y), false);
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

        double ellicentX = patt2D.getCentrX()+zdis * FastMath.sin(phiRad);
        double ellicentY = patt2D.getCentrY()-zdis * FastMath.cos(phiRad);

        return new EllipsePars(rMaj, rMen, ellicentX, ellicentY,phiRad);
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
//            this.pattsum = patt;
        }

        public Pattern2D getpattSum(){
            return pattsum;
        }
        
        @Override
        protected Integer doInBackground() throws Exception {
            
            int totalfiles = files.length -1;
            
            Pattern2D patt = ImgFileUtils.readPatternFile(files[0]);
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
                patt = ImgFileUtils.readPatternFile(files[k]);
                if (patt == null){
                    if (taOut!=null)taOut.stat("Error reading "+files[k].getName()+" ... skipping");
                    log.info("could not read "+files[k].getName()+" ... skiping");
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
                    pattsum.setInten(j, i, FastMath.round((float)dataI4temp.getInten(j, i)*fscale));
                }
            }
            this.setProgress(100);
            pattsum.setScale(fscale);
            return 0;            

        }
    }
    
}
