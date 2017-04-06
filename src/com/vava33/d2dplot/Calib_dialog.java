package com.vava33.d2dplot;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
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
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;

import org.apache.commons.math3.optim.InitialGuess;
import org.apache.commons.math3.optim.MaxEval;
import org.apache.commons.math3.optim.PointValuePair;
import org.apache.commons.math3.optim.nonlinear.scalar.GoalType;
import org.apache.commons.math3.optim.nonlinear.scalar.ObjectiveFunction;
import org.apache.commons.math3.optim.nonlinear.scalar.noderiv.NelderMeadSimplex;
import org.apache.commons.math3.optim.nonlinear.scalar.noderiv.SimplexOptimizer;
import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.analysis.*;

import com.vava33.d2dplot.auxi.CalibOps;
import com.vava33.d2dplot.auxi.CircleFitter;
import com.vava33.d2dplot.auxi.EllipsePars;
import com.vava33.d2dplot.auxi.Pattern2D;
import com.vava33.jutils.FileUtils;
import com.vava33.jutils.LogJTextArea;
import com.vava33.jutils.VavaLogger;

import net.miginfocom.swing.MigLayout;

import javax.swing.JToggleButton;
import javax.swing.border.TitledBorder;


public class Calib_dialog extends JDialog {

    private static final long serialVersionUID = -5947817749730984383L;

    private JButton btnApply;
    private JCheckBox cbox_onTop;
    private JCheckBox chckbxCalibrate;
    private final JPanel contentPanel = new JPanel();
    private JLabel lbllist;
    private JPanel panel_left;
    private JPanel panel_right;
    private JScrollPane scrollPane_1;
    private JSplitPane splitPane;
    private JToggleButton btnAutoCalibration;
    private LogJTextArea tAOut;
//    private Param_dialog paramDialog;
    private JButton btnImageParameters;
    private JPanel panel;
    private JCheckBox chckbxShowGuessPoints;
    private JCheckBox chckbxShowFittedEllipses;
    private JCheckBox chckbxShowSearchEllipses;
    private JPanel panel_1;
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
    private Pattern2D patt2D;
    private ArrayList<Point2D.Float> pointsRing1circle;
    private ArrayList<EllipsePars> solutions;
    private boolean isSetting1stPeakCircle=false;
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
    
    private static VavaLogger log = D2Dplot_global.getVavaLogger(Calib_dialog.class.getName());
    private JButton btnAuto;
    
    private ImagePanel ip;
    private JButton btnWriteCalFile;
    
