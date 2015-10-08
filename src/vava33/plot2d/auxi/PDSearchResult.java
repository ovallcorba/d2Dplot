package vava33.plot2d.auxi;

public class PDSearchResult implements Comparable<PDSearchResult>{
    private PDCompound c;
    private float residual_positions;
    private float residual_intensities;
    private static float minDSPin;  //minimum dspacing entered in the search peaks
    private static int nDSPin; //number of peaks entered in the search
    
    public PDSearchResult(PDCompound comp, float res, float resInten){
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
    public int compareTo(PDSearchResult arg0) {
        float comparedR = arg0.getResidual();
        float diff = this.residual_positions - comparedR;
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
        return String.format("%7.3f  %d/%d  %s  [%s]",this.getResidual(),nDSPin,this.getC().getNrRefUpToDspacing(minDSPin),this.getC().getCompName(),this.getC().getFormula());
    }
}
