package com.vava33.d2dplot.auxi;

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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.Scanner;
import java.util.zip.ZipFile;

import javax.swing.SwingWorker;

import org.apache.commons.math3.util.FastMath;

import com.vava33.cellsymm.CellSymm_global;
import com.vava33.d2dplot.D2Dplot_global;
import com.vava33.d2dplot.MainFrame;
import com.vava33.jutils.FileUtils;
import com.vava33.jutils.VavaLogger;

public final class PDDatabase {

    //Full DB
    private static int nCompounds = 0;  //number of compounds in the DB
    private static String localDB = System.getProperty("user.dir") + D2Dplot_global.separator + "default.db";  // local DB default file
    private static String currentDB;
    private static ArrayList<PDCompound> DBcompList = new ArrayList<PDCompound>();
    private static ArrayList<PDSearchResult> DBsearchresults = new ArrayList<PDSearchResult>();

    //quicklist
    private static String localQL = System.getProperty("user.dir") + D2Dplot_global.separator + "quicklist.db";  // local DB default file
    private static String currentQL;
    private static ArrayList<PDCompound> QLcompList = new ArrayList<PDCompound>();
    private static boolean QLmodified = false;
    private static boolean DBmodified = false;

    private static final String className = "PDdatabase";
    private static VavaLogger log = D2Dplot_global.getVavaLogger(className);

    public static void resetDB() {
        DBcompList.clear();
        nCompounds = 0;
    }

    public static void resetQL(boolean updateComboMF) {
        QLcompList.clear();
        if (updateComboMF) {
            MainFrame.updateQuickList();
        }
        setQLmodified(true);
    }

    public static void addCompoundDB(PDCompound c) {
        DBcompList.add(c);
        nCompounds = nCompounds + 1;
    }

    public static void addCompoundQL(PDCompound c, boolean updateComboMF) {
        QLcompList.add(c);
        if (updateComboMF) {
            MainFrame.updateQuickList();
        }
        setQLmodified(true);

    }

