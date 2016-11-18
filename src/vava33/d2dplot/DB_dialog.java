package vava33.d2dplot;
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
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.ProgressMonitor;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.vava33.jutils.FileUtils;
import com.vava33.jutils.LogJTextArea;
import com.vava33.jutils.VavaLogger;

import vava33.d2dplot.auxi.FilteredListModel;
import vava33.d2dplot.auxi.PDCompound;
import vava33.d2dplot.auxi.PDDatabase;
import vava33.d2dplot.auxi.PDSearchResult;
import vava33.d2dplot.auxi.Pattern2D;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.JProgressBar;

import net.miginfocom.swing.MigLayout;

public class DB_dialog extends JFrame {

    private static final long serialVersionUID = -6104927797410689910L;
    private static float minDspacingToSearch = 1.15f;
    private static final int maxNsol = 50;

    private JButton btnLoadDB;
    private JCheckBox cbox_onTop;
    private JCheckBox chckbxPDdata;
    private final JPanel contentPanel = new JPanel();
    private JLabel lblHelp;
    private JList listCompounds;
    private JPanel panel_left;
    private JPanel panel_right;
    private JScrollPane scrollPane;
    private JScrollPane scrollPane_1;
    private JSplitPane splitPane;
    private static LogJTextArea tAOut;
    
    private DefaultListModel lm;
    private boolean showPDDataRings;
    private JButton btnShowDsp;
    private static JProgressBar pBarDB;
    private ProgressMonitor pm;
    private PDDatabase.openDBfileWorker openDBFwk;
    private PDDatabase.saveDBfileWorker saveDBFwk;
    private PDDatabase.searchDBWorker searchDBwk;
    private JButton btnSearchByPeaks;
    private JCheckBox chckbxIntensityInfo;
    private JCheckBox chckbxNameFilter;
    private JTextField txtNamefilter;
    private JCheckBox chckbxNpksInfo;
    
    private JButton btnResetSearch;
    private JButton btnSaveDb;
    private JLabel lblHeader;
    private JPanel panel;
    private JPanel panel_1;
    private JPanel panel_2;
    private JButton btnAddCompound;
    private JButton btnEditCompound;
    private static VavaLogger log = D2Dplot_global.getVavaLogger(DB_dialog.class.getName());

    private Pattern2D patt2d;
    private ImagePanel ipanel;
    private JButton btnAddToQuicklist;
    
