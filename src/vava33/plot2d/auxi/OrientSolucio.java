package vava33.plot2d.auxi;

import java.awt.Color;
import java.util.ArrayList;

public class OrientSolucio implements Comparable{
    // statics aplicables a totes les solucions, es a dir caracteristiques de les solucions
    public static int grainIdent; // ens diu si al fitxer hi ha varies solucions(grans) o les d'un sol gra (i properes), 0 o N
    public static int hasFc; // variable de classe que indica si s'especifiquen els factors d'estructura (0=no, 1=si)
    public static int numSolucions; // variable de classe que indica el numero de solucions que hi ha

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
    private boolean showSol; // mostra la solucio o no la mostra
    private ArrayList<PuntSolucio> sol; // contindra els punts de la solucio
    private float valorFrot; // valor de la funcio de rotacio de la solucio en questio
    
    // crea una nova solucio
    public OrientSolucio(int nsol) {
        this.sol = new ArrayList<PuntSolucio>(); // inicialitzem
        showSol = false;
        // assignem el color
        this.numSolucio = nsol;
        // El color de la solucio dependra del numero, en tindrem 10 de
        // diferents... suposo que es suficient
        if (numSolucions != 0) {
            int opt = nsol % 9;
            switch (opt) {
                case 0:this.colorSolucio = Color.green;break;
                case 1:this.colorSolucio = Color.red;break;
                case 2:this.colorSolucio = Color.cyan;break;
                case 3:this.colorSolucio = Color.yellow;break;
                case 4:this.colorSolucio = Color.magenta;break;
                case 5:this.colorSolucio = Color.orange;break;
                case 6:this.colorSolucio = Color.pink;break;
                case 7:this.colorSolucio = Color.blue;break;
                case 8:this.colorSolucio = Color.lightGray;break;
            // case 9:this.colorSolucio=Color.darkGray;break;
            }
        } else {
            this.colorSolucio = Color.green;
        }
    }

    // afegeix un punt solucio donats els seus PIXELS x,y
    public void addSolPoint(float x, float y, int ih, int ik, int il, float festructura, float foscil) {
        // int midaPunt = 8;
        sol.add(new PuntSolucio(x, y, ih, ik, il, festructura, foscil, this.colorSolucio));
    }

    public void clearSolPoints() {
        sol.clear();
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

    public ArrayList<PuntSolucio> getSol() {
        return sol;
    }

    public float getValorFrot() {
        return valorFrot;
    }

    public boolean isShowSol() {
        return showSol;
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

    public void setShowSol(boolean showSol) {
        this.showSol = showSol;
    }

    public void setSol(ArrayList<PuntSolucio> sol) {
        this.sol = sol;
    }

    public void setValorFrot(float valorFrot) {
        this.valorFrot = valorFrot;
    }
    
//    //Compares its two arguments for order. Returns a negative 
//    //integer, zero, or a positive integer as the first argument 
//    //is less than, equal to, or greater than the second.
//    public int compare(Object arg1, Object arg2) {
//        try{
//            OrientSolucio os1=(OrientSolucio)arg1;
//            OrientSolucio os2=(OrientSolucio)arg2;
//            if(os1.getValorFrot()>os2.getValorFrot()){
//                return 1;
//            }
//            if(os1.getValorFrot()<os2.getValorFrot()){
//                return -1;
//            }
//            return 0;
//        }catch(Exception e){
//            e.printStackTrace();
//            System.err.println("comparing different object types");
//            return 0;
//        }
//    }
    @Override
    //Compares this object with the specified object for order. Returns a 
    //negative integer, zero, or a positive integer as this object is less 
    //than, equal to, or greater than the specified object. 
    public int compareTo(Object arg0) {
        try{
            OrientSolucio os=(OrientSolucio)arg0;
            if(this.getValorFrot()>os.getValorFrot()){
              return 1;
            }
            if(this.getValorFrot()<os.getValorFrot()){
              return -1;
            }
            return 0;
        }catch(Exception e){
          e.printStackTrace();
          System.err.println("comparing different object types");
          return 0;
        }
    }
}
