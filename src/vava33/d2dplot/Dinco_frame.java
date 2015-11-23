package vava33.d2dplot;

import java.awt.Color;
import java.awt.Component;
import java.awt.Toolkit;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.filechooser.FileNameExtensionFilter;

import net.miginfocom.swing.MigLayout;

import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JLabel;
import javax.swing.ListCellRenderer;

import vava33.d2dplot.auxi.ImgFileUtils;
import vava33.d2dplot.auxi.OrientSolucio;
import vava33.d2dplot.auxi.Pattern2D;
import vava33.d2dplot.auxi.PuntSolucio;

import com.vava33.jutils.FileUtils;
import com.vava33.jutils.VavaLogger;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.Collections;
import java.util.Iterator;
import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;

import javax.swing.event.ListSelectionListener;
import javax.swing.JSplitPane;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class Dinco_frame extends JFrame {

    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    private JCheckBox chckbxOnTop;
    private JList listSol;
    private JList listEdit;
    private JCheckBox chckbxAddPeaks;
    private VavaLogger log = D2Dplot_global.log;
    private JButton btnExtractIntensities;
    private JCheckBox chckbxShowHkl;
    private JCheckBox chckbxShowSpots;
    private JSplitPane splitPane;
    private JPanel panel;
    private JPanel panel_1;

    private Pattern2D patt2D;
    private ImagePanel ip;
    
    /**
     * Create the frame.
     */
    public Dinco_frame(ImagePanel ip) {
        setTitle("DINCO");
        setIconImage(Toolkit.getDefaultToolkit().getImage(Dinco_frame.class.getResource("/img/Icona.png")));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setBounds(100, 100, 450, 440);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(new MigLayout("fill, insets 5", "[][][grow]", "[][grow][]"));
        
        JButton btnLoadSol = new JButton("Load PXY/SOL file");
        btnLoadSol.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                do_btnLoadSol_actionPerformed(e);
            }
        });
        contentPane.add(btnLoadSol, "cell 0 0 2 1");
        
        chckbxOnTop = new JCheckBox("on top");
        chckbxOnTop.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                do_chckbxOnTop_itemStateChanged(e);
            }
        });
        chckbxOnTop.setSelected(true);
        contentPane.add(chckbxOnTop, "cell 2 0,alignx right");
        
        splitPane = new JSplitPane();
        splitPane.setResizeWeight(0.5);
        splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
        contentPane.add(splitPane, "cell 0 1 3 1,grow");
        
        panel = new JPanel();
        splitPane.setRightComponent(panel);
        panel.setLayout(new MigLayout("fill, insets 5", "[grow]", "[][grow]"));
        
        JLabel lblPeakList = new JLabel("Peak List:");
        panel.add(lblPeakList, "cell 0 0");
        lblPeakList.setToolTipText("");
        
        JScrollPane scrollPane_1 = new JScrollPane();
        panel.add(scrollPane_1, "cell 0 1,grow");
        
        listEdit = new JList();
        ListCellRenderer renderer = new PointSolutionRenderer();
        listEdit.setCellRenderer(renderer);
        
        scrollPane_1.setViewportView(listEdit);
        
        panel_1 = new JPanel();
        splitPane.setLeftComponent(panel_1);
        panel_1.setLayout(new MigLayout("fill, insets 5", "[grow][grow][grow]", "[][grow][]"));
        
        JLabel lblSolutionList = new JLabel("Solution list (solNum numOfRef sumFunc):");
        panel_1.add(lblSolutionList, "cell 0 0 3 1");
        
        JScrollPane scrollPane = new JScrollPane();
        panel_1.add(scrollPane, "cell 0 1 3 1,grow");
        
        listSol = new JList();
        listSol.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                do_listSol_keyReleased(e);
            }
        });
        listSol.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent arg0) {
                do_listSol_valueChanged(arg0);
            }
        });
        scrollPane.setViewportView(listSol);

        chckbxShowSpots = new JCheckBox("Show Spots");
        panel_1.add(chckbxShowSpots, "cell 0 2");
        chckbxShowSpots.setSelected(true);

        chckbxShowHkl = new JCheckBox("Show HKL");
        panel_1.add(chckbxShowHkl, "cell 1 2");

        chckbxAddPeaks = new JCheckBox("Add Peaks");
        panel_1.add(chckbxAddPeaks, "cell 2 2");
        chckbxAddPeaks.setToolTipText("Add missing peaks not found automatically (e.g. weak spots)");

        btnExtractIntensities = new JButton("Extract Intensities");
        btnExtractIntensities.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                do_btnExtractIntensities_actionPerformed(arg0);
            }
        });
        contentPane.add(btnExtractIntensities, "cell 0 2 2 1");
        
        JButton btnClose = new JButton("Close");
        btnClose.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                do_btnClose_actionPerformed(e);
            }
        });
        contentPane.add(btnClose, "cell 2 2,alignx right");
        
        this.ip=ip;
        this.setPatt2D(ip.getPatt2D());
        this.inicia();
    }
    
    private void inicia(){
        this.setAlwaysOnTop(chckbxOnTop.isSelected());
        this.addSolutionsToList();
        this.loadPeakList();
    }
    
    @Override
    public void dispose() {
        this.chckbxShowSpots.setSelected(false);
        this.chckbxShowHkl.setSelected(false);
        this.chckbxAddPeaks.setSelected(false);
        super.dispose();
    }
    
    protected void do_btnClose_actionPerformed(ActionEvent e) {
        this.dispose();
    }
    
    protected void do_btnLoadSol_actionPerformed(ActionEvent e) {
        FileNameExtensionFilter[] filter = new FileNameExtensionFilter[2];
        filter[0] = new FileNameExtensionFilter("DINCO SOL/PXY files (*.SOL *.PXY)","SOL","sol","PXY","pxy");
        filter[1] = new FileNameExtensionFilter("XDS files (SPOT.XDS)","XDS","xds");
        File fsol = FileUtils.fchooserOpen(this,new File(D2Dplot_global.workdir), filter, 0);
        if (fsol!=null){
            this.getPatt2D().clearSolutions();
            if (FileUtils.getExtension(fsol).equalsIgnoreCase("XDS")){
                fsol = ImgFileUtils.readXDS(fsol,patt2D);
            }else{
                fsol = ImgFileUtils.readSOL(fsol,patt2D);
            }
            if (fsol == null) {
                log.debug("No SOL file opened");
            }else{
                this.addSolutionsToList();
            }
            D2Dplot_global.setWorkdir(fsol);
        }
    }
    
    public void openSOL(){
        FileNameExtensionFilter[] filter = new FileNameExtensionFilter[1];
        filter[0] = new FileNameExtensionFilter("DINCO SOL/PXY files (*.SOL *.PXY)","SOL","sol","PXY","pxy");
        File fsol = FileUtils.fchooserOpen(this,new File(D2Dplot_global.workdir), filter, 0);
        if (fsol!=null){
            this.getPatt2D().clearSolutions();
            fsol = ImgFileUtils.readSOL(fsol, this.getPatt2D());
            if (fsol == null) {
                log.debug("No SOL file opened");
            }else{
                this.addSolutionsToList();
            }
            D2Dplot_global.setWorkdir(fsol);
        }
    }
    
    public void openXDS(){
        FileNameExtensionFilter[] filter = new FileNameExtensionFilter[1];
        filter[0] = new FileNameExtensionFilter("XDS files (SPOT.XDS)","XDS","xds");
        File fsol = FileUtils.fchooserOpen(this,new File(D2Dplot_global.workdir), filter, 0);
        if (fsol!=null){
            this.getPatt2D().clearSolutions();
            fsol = ImgFileUtils.readXDS(fsol, this.getPatt2D());
            if (fsol == null) {
                log.debug("No XDS file opened");
            }else{
                this.addSolutionsToList();
            }
            D2Dplot_global.setWorkdir(fsol);
        }
    }
    
    private void addSolutionsToList(){
        // afegim les solucions a la llista
        DefaultListModel lm = new DefaultListModel();
        // ordenem arraylist
        Collections.sort(this.getPatt2D().getSolucions(),Collections.reverseOrder());
        Iterator<OrientSolucio> iteros = this.getPatt2D().getSolucions().iterator();
        while (iteros.hasNext()){
            lm.addElement(iteros.next());
        }
        
        this.listSol.setModel(lm);
        this.listSol.setSelectedIndex(0);
        this.loadPeakList();
    }
    
    public void setXDSMode(){
        //desactivem components
        btnExtractIntensities.setVisible(false);
        btnExtractIntensities.setVisible(false);
    }
    
    public void setSOLMode(){
        //activem components
        btnExtractIntensities.setVisible(true); 
        btnExtractIntensities.setEnabled(true); 
    }

    public OrientSolucio getActiveOrientSol(){
        if (listSol.getSelectedValue()==null)return null;
        return (OrientSolucio) listSol.getSelectedValue();
    }
    
    public Object[] getActiveOrientSols(){
        if ((listSol.getSelectedValues()==null) || (listSol.getSelectedValues().length==0))return null;
        return listSol.getSelectedValues();
    }
    
    public void loadPeakList(){
        if (listSol.getSelectedValue()==null)return;

        DefaultListModel lm = new DefaultListModel();
        
        //mostrarem tots els punts de la solucio seleccionada
        OrientSolucio os = (OrientSolucio) listSol.getSelectedValue();
        Iterator<PuntSolucio> itrPS = os.getSol().iterator();
        while (itrPS.hasNext()) {
            PuntSolucio s = itrPS.next();
            //MOSTREM TOTS:
            lm.addElement(s);
        }
        listEdit.setModel(lm);
    }

    
    public Pattern2D getPatt2D() {
        return patt2D;
    }

    public void setPatt2D(Pattern2D patt2d) {
        patt2D = patt2d;
    }
    
    public boolean isAddPeaks(){
        return chckbxAddPeaks.isSelected();
    }
    public boolean isShowSpots(){
        return chckbxShowSpots.isSelected();
    }
    public boolean isShowHKL(){
        return chckbxShowHkl.isSelected();
    }
    
    protected void do_listSol_valueChanged(ListSelectionEvent arg0) {
        this.loadPeakList();
    }
    
    
    
    public boolean hasSolutionsLoaded(){
        if (listSol.getModel().getSize()>0){
            return true;
        }else{
            return false;
        }
        
    }
    protected void do_listSol_keyReleased(KeyEvent e) {
        if (e.getKeyCode()==KeyEvent.VK_DELETE){
            if (getActiveOrientSols()!=null){
                Object[] oos = this.getActiveOrientSols();
                if (oos == null) return;
                for (int i=0;i<oos.length;i++){
                    OrientSolucio os = (OrientSolucio)oos[i];
                    this.getPatt2D().removeSolucio(os);
                }
                this.addSolutionsToList();
            }
        }
    }
    
    
    
    /**
     * Custom renderer to display in red the new added points
     */
    public class PointSolutionRenderer extends JLabel implements ListCellRenderer {
        
        /**
         * 
         */
        private static final long serialVersionUID = 6262600607370618116L;
        protected DefaultListCellRenderer defRenderer = new DefaultListCellRenderer();
        
        @Override
        public Component getListCellRendererComponent(JList list, Object value,
                int index, boolean isSelected, boolean cellHasFocus) {
            JLabel renderer = (JLabel) defRenderer.getListCellRendererComponent(list, value, index,
                    isSelected, cellHasFocus);  
            if (((PuntSolucio)value).isManuallyAdded()){
                renderer.setForeground(Color.red);
            }
            return renderer;
        }
    }
    protected void do_chckbxOnTop_itemStateChanged(ItemEvent e) {
        this.setAlwaysOnTop(chckbxOnTop.isSelected());
    }
    protected void do_btnExtractIntensities_actionPerformed(ActionEvent arg0) {
        PKsearch_frame pksframe = new PKsearch_frame(this.getPatt2D(),true,this);
        ip.setPKsearch(pksframe);
        pksframe.setVisible(true);
    }
}