    /**
     * Create the dialog.
     */
    public DB_dialog(ImagePanel ip) {
        this.setIpanel(ip);
        
        setIconImage(Toolkit.getDefaultToolkit().getImage(DB_dialog.class.getResource("/img/Icona.png")));
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Compound DB");
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        int width = 660;
        int height = 730;
        int x = (screen.width - width) / 2;
        int y = (screen.height - height) / 2;
        setBounds(x, y, 680, 680);
        getContentPane().setLayout(new MigLayout("fill, insets 5", "[grow]", "[grow][37px]"));
        getContentPane().add(this.contentPanel, "cell 0 0,grow");
        contentPanel.setLayout(new MigLayout("fill, insets 0", "[grow]", "[598px,grow]"));
        {
            this.splitPane = new JSplitPane();
            splitPane.setResizeWeight(0.7);
            this.splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
            contentPanel.add(this.splitPane, "cell 0 0,grow");
            {
                this.panel_left = new JPanel();
                this.splitPane.setLeftComponent(this.panel_left);
                {
                    {
                        {
                            panel_left.setLayout(new MigLayout("fill, insets 0", "[grow]", "[25px][grow][]"));
                            {
                                panel = new JPanel();
                                panel_left.add(panel, "cell 0 0,grow");
                                panel.setLayout(new MigLayout("fill, insets 0", "[][][][][grow]", "[][][]"));
                                this.btnLoadDB = new JButton("Load DB");
                                panel.add(btnLoadDB, "cell 0 0,growx,aligny center");
                                {
                                    btnSaveDb = new JButton("Save DB");
                                    panel.add(btnSaveDb, "cell 1 0,alignx center,aligny center");
                                    this.chckbxPDdata = new JCheckBox("ShowRings");
                                    panel.add(chckbxPDdata, "cell 2 0,alignx left,aligny center");
                                    this.chckbxPDdata.addItemListener(new ItemListener() {
                                        @Override
                                        public void itemStateChanged(ItemEvent arg0) {
                                            do_chckbxCalibrate_itemStateChanged(arg0);
                                        }
                                    });
                                    this.chckbxPDdata.setSelected(true);
                                    this.cbox_onTop = new JCheckBox("on top");
                                    panel.add(cbox_onTop, "cell 4 0,alignx right,aligny center");
                                    this.cbox_onTop.setHorizontalTextPosition(SwingConstants.LEADING);
                                    this.cbox_onTop.addItemListener(new ItemListener() {
                                        @Override
                                        public void itemStateChanged(ItemEvent arg0) {
                                            do_cbox_onTop_itemStateChanged(arg0);
                                        }
                                    });
                                    this.cbox_onTop.setActionCommand("on top");
                                    {
                                        btnSearchByPeaks = new JButton("Search by peaks");
                                        panel.add(btnSearchByPeaks, "cell 0 1 2 1,growx,aligny center");
                                        {
                                            chckbxIntensityInfo = new JCheckBox("Intensity info");
                                            chckbxIntensityInfo.addItemListener(new ItemListener() {
                                                public void itemStateChanged(ItemEvent e) {
                                                    do_chckbxIntensityInfo_itemStateChanged(e);
                                                }
                                            });
                                            panel.add(chckbxIntensityInfo, "hidemode 3,cell 3 1,alignx left,aligny center");
                                        }
                                        this.lblHelp = new JLabel("?");
                                        panel.add(lblHelp, "cell 4 1,alignx right,aligny center");
                                        this.lblHelp.addMouseListener(new MouseAdapter() {
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
                                        this.lblHelp.setFont(new Font("Tahoma", Font.BOLD, 14));
                                        {
                                            chckbxNameFilter = new JCheckBox("Apply name filter:");
                                            panel.add(chckbxNameFilter, "cell 0 2 2 1,alignx right,aligny center");
                                        }
                                        {
                                            txtNamefilter = new JTextField();
                                            panel.add(txtNamefilter, "cell 2 2 3 1,growx,aligny center");
                                            txtNamefilter.setColumns(10);
                                            {
                                                chckbxNpksInfo = new JCheckBox("Use nr. of total reflections");
                                                chckbxNpksInfo.addItemListener(new ItemListener() {
                                                    public void itemStateChanged(ItemEvent arg0) {
                                                        do_chckbxNpksInfo_itemStateChanged(arg0);
                                                    }
                                                });
                                                panel.add(chckbxNpksInfo, "hidemode 3,cell 2 1,alignx left,aligny center");
                                                chckbxNpksInfo.setSelected(true);
                                            }
                                            
                                            txtNamefilter.getDocument().addDocumentListener(new DocumentListener() {
                                                public void changedUpdate(DocumentEvent e) {
                                                    filterList();
                                                  }
                                                  public void removeUpdate(DocumentEvent e) {
                                                    filterList();
                                                  }
                                                  public void insertUpdate(DocumentEvent e) {
                                                    filterList();
                                                  }
                                                });
                                        }
                                        btnSearchByPeaks.addActionListener(new ActionListener() {
                                            public void actionPerformed(ActionEvent e) {
                                                do_btnSearchByPeaks_actionPerformed(e);
                                            }
                                        });
                                    }
                                    btnSaveDb.addActionListener(new ActionListener() {
                                        public void actionPerformed(ActionEvent arg0) {
                                            do_btnSaveDb_actionPerformed(arg0);
                                        }
                                    });
                                }
                                this.btnLoadDB.addActionListener(new ActionListener() {
                                    @Override
                                    public void actionPerformed(ActionEvent arg0) {
                                        do_btnLoadDB_actionPerformed(arg0,true);
                                    }
                                });
                            }
                        }
                    }
                }
                {
                    panel_1 = new JPanel();
                    panel_left.add(panel_1, "cell 0 1,grow");
                    panel_1.setLayout(new MigLayout("fill, insets 0", "[grow][]", "[][grow][]"));
                    {
                        lblHeader = new JLabel("");
                        panel_1.add(lblHeader, "cell 0 0 2 1");
                    }
                    {
                        this.scrollPane = new JScrollPane();
                        panel_1.add(scrollPane, "cell 0 1 2 1,grow");
                        {
                            this.listCompounds = new JList();
                            listCompounds.setFont(new Font("Monospaced", Font.PLAIN, 15));
                            listCompounds.addListSelectionListener(new ListSelectionListener() {
                                public void valueChanged(ListSelectionEvent arg0) {
                                    do_listCompounds_valueChanged(arg0);
                                }
                            });
                            this.listCompounds.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
                            this.scrollPane.setViewportView(this.listCompounds);
                        }
                    }
                    {
                        pBarDB = new JProgressBar();
                        panel_1.add(pBarDB, "cell 0 2,growx");
                        pBarDB.setStringPainted(true);
                    }
                    btnResetSearch = new JButton("reset list");
                    panel_1.add(btnResetSearch, "cell 1 2");
                    btnResetSearch.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent arg0) {
                            do_btnResetSearch_actionPerformed(arg0);
                        }
                    });
                }
                {
                }
            }
            {
                this.panel_right = new JPanel();
                this.panel_right.setBackground(Color.BLACK);
                this.splitPane.setRightComponent(this.panel_right);
                panel_right.setLayout(new MigLayout("fill, insets 5", "[grow]", "[grow]"));
                {
                    this.scrollPane_1 = new JScrollPane();
                    scrollPane_1.setViewportBorder(null);
                    this.scrollPane_1.setBorder(null);
                    this.panel_right.add(this.scrollPane_1, "cell 0 0,grow");
                    {
                        DB_dialog.tAOut = new LogJTextArea();
                        DB_dialog.tAOut.setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));
                        DB_dialog.tAOut.setWrapStyleWord(true);
                        DB_dialog.tAOut.setLineWrap(true);
                        DB_dialog.tAOut.setEditable(false);
                        this.scrollPane_1.setViewportView(DB_dialog.tAOut);
                    }
                }
            }
        }
        {
            JPanel buttonPane = new JPanel();
            getContentPane().add(buttonPane, "cell 0 1,growx,aligny top");
            JButton okButton = new JButton("close");
            okButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent arg0) {
                    do_okButton_actionPerformed(arg0);
                }
            });
            {
                buttonPane.setLayout(new MigLayout("fill, insets 0", "[][]", "[]"));
            }
            okButton.setActionCommand("OK");
            buttonPane.add(okButton, "cell 1 0,alignx right,aligny center");
            getRootPane().setDefaultButton(okButton);
        }
        {
            {
                panel_2 = new JPanel();
                panel_left.add(panel_2, "cell 0 2,grow");
                panel_2.setLayout(new MigLayout("fill, insets 0", "[][][][]", "[]"));
                {
                    btnAddCompound = new JButton("Add Compound");
                    btnAddCompound.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent arg0) {
                            do_btnAddCompound_actionPerformed(arg0);
                        }
                    });
                    panel_2.add(btnAddCompound, "cell 0 0,alignx center");
                }
                {
                    btnEditCompound = new JButton("Edit Compound");
                    btnEditCompound.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent e) {
                            do_btnEditCompound_actionPerformed(e);
                        }
                    });
                    panel_2.add(btnEditCompound, "cell 1 0,alignx center");
                }
                btnShowDsp = new JButton("Compound info");
                panel_2.add(btnShowDsp, "cell 2 0,alignx center,aligny center");
                {
                    btnAddToQuicklist = new JButton("Add to Quicklist");
                    btnAddToQuicklist.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent e) {
                            do_btnAddToQuicklist_actionPerformed(e);
                        }
                    });
                    panel_2.add(btnAddToQuicklist, "cell 3 0,alignx center");
                }
                btnShowDsp.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent arg0) {
                        do_btnShowDsp_actionPerformed(arg0);
                    }
                });
            }
        }
        this.setAlwaysOnTop(cbox_onTop.isSelected());
        tAOut.ln("** PDDatabase **");
        this.inicia();
    }

    @Override
    public void dispose() {
        this.chckbxPDdata.setSelected(false);
        super.dispose();
    }

    public void inicia(){
        this.setPatt2d(this.getIpanel().getPatt2D());
        lm = new DefaultListModel();
        listCompounds.setModel(lm);
        tAOut.ln(" Reading default database: "+PDDatabase.getDefaultDBpath());
        do_btnLoadDB_actionPerformed(null,false);
    }
   
    //boolean ask for default or not
    protected void do_btnLoadDB_actionPerformed(ActionEvent arg0,boolean askIfDefaultDB) {
        
        //primer creem el progress monitor, 
        pm = new ProgressMonitor(this,
                "Reading DB file...",
                "", 0, 100);
        pm.setProgress(0);
        pBarDB.setString("Reading DB");
        pBarDB.setStringPainted(true);
        
        //db per defecte:
        File DBFile = new File(PDDatabase.getDefaultDBpath());
        
        //Ara preguntem (si s'escau) sobre obrir una externa o la default
        if (askIfDefaultDB){
            Object[] options = {"Load default DB",
            "Open external DB file"};
            int n = JOptionPane.showOptionDialog(this,
            "Open default DB or one from a file?",
            "Load DB",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE,
            null,
            options,
            options[0]);
            
            log.debug("option = "+n);
            
            if (n == JOptionPane.CLOSED_OPTION) {
                return;
            }
            if (n != JOptionPane.YES_OPTION) {
                //Carrega un fitxer de base de dades
                FileNameExtensionFilter[] filter = {new FileNameExtensionFilter("DB file (db,txt,dat)", "db", "txt", "dat")};
                DBFile = FileUtils.fchooserOpen(this,new File(D2Dplot_global.getWorkdir()), filter, 0);
                if(DBFile==null){
                    tAOut.stat("No data file selected");
                    return;
                }
                D2Dplot_global.setWorkdir(DBFile);
            }
        }
        
        //Si hem arribat aquí creem el worker, hi afegim el listener
        openDBFwk = new PDDatabase.openDBfileWorker(DBFile,false);
        openDBFwk.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
              //log.debug("hello from propertyChange");
              log.debug(evt.getPropertyName());
              if ("progress" == evt.getPropertyName() ) {
                  int progress = (Integer) evt.getNewValue();
                  pm.setProgress(progress);
                  pBarDB.setValue(progress);
                  pm.setNote(String.format("%d%%\n", progress));
                  if (pm.isCanceled() || openDBFwk.isDone()) {
                      Toolkit.getDefaultToolkit().beep();
                      if (pm.isCanceled()) {
                          openDBFwk.cancel(true);
                          tAOut.stat("reading of DB file "+openDBFwk.getReadedFile()+" stopped!");
                          tAOut.stat("Number of compounds = "+PDDatabase.getnCompounds());    
                      } else {
                          tAOut.stat("reading of DB file "+openDBFwk.getReadedFile()+" finished!");
                          tAOut.stat("Number of compounds = "+PDDatabase.getnCompounds());    
                      }
                      pm.close();
                      pBarDB.setValue(100);
                      pBarDB.setStringPainted(true);
                      updateListAllCompounds();
                      //startButton.setEnabled(true);
                  }
              }
            }
        });
        
        //reset current Database
        PDDatabase.resetDB();
        //read database file, executing the swingworker task
        openDBFwk.execute();
    }

    protected void do_cbox_onTop_itemStateChanged(ItemEvent arg0) {
        this.setAlwaysOnTop(cbox_onTop.isSelected());
    }

    protected void do_chckbxCalibrate_itemStateChanged(ItemEvent arg0) {
        this.showPDDataRings = chckbxPDdata.isSelected();
        this.getIpanel().setShowDBCompoundRings(this.isShowDataRings(), this.getCurrentCompound());
    }

    protected void do_lbllist_mouseEntered(MouseEvent e) {
        // lbllist.setText("<html><font color='red'>?</font></html>");
        lblHelp.setForeground(Color.red);
    }

    protected void do_lbllist_mouseExited(MouseEvent e) {
        lblHelp.setForeground(Color.black);
    }

    protected void do_lbllist_mouseReleased(MouseEvent e) {
        tAOut.ln("");
        tAOut.ln("** General help **");
        tAOut.ln(" - Click on a compound to see the rings on the image (if ShowRings is selected)");
        tAOut.ln(" - Check apply name filter and type to find the desired compound");
        tAOut.ln(" - Add/Edit compounds by clicking the respective buttons and filling the info. Alternatively you can edit manually the DB file "
                + "(which is a simple self-explanatory text file)");
        tAOut.ln(" - Add to QuickList (QL) to access the rings from the main window directly. Compounds in the QL are saved in a separate file "
                + "with the same format as the DB file and can also be edited the same way");
        tAOut.ln("** Search by peaks **");
        tAOut.ln(" - On the main window click on the desired rings so that they are selected in the point list (Sel.points should be active)");
        tAOut.ln(" - Click the button -search by peaks-");
        tAOut.ln(" - List will be updated by the best matching compounds (with respective residuals)");
        tAOut.ln(" - Click on the compounds to see the rings on top of your image and check if they really match");
        tAOut.ln("");
        tAOut.ln("Note:\n"
                + "The default DB is a small selection of compounds taken from different sources, mostly publications. Each entry contains the reference from "
                + "where it has been taken (with the respective authors) which can be retrieved by clicking -compound info- or by editing the compound."
                + "For any doubts/comments/complaints/suggestions, please contact the author\n");
        tAOut.ln("");
    }

    protected void do_okButton_actionPerformed(ActionEvent arg0) {
        this.dispose();
    }

    public PDCompound getCurrentCompound() {
        if (listCompounds == null){return null;}
        if (listCompounds.getSelectedIndex() >= 0) {
            if (listCompounds.getSelectedValue() instanceof PDCompound){
                PDCompound comp = (PDCompound) listCompounds.getSelectedValue();
                return comp;
            }
            if (listCompounds.getSelectedValue() instanceof PDSearchResult){
                PDSearchResult sr = (PDSearchResult) listCompounds.getSelectedValue();
                return sr.getC();
            }
        }
        return null;
    }
    

    public boolean isShowDataRings() {
        return showPDDataRings;
    }

    public void updateListAllCompounds() {
        pBarDB.setString("Updating DB");
        pBarDB.setStringPainted(true);
        lm.clear();
        Iterator<PDCompound> itrcomp = PDDatabase.getDBCompList().iterator();
        int n = 0;
        int ncomp = PDDatabase.getDBCompList().size();
        while (itrcomp.hasNext()){
            PDCompound c = (PDCompound) itrcomp.next();
            lm.addElement(c); 
            //progress
            if (n%100 == 0){
                pBarDB.setValue((int)(((float)n/(float)ncomp)/100.f));    
            }
        }
        lblHeader.setText(" RefNum  Name  [Formula]  (alt. names)");
        pBarDB.setValue(100);
        pBarDB.setStringPainted(false);
        
    }
    protected void do_btnShowDsp_actionPerformed(ActionEvent arg0) {
        PDCompound c = this.getCurrentCompound();
        if (c == null){
            tAOut.ln("Select a compound first");
            return;
        }
        tAOut.ln(c.printInfoMultipleLines());

        this.getIpanel().setShowDBCompoundRings(this.isShowDataRings(), c);
    }

    public boolean isShowPDDataRings() {
        return showPDDataRings;
    }

    public void setShowPDDataRings(boolean showPDDataRings) {
        this.showPDDataRings = showPDDataRings;
    }
    protected void do_listCompounds_valueChanged(ListSelectionEvent arg0) {
        if (arg0.getValueIsAdjusting()) return;
        PDCompound comp = this.getCurrentCompound();
        this.getIpanel().setShowDBCompoundRings(this.isShowDataRings(), comp);
        if (comp!=null)tAOut.ln(comp.printInfo2Line());
        this.getIpanel().actualitzarVista();
    }
    
    
    //CANVIEM PER NO UTILITZAR EL LM AMB TOTS ELS COMPOSTOS  -- AL FINAL NO, fem boto per tornar a mostrar tots
    public void loadSearchPeaksResults(){
        
        if (lm==null){return;}
        if (PDDatabase.getDBSearchresults().size()==0){return;}
        
        //aqui en principi tindrem una llista de resultats a PDDatabase i s'haurà de mostrar
        lm.clear();
        
        ArrayList<PDSearchResult> res = PDDatabase.getDBSearchresults();
        
        //mirem si hi ha criteris complementaris pel residual
        if (chckbxIntensityInfo.isSelected() || chckbxNpksInfo.isSelected()){
            Iterator<PDSearchResult> itrcomp = res.iterator();
            while (itrcomp.hasNext()){
                PDSearchResult c = itrcomp.next();
                float resid = c.getResidualPositions();
                if (chckbxIntensityInfo.isSelected()){
                    resid = resid + c.getResidual_intensities();
                }
                if (chckbxNpksInfo.isSelected()){
                    resid = resid * ((Math.max((float)c.getC().getNrRefUpToDspacing(PDSearchResult.getMinDSPin())/(float)PDSearchResult.getnDSPin(),1))/2);
                }
                c.setTotal_residual(resid);
            }            
        }
        Collections.sort(res);
        Iterator<PDSearchResult> itrcomp = res.iterator();
        int nsol = 0;
        while (itrcomp.hasNext()){
            if (nsol >= maxNsol) break;
            PDSearchResult c = itrcomp.next();
            lm.addElement(c);
            nsol = nsol + 1;
        }
        lblHeader.setText(" Residual  inputRefs/compoundRefs  CompoundName  [Formula]  (alt. names)");
        this.getIpanel().actualitzarVista();
    }
    
    public void searchPeaks(){
        
        pm = new ProgressMonitor(this,
                "Searching for peak matching...",
                "", 0, 100);
        pm.setProgress(0);
        
        pBarDB.setString("Searching DB");
        pBarDB.setStringPainted(true);
        
        searchDBwk = new PDDatabase.searchDBWorker(patt2d,minDspacingToSearch);
        searchDBwk.addPropertyChangeListener(new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent evt) {
              //log.debug("hello from propertyChange");
              log.debug(evt.getPropertyName());
              if ("progress" == evt.getPropertyName() ) {
                  int progress = (Integer) evt.getNewValue();
                  pm.setProgress(progress);
                  pBarDB.setValue(progress);
                  pm.setNote(String.format("%d%%\n", progress));
                  if (pm.isCanceled() || searchDBwk.isDone()) {
                      Toolkit.getDefaultToolkit().beep();
                      if (pm.isCanceled()) {
                          searchDBwk.cancel(true);
                          searchDBwk.setStop(true);
                          tAOut.stat("search cancelled");
                      } else {
                          tAOut.stat("search finished!");
                          loadSearchPeaksResults();
                      }
                      pm.close();
                      pBarDB.setValue(100);
                      pBarDB.setStringPainted(false);
                      //startButton.setEnabled(true);
                  }
              }
            }
        });
                
        searchDBwk.execute();
        
    }
    
    
    //la faig nova passant PuntsCercles al swingworker
    protected void do_btnSearchByPeaks_actionPerformed(ActionEvent e) {
        if (getPatt2d().getPuntsCercles().isEmpty()){
            tAOut.stat("Please select some peaks clicking in the image");
            return;
        }
        this.searchPeaks();
    }
    
    protected void filterList(){
        if (lm.isEmpty()){return;}
        if (chckbxNameFilter.isSelected()){
            //filter list
            FilteredListModel filteredListModel = new FilteredListModel(lm);
            listCompounds.setModel(filteredListModel);
            filteredListModel.setFilter(new FilteredListModel.Filter() {
                public boolean accept(Object element) {

                    PDCompound comp = null;
                    try{
                        comp = (PDCompound)element;    
                    }catch(Exception e){
                        log.debug("trying searchresult...");
                        comp = ((PDSearchResult)element).getC();
                    }
                    if (comp == null) return false;
                    
                    StringBuilder compinfo = new StringBuilder();
                    compinfo.append(comp.getCompName()).append(" ");
                    compinfo.append(comp.getFormula()).append(" ");
                    compinfo.append(comp.getAltNames()).append(" ");
                    compinfo.append(comp.getCellParameters()).append(" ");
                    compinfo.append(comp.getSpaceGroup()).append(" ");
                    compinfo.append(comp.getAllComments()).append(" ");
                    
                    String s = compinfo.toString().trim();
                    
                    String[] sMult = txtNamefilter.getText().split("\\s+");
                    for (int i=0; i<sMult.length; i++){
                        if (!FileUtils.containsIgnoreCase(s, sMult[i])){
                            return false;
                        }
                    }
                    return true;
                    
                }
            });
            if (txtNamefilter.getText().trim().length() == 0){
                listCompounds.setModel(lm);
                tAOut.stat("Number of compounds = "+lm.getSize());    
            }else{
                tAOut.stat("Number of (filtered) compounds = "+filteredListModel.getSize());    
            }
        }
        this.getIpanel().actualitzarVista();
        
    }
    protected void do_btnResetSearch_actionPerformed(ActionEvent arg0) {
        this.updateListAllCompounds();
        this.getIpanel().actualitzarVista();
    }
    protected void do_btnSaveDb_actionPerformed(ActionEvent arg0) {
        FileNameExtensionFilter[] filter = {new FileNameExtensionFilter("DB files", "db", "DB")};
        File f = FileUtils.fchooserSaveAsk(this, new File(D2Dplot_global.getWorkdir()),filter);
        if (f == null)return;
        D2Dplot_global.setWorkdir(f);
        //primer creem el progress monitor, 
        pm = new ProgressMonitor(this,
                "Saving DB file...",
                "", 0, 100);
        pm.setProgress(0);
        pBarDB.setString("Saving DB");
        pBarDB.setStringPainted(true);
        
        //Si hem arribat aquí creem el worker, hi afegim el listener
        saveDBFwk = new PDDatabase.saveDBfileWorker(f,false);
        saveDBFwk.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                log.debug(evt.getPropertyName());
                if ("progress" == evt.getPropertyName() ) {
                    int progress = (Integer) evt.getNewValue();
                    pm.setProgress(progress);
                    pBarDB.setValue(progress);
                    pm.setNote(String.format("%d%%\n", progress));
                    if (pm.isCanceled() || saveDBFwk.isDone()) {
                        Toolkit.getDefaultToolkit().beep();
                        if (pm.isCanceled()) {
                            saveDBFwk.cancel(true);
                            tAOut.stat("Error saving file "+saveDBFwk.getDbFileString());
                        } else {
                            tAOut.stat("DB saved to "+saveDBFwk.getDbFileString());
                        }
                        pm.close();
                        pBarDB.setValue(100);
                        pBarDB.setStringPainted(true);
                        updateListAllCompounds();
                    }
                }
            }
        });
        
        saveDBFwk.execute();
        
    }
    protected void do_btnAddCompound_actionPerformed(ActionEvent arg0) {
        DB_editor dbe = new DB_editor(null,this);
        dbe.setVisible(true);
    }
    protected void do_btnEditCompound_actionPerformed(ActionEvent e) {
        DB_editor dbe = new DB_editor(this.getCurrentCompound(),this);
        dbe.setVisible(true);
    }

    public Pattern2D getPatt2d() {
        return patt2d;
    }

    public void setPatt2d(Pattern2D patt2d) {
        this.patt2d = patt2d;
    }

    public ImagePanel getIpanel() {
        return ipanel;
    }

    public void setIpanel(ImagePanel ipanel) {
        this.ipanel = ipanel;
    }
    protected void do_chckbxNpksInfo_itemStateChanged(ItemEvent arg0) {
        this.loadSearchPeaksResults();
    }
    protected void do_chckbxIntensityInfo_itemStateChanged(ItemEvent e) {
        this.loadSearchPeaksResults();
    }
    protected void do_btnAddToQuicklist_actionPerformed(ActionEvent e) {
        PDCompound pdc = this.getCurrentCompound();
        if (pdc!=null){
            PDDatabase.addCompoundQL(this.getCurrentCompound(),true);            
        }
    }

    public static float getMinDspacingToSearch() {
        return minDspacingToSearch;
    }

    public static void setMinDspacingToSearch(float minDspacingToSearch) {
        DB_dialog.minDspacingToSearch = minDspacingToSearch;
    }
}