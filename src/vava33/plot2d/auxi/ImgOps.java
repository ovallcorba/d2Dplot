package vava33.plot2d.auxi;

import java.util.Locale;

import javax.swing.JProgressBar;

import com.vava33.jutils.LogJTextArea;

/*
 * Operacions sobre imatges (sostracció fons, correccions, etc...)
 */
public final class ImgOps {
	
	private static int bkgIter;
	
	public static Pattern2D firstBkgPass(Pattern2D dataIn){
		
		Pattern2D dataOut = new Pattern2D(dataIn);
		bkgIter=0; //inicialitzem iter
		int Imin=0;
		int Imean=0;
		long Iacum=0;
		int countPoints=0;
		for(int i=0; i<dataOut.getDimY();i++){
			for(int j=0; j<dataOut.getDimX();j++){
				if(dataIn.getIntenB2(j, i)<0)continue;
				if(dataIn.getIntenB2(j, i)<Imin)Imin=dataIn.getIntenB2(j, i);
				Iacum=Iacum+dataIn.getIntenB2(j, i);
				countPoints++;
			}
		}
		Iacum=Math.round(Iacum/countPoints);
		Imean = (int) Iacum;
		
		//si la intensitat mitjana es zero, abortem el firstBkgPass
		if(Imean<0)return dataOut;
		
		//corregim els punts (els mascara ja es mantenen a -1)
		for(int i=0; i<dataOut.getDimY();i++){
			for(int j=0; j<dataOut.getDimX();j++){
				if(dataIn.getIntenB2(j, i)>(Imean+2*(Imean-Imin))){
					dataOut.setIntenB2(j, i, (short) (Imean+2*(Imean-Imin)));
				}else{
					dataOut.setIntenB2(j, i, dataIn.getIntenB2(j, i));
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
				if(dataIn.getIntenB2(j, i)<0){
					dataOut.setIntenB2(j, i, dataIn.getIntenB2(j, i));
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
							//comprovem si el punt es mascara, si es així el "restem" (no el
							//tenim en compte) i saltem al seguent
							if(dataIn.getIntenB2(l, k)<0){
								nMaskP++;
								continue;
							}
							//CAS NORMAL: punt al "centre" que no xoca amb limits (ja hem comprovat que no sigui
			                //(un punt mascara al principi)
							sumI=sumI+dataIn.getIntenB2(l, k);
						}else{
							//ESTEM FORA LA IMAGE
//			                  !Agafarem el punt mes proper al punt en questió, sempre i quant no sigui mascara
//			                  !calcularem els nous indexs l,k
//			                  !EN TOTS ELS CASOS QUE SOBRESURT HEM DE VIGILAR AMB ELS MARGES DE LA IMATGE
//			                  !SI ES QUE N'HI HA, és a dir:
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
	                        if(dataIn.getIntenB2(newL,newK)<0){
	                            nMaskP++;
	                            continue;
	                        }
	                        //si es un pixel normal el sumem
	                        sumI=sumI+dataIn.getIntenB2(newL, newK);
						}
					}
				}
				int Inew= sumI/((2*N+1)*(2*N+1)-1-nMaskP); //restem els nMaskP ja que NO han contribuit
				//si el valor de Inew es menor del que hi havia actualitzem, sino el deixem
	            if(Inew<dataIn.getIntenB2(j,i)){
	                dataOut.setIntenB2(j,i,(short) Inew);
	            }else{
	                dataOut.setIntenB2(j,i,dataIn.getIntenB2(j, i));
	            }
			}//dimX
			//PROGRESS BAR:
			if(progress!=null){
				progress.setString(String.format(Locale.ENGLISH, "Iter. %d -> %6.2f %%",bkgIter,((float)i/(float)dataOut.getDimY())*100));
			}
			//per si s'ha aturat
			if (Thread.interrupted()) {
			    throw new InterruptedException();
			}
		}//dimY
		