    /**
     * Create the dialog.
     */
    public Calib_dialog(ImagePanel ipanel) {
        this.setIpanel(ipanel);
        setIconImage(Toolkit.getDefaultToolkit().getImage(Calib_dialog.class.getResource("/img/Icona.png")));
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("LaB6 Calibration");
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        int width = 660;
        int height = 730;
        int x = (screen.width - width) / 2;
        int y = (screen.height - height) / 2;
        setBounds(x, y, 629, 580);
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(this.contentPanel, BorderLayout.CENTER);
        contentPanel.setLayout(new MigLayout("insets 0", "[grow]", "[grow]"));
        {
            this.splitPane = new JSplitPane();
            this.splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
            contentPanel.add(this.splitPane, "cell 0 0,grow");
            {
                this.panel_left = new JPanel();
                this.splitPane.setLeftComponent(this.panel_left);
                {
                    panel_left.setLayout(new MigLayout("", "[grow][][]", "[26px][][][][grow]"));
                }
                {
                    this.cbox_onTop = new JCheckBox("on top");
                    cbox_onTop.setSelected(true);
                    this.cbox_onTop.setHorizontalTextPosition(SwingConstants.LEADING);
                    this.cbox_onTop.addItemListener(new ItemListener() {
                        @Override
                        public void itemStateChanged(ItemEvent arg0) {
                            do_cbox_onTop_itemStateChanged(arg0);
                        }
                    });
                    {
                        this.chckbxCalibrate = new JCheckBox("Calibration Mode");
                        this.chckbxCalibrate.addItemListener(new ItemListener() {
                            @Override
                            public void itemStateChanged(ItemEvent arg0) {
                                do_chckbxCalibrate_itemStateChanged(arg0);
                            }
                        });
                        this.chckbxCalibrate.setSelected(true);
                        this.panel_left.add(this.chckbxCalibrate, "cell 0 0,alignx left,aligny center");
                    }
                    this.cbox_onTop.setActionCommand("on top");
                    this.panel_left.add(this.cbox_onTop, "cell 1 0 2 1,alignx right,aligny center");
                }
                {
                    {
                        {
                        }
                    }
                }
                btnAutoCalibration = new JToggleButton("Start Manual Calibration");
                btnAutoCalibration.addItemListener(new ItemListener() {
                    public void itemStateChanged(ItemEvent e) {
                        do_btnAutoCalibration_itemStateChanged(e);
                    }
                });
                panel_left.add(btnAutoCalibration, "flowy,cell 0 2,grow");
                {
                    this.lbllist = new JLabel("?");
                    this.lbllist.addMouseListener(new MouseAdapter() {
                        @Override
                        public void mouseEntered(MouseEvent e) {
                            do_lbllist_mouseEntered(e);
                        }

                        @Override
                        public void mouseExited(MouseEvent e) {
                            do_lbllist_mouseExited(e);
                        }

                        @Override
                        public void mouseReleased(MouseEvent e) {
                            do_lbllist_mouseReleased(e);
                        }
                    });
                    {
                        btnAuto = new JButton("Autocalibration");
                        btnAuto.addActionListener(new ActionListener() {
                            public void actionPerformed(ActionEvent arg0) {
                                do_btnAuto_actionPerformed(arg0);
                            }
                        });
                        panel_left.add(btnAuto, "cell 1 2");
                    }
                    this.lbllist.setFont(new Font("Tahoma", Font.BOLD, 14));
                    this.panel_left.add(this.lbllist, "cell 2 2,alignx right,aligny center");
                }
                {
                    panel_1 = new JPanel();
                    panel_left.add(panel_1, "cell 0 4 3 1,grow");
                    panel_1.setLayout(new MigLayout("", "[grow][grow]", "[grow]"));
                    {
                        panel = new JPanel();
                        panel_1.add(panel, "cell 0 0,grow");
                        panel.setBorder(new TitledBorder(null, "Display settings", TitledBorder.LEADING, TitledBorder.TOP, null, null));
                        panel.setLayout(new MigLayout("", "[grow]", "[][][][]"));
                        {
                            chckbxShowGuessPoints = new JCheckBox("Show Guess Points");
                            chckbxShowGuessPoints.addItemListener(new ItemListener() {
                                public void itemStateChanged(ItemEvent arg0) {
                                    do_chckbxShowGuessPoints_itemStateChanged(arg0);
                                }
                            });
                            chckbxShowGuessPoints.setSelected(true);
                            panel.add(chckbxShowGuessPoints, "flowy,cell 0 0,alignx left");
                        }
                        {
                            chckbxShowFittedEllipses = new JCheckBox("Show Fitted Ellipses");
                            chckbxShowFittedEllipses.addItemListener(new ItemListener() {
                                public void itemStateChanged(ItemEvent e) {
                                    do_chckbxShowFittedEllipses_itemStateChanged(e);
                                }
                            });
                            chckbxShowFittedEllipses.setSelected(true);
                            panel.add(chckbxShowFittedEllipses, "flowx,cell 0 1,alignx left");
                        }
                        {
                            chckbxShowSearchEllipses = new JCheckBox("Show search boundaries");
                            chckbxShowSearchEllipses.addItemListener(new ItemListener() {
                                public void itemStateChanged(ItemEvent e) {
                                    do_chckbxShowSearchEllipses_itemStateChanged(e);
                                }
                            });
                            chckbxShowSearchEllipses.setActionCommand("Show search ellipses boundaries");
                            panel.add(chckbxShowSearchEllipses, "cell 0 2,alignx left");
                        }
                    }
                    {
                        panel_2 = new JPanel();
                        panel_2.setBorder(new TitledBorder(null, "Advanced Parameters", TitledBorder.LEADING, TitledBorder.TOP, null, null));
                        panel_1.add(panel_2, "cell 1 0,grow");
                        panel_2.setLayout(new MigLayout("", "[][grow]", "[][][][][]"));
                        {
                            lblPixtol = new JLabel("Pixel Range");
                            panel_2.add(lblPixtol, "cell 0 0,alignx trailing");
                        }
                        {
                            txtPixtol = new JTextField();
                            panel_2.add(txtPixtol, "cell 1 0,growx");
                            txtPixtol.setColumns(10);
                        }
                        {
                            lblArcSize = new JLabel("Arc Size");
                            panel_2.add(lblArcSize, "cell 0 1,alignx trailing");
                        }
                        {
                            txtArcsize = new JTextField();
                            panel_2.add(txtArcsize, "cell 1 1,growx");
                            txtArcsize.setColumns(10);
                        }
                        {
                            lblEsdfact = new JLabel("Thold (ESD fact)");
                            panel_2.add(lblEsdfact, "cell 0 2,alignx trailing");
                        }
                        {
                            txtEsdfact = new JTextField();
                            panel_2.add(txtEsdfact, "cell 1 2,growx");
                            txtEsdfact.setColumns(10);
                        }
                        {
                            lblOmmitRings = new JLabel("ommit rings");
                            panel_2.add(lblOmmitRings, "cell 0 3,alignx trailing");
                        }
                        {
                            txtOmmitrings = new JTextField();
                            panel_2.add(txtOmmitrings, "cell 1 3,growx");
                            txtOmmitrings.setColumns(10);
                        }
                        {
                            btnRecalc = new JButton("Recalc");
                            panel_2.add(btnRecalc, "cell 0 4 2 1,alignx center");
                            btnRecalc.addActionListener(new ActionListener() {
                                public void actionPerformed(ActionEvent arg0) {
                                    do_btnRecalc_actionPerformed(arg0);
                                }
                            });
                        }
                    }
                }
            }
            {
                this.panel_right = new JPanel();
                this.panel_right.setBackground(Color.BLACK);
                this.splitPane.setRightComponent(this.panel_right);
                panel_right.setLayout(new MigLayout("insets 5", "[grow]", "[grow]"));
                {
                    this.scrollPane_1 = new JScrollPane();
                    this.scrollPane_1.setBorder(null);
                    this.panel_right.add(this.scrollPane_1, "cell 0 0,grow");
                    {
                        this.tAOut = new LogJTextArea();
                        this.tAOut.setTabSize(4);
                        this.tAOut.setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));
                        this.tAOut.setWrapStyleWord(true);
                        this.tAOut.setLineWrap(true);
                        this.tAOut.setEditable(false);
                        // this.textArea.setForeground(Color.GREEN);
                        // this.textArea.setBackground(Color.BLACK);
                        this.scrollPane_1.setViewportView(this.tAOut);
                    }
                }
            }
        }
        {
            JPanel buttonPane = new JPanel();
            getContentPane().add(buttonPane, BorderLayout.SOUTH);
            JButton okButton = new JButton("close");
            okButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent arg0) {
                    do_okButton_actionPerformed(arg0);
                }
            });
            {
                this.btnApply = new JButton("Apply results to Image");
                this.btnApply.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent arg0) {
                        do_btnNewButton_actionPerformed(arg0);
                    }
                });
                buttonPane.setLayout(new MigLayout("", "[][][][grow]", "[25px]"));
                btnImageParameters = new JButton("Image Parameters");
                buttonPane.add(btnImageParameters, "cell 0 0");
                btnImageParameters.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent arg0) {
                        do_btnImageParameters_actionPerformed(arg0);
                    }
                });
                buttonPane.add(this.btnApply, "cell 1 0,alignx center,aligny center");
            }
            {
                btnWriteCalFile = new JButton("Write CAL file");
                btnWriteCalFile.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        do_btnWriteCalFile_actionPerformed(e);
                    }
                });
                buttonPane.add(btnWriteCalFile, "cell 2 0");
            }
            okButton.setActionCommand("OK");
            buttonPane.add(okButton, "cell 3 0,alignx right,aligny center");
            getRootPane().setDefaultButton(okButton);
        }

        this.setAlwaysOnTop(true);

        tAOut.ln("** LaB6 instrumental parameters calibration **");
        inicia();
        
        
    }

    public void setIpanel(ImagePanel ipanel){
        this.ip = ipanel;
    }
    
    public ImagePanel getIPanel() {
        return ip;
    }

    protected void inicia(){
        this.patt2D = this.getIPanel().getPatt2D();
        this.pointsRing1circle=null; //perque no funcioni el recalc

        refCX=0;
        refCY=0;
        refMD=0;
        refTiltDeg=0;
        refRotDeg=0;
        
        txtPixtol.setText(Integer.toString(findElliPointsTolPix));
        txtArcsize.setText(Integer.toString(findElliPointsArcSizemm));
        txtEsdfact.setText(Integer.toString(factESDIntensityThreshold));

    }
    
    @Override
    public void dispose() {
        this.chckbxCalibrate.setSelected(false);
        this.getIPanel().actualitzarVista();
        super.dispose();
    }

    protected void do_btnNewButton_actionPerformed(ActionEvent arg0) {
        if (getRefMD() > 0) {
            patt2D.setDistMD(refMD);
            patt2D.setCentrX(refCX);
            patt2D.setCentrY(refCY);
            patt2D.setTiltDeg(refTiltDeg);
            patt2D.setRotDeg(refRotDeg);
            tAOut.ln("Values set as image calibration parameters");
            this.getIPanel().actualitzarVista();
            this.getIPanel().getMainFrame().updateIparameters();
            return;
        }
        tAOut.ln("Please perform a calculation first");
    }

    protected void do_cbox_onTop_itemStateChanged(ItemEvent arg0) {
        this.setAlwaysOnTop(cbox_onTop.isSelected());
    }

    protected void do_chckbxCalibrate_itemStateChanged(ItemEvent arg0) {
        this.calibrating = chckbxCalibrate.isSelected();
        this.getIPanel().actualitzarVista();
    }

    protected void do_lbllist_mouseEntered(MouseEvent e) {
        lbllist.setForeground(Color.red);
    }

    protected void do_lbllist_mouseExited(MouseEvent e) {
        lbllist.setForeground(Color.black);
    }

    protected void do_lbllist_mouseReleased(MouseEvent e) {
      tAOut.ln("");
      tAOut.ln("** CALIBRATION HELP **");
      tAOut.ln(" 1) Click Start Manual Calibration\n"
              + " 2) Click 5 or more points within the inner ring\n"
              + " 3) Click Finish -- ellipses will be fitted to the peak rings\n"
              + " 4) Click on recalc if you change the parameters (without clicking points again)\n"
              + " 5) To keep final parameters click on apply\n"
              + " \n"
              + "OR\n"
              + " 1) Click on autocalibration\n"
              + " (it need aprox. initial parameters and the first ring should be close to a circle)\n"
              + "\n");
    }

    protected void do_okButton_actionPerformed(ActionEvent arg0) {
        this.getIPanel().actualitzarVista();
        this.dispose();
    }
    
    protected void do_btnImageParameters_actionPerformed(ActionEvent arg0) {
        if (patt2D != null) {
            this.ip.getMainFrame().do_mntmInstrumentalParameters_actionPerformed(null);
//            if (paramDialog == null){
//                paramDialog = new Param_dialog(this.ip,this.patt2D);
//            }
//            paramDialog.inicia();;
//            paramDialog.setVisible(true);
        }
    }
    
    private void readFindElliPars(){
        try{
            findElliPointsTolPix = Integer.parseInt(txtPixtol.getText());   
            findElliPointsMinPixLine = findElliPointsTolPix;
        }catch(Exception ex){
            log.config("using default value for findElliPointsTolPix");
        }
        try{
            findElliPointsArcSizemm = Integer.parseInt(txtArcsize.getText());   
        }catch(Exception ex){
            log.config("using default value for findElliPointsArcSizemm");
        }
        try{
            factESDIntensityThreshold = Integer.parseInt(txtEsdfact.getText());   
        }catch(Exception ex){
            log.config("using default value for factESDIntensityThreshold");
        }
        try{
            String ommitstr = txtOmmitrings.getText().trim().replace(',', ' ');
            log.config(ommitstr);
            String[] ommitlist = ommitstr.trim().split("\\s+");
            ommitRings = new ArrayList<Integer>();
            for (int i=0; i< ommitlist.length;i++){
                ommitRings.add(Integer.parseInt(ommitlist[i]));
                log.config(Integer.toString(ommitRings.get(i)));    
            }
        }catch(Exception ex){
            ommitRings = new ArrayList<Integer>();
            log.config("using default value for ommitRings");
        }       
    }
    
    //omplim automaticament els pointsRing1circle segons distMD i dspacing 1r pic LaB6 (considerem CERCLE)
    public void do_btnAuto_actionPerformed(ActionEvent arg0) {
        
        solutions = new ArrayList<EllipsePars>(); //BORREM ANTERIORS SOLUCIONS
        
        if(!checkWavePixSDist(true))return;
        
        this.readFindElliPars();
        
        //ara cal buscar el suposat radi del primer pic de LaB6 considerant els param instrumentals actuals
        circleRadius = (float) ((patt2D.getDistMD()/patt2D.getPixSx()) * FastMath.tan(2*FastMath.asin(patt2D.getWavel()/(2*CalibOps.LaB6_d[1]))));
        circleCenter = new Point2D.Float(patt2D.getCentrX(),patt2D.getCentrY());
        log.writeNameNumPairs("config", true, "circleRadius", circleRadius);
        this.pointsRing1circle = findRingPoints(this.getCircleRadius(),findElliPointsTolPix);
        
        //busquem ellipses (ja llegirem els parametres dins, abans ho feiem aqui pero calcEllipses tambe es pot cridar amb recalc)
        this.calcEllipses();
        
        //calculem parametres instrumentals
        if (solutions.size()>0){
            this.calcInstrumFromEllipses();
        }
        this.getIPanel().actualitzarVista();
    }
    
    private boolean checkWavePixSDist(boolean showParams){
        if((!patt2D.checkIfWavel() || !patt2D.checkIfPixSize() || !patt2D.checkIfDistMD())){
            tAOut.afegirText(true, true, "Please provide pixel size, sample-detector distance and wavelength");
            if(showParams)btnImageParameters.doClick();
            return false;
        }
        return true;
    }
    
    protected void do_btnAutoCalibration_itemStateChanged(ItemEvent e) {
        
        if (this.btnAutoCalibration.isSelected()){
            //comprovem que hi hagin els valors de pixsize i wavelength
            if(!checkWavePixSDist(true)){
                this.btnAutoCalibration.setSelected(false);
                return;
            }
            this.pointsRing1circle = new ArrayList<Point2D.Float>();
            solutions = new ArrayList<EllipsePars>(); //BORREM ANTERIORS SOLUCIONS
            this.btnAutoCalibration.setText("Click here when finished!");
            this.setSetting1stPeakCircle(true);
        }else{
            this.btnAutoCalibration.setText("Start Calibration");
            this.setSetting1stPeakCircle(false);
            //busquem ellipses (ja llegirem els parametres dins, abans ho feiem aqui pero calcEllipses tambe es pot cridar amb recalc)
            this.readFindElliPars();
            this.calcEllipses();
            
            //calculem parametres instrumentals
            if (solutions.size()>0){
                this.calcInstrumFromEllipses();
            }
        }
        this.getIPanel().actualitzarVista();
    }
    
    private void calcEllipses(){
        if ((this.pointsRing1circle!=null)){
            
            //INICIALITZEM I CHECKEJEM ELS PARAMETRES ABANS DE CALCULAR/BUSCAR ELLIPSES
            this.cerques=new ArrayList<Line2D.Float>();
            this.ellicerques=new ArrayList<EllipsePars>();
            
            //ARA JA PODEM FER LA CERCA
            
            //first we fit a circle and calculate the aprox. Center and distMD, we assume the wavelength known
            Point2D.Float[] d = new Point2D.Float[pointsRing1circle.size()];
            for (int i=0; i<pointsRing1circle.size();i++){
                d[i] = pointsRing1circle.get(i);
            }
            pointsRing1circle.toArray(d);

            solutions = new ArrayList<EllipsePars>();

            //ARA PROVEM DE FITTEJAR UNA ELLIPSE i SI NO FUNCIONA DONCS UN CERCLE
            EllipsePars p = new EllipsePars();
            p.setLab6ring(1);
            p.setEstimPoints(pointsRing1circle);
            p.fitElli();
            if (!p.isFit()){
                log.warning("Error fitting ellipse for 1st ring, trying 1st with circle...");
                //try with a circle
                this.fitCircle(d);
                if (this.getCircleRadius()<=0){
                    log.warning("Error also in circle fit, try to add more points and/or check image parameters");
                    tAOut.stat("Impossible to fit lab6 peak number 1. Please check image parameters and/or add more points");
                    return;
                }
                p.setEstimPoints(findRingPoints(this.getCircleRadius(),findElliPointsTolPix)); //posem punts del cercle
            }else{
                p.setEstimPoints(findRingPoints(p,findElliPointsTolPix)); //busquem mes punts de l'elipse
            }
            
            //aqui tindrem o bé els punts del cercle o de l'elipse a estimpoints
            //fem ara el fit definitiu d'una ellipse amb tots els punts
            p.fitElli();
            
            if (p.isFit()){
                p.logElliPars("debug");
                solutions.add(p);                
            }else{
                log.warning("Impossible to fit ellipse number 1. Please check.");
                tAOut.stat("Impossible to fit lab6 peak number 1. Please check image parameters and/or add more points");
                return;
            }

            //distMD en pixels...
            float estMD = -1;
            estMD = estimMDdist(p, 1);//estimacio distMD primer pic
            float wave = patt2D.getWavel();
            
            //now we use the previous ellipse to fit the next one
            int fitCount = 1; //number of rings fitted
            for (int i=2;i<maxLaB6ring;i++){
                
                log.config("ring no."+i);
                
                if (ommitRings.contains(i))continue;
                
                EllipsePars p0 = solutions.get(fitCount-1);
                
                float twoth = (float) (2*FastMath.asin(wave/(2*CalibOps.LaB6_d[i])));
                float radiPix = (float) (FastMath.tan(twoth)*estMD);
                
                float twothP0 = (float) (2*FastMath.asin(wave/(2*CalibOps.LaB6_d[p0.getLab6ring()])));
                float radiPixP0 = (float) (FastMath.tan(twothP0)*estMD);
                
                float factRP0maj = (float) (p0.getRmax()/radiPixP0);
                float factRP0men = (float) (p0.getRmin()/radiPixP0);

                log.writeNameNumPairs("CONFIG",true,"radiPixP0 factRP0maj factRP0men", radiPixP0,factRP0maj,factRP0men);

                float r1 = (float) (p0.getRmax()); 
                float r2 = (float) (p0.getRmin());                
                float factRmax = factRP0maj;
                float factRmin = factRP0men;
                
                log.writeNameNumPairs("CONFIG",true,"r1 r2 MD1 twoth radipix", r1,r2,estMD*patt2D.getPixSx(),FastMath.toDegrees(twoth),radiPix);

                searchElliLimitFactor = FastMath.max(1.2f, searchElliLimitFactor);
                
                if ((radiPix*factRmax)>(patt2D.getDimX()/searchElliLimitFactor))break;
                if ((radiPix*factRmin)>(patt2D.getDimX()/searchElliLimitFactor))break;
                
                EllipsePars p1 = new EllipsePars(radiPix*factRmax, radiPix*factRmin, p0.getXcen(), p0.getYcen(), p0.getAngrot());
                EllipsePars pN = new EllipsePars();
                pN.setEstimPoints(this.findRingPoints(p1, findElliPointsTolPix));
                //test with circle...
                pN.setLab6ring(i);
                pN.fitElli();
                if(pN.isFit()){
                    pN.logElliPars("debug");
                    solutions.add(pN);
                    fitCount = fitCount +1;
                    //fem nova estimacio mostra-detector amb la nova ellipse (en pixels)
                    estMD = estimMDdist(pN,i);
                }else{
                    log.warning("Could not fit ring nr. "+i);
                }
            }
        }
    }
    
    //busca punts al voltant d'un cercle radi cradi amb un rang +-tol (del radi)
    //unitats PIXELS
    private ArrayList<Point2D.Float> findRingPoints(float cradi,float tol){
        //parametric circle
        //x  =  h + r cos(t)
        //y  =  k + r sin(t)
        ArrayList<Point2D.Float> ringPoints = new ArrayList<Point2D.Float>();
        //Per determinar el num de punts, l'unic parametre es la separacio en pixels entre els punts (arcpix)
        float arcInPixels = findElliPointsArcSizemm/this.patt2D.getPixSx();
        float angstep = (float) (FastMath.asin(arcInPixels/(2*cradi))/2);
        log.writeNameNumPairs("config", true, "angstep", FastMath.toDegrees(angstep));
        
        for (float a = 0f; a<2*FastMath.PI; a = a + angstep){
            float xmaxI=0;
            float ymaxI=0;
            float maxI=0;
            float meanI=0;
            int npix = 0;
            for (float r = cradi-tol; r<=cradi+tol; r = r+0.5f){ //161103 canvi +1 per +0.5
                float xpix = (float) (this.getCircleCenter().x + r * FastMath.cos(a));
                float ypix = (float) (this.getCircleCenter().y + r * FastMath.sin(a));
                //agafem aquest com a pic central i ara analitzem una zona quadrada de nxn pixels. e.g. n=3?
                float inten = 0;
                for (int i = -1; i<2; i++){
                    int px = (int)(xpix)+i;
                    int py = (int)(ypix)+i;
                    if (!this.patt2D.isInside(px, py))continue;
                    if (this.patt2D.isInExZone(px, py))continue;
                    inten = this.patt2D.getInten(px, py);
                    if (inten>maxI){
                        maxI = inten;
                        xmaxI = px;
                        ymaxI = py;
                    }
                }
                meanI = meanI + inten;
                npix = npix + 1;
            }
            meanI = meanI/npix;
            float threshold = (float) (meanI + factESDIntensityThreshold*FastMath.sqrt(meanI));
            if (maxI > threshold){
                ringPoints.add(new Point2D.Float(xmaxI,ymaxI));    
            }
        }
        return ringPoints;
    }
    
    //busca punts al voltant d'una ellipse amb un rang +-tol (del radi)
    //unitats PIXELS
    private ArrayList<Point2D.Float> findRingPoints(EllipsePars elli,float tol){

        ArrayList<Point2D.Float> ringPoints = new ArrayList<Point2D.Float>();
        
        float cradi = (float) ((elli.getRmax()+elli.getRmin())/2);
        float facRmax = (float) (elli.getRmax()/cradi);
        float facRmin = (float) (elli.getRmin()/cradi);

        //ANGULAR STEP in order to have an arc of aprox 1mm
        float arcInPixels = findElliPointsArcSizemm/this.patt2D.getPixSx();
        float angstep = (float) (FastMath.asin(arcInPixels/(2*cradi))/2);
        log.writeNameNumPairs("config", true, "angstep", FastMath.toDegrees(angstep));
        
        //debug Posem les max i min elli de la cerca
        ellicerques.add(new EllipsePars((cradi-tol)*facRmax, (cradi-tol)*facRmin, elli.getXcen(),elli.getYcen(),elli.getAngrot()));
        ellicerques.add(new EllipsePars((cradi+tol)*facRmax, (cradi+tol)*facRmin, elli.getXcen(),elli.getYcen(),elli.getAngrot()));
        
        for (float a = 0f; a<2*FastMath.PI; a = a + angstep){
            float xmaxI=0;
            float ymaxI=0;
            float maxI=0;
            float meanI=0;
            int npix = 0;
            //debug segments
//            if (a==FastMath.PI/4) cerques.add(new Line2D.Float(x1, y1, x2, y2))
            for (float r = cradi-tol; r<=cradi+tol; r = r+1){
                Point2D.Float p = elli.getEllipsePoint((float)FastMath.toDegrees(a), r*facRmax, r*facRmin);
                float inten = 0;
                //cerca en un quadrat de aresta 3
                for (int i = -1; i<2; i++){
                    int px = (int)(p.x)+i;
                    int py = (int)(p.y)+i;
                    if (!this.patt2D.isInside(px,py)){
                        continue;
                    }
                    if (this.patt2D.isInExZone(px,py)){
                        continue;
                    }
                    inten = this.patt2D.getInten(px,py);
                    log.writeNameNumPairs("fine", true, "ang,radi,px,py,inten,maxI,xmaxI,ymaxI", FastMath.toDegrees(a),r,px,py,inten,maxI,xmaxI,ymaxI);
                    if (inten>maxI){
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
            meanI = meanI/npix;
            float threshold = (float) (meanI + factESDIntensityThreshold*FastMath.sqrt(meanI));
//            float threshold = meanI * 1.5f; //criteri mes estricte
            if (maxI > threshold){
                ringPoints.add(new Point2D.Float(xmaxI,ymaxI));    
            }else{
                log.writeNameNums("fine", true, "under threshold (maxI meanI npix threshold)",maxI,meanI,npix,threshold);
            }
        }
        return ringPoints;
    }
    
    
    private float estimMDdist(EllipsePars e, int lab6peak){
        float wave = patt2D.getWavel();
        float twoth = (float) (2*FastMath.asin(wave/(2*CalibOps.LaB6_d[lab6peak])));
        float estMD = (float) ((e.getRmin()*e.getRmin())/(FastMath.tan(twoth)*e.getRmax()));
        return estMD;
    }
    
    
    public EllipsePars getEllipseForLaB6ring(int ringN){
        Iterator<EllipsePars> itre = solutions.iterator();
        while (itre.hasNext()){
            EllipsePars e = itre.next();
            if (e.getLab6ring()==ringN){
                return e;
            }
        }
        return null;
    }
    
    private void calcInstrumFromEllipses(){
        
        ArrayList<Double> distsMD = new ArrayList<Double>();
        ArrayList<Double> tilts = new ArrayList<Double>();
        ArrayList<Double> rots = new ArrayList<Double>();
        ArrayList<Point2D.Float> centresMenys = new ArrayList<Point2D.Float>();
        ArrayList<Point2D.Float> centresMes = new ArrayList<Point2D.Float>();
        //we do it for all the rings up to maxLaB6 rings ecept the ommitted
        for (int i=1; i<maxLaB6ring; i++){

            if (ommitRings.contains(i))continue;

            EllipsePars e = this.getEllipseForLaB6ring(i);
            if (e==null){continue;}
            
            float wave = patt2D.getWavel();
            double tth = 2*FastMath.asin(wave/(2*CalibOps.LaB6_d[i]));
            double tantth = FastMath.tan(tth);

            //estimate of tilt,distMD 
            double exen = FastMath.sqrt(FastMath.max(0.,1.-((e.getRmin()*e.getRmin())/(e.getRmax()*e.getRmax()))));
            double tilt = FastMath.asin(exen*FastMath.cos(tth));
            double distMD = (e.getRmin()*e.getRmin())/(tantth*e.getRmax());
            //distance from the ellipse center to the beam center
            double c = e.getRmax()*tantth*FastMath.tan(tilt); 

            double rot = e.getAngrot();

            //angrot es l'angle "azimut" respecte les 12h (+cw) que ens diu quan està rotat Rmaj
            //agafem punt 0,0 el centre de l'ellipse (relatiu) i apliquem la rotacio de "c"
            //l'angle el faig negatiu perque sigui una rotacio clockwise tal com defineix angrot
            double xmes = 0*FastMath.cos(-rot) - c*FastMath.sin(-rot);
            double ymes = 0*FastMath.sin(-rot) + (-c)*FastMath.cos(-rot);
            double xmenys = 0*FastMath.cos(-rot) - (-c)*FastMath.sin(-rot);
            double ymenys = 0*FastMath.sin(-rot) + c*FastMath.cos(-rot);
            
            //ara ja tinc en relatiu (centre ellipse) les dues possibles posicions del beam, hem de tornar-ho a pixels absoluts:
            //atencio, es -y perque tenim l'origen a dalt a l'esquerra
            log.writeNameNumPairs("config", true, "xmes,ymes,xmenys,ymenys,e.getXcen(),e.getYcen()", xmes,ymes,xmenys,ymenys,e.getXcen(),e.getYcen());
            double centreXmes = e.getXcen()+xmes;
            double centreYmes = e.getYcen()-ymes;
            double centreXmenys = e.getXcen()+xmenys;
            double centreYmenys = e.getYcen()-ymenys;
            
            //logging debug
            log.config("---- RING nr."+i+" 2theta="+FastMath.toDegrees(tth));
            log.writeNameNumPairs("CONFIG",true,"angRotDeg,tiltDeg,distMD",FastMath.toDegrees(rot), FastMath.toDegrees(tilt), distMD);
            log.writeNameNumPairs("FINE",true,"c,xmes,ymes,xmenys,ymenys", c,xmes,ymes,xmenys,ymenys);
            log.writeNameNumPairs("CONFIG",true,"centreX+,centreY+,centreX-,centreY-", centreXmes,centreYmes,centreXmenys,centreYmenys);
            
            distsMD.add(distMD*patt2D.getPixSx());
            tilts.add(tilt);
            centresMenys.add(new Point2D.Float((float)centreXmenys,(float)centreYmenys));
            centresMes.add(new Point2D.Float((float)centreXmes,(float)centreYmes));
            rots.add(rot);
        }
        
        double meanMD=0;
        double meanTilt=0;
        double meanRot=0;
        double meanCXm=0;
        double meanCYm=0;
        double meanCXp=0;
        double meanCYp=0;
        
        for (int i=0;i<distsMD.size();i++){
            log.writeNameNumPairs("config", true, "MD CXM CYM CXP CXP Tilt", distsMD.get(i),centresMenys.get(i).x,centresMenys.get(i).y,centresMes.get(i).x,centresMes.get(i).y,FastMath.toDegrees(tilts.get(i)));
            meanMD=meanMD+distsMD.get(i);
            meanTilt=meanTilt+tilts.get(i);
            meanRot=meanRot+rots.get(i);
            meanCXm=meanCXm+centresMenys.get(i).x;
            meanCYm=meanCYm+centresMenys.get(i).y;
            meanCXp=meanCXp+centresMes.get(i).x;
            meanCYp=meanCYp+centresMes.get(i).y;
        }
        int n = distsMD.size();
        meanMD=meanMD/n;
        meanTilt=meanTilt/n;
        meanRot=meanRot/n;
        meanCXm=meanCXm/n;
        meanCYm=meanCYm/n;
        meanCXp=meanCXp/n;
        meanCYp=meanCYp/n;

        log.config("Rot guess (º)="+FastMath.toDegrees(meanRot));
        log.config("tilt guess(º)="+FastMath.toDegrees(meanTilt));
        
        //CHOOSE THE BEST CENTER
        //as initial guess I will chose the closest to the first ellipse center
        double bestCX=0;
        double bestCY=0;
        EllipsePars e = this.getEllipseForLaB6ring(1);
        double ex = e.getXcen();
        double ey = e.getXcen();
        double diffPlus = FastMath.abs(ex-meanCXp)*FastMath.abs(ex-meanCXp) + FastMath.abs(ey-meanCYp)*FastMath.abs(ey-meanCYp);
        double diffMinus = FastMath.abs(ex-meanCXm)*FastMath.abs(ex-meanCXm) + FastMath.abs(ey-meanCYm)*FastMath.abs(ey-meanCYm);
        if (diffMinus < diffPlus){
            bestCX=meanCXm;
            bestCY=meanCYm;
        }else{
            bestCX=meanCXp;
            bestCY=meanCYp;
        }
        log.config("Center guess option 1 (pX pY)="+meanCXm+" "+meanCYm);
        log.config("Center guess option 2 (pX pY)="+meanCXp+" "+meanCYp);
        log.config("Best? Center guess (pX pY)="+bestCX+" "+bestCY);
        log.config("DistMD guess (mm)="+meanMD);

        this.setRefCX((float) bestCX);
        this.setRefCY((float) bestCY);
        this.setRefMD((float) meanMD);
        this.setRefTiltDeg((float) FastMath.toDegrees(meanTilt));
        this.setRefRotDeg((float) FastMath.toDegrees(meanRot));
        
        //=========== MINIMITZACIO AMB 2THETA (dspacing)
        MultivariateFunction function = new MultivariateFunction() {
            @Override
            public double value(double[] array) {
                double res = CalibOps.minimize2Theta(patt2D, array[0],array[1],array[2],array[3],array[4], solutions);
                log.writeNameNums("config", true, "array,sum", array[0],array[1],array[2],array[3],array[4],res);
                return res;
            }
        };
        SimplexOptimizer optimizer = new SimplexOptimizer(1e-5, 1e-10);
        PointValuePair optimum =
                optimizer.optimize(
                        new MaxEval(1000000), 
                        new ObjectiveFunction(function), 
                        GoalType.MINIMIZE, 
                        new InitialGuess(new double[]{ this.getRefCX(),this.getRefCY(),this.getRefMD(),this.getRefTiltDeg(),this.getRefRotDeg() }), 
                        new NelderMeadSimplex(new double[]{ 5, 5, 5, 0.2, 0.2 },1,2,0.5,0.5));

        log.debug("opt sol="+Arrays.toString(optimum.getPoint()) + " : " + optimum.getSecond());
        
        this.setRefCX((float) optimum.getPoint()[0]);
        this.setRefCY((float) optimum.getPoint()[1]);
        this.setRefMD((float) optimum.getPoint()[2]);
        this.setRefTiltDeg((float) optimum.getPoint()[3]);
        this.setRefRotDeg((float) optimum.getPoint()[4]);
        
        tAOut.afegirText(true, true,"Calibration results ================");
        tAOut.ln(" Distance Sample-Det (mm) ="+FileUtils.dfX_3.format(this.getRefMD()));
        tAOut.ln(" Beam Center (best) (pX pY) ="+FileUtils.dfX_3.format(this.getRefCX())+" "+FileUtils.dfX_3.format(this.getRefCY()));
        tAOut.ln(" Detecctor rot. (ind) (deg) = "+FileUtils.dfX_2.format(this.getRefRotDeg()));                
        tAOut.ln(" Detector tilt (deg) = "+FileUtils.dfX_3.format(this.getRefTiltDeg()));    
        tAOut.ln("=================================");
    }
        
    private void fitCircle(Point2D.Float[] points){
        try{
          CircleFitter fitter = new CircleFitter();
          fitter.initialize(points);
          int iter = fitter.minimize(100, 0.1f, 1.0e-12f);
          log.config("converged after " + iter + " iterations");
          log.writeNameNums("config",true,"Circle fit (x y radi iter): ",fitter.getCenter().x,fitter.getCenter().y,fitter.getRadius(),iter);
          this.setCircleCenter(fitter.getCenter());
          this.setCircleRadius(fitter.getRadius());
          
        } catch (Exception e) {
          log.config(e.getMessage());
          return;
        }
        
    }

    protected void do_btnRecalc_actionPerformed(ActionEvent arg0) {
        //busquem ellipses
        this.calcEllipses();
        
        //calculem parametres instrumentals
        if (solutions.size()>0){
            this.calcInstrumFromEllipses();
        }
        this.getIPanel().actualitzarVista();
    }
    
    public boolean isCalibrating() {
        return calibrating;
    }
    
    public void addPointToRing1Circle(Point2D.Float p){
        if (this.pointsRing1circle==null)return;
        this.pointsRing1circle.add(p);
    }
   
    public ArrayList<Point2D.Float> getPointsRing1circle() {
        return pointsRing1circle;
    }

    public static int getRadipunt() {
        return radiPunt;
    }
    
    public ArrayList<EllipsePars> getSolutions() {
        return solutions;
    }

    public boolean isSetting1stPeakCircle() {
        return isSetting1stPeakCircle;
    }

    public void setSetting1stPeakCircle(boolean isSetting1stPeakCircle) {
        this.isSetting1stPeakCircle = isSetting1stPeakCircle;
    }
    
    public Point2D.Float getCircleCenter() {
        return circleCenter;
    }

    public void setCircleCenter(Point2D.Float circleCenter) {
        this.circleCenter = circleCenter;
    }

    public float getCircleRadius() {
        return circleRadius;
    }

    public void setCircleRadius(float circleRadius) {
        this.circleRadius = circleRadius;
    }

    public float getRefCX() {
        return refCX;
    }

    public void setRefCX(float refCX) {
        this.refCX = refCX;
    }

    public float getRefCY() {
        return refCY;
    }

    public void setRefCY(float refCY) {
        this.refCY = refCY;
    }

    public float getRefMD() {
        return refMD;
    }

    public void setRefMD(float refMD) {
        this.refMD = refMD;
    }

    public float getRefTiltDeg() {
        return refTiltDeg;
    }

    public void setRefTiltDeg(float refTiltDeg) {
        this.refTiltDeg = refTiltDeg;
    }

    public float getRefRotDeg() {
        return refRotDeg;
    }

    public void setRefRotDeg(float refRotDeg) {
        this.refRotDeg = refRotDeg;
    }
    
    public boolean isShowGuessPoints(){
        return chckbxShowGuessPoints.isSelected();
    }
    
    public boolean isShowFittedEllipses(){
        return chckbxShowFittedEllipses.isSelected();
    }
    
    public boolean isShowSearchEllipsesBoundaries(){
        return chckbxShowSearchEllipses.isSelected();
    }
   
    public static float getSearchElliLimitFactor() {
        return searchElliLimitFactor;
    }

    public static void setSearchElliLimitFactor(float searchElliLimitFactor) {
        Calib_dialog.searchElliLimitFactor = searchElliLimitFactor;
    }
    
//    protected void do_btnGenlab_actionPerformed(ActionEvent arg0) {
//        ImgFileUtils.writeEDF(new File(D2Dplot_global.getWorkdir()+D2Dplot_global.separator+"LaB6gen.edf"), CalibOps.createLaB6Img(1024.f, 1024.f, 180.f, 15.f, 0.f, 0.4246f));
//    }

    public ArrayList<EllipsePars> getElliCerques(){
        return ellicerques;
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
        File calfile = FileUtils.fchooserSaveAsk(this,new File(D2Dplot_global.getWorkdir()), null);
        if (calfile != null){
            this.writeCALfile(calfile);
            D2Dplot_global.setWorkdir(calfile);
        }
    }
    
    public void writeCALfile(File calfile){
        
        String tm = D2Dplot_global.getStringTimeStamp("yyyy-MM-dd 'at' HH:mm");
        
        try {
            PrintWriter output = new PrintWriter(new BufferedWriter(new FileWriter(calfile)));
            output.println(String.format("# Calibration for %s on %s", patt2D.getImgfileString(),tm));
            output.println(String.format("CALFIL = %s [d2Dplot]", patt2D.getImgfile().getAbsolutePath()));
            output.println(String.format("X-BEAM CENTRE = %.2f", this.getRefCX()));
            output.println(String.format("Y-BEAM CENTRE = %.2f", this.getRefCY()));
            output.println(String.format("DISTANCE = %.3f", this.getRefMD()));
            output.println(String.format("WAVELENGTH = %.5f", patt2D.getWavel()));
            output.println(String.format("TILT ROTATION = %.2f", this.getRefRotDeg()));
            output.println(String.format("ANGLE OF TILT = %.3f", this.getRefTiltDeg()));
            output.println();
            output.println(String.format("SUBADU = %.1f", -9.5f));
            output.println(String.format("START AZIMUTH = %.1f", 0.f));
            output.println(String.format("END AZIMUTH = %.1f", 360.f));
            output.println(String.format("INNER RADIUS = %d", 0));
            output.println(String.format("OUTER RADIUS = %d", (int)(patt2D.getDimX()/2)-1));
            output.println(String.format("AZIMUTH BINS = %d", 1));
            output.println(String.format("RADIAL BINS = %d", (int)(patt2D.getDimX()/2)-1));
            output.println();
            output.println(String.format("#MASK FILE = %s", D2Dplot_global.getWorkdir()+D2Dplot_global.separator+"MASK.bin"));
            output.println("#Mask file is a zero-intensity image with mask pixels at -1 intensity");
            output.close();
        }catch(Exception ex){
            if (D2Dplot_global.isDebug())ex.printStackTrace();
            log.info(String.format("Error writting output CAL file: %s",calfile.toString()));
        }
    }
}