    public static Iterator<PDCompound> getQuickListIterator() {
        if (QLcompList == null) {
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
        final InputStream is = new BufferedInputStream(new FileInputStream(filename));
        try {
            final byte[] c = new byte[1024];
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
        final InputStream is = zfile.getInputStream(zfile.getEntry(entry));
        try {
            final byte[] c = new byte[1024];
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
            final byte[] c = new byte[1024];
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

    public static String getDefaultDBpath() {
        final File f = new File(localDB);
        return f.getAbsolutePath();
    }

    public static String getDefaultQLpath() {
        final File f = new File(localQL);
        return f.getAbsolutePath();
    }

    public static ArrayList<PDSearchResult> getDBSearchresults() {
        return DBsearchresults;
    }

    public static int getFirstEmptyNum() {
        //TODO:IMPLEMENTAR-HO, momentaneament fa aixo:
        return PDDatabase.getDBCompList().size() + 1;
    }

    //sets the QLDB modified (useful for the first run)
    public static boolean populateQuickList(final boolean setDBModified) {

        //llegirem la default QL db
        final File QLfile = new File(getDefaultQLpath());
        if (!QLfile.exists()) {
            return false;
        }
        final OpenDBfileWorker openDBFwk = new PDDatabase.OpenDBfileWorker(new File(getDefaultQLpath()), true);
        openDBFwk.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                //log.debug("hello from propertyChange");
                log.debug(evt.getPropertyName());
                if ("progress" == evt.getPropertyName()) {
                    if (evt.getSource() instanceof OpenDBfileWorker) {
                        final OpenDBfileWorker sw = (OpenDBfileWorker) evt.getSource();
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

    public static boolean isDBmodified() {
        return DBmodified;
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

    public static void setDBmodified(boolean dBmodified) {
        DBmodified = dBmodified;
    }

    public static String getCurrentDB() {
        return currentDB;
    }

    public static void setCurrentDB(String currentDB) {
        PDDatabase.currentDB = currentDB;
    }

    public static String getCurrentQL() {
        return currentQL;
    }

    public static void setCurrentQL(String currentQL) {
        PDDatabase.currentQL = currentQL;
    }

    //Aixo llegira el fitxer per omplir la base de dades o la quicklist
    public static class OpenDBfileWorker extends SwingWorker<Integer, Integer> {

        private final File dbfile;
        private final boolean stop;
        private final boolean toQuickList;

        public OpenDBfileWorker(File datafile, boolean useQuickList) {
            this.dbfile = datafile;
            this.stop = false;
            this.toQuickList = useQuickList;
        }

        @Override
        protected Integer doInBackground() throws Exception {
            //number of lines
            int totalLines = 0;
            totalLines = countLines(this.dbfile.toString());

            int lines = 0;
            try {
                Scanner scDBfile;
                scDBfile = new Scanner(this.dbfile);

                while (scDBfile.hasNextLine()) {

                    if (this.stop)
                        break;

                    final String line = scDBfile.nextLine();

                    if ((lines % 500) == 0) {
                        final float percent = ((float) lines / (float) totalLines) * 100.f;
                        this.setProgress((int) percent);
                        log.debug(String.valueOf(percent));
                    }

                    lines = lines + 1;

                    if ((line.startsWith("#COMP")) || (line.startsWith("#S "))) {  //#S per compatibilitat amb altres DBs
                        //new compound

                        PDCompound comp;
                        if (line.startsWith("#S ")) {
                            final String[] cname = line.split("\\s+");
                            final StringBuilder sb = new StringBuilder();
                            for (int i = 2; i < cname.length; i++) {
                                sb.append(cname[i]);
                                sb.append(" ");
                            }

                            comp = new PDCompound(sb.toString().trim());
                        } else {
                            comp = new PDCompound(line.split(":")[1].trim());
                        }

                        boolean cfinished = false;
                        while (!cfinished) {
                            final String line2 = scDBfile.nextLine();
                            lines = lines + 1;

                            //posem entre try/catch la lectura dels parametres per si de cas
                            try {
                                if (line2.startsWith("#CELL_PARAMETERS:")) {
                                    final String[] cellPars = line2.split("\\s+");
                                    comp.getCella().setCellParameters(Double.parseDouble(cellPars[1]),
                                            Double.parseDouble(cellPars[2]), Double.parseDouble(cellPars[3]),
                                            Double.parseDouble(cellPars[4]), Double.parseDouble(cellPars[5]),
                                            Double.parseDouble(cellPars[6]), true);
                                }
                                if (line2.startsWith("#NAME")) {
                                    if (line2.contains(":")) {
                                        comp.addCompoundName((line2.split(":"))[1].trim());
                                    }
                                }

                                if (line2.startsWith("#SPACE_GROUP:")) {
                                    comp.getCella().setSg(
                                            CellSymm_global.getSpaceGroupByName((line2.split(":"))[1].trim(), false));
                                }

                                if (line2.startsWith("#FORMULA:")) {
                                    comp.setFormula((line2.split(":"))[1].trim());
                                }

                                if (line2.startsWith("#REF")) {
                                    if (line2.contains(":")) {
                                        comp.setReference((line2.split(":"))[1].trim());
                                    }
                                }

                                if (line2.startsWith("#COMMENT:")) {
                                    comp.addComent((line2.split(":"))[1].trim());
                                }

                                if (line2.startsWith("#LIST:")) {
                                    boolean dsplistfinished = false;

                                    while (!dsplistfinished) {
                                        if (!scDBfile.hasNextLine()) {
                                            dsplistfinished = true;
                                            cfinished = true;
                                            continue;
                                        }
                                        final String line3 = scDBfile.nextLine();
                                        lines = lines + 1;
                                        if (line3.trim().isEmpty()) {
                                            dsplistfinished = true;
                                            cfinished = true;
                                            continue;
                                        }
                                        //log.debug(line3);
                                        final String[] dspline = line3.trim().split("\\s+");
                                        final int h = Integer.parseInt(dspline[0]);
                                        final int k = Integer.parseInt(dspline[1]);
                                        final int l = Integer.parseInt(dspline[2]);
                                        final float dsp = Float.parseFloat(dspline[3]);
                                        float inten = 1.0f;
                                        try {
                                            inten = Float.parseFloat(dspline[4]);
                                        } catch (final Exception exinten) {
                                            log.debug(String.format("no intensity found for reflection %d %d %d", h, k,
                                                    l));
                                        }
                                        comp.addPeak(h, k, l, dsp, inten);
                                    }
                                }

                                //COMPATIBILITAT AMB ALTRE BASE DE DADES
                                if (line2.startsWith("#UXRD_REFERENCE ")) {
                                    comp.setReference(line2.substring(16).trim());
                                }

                                if (line2.startsWith("#UXRD_INFO CELL PARAMETERS:")) {
                                    final String[] cellPars = line2.split("\\s+");
                                    comp.getCella().setCellParameters(Double.parseDouble(cellPars[1]),
                                            Double.parseDouble(cellPars[2]), Double.parseDouble(cellPars[3]),
                                            Double.parseDouble(cellPars[4]), Double.parseDouble(cellPars[5]),
                                            Double.parseDouble(cellPars[6]), true);
                                }

                                if (line2.startsWith("#UXRD_INFO SPACE GROUP: ")) {
                                    comp.getCella().setSg(
                                            CellSymm_global.getSpaceGroupByName((line2.split(":"))[1].trim(), false));
                                }

                                if (line2.startsWith("#UXRD_ELEMENTS")) {
                                    comp.setFormula(line2.substring(15).trim());
                                }

                                if (line2.startsWith("#L ")) {
                                    boolean dsplistfinished = false;

                                    while (!dsplistfinished) {
                                        if (!scDBfile.hasNextLine()) {
                                            dsplistfinished = true;
                                            cfinished = true;
                                            continue;
                                        }
                                        final String line3 = scDBfile.nextLine();
                                        lines = lines + 1;
                                        if (line3.trim().isEmpty()) {
                                            dsplistfinished = true;
                                            cfinished = true;
                                            continue;
                                        }
                                        //log.debug(line3);
                                        final String[] dspline = line3.trim().split("\\s+");
                                        final int h = Integer.parseInt(dspline[2]);
                                        final int k = Integer.parseInt(dspline[3]);
                                        final int l = Integer.parseInt(dspline[4]);
                                        final float dsp = Float.parseFloat(dspline[0]);
                                        final float inten = Float.parseFloat(dspline[1]);
                                        comp.addPeak(h, k, l, dsp, inten);
                                    }
                                }

                            } catch (final Exception e) {
                                if (D2Dplot_global.isDebug())
                                    e.printStackTrace();
                                log.warning("Error reading compound: " + comp.getCompName());
                            }

                        }

                        if (this.toQuickList) {
                            addCompoundQL(comp, true);
                        } else {
                            addCompoundDB(comp);
                        }
                    }
                }
                scDBfile.close();
            } catch (final Exception e) {
                if (D2Dplot_global.isDebug())
                    e.printStackTrace();
                log.warning("Error reading DB file");
                this.cancel(true);
                return 1;
            }
            this.setProgress(100);
            if (this.toQuickList) {
                setCurrentQL(this.dbfile.toString());
            } else {
                setCurrentDB(this.dbfile.toString());
            }
            return 0;
        }

        public File getDbfile() {
            return this.dbfile;
        }

        public String getReadedFile() {
            return this.dbfile.toString();
        }

    }

    //Aixo llegira el fitxer per omplir la base de dades o la quicklist
    public static class SaveDBfileWorker extends SwingWorker<Integer, Integer> {

        private final File dbfile;
        private final boolean stop;
        private final boolean toQuickList;

        public SaveDBfileWorker(File datafile, boolean useQuickList) {
            this.dbfile = FileUtils.canviExtensio(datafile, "db");
            this.stop = false;
            this.toQuickList = useQuickList;
        }

        @Override
        protected Integer doInBackground() throws Exception {

            try {
                final PrintWriter output = new PrintWriter(new BufferedWriter(new FileWriter(this.dbfile)));
                Iterator<PDCompound> itC = null;
                int ncomp = 0;
                int icomp = 0;

                //passos previs depenents de si QL or DB
                if (this.toQuickList) {
                    ncomp = getQLCompList().size();
                    itC = QLcompList.iterator();
                } else {
                    ncomp = getDBCompList().size();
                    itC = DBcompList.iterator();
                }

                log.writeNameNumPairs("config", true, "ncomp", ncomp);

                //                SimpleDateFormat fHora = new SimpleDateFormat("[yyyy-MM-dd 'at' HH:mm]");
                //                String dt = fHora.format(new Date());

                final String dt = D2Dplot_global.getStringTimeStamp("[yyyy-MM-dd 'at' HH:mm]");

                output.println("# ====================================================================");
                output.println("#         D2Dplot compound database " + dt);
                output.println("# ====================================================================");
                output.println();

                while (itC.hasNext()) {

                    if (this.stop)
                        break;

                    if ((icomp % 100) == 0) {
                        final float percent = ((float) icomp / (float) ncomp) * 100.f;
                        this.setProgress((int) percent);
                        log.debug(String.valueOf(percent));
                    }

                    icomp = icomp + 1;

                    final PDCompound c = itC.next();
                    output.println(String.format("#COMP: %s", c.getCompName().get(0)));

                    final String altnames = c.getAltNames();
                    if (!altnames.isEmpty())
                        output.println(String.format("#NAMEALT: %s", altnames));

                    if (!c.getFormula().isEmpty()) {
                        output.println(String.format("#FORMULA: %s", c.getFormula()));
                    }
                    if (!c.getCellParameters().isEmpty()) {
                        output.println(String.format("#CELL_PARAMETERS: %s", c.getCellParameters()));
                    }
                    if (!c.getCella().getSg().getName().isEmpty()) {
                        output.println(String.format("#SPACE_GROUP: %s", c.getCella().getSg().getName()));
                    }
                    if (!c.getReference().isEmpty()) {
                        output.println(String.format("#REF: %s", c.getReference()));
                    }
                    if (!c.getComment().isEmpty()) {
                        output.println(String.format("#COMMENT: %s", c.getComment()));
                    }
                    output.println("#LIST: H  K  L  dsp  Int");

                    final int refs = c.getPeaks().size();
                    for (int i = 0; i < refs; i++) {
                        final int h = c.getPeaks().get(i).getH();
                        final int k = c.getPeaks().get(i).getK();
                        final int l = c.getPeaks().get(i).getL();
                        final double dsp = c.getPeaks().get(i).getDsp();
                        final double inten = c.getPeaks().get(i).getYcalc();
                        output.println(String.format("%3d %3d %3d %9.5f %7.2f", h, k, l, dsp, inten));
                    }
                    output.println(); //linia en blanc entre compostos

                    log.config("itC end loop cycle");

                }
                output.close();

            } catch (final Exception e) {
                e.printStackTrace();
                this.cancel(true);
                log.info("Error writting compound DB: " + this.dbfile.toString());
                return 1;
            }
            this.setProgress(100);
            if (this.toQuickList) {
                setCurrentQL(this.dbfile.toString());
            } else {
                setCurrentDB(this.dbfile.toString());
            }
            return 0;
        }

        public File getDbfile() {
            return this.dbfile;
        }

        public String getDbFileString() {
            return this.dbfile.toString();
        }

    }

    /*
     * Farem que la intensitat integrada dels pics seleccionats es normalitzi amb el valor màxim dels
     * N primers pics de cada compost per poder-se comparar bé. (N sera igual al nombre de dsp entrats, que
     * no te perquè ser els N primers però es una bona aproximació).
     */

    public static class SearchDBWorker extends SwingWorker<Integer, Integer> {

        private final ArrayList<Float> dspList;
        private final ArrayList<Float> intList;
        private boolean stop;
        private final float mindsp;
        private final Pattern2D patt2D;

        public SearchDBWorker(Pattern2D patt2d, float mindsp) {
            this.patt2D = patt2d;
            this.mindsp = mindsp;
            this.dspList = new ArrayList<Float>();
            this.intList = new ArrayList<Float>();
            DBsearchresults.clear();
            this.stop = false;
        }

        public void mySetProgress(int prog) {
            this.setProgress(prog);
        }

        @Override
        protected Integer doInBackground() throws Exception {

            //generem les llistes de dspacing i intensitats a partir dels punts seleccionats a un pattern2D i un mindsp
            //(ho hem passat aquí perque es costos, sobretot l'extraccio d'intensitats)
            final float[] t2deglist = new float[this.patt2D.getPuntsCercles().size()];
            final Iterator<PuntClick> itrPks = this.patt2D.getPuntsCercles().iterator();
            int n = 0;
            while (itrPks.hasNext()) {
                final PuntClick pc = itrPks.next();
                final float dsp = (float) this.patt2D.calcDsp(pc.getT2rad());
                if (dsp > this.mindsp) {
                    this.dspList.add(dsp);
                    t2deglist[n] = (float) FastMath.toDegrees(pc.getT2rad());
                    n = n + 1;
                }
            }
            final float[] circleIntensities = ImgOps.radialIntegrationVarious2th(this.patt2D, t2deglist, -1, false,
                    false, true, this);
            for (final float circleIntensitie : circleIntensities) {
                this.intList.add(circleIntensitie);
            }

            final float maxIslist = Collections.max(this.intList);

            PDSearchResult.setMinDSPin(Collections.min(this.dspList));
            PDSearchResult.setnDSPin(this.dspList.size());

            final Iterator<PDCompound> itrComp = DBcompList.iterator();
            int compIndex = 0;
            while (itrComp.hasNext()) {
                if (this.stop)
                    break;
                final PDCompound c = itrComp.next();
                final Iterator<Float> itrDSP = this.dspList.iterator();
                float diffPositions = 0;
                float diffIntensities = 0;
                int npk = 0;

                //mirem la intensitat màxima dels n primers pics de COMP per normalitzar!
                float maxI_factorPerNormalitzar = (float) c.getMaxInten(this.dspList.size());
                if (maxI_factorPerNormalitzar <= 0) {
                    maxI_factorPerNormalitzar = 1.0f;
                }

                while (itrDSP.hasNext()) {
                    final float dsp = itrDSP.next();  //pic entrat a buscar
                    final int index = c.closestPeak(dsp);
                    final float diffpk = (float) FastMath.abs(dsp - c.getPeaks().get(index).getDsp());
                    //                    diffPositions = diffPositions + diffpk; //es podria fer més estricte
                    //                    diffPositions = diffPositions + (1+diffpk)*(1+diffpk); //una especie de quadrat...
                    diffPositions = diffPositions + (diffpk * 2.5f);
                    float intensity = this.intList.get(npk);
                    //normalitzem la intensitat utilitzant el maxim dels N primers pics.
                    intensity = (intensity / maxIslist) * maxI_factorPerNormalitzar;
                    if (c.getPeaks().get(index).getYcalc() >= 0) { //no tenim en compte les -1 (NaN)
                        diffIntensities = (float) (diffIntensities
                                + FastMath.abs(intensity - c.getPeaks().get(index).getYcalc()));
                    }
                    npk = npk + 1;
                }
                //                searchresults.add(new PDSearchResult(c,(float)FastMath.sqrt(diffPositions),diffIntensities));
                DBsearchresults.add(new PDSearchResult(c, diffPositions, diffIntensities));
                compIndex = compIndex + 1;

                if ((compIndex % nCompounds / 100) == 0) {
                    final float percent = ((float) compIndex / (float) nCompounds) * 100.f;
                    this.setProgress((int) percent);
                    log.debug(String.valueOf(percent));
                }
            }
            this.setProgress(100);
            return 0;
        }

        public void setStop(boolean stop) {
            this.stop = stop;
        }
    }
}