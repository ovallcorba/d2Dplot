package com.vava33.d2dplot.auxi;

import org.apache.commons.math3.util.FastMath;

public class Pixel {

    private int x, y, intensity;
    private boolean isExcluded;
    private float err;
    //TODO: afegir RO, 2Theta, etc...

    public Pixel(int x_col, int y_row, int intensity_counts) {
        this.x = x_col;
        this.y = y_row;
        this.intensity = intensity_counts;
        this.setErr((float) FastMath.sqrt(FastMath.abs(intensity_counts)));
        this.isExcluded = false;
    }

    public Pixel(int y_row, int x_col, int intensity_counts, boolean isExcluded) {
        this(y_row, x_col, intensity_counts);
        this.isExcluded = false;
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

    public void setIntensity(int intensity, boolean updateErr) {
        this.intensity = intensity;
        if (updateErr)
            this.setErr((float) FastMath.sqrt(FastMath.abs(intensity)));
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
