package com.vava33.d2dplot.auxi;

public class Pixel {

    private int x,y, intensity;
    private boolean isExcluded;
    //TODO: afegir RO, 2Theta, etc...
    
    
    public Pixel(int x_col, int y_row, int intensity_counts) {
        this.x=x_col;
        this.y=y_row;
        this.intensity=intensity_counts;
        this.isExcluded=false;
    }
    
    public Pixel(int y_row, int x_col, int intensity_counts, boolean isExcluded) {
        this(y_row, x_col, intensity_counts);
        this.isExcluded=false;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getIntensity() {
        return intensity;
    }

    public void setIntensity(int intensity) {
        this.intensity = intensity;
    }

    public boolean isExcluded() {
        return isExcluded;
    }

    public void setExcluded(boolean isExcluded) {
        this.isExcluded = isExcluded;
    }
    
    
    
}
