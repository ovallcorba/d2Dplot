package com.vava33.d2dplot.auxi;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.EigenDecomposition;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;
import org.apache.commons.math3.util.FastMath;

import com.vava33.d2dplot.D2Dplot_global;
import com.vava33.jutils.VavaLogger;


public class EllipsePars {
    private static final int ElliFitMinPoints = 5;

    private double a,b,c,d,e,f;
    private double rmaj,rmin; //radi major i menor de l'ellipse
    private double angrot;  //respecte rmax, zero a les "12h" i positiu horari EN RADIANS
    private double xcen, ycen;
    private ArrayList<Point2D.Float> estimPoints;
    private boolean isFit;
    private int lab6ring;
    private static final String className = "EllipsePars";
    private static VavaLogger log = D2Dplot_global.getVavaLogger(className);

    public EllipsePars(){
        this.estimPoints = new ArrayList<Point2D.Float>();
        this.isFit=false;
        setLab6ring(-1);
    }
    
    public EllipsePars(double a, double b, double c, double d, double e, double f){
        super();
        this.a=a;
        this.b=b;
        this.c=c;
        this.d=d;
        this.e=e;
        this.f=f;
        
        this.calcCenAxisRot();
    }

    //create directly only from parametric
    public EllipsePars(double rmax, double rmin, double xcen, double ycen, double angrot){
        super();
        this.xcen=xcen;
        this.ycen=ycen;
        this.rmaj=rmax;
        this.rmin=rmin;
        this.angrot=angrot;
        isFit=true;
    }
    
    private void setPars(double a, double b, double c, double d, double e, double f){
        this.a=a;
        this.b=b;
        this.c=c;
        this.d=d;
        this.e=e;
        this.f=f;
        
        this.calcCenAxisRot();
    }
    
    private void calcCenAxisRot(){
        //CENTRE
        double num = b*b-a*c;
        this.xcen=(c*d-b*e)/num;
        this.ycen=(a*e-b*d)/num;
        
        //EIXOS
        double up = 2*(a*e*e+c*d*d+f*b*b-2*b*d*e-a*c*f);
        double down1=(b*b-a*c)*( (c-a)*Math.sqrt(1+4*b*b/((a-c)*(a-c)))-(c+a));
        double down2=(b*b-a*c)*( (a-c)*Math.sqrt(1+4*b*b/((a-c)*(a-c)))-(c+a));
        double rH = Math.sqrt(up/down1);
        double rV=Math.sqrt(up/down2);

        //ROTACIO
        float angr = (float) (0.5*Math.atan(b/(a-c)));
//        float angr = (float) FastMath.atan2(2*b, a-c);
        log.debug("angr directe="+FastMath.toDegrees(angr));
//        angr = angr*2;
        this.isFit = true;
        
        log.writeNameNumPairs("debug", true, "rH,rV", rH,rV);
        
        /*
         * ATENCIO:
         * l'angle de rotacio sempre ve donat en referencia a RH,
         * de forma que el zero correspon a les "3" d'un rellotge 
         * i va en positiu sentit horari i negatiu antihorari.
         * 
         * Alehores hem d'agafar un conveni per sempre representar
         * totes les ellipses igual, que sapiguem com és.
         * 
         * Farem que la rotació vingui determinada per l'EIX MAJOR de
         * forma que el zero correspondrà a les "12" d'un rellotge 
         * serà positiu en sentit horari.
         *  
         *  
         * FIT2D: angle of Rmaj respecte eix X en ACW
         */
        
       
        //ARA ROT ES L'ANGLE de EIX VERTICAL AMB CW +
        if (rH>rV){ //vol dir Rmaj es a les X, angr es l'angle CW des de les 3 respecte Rmaj, cal canviar-ho
            this.rmaj = rH;
            this.rmin = rV;
            //aixi doncs ara l'angle que vull es equivalent, no cal canviar res... (no sé si hauré de canviar signe, igual que m'ha passat a anterior versió
            this.angrot = angr;
        }else{ //tenim l'angle de rmin respecte l'eix X, he de sumar 90º
            this.rmaj = rV;
            this.rmin = rH;
            this.angrot = angr + (FastMath.PI/2);
        }
        
        
        log.writeNameNumPairs("debug", true, "rmaj,rmin,angrot", this.rmaj,this.rmin,FastMath.toDegrees(this.angrot));
    }
    
