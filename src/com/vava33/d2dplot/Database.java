package com.vava33.d2dplot;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
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
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.ProgressMonitor;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.vava33.cellsymm.Cell;
import com.vava33.cellsymm.CellSymm_global;
import com.vava33.cellsymm.HKLrefl;
import com.vava33.cellsymm.SpaceGroup;
// import com.vava33.cellsymm.Cell;
// import com.vava33.cellsymm.SpaceGroup;
import com.vava33.d2dplot.auxi.FilteredListModel;
import com.vava33.d2dplot.auxi.PDCompound;
import com.vava33.d2dplot.auxi.PDDatabase;
import com.vava33.d2dplot.auxi.PDSearchResult;
import com.vava33.jutils.Cif_file;
import com.vava33.jutils.FileUtils;
import com.vava33.jutils.VavaLogger;

import net.miginfocom.swing.MigLayout;

public class Database {

    private static float minDspacingToSearch = 1.15f; //def 1.15
    private static float minDspacingLatGen = 1.05f; //def 1.05
    private static final int maxNsol = 50;

    private final JDialog DBdialog;
    private JButton btnLoadDB;
    private JCheckBox cbox_onTop;
    private JCheckBox chckbxPDdata;
    private final JPanel contentPanel;
    private JLabel lblHelp;
    private JList<Object> listCompounds;
    private JPanel panel_left;
    private JScrollPane scrollPane;

    private DefaultListModel<Object> lm;
    private boolean showPDDataRings;
    private static JProgressBar pBarDB;
    private ProgressMonitor pm;
    private PDDatabase.OpenDBfileWorker openDBFwk;
    private PDDatabase.SaveDBfileWorker saveDBFwk;
    private PDDatabase.SearchDBWorker searchDBwk;
    private JButton btnSearchByPeaks;
    private JCheckBox chckbxNameFilter;
    private JTextField txtNamefilter;

    private JButton btnResetSearch;
    private JButton btnSaveDb;
    private JLabel lblHeader;
    private JPanel panel;
    private JPanel panel_1;
    private JButton btnAddCompound;
    private static final String className = "DB";
    private static VavaLogger log = D2Dplot_global.getVavaLogger(className);

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
//    private PDCompound currCompound;
    private JSplitPane splitPane_1;

