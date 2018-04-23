package com.vava33.d2dplot;
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
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.Scanner;

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
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.vava33.cellsymm.Cell;
import com.vava33.cellsymm.SpaceGroup;
import com.vava33.d2dplot.auxi.FilteredListModel;
import com.vava33.d2dplot.auxi.ImgOps;
import com.vava33.d2dplot.auxi.PDCompound;
import com.vava33.d2dplot.auxi.PDDatabase;
import com.vava33.d2dplot.auxi.PDReflection;
import com.vava33.d2dplot.auxi.PDSearchResult;
import com.vava33.d2dplot.auxi.Pattern2D;
import com.vava33.jutils.Cif_file;
import com.vava33.jutils.FileUtils;
import com.vava33.jutils.LogJTextArea;
import com.vava33.jutils.VavaLogger;

import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.JProgressBar;
import javax.swing.JScrollBar;

import net.miginfocom.swing.MigLayout;
import javax.swing.JTextArea;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class DB_dialog extends JFrame {

    private static final long serialVersionUID = -6104927797410689910L;
    private static float minDspacingToSearch = 1.15f;
    private static final int maxNsol = 50;

    private JButton btnLoadDB;
    private JCheckBox cbox_onTop;
    private JCheckBox chckbxPDdata;
    private final JPanel contentPanel = new JPanel();
    private JLabel lblHelp;
    private JList<Object> listCompounds;
    private JPanel panel_left;
    private JPanel panel_right;
    private JScrollPane scrollPane;
    private JScrollPane scrollPane_1;
    private JSplitPane splitPane;
    private static LogJTextArea tAOut;
    
    private DefaultListModel<Object> lm;
    private boolean showPDDataRings;
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
    private JButton btnAddCompound;
    private static VavaLogger log = D2Dplot_global.getVavaLogger(DB_dialog.class.getName());

    private Pattern2D patt2d;
    private ImagePanel ipanel;
    private JButton btnAddToQuicklist;
    private JPanel panel_3;
    private JLabel lblName;
    private JLabel lblNamealt;
    private JLabel lblFormula;
    private JLabel lblCellParameters;
    private JLabel lblSpaceGroup;
    private JLabel lblReference;
    private JLabel lblComment;
    private JLabel label;
    private JTextArea textAreaDsp;
    private JScrollPane scrollPane_2;
    private JTextField txtName;
    private JTextField txtNamealt;
    private JTextField txtFormula;
    private JTextField txtCellParameters;
    private JTextField txtSpaceGroup;
    private JTextField txtReference;
    private JTextField txtComment;
    private JButton btnCalcRefl;
    private JButton btnApplyChanges;
    private JButton btnRemove;
    private JButton btnAddAsNew;
    private JButton btnImportCif;
    private JButton btnImportHkl;
    private PDCompound currCompound;
    private JSplitPane splitPane_1;
    /**
     * Create the dialog.
     */
    public DB_dialog(ImagePanel ip) {
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                do_this_windowClosing(e);
            }
        });
        this.setIpanel(ip);
        
        setIconImage(Toolkit.getDefaultToolkit().getImage(DB_dialog.class.getResource("/img/Icona.png")));
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setTitle("Compound DB");
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        int width = 660;
        int height = 730;
        int x = (screen.width - width) / 2;
        int y = (screen.height - height) / 2;
        setBounds(x, y, 820, 680);
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
                            panel_left.setLayout(new MigLayout("fill, insets 0", "[grow]", "[25px][grow]"));
                            {
                                panel = new JPanel();
                                panel_left.add(panel, "cell 0 0,grow");
                                panel.setLayout(new MigLayout("fill, insets 0", "[][][][grow][]", "[][][]"));
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
                                        do_btnLoadDB_actionPerformed(arg0);
                                    }
                                });
                            }
                        }
                    }
                }
                {
                    splitPane_1 = new JSplitPane();
                    splitPane_1.setResizeWeight(0.5);
                    splitPane_1.setContinuousLayout(true);
                    panel_left.add(splitPane_1, "cell 0 1,grow");
                    {
                        panel_3 = new JPanel();
                        splitPane_1.setRightComponent(panel_3);
                        panel_3.setLayout(new MigLayout("", "[][grow][]", "[][][][][][][][][][grow][]"));
                        {
                            btnImportCif = new JButton("Import CIF");
                            btnImportCif.addActionListener(new ActionListener() {
                                public void actionPerformed(ActionEvent e) {
                                    do_btnImportCif_actionPerformed(e);
                                }
                            });
                            panel_3.add(btnImportCif, "cell 1 0,alignx right");
                        }
                        {
                            btnImportHkl = new JButton("Import HKL");
                            btnImportHkl.addActionListener(new ActionListener() {
                                public void actionPerformed(ActionEvent e) {
                                    do_btnImportHkl_actionPerformed(e);
                                }
                            });
                            panel_3.add(btnImportHkl, "cell 2 0,alignx right");
                        }
                        {
                            lblName = new JLabel("Name");
                            panel_3.add(lblName, "cell 0 1,alignx trailing");
                        }
                        {
                            txtName = new JTextField();
                            txtName.setText("Name");
                            panel_3.add(txtName, "cell 1 1 2 1,growx");
                            txtName.setColumns(10);
                        }
                        {
                            lblNamealt = new JLabel("Name (alt)");
                            panel_3.add(lblNamealt, "cell 0 2,alignx trailing");
                        }
                        {
                            txtNamealt = new JTextField();
                            txtNamealt.setText("NameAlt");
                            panel_3.add(txtNamealt, "cell 1 2 2 1,growx");
                            txtNamealt.setColumns(10);
                        }
                        {
                            lblFormula = new JLabel("Formula");
                            panel_3.add(lblFormula, "cell 0 3,alignx trailing");
                        }
                        {
                            txtFormula = new JTextField();
                            txtFormula.setText("Formula");
                            panel_3.add(txtFormula, "cell 1 3 2 1,growx");
                            txtFormula.setColumns(10);
                        }
                        {
                            lblCellParameters = new JLabel("Cell parameters");
                            panel_3.add(lblCellParameters, "cell 0 4,alignx trailing");
                        }
                        {
                            txtCellParameters = new JTextField();
                            txtCellParameters.setText("Cell Parameters");
                            panel_3.add(txtCellParameters, "cell 1 4 2 1,growx");
                            txtCellParameters.setColumns(10);
                        }
                        {
                            lblSpaceGroup = new JLabel("Space group");
                            panel_3.add(lblSpaceGroup, "cell 0 5,alignx trailing");
                        }
                        {
                            txtSpaceGroup = new JTextField();
                            txtSpaceGroup.setText("Space Group");
                            panel_3.add(txtSpaceGroup, "cell 1 5,growx");
                            txtSpaceGroup.setColumns(10);
                        }
                        {
                            btnCalcRefl = new JButton("Calc Refl.");
                            btnCalcRefl.addActionListener(new ActionListener() {
                                public void actionPerformed(ActionEvent e) {
                                    do_btnCalcRefl_actionPerformed(e);
                                }
                            });
                            panel_3.add(btnCalcRefl, "cell 2 5,growx");
                        }
                        {
                            lblReference = new JLabel("Reference");
                            panel_3.add(lblReference, "cell 0 6,alignx trailing");
                        }
                        {
                            txtReference = new JTextField();
                            txtReference.setText("Reference");
                            panel_3.add(txtReference, "cell 1 6 2 1,growx");
                            txtReference.setColumns(10);
                        }
                        {
                            lblComment = new JLabel("Comment");
                            panel_3.add(lblComment, "cell 0 7,alignx trailing");
                        }
                        {
                            txtComment = new JTextField();
                            txtComment.setText("Comment");
                            panel_3.add(txtComment, "cell 1 7 2 1,growx");
                            txtComment.setColumns(10);
                        }
                        {
                            label = new JLabel("list of (one per line): h k l d-spacing intensity");
                            panel_3.add(label, "cell 0 8 3 1,alignx left");
                        }
                        {
                            scrollPane_2 = new JScrollPane();
                            panel_3.add(scrollPane_2, "cell 0 9 3 1,grow");
                            {
                                textAreaDsp = new JTextArea();
                                scrollPane_2.setViewportView(textAreaDsp);
                                textAreaDsp.setRows(3);
                            }
                        }
                        {
                            btnApplyChanges = new JButton("Apply Changes");
                            btnApplyChanges.addActionListener(new ActionListener() {
                                public void actionPerformed(ActionEvent e) {
                                    do_btnApplyChanges_actionPerformed(e);
                                }
                            });
                            panel_3.add(btnApplyChanges, "cell 1 10,alignx right");
                        }
                        {
                            btnAddAsNew = new JButton("Add as New");
                            btnAddAsNew.addActionListener(new ActionListener() {
                                public void actionPerformed(ActionEvent e) {
                                    do_btnAddAsNew_actionPerformed(e);
                                }
                            });
                            panel_3.add(btnAddAsNew, "cell 2 10");
                        }
                    }
                    {
                        panel_1 = new JPanel();
                        splitPane_1.setLeftComponent(panel_1);
                        panel_1.setLayout(new MigLayout("fill, insets 2", "[][][grow][]", "[][][grow][]"));
                        {
                            chckbxNameFilter = new JCheckBox("Apply name filter:");
                            panel_1.add(chckbxNameFilter, "cell 0 0 2 1,alignx left");
                        }
                        txtNamefilter = new JTextField();
                        panel_1.add(txtNamefilter, "cell 2 0 2 1,growx,aligny center");
                        
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
                        {
                            lblHeader = new JLabel("header");
                            panel_1.add(lblHeader, "cell 0 1 3 1,alignx left");
                        }
                        btnResetSearch = new JButton("reset list");
                        panel_1.add(btnResetSearch, "cell 3 1,alignx right");
                        btnResetSearch.addActionListener(new ActionListener() {
                            public void actionPerformed(ActionEvent arg0) {
                                do_btnResetSearch_actionPerformed(arg0);
                            }
                        });
                        {
                            this.scrollPane = new JScrollPane();
                            panel_1.add(scrollPane, "cell 0 2 4 1,grow");
                            {
                                this.listCompounds = new JList<Object>();
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
                            btnAddCompound = new JButton("New");
                            panel_1.add(btnAddCompound, "cell 0 3,growx");
                            {
                                btnRemove = new JButton("Remove");
                                btnRemove.addActionListener(new ActionListener() {
                                    public void actionPerformed(ActionEvent e) {
                                        do_btnRemove_actionPerformed(e);
                                    }
                                });
                                panel_1.add(btnRemove, "cell 1 3,alignx left");
                            }
                            {
                                btnAddToQuicklist = new JButton("Add to Quicklist");
                                panel_1.add(btnAddToQuicklist, "cell 3 3,alignx right");
//                                splitPane_1.setDividerLocation(375);
                                splitPane_1.setDividerLocation(this.getWidth()/2);
                                btnAddToQuicklist.addActionListener(new ActionListener() {
                                    public void actionPerformed(ActionEvent e) {
                                        do_btnAddToQuicklist_actionPerformed(e);
                                    }
                                });
                            }
                            btnAddCompound.addActionListener(new ActionListener() {
                                public void actionPerformed(ActionEvent arg0) {
                                    do_btnAddCompound_actionPerformed(arg0);
                                }
                            });
                        }
                    }
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
                buttonPane.setLayout(new MigLayout("fill, insets 0", "[grow][]", "[]"));
            }
            {
                pBarDB = new JProgressBar();
                buttonPane.add(pBarDB, "cell 0 0,growx");
                pBarDB.setStringPainted(true);
            }
            okButton.setActionCommand("OK");
            buttonPane.add(okButton, "cell 1 0,alignx right,aligny center");
            getRootPane().setDefaultButton(okButton);
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
        lm = new DefaultListModel<Object>();
        listCompounds.setModel(lm);
        tAOut.ln(" Reading default database: "+PDDatabase.getDefaultDBpath());
        this.readDB(true);
        //select the first compound
    }
   
    private void readDB(boolean readDefault) {
        
        //primer creem el progress monitor, 
        pm = new ProgressMonitor(this,
                "Reading DB file...",
                "", 0, 100);
        pm.setProgress(0);
        pBarDB.setString("Reading DB");
        pBarDB.setStringPainted(true);
        
        //db per defecte:
        File DBFile = new File(PDDatabase.getDefaultDBpath());
        
        if (!readDefault) {
            //Load file
            FileNameExtensionFilter[] filter = {new FileNameExtensionFilter("DB file (db,txt,dat)", "db", "txt", "dat")};
            DBFile = FileUtils.fchooserOpen(this,new File(D2Dplot_global.getWorkdir()), filter, 0);
            if(DBFile==null){
                tAOut.stat("No data file selected");
                return;
            }
            D2Dplot_global.setWorkdir(DBFile);
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
                      PDDatabase.setDBmodified(false);
                      //startButton.setEnabled(true);
                  }
              }
            }
        });
        
        //reset current Database
        PDDatabase.resetDB();
        //read database file, executing the swingworker task
        openDBFwk.execute();
        if (!new File(D2Dplot_global.DBfile).getName().equalsIgnoreCase(DBFile.getName())){
            //ask if this new file should become the default one on config
            boolean defDB = FileUtils.YesNoDialog(this, "Set this DB file as the default for further sessions?");
            if (defDB) {
                D2Dplot_global.DBfile=DBFile.getAbsolutePath();
            }
        }
    }
    
    //boolean ask for default or not
    protected void do_btnLoadDB_actionPerformed(ActionEvent arg0) {
        this.readDB(false);
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
        this.checkSaveAndDispose();
    }

    public PDCompound getCurrentCompound() {
        if (listCompounds == null){return null;}
        if (listCompounds.getSelectedIndex() >= 0) {
            if (listCompounds.getSelectedValue() instanceof PDCompound){
                PDCompound comp = (PDCompound) listCompounds.getSelectedValue();
                this.currCompound=comp;
                return comp;
            }
            if (listCompounds.getSelectedValue() instanceof PDSearchResult){
                PDSearchResult sr = (PDSearchResult) listCompounds.getSelectedValue();
                this.currCompound=sr.getC();
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
        lblHeader.setText(" Name  [Formula]  (alt. names)");
        pBarDB.setValue(100);
        pBarDB.setStringPainted(false);
        listCompounds.setSelectedIndex(0);//TODO: mirar que no s'hagi de cambiar a algun altre lloc
    }

    public boolean isShowPDDataRings() {
        return showPDDataRings;
    }

    public void setShowPDDataRings(boolean showPDDataRings) {
        this.showPDDataRings = showPDDataRings;
    }
    protected void do_listCompounds_valueChanged(ListSelectionEvent arg0) {
        if (arg0.getValueIsAdjusting()) return;

        //Test for changes in compound
        if (currCompound!=null) {
            PDCompound oldCompound = this.currCompound;
            PDCompound edited = new PDCompound("aa");
            this.updateCompoundFromFields(edited,false);
            if (edited.compareTo(oldCompound)!=0) {
                boolean update = FileUtils.YesNoDialog(this, "Previous compound had changed, update it?");
                if (update) {
                    this.updateCompoundFromFields(oldCompound,true);
                    PDDatabase.setDBmodified(true);
                }
            }
        }
        
        PDCompound comp = this.getCurrentCompound();
        this.getIpanel().setShowDBCompoundRings(this.isShowDataRings(), comp);
        if (comp!=null) {
//            tAOut.ln(comp.printInfo2Line());
            //fill the fields of DB
            this.updateInfo(comp);
        }
        this.getIpanel().actualitzarVista();
    }
    
    public void updateInfo(PDCompound c){
        txtName.setText(c.getCompName().get(0));
        txtNamealt.setText(c.getAltNames());
        txtFormula.setText(c.getFormula());
        txtCellParameters.setText(c.getCellParameters());
        txtSpaceGroup.setText(c.getSpaceGroup());
        txtReference.setText(c.getReference());
        txtComment.setText(c.getAllComments());
        textAreaDsp.setText(c.getHKLlines());
    }
    
    public void prepareFields(){
        txtName.setText("");
        txtNamealt.setText("");
        txtFormula.setText("");
        txtCellParameters.setText("");
        txtSpaceGroup.setText("");
        txtReference.setText("");
        txtComment.setText("");
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
        File f = FileUtils.fchooserSaveAsk(this, new File(PDDatabase.getCurrentDB()),filter);
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
                        PDDatabase.setDBmodified(false);
                    }
                }
            }
        });
        
        saveDBFwk.execute();
        if (!new File(D2Dplot_global.DBfile).getName().equalsIgnoreCase(saveDBFwk.getDbFileString())){
            //ask if this new file should become the default one on config
            boolean defDB = FileUtils.YesNoDialog(this, "Set this DB file as the default for further sessions?");
            if (defDB) {
                D2Dplot_global.DBfile=f.getAbsolutePath();
            }
        }
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
    
    protected void do_btnApplyChanges_actionPerformed(ActionEvent e) {
        this.updateCompoundFromFields(this.getCurrentCompound(),true);
        this.updateListAllCompounds();
    }
    
    
    protected void do_btnAddAsNew_actionPerformed(ActionEvent e) {
        //ADD COMPOUND:
        PDCompound co = new PDCompound(txtName.getText().trim());
        PDDatabase.addCompoundDB(co);
        this.updateCompoundFromFields(co,true);
        this.updateListAllCompounds();
        PDDatabase.setDBmodified(true);
        listCompounds.setSelectedIndex(listCompounds.getModel().getSize()-1);
        this.scrollPane.validate();
        JScrollBar vertical = this.scrollPane.getVerticalScrollBar();
        vertical.setValue(vertical.getMaximum());
    }
    
    protected void do_btnAddCompound_actionPerformed(ActionEvent arg0) {
        //ADD COMPOUND:
        PDCompound co = new PDCompound("NEW COMPOUND");
        co.setDefParams();
        PDDatabase.addCompoundDB(co);
        //select the compound
        this.updateListAllCompounds();
        PDDatabase.setDBmodified(true);
        listCompounds.setSelectedIndex(listCompounds.getModel().getSize()-1);
        this.scrollPane.validate();
        JScrollBar vertical = this.scrollPane.getVerticalScrollBar();
        vertical.setValue(vertical.getMaximum());
    }
    
    protected void do_btnRemove_actionPerformed(ActionEvent e) {
        boolean remove = FileUtils.YesNoDialog(this, "Remove selected Compound?");
        if (remove) PDDatabase.getDBCompList().remove(this.getCurrentCompound());
        this.updateListAllCompounds();
        PDDatabase.setDBmodified(true);
    }
    
    private boolean updateCompoundFromFields(PDCompound comp, boolean warningSave) {
        
        String cell = txtCellParameters.getText().trim();
        String[] cellp = cell.split("\\s+");
        float a,b,c,alfa,beta,gamma;
        try{
            a = Float.parseFloat(cellp[0]);
            b = Float.parseFloat(cellp[1]);
            c = Float.parseFloat(cellp[2]);
            alfa = Float.parseFloat(cellp[3]);
            beta = Float.parseFloat(cellp[4]);
            gamma = Float.parseFloat(cellp[5]);
        }catch(Exception e){
            if (D2Dplot_global.isDebug())e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error parsing cell parameters, should be: a b c alpha beta gamma");
            return false;
        }
        if (txtName.getText().isEmpty()){
            JOptionPane.showMessageDialog(this, "Please give the compound name");
            return false;
        }
        
        String[] hkl_lines = textAreaDsp.getText().trim().split("\\n");
        log.debug(Arrays.toString(hkl_lines));
        ArrayList<PDReflection> pdref = new ArrayList<PDReflection>();
        //CHECK CONSISTENCY HKL
        for (int i=0;i<hkl_lines.length;i++){
            String[] line = hkl_lines[i].trim().split("\\s+");
            if (line.length<5){
                JOptionPane.showMessageDialog(this, "Error in hkl lines, should be: h k l dspacing Intensity");
                return false;
            }else{
                try{
                    log.debug(Arrays.toString(line));
                    int h = Integer.parseInt(line[0]);
                    int k = Integer.parseInt(line[1]);
                    int l = Integer.parseInt(line[2]);
                    float dsp = Float.parseFloat(line[3]);
                    float inten = 0.0f;
                    try {
                        inten = Float.parseFloat(line[4]);    
                    }catch(Exception e2) {
                        log.debug("no intensity for reflection");
                    }
                    PDReflection refl = new PDReflection(h,k,l,dsp,inten);
                    pdref.add(refl);
                }catch(Exception e){
                    JOptionPane.showMessageDialog(this, "Error in parsing hkl lines, e.g: 1 0 0 12.5 100.0");
                    return false;
                }
            }
        }
        
        //now we put the info into COMP
        comp.getCompName().clear();
        comp.addCompoundName(txtName.getText().trim());
        comp.addCompoundName(txtNamealt.getText().trim());
        comp.setFormula(txtFormula.getText().trim());
        comp.setA(a);
        comp.setB(b);
        comp.setC(c);
        comp.setAlfa(alfa);
        comp.setBeta(beta);
        comp.setGamma(gamma);
        comp.setSpaceGroup(txtSpaceGroup.getText().trim());
        comp.setReference(txtReference.getText().trim());
        comp.getComment().clear();
        comp.addComent(txtComment.getText().trim());
        //dsp + intensities
        comp.getPeaks().clear();
        comp.setPeaks(pdref);
        
        //don't forget to save the DB file to keep changes for future openings.
        if(warningSave)JOptionPane.showMessageDialog(this, "Do not forget to save the DB into a file \n(otherwise changes will be lost on close)");
        
//        this.updateListAllCompounds();
        return true;
    }
    
    protected void do_btnImportHkl_actionPerformed(ActionEvent e) {
        //lines like this:
        //0  -1  -1  26042. 547.139   1
        FileNameExtensionFilter[] filter = {new FileNameExtensionFilter("HKL file", "hkl", "HKL")};
        File hklfile = FileUtils.fchooserOpen(this,new File(D2Dplot_global.getWorkdir()), filter, 0);
        if(hklfile==null)return;
        D2Dplot_global.setWorkdir(hklfile);
        
        String cell = txtCellParameters.getText().trim();
        String[] cellp = cell.split("\\s+");
        float a,b,c,alfa,beta,gamma;
        try{
            a = Float.parseFloat(cellp[0]);
            b = Float.parseFloat(cellp[1]);
            c = Float.parseFloat(cellp[2]);
            alfa = Float.parseFloat(cellp[3]);
            beta = Float.parseFloat(cellp[4]);
            gamma = Float.parseFloat(cellp[5]);
        }catch(Exception ex){
            if (D2Dplot_global.isDebug())ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Cell parameters needed (a b c alpha beta gamma) to parse hkl file");
            return;
        }
        
        Scanner shkl;
        ArrayList<PDReflection> refs = new ArrayList<PDReflection>();
        try {
            shkl = new Scanner(hklfile);
            while (shkl.hasNextLine()){
                String line = shkl.nextLine();
                String[] values = line.split("\\s+");
                try{
                    int h = Integer.parseInt(values[0]);
                    int k = Integer.parseInt(values[1]);
                    int l = Integer.parseInt(values[2]);
                    float inten = Integer.parseInt(values[3]);
                    refs.add(new PDReflection(h,k,l,-1,inten));
                }catch(Exception ex2){
                    if (D2Dplot_global.isDebug())ex2.printStackTrace();
                    log.warning("error parsing values");
                }
            }
            ImgOps.getDspacingFromHKL(refs, a, b, c, alfa, beta, gamma);
            shkl.close();
        } catch (Exception ex) {
            if (D2Dplot_global.isDebug())ex.printStackTrace();
            log.warning("error reading file");
        }
        
        if (refs.size()==0){
            log.warning("no reflections found");
            return;
        }
        
        //calculem el factor de normalitzacio de les intensitats a 100
        Iterator<PDReflection> itrr = refs.iterator();
        float maxInten = -1;
        while (itrr.hasNext()){
            PDReflection p = itrr.next();
            Float pinten = p.getInten();
            if(pinten>maxInten){
                maxInten = pinten;
            }
        }
        float factor = 100/maxInten;
        
        //Ara ja escribim al textarea
        textAreaDsp.setText("");
        itrr = refs.iterator();
        while (itrr.hasNext()){
            PDReflection p = itrr.next();
            textAreaDsp.append(String.format("%4d %4d %4d %8.4f %8.2f\n", p.getH(),p.getK(),p.getL(),p.getDsp(),p.getInten()*factor));
        }
    }
    
    protected void do_btnImportCif_actionPerformed(ActionEvent e) {
        FileNameExtensionFilter[] filter = {new FileNameExtensionFilter("CIF file", "cif", "CIF")};
        File ciffile = FileUtils.fchooserOpen(this,new File(D2Dplot_global.getWorkdir()), filter, 0);
        if(ciffile==null)return;
        D2Dplot_global.setWorkdir(ciffile);
        Cif_file cf = new Cif_file(ciffile,true);
        
        //show a dialog with the important fields, to check and correct them if necessary  -- MOGUT a CIFFILE
//        ImportCIFdialog cifdiag = new ImportCIFdialog(cf);
//        cifdiag.setVisible(true);
//        cifdiag.setAlwaysOnTop(true);
//        double a = cifdiag.getA();
//        log.writeNameNumPairs("config", true, "a", a);
        
        //populate fields
        txtName.setText(cf.getNom());
        txtNamealt.setText("");
        txtFormula.setText("");
        txtCellParameters.setText(cf.getCellParametersAsString());
        txtSpaceGroup.setText(cf.getSgString());
        txtReference.setText("");
        txtComment.setText("");
        if (cf.getSgNum()==0) return;
        //else calculem reflexions, utilitzem directament cf que ha estat corregit si era necessari
        Cell cel = new Cell(cf);
        cel.latgen(1.05f);
        cel.calcInten(true);
        cel.normIntensities(100);
        this.textAreaDsp.setText("");
        this.textAreaDsp.setText(cel.getListAsString_HKLMerged_dsp_Fc2());
    }
    
    protected void do_btnCalcRefl_actionPerformed(ActionEvent e) {
        SpaceGroup sg = new SpaceGroup(txtSpaceGroup.getText().trim());
        if (sg.getSGnum()<=0) {
//            FileUtils.InfoDialog(this, "SG not found, calculation of reflections not possible", "SG not found");
            return; //el info dialog esta dins de SpaceGroup
        }
        String cell = txtCellParameters.getText().trim();
        String[] cellp = cell.split("\\s+");
        float a,b,c,alfa,beta,gamma;
        try{
            a = Float.parseFloat(cellp[0]);
            b = Float.parseFloat(cellp[1]);
            c = Float.parseFloat(cellp[2]);
            alfa = Float.parseFloat(cellp[3]);
            beta = Float.parseFloat(cellp[4]);
            gamma = Float.parseFloat(cellp[5]);
        }catch(Exception ex){
            if (D2Dplot_global.isDebug())ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Cell parameters needed (a b c alpha beta gamma) to parse hkl file");
            return;
        }
        Cell cel = new Cell(a,b,c,alfa,beta,gamma,sg.getSGnum());
        cel.latgen(1.0f);
        
        this.textAreaDsp.setText("");
        this.textAreaDsp.setText(cel.getListAsString_HKLMerged_dsp_Fc2());
    }
    
    
    protected void do_this_windowClosing(WindowEvent e) {
        //SECOND SAVE DB FILE IF MODIFIED
//        log.debug("window closing event entered");
        checkSaveAndDispose();
    }
    
    private void checkSaveAndDispose() {
        if(PDDatabase.isDBmodified()){
            //prompt and save QL file if necessary
            int save = FileUtils.YesNoCancelDialog(this, "Database has changed, Do you want to save it?");
            if (save==1) {
                this.do_btnSaveDb_actionPerformed(null);
            }
            if (save==-1) {
                return; //not save nor exit
            }
        }
        this.dispose();
    }
}