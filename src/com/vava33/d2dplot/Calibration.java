package com.vava33.d2dplot;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;
import javax.swing.border.TitledBorder;

import org.apache.commons.math3.analysis.MultivariateFunction;
import org.apache.commons.math3.optim.InitialGuess;
import org.apache.commons.math3.optim.MaxEval;
import org.apache.commons.math3.optim.PointValuePair;
import org.apache.commons.math3.optim.nonlinear.scalar.GoalType;
import org.apache.commons.math3.optim.nonlinear.scalar.ObjectiveFunction;
import org.apache.commons.math3.optim.nonlinear.scalar.noderiv.NelderMeadSimplex;
import org.apache.commons.math3.optim.nonlinear.scalar.noderiv.SimplexOptimizer;
import org.apache.commons.math3.util.FastMath;

import com.vava33.d2dplot.auxi.CalibOps;
import com.vava33.d2dplot.auxi.Calibrant;
import com.vava33.d2dplot.auxi.CircleFitter;
import com.vava33.d2dplot.auxi.EllipsePars;
import com.vava33.d2dplot.auxi.ImgFileUtils;
import com.vava33.d2dplot.auxi.Pattern2D;
import com.vava33.jutils.FileUtils;
import com.vava33.jutils.VavaLogger;

import net.miginfocom.swing.MigLayout;

public class Calibration {

    private final JDialog calibDialog;
    private JButton btnApply;
    private JCheckBox cbox_onTop;
    private JCheckBox chckbxCalibrate;
    private final JPanel contentPanel;
    private JLabel lbllist;
    private JToggleButton btnAutoCalibration;
    private JButton btnImageParameters;
    private JPanel panel;
    private JCheckBox chckbxShowGuessPoints;
    private JCheckBox chckbxShowFittedEllipses;
    private JCheckBox chckbxShowSearchEllipses;
    private JPanel panel_2;
    private JLabel lblPixtol;
    private JLabel lblArcSize;
    private JLabel lblEsdfact;
    private JTextField txtPixtol;
    private JTextField txtArcsize;
    private JTextField txtEsdfact;
    private JLabel lblOmmitRings;
    private JTextField txtOmmitrings;
    private JButton btnRecalc;

    private float refCX, refCY, refMD, refTiltDeg, refRotDeg;
    private boolean calibrating;
    //    private Pattern2D patt2D;
    private ArrayList<Point2D.Float> pointsRing1circle;
    private ArrayList<EllipsePars> solutions;
    private boolean isSetting1stPeakCircle = false;
    private Point2D.Float circleCenter = new Point2D.Float();
    private float circleRadius = -1f;

    //CALIBRATION PARAMETERS
    private static int findElliPointsTolPix = 10;
    private static int findElliPointsMinPixLine = findElliPointsTolPix;
    private static int findElliPointsArcSizemm = 3;
    private static int factESDIntensityThreshold = 8;
    private static int radiPunt = 2;
    private static float searchElliLimitFactor = 1.2f;
    private static ArrayList<Integer> ommitRings = new ArrayList<Integer>();
    private static int maxLaB6ring = 9;

    public ArrayList<Line2D.Float> cerques;
    public ArrayList<EllipsePars> ellicerques;

    private static final String className = "Calibration";
    private static VavaLogger log = D2Dplot_global.getVavaLogger(className);
    private JButton btnAuto;

    private ImagePanel ip;
    private JButton btnWriteCalFile;
    private JLabel lblCalibrant;
    private JComboBox<String> comboCalib;

    public float[] cal_d;

