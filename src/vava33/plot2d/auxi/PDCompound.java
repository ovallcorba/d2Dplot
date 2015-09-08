package vava33.plot2d.auxi;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.math3.util.FastMath;

public class PDCompound {
    
    private int cnumber; //compound number in the DB
    private ArrayList<String> compName;
    private float a;
    private float b;
    private float c;
    private float alfa;
    private float beta;
    private float gamma;
    private String spaceGroup;
    private String formula;
    private String reference;
    private ArrayList<String> comment;
//    private ArrayList<Float> dspacings;
//    private ArrayList<Float> intensities;
    private ArrayList<PDReflection> peaks;
    
    
    public PDCompound(String name){
        this.compName = new ArrayList<String>();
        this.compName.add(name);
        this.comment = new ArrayList<String>();
//        this.dspacings = new ArrayList<Float>();
//        this.intensities = new ArrayList<Float>();
        this.peaks = new ArrayList<PDReflection>();
        spaceGroup="";
        formula="";
        reference="";
        a=0;
        b=0;
        c=0;
        alfa=0;
        beta=0;
        gamma=0;
    }
    
    public PDCompound(String name, float a, float b, float c, float al, float be, float ga, String sg, String elem, ArrayList<PDReflection> pks){
        this(name);
        this.a = a;
        this.b = b;
        this.c = c;
        this.alfa = al;
        this.beta = be;
        this.gamma = ga;
        this.spaceGroup = sg;
        this.formula = elem;
        this.peaks = pks;
//        this.dspacings = dsp;
//        this.intensities = inten;
    }
    
    public PDCompound(String name, float a, float b, float c, float al, float be, float ga, String sg, String elem){
        this(name);
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
        return String.format("%6d %s [%s]", this.getCnumber(), this.getCompName().get(0), this.getFormula());
    }
    
    public String printInfoLine(){
        return String.format("#%d: %.4f %.4f %.4f %.3f %.3f %.3f (s.g. %s)", this.getCnumber(), this.getA(),this.getB(),this.getC(), this.getAlfa(),this.getBeta(),this.getGamma(),this.getSpaceGroup());
    }
    
