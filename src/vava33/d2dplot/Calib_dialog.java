package vava33.d2dplot;

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

import org.apache.commons.math3.stat.descriptive.moment.Mean;
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
    private Param_dialog paramDialog;
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
    private static int findElliPointsArcSizemm = 1;
    private static int factESDIntensityThreshold = 5;
    private static int radiPunt = 2;
    private static boolean useCircle = false;
    private static float outliersFactSD = 1.5f;
    private static boolean rejectOutliers = true;
    private static boolean showAltCenter = false;
    private static boolean considerGlobalRot = true;
    private static boolean forceGlobalRot = false;
    private static float searchElliLimitFactor = 1.2f;
    private static ArrayList<Integer> ommitRings = new ArrayList<Integer>();

    public ArrayList<Line2D.Float> cerques;
    public ArrayList<EllipsePars> ellicerques;
    
    private static VavaLogger log = D2Dplot_global.log;
        
    /**
     * Create the dialog.
     */
    public Calib_dialog(Pattern2D pattern) {
        this.patt2D = pattern;
        setIconImage(Toolkit.getDefaultToolkit().getImage(Calib_dialog.class.getResource("/img/Icona.png")));
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("LaB6 Calibration (ON DEVELOPMENT)");
        // setBounds(100, 100, 660, 730);
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        int width = 660;
        int height = 730;
        int x = (screen.width - width) / 2;
        int y = (screen.height - height) / 2;
        setBounds(x, y, 522, 580);
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
                    panel_left.setLayout(new MigLayout("", "[grow][grow][]", "[26px][][][][grow]"));
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
                    panel_left.add(panel_1, "cell 0 4 3 1,grow");
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
        
        txtPixtol.setText(Integer.toString(findElliPointsTolPix));
        txtArcsize.setText(Integer.toString(findElliPointsArcSizemm));
        txtEsdfact.setText(Integer.toString(factESDIntensityThreshold));

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
              + " 4) Click on recalc if you change the parameters\n"
              + " 5) To keep final parameters click on apply\n"
              + "\n");
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
            
            //busquem ellipses (ja llegirem els parametres dins, abans ho feiem aqui pero calcEllipses tambe es pot cridar amb recalc)
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
        
        //millorem la recta
        double rsqr = sr.getRSquare();
        int niter = 0;
        float factErr=1.5f;
        int minPoints = 3;
        while (rsqr<0.8 && niter<=3){
            itre = solutions.iterator();
            ArrayList<Point2D.Double> toRemove = new ArrayList<Point2D.Double>();
            double ssqerr = sr.getSumSquaredErrors();
            double slopesqerr = sr.getSlopeStdErr();
            double meansqerr = sr.getMeanSquareError();
            double regrsumsq = sr.getRegressionSumSquares(); 
            log.writeNameNumPairs("config", true, "ssqerr,slopesqerr,meansqerr,rsqr,regrsumsq", ssqerr,slopesqerr,meansqerr,rsqr,regrsumsq);
            while (itre.hasNext()){
                EllipsePars e = itre.next();
                    Double pry = sr.predict(e.getXcen());
                    log.writeNameNumPairs("config", true, "px, py, pry",e.getXcen(),e.getYcen(), pry);
                    if (FastMath.abs(pry-e.getYcen())>(factErr*ssqerr)){
                        toRemove.add(new Point2D.Double(e.getXcen(),e.getYcen()));
                    }
            }
            for(int i=0;i<toRemove.size();i++){
                if (sr.getN()<=minPoints)break;
                sr.removeData(toRemove.get(i).getX(),toRemove.get(i).getY());
                log.config("removing (x y)= "+toRemove.get(i).getX()+" "+toRemove.get(i).getY());
            }
            rsqr = sr.getRSquare();            
            niter=niter+1;
        }
        
        double slope = 0;
        double intercept = 0;
        try{
            slope = sr.getSlope();
            intercept = sr.getIntercept();
            log.writeNameNumPairs("CONFIG",true,"slope intercept niter rsqr npoints", slope, intercept,niter,rsqr,sr.getN());            
        }catch(Exception ex){
            if (D2Dplot_global.isDebug())ex.printStackTrace();
            log.warning("error calculating intrumental parameters from ellipses");
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
        double rotRegression = FastMath.acos((vx*0+vy*-1)/(mod1*mod2));
        log.writeNameNumPairs("CONFIG",true,"ROT(regr)=", FastMath.toDegrees(rotRegression));
        
        boolean useGlobalRot = false;
        if(forceGlobalRot)useGlobalRot=true;
        if(considerGlobalRot){
            if (rsqr>=0.998 && sr.getN()>=5)useGlobalRot = true;    
        }

        ArrayList<Double> distsMD = new ArrayList<Double>();
        ArrayList<Double> tilts = new ArrayList<Double>();
        ArrayList<Double> rots = new ArrayList<Double>();
        ArrayList<Point2D.Float> centresMenys = new ArrayList<Point2D.Float>();
        ArrayList<Point2D.Float> centresMes = new ArrayList<Point2D.Float>();
        //we do it for all the rings that we have
        for (int i=1; i<LaB6_d.length; i++){

            if (ommitRings.contains(i))continue;

            EllipsePars e = this.getEllipseForLaB6ring(i);
            if (e==null){continue;}
            
            float wave = patt2D.getWavel();
            double tth = 2*FastMath.asin(wave/(2*LaB6_d[i]));
            double tantth = FastMath.tan(tth);

            //estimate of tilt,distMD 
            double exen = FastMath.sqrt(FastMath.max(0.,1.-((e.getRmin()*e.getRmin())/(e.getRmax()*e.getRmax()))));
            double tilt = FastMath.asin(exen*FastMath.cos(tth));
            double distMD = (e.getRmin()*e.getRmin())/(tantth*e.getRmax());
            //distance from the ellipse center to the beam center
            double c = e.getRmax()*tantth*FastMath.tan(tilt);

            //aquesta tampoc esta malament.
//            double num = 2*distMD*FastMath.sin(tilt)*sintth*sintth;
//            double den = FastMath.cos(2*tilt)+FastMath.cos(2*tth);
//            c = num/den;
            
            double rot = e.getAngrot();
            if(useGlobalRot){
                rot = rotRegression;
                log.config("using ROT from regression");
            }
            
            //angrot es l'angle "azimut" respecte les 12h (+cw) que ens diu quan està rotat Rmaj
            //agafem punt 0,0 el centre de l'ellipse (relatiu) i apliquem la rotacio de "c"
            //l'angle el faig negatiu perque sigui una rotacio clockwise tal com defineix angrot
            double xmes = 0*FastMath.cos(-rot) - c*FastMath.sin(-rot);
            double ymes = 0*FastMath.sin(-rot) + c*FastMath.cos(-rot);
            double xmenys = 0*FastMath.cos(-rot) - (-c)*FastMath.sin(-rot);
            double ymenys = 0*FastMath.sin(-rot) + (-c)*FastMath.cos(-rot);
            
            //ara ja tinc en relatiu (centre ellipse) les dues possibles posicions del beam, hem de tornar-ho a pixels absoluts:
            //atencio, es -y perque tenim l'origen a dalt a l'esquerra
            double centreXmes = e.getXcen()+xmes;
            double centreYmes = e.getYcen()-ymes;
            double centreXmenys = e.getXcen()+xmenys;
            double centreYmenys = e.getYcen()-ymenys;
            
            //logging debug
            log.config("---- RING nr."+i+" 2theta="+FastMath.toDegrees(tth));
            log.writeNameNumPairs("CONFIG",true,"angRotDeg,tiltDeg,distMD",FastMath.toDegrees(e.getAngrot()), FastMath.toDegrees(tilt), distMD);
            log.writeNameNumPairs("FINE",true,"c,xmes,ymes,xmenys,ymenys", c,xmes,ymes,xmenys,ymenys);
            log.writeNameNumPairs("CONFIG",true,"centreX+,centreY+,centreX-,centreY-", centreXmes,centreYmes,centreXmenys,centreYmenys);
            
            distsMD.add(distMD*patt2D.getPixSx());
            tilts.add(tilt);
            centresMenys.add(new Point2D.Float((float)centreXmenys,(float)centreYmenys));
            centresMes.add(new Point2D.Float((float)centreXmes,(float)centreYmes));
            rots.add(e.getAngrot());
        }
        
        double meanMD=0;
        double meanTilt=0;
        double meanRot=0;
        double meanCXm=0;
        double meanCYm=0;
        double meanCXp=0;
        double meanCYp=0;
        
        //detectem "outliers"
        double[] tiltsAll = new double[tilts.size()];
        for(int i=0;i<tilts.size();i++){
            tiltsAll[i] = tilts.get(i);
        }
        double[] tiltsClean = FileUtils.deleteOutliersFromList(tiltsAll, outliersFactSD);
        
        double[] rotsAll = new double[rots.size()];
        for(int i=0;i<rots.size();i++){
            rotsAll[i] = rots.get(i);
        }
        double[] rotsClean = FileUtils.deleteOutliersFromList(rotsAll, outliersFactSD);        
        
        double[] distsMDAll = new double[distsMD.size()];
        for(int i=0;i<distsMD.size();i++){
            distsMDAll[i] = distsMD.get(i);
        }
        double[] distsMDClean = FileUtils.deleteOutliersFromList(distsMDAll, outliersFactSD);
        
        double[] cXmesAll = new double[centresMes.size()];
        for(int i=0;i<centresMes.size();i++){
            cXmesAll[i] = centresMes.get(i).getX();
        }
        double[] cXmesClean = FileUtils.deleteOutliersFromList(cXmesAll, outliersFactSD);

        double[] cYmesAll = new double[centresMes.size()];
        for(int i=0;i<centresMes.size();i++){
            cYmesAll[i] = centresMes.get(i).getY();
        }
        double[] cYmesClean = FileUtils.deleteOutliersFromList(cYmesAll, outliersFactSD);
        
        double[] cXmenysAll = new double[centresMenys.size()];
        for(int i=0;i<centresMenys.size();i++){
            cXmenysAll[i] = centresMenys.get(i).getX();
        }
        double[] cXmenysClean = FileUtils.deleteOutliersFromList(cXmenysAll, outliersFactSD);

        double[] cYmenysAll = new double[centresMenys.size()];
        for(int i=0;i<centresMenys.size();i++){
            cYmenysAll[i] = centresMenys.get(i).getY();
        }
        double[] cYmenysClean = FileUtils.deleteOutliersFromList(cYmenysAll, outliersFactSD);
        
        boolean rejOutliers=isRejectOutliers();
        if(tiltsClean.length<3)rejOutliers=false;
        if(rotsClean.length<3)rejOutliers=false;
        if(distsMDClean.length<3)rejOutliers=false;
        if(cXmesClean.length<3)rejOutliers=false;
        if(cYmesClean.length<3)rejOutliers=false;
        if(cXmenysClean.length<3)rejOutliers=false;
        if(cYmenysClean.length<3)rejOutliers=false;
        
        if (rejOutliers){
            log.config("rejectOutliers true");
            Mean meanCalc = new Mean();
            meanTilt = meanCalc.evaluate(tiltsClean);
            meanCalc.clear();
            meanRot = meanCalc.evaluate(rotsClean);
            meanCalc.clear();
            meanMD = meanCalc.evaluate(distsMDClean);
            meanCalc.clear();
            meanCXm = meanCalc.evaluate(cXmenysClean);
            meanCalc.clear();
            meanCYm = meanCalc.evaluate(cYmenysClean);
            meanCalc.clear();
            meanCXp = meanCalc.evaluate(cXmesClean);
            meanCalc.clear();
            meanCYp = meanCalc.evaluate(cYmesClean);
            meanCalc.clear();
            
            log.config("Rot regr (º)="+FastMath.toDegrees(rotRegression));
            log.config("Rot mean (º)="+FastMath.toDegrees(meanRot));
            log.config("tilt (º)="+FastMath.toDegrees(meanTilt));
            //CHOOSE THE BEST CENTER
            //as initial guess I will chose the closest to the first ellipse center (ho fem al quadrat per exigerar)
            double bestCX=0;
            double bestCY=0;
            double altCX=0;
            double altCY=0;
            EllipsePars e = this.getEllipseForLaB6ring(1);
            double ex = e.getXcen();
            double ey = e.getXcen();
            double diffPlus = FastMath.abs(ex-meanCXp)*FastMath.abs(ex-meanCXp) + FastMath.abs(ey-meanCYp)*FastMath.abs(ey-meanCYp);
            double diffMinus = FastMath.abs(ex-meanCXm)*FastMath.abs(ex-meanCXm) + FastMath.abs(ey-meanCYm)*FastMath.abs(ey-meanCYm);
            if (diffMinus < diffPlus){
                bestCX=meanCXm;
                bestCY=meanCYm;
                altCX=meanCXp;
                altCY=meanCYp;
            }else{
                bestCX=meanCXp;
                bestCY=meanCYp;
                altCX=meanCXm;
                altCY=meanCYm;
            }
            log.config("Center option 1 (pX pY)="+meanCXm+" "+meanCYm);
            log.config("Center option 2 (pX pY)="+meanCXp+" "+meanCYp);
            log.config("Best? Center (pX pY)="+bestCX+" "+bestCY);
            log.config("DistMD (mm)="+meanMD);

            tAOut.afegirText(true, true,"Calibration results ================");
            tAOut.ln(" Distance Sample-Det (mm) ="+FileUtils.dfX_3.format(meanMD));
            tAOut.ln(" Beam Center (best) (pX pY) ="+FileUtils.dfX_3.format(bestCX)+" "+FileUtils.dfX_3.format(bestCY));
            if(showAltCenter)tAOut.ln(" Beam Center (alt) (pX pY) ="+FileUtils.dfX_3.format(altCX)+" "+FileUtils.dfX_3.format(altCY));
            if(useGlobalRot){
                tAOut.ln(" Detecctor rot. (regr) (deg) = "+FileUtils.dfX_2.format(FastMath.toDegrees(rotRegression)));                
            }else{
                tAOut.ln(" Detecctor rot. (ind) (deg) = "+FileUtils.dfX_2.format(FastMath.toDegrees(meanRot)));                
            }
            tAOut.ln(" Detector tilt (deg) = "+FileUtils.dfX_3.format(FastMath.toDegrees(meanTilt)));    
            tAOut.ln("=================================");
            
            this.setRefCX((float) bestCX);
            this.setRefCY((float) bestCY);
            this.setRefMD((float) meanMD);
            this.setRefTiltDeg((float) FastMath.toDegrees(meanTilt));
            if(useGlobalRot){
                this.setRefRotDeg((float) FastMath.toDegrees(rotRegression));
            }else{
                this.setRefRotDeg((float) FastMath.toDegrees(meanRot));
            }
            
        }else{//use all points
            log.config("rejectOutliers false");
            for (int i=0;i<distsMD.size();i++){
                log.writeNameNumPairs("config", true, "MD CXM CYM CXP CXP Tilt )", distsMD.get(i),centresMenys.get(i).x,centresMenys.get(i).y,centresMes.get(i).x,centresMes.get(i).y,FastMath.toDegrees(tilts.get(i)));
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

            log.config("Rot regr (º)="+FastMath.toDegrees(rotRegression));
            log.config("Rot mean (º)="+FastMath.toDegrees(meanRot));
            log.config("tilt (º)="+FastMath.toDegrees(meanTilt));
            //CHOOSE THE BEST CENTER
            //as initial guess I will chose the closest to the first ellipse center
            double bestCX=0;
            double bestCY=0;
            double altCX=0;
            double altCY=0;
            EllipsePars e = this.getEllipseForLaB6ring(1);
            double ex = e.getXcen();
            double ey = e.getXcen();
            double diffPlus = FastMath.abs(ex-meanCXp)*FastMath.abs(ex-meanCXp) + FastMath.abs(ey-meanCYp)*FastMath.abs(ey-meanCYp);
            double diffMinus = FastMath.abs(ex-meanCXm)*FastMath.abs(ex-meanCXm) + FastMath.abs(ey-meanCYm)*FastMath.abs(ey-meanCYm);
            if (diffMinus < diffPlus){
                bestCX=meanCXm;
                bestCY=meanCYm;
                altCX=meanCXp;
                altCY=meanCYp;
            }else{
                bestCX=meanCXp;
                bestCY=meanCYp;
                altCX=meanCXm;
                altCY=meanCYm;
            }
            log.config("Center option 1 (pX pY)="+meanCXm+" "+meanCYm);
            log.config("Center option 2 (pX pY)="+meanCXp+" "+meanCYp);
            log.config("Best? Center (pX pY)="+bestCX+" "+bestCY);
            log.config("DistMD (mm)="+meanMD);

            tAOut.afegirText(true, true,"Calibration results ================");
            tAOut.ln(" Distance Sample-Det (mm) ="+FileUtils.dfX_3.format(meanMD));
            tAOut.ln(" Beam Center (best) (pX pY) ="+FileUtils.dfX_3.format(bestCX)+" "+FileUtils.dfX_3.format(bestCY));
            if(showAltCenter)tAOut.ln(" Beam Center (alt) (pX pY) ="+FileUtils.dfX_3.format(altCX)+" "+FileUtils.dfX_3.format(altCY));
            if(useGlobalRot){
                tAOut.ln(" Detecctor rot. (regr) (deg) = "+FileUtils.dfX_2.format(FastMath.toDegrees(rotRegression)));                
            }else{
                tAOut.ln(" Detecctor rot. (ind) (deg) = "+FileUtils.dfX_2.format(FastMath.toDegrees(meanRot)));                
            }
            tAOut.ln(" Detector tilt (deg) = "+FileUtils.dfX_3.format(FastMath.toDegrees(meanTilt)));    
            tAOut.ln("=================================");
            
            this.setRefCX((float) bestCX);
            this.setRefCY((float) bestCY);
            this.setRefMD((float) meanMD);
            this.setRefTiltDeg((float) FastMath.toDegrees(meanTilt));
            if(useGlobalRot){
                this.setRefRotDeg((float) FastMath.toDegrees(rotRegression));
            }else{
                this.setRefRotDeg((float) FastMath.toDegrees(meanRot));
            }
            
        }
        
    }
    
    protected void calcEllipses(){
        if ((this.pointsRing1circle!=null)){
            
            //INICIALITZEM I CHECKEJEM ELS PARAMETRES ABANS DE CALCULAR/BUSCAR ELLIPSES
            this.cerques=new ArrayList<Line2D.Float>();
            this.ellicerques=new ArrayList<EllipsePars>();
            
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
            
            //ARA JA PODEM FER LA CERCA
            
            //first we fit a circle and calculate the aprox. Center and distMD, we assume the wavelength known
            Point2D.Float[] d = new Point2D.Float[pointsRing1circle.size()];
//            double[] xs = new double[pointsRing1circle.size()];
//            double[] ys = new double[pointsRing1circle.size()];
            for (int i=0; i<pointsRing1circle.size();i++){
                d[i] = pointsRing1circle.get(i);
//                xs[i] = pointsRing1circle.get(i).getX();
//                ys[i] = pointsRing1circle.get(i).getY();
            }
            pointsRing1circle.toArray(d);
            
            if (useCircle){
                this.fitCircle(d);
                if (this.getCircleRadius()<=0){
                    log.info("Error in circle fit");
                    return;
                }
            }

            solutions = new ArrayList<EllipsePars>();
            
            //we fit an ellipse to the first ring using the previous aprox. cirlce fit or using directly the click points
            EllipsePars p = new EllipsePars();
            p.setLab6ring(1);
            if(useCircle){
                p.setEstimPoints(findRingPoints(this.getCircleRadius(),findElliPointsTolPix));  
            }else{
                p.setEstimPoints(pointsRing1circle);                
            }
            p.fitElli();
            
            //NEW: now we try a better fit.
            p.setEstimPoints(findRingPoints(p,findElliPointsTolPix));
            p.fitElli();
            
            if (p.isFit()){
                p.logElliPars("debug");
                solutions.add(p);                
            }else{
                log.warning("Impossible to fit ellipse number 1. Please check.");
                return;
            }

            //distMD en pixels...
            float estMD = -1;
            if(useCircle){
                estMD = estimMDdist(this.getCircleRadius(), this.getCircleRadius(), 1);//estimacio distMD primer pic                
            }else{
                estMD = estimMDdist(p, 1);//estimacio distMD primer pic
            }
            float wave = patt2D.getWavel();
            
            //now we use the previous ellipse to fit the next one
            int fitCount = 1; //number of rings fitted
            for (int i=2;i<LaB6_d.length;i++){
                
                log.config("ring no."+i);
                
                if (ommitRings.contains(i))continue;
                
                EllipsePars p0 = solutions.get(fitCount-1);
                
                float twoth = (float) (2*FastMath.asin(wave/(2*LaB6_d[i])));
                float radiPix = (float) (FastMath.tan(twoth)*estMD);
                
                float twothP0 = (float) (2*FastMath.asin(wave/(2*LaB6_d[p0.getLab6ring()])));
                float radiPixP0 = (float) (FastMath.tan(twothP0)*estMD);
                
                float factRP0maj = (float) (p0.getRmax()/radiPixP0);
                float factRP0men = (float) (p0.getRmin()/radiPixP0);

                log.writeNameNumPairs("CONFIG",true,"radiPixP0 factRP0maj factRP0men", radiPixP0,factRP0maj,factRP0men);

                float r1 = (float) (p0.getRmax()); 
                float r2 = (float) (p0.getRmin());                
//                float factRmax = r1/((r1+r2)/2);
//                float factRmin = r2/((r1+r2)/2);
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
//                pN.setEstimPoints(this.findRingPoints(radiPix,findElliPointsTolPix));
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
            if (maxI > threshold){
                ringPoints.add(new Point2D.Float(xmaxI,ymaxI));    
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
                if (!this.patt2D.isInside(FastMath.round(p.x), FastMath.round(p.y))){
                    continue;
                }
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
          log.writeNameNums("config",true,"Circle fit (x y radi iter): ",fitter.getCenter().x,fitter.getCenter().y,fitter.getRadius(),iter);
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
   
    protected void do_btnRecalc_actionPerformed(ActionEvent arg0) {
        //busquem ellipses
        this.calcEllipses();
        
        //calculem parametres instrumentals
        if (solutions.size()>0){
            this.calcInstrumFromEllipses();
        }
    }

    public static float getOutliersFactSD() {
        return outliersFactSD;
    }

    public static void setOutliersFactSD(float outliersFactSD) {
        Calib_dialog.outliersFactSD = outliersFactSD;
    }

    public static boolean isShowAltCenter() {
        return showAltCenter;
    }

    public static void setShowAltCenter(boolean showAltCenter) {
        Calib_dialog.showAltCenter = showAltCenter;
    }

    public static boolean isConsiderGlobalRot() {
        return considerGlobalRot;
    }

    public static void setConsiderGlobalRot(boolean considerGlobalRot) {
        Calib_dialog.considerGlobalRot = considerGlobalRot;
    }

    public static boolean isForceGlobalRot() {
        return forceGlobalRot;
    }

    public static void setForceGlobalRot(boolean forceGlobalRot) {
        Calib_dialog.forceGlobalRot = forceGlobalRot;
    }

    public static boolean isRejectOutliers() {
        return rejectOutliers;
    }

    public static void setRejectOutliers(boolean rejectOutliers) {
        Calib_dialog.rejectOutliers = rejectOutliers;
    }

    public static float getSearchElliLimitFactor() {
        return searchElliLimitFactor;
    }

    public static void setSearchElliLimitFactor(float searchElliLimitFactor) {
        Calib_dialog.searchElliLimitFactor = searchElliLimitFactor;
    }
}
