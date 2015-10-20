package vava33.plot2d.auxi;

public class PDReflection {

    
        private int h;
        private int k;
        private int l;
        private float dsp;
        private float inten;
        
        public PDReflection(int h, int k, int l, float dsp, float inten){
            this.h=h;
            this.k=k;
            this.l=l;
            this.dsp=dsp;
            this.inten=inten;
        }
        
        public int getH() {
            return h;
        }
        public void setH(int h) {
            this.h = h;
        }
        public int getK() {
            return k;
        }
        public void setK(int k) {
            this.k = k;
        }
        public int getL() {
            return l;
        }
        public void setL(int l) {
            this.l = l;
        }
        public float getDsp() {
            return dsp;
        }
        public void setDsp(float dsp) {
            this.dsp = dsp;
        }
        public float getInten() {
            return inten;
        }
        public void setInten(float inten) {
            this.inten = inten;
        }    
}
