package com.vava33.d2dplot.d1dplot;

public class DataPoint {

    private double x;
    private double y;
    private double sdy;
    private double yBkg;

    public DataPoint(double px, double py, double pysd) {
        this.setX(px);
        this.setY(py);
        this.setSdy(pysd);
        this.setyBkg(0);
    }

    public DataPoint(double px, double py, double pysd, double ybkg) {
        this.setX(px);
        this.setY(py);
        this.setSdy(pysd);
        this.setyBkg(ybkg);
    }

    public double getX() {
        return this.x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return this.y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getSdy() {
        return this.sdy;
    }

    public void setSdy(double sdy) {
        this.sdy = sdy;
    }

    public double getyBkg() {
        return this.yBkg;
    }

    public void setyBkg(double yBkg) {
        this.yBkg = yBkg;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        final DataPoint dp = (DataPoint) obj;
        if ((dp.getX() == this.getX()) && (dp.getY() == this.getY()) && (dp.getSdy() == this.getSdy())) {
            return true;
        } else {
            return false;
        }

    }

}
