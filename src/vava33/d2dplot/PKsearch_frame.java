package vava33.d2dplot;

import java.awt.Toolkit;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import com.vava33.jutils.FileUtils;
import com.vava33.jutils.VavaLogger;

import vava33.d2dplot.auxi.ImgOps;
import vava33.d2dplot.auxi.OrientSolucio;
import vava33.d2dplot.auxi.Patt2Dzone;
import vava33.d2dplot.auxi.Pattern2D;
import vava33.d2dplot.auxi.PuntSolucio;
import vava33.d2dplot.auxi.findPksTableRenderer;
import net.miginfocom.swing.MigLayout;

import javax.swing.JCheckBox;
import javax.swing.JLabel;

import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;

import javax.swing.JSplitPane;
import javax.swing.JButton;
import javax.swing.JTextField;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

import org.apache.commons.math3.util.FastMath;

import javax.swing.border.TitledBorder;

import java.awt.Color;

import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.SpinnerListModel;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;

public class PKsearch_frame extends JFrame {
    /**
     * 
     */
    private static final long serialVersionUID = -3599627105512682021L;

    private VavaLogger log = D2Dplot_global.log;

    private JPanel contentPane;

    private Pattern2D patt2d;
    private boolean fromInco;
    private Dinco_frame dincoFrame;

    private JCheckBox chckbxOnTop;
    private JPanel panel;
    private JLabel lblDelsig;
    private JLabel lblZoneradius;
    private JButton btnCalculate;
    private JCheckBox chckbxShowPoints;
    private JTextField txtDelsig;
    private JTextField txtZoneradius;
    private JButton btnClose;
    private JPanel panel_1;
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
    
    private ArrayList<Patt2Dzone> pkinteg;
    private JTable table;
    private JScrollPane scrollPane_1;
    private JSpinner spinerAxis;
    private JButton btnExportPeakList;
    
    /**
     * Launch the application.
     */
//    public static void main(String[] args) {
//        EventQueue.invokeLater(new Runnable() {
//            public void run() {
//                try {
//                    PKsearch_frame frame = new PKsearch_frame();
//                    frame.setVisible(true);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        });
//    }

