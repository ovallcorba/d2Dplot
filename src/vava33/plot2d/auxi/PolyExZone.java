package vava33.plot2d.auxi;

import java.awt.Polygon;
import java.awt.geom.Point2D;

import org.apache.commons.math3.util.FastMath;

public class PolyExZone extends Polygon {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    int[] defXpoints = {200,400,400,200};
    int[] defYpoints = {200,200,400,400};

    //genera un poligon de 4 vertexs per defecte: rectangle 200,200,200,200 (x,y,w,h)
    public PolyExZone(boolean defaultPol4){
        super();
        if (defaultPol4){
            this.xpoints=defXpoints;
            this.ypoints=defYpoints;
            this.npoints=4;
        }
        else{
            this.npoints=0;
            //new empty poligon
        }
    }
    
    public PolyExZone(int v1x, int v1y, int v2x, int v2y, int v3x, int v3y, int v4x, int v4y){
        super();
        this.addPoint(v1x, v1y);
        this.addPoint(v2x, v2y);
        this.addPoint(v3x, v3y);
        this.addPoint(v4x, v4y);
        this.npoints=4;
    }
    
    public String printLnVertexs(){
        StringBuilder sb = new StringBuilder();
        for(int i=0;i<this.npoints;i++){
            sb.append(this.xpoints[i]+" "+this.ypoints[i]+" ");
        }
        return sb.toString().trim();
    }
    
    //retorna la X del vertex i
    public int getXVertex(int i){
        return this.xpoints[i];
    }
    public int getYVertex(int i){
        return this.ypoints[i];
    }
    
    //incrementa +val a la X del vertex i
    public void incXVertex(int i,float val){
        this.xpoints[i]=this.xpoints[i]+FastMath.round(val);
    }
    public void incYVertex(int i,float val){
        this.ypoints[i]=this.ypoints[i]+FastMath.round(val);
    }
    
    public String getXYvertex(int i){
        return this.xpoints[i]+" "+this.ypoints[i];
    }
    
    
    public boolean contains2(Point2D.Float test) {
      int[] xs = this.xpoints;
      int[] ys = this.ypoints;
      int i;
      int j;
      boolean result = false;
      for (i = 0, j = xs.length - 1; i < xs.length; j = i++) {
        if ((ys[i] > test.y) != (ys[j] > test.y) &&
            (test.x < (xs[j] - xs[i]) * (test.y - ys[i]) / (ys[j]-ys[i]) + xs[i])) {
          result = !result;
         }
      }
      return result;
    }
}
