package vava33.d2dplot;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.Toolkit;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.filechooser.FileNameExtensionFilter;

import net.miginfocom.swing.MigLayout;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JLabel;

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
import java.util.Locale;
import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;
import javax.swing.event.ListSelectionListener;

public class Dinco_frame extends JFrame {

    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    private JCheckBox chckbxOnTop;
    private JList listSol;
    private JList listEdit;
    private JCheckBox chckbxWorkSol;
    private Pattern2D patt2D;
    private VavaLogger log = D2Dplot_global.log;
    private JButton btnExtractIntensities;
    private JCheckBox chckbxShowHkl;
    private JCheckBox chckbxShowSpots;
    
    /**
     * Create the frame.
     */
    public Dinco_frame(Pattern2D patt) {
        setTitle("DINCO");
        setIconImage(Toolkit.getDefaultToolkit().getImage(Dinco_frame.class.getResource("/img/Icona.png")));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setBounds(100, 100, 450, 440);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(new MigLayout("fill, insets 5", "[][][grow]", "[][][grow][][][grow][]"));
        
        JButton btnLoadSol = new JButton("Load SOL file");
        btnLoadSol.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                do_btnLoadSol_actionPerformed(e);
            }
        });
        contentPane.add(btnLoadSol, "cell 0 0 2 1");
        
        chckbxOnTop = new JCheckBox("on top");
        chckbxOnTop.setSelected(true);
        contentPane.add(chckbxOnTop, "cell 2 0,alignx right");
        
        JLabel lblSolutionList = new JLabel("Solution list:");
        contentPane.add(lblSolutionList, "cell 0 1 3 1");
        
        JScrollPane scrollPane = new JScrollPane();
        contentPane.add(scrollPane, "cell 0 2 3 1,grow");
        
        listSol = new JList();
        listSol.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent arg0) {
                do_listSol_valueChanged(arg0);
            }
        });
        scrollPane.setViewportView(listSol);
        
        chckbxWorkSol = new JCheckBox("Work SOL");
