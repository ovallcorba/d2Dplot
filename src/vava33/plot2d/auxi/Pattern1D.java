package vava33.plot2d.auxi;

import java.io.File;
import java.util.ArrayList;

public class Pattern1D {
	float t2i,t2f;
	float step; //stepsize
	ArrayList<PointPatt1D> points;
	
	public class PointPatt1D{
		int counts;
		int npix;
		float desv;
		public PointPatt1D(int counts, int npix, float desv){
			this.counts=counts;
			this.npix=npix;
			this.desv=desv;}
		public int getCounts() {return counts;}
		public void setCounts(int counts) {this.counts = counts;}
		public void addCounts(int counts) {this.counts = this.counts + counts;}
		public int getNpix() {return npix;}
		public void setNpix(int npix) {this.npix = npix;}
		public void addNpix(int npix) {this.npix = this.npix + npix;}
		public float getDesv() {return desv;}
		public void setDesv(float desv) {this.desv = desv;}
		public void addDesv(float desv) {this.desv = this.desv + desv;}
	}
	
	public Pattern1D(float t2ini, float t2fin, float stepsize){
		this.t2i=t2ini;
		this.t2f=t2fin;
		this.step=stepsize;
		int npoints = Math.round((t2fin-t2ini)/stepsize)+1; //+1 perque volem incloure t2ini i t2fin
		this.points = new ArrayList<PointPatt1D>(npoints);
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
	public void writeXY(File fileout){
		if(fileout==null){
			//filechoser
		}
		//TODO: escriure fitxer format xy
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