    /**
     * Create the frame.
     */
    public PKsearch_frame(Pattern2D patt, boolean fromInco, Dinco_frame dframe) {
        setAlwaysOnTop(true);
        this.patt2d=patt;
        this.fromInco=fromInco;
        this.dincoFrame = dframe;
        setTitle("PeakSearch");
        setIconImage(Toolkit.getDefaultToolkit().getImage(Dinco_frame.class.getResource("/img/Icona.png")));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setBounds(100, 100, 560, 600);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(new MigLayout("fill, insets 3", "[grow]", "[grow][]"));
        
        JSplitPane splitPane = new JSplitPane();
        splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
        contentPane.add(splitPane, "cell 0 0,grow");
        
        panel = new JPanel();
        splitPane.setLeftComponent(panel);
        panel.setLayout(new MigLayout("insets 5", "[grow][grow][]", "[][][][][][]"));
        
        chckbxShowPoints = new JCheckBox("Show Points");
        chckbxShowPoints.setSelected(true);
        panel.add(chckbxShowPoints, "cell 0 0 2 1");
        
        chckbxOnTop = new JCheckBox("on top");
        panel.add(chckbxOnTop, "cell 2 0");
        chckbxOnTop.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent arg0) {
                do_chckbxOnTop_itemStateChanged(arg0);
            }
        });
        chckbxOnTop.setSelected(true);
        
        lblDelsig = new JLabel("ESD factor (delsig)=");
        panel.add(lblDelsig, "cell 0 1,alignx trailing");
        
        txtDelsig = new JTextField();
        txtDelsig.setText("3");
        panel.add(txtDelsig, "cell 1 1 2 1,growx");
        txtDelsig.setColumns(10);
        
        lblZoneradius = new JLabel("Pk merge zone (zoneRadius, px)=");
        panel.add(lblZoneradius, "cell 0 2,alignx trailing");
        
        txtZoneradius = new JTextField();
        txtZoneradius.setText("5");
        panel.add(txtZoneradius, "cell 1 2 2 1,growx");
        txtZoneradius.setColumns(10);
        
        btnCalculate = new JButton("Calculate");
        btnCalculate.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                do_btnCalculate_actionPerformed(arg0);
            }
        });
        
        panel_1 = new JPanel();
        panel.add(panel_1, "cell 0 3 3 1,grow");
        panel_1.setLayout(new MigLayout("insets 0", "[grow][][grow]", "[]"));
        
        lblPlotSize = new JLabel("Plot size");
        panel_1.add(lblPlotSize, "cell 0 0,alignx right");
        
        spinner = new JSpinner();
        spinner.setModel(new SpinnerNumberModel(2, 1, 5, 1));
        panel_1.add(spinner, "cell 1 0,alignx left");
        
        chckbxAddremovePeaks = new JCheckBox("Add/Remove peaks");
        panel_1.add(chckbxAddremovePeaks, "cell 2 0,alignx center");
        
        panel_2 = new JPanel();
        panel_2.setBorder(new TitledBorder(null, "Integration parameters", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(51, 51, 51)));
        panel.add(panel_2, "cell 0 4 3 1,grow");
        panel_2.setLayout(new MigLayout("", "[][grow][][grow]", "[][]"));
        
        lblthWidth = new JLabel("2"+D2Dplot_global.theta+"(º) width");
        panel_2.add(lblthWidth, "cell 0 0,alignx trailing");
        
        txtTtol = new JTextField();
        txtTtol.setText("0.4");
        panel_2.add(txtTtol, "cell 1 0,growx");
        txtTtol.setColumns(10);
        
        lblAzimAperture = new JLabel("Azim aperture (º)");
        panel_2.add(lblAzimAperture, "cell 2 0,alignx trailing");
        
        txtAngdeg = new JTextField();
        txtAngdeg.setText("3");
        panel_2.add(txtAngdeg, "cell 3 0,growx");
        txtAngdeg.setColumns(10);
        
        lblBkgPoints = new JLabel("bkg points");
        panel_2.add(lblBkgPoints, "cell 0 1,alignx trailing");
        
        txtBkgPt = new JTextField();
        txtBkgPt.setText("20");
        panel_2.add(txtBkgPt, "cell 1 1,growx");
        txtBkgPt.setColumns(10);
        
        chckbxLpCorrection = new JCheckBox("LP correction");
        panel_2.add(chckbxLpCorrection, "cell 2 1,alignx center");
        
        spinerAxis = new JSpinner();
        spinerAxis.setModel(new SpinnerListModel(new String[] {"V oscil. axis", "H oscil. axis"}));
        panel_2.add(spinerAxis, "cell 3 1,growx");
        panel.add(btnCalculate, "cell 0 5 3 1,alignx center");
        
