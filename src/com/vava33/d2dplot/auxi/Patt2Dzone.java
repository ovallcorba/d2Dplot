package vava33.d2dplot.auxi;

import java.awt.geom.Point2D;
import java.util.Locale;

/*
 * Encapsula una zona de la imatge
 */
public class Patt2Dzone implements Comparable<Patt2Dzone>{
//    !  - ysum: intensitat suma de la zona
//    !  - npix: nombre de pixels que han contribuit a la suma
//    !  - ymean: intensitat mitjana de la zona
//    !  - ymeandesv: desviacio estandard de la mitjana (ysum/npix)
//    !  - ymax: intensitat maxima del pixel
//    !  - ybkg: intensitat del fons (mitjana dels 20 punts de menor intensitat)
//    !  - ybkgdesv: desviacio de la intensitat del fons (entre els 20 punts)
	
	int ysum, npix, ymax;
	float ymean,ymeandesv,ybkg,ybkgdesv;
	int intradPix,bkgpt;
	float azimAngle;
	Point2D.Float centralPoint;
	Pattern2D patt2d; //referencia a quin pattern es la zona
	
	//new zone
	public Patt2Dzone(int npix, int ysum, int ymax, float ymean, float ymeandesv, float ybkg, float ybkgdesv){
		this.npix=npix;
		this.ysum=ysum;
		this.ymax=ymax;
		this.ymean=ymean;
		this.ymeandesv=ymeandesv;
		this.ybkg=ybkg;
		this.ybkgdesv=ybkgdesv;
		this.intradPix=-1;
		this.azimAngle=-1;
		this.centralPoint=null;
	}
	
	//new empty zone
	public Patt2Dzone(){
	    
	}

	public int getYsum() {
		return ysum;
	}

	public void setYsum(int ysum) {
		this.ysum = ysum;
	}

	public int getNpix() {
		return npix;
	}

	public void setNpix(int npix) {
		this.npix = npix;
	}

	public int getYmax() {
		return ymax;
	}

	public void setYmax(int ymax) {
		this.ymax = ymax;
	}

	public float getYmean() {
		return ymean;
	}

	public void setYmean(float ymean) {
		this.ymean = ymean;
	}

	public float getYmeandesv() {
		return ymeandesv;
	}

	public void setYmeandesv(float ymeandesv) {
		this.ymeandesv = ymeandesv;
	}

	public float getYbkg() {
		return ybkg;
	}

	public void setYbkg(float ybkg) {
		this.ybkg = ybkg;
	}

	public float getYbkgdesv() {
		return ybkgdesv;
	}

	public void setYbkgdesv(float ybkgdesv) {
		this.ybkgdesv = ybkgdesv;
	}

	public int getIntradPix() {
        return intradPix;
    }

    public void setIntradPix(int intradPix) {
        this.intradPix = intradPix;
    }

    public float getAzimAngle() {
        return azimAngle;
    }

    public void setAzimAngle(float azimAngle) {
        this.azimAngle = azimAngle;
    }

    public int getBkgpt() {
        return bkgpt;
    }

    public void setBkgpt(int bkgpt) {
        this.bkgpt = bkgpt;
    }

    public Point2D.Float getCentralPoint() {
        return centralPoint;
    }

    public void setCentralPoint(Point2D.Float centralPoint) {
        this.centralPoint = centralPoint;
    }

    public Pattern2D getPatt2d() {
        return patt2d;
    }

    public void setPatt2d(Pattern2D patt2d) {
        this.patt2d = patt2d;
    }

    public String toString(){
	    String s = String.format(Locale.ENGLISH, "%d %d %d %.2f %.2f %.2f %.2f",
	        this.npix,
            this.ysum,
            this.ymax,
            this.ymean,
            this.ymeandesv,
            this.ybkg,
            this.ybkgdesv);
        return s;
	}

	//per Ymax decreixent
    @Override
    public int compareTo(Patt2Dzone o) {
        if (this.ymax>o.ymax){
            return 0;
        }else{
            return 1;    
        }
    }
}
