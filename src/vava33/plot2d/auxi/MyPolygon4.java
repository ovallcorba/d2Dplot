package vava33.plot2d.auxi;

import java.awt.Polygon;

public class MyPolygon4 extends Polygon {

    int[] defXpoints = {200,400,400,200};
    int[] defYpoints = {200,200,400,400};

    //genera un poligon de 4 vertexs per defecte: rectangle 200,200,200,200 (x,y,w,h)
    public MyPolygon4(){
        super();
        this.xpoints=defXpoints;
        this.ypoints=defYpoints;
        this.npoints=4;
    }
    
    public MyPolygon4(int v1x, int v1y, int v2x, int v2y, int v3x, int v3y, int v4x, int v4y){
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
        this.xpoints[i]=this.xpoints[i]+Math.round(val);
    }
    public void incYVertex(int i,float val){
        this.ypoints[i]=this.ypoints[i]+Math.round(val);
    }
    
    public String getXYvertex(int i){
        return this.xpoints[i]+" "+this.ypoints[i];
    }
}
