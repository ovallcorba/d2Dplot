package vava33.plot2d.auxi;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.math3.util.FastMath;

public class PDCompound {
    
    private int codCODE;
    private int ov_number;
    private String compName;
    private float a;
    private float b;
    private float c;
    private float alfa;
    private float beta;
    private float gamma;
    private String spaceGroup;
    private String formula;
    private ArrayList<Float> dspacings;
    private ArrayList<Float> intensities;
    
    
    public PDCompound(String name){
        this.compName = name;
        this.dspacings = new ArrayList<Float>();
        this.intensities = new ArrayList<Float>();
    }
    
    public PDCompound(String name, float a, float b, float c, float al, float be, float ga, String sg, String elem, ArrayList<Float> dsp, ArrayList<Float> inten){
        this.a = a;
        this.b = b;
        this.c = c;
        this.alfa = al;
        this.beta = be;
        this.gamma = ga;
        this.spaceGroup = sg;
        this.formula = elem;
        this.dspacings = dsp;
        this.intensities = inten;
    }
    
    public PDCompound(String name, float a, float b, float c, float al, float be, float ga, String sg, String elem){
        this.dspacings = new ArrayList<Float>();
        this.intensities = new ArrayList<Float>();
        this.a = a;
        this.b = b;
        this.c = c;
        this.alfa = al;
        this.beta = be;
        this.gamma = ga;
        this.spaceGroup = sg;
        this.formula = elem;
    }
    
    public String printCompound(){
        //return String.format("%d %s %s", this.getOv_number(), this.getCompName(), this.getFormula());
        return String.format("%6d %s [%s]", this.getOv_number(), this.getCompName(), this.getFormula());
    }
    
    public String printInfoMultipleLines(){
        //return String.format("%d %s %s", this.getOv_number(), this.getCompName(), this.getFormula());
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("--- RefNum: %d  COD_code: %d ---\n", this.getOv_number(),this.getCodCODE()));
        sb.append(String.format(" Name:     %s\n", this.getCompName()));
        sb.append(String.format(" Formula:  %s\n", this.getFormula()));
        sb.append(String.format(" CellPars: %.4f %.4f %.4f %.3f %.3f %.3f\n", this.getA(),this.getB(),this.getC(), this.getAlfa(),this.getBeta(),this.getGamma()));
        sb.append(String.format(" SGroup:   %s\n", this.getSpaceGroup()));
        sb.append(String.format("-------\n", this.getSpaceGroup()));
        return sb.toString();
    }
    
    public int getNrRefUpToDspacing(float dspacing){
        float tolerance = 0.05f;
        Iterator<Float> itref = this.getDspacings().iterator();
        int nref = 0;
        while (itref.hasNext()){
            float r = itref.next();
            if (r>=(dspacing-tolerance)){
                nref = nref + 1;
            }
        }
        return nref;
    }
    
    //return the maximum intensity of the first npeaks of the compound
    public float getMaxInten(int npeaks){
//        List<Float> sub = this.getIntensities().subList(0, npeaks-1);
//        return Collections.max(sub);
        Iterator<Float> it = this.getIntensities().iterator();
        int count = 0;
        float maxI = -1;
        while ((it.hasNext()) && (count < npeaks)){
            float cI = it.next();
            if (cI > maxI){maxI=cI;}
            count = count + 1;
        }
        return maxI;
    }

    public String getCompName() {
        return compName;
    }

    public void setCompName(String compName) {
        this.compName = compName;
    }

    public float getA() {
        return a;
    }

    public void setA(float a) {
        this.a = a;
    }

    public float getB() {
        return b;
    }

    public void setB(float b) {
        this.b = b;
    }

    public float getC() {
        return c;
    }

    public void setC(float c) {
        this.c = c;
    }

    public float getAlfa() {
        return alfa;
    }

    public void setAlfa(float alfa) {
        this.alfa = alfa;
    }

    public float getBeta() {
        return beta;
    }

    public void setBeta(float beta) {
        this.beta = beta;
    }

    public float getGamma() {
        return gamma;
    }

    public void setGamma(float gamma) {
        this.gamma = gamma;
    }

    public String getSpaceGroup() {
        return spaceGroup;
    }

    public void setSpaceGroup(String spaceGroup) {
        this.spaceGroup = spaceGroup;
    }

    public String getFormula() {
        return formula;
    }

    public void setFormula(String formula) {
        this.formula = formula;
    }

    public ArrayList<Float> getDspacings() {
        return dspacings;
    }

    public void setDspacings(ArrayList<Float> dspacings) {
        this.dspacings = dspacings;
    }

    public ArrayList<Float> getIntensities() {
        return intensities;
    }

    public void setIntensities(ArrayList<Float> intensities) {
        this.intensities = intensities;
    }

    public int getCodCODE() {
        return codCODE;
    }

    public void setCodCODE(int codCODE) {
        this.codCODE = codCODE;
    }

    public int getOv_number() {
        return ov_number;
    }

    public void setOv_number(int ov_number) {
        this.ov_number = ov_number;
    }
    
    
}