		long endTime   = System.currentTimeMillis();
		float totalTime = (endTime - startTime)/1000;
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
				//si la intensitat és zero saltem
				if(dataIn.getIntenB2(j, i)<0){
					dataSub.setIntenB2(j, i, dataIn.getIntenB2(j, i));
					continue;
				}
				//si la intensitat es menor a LA FEM ZERO
				if(dataIn.getIntenB2(j, i)<dataToSubtract.getIntenB2(j, i)){
					dataSub.setIntenB2(j, i, (short) 0);
					continue;
				}
				dataSub.setIntenB2(j, i, (short) (dataIn.getIntenB2(j, i)-dataToSubtract.getIntenB2(j, i)));
				if(dataSub.getIntenB2(j, i)>dataSub.getMaxI())dataSub.setMaxI(dataSub.getIntenB2(j, i));
				if(dataSub.getIntenB2(j, i)<dataSub.getMinI())dataSub.setMinI(dataSub.getIntenB2(j, i));
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
				//dataMask comença amb el pixel a zero
				dataSub[1].setIntenB2(j, i, (short)0);
				//si la intensitat és zero saltem
				if(dataIn.getIntenB2(j, i)<0){
					dataSub[0].setIntenB2(j, i, dataIn.getIntenB2(j, i));
					continue;
				}
				//si la intensitat es menor a LA FEM ZERO
				if(dataIn.getIntenB2(j, i)<dataToSubtract.getIntenB2(j, i)){
					dataSub[0].setIntenB2(j, i, (short) 0);
					dataSub[1].setIntenB2(j, i, (short) Math.abs(dataIn.getIntenB2(j, i)-dataToSubtract.getIntenB2(j, i)));
					nover=nover+1;
					continue;
				}
				dataSub[0].setIntenB2(j, i, (short) (dataIn.getIntenB2(j, i) - (dataToSubtract.getIntenB2(j, i)*factor)));
				if(dataSub[0].getIntenB2(j, i)>dataSub[0].getMaxI())dataSub[0].setMaxI(dataSub[0].getIntenB4(j, i));
				if(dataSub[0].getIntenB2(j, i)<dataSub[0].getMinI())dataSub[0].setMinI(dataSub[0].getIntenB4(j, i));
				
				if(out!=null){
		            String linia = String.format(Locale.ENGLISH, "No. of pixels with Ybkg>Ydata = %d (%.1f)",
		            		nover,((float)(nover)/(float)(dataSub[0].getDimX()*dataSub[0].getDimY())*100));
					out.ln(linia);
				}
			}
		}
		return dataSub;
	}
	
	public static float calcGlassScale(Pattern2D data, Pattern2D glass){
		float scale = 10000000;
		for(int i=0; i<data.getDimY();i++){
			for(int j=0; j<data.getDimX();j++){
				//no considerem I=0 o mascara
				if (data.getIntenB2(j, i)<0)continue;
				if (glass.getIntenB2(j, i)<0)continue;
				float sc = (float)(data.getIntenB2(j, i))-(float)Math.sqrt((float)data.getIntenB2(j, i))/(float)(glass.getIntenB2(j, i));
				if (sc<scale)scale=sc;
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
		Pattern1D intrad = glass.intRad(t2ini, t2fin, stepsize, false);
		
		//per cada pixel mirarem la intensitat mitjana a l'anell que es troba i si aquesta intensitat es
        //mes gran que ymean+2*desv el considerem pic espuri
		for(int i=0; i<glass.getDimY();i++){
			for(int j=0; j<glass.getDimX();j++){
				//s'haurien d'haver aplicat les zones excloses al vidre
				if(glass.isInExZone(j, i))continue;
				//2Theta pixel imatge per determinar amplada i angle
				t2p = glass.calc2T(j, i, true);
				//ara hem de mirar a quina posicio del vector desv es troba aquesta 2t
				pos = Math.round(t2p/stepsize)-Math.round(t2ini/stepsize);
	            //ara hem de decidir si es pic espuri o no
	            //dos opcions, amb la mitjana+% o amb la fact*desviacio
				float ysum = (float)intrad.getPoint(pos).getCounts();
				float npix = (float)intrad.getPoint(pos).getNpix();
				criteri= ysum/npix + (ysum/npix)*percent; //factDesv*desv(p)
				//criteri=(real(ysum(p))/real(npix(p)))+factDesv*desv(p)		
	            if(glass.getIntenB2(j,i)>criteri)glass.setIntenB2(j,i,(short) Math.round(ysum/npix));
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
				diff=dataIn.getIntenB2(j, i)-dataBkg.getIntenB2(j, i);
				if(diff<minDiff){
					minDiff=diff;
					col_X=j;
					row_Y=i;
				}
			}
		}
		return minDiff;
	}
	
//    !calcula una nova iteració del fons a partir de l'anterior
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
   		Pattern1D intrad = dataIn.intRad(0.0f, t2fin, stepsize, false); //realment aqui en el promig hauriem de NO considerar pics
   		
   		float t2p,ysum,npix;
   		int pos;
   		//dataOut sera el promig de la integracio radial de dataIn
   		for(int i=0; i<dataOut.getDimY();i++){
   			for(int j=0; j<dataOut.getDimX();j++){
   				if(dataIn.isInExZone(j, i)){
   					dataOut.setIntenB2(j, i, dataIn.getIntenB2(j, i));
   					continue;
   				}
   				t2p = dataOut.calc2T(j, i, true);
   				//pos al vector
				pos = Math.round(t2p/stepsize)-Math.round(0.0f/stepsize);
	            //ara hem de decidir si es pic espuri o no
	            //dos opcions, amb la mitjana+% o amb la fact*desviacio
				ysum = (float)intrad.getPoint(pos).getCounts();
				npix = (float)intrad.getPoint(pos).getNpix();
				dataOut.setIntenB2(j, i, (short) (ysum/npix));
   			}
   			
			//PROGRESS BAR:
			if(progress!=null){
				progress.setString(String.format(Locale.ENGLISH, "Iter. %d -> %6.2f %%",bkgIter,((float)i/(float)dataOut.getDimY())*100));
			}
			//per si s'ha aturat
			if (Thread.interrupted()) {
			    throw new InterruptedException();
			}
   			
   		}
		long endTime   = System.currentTimeMillis();
		float totalTime = (endTime - startTime)/1000;
		if(txtOut!=null){
			txtOut.addtxt(true, true, "fi iter. "+bkgIter+" (time: "+totalTime+" s)");
		}
		return dataOut;
    }
    
//    !calcula una nova iteració del fons a partir de l'anterior
//    !parametres: -it0: iteracio anterior de referencia
//    !            -it1: nova iteracio
//    !            -N: pixels de la regio rectangular
//    !els valors de -1 (mascara) els deixarem tal qual (no calcularem fons) i
//    !tambe farem que no contribueixin a promitjar el fons d'altres punts. Per
//    !això emmagatzemarem a una variable int (nMaskP) el nombre de punts d'aquests
//    !que haguéssin contribuit i ho restarem al fer el promig
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
   					dataOut.setIntenB2(j, i, dataIn.getIntenB2(j, i));
   					continue;
   				}
   				//per cada pixel mirem el promig (iteracio anterior)