    /**
     * Create the dialog.
     */
    public Calibration(JFrame parent, ImagePanel ipanel) {
        this.setIpanel(ipanel);
        this.calibDialog = new JDialog(parent, "Instrumental Parameters Calibration", false);
        this.contentPanel = new JPanel();
        this.calibDialog
                .setIconImage(Toolkit.getDefaultToolkit().getImage(Calibration.class.getResource("/img/Icona.png")));
        this.calibDialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        final Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        final int width = 300;
        final int height = 560;
        final int x = (screen.width - width) / 2;
        final int y = (screen.height - height) / 2;
        this.calibDialog.setBounds(x, y, width, height);
        this.calibDialog.getContentPane().setLayout(new BorderLayout());
        this.calibDialog.getContentPane().add(this.contentPanel, BorderLayout.NORTH);
        this.contentPanel.setLayout(new MigLayout("", "[][][grow][]", "[][][][][][grow]"));

        {
            {
                {
                    {
                        this.chckbxCalibrate = new JCheckBox("Calibration Mode");
                        this.contentPanel.add(this.chckbxCalibrate, "cell 0 0 2 1");
                        this.chckbxCalibrate.addItemListener(new ItemListener() {
                            @Override
                            public void itemStateChanged(ItemEvent arg0) {
                                Calibration.this.do_chckbxCalibrate_itemStateChanged(arg0);
                            }
                        });
                        this.chckbxCalibrate.setSelected(true);
                    }
                    {
                        this.cbox_onTop = new JCheckBox("on top");
                        this.contentPanel.add(this.cbox_onTop, "cell 2 0,alignx right");
                        this.cbox_onTop.setSelected(true);
                        this.cbox_onTop.setHorizontalTextPosition(SwingConstants.LEADING);
                        this.cbox_onTop.addItemListener(new ItemListener() {
                            @Override
                            public void itemStateChanged(ItemEvent arg0) {
                                Calibration.this.do_cbox_onTop_itemStateChanged(arg0);
                            }
                        });
                        this.cbox_onTop.setActionCommand("on top");
                    }
                    {
                        this.lbllist = new JLabel("?");
                        this.contentPanel.add(this.lbllist, "cell 3 0");
                        this.lbllist.addMouseListener(new MouseAdapter() {
                            @Override
                            public void mouseEntered(MouseEvent e) {
                                Calibration.this.do_lbllist_mouseEntered(e);
                            }

                            @Override
                            public void mouseExited(MouseEvent e) {
                                Calibration.this.do_lbllist_mouseExited(e);
                            }

                            @Override
                            public void mouseReleased(MouseEvent e) {
                                Calibration.this.do_lbllist_mouseReleased(e);
                            }
                        });
                        this.lbllist.setFont(new Font("Tahoma", Font.BOLD, 14));
                    }
                    {
                        this.lblCalibrant = new JLabel("Calibrant");
                        this.contentPanel.add(this.lblCalibrant, "cell 0 1");
                    }
                    {
                        this.comboCalib = new JComboBox<String>();
                        this.contentPanel.add(this.comboCalib, "cell 1 1 3 1,growx");
                    }
                    {
                        this.btnAuto = new JButton("Autocalibration");
                        this.btnAuto.setFont(new Font("Dialog", Font.BOLD, 15));
                        this.contentPanel.add(this.btnAuto, "cell 0 2 4 1,growx");
                        this.btnAuto.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent arg0) {
                                Calibration.this.do_btnAuto_actionPerformed(arg0);
                            }
                        });
                        this.btnAutoCalibration = new JToggleButton("Manual Calibration");
                        this.contentPanel.add(this.btnAutoCalibration, "cell 0 3 4 1,growx");
                        this.btnAutoCalibration.addItemListener(new ItemListener() {
                            @Override
                            public void itemStateChanged(ItemEvent e) {
                                Calibration.this.do_btnAutoCalibration_itemStateChanged(e);
                            }
                        });
                        {
                            this.panel = new JPanel();
                            this.contentPanel.add(this.panel, "cell 0 4 4 1,grow");
                            this.panel.setBorder(new TitledBorder(null, "Display settings", TitledBorder.LEADING,
                                    TitledBorder.TOP, null, null));
                            this.panel.setLayout(new MigLayout("", "[grow]", "[][][]"));
                            {
                                this.chckbxShowGuessPoints = new JCheckBox("Show Guess Points");
                                this.chckbxShowGuessPoints.addItemListener(new ItemListener() {
                                    @Override
                                    public void itemStateChanged(ItemEvent arg0) {
                                        Calibration.this.do_chckbxShowGuessPoints_itemStateChanged(arg0);
                                    }
                                });
                                this.chckbxShowGuessPoints.setSelected(true);
                                this.panel.add(this.chckbxShowGuessPoints, "flowy,cell 0 0,alignx left");
                            }
                            {
                                this.chckbxShowFittedEllipses = new JCheckBox("Show Fitted Ellipses");
                                this.chckbxShowFittedEllipses.addItemListener(new ItemListener() {
                                    @Override
                                    public void itemStateChanged(ItemEvent e) {
                                        Calibration.this.do_chckbxShowFittedEllipses_itemStateChanged(e);
                                    }
                                });
                                this.chckbxShowFittedEllipses.setSelected(true);
                                this.panel.add(this.chckbxShowFittedEllipses, "flowx,cell 0 1,alignx left");
                            }
                            {
                                this.chckbxShowSearchEllipses = new JCheckBox("Show search boundaries");
                                this.chckbxShowSearchEllipses.addItemListener(new ItemListener() {
                                    @Override
                                    public void itemStateChanged(ItemEvent e) {
                                        Calibration.this.do_chckbxShowSearchEllipses_itemStateChanged(e);
                                    }
                                });
                                this.chckbxShowSearchEllipses.setActionCommand("Show search ellipses boundaries");
                                this.panel.add(this.chckbxShowSearchEllipses, "cell 0 2,alignx left");
                            }
                        }
                        this.panel_2 = new JPanel();
                        this.panel_2.setPreferredSize(new Dimension(310, 10));
                        this.contentPanel.add(this.panel_2, "cell 0 5 4 1,grow");
                        this.panel_2.setBorder(new TitledBorder(null, "Advanced Parameters", TitledBorder.LEADING,
                                TitledBorder.TOP, null, null));
                        this.panel_2.setLayout(new MigLayout("", "[][grow]", "[][][][][grow]"));
                        {
                            this.lblPixtol = new JLabel("Pixel Range");
                            this.panel_2.add(this.lblPixtol, "cell 0 0,alignx trailing");
                        }
                        {
                            this.txtPixtol = new JTextField();
                            this.panel_2.add(this.txtPixtol, "cell 1 0,growx");
                            this.txtPixtol.setColumns(10);
                        }
                        {
                            this.lblArcSize = new JLabel("Arc Size");
                            this.panel_2.add(this.lblArcSize, "cell 0 1,alignx trailing");
                        }
                        {
                            this.txtArcsize = new JTextField();
                            this.panel_2.add(this.txtArcsize, "cell 1 1,growx");
                            this.txtArcsize.setColumns(10);
                        }
                        {
                            this.lblEsdfact = new JLabel("Thold (ESD fact)");
                            this.panel_2.add(this.lblEsdfact, "cell 0 2,alignx trailing");
                        }
                        {
                            this.txtEsdfact = new JTextField();
                            this.panel_2.add(this.txtEsdfact, "cell 1 2,growx");
                            this.txtEsdfact.setColumns(10);
                        }
                        {
                            this.lblOmmitRings = new JLabel("ommit rings");
                            this.panel_2.add(this.lblOmmitRings, "cell 0 3,alignx trailing");
                        }
                        {
                            this.txtOmmitrings = new JTextField();
                            this.panel_2.add(this.txtOmmitrings, "cell 1 3,growx");
                            this.txtOmmitrings.setColumns(10);
                        }
                        this.btnRecalc = new JButton("Recalculate ellipses");
                        this.panel_2.add(this.btnRecalc, "cell 0 4 2 1,alignx right");
                        this.btnRecalc.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent arg0) {
                                Calibration.this.do_btnRecalc_actionPerformed(arg0);
                            }
                        });
                    }
                }
            }
        }
        {
            final JPanel buttonPane = new JPanel();
            this.calibDialog.getContentPane().add(buttonPane, BorderLayout.SOUTH);
            {
                buttonPane.setLayout(new MigLayout("", "[grow][grow]", "[][][25px]"));
                this.btnImageParameters = new JButton("Image Parameters");
                buttonPane.add(this.btnImageParameters, "cell 0 0,growx");
                this.btnImageParameters.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent arg0) {
                        Calibration.this.do_btnImageParameters_actionPerformed(arg0);
                    }
                });
            }
            this.btnWriteCalFile = new JButton("Write CAL file");
            this.btnWriteCalFile.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    Calibration.this.do_btnWriteCalFile_actionPerformed(e);
                }
            });
            {
                this.btnApply = new JButton("Apply results to Image");
                this.btnApply.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent arg0) {
                        Calibration.this.do_btnNewButton_actionPerformed(arg0);
                    }
                });
                buttonPane.add(this.btnApply, "cell 0 1,growx,aligny center");
            }
            buttonPane.add(this.btnWriteCalFile, "cell 0 2,growx");
            final JButton okButton = new JButton("close");
            okButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent arg0) {
                    Calibration.this.do_okButton_actionPerformed(arg0);
                }
            });
            okButton.setActionCommand("OK");
            buttonPane.add(okButton, "cell 1 2,alignx right,aligny center");
            this.calibDialog.getRootPane().setDefaultButton(okButton);
        }

        this.calibDialog.setAlwaysOnTop(true);

        log.info("** Instrumental parameters calibration **");
        this.calibDialog.pack();
        this.inicia();

    }

    public void setIpanel(ImagePanel ipanel) {
        this.ip = ipanel;
    }

    public ImagePanel getIPanel() {
        return this.ip;
    }

    //es una manera de posar al log tot el que surt pel txtArea local
    //	public void printTaOut(String msg,boolean stat) {
    //        if (stat) {
    //        	this.tAOut.stat(msg);
    //        }else {
    //        	this.tAOut.ln(msg);
    //        }
    //		log.debug(msg);
    //	}

    protected void inicia() {
        this.pointsRing1circle = null; //perque no funcioni el recalc

        this.refCX = 0;
        this.refCY = 0;
        this.refMD = 0;
        this.refTiltDeg = 0;
        this.refRotDeg = 0;

        this.txtPixtol.setText(Integer.toString(findElliPointsTolPix));
        this.txtArcsize.setText(Integer.toString(findElliPointsArcSizemm));
        this.txtEsdfact.setText(Integer.toString(factESDIntensityThreshold));

        //La llista de calibrants s'ha omplert a l'obrir el programa (opcions), la llegim i posem calibrant per defecte (LaB6)
        final Iterator<Calibrant> itrC = CalibOps.getCalibrants().iterator();
        this.comboCalib.removeAllItems();
        while (itrC.hasNext()) {
            this.comboCalib.addItem(itrC.next().getName());
            this.comboCalib.setSelectedIndex(0);//we suppose the first is LaB6
        }
    }

    public void dispose() {
        this.chckbxCalibrate.setSelected(false);
        this.getIPanel().actualitzarVista();
        this.calibDialog.dispose();
    }

    public void setVisible(boolean vis) {
        this.calibDialog.setVisible(vis);
        if (vis == true) {
            this.chckbxCalibrate.setSelected(true);
        }
    }

    public void selectCalibrant(int index) {
        try {
            this.comboCalib.setSelectedIndex(index);
        } catch (final Exception e) {
            log.debug("calibrant not exists");
        }

    }

    protected void do_btnNewButton_actionPerformed(ActionEvent arg0) {
        final Pattern2D patt2D = this.getIPanel().getPatt2D();
        if (this.getRefMD() > 0) {
            patt2D.setDistMD(this.refMD);
            patt2D.setCentrX(this.refCX);
            patt2D.setCentrY(this.refCY);
            patt2D.setTiltDeg(this.refTiltDeg);
            patt2D.setRotDeg(this.refRotDeg);
            log.info("Values set as image calibration parameters");
            this.getIPanel().actualitzarVista();
            this.getIPanel().getMainFrame().updateIparameters();
            return;
        }
        log.info("Please perform a calculation first");
    }

    protected void do_cbox_onTop_itemStateChanged(ItemEvent arg0) {
        this.calibDialog.setAlwaysOnTop(this.cbox_onTop.isSelected());
    }

    protected void do_chckbxCalibrate_itemStateChanged(ItemEvent arg0) {
        this.calibrating = this.chckbxCalibrate.isSelected();
        this.getIPanel().actualitzarVista();
    }

    protected void do_lbllist_mouseEntered(MouseEvent e) {
        this.lbllist.setForeground(Color.red);
    }

    protected void do_lbllist_mouseExited(MouseEvent e) {
        this.lbllist.setForeground(Color.black);
    }

    protected void do_lbllist_mouseReleased(MouseEvent e) {
        final String msg = "\n" + "** CALIBRATION HELP **\n" + " 1) Click Start Manual Calibration\n"
                + " 2) Click 5 or more points within the inner ring\n"
                + " 3) Click Finish -- ellipses will be fitted to the peak rings\n"
                + " 4) Click on recalc if you change the parameters (without clicking points again)\n"
                + " 5) To keep final parameters click on apply\n" + " \n" + "OR\n" + " 1) Click on autocalibration\n"
                + " (it need aprox. initial parameters and the first ring should be close to a circle)\n" + "\n";
        FileUtils.InfoDialog(this.calibDialog, msg, "Calibration Help");

    }

    protected void do_okButton_actionPerformed(ActionEvent arg0) {
        this.getIPanel().actualitzarVista();
        this.dispose();
    }

    protected void do_btnImageParameters_actionPerformed(ActionEvent arg0) {
        if (this.getIPanel().getPatt2D() != null) {
            this.ip.getMainFrame().do_mntmInstrumentalParameters_actionPerformed(null);
        }
    }

    private void readFindElliPars() {
        try {
            findElliPointsTolPix = Integer.parseInt(this.txtPixtol.getText());
            findElliPointsMinPixLine = findElliPointsTolPix;
        } catch (final Exception ex) {
            log.config("using default value for findElliPointsTolPix");
        }
        try {
            findElliPointsArcSizemm = Integer.parseInt(this.txtArcsize.getText());
        } catch (final Exception ex) {
            log.config("using default value for findElliPointsArcSizemm");
        }
        try {
            factESDIntensityThreshold = Integer.parseInt(this.txtEsdfact.getText());
        } catch (final Exception ex) {
            log.config("using default value for factESDIntensityThreshold");
        }
        try {
            final String ommitstr = this.txtOmmitrings.getText().trim().replace(',', ' ');
            log.config(ommitstr);
            final String[] ommitlist = ommitstr.trim().split("\\s+");
            ommitRings = new ArrayList<Integer>();
            for (int i = 0; i < ommitlist.length; i++) {
                ommitRings.add(Integer.parseInt(ommitlist[i]));
                log.config(Integer.toString(ommitRings.get(i)));
            }
        } catch (final Exception ex) {
            ommitRings = new ArrayList<Integer>();
            log.config("using default value for ommitRings");
        }
        //set the dspacings of the current selection
        this.setCal_d(CalibOps.getCalibrants().get(this.comboCalib.getSelectedIndex()).getDsp());
        log.debug("selected calibrant = " + CalibOps.getCalibrants().get(this.comboCalib.getSelectedIndex()).getName());
    }

    //omplim automaticament els pointsRing1circle segons distMD i dspacing 1r pic LaB6 (considerem CERCLE)
    public void do_btnAuto_actionPerformed(ActionEvent arg0) {
        final Pattern2D patt2D = this.getIPanel().getPatt2D();
        this.solutions = new ArrayList<EllipsePars>(); //BORREM ANTERIORS SOLUCIONS

        if (!this.checkWavePixSDist(true))
            return;

        this.readFindElliPars();

        //ara cal buscar el suposat radi del primer pic de LaB6 considerant els param instrumentals actuals
        this.circleRadius = (float) ((patt2D.getDistMD() / patt2D.getPixSx())
                * FastMath.tan(2 * FastMath.asin(patt2D.getWavel() / (2 * this.getCal_d()[1 - 1]))));
        this.circleCenter = new Point2D.Float(patt2D.getCentrX(), patt2D.getCentrY());
        log.writeNameNumPairs("config", true, "circleRadius", this.circleRadius);
        this.pointsRing1circle = this.findRingPoints(this.getCircleRadius(), findElliPointsTolPix);

        //busquem ellipses (ja llegirem els parametres dins, abans ho feiem aqui pero calcEllipses tambe es pot cridar amb recalc)
        this.calcEllipses();

        //calculem parametres instrumentals
        if (this.solutions.size() > 0) {
            this.calcInstrumFromEllipses();
        }
        this.getIPanel().actualitzarVista();
    }

    private boolean checkWavePixSDist(boolean showParams) {
        final Pattern2D patt2D = this.getIPanel().getPatt2D();
        if ((!patt2D.checkIfWavel() || !patt2D.checkIfPixSize() || !patt2D.checkIfDistMD())) {
            log.warning("Please provide pixel size, sample-detector distance and wavelength");
            if (showParams)
                this.btnImageParameters.doClick();
            return false;
        }
        return true;
    }

    protected void do_btnAutoCalibration_itemStateChanged(ItemEvent e) {

        if (this.btnAutoCalibration.isSelected()) {
            //comprovem que hi hagin els valors de pixsize i wavelength
            if (!this.checkWavePixSDist(true)) {
                this.btnAutoCalibration.setSelected(false);
                return;
            }
            this.pointsRing1circle = new ArrayList<Point2D.Float>();
            this.solutions = new ArrayList<EllipsePars>(); //BORREM ANTERIORS SOLUCIONS
            this.btnAutoCalibration.setText("Click here when finished!");
            this.setSetting1stPeakCircle(true);
        } else {
            this.btnAutoCalibration.setText("Manual Calibration");
            this.setSetting1stPeakCircle(false);
            //busquem ellipses (ja llegirem els parametres dins, abans ho feiem aqui pero calcEllipses tambe es pot cridar amb recalc)
            this.readFindElliPars();
            this.calcEllipses();

            //calculem parametres instrumentals
            if (this.solutions.size() > 0) {
                this.calcInstrumFromEllipses();
            }
        }
        this.getIPanel().actualitzarVista();
    }

    private void calcEllipses() {
        if ((this.pointsRing1circle != null)) {
            final Pattern2D patt2D = this.getIPanel().getPatt2D();

            //INICIALITZEM I CHECKEJEM ELS PARAMETRES ABANS DE CALCULAR/BUSCAR ELLIPSES
            this.cerques = new ArrayList<Line2D.Float>();
            this.ellicerques = new ArrayList<EllipsePars>();

            //ARA JA PODEM FER LA CERCA

            //first we fit a circle and calculate the aprox. Center and distMD, we assume the wavelength known
            final Point2D.Float[] d = new Point2D.Float[this.pointsRing1circle.size()];
            for (int i = 0; i < this.pointsRing1circle.size(); i++) {
                d[i] = this.pointsRing1circle.get(i);
            }
            this.pointsRing1circle.toArray(d);

            this.solutions = new ArrayList<EllipsePars>();

            //ARA PROVEM DE FITTEJAR UNA ELLIPSE i SI NO FUNCIONA DONCS UN CERCLE
            final EllipsePars p = new EllipsePars();
            p.setLab6ring(1);
            p.setEstimPoints(this.pointsRing1circle);
            p.fitElli();
            if (!p.isFit()) {
                log.debug("Error fitting ellipse for 1st ring, trying 1st with circle...");
                //try with a circle
                this.fitCircle(d);
                if (this.getCircleRadius() <= 0) {
                    log.warning(
                            "Error fitting calibrant peak number 1. Please check image parameters and/or add more points");
                    return;
                }
                p.setEstimPoints(this.findRingPoints(this.getCircleRadius(), findElliPointsTolPix)); //posem punts del cercle
            } else {
                p.setEstimPoints(this.findRingPoints(p, findElliPointsTolPix)); //busquem mes punts de l'elipse
            }

            //aqui tindrem o bé els punts del cercle o de l'elipse a estimpoints
            //fem ara el fit definitiu d'una ellipse amb tots els punts
            p.fitElli();

            if (p.isFit()) {
                p.logElliPars("debug");
                this.solutions.add(p);
            } else {
                log.warning(
                        "Error fitting calibrant peak number 1. Please check image parameters and/or add more points");
                return;
            }

            //distMD en pixels...
            float estMD = -1;
            estMD = this.estimMDdist(p, 1);//estimacio distMD primer pic
            final float wave = patt2D.getWavel();
            log.debug("estMD=" + estMD);

            //now we use the previous ellipse to fit the next one
            int fitCount = 1; //number of rings fitted
            for (int i = 2; i < maxLaB6ring; i++) {

                log.config("ring no." + i);

                if (ommitRings.contains(i))
                    continue;

                final EllipsePars p0 = this.solutions.get(fitCount - 1);

                final float twoth = (float) (2 * FastMath.asin(wave / (2 * this.getCal_d()[i - 1])));
                final float radiPix = (float) (FastMath.tan(twoth) * estMD);

                final float twothP0 = (float) (2 * FastMath.asin(wave / (2 * this.getCal_d()[p0.getLab6ring() - 1])));
                final float radiPixP0 = (float) (FastMath.tan(twothP0) * estMD);

                final float factRP0maj = (float) (p0.getRmax() / radiPixP0);
                final float factRP0men = (float) (p0.getRmin() / radiPixP0);

                log.writeNameNumPairs("FINE", true, "radiPixP0 factRP0maj factRP0men", radiPixP0, factRP0maj,
                        factRP0men);

                final float r1 = (float) (p0.getRmax());
                final float r2 = (float) (p0.getRmin());
                final float factRmax = factRP0maj;
                final float factRmin = factRP0men;

                log.writeNameNumPairs("FINE", true, "r1 r2 MD1 twoth radipix", r1, r2, estMD * patt2D.getPixSx(),
                        FastMath.toDegrees(twoth), radiPix);

                searchElliLimitFactor = FastMath.max(1.2f, searchElliLimitFactor);

                if ((radiPix * factRmax) > (patt2D.getDimX() / searchElliLimitFactor))
                    break;
                if ((radiPix * factRmin) > (patt2D.getDimX() / searchElliLimitFactor))
                    break;

                final EllipsePars p1 = new EllipsePars(radiPix * factRmax, radiPix * factRmin, p0.getXcen(),
                        p0.getYcen(), p0.getAngrot());
                final EllipsePars pN = new EllipsePars();
                pN.setEstimPoints(this.findRingPoints(p1, findElliPointsTolPix));
                //test with circle...
                pN.setLab6ring(i);
                pN.fitElli();
                if (pN.isFit()) {
                    pN.logElliPars("debug");
                    this.solutions.add(pN);
                    fitCount = fitCount + 1;
                    //fem nova estimacio mostra-detector amb la nova ellipse (en pixels)
                    estMD = this.estimMDdist(pN, i);
                } else {
                    log.warning("Could not fit ring nr. " + i);
                }
            }
        }
    }

    //busca punts al voltant d'un cercle radi cradi amb un rang +-tol (del radi)
    //unitats PIXELS
    private ArrayList<Point2D.Float> findRingPoints(float cradi, float tol) {
        final Pattern2D patt2D = this.getIPanel().getPatt2D();
        //parametric circle
        //x  =  h + r cos(t)
        //y  =  k + r sin(t)
        final ArrayList<Point2D.Float> ringPoints = new ArrayList<Point2D.Float>();
        //Per determinar el num de punts, l'unic parametre es la separacio en pixels entre els punts (arcpix)
        final float arcInPixels = findElliPointsArcSizemm / patt2D.getPixSx();
        final float angstep = (float) (FastMath.asin(arcInPixels / (2 * cradi)) / 2);
        log.writeNameNumPairs("debug", true, "angstep", FastMath.toDegrees(angstep));

        for (float a = 0f; a < 2 * FastMath.PI; a = a + angstep) {
            float xmaxI = 0;
            float ymaxI = 0;
            float maxI = 0;
            float meanI = 0;
            int npix = 0;
            for (float r = cradi - tol; r <= cradi + tol; r = r + 0.5f) { //161103 canvi +1 per +0.5
                final float xpix = (float) (this.getCircleCenter().x + r * FastMath.cos(a));
                final float ypix = (float) (this.getCircleCenter().y + r * FastMath.sin(a));
                //agafem aquest com a pic central i ara analitzem una zona quadrada de nxn pixels. e.g. n=3?
                float inten = 0;
                for (int i = -1; i < 2; i++) {
                    final int px = (int) (xpix) + i;
                    final int py = (int) (ypix) + i;
                    if (!patt2D.isInside(px, py))
                        continue;
                    if (patt2D.isExcluded(px, py))
                        continue;
                    inten = patt2D.getInten(px, py);
                    if (inten > maxI) {
                        maxI = inten;
                        xmaxI = px;
                        ymaxI = py;
                    }
                }
                meanI = meanI + inten;
                npix = npix + 1;
            }
            meanI = meanI / npix;
            final float threshold = (float) (meanI + factESDIntensityThreshold * FastMath.sqrt(meanI));
            if (maxI > threshold) {
                ringPoints.add(new Point2D.Float(xmaxI, ymaxI));
            }
        }
        return ringPoints;
    }

    //busca punts al voltant d'una ellipse amb un rang +-tol (del radi)
    //unitats PIXELS
    private ArrayList<Point2D.Float> findRingPoints(EllipsePars elli, float tol) {
        final Pattern2D patt2D = this.getIPanel().getPatt2D();
        final ArrayList<Point2D.Float> ringPoints = new ArrayList<Point2D.Float>();

        final float cradi = (float) ((elli.getRmax() + elli.getRmin()) / 2);
        final float facRmax = (float) (elli.getRmax() / cradi);
        final float facRmin = (float) (elli.getRmin() / cradi);

        //ANGULAR STEP in order to have an arc of aprox 1mm
        final float arcInPixels = findElliPointsArcSizemm / patt2D.getPixSx();
        final float angstep = (float) (FastMath.asin(arcInPixels / (2 * cradi)) / 2);
        log.writeNameNumPairs("debug", true, "angstep", FastMath.toDegrees(angstep));
        //        System.out.println(FastMath.toDegrees(elli.getAngrot()));
        //debug Posem les max i min elli de la cerca
        this.ellicerques.add(new EllipsePars((cradi - tol) * facRmax, (cradi - tol) * facRmin, elli.getXcen(),
                elli.getYcen(), elli.getAngrot()));
        this.ellicerques.add(new EllipsePars((cradi + tol) * facRmax, (cradi + tol) * facRmin, elli.getXcen(),
                elli.getYcen(), elli.getAngrot()));

        log.config("ellicerques");
        this.ellicerques.get(0).logElliPars("CONFIG");
        this.ellicerques.get(1).logElliPars("CONFIG");

        for (float a = 0f; a < 2 * FastMath.PI; a = a + angstep) {
            float xmaxI = 0;
            float ymaxI = 0;
            float maxI = 0;
            float meanI = 0;
            int npix = 0;
            //debug segments
            //            if (a==FastMath.PI/4) cerques.add(new Line2D.Float(x1, y1, x2, y2))
            for (float r = cradi - tol; r <= cradi + tol; r = r + 1) {
                final Point2D.Float p = elli.getEllipsePoint((float) FastMath.toDegrees(a), r * facRmax, r * facRmin);
                float inten = 0;
                //cerca en un quadrat de aresta 3
                for (int i = -1; i < 2; i++) {
                    final int px = (int) (p.x) + i;
                    final int py = (int) (p.y) + i;
                    if (!patt2D.isInside(px, py)) {
                        continue;
                    }
                    if (patt2D.isExcluded(px, py)) {
                        continue;
                    }
                    inten = patt2D.getInten(px, py);
                    log.writeNameNumPairs("fine", true, "ang,radi,px,py,inten,maxI,xmaxI,ymaxI", FastMath.toDegrees(a),
                            r, px, py, inten, maxI, xmaxI, ymaxI);
                    if (inten > maxI) {
                        maxI = inten;
                        xmaxI = px;
                        ymaxI = py;
                    }
                }
                meanI = meanI + inten;
                npix = npix + 1;
            }
            if (npix < findElliPointsMinPixLine) {
                log.config("not enougth pixels in a line to find the peak");
                continue; //minim pixels que s'han analitzat
            }
            meanI = meanI / npix;
            final float threshold = (float) (meanI + factESDIntensityThreshold * FastMath.sqrt(meanI));
            //            float threshold = meanI * 1.5f; //criteri mes estricte
            if (maxI > threshold) {
                ringPoints.add(new Point2D.Float(xmaxI, ymaxI));
            } else {
                log.writeNameNums("fine", true, "under threshold (maxI meanI npix threshold)", maxI, meanI, npix,
                        threshold);
            }
        }
        return ringPoints;
    }

    private float estimMDdist(EllipsePars e, int lab6peak) {
        final float wave = this.getIPanel().getPatt2D().getWavel();
        final float twoth = (float) (2 * FastMath.asin(wave / (2 * this.getCal_d()[lab6peak - 1])));
        final float estMD = (float) ((e.getRmin() * e.getRmin()) / (FastMath.tan(twoth) * e.getRmax()));
        return estMD;
    }

    public EllipsePars getEllipseForLaB6ring(int ringN) {
        final Iterator<EllipsePars> itre = this.solutions.iterator();
        while (itre.hasNext()) {
            final EllipsePars e = itre.next();
            if (e.getLab6ring() == ringN) {
                return e;
            }
        }
        return null;
    }

    private void calcInstrumFromEllipses() {
        final Pattern2D patt2D = this.getIPanel().getPatt2D();
        final ArrayList<Double> distsMD = new ArrayList<Double>();
        final ArrayList<Double> tilts = new ArrayList<Double>();
        final ArrayList<Double> rots = new ArrayList<Double>();
        final ArrayList<Point2D.Float> centresMenys = new ArrayList<Point2D.Float>();
        final ArrayList<Point2D.Float> centresMes = new ArrayList<Point2D.Float>();
        //we do it for all the rings up to maxLaB6 rings ecept the ommitted
        for (int i = 1; i < maxLaB6ring; i++) {

            if (ommitRings.contains(i))
                continue;

            final EllipsePars e = this.getEllipseForLaB6ring(i);
            if (e == null) {
                continue;
            }

            final float wave = patt2D.getWavel();
            final double tth = 2 * FastMath.asin(wave / (2 * this.getCal_d()[i - 1]));
            final double tantth = FastMath.tan(tth);

            //estimate of tilt,distMD
            final double exen = FastMath
                    .sqrt(FastMath.max(0., 1. - ((e.getRmin() * e.getRmin()) / (e.getRmax() * e.getRmax()))));
            final double tilt = -1 * FastMath.asin(exen * FastMath.cos(tth)); //tenia un -1 pero no hi hauria de ser...
            final double distMD = (e.getRmin() * e.getRmin()) / (tantth * e.getRmax());
            //distance from the ellipse center to the beam center
            final double c = e.getRmax() * tantth * FastMath.tan(tilt);

            final double rot = e.getAngrot();

            //angrot es l'angle "azimut" respecte les 12h (+cw) que ens diu quan està rotat Rmaj
            //agafem punt 0,0 el centre de l'ellipse (relatiu) i apliquem la rotacio de "c"
            //l'angle el faig negatiu perque sigui una rotacio clockwise tal com defineix angrot
            //            double xmes = 0*FastMath.cos(-rot) - c*FastMath.sin(-rot);
            //            double ymes = 0*FastMath.sin(-rot) + (-c)*FastMath.cos(-rot);
            //            double xmenys = 0*FastMath.cos(-rot) - (-c)*FastMath.sin(-rot);
            //            double ymenys = 0*FastMath.sin(-rot) + c*FastMath.cos(-rot);

            //tenim la c, que es damunt de Rmaj, seria el modul del vector ElliCen-BeamCen. Cal aplicar-li rot,
            //es a dir, rotacio ACW de ROT per passar de eix horitzontal al real, alerta que les y van negatives!
            //en conveni fit2d cal aplicar-lo CW
            final double xmes = c * FastMath.cos(rot);
            final double ymes = c * (-1) * FastMath.sin(rot);
            final double xmenys = (-c) * FastMath.cos(rot);
            final double ymenys = (-c) * (-1) * FastMath.sin(rot);

            //ara ja tinc en relatiu (centre ellipse) les dues possibles posicions del beam, hem de tornar-ho a pixels absoluts:
            //atencio, es -y perque tenim l'origen a dalt a l'esquerra
            log.writeNameNumPairs("fine", true, "xmes,ymes,xmenys,ymenys,e.getXcen(),e.getYcen()", xmes, ymes, xmenys,
                    ymenys, e.getXcen(), e.getYcen());
            final double centreXmes = e.getXcen() + xmes;
            final double centreYmes = e.getYcen() - ymes;
            final double centreXmenys = e.getXcen() + xmenys;
            final double centreYmenys = e.getYcen() - ymenys;

            //logging debug
            log.config("---- RING nr." + i + " 2theta=" + FastMath.toDegrees(tth));
            log.writeNameNumPairs("fine", true, "angRotDeg,tiltDeg,distMD", FastMath.toDegrees(rot),
                    FastMath.toDegrees(tilt), distMD);
            log.writeNameNumPairs("fine", true, "c,xmes,ymes,xmenys,ymenys", c, xmes, ymes, xmenys, ymenys);
            log.writeNameNumPairs("fine", true, "centreX+,centreY+,centreX-,centreY-", centreXmes, centreYmes,
                    centreXmenys, centreYmenys);

            distsMD.add(distMD * patt2D.getPixSx());
            tilts.add(tilt);
            centresMenys.add(new Point2D.Float((float) centreXmenys, (float) centreYmenys));
            centresMes.add(new Point2D.Float((float) centreXmes, (float) centreYmes));
            rots.add(rot);
        }

        double meanMD = 0;
        double meanTilt = 0;
        double meanRot = 0;
        double meanCXm = 0;
        double meanCYm = 0;
        double meanCXp = 0;
        double meanCYp = 0;

        for (int i = 0; i < distsMD.size(); i++) {
            log.writeNameNumPairs("fine", true, "MD CXM CYM CXP CXP Tilt", distsMD.get(i), centresMenys.get(i).x,
                    centresMenys.get(i).y, centresMes.get(i).x, centresMes.get(i).y, FastMath.toDegrees(tilts.get(i)));
            meanMD = meanMD + distsMD.get(i);
            meanTilt = meanTilt + tilts.get(i);
            meanRot = meanRot + rots.get(i);
            meanCXm = meanCXm + centresMenys.get(i).x;
            meanCYm = meanCYm + centresMenys.get(i).y;
            meanCXp = meanCXp + centresMes.get(i).x;
            meanCYp = meanCYp + centresMes.get(i).y;
        }
        final int n = distsMD.size();
        meanMD = meanMD / n;
        meanTilt = meanTilt / n;
        meanRot = meanRot / n;
        meanCXm = meanCXm / n;
        meanCYm = meanCYm / n;
        meanCXp = meanCXp / n;
        meanCYp = meanCYp / n;

        log.config("Rot guess (º)=" + FastMath.toDegrees(meanRot));
        log.config("tilt guess(º)=" + FastMath.toDegrees(meanTilt));

        //CHOOSE THE BEST CENTER
        //as initial guess I will chose the closest to the first ellipse center
        double bestCX = 0;
        double bestCY = 0;
        final EllipsePars e = this.getEllipseForLaB6ring(1);
        final double ex = e.getXcen();
        final double ey = e.getXcen();
        final double diffPlus = FastMath.abs(ex - meanCXp) * FastMath.abs(ex - meanCXp)
                + FastMath.abs(ey - meanCYp) * FastMath.abs(ey - meanCYp);
        final double diffMinus = FastMath.abs(ex - meanCXm) * FastMath.abs(ex - meanCXm)
                + FastMath.abs(ey - meanCYm) * FastMath.abs(ey - meanCYm);
        if (diffMinus < diffPlus) {
            bestCX = meanCXm;
            bestCY = meanCYm;
        } else {
            bestCX = meanCXp;
            bestCY = meanCYp;
        }
        log.config("Center guess option 1 (pX pY)=" + meanCXm + " " + meanCYm);
        log.config("Center guess option 2 (pX pY)=" + meanCXp + " " + meanCYp);
        log.config("Best? Center guess (pX pY)=" + bestCX + " " + bestCY);
        log.config("DistMD guess (mm)=" + meanMD);

        this.setRefCX((float) bestCX);
        this.setRefCY((float) bestCY);
        this.setRefMD((float) meanMD);
        this.setRefTiltDeg((float) FastMath.toDegrees(meanTilt));
        this.setRefRotDeg((float) FastMath.toDegrees(meanRot));

        //=========== MINIMITZACIO AMB 2THETA (dspacing)
        final MultivariateFunction function = new MultivariateFunction() {
            @Override
            public double value(double[] array) {
                final double res = CalibOps.minimize2Theta(Calibration.this.getIPanel().getPatt2D(), array[0], array[1],
                        array[2], array[3], array[4], Calibration.this.solutions, Calibration.this.cal_d);
                log.writeNameNums("fine", true, "array,sum", array[0], array[1], array[2], array[3], array[4], res);
                return res;
            }
        };
        final SimplexOptimizer optimizer = new SimplexOptimizer(1e-5, 1e-10);
        final PointValuePair optimum = optimizer.optimize(new MaxEval(1000000), new ObjectiveFunction(function),
                GoalType.MINIMIZE,
                new InitialGuess(new double[] { this.getRefCX(), this.getRefCY(), this.getRefMD(), this.getRefTiltDeg(),
                        this.getRefRotDeg() }),
                new NelderMeadSimplex(new double[] { 5, 5, 5, 0.2, 0.2 }, 1, 2, 0.5, 0.5));

        log.debug("opt sol=" + Arrays.toString(optimum.getPoint()) + " : " + optimum.getSecond());

        this.setRefCX((float) optimum.getPoint()[0]);
        this.setRefCY((float) optimum.getPoint()[1]);
        this.setRefMD((float) optimum.getPoint()[2]);
        this.setRefTiltDeg((float) optimum.getPoint()[3]);
        this.setRefRotDeg((float) optimum.getPoint()[4]);

        //        log.info("Calibration results ================",className);
        //        log.info(" Distance Sample-Det (mm) ="+FileUtils.dfX_3.format(this.getRefMD()),className);
        //        log.info(" Beam Center (best) (pX pY) ="+FileUtils.dfX_3.format(this.getRefCX())+" "+FileUtils.dfX_3.format(this.getRefCY()),className);
        //        log.info(" Detecctor rot. (ind) (deg) = "+FileUtils.dfX_2.format(this.getRefRotDeg()),className);                
        //        log.info(" Detector tilt (deg) = "+FileUtils.dfX_3.format(this.getRefTiltDeg()),className);    
        //        log.info("=================================",className);

        log.info("Calibration results ================");
        log.info(" Distance Sample-Det (mm) =" + FileUtils.dfX_3.format(this.getRefMD()));
        log.info(" Beam Center (best) (pX pY) =" + FileUtils.dfX_3.format(this.getRefCX()) + " "
                + FileUtils.dfX_3.format(this.getRefCY()));
        log.info(" Detecctor rot. (ind) (deg) = " + FileUtils.dfX_2.format(this.getRefRotDeg()));
        log.info(" Detector tilt (deg) = " + FileUtils.dfX_3.format(this.getRefTiltDeg()));
        log.info("=================================");
    }

    private void fitCircle(Point2D.Float[] points) {
        try {
            final CircleFitter fitter = new CircleFitter();
            fitter.initialize(points);
            final int iter = fitter.minimize(100, 0.1f, 1.0e-12f);
            log.config("converged after " + iter + " iterations");
            log.writeNameNums("config", true, "Circle fit (x y radi iter): ", fitter.getCenter().x,
                    fitter.getCenter().y, fitter.getRadius(), iter);
            this.setCircleCenter(fitter.getCenter());
            this.setCircleRadius(fitter.getRadius());

        } catch (final Exception e) {
            log.config(e.getMessage());
            return;
        }

    }

    protected void do_btnRecalc_actionPerformed(ActionEvent arg0) {
        //busquem ellipses
        this.calcEllipses();

        //calculem parametres instrumentals
        if (this.solutions.size() > 0) {
            this.calcInstrumFromEllipses();
        }
        this.getIPanel().actualitzarVista();
    }

    public boolean isCalibrating() {
        return this.calibrating;
    }

    public void addPointToRing1Circle(Point2D.Float p) {
        if (this.pointsRing1circle == null)
            return;
        this.pointsRing1circle.add(p);
    }

    public ArrayList<Point2D.Float> getPointsRing1circle() {
        return this.pointsRing1circle;
    }

    public static int getRadipunt() {
        return radiPunt;
    }

    public ArrayList<EllipsePars> getSolutions() {
        return this.solutions;
    }

    public boolean isSetting1stPeakCircle() {
        return this.isSetting1stPeakCircle;
    }

    public void setSetting1stPeakCircle(boolean isSetting1stPeakCircle) {
        this.isSetting1stPeakCircle = isSetting1stPeakCircle;
    }

    public Point2D.Float getCircleCenter() {
        return this.circleCenter;
    }

    public void setCircleCenter(Point2D.Float circleCenter) {
        this.circleCenter = circleCenter;
    }

    public float getCircleRadius() {
        return this.circleRadius;
    }

    public void setCircleRadius(float circleRadius) {
        this.circleRadius = circleRadius;
    }

    public float getRefCX() {
        return this.refCX;
    }

    public void setRefCX(float refCX) {
        this.refCX = refCX;
    }

    public float getRefCY() {
        return this.refCY;
    }

    public void setRefCY(float refCY) {
        this.refCY = refCY;
    }

    public float getRefMD() {
        return this.refMD;
    }

    public void setRefMD(float refMD) {
        this.refMD = refMD;
    }

    public float getRefTiltDeg() {
        return this.refTiltDeg;
    }

    public void setRefTiltDeg(float refTiltDeg) {
        this.refTiltDeg = refTiltDeg;
    }

    public float getRefRotDeg() {
        return this.refRotDeg;
    }

    public void setRefRotDeg(float refRotDeg) {
        this.refRotDeg = refRotDeg;
    }

    public boolean isShowGuessPoints() {
        return this.chckbxShowGuessPoints.isSelected();
    }

    public boolean isShowFittedEllipses() {
        return this.chckbxShowFittedEllipses.isSelected();
    }

    public boolean isShowSearchEllipsesBoundaries() {
        return this.chckbxShowSearchEllipses.isSelected();
    }

    public static float getSearchElliLimitFactor() {
        return searchElliLimitFactor;
    }

    public static void setSearchElliLimitFactor(float searchElliLimitFactor) {
        Calibration.searchElliLimitFactor = searchElliLimitFactor;
    }

    public ArrayList<EllipsePars> getElliCerques() {
        return this.ellicerques;
    }

    protected void do_chckbxShowGuessPoints_itemStateChanged(ItemEvent arg0) {
        this.getIPanel().actualitzarVista();
    }

    protected void do_chckbxShowFittedEllipses_itemStateChanged(ItemEvent e) {
        this.getIPanel().actualitzarVista();
    }

    protected void do_chckbxShowSearchEllipses_itemStateChanged(ItemEvent e) {
        this.getIPanel().actualitzarVista();
    }

    protected void do_btnWriteCalFile_actionPerformed(ActionEvent e) {
        File calfile = FileUtils.fchooserSaveAsk(this.calibDialog, new File(D2Dplot_global.getWorkdir()), null, "cal");
        if (calfile != null) {
            calfile = ImgFileUtils.writeCALfile(calfile, this.getIPanel().getPatt2D(), this, false); //extensio forçada al fchooser
            D2Dplot_global.setWorkdir(calfile);
        }
    }

    public float[] getCal_d() {
        return this.cal_d;
    }

    public void setCal_d(float[] cal_d) {
        this.cal_d = cal_d;
    }
}
