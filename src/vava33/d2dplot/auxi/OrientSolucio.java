package vava33.d2dplot.auxi;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.apache.commons.math3.util.FastMath;

import com.vava33.jutils.VavaLogger;

import vava33.d2dplot.D2Dplot_global;

public class OrientSolucio implements Comparable<Object>{
    // statics aplicables a totes les solucions, es a dir caracteristiques de les solucions
    public static int grainIdent; // ens diu si al fitxer hi ha varies solucions(grans) o les d'un sol gra (i properes), 0 o N
    public static int hasFc; // variable de classe que indica si s'especifiquen els factors d'estructura (0=no, 1=si)
    public static int numSolucions; // variable de classe que indica el numero de solucions que hi ha
    public static boolean isPXY = false; //ens diu si la "solucio" prové d'un fitxer SOL (dinco) o d'un fitxer PXY (tts_reduc)
    public static boolean isPCS = false;
    
    public static int getGrainIdent() {return grainIdent;}
    public static int getHasFc() {return hasFc;}
    public static int getNumSolucions() {return numSolucions;}
    public static void setGrainIdent(int grainIdent) {OrientSolucio.grainIdent = grainIdent;}
    public static void setHasFc(int hasFc) {OrientSolucio.hasFc = hasFc;}
    public static void setNumSolucions(int numSolucions) {OrientSolucio.numSolucions = numSolucions;}

    private Color colorSolucio;
    private int grainNr; // numero de gra segons diu el fitxer de solucio (com un identificador)
    private int numReflexions;
    private int numSolucio;
    private ArrayList<PuntSolucio> sol; // contindra els punts de la solucio
    private ArrayList<Peak> peaksPCS; // contindra els pics en cas que sigui un PCS
    private float numRefCoincidents; // valor de la funcio de rotacio de la solucio en questio -->ARA JA NO, ES EL NOMBRE DE REFLEXIONS COINCIDENTS
    private float angR_lon,angS_lat,angT_spin;
    private static VavaLogger log = D2Dplot_global.getVavaLogger(OrientSolucio.class.getName());

//  private boolean showSol; // mostra la solucio o no la mostra
    
    // crea una nova solucio
    public OrientSolucio(int nsol) {
        this.sol = new ArrayList<PuntSolucio>(); // inicialitzem
        // assignem el color
        this.numSolucio = nsol;
        // El color de la solucio dependra del numero, en tindrem 10 de
        // diferents... suposo que es suficient
        this.assignaColorSol();
        this.angR_lon=-1;
        this.angS_lat=-1;
        this.angT_spin=-1;
    }

    // afegeix un punt solucio donats els seus PIXELS x,y
    public void addSolPoint(int refNum, float x, float y, int ih, int ik, int il, float festructura, float foscil) {
        sol.add(new PuntSolucio(refNum, x, y, ih, ik, il, festructura, foscil, this.colorSolucio));
        this.renumberSOLpoints();
    }
    
    public void removeSolPoint(PuntSolucio ps){
        this.getSol().remove(ps);
        this.renumberSOLpoints();
    }
    
