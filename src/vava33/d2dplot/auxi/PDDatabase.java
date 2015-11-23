package vava33.d2dplot.auxi;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
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

import org.apache.commons.math3.util.FastMath;

import vava33.d2dplot.D2Dplot_global;
import vava33.d2dplot.MainFrame;

import com.vava33.jutils.FileUtils;
import com.vava33.jutils.VavaLogger;


public final class PDDatabase {

    //Full DB
    private static int nCompounds = 0;  //number of compounds in the DB
    private static String localDB = System.getProperty("user.dir") + D2Dplot_global.separator + "default.db";  // local DB default file
    private static ArrayList<PDCompound> DBcompList = new ArrayList<PDCompound>();  
    private static ArrayList<PDSearchResult> DBsearchresults = new ArrayList<PDSearchResult>();
    
    //quicklist
    private static String localQL = System.getProperty("user.dir") + D2Dplot_global.separator + "quicklist.db";  // local DB default file
    private static ArrayList<PDCompound> QLcompList = new ArrayList<PDCompound>();  
    private static boolean QLmodified = false;
    
    private static VavaLogger log = D2Dplot_global.log;
    
    public static void resetDB(){
        DBcompList.clear();
        nCompounds = 0;
    }
    
    public static void resetQL(boolean updateComboMF){
        QLcompList.clear();
        //TODO:ACTUALIZAR COMBO_LAT AQUI??
        if (updateComboMF){
            MainFrame.updateQuickList();
        }
        setQLmodified(true);
    }
    
    public static void addCompoundDB(PDCompound c){
        DBcompList.add(c);
        nCompounds = nCompounds + 1;
    }
    
    public static void addCompoundQL(PDCompound c, boolean updateComboMF){
        QLcompList.add(c);
        //TODO:ACTUALIZAR COMBO_LAT???
        if (updateComboMF){
            MainFrame.updateQuickList();
        }
        setQLmodified(true);

    }

    public static Iterator<PDCompound> getQuickListIterator(){
        if (QLcompList==null){
            QLcompList = new ArrayList<PDCompound>();
        }
        return getQLCompList().iterator();
    }
    
    public static int getnCompounds() {
        return nCompounds;
    }

    public static void setnCompounds(int nCompounds) {
        PDDatabase.nCompounds = nCompounds;
    }

    public static ArrayList<PDCompound> getDBCompList() {
        return DBcompList;
    }

    public static ArrayList<PDCompound> getQLCompList() {
        return QLcompList;
    }
    
