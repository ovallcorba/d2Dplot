package vava33.plot2d.auxi;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.Scanner;
import java.util.zip.ZipFile;

import javax.swing.SwingWorker;

import com.vava33.jutils.VavaLogger;

public final class PDDatabase {

    private static int nCompounds = 0;
    private static ArrayList<PDCompound> compList = new ArrayList<PDCompound>();
    private static ArrayList<SearchResult> searchresults = new ArrayList<SearchResult>();
    
    public static class SearchResult implements Comparable<SearchResult>{
        
        private PDCompound c;
        private float residual_positions;
        private float residual_intensities;
        
        public SearchResult(PDCompound comp, float res, float resInten){
            this.c=comp;
            this.residual_positions=res;
            this.residual_intensities=resInten;
        }
        
        public float getResidual() {
            return residual_positions;
        }
        public void setResidual(float residual) {
            this.residual_positions = residual;
        }
        public float getResidual_intensities() {
            return residual_intensities;
        }

        public void setResidual_intensities(float residual_intensities) {
            this.residual_intensities = residual_intensities;
        }

        public PDCompound getC() {
            return c;
        }
        public void setC(PDCompound c) {
            this.c = c;
        }

        @Override
        public int compareTo(SearchResult arg0) {
            float comparedR = arg0.getResidual();
            float diff = this.residual_positions - comparedR;
            if (diff > 0) return 1;
            if (diff < 0) return -1;
            return 0;
        }
    }
    
    
    public static void reset(){
        compList.clear();
        nCompounds = 0;
    }
    
    public static void addCompound(PDCompound c){
        compList.add(c);
        nCompounds = nCompounds + 1;
    }
    


    public static PDCompound get_compound_from_ovNum(int num){
        Iterator<PDCompound> it = compList.iterator();
        while (it.hasNext()){
            PDCompound c = it.next();
            if (c.getOv_number() == num){
                return c;
            }
        }
        return null;
    }
    
    public static int getnCompounds() {
        return nCompounds;
    }

    public static void setnCompounds(int nCompounds) {
        PDDatabase.nCompounds = nCompounds;
    }

    public static ArrayList<PDCompound> getCompList() {
        return compList;
    }

    public static void setCompList(ArrayList<PDCompound> compList) {
        PDDatabase.compList = compList;
    }
    
    public static int countLines(String filename) throws IOException {
        InputStream is = new BufferedInputStream(new FileInputStream(filename));
        try {
            byte[] c = new byte[1024];
            int count = 0;
            int readChars = 0;
            boolean empty = true;
            while ((readChars = is.read(c)) != -1) {
                empty = false;
                for (int i = 0; i < readChars; ++i) {
                    if (c[i] == '\n') {
                        ++count;
                    }
                }
            }
            return (count == 0 && !empty) ? 1 : count;
        } finally {
            is.close();
        }
    }
    
    public static int countLines(ZipFile zfile, String entry) throws IOException {
        InputStream is = zfile.getInputStream(zfile.getEntry(entry));
        try {
            byte[] c = new byte[1024];
            int count = 0;
            int readChars = 0;
            boolean empty = true;
            while ((readChars = is.read(c)) != -1) {
                empty = false;
                for (int i = 0; i < readChars; ++i) {
                    if (c[i] == '\n') {
                        ++count;
                    }
                }
            }
            return (count == 0 && !empty) ? 1 : count;
        } finally {
            is.close();
        }
    }
    
    public static float takeClosest(ArrayList<Float> pklist, float peak){
        int index = Collections.binarySearch(pklist,peak,Collections.reverseOrder());
//        VavaLogger.LOG.info(pklist.toString());
//        VavaLogger.LOG.info("peak="+peak+" indexFound="+index);
        index = Math.abs(index) - 1; //ara apunta al seguent valor, mes petit
        if (index == 0){
            return pklist.get(0);
        }
        if ((index == pklist.size())||(index == (pklist.size()-1))){
            return pklist.get(pklist.size()-1);
        }
        //sempre estara entre index i index-1 (havent corregit ja un cop el -1)
//        VavaLogger.LOG.info("peak="+peak+" index="+pklist.get(index)+" index-1="+pklist.get(index-1));
        
        //index -1 sempre > que valor, mentre que index sempre < que valor
        float afterdiff = pklist.get(index-1) - peak;
        float beforediff = peak - pklist.get(index);
        if (afterdiff < beforediff){
            return pklist.get(index-1);
        }else{
            return pklist.get(index);
        }
    }
    
    //igual anterior pero en comptes de valor torna index
    public static int takeClosestIndex(ArrayList<Float> pklist, float peak){
        int index = Collections.binarySearch(pklist,peak,Collections.reverseOrder());
//        VavaLogger.LOG.info(pklist.toString());
//        VavaLogger.LOG.info("peak="+peak+" indexFound="+index);
        index = Math.abs(index) - 1; //ara apunta al seguent valor, mes petit
        if (index == 0){
            return 0;
        }
        if ((index == pklist.size())||(index == (pklist.size()-1))){
            return pklist.size()-1;
        }
        //sempre estara entre index i index-1 (havent corregit ja un cop el -1)
//        VavaLogger.LOG.info("peak="+peak+" index="+pklist.get(index)+" index-1="+pklist.get(index-1));
        
        //index -1 sempre > que valor, mentre que index sempre < que valor
        float afterdiff = pklist.get(index-1) - peak;
        float beforediff = peak - pklist.get(index);
        if (afterdiff < beforediff){
            return index-1;
        }else{
            return index;
        }
    }
    
    public static ArrayList<SearchResult> getSearchresults() {
        return searchresults;
    }

    public static class openDBfileWorker extends SwingWorker<Integer,Integer> {

        private File dbfile;
        private ZipFile zfile;
        private boolean stop;
        boolean isZipFile;
        
        public openDBfileWorker(File datafile) {
            this.dbfile = datafile;
            this.zfile = null;
            this.stop = false;
            this.isZipFile=false;
        }
        
        public openDBfileWorker(ZipFile zipdatafile){
            this.dbfile=null;
            this.zfile=zipdatafile;
            this.isZipFile=true;
            this.stop = false;
        }
        
        public openDBfileWorker(){
            this.stop = false;
            this.dbfile = null;
            this.zfile = null;
            this.isZipFile = false;
        }
        
        public void setFileToRead(File datafile){
            this.dbfile = datafile;
            this.isZipFile=false;
        }
        
        public void setFileToRead(ZipFile zipdatafile){
            this.zfile=zipdatafile;
            this.isZipFile=true;
        }
        
        @Override
        protected Integer doInBackground() throws Exception {
            //number of lines
            int totalLines = 0;
            if (this.isZipFile){
                totalLines = countLines(zfile,"codDB.db");
            }else{
                totalLines = countLines(dbfile.toString());                
            }
            int lines = 0;
            try {
                Scanner scDBfile;
                if (this.isZipFile){
                    InputStream is = zfile.getInputStream(zfile.getEntry("codDB.db"));
                    scDBfile = new Scanner(is);    
                }else{
                    scDBfile = new Scanner(dbfile);
                }
                
                while (scDBfile.hasNextLine()){
                    
                    if (stop) break;
                    
                    String line = scDBfile.nextLine();
                    
                    if ((lines % 500) == 0){
                        float percent = ((float)lines/(float)totalLines)*100.f;
                        setProgress((int) percent);
                        VavaLogger.LOG.info(String.valueOf(percent));
                    }
                    
                    lines = lines + 1;
                    
                    if (line.startsWith("#COMP")) {
                        //new compound
                        String[] cname = line.split("\\s+");
                        StringBuilder sb = new StringBuilder();
                        for (int i=2;i<cname.length;i++){
                            sb.append(cname[i]);
                            sb.append(" ");
                        }
                        
                        PDCompound comp = new PDCompound(sb.toString().trim());
                        comp.setOv_number(Integer.parseInt(cname[1]));
                        
                        boolean cfinished = false;
                        while (!cfinished){
                            String line2 = scDBfile.nextLine();
                            lines = lines + 1;
                            
                            //posem entre try/catch la lectura dels parametres per si de cas
                            try {
                                if (line2.startsWith("#CELL_PARAMETERS:")){
                                    String[] cellPars = line2.split("\\s+");
                                    comp.setA(Float.parseFloat(cellPars[1]));
                                    comp.setB(Float.parseFloat(cellPars[2]));
                                    comp.setC(Float.parseFloat(cellPars[3]));
                                    comp.setAlfa(Float.parseFloat(cellPars[4]));
                                    comp.setBeta(Float.parseFloat(cellPars[5]));
                                    comp.setGamma(Float.parseFloat(cellPars[6]));
                                }
                                
                                if (line2.startsWith("#COD_CODE:")){
                                    comp.setCodCODE(Integer.parseInt((line2.split(":"))[1].trim()));
                                }
                                
                                if (line2.startsWith("#SPACE_GROUP:")){
                                    comp.setSpaceGroup((line2.split(":"))[1].trim());
                                }
                                
                                if (line2.startsWith("#FORMULA:")){
                                    comp.setFormula((line2.split(":"))[1].trim());
                                }
                                
                                if (line2.startsWith("#LIST:")){
                                    boolean dsplistfinished = false;
                                    ArrayList<Float> dsp = new ArrayList<Float>();
                                    ArrayList<Float> inten = new ArrayList<Float>();
                                    while (!dsplistfinished){
                                        if (!scDBfile.hasNextLine()){
                                            dsplistfinished = true;
                                            cfinished = true;
                                            continue;
                                        }
                                        String line3 = scDBfile.nextLine();
                                        lines = lines + 1;
                                        if (line3.trim().isEmpty()){
                                            dsplistfinished = true;
                                            cfinished = true;
                                            continue;
                                        }
                                        //VavaLogger.LOG.info(line3);
                                        String[] dspline = line3.trim().split("\\s+");
                                        
                                        dsp.add(Float.parseFloat(dspline[3]));
                                        inten.add(Float.parseFloat(dspline[4]));
                                    }
                                    comp.setDspacings(dsp);
                                    comp.setIntensities(inten);
                                }
                            } catch (Exception e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                                VavaLogger.LOG.info("error reading compound num. "+comp.getOv_number());
                            }                        
                            
                        }
                        addCompound(comp);
                    }
                    
                }
            }catch(Exception e){
                e.printStackTrace();
                this.cancel(true);
                return 1;
            }
            setProgress(100);
            return 0;
        }
        