//   		    float angle = (float) (factAngle*Math.toRadians(((1-t2p)*1.5))); //TODO:revisar
//   			float amplada = (1/t2p)*factAmplada;
   				float[] fact = dataIn.getFactAngleAmplada(j, i);
   				float obertura = oberturaArc * fact[0];
   				float amplada = ampladaArc * fact[1];
   				Patt2Dzone arc = dataIn.Yarc2(j, i, amplada, obertura, false, 0, false);
   				
//   	        assignem al pixel la intensitat mitjana (fitxer de fons que despres restarem)
//   	        nomes si la intensitat anterior es superior
   				//TODO: aqui es podria introduir la intensitat del fons calculada a Yarc en comptes
   				//      de Ymean
   				if(dataIn.getIntenB2(j, i)>arc.getYmean()){
   					dataOut.setIntenB2(j, i, (short) arc.getYmean());
   				}else{
   					dataOut.setIntenB2(j, i, dataIn.getIntenB2(j, i));
   				}
   			}
   			
			//PROGRESS BAR:
//			if((progress!=null)&&(i%10==0)){
			if((progress!=null)){
				progress.setString(String.format(Locale.ENGLISH, "Iter. %d -> %6.2f %%",bkgIter,((float)i/(float)dataOut.getDimY())*100));
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
//   			float angle = (float) (factAngle*Math.toRadians(((1-t2p)*1.5)));
//   			float amplada = (1/t2p)*factAmplada;
//   			dataOut.Yarc(j, i, amplada, angle, false, 0, true);
//   			j=j+300;
//   			i=i+300;
//   		}

		long endTime   = System.currentTimeMillis();
		float totalTime = (endTime - startTime)/1000;
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
    	
   		bkgIter=bkgIter+1;
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
   					dataOut.setIntenB2(j, i, dataIn.getIntenB2(j, i));
   					continue;
   				}
   				//punt central (normalment mascara)
   				if(i==Math.round(dataIn.getCentrX())&&j==Math.round(dataIn.getCentrY())){
   					dataOut.setIntenB2(j, i, dataIn.getIntenB2(j, i));
   					continue;
   				}
   				//inicialitzem
