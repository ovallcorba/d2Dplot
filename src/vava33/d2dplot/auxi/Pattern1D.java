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
	private String comment;
    private static VavaLogger log = D2Dplot_global.getVavaLogger(Pattern1D.class.getName());

	public class PointPatt1D{
		int counts;
		int npix;
		float desv;
		float t2;
		float intensity; //INTENSITY (means divided by npix and corrected for LP)
		public PointPatt1D(){
			this.counts=0;
			this.npix=0;
			this.intensity=0;
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
	    public float getIntensity() {return intensity;}
	    public void setIntensity(float inten) {this.intensity = inten;}
	}
	
	public Pattern1D(float t2ini, float t2fin, float stepsize){
		this.t2i=t2ini;
		this.t2f=t2fin;
		this.step=stepsize;
		this.comment="";
		int npoints = FastMath.round((t2fin-t2ini)/stepsize)+1; //+1 perque volem incloure t2ini i t2fin
		this.points = new ArrayList<PointPatt1D>(npoints);
		//inicialitzem a zero els punts
		float t2p=t2ini;
		for (int i=0;i<=npoints;i++){
			this.points.add(new PointPatt1D(t2p));
			t2p=t2p+stepsize;
		}
	}
	
	//suma en un punt existent del vector punts
	public void sumPoint(int posicio, int counts, int npix){
		this.getPoints().get(posicio).addCounts(counts);
		this.getPoints().get(posicio).addNpix(npix);
	}
	
	public PointPatt1D getPoint(int pos){
		return this.getPoints().get(pos);
	}
	
    //escriu fitxer xy
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
                linia = String.format(Locale.ENGLISH, "  %.5f  %.5f  %.5f",p.getT2(),p.getIntensity(),p.getDesv());
                output.println(linia);
            }
            output.close();
            
        } catch (IOException e) {
            if(D2Dplot_global.isDebug())e.printStackTrace();
            log.warning("error writting xy file");
        }
    }
    
    //escriu fitxer dat
    //afegeix comentari i els simbols son #
    public void writeDAT(File fileout, String title){
        if(fileout==null)return;
        //Escriure fitxer format dat
        try {
            PrintWriter output = new PrintWriter(new BufferedWriter(new FileWriter(fileout)));
            // ESCRIBIM AL FITXER:
            String linia = String.format(Locale.ENGLISH, "# %s",title);
            output.println(linia);
            linia = String.format(Locale.ENGLISH, "# %s",this.getComment());
            output.println(linia);
            Iterator<PointPatt1D> itp = this.getPoints().iterator();
            while(itp.hasNext()){
                PointPatt1D p = itp.next();
                if (p.getNpix()==0) {p.setNpix(1);}
                linia = String.format(Locale.ENGLISH, "  %.7E  %.7E  %.7E",p.getT2(),p.getIntensity(),p.getDesv());
                output.println(linia);
            }
            output.close();
            
        } catch (IOException e) {
            if(D2Dplot_global.isDebug())e.printStackTrace();
            log.warning("error writting dat file");
        }
    }
    
    public float getDesvForA2Trange(float t2ini, float t2fin){
        Iterator<PointPatt1D> itp = this.getPoints().iterator();
        int npoints = 0;
        float sumSD = 0;
        while(itp.hasNext()){
            PointPatt1D p = itp.next();
            if ((p.t2>=t2ini)&&(p.t2<=t2fin)){
                if (p.npix<=0) continue;
                sumSD = sumSD + (p.desv/p.npix);
                npoints=npoints+1;
            }
        }
        if (npoints>0){
            return sumSD/npoints;            
        }else{
            return 0;
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

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
	
	
}