    public void logElliPars(String level){
        log.printmsg(level, "coefs (a b c d e f)="+this.a+" "+this.b+" "+this.c+" "+this.d+" "+this.e+" "+this.f);
        log.printmsg(level, "centre (x y)="+this.xcen+" "+this.ycen);
        log.printmsg(level, "axes (maj min)="+this.rmaj+" "+this.rmin);
        log.printmsg(level, "rot="+this.angrot+" (deg="+FastMath.toDegrees(this.angrot)+")");            
    }
    
    public String toStringElliPars(){
        StringBuilder sb = new StringBuilder();
        sb.append("coefs (a b c d e f)="+this.a+" "+this.b+" "+this.c+" "+this.d+" "+this.e+" "+this.f);
        sb.append("\n");
        sb.append("centre (x y)="+this.xcen+" "+this.ycen);
        sb.append("\n");
        sb.append("axes (maj min)="+this.rmaj+" "+this.rmin);
        sb.append("\n");
        sb.append("rot="+this.angrot+" (deg="+FastMath.toDegrees(this.angrot)+")");
        sb.append("\n");
        return sb.toString().trim();
    }
    
    //give an angle in degrees from the vertical up (0) to clockwise (+) to get the point of the ellipse (from parametric ecuation)
    public Point2D.Float getEllipsePoint(float angleDeg, double rM, double rm){
        if (!isFit) return null;

        double angRad = FastMath.toRadians(-angleDeg)+Math.PI/2+this.angrot;
        double ext = rM * FastMath.cos(angRad);
        double eyt = rm * FastMath.sin(angRad);

        double ex = this.xcen + ext*FastMath.cos(this.angrot)+eyt*FastMath.sin(this.angrot);
        double ey = this.ycen + -1*(-ext*FastMath.sin(this.angrot)+eyt*FastMath.cos(this.angrot));
        
        return new Point2D.Float((float)ex,(float)ey);

    }
    
    public Point2D.Float getEllipsePoint(float angleDeg){
        return this.getEllipsePoint(angleDeg,this.getRmax(),this.getRmin());
    }
    
    //retorna arraylist amb els punts d'una ellipse per un rang donat (0-360 per tota), step es icrement angular (-1 per default)
    //angleFin es respecte el zero, es podria fer també un altre amb increment...
    public ArrayList<Point2D.Float> getEllipsePoints(float angleIni, float angleFin, float step){
        if (!isFit) return null;
        ArrayList<Point2D.Float> fit = new ArrayList<Point2D.Float>();
//        if (step<0) step = (float) FastMath.toRadians(1);
        if (step<0) step = 0.05f;
        for (float i = angleIni; i<angleFin;i=(float) (i+step)){
          fit.add(getEllipsePoint(i));
        }
        return fit;
    }