    public static void setDBCompList(ArrayList<PDCompound> compList) {
        PDDatabase.DBcompList = compList;
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
    
    public static String getDefaultQLpath(){
        File f = new File(localQL);
        return f.getAbsolutePath();
    }
    
    public static ArrayList<PDSearchResult> getDBSearchresults() {
        return DBsearchresults;
    }
    
    public static int getFirstEmptyNum(){
        //TODO:IMPLEMENTAR-HO, momentaneament fa aixo:
        return PDDatabase.getDBCompList().size()+1;
    }

    //sets the QLDB modified (useful for the first run)
    public static boolean populateQuickList(final boolean setDBModified){
        
        //first try to read LAT files in the current folder
        try{
            File folder = new File(new File(D2Dplot_global.userDir).getPath());
            File[] flist = folder.listFiles();
            for (int i=0; i<flist.length; i++){
                if (FileUtils.getExtension(flist[i]).equalsIgnoreCase("lat")){
                    //llegim el lat i l'afegim a la quicklist
                    String name = FileUtils.getFNameNoExt(flist[i].getName());
                    PDCompound p = ImgFileUtils.readLAT(name, flist[i]);
                    if (p!=null){
                        //calculem els dsp
                        p.calcDSPfromHKL();
                        addCompoundQL(p,true);    
                    }else{
                        log.warning("error reading LAT file to QuickList: "+flist[i]);
                    }
                }
            }
        }catch(Exception e){
            //no passa res si hi ha hagut algun error
            log.debug(e.toString());
        }
        
        //ara llegirem la default QL db
        File QLfile = new File(getDefaultQLpath());
        if (!QLfile.exists()){return false;}
        openDBfileWorker openDBFwk = new PDDatabase.openDBfileWorker(new File(getDefaultQLpath()),true);
        openDBFwk.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
              //log.debug("hello from propertyChange");
              log.debug(evt.getPropertyName());
              if ("progress" == evt.getPropertyName() ) {
                  if (evt.getSource() instanceof openDBfileWorker){
                      openDBfileWorker sw = (openDBfileWorker)evt.getSource();
                      if (sw.isDone()) {
                          //s'hauria d'actualitzar el mainframe combo_lat -- o potser ho fem al fer addcompound?
                          MainFrame.updateQuickList();
                          setQLmodified(setDBModified);
                      }                      
                  }

              }
            }
        });
        //reset current Database
        PDDatabase.resetQL(true);
        //read database file, executing the swingworker task
        openDBFwk.execute();
        return true;
    } 
    
    
    public static boolean isQLmodified() {
        return QLmodified;
    }

    public static String getLocalDB() {
        return localDB;
    }

    public static void setLocalDB(String localDB) {
        PDDatabase.localDB = localDB;
    }

    public static String getLocalQL() {
        return localQL;
    }

    public static void setLocalQL(String localQL) {
        PDDatabase.localQL = localQL;
    }

    public static void setQLmodified(boolean qLmodified) {
        QLmodified = qLmodified;
    }

    //Aixo llegira el fitxer per omplir la base de dades o la quicklist
    public static class openDBfileWorker extends SwingWorker<Integer,Integer> {

        private File dbfile;
        private boolean stop;
        private boolean toQuickList;
        
        public openDBfileWorker(File datafile, boolean useQuickList) {
            this.dbfile = datafile;
            this.stop = false;
            this.toQuickList=useQuickList;
        }
        
        @Override
        protected Integer doInBackground() throws Exception {
            //number of lines
            int totalLines = 0;
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
                        log.debug(String.valueOf(percent));
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
                                        //log.debug(line3);
                                        String[] dspline = line3.trim().split("\\s+");
                                        int h = Integer.parseInt(dspline[0]);
                                        int k = Integer.parseInt(dspline[1]);
                                        int l = Integer.parseInt(dspline[2]);
                                        float dsp = Float.parseFloat(dspline[3]);
                                        float inten = 1.0f;
                                        try{
                                            inten = Float.parseFloat(dspline[4]);    
                                        }catch(Exception exinten){
                                            log.debug(String.format("no intensity found for reflection %d %d %d",h,k,l));
                                        }
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
                                        //log.debug(line3);
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
                                if(D2Dplot_global.isDebug())e.printStackTrace();
                                log.warning("error reading compound: "+comp.getCompName());
                            }                        
                            
                        }
                        
                        if (toQuickList){
                            addCompoundQL(comp,true);
                        }else{
                            addCompoundDB(comp);    
                        }
                        
                    }
                    
                }
            }catch(Exception e){
                if(D2Dplot_global.isDebug())e.printStackTrace();
                log.warning("error reading DB file");
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
            return this.dbfile.toString();
        }
        
    }
    
    //Aixo llegira el fitxer per omplir la base de dades o la quicklist
    public static class saveDBfileWorker extends SwingWorker<Integer,Integer> {

        private File dbfile;
        private boolean stop;
        private boolean toQuickList;
        
        public saveDBfileWorker(File datafile, boolean useQuickList) {
            this.dbfile = datafile;
            this.stop = false;
            this.toQuickList=useQuickList;
        }
        
        @Override
        protected Integer doInBackground() throws Exception {
            
            try{
                PrintWriter output = new PrintWriter(new BufferedWriter(new FileWriter(dbfile)));
                Iterator<PDCompound> itC = null;
                int ncomp = 0;
                int icomp = 0;
                
                //passos previs depenents de si QL or DB
                if (toQuickList){
                    ncomp = getQLCompList().size();
                    itC = QLcompList.iterator();
                }else{
                    ncomp = getDBCompList().size();
                    itC = DBcompList.iterator();
                }
                
                SimpleDateFormat fHora = new SimpleDateFormat("[yyyy-MM-dd 'at' HH:mm]");
                String dt = fHora.format(new Date());
                
                output.println("# ====================================================================");
                output.println("#         D2Dplot compound database "+dt);
                output.println("# ====================================================================");
                output.println();
                
                while (itC.hasNext()){

                    if (stop) break;

                    if ((icomp % 100) == 0){
                        float percent = ((float)icomp/(float)ncomp)*100.f;
                        setProgress((int) percent);
                        log.debug(String.valueOf(percent));
                    }
                    
                    icomp = icomp + 1;
                    
                    PDCompound c = itC.next();
                    output.println(String.format("#COMP: %s",c.getCompName().get(0)));
                    
                    String altnames = c.getAltNames();
                    if (!altnames.isEmpty())output.println(String.format("#NAMEALT: %s",altnames));
                    
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
                }
                output.close();
                
            }catch(Exception e){
                e.printStackTrace();
                this.cancel(true);
                log.info("error writting compound DB: "+dbfile.toString());
                return 1;
            }
            setProgress(100);
            return 0;
        }
        
        public File getDbfile() {
            return dbfile;
        }
        
        public String getDbFileString(){
            return this.dbfile.toString();
        }
        
    }
    
    
    /*
     * Farem que la intensitat integrada dels pics seleccionats es normalitzi amb el valor màxim dels
     * N primers pics de cada compost per poder-se comparar bé. (N sera igual al nombre de dsp entrats, que 
     * no te perquè ser els N primers però es una bona aproximació).
     */
    
    public static class searchDBWorker extends SwingWorker<Integer,Integer> {

        private ArrayList<Float> dspList;
        private ArrayList<Float> intList;
        private boolean stop;
        private float mindsp;
        private Pattern2D patt2D;
        
        public searchDBWorker(Pattern2D patt2d, float mindsp) {
            this.patt2D=patt2d;
            this.mindsp=mindsp;
            dspList = new ArrayList<Float>();
            intList = new ArrayList<Float>();
            DBsearchresults.clear();
            this.stop = false;
        }
        
        public void mySetProgress(int prog){
            setProgress(prog);
        }
        
        @Override
        protected Integer doInBackground() throws Exception {
            
            //generem les llistes de dspacing i intensitats a partir dels punts seleccionats a un pattern2D i un mindsp
            //(ho hem passat aquí perque es costos, sobretot l'extraccio d'intensitats)
            float[] t2deglist = new float[patt2D.getPuntsCercles().size()];
            Iterator<PuntClick> itrPks = patt2D.getPuntsCercles().iterator();
            int n=0;
            while (itrPks.hasNext()){
                PuntClick pc = itrPks.next();
                float dsp = (float) patt2D.calcDsp(pc.getT2rad());
                if (dsp > mindsp){
                    dspList.add(dsp);
                    t2deglist[n]=(float) FastMath.toDegrees(pc.getT2rad());
                    n = n +1;
                }
            }
            float[] circleIntensities = ImgOps.radialIntegrationVarious2th(patt2D, t2deglist, -1, false, false,this);
            for (int i=0;i<circleIntensities.length;i++){
                intList.add(circleIntensities[i]);    
            }
            
            float maxIslist = Collections.max(intList);
            
            PDSearchResult.setMinDSPin(Collections.min(dspList));
            PDSearchResult.setnDSPin(dspList.size());
            
            Iterator<PDCompound> itrComp = DBcompList.iterator();
            int compIndex = 0;
            while (itrComp.hasNext()){
                if (stop) break;
                PDCompound c = itrComp.next();
                Iterator<Float> itrDSP = this.dspList.iterator();
                float diffPositions = 0;
                float diffIntensities = 0;
                int npk = 0;
                
                //mirem la intensitat màxima dels n primers pics de COMP per normalitzar!
                float maxI_factorPerNormalitzar = c.getMaxInten(dspList.size());
                if (maxI_factorPerNormalitzar <= 0){maxI_factorPerNormalitzar=1.0f;}
                
                while (itrDSP.hasNext()){
                    float dsp = itrDSP.next();  //pic entrat a buscar
                    int index = c.closestPeak(dsp);
                    float diffpk = FastMath.abs(dsp-c.getPeaks().get(index).getDsp());
//                    diffPositions = diffPositions + diffpk; //es podria fer més estricte
//                    diffPositions = diffPositions + (1+diffpk)*(1+diffpk); //una especie de quadrat...
                    diffPositions = diffPositions + (diffpk*2.5f); 
                    float intensity = this.intList.get(npk);
                    //normalitzem la intensitat utilitzant el maxim dels N primers pics.
                    intensity = (intensity/maxIslist) * maxI_factorPerNormalitzar;
                    if (c.getPeaks().get(index).getInten()>=0){ //no tenim en compte les -1 (NaN)
                        diffIntensities = diffIntensities + FastMath.abs(intensity-c.getPeaks().get(index).getInten());    
                    }
                    npk = npk +1;
                }
//                searchresults.add(new PDSearchResult(c,(float)FastMath.sqrt(diffPositions),diffIntensities));
                DBsearchresults.add(new PDSearchResult(c,diffPositions,diffIntensities));
                compIndex = compIndex + 1;
                
                if ((compIndex % nCompounds/100) == 0){
                    float percent = ((float)compIndex/(float)nCompounds)*100.f;
                    setProgress((int) percent);
                    log.debug(String.valueOf(percent));
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