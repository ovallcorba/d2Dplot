package com.vava33.d2dplot;

import java.awt.Toolkit;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import com.vava33.d2dplot.auxi.ImgFileUtils;
import com.vava33.d2dplot.auxi.ImgOps;
import com.vava33.d2dplot.auxi.ImgOps.PkSCIntegrateFileWorker;
import com.vava33.d2dplot.auxi.OrientSolucio;
import com.vava33.d2dplot.auxi.Patt2Dzone;
import com.vava33.d2dplot.auxi.Pattern2D;
import com.vava33.d2dplot.auxi.Peak;
import com.vava33.d2dplot.auxi.PuntSolucio;
import com.vava33.d2dplot.auxi.findPksTableRenderer;
import com.vava33.d2dplot.auxi.ImgOps.PkIntegrateFileWorker;
import com.vava33.jutils.FileUtils;
import com.vava33.jutils.VavaLogger;

import net.miginfocom.swing.MigLayout;

import javax.swing.JCheckBox;
import javax.swing.JLabel;

import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;

import javax.swing.JSplitPane;
import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.ProgressMonitor;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.Scanner;

import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

import org.apache.commons.math3.util.FastMath;

import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

import java.awt.Color;

import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.SpinnerListModel;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class PKsearch_frame extends JFrame {
    /**
     * 
     */
    private static final long serialVersionUID = -3599627105512682021L;

    private static VavaLogger log = D2Dplot_global.getVavaLogger(PKsearch_frame.class.getName());

    private JPanel contentPane;

    private Pattern2D patt2d;
    private boolean fromInco;
    private Dinco_frame dincoFrame;
    private ImagePanel iPanel;

    private JCheckBox chckbxOnTop;
    private JPanel panel;
    private JLabel lblDelsig;
    private JLabel lblZoneradius;
    private JButton btnCalculate;
    private JCheckBox chckbxShowPoints;
    private JTextField txtDelsig;
    private JTextField txtZoneradius;
    private JButton btnClose;
    private JSpinner spinner;
    private JLabel lblPlotSize;
    private JCheckBox chckbxLpCorrection;
    private JCheckBox chckbxAddremovePeaks;
    private JPanel panel_2;
    private JLabel lblthWidth;
    private JTextField txtTtol;
    private JLabel lblAzimAperture;
    private JTextField txtAngdeg;
    private JLabel lblBkgPoints;
    private JTextField txtBkgPt;
    
//    private ArrayList<Patt2Dzone> pkinteg;
    private JTable table;
    private JScrollPane scrollPane_1;
    private JSpinner spinerAxis;
    private JButton btnExportPeakList;
    
    //defaults
    public static int def_bkgpt = 20;
    public static int def_tol2tpix = 30;
    public static float def_angDeg = 4.0f;
    public static float def_delsig = 6.0f;
    public static int def_zoneR=FastMath.round(def_tol2tpix/2);
    public static int def_minpix=6;
    public static int nzonesFindPeaks = 24;
    public static float def_bkgPxAutoPercent = 0.005f;
    public static int def_minbkgPx = 10;
    
    //generals
    public static int bkgpt;
    public static float tol2t;
    public static int tol2tpix;
    public static float angDeg;
    public static float delsig;
    public static int zoneR;
    public static int minpix;
    public static int iosc=0;
    
    private JPanel panel_3;
    private JPanel panel_4;
    private JCheckBox chckbxAutodelsig;
    private JLabel lblMinNrPixels;
    private JTextField txtMinpixels;
    private JCheckBox chckbxAutointrad;
    private JCheckBox chckbxAutoazim;
    private JCheckBox chckbxAutobkgpt;
    private JPanel panel_1;
    private JButton btnExportTable;
    private JButton btnMaskbin;
    
    Pattern2D maskInt;
    private JButton btnBatch;
    private JLabel lblNpks;

    
    ProgressMonitor pm;
    PkIntegrateFileWorker convwk;
    PkSCIntegrateFileWorker pkscwk;
    private JButton btnImport;
    private JButton btnRemoveDiamonds;
    private JButton btnRemoveSaturated;
    
    private File fileout;
    private JButton btnBatchout;
    
    /**
     * Create the frame.
     */
    public PKsearch_frame(ImagePanel ip, boolean fromInco, Dinco_frame dframe) {
        setAlwaysOnTop(true);
        this.fromInco=fromInco;
        this.dincoFrame = dframe;
        this.iPanel=ip;
        this.patt2d=ip.getPatt2D();
        setTitle("Peak Search and Integrate");
        setIconImage(Toolkit.getDefaultToolkit().getImage(Dinco_frame.class.getResource("/img/Icona.png")));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setBounds(100, 100, 860, 600);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(new MigLayout("fill, insets 3", "[grow]", "[grow][]"));
        
        JSplitPane splitPane = new JSplitPane();
        splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
        contentPane.add(splitPane, "cell 0 0,grow");
        
        panel = new JPanel();
        splitPane.setLeftComponent(panel);
        panel.setLayout(new MigLayout("insets 5", "[grow][grow]", "[][][]"));
        
        panel_4 = new JPanel();
        panel.add(panel_4, "cell 0 0 2 1,grow");
        panel_4.setLayout(new MigLayout("", "[][][][grow]", "[]"));
        
        chckbxShowPoints = new JCheckBox("Show Points");
        chckbxShowPoints.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                do_chckbxShowPoints_itemStateChanged(e);
            }
        });
        panel_4.add(chckbxShowPoints, "cell 0 0");
        chckbxShowPoints.setSelected(true);
        
        lblPlotSize = new JLabel("Plot size");
        panel_4.add(lblPlotSize, "cell 1 0");
        
        spinner = new JSpinner();
        spinner.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                do_spinner_stateChanged(e);
            }
        });
        panel_4.add(spinner, "cell 2 0");
        spinner.setModel(new SpinnerNumberModel(5, 1, 10, 1));
        
        chckbxOnTop = new JCheckBox("on top");
        panel_4.add(chckbxOnTop, "cell 3 0,alignx right");
        chckbxOnTop.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent arg0) {
                do_chckbxOnTop_itemStateChanged(arg0);
            }
        });
        chckbxOnTop.setSelected(true);
        
        panel_3 = new JPanel();
        panel_3.setBorder(new TitledBorder(null, "Peak detection", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(51, 51, 51)));
        panel.add(panel_3, "cell 0 1,grow");
        panel_3.setLayout(new MigLayout("", "[][grow][]", "[][][][][]"));
        
        lblDelsig = new JLabel("ESD factor=");
        panel_3.add(lblDelsig, "cell 0 0,alignx right");
        
        txtDelsig = new JTextField();
        panel_3.add(txtDelsig, "cell 1 0,growx");
        txtDelsig.setText("1.5");
        txtDelsig.setColumns(10);
        
        chckbxAutodelsig = new JCheckBox("f(2"+D2Dplot_global.theta+")");
        chckbxAutodelsig.setSelected(true);
        panel_3.add(chckbxAutodelsig, "cell 2 0");
        
        lblZoneradius = new JLabel("Peak merge zone (px)=");
        panel_3.add(lblZoneradius, "cell 0 1,alignx right");
        
        txtZoneradius = new JTextField();
        panel_3.add(txtZoneradius, "cell 1 1,growx");
        txtZoneradius.setText(Integer.toString(def_zoneR));
        txtZoneradius.setColumns(10);
        
        lblMinNrPixels = new JLabel("Min. pixels for a peak=");
        panel_3.add(lblMinNrPixels, "cell 0 2,alignx right");
        
        txtMinpixels = new JTextField();
        txtMinpixels.setText(Integer.toString(def_minpix));
        panel_3.add(txtMinpixels, "cell 1 2,growx");
        txtMinpixels.setColumns(10);
        
        chckbxAddremovePeaks = new JCheckBox("Add/Remove peaks");
        panel_3.add(chckbxAddremovePeaks, "cell 0 3 3 1,alignx center");
        
        btnRemoveDiamonds = new JButton("Remove Diamonds");
        panel_3.add(btnRemoveDiamonds, "cell 0 4");
        
        btnRemoveSaturated = new JButton("Remove Saturated");
        panel_3.add(btnRemoveSaturated, "cell 1 4");
        btnRemoveSaturated.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                do_btnRemoveSaturated_actionPerformed(arg0);
            }
        });
        btnRemoveDiamonds.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                do_btnRemoveDiamonds_actionPerformed(e);
            }
        });
        
        btnCalculate = new JButton("Calculate");
        btnCalculate.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                do_btnCalculate_actionPerformed(arg0);
            }
        });
        
        panel_2 = new JPanel();
        panel_2.setBorder(new TitledBorder(null, "Integration parameters", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(51, 51, 51)));
        panel.add(panel_2, "cell 1 1,grow");
        panel_2.setLayout(new MigLayout("", "[][grow][]", "[][][][]"));
        
        lblthWidth = new JLabel("Radial width (px)");
        panel_2.add(lblthWidth, "cell 0 0,alignx trailing");
        
        txtTtol = new JTextField();
        txtTtol.setText(Integer.toString(def_tol2tpix));
        panel_2.add(txtTtol, "cell 1 0,growx");
        txtTtol.setColumns(10);
        
        chckbxAutointrad = new JCheckBox("auto");
        chckbxAutointrad.setSelected(true);
        panel_2.add(chckbxAutointrad, "cell 2 0");
        
        lblAzimAperture = new JLabel("Azim aperture (º)");
        panel_2.add(lblAzimAperture, "cell 0 1,alignx trailing");
        
        txtAngdeg = new JTextField();
        txtAngdeg.setText(Float.toString(def_angDeg));
        panel_2.add(txtAngdeg, "cell 1 1,growx");
        txtAngdeg.setColumns(10);
        
        chckbxAutoazim = new JCheckBox("auto");
        chckbxAutoazim.setSelected(true);
        panel_2.add(chckbxAutoazim, "cell 2 1");
        
        lblBkgPoints = new JLabel("Background px");
        panel_2.add(lblBkgPoints, "cell 0 2,alignx trailing");
        
        txtBkgPt = new JTextField();
        txtBkgPt.setText(Integer.toString(def_bkgpt));
        panel_2.add(txtBkgPt, "cell 1 2,growx");
        txtBkgPt.setColumns(10);
        
        chckbxAutobkgpt = new JCheckBox("auto");
        chckbxAutobkgpt.setSelected(true);
        panel_2.add(chckbxAutobkgpt, "cell 2 2");
        
        chckbxLpCorrection = new JCheckBox("LP correction");
        chckbxLpCorrection.setSelected(true);
        panel_2.add(chckbxLpCorrection, "cell 0 3,alignx center");
        
        spinerAxis = new JSpinner();
        spinerAxis.setModel(new SpinnerListModel(new String[] {"V oscil. axis", "H oscil. axis"}));
        panel_2.add(spinerAxis, "cell 1 3,growx");
        panel.add(btnCalculate, "flowx,cell 0 2 2 1,alignx center");
        
        scrollPane_1 = new JScrollPane();
        splitPane.setRightComponent(scrollPane_1);
        table = new JTable(){
            private static final long serialVersionUID = 1403003908758846889L;
            public boolean getScrollableTracksViewportWidth()
            {
                return getPreferredSize().width < getParent().getWidth();
            }};
        table.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                do_table_keyReleased(e);
            }
        });
        table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent lse) {
                do_table_valueChanged(lse);
            }
        });
        
        
        table.setModel(new DefaultTableModel(
            new Object[][] {
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
            },
            new String[] {
                  "XPix", "YPix", "Radius", "Ymax", "Fh2", "s(Fh2)", "Ymean", "Npix", "Ybkg", "s(Ybkg)", "Nbkg", "RadWth", "AzimDeg", "dsp", "p", "Swarm", "Satur", "nearMsk"
                 //  0      1        2         3      4       5         6        7       8       9         10        11       12        13    14    15       16        17
//                Float   Float     Float     Int   Float   Float     Float     Int    Float   Float      Int       Int      Float    Float  Float  Int      Int      Bool
            }
        ) {
            /**
             * 
             */
            private static final long serialVersionUID = -4167032016502576681L;
            @SuppressWarnings("rawtypes")
            Class[] columnTypes = new Class[] {
                Float.class, Float.class, Float.class, Integer.class, Float.class, Float.class, Float.class, 
                Integer.class, Float.class, Float.class, Integer.class,Integer.class, Float.class, Float.class,
                Float.class, Integer.class, Integer.class, String.class
            };
            @SuppressWarnings({ "unchecked", "rawtypes" })
            public Class getColumnClass(int columnIndex) {
                return columnTypes[columnIndex];
            }
            public boolean isCellEditable(int row, int column){
              return false;//This causes all cells to be not editable
            }
            
        });
        table.setFillsViewportHeight(true);
        table.setDefaultRenderer(Float.class, new findPksTableRenderer());
        table.setDefaultRenderer(Integer.class, new findPksTableRenderer());
        table.setDefaultRenderer(String.class, new findPksTableRenderer());
        table.setAutoCreateRowSorter(true);
        table.getTableHeader().setReorderingAllowed(false);
        
        scrollPane_1.setViewportView(table);
        
        panel_1 = new JPanel();
        contentPane.add(panel_1, "flowx,cell 0 1,grow");
        panel_1.setLayout(new MigLayout("", "[][][][grow][][grow]", "[][]"));
        
        lblNpks = new JLabel("");
        panel_1.add(lblNpks, "cell 0 0");
        
        btnExportPeakList = new JButton("Write PCS file for INCO");
        panel_1.add(btnExportPeakList, "cell 1 0 2 1,growx");
        
        btnBatch = new JButton("Batch Processing (PCS)");
        btnBatch.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                do_btnBatch_actionPerformed(arg0);
            }
        });
        
        btnImport = new JButton("Import");
        btnImport.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                do_btnImport_actionPerformed(arg0);
            }
        });
        panel_1.add(btnImport, "cell 3 0");
        panel_1.add(btnBatch, "cell 4 0");
        
        btnExportTable = new JButton("Export Full Table");
        btnExportTable.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                do_btnExportTable_actionPerformed(arg0);
            }
        });
        panel_1.add(btnExportTable, "cell 1 1");
        
        btnMaskbin = new JButton("MASK.BIN");
        btnMaskbin.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                do_btnMaskbin_actionPerformed(e);
            }
        });
        panel_1.add(btnMaskbin, "cell 2 1");
        
        btnBatchout = new JButton("Batch Processing (OUT)");
        btnBatchout.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                do_btnBatchout_actionPerformed(arg0);
            }
        });
        panel_1.add(btnBatchout, "cell 4 1");
        
        btnClose = new JButton("close");
        panel_1.add(btnClose, "cell 5 1,alignx right");
        btnClose.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                do_btnClose_actionPerformed(e);
            }
        });
        btnExportPeakList.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                do_btnExportPeakList_actionPerformed(arg0);
            }
        });
        
        
        table.getColumnModel().getColumn(0).setPreferredWidth(70);
        table.getColumnModel().getColumn(1).setPreferredWidth(70);
        table.getColumnModel().getColumn(2).setPreferredWidth(70);
        table.getColumnModel().getColumn(3).setPreferredWidth(70);
        table.getColumnModel().getColumn(4).setPreferredWidth(65);
        table.getColumnModel().getColumn(5).setPreferredWidth(60);
        table.getColumnModel().getColumn(6).setPreferredWidth(60);
        table.getColumnModel().getColumn(7).setPreferredWidth(55);
        table.getColumnModel().getColumn(8).setPreferredWidth(60);
        table.getColumnModel().getColumn(9).setPreferredWidth(55);
        table.getColumnModel().getColumn(10).setPreferredWidth(50);
        table.getColumnModel().getColumn(11).setPreferredWidth(50);
        table.getColumnModel().getColumn(12).setPreferredWidth(60);
        table.getColumnModel().getColumn(13).setPreferredWidth(60);
        table.getColumnModel().getColumn(14).setPreferredWidth(55);
        table.getColumnModel().getColumn(15).setPreferredWidth(50);
        table.getColumnModel().getColumn(16).setPreferredWidth(50);
        table.getColumnModel().getColumn(17).setPreferredWidth(50);
        
        this.inicia(fromInco);
    }
    
    @Override
    public void dispose() {
        this.chckbxShowPoints.setSelected(false);
        super.dispose();
    }
    
    public void inicia(boolean fromINCO){
        this.fromInco=fromINCO;
        this.patt2d=this.iPanel.getPatt2D();
        
        //resetejem si hi ha peaksearch previ (de l'inco o wherever)
        if(this.patt2d.getPkSearchResult()!=null)this.patt2d.getPkSearchResult().clear();
        
        if(fromInco){
            lblDelsig.setEnabled(false);
            lblZoneradius.setEnabled(false);
            txtDelsig.setEnabled(false);
            txtZoneradius.setEnabled(false);
            btnCalculate.doClick();
            lblMinNrPixels.setEnabled(false);
            txtMinpixels.setEnabled(false);
        }
        
//        this.updateTable(); //aixo peta perque llista pics es null
        ((DefaultTableModel)table.getModel()).setRowCount(0);
        //podria fer això pero millor ho deixo tot tal com està
//        txtDelsig.setText(Float.toString(def_delsig));
//        txtZoneradius.setText(Integer.toString(def_zoneR));
//        txtMinpixels.setText(Integer.toString(def_minpix));
//        txtTtol.setText(Integer.toString(def_tol2tpix));
//        txtAngdeg.setText(Float.toString(def_angDeg));
//        txtBkgPt.setText(Integer.toString(def_bkgpt));
        this.iPanel.actualitzarVista();
    }
    
    protected void do_chckbxOnTop_itemStateChanged(ItemEvent arg0) {
        this.setAlwaysOnTop(chckbxOnTop.isSelected());
    }
    
    public boolean isShowPoints(){
        return chckbxShowPoints.isSelected();
    }
    
    public boolean isEditPoints(){
        return chckbxAddremovePeaks.isSelected();
    }
    
    private void searchPeaks(){
        boolean t2dep = chckbxAutodelsig.isSelected();
        this.readSearchOptions();
//        ArrayList<Peak> pks = ImgOps.findPeaks(patt2d, delsig, zoneR, t2dep, nzonesFindPeaks, minpix, false);
        ArrayList<Peak> pks = ImgOps.findPeaksBetter(patt2d, delsig, zoneR, t2dep, nzonesFindPeaks, minpix, false,-1);
        
//        if(chckbxAvoidDiamonds.isSelected()){
//            
//        }
        //if noSaturated //TODO
        
        patt2d.setPkSearchResult(pks);
        this.iPanel.actualitzarVista();
    }
    
    protected void do_btnRemoveSaturated_actionPerformed(ActionEvent arg0) {
        int removed = ImgOps.removeSaturPeaks(patt2d.getPkSearchResult());
        log.info(String.format("Removed %d saturated peaks",removed));
        lblNpks.setText(String.format("%d Peaks!", patt2d.getPkSearchResult().size()));
        this.iPanel.actualitzarVista();
    }
    
    protected void do_btnRemoveDiamonds_actionPerformed(ActionEvent e) {
        int removed = ImgOps.removeDiamondPeaks(patt2d.getPkSearchResult());
        log.info(String.format("Removed %d diamond (guessed) peaks",removed));
        lblNpks.setText(String.format("%d Peaks!", patt2d.getPkSearchResult().size()));
        this.iPanel.actualitzarVista(); 
    }
    
    
    public void readSearchOptions(){
        try{
            delsig = Float.parseFloat(txtDelsig.getText());
        }catch(Exception e){
            log.warning(String.format("Cannot read delsig, using default value %f",def_delsig));
        }
        try{
            zoneR = Integer.parseInt(txtZoneradius.getText());
        }catch(Exception e){
            log.warning(String.format("Cannot read zone radius, using default value %d",def_zoneR));
        }
        try{
            minpix = Integer.parseInt(txtMinpixels.getText());
        }catch(Exception e){
            log.warning(String.format("Cannot read min pixels, using default value %d",def_minpix));
        }          
    }
    
    private void integratePeaks(){

        boolean debug = false;
        boolean autoTol = chckbxAutointrad.isSelected();
        boolean autoazim = chckbxAutoazim.isSelected();
        boolean autobkgpt = chckbxAutobkgpt.isSelected();
        
        this.readIntegrateOptions();
        
        if (patt2d.getPkSearchResult()==null){
            log.info("no peaks found");
            return;
        }
        Iterator<Peak> itrpks = patt2d.getPkSearchResult().iterator();
        while (itrpks.hasNext()){
            Peak pk = (Peak)itrpks.next();
            Point2D.Float pxc = pk.getPixelCentre();
            
            if (autobkgpt){
                int npix = ImgOps.ArcNPix(patt2d, (int)(pxc.x), (int)(pxc.y), tol2tpix, angDeg);
                //agafem un 5% dels pixels com a fons
                bkgpt = FastMath.round(npix*def_bkgPxAutoPercent);
                if (bkgpt<def_minbkgPx)bkgpt=def_minbkgPx;
                log.fine("bkgPT="+bkgpt);
            }
            if (autoTol){
                tol2tpix = ImgOps.intRadPixelsOfAPeak(patt2d,pxc.x,pxc.y);
                //TODO: posem limits?
            }
            if (autoazim){
                angDeg = ImgOps.azimAngleOfAPeak(patt2d,pxc.x,pxc.y);
                log.writeNameNums("FINE", true, "x,y,angDeg", pxc.x,pxc.y,angDeg);
                //TODO
            }
            
            Patt2Dzone pz = ImgOps.YarcTilt(patt2d, (int)(pxc.x), (int)(pxc.y), tol2tpix, angDeg, true, bkgpt, debug);
            pk.setZona(pz);
            pk.calculate(chckbxLpCorrection.isSelected());
        }
        this.updateTable(); //ja actualitza IP
    }
    
    protected void do_btnCalculate_actionPerformed(ActionEvent arg0) {
        if(fromInco)log.debug("CALCULATE CALLED FROM INCO");
        if(!fromInco){ //busquem els pics amb els parametres entrats
            this.searchPeaks();
        }else{//importem els pics de la solucio seleccionada de l'inco
            OrientSolucio[] oos = dincoFrame.getActiveOrientSols();
            if (oos == null) return;
            if(OrientSolucio.isPCS){
                //llegir array peaks nomes hi haura una solucio, la zero
                patt2d.setPkSearchResult(oos[0].getPeaksPCS());
            }else{ //ve de SOL o PXY, pot haver-hi més d'una
                ArrayList<Peak> dincoSolPunts = new ArrayList<Peak>();
                for (int i=0;i<oos.length;i++){
                    //llegir array puntSolucio
                    Iterator<PuntSolucio> itrS = oos[i].getSol().iterator();
                    while (itrS.hasNext()) {
                        PuntSolucio s = itrS.next();
                        Peak pic = ImgOps.addPeakFromCoordinates(patt2d, new Point2D.Float(s.getCoordX(),s.getCoordY()), PKsearch_frame.zoneR);
                        dincoSolPunts.add(pic);
                    }
                }
                patt2d.setPkSearchResult(dincoSolPunts);
            }
        }
        //now we integrate the peaks using Yarctilt
        this.integratePeaks();
        this.updateTable();
    }
    
    public void integratePk(Peak pic){
        this.readIntegrateOptions();
        //afegim comprovacio "autos"
        boolean autoTol = chckbxAutointrad.isSelected();
        boolean autoazim = chckbxAutoazim.isSelected();
        boolean autobkgpt = chckbxAutobkgpt.isSelected();
        if (autobkgpt){
            int npix = ImgOps.ArcNPix(patt2d, (int)(pic.getPixelCentre().x), (int)(pic.getPixelCentre().y), tol2tpix, angDeg);
            //agafem un 5% dels pixels com a fons
            bkgpt = FastMath.round(npix*def_bkgPxAutoPercent);
            if (bkgpt<def_minbkgPx)bkgpt=def_minbkgPx;
            log.debug("bkgPT="+bkgpt);
        }
        if (autoTol){
            tol2tpix = ImgOps.intRadPixelsOfAPeak(patt2d,pic.getPixelCentre().x,pic.getPixelCentre().y);
            //TODO: posem limits?
        }
        if (autoazim){
            angDeg = ImgOps.azimAngleOfAPeak(patt2d,pic.getPixelCentre().x,pic.getPixelCentre().y);
            log.writeNameNums("CONFIG", true, "x,y,angDeg", pic.getPixelCentre().x,pic.getPixelCentre().y,angDeg);
            //TODO
        }
        pic.setZona(ImgOps.YarcTilt(patt2d, (int)(pic.getPixelCentre().x), (int)(pic.getPixelCentre().y), tol2tpix, angDeg, true, bkgpt, false));
        pic.calculate(chckbxLpCorrection.isSelected());
        if (this.patt2d.getPkSearchResult()==null)this.patt2d.setPkSearchResult(new ArrayList<Peak>()); //in case it is not initialized
        this.patt2d.getPkSearchResult().add(pic);
        this.updateTable();
    }
    
    //hauria de ser privat
    public void readIntegrateOptions(){
        try{
            tol2tpix = Integer.parseInt(txtTtol.getText());
        }catch(Exception e){
            log.warning(String.format("Cannot read tol2t, using default value %d",tol2tpix));
            txtTtol.setText(String.valueOf(def_tol2tpix));
        }
        try{
            angDeg = Float.parseFloat(txtAngdeg.getText());
        }catch(Exception e){
            log.warning(String.format("Cannot read angDeg, using default value %f",angDeg));
            txtAngdeg.setText(String.valueOf(def_angDeg));
        }
        try{
            bkgpt = Integer.parseInt(txtBkgPt.getText());
        }catch(Exception e){
            log.warning(String.format("Cannot read bkgpt, using default value %d",bkgpt));
            txtBkgPt.setText(String.valueOf(def_bkgpt));
        }
        iosc = 0; //iosc es l'eix de tilt, 1=horitzontal, 2=vertical, 0=noOscil
        String val = spinerAxis.getValue().toString();
        log.fine(val.toString()); //V oscil. axis
        log.fine(val.getClass().getName()); //java.lang.String
        if (val.contains("H")){
            iosc = 1;
        }
        if (val.contains("V")){
            iosc = 2;
        }
        patt2d.setIscan(iosc);
    }

    
    public void updateTable(){
        ((DefaultTableModel)table.getModel()).setRowCount(0);
        
        //mostrarem els pics
        if (patt2d.getPkSearchResult()==null){
            lblNpks.setText("No Peaks found");
            return;
        }
        for(int i=0; i<patt2d.getPkSearchResult().size();i++){
            Peak pk = patt2d.getPkSearchResult().get(i);
            Object[] row = new Object[table.getColumnCount()];
//          "XPix", "YPix", "Radius", "Ymax", "Fh2", "s(Fh2)", "Ymean", "Npix", "Ybkg", "s(Ybkg)", "Nbkg", "RadWth", "AzimDeg", "dsp", "p", "Swarm", "Satur", "nearMsk"
//             0      1        2         3      4       5         6        7       8       9         10        11       12        13    14    15       16        17
//          Float   Float     Float     Int   Float   Float     Float     Int    Float   Float      Int       Int      Float    Float  Float  Int      Int      Bool
            row[0] = pk.getPixelCentre().x;
            row[1] = pk.getPixelCentre().y;
            row[2] = pk.getRadi();
            row[3] = pk.getYmax();
            row[4] = pk.getFh2();
            row[5] = pk.getSfh2();
            row[6] = pk.getYmean();
            row[7] = pk.getNpix();
            row[8] = pk.getYbkg();
            row[9] = pk.getYbkgSD();
            row[10] = pk.getNbkgpix();
            row[11] = pk.getIntRadPx();
            row[12] = pk.getAzimAper();
            row[13] = pk.getDsp();
            row[14] = pk.getP();
            row[15] = pk.getnVeinsEixam();
            row[16] = pk.getnSatur();
            String nmsk = "No";
            if (pk.isNearMask())nmsk="Yes";
            row[17] = nmsk;

            ((DefaultTableModel)table.getModel()).addRow(row);
        }
        //ordenem per Ymax
        table.getRowSorter().toggleSortOrder(3);
        table.getRowSorter().toggleSortOrder(3);
        lblNpks.setText(String.format("%d Peaks!", patt2d.getPkSearchResult().size()));
        
        this.iPanel.actualitzarVista();
    }
    
    public Peak[] getSelectedPeaks(){
        int[] val = table.getSelectedRows();
        if (val.length<=0)return null;
        Peak[] selpeaks = new Peak[val.length];
        
        for (int i=0;i<val.length;i++){
            int modelRow = table.convertRowIndexToModel(val[i]);
            Peak pk = null;
            try{
                float px = (Float)table.getModel().getValueAt(modelRow, 0);
                float py = (Float)table.getModel().getValueAt(modelRow, 1);
                pk = patt2d.getPeakFromCoordinates(new Point2D.Float(px,py));
            }catch(Exception e){
                log.debug("error getting selected point coordinates");
            }
            selpeaks[i]=pk;
        }
        return selpeaks;
    }
    
    public float getCurrentTol2T(){
        float angDeg = -1;
        try{
            angDeg = Float.parseFloat(txtTtol.getText());
        }catch(Exception e){
            log.debug(String.format("error reading angdeg"));
        }
        return angDeg;
    }
    
    public float getCurrentAngDeg(){
        float tol2t = -1;
        try{
            tol2t = Float.parseFloat(txtAngdeg.getText());
        }catch(Exception e){
            log.debug(String.format("error reading tol2t"));
        }
        return tol2t;
    }
    
    public int getPlotSize(){
        return (Integer) spinner.getValue();
    }

    protected void do_btnClose_actionPerformed(ActionEvent e) {
        this.iPanel.actualitzarVista();
        this.dispose();
    }
    
    protected void do_table_keyReleased(KeyEvent e) {
        if (e.getKeyCode()==KeyEvent.VK_DELETE){
            Peak[] selpeaks = this.getSelectedPeaks();
            if (selpeaks!=null){
                for (int i=0;i<selpeaks.length;i++){
                    log.writeNameNums("CONFIG", true, "remove peak",selpeaks[i].getPixelCentre().x,selpeaks[i].getPixelCentre().y);
                    this.patt2d.removePeak(selpeaks[i]);
                }
            }
            this.updateTable();
        }
    }
    
    protected void removePeak(Peak pk){
        patt2d.removePeak(pk);
        this.updateTable();
    }
    
    protected void do_btnExportPeakList_actionPerformed(ActionEvent arg0) {
        File f = FileUtils.fchooserSaveAsk(this,new File(D2Dplot_global.getWorkdir()), null);
        if (f==null) return;
        D2Dplot_global.setWorkdir(f);
        f = FileUtils.canviExtensio(f, "PCS");
        ImgFileUtils.writePCS(patt2d,f,delsig,chckbxAutodelsig.isSelected(),angDeg,chckbxAutointrad.isSelected(),zoneR,minpix,bkgpt,chckbxAutobkgpt.isSelected(),chckbxAutoazim.isSelected());
        
    }
    

    protected void do_btnExportTable_actionPerformed(ActionEvent arg0) {
        File f = FileUtils.fchooserSaveAsk(this,new File(D2Dplot_global.getWorkdir()), null);
        if (f==null) return;
        D2Dplot_global.setWorkdir(f);
        this.exportTable(f);
    }
    
    private void importTable(File txtFile){
        try {
            Scanner scPCSfile = new Scanner(txtFile);
            boolean firstLine = false;
            String line;
            while (!firstLine){
                line = scPCSfile.nextLine();
                if (line.trim().startsWith("-----"))firstLine=true;
            }
            line = scPCSfile.nextLine(); //capçalera
            ArrayList<Peak> realPeaks = new ArrayList<Peak>();
            while (scPCSfile.hasNextLine()){
                line = scPCSfile.nextLine();
                String[] lineS = line.trim().split("\\s+");
// Npeak     Xpix     Ypix  Radi(px)      Ymax      Fh2   s(Fh2)      Ymean     Npix     Ybkg  s(Ybkg) nBkgPx RadWthPx RadWth2t AzimDeg      dsp  Nbour Nsatur NearMsk      p
//      1  1134.83  1606.13   592.37   38557.12   104.61     4.84     227.39     1150    19.88     1.82     26       32     0.74    3.48   1.7195      1      0      No  0.932
                
                Peak pic = new Peak(Float.parseFloat(lineS[1]),Float.parseFloat(lineS[2]));
                pic.setRadi(Float.parseFloat(lineS[3]));
                pic.setYmax(Float.parseFloat(lineS[4]));
                pic.setFh2(Float.parseFloat(lineS[5]));
                pic.setSfh2(Float.parseFloat(lineS[6]));
                pic.setYmean(Float.parseFloat(lineS[7]));
                pic.setNpix(Integer.parseInt(lineS[8]));
                pic.setYbkg(Float.parseFloat(lineS[9]));
                pic.setYbkgSD(Float.parseFloat(lineS[10]));
                pic.setNbkgpix(Integer.parseInt(lineS[11]));
                pic.setIntRadPx(Integer.parseInt(lineS[12]));
                pic.setIntRad2th(Float.parseFloat(lineS[13]));
                pic.setAzimAper(Float.parseFloat(lineS[14]));
                pic.setDsp(Float.parseFloat(lineS[15]));
                pic.setnVeinsEixam(Integer.parseInt(lineS[16]));
                pic.setnSatur(Integer.parseInt(lineS[17]));
                pic.setNearMask(false);
                if (lineS[18].trim()=="Yes")pic.setNearMask(true);
                pic.setP(Float.parseFloat(lineS[19]));

                Patt2Dzone pz = new Patt2Dzone(pic.getNpix(), -1, (int)pic.getYmax(), pic.getYmean(), -1, pic.getYbkg(), pic.getYbkgSD());
                pz.setIntradPix(pic.getIntRadPx());
                pz.setAzimAngle(pic.getAzimAper());
                pz.setBkgpt(pic.getNbkgpix());
                pz.setCentralPoint(pic.getPixelCentre());
                pz.setPatt2d(this.patt2d);
                pic.setZona(pz);
                
                pic.setIntegrated(true);
                
                realPeaks.add(pic);
            }
            patt2d.setPkSearchResult(realPeaks);
            scPCSfile.close();
            this.updateTable();
        } catch (Exception e) {
            if (D2Dplot_global.isDebug()) e.printStackTrace();
            log.warning("Error reading SOL file");
        }
    }
    
    private File exportTable(File txtFile){
        String eqLine="===========================================================================================================================================================================";
        String minLine="---------------------------------------------------------------------------------------------------------------------------------------------------------------------------";
     
        // creem un printwriter amb el fitxer file (el que estem escribint)
        try {
            //preparem les files (ho he mogut aqui perque em fan falta alguns calculs per la capçalera
            Iterator<Peak> itrR = patt2d.getPkSearchResult().iterator();
            int maxIntRad = 0;
            float maxAzim = 0.f;
            float minAzim = 99999999.f;
            while (itrR.hasNext()){
                Peak r = itrR.next();
                if(r.getIntRadPx()>maxIntRad)maxIntRad=r.getIntRadPx();
                if(r.getAzimAper()>maxAzim)maxAzim=r.getAzimAper();
                if(r.getAzimAper()<minAzim)minAzim=r.getAzimAper();
            }
            
            PrintWriter output = new PrintWriter(new BufferedWriter(new FileWriter(txtFile)));
            // ESCRIBIM AL FITXER:
            output.println(eqLine);
            output.println("D2Dplot peak integration for INCO");
            output.println(eqLine);
            output.println("Image File= "+patt2d.getImgfileString());
            output.println(String.format("dimX= %d, dimY= %d, centX= %.2f, centY= %.2f",patt2d.getDimX(),patt2d.getDimY(),patt2d.getCentrX(),patt2d.getCentrY()));
            output.println(String.format("pixSize(micron)= %.3f, dist(mm)= %.3f, wave(A)= %.5f",patt2d.getPixSx()*1000,patt2d.getDistMD(),patt2d.getWavel()));
            switch (patt2d.getIscan()){
                case 1:
                    output.println("HORIZONTAL rotation axis");
                    break;
                case 2:
                    output.println("VERTICAL rotation axis");
                    break;
                default:
                    output.println("NO rotation axis");
                    break;
            }
            output.println(String.format("Saturated pixels= %d (sat. value= %d)",patt2d.getnSatur(),patt2d.getSaturValue()));
            output.println(String.format("Saturated peaks= %d",patt2d.getnPkSatur())); 
            output.println(String.format("MeanI= %d Sigma(I)= %.3f",patt2d.getMeanI(),patt2d.getSdevI()));
            if (chckbxAutodelsig.isSelected()){
                output.println(String.format("ESD factor (º)= %.2f (2theta dependance ENABLED)",angDeg));    
            }else{
                output.println(String.format("ESD factor (º)= %.2f",angDeg));    
            }
            if (chckbxAutoazim.isSelected()){
                output.println(String.format("Auto azim aperture of the integration (º) in the range %.2f to %.2f",minAzim,maxAzim));
            }else{
                output.println(String.format("Azim aperture of the integration (º)= %.2f",angDeg));
            }
            output.println(String.format("Max radial integration width (pixels)= %d",maxIntRad));
            output.println(String.format("Peak merge zone radius (pixels)= %d",zoneR));
            output.println(String.format("Min pixels for a peak= %d",minpix));
            if (chckbxAutobkgpt.isSelected()){
                output.println(String.format("Background pixels determined automatically"));
            }else{
                output.println(String.format("Background pixels= %d",bkgpt));
            }
            output.println(minLine);
            
            output.println(Peak.all_header);
            
            //ara ordenem la llista i l'escribim
            Collections.sort(patt2d.getPkSearchResult());
            Iterator<Peak> itrpcs = patt2d.getPkSearchResult().iterator();
            int npk = 1;
            while (itrpcs.hasNext()){
                Peak pcsr= itrpcs.next();
                output.println(String.format("%6d %s", npk,pcsr.getFormmattedStringAll()));
                npk=npk+1;
            }
            output.close();

        } catch (IOException e) {
            if (D2Dplot_global.isDebug()) e.printStackTrace();
            log.warning("Error writting PCS file");
            return null;
        }
        log.printmsg("INFO", "Full info table exported in text format!");
        return txtFile;
    }
        
    protected void do_btnMaskbin_actionPerformed(ActionEvent e) {
        if (this.patt2d != null) {
            // tanquem calibracio en cas que estigui obert
            ExZones_dialog exZones = new ExZones_dialog(this.iPanel);
            exZones.setVisible(true);
            this.iPanel.setExZones(exZones);
        }
    }
    protected void do_btnBatch_actionPerformed(ActionEvent arg0) {
        //obrir un fchoser multiple i seleccionar imatges
        FileUtils.InfoDialog(this, "Selected images will be integrated with current options \nand PCS files with the same filename generated." , "batch pk search and integrate");
        
        //integrar amb les opcions actuals
        this.readSearchOptions();
        this.readIntegrateOptions();//aqui s'assigna iosc
        
        //carregar imatge, integrar, escriure PCS amb el mateix nom 
        //i tal s'encarrega el swingworker de imgops
        FileNameExtensionFilter filt[] = ImgFileUtils.getExtensionFilterRead();
        File[] flist = FileUtils.fchooserMultiple(this,new File(D2Dplot_global.getWorkdir()), filt, filt.length-1);
        if (flist==null) return;
        D2Dplot_global.setWorkdir(flist[0]);
        
        pm = new ProgressMonitor(null,
                "Peak Search and Integrate on several images in progress...",
                "", 0, 100);
        pm.setProgress(0);
        //TODO POSAR LOG DEL MAINFRAME
        convwk = new ImgOps.PkIntegrateFileWorker(flist,this.iPanel.getMainFrame().gettAOut(),delsig,
                chckbxAutodelsig.isSelected(),zoneR,minpix,bkgpt,chckbxAutobkgpt.isSelected(),tol2tpix,chckbxAutointrad.isSelected(),
                angDeg,chckbxAutoazim.isSelected(),chckbxLpCorrection.isSelected(),iosc);
        
        convwk.addPropertyChangeListener(new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                log.debug(evt.getPropertyName());
                if ("progress" == evt.getPropertyName() ) {
                    int progress = (Integer) evt.getNewValue();
                    pm.setProgress(progress);
                    pm.setNote(String.format("%d%%\n", progress));
                    if (pm.isCanceled() || convwk.isDone()) {
                        Toolkit.getDefaultToolkit().beep();
                        if (pm.isCanceled()) {
                            convwk.cancel(true);
                            log.debug("Pk Search and Integrate stopped by user");
                        } else {
                            log.debug("Pk Search and Integrate finished!!");
                        }
                        pm.close();
                    }
                }
            }
        });
        convwk.execute();
    }
    protected void do_btnImport_actionPerformed(ActionEvent arg0) {
        File f = FileUtils.fchooserOpen(this, new File(D2Dplot_global.getWorkdir()), null, 0);
        if (f==null)return;
        if (FileUtils.getExtension(f).equalsIgnoreCase("OUT")){
            fileout = f;
            this.importOUT(f);
        }else{
            this.importTable(f);            
        }
    }
    
    public File getFileOut(){
        return this.fileout;
    }
    
    public void importOUT(File outFile){
        boolean singleImg = false;
        int inum = patt2d.getFileNameNumber();
        log.debug("image Number="+inum);
        if (inum>=0)singleImg = true;
        
        try {
            Scanner scOUTfile = new Scanner(outFile);
            boolean firstLine = false;
            String line;
            while (!firstLine){
                line = scOUTfile.nextLine();
                if (line.trim().startsWith("NPEAK"))firstLine=true;
            }
            line = scOUTfile.nextLine(); //linia numero d'imatge
            String[] lineS = line.trim().split("\\s+");
//            int cnum = Integer.parseInt(lineS[0])-1; //-1 perque fortran comença a escriure a 1 i no a zero
            int cnum = Integer.parseInt(lineS[0]); // numero de la imatge segons el fitxer
            log.debug("current out reading Number="+cnum);

            
            ArrayList<Peak> realPeaks = new ArrayList<Peak>();
            while (scOUTfile.hasNextLine()){
                //System.out.println(line);
                line = scOUTfile.nextLine();
                if (line.trim().startsWith("NPEAK")){
                    line = scOUTfile.nextLine(); //linia numero d'imatge
                    lineS = line.trim().split("\\s+");
//                    cnum = Integer.parseInt(lineS[0])-1;
                    cnum = Integer.parseInt(lineS[0]);
                    log.debug("current out reading Number="+cnum);
                    continue;//seguim
                }
                if(line.trim().startsWith("----"))continue;
                if(line.trim().startsWith("NOMBRE"))continue;
                if(line.trim().isEmpty())continue;
                
                if (singleImg){
                    if (cnum!=inum){
                        log.debug("cnum!=inum");
                        continue;
                    }
                }
                
                //en principi aqui tindrem pics a afegir
                
                log.debug("Reading Matching Image Peaks="+inum+" (out file="+cnum+")");
                
                lineS = line.trim().split("\\s+");
                Peak pic = new Peak(Float.parseFloat(lineS[1]),Float.parseFloat(lineS[2]));
                try{
                    pic.setRadi(Float.parseFloat(lineS[3]));
                }catch(Exception ex){
                    ex.printStackTrace();
                    pic.setRadi(0);
                }
                try{
                    pic.setYmax(Float.parseFloat(lineS[4]));
                }catch(Exception ex){
                    ex.printStackTrace();
                    pic.setYmax(0);
                }
                try{
                    pic.setFh2(Float.parseFloat(lineS[5])*Float.parseFloat(lineS[5]));
                }catch(Exception ex){
                    ex.printStackTrace();
                    pic.setFh2(0);
                }
                try{
                    pic.setSfh2(Float.parseFloat(lineS[6])*Float.parseFloat(lineS[6]));
                }catch(Exception ex){
                    ex.printStackTrace();
                    pic.setSfh2(0);
                }
                try{
                    pic.setP(Float.parseFloat(lineS[7]));
                }catch(Exception ex){
                    ex.printStackTrace();
                    pic.setP(0);
                }
                try{
                    pic.setIntRadPx(Integer.parseInt(lineS[9]));
                }catch(Exception ex){
                    ex.printStackTrace();
                    pic.setIntRadPx(0);
                }
                try{
                    pic.setDsp(Float.parseFloat(lineS[10]));
                }catch(Exception ex){
                    ex.printStackTrace();
                    pic.setDsp(0);
                }

                
                pic.setYmean(0);
                pic.setNpix(0);
                pic.setYbkg(0);
                pic.setYbkgSD(0);
                pic.setNbkgpix(0);
                pic.setIntRad2th(0);
                pic.setAzimAper(0);
                pic.setnVeinsEixam(0);
                pic.setnSatur(0);
                pic.setNearMask(false);

                Patt2Dzone pz = new Patt2Dzone(pic.getNpix(), -1, (int)pic.getYmax(), pic.getYmean(), -1, pic.getYbkg(), pic.getYbkgSD());
                pz.setIntradPix(pic.getIntRadPx());
                pz.setAzimAngle(pic.getAzimAper());
                pz.setBkgpt(pic.getNbkgpix());
                pz.setCentralPoint(pic.getPixelCentre());
                pz.setPatt2d(this.patt2d);
                pic.setZona(pz);
                
                pic.setIntegrated(true);
                
                realPeaks.add(pic);
                
                if (singleImg){
                    if (cnum>inum){
                        log.debug("cnum>inum");
                        break; //ja hem llegit la imatge bona
                    }
                }
            }
            patt2d.setPkSearchResult(realPeaks);
            scOUTfile.close();
            this.updateTable();
        } catch (Exception e) {
            if (D2Dplot_global.isDebug()) e.printStackTrace();
            log.warning("Error reading OUT file");
        }
    }
    
    protected void do_chckbxShowPoints_itemStateChanged(ItemEvent e) {
        this.iPanel.actualitzarVista();
    }
    protected void do_spinner_stateChanged(ChangeEvent e) {
        this.iPanel.actualitzarVista();
    }

    protected void do_table_valueChanged(ListSelectionEvent e) {
        if (!e.getValueIsAdjusting()) {
            this.iPanel.actualitzarVista();
        }   
    }
    
    
    //TODO
    public void writeOUT(File outFile){
        boolean singleImg = false;
        int inum = patt2d.getFileNameNumber();
        log.debug("image Number="+inum);
        if (inum>=0)singleImg = true;
        
        try {
            Scanner scOUTfile = new Scanner(outFile);
            boolean firstLine = false;
            String line;
            while (!firstLine){
                line = scOUTfile.nextLine();
                if (line.trim().startsWith("NPEAK"))firstLine=true;
            }
            line = scOUTfile.nextLine(); //linia numero d'imatge
            String[] lineS = line.trim().split("\\s+");
//            int cnum = Integer.parseInt(lineS[0])-1; //-1 perque fortran comença a escriure a 1 i no a zero
            int cnum = Integer.parseInt(lineS[0]); // numero de la imatge segons el fitxer
            log.debug("current out reading Number="+cnum);

            
            ArrayList<Peak> realPeaks = new ArrayList<Peak>();
            while (scOUTfile.hasNextLine()){
                System.out.println(line);
                line = scOUTfile.nextLine();
                if (line.trim().startsWith("NPEAK")){
                    line = scOUTfile.nextLine(); //linia numero d'imatge
                    lineS = line.trim().split("\\s+");
//                    cnum = Integer.parseInt(lineS[0])-1;
                    cnum = Integer.parseInt(lineS[0]);
                    log.debug("current out reading Number="+cnum);
                    continue;//seguim
                }
                if(line.trim().startsWith("----"))continue;
                if(line.trim().startsWith("NOMBRE"))continue;
                if(line.trim().isEmpty())continue;
                
                if (singleImg){
                    if (cnum!=inum){
                        log.debug("cnum!=inum");
                        continue;
                    }
                }
                
                //en principi aqui tindrem pics a afegir
                
                lineS = line.trim().split("\\s+");
                Peak pic = new Peak(Float.parseFloat(lineS[1]),Float.parseFloat(lineS[2]));
                try{
                    pic.setRadi(Float.parseFloat(lineS[3]));
                }catch(Exception ex){
                    ex.printStackTrace();
                    pic.setRadi(0);
                }
                try{
                    pic.setYmax(Float.parseFloat(lineS[4]));
                }catch(Exception ex){
                    ex.printStackTrace();
                    pic.setYmax(0);
                }
                try{
                    pic.setFh2(Float.parseFloat(lineS[5])*Float.parseFloat(lineS[5]));
                }catch(Exception ex){
                    ex.printStackTrace();
                    pic.setFh2(0);
                }
                try{
                    pic.setSfh2(Float.parseFloat(lineS[6])*Float.parseFloat(lineS[6]));
                }catch(Exception ex){
                    ex.printStackTrace();
                    pic.setSfh2(0);
                }
                try{
                    pic.setP(Float.parseFloat(lineS[7]));
                }catch(Exception ex){
                    ex.printStackTrace();
                    pic.setP(0);
                }
                try{
                    pic.setIntRadPx(Integer.parseInt(lineS[9]));
                }catch(Exception ex){
                    ex.printStackTrace();
                    pic.setIntRadPx(0);
                }
                try{
                    pic.setDsp(Float.parseFloat(lineS[10]));
                }catch(Exception ex){
                    ex.printStackTrace();
                    pic.setDsp(0);
                }

                
                pic.setYmean(0);
                pic.setNpix(0);
                pic.setYbkg(0);
                pic.setYbkgSD(0);
                pic.setNbkgpix(0);
                pic.setIntRad2th(0);
                pic.setAzimAper(0);
                pic.setnVeinsEixam(0);
                pic.setnSatur(0);
                pic.setNearMask(false);

                Patt2Dzone pz = new Patt2Dzone(pic.getNpix(), -1, (int)pic.getYmax(), pic.getYmean(), -1, pic.getYbkg(), pic.getYbkgSD());
                pz.setIntradPix(pic.getIntRadPx());
                pz.setAzimAngle(pic.getAzimAper());
                pz.setBkgpt(pic.getNbkgpix());
                pz.setCentralPoint(pic.getPixelCentre());
                pz.setPatt2d(this.patt2d);
                pic.setZona(pz);
                
                pic.setIntegrated(true);
                
                realPeaks.add(pic);
                
                if (singleImg){
                    if (cnum>inum){
                        log.debug("cnum>inum");
                        break; //ja hem llegit la imatge bona
                    }
                }
            }
            patt2d.setPkSearchResult(realPeaks);
            scOUTfile.close();
            this.updateTable();
        } catch (Exception e) {
            if (D2Dplot_global.isDebug()) e.printStackTrace();
            log.warning("Error reading OUT file");
        }
    }
   
    protected void do_btnBatchout_actionPerformed(ActionEvent arg0) {
        //obrir un fchoser multiple i seleccionar imatges
        FileUtils.InfoDialog(this, "Selected images will be integrated with current options \nand a single OUT file will be generated." , "batch pk search and integrate");
        
        //integrar amb les opcions actuals
        this.readSearchOptions();
        this.readIntegrateOptions();//aqui s'assigna iosc
        
        //carregar imatge, integrar, escriure PCS amb el mateix nom 
        //i tal s'encarrega el swingworker de imgops
        FileNameExtensionFilter filt[] = ImgFileUtils.getExtensionFilterRead();
        File[] flist = FileUtils.fchooserMultiple(this,new File(D2Dplot_global.getWorkdir()), filt, filt.length-1);
        if (flist==null) return;
        D2Dplot_global.setWorkdir(flist[0]);
        
        pm = new ProgressMonitor(null,
                "Peak Search and Integrate on several images in progress...",
                "", 0, 100);
        pm.setProgress(0);
        //TODO POSAR LOG DEL MAINFRAME
        pkscwk = new ImgOps.PkSCIntegrateFileWorker(flist,this.iPanel.getMainFrame().gettAOut(),delsig,
                chckbxAutodelsig.isSelected(),zoneR,minpix,bkgpt,chckbxAutobkgpt.isSelected(),tol2tpix,chckbxAutointrad.isSelected(),
                angDeg,chckbxAutoazim.isSelected(),chckbxLpCorrection.isSelected(),iosc);
        
        pkscwk.addPropertyChangeListener(new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                log.debug(evt.getPropertyName());
                if ("progress" == evt.getPropertyName() ) {
                    int progress = (Integer) evt.getNewValue();
                    pm.setProgress(progress);
                    pm.setNote(String.format("%d%%\n", progress));
                    if (pm.isCanceled() || pkscwk.isDone()) {
                        Toolkit.getDefaultToolkit().beep();
                        if (pm.isCanceled()) {
                            pkscwk.cancel(true);
                            log.debug("Pk Search and Integrate stopped by user");
                        } else {
                            log.debug("Pk Search and Integrate finished!!");
                        }
                        pm.close();
                    }
                }
            }
        });
        pkscwk.execute();
    }
}