//        lblHeader = new JLabel("pX pY Intensity SDIntensity Npx Ysum Ybkg Ymean SDYmean SDbkg");
//        panel.add(lblHeader, "cell 0 6");
//        
//        scrollPane = new JScrollPane();
//        panel.add(scrollPane, "cell 0 7 3 1,grow");
//        
//        list = new JList();
//        scrollPane.setViewportView(list);
        
        scrollPane_1 = new JScrollPane();
        splitPane.setRightComponent(scrollPane_1);
        
        table = new JTable();
        table.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                do_table_keyReleased(e);
            }
        });
        table.setModel(new DefaultTableModel(
            new Object[][] {
                {null, null, null, null, null, null, null, null, null, null},
            },
            new String[] {
                "XPix", "YPix", "FH2", "sigma(FH2)", "Npix", "Ymax", "Ymean", "Ybkg", "SDbkg"
            }
        ) {
            /**
             * 
             */
            private static final long serialVersionUID = -4167032016502576681L;
            @SuppressWarnings("rawtypes")
            Class[] columnTypes = new Class[] {
                Integer.class, Integer.class, Float.class, Float.class, Integer.class, Integer.class, Float.class, Float.class, Float.class
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
        //        table.setDefaultRenderer(Float.class, new findPksTableRenderer());
        table.setDefaultRenderer(Float.class, new findPksTableRenderer());
        table.setDefaultRenderer(Integer.class, new findPksTableRenderer());
        table.setAutoCreateRowSorter(true);
        table.getTableHeader().setReorderingAllowed(false);
        
        scrollPane_1.setViewportView(table);
        
        btnClose = new JButton("close");
        btnClose.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                do_btnClose_actionPerformed(e);
            }
        });
        
        btnExportPeakList = new JButton("Export Peak List");
        btnExportPeakList.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                do_btnExportPeakList_actionPerformed(arg0);
            }
        });
        contentPane.add(btnExportPeakList, "flowx,cell 0 1,alignx right");
        contentPane.add(btnClose, "cell 0 1,alignx right");
        
        //resetejem si hi ha peaksearch previ (de l'inco o wherever)
        if(this.patt2d.getPkSearchResult()!=null)this.patt2d.getPkSearchResult().clear();
        
        if(fromInco){
            lblDelsig.setEnabled(false);
            lblZoneradius.setEnabled(false);
            txtDelsig.setEnabled(false);
            txtZoneradius.setEnabled(false);
            chckbxAddremovePeaks.setEnabled(false);
            btnCalculate.doClick();
        }
    }

    @Override
    public void dispose() {
        this.chckbxShowPoints.setSelected(false);
        super.dispose();
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
    
    protected void do_btnCalculate_actionPerformed(ActionEvent arg0) {
        if(!fromInco){ //busquem els pics amb els parametres entrats
            float delsig=3.f;
            int zoneR=5;
            try{
                delsig = Float.parseFloat(txtDelsig.getText());
            }catch(Exception e){
                log.warning(String.format("Cannot read delsig, using default value %f",delsig));
            }
            try{
                zoneR = Integer.parseInt(txtZoneradius.getText());
            }catch(Exception e){
                log.warning(String.format("Cannot read zone radius, using default value %d",zoneR));
            }
            patt2d.setPkSearchResult(ImgOps.findPeaks(patt2d, delsig, zoneR,true));
        }else{
            //importem els pics de la solucio seleccionada de l'inco
            Object[] oos = dincoFrame.getActiveOrientSols();
            if (oos == null) return;
            ArrayList<Point2D.Float> dincoSolPunts = new ArrayList<Point2D.Float>();
            for (int i=0;i<oos.length;i++){
                OrientSolucio os = (OrientSolucio)oos[i];
                Iterator<PuntSolucio> itrS = os.getSol().iterator();
                while (itrS.hasNext()) {
                    PuntSolucio s = itrS.next();
                    dincoSolPunts.add(new Point2D.Float(s.getCoordX(),s.getCoordY()));
                }
            }
            patt2d.setPkSearchResult(dincoSolPunts);
        }
        
        //now we integrate the peaks using Yarctilt
        //public static Patt2Dzone YarcTilt(Pattern2D patt2D, int px, int py, float tol2t, float angleDeg, boolean self, int bkgpt, boolean debug){
        int bkgpt = 20;
        float tol2t = 0.4f;
        float angDeg = 3.0f;
        boolean debug = false;
        
        try{
            tol2t = Float.parseFloat(txtTtol.getText());
        }catch(Exception e){
            log.warning(String.format("Cannot read tol2t, using default value %f",tol2t));
            txtTtol.setText(String.valueOf(tol2t));
        }
        try{
            angDeg = Float.parseFloat(txtAngdeg.getText());
        }catch(Exception e){
            log.warning(String.format("Cannot read angDeg, using default value %f",angDeg));
            txtAngdeg.setText(String.valueOf(angDeg));
        }
        try{
            bkgpt = Integer.parseInt(txtBkgPt.getText());
        }catch(Exception e){
            log.warning(String.format("Cannot read bkgpt, using default value %d",bkgpt));
            txtBkgPt.setText(String.valueOf(bkgpt));
        }
        
        pkinteg = new ArrayList<Patt2Dzone>();
        Iterator<Point2D.Float> itrpks = patt2d.getPkSearchResult().iterator();
        while (itrpks.hasNext()){
            Point2D.Float pk = itrpks.next();
            Patt2Dzone pz = ImgOps.YarcTilt(patt2d, FastMath.round(pk.x), FastMath.round(pk.y), tol2t, angDeg, true, bkgpt, debug);
            pkinteg.add(pz);
        }
        
//        this.updateList();
        this.updateTable();
    }
    
    public Patt2Dzone integratePk(Point2D.Float pk){
        int bkgpt = 20;
        float tol2t = 0.4f;
        float angDeg = 3.0f;
        boolean debug = false;
        
        try{
            tol2t = Float.parseFloat(txtTtol.getText());
        }catch(Exception e){
            log.warning(String.format("Cannot read tol2t, using default value %f",tol2t));
            txtTtol.setText(String.valueOf(tol2t));
        }
        try{
            angDeg = Float.parseFloat(txtAngdeg.getText());
        }catch(Exception e){
            log.warning(String.format("Cannot read angDeg, using default value %f",angDeg));
            txtAngdeg.setText(String.valueOf(angDeg));
        }
        try{
            bkgpt = Integer.parseInt(txtBkgPt.getText());
        }catch(Exception e){
            log.warning(String.format("Cannot read bkgpt, using default value %d",bkgpt));
            txtBkgPt.setText(String.valueOf(bkgpt));
        }
        
        return ImgOps.YarcTilt(patt2d, FastMath.round(pk.x), FastMath.round(pk.y), tol2t, angDeg, true, bkgpt, debug);

    }
    
    public void updateTable(){
        ((DefaultTableModel)table.getModel()).setRowCount(0);
        
        //mostrarem els pics
        for(int i=0; i<patt2d.getPkSearchResult().size();i++){
            Point2D.Float pk = patt2d.getPkSearchResult().get(i);
            Patt2Dzone pz = pkinteg.get(i);

            Object[] row = new Object[9];
            //"XPix", "YPix", "FH2", "sigma(FH2)", "Npix", "Ymax", "Ymean", "Ybkg", "SDbkg"
            row[0] = FastMath.round(pk.x);
            row[1] = FastMath.round(pk.y);

//            APLP=AP(IPIC)*RLOREN/POL
//            APSLP=FSDVAP(IPIC)*APLP
            
            float inten = pz.getYsum()-(pz.getNpix()*pz.getYbkg());
            float esdinten = (float)FastMath.sqrt(pz.getYsum()+2*pz.getYbkgdesv())/inten;
            
            if (chckbxLpCorrection.isSelected()){
                String val = spinerAxis.getValue().toString();
                log.fine(val.toString()); //V oscil. axis
                log.fine(val.getClass().getName()); //java.lang.String
                int iosc = 2;
                if (val.contains("H")){
                    iosc = 1;
                }
                //iosc es l'eix de tilt, 1=horitzontal, 2=vertical
                float[] lpfac = ImgOps.corrLP(patt2d, (Integer)row[0], (Integer)row[1], 1, 1, iosc, false);
                
                row[2] = (float)FastMath.sqrt(inten*lpfac[1]*lpfac[2]); //this is FH2 of a lorentz polar correction
                row[3] = (float)FastMath.sqrt(esdinten*(Float)row[2]);
                
            }else{
                row[2] = (float)FastMath.sqrt(inten); //this is FH2 without lorentz polar correction
                row[3] = (float)FastMath.sqrt(esdinten*(Float)row[2]);
            }
            row[4] = pz.getNpix();
            row[5] = pz.getYmax();
            row[6] = pz.getYmean();
            row[7] = pz.getYbkg();
            row[8] = pz.getYbkgdesv();
            ((DefaultTableModel)table.getModel()).addRow(row);
        }
    }
    
//    private void updateList(){
//
//        DefaultListModel lm = new DefaultListModel();
//
//        //mostrarem els pics
//        for(int i=1; i<patt2d.getPkSearchResult().size();i++){
//            Point2D.Float pk = patt2d.getPkSearchResult().get(i);
//            Patt2Dzone pz = pkinteg.get(i);
//
//            //pX pY Ysum Ybkg Ymean SDYmean SDbkg Npx
//            //CAS DE TOTA LA INFO COMPLETA
//            lblHeader.setText("pX pY Ysum Ybkg Ymean SDYmean SDbkg Npx");
//            String s = String.format("%6d %6d %8d %8.2f %8.2f %8.2f %8.2f %5d", FastMath.round(pk.x),FastMath.round(pk.y),pz.getYsum(),pz.getYbkg(),pz.getYmean(),pz.getYmeandesv(),pz.getYbkgdesv(),pz.getNpix());
//            lm.addElement(s);
//            
//            //jordi:
//            //FSDVAP(I)=SQRT(PKINT+PKFNS+2*SGLAS)/AP(I)
//            
//            //info util neta
////            lblHeader.setText("pX pY Intensity SDIntensity Npx Ysum Ybkg Ymean SDYmean SDbkg");
////            String s = String.format("%6d %6d %8d %8.2f %8.2f %8.2f %8.2f %5d", FastMath.round(pk.x),FastMath.round(pk.y),pz.getYsum(),pz.getYbkg(),pz.getYmean(),pz.getYmeandesv(),pz.getYbkgdesv(),pz.getNpix());
////            lm.addElement(s);
//            
//        }
//
//        list.setModel(lm);
//    }
    
//    public Point2D.Float getSelectedPeak(){
//        String val = (String)list.getSelectedValue();
//        String[] vals = val.split("\\s+");
//        Point2D.Float punt = null;
//        try{
//            punt = new Point2D.Float(Integer.parseInt(vals[0]),Integer.parseInt(vals[1]));            
//        }catch(Exception e){
//            log.debug("error getting selected point coordinates");
//        }
//        return punt;
//    }
    
//    public Point2D.Float[] getSelectedPeaks(){
//        Object[] val = list.getSelectedValues();
//        if (val.length<=0)return null;
//        Point2D.Float[] selpunts = new Point2D.Float[val.length];
//        for (int i=0;i<val.length;i++){
//            String[] vals = ((String)val[i]).split("\\s+");
//            Point2D.Float punt = null;
//            try{
//                punt = new Point2D.Float(Integer.parseInt(vals[0]),Integer.parseInt(vals[1]));            
//            }catch(Exception e){
//                log.debug("error getting selected point coordinates");
//            }
//            selpunts[i]=punt;
//        }
//        return selpunts;
//    }
    
    public Point2D.Float[] getSelectedPeaks(){
        

//MODEL ROW VS VIEW ROW!!!
//        int viewRow = table.getSelectedRow();
//        int modelRow = table.convertRowIndexToModel(viewRow);
//        int viewColumn = table.getSelectedColumn();
//        int modelColumn = table.convertColumnIndexToModel(viewColumn);
//        Object cell = model.getValueAt( modelRow, modelColumn );
        
        int[] val = table.getSelectedRows();
        if (val.length<=0)return null;
        Point2D.Float[] selpunts = new Point2D.Float[val.length];
        
        for (int i=0;i<val.length;i++){
            int modelRow = table.convertRowIndexToModel(val[i]);
            Point2D.Float punt = null;
            try{
                int px = (Integer)table.getModel().getValueAt(modelRow, 0);
                int py = (Integer)table.getModel().getValueAt(modelRow, 1);
                punt = new Point2D.Float(px,py);            
            }catch(Exception e){
                log.debug("error getting selected point coordinates");
            }
            selpunts[i]=punt;
        }
        return selpunts;
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
        return (Integer)spinner.getValue();
    }
    
    
    public ArrayList<Patt2Dzone> getPkinteg() {
        return pkinteg;
    }

    protected void do_btnClose_actionPerformed(ActionEvent e) {
        this.dispose();
    }
    
    protected void do_table_keyReleased(KeyEvent e) {
        if (e.getKeyCode()==KeyEvent.VK_DELETE){
            if (this.getSelectedPeaks()!=null){
                Point2D.Float[] selpunts = this.getSelectedPeaks();
                for (int i=0;i<selpunts.length;i++){
                    log.writeNameNums("CONFIG", true, "remove peak",selpunts[i].x,selpunts[i].y);
                    this.removePeak(selpunts[i]);
                }
            }
        }
    }
    
    public void removePeak(Point2D.Float pk){
        int index = patt2d.getPkSearchResult().indexOf(pk);
        log.config("removePeak at index="+index);
        if (index<0)return;
        patt2d.getPkSearchResult().remove(index);
        this.pkinteg.remove(index);
        this.updateTable();
    }
    
    public Point2D.Float findNearestPeak(Point2D.Float pixel, float maxDistToConsider){
        Iterator<Point2D.Float> itrp = patt2d.getPkSearchResult().iterator();
        Point2D.Float closest = null;
        double minDist = maxDistToConsider +1;
        while (itrp.hasNext()){
            Point2D.Float p = itrp.next();
            double dist = p.distance(pixel);
            log.writeNameNums("FINE", true, "p(list) pixel", p.x,p.y,pixel.x,pixel.y);
            log.fine("dist="+Double.toString(dist));
            if ((dist<maxDistToConsider)){
                if (dist<minDist){
                    minDist = dist;
                    closest = p;
                }
            }
        }
        return closest;
    }
    protected void do_btnExportPeakList_actionPerformed(ActionEvent arg0) {
        File f = FileUtils.fchooserSaveAsk(this,new File(D2Dplot_global.workdir), null);
        if (f==null) return;
        D2Dplot_global.setWorkdir(f);
        // creem un printwriter amb el fitxer file (el que estem escribint)
        try {
            PrintWriter output = new PrintWriter(new BufferedWriter(new FileWriter(f)));
            // ESCRIBIM AL FITXER:
            String head = String.format("%5s %5s %10s %8s %5s %8s %8s %8s %5s", "XPix","YPix","FH2","s(FH2)","Npix","Ymax","Ymean","Ybkg","SDbkg");
            output.println(head);
            
            for (int i=0; i<table.getRowCount(); i++){
                int modelRow = table.convertRowIndexToModel(i);
                //"XPix  YPix  FH2, "sigma(FH2)", "Npix", "Ymax", "Ymean", "Ybkg", "SDbkg"
                
                int px = (Integer)table.getModel().getValueAt(modelRow, 0);
                int py = (Integer)table.getModel().getValueAt(modelRow, 1);
                float fh2 = (Float)table.getModel().getValueAt(modelRow, 2);
                float sfh2 = (Float)table.getModel().getValueAt(modelRow, 3);
                int npix = (Integer)table.getModel().getValueAt(modelRow, 4);
                int ymax = (Integer)table.getModel().getValueAt(modelRow, 5);
                float ymean = (Float)table.getModel().getValueAt(modelRow, 6);
                float ybkg = (Float)table.getModel().getValueAt(modelRow, 7);
                float sdbkg = (Float)table.getModel().getValueAt(modelRow, 8);
                String pr = String.format("%5d %5d %10.2f %8.2f %5d %8d %8.2f %8.2f %5.2f", px,py,fh2,sfh2,npix,ymax,ymean,ybkg,sdbkg);
                output.println(pr);
            }
            
            output.close();
            
        } catch (Exception e) {
            if(D2Dplot_global.isDebug())e.printStackTrace();
            log.warning("Error writting the file");
        }
    }
}