    // afegeix un punt solucio donat UNICAMENT els PIXELS x,y
    //from click significa que ve d'un click i per tant que el farem més fosc
    public void addSolPoint(float x, float y, boolean FromClick) {
        PuntSolucio ps;
        if (isPXY()){
            int[] ihkl = this.getNextSOLnumberAsHKL();
            ps = new PuntSolucio(this.getNextSOLnumber(),x, y,ihkl[0],ihkl[1],ihkl[2],0.1f,0.0f);
        }else{
            //PREGUNTEM HKL,FSTRUCT AND OSCIL?
            JPanel myPanel = new JPanel();
            JTextField txtHKL = new JTextField(10);
            JTextField txtFstruc = new JTextField(10);
            JTextField txtOscil = new JTextField(10);
            txtHKL.setText("h k l");
            txtFstruc.setText("0.1");
            txtOscil.setText("0.0");
            myPanel.add(txtHKL);
            myPanel.add(txtFstruc);
            myPanel.add(txtOscil);
            JOptionPane.showMessageDialog(null, myPanel);
            
            //intentem treure els inputs:
            int ih=0;
            int ik=0;
            int il=0;
            float fstruc=0.1f;
            float foscil=0.0f;
            
            try{
                String shkl[] = txtHKL.getText().split("\\s+");
                ih = Integer.parseInt(shkl[0]);
                ik = Integer.parseInt(shkl[1]);
                il = Integer.parseInt(shkl[2]);
            }catch(Exception e){
                if(D2Dplot_global.isDebug())e.printStackTrace();
                log.warning("Error parsing hkl");
            }
            
            try{
                fstruc = Float.parseFloat(txtFstruc.getText());    
            }catch(Exception e){
                if(D2Dplot_global.isDebug())e.printStackTrace();
                log.warning("Error parsing fstruct");
            }
            
            try{
                foscil = Float.parseFloat(txtOscil.getText());  
            }catch(Exception e){
                if(D2Dplot_global.isDebug())e.printStackTrace();
                log.warning("Error parsing oscil");
            }
            ps = new PuntSolucio(this.getNextSOLnumber(),x, y, ih, ik, il, fstruc, foscil);
        }
        
        if (FromClick){
            ps.setColorPunt(this.getColorSolucio().darker());
            ps.setManuallyAdded(true);
        }
        sol.add(ps);
        this.renumberSOLpoints();

    }

    public PuntSolucio getSolPoint(int nr){
        return sol.get(nr);
    }
    
    public PuntSolucio getLastSolPoint(){
        return sol.get(sol.size()-1);
    }
    
    public void clearSolPoints() {
        sol.clear();
    }

    public void repintaSolucio(){
    	Iterator<PuntSolucio> itrPunts = sol.iterator();
    	while (itrPunts.hasNext()){
    		PuntSolucio ps = itrPunts.next();
    		ps.setColorPunt(this.colorSolucio);
    	}
    }
    
    public void assignaColorSol(){
        if (numSolucions != 0) {
            int opt = this.getNumSolucio() % 8;
            switch (opt) {
                case 0:this.colorSolucio = Color.green;break;
                case 1:this.colorSolucio = Color.red;break;
                case 2:this.colorSolucio = Color.cyan;break;
                case 3:this.colorSolucio = Color.yellow;break;
                case 4:this.colorSolucio = Color.magenta;break;
                case 5:this.colorSolucio = Color.orange;break;
                case 6:this.colorSolucio = Color.pink;break;
                case 7:this.colorSolucio = Color.blue;break;
            }
        } else {
            this.colorSolucio = Color.green;
        }
        this.repintaSolucio();
    }
    
    public int getGrainNr() {
        return grainNr;
    }

    public int getNumReflexions() {
        return numReflexions;
    }

    public int getNumSolucio() {
        return numSolucio;
    }

    public static boolean isPXY() {
        return isPXY;
    }
    public static void setPXY(boolean isPXY) {
        OrientSolucio.isPXY = isPXY;
    }
    
    public ArrayList<PuntSolucio> getSol() {
        return sol;
    }

    public float getNrefsCoincidents() {
        return numRefCoincidents;
    }

    public void setGrainNr(int grainNr) {
        this.grainNr = grainNr;
    }

    public void setNumReflexions(int numReflexions) {
        this.numReflexions = numReflexions;
    }

    public void setNumSolucio(int numSolucio) {
        this.numSolucio = numSolucio;
    }

    public void setSol(ArrayList<PuntSolucio> sol) {
        this.sol = sol;
    }

    public void setNrefCoincidents(float nrefs) {
        this.numRefCoincidents = nrefs;
    }
    
    public Color getColorSolucio() {
        return colorSolucio;
    }
    public void setColorSolucio(Color colorSolucio) {
        this.colorSolucio = colorSolucio;
    }
    
