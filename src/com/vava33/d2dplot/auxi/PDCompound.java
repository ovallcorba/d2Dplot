package vava33.d2dplot.auxi;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

import org.apache.commons.math3.util.FastMath;

import com.vava33.jutils.VavaLogger;

import vava33.d2dplot.D2Dplot_global;

public class PDCompound {
    
//    private int cnumber; //compound number in the DB
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
    private ArrayList<PDReflection> peaks;
    
    private static VavaLogger log = D2Dplot_global.getVavaLogger(PDCompound.class.getName());
    
    public PDCompound(String name){
        this.compName = new ArrayList<String>();
        this.compName.add(name);
        this.comment = new ArrayList<String>();
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
    
    public String toString(){
        String altnames = this.getAltNames();
        if (!altnames.isEmpty()){
            return String.format("%s [%s] (aka: %s)", this.getCompName().get(0), this.getFormula(), this.getAltNames());    
        }else{
            return String.format("%s [%s]", this.getCompName().get(0), this.getFormula());
        }
    }
    
    public String toStringNameFormula(){
        return String.format("%s [%s]", this.getCompName().get(0), this.getFormula());
    }
    
    public String printInfoLine(){
        return String.format("%s: %.4f %.4f %.4f %.3f %.3f %.3f (s.g. %s)", this.getCompName().get(0), this.getA(),this.getB(),this.getC(), this.getAlfa(),this.getBeta(),this.getGamma(),this.getSpaceGroup());
    }
    
    public String printInfo2Line(){
        String altnames = this.getAltNames();
        if (!altnames.isEmpty()){
            return String.format("-- %s (aka: %s)\n"
                    + "cell: %.4f %.4f %.4f %.3f %.3f %.3f (s.g. %s)", 
                    this.getCompName().get(0), this.getAltNames(),
                    this.getA(),this.getB(),this.getC(), this.getAlfa(),this.getBeta(),this.getGamma(),this.getSpaceGroup());
        }else{
            return String.format("-- %s\n"
                    + "   cell: %.4f %.4f %.4f %.3f %.3f %.3f (s.g. %s)", 
                    this.getCompName().get(0),
                    this.getA(),this.getB(),this.getC(), this.getAlfa(),this.getBeta(),this.getGamma(),this.getSpaceGroup());
        }
    }
    
    public String printInfoMultipleLines(){
        StringBuilder sb = new StringBuilder();
        sb.append(String.format(" %s [%s]\n", this.getCompName().get(0),this.getFormula()));
        String altnames = this.getAltNames();
        if (!altnames.isEmpty())sb.append(String.format(" Other names: %s\n", altnames));
        sb.append(String.format(" Cell: %.4f %.4f %.4f %.3f %.3f %.3f (s.g. %s)\n", this.getA(),this.getB(),this.getC(), this.getAlfa(),this.getBeta(),this.getGamma(),this.getSpaceGroup()));
        if (!this.getReference().isEmpty()) {
            sb.append(String.format(" Reference: %s\n", this.getReference()));
        }   
        String comments = this.getAllComments();
        if (!comments.isEmpty())sb.append(String.format(" Comments: %s\n", comments));
        sb.append(" Reflection list:\n");
        
        sb.append(this.getHKLlines());
        
        return sb.toString();
    }
    
    public String getHKLlines(){
        StringBuilder sb = new StringBuilder();
        for (int i=0; i<this.getPeaks().size();i++){
            int h = this.getPeaks().get(i).getH();
            int k = this.getPeaks().get(i).getK();
            int l = this.getPeaks().get(i).getL();
            float dsp = this.getPeaks().get(i).getDsp();
            float inten = this.getPeaks().get(i).getInten();
            sb.append(String.format("%3d %3d %3d %9.5f %7.2f\n",h,k,l,dsp,inten));
          }
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
    
    @SuppressWarnings("unused")
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

        index = Math.abs(index) - 1; //ara apunta al seguent valor, mes petit
        if (index == 0){
            return 0;
        }
        if ((index == this.getPeaks().size())||(index == (this.getPeaks().size()-1))){
            return this.getPeaks().size()-1;
        }
      
        //index -1 sempre > que valor, mentre que index sempre < que valor
        float afterdiff = this.getPeaks().get(index-1).getDsp() - dsp;
        float beforediff = dsp - this.getPeaks().get(index).getDsp();
        if (afterdiff < beforediff){
            return index-1;
        }else{
            return index;
        }
    }
    
    //for a given wavelenght calculate the 2Theta of every reflection    
    public void calcDSPfromHKL(){
        //constants reticulars recíproques:
        float al = (float) FastMath.toRadians(this.getAlfa());
        float be = (float) FastMath.toRadians(this.getBeta());
        float ga = (float) FastMath.toRadians(this.getGamma());
        float D2 = (float) (1.f - FastMath.pow(FastMath.cos(al), 2) - 
                FastMath.pow(FastMath.cos(be), 2) -
                FastMath.pow(FastMath.cos(ga), 2) +
                2.f * FastMath.cos(al) * FastMath.cos(be) * FastMath.cos(ga));
        float CR11 = (float) (FastMath.pow(FastMath.sin(al), 2) / (D2*this.getA()*this.getA()));
        float CR22 = (float) (FastMath.pow(FastMath.sin(be), 2) / (D2*this.getB()*this.getB()));
        float CR33 = (float) (FastMath.pow(FastMath.sin(ga), 2) / (D2*this.getC()*this.getC()));
        float CR12 = (float) ((FastMath.cos(al) * FastMath.cos(be) - FastMath.cos(ga)) / (this.getA()*this.getB()*D2));
        float CR13 = (float) ((FastMath.cos(al) * FastMath.cos(ga) - FastMath.cos(be)) / (this.getA()*this.getC()*D2));
        float CR23 = (float) ((FastMath.cos(be) * FastMath.cos(ga) - FastMath.cos(al)) / (this.getB()*this.getC()*D2));
        
        Iterator<PDReflection> it = this.getPeaks().iterator();
        while (it.hasNext()){
            PDReflection r = it.next();
            float d2hkl = r.getH()*r.getH()*CR11 + r.getK()*r.getK()*CR22 + r.getL()*r.getL()*CR33 + 2*r.getH()*r.getK()*CR12 + 2*r.getK()*r.getL()*CR23 + 2*r.getH()*r.getL()*CR13;
            r.setDsp((float) FastMath.sqrt(1.f/d2hkl));
            log.writeNameNums("CONFIG", true, "h k l dsp", r.getH(),r.getK(),r.getL(),r.getDsp());
        }
    }

}
