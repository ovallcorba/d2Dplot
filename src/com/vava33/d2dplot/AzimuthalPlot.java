package com.vava33.d2dplot;

import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
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

import org.apache.commons.math3.util.FastMath;

import com.vava33.d2dplot.auxi.EllipsePars;
import com.vava33.d2dplot.auxi.ImgOps;
import com.vava33.d2dplot.auxi.Patt2Dzone;
import com.vava33.d2dplot.auxi.Pattern1D;
import com.vava33.d2dplot.auxi.Pattern1D.PointPatt1D;
import com.vava33.d2dplot.d1dplot.DataPoint;
import com.vava33.d2dplot.d1dplot.DataSerie;
import com.vava33.d2dplot.d1dplot.PlotPanel;
import com.vava33.jutils.FileUtils;
import com.vava33.jutils.VavaLogger;

import net.miginfocom.swing.MigLayout;

public class AzimuthalPlot {

    private final JDialog azimDialog;
    private final JPanel contentPane;
    private final JTextField txt_2t;
    private ArrayList<Pattern1D> patt1D;
    private static final String className = "Azim_plot";
    private static VavaLogger log = D2Dplot_global.getVavaLogger(className);

    private final PlotPanel plotpanel;
    private final JButton btn_save;
    private final JLabel lblCakeIni;
    private final JLabel lblCakeEnd;
    private final JTextField txtT2w;
    private final JTextField txtAzStep;
    private final JButton btnIntegrar;

    private ImagePanel ip;
    private File maskfile;
    private final JButton btnClose;
    private final JLabel lblInfo;

    com.vava33.d2dplot.d1dplot.Pattern1D p1;