//        chckbxWorkSol.addItemListener(new ItemListener() {
//            public void itemStateChanged(ItemEvent e) {
//                do_chckbxWorkSol_itemStateChanged(e);
//            }
//        });
        
        chckbxShowSpots = new JCheckBox("Show Spots");
        chckbxShowSpots.setSelected(true);
        contentPane.add(chckbxShowSpots, "cell 0 3");
        
        chckbxShowHkl = new JCheckBox("Show HKL");
        contentPane.add(chckbxShowHkl, "cell 1 3");
        contentPane.add(chckbxWorkSol, "cell 2 3");
        
        JLabel lblEditedReflectionList = new JLabel("Edited (work SOL) ref. list:");
        lblEditedReflectionList.setToolTipText("Useful for fine pixel adjustments prior to integration?");
        contentPane.add(lblEditedReflectionList, "cell 0 4 3 1");
        
        JScrollPane scrollPane_1 = new JScrollPane();
        contentPane.add(scrollPane_1, "cell 0 5 3 1,grow");
        
        listEdit = new JList();
        scrollPane_1.setViewportView(listEdit);
        
        btnExtractIntensities = new JButton("Extract Intensities");
        contentPane.add(btnExtractIntensities, "cell 0 6 2 1");
        
        JButton btnClose = new JButton("Close");
        btnClose.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                do_btnClose_actionPerformed(e);
            }
        });
        contentPane.add(btnClose, "cell 2 6,alignx right");
        
        this.setPatt2D(patt);
        this.inicia();
    }
    
    private void inicia(){
        this.setAlwaysOnTop(chckbxOnTop.isSelected());
        this.addSolutionsToList();
        this.loadEditPeakList();
    }
    
    @Override
    public void dispose() {
        this.chckbxShowSpots.setSelected(false);
        this.chckbxShowHkl.setSelected(false);
        this.chckbxWorkSol.setSelected(false);
        super.dispose();
    }
    
    protected void do_btnClose_actionPerformed(ActionEvent e) {
        this.dispose();
    }
    
    protected void do_btnLoadSol_actionPerformed(ActionEvent e) {
        FileNameExtensionFilter[] filter = new FileNameExtensionFilter[2];
        filter[0] = new FileNameExtensionFilter("DINCO SOL files (*.SOL)","SOL","sol");
        filter[1] = new FileNameExtensionFilter("XDS files (SPOT.XDS)","XDS","xds");
        File fsol = FileUtils.fchooserOpen(new File(D2Dplot_global.workdir), filter, 0);
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
        }
    }
    
    public void openSOL(){
        FileNameExtensionFilter[] filter = new FileNameExtensionFilter[1];
        filter[0] = new FileNameExtensionFilter("DINCO SOL files (*.SOL)","SOL","sol");
        File fsol = FileUtils.fchooserOpen(new File(D2Dplot_global.workdir), filter, 0);
        if (fsol!=null){
            this.getPatt2D().clearSolutions();
            fsol = ImgFileUtils.readSOL(fsol, this.getPatt2D());
            if (fsol == null) {
                log.debug("No SOL file opened");
            }else{
                this.addSolutionsToList();
            }
        }
    }
    
    public void openXDS(){
        FileNameExtensionFilter[] filter = new FileNameExtensionFilter[1];
        filter[0] = new FileNameExtensionFilter("XDS files (SPOT.XDS)","XDS","xds");
        File fsol = FileUtils.fchooserOpen(new File(D2Dplot_global.workdir), filter, 0);
        if (fsol!=null){
            this.getPatt2D().clearSolutions();
            fsol = ImgFileUtils.readXDS(fsol, this.getPatt2D());
            if (fsol == null) {
                log.debug("No XDS file opened");
            }else{
                this.addSolutionsToList();
            }
        }
    }
    
    private void addSolutionsToList(){
        // afegim les solucions a la llista
        DefaultListModel lm = new DefaultListModel();
        // ordenem arraylist
        Collections.sort(this.getPatt2D().getSolucions(),Collections.reverseOrder());
        for (int i = 0; i < this.getPatt2D().getSolucions().size(); i++) {
            lm.addElement((i + 1) + " " + this.getPatt2D().getSolucions().get(i).getNumReflexions() + " "
                    + this.getPatt2D().getSolucions().get(i).getValorFrot());
        }
        this.listSol.setModel(lm);
        this.listSol.setSelectedIndex(0);
    }
    
    public void setXDSMode(){
        //desactivem components
        btnExtractIntensities.setVisible(false);
        btnExtractIntensities.setVisible(false);
    }
    
    public void setSOLMode(){
        //activem components
        btnExtractIntensities.setVisible(true); //TODO ACTIVAR QUAN SIGUI NECESSARI
        btnExtractIntensities.setEnabled(false); //TODO ACTIVAR QUAN SIGUI NECESSARI
    }

    public void loadEditPeakList(){
//        if (listEdit.getModel()==null){
//            listEdit.setModel(new DefaultListModel());
//        }
//        DefaultListModel lm = (DefaultListModel)listEdit.getModel();
        DefaultListModel lm = new DefaultListModel();
        
        //mostrem a la llista els punts HKL que s'han modificat les coordenades clic
        Iterator<OrientSolucio> itrOS = this.getPatt2D().getSolucions().iterator();
        OrientSolucio os = null;
        while (itrOS.hasNext()) {
            os = itrOS.next();
            if (os.isShowSol()) {
                break;
            }
        }
        if(os==null)return;
        Iterator<PuntSolucio> itrPS = os.getSol().iterator();
        while (itrPS.hasNext()) {
            PuntSolucio s = itrPS.next();
            if(s.getCoordXclic()>0){
                //mostrem a la llista
                String entry = String.format(Locale.ENGLISH, " %s  %8.2f %8.2f  %5d", 
                        s.getHKLspaces(),s.getCoordXclic(),s.getCoordYclic(),(int)s.getIntenClic());
                if(!lm.contains(entry)){
                    lm.addElement(entry);   
                }
            }
        }

        listEdit.setModel(lm);
    }

    
//    protected void do_chckbxWorkSol_itemStateChanged(ItemEvent e) {
//        if(chckbxWorkSol.isSelected()){
//            chckbxIndex.setSelected(false);
//        }
//        panelImatge.setShowHKLIndexing(chckbxWorkSol.isSelected());
//        this.btnSaveDicvol.setEnabled(chckbxWorkSol.isSelected());
//        log.debug("panelImatge.isShowIndexing="+panelImatge.isShowIndexing());
//        log.debug("panelImatge.isShowHKLIndexing="+panelImatge.isShowHKLIndexing());
//    }
    
    public Pattern2D getPatt2D() {
        return patt2D;
    }

    public void setPatt2D(Pattern2D patt2d) {
        patt2D = patt2d;
    }
    
    public boolean isWorkSOL(){
        return chckbxWorkSol.isSelected();
    }
    public boolean isShowSpots(){
        return chckbxShowSpots.isSelected();
    }
    public boolean isShowHKL(){
        return chckbxShowHkl.isSelected();
    }
    
    protected void do_listSol_valueChanged(ListSelectionEvent arg0) {
       for (int i = 0; i < this.getPatt2D().getSolucions().size(); i++) {
          this.getPatt2D().getSolucions().get(i).setShowSol(listSol.isSelectedIndex(i));
       }
    }
}