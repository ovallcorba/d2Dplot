package vava33.plot2d;
/*
 * TODO:
 *  - Nicer list compounds, results...  DONE!
 *  - Search taking into account number of peaks, intensities, etc..  DONE!
 *  - Search by name  -- DONE FILTER
 *  - Search by 1D pattern
 */
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
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
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
import vava33.plot2d.auxi.VavaLogger;

import vava33.plot2d.auxi.FilteredListModel;
import vava33.plot2d.auxi.ImgOps;
import vava33.plot2d.auxi.PDCompound;
import vava33.plot2d.auxi.PDDatabase;
import vava33.plot2d.auxi.PDSearchResult;
import vava33.plot2d.auxi.PuntCercle;

import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.JProgressBar;

import org.apache.commons.math3.util.FastMath;

import net.miginfocom.swing.MigLayout;

public class DB_dialog extends JFrame {

    private static final long serialVersionUID = -6104927797410689910L;
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
//    private File dataFile;
//    private Pattern2D patt2D;
    private boolean showPDDataRings;
    private JButton btnShowDsp;
    private MainFrame mf;
    private static JProgressBar pBarDB;
//    private Thread readDBFileThread;
    private ProgressMonitor pm;
    private PDDatabase.openDBfileWorker openDBFwk;
    private PDDatabase.searchDBWorker searchDBwk;
    private JButton btnTestclosest;
    private JButton btnSearchByPeaks;
    private JCheckBox chckbxIntensityInfo;
    private JCheckBox chckbxNameFilter;
    private JTextField txtNamefilter;
    private JCheckBox chckbxNpksInfo;
    
    private int maxNsol = 50;
    private JButton btnResetSearch;
    private JButton btnSaveDb;
    private JLabel lblHeader;
    private JPanel panel;
    private JPanel panel_1;
    private JPanel panel_2;
    private JButton btnAddCompound;
    private JButton btnEditCompound;
    
    /**
     * Create the dialog.
     */
    public DB_dialog(MainFrame mf) {
        this.mf = mf;
        setIconImage(Toolkit.getDefaultToolkit().getImage(DB_dialog.class.getResource("/img/Icona.png")));
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Compound DB");
        // setBounds(100, 100, 660, 730);
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        int width = 660;
        int height = 730;
        int x = (screen.width - width) / 2;
        int y = (screen.height - height) / 2;
        setBounds(x, y, 600, 660);
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(this.contentPanel, BorderLayout.CENTER);
        contentPanel.setLayout(new MigLayout("", "[grow]", "[598px,grow]"));
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
                            panel_left.setLayout(new MigLayout("", "[grow]", "[25px][grow][]"));
                            {
                                panel = new JPanel();
                                panel_left.add(panel, "cell 0 0,grow");
                                panel.setLayout(new MigLayout("", "[40.00px][][][][grow]", "[25px][25px][23px]"));
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
                                    cbox_onTop.setSelected(true);
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
                                            panel.add(chckbxIntensityInfo, "cell 2 1,alignx left,aligny center");
                                        }
                                        {
                                            chckbxNpksInfo = new JCheckBox("Npks info");
                                            panel.add(chckbxNpksInfo, "cell 3 1,alignx left,aligny center");
                                            chckbxNpksInfo.setSelected(true);
                                        }
                                        this.lblHelp = new JLabel("?");
                                        panel.add(lblHelp, "cell 4 1,alignx right,aligny center");
                                        lblHelp.setEnabled(false);
                                        lblHelp.setVisible(false);
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
                    panel_1.setLayout(new MigLayout("", "[grow][]", "[][grow][]"));
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
                panel_right.setLayout(new MigLayout("", "[grow]", "[grow]"));
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
            getContentPane().add(buttonPane, BorderLayout.SOUTH);
            JButton okButton = new JButton("close");
            okButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent arg0) {
                    do_okButton_actionPerformed(arg0);
                }
            });
            {
                btnTestclosest = new JButton("testClosest");
                btnTestclosest.setVisible(false);
                buttonPane.setLayout(new MigLayout("", "[112px][70px]", "[25px]"));
                btnTestclosest.setEnabled(false);
                buttonPane.add(btnTestclosest, "cell 0 0,alignx center,aligny center");
                btnTestclosest.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent arg0) {
                        do_btnTestclosest_actionPerformed(arg0);
                    }
                });
            }
            okButton.setActionCommand("OK");
            buttonPane.add(okButton, "cell 1 0,alignx right,aligny center");
            getRootPane().setDefaultButton(okButton);
        }
        {
            {
                panel_2 = new JPanel();
                panel_left.add(panel_2, "cell 0 2,grow");
                panel_2.setLayout(new MigLayout("", "[][][]", "[]"));
                {
                    btnAddCompound = new JButton("Add Compound");
                    btnAddCompound.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent arg0) {
                            do_btnAddCompound_actionPerformed(arg0);
                        }
                    });
                    panel_2.add(btnAddCompound, "cell 0 0");
                }
                {
                    btnEditCompound = new JButton("Edit Compound");
                    btnEditCompound.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent e) {
                            do_btnEditCompound_actionPerformed(e);
                        }
                    });
                    panel_2.add(btnEditCompound, "cell 1 0");
                }
                btnShowDsp = new JButton("Compound info");
                panel_2.add(btnShowDsp, "cell 2 0,alignx center,aligny center");
                btnShowDsp.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent arg0) {
                        do_btnShowDsp_actionPerformed(arg0);
                    }
                });
            }
        }
        tAOut.ln("** PDDatabase **");