    /**
     * Create the frame.
     */
    public AzimuthalPlot(JFrame parent, ImagePanel ipanel) {
        this.setIp(ipanel);
        this.azimDialog = new JDialog(parent, "Azimuthal Plotting", false);
        this.azimDialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        this.azimDialog.setBounds(100, 100, 940, 540);
        this.azimDialog.setIconImage(Toolkit.getDefaultToolkit().getImage(Help.class.getResource("/img/Icona.png")));
        this.contentPane = new JPanel();
        this.contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        this.azimDialog.setContentPane(this.contentPane);
        this.contentPane.setLayout(new MigLayout("", "[][grow][][grow][][grow][][]", "[][grow][]"));

        final JLabel lbltini = new JLabel("2θ (º)");
        this.contentPane.add(lbltini, "cell 0 0,alignx right");

        this.txt_2t = new JTextField();
        this.txt_2t.setText("3.50");
        this.contentPane.add(this.txt_2t, "cell 1 0,growx");

        this.lblCakeIni = new JLabel("2θ window (º)");
        this.contentPane.add(this.lblCakeIni, "cell 2 0,alignx trailing");

        this.txtT2w = new JTextField();
        this.txtT2w.setText("0.05");
        this.contentPane.add(this.txtT2w, "cell 3 0,growx");

        this.btnIntegrar = new JButton("Plot");
        this.btnIntegrar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                AzimuthalPlot.this.do_btnIntegrartilt_actionPerformed(arg0);
            }
        });

        this.lblCakeEnd = new JLabel("Azim step (º)");
        this.contentPane.add(this.lblCakeEnd, "cell 4 0,alignx trailing");

        this.txtAzStep = new JTextField();
        this.txtAzStep.setText("0.5");
        this.contentPane.add(this.txtAzStep, "cell 5 0,growx");
        this.contentPane.add(this.btnIntegrar, "cell 6 0,growx");

        this.btn_save = new JButton("Save");
        this.btn_save.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                AzimuthalPlot.this.do_btn_save_actionPerformed(arg0);
            }
        });
        this.contentPane.add(this.btn_save, "cell 7 0,growx");

        this.plotpanel = new PlotPanel();
        this.contentPane.add(this.plotpanel.getPlotPanel(), "cell 0 1 8 1,grow");

        this.btnClose = new JButton("Close");
        this.btnClose.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                AzimuthalPlot.this.do_btnClose_actionPerformed(e);
            }
        });

        this.lblInfo = new JLabel("");
        this.lblInfo.setFont(new Font("Dialog", Font.BOLD, 12));
        this.contentPane.add(this.lblInfo, "cell 0 2 7 1");
        this.contentPane.add(this.btnClose, "cell 7 2");
        this.patt1D = new ArrayList<Pattern1D>();
        this.inicia();
        this.p1 = new com.vava33.d2dplot.d1dplot.Pattern1D();
    }

    public void inicia() {
        //	    this.patt2D=ip.getPatt2D();
        this.plotpanel.setAzIntegrationLabel(true);
        this.lblInfo.setText("");
    }

    protected void do_btn_save_actionPerformed(ActionEvent arg0) {
        if (this.patt1D.size() > 0) {
            File fileout;
            final FileNameExtensionFilter[] filter = {
                    new FileNameExtensionFilter("Data file (2T I ESD)", "dat", "xy") };
            fileout = FileUtils.fchooserSaveAsk(this.azimDialog, new File(MainFrame.getWorkdir()), filter, null);
            if (!FileUtils.getExtension(fileout).equalsIgnoreCase("dat")
                    || !FileUtils.getExtension(fileout).equalsIgnoreCase("xy")) {
                fileout = FileUtils.canviExtensio(fileout, "dat");
            }
            if (fileout != null) {
                this.savePatterns(fileout);
            }
        }
    }

    public void savePatterns(File fileout) {
        if (this.patt1D.size() == 1) {
            this.patt1D.get(0).writeDAT(fileout, this.getIp().getPatt2D().getImgfile().toString());
            log.info(String.format("File %s written", fileout.toString()));
        } else {
            for (int i = 0; i < this.patt1D.size(); i++) {
                final File fout = FileUtils.canviNomFitxer(fileout,
                        FileUtils.getFNameNoExt(fileout.getName()) + String.format("_azbin%02d", i));
                this.patt1D.get(i).writeDAT(fout, this.getIp().getPatt2D().getImgfile().toString());
                log.info(String.format("File %s written", fout.toString()));
            }
        }
    }

    public float getTxtT2() {
        try {
            return Float.parseFloat(this.txt_2t.getText());
        } catch (final Exception e) {
            if (D2Dplot_global.isDebug())
                e.printStackTrace();
            log.warning("Error parsing 2theta");
        }
        return -1f;
    }

    public float getTxtT2w() {
        try {
            return Float.parseFloat(this.txtT2w.getText());
        } catch (final Exception e) {
            if (D2Dplot_global.isDebug())
                e.printStackTrace();
            log.warning("Error parsing 2theta window");
        }
        return -1f;
    }

    public float getTxtAzStep() {
        try {
            return Float.parseFloat(this.txtAzStep.getText());
        } catch (final Exception e) {
            if (D2Dplot_global.isDebug())
                e.printStackTrace();
            log.warning("Eror parsing Azimuth step");
        }
        return -1f;
    }

    private void plotPattern(Pattern1D p, boolean norm, boolean appendPatt, String seriesName) {

        final DataSerie ds = new DataSerie();

        ds.setWavelength(this.getIp().getPatt2D().getWavel());

        final Iterator<PointPatt1D> itp = p.getPoints().iterator();
        while (itp.hasNext()) {
            final PointPatt1D point = itp.next();
            if (D2Dplot_global.isDebug()) {
                log.fine(point.getT2() + "," + point.getCounts());
            }
            if (norm) {
                ds.addPoint(new DataPoint(point.getT2(), (float) point.getCounts() / (float) point.getNpix(), 0.f));
            } else {
                ds.addPoint(new DataPoint(point.getT2(), point.getCounts(), 0.f));
            }
        }

        ds.setSerieName(seriesName);
        this.p1.addDataSerie(ds);
        this.plotpanel.fitGraph();
    }

    public void do_btnIntegrartilt_actionPerformed(ActionEvent arg0) {
        this.patt1D.clear();

        if (this.getIp().getPatt2D() == null)
            return;
        float t2 = 10.0f;
        float t2w = 0.2f;
        float Azstep = 1.0f;
        try {
            t2 = Float.parseFloat(this.txt_2t.getText());
            t2w = Float.parseFloat(this.txtT2w.getText());
            Azstep = Float.parseFloat(this.txtAzStep.getText());
        } catch (final Exception e) {
            if (D2Dplot_global.isDebug())
                e.printStackTrace();
            JOptionPane.showMessageDialog(this.azimDialog, "Check input angles and step", "Incorrect values",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        final EllipsePars e = ImgOps.getElliPars(this.getIp().getPatt2D(), FastMath.toRadians(t2));
        final ArrayList<Point2D.Float> punts = e.getEllipsePoints(0, 360, Azstep);

        final Iterator<Point2D.Float> itrp = punts.iterator();

        final Pattern1D patt1Daz = new Pattern1D(0, 360, Azstep);
        int count = 0;
        while (itrp.hasNext()) {
            final Point2D.Float p = itrp.next();
            final Patt2Dzone z = ImgOps.yArcTilt(this.getIp().getPatt2D(), FastMath.round(p.x), FastMath.round(p.y),
                    t2w / 2.f, Azstep / 2.f, true, 0, false);
            patt1Daz.getPoint(count).setIntensity(z.getYsum());
            patt1Daz.getPoint(count).setCounts(z.getYsum());
            patt1Daz.getPoint(count).setNpix(1);
            patt1Daz.getPoint(count).setDesv(z.getYmeandesv());
            count = count + 1;
        }

        //ja tindrem el pattern1D
        final String comment = String.format("Az.Integr.(0-360º) at t2=%.2fº (t2win=%.2fº, step=%.2fº)", t2, t2w,
                Azstep);
        this.lblInfo.setText(
                String.format("Azimuthal integration (0-360º) at t2=%.2fº (t2win=%.2fº, step=%.2fº)", t2, t2w, Azstep));
        patt1Daz.setComment(comment);
        this.patt1D.add(patt1Daz);
        this.p1.removeAllSeries();
        this.plotpanel.getPatterns().clear();
        this.plotpanel.getPatterns().add(this.p1);
        this.plotPattern(this.patt1D.get(this.patt1D.size() - 1), false, false, comment);

    }

    public ArrayList<Pattern1D> getPatt1D() {
        return this.patt1D;
    }

    public void setPatt1D(ArrayList<Pattern1D> patt1d) {
        this.patt1D = patt1d;
    }

    public File getMaskfile() {
        return this.maskfile;
    }

    public void setMaskfile(File maskfile) {
        this.maskfile = maskfile;
    }

    protected void do_btnClose_actionPerformed(ActionEvent e) {
        this.dispose();
    }

    /**
     * @return the ip
     */
    public ImagePanel getIp() {
        return this.ip;
    }

    /**
     * @param ip the ip to set
     */
    public void setIp(ImagePanel ip) {
        this.ip = ip;
    }

    public void dispose() {
        this.azimDialog.dispose();
    }

    public void setVisible(boolean vis) {
        this.azimDialog.setVisible(vis);
    }

}
