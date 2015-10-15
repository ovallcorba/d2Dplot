package vava33.plot2d.auxi;

import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Locale;

import javax.swing.JProgressBar;

import com.vava33.jutils.LogJTextArea;

import vava33.plot2d.auxi.VavaLogger;

import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.util.FastMath;

import vava33.plot2d.auxi.Pattern1D.PointPatt1D;

/*
 * Operacions sobre imatges (sostracci� fons, correccions, etc...)
 */
public final class ImgOps {
	
	private static int bkgIter;
	
	public static Pattern2D firstBkgPass(Pattern2D dataIn){
		
		Pattern2D dataOut = new Pattern2D(dataIn);
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
		Pattern2D dataOut = new Pattern2D(dataIn);
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
		Pattern2D dataSub = new Pattern2D(dataIn);
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
		Pattern2D[] dataSub = {new Pattern2D(dataIn),new Pattern2D(dataIn)};
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
		Pattern1D intrad = intRad(glass, t2ini, t2fin, stepsize, false);
		
		//per cada pixel mirarem la intensitat mitjana a l'anell que es troba i si aquesta intensitat es
        //mes gran que ymean+2*desv el considerem pic espuri
		for(int i=0; i<glass.getDimY();i++){
			for(int j=0; j<glass.getDimX();j++){
				//s'haurien d'haver aplicat les zones excloses al vidre
				if(glass.isInExZone(j, i))continue;
				//2Theta pixel imatge per determinar amplada i angle
				t2p = glass.calc2T(j, i, true);
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
   		Pattern2D dataOut = new Pattern2D(dataIn);
   		
//        !primer fem la integracio radial de la iteracio anterior
//        !calcul del 2T maxim (pixel extrem: 0,0 per exemple)
//        !calcul angle 2teta en graus:
   		float t2fin = dataIn.calc2T(0, 0, true);
   		Pattern1D intrad = intRad(dataIn,0.0f, t2fin, stepsize, false); //realment aqui en el promig hauriem de NO considerar pics
   		
   		float t2p,ysum,npix;
   		int pos;
   		//dataOut sera el promig de la integracio radial de dataIn
   		for(int i=0; i<dataOut.getDimY();i++){
   			for(int j=0; j<dataOut.getDimX();j++){
   				if(dataIn.isInExZone(j, i)){
   					dataOut.setInten(j, i, dataIn.getInten(j, i));
   					continue;
   				}
   				t2p = dataOut.calc2T(j, i, true);
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
   		Pattern2D dataOut = new Pattern2D(dataIn);
   		
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
   				Patt2Dzone arc = Yarc2(dataIn, j, i, amplada, obertura, false, 0, false);
   				
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
   		Pattern2D dataOut = new Pattern2D(dataIn);
   		
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
   					zone = Yarc2(dataIn, j, i, amplada, obertura, true, 0, false);
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
   						zone = Yarc2(dataIn, newj, i, amplada, obertura, true, 0, false);
   						if(zone.getNpix()>0)v1=FastMath.round(zone.getYmean());
   					}
   					if(ver){
   						zone = Yarc2(dataIn, j, newi, amplada, obertura, true, 0, false);
   						if(zone.getNpix()>0)v1=FastMath.round(zone.getYmean());
   					}
   					if(horver){
   						zone = Yarc2(dataIn,newj, newi, amplada, obertura, true, 0, false);
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
    
	public static Pattern2D corrLP(Pattern2D dataIn,int iPol, int iLor, int iosc, boolean debug){
		Pattern2D dataOut = new Pattern2D(dataIn);
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
                if (dataI4temp.getIntenB4(j, i)>maxVal) maxVal=dataI4temp.getIntenB4(j, i);
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
				dataOut.setInten(j, i, FastMath.round((float)dataI4temp.getIntenB4(j, i)*fscale));
			}
		}
		
		return dataOut;
	}
	
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
        float tthRad = dataIn.calc2T(pX, pY, false);
        
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
	
	//canvi sep15 treballem amb Ints
	public static Pattern2D sumImages(File[] files){
		
		Pattern2D patt = ImgFileUtils.openPatternFile(files[0]);
		int dimY = patt.getDimY();
		int dimX = patt.getDimX();
		
		Pattern2D pattsum = new Pattern2D(patt); //copiem dades instr de les originals
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
			patt = ImgFileUtils.openPatternFile(files[k]);
			for(int i=0; i<dimY;i++){
				for(int j=0; j<dimX;j++){
					//zona exclosa saltem
					if (patt.isInExZone(j, i))continue;
					int s = dataI4temp.getInten(j, i) + patt.getInten(j, i);
					dataI4temp.setInten(j, i, s);
				}
			}
		}

		//escala
		for(int i=0; i<dimY;i++){
			for(int j=0; j<dimX;j++){
				//mirem si superem els limits (per escalar despres)
				if (dataI4temp.getIntenB4(j, i)>maxVal) maxVal=dataI4temp.getIntenB4(j, i);
			}
		}
		
		//si ens hem passat del maxim calculem el factor d'escala i escalem
		float fscale=1.0f;
		VavaLogger.LOG.info("maxVal= "+maxVal+"  satur="+pattsum.getSaturValue());
		if(maxVal>pattsum.getSaturValue()){
			fscale = (float)maxVal/(float)(pattsum.getSaturValue()-1); // -1 per assegurar-nos que entra
		}
		
		for(int i=0; i<dimY;i++){
			for(int j=0; j<dimX;j++){
				//mascara el deixem tal qual
//				if(dataIn.isInExZone(j, i)){
//					dataOut.setInten(j, i, dataIn.getInten(j, i));
//					continue;
//				}
				pattsum.setInten(j, i, FastMath.round((float)dataI4temp.getInten(j, i)*fscale));
			}
		}
		pattsum.setScale(fscale);
		return pattsum;
	}
	
	
//  !Aquesta subrutina sumar� les intensitats dels p�xels en una zona en forma d'arc al voltant
//  !d'un pixel determinat.
//  ! 1)calculem (utilitzant amplada i angle) la 2Tmin i 2Tmax de l'arc, aix� com els 4 vertexs 
//  !   de l'arc
//  ! 2)trobarem el quadrat minim que cont� l'arc
//  ! 3)per cada pixel del cuadrat:
//  !   -mirarem si est� entre 2Tmin i 2Tmax
//  !   -mirarem si l'angle entre el vector reflexio-centre i el vector pixel-centre es menor a 
//  !    "angle"
//  !   -si es compleixen les dues condicions anteriors en sumarem la intensitat
//  !parametres d'entrada:
//  !  - patt2d: la imatge de treball (CONSIDEREM QUE S'HAN ASSIGNAT EL CENTRE, DISTOD i PIXSIZE)
//  !  - px, py: Coordenades x,y del p�xel
//  !  - amplada: amplada de l'arc (gruix)
//  !  - angle: obertura de l'arc
//  !  - self: si es considera o no el propi pixel
//  !  - bkgpt: num de punts de menor intensitat a considerar per calcular el fons
//     - debug: per "pintar" de -1 l'arc, si no es per comprovacions ha de valer false
//  !parametres de sortida: objecte patt2dzone
//  !  - ysum: intensitat suma de la zona
//  !  - npix: nombre de pixels que han contribuit a la suma
//  !  - ymean: intensitat mitjana de la zona
//  !  - ymeandesv: desviacio estandard de la mitjana (ysum/npix)
//  !  - ymax: intensitat maxima del pixel
//  !  - ybkg: intensitat del fons (mitjana dels 20 punts de menor intensitat)
//  !  - ybkgdesv: desviacio de la intensitat del fons (entre els 20 punts)
  public static Patt2Dzone Yarc(Pattern2D patt2D, int px, int py, float amplada, float angle, boolean self, int bkgpt, boolean debug){
      
//    vars calculs extraccio (quadrat,etc..)
      float RcX,RcY,RcXmax,RcYmax,RcXmin,RcYmin;
      float RcXcw,RcYcw,RcXcwMax,RcYcwMax,RcXcwMin,RcYcwMin;
      float RcXacw,RcYacw,RcXacwMax,RcYacwMax,RcXacwMin,RcYacwMin;
      float vSupX,vSupY,vInfX,vInfY;
      float t2min, t2max,x2,y2,radi,t2p;
      float vPCx,vPCy;
      int bkgsum;
      float pesc, modvPC, modvRc, angleB;
      ArrayList<Integer> intensitats = new ArrayList<Integer>(); //vector on guardarem les intensitats per calcular desv.estd
      float xmean,sumdesv;
      ArrayList<Integer> minint = new ArrayList<Integer>(bkgpt); //vector amb les bkgpt intensitats menors
      
      //debug time
//      long startTime = System.currentTimeMillis();
      
      int npix=0;
      int ysum=0;
      int ymax=0;
      float ymean=0;
      float ybkg=0;
      float ymeandesv=0;
      float ybkgdesv=0;
      for(int i=0;i<bkgpt;i++){
          minint.add(patt2D.getSaturValue());
      }
      float angleRad = (float) FastMath.toRadians(angle);
      VavaLogger.LOG.info("Angle (deg | rad) = "+angle+" | "+angleRad);
      
      //calcul vector centre-pixelReflexio (Rc) amb l'origen al centre de la imatge (per fer rotacions)
      RcX=px-patt2D.getCentrX();
      RcY=patt2D.getCentrY()-py;
      VavaLogger.LOG.info("Rc = "+RcX+" "+RcY);
      //Amplada de l'arc (es podria fer RcX-Amplada*nxmx i RcY-Amp*nymx si la imatge no fos quadrada)
      //l'amplada es en "pixels"
      float modR = (float) FastMath.sqrt(RcX*RcX+RcY*RcY); //modul, per calcular l'unitari i els vectors d'amplada de l'arc
      VavaLogger.LOG.info("modR = "+modR);
      float RcUx = RcX/modR;
      float RcUy = RcY/modR;
      RcXmax=RcUx*(modR+amplada/2);
      RcYmax=RcUy*(modR+amplada/2);
      RcXmin=RcUx*(modR-amplada/2);
      RcYmin=RcUy*(modR-amplada/2);
      VavaLogger.LOG.info("RcMax;RcMin = "+RcXmax+" "+RcYmax+"; "+RcXmin+" "+RcYmin);
      
      //ara fem rotar el vector Rc +angle/2 i -angle/2 per trobar els limits de l'arc, trobem els vectors
      //RcCWMax i Min (clockwise) i RcACWMax i min (anticlockwise) i tindrem els vertexs de l'arc
      float cosAng=(float) FastMath.cos(angleRad/2);
      float sinAng=(float) FastMath.sin(angleRad/2);
      RcXcw=(float) (RcX*cosAng+RcY*sinAng);
      RcYcw=(float) (-RcX*sinAng+RcY*cosAng);
      VavaLogger.LOG.info("RcCW = "+RcXcw+" "+RcYcw);
      //amplada
      float RcUXcw = RcXcw/modR;
      float RcUYcw = RcYcw/modR;
      RcXcwMax=RcUXcw*(modR+amplada/2);
      RcYcwMax=RcUYcw*(modR+amplada/2);
      RcXcwMin=RcUXcw*(modR-amplada/2);
      RcYcwMin=RcUYcw*(modR-amplada/2);
      
      RcXacw=(float) (RcX*cosAng-RcY*sinAng);
      RcYacw=(float) (RcX*sinAng+RcY*cosAng);
      VavaLogger.LOG.info("RcACW = "+RcXacw+" "+RcYacw);
      //amplada
      float RcUXacw = RcXacw/modR;
      float RcUYacw = RcYacw/modR;
      RcXacwMax=RcUXacw*(modR+amplada/2);
      RcYacwMax=RcUYacw*(modR+amplada/2);
      RcXacwMin=RcUXacw*(modR-amplada/2);
      RcYacwMin=RcUYacw*(modR-amplada/2);

      VavaLogger.LOG.info("RcCWmax = "+RcXcwMax+" "+RcYcwMax);
      VavaLogger.LOG.info("RcCWmin = "+RcXcwMin+" "+RcYcwMin);
      VavaLogger.LOG.info("RcACWmax = "+RcXacwMax+" "+RcYacwMax);
      VavaLogger.LOG.info("RcACWmin = "+RcXacwMin+" "+RcYacwMin);
      
      
      //ara ja tenim els vertexs, per definir el quadrat mirem el superior esquerra i l'inferior dret,
      vSupX=ImgOps.findMin(RcXcwMax,RcXcwMin,RcXacwMax,RcXacwMin);
      vSupY=ImgOps.findMax(RcYcwMax,RcYcwMin,RcYacwMax,RcYacwMin);
      vInfX=ImgOps.findMax(RcXcwMax,RcXcwMin,RcXacwMax,RcXacwMin);
      vInfY=ImgOps.findMin(RcYcwMax,RcYcwMin,RcYacwMax,RcYacwMin);
      VavaLogger.LOG.info("Vsup;Vinf(centre00) = "+vSupX+" "+vSupY+"; "+vInfX+" "+vInfY);
      
      //Aquests valors definiran el quadrat m�nim sempre i quant s'hagi creuat algun eix.
      //MIREM si hem creuat algun eix (vertical o horitzontal) per tal d'evitar definir un
      //quadrat insuficient (i en conseq��ncia actualitzem el vertex)
      if(RcXacw<0&&RcXcw>0){
          //estem creuant la vertical per l'hemisferi superior
          //caldr� considerar com a vSupY el vector sobre aquesta vertical (0,y)
          //amb y=(modR+amplada/2)
          vSupY=(modR+amplada/2);
      }
      if(RcXacw>0&&RcXcw<0){
          //estem creuant la vertical per l'hemisferi inferior
          //caldr� considerar com a vInfY el vector sobre aquesta vertical (0,-y)
          //amb y=(modR+amplada/2)
          vInfY=-(modR+amplada/2);
      }
      if(RcYacw<0&&RcYcw>0){
          //estem creuant la horitzontal per l'hemisferi esquerra
          //caldr� considerar com a vSupX el vector sobre aquesta horitzontal (-x,0)
          //amb x=(modR+amplada/2)
          vSupX=-(modR+amplada/2);
      }
      if(RcYacw>0&&RcYcw<0){
          //estem creuant la horitzontal per l'hemisferi dret
          //caldr� considerar com a vInfX el vector sobre aquesta horitzontal (x,0)
          //amb x=(modR+amplada/2)
          vInfX=(modR+amplada/2);
      }
      //si l'angle es mes gran de 180 pot ser que toqui 3 hemisferis, agafem tot el quadrat
      if(angle>180){
          vSupY=(modR+amplada/2);
          vInfY=-(modR+amplada/2);
          vSupX=-(modR+amplada/2);
          vInfX=(modR+amplada/2);
      }
      
      VavaLogger.LOG.info("Vsup;Vinf(centre00corr) = "+vSupX+" "+vSupY+"; "+vInfX+" "+vInfY);
      
      //i li sumem el centre per tenir els pixels en la imatge real
      //(tornem a referenciar l'origen (0,0) al vertex superior? esquerra)
      vSupX=vSupX+patt2D.getCentrX();
      vSupY=patt2D.getCentrY()-vSupY;
      vInfX=vInfX+patt2D.getCentrX();
      vInfY=patt2D.getCentrY()-vInfY;
      VavaLogger.LOG.info("Vsup;Vinf = "+vSupX+" "+vSupY+"; "+vInfX+" "+vInfY);
      
      //calculem els angles t2min i t2max de l'arc (sepMD pixL en micres=> radi micres)
      x2 = (RcXmax*patt2D.getPixSx())*(RcXmax*patt2D.getPixSx());
      y2 = (RcYmax*patt2D.getPixSy())*(RcYmax*patt2D.getPixSy());
      radi = (float) FastMath.sqrt(x2+y2);
      t2max=(float) FastMath.atan(radi/(patt2D.getDistMD()));
      
      x2 = (RcXmin*patt2D.getPixSx())*(RcXmin*patt2D.getPixSx());
      y2 = (RcYmin*patt2D.getPixSy())*(RcYmin*patt2D.getPixSy());
      radi = (float) FastMath.sqrt(x2+y2);
      t2min= (float) FastMath.atan(radi/(patt2D.getDistMD()));

      //debug time
//      long checkpoint1  = System.currentTimeMillis();
//      float check1time = (checkpoint1 - startTime);
      
//      !Ara ja toca la part:
//      !3)per cada pixel del cuadrat:
//      !   -mirarem si est� entre 2Tmin i 2Tmax
//      !   -mirarem si l'angle entre el vector reflexio-centre i el vector pixel-centre es menor a "angle"
//      !   -si es compleixen les dues condicions anteriors en sumarem la intensitat
      int ivSupY = FastMath.round(vSupY);
      int ivInfY = FastMath.round(vInfY);
      int ivSupX = FastMath.round(vSupX);
      int ivInfX = FastMath.round(vInfX);
      for (int j=ivSupY;j<=ivInfY;j++){
          for (int i=ivSupX;i<=ivInfX;i++){
              //si esta fora la imatge o es mascara el saltem
              if(!patt2D.isInside(i, j))continue;
              if(patt2D.isInExZone(i, j))continue;
              //ell mateix?
              if ((i==px)&&(j==py)&&(!self))continue;
              
              //calculem el vector pixel-centre (corregit d'origen, centre 0,0 i necessari pels calculs seguents)
              vPCx=i-patt2D.getCentrX();
              vPCy=patt2D.getCentrY()-j;
                      
              //mirem si esta entre 2tmin i 2tmax, sino saltem (cal calcular t2p)
              x2 = (vPCx*patt2D.getPixSx())*(vPCx*patt2D.getPixSx());
              y2 = (vPCy*patt2D.getPixSy())*(vPCy*patt2D.getPixSy());
              radi = (float) FastMath.sqrt(x2+y2);
              t2p = (float) FastMath.atan(radi/(patt2D.getDistMD()));
              if((t2p<t2min)||(t2p>t2max))continue;
              
              //angle entre vPC i Rc (prod. escalar)
              pesc = vPCx*RcX + vPCy*RcY;
              modvRc = (float) FastMath.sqrt(RcX*RcX+RcY*RcY);
              modvPC = (float) FastMath.sqrt(vPCx*vPCx+vPCy*vPCy);
              if((pesc/(modvRc*modvPC))>1){
                angleB=(float) FastMath.acos(1.0);
              }else{
                angleB=(float) FastMath.acos(pesc/(modvRc*modvPC));
              }
              //si angleB es major a angle -> saltem
//              if(angleB>angle)continue;  OLD
              if(FastMath.toDegrees(angleB)>(angle/2))continue;
              
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
          //systemout? per debug
      }
      
//      long endTime  = System.currentTimeMillis();
//      float totalTime = (endTime - startTime);
//      
//      VavaLogger.LOG.info("Yarc took "+totalTime+"ms "+"(checkpoint at "+check1time+"ms, npix= "+npix+", zona="+ivSupX+","+ivSupY+";"+ivInfX+","+ivInfY+")");
//      
      return new Patt2Dzone(npix, ysum, ymax, ymean, ymeandesv, ybkg, ybkgdesv);
  }
	
  
  //fix yarc, que perdia pixels de fora el quadrat si l'arc era ample o estava a l'eix vertical/horitzontal
  //es pot fer manual pero es una matada:
//  //ara hem de mirar si hem creuat algun eix (vertical o horitzontal) per tal d'evitar definir un
//  //quadrat insuficient
//  if(RcXacw<0&&RcXcw>0){
//    //estem creuant la vertical per l'hemisferi superior
//    //caldr� considerar com a vSupY el vector sobre aquesta vertical (0,y)
//    
//    
//  }
//  if(RcXacw>0&&RcXcw<0){
//    //estem creuant la vertical per l'hemisferi inferior
//    //caldr� considerar com a vInfY el vector sobre aquesta vertical (0,-y)
//  }
//  
  
  private static Ellipse2D.Float getEllipseFromCenter(float x, float y, float width, float height)
  {
      float newX = x - width / 2.0f;
      float newY = y - height / 2.0f;

      Ellipse2D.Float ellipse = new Ellipse2D.Float(newX, newY, width, height);

      return ellipse;
  }
  
  //ho provare de fer amb ellipse2D de java
  public static Patt2Dzone Yarc2(Pattern2D patt2D, int px, int py, float amplada, float angle, boolean self, int bkgpt, boolean debug){
      
      //debug time
//      long startTime = System.currentTimeMillis();
      
//    vars calculs extraccio (quadrat,etc..)
      float RcX,RcY,RcXmax,RcYmax,RcXmin,RcYmin;
      float vSupX,vSupY,vInfX,vInfY;
      float t2min, t2max,x2,y2,radi,t2p;
      float vPCx,vPCy;
      int bkgsum;
      float pesc, modvPC, modvRc, angleB;
      ArrayList<Integer> intensitats = new ArrayList<Integer>(); //vector on guardarem les intensitats per calcular desv.estd
      float sumdesv;
      ArrayList<Integer> minint = new ArrayList<Integer>(bkgpt); //vector amb les bkgpt intensitats menors
      
      int npix=0;
      int ysum=0;
      int ymax=0;
      float ymean=0;
      float ybkg=0;
      float ymeandesv=0;
      float ybkgdesv=0;
      for(int i=0;i<bkgpt;i++){
          minint.add(patt2D.getSaturValue());
      }
      
      //calcul vector centre-pixelReflexio (Rc) amb l'origen al centre de la imatge (per fer rotacions)
      RcX=px-patt2D.getCentrX();
      RcY=patt2D.getCentrY()-py;
//      VavaLogger.LOG.info("Rc = "+RcX+" "+RcY);
      float modR = (float) FastMath.sqrt(RcX*RcX+RcY*RcY); //modul, per calcular l'unitari i els vectors d'amplada de l'arc
      float RcUx = RcX/modR;
      float RcUy = RcY/modR;
      RcXmax=RcUx*(modR+amplada/2);
      RcYmax=RcUy*(modR+amplada/2);
      RcXmin=RcUx*(modR-amplada/2);
      RcYmin=RcUy*(modR-amplada/2);
      
      //Amplada de l'arc 
      float radiExt = (float) FastMath.sqrt(RcXmax*RcXmax+RcYmax*RcYmax);
      float radiInt = (float) FastMath.sqrt(RcXmin*RcXmin+RcYmin*RcYmin);
//      VavaLogger.LOG.info("radiExt;radiInt = "+radiExt+" "+radiInt);

      //ara podem calcular les ellipses exterior i interior (considerem CERCLES)
      Ellipse2D.Float elext = getEllipseFromCenter(patt2D.getCentrX(), patt2D.getCentrY(), radiExt*2, radiExt*2);
      Ellipse2D.Float elint = getEllipseFromCenter(patt2D.getCentrX(), patt2D.getCentrY(), radiInt*2, radiInt*2);
//      Ellipse2D.Float elext = this.getEllipseFromCenter(0, 0, radiExt*2, radiExt*2);
//      Ellipse2D.Float elint = this.getEllipseFromCenter(0, 0, radiInt*2, radiInt*2);
      
//      //ara fem rotar el vector Rc - angle/2 per trobar el vector per calcular l'angle on ha de comen�ar l'arc
//      RcXacw=(float) (RcX*FastMath.cos(angle)-RcY*FastMath.sin(angle));
//      RcYacw=(float) (RcX*FastMath.sin(angle)+RcY*FastMath.cos(angle));
//      //VavaLogger.LOG.info("RcACW = "+RcXacw+" "+RcYacw);

      //calculem l'angle entre el vector (1,0) i Rc (ARC2D agafa angle 0 a l'eix X positiu i dibuixa ACW.)
      //prod escalar
      pesc = RcX * 1 + RcY * 0;
      float arcAngle = (float) FastMath.toDegrees(FastMath.acos(pesc/(modR)));
      //hem de determinar l'hemisferi
      if(RcY<0){
          arcAngle = 360 - arcAngle;  
      }
//      VavaLogger.LOG.info("pesc;modR;arcAngle = "+pesc+"; "+modR+"; "+arcAngle);
      
//      VavaLogger.LOG.info("elext bounds = "+elext.getBounds2D().toString());
      
      //ara construirem l'arc desitjat i en mirarem els limits del quadrat delimitador
//      Arc2D.Float arcExt = new Arc2D.Float(elext.getBounds2D(), arcAngle, (float) FastMath.toDegrees(angle), Arc2D.OPEN);
      Arc2D.Float arcExt = new Arc2D.Float(elext.getBounds2D(), arcAngle-(angle/2), angle, Arc2D.OPEN);
      Arc2D.Float arcInt = new Arc2D.Float(elint.getBounds2D(), arcAngle-(angle/2), angle, Arc2D.OPEN);
      Rectangle2D.Float rextern = (java.awt.geom.Rectangle2D.Float) arcExt.getBounds2D();
      Rectangle2D.Float rintern = (java.awt.geom.Rectangle2D.Float) arcInt.getBounds2D();
      
//      VavaLogger.LOG.info("arcext bounds = "+arcExt.getBounds2D().toString());
//      VavaLogger.LOG.info("angle = "+angle);
      
//      vSupX = (float) FastMath.min(rextern.getMinX(), rintern.getMinX());
//      vSupY = (float) FastMath.max(rextern.getMaxY(), rintern.getMaxY());
//      vInfX = (float) FastMath.max(rextern.getMaxX(), rintern.getMaxX());
//      vInfY = (float) FastMath.min(rextern.getMinY(), rintern.getMinY());
      //COORD IMATGE
      vSupX = (float) FastMath.min(rextern.getMinX(), rintern.getMinX());
      vSupY = (float) FastMath.min(rextern.getMinY(), rintern.getMinY());
      vInfX = (float) FastMath.max(rextern.getMaxX(), rintern.getMaxX());
      vInfY = (float) FastMath.max(rextern.getMaxY(), rintern.getMaxY());
//      VavaLogger.LOG.info("Vsup;Vinf = "+vSupX+" "+vSupY+"; "+vInfX+" "+vInfY);

//     
//      vSupX=ImgOps.findMin(RcXcwMax,RcXcwMin,RcXacwMax,RcXacwMin);
//      vSupY=ImgOps.findMax(RcYcwMax,RcYcwMin,RcYacwMax,RcYacwMin);
//      vInfX=ImgOps.findMax(RcXcwMax,RcXcwMin,RcXacwMax,RcXacwMin);
//      vInfY=ImgOps.findMin(RcYcwMax,RcYcwMin,RcYacwMax,RcYacwMin);

      //tornem a referenciar l'origen (0,0) al vertex superior? esquerra
//      vSupX=vSupX+this.getCentrX();
//      vSupY=this.getCentrY()-vSupY;
//      vInfX=vInfX+this.getCentrX();
//      vInfY=this.getCentrY()-vInfY;
      //NO CAL, HEM TREBALLAT AMB ELLIPSES AMB COORD D'IMATGE
      
      //calculem els angles t2min i t2max de l'arc (sepMD pixL en micres=> radi micres)
      x2 = (RcXmax*patt2D.getPixSx())*(RcXmax*patt2D.getPixSx());
      y2 = (RcYmax*patt2D.getPixSy())*(RcYmax*patt2D.getPixSy());
      radi = (float) FastMath.sqrt(x2+y2);
      t2max=(float) FastMath.atan(radi/(patt2D.getDistMD()));
      
      x2 = (RcXmin*patt2D.getPixSx())*(RcXmin*patt2D.getPixSx());
      y2 = (RcYmin*patt2D.getPixSy())*(RcYmin*patt2D.getPixSy());
      radi = (float) FastMath.sqrt(x2+y2);
      t2min= (float) FastMath.atan(radi/(patt2D.getDistMD()));

//      !Ara ja toca la part:
//      !3)per cada pixel del cuadrat:
//      !   -mirarem si est� entre 2Tmin i 2Tmax
//      !   -mirarem si l'angle entre el vector reflexio-centre i el vector pixel-centre es menor a "angle"
//      !   -si es compleixen les dues condicions anteriors en sumarem la intensitat
      int ivSupY = FastMath.round(vSupY);
      int ivInfY = FastMath.round(vInfY);
      int ivSupX = FastMath.round(vSupX);
      int ivInfX = FastMath.round(vInfX);
      
      //debug
//    long checkpoint1  = System.currentTimeMillis();
//    float check1time = (checkpoint1 - startTime);
      
      for (int j=ivSupY;j<=ivInfY;j++){
          for (int i=ivSupX;i<=ivInfX;i++){
              //si esta fora la imatge o es mascara el saltem
              if(!patt2D.isInside(i, j))continue;
              if(patt2D.isInExZone(i, j))continue;
              //ell mateix?
              if ((i==px)&&(j==py)&&(!self))continue;
              
              //calculem el vector pixel-centre (corregit d'origen, centre 0,0 i necessari pels calculs seguents)
              vPCx=i-patt2D.getCentrX();
              vPCy=patt2D.getCentrY()-j;
                      
              //mirem si esta entre 2tmin i 2tmax, sino saltem (cal calcular t2p)
              x2 = (vPCx*patt2D.getPixSx())*(vPCx*patt2D.getPixSx());
              y2 = (vPCy*patt2D.getPixSy())*(vPCy*patt2D.getPixSy());
              radi = (float) FastMath.sqrt(x2+y2);
              t2p = (float) FastMath.atan(radi/(patt2D.getDistMD()));
              if((t2p<t2min)||(t2p>t2max))continue;
              
              //angle entre vPC i Rc (prod. escalar)
              pesc = vPCx*RcX + vPCy*RcY;
              modvRc = (float) FastMath.sqrt(RcX*RcX+RcY*RcY);
              modvPC = (float) FastMath.sqrt(vPCx*vPCx+vPCy*vPCy);
              if((pesc/(modvRc*modvPC))>1){
                angleB=(float) FastMath.acos(1.0);
              }else{
                angleB=(float) FastMath.acos(pesc/(modvRc*modvPC));
              }
              //si angleB es major a angle -> saltem
              if(FastMath.toDegrees(angleB)>(angle/2))continue;
              
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
          //systemout? per debug
      }

//    long endTime   = System.currentTimeMillis();
//    float totalTime = (endTime - startTime);
//    
//    VavaLogger.LOG.info("Yarc took "+totalTime+"ms "+"(checkpoint at "+check1time+"ms, npix= "+npix+", zona="+ivSupX+","+ivSupY+";"+ivInfX+","+ivInfY+")");

      return new Patt2Dzone(npix, ysum, ymax, ymean, ymeandesv, ybkg, ybkgdesv);
          
  }
  
  
//  !Aquesta subrutina fa la integraci� radial de la imatge
//  !parametres d'entrada:
//  !  - patt2d: la imatge de treball (CONSIDEREM QUE S'HAN ASSIGNAT EL CENTRE, DISTOD i PIXSIZE)
//  !  - t2ini, t2fin
//  !  - stepsize: pas del "diagrama" a generar == gruix dels anells
//  !parametres de sortida:
//  !  - ysum: vector d'intensitats
//  !  - npix: vector de numero de pixels que han contribuit a cada punt
//  !  - desv: vector de desviacions estandard de ysum en cada punt
  public static Pattern1D intRad(Pattern2D patt2D, float t2ini, float t2fin, float stepsize, boolean factorN){
      Pattern1D out = new Pattern1D(t2ini,t2fin,stepsize);
      float xmean,sumdesv,vPCx,vPCy,x2,y2,radi,t2p;
      int npoints,p;
      float factN=1.0f;
      
      npoints = FastMath.round((t2fin-t2ini)/stepsize)+1; //+1 perque volem incloure t2ini i t2fin
      
      for (int i = 0; i < patt2D.getDimY(); i++) { // per cada fila (Y)
          for (int j = 0; j < patt2D.getDimX(); j++) { // per cada columna (X)
              //mask o zero el descartem
              if(patt2D.isInExZone(j, i))continue;
              //mirem la 2T del pixel en la imatge per determinar amplada i angle
              //1)vector centre pixel:
              vPCx=j-patt2D.getCentrX();
              vPCy=patt2D.getCentrY()-i;
              //2)calcul angle 2teta en graus
              x2 = (vPCx*patt2D.getPixSx())*(vPCx*patt2D.getPixSx());
              y2 = (vPCy*patt2D.getPixSy())*(vPCy*patt2D.getPixSy());
              radi = (float) FastMath.sqrt(x2+y2);
              t2p=(float) FastMath.toDegrees(FastMath.atan(radi/(patt2D.getDistMD())));
              if(t2p<t2ini||t2p>t2fin)continue;
              //mirem a quina posicio del vector (diagrama pols) ha d'anar
              p=FastMath.round(t2p/stepsize)-FastMath.round(t2ini/stepsize);
              if(factorN){
                  factN= (float) (((2*FastMath.PI*(patt2D.getDistMD()))/(patt2D.getPixSx()*patt2D.getPixSy()))  * FastMath.tan(FastMath.toRadians(t2p)));                   
              }
              //debug
//            VavaLogger.LOG.info(FastMath.round(patt2D.getIntenB2(j,i)*patt2D.getScale()*(1.0f/factN)));
              //i sumem
              out.sumPoint(p, FastMath.round(patt2D.getInten(j,i)*patt2D.getScale()*(1.0f/factN)), 1);
          }
      }
      
      //ara ja hauriem de tenir al vector el diagrama de pols.
      
      //tornem a fer una passada per calcular la desviacio estandar sqrt((sum(xi-xmean)^2)/N-1)
      for (int i = 0; i < patt2D.getDimY(); i++) { // per cada fila (Y)
          for (int j = 0; j < patt2D.getDimX(); j++) { // per cada columna (X)
              //mask o zero el descartem
              if(patt2D.isInExZone(j, i))continue;
              //mirem la 2T del pixel en la imatge per determinar amplada i angle
              //1)vector centre pixel:
              vPCx=j-patt2D.getCentrX();
              vPCy=patt2D.getCentrY()-i;
              //2)calcul angle 2teta en graus
              x2 = (vPCx*patt2D.getPixSx())*(vPCx*patt2D.getPixSx());
              y2 = (vPCy*patt2D.getPixSy())*(vPCy*patt2D.getPixSy());
              radi = (float) FastMath.sqrt(x2+y2);
              t2p=(float) FastMath.toDegrees(FastMath.atan(radi/(patt2D.getDistMD())));
              if(t2p<t2ini||t2p>t2fin)continue;
              //mirem a quina posicio del vector (diagrama pols) ha d'anar
              p=FastMath.round(t2p/stepsize)-FastMath.round(t2ini/stepsize);
              //i ara acumulem la desv
              xmean=(float)(out.getPoint(p).getCounts())/(float)(out.getPoint(p).getNpix());
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
  
  //Aquest pondero segons lo lluny que estigui del centre 2Theta
  public static Pattern1D intRadPond(Pattern2D patt2D, float t2ini, float t2fin, float stepsize, boolean factorN){
      Pattern1D out = new Pattern1D(t2ini,t2fin,stepsize);
      float xmean,sumdesv,vPCx,vPCy,x2,y2,radi,t2p;
      int npoints,p;
      float factN=1.0f;
      
      npoints = FastMath.round((t2fin-t2ini)/stepsize)+1; //+1 perque volem incloure t2ini i t2fin
      
      for (int i = 0; i < patt2D.getDimY(); i++) { // per cada fila (Y)
          for (int j = 0; j < patt2D.getDimX(); j++) { // per cada columna (X)
              //mask o zero el descartem
              if(patt2D.isInExZone(j, i))continue;
              //mirem la 2T del pixel en la imatge per determinar amplada i angle
              //1)vector centre pixel:
              vPCx=j-patt2D.getCentrX();
              vPCy=patt2D.getCentrY()-i;
              //2)calcul angle 2teta en graus
              x2 = (vPCx*patt2D.getPixSx())*(vPCx*patt2D.getPixSx());
              y2 = (vPCy*patt2D.getPixSy())*(vPCy*patt2D.getPixSy());
              radi = (float) FastMath.sqrt(x2+y2);
              t2p=(float) FastMath.toDegrees(FastMath.atan(radi/(patt2D.getDistMD())));
              if(t2p<t2ini||t2p>t2fin)continue;
              //mirem a quina posicio del vector (diagrama pols) ha d'anar
              p=FastMath.round(t2p/stepsize)-FastMath.round(t2ini/stepsize);
              //ponderacio segons lo allunyat que estigui del centre de la posició
              float diff = FastMath.abs(((t2p/stepsize)-(t2ini/stepsize))-(FastMath.round(t2p/stepsize)-FastMath.round(t2ini/stepsize)));
//              VavaLogger.LOG.info("diff= "+diff);
              //dif valdrà entre 0 i 0.5, zero es el mes proper a la posició ideal per tant fem un factor tal que:
              float pond = 1.f - diff;
//              VavaLogger.LOG.info("pond= "+pond);
              if(factorN){
                  factN= (float) (((2*FastMath.PI*(patt2D.getDistMD()))/(patt2D.getPixSx()*patt2D.getPixSy()))  * FastMath.tan(FastMath.toRadians(t2p)));                   
              }
              //debug
//            VavaLogger.LOG.info(FastMath.round(patt2D.getIntenB2(j,i)*patt2D.getScale()*(1.0f/factN)));
              //i sumem
              out.sumPoint(p, FastMath.round(pond*patt2D.getInten(j,i)*patt2D.getScale()*(1.0f/factN)), 1);
          }
      }
      
      //ara ja hauriem de tenir al vector el diagrama de pols.
      
      //tornem a fer una passada per calcular la desviacio estandar sqrt((sum(xi-xmean)^2)/N-1)
      for (int i = 0; i < patt2D.getDimY(); i++) { // per cada fila (Y)
          for (int j = 0; j < patt2D.getDimX(); j++) { // per cada columna (X)
              //mask o zero el descartem
              if(patt2D.isInExZone(j, i))continue;
              //mirem la 2T del pixel en la imatge per determinar amplada i angle
              //1)vector centre pixel:
              vPCx=j-patt2D.getCentrX();
              vPCy=patt2D.getCentrY()-i;
              //2)calcul angle 2teta en graus
              x2 = (vPCx*patt2D.getPixSx())*(vPCx*patt2D.getPixSx());
              y2 = (vPCy*patt2D.getPixSy())*(vPCy*patt2D.getPixSy());
              radi = (float) FastMath.sqrt(x2+y2);
              t2p=(float) FastMath.toDegrees(FastMath.atan(radi/(patt2D.getDistMD())));
              if(t2p<t2ini||t2p>t2fin)continue;
              //mirem a quina posicio del vector (diagrama pols) ha d'anar
              p=FastMath.round(t2p/stepsize)-FastMath.round(t2ini/stepsize);
              //i ara acumulem la desv
              xmean=(float)(out.getPoint(p).getCounts())/(float)(out.getPoint(p).getNpix());
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
  
  //TODO: Aquest el farem anar ampliant un cercle i mirant el pixel m�s proper
//  public Pattern1D intRad2(float t2ini, float t2fin, float stepsize, boolean factorN){
//    
//  }
  
  
  //Aquest pondero segons lo lluny que estigui del centre 2Theta
  public static Pattern1D intRadCircles(Pattern2D patt2D, float t2ini, float t2fin, float step, float cakeIn, float cakeOut){
      
//      float step = (float) FastMath.toDegrees(FastMath.atan(this.getPixSx()/this.getDistMD()));
      //todo=hauriem d'agafar el pitjor dels casos d'step (el mes allunyat) per l'allocate 
      if (step < 0) {
          step = (float) FastMath.toDegrees(FastMath.atan(patt2D.getPixSx()/patt2D.getDistMD()));
      }
      
      if (cakeIn < 0){
          cakeIn = 0.0f;
      }
      if (cakeOut < 0){
          cakeOut = 360.f;
      }
      
//      step = (float)Math.round(step * 1000) / 1000.f;
//      step = 0.0214999f;
      
      VavaLogger.LOG.info("step ="+step+" t2ini="+t2ini+" t2fin="+t2fin);
      Pattern1D out = new Pattern1D(t2ini,t2fin,step);
      
      //step i t2 per pixel es el mateix
      
      //vector vertical considerant centre 0,0 seran (0,n*step)
      float ypx = 0;
      float xpx = 0;
      float t2ang = t2ini;
       //posicio al vector del pattern 1d comença a zero
      int pos = 0;

      float psx2 = patt2D.getPixSx()*patt2D.getPixSx();
      float psy2 = patt2D.getPixSy()*patt2D.getPixSy();
      float halfpixdiag = (float) FastMath.sqrt(psx2/2+psx2/2); //mm
      VavaLogger.LOG.info("halfpixdiag="+halfpixdiag);
      
      while (t2ang < t2fin){
//          float ycomp = patt2D.getCentrY() - (npunt*step + t2punt);
          t2ang = pos*step + t2ini; //angle que estem
          //correspondencia en pixels?
          ypx = (float) ((patt2D.getDistMD() * FastMath.tan(FastMath.toRadians(t2ang))) / patt2D.getPixSx());
///            ypx = (pos*step + t2ini)/step; //correspondencia en pixels -- no funciona per la ortogonalitat del det
          xpx = 0;
          
//          VavaLogger.LOG.info("pos="+pos+" ypx="+ypx+" xpx="+xpx+" t2ang="+t2ang);
          //vector vertical (0,ycomp)
          
          float ang = cakeIn;
          //TODO: incrementar l'increment de rotacio amb l'angle
          //float dang = (float) FastMath.toRadians(0.03f); //delta Angle azimut
          //dang sera el valor just per anar al pixel del costat, que segons la 2theta que estiguem es diferen
          //farem que sigui la meitat de la diagonal d'un pixel (calculat fora el while)
          float dang = (float) FastMath.atan(halfpixdiag/(ypx * patt2D.getPixSy())); //mm
          VavaLogger.LOG.info("pos="+pos+" ypx="+ypx+" xpx="+xpx+" t2ang="+t2ang+" dang="+dang);

          //pixel corresponent a l'angle inicial (important en cas que sigui diferent de zero)
          xpx = (float) (xpx * FastMath.cos(dang) - ypx * FastMath.sin(dang));
          ypx = (float) (xpx * FastMath.sin(dang) + ypx * FastMath.cos(dang));
          
          while (ang < cakeOut){
              //VavaLogger.LOG.info("rotangle="+ang+" ypx="+ypx+" xpx="+xpx);

              //sumem intensitat al pattern del pixel mes proper i girem
              float y_row = patt2D.getCentrY() - ypx;
              float x_col = xpx + patt2D.getCentrX();
              
              int inten = patt2D.getInten(FastMath.round(x_col),FastMath.round(y_row));
              out.sumPoint(pos, inten, 1);
              
              //apliquem rotacio DANG a xcomp, ycomp
              xpx = (float) (xpx * FastMath.cos(dang) - ypx * FastMath.sin(dang));
              ypx = (float) (xpx * FastMath.sin(dang) + ypx * FastMath.cos(dang));

              ang = (float) (ang + FastMath.toDegrees(dang));
          }
          pos = pos + 1;
      }
      
      //Calcul desviacio (simple)
      Iterator<PointPatt1D> it = out.getPoints().iterator();
      while(it.hasNext()){
          PointPatt1D punt = it.next();
          if (punt.getNpix()<2){
              punt.setDesv(0);
              continue;
          }
          float des = (float) FastMath.sqrt(punt.getCounts());
          punt.setDesv(des);
      }
      
      return out;
      
  }
  
  public static Pattern1D intRadEllipse(Pattern2D patt2D, float t2ini, float t2fin, float step, float rmin, float rmax, float angrot, float cakeIn, float cakeOut){
      return ImgOps.intRadEllipse(patt2D, t2ini, t2fin, step, rmin, rmax, angrot, cakeIn, cakeOut, 0.006734814466938165f, -0.003927179165583287f);
  }
  
  //Considerant una ellipse en comptes d'un cercle
  public static Pattern1D intRadEllipse(Pattern2D patt2D, float t2ini, float t2fin, float step, float rmin, float rmax, float angrot, float cakeIn, float cakeOut,float vecX, float vecY){

    //comprovacions previes
    if (step < 0) {step = patt2D.getMinStepsize();}
    if (cakeIn < 0){cakeIn = 0.0f;}
    if (cakeOut < 0){cakeOut = 360.f;}
    
    VavaLogger.LOG.info("step ="+step+" t2ini="+t2ini+" t2fin="+t2fin);
    Pattern1D out = new Pattern1D(t2ini,t2fin,step);
    
    int pos = 0; //posicio al vector del pattern 1d comença a zero

    float psx2 = patt2D.getPixSx()*patt2D.getPixSx();
    float psy2 = patt2D.getPixSy()*patt2D.getPixSy();
    float halfpixdiag = (float) FastMath.sqrt(psx2/2+psy2/2); //mm
    VavaLogger.LOG.info("halfpixdiag="+halfpixdiag);
    
    //posicionem l'angle inicial d'integracio a l'eix Y cap amunt (prenem com a zero angle i integrarem clockwise)
    //es el conveni que agafo, cal corregir el de l'ellipse
    float zeroPos = (float) (-angrot -(Math.PI/2));
    //float drawFin = (float) (drawIni + FastMath.toRadians(90));
    
    float t2ang = t2ini;

    //mirem relacio entre rmax i rmin amb un rpromig (que farem servir per determinar 2theta)
    float rpromig = (rmax + rmin)/2.f;
    float facRmax = rmax/rpromig;
    float facRmin = rmin/rpromig;
    
    //integrem
    while (t2ang < t2fin){
        t2ang = pos*step + t2ini; //angle 2theta que estem
        
        //corregim el "zero" segons cakein
        float ang = zeroPos + cakeIn;
        
        //ara hem d'editar rmax i rmin conforme l'angle 2theta en que estem, farem que sigui el "promig"
        rpromig = (float) (patt2D.distMD * FastMath.atan(FastMath.toRadians(t2ang)) / patt2D.getPixSx());
        float rmaxt2 = rpromig * facRmax;
        float rmint2 = rpromig * facRmin;

        float cx = patt2D.getCentrX() + rpromig*vecX;
        float cy = patt2D.getCentrX() + rpromig*vecX;
        
        //mirem a quin pixel es correspon considerant l'angle inicial (equacio ellipse) ("zero" en cas de 0-360)
//        float xpx = (float) (patt2D.getCentrX() + rmaxt2*Math.cos(ang)*Math.cos(angrot) - rmint2*Math.sin(ang)*Math.sin(angrot));
//        float ypx = (float) (patt2D.getCentrY() + rmaxt2*Math.cos(ang)*Math.sin(angrot) + rmint2*Math.sin(ang)*Math.cos(angrot));
        float xpx = (float) (cx + rmaxt2*Math.cos(ang)*Math.cos(angrot) - rmint2*Math.sin(ang)*Math.sin(angrot));
        float ypx = (float) (cy + rmaxt2*Math.cos(ang)*Math.sin(angrot) + rmint2*Math.sin(ang)*Math.cos(angrot));
        
        //increment en l'azimut (integracio) segons el 2theta que estem
        float dang = (float) FastMath.atan(halfpixdiag/(ypx * patt2D.getPixSy())); //mm

        VavaLogger.LOG.info("pos="+pos+" ypx="+ypx+" xpx="+xpx+" t2ang="+t2ang+" dang="+dang+" rmaxt2="+rmaxt2+" rmint2="+rmint2);


        while (ang < cakeOut){
            //sumem intensitat al pattern del pixel mes proper i girem
//            float y_row = patt2D.getCentrY() - ypx;
//            float x_col = xpx + patt2D.getCentrX();
            
            float y_row = ypx;
            float x_col = xpx;
            
            if (!patt2D.isInside(FastMath.round(x_col),FastMath.round(y_row)))break;
            
            int inten = patt2D.getInten(FastMath.round(x_col),FastMath.round(y_row));
            out.sumPoint(pos, inten, 1);
            
            //incrementem ang i "apliquem" rotacio
            ang = (float) (ang + FastMath.toDegrees(dang));
            xpx = (float) (patt2D.getCentrX() + rmaxt2*Math.cos(ang)*Math.cos(angrot) - rmint2*Math.sin(ang)*Math.sin(angrot));
            ypx = (float) (patt2D.getCentrY() + rmaxt2*Math.cos(ang)*Math.sin(angrot) + rmint2*Math.sin(ang)*Math.cos(angrot));
        }
        pos = pos + 1;
    }
    
    //Calcul desviacio (simple)
    Iterator<PointPatt1D> it = out.getPoints().iterator();
    while(it.hasNext()){
        PointPatt1D punt = it.next();
        if (punt.getNpix()<2){
            punt.setDesv(0);
            continue;
        }
        float des = (float) FastMath.sqrt(punt.getCounts());
        punt.setDesv(des);
    }
    
    return out;
  }
  
  
  public static Pattern1D intRadTilt(Pattern2D patt2D,float t2ini, float t2fin, float step, float cakeIn, float cakeOut, boolean useTilt, boolean corrLP, boolean corrIAng){
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
      
      VavaLogger.LOG.info("step ="+step+" t2ini="+t2ini+" t2fin="+t2fin);
      Pattern1D out = new Pattern1D(t2ini,t2fin,step);
      
//      int pos = 0; //posicio al vector del pattern 1d comença a zero
//      int npoints = FastMath.round((t2fin-t2ini)/step)+1; //+1 perque volem incloure t2ini i t2fin
      
      //calculs previs "constants"
      double tiltRad = 0;
      double rotRad = 0;
      if (useTilt){
          tiltRad = FastMath.toRadians(patt2D.getTiltDeg());
          rotRad = FastMath.toRadians(patt2D.getRotDeg());
      }
      double cos2tilt = FastMath.cos(tiltRad)*FastMath.cos(tiltRad);
      double sintilt = FastMath.sin(tiltRad);
      double cosrot = FastMath.cos(rotRad);
      double sinrot = FastMath.sin(rotRad);
      
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
//                  VavaLogger.LOG.info("x,y,azim="+j+","+i+","+azim);
                  //debug
                  if(j==patt2D.getCentrXI() && i==patt2D.getCentrYI()-100)VavaLogger.LOG.info("x,y,azim="+j+","+i+","+azim);
                  if(j==patt2D.getCentrXI() && i==patt2D.getCentrYI()+100)VavaLogger.LOG.info("x,y,azim="+j+","+i+","+azim);
                  if(j==patt2D.getCentrXI()+100 && i==patt2D.getCentrYI())VavaLogger.LOG.info("x,y,azim="+j+","+i+","+azim);
                  if(j==patt2D.getCentrXI()-100 && i==patt2D.getCentrYI())VavaLogger.LOG.info("x,y,azim="+j+","+i+","+azim);
                  
                  if (cakeOut<cakeIn){
                      //va al reves
                      if ((azim < cakeIn) && (azim > cakeOut))continue; 
                  }else{
                      if ((azim < cakeIn) || (azim > cakeOut))continue;    
                  }
              }
              
              //mirem la 2T del pixel en la imatge
//              double suma1 = ((j*cosrot + i*sinrot)*(j*cosrot + i*sinrot))+(-j*sinrot + i*cosrot);
//              double num = cos2tilt*(suma1*suma1);
//              double den = (patt2D.getDistMD()/patt2D.getPixSx()) + sintilt*(j*cosrot+i*sinrot);
//              double t2p = FastMath.atan(FastMath.sqrt(num/(den*den)));

//              double suma1 = ((vPCx*cosrot + vPCy*sinrot)*(vPCx*cosrot + vPCy*sinrot))+((-vPCx*sinrot + vPCy*cosrot)*(-vPCx*sinrot + vPCy*cosrot));
//              double num = cos2tilt*(suma1);
//              double den = (patt2D.getDistMD()/patt2D.getPixSx()) + sintilt*(vPCx*cosrot+vPCy*sinrot);
//              double t2p = FastMath.toDegrees(FastMath.atan(FastMath.sqrt(num/(den*den))));
              
              double t2p = patt2D.calcT2new(new Point2D.Float(j,i), true);
              
              if(t2p<t2ini||t2p>t2fin)continue;
//              if ((i==300)||(i==600)||(i==900)||(i==1200)||(i==1500)||(i==1800)){
//                  VavaLogger.LOG.info("t2p="+t2p);    
//              }
//              if ((j==300)||(j==600)||(j==900)||(j==1200)||(j==1500)||(j==1800)){
//                  VavaLogger.LOG.info("t2p="+t2p);    
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
                  float[] facLP = ImgOps.corrLP(patt2D, j, i, 1, 2, -1, false);
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
              double suma1 = ((vPCx*cosrot + vPCy*sinrot)*(vPCx*cosrot + vPCy*sinrot))+((-vPCx*sinrot + vPCy*cosrot)*(-vPCx*sinrot + vPCy*cosrot));
              double num = cos2tilt*(suma1);
              double den = (patt2D.getDistMD()/patt2D.getPixSx()) + sintilt*(vPCx*cosrot+vPCy*sinrot);
              double t2p = FastMath.toDegrees(FastMath.atan(FastMath.sqrt(num/(den*den))));
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
  public static float intRadCircle(Pattern2D patt2D, float t2,float tol2t){
      
      if (tol2t < 0) { //default value
          tol2t = 0.1f;
      }
      float step = (float) FastMath.toDegrees(FastMath.atan(patt2D.getPixSx()/patt2D.getDistMD()));

      if (step>tol2t){return -1;}
      
      //vector vertical considerant centre 0,0 seran (0,n*step)
      float ypx = 0;
      float xpx = 0;
      float t2ini = t2-tol2t;
      float t2fin = t2+tol2t;
      float t2ang = t2ini;
      long sum = 0; //valor de intensitat suma
      int npix = 0;
      //TODO: incrementar l'increment de rotacio amb l'angle
      while (t2ang <= t2fin){

          t2ang = t2ang + step; //angle que estem
          //correspondencia en pixels?
          ypx = (float) ((patt2D.getDistMD() * FastMath.tan(FastMath.toRadians(t2ang))) / patt2D.getPixSx());
          xpx = 0;
          
          VavaLogger.LOG.info("ypx="+ypx+" xpx="+xpx+" t2ang="+t2ang+" npix="+npix);
          //vector vertical (0,ycomp)
          
          float ang = 0.f;
          float dang = (float) FastMath.toRadians(0.03f); //delta Angle azimut
          
          while (ang < 360){
              //VavaLogger.LOG.info("rotangle="+ang+" ypx="+ypx+" xpx="+xpx);

              //sumem intensitat al pattern del pixel mes proper i girem
              float y_row = patt2D.getCentrY() - ypx;
              float x_col = xpx + patt2D.getCentrX();
              
              int inten = patt2D.getInten(FastMath.round(x_col),FastMath.round(y_row));
              sum = sum + inten;
              npix = npix +1;
              
              //apliquem rotacio DANG a xcomp, ycomp
              xpx = (float) (xpx * FastMath.cos(dang) - ypx * FastMath.sin(dang));
              ypx = (float) (xpx * FastMath.sin(dang) + ypx * FastMath.cos(dang));

              ang = (float) (ang + FastMath.toDegrees(dang));
          }
      }
      
      //normalitzem pel nombre de pixels
      sum = sum / npix;
      return sum;
      
  }
	
    //string is X, Y or Z
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
}