//   				outx=false;
//   				outy=false;
   				//calcul de v0,v1,v2,v3
   				float obertura = 1,amplada = 1;
   				if(minarc){
//   					t2p = dataIn.calc2T(j, i, false);
//   					angle = (float) (oberturaArc*Math.toRadians(((1-t2p)*1.5))); //TODO: revisar si cal passar a radiants
//   					amplada = (1.f/t2p) * ampladaArc;
   	   				float[] fact = dataIn.getFactAngleAmplada(j, i);
   	   				obertura = oberturaArc * fact[0];
   	   				amplada = ampladaArc * fact[1];
   					zone = dataIn.Yarc2(j, i, amplada, obertura, true, 0, false);
   					v0 = Math.round(zone.getYmean());
//   					txtOut.ln("pix"+j+","+i);
   				}else{
   					v0 = dataIn.calcIntSquare(j, i, aresta, true);
   				}
   				//inicialitzem v1,v2 i v3 igual a v0 en cas que no s'assignin despres perque queden fora
   				v1=v0;
   				v2=v0;
   				v3=v0;
   				newj=Math.round(dataIn.getCentrX()+(dataIn.getCentrX()-j));
   				newi=Math.round(dataIn.getCentrY()+(dataIn.getCentrY()-i));
   				
   				if (minarc){
   					//prova amb integracio arc
   					if(hor){
   						zone = dataIn.Yarc2(newj, i, amplada, obertura, true, 0, false);
   						if(zone.getNpix()>0)v1=Math.round(zone.getYmean());
   					}
   					if(ver){
   						zone = dataIn.Yarc2(j, newi, amplada, obertura, true, 0, false);
   						if(zone.getNpix()>0)v1=Math.round(zone.getYmean());
   					}
   					if(horver){
   						zone = dataIn.Yarc2(newj, newi, amplada, obertura, true, 0, false);
   						if(zone.getNpix()>0)v1=Math.round(zone.getYmean());
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
   				dataOut.setIntenB2(j, i, (short) ImgOps.findMin(v0,v1,v2,v3));
   			}
			//PROGRESS BAR:
			if(progress!=null){
				progress.setString(String.format(Locale.ENGLISH, "Iter. %d -> %6.2f %%",bkgIter,((float)i/(float)dataOut.getDimY())*100));
			}
			//per si s'ha aturat
			if (Thread.interrupted()) {
			    throw new InterruptedException();
			}
   			
   		}
		long endTime   = System.currentTimeMillis();
		float totalTime = (endTime - startTime)/1000;
		if(txtOut!=null){
			txtOut.addtxt(true, true, "fi iter. "+bkgIter+" (time: "+totalTime+" s)");
		}
		return dataOut; 
    }
    
	public static Pattern2D corrLP(Pattern2D dataIn,int iPol, int iLor, boolean debug){
		Pattern2D dataOut = new Pattern2D(dataIn);
		Pattern2D dataI4temp = new Pattern2D(dataIn.getDimX(),dataIn.getDimY(),true);
		int maxVal=0;
		int minVal=99999999;
		
//	    per cada pixel s'ha de calcular l'angle azimutal (entre la normal
//	    (al pla de polaritzacio *i* el vector del centre de la imatge (xc,yc)
//	    al pixel (x,y) en questió). També s'ha de calcular l'angle 2T del pixel
		for(int i=0; i<dataIn.getDimY();i++){
			for(int j=0; j<dataIn.getDimX();j++){
				//zona exclosa saltem
				if (dataIn.isInExZone(j, i))continue;
				//el punt central el saltem
				if ((i == dataIn.getCentrY())&&(j == dataIn.getCentrX()))continue;
				
				//debug: tots els punts amb intensitat 12500 (per veure efectes)
				if(debug){
					dataIn.setIntenB2(j, i, (short) 12500);
					dataIn.setMaxI(12500);
				}
				
				float vecCPx = (float)(j-dataIn.getCentrX())*dataIn.pixSx;
				float vecCPy = (float)(dataIn.getCentrY()-i)*dataIn.pixSy;
				float vecCPmod = (float) Math.sqrt(vecCPx*vecCPx+vecCPy*vecCPy);
				float t2 = (float) Math.atan(vecCPmod/dataIn.getDistMD());
				
				//vector perpendicular al pla de polarització (el modul no importa)
				float vecPerX=0;
				float vecPerY=100;
				//calculem l'angle amb prod. escalar
				float num = vecCPy*vecPerY;
				float den = vecCPmod*vecPerY;
				float azim;
				if((den<0.000001)&&(den>-0.000001)){
					azim=0.f; //cas punt central
				}else{
					azim=(float) Math.acos(num/den);
				}
				
				//calcul de la polarització
				float pol = 1.0f;
				if(iPol==1){
					pol=(float) ((Math.cos(azim)*Math.cos(azim))+(Math.sin(azim)*Math.sin(azim))*(Math.cos(t2)*Math.cos(t2)));
				}
				if(iPol==2){
					pol=(float) (0.5f+0.5f*(Math.cos(t2)*Math.cos(t2)));
				}
				
				//calcul de lorentz
				float lor = 1.0f;
				if(iLor==1){
					lor = (float) (Math.cos(t2)*Math.abs(vecCPy));
				}
				if(iLor==2){
					lor = (float) (1/(Math.cos(t2/2)*Math.sin(t2/2)*Math.sin(t2/2))); //igual que el dajust
				}
				dataI4temp.setIntenB4(j, i, Math.round((dataIn.getIntenB2(j, i)*lor)/pol));
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
				if(dataIn.isInExZone(j, i))dataOut.setIntenB2(j, i, dataIn.getIntenB2(j, i));
				dataOut.setIntenB2(j, i, (short) Math.round((float)dataI4temp.getIntenB4(j, i)*fscale));
			}
		}
		
		return dataOut;
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
	
	
	
}