    //fits an ellipse
    public void fitElli(){
        
        if (this.getEstimPoints().size()<=ElliFitMinPoints){
            log.warning("No enougth points to fit ellipse");
            return;
        }
        
        Iterator<Point2D.Float> itrp = this.getEstimPoints().iterator();
        double[] xr = new double[this.getEstimPoints().size()];
        double[] yr = new double[this.getEstimPoints().size()];
        int i = 0;
        while (itrp.hasNext()){
            Point2D.Float p = itrp.next();
            xr[i]=p.getX();
            yr[i]=p.getY();
            i = i + 1;
        }
        
        RealVector xv = new ArrayRealVector(xr);
        RealVector yv = new ArrayRealVector(yr);
        //xv.ebeMultiply(xv), xv.ebeMultiply(yv), yv.ebeMultiply(yv), xv, yv, new ArrayRealVector(xv.getDimension(), 1d);
        RealVector xvxv = xv.ebeMultiply(xv);
        RealVector xvyv = xv.ebeMultiply(yv);
        RealVector yvyv = yv.ebeMultiply(yv);
        
        //now we create the horizontal stack matrix:
        //6 cols, n rows
        RealMatrix D = new Array2DRowRealMatrix(xv.getDimension(), 6); 
        
        for(i = 0; i<xv.getDimension(); i++){
            xv.getEntry(i);
            double[] vals = new double[6];
            vals[0]=xvxv.getEntry(i);
            vals[1]=xvyv.getEntry(i);
            vals[2]=yvyv.getEntry(i);
            vals[3]=xv.getEntry(i);
            vals[4]=yv.getEntry(i);
            vals[5]=1d;
            
            RealVector row = new ArrayRealVector(vals);
            D.setRowVector(i, row);
        }
        
        log.fine("xv="+xv);
        log.fine("yv="+yv);
        log.fine("D="+D);
        
        RealMatrix S = D.transpose().multiply(D);
        
        //zero 6x6 matrix
        RealVector zero = new ArrayRealVector(6);
        RealMatrix C = new Array2DRowRealMatrix(6, 6);
        C.setRowVector(0, zero);
        C.setRowVector(1, zero);
        C.setRowVector(2, zero);
        C.setRowVector(3, zero);
        C.setRowVector(4, zero);
        C.setRowVector(5, zero);
        
        C.setEntry(2, 0, 2);
        C.setEntry(0, 2, 2);
        C.setEntry(1, 1, -1);
        
        try{
            EigenDecomposition EV = new EigenDecomposition(MatrixUtils.inverse(S).multiply(C)); 
            RealVector eigenvalues = new ArrayRealVector(EV.getRealEigenvalues());
            int index = -1;
            if (eigenvalues.getMaxValue()>Math.abs(eigenvalues.getMinValue())){
                index = eigenvalues.getMaxIndex();
            }else{
                index = eigenvalues.getMinIndex();
            }
            
            RealVector sol = EV.getEigenvector(index);
            log.fine("sol="+sol);
            
            double a = sol.getEntry(0);
            double b = sol.getEntry(1)/2;
            double c = sol.getEntry(2);
            double d = sol.getEntry(3)/2;
            double e = sol.getEntry(4)/2;
            double f = sol.getEntry(5);
            
            this.setPars(a,b,c,d,e,f); //AQUI DINS ES POSA EL ISFIT a TRUE SI TOT HA ANAT BE
            
        }catch(Exception e){
            log.warning("Error during ellipse fitting");
        }

    }
    
    public double getDistTofocus() {
        //c**2 = a**2 -b**2
        return FastMath.sqrt(this.getRmax()*this.getRmax()-this.getRmin()*this.getRmin());
    }
    
    public Point2D.Float getF1(double rotRad) {
        //first focus considering ROT
        float c = (float) this.getDistTofocus();
        float f1x = (float) (this.getXcen() + c * FastMath.sin(rotRad));
        float f1y = (float) (this.getYcen() + c * FastMath.cos(rotRad));
        return new Point2D.Float(f1x, f1y);
    }
    
    public Point2D.Float getF2(double rotRad) {
        //first focus considering ROT
        float c = (float) this.getDistTofocus();
        float f1x = (float) (this.getXcen() - c * FastMath.sin(rotRad));
        float f1y = (float) (this.getYcen() - c * FastMath.cos(rotRad));
        return new Point2D.Float(f1x, f1y);
    }
    
    public double getA() {
        return a;
    }

    public void setA(double a) {
        this.a = a;
    }

    public double getB() {
        return b;
    }

    public void setB(double b) {
        this.b = b;
    }

    public double getC() {
        return c;
    }

    public void setC(double c) {
        this.c = c;
    }

    public double getD() {
        return d;
    }

    public void setD(double d) {
        this.d = d;
    }

    public double getE() {
        return e;
    }

    public void setE(double e) {
        this.e = e;
    }

    public double getF() {
        return f;
    }

    public void setF(double f) {
        this.f = f;
    }

    public double getRmin() {
        return rmin;
    }

    public void setRmin(double r) {
        this.rmin = r;
    }

    public double getRmax() {
        return rmaj;
    }

    public void setRmax(double r) {
        this.rmaj = r;
    }

    public double getAngrot() {
        return angrot;
    }

    public void setAngrot(double angrot) {
        this.angrot = angrot;
    }

    public double getXcen() {
        return xcen;
    }

    public void setXcen(double xcen) {
        this.xcen = xcen;
    }

    public double getYcen() {
        return ycen;
    }

    public void setYcen(double ycen) {
        this.ycen = ycen;
    }

    public ArrayList<Point2D.Float> getEstimPoints() {
        return estimPoints;
    }
    
    public void addEstimPoint(Point2D.Float p) {
        this.getEstimPoints().add(p);
    }
    
    public void setEstimPoints(ArrayList<Point2D.Float> pointlist) {
        this.estimPoints=pointlist;
    }

    public boolean isFit() {
        return isFit;
    }

    public int getLab6ring() {
        return lab6ring;
    }

    public void setLab6ring(int lab6ring) {
        this.lab6ring = lab6ring;
    }
}