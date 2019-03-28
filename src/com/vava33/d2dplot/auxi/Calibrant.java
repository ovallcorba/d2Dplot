package com.vava33.d2dplot.auxi;

public class Calibrant {

    public static float[] LaB6_d = { 4.156878635f, 2.939357609f, 2.399975432f, 2.078432243f, 1.859004281f, 1.697043447f,
            1.469674856f, 1.385628455f, 1.314520218f, 1.25335391f, 1.199991704f, 1.152911807f, 1.110975349f,
            1.039218708f, 1.008191043f };

    public static float[] Silicon_d = { 3.1356938f, 1.9202274f, 1.6375760f, 1.3578002f, 1.2460120f, 1.1086449f,
            1.0452367f, 0.9601140f, 0.9180468f, 0.8587511f, 0.8282547f };

    private String name;
    private float[] dsp;

    public Calibrant(String calName, float[] dsps) {
        this.setName(calName);
        this.setDsp(dsps);
    }

    public Calibrant(String calName) {
        this.setName(calName);
    }

    /**
     * @return the name
     */
    public String getName() {
        return this.name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the dsp
     */
    public float[] getDsp() {
        return this.dsp;
    }

    /**
     * @param dsp the dsp to set
     */
    public void setDsp(float[] dsp) {
        this.dsp = dsp;
    }

}
