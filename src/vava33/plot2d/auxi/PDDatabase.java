package vava33.plot2d.auxi;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.Scanner;
import java.util.zip.ZipFile;

import javax.swing.SwingWorker;

//import vava33.plot2d.auxi.PDCompound.DBreflection;


import org.apache.commons.math3.util.FastMath;

import vava33.plot2d.auxi.VavaLogger;

public final class PDDatabase {

    private static int nCompounds = 0;
//    private static final String localDB = "/latFiles/codDB.db";
    private static final String localDB = "default.db";
    private static ArrayList<PDCompound> compList = new ArrayList<PDCompound>();
    private static ArrayList<PDSearchResult> searchresults = new ArrayList<PDSearchResult>();
    
    
    public static void reset(){
        compList.clear();
        nCompounds = 0;
    }
    
    public static void addCompound(PDCompound c){
        compList.add(c);
        nCompounds = nCompounds + 1;
    }

//    public static PDCompound get_compound_from_ovNum(int num){
//        Iterator<PDCompound> it = compList.iterator();
//        while (it.hasNext()){
//            PDCompound c = it.next();
//            if (c.getCnumber() == num){
//                return c;
//            }
//        }
//        return null;
//    }
    
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
    
    //it closes the inputstream
    public static int countLines(InputStream is) throws IOException {
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
    
    public static String getDefaultDBpath(){
        File f = new File(localDB);
        return f.getAbsolutePath();
    }
    
    public static ArrayList<PDSearchResult> getSearchresults() {
        return searchresults;
    }
    
    public static int getFirstEmptyNum(){
        //TODO:IMPLEMENTAR-HO, momentaneament fa aixo:
        return PDDatabase.getCompList().size()+1;
    }

    
    public static boolean saveDB(File f){
        // creem un printwriter amb el fitxer file (el que estem escribint)
        SimpleDateFormat fHora = new SimpleDateFormat("[yyyy-MM-dd 'at' HH:mm]");
        String dt = fHora.format(new Date());
                
        try {
            PrintWriter output = new PrintWriter(new BufferedWriter(new FileWriter(f)));
            output.println("# ====================================================================");
            output.println("#         D2Dplot compound database "+dt);
            output.println("# ====================================================================");
            output.println();
            
            Iterator<PDCompound> itC = compList.iterator();
            
//            int n = 1;
            while (itC.hasNext()){
                PDCompound c = itC.next();
//                output.println(String.format("#COMP: %d %s",c.getCnumber(),c.getCompName().get(0)));
//                output.println(String.format("#COMP: %d %s",n,c.getCompName().get(0)));
                output.println(String.format("#COMP: %s",c.getCompName().get(0)));
                
                String altnames = c.getAltNames();
                if (!altnames.isEmpty())output.println(String.format("#NAMEALT: %s",altnames));

//                if (c.getCompName().size()>1){
//                    StringBuilder sb = new StringBuilder();
//                    for (int i=1;i<c.getCompName().size();i++){
//                        sb.append(c.getCompName().get(i));
//                        sb.append(" ");
//                    }
//                    output.println(String.format("#NAMEALT: %s",sb.toString().trim()));
//                }
                
                if (!c.getFormula().isEmpty()){
                    output.println(String.format("#FORMULA: %s",c.getFormula()));
                }
                if (!c.getCellParameters().isEmpty()){
                    output.println(String.format("#CELL_PARAMETERS: %s",c.getCellParameters()));
                }
                if (!c.getSpaceGroup().isEmpty()){
                    output.println(String.format("#SPACE_GROUP: %s",c.getSpaceGroup()));
                }
                if (!c.getReference().isEmpty()){
                    output.println(String.format("#REF: %s",c.getReference()));    
                }
                if (!c.getComment().isEmpty()){
                    output.println(String.format("#COMMENT: %s",c.getComment()));    
                }
                output.println("#LIST: H  K  L  dsp  Int");
                
                int refs = c.getPeaks().size();
                for (int i=0;i<refs;i++){
                    int h = c.getPeaks().get(i).getH();
                    int k = c.getPeaks().get(i).getK();
                    int l = c.getPeaks().get(i).getL();
                    float dsp = c.getPeaks().get(i).getDsp();
                    float inten = c.getPeaks().get(i).getInten();
                    output.println(String.format("%3d %3d %3d %9.5f %7.2f",h,k,l,dsp,inten));                    
                }
                output.println(); //linia en blanc entre compostos
//                n = n +1;
            }
            output.close();
        }catch(Exception e){
            e.printStackTrace();
            return false;
        }
        
        return true;
    }
    
    
    public static class openDBfileWorker extends SwingWorker<Integer,Integer> {

        private File dbfile;
        private boolean stop;
        private boolean readLocal;
        
        public openDBfileWorker(File datafile, boolean readlocal) {
            this.dbfile = datafile;
            this.stop = false;
            this.readLocal=readlocal;
        }
        //        
//        public openDBfileWorker(ZipFile zipdatafile){
//            this.dbfile=null;
//            this.zfile=zipdatafile;
//            this.isZipFile=true;
//            this.stop = false;
//        }
//        
//        public openDBfileWorker(){
//            this.stop = false;
//            this.dbfile = null;
//            this.zfile = null;
//            this.isZipFile = false;
//        }
//        
        public void setFileToRead(File datafile){
            this.dbfile = datafile;
            this.readLocal=false;
        }
        public boolean isReadLocal() {
            return readLocal;
        }
        public void setReadLocal(boolean readLocal) {
            this.readLocal = readLocal;
        }
//        
//        public void setFileToRead(ZipFile zipdatafile){
//            this.zfile=zipdatafile;
//            this.isZipFile=true;
//        }
        
        @Override
        protected Integer doInBackground() throws Exception {
            //number of lines
            int totalLines = 0;
            if (this.readLocal){ 
                dbfile = new File(localDB);
                System.out.println(localDB);
            }
            totalLines = countLines(dbfile.toString());                

            int lines = 0;
            try {
                Scanner scDBfile;
                scDBfile = new Scanner(dbfile);
                
                while (scDBfile.hasNextLine()){
                    
                    if (stop) break;
                    
                    String line = scDBfile.nextLine();
                    
                    if ((lines % 500) == 0){
                        float percent = ((float)lines/(float)totalLines)*100.f;
                        setProgress((int) percent);
                        VavaLogger.LOG.info(String.valueOf(percent));
                    }
                    
                    lines = lines + 1;
                    
                    if ((line.startsWith("#COMP")) || (line.startsWith("#S "))) {  //#S per compatibilitat amb altres DBs
                        //new compound
                        
                        PDCompound comp;
                        if (line.startsWith("#S ")){
                          String[] cname = line.split("\\s+");
                          StringBuilder sb = new StringBuilder();
                          for (int i=2;i<cname.length;i++){
                              sb.append(cname[i]);
                              sb.append(" ");
                          }
                          
                          comp = new PDCompound(sb.toString().trim());
                        }else{
                          comp = new PDCompound(line.split(":")[1].trim());
                        }
                        
//                        String[] cname = line.split("\\s+");
//                        StringBuilder sb = new StringBuilder();
//                        for (int i=2;i<cname.length;i++){
//                            sb.append(cname[i]);
//                            sb.append(" ");
//                        }
//                        
//                        PDCompound comp = new PDCompound(sb.toString().trim());
//                        comp.setCnumber(Integer.parseInt(cname[1]));
                        
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
                                if (line2.startsWith("#NAME")){
                                    if (line2.contains(":")){
                                        comp.addCompoundName((line2.split(":"))[1].trim());
                                    }
                                }
                               
                                if (line2.startsWith("#SPACE_GROUP:")){
                                    comp.setSpaceGroup((line2.split(":"))[1].trim());
                                }
                                
                                if (line2.startsWith("#FORMULA:")){
                                    comp.setFormula((line2.split(":"))[1].trim());
                                }
                                
                                if (line2.startsWith("#REF")){
                                    if (line2.contains(":")){
                                        comp.setReference((line2.split(":"))[1].trim());
                                    }
                                }
                                
                                if (line2.startsWith("#COMMENT:")){
                                    comp.addComent((line2.split(":"))[1].trim());
                                }
                                
                                if (line2.startsWith("#LIST:")){
                                    boolean dsplistfinished = false;

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
                                        int h = Integer.parseInt(dspline[0]);
                                        int k = Integer.parseInt(dspline[1]);
                                        int l = Integer.parseInt(dspline[2]);
                                        float dsp = Float.parseFloat(dspline[3]);
                                        float inten = Float.parseFloat(dspline[4]);
                                        comp.addPeak(h, k, l, dsp, inten);
                                    }
                                }
                                
                                //COMPATIBILITAT AMB ALTRE BASE DE DADES
                                if (line2.startsWith("#UXRD_REFERENCE ")){
                                    comp.setReference(line2.substring(16).trim());
                                }
                                
                                if (line2.startsWith("#UXRD_INFO CELL PARAMETERS:")){
                                    String[] cellPars = line2.split("\\s+");
                                    comp.setA(Float.parseFloat(cellPars[3]));
                                    comp.setB(Float.parseFloat(cellPars[4]));
                                    comp.setC(Float.parseFloat(cellPars[5]));
                                    comp.setAlfa(Float.parseFloat(cellPars[6]));
                                    comp.setBeta(Float.parseFloat(cellPars[7]));
                                    comp.setGamma(Float.parseFloat(cellPars[8]));
                                }
//                                if (line2.startsWith("#S ")){
//                                    comp.addCompoundName((line2.split(":"))[1].trim());
//                                }
                               
                                if (line2.startsWith("#UXRD_INFO SPACE GROUP: ")){
                                    comp.setSpaceGroup((line2.split(":"))[1].trim());
                                }
                                
                                if (line2.startsWith("#UXRD_ELEMENTS")){
                                    comp.setFormula(line2.substring(15).trim());
                                }
                                
                                if (line2.startsWith("#L ")){
                                    boolean dsplistfinished = false;

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
                                        int h = Integer.parseInt(dspline[2]);
                                        int k = Integer.parseInt(dspline[3]);
                                        int l = Integer.parseInt(dspline[4]);
                                        float dsp = Float.parseFloat(dspline[0]);
                                        float inten = Float.parseFloat(dspline[1]);
                                        comp.addPeak(h, k, l, dsp, inten);
                                    }
                                }
                                
                                
                            } catch (Exception e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                                VavaLogger.LOG.info("error reading compound: "+comp.getCompName());
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
            if (this.readLocal){
//                return this.zfile.toString();
                return localDB;
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
                    int index = c.closestPeak(dsp);
                    float diffpk = FastMath.abs(dsp-c.getPeaks().get(index).getDsp());
//                    diffPositions = diffPositions + diffpk; //es podria fer més estricte
//                    diffPositions = diffPositions + (1+diffpk)*(1+diffpk); //una especie de quadrat...
                    diffPositions = diffPositions + (diffpk*2.5f); 
                    float intensity = this.slistInten.get(npk);
                    //normalitzem la intensitat utilitzant el maxim dels N primers pics.
                    intensity = (intensity/maxIslist) * maxI_factorPerNormalitzar;
                    if (c.getPeaks().get(index).getInten()>=0){ //no tenim en compte les -1 (NaN)
                        diffIntensities = diffIntensities + FastMath.abs(intensity-c.getPeaks().get(index).getInten());    
                    }
                    npk = npk +1;
                }
//                searchresults.add(new PDSearchResult(c,(float)FastMath.sqrt(diffPositions),diffIntensities));
                searchresults.add(new PDSearchResult(c,diffPositions,diffIntensities));
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