    //TODO: REESTILITZAR
    public String printInfoMultipleLines(){
        //return String.format("%d %s %s", this.getOv_number(), this.getCompName(), this.getFormula());
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("--- Compound num. %d  ---\n", this.getCnumber()));
        sb.append(String.format(" Name:     %s\n", this.getCompName().get(0)));
        String altnames = this.getAltNames();
        if (!altnames.isEmpty())sb.append(String.format(" NameAlt:     %s\n", altnames));
        sb.append(String.format(" Formula:  %s\n", this.getFormula()));
        sb.append(String.format(" CellPars: %.4f %.4f %.4f %.3f %.3f %.3f\n", this.getA(),this.getB(),this.getC(), this.getAlfa(),this.getBeta(),this.getGamma()));
        sb.append(String.format(" SGroup:   %s\n", this.getSpaceGroup()));
        if (!this.getReference().isEmpty()) {
            sb.append(String.format(" Reference:     %s\n", this.getReference()));
        }        
        if (!this.getComment().isEmpty()) {
            int index = 0;
            while (index < getComment().size()){
                sb.append(String.format(" Comment:     %s\n", this.getComment().get(index)));
                index = index + 1;
            }
        }                
        if (!this.getComment().isEmpty()) {
            int index = 0;
            while (index < getComment().size()){
                sb.append(String.format(" Comment:     %s\n", this.getComment().get(index)));
                index = index + 1;
            }
        }        
        sb.append(String.format("-------\n"));
        return sb.toString();
    }
    
    public String getCellParameters(){
        return String.format("%.4f %.4f %.4f %.3f %.3f %.3f", this.getA(),this.getB(),this.getC(), this.getAlfa(),this.getBeta(),this.getGamma());

    }
    
    public int getNrRefUpToDspacing(float dspacing){
        float tolerance = 0.05f;
        Iterator<PDReflection> itpks = this.getPeaks().iterator();
        int nref = 0;
        while (itpks.hasNext()){
            PDReflection pk = itpks.next();
            float r = pk.getDsp();
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
        Iterator<PDReflection> itpks = this.getPeaks().iterator();
        int count = 0;
        float maxI = -1;
        while ((itpks.hasNext()) && (count < npeaks)){
            PDReflection pk = itpks.next();
            float cI = pk.getInten();
            if (cI > maxI){maxI=cI;}
            count = count + 1;
        }
        return maxI;
    }

    public ArrayList<String> getCompName() {
        return compName;
    }

    public void addCompoundName(String name){
        this.getCompName().add(name);
    }

    public String getAltNames(){
        StringBuilder sb = new StringBuilder();
        if (this.getCompName().size()>1){
            int index = 1;
            while (index < getCompName().size()){
                sb.append(this.getCompName().get(index));
                sb.append(" ");
                index = index + 1;
            }
        }
        return sb.toString().trim();
    }
    
    public String getAllComments(){
        StringBuilder sb = new StringBuilder();
        if (this.getComment().size()>1){
            int index = 1;
            while (index < getComment().size()){
                sb.append(this.getComment().get(index));
                sb.append(" ");
                index = index + 1;
            }
        }
        return sb.toString().trim();
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

    public ArrayList<PDReflection> getPeaks() {
        return peaks;
    }

    public void setPeaks(ArrayList<PDReflection> peaks) {
        this.peaks = peaks;
    }
    
    public void addPeak(int h, int k, int l, float dsp, float inten){
        this.getPeaks().add(new PDReflection(h,k,l,dsp,inten));
    }

    private ArrayList<Float> getDspacings(){
        ArrayList<Float> dsp = new ArrayList<Float>();
        Iterator<PDReflection> itpks = this.getPeaks().iterator();
        while (itpks.hasNext()){
            PDReflection ref = itpks.next();
            dsp.add(ref.getDsp());
        }
        return dsp;
    }
    
    private ArrayList<Float> getIntensities(){
        ArrayList<Float> inten = new ArrayList<Float>();
        Iterator<PDReflection> itpks = this.getPeaks().iterator();
        while (itpks.hasNext()){
            PDReflection ref = itpks.next();
            inten.add(ref.getInten());
        }
        return inten;
    }
    
    public String getDspacingsString(){
        StringBuilder sb = new StringBuilder();
        Iterator<PDReflection> itpks = this.getPeaks().iterator();
        while (itpks.hasNext()){
            PDReflection ref = itpks.next();
            sb.append(String.format("%.5f ", ref.getDsp()));
        }
        return sb.toString().trim();
    }
    
    public String getIntensitiesString(){
        StringBuilder sb = new StringBuilder();
        Iterator<PDReflection> itpks = this.getPeaks().iterator();
        while (itpks.hasNext()){
            PDReflection ref = itpks.next();
            sb.append(String.format("%.2f ", ref.getInten()));
        }
        return sb.toString().trim();
    }
    
    
    public int getCnumber() {
        return cnumber;
    }

    public void setCnumber(int cnumber) {
        this.cnumber = cnumber;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public ArrayList<String> getComment() {
        return comment;
    }
  
    public void addComent(String comment){
        this.getComment().add(comment);
    }
    
    //returns the closest peak of the compound to a given dspacing
    public int closestPeak(float dsp){
        
        int index = Collections.binarySearch(this.getDspacings(),dsp,Collections.reverseOrder());
//      VavaLogger.LOG.info(pklist.toString());
//      VavaLogger.LOG.info("peak="+peak+" indexFound="+index);

        index = Math.abs(index) - 1; //ara apunta al seguent valor, mes petit
        if (index == 0){
            return 0;
        }
        if ((index == this.getPeaks().size())||(index == (this.getPeaks().size()-1))){
            return this.getPeaks().size()-1;
        }
        
        //sempre estara entre index i index-1 (havent corregit ja un cop el -1)
//      VavaLogger.LOG.info("peak="+peak+" index="+pklist.get(index)+" index-1="+pklist.get(index-1));
      
        //index -1 sempre > que valor, mentre que index sempre < que valor
        float afterdiff = this.getPeaks().get(index-1).getDsp() - dsp;
        float beforediff = dsp - this.getPeaks().get(index).getDsp();
        if (afterdiff < beforediff){
            return index-1;
        }else{
            return index;
        }
    }
}
