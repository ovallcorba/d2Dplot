package vava33.d2dplot;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
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

import org.apache.commons.math3.stat.regression.SimpleRegression;
import org.apache.commons.math3.util.FastMath;

import com.vava33.jutils.FileUtils;
import com.vava33.jutils.LogJTextArea;
import com.vava33.jutils.VavaLogger;

import vava33.d2dplot.auxi.CircleFitter;
import vava33.d2dplot.auxi.EllipsePars;
import vava33.d2dplot.auxi.Pattern2D;
import net.miginfocom.swing.MigLayout;

import javax.swing.JToggleButton;
import javax.swing.border.TitledBorder;


public class Calib_dialog extends JDialog {

    private static final long serialVersionUID = -5947817749730984383L;

    private static float[] LaB6_d = { 0.0f, 4.156878635f, 2.939357609f, 2.399975432f, 2.078432243f, 1.859004281f,
            1.697043447f, 1.469674856f, 1.385628455f, 1.314520218f, 1.25335391f, 1.199991704f, 1.152911807f,
            1.110975349f, 1, .039218708f, 1.008191043f };
     
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
    private float refCX, refCY, refMD, refTiltDeg, refRotDeg;
    private boolean calibrating;
    private Pattern2D patt2D;

    private ArrayList<Point2D.Float> pointsRing1circle;
    private ArrayList<EllipsePars> solutions;
    private boolean isSetting1stPeakCircle=false;
    private Point2D.Float circleCenter = new Point2D.Float();
    private float circleRadius = -1f;
    
    private static final int radiPunt = 2;
    private static int findElliPointsTolPix = 5; //+- range
    private static int findElliPointsMinPixLine = findElliPointsTolPix;
    private static int findElliPointsArcSizemm = 1;
    private static int factESDIntensityThreshold = 4;
    private static ArrayList<Integer> ommitRings = new ArrayList<Integer>();
    
    private final static int DEF_findElliPointsTolPix = 5;
    private final static int DEF_findElliPointsMinPixLine = DEF_findElliPointsTolPix;
    private final static int DEF_findElliPointsArcSizemm = 1;
    private final static int DEF_factESDIntensityThreshold = 4;
    
    private static VavaLogger log = D2Dplot_global.log;
    private Param_dialog paramDialog;
    private JButton btnImageParameters;
    
    public ArrayList<Line2D.Float> cerques;
    public ArrayList<EllipsePars> ellicerques;
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
    
