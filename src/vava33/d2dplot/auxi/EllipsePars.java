package vava33.d2dplot.auxi;

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

import vava33.d2dplot.D2Dplot_global;
import vava33.d2dplot.MainFrame;

import com.vava33.jutils.VavaLogger;


public class EllipsePars {
    private double a,b,c,d,e,f;
    private double rmaj,rmin; //radi major i menor de l'ellipse
    private double angrot;  //respecte rmax, zero a les "12h" i positiu horari EN RADIANS
    private double xcen, ycen;
    private ArrayList<Point2D.Float> estimPoints;
    private boolean isFit;
    private int lab6ring;
    private static int ElliFitMinPoints = 15;
    private static VavaLogger log = D2Dplot_global.log;

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
        float angr = (float) (0.5*Math.atan(2*b/(a-c)));
        
        this.isFit = true;
        
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
         * 
         */
        
        if (rH>rV){ //vol dir que nomes hem de restar 90º
            this.rmaj = rH;
            this.rmin = rV;
            this.angrot = angr + (Math.PI/2);
            //corregim si passem de 360º
            if (this.angrot > (2*Math.PI)){
                this.angrot = this.angrot - (2*Math.PI);
            }
        }else{ //angr ens dona directament la variacio de rmaj respecte la vertical
            this.rmaj = rV;
            this.rmin = rH;
            this.angrot = angr;
        }
        
        //i si ho dona respecte rV des de la vertical? es equivalent?
//          if (rV>rH){ //angrot esta correcte
//              this.rmaj = rV;
//              this.rmin = rH;
//              this.angrot = angr;
//          }else{ //hem de sumar 90º a angr
//              this.rmaj = rH;
//              this.rmin = rV;
//              this.angrot = angr + (Math.PI/2);
//          }
       
        
    }
    
    public void printElliPars(){
        System.out.println("coefs (a b c d e f)="+this.a+" "+this.b+" "+this.c+" "+this.d+" "+this.e+" "+this.f);
        System.out.println("centre (x y)="+this.xcen+" "+this.ycen);
        System.out.println("axes (maj min)="+this.rmaj+" "+this.rmin);
        System.out.println("rot="+this.angrot+" (deg="+FastMath.toDegrees(this.angrot)+")");            
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
    public Point2D.Float getEllipsePoint(float angle, double rM, double rm){
        if (!isFit) return null;

        float zero = (float) ((-this.angrot) -(Math.PI/2)); //value of the vertical zero according d2dplot convention and the parametric eq below
//        float zero = (float) (-this.angrot -(Math.PI/2)); //value of the vertical zero according d2dplot convention and the parametric eq below
//        float zero = (float) ((-1)*this.angrot);
//        float zero = 0;
        float drawpoint = (float) (zero + FastMath.toRadians(angle));
        float ex = (float) (this.xcen + rm*Math.cos(drawpoint)*Math.cos(this.angrot) - rM*Math.sin(drawpoint)*Math.sin(this.angrot));
        float ey = (float) (this.ycen + rm*Math.cos(drawpoint)*Math.sin(this.angrot) + rM*Math.sin(drawpoint)*Math.cos(this.angrot));
        return new Point2D.Float(ex,ey);
    }
    
    public Point2D.Float getEllipsePoint(float angle){
        return this.getEllipsePoint(angle,this.getRmax(),this.getRmin());
    }
    
    //retorna arraylist amb els punts d'una ellipse per un rang donat (0-360 per tota), step es icrement angular (-1 per default)
    //angleFin es respecte el zero, es podria fer també un altre amb increment...
    public ArrayList<Point2D.Float> getEllipsePoints(float angleIni, float angleFin, float step){
        if (!isFit) return null;
        ArrayList<Point2D.Float> fit = new ArrayList<Point2D.Float>();
        if (step<0) step = (float) FastMath.toRadians(1);
        for (float i = angleIni; i<angleFin;i=(float) (i+step)){
          fit.add(getEllipsePoint(i));
        }
        return fit;
        
        //            float zero = (float) (-this.angrot -(Math.PI/2)); //value of the vertical (zero according d2dplot convention)
//        float drawIni = (float) (zero + FastMath.toRadians(angleIni));
//        float drawFin = (float) (zero + FastMath.toRadians(angleFin));
//        if (step<0) step = (float) FastMath.toRadians(1);
//        for (float i = drawIni; i<drawFin;i=(float) (i+step)){
//            
//            float ex = (float) (x0 + res1*Math.cos(i)*Math.cos(angrot) - res2*Math.sin(i)*Math.sin(angrot));
//            float ey = (float) (y0 + res1*Math.cos(i)*Math.sin(angrot) + res2*Math.sin(i)*Math.cos(angrot));
//            fit.add(new Point2D.Float(ex,ey));
//        }
    }

    //fits an ellipse
    public void fitElli(){
        
        if (this.getEstimPoints().size()<ElliFitMinPoints){
            log.info("no enougth points to fit ellipse");
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
        
        System.out.println(xv);
        System.out.println(yv);
        System.out.println(D);
        
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
            System.out.println(sol);
            
            double a = sol.getEntry(0);
            double b = sol.getEntry(1)/2;
            double c = sol.getEntry(2);
            double d = sol.getEntry(3)/2;
            double e = sol.getEntry(4)/2;
            double f = sol.getEntry(5);
            
            this.setPars(a,b,c,d,e,f);
            
        }catch(Exception e){
            log.info("Error during ellipse fitting");
        }

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
    