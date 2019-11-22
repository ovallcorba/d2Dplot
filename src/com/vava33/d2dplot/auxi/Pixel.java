package com.vava33.d2dplot.auxi;

import org.apache.commons.math3.util.FastMath;

public class Pixel {
//    private static final String className = "Pixel";
//    private static VavaLogger log = D2Dplot_global.getVavaLogger(className);
    
    private int x, y, intensity;
    private boolean isExcluded;
    private float err;
    private int contrib; //in case of sums... number of contributing pixels to the pixel (useful in the 2D azim plot...)
    //TODO: afegir RO, 2Theta, etc...

    public Pixel(int x_col, int y_row, int intensity_counts) {
        this.x = x_col;
        this.y = y_row;
        this.intensity = intensity_counts;
        this.contrib=1;
        this.setErr((float) FastMath.sqrt(FastMath.abs(intensity_counts)));
        this.isExcluded = false;
    }

    public Pixel(int y_row, int x_col, int intensity_counts, boolean isExcluded) {
        this(y_row, x_col, intensity_counts);
        this.isExcluded = isExcluded;
    }
    
    //contrib 0, just initialize with 0 intensity
    public Pixel(int y_row, int x_col) {
        this(y_row,x_col,0);
        this.isExcluded=false;
        this.contrib=0;
    }

    public int getX() {
        return this.x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return this.y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getIntensity() {
        return this.intensity;
    }
    
    public void normalizeIntensity() {
        if(contrib<=0)return;
//        log.writeNameNumPairs("config", true, "x,y,intensity,contrib", x,y,intensity,contrib);
        this.intensity = FastMath.round((float)this.intensity/(float)this.contrib);
    }

    public void setIntensity(int intensity, boolean updateErr) {
        this.intensity = intensity;
        this.contrib=1;
        if (updateErr)
            this.setErr((float) FastMath.sqrt(FastMath.abs(intensity)));
    }
    
    public void addIntensity(int intensity, boolean updateErr) {
        this.intensity = this.intensity+intensity;
        this.contrib=this.contrib+1;
        if (updateErr)
            this.setErr((float) FastMath.sqrt(FastMath.abs(this.intensity)));
    }

    public boolean isExcluded() {
        return this.isExcluded;
    }

    public void setExcluded(boolean isExcluded) {
        this.isExcluded = isExcluded;
    }

    public float getErr() {
        return this.err;
    }

    public void setErr(float err) {
        this.err = err;
    }

}
