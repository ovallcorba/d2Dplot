package vava33.d2dplot.auxi;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Locale;

import org.apache.commons.math3.util.FastMath;

import vava33.d2dplot.D2Dplot_global;
import com.vava33.jutils.VavaLogger;

public class Pattern1D {
	float t2i,t2f;
	float step; //stepsize
	ArrayList<PointPatt1D> points;
	private static VavaLogger log = D2Dplot_global.log;

	public class PointPatt1D{
		int counts;
		int npix;
		float desv;
		float t2;
		public PointPatt1D(int counts, int npix, float desv){
			this.counts=counts;
			this.npix=npix;
			this.desv=desv;}
		public PointPatt1D(){
			this.counts=0;
			this.npix=0;
			this.desv=0;}
		public PointPatt1D(float t2){
			new PointPatt1D();
			this.t2=t2;}
		public int getCounts() {return counts;}
		public void setCounts(int counts) {this.counts = counts;}
		public void addCounts(int counts) {this.counts = this.counts + counts;}
		public int getNpix() {return npix;}
		public void setNpix(int npix) {this.npix = npix;}
		public void addNpix(int npix) {this.npix = this.npix + npix;}
		public float getDesv() {return desv;}
		public void setDesv(float desv) {this.desv = desv;}
		public void addDesv(float desv) {this.desv = this.desv + desv;}
		public float getT2() {return t2;}
		public void setT2(float t2) {this.t2 = t2;}
	}
	
	public Pattern1D(float t2ini, float t2fin, float stepsize){
		this.t2i=t2ini;
		this.t2f=t2fin;
		this.step=stepsize;
		int npoints = FastMath.round((t2fin-t2ini)/stepsize)+1; //+1 perque volem incloure t2ini i t2fin
		this.points = new ArrayList<PointPatt1D>(npoints);
		//inicialitzem a zero els punts
		float t2p=t2ini;
		for (int i=0;i<=npoints;i++){
			this.points.add(new PointPatt1D(t2p));
			t2p=t2p+stepsize;
		}
	}
	
	//afegeix un punt a una posicio concreta del vector (sobreescriu el que hi ha)
	public void addPoint(int posicio, int counts, int npix, float desv){
		this.getPoints().set(posicio, new PointPatt1D(counts,npix,desv));
	}
	
	//suma en un punt existent del vector punts
	public void sumPoint(int posicio, int counts, int npix){
		this.getPoints().get(posicio).addCounts(counts);
		this.getPoints().get(posicio).addNpix(npix);
	}
	
	public PointPatt1D getPoint(int pos){
		return this.getPoints().get(pos);
	}

	//escriu fitxer xy (si fileout==null pregunta on guardar)
	public void writeXY(File fileout, String title){
       if(fileout==null)return;
		//Escriure fitxer format xy
        try {
            PrintWriter output = new PrintWriter(new BufferedWriter(new FileWriter(fileout)));
            // ESCRIBIM AL FITXER:
            String linia = String.format(Locale.ENGLISH, "! %s",title);
            output.println(linia);
            linia = String.format(Locale.ENGLISH, "  %.5f  %.5f  %.5f",this.t2i,this.step,this.t2f);
            output.println(linia);
            Iterator<PointPatt1D> itp = this.getPoints().iterator();
            while(itp.hasNext()){
            	PointPatt1D p = itp.next();
            	linia = String.format(Locale.ENGLISH, "  %.5f  %d  %.5f",p.getT2(),p.getCounts(),p.getDesv());
            	output.println(linia);
            }
            output.close();
            
        } catch (IOException e) {
            if(D2Dplot_global.isDebug())e.printStackTrace();
            log.warning("error writting xy file");
        }
	}
	
    //escriu fitxer xy (si fileout==null pregunta on guardar)
    public void writeXYnorm(File fileout, String title){
        if(fileout==null)return;
        //Escriure fitxer format xy
        try {
            PrintWriter output = new PrintWriter(new BufferedWriter(new FileWriter(fileout)));
            // ESCRIBIM AL FITXER:
            String linia = String.format(Locale.ENGLISH, "! %s",title);
            output.println(linia);
            linia = String.format(Locale.ENGLISH, "  %.5f  %.5f  %.5f",this.t2i,this.step,this.t2f);
            output.println(linia);
            Iterator<PointPatt1D> itp = this.getPoints().iterator();
            while(itp.hasNext()){
                PointPatt1D p = itp.next();
                if (p.getNpix()==0) {p.setNpix(1);}
                linia = String.format(Locale.ENGLISH, "  %.5f  %.5f  %.5f",p.getT2(),(float)p.getCounts()/(float)p.getNpix(),p.getDesv()/p.getNpix());
                output.println(linia);
            }
            output.close();
            
        } catch (IOException e) {
            if(D2Dplot_global.isDebug())e.printStackTrace();
            log.warning("error writting xy file");
        }
    }
	
	public float getT2i() {
		return t2i;
	}

	public void setT2i(float t2i) {
		this.t2i = t2i;
	}

	public float getT2f() {
		return t2f;
	}

	public void setT2f(float t2f) {
		this.t2f = t2f;
	}

	public float getStep() {
		return step;
	}

	public void setStep(float step) {
		this.step = step;
	}

	public ArrayList<PointPatt1D> getPoints() {
		return points;
	}

	public void setPoints(ArrayList<PointPatt1D> points) {
		this.points = points;
	}
	
	
}