    public float getAngR_lon() {
        return angR_lon;
    }
    public void setAngR_lon(float angR_lon) {
        this.angR_lon = angR_lon;
    }
    public float getAngS_lat() {
        return angS_lat;
    }
    public void setAngS_lat(float angS_lat) {
        this.angS_lat = angS_lat;
    }
    public float getAngT_spin() {
        return angT_spin;
    }
    public void setAngT_spin(float angT_spin) {
        this.angT_spin = angT_spin;
    }
    public static boolean isPCS() {
        return isPCS;
    }
    public static void setPCS(boolean isPCS) {
        OrientSolucio.isPCS = isPCS;
    }
    public ArrayList<Peak> getPeaksPCS() {
        if (peaksPCS==null){
            this.peaksPCS = new ArrayList<Peak>();
        }
        return peaksPCS;
    }
    public void setPeaksPCS(ArrayList<Peak> peaksPCS) {
        this.peaksPCS = peaksPCS;
    }
    public void renumberSOLpoints(){
        Iterator<PuntSolucio> itrps = sol.iterator();
        while (itrps.hasNext()){
            PuntSolucio ps = itrps.next();
            ps.setSeqNumber(sol.indexOf(ps)+1);
        }
    }
    
    //numero sequencial
    public int getNextSOLnumber(){
        if (sol!=null){
            return sol.size()+1;
        }else{
            return 0;
        }
    }
    
    //numero sequencial
    public int[] getNextSOLnumberAsHKL(){
        if (sol!=null){
            String shkl = String.format("%03d", getNextSOLnumber());
            char[] chkl = shkl.toCharArray();
            int[] ihkl = new int[]{Character.getNumericValue(chkl[0]),Character.getNumericValue(chkl[1]),Character.getNumericValue(chkl[2])};
            return ihkl;
        }else{
            return new int[]{0,0,0};
        }
    }
    
    //el punt ha d'estar mes aprop que maxPixDist
    public PuntSolucio getNearestPS(float px, float py, float maxPixDist){
        // d'aquesta solucio, es busca el puntSolucio m�s proper al punt entrat
        if (maxPixDist<0){
            //default
            maxPixDist = 5.0f;
        }
        Iterator<PuntSolucio> itrS = this.getSol().iterator();
        PuntSolucio nearestPS = null;
        float minModul = maxPixDist;
        while (itrS.hasNext()) {
            PuntSolucio s = itrS.next();
            float modul = (float) FastMath.sqrt((s.getCoordX()-px)*(s.getCoordX()-px)+(s.getCoordY()-py)*(s.getCoordY()-py));
            if (modul<minModul){
                nearestPS = s;
                minModul=modul;
            }
        }
        return nearestPS;
    }

    public int compareTo(Object arg0) { //EN TOT CAS HAURIEM D'ORDENAR PER VALOR FROT (nrefs coincidents) pero ara al SOL ja surt ordenat
        try{
            OrientSolucio os=(OrientSolucio)arg0;
            if(this.getNrefsCoincidents()>os.getNrefsCoincidents()){
              return 1;
            }
            if(this.getNrefsCoincidents()<os.getNrefsCoincidents()){
              return -1;
            }
            //si son iguals mirem el num de solucio
            if(this.getNumSolucio()>os.getNumSolucio()){
                return -1;
            }
            if(this.getNumSolucio()<os.getNumSolucio()){
                return 1;
            }
            return 0;
        }catch(Exception e){
            if(D2Dplot_global.isDebug())e.printStackTrace();
            log.warning("OrientSolucio: comparing different object types");
          return 0;
        }
    }
    
    @Override
    public String toString(){
        if (this.angR_lon>-1){
            return String.format("%3d %5d  %.0f alon=%.3f alat=%.3f aspin=%.3f ", this.getGrainNr(), this.getNumReflexions(), this.getNrefsCoincidents(), this.getAngR_lon(),this.getAngS_lat(),this.getAngT_spin());
        }else{
            return String.format("%3d %5d  %.0f", this.getGrainNr(), this.getNumReflexions(), this.getNrefsCoincidents());    
        }
        
    }
}