    /**
     * Create the dialog.
     */
    public Calib_dialog(Pattern2D pattern) {
        this.patt2D = pattern;
        setIconImage(Toolkit.getDefaultToolkit().getImage(Calib_dialog.class.getResource("/img/Icona.png")));
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("LaB6 Calibration");
        // setBounds(100, 100, 660, 730);
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        int width = 660;
        int height = 730;
        int x = (screen.width - width) / 2;
        int y = (screen.height - height) / 2;
        setBounds(x, y, 522, 580);
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(this.contentPanel, BorderLayout.CENTER);
        GridBagLayout gbl_contentPanel = new GridBagLayout();
        gbl_contentPanel.columnWidths = new int[] { 0, 0 };
        gbl_contentPanel.rowHeights = new int[] { 0, 0 };
        gbl_contentPanel.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
        gbl_contentPanel.rowWeights = new double[] { 1.0, Double.MIN_VALUE };
        contentPanel.setLayout(gbl_contentPanel);
        {
            this.splitPane = new JSplitPane();
            this.splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
            GridBagConstraints gbc_splitPane = new GridBagConstraints();
            gbc_splitPane.fill = GridBagConstraints.BOTH;
            gbc_splitPane.gridx = 0;
            gbc_splitPane.gridy = 0;
            contentPanel.add(this.splitPane, gbc_splitPane);
            {
                this.panel_left = new JPanel();
                this.splitPane.setLeftComponent(this.panel_left);
                {
                    panel_left.setLayout(new MigLayout("", "[grow][grow][]", "[26px][][][grow]"));
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
                btnAutoCalibration = new JToggleButton("Start Calibration");
                btnAutoCalibration.addItemListener(new ItemListener() {
                    public void itemStateChanged(ItemEvent e) {
                        do_btnAutoCalibration_itemStateChanged(e);
                    }
                });
                panel_left.add(btnAutoCalibration, "flowy,cell 0 2 2 1,grow");
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
                    this.lbllist.setFont(new Font("Tahoma", Font.BOLD, 14));
                    this.panel_left.add(this.lbllist, "cell 2 2,alignx right,aligny center");
                }
                {
                    panel_1 = new JPanel();
                    panel_left.add(panel_1, "cell 0 3 3 1,grow");
                    panel_1.setLayout(new MigLayout("", "[grow][grow]", "[grow]"));
                    {
                        panel = new JPanel();
                        panel_1.add(panel, "cell 0 0,grow");
                        panel.setBorder(new TitledBorder(null, "Display settings", TitledBorder.LEADING, TitledBorder.TOP, null, null));
                        panel.setLayout(new MigLayout("", "[grow]", "[][][]"));
                        {
                            chckbxShowGuessPoints = new JCheckBox("Show Guess Points");
                            chckbxShowGuessPoints.setSelected(true);
                            panel.add(chckbxShowGuessPoints, "flowy,cell 0 0,alignx left");
                        }
                        {
                            chckbxShowFittedEllipses = new JCheckBox("Show Fitted Ellipses");
                            chckbxShowFittedEllipses.setSelected(true);
                            panel.add(chckbxShowFittedEllipses, "flowx,cell 0 1,alignx left");
                        }
                        {
                            chckbxShowSearchEllipses = new JCheckBox("Show search boundaries");
                            chckbxShowSearchEllipses.setActionCommand("Show search ellipses boundaries");
                            panel.add(chckbxShowSearchEllipses, "cell 0 2,alignx left");
                        }
                    }
                    {
                        panel_2 = new JPanel();
                        panel_2.setBorder(new TitledBorder(null, "Advanced Parameters", TitledBorder.LEADING, TitledBorder.TOP, null, null));
                        panel_1.add(panel_2, "cell 1 0,grow");
                        panel_2.setLayout(new MigLayout("", "[][grow]", "[][][][]"));
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
                    }
                }
            }
            {
                this.panel_right = new JPanel();
                this.panel_right.setBackground(Color.BLACK);
                this.splitPane.setRightComponent(this.panel_right);
                GridBagLayout gbl_panel_right = new GridBagLayout();
                gbl_panel_right.columnWidths = new int[] { 0, 0 };
                gbl_panel_right.rowHeights = new int[] { 0, 0 };
                gbl_panel_right.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
                gbl_panel_right.rowWeights = new double[] { 1.0, Double.MIN_VALUE };
                this.panel_right.setLayout(gbl_panel_right);
                {
                    this.scrollPane_1 = new JScrollPane();
                    this.scrollPane_1.setBorder(null);
                    GridBagConstraints gbc_scrollPane_1 = new GridBagConstraints();
                    gbc_scrollPane_1.insets = new Insets(5, 5, 5, 5);
                    gbc_scrollPane_1.fill = GridBagConstraints.BOTH;
                    gbc_scrollPane_1.gridx = 0;
                    gbc_scrollPane_1.gridy = 0;
                    this.panel_right.add(this.scrollPane_1, gbc_scrollPane_1);
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
                buttonPane.setLayout(new MigLayout("", "[][][grow]", "[25px]"));
                btnImageParameters = new JButton("Image Parameters");
                buttonPane.add(btnImageParameters, "cell 0 0");
                btnImageParameters.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent arg0) {
                        do_btnImageParameters_actionPerformed(arg0);
                    }
                });
                buttonPane.add(this.btnApply, "cell 1 0,alignx center,aligny center");
            }
            okButton.setActionCommand("OK");
            buttonPane.add(okButton, "cell 2 0,alignx right,aligny center");
            getRootPane().setDefaultButton(okButton);
        }

        inicia();
        
    }

    protected void inicia(){
        tAOut.ln("** The LaB6 calibration is an experimental feature, use with caution **");
        this.setAlwaysOnTop(true);
        refCX=0;
        refCY=0;
        refMD=0;
        refTiltDeg=0;
        refRotDeg=0;
        
        txtPixtol.setText(Integer.toString(DEF_findElliPointsTolPix));
        txtArcsize.setText(Integer.toString(DEF_findElliPointsArcSizemm));
        txtEsdfact.setText(Integer.toString(DEF_factESDIntensityThreshold));

    }
    
    @Override
    public void dispose() {
        this.chckbxCalibrate.setSelected(false);
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
            return;
        }
        tAOut.ln("Please perform a calculation first");
    }

    protected void do_cbox_onTop_itemStateChanged(ItemEvent arg0) {
        this.setAlwaysOnTop(cbox_onTop.isSelected());
    }

    protected void do_chckbxCalibrate_itemStateChanged(ItemEvent arg0) {
        this.calibrating = chckbxCalibrate.isSelected();
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
      tAOut.ln(" 1) Click Start Calibration\n"
              + " 2) Click 5 or more points within the inner ring\n"
              + " 3) Click Finish -- ellipses will be fitted to peaks\n"
              + " 4) Click con calculate parameters\n"
              + " 5) To keep final parameters click on apply\n"
              + "\n");
//        tAOut.ln("");
//        tAOut.ln("** CALIBRATION HELP **");
//        tAOut.ln(" Fit manually (and carefully) the ellipses of two rings (of your choice) by "
//                + "moving and resizing the drawn boxes in the image. Then click on calculate.");
//        tAOut.ln("+----------------------+");
//        tAOut.ln("| LaB6 reflection list |");
//        tAOut.ln("+----------------------+");
//        tAOut.ln("|  Ring  h k l   d(A)  |");
//        tAOut.ln("|   1    1 0 0  4.156  |");
//        tAOut.ln("|   2    1 1 0  2.939  |");
//        tAOut.ln("|   3    1 1 1  2.399  |");
//        tAOut.ln("|   4    2 0 0  2.078  |");
//        tAOut.ln("|   5    2 1 0  1.859  |");
//        tAOut.ln("|   6    2 1 1  1.697  |");
//        tAOut.ln("|   7    2 2 0  1.469  |");
//        tAOut.ln("|   8    3 0 0  1.385  |");
//        tAOut.ln("|   9    3 1 0  1.314  |");
//        tAOut.ln("|  10    3 1 1  1.253  |");
//        tAOut.ln("|  11    2 2 2  1.199  |");
//        tAOut.ln("|  12    3 2 0  1.152  |");
//        tAOut.ln("|  13    3 2 1  1.110  |");
//        tAOut.ln("|  14    4 0 0  1.039  |");
//        tAOut.ln("|  15    4 1 0  1.008  |");
//        tAOut.ln("+----------------------+");
//        tAOut.ln("");
    }

    protected void do_okButton_actionPerformed(ActionEvent arg0) {
        this.dispose();
    }
    
    protected void do_btnImageParameters_actionPerformed(ActionEvent arg0) {
        if (patt2D != null) {
            if (paramDialog == null){
                paramDialog = new Param_dialog(patt2D);
            }
            paramDialog.setVisible(true);
        }
    }
    
    protected void do_btnAutoCalibration_itemStateChanged(ItemEvent e) {
        
        if (this.btnAutoCalibration.isSelected()){
            //comprovem que hi hagin els valors de pixsize i wavelength
            if((!patt2D.checkIfWavel() || !patt2D.checkIfPixSize())){
                tAOut.afegirText(true, true, "Please provide pixel size and wavelength");
                btnImageParameters.doClick();
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
            this.cerques=new ArrayList<Line2D.Float>();
            this.ellicerques=new ArrayList<EllipsePars>();
            
            //checkejem advanced parameters i calculem
            try{
                findElliPointsTolPix = Integer.parseInt(txtPixtol.getText());   
                findElliPointsMinPixLine = findElliPointsTolPix;
            }catch(Exception ex){
                findElliPointsTolPix = DEF_findElliPointsTolPix;
                findElliPointsMinPixLine = findElliPointsTolPix;
                log.config("using default value for findElliPointsTolPix");
            }
            try{
                findElliPointsArcSizemm = Integer.parseInt(txtArcsize.getText());   
            }catch(Exception ex){
                findElliPointsArcSizemm = DEF_findElliPointsArcSizemm;
                log.config("using default value for findElliPointsArcSizemm");
            }
            try{
                factESDIntensityThreshold = Integer.parseInt(txtEsdfact.getText());   
            }catch(Exception ex){
                factESDIntensityThreshold = DEF_factESDIntensityThreshold;
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
            
            //busquem ellipses
            this.calcEllipses();
            
            //calculem parametres instrumentals
            if (solutions.size()>0){
                this.calcInstrumFromEllipses();
            }
        }
    }
    
    private float estimMDdist(EllipsePars e, int lab6peak){
        float wave = patt2D.getWavel();
        float twoth = (float) (2*FastMath.asin(wave/(2*LaB6_d[lab6peak])));
        float estMD = (float) ((e.getRmin()*e.getRmin())/(FastMath.tan(twoth)*e.getRmax()));
        return estMD;
    }
    
    private float estimMDdist(float r1, float r2, int lab6peak){
        float wave = patt2D.getWavel();
        float twoth = (float) (2*FastMath.asin(wave/(2*LaB6_d[lab6peak])));
        float estMD = (float) ((r2*r2)/(FastMath.tan(twoth)*r1));
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
        
        //farem recta que millor s'ajusta als centres
        SimpleRegression sr = new SimpleRegression();
        Iterator<EllipsePars> itre = solutions.iterator();
        while (itre.hasNext()){
            EllipsePars e = itre.next();
            sr.addData(e.getXcen(), e.getYcen());
        }
        
        double slope = 0;
        double intercept = 0;
        try{
            slope = sr.getSlope();
            intercept = sr.getIntercept();
            log.writeNameNumPairs("CONFIG",true,"slope intercept", slope, intercept);            
        }catch(Exception ex){
            ex.printStackTrace();
            return;
        }


        //angle entre la recta i l'eix Y === rot
        //dos punts de la recta (punt 0,intercept) i un altre
        double x1 = 1;
        double y1 = slope * x1 + intercept;
        double vx = x1-0;
        double vy = y1-intercept;
        //eix Y cap amunt es vector (0,-1)
        double mod1 = FastMath.sqrt(vx*vx + vy*vy);
        double mod2 = FastMath.sqrt(1);
        double angle = FastMath.acos((vx*0+vy*-1)/(mod1*mod2));
        log.writeNameNumPairs("CONFIG",true,"ANGLE=", FastMath.toDegrees(angle));
        
        ArrayList<Double> distsMD = new ArrayList<Double>();
        ArrayList<Double> tilts = new ArrayList<Double>();
        ArrayList<Point2D.Float> centersMinus = new ArrayList<Point2D.Float>();
        ArrayList<Point2D.Float> centersPlus = new ArrayList<Point2D.Float>();

        
        //we do it for all the rings that we have
        for (int i=1; i<LaB6_d.length; i++){

            if (ommitRings.contains(i))continue;

            EllipsePars e = this.getEllipseForLaB6ring(i);
            if (e==null){continue;}
            
            float wave = patt2D.getWavel();
            double tth = (float) (2*FastMath.asin(wave/(2*LaB6_d[i])));
            double tantth = FastMath.tan(tth);
            double sintth = FastMath.sin(tth);
            double costth = FastMath.cos(tth);
            
            //estimate of tilt,distMD
            double tilt = FastMath.asin(FastMath.sqrt(FastMath.max(0.,1.-((e.getRmin()/e.getRmax())*(e.getRmin()/e.getRmax())))*costth));
            double distMD = (e.getRmin()*e.getRmin())/(tantth*e.getRmax());

            //test other method
            double exen = FastMath.sqrt(1-((e.getRmin()*e.getRmin())/(e.getRmax()*e.getRmax())));
            double tilt_alt = FastMath.atan(exen/tantth);
            
            //and centre (2 choices? sign of tilt)
            double zdisp = e.getRmax()*tantth*FastMath.tan(tilt);
            double zdism = e.getRmax()*tantth*FastMath.tan(-tilt);
            
            double centpx = e.getXcen()+zdisp*FastMath.sin(e.getAngrot());
            double centpy = e.getYcen()-zdisp*FastMath.cos(e.getAngrot());
            double centmx = e.getXcen()+zdism*FastMath.sin(e.getAngrot());
            double centmy = e.getYcen()-zdism*FastMath.cos(e.getAngrot());
            
            log.config("---- RING nr."+i);
            log.writeNameNumPairs("CONFIG",true,"tilt tiltdeg distMD zdisp zdism", tilt, FastMath.toDegrees(tilt), distMD, zdisp, zdism);
            log.writeNameNumPairs("CONFIG",true,"centpx centpy centmx centmy", centpx, centpy, centmx, centmy);
            log.writeNameNumPairs("CONFIG",true,"tiltalt tiltAltDeg", tilt_alt, FastMath.toDegrees(tilt_alt));
            
            //he canviat cos i sin degut a la meva convencio de rotacio des de la vertical
            centpx = e.getXcen()+zdisp*FastMath.cos(angle);
            centpy = e.getYcen()-zdisp*FastMath.sin(angle);
            centmx = e.getXcen()+zdism*FastMath.cos(angle);
            centmy = e.getYcen()-zdism*FastMath.sin(angle);
            log.config("COMONANGLE");
            log.writeNameNumPairs("CONFIG",true,"centpx centpy centmx centmy", centpx, centpy, centmx, centmy);
            log.config("----------------");
            
            distsMD.add(distMD*patt2D.getPixSx());
            tilts.add(tilt);
            centersMinus.add(new Point2D.Float((float)centmx,(float)centmy));
            centersPlus.add(new Point2D.Float((float)centpx,(float)centpy));

        }
        
        double meanMD=0;
        double meanTilt=0;
        double meanCXm=0;
        double meanCYm=0;
        double meanCXp=0;
        double meanCYp=0;
        for (int i=0;i<distsMD.size();i++){
            log.writeNameNumPairs("config", true, "MD CXM CYM CXP CXP Tilt )", distsMD.get(i),centersMinus.get(i).x,centersMinus.get(i).y,centersPlus.get(i).x,centersPlus.get(i).y,FastMath.toDegrees(tilts.get(i)));
            meanMD=meanMD+distsMD.get(i);
            meanTilt=meanTilt+tilts.get(i);
            meanCXm=meanCXm+centersMinus.get(i).x;
            meanCYm=meanCYm+centersMinus.get(i).y;
            meanCXp=meanCXp+centersPlus.get(i).x;
            meanCYp=meanCYp+centersPlus.get(i).y;
        }
        
        //FINAL RESULTS
        int n = distsMD.size();
        meanMD=meanMD/n;
        meanTilt=meanTilt/n;
        meanCXm=meanCXm/n;
        meanCYm=meanCYm/n;
        meanCXp=meanCXp/n;
        meanCYp=meanCYp/n;
        double bestCX=0;
        double bestCY=0;
        
        log.info("Rot (º)="+FastMath.toDegrees(angle));
        log.info("tilt (º)="+FastMath.toDegrees(meanTilt));
        //TODO: CHOOSE THE BEST CENTER
        //as initial guess I will chose the closest to the first ellipse center
        EllipsePars e = this.getEllipseForLaB6ring(1);
        double ex = e.getXcen();
        double ey = e.getXcen();
        double diffPlus = FastMath.abs(ex-meanCXp) + FastMath.abs(ey-meanCYp);
        double diffMinus = FastMath.abs(ex-meanCXm) + FastMath.abs(ey-meanCYm);
        if (diffMinus < diffPlus){
            bestCX=meanCXm;
            bestCY=meanCYm;
        }else{
            bestCX=meanCXp;
            bestCY=meanCYp;
        }
        log.config("Center option 1 (pX pY)="+meanCXm+" "+meanCYm);
        log.config("Center option 2 (pX pY)="+meanCXp+" "+meanCYp);
        log.info("Center (pX pY)="+bestCX+" "+bestCY);
        log.info("DistMD (mm)="+meanMD);
        
        this.setRefCX((float) bestCX);
        this.setRefCY((float) bestCY);
        this.setRefMD((float) meanMD);
        this.setRefTiltDeg((float) FastMath.toDegrees(meanTilt));
        this.setRefRotDeg((float) FastMath.toDegrees(angle));

        tAOut.afegirText(true, true,"Calibration results ============");
        tAOut.ln(" Detecctor rot. (deg) = "+FileUtils.dfX_2.format(FastMath.toDegrees(angle)));
        tAOut.ln(" Detector tilt (deg)  = "+FileUtils.dfX_3.format(FastMath.toDegrees(meanTilt)));    
        tAOut.ln(" Beam Center (pX pY)  ="+FileUtils.dfX_3.format(bestCX)+" "+FileUtils.dfX_3.format(bestCY));
        tAOut.ln(" Distance S-Det (mm)  ="+FileUtils.dfX_3.format(meanMD));
        tAOut.ln("=============================");
    }
    
    protected void calcEllipses(){
        if ((this.pointsRing1circle!=null)){
            
            //first we fit a circle and calculate the aprox. Center and distMD, we assume the wavelength known
            Point2D.Float[] d = new Point2D.Float[pointsRing1circle.size()];
            for (int i=0; i<pointsRing1circle.size();i++){
                d[i] = pointsRing1circle.get(i);
            }
            pointsRing1circle.toArray(d);
            this.fitCircle(d);
            if (this.getCircleRadius()<=0){
                log.info("Error in circle fit");
                return;
            }
            
            //distMD en pixels...
            float estMD = estimMDdist(this.getCircleRadius(), this.getCircleRadius(), 1);//estimacio distMD primer pic
            float wave = patt2D.getWavel();

            solutions = new ArrayList<EllipsePars>();
            
            //we fit an ellipse to the first ring using the previous aprox. cirlce fit //TODO USING CLICK POINTS
            EllipsePars p = new EllipsePars();
            p.setLab6ring(1);
//            p.setEstimPoints(findRingPoints(this.getCircleRadius(),findElliPointsTolPix));
            p.setEstimPoints(pointsRing1circle);
            p.fitElli();
            if (p.isFit()){
                p.printElliPars();
                solutions.add(p);                
            }else{
                log.info("Impossible to fit ellipse number 1. Please check.");
                return;
            }

            
            //now we use the previous ellipse to fit the next one
            int fitCount = 1; //number of rings fitted
            for (int i=2;i<LaB6_d.length;i++){
                
                log.config("ring no."+i);
                
                if (ommitRings.contains(i))continue;
                
                EllipsePars p0 = solutions.get(fitCount-1);
                
                float twoth = (float) (2*FastMath.asin(wave/(2*LaB6_d[i])));
                float radiPix = (float) (FastMath.tan(twoth)*estMD);

                if (radiPix>(patt2D.getDimX()/2.1))break;

                float r1 = (float) (p0.getRmax()); 
                float r2 = (float) (p0.getRmin());                
                float factRmax = r1/((r1+r2)/2);
                float factRmin = r2/((r1+r2)/2);
                
                log.writeNameNumPairs("CONFIG",true,"r1 r2 MD1 twoth radipix", r1,r2,estMD*patt2D.getPixSx(),FastMath.toDegrees(twoth),radiPix);
                
                EllipsePars p1 = new EllipsePars(radiPix*factRmax, radiPix*factRmin, p0.getXcen(), p0.getYcen(), p0.getAngrot());
                EllipsePars pN = new EllipsePars();
                pN.setEstimPoints(this.findRingPoints(p1, findElliPointsTolPix));
                //test with circle...
//                pN.setEstimPoints(this.findRingPoints(radiPix,findElliPointsTolPix));
                pN.setLab6ring(i);
                pN.fitElli();
                if(pN.isFit()){
                    pN.printElliPars();
                    solutions.add(pN);
                    fitCount = fitCount +1;
                    //fem nova estimacio mostra-detector amb la nova ellipse (en pixels)
//                    estMD = getMDdist((float)pN.getRmax(), (float)pN.getRmin(), i,i);
//                    estMD = (float) ((pN.getRmin()*pN.getRmin())/(FastMath.tan(twoth)*pN.getRmax()));
                    estMD = estimMDdist(pN,i);
                }else{
                    log.info("Could not fit ring nr. "+i);
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
        //to estimate 30 points per ellipse
//        float angstep = (float) FastMath.toRadians(360f / 100f);
        //TODO:angstep dependra del radi per tenir una distribucio homogenia
        //ANGULAR STEP in order to have an arc of aprox 1mm
        float arcInPixels = findElliPointsArcSizemm/this.patt2D.getPixSx();
        float angstep = (float) (FastMath.asin(arcInPixels/(2*cradi))/2);
        log.writeNameNumPairs("config", true, "angstep", FastMath.toDegrees(angstep));
        
        for (float a = 0f; a<2*FastMath.PI; a = a + angstep){
            float xmaxI=0;
            float ymaxI=0;
            float maxI=0;
            float meanI=0;
            int npix = 0;
            for (float r = cradi-tol; r<=cradi+tol; r = r+1){
                float xpix = (float) (this.getCircleCenter().x + r * FastMath.cos(a));
                float ypix = (float) (this.getCircleCenter().y + r * FastMath.sin(a));
                float inten = this.patt2D.getInten(FastMath.round(xpix), FastMath.round(ypix));
                if (inten>maxI){
                    maxI = inten;
                    xmaxI = xpix;
                    ymaxI = ypix;
                }
                meanI = meanI + inten;
                npix = npix + 1;
            }
            meanI = meanI/npix;
            float threshold = (float) (meanI + factESDIntensityThreshold*FastMath.sqrt(meanI));
//            float threshold = meanI * 1.5f; //criteri mes estricte
            if (maxI > threshold){
                ringPoints.add(new Point2D.Float(xmaxI,ymaxI));    
            }else{
//                log.writeNameNums("config", true, "under threshold (maxI meanI npix threshold)",maxI,meanI,npix,threshold);
            }
        }
        return ringPoints;
    }
    
    public ArrayList<EllipsePars> getElliCerques(){
        return ellicerques;
    }
    
    //busca punts al voltant d'una ellipse amb un rang +-tol (del radi)
    //unitats PIXELS
    private ArrayList<Point2D.Float> findRingPoints(EllipsePars elli,float tol){
        //parametric circle
        //x  =  h + r cos(t)
        //y  =  k + r sin(t)
        ArrayList<Point2D.Float> ringPoints = new ArrayList<Point2D.Float>();
        //to estimate 30 points per ellipse
//        float angstep = (float) FastMath.toRadians(360f / 180f);
        //TODO:angstep dependra del radi per tenir una distribucio homogenia
        
        float cradi = (float) ((elli.getRmax()+elli.getRmin())/2);
        float facRmax = (float) (elli.getRmax()/cradi);
        float facRmin = (float) (elli.getRmin()/cradi);

        //ANGULAR STEP in order to have an arc of aprox 1mm
        float arcInPixels = findElliPointsArcSizemm/this.patt2D.getPixSx();
        float angstep = (float) (FastMath.asin(arcInPixels/(2*cradi))/2);
        log.writeNameNumPairs("config", true, "angstep", FastMath.toDegrees(angstep));
        
//        float angstep = (float) FastMath.toRadians(360f / 180f);
        
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
                if (this.patt2D.isInExZone(FastMath.round(p.x), FastMath.round(p.y))){
                    continue;
                }
                float inten = this.patt2D.getInten(FastMath.round(p.x), FastMath.round(p.y));
                if (inten>maxI){
                    maxI = inten;
                    xmaxI = p.x;
                    ymaxI = p.y;
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
    
    private void fitCircle(Point2D.Float[] points){
//      // fit a circle
        try{
          CircleFitter fitter = new CircleFitter();
          fitter.initialize(points);
//          log.info("initial circle: "
//                             + format.format(fitter.getCenter().x)
//                             + " "     + format.format(fitter.getCenter().y)
//                             + " "     + format.format(fitter.getRadius()));
          // minimize the residuals
          int iter = fitter.minimize(100, 0.1f, 1.0e-12f);
//          log.info("converged after " + iter + " iterations");
          log.writeNameNums("config",true,"Circle fit (x y radi): ",fitter.getCenter().x,fitter.getCenter().y,fitter.getRadius());
          this.setCircleCenter(fitter.getCenter());
          this.setCircleRadius(fitter.getRadius());
          
        } catch (Exception e) {
          log.config(e.getMessage());
          return;
        }
        
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
   
}

//agafem wavelength escrita, sino del patt2d --- CANVIAT PER AGAFAR SEMPRE LA DEL PATT2D
//float wave = patt2D.getWavel();
//try{
//    wave = Float.parseFloat(txtWave.getText());
//}catch(Exception e){
//    log.info("using wavelength from patt2d");
//    wave = patt2D.getWavel();
//}