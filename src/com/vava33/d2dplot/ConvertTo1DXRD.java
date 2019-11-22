package com.vava33.d2dplot;

import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.vava33.d2dplot.auxi.ImgOps;
import com.vava33.d2dplot.auxi.Pattern1D;
import com.vava33.d2dplot.auxi.Pattern1D.PointPatt1D;
import com.vava33.d2dplot.d1dplot.DataPoint;
import com.vava33.d2dplot.d1dplot.DataSerie;
import com.vava33.d2dplot.d1dplot.PlotPanel;
import com.vava33.jutils.FileUtils;
import com.vava33.jutils.VavaLogger;

import net.miginfocom.swing.MigLayout;

public class ConvertTo1DXRD {

    private final JDialog D1dialog;
    private final JPanel contentPane;
    private final JTextField txt_2ti;
    private final JTextField txt_2tf;
    private final JTextField txt_step;
    private static String theta = "\u03B8";
    private ArrayList<Pattern1D> patt1D;
    private static final String className = "1DPXRD";
    private static VavaLogger log = D2Dplot_global.getVavaLogger(className);

    //	private Pattern2D patt2D;
    private final PlotPanel plotpanel;
    private final JButton btn_save;
    private final JLabel lblCakeIni;
    private final JLabel lblCakeEnd;
    private final JTextField txtCakein;
    private final JTextField txtCakefin;
    private final JButton btnIntegrar;
    private final JLabel lblAzimBins;
    private final JTextField txtAzimBins;
    private final JButton btnSetMin;
    private final JButton btnSetMax;
    private final JLabel lblSubtractI;
    private final JTextField txtZeroval;

    private final ImagePanel ip;
//    private File maskfile;
    private final JButton btnClose;

    com.vava33.d2dplot.d1dplot.Pattern1D p1;