//      dataFile = mf.getOpenedFile();
//      patt2D = mf.getPatt2D();
        lm = new DefaultListModel();
        listCompounds.setModel(lm);
        tAOut.ln(" Reading default database: "+PDDatabase.getDefaultDBpath());
        do_btnLoadDB_actionPerformed(null,false);
    }

    @Override
    public void dispose() {
        this.chckbxPDdata.setSelected(false);
        super.dispose();
    }

    //boolean ask for default or not
    protected void do_btnLoadDB_actionPerformed(ActionEvent arg0,boolean ask) {
        
        //primer creem el progress monitor, el worker i hi afegim el listener
        pm = new ProgressMonitor(this,
                "Reading DB file...",
                "", 0, 100);
        pm.setProgress(0);
        pBarDB.setString("Reading DB");
        pBarDB.setStringPainted(true);
        openDBFwk = new PDDatabase.openDBfileWorker(null,false);
        openDBFwk.addPropertyChangeListener(new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent evt) {
              //VavaLogger.LOG.info("hello from propertyChange");
              VavaLogger.LOG.info(evt.getPropertyName());
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
        
        //Ara preguntem si carregar la DB interna o carregar un fitxer apart:
        int n = JOptionPane.YES_OPTION; //per defecte obrim local
        if (ask){
            Object[] options = {"Load default DB",
            "Open external DB file"};
            n = JOptionPane.showOptionDialog(this,
            "Open default DB or one from a file?",
            "Load DB",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE,
            null,
            options,
            options[0]);
            
            VavaLogger.LOG.info("option = "+n);
        }
        
        if (n == JOptionPane.YES_OPTION) {
            //load internal DB
            PDDatabase.reset();
            openDBFwk.setReadLocal(true);
            openDBFwk.execute();
//            this.acknowledgeCOD();
            
            //            URL zipUrl = this.getClass().getResource(localDB);
//            try{
//                File zipFile = new File(zipUrl.toURI());
//                ZipFile zip = new ZipFile(zipFile);
////                InputStream is = zip.getInputStream(zip.getEntry("codDB.db"));
//                //reset current Database
//                PDDatabase.reset();
//                //read database file in zip
//                openDBFwk.setFileToRead(zip);
//                openDBFwk.execute();
//                this.acknowledgeCOD();
//            }catch(Exception e){
//                e.printStackTrace();
//            }
            
            //////File file = new File(classLoader.getResource("file/test.xml").getFile());
            return;
        }
        
        if (n == JOptionPane.CLOSED_OPTION) {
            return;
        }
        
        //SI ARRIBEM AQUI HEM D'OBRIR EL DIALEG:
        
        //Carrega un fitxer de base de dades
        JFileChooser fileChooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("DB file (db,txt,dat)", "db", "txt", "dat");
        fileChooser.addChoosableFileFilter(filter);
        fileChooser.setCurrentDirectory(new File(MainFrame.getWorkdir()));
        int selection = fileChooser.showOpenDialog(null);
        if (selection != JFileChooser.APPROVE_OPTION) {
            tAOut.stat("No data file selected");
            return;
        }
        //reset current Database
        PDDatabase.reset();
        //read database file
        openDBFwk.setFileToRead(fileChooser.getSelectedFile());
        openDBFwk.execute();
                
        //DEBUG ADD ALL COMPOUNDS TO LIST
//        this.updateList();
        
    }

    protected void do_cbox_onTop_itemStateChanged(ItemEvent arg0) {
        this.setAlwaysOnTop(cbox_onTop.isSelected());
    }

    protected void do_chckbxCalibrate_itemStateChanged(ItemEvent arg0) {
        this.showPDDataRings = chckbxPDdata.isSelected();
        mf.getPanelImatge().setShowDSPRings(this.isShowDataRings(), this.getCurrentCompound());
//        this.showPDDataRings = chckbxPDdata.isSelected();
//        if (chckbxPDdata.isSelected() && this.getCurrentCompound()!=null){
//            mf.getPanelImatge().setShowDSPRings(this.isShowDataRings(), this.getCurrentCompound());    
//        }
    }

    protected void do_lbllist_mouseEntered(MouseEvent e) {
        // lbllist.setText("<html><font color='red'>?</font></html>");
        lblHelp.setForeground(Color.red);
    }

    protected void do_lbllist_mouseExited(MouseEvent e) {
        lblHelp.setForeground(Color.black);
    }

    //TODO: HELP
    protected void do_lbllist_mouseReleased(MouseEvent e) {
        tAOut.ln("");
        tAOut.ln("** PDDatabase HELP **");
        tAOut.ln("");
//        tAOut.ln("Acknowledgements:\n"
//                + "The default DB is a selection of inorganic compounds taken from the Crystallography Open Database (COD) "
//                + "for which the d-spacings have been calculated according to the reported cell parameters and contents (on 06/03/2015).\n"
//                + "COD references (http://www.crystallography.net/):\n"
//                + "Grazulis, S., Daškevič, A., Merkys, A., Chateigner, D., Lutterotti, L., Quiros, M., Serebryanaya, N.R., Moeck, P., Downs, R. T. & Le Bail, A. (2012) Nucl. Acids Res. 40 (D1), D420-D427\n"
//                + "Grazulis, S., Chateigner, D., Downs, R. T., Yokochi, A.  F.  T., Quiros, M., Lutterotti, L., Manakova, E., Butkus, J., Moeck, P. & Le Bail, A. (2009). J. Appl. Cryst. 42, 726-729.");
//        tAOut.ln("");
    }
    
//    protected void acknowledgeCOD_tArea(){
//        tAOut.ln("");
//        tAOut.ln("The default DB is a selection of inorganic compounds taken from the Crystallography Open Database (COD, http://www.crystallography.net) "
//                + "for which the d-spacings have been calculated according to the reported cell parameters and contents (on 06/03/2015 by OV).\n"
//                + "COD references:\n"
//                + "Grazulis, S., Daškevič, A., Merkys, A., Chateigner, D., Lutterotti, L., Quiros, M., Serebryanaya, N.R., Moeck, P., Downs, R. T. & Le Bail, A. (2012) Nucl. Acids Res. 40 (D1), D420-D427\n"
//                + "Grazulis, S., Chateigner, D., Downs, R. T., Yokochi, A.  F.  T., Quiros, M., Lutterotti, L., Manakova, E., Butkus, J., Moeck, P. & Le Bail, A. (2009). J. Appl. Cryst. 42, 726-729.");
//        tAOut.ln("");        
//    }
    
//    protected void acknowledgeCOD(){
//        if (!cod_acknowledged){
//            String codack="<html> <div style=\"text-align:justify\"> The default DB is a selection of inorganic compounds taken from the Crystallography Open Database (COD, http://www.crystallography.net) for which the d-spacings have been calculated according to the reported cell parameters (on 06/03/2015 by OV) <br> <br> <font size=-1>COD references: <br> Grazulis, S., Daškevič, A., Merkys, A., Chateigner, D., Lutterotti, L., Quiros, M., Serebryanaya, N.R., Moeck, P., Downs, R. T. & Le Bail, A. (2012) Nucl. Acids Res. 40 (D1), D420-D427 <br> Grazulis, S., Chateigner, D., Downs, R. T., Yokochi, A.  F.  T., Quiros, M., Lutterotti, L., Manakova, E., Butkus, J., Moeck, P. & Le Bail, A. (2009). J. Appl. Cryst. 42, 726-729.<br> </font> </div> </html>";
//            Help_dialog hd = new Help_dialog("COD acknowledgements",codack);
//            hd.setSize(750,320);
//            hd.setLocationRelativeTo(this);
//            hd.setLocation(hd.getLocation().x, hd.getLocation().y-240);
//            hd.setVisible(true);
//            this.setAlwaysOnTop(true); //to get the focus
////            this.requestFocusInWindow();
//            cod_acknowledged=true;
//        }
//    }

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
    
//    public PDCompound getCurrentCompound() {
//        if (listCompounds == null){return null;}
//        if (listCompounds.getSelectedIndex() >= 0) {
//            String sel = (String) listCompounds.getSelectedValue();
//            int ov_index = Integer.parseInt((sel.trim().split("\\s+"))[0]);
//            PDCompound comp = PDDatabase.get_compound_from_ovNum(ov_index);
////            tAOut.ln(comp.printInfoMultipleLines());
//            return comp;
//        } else {
//            return null;
//        }
//    }

    public boolean isShowDataRings() {
        return showPDDataRings;
    }

    public void updateListAllCompounds() {
        pBarDB.setString("Updating DB");
        pBarDB.setStringPainted(true);
        lm.clear();
        Iterator<PDCompound> itrcomp = PDDatabase.getCompList().iterator();
        int n = 0;
        int ncomp = PDDatabase.getCompList().size();
        while (itrcomp.hasNext()){
            PDCompound c = (PDCompound) itrcomp.next();
//            lm.addElement(c.printCompound()); //he tret el .trim()
            lm.addElement(c); 
            //progress
            if (n%100 == 0){
                pBarDB.setValue((int)(((float)n/(float)ncomp)/100.f));    
            }
        }
        lblHeader.setText(" RefNum  Name  [Formula]");
        pBarDB.setValue(100);
        pBarDB.setStringPainted(false);
        
    }
    protected void do_btnShowDsp_actionPerformed(ActionEvent arg0) {
        PDCompound c = this.getCurrentCompound();
        if (c == null){
            tAOut.ln("Select a compound first");
            return;
        }
//        tAOut.ln(c.getOv_number()+" "+c.getCompName()+" "+c.getFormula()+" "+c.getCodCODE());
//        tAOut.ln(c.getA()+" "+c.getB()+" "+c.getC()+" "+c.getAlfa()+" "+c.getBeta()+" "+c.getGamma());
        tAOut.ln(c.printInfoMultipleLines());
//        for (int i=0; i<c.getPeaks().size();i++){
//            int h = c.getPeaks().get(i).getH();
//            int k = c.getPeaks().get(i).getK();
//            int l = c.getPeaks().get(i).getL();
//            float dsp = c.getPeaks().get(i).getDsp();
//            float inten = c.getPeaks().get(i).getInten();
//            tAOut.ln(String.format("%3d %3d %3d %9.5f %7.2f",h,k,l,dsp,inten));
//        }
        
        //debug:
        mf.getPanelImatge().setShowDSPRings(this.isShowDataRings(), c);
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
        mf.getPanelImatge().setShowDSPRings(this.isShowDataRings(), comp);
        if (comp!=null)tAOut.ln(comp.printInfoLine());
    }
    
    
//    @Override
//    public void propertyChange(PropertyChangeEvent evt) {
//        // TODO Auto-generated method stub
//        VavaLogger.LOG.info("hello from propertyChange");
//        if ("percent" == evt.getPropertyName() ) {
//            int progress = (Integer) evt.getNewValue();
//            pm.setProgress(progress);
//            pm.setNote(String.format("Completed %d%%.\n", progress));
//            if (pm.isCanceled() || openDBFwk.isDone()) {
//                Toolkit.getDefaultToolkit().beep();
//                if (pm.isCanceled()) {
//                    openDBFwk.cancel(true);
//                    tAOut.stat("reading of DB file "+openDBFwk.getDbfile()+" stopped!");
//                    tAOut.stat(" --> ncompounds = "+getnCompounds());    
//                } else {
//                    tAOut.stat("reading of DB file "+openDBFwk.getDbfile()+" stopped!");
//                    tAOut.stat(" --> ncompounds = "+getnCompounds());    
//                }
//                //startButton.setEnabled(true);
//            }
//        }
//    }
    
    
    protected void do_btnTestclosest_actionPerformed(ActionEvent arg0) {
//        ArrayList<Float> lab6pks = new ArrayList<Float>();
//        lab6pks.add(4.15710f);
//        lab6pks.add(2.93950f);
//        lab6pks.add(2.40010f);
//        lab6pks.add(2.07850f);
//        lab6pks.add(1.85910f);
//        lab6pks.add(1.69710f);
//        lab6pks.add(1.46970f);
//        lab6pks.add(1.38570f);
//        lab6pks.add(1.38570f);
//        lab6pks.add(1.31460f);
//        lab6pks.add(1.25340f);
//        PDDatabase.takeClosest(lab6pks,4.141f);
//        PDDatabase.takeClosest(lab6pks,2.936f);
//        PDDatabase.takeClosest(lab6pks,2.395f);
//        PDDatabase.takeClosest(lab6pks,2.075f);
//        PDDatabase.takeClosest(lab6pks,1.861f);
//        PDDatabase.takeClosest(lab6pks,1.697f);
//        PDDatabase.takeClosest(lab6pks,1.466f);
//        PDDatabase.takeClosest(lab6pks,1.383f);
//        PDDatabase.takeClosest(lab6pks,1.312f);
//        PDDatabase.takeClosest(lab6pks,1.249f);
    }
    
    //CANVIEM PER NO UTILITZAR EL LM AMB TOTS ELS COMPOSTOS  -- AL FINAL NO, fem boto per tornar a mostrar tots
    public void loadSearchPeaksResults(ArrayList<Float> searchlist){
        //aqui en principi tindrem una llista de resultats a PDDatabase i s'haurà de mostrar
        lm.clear();
//        float minDSPin = Collections.min(searchlist);
//        int nDSPin = searchlist.size();
        PDSearchResult.setMinDSPin(Collections.min(searchlist));
        PDSearchResult.setnDSPin(searchlist.size());
        
        ArrayList<PDSearchResult> res = PDDatabase.getSearchresults();
        
        //mirem si hi ha criteris complementaris pel residual
        if (chckbxIntensityInfo.isSelected() || chckbxNpksInfo.isSelected()){
            Iterator<PDSearchResult> itrcomp = res.iterator();
            while (itrcomp.hasNext()){
                PDSearchResult c = itrcomp.next();
                float resid = c.getResidual();
                if (chckbxIntensityInfo.isSelected()){
                    resid = resid + c.getResidual_intensities();
                }
                if (chckbxNpksInfo.isSelected()){
                    resid = resid * ((Math.max((float)c.getC().getNrRefUpToDspacing(PDSearchResult.getMinDSPin())/(float)PDSearchResult.getnDSPin(),1))/2);
//                    resid = resid * (Math.max((float)c.getC().getNrRefUpToDspacing(PDSearchResult.getMinDSPin())/(float)PDSearchResult.getnDSPin(),1)); 
//                                  * (Math.max((float)c.getC().getNrRefUpToDspacing(PDSearchResult.getMinDSPin())/(float)PDSearchResult.getnDSPin(),1));
                }
                c.setResidual(resid);
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
        lblHeader.setText(" Residual  inputRefs/compoundRefs  CompoundName  [Formula] ");
    }
    
    public void searchPeaks(final ArrayList<Float> searchlist, ArrayList<Float> searchlistIntensities){
        pm = new ProgressMonitor(this,
                "Searching for peak matching...",
                "", 0, 100);
        pm.setProgress(0);
        
        pBarDB.setString("Searching DB");
        pBarDB.setStringPainted(true);
        
        searchDBwk = new PDDatabase.searchDBWorker(searchlist,searchlistIntensities);
        searchDBwk.addPropertyChangeListener(new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent evt) {
              //VavaLogger.LOG.info("hello from propertyChange");
              VavaLogger.LOG.info(evt.getPropertyName());
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
                          loadSearchPeaksResults(searchlist);
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
    
    
    protected void do_btnSearchByPeaks_actionPerformed(ActionEvent e) {
        if (MainFrame.getPatt2D().getPuntsCercles().isEmpty()){
            tAOut.stat("Please select some peaks clicking in the image");
            return;
        }
        Iterator<PuntCercle> itrP = MainFrame.getPatt2D().getPuntsCercles().iterator();
        ArrayList<Float> searchlist = new ArrayList<Float>();
        ArrayList<Float> searchlistInten = new ArrayList<Float>();
        while (itrP.hasNext()) {
            PuntCercle pa = itrP.next();
            float dsp = (float)MainFrame.getPatt2D().calcDsp(pa.getT2());
            if (dsp > MainFrame.minDspacingToSearch){
                searchlist.add(dsp);    
                //per la intensitat fem el promig de l'anell
                float circleInt = ImgOps.intRadCircle(MainFrame.getPatt2D(),(float)FastMath.toDegrees(pa.getT2()), -1.f);
                VavaLogger.LOG.info("Intensity (NOT normalized to 100 or 1st peak) of peak: "+(float)FastMath.toDegrees(pa.getT2())+" is "+circleInt);
                searchlistInten.add(circleInt);
            }
        }
        if (searchlist.isEmpty()){
            tAOut.stat("no peaks found");
            return;
        }
        this.searchPeaks(searchlist,searchlistInten);
    }
    
    protected void filterList(){
        if (lm.isEmpty()){return;}
        if (chckbxNameFilter.isSelected()){
            //filter list
            FilteredListModel filteredListModel = new FilteredListModel(lm);
            listCompounds.setModel(filteredListModel);
            filteredListModel.setFilter(new FilteredListModel.Filter() {
                public boolean accept(Object element) {
//                    String s = (String)element;
                    
                    //PROVA PER BUSCAR EN TOTS ELS CAMPS DE LA DATABASE (name, namealt, cell, comment, spacegroup,...)
//                    PDCompound comp = null;
//                    if (element instanceof PDCompound){
//                        comp = (PDCompound) listCompounds.getSelectedValue();
//                    }
//                    if (element instanceof PDSearchResult){
//                        PDSearchResult sr = (PDSearchResult) listCompounds.getSelectedValue();
//                        comp = sr.getC();
//                    }
//                    if (comp == null) return false;
                    PDCompound comp = null;
                    try{
                        comp = (PDCompound)element;    
                    }catch(Exception e){
                        System.out.println("trying searchresult...");
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
                    
                    //aixo funciona pero es simple, vull que amb espais provi tots els ordres de paraules
                    //i sigui CASE INSENSITIVE
//                    String s = (String)element;
//                    if (s.contains(txtNamefilter.getText())){
//                        return true;
//                    }
//                    return false;
                    
                }
            });
            if (txtNamefilter.getText().trim().length() == 0){
                listCompounds.setModel(lm);
                tAOut.stat("Number of compounds = "+lm.getSize());    
            }else{
                tAOut.stat("Number of (filtered) compounds = "+filteredListModel.getSize());    
            }
        }
            
//        }else{
//            listCompounds.setModel(lm);
//            tAOut.stat("Number of compounds = "+lm.getSize());    
//        }
        
    }
    protected void do_btnResetSearch_actionPerformed(ActionEvent arg0) {
        this.updateListAllCompounds();
    }
    protected void do_btnSaveDb_actionPerformed(ActionEvent arg0) {
        File f = FileUtils.fchooser(null, null, true);
        if (f == null)return;
        boolean ok = PDDatabase.saveDB(f);
        if (ok){
            tAOut.stat("DB saved to "+f.toString());
        }else{
            tAOut.stat("Error saving file "+f.toString());
        }
        
    }
    protected void do_btnAddCompound_actionPerformed(ActionEvent arg0) {
        DB_editor dbe = new DB_editor(null,this);
        dbe.setVisible(true);
    }
    protected void do_btnEditCompound_actionPerformed(ActionEvent e) {
        DB_editor dbe = new DB_editor(this.getCurrentCompound(),this);
        dbe.setVisible(true);
    }
}








//ALTRES COSES QUE HE DESCARTAT

//public void loadSearchPeaksResults(ArrayList<Float> searchlist){
//    //aqui en principi tindrem una llista de resultats a PDDatabase i s'haurà de mostrar
//    lm.clear();
//    ArrayList<PDDatabase.SearchResult> res = PDDatabase.getSearchresults();
//    Collections.sort(res);
//    float minDSPin = Collections.min(searchlist);
//    int nDSPin = searchlist.size();
//    Iterator<PDDatabase.SearchResult> itrcomp = res.iterator();
//    while (itrcomp.hasNext()){
//        PDDatabase.SearchResult c = itrcomp.next();
//        Iterator<Float> itref = c.getC().getDspacings().iterator();
//        int nrefcomp = 0;
//        while (itref.hasNext()){
//            float r = itref.next();
//            if (r>=(minDSPin-0.05f)){
//                nrefcomp = nrefcomp + 1;
//            }
//        }
//        lm.addElement(c.getC().printCompound().trim()+" "+c.getResidual()+" "+nDSPin+" "+nrefcomp);
//    }
//    lblHeader.setText(" RefNum  Residual  inputRefs/compoundRefs  CompoundName  [Formula] ");
//}


//private static class checkDBread implements Runnable {
//
//    Thread th;
//    String fpath;
//    
//    checkDBread(Thread t, String filepath){
//        this.th=t;
//        this.fpath=filepath;
//    }
//    @Override
//    public void run() {
//        // TODO Auto-generated method stub
//        while (th.isAlive()){
//            try {
//                VavaLogger.LOG.info("Sleep");
//                Thread.sleep(1000);
//            } catch (InterruptedException e) {
//                // TODO Auto-generated catch block
//                e.printStackTrace();
//            }
//            if (th.isInterrupted()){
//                tAOut.stat("reading of DB file "+this.fpath+" stopped!");
//                tAOut.stat(" --> ncompounds = "+PDDatabase.getnCompounds());    
//                return;                    
//            }
//        }
//        tAOut.stat("reading of DB file "+this.fpath+" stopped!");
//        tAOut.stat(" --> ncompounds = "+PDDatabase.getnCompounds());    
//    }
//}


//this.pBarDB.setIndeterminate(true);
//this.pBarDB.setString("reading DB");
//this.pBarDB.setStringPainted(true);

//readDBFileThread = PDDatabase.readDataFile(fileChooser.getSelectedFile(),pBarDB);
//checkDBread th = new checkDBread(readDBFileThread,fileChooser.getSelectedFile().toString()); 
//SwingUtilities.invokeLater(th);





//////////////AQUEST VA PERFECTE, ABANS DE CANVIAR PER LLEGIR ZIP

//protected void do_btnLoadDB_actionPerformed(ActionEvent arg0) {
//    
//    //preguntem si carregar la DB interna o carregar un fitxer apart:
//    
//    URL zipUrl = this.getClass().getResource(localDB);
//    try{
//        File zipFile = new File(zipUrl.toURI());
//        ZipFile zip = new ZipFile(zipFile);
////        InputStream is = zip.getInputStream(zip.getEntry("codDB.db"));        
//        pm = new ProgressMonitor(this,
//                "Reading DB file...",
//                "", 0, 100);
//        pm.setProgress(0);
//        openDBFwk = new PDDatabase.openDBfileWorker(zip); 
//        openDBFwk.addPropertyChangeListener(new PropertyChangeListener() {
//
//            @Override
//            public void propertyChange(PropertyChangeEvent evt) {
//              //VavaLogger.LOG.info("hello from propertyChange");
//              VavaLogger.LOG.info(evt.getPropertyName());
//              if ("progress" == evt.getPropertyName() ) {
//                  int progress = (Integer) evt.getNewValue();
//                  pm.setProgress(progress);
//                  pm.setNote(String.format("%d%%\n", progress));
//                  if (pm.isCanceled() || openDBFwk.isDone()) {
//                      Toolkit.getDefaultToolkit().beep();
//                      if (pm.isCanceled()) {
//                          openDBFwk.cancel(true);
//                          tAOut.stat("reading of DB file "+openDBFwk.getReadedFile()+" stopped!");
//                          tAOut.stat("Number of compounds = "+PDDatabase.getnCompounds());    
//                      } else {
//                          tAOut.stat("reading of DB file "+openDBFwk.getReadedFile()+" finished!");
//                          tAOut.stat("Number of compounds = "+PDDatabase.getnCompounds());    
//                      }
//                      pm.close();
//                      updateListAllCompounds();
//                      //startButton.setEnabled(true);
//                  }
//              }
//            }
//        });
//                
//        openDBFwk.execute();
//    }catch(Exception e){
//        e.printStackTrace();
//    }
//    
//    //////File file = new File(classLoader.getResource("file/test.xml").getFile());
//    
//    
//    //Carrega un fitxer de base de dades
//    JFileChooser fileChooser = new JFileChooser();
//    FileNameExtensionFilter filter = new FileNameExtensionFilter("DB file (db,txt,dat)", "db", "txt", "dat");
//    fileChooser.addChoosableFileFilter(filter);
//    fileChooser.setCurrentDirectory(new File(MainFrame.getWorkdir()));
//    int selection = fileChooser.showOpenDialog(null);
//    if (selection != JFileChooser.APPROVE_OPTION) {
//        tAOut.stat("No data file selected");
//        return;
//    }
//    //reset current Database
//    PDDatabase.reset();
//    
//    pm = new ProgressMonitor(this,
//            "Reading DB file...",
//            "", 0, 100);
//    pm.setProgress(0);
//    openDBFwk = new PDDatabase.openDBfileWorker(fileChooser.getSelectedFile());
//    openDBFwk.addPropertyChangeListener(new PropertyChangeListener() {
//
//        @Override
//        public void propertyChange(PropertyChangeEvent evt) {
//          //VavaLogger.LOG.info("hello from propertyChange");
//          VavaLogger.LOG.info(evt.getPropertyName());
//          if ("progress" == evt.getPropertyName() ) {
//              int progress = (Integer) evt.getNewValue();
//              pm.setProgress(progress);
//              pm.setNote(String.format("%d%%\n", progress));
//              if (pm.isCanceled() || openDBFwk.isDone()) {
//                  Toolkit.getDefaultToolkit().beep();
//                  if (pm.isCanceled()) {
//                      openDBFwk.cancel(true);
//                      tAOut.stat("reading of DB file "+openDBFwk.getDbfile()+" stopped!");
//                      tAOut.stat("Number of compounds = "+PDDatabase.getnCompounds());    
//                  } else {
//                      tAOut.stat("reading of DB file "+openDBFwk.getDbfile()+" stopped!");
//                      tAOut.stat("Number of compounds = "+PDDatabase.getnCompounds());    
//                  }
//                  pm.close();
//                  updateListAllCompounds();
//                  //startButton.setEnabled(true);
//              }
//          }
//        }
//    });
//            
//    openDBFwk.execute();
//            
//    //DEBUG ADD ALL COMPOUNDS TO LIST
////    this.updateList();
//    
//}
