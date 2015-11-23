package vava33.d2dplot.auxi;

public class PDSearchResult implements Comparable<PDSearchResult>{
    private PDCompound c;
    private float residual_positions; //es el residual basic per defecte
    private float residual_intensities;
    private float total_residual; //el que serà la suma de les opcions triades (i es mostrarà en pantalla)
    private static float minDSPin;  //minimum dspacing entered in the search peaks
    private static int nDSPin; //number of peaks entered in the search
    
    public PDSearchResult(PDCompound comp, float res, float resInten){
        this.c=comp;
        this.residual_positions=res;
        this.residual_intensities=resInten;
        //by default the total_residual will be the position residual only
        this.total_residual=res;
    }
    
    public float getResidualPositions() {
        return residual_positions;
    }
    public float getResidual_intensities() {
        return residual_intensities;
    }
    public float getTotal_residual() {
        return total_residual;
    }
    public void setTotal_residual(float total_residual) {
        this.total_residual = total_residual;
    }

    public PDCompound getC() {
        return c;
    }
    public void setC(PDCompound c) {
        this.c = c;
    }

    @Override
    public int compareTo(PDSearchResult arg0) {
        float comparedR = arg0.getTotal_residual();
        float diff = this.getTotal_residual() - comparedR;
        if (diff > 0) return 1;
        if (diff < 0) return -1;
        return 0;
    }

    public static float getMinDSPin() {
        return minDSPin;
    }

    public static void setMinDSPin(float minDSPin) {
        PDSearchResult.minDSPin = minDSPin;
    }

    public static int getnDSPin() {
        return nDSPin;
    }

    public static void setnDSPin(int nDSPin) {
        PDSearchResult.nDSPin = nDSPin;
    }
    
    public String toString(){
        return String.format("%7.3f  %d/%d  %s  [%s] (aka: %s)",this.getTotal_residual(),nDSPin,this.getC().getNrRefUpToDspacing(minDSPin),this.getC().getCompName().get(0),this.getC().getFormula(),this.getC().getAltNames());
    }
}