    /**
     * Create the frame.
     */
    public ConvertTo1DXRD(JFrame parent, ImagePanel ipanel) {
        this.ip = ipanel;
        this.D1dialog = new JDialog(parent, "Integration of 2DXRD to 1DXRD", false);
        //	    this.inicia();
        this.D1dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        this.D1dialog.setBounds(100, 100, 940, 540);
        this.D1dialog.setIconImage(Toolkit.getDefaultToolkit().getImage(Help.class.getResource("/img/Icona.png")));
        this.contentPane = new JPanel();
        this.contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        this.D1dialog.setContentPane(this.contentPane);
        this.contentPane.setLayout(new MigLayout("", "[][grow][][][grow][][grow][grow]", "[][][][grow][]"));

        final JLabel lbltini = new JLabel("2" + theta + " ini");
        this.contentPane.add(lbltini, "cell 0 0,alignx right");

        this.txt_2ti = new JTextField();
        this.txt_2ti.setText("1.00");
        this.contentPane.add(this.txt_2ti, "cell 1 0 2 1,growx");

        this.lblCakeIni = new JLabel("Cake ini");
        this.contentPane.add(this.lblCakeIni, "cell 3 0,alignx trailing");

        this.txtCakein = new JTextField();
        this.txtCakein.setText("0");
        this.contentPane.add(this.txtCakein, "cell 4 0,growx");

        this.btnIntegrar = new JButton("Integrate");
        this.btnIntegrar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                ConvertTo1DXRD.this.do_btnIntegrartilt_actionPerformed(arg0);
            }
        });

        this.lblSubtractI = new JLabel("Add I");
        this.lblSubtractI.setToolTipText(
                "Subtract intensity to all pixels (useful if detector is adding a value to avoid negative intensities)");
        this.contentPane.add(this.lblSubtractI, "cell 5 0,alignx trailing");

        this.txtZeroval = new JTextField();
        this.txtZeroval.setText("-9.5");
        this.contentPane.add(this.txtZeroval, "cell 6 0,growx");
        this.contentPane.add(this.btnIntegrar, "cell 7 0,growx");

        final JLabel lblStep = new JLabel("Step");
        this.contentPane.add(lblStep, "cell 0 1,alignx right");

        this.txt_step = new JTextField();
        this.txt_step.setText("0.01");
        this.contentPane.add(this.txt_step, "cell 1 1,growx");

        this.btnSetMin = new JButton("set Min");
        this.btnSetMin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                ConvertTo1DXRD.this.do_btnSetMin_actionPerformed(arg0);
            }
        });
        this.btnSetMin.setMargin(new Insets(2, 2, 2, 2));
        this.contentPane.add(this.btnSetMin, "cell 2 1");

        this.lblCakeEnd = new JLabel("Cake end");
        this.contentPane.add(this.lblCakeEnd, "cell 3 1,alignx trailing");

        this.txtCakefin = new JTextField();
        this.txtCakefin.setText("360");
        this.contentPane.add(this.txtCakefin, "cell 4 1,growx");

        this.btn_save = new JButton("Save");
        this.btn_save.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                ConvertTo1DXRD.this.do_btn_save_actionPerformed(arg0);
            }
        });
        this.contentPane.add(this.btn_save, "cell 7 1,growx");

        final JLabel lbltfin = new JLabel("2" + theta + " end");
        this.contentPane.add(lbltfin, "cell 0 2,alignx right");

        this.txt_2tf = new JTextField();
        this.txt_2tf.setText("40.0");
        this.contentPane.add(this.txt_2tf, "cell 1 2,growx");

        this.btnSetMax = new JButton("set Max");
        this.btnSetMax.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ConvertTo1DXRD.this.do_btnSetMax_actionPerformed(e);
            }
        });
        this.btnSetMax.setMargin(new Insets(2, 2, 2, 2));
        this.contentPane.add(this.btnSetMax, "cell 2 2");

        this.lblAzimBins = new JLabel("Azim Bins");
        this.contentPane.add(this.lblAzimBins, "cell 3 2,alignx trailing");

        this.txtAzimBins = new JTextField();
        this.txtAzimBins.setText("1");
        this.contentPane.add(this.txtAzimBins, "cell 4 2,growx");

        this.plotpanel = new PlotPanel();
        this.contentPane.add(this.plotpanel.getPlotPanel(), "cell 0 3 8 1,grow");

        this.btnClose = new JButton("Close");
        this.btnClose.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ConvertTo1DXRD.this.do_btnClose_actionPerformed(e);
            }
        });
        this.contentPane.add(this.btnClose, "cell 7 4,alignx right,aligny center");

        this.btnSetMin.doClick();
        this.btnSetMax.doClick();
        this.patt1D = new ArrayList<Pattern1D>();
        this.p1 = new com.vava33.d2dplot.d1dplot.Pattern1D();
    }

    //	public void inicia(){
    //	    this.patt2D=ip.getPatt2D();
    //	}

    protected void do_btn_save_actionPerformed(ActionEvent arg0) {
        if (this.patt1D.size() > 0) {
            File fileout;
            final FileNameExtensionFilter[] filter = {
                    new FileNameExtensionFilter("Data file (2T I ESD)", "dat", "xy") };
            fileout = FileUtils.fchooserSaveAsk(null, new File(MainFrame.getWorkdir()), filter, "dat");
            //	        if(!FileUtils.getExtension(fileout).equalsIgnoreCase("dat")||!FileUtils.getExtension(fileout).equalsIgnoreCase("xy")){
            //	            fileout = FileUtils.canviExtensio(fileout, "dat");  
            //	        }
            if (fileout != null) {
                this.savePatterns(fileout);
            }
        }
    }

    public void savePatterns(File fileout) {
        String title = "d2Dplot 2D to 1D conversion";
        if (this.patt1D.size() == 1) {
            try {
                title = this.ip.getPatt2D().getImgfile().toString();
            } catch (final NullPointerException ex) {
                log.debug("no imgfile assigned to opened image");
            }

            this.patt1D.get(0).writeDAT(fileout, title);
            log.info(String.format("file %s written", fileout.toString()));
        } else {
            for (int i = 0; i < this.patt1D.size(); i++) {
                final File fout = FileUtils.canviNomFitxer(fileout,
                        FileUtils.getFNameNoExt(fileout.getName()) + String.format("_azbin%02d", i));
                this.patt1D.get(i).writeDAT(fout, title);
                log.info(String.format("file %s written", fout.toString()));
            }
        }
    }

    public void setTxtT2i(float t2i) {
        this.txt_2ti.setText(Float.toString(t2i));
    }

    public void setTxtT2f(float t2f) {
        this.txt_2tf.setText(Float.toString(t2f));
    }

    public void setTxtStep(float step) {
        this.txt_step.setText(Float.toString(step));
    }

    public void setTxtCakeIn(float cakeinDeg) {
        this.txtCakein.setText(Float.toString(cakeinDeg));
    }

    public void setTxtCakeFin(float cakefinDeg) {
        this.txtCakefin.setText(Float.toString(cakefinDeg));
    }

    public void setTxtSubadu(float subadu) {
        this.txtZeroval.setText(Float.toString(subadu));
    }

    public void setTxtAzimbins(int abins) {
        this.txtAzimBins.setText(Integer.toString(abins));
    }

    public float getTxtT2i() {
        try {
            return Float.parseFloat(this.txt_2ti.getText());
        } catch (final Exception e) {
            if (D2Dplot_global.isDebug())
                e.printStackTrace();
            log.warning("Error parsing initial 2theta");
        }
        return -1f;
    }

    public float getTxtT2f() {
        try {
            return Float.parseFloat(this.txt_2tf.getText());
        } catch (final Exception e) {
            if (D2Dplot_global.isDebug())
                e.printStackTrace();
            log.warning("Error parsing final 2theta");
        }
        return -1f;
    }

    public float getTxtStep() {
        try {
            return Float.parseFloat(this.txt_step.getText());
        } catch (final Exception e) {
            if (D2Dplot_global.isDebug())
                e.printStackTrace();
            log.warning("Error parsing stepsize");
        }
        return -1f;
    }

    public float getTxtCakein() {
        try {
            return Float.parseFloat(this.txtCakein.getText());
        } catch (final Exception e) {
            if (D2Dplot_global.isDebug())
                e.printStackTrace();
            log.warning("Error parsing start azimuth");
        }
        return -1f;
    }

    public float getTxtCakefin() {
        try {
            return Float.parseFloat(this.txtCakefin.getText());
        } catch (final Exception e) {
            if (D2Dplot_global.isDebug())
                e.printStackTrace();
            log.warning("Error parsing end azimuth");
        }
        return -1f;
    }

    public float getTxtZeroval() {
        try {
            return Float.parseFloat(this.txtZeroval.getText());
        } catch (final Exception e) {
            if (D2Dplot_global.isDebug())
                e.printStackTrace();
            log.warning("Error parsing zero intensity level");
        }
        return -1f;
    }

    public int getTxtAzimBins() {
        try {
            return Integer.parseInt(this.txtAzimBins.getText());
        } catch (final Exception e) {
            if (D2Dplot_global.isDebug())
                e.printStackTrace();
            log.warning("Error parsing Azimuthal bins");
        }
        return -1;
    }

    private void plotPattern(Pattern1D p, boolean appendPatt, String seriesName) {

        final DataSerie ds = new DataSerie();

        ds.setWavelength(this.ip.getPatt2D().getWavel());

        final Iterator<PointPatt1D> itp = p.getPoints().iterator();
        while (itp.hasNext()) {
            final PointPatt1D point = itp.next();
            log.fine(point.getT2() + "," + point.getCounts());
            ds.addPoint(new DataPoint(point.getT2(), point.getIntensity(), 0.f));
        }

        ds.setSerieName(seriesName);
        this.p1.addDataSerie(ds);
        this.plotpanel.fitGraph();
    }

    public void do_btnIntegrartilt_actionPerformed(ActionEvent arg0) {
        this.patt1D.clear();
        if (this.ip.getPatt2D() == null)
            return;
        if ((!this.ip.getPatt2D().checkIfDistMD()) || (!this.ip.getPatt2D().checkIfPixSize())) {
            log.warning("Instrumental parameters missing");
            return;
        }
        double t2ini = 1.0;
        double t2fin = 40.0;
        double stepsize = -0.01;
        try {
            t2ini = Double.parseDouble(this.txt_2ti.getText());
            t2fin = Double.parseDouble(this.txt_2tf.getText());
            stepsize = Double.parseDouble(this.txt_step.getText());
        } catch (final Exception e) {
            if (D2Dplot_global.isDebug())
                e.printStackTrace();
            JOptionPane.showMessageDialog(this.D1dialog, "Check input angles and step", "Incorrect values",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        //comprovacio que el minim stepsize no sigui inferior al entrepixels TODO: NOV18 desactivat
        //        float minstep = this.patt2D.calcMinStepsizeBy2Theta4Directions();
        //        if (stepsize < minstep){
        //            stepsize = minstep;
        //        }

        //t2fin dins dels limits: --ho trec a veure que passa (util quan el centre no es al centre)
        //        t2fin = FastMath.min(t2fin, patt2D.getMax2TdegCircle() - 2*stepsize);

        //cake
        double cakein = -1;
        double cakeout = -1;
        try {
            cakein = Double.parseDouble(this.txtCakein.getText());
            cakeout = Double.parseDouble(this.txtCakefin.getText());
        } catch (final Exception e) {
            log.info("Taking default cake value, full pattern (0-360)");
        }

        //subadu
        double subadu = 0.0;
        try {
            subadu = Double.parseDouble(this.txtZeroval.getText());
        } catch (final Exception e) {
            log.info("Taking default zeroIVal value, 0");
        }

        //AZIM BINS
        int azimBins = 1;
        try {
            azimBins = Integer.parseInt(this.txtAzimBins.getText());
        } catch (final Exception e) {
            log.info("Taking default azimuthal bins value, 1");
        }
        double azimRange = cakeout - cakein;
        if (cakeout < cakein)
            azimRange = 360 + azimRange;
        final double azimInc = azimRange / azimBins;

        final boolean corrLP = true;
        final boolean corrInAng = false;

        //NOW WE INTEGRATE ALL THE BINS
        this.p1.removeAllSeries();
        this.plotpanel.getPatterns().clear();
        this.plotpanel.getPatterns().add(this.p1);
        for (int k = 0; k < azimBins; k++) {

            double azIni = cakein + azimInc * k;
            if (azIni > 360)
                azIni = azIni - 360.0;
            double azFin = azIni + azimInc;
            if (azFin > 360)
                azFin = azFin - 360.0;

            try {
                final Pattern1D p1D = ImgOps.radialIntegration(this.ip.getPatt2D(), t2ini, t2fin, stepsize, azIni,
                        azFin, corrLP, corrInAng, subadu);
                final String comment = String.format(
                        "I vs. 2Theta [deg] t2i step t2f: %.4f %.4f %.4f Wave: %.4f Azim: %.2f %.2f addI: %.1f", t2ini,
                        stepsize, t2fin, this.ip.getPatt2D().getWavel(), azIni, azFin, subadu);
                p1D.setComment(comment);
                this.patt1D.add(p1D);
            } catch (final Exception e) {
                log.warning("Error during radial integration");
                if (D2Dplot_global.isDebug())
                    e.printStackTrace();
                return;
            }

            if (k == 0) {//new plot
                this.plotPattern(this.patt1D.get(this.patt1D.size() - 1), false,
                        String.format("azRange= %.1f to %.1f", azIni, azFin));
            } else {//append
                this.plotPattern(this.patt1D.get(this.patt1D.size() - 1), true,
                        String.format("azRange= %.1f to %.1f", azIni, azFin));
            }
        }

    }

    protected void do_btnSetMin_actionPerformed(ActionEvent arg0) {
        this.txt_step.setText(FileUtils.dfX_4.format(this.ip.getPatt2D().calcMinStepsizeBy2Theta4Directions()));
    }

    protected void do_btnSetMax_actionPerformed(ActionEvent e) {
        this.txt_2tf.setText(FileUtils.dfX_2.format(this.ip.getPatt2D().getMax2TdegCircle()
                - 5 * this.ip.getPatt2D().calcMinStepsizeBy2Theta4Directions()));
    }

    public ArrayList<Pattern1D> getPatt1D() {
        return this.patt1D;
    }

    public void setPatt1D(ArrayList<Pattern1D> patt1d) {
        this.patt1D = patt1d;
    }

//    public File getMaskfile() {
//        return this.maskfile;
//    }
//
//    public void setMaskfile(File maskfile) {
//        this.maskfile = maskfile;
//    }

    public void dispose() {
        this.D1dialog.dispose();
    }

    public void setVisible(boolean vis) {
        this.D1dialog.setVisible(vis);
    }

    protected void do_btnClose_actionPerformed(ActionEvent e) {
        this.dispose();
    }
}
