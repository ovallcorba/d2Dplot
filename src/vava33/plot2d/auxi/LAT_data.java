package vava33.plot2d.auxi;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.commons.math3.util.FastMath;

import com.vava33.jutils.VavaLogger;

public class LAT_data {
    
    //inbuild files
//    private static final String lauFile = "laumontite.lat";
//    private static final String diopFile = "diopsid.lat";
//    private static final String IleFile = "ilerdite.lat";
//    private static final String AerFile = "aerinite.lat";
//    private static final String AxiFile = "axinite.lat";
//    private static final String PreFile = "prehnite.lat";
    private static final String[] defaultDataFiles = {"laumontite.lat","diopside.lat","ilerdite.lat","aerinite.lat","axinite.lat","prehnite.lat"};
    private static final String defaultDataFilesPath = "/latFiles/";
    private static boolean defaultDataRead = false;  //once read, it becomes true and no need to read them again

    public class HKL_reflection{
        public int h;
        public int k;
        public int l;
        public float t2;
        
        public HKL_reflection(int h,int k, int l){
            this.h=h;
            this.k=k;
            this.l=l;
            this.t2=-1;
        }

        public float getT2() {
            return t2;
        }

        public void setT2(float t2) {
            this.t2 = t2;
        }
    }
    
    private ArrayList<HKL_reflection> ref;
    private String name;
    private float a,b,c,alpha,beta,gamma;
    

    public LAT_data(String CompoundName){
            this.name = CompoundName;
            this.ref = new ArrayList<HKL_reflection>();
    }
        
    
    public String getName() {
        return name;
    }


    public boolean readinternalResourceLATfile(String latfileName){
        VavaLogger.LOG.info("reading rings for "+this.getName()+" in (resource) "+latfileName.toString());
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(LAT_data.class.getResourceAsStream(defaultDataFilesPath+latfileName)));
            String line = null;
            //first line is the cell
            line = reader.readLine();
            String cell[] = line.trim().split("\\s+");
            if (cell.length>=6){
                this.a = Float.parseFloat(cell[0]);
                this.b = Float.parseFloat(cell[1]);
                this.c = Float.parseFloat(cell[2]);
                this.alpha = Float.parseFloat(cell[3]);
                this.beta = Float.parseFloat(cell[4]);
                this.gamma = Float.parseFloat(cell[5]);
            }
            //reflections --> we do not read 2theta (wavelength dependent)
            while ((line = reader.readLine()) != null) {
              String hkl[] = line.trim().split("\\s+");
              int h = Integer.parseInt(hkl[0]);
              int k = Integer.parseInt(hkl[1]);
              int l = Integer.parseInt(hkl[2]);
              HKL_reflection r = new HKL_reflection(h,k,l);
//              if (hkl.length>=4){
//                  //llegim 2theta
//                  float t2 = Float.parseFloat(hkl[3]);
//                  r.setT2(t2);
//              }
              this.ref.add(r);
            }
            reader.close();
        }catch(Exception e){
            e.printStackTrace();
            return false;
        }
        return true;
    }
    
    
    public boolean readLATfile(File latfile){
        VavaLogger.LOG.info("reading rings for "+this.getName()+" in "+latfile.toString());
        try {
            BufferedReader reader = new BufferedReader(new FileReader(latfile));
            String line = null;
            //first line is the cell
            line = reader.readLine();
            String cell[] = line.trim().split("\\s+");
            if (cell.length>=6){
                this.a = Float.parseFloat(cell[0]);
                this.b = Float.parseFloat(cell[1]);
                this.c = Float.parseFloat(cell[2]);
                this.alpha = Float.parseFloat(cell[3]);
                this.beta = Float.parseFloat(cell[4]);
                this.gamma = Float.parseFloat(cell[5]);
            }
            //reflections --> we do not read 2theta (wavelength dependent)
            while ((line = reader.readLine()) != null) {
              String hkl[] = line.trim().split("\\s+");
              int h = Integer.parseInt(hkl[0]);
              int k = Integer.parseInt(hkl[1]);
              int l = Integer.parseInt(hkl[2]);
              HKL_reflection r = new HKL_reflection(h,k,l);
//              if (hkl.length>=4){
//                  //llegim 2theta
//                  float t2 = Float.parseFloat(hkl[3]);
//                  r.setT2(t2);
//              }
              this.ref.add(r);
            }
            reader.close();
        }catch(Exception e){
            e.printStackTrace();
            return false;
        }
        return true;
    }
    
    
    //for a given wavelenght calculate the 2Theta of every reflection    
    public void calc2T(float wavelength){
        //constants reticulars recíproques:
        float al = (float) FastMath.toRadians(this.alpha);
        float be = (float) FastMath.toRadians(this.beta);
        float ga = (float) FastMath.toRadians(this.gamma);
        float D2 = (float) (1.f - FastMath.pow(FastMath.cos(al), 2) - 
                FastMath.pow(FastMath.cos(be), 2) -
                FastMath.pow(FastMath.cos(ga), 2) +
                2.f * FastMath.cos(al) * FastMath.cos(be) * FastMath.cos(ga));
        float CR11 = (float) (FastMath.pow(FastMath.sin(al), 2) / (D2*this.a*this.a));
        float CR22 = (float) (FastMath.pow(FastMath.sin(be), 2) / (D2*this.b*this.b));
        float CR33 = (float) (FastMath.pow(FastMath.sin(ga), 2) / (D2*this.c*this.c));
        float CR12 = (float) ((FastMath.cos(al) * FastMath.cos(be) - FastMath.cos(ga)) / (this.a*this.b*D2));
        float CR13 = (float) ((FastMath.cos(al) * FastMath.cos(ga) - FastMath.cos(be)) / (this.a*this.c*D2));
        float CR23 = (float) ((FastMath.cos(be) * FastMath.cos(ga) - FastMath.cos(al)) / (this.b*this.c*D2));

        
        Iterator<HKL_reflection> it = ref.iterator();
        while (it.hasNext()){
            HKL_reflection r = it.next();
            float d2hkl = r.h*r.h*CR11 + r.k*r.k*CR22 + r.l*r.l*CR33 + 2*r.h*r.k*CR12 + 2*r.k*r.l*CR23 + 2*r.h*r.l*CR13;
            float dhkl = (float) FastMath.sqrt(1.f/d2hkl);
            r.t2 = (float) (2 * FastMath.toDegrees(FastMath.asin(wavelength / (2*dhkl)))); //RADIANTS?
        }
        it = ref.iterator();
        while (it.hasNext()){
            HKL_reflection r = it.next();
            VavaLogger.LOG.info(r.h+" "+r.k+" "+r.l+" "+r.t2);
        }
    }
    
    public Iterator<HKL_reflection> getHKLIterator(){
        return this.ref.iterator();
    }
    
    public ArrayList<HKL_reflection> getHKLlist(){
        return this.ref;
    }


    public static boolean isDefaultDataRead() {
        return defaultDataRead;
    }


    public static void setDefaultDataRead(boolean defaultDataRead) {
        LAT_data.defaultDataRead = defaultDataRead;
    }


    public static String[] getDefaultdatafiles() {
        return defaultDataFiles;
    }


    public static String getDefaultdatafilespath() {
        return defaultDataFilesPath;
    }
    
    public String toString(){
        return this.getName();
    }
    
}