        public File getDbfile() {
            return dbfile;
        }
        
        public String getReadedFile(){
            if (this.isZipFile){
//                return this.zfile.toString();
                return "(internal)";
            }else{
                return this.dbfile.toString();
            }
        }
        
    }
    
    /*
     * Farem que la intensitat integrada dels pics seleccionats es normalitzi amb el valor màxim dels
     * N primers pics de cada compost per poder-se comparar bé. (N sera igual al nombre de dsp entrats, que 
     * no te perquè ser els N primers però es una bona aproximació).
     */
    
    public static class searchDBWorker extends SwingWorker<Integer,Integer> {

        private ArrayList<Float> slist;
        private ArrayList<Float> slistInten;
        private float maxIslist;
        private boolean stop;
        
        public searchDBWorker(ArrayList<Float> searchList, ArrayList<Float> searchlistIntensities) {
            this.slist = searchList;
//            slistInten = new ArrayList<Float>();
            //normalitzem les intensitats a 100   -- de moment ho trec, normalitzarem a cada compost utilitzant un cert nombre de pics ja que el 100% no te perquè estar seleccionat!!
//            float maxI = Collections.max(searchlistIntensities);
//            Iterator<Float> itrIn = searchlistIntensities.iterator();
//            while (itrIn.hasNext()){
//                float inten = itrIn.next();
//                inten = (inten/maxI) * 100.f;
//                slistInten.add(inten);
//            }
            this.slistInten = searchlistIntensities;
            this.maxIslist = Collections.max(searchlistIntensities);
            searchresults.clear();
            this.stop = false;
        }
        
        @Override
        protected Integer doInBackground() throws Exception {
            
            Iterator<PDCompound> itrComp = compList.iterator();
            int compIndex = 0;
            while (itrComp.hasNext()){
                if (stop) break;
                PDCompound c = itrComp.next();
                Iterator<Float> itrPks = this.slist.iterator();
                float diffPositions = 0;
                float diffIntensities = 0;
                int npk = 0;
                
                //mirem la intensitat màxima dels n primers pics de COMP per normalitzar!
                float maxI_factorPerNormalitzar = c.getMaxInten(slist.size());
                if (maxI_factorPerNormalitzar <= 0){maxI_factorPerNormalitzar=1.0f;}
                
                while (itrPks.hasNext()){
                    float dsp = itrPks.next();  //pic entrat a buscar
//                    float value = takeClosest(c.getDspacings(),dsp);
                    int index = takeClosestIndex(c.getDspacings(),dsp);
                    diffPositions = diffPositions + Math.abs(dsp-c.getDspacings().get(index));
                    float intensity = this.slistInten.get(npk);
                    //normalitzem la intensitat utilitzant el maxim dels N primers pics.
                    intensity = (intensity/maxIslist) * maxI_factorPerNormalitzar;
                    if (c.getIntensities().get(index)>=0){ //no tenim en compte les -1 (NaN)
                        diffIntensities = diffIntensities + Math.abs(intensity-c.getIntensities().get(index));    
                    }
                    npk = npk +1;
                }
                searchresults.add(new SearchResult(c,diffPositions,diffIntensities));
                compIndex = compIndex + 1;
                
                if ((compIndex % 500) == 0){
                    float percent = ((float)compIndex/(float)nCompounds)*100.f;
                    setProgress((int) percent);
                    VavaLogger.LOG.info(String.valueOf(percent));
                }
            }
            setProgress(100);
            return 0;
        }

        public void setStop(boolean stop) {
            this.stop = stop;
        }
    }
    
}