    /**
     * Create the dialog.
     */
    public Database(JFrame parent, ImagePanel ip) {
        this.DBdialog = new JDialog(parent, "Compound DB", false);
        this.contentPanel = new JPanel();
        this.DBdialog.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                Database.this.do_this_windowClosing(e);
            }
        });
        this.setIpanel(ip);

        this.DBdialog.setIconImage(Toolkit.getDefaultToolkit().getImage(Database.class.getResource("/img/Icona.png")));
        this.DBdialog.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        final Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        final int width = 660;
        final int height = 730;
        final int x = (screen.width - width) / 2;
        final int y = (screen.height - height) / 2;
        this.DBdialog.setBounds(x, y, width, height);
        this.DBdialog.getContentPane().setLayout(new MigLayout("fill, insets 5", "[grow]", "[grow][37px]"));
        this.DBdialog.getContentPane().add(this.contentPanel, "cell 0 0,grow");
        this.contentPanel.setLayout(new MigLayout("fill, insets 0", "[grow]", "[598px,grow]"));
        {
            {
                this.panel_left = new JPanel();
                this.contentPanel.add(this.panel_left, "cell 0 0,grow");
                {
                    {
                        {
                            this.panel_left.setLayout(new MigLayout("fill, insets 0", "[grow]", "[25px][grow]"));
                            {
                                this.panel = new JPanel();
                                this.panel_left.add(this.panel, "cell 0 0,grow");
                                this.panel.setLayout(new MigLayout("fill, insets 0", "[][][][grow][][]", "[]"));
                                this.btnLoadDB = new JButton("Load DB");
                                this.panel.add(this.btnLoadDB, "cell 0 0,growx,aligny center");
                                {
                                    this.btnSaveDb = new JButton("Save DB");
                                    this.panel.add(this.btnSaveDb, "cell 1 0,alignx center,aligny center");
                                    this.chckbxPDdata = new JCheckBox("ShowRings");
                                    this.panel.add(this.chckbxPDdata, "cell 2 0,alignx left,aligny center");
                                    this.chckbxPDdata.addItemListener(new ItemListener() {
                                        @Override
                                        public void itemStateChanged(ItemEvent arg0) {
                                            Database.this.do_chckbxCalibrate_itemStateChanged(arg0);
                                        }
                                    });
                                    this.chckbxPDdata.setSelected(true);
                                    {
                                        this.lblHelp = new JLabel("?");
                                        this.panel.add(this.lblHelp, "cell 4 0,alignx right,aligny center");
                                        this.lblHelp.addMouseListener(new MouseAdapter() {
                                            @Override
                                            public void mouseEntered(MouseEvent e) {
                                                Database.this.do_lbllist_mouseEntered(e);
                                            }

                                            @Override
                                            public void mouseExited(MouseEvent e) {
                                                Database.this.do_lbllist_mouseExited(e);
                                            }

                                            @Override
                                            public void mouseReleased(MouseEvent e) {
                                                Database.this.do_lbllist_mouseReleased(e);
                                            }
                                        });
                                        this.lblHelp.setFont(new Font("Tahoma", Font.BOLD, 14));
                                    }
                                    this.cbox_onTop = new JCheckBox("on top");
                                    this.panel.add(this.cbox_onTop, "cell 5 0,alignx right,aligny center");
                                    this.cbox_onTop.setHorizontalTextPosition(SwingConstants.LEADING);
                                    this.cbox_onTop.addItemListener(new ItemListener() {
                                        @Override
                                        public void itemStateChanged(ItemEvent arg0) {
                                            Database.this.do_cbox_onTop_itemStateChanged(arg0);
                                        }
                                    });
                                    this.cbox_onTop.setActionCommand("on top");
                                    this.btnSaveDb.addActionListener(new ActionListener() {
                                        @Override
                                        public void actionPerformed(ActionEvent arg0) {
                                            Database.this.do_btnSaveDb_actionPerformed(arg0);
                                        }
                                    });
                                }
                                this.btnLoadDB.addActionListener(new ActionListener() {
                                    @Override
                                    public void actionPerformed(ActionEvent arg0) {
                                        Database.this.do_btnLoadDB_actionPerformed(arg0);
                                    }
                                });
                            }
                        }
                    }
                }
                {
                    this.splitPane_1 = new JSplitPane();
                    this.splitPane_1.setResizeWeight(0.5);
                    this.splitPane_1.setContinuousLayout(true);
                    this.panel_left.add(this.splitPane_1, "cell 0 1,grow");
                    {
                        this.panel_3 = new JPanel();
                        this.splitPane_1.setRightComponent(this.panel_3);
                        this.panel_3.setLayout(new MigLayout("", "[][grow][]", "[][][][][][][][][][grow][]"));
                        {
                            this.btnImportCif = new JButton("Import CIF");
                            this.btnImportCif.addActionListener(new ActionListener() {
                                @Override
                                public void actionPerformed(ActionEvent e) {
                                    Database.this.do_btnImportCif_actionPerformed(e);
                                }
                            });
                            this.panel_3.add(this.btnImportCif, "cell 1 0,alignx right");
                        }
                        {
                            this.btnImportHkl = new JButton("Import HKL");
                            this.btnImportHkl.addActionListener(new ActionListener() {
                                @Override
                                public void actionPerformed(ActionEvent e) {
                                    Database.this.do_btnImportHkl_actionPerformed(e);
                                }
                            });
                            this.panel_3.add(this.btnImportHkl, "cell 2 0,alignx right");
                        }
                        {
                            this.lblName = new JLabel("Name");
                            this.panel_3.add(this.lblName, "cell 0 1,alignx trailing");
                        }
                        {
                            this.txtName = new JTextField();
                            this.txtName.setText("Name");
                            this.panel_3.add(this.txtName, "cell 1 1 2 1,growx");
                            this.txtName.setColumns(10);
                        }
                        {
                            this.lblNamealt = new JLabel("Name (alt)");
                            this.panel_3.add(this.lblNamealt, "cell 0 2,alignx trailing");
                        }
                        {
                            this.txtNamealt = new JTextField();
                            this.txtNamealt.setText("NameAlt");
                            this.panel_3.add(this.txtNamealt, "cell 1 2 2 1,growx");
                            this.txtNamealt.setColumns(10);
                        }
                        {
                            this.lblFormula = new JLabel("Formula");
                            this.panel_3.add(this.lblFormula, "cell 0 3,alignx trailing");
                        }
                        {
                            this.txtFormula = new JTextField();
                            this.txtFormula.setText("Formula");
                            this.panel_3.add(this.txtFormula, "cell 1 3 2 1,growx");
                            this.txtFormula.setColumns(10);
                        }
                        {
                            this.lblCellParameters = new JLabel("Cell parameters");
                            this.panel_3.add(this.lblCellParameters, "cell 0 4,alignx trailing");
                        }
                        {
                            this.txtCellParameters = new JTextField();
                            this.txtCellParameters.setText("Cell Parameters");
                            this.panel_3.add(this.txtCellParameters, "cell 1 4 2 1,growx");
                            this.txtCellParameters.setColumns(10);
                        }
                        {
                            this.lblSpaceGroup = new JLabel("Space group");
                            this.panel_3.add(this.lblSpaceGroup, "cell 0 5,alignx trailing");
                        }
                        {
                            this.txtSpaceGroup = new JTextField();
                            this.txtSpaceGroup.setText("Space Group");
                            this.panel_3.add(this.txtSpaceGroup, "cell 1 5,growx");
                            this.txtSpaceGroup.setColumns(10);
                        }
                        {
                            this.btnCalcRefl = new JButton("Calc Refl.");
                            this.btnCalcRefl.addActionListener(new ActionListener() {
                                @Override
                                public void actionPerformed(ActionEvent e) {
                                    Database.this.do_btnCalcRefl_actionPerformed(e);
                                }
                            });
                            this.panel_3.add(this.btnCalcRefl, "cell 2 5,growx");
                        }
                        {
                            this.lblReference = new JLabel("Reference");
                            this.panel_3.add(this.lblReference, "cell 0 6,alignx trailing");
                        }
                        {
                            this.txtReference = new JTextField();
                            this.txtReference.setText("Reference");
                            this.panel_3.add(this.txtReference, "cell 1 6 2 1,growx");
                            this.txtReference.setColumns(10);
                        }
                        {
                            this.lblComment = new JLabel("Comment");
                            this.panel_3.add(this.lblComment, "cell 0 7,alignx trailing");
                        }
                        {
                            this.txtComment = new JTextField();
                            this.txtComment.setText("Comment");
                            this.panel_3.add(this.txtComment, "cell 1 7 2 1,growx");
                            this.txtComment.setColumns(10);
                        }
                        {
                            this.label = new JLabel("list of (one per line): h k l d-spacing intensity");
                            this.panel_3.add(this.label, "cell 0 8 3 1,alignx left");
                        }
                        {
                            this.scrollPane_2 = new JScrollPane();
                            this.panel_3.add(this.scrollPane_2, "cell 0 9 3 1,grow");
                            {
                                this.textAreaDsp = new JTextArea();
                                this.scrollPane_2.setViewportView(this.textAreaDsp);
                                this.textAreaDsp.setRows(3);
                            }
                        }
                        {
                            this.btnApplyChanges = new JButton("Apply Changes");
                            this.btnApplyChanges.addActionListener(new ActionListener() {
                                @Override
                                public void actionPerformed(ActionEvent e) {
                                    Database.this.do_btnApplyChanges_actionPerformed(e);
                                }
                            });
                            this.btnAddToQuicklist = new JButton("Add to Quicklist");
                            this.panel_3.add(this.btnAddToQuicklist, "cell 0 10,alignx left");
                            this.btnAddToQuicklist.addActionListener(new ActionListener() {
                                @Override
                                public void actionPerformed(ActionEvent e) {
                                    Database.this.do_btnAddToQuicklist_actionPerformed(e);
                                }
                            });
                            this.panel_3.add(this.btnApplyChanges, "cell 1 10,alignx right");
                        }
                        {
                            this.btnAddAsNew = new JButton("Add as New");
                            this.btnAddAsNew.addActionListener(new ActionListener() {
                                @Override
                                public void actionPerformed(ActionEvent e) {
                                    Database.this.do_btnAddAsNew_actionPerformed(e);
                                }
                            });
                            this.panel_3.add(this.btnAddAsNew, "cell 2 10");
                        }
                    }
                    {
                        this.panel_1 = new JPanel();
                        this.splitPane_1.setLeftComponent(this.panel_1);
                        this.panel_1.setLayout(new MigLayout("fill", "[][][grow][]", "[][][grow][]"));
                        {
                            this.chckbxNameFilter = new JCheckBox("Apply name filter:");
                            this.panel_1.add(this.chckbxNameFilter, "cell 0 0 2 1,alignx left");
                        }
                        this.txtNamefilter = new JTextField();
                        this.panel_1.add(this.txtNamefilter, "cell 2 0 2 1,growx,aligny center");

                        this.txtNamefilter.getDocument().addDocumentListener(new DocumentListener() {
                            @Override
                            public void changedUpdate(DocumentEvent e) {
                                Database.this.filterList();
                            }

                            @Override
                            public void removeUpdate(DocumentEvent e) {
                                Database.this.filterList();
                            }

                            @Override
                            public void insertUpdate(DocumentEvent e) {
                                Database.this.filterList();
                            }
                        });
                        {
                            this.lblHeader = new JLabel("header");
                            this.panel_1.add(this.lblHeader, "cell 0 1 4 1,alignx left");
                        }
                        {
                            this.scrollPane = new JScrollPane();
                            this.panel_1.add(this.scrollPane, "cell 0 2 4 1,grow");
                            {
                                this.listCompounds = new JList<Object>();
                                this.listCompounds.setFont(new Font("Monospaced", Font.PLAIN, 15));
                                this.listCompounds.addListSelectionListener(new ListSelectionListener() {
                                    @Override
                                    public void valueChanged(ListSelectionEvent arg0) {
                                        Database.this.do_listCompounds_valueChanged(arg0);
                                    }
                                });
                                this.listCompounds.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
                                this.scrollPane.setViewportView(this.listCompounds);
                            }
                        }
                        {
                            this.btnAddCompound = new JButton("New");
                            this.panel_1.add(this.btnAddCompound, "cell 0 3,growx");
                            {
                                this.btnRemove = new JButton("Remove");
                                this.btnRemove.addActionListener(new ActionListener() {
                                    @Override
                                    public void actionPerformed(ActionEvent e) {
                                        Database.this.do_btnRemove_actionPerformed(e);
                                    }
                                });
                                this.panel_1.add(this.btnRemove, "cell 1 3,alignx left");
                            }
                            this.btnSearchByPeaks = new JButton("Search by peaks");
                            this.panel_1.add(this.btnSearchByPeaks, "cell 2 3,alignx right");
                            this.btnSearchByPeaks.addActionListener(new ActionListener() {
                                @Override
                                public void actionPerformed(ActionEvent e) {
                                    Database.this.do_btnSearchByPeaks_actionPerformed(e);
                                }
                            });
                            this.btnResetSearch = new JButton("reset list");
                            this.panel_1.add(this.btnResetSearch, "cell 3 3,alignx center");
                            this.btnResetSearch.addActionListener(new ActionListener() {
                                @Override
                                public void actionPerformed(ActionEvent arg0) {
                                    Database.this.do_btnResetSearch_actionPerformed(arg0);
                                }
                            });
                            {
                                //                                splitPane_1.setDividerLocation(375);
                                this.splitPane_1.setDividerLocation(this.DBdialog.getWidth() / 2);
                            }
                            this.btnAddCompound.addActionListener(new ActionListener() {
                                @Override
                                public void actionPerformed(ActionEvent arg0) {
                                    Database.this.do_btnAddCompound_actionPerformed(arg0);
                                }
                            });
                        }
                    }
                }
                {
                }
            }
        }
        {
            final JPanel buttonPane = new JPanel();
            this.DBdialog.getContentPane().add(buttonPane, "cell 0 1,growx,aligny top");
            final JButton okButton = new JButton("close");
            okButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent arg0) {
                    Database.this.do_okButton_actionPerformed(arg0);
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
            this.DBdialog.getRootPane().setDefaultButton(okButton);
        }
        this.DBdialog.setAlwaysOnTop(this.cbox_onTop.isSelected());
        log.info("** PDDatabase **");
        this.DBdialog.pack();
        this.inicia();
    }

    public void inicia() {
        //        this.setPatt2d(this.getIpanel().getPatt2D());
        this.lm = new DefaultListModel<Object>();
        this.listCompounds.setModel(this.lm);
        //        tAOut.ln(" Reading default database: "+PDDatabase.getDefaultDBpath());//TODO no cal dir-ho ja que es diu al llegir-ho
        this.readDB(true);
    }

    //es una manera de posar al log tot el que surt pel txtArea local
    //	public void printTaOut(String msg) {
    //        tAOut.stat(msg);
    //		log.debug(msg);
    //	}

    private void readDB(boolean readDefault) {

        //primer creem el progress monitor,
        this.pm = new ProgressMonitor(this.DBdialog, "Reading DB file...", "", 0, 100);
        this.pm.setProgress(0);
        pBarDB.setString("Reading DB");
        pBarDB.setStringPainted(true);

        //db per defecte:
        File DBFile = new File(PDDatabase.getDefaultDBpath());

        if (!readDefault) {
            //Load file
            final FileNameExtensionFilter[] filter = {
                    new FileNameExtensionFilter("DB file (db,txt,dat)", "db", "txt", "dat") };
            DBFile = FileUtils.fchooserOpen(this.DBdialog, new File(D2Dplot_global.getWorkdir()), filter, 0);
            if (DBFile == null) {
                log.warning("No data file selected");
                return;
            }
            D2Dplot_global.setWorkdir(DBFile);
        }

        //Si hem arribat aquí creem el worker, hi afegim el listener
        this.openDBFwk = new PDDatabase.OpenDBfileWorker(DBFile, false);
        this.openDBFwk.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                //log.debug("hello from propertyChange");
                log.debug(evt.getPropertyName());
                if ("progress" == evt.getPropertyName()) {
                    final int progress = (Integer) evt.getNewValue();
                    Database.this.pm.setProgress(progress);
                    pBarDB.setValue(progress);
                    Database.this.pm.setNote(String.format("%d%%\n", progress));
                    if (Database.this.pm.isCanceled() || Database.this.openDBFwk.isDone()) {
                        Toolkit.getDefaultToolkit().beep();
                        if (Database.this.pm.isCanceled()) {
                            Database.this.openDBFwk.cancel(true);
                            log.info("reading of DB file " + Database.this.openDBFwk.getReadedFile() + " stopped!");
                            log.info("Number of compounds = " + PDDatabase.getnCompounds());
                        } else {
                            log.info("reading of DB file " + Database.this.openDBFwk.getReadedFile() + " finished!");
                            log.info("Number of compounds = " + PDDatabase.getnCompounds());
                        }
                        Database.this.pm.close();
                        pBarDB.setValue(100);
                        pBarDB.setStringPainted(true);
                        Database.this.updateListAllCompounds();
                        PDDatabase.setDBmodified(false);
                        //startButton.setEnabled(true);
                    }
                }
            }
        });

        //reset current Database
        PDDatabase.resetDB();
        //read database file, executing the swingworker task
        this.openDBFwk.execute();
        if (!new File(D2Dplot_global.DBfile).getName().equalsIgnoreCase(DBFile.getName())) {
            //ask if this new file should become the default one on config
            final boolean defDB = FileUtils.YesNoDialog(this.DBdialog,
                    "Set this DB file as the default for further sessions?");
            if (defDB) {
                D2Dplot_global.DBfile = DBFile.getAbsolutePath();
            }
        }
    }

    //boolean ask for default or not
    protected void do_btnLoadDB_actionPerformed(ActionEvent arg0) {
        this.readDB(false);
    }

    protected void do_cbox_onTop_itemStateChanged(ItemEvent arg0) {
        this.DBdialog.setAlwaysOnTop(this.cbox_onTop.isSelected());
    }

    protected void do_chckbxCalibrate_itemStateChanged(ItemEvent arg0) {
        this.showPDDataRings = this.chckbxPDdata.isSelected();
        this.getIpanel().setShowDBCompoundRings(this.isShowDataRings(), this.getCurrentCompound());
    }

    protected void do_lbllist_mouseEntered(MouseEvent e) {
        // lbllist.setText("<html><font color='red'>?</font></html>");
        this.lblHelp.setForeground(Color.red);
    }

    protected void do_lbllist_mouseExited(MouseEvent e) {
        this.lblHelp.setForeground(Color.black);
    }

    protected void do_lbllist_mouseReleased(MouseEvent e) {
        final String msg = "\n" + "** General help **\n"
                + " - Click on a compound to see the rings on the image (if ShowRings is selected)\n"
                + " - Check apply name filter and type to find the desired compound\n"
                + " - Add/Edit compounds by clicking the respective buttons and filling the info. Alternatively you can edit manually the DB file\n"
                + "(which is a simple self-explanatory text file)\n"
                + " - Add to QuickList (QL) to access the rings from the main window directly. Compounds in the QL are saved in a separate file\n"
                + "with the same format as the DB file and can also be edited the same way\n"
                + "** Search by peaks **\n"
                + " - On the main window click on the desired rings so that they are selected in the point list (Sel.points should be active)\n"
                + " - Click the button -search by peaks-\n"
                + " - List will be updated by the best matching compounds (with respective residuals)\n"
                + " - Click on the compounds to see the rings on top of your image and check if they really match\n"
                + "\n" + "Note:\n"
                + "The default DB is a small selection of compounds taken from different sources, mostly publications. Each entry contains the reference from\n"
                + "where it has been taken (with the respective authors) which can be retrieved by clicking -compound info- or by editing the compound.\n"
                + "For any doubts/comments/complaints/suggestions, please contact the author\n" + "\n";
        FileUtils.InfoDialog(this.DBdialog, msg, "Database Help");
    }

    protected void do_okButton_actionPerformed(ActionEvent arg0) {
        this.checkSaveAndDispose();
    }

    public PDCompound getCurrentCompound() {
        if (this.listCompounds == null) {
            return null;
        }
        if (this.listCompounds.getSelectedIndex() >= 0) {
            if (this.listCompounds.getSelectedValue() instanceof PDCompound) {
                final PDCompound comp = (PDCompound) this.listCompounds.getSelectedValue();
//                this.currCompound = comp;
                return comp;
            }
            if (this.listCompounds.getSelectedValue() instanceof PDSearchResult) {
                final PDSearchResult sr = (PDSearchResult) this.listCompounds.getSelectedValue();
//                this.currCompound = sr.getC();
                return sr.getC();
            }
        }
        return null;
    }

    public boolean isShowDataRings() {
        return this.showPDDataRings;
    }

    public void updateListAllCompounds() {
        pBarDB.setString("Updating DB");
        pBarDB.setStringPainted(true);
        this.lm.clear();
        final Iterator<PDCompound> itrcomp = PDDatabase.getDBCompList().iterator();
        int n = 0;
        final int ncomp = PDDatabase.getDBCompList().size();
        while (itrcomp.hasNext()) {
            final PDCompound c = itrcomp.next();
            this.lm.addElement(c);
            //progress
            if (n % 100 == 0) {
                pBarDB.setValue((int) (((float) n / (float) ncomp) / 100.f));
            }
            n++;
        }
        this.lblHeader.setText(" Name  [Formula]  (alt. names)");
        pBarDB.setValue(100);
        pBarDB.setStringPainted(false);
        this.listCompounds.setSelectedIndex(0);//TODO: mirar que no s'hagi de cambiar a algun altre lloc
    }

    public boolean isShowPDDataRings() {
        return this.showPDDataRings;
    }

    public void setShowPDDataRings(boolean showPDDataRings) {
        this.showPDDataRings = showPDDataRings;
    }

    protected void do_listCompounds_valueChanged(ListSelectionEvent arg0) {
        if (arg0.getValueIsAdjusting())
            return;

        //Test for changes in compound ---- Apr 2019 removed, it was confusing
//        if (this.currCompound != null) {
//            final PDCompound oldCompound = this.currCompound;
//            final PDCompound edited = new PDCompound("aa");
//            this.updateCompoundFromFields(edited, false);
//            if (edited.compareTo(oldCompound) != 0) {
//                final boolean update = FileUtils.YesNoDialog(this.DBdialog,
//                        "Previous compound had changed, update it?");
//                if (update) {
//                    this.updateCompoundFromFields(oldCompound, true);
//                    PDDatabase.setDBmodified(true);
//                }
//            }
//        }

        final PDCompound comp = this.getCurrentCompound();
        this.getIpanel().setShowDBCompoundRings(this.isShowDataRings(), comp);
        if (comp != null) {
            //            tAOut.ln(comp.printInfo2Line());
            //fill the fields of DB
            this.updateInfo(comp);
        }
        this.getIpanel().actualitzarVista();
    }

    public void updateInfo(PDCompound c) {
        this.txtName.setText(c.getCompName().get(0));
        this.txtNamealt.setText(c.getAltNames());
        this.txtFormula.setText(c.getFormula());
        this.txtCellParameters.setText(c.getCellParameters());
        this.txtSpaceGroup.setText(c.getCella().getSg().getName());
        this.txtReference.setText(c.getReference());
        this.txtComment.setText(c.getAllComments());
        this.textAreaDsp.setText(c.getHKLlines());
    }

    public void prepareFields() {
        this.txtName.setText("");
        this.txtNamealt.setText("");
        this.txtFormula.setText("");
        this.txtCellParameters.setText("");
        this.txtSpaceGroup.setText("");
        this.txtReference.setText("");
        this.txtComment.setText("");
    }

    //CANVIEM PER NO UTILITZAR EL LM AMB TOTS ELS COMPOSTOS  -- AL FINAL NO, fem boto per tornar a mostrar tots
    public void loadSearchPeaksResults() {

        if (this.lm == null) {
            return;
        }
        if (PDDatabase.getDBSearchresults().size() == 0) {
            return;
        }

        //aqui en principi tindrem una llista de resultats a PDDatabase i s'haurà de mostrar
        this.lm.clear();

        final ArrayList<PDSearchResult> res = PDDatabase.getDBSearchresults();

        //mirem si hi ha criteris complementaris pel residual
        //        if (chckbxIntensityInfo.isSelected() || chckbxNpksInfo.isSelected()){
        Iterator<PDSearchResult> itrcomp = res.iterator();
        while (itrcomp.hasNext()) {
            final PDSearchResult c = itrcomp.next();
            float resid = c.getResidualPositions();
            //                if (chckbxIntensityInfo.isSelected()){
            //                    resid = resid + c.getResidual_intensities();
            //                }
            //                if (chckbxNpksInfo.isSelected()){
            //                    resid = resid * ((Math.max((float)c.getC().getNrRefUpToDspacing(PDSearchResult.getMinDSPin())/(float)PDSearchResult.getnDSPin(),1))/2);
            //                }
            resid = resid * ((Math.max((float) c.getC().getNrRefUpToDspacing(PDSearchResult.getMinDSPin())
                    / (float) PDSearchResult.getnDSPin(), 1)) / 2);
            c.setTotal_residual(resid);
        }
        //        }
        Collections.sort(res);
        itrcomp = res.iterator();
        int nsol = 0;
        while (itrcomp.hasNext()) {
            if (nsol >= maxNsol)
                break;
            final PDSearchResult c = itrcomp.next();
            this.lm.addElement(c);
            nsol = nsol + 1;
        }
        this.lblHeader.setText(" Residual  inputRefs/compoundRefs  CompoundName  [Formula]  (alt. names)");
        this.getIpanel().actualitzarVista();
    }

    public void searchPeaks() {

        this.pm = new ProgressMonitor(this.DBdialog, "Searching for peak matching...", "", 0, 100);
        this.pm.setProgress(0);

        pBarDB.setString("Searching DB");
        pBarDB.setStringPainted(true);

        this.searchDBwk = new PDDatabase.SearchDBWorker(this.getIpanel().getPatt2D(), minDspacingToSearch);
        this.searchDBwk.addPropertyChangeListener(new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                //log.debug("hello from propertyChange");
                log.debug(evt.getPropertyName());
                if ("progress" == evt.getPropertyName()) {
                    final int progress = (Integer) evt.getNewValue();
                    Database.this.pm.setProgress(progress);
                    pBarDB.setValue(progress);
                    Database.this.pm.setNote(String.format("%d%%\n", progress));
                    if (Database.this.pm.isCanceled() || Database.this.searchDBwk.isDone()) {
                        Toolkit.getDefaultToolkit().beep();
                        if (Database.this.pm.isCanceled()) {
                            Database.this.searchDBwk.cancel(true);
                            Database.this.searchDBwk.setStop(true);
                            log.warning("search cancelled");
                        } else {
                            log.info("search finished!");
                            Database.this.loadSearchPeaksResults();
                        }
                        Database.this.pm.close();
                        pBarDB.setValue(100);
                        pBarDB.setStringPainted(false);
                        //startButton.setEnabled(true);
                    }
                }
            }
        });

        this.searchDBwk.execute();

    }

    //la faig nova passant PuntsCercles al swingworker
    protected void do_btnSearchByPeaks_actionPerformed(ActionEvent e) {
        if (this.getIpanel().getPatt2D().getPuntsCercles().isEmpty()) {
            log.info("Please select some peaks clicking in the image");
            return;
        }
        this.searchPeaks();
    }

    protected void filterList() {
        if (this.lm.isEmpty()) {
            return;
        }
        if (this.chckbxNameFilter.isSelected()) {
            //filter list
            final FilteredListModel filteredListModel = new FilteredListModel(this.lm);
            this.listCompounds.setModel(filteredListModel);
            filteredListModel.setFilter(new FilteredListModel.Filter() {
                @Override
                public boolean accept(Object element) {

                    PDCompound comp = null;
                    try {
                        comp = (PDCompound) element;
                    } catch (final Exception e) {
                        log.debug("trying searchresult...");
                        comp = ((PDSearchResult) element).getC();
                    }
                    if (comp == null)
                        return false;

                    final StringBuilder compinfo = new StringBuilder();
                    compinfo.append(comp.getCompName()).append(" ");
                    compinfo.append(comp.getFormula()).append(" ");
                    compinfo.append(comp.getAltNames()).append(" ");
                    compinfo.append(comp.getCellParameters()).append(" ");
                    compinfo.append(comp.getCella().getSg().getName()).append(" ");
                    compinfo.append(comp.getAllComments()).append(" ");

                    final String s = compinfo.toString().trim();

                    final String[] sMult = Database.this.txtNamefilter.getText().split("\\s+");
                    for (final String element2 : sMult) {
                        if (!FileUtils.containsIgnoreCase(s, element2)) {
                            return false;
                        }
                    }
                    return true;

                }
            });
            if (this.txtNamefilter.getText().trim().length() == 0) {
                this.listCompounds.setModel(this.lm);
                log.info("Number of compounds = " + this.lm.getSize());
            } else {
                log.info("Number of (filtered) compounds = " + filteredListModel.getSize());
            }
        }
        this.getIpanel().actualitzarVista();

    }

    protected void do_btnResetSearch_actionPerformed(ActionEvent arg0) {
        this.txtNamefilter.setText("");
        this.updateListAllCompounds();
        this.getIpanel().actualitzarVista();
    }

    protected void do_btnSaveDb_actionPerformed(ActionEvent arg0) {
        final FileNameExtensionFilter[] filter = { new FileNameExtensionFilter("DB files", "db", "DB") };
        final File f = FileUtils.fchooserSaveAsk(this.DBdialog, new File(PDDatabase.getCurrentDB()), filter, null);
        if (f == null)
            return;
        D2Dplot_global.setWorkdir(f);
        //primer creem el progress monitor,
        this.pm = new ProgressMonitor(this.DBdialog, "Saving DB file...", "", 0, 100);
        this.pm.setProgress(0);
        pBarDB.setString("Saving DB");
        pBarDB.setStringPainted(true);

        //Si hem arribat aquí creem el worker, hi afegim el listener
        this.saveDBFwk = new PDDatabase.SaveDBfileWorker(f, false);
        this.saveDBFwk.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                log.debug(evt.getPropertyName());
                if ("progress" == evt.getPropertyName()) {
                    final int progress = (Integer) evt.getNewValue();
                    Database.this.pm.setProgress(progress);
                    pBarDB.setValue(progress);
                    Database.this.pm.setNote(String.format("%d%%\n", progress));
                    if (Database.this.pm.isCanceled() || Database.this.saveDBFwk.isDone()) {
                        Toolkit.getDefaultToolkit().beep();
                        if (Database.this.pm.isCanceled()) {
                            Database.this.saveDBFwk.cancel(true);
                            log.warning("Error saving file " + Database.this.saveDBFwk.getDbFileString());
                        } else {
                            log.info("DB saved to " + Database.this.saveDBFwk.getDbFileString());
                        }
                        Database.this.pm.close();
                        pBarDB.setValue(100);
                        pBarDB.setStringPainted(true);
                        Database.this.updateListAllCompounds();
                        PDDatabase.setDBmodified(false);
                    }
                }
            }
        });

        this.saveDBFwk.execute();
        if (!new File(D2Dplot_global.DBfile).getName().equalsIgnoreCase(this.saveDBFwk.getDbFileString())) {
            //ask if this new file should become the default one on config
            final boolean defDB = FileUtils.YesNoDialog(this.DBdialog,
                    "Set this DB file as the default for further sessions?");
            if (defDB) {
                D2Dplot_global.DBfile = f.getAbsolutePath();
            }
        }
    }

    public ImagePanel getIpanel() {
        return this.ipanel;
    }

    public void setIpanel(ImagePanel ipanel) {
        this.ipanel = ipanel;
    }

    protected void do_btnAddToQuicklist_actionPerformed(ActionEvent e) {
        final PDCompound pdc = this.getCurrentCompound();
        if (pdc != null) {
            PDDatabase.addCompoundQL(this.getCurrentCompound(), true);
        }
    }

    public static float getMinDspacingToSearch() {
        return minDspacingToSearch;
    }

    public static void setMinDspacingToSearch(float minDspacingToSearch) {
        Database.minDspacingToSearch = minDspacingToSearch;
    }

    protected void do_btnApplyChanges_actionPerformed(ActionEvent e) {
        this.updateCompoundFromFields(this.getCurrentCompound(), true);
        this.updateListAllCompounds();
    }

    protected void do_btnAddAsNew_actionPerformed(ActionEvent e) {
        //ADD COMPOUND:
        final PDCompound co = new PDCompound(this.txtName.getText().trim());
        PDDatabase.addCompoundDB(co);
        this.updateCompoundFromFields(co, true);
        this.updateListAllCompounds();
        PDDatabase.setDBmodified(true);
        this.listCompounds.setSelectedIndex(this.listCompounds.getModel().getSize() - 1);
        this.scrollPane.validate();
        final JScrollBar vertical = this.scrollPane.getVerticalScrollBar();
        vertical.setValue(vertical.getMaximum());
    }

    protected void do_btnAddCompound_actionPerformed(ActionEvent arg0) {
        //ADD COMPOUND:
        final PDCompound co = new PDCompound("NEW COMPOUND");
        //        co.setDefParams();
        PDDatabase.addCompoundDB(co);
        //select the compound
        this.updateListAllCompounds();
        PDDatabase.setDBmodified(true);
        this.listCompounds.setSelectedIndex(this.listCompounds.getModel().getSize() - 1);
        this.scrollPane.validate();
        final JScrollBar vertical = this.scrollPane.getVerticalScrollBar();
        vertical.setValue(vertical.getMaximum());
    }

    protected void do_btnRemove_actionPerformed(ActionEvent e) {
        final boolean remove = FileUtils.YesNoDialog(this.DBdialog, "Remove selected Compound?");
        if (remove)
            PDDatabase.getDBCompList().remove(this.getCurrentCompound());
        this.updateListAllCompounds();
        PDDatabase.setDBmodified(true);
    }

    private boolean updateCompoundFromFields(PDCompound comp, boolean warningSave) {

        final String cell = this.txtCellParameters.getText().trim();
        final String[] cellp = cell.split("\\s+");
        float a, b, c, alfa, beta, gamma;
        try {
            a = Float.parseFloat(cellp[0]);
            b = Float.parseFloat(cellp[1]);
            c = Float.parseFloat(cellp[2]);
            alfa = Float.parseFloat(cellp[3]);
            beta = Float.parseFloat(cellp[4]);
            gamma = Float.parseFloat(cellp[5]);
        } catch (final Exception e) {
            if (D2Dplot_global.isDebug())
                e.printStackTrace();
            JOptionPane.showMessageDialog(this.DBdialog,
                    "Error parsing cell parameters, should be: a b c alpha beta gamma");
            return false;
        }
        if (this.txtName.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this.DBdialog, "Please give the compound name");
            return false;
        }

        final String[] hkl_lines = this.textAreaDsp.getText().trim().split("\\n");
        log.debug(Arrays.toString(hkl_lines));
        final ArrayList<HKLrefl> pdref = new ArrayList<HKLrefl>();
        //CHECK CONSISTENCY HKL
        for (final String hkl_line : hkl_lines) {
            final String[] line = hkl_line.trim().split("\\s+");
            if (line.length < 5) {
                JOptionPane.showMessageDialog(this.DBdialog, "Error in hkl lines, should be: h k l dspacing Intensity");
                return false;
            } else {
                try {
                    log.debug(Arrays.toString(line));
                    final int h = Integer.parseInt(line[0]);
                    final int k = Integer.parseInt(line[1]);
                    final int l = Integer.parseInt(line[2]);
                    final float dsp = Float.parseFloat(line[3]);
                    float inten = 0.0f;
                    try {
                        inten = Float.parseFloat(line[4]);
                    } catch (final Exception e2) {
                        log.debug("no intensity for reflection");
                    }
                    final HKLrefl refl = new HKLrefl(h, k, l, dsp, inten, 2);
                    pdref.add(refl);
                } catch (final Exception e) {
                    JOptionPane.showMessageDialog(this.DBdialog, "Error in parsing hkl lines, e.g: 1 0 0 12.5 100.0");
                    return false;
                }
            }
        }

        //now we put the info into COMP
        comp.getCompName().clear();
        comp.addCompoundName(this.txtName.getText().trim());
        comp.addCompoundName(this.txtNamealt.getText().trim());
        comp.setFormula(this.txtFormula.getText().trim());
        comp.getCella().setCellParameters(a, b, c, alfa, beta, gamma, true);
        comp.getCella().setSg(CellSymm_global.getSpaceGroupByName(this.txtSpaceGroup.getText().trim(), true));
        comp.setReference(this.txtReference.getText().trim());
        comp.getComment().clear();
        comp.addComent(this.txtComment.getText().trim());
        //dsp + intensities
        comp.getPeaks().clear();
        comp.setPeaks(pdref);

        //don't forget to save the DB file to keep changes for future openings.
        if (warningSave)
            JOptionPane.showMessageDialog(this.DBdialog,
                    "Do not forget to save the DB into a file \n(otherwise changes will be lost on close)");

        //        this.updateListAllCompounds();
        return true;
    }

    protected void do_btnImportHkl_actionPerformed(ActionEvent e) {
        //lines like this:
        //0  -1  -1  26042. 547.139   1
        final FileNameExtensionFilter[] filter = { new FileNameExtensionFilter("HKL file", "hkl", "HKL") };
        final File hklfile = FileUtils.fchooserOpen(this.DBdialog, new File(D2Dplot_global.getWorkdir()), filter, 0);
        if (hklfile == null)
            return;
        D2Dplot_global.setWorkdir(hklfile);

        //        String cell = txtCellParameters.getText().trim();
        //        String[] cellp = cell.split("\\s+");
        //        float a,b,c,alfa,beta,gamma;
        //        try{
        //            a = Float.parseFloat(cellp[0]);
        //            b = Float.parseFloat(cellp[1]);
        //            c = Float.parseFloat(cellp[2]);
        //            alfa = Float.parseFloat(cellp[3]);
        //            beta = Float.parseFloat(cellp[4]);
        //            gamma = Float.parseFloat(cellp[5]);
        //        }catch(Exception ex){
        //            if (D2Dplot_global.isDebug())ex.printStackTrace();
        //            JOptionPane.showMessageDialog(DBdialog, "Cell parameters needed (a b c alpha beta gamma) to parse hkl file");
        //            return;
        //        }

        Scanner shkl;
        final ArrayList<HKLrefl> refs = new ArrayList<HKLrefl>();
        try {
            shkl = new Scanner(hklfile);
            while (shkl.hasNextLine()) {
                final String line = shkl.nextLine();
                final String[] values = line.split("\\s+");
                try {
                    final int h = Integer.parseInt(values[0]);
                    final int k = Integer.parseInt(values[1]);
                    final int l = Integer.parseInt(values[2]);
                    final float inten = Integer.parseInt(values[3]);
                    refs.add(new HKLrefl(h, k, l, -1, inten));
                } catch (final Exception ex2) {
                    if (D2Dplot_global.isDebug())
                        ex2.printStackTrace();
                    log.warning("Error parsing h,k,l,intensity values");
                }
            }
            shkl.close();
        } catch (final Exception ex) {
            if (D2Dplot_global.isDebug())
                ex.printStackTrace();
            log.warning("Error reading HKL file");
        }

        if (refs.size() == 0) {
            log.warning("No reflections found");
            return;
        }

        //calculem el factor de normalitzacio de les intensitats a 100
        Iterator<HKLrefl> itrr = refs.iterator();
        float maxInten = -1;
        while (itrr.hasNext()) {
            final HKLrefl p = itrr.next();
            final float pinten = (float) p.getYcalc();
            if (pinten > maxInten) {
                maxInten = pinten;
            }
        }
        final float factor = 100 / maxInten;

        //Ara ja escribim al textarea
        this.textAreaDsp.setText("");
        itrr = refs.iterator();
        while (itrr.hasNext()) {
            final HKLrefl p = itrr.next();
            this.textAreaDsp.append(String.format("%4d %4d %4d %8.4f %8.2f\n", p.getH(), p.getK(), p.getL(), p.getDsp(),
                    p.getYcalc() * factor));
        }
    }

    protected void do_btnImportCif_actionPerformed(ActionEvent e) {
        final FileNameExtensionFilter[] filter = { new FileNameExtensionFilter("CIF file", "cif", "CIF") };
        final File ciffile = FileUtils.fchooserOpen(this.DBdialog, new File(D2Dplot_global.getWorkdir()), filter, 0);
        if (ciffile == null)
            return;
        D2Dplot_global.setWorkdir(ciffile);
        final Cif_file cf = new Cif_file(ciffile, true);

        //show a dialog with the important fields, to check and correct them if necessary  -- MOGUT a CIFFILE
        //        ImportCIFdialog cifdiag = new ImportCIFdialog(cf);
        //        cifdiag.setVisible(true);
        //        cifdiag.setAlwaysOnTop(true);
        //        double a = cifdiag.getA();
        //        log.writeNameNumPairs("config", true, "a", a);

        //populate fields
        this.txtName.setText(cf.getNom());
        this.txtNamealt.setText("");
        this.txtFormula.setText("");
        this.txtCellParameters.setText(cf.getCellParametersAsString());
        this.txtSpaceGroup.setText(cf.getSgString());
        this.txtReference.setText("");
        this.txtComment.setText("");
        if (cf.getSgNum() == 0)
            return;
        //else calculem reflexions, utilitzem directament cf que ha estat corregit si era necessari
        final Cell cel = new Cell(cf);
        cel.generateHKLsAsymetricUnitCrystalFamily(1 / (minDspacingLatGen * minDspacingLatGen), true, true, true, true,
                true);
        cel.calcInten(true);
        cel.normIntensities(100);
        this.textAreaDsp.setText("");
        this.textAreaDsp.setText(cel.getListAsString_HKLMerged_dsp_Fc2());
        log.debug(cel.getListAsString_HKLMerged_tth_mult_Fc2(0.4246f));
    }

    protected void do_btnCalcRefl_actionPerformed(ActionEvent e) {
        final SpaceGroup sg = CellSymm_global.getSpaceGroupByName(this.txtSpaceGroup.getText().trim(), true);
        final String cell = this.txtCellParameters.getText().trim();
        final String[] cellp = cell.split("\\s+");
        float a, b, c, alfa, beta, gamma;
        try {
            a = Float.parseFloat(cellp[0]);
            b = Float.parseFloat(cellp[1]);
            c = Float.parseFloat(cellp[2]);
            alfa = Float.parseFloat(cellp[3]);
            beta = Float.parseFloat(cellp[4]);
            gamma = Float.parseFloat(cellp[5]);
        } catch (final Exception ex) {
            if (D2Dplot_global.isDebug())
                ex.printStackTrace();
            JOptionPane.showMessageDialog(this.DBdialog,
                    "Cell parameters needed (a b c alpha beta gamma) to parse hkl file");
            return;
        }
        final Cell cel = new Cell(a, b, c, alfa, beta, gamma, true, sg);
        cel.generateHKLsAsymetricUnitCrystalFamily(1, true, true, true, true, true);

        this.textAreaDsp.setText("");
        this.textAreaDsp.setText(cel.getListAsString_HKLMerged_dsp_Fc2());
    }

    protected void do_this_windowClosing(WindowEvent e) {
        //SECOND SAVE DB FILE IF MODIFIED
        //        log.debug("window closing event entered");
        this.checkSaveAndDispose();
    }

    private void checkSaveAndDispose() {
        if (PDDatabase.isDBmodified()) {
            //prompt and save QL file if necessary
            final int save = FileUtils.YesNoCancelDialog(this.DBdialog,
                    "Database has changed, Do you want to save it?");
            if (save == 1) {
                this.do_btnSaveDb_actionPerformed(null);
            }
            if (save == -1) {
                return; //not save nor exit
            }
        }
        this.dispose();
    }

    public void setVisible(boolean vis) {
        this.DBdialog.setVisible(vis);
        if (vis)
            this.chckbxPDdata.setSelected(true);
    }

    public void dispose() {
        this.chckbxPDdata.setSelected(false);
        this.DBdialog.dispose();
    }
}