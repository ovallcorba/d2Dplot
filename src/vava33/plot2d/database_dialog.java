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
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.Scanner;
import java.util.zip.ZipFile;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
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
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.WindowConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.vava33.jutils.FileUtils;
import com.vava33.jutils.LogJTextArea;
import com.vava33.jutils.VavaLogger;

import vava33.plot2d.auxi.ExZone;
import vava33.plot2d.auxi.FilteredListModel;
import vava33.plot2d.auxi.PDCompound;
import vava33.plot2d.auxi.PDDatabase;
import vava33.plot2d.auxi.Pattern2D;
import vava33.plot2d.auxi.PuntCercle;

import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.JProgressBar;

import org.apache.commons.math3.util.FastMath;

import java.awt.event.InputMethodListener;
import java.awt.event.InputMethodEvent;

public class database_dialog extends JDialog {

    private static final String localDB = "/latFiles/codDB.zip";
    private static final long serialVersionUID = -6104927797410689910L;
    private static boolean cod_acknowledged = false;
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
    private JLabel lblHeader;
    
    private int maxNsol = 50;
    private JButton btnResetSearch;
    
    /**
     * Create the dialog.
     */
    public database_dialog(MainFrame mf) {
        this.mf = mf;
        setIconImage(Toolkit.getDefaultToolkit().getImage(database_dialog.class.getResource("/img/Icona.png")));
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Phase Identification");
        // setBounds(100, 100, 660, 730);
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        int width = 660;
        int height = 730;
        int x = (screen.width - width) / 2;
        int y = (screen.height - height) / 2;
        setBounds(x, y, 600, 660);
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
            splitPane.setResizeWeight(0.7);
            this.splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
            GridBagConstraints gbc_splitPane = new GridBagConstraints();
            gbc_splitPane.fill = GridBagConstraints.BOTH;
            gbc_splitPane.gridx = 0;
            gbc_splitPane.gridy = 0;
            contentPanel.add(this.splitPane, gbc_splitPane);
            {
                this.panel_left = new JPanel();
                this.splitPane.setLeftComponent(this.panel_left);
                GridBagLayout gbl_panel_left = new GridBagLayout();
                gbl_panel_left.columnWidths = new int[] { 0, 0, 0, 0, 0 };
                gbl_panel_left.rowHeights = new int[] { 0, 0, 0, 0, 0, 0, 0 };
                gbl_panel_left.columnWeights = new double[] { 0.0, 0.0, 1.0, 0.0, 0.0 };
                gbl_panel_left.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, Double.MIN_VALUE };
                this.panel_left.setLayout(gbl_panel_left);
                {
                    {
                        this.chckbxPDdata = new JCheckBox("ShowRings");
                        this.chckbxPDdata.addItemListener(new ItemListener() {
                            @Override
                            public void itemStateChanged(ItemEvent arg0) {
                                do_chckbxCalibrate_itemStateChanged(arg0);
                            }
                        });
                        {
                            this.btnLoadDB = new JButton("Load Database");
                            this.btnLoadDB.addActionListener(new ActionListener() {
                                @Override
                                public void actionPerformed(ActionEvent arg0) {
                                    do_btnLoadDB_actionPerformed(arg0);
                                }
                            });
                            GridBagConstraints gbc_btnLoadDB = new GridBagConstraints();
                            gbc_btnLoadDB.fill = GridBagConstraints.HORIZONTAL;
                            gbc_btnLoadDB.insets = new Insets(5, 5, 5, 5);
                            gbc_btnLoadDB.gridx = 0;
                            gbc_btnLoadDB.gridy = 0;
                            this.panel_left.add(this.btnLoadDB, gbc_btnLoadDB);
                        }
                        this.chckbxPDdata.setSelected(true);
                        GridBagConstraints gbc_chckbxPDdata = new GridBagConstraints();
                        gbc_chckbxPDdata.anchor = GridBagConstraints.WEST;
                        gbc_chckbxPDdata.insets = new Insets(5, 0, 5, 5);
                        gbc_chckbxPDdata.gridx = 1;
                        gbc_chckbxPDdata.gridy = 0;
                        this.panel_left.add(this.chckbxPDdata, gbc_chckbxPDdata);
                    }
                }
                {
                    {
                        btnSearchByPeaks = new JButton("Search by peaks");
                        btnSearchByPeaks.addActionListener(new ActionListener() {
                            public void actionPerformed(ActionEvent e) {
                                do_btnSearchByPeaks_actionPerformed(e);
                            }
                        });
                        this.cbox_onTop = new JCheckBox("on top");
                        cbox_onTop.setSelected(true);
                        this.cbox_onTop.setHorizontalTextPosition(SwingConstants.LEADING);
                        this.cbox_onTop.addItemListener(new ItemListener() {
                            @Override
                            public void itemStateChanged(ItemEvent arg0) {
                                do_cbox_onTop_itemStateChanged(arg0);
                            }
                        });
                        this.cbox_onTop.setActionCommand("on top");
                        GridBagConstraints gbc_cbox_onTop = new GridBagConstraints();
                        gbc_cbox_onTop.insets = new Insets(5, 0, 5, 0);
                        gbc_cbox_onTop.anchor = GridBagConstraints.EAST;
                        gbc_cbox_onTop.gridx = 4;
                        gbc_cbox_onTop.gridy = 0;
                        this.panel_left.add(this.cbox_onTop, gbc_cbox_onTop);
                        GridBagConstraints gbc_btnSearchByPeaks = new GridBagConstraints();
                        gbc_btnSearchByPeaks.fill = GridBagConstraints.HORIZONTAL;
                        gbc_btnSearchByPeaks.insets = new Insets(0, 5, 5, 5);
                        gbc_btnSearchByPeaks.gridx = 0;
                        gbc_btnSearchByPeaks.gridy = 1;
                        panel_left.add(btnSearchByPeaks, gbc_btnSearchByPeaks);
                    }
                    {
                        chckbxIntensityInfo = new JCheckBox("Intensity info");
                        GridBagConstraints gbc_chckbxIntensityInfo = new GridBagConstraints();
                        gbc_chckbxIntensityInfo.anchor = GridBagConstraints.WEST;
                        gbc_chckbxIntensityInfo.insets = new Insets(0, 0, 5, 5);
                        gbc_chckbxIntensityInfo.gridx = 1;
                        gbc_chckbxIntensityInfo.gridy = 1;
                        panel_left.add(chckbxIntensityInfo, gbc_chckbxIntensityInfo);
                    }
                    {
                        chckbxNpksInfo = new JCheckBox("Npks info");
                        chckbxNpksInfo.setSelected(true);
                        GridBagConstraints gbc_chckbxNpksInfo = new GridBagConstraints();
                        gbc_chckbxNpksInfo.anchor = GridBagConstraints.WEST;
                        gbc_chckbxNpksInfo.insets = new Insets(0, 0, 5, 5);
                        gbc_chckbxNpksInfo.gridx = 3;
                        gbc_chckbxNpksInfo.gridy = 1;
                        panel_left.add(chckbxNpksInfo, gbc_chckbxNpksInfo);
                    }
                    this.lblHelp = new JLabel("?");
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
                    GridBagConstraints gbc_lbllist = new GridBagConstraints();
                    gbc_lbllist.anchor = GridBagConstraints.EAST;
                    gbc_lbllist.insets = new Insets(0, 0, 5, 0);
                    gbc_lbllist.gridx = 4;
                    gbc_lbllist.gridy = 1;
                    this.panel_left.add(this.lblHelp, gbc_lbllist);
                    {
                        chckbxNameFilter = new JCheckBox("Apply name filter:");
                        GridBagConstraints gbc_chckbxNameFilter = new GridBagConstraints();
                        gbc_chckbxNameFilter.anchor = GridBagConstraints.EAST;
                        gbc_chckbxNameFilter.insets = new Insets(0, 0, 5, 5);
                        gbc_chckbxNameFilter.gridx = 0;
                        gbc_chckbxNameFilter.gridy = 2;
                        panel_left.add(chckbxNameFilter, gbc_chckbxNameFilter);
                    }
                    {
                        txtNamefilter = new JTextField();
                        GridBagConstraints gbc_txtNamefilter = new GridBagConstraints();
                        gbc_txtNamefilter.gridwidth = 4;
                        gbc_txtNamefilter.insets = new Insets(0, 0, 5, 0);
                        gbc_txtNamefilter.fill = GridBagConstraints.HORIZONTAL;
                        gbc_txtNamefilter.gridx = 1;
                        gbc_txtNamefilter.gridy = 2;
                        panel_left.add(txtNamefilter, gbc_txtNamefilter);
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
                    {
                        lblHeader = new JLabel("Header");
                        GridBagConstraints gbc_lblHeader = new GridBagConstraints();
                        gbc_lblHeader.fill = GridBagConstraints.HORIZONTAL;
                        gbc_lblHeader.gridwidth = 5;
                        gbc_lblHeader.insets = new Insets(0, 0, 5, 0);
                        gbc_lblHeader.gridx = 0;
                        gbc_lblHeader.gridy = 3;
                        panel_left.add(lblHeader, gbc_lblHeader);
                    }
                    {
                        this.scrollPane = new JScrollPane();
                        GridBagConstraints gbc_scrollPane = new GridBagConstraints();
                        gbc_scrollPane.gridwidth = 5;
                        gbc_scrollPane.fill = GridBagConstraints.BOTH;
                        gbc_scrollPane.insets = new Insets(0, 5, 5, 5);
                        gbc_scrollPane.gridx = 0;
                        gbc_scrollPane.gridy = 4;
                        this.panel_left.add(this.scrollPane, gbc_scrollPane);
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
                    scrollPane_1.setViewportBorder(null);
                    this.scrollPane_1.setBorder(null);
                    GridBagConstraints gbc_scrollPane_1 = new GridBagConstraints();
                    gbc_scrollPane_1.insets = new Insets(5, 5, 5, 5);
                    gbc_scrollPane_1.fill = GridBagConstraints.BOTH;
                    gbc_scrollPane_1.gridx = 0;
                    gbc_scrollPane_1.gridy = 0;
                    this.panel_right.add(this.scrollPane_1, gbc_scrollPane_1);
                    {
                        database_dialog.tAOut = new LogJTextArea();
                        database_dialog.tAOut.setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));
                        database_dialog.tAOut.setWrapStyleWord(true);
                        database_dialog.tAOut.setLineWrap(true);
                        database_dialog.tAOut.setEditable(false);
                        this.scrollPane_1.setViewportView(database_dialog.tAOut);
                    }
                }
            }
        }
        {
            JPanel buttonPane = new JPanel();
            getContentPane().add(buttonPane, BorderLayout.SOUTH);
            {
                GridBagLayout gbl_buttonPane = new GridBagLayout();
                gbl_buttonPane.columnWidths = new int[] { 0, 0, 0 };
                gbl_buttonPane.rowHeights = new int[] { 0, 0 };
                gbl_buttonPane.columnWeights = new double[] { 0.0, 1.0, Double.MIN_VALUE };
                gbl_buttonPane.rowWeights = new double[] { 0.0, Double.MIN_VALUE };
                buttonPane.setLayout(gbl_buttonPane);
            }
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
                btnTestclosest.setEnabled(false);
                GridBagConstraints gbc_btnTestclosest = new GridBagConstraints();
                gbc_btnTestclosest.insets = new Insets(0, 0, 0, 5);
                gbc_btnTestclosest.gridx = 0;
                gbc_btnTestclosest.gridy = 0;
                buttonPane.add(btnTestclosest, gbc_btnTestclosest);
                btnTestclosest.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent arg0) {
                        do_btnTestclosest_actionPerformed(arg0);
                    }
                });
            }
            okButton.setActionCommand("OK");
            GridBagConstraints gbc_okButton = new GridBagConstraints();
            gbc_okButton.anchor = GridBagConstraints.EAST;
            gbc_okButton.insets = new Insets(5, 5, 5, 5);
            gbc_okButton.gridx = 1;
            gbc_okButton.gridy = 0;
            buttonPane.add(okButton, gbc_okButton);
            getRootPane().setDefaultButton(okButton);
        }

        {
            btnShowDsp = new JButton("Show d-spacing");
            btnShowDsp.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent arg0) {
                    do_btnShowDsp_actionPerformed(arg0);
                }
            });
            {
                pBarDB = new JProgressBar();
                pBarDB.setStringPainted(true);
                GridBagConstraints gbc_pBarDB = new GridBagConstraints();
                gbc_pBarDB.gridwidth = 3;
                gbc_pBarDB.fill = GridBagConstraints.BOTH;
                gbc_pBarDB.insets = new Insets(0, 5, 5, 5);
                gbc_pBarDB.gridx = 0;
                gbc_pBarDB.gridy = 5;
                panel_left.add(pBarDB, gbc_pBarDB);
            }
            {
                btnResetSearch = new JButton("reset list");
                btnResetSearch.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent arg0) {
                        do_btnResetSearch_actionPerformed(arg0);
                    }
                });
                GridBagConstraints gbc_btnResetSearch = new GridBagConstraints();
                gbc_btnResetSearch.anchor = GridBagConstraints.EAST;
                gbc_btnResetSearch.insets = new Insets(0, 0, 5, 5);
                gbc_btnResetSearch.gridx = 3;
                gbc_btnResetSearch.gridy = 5;
                panel_left.add(btnResetSearch, gbc_btnResetSearch);
            }
            GridBagConstraints gbc_btnShowDsp = new GridBagConstraints();
            gbc_btnShowDsp.insets = new Insets(0, 0, 5, 5);
            gbc_btnShowDsp.gridx = 4;
            gbc_btnShowDsp.gridy = 5;
            panel_left.add(btnShowDsp, gbc_btnShowDsp);
        }
        
        tAOut.ln("** PDDatabase **");

//      dataFile = mf.getOpenedFile();
//      patt2D = mf.getPatt2D();
        lm = new DefaultListModel();
        listCompounds.setModel(lm);
        lblHeader.setText("");

    }

    @Override
    public void dispose() {
        this.chckbxPDdata.setSelected(false);
        super.dispose();
    }


    protected void do_btnLoadDB_actionPerformed(ActionEvent arg0) {
        
        //primer creem el progress monitor, el worker i hi afegim el listener
        pm = new ProgressMonitor(this,
                "Reading DB file...",
                "", 0, 100);
        pm.setProgress(0);
        pBarDB.setString("Reading DB");
        pBarDB.setStringPainted(true);
        openDBFwk = new PDDatabase.openDBfileWorker();
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
        
        VavaLogger.LOG.info("option = "+n);
        
        if (n == JOptionPane.YES_OPTION) {
            //load internal DB
            URL zipUrl = this.getClass().getResource(localDB);
            try{
                File zipFile = new File(zipUrl.toURI());
                ZipFile zip = new ZipFile(zipFile);
                //reset current Database
                PDDatabase.reset();
                //read database file in zip
                openDBFwk.setFileToRead(zip);
                openDBFwk.execute();
                this.acknowledgeCOD();
            }catch(Exception e){
                e.printStackTrace();
            }
            
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
        tAOut.ln("Acknowledgements:\n"
                + "The default DB is a selection of inorganic compounds taken from the Crystallography Open Database (COD) "
                + "for which the d-spacings have been calculated according to the reported cell parameters and contents (on 06/03/2015).\n"
                + "COD references (http://www.crystallography.net/):\n"
                + "Grazulis, S., Daškevič, A., Merkys, A., Chateigner, D., Lutterotti, L., Quiros, M., Serebryanaya, N.R., Moeck, P., Downs, R. T. & Le Bail, A. (2012) Nucl. Acids Res. 40 (D1), D420-D427\n"
                + "Grazulis, S., Chateigner, D., Downs, R. T., Yokochi, A.  F.  T., Quiros, M., Lutterotti, L., Manakova, E., Butkus, J., Moeck, P. & Le Bail, A. (2009). J. Appl. Cryst. 42, 726-729.");
        tAOut.ln("");
    }
    
    protected void acknowledgeCOD_tArea(){
        tAOut.ln("");
        tAOut.ln("The default DB is a selection of inorganic compounds taken from the Crystallography Open Database (COD, http://www.crystallography.net) "
                + "for which the d-spacings have been calculated according to the reported cell parameters and contents (on 06/03/2015 by OV).\n"
                + "COD references:\n"
                + "Grazulis, S., Daškevič, A., Merkys, A., Chateigner, D., Lutterotti, L., Quiros, M., Serebryanaya, N.R., Moeck, P., Downs, R. T. & Le Bail, A. (2012) Nucl. Acids Res. 40 (D1), D420-D427\n"
                + "Grazulis, S., Chateigner, D., Downs, R. T., Yokochi, A.  F.  T., Quiros, M., Lutterotti, L., Manakova, E., Butkus, J., Moeck, P. & Le Bail, A. (2009). J. Appl. Cryst. 42, 726-729.");
        tAOut.ln("");        
    }
    
    protected void acknowledgeCOD(){
        if (!cod_acknowledged){
            String codack="<html> <div style=\"text-align:justify\"> The default DB is a selection of inorganic compounds taken from the Crystallography Open Database (COD, http://www.crystallography.net) for which the d-spacings have been calculated according to the reported cell parameters (on 06/03/2015 by OV) <br> <br> <font size=-1>COD references: <br> Grazulis, S., Daškevič, A., Merkys, A., Chateigner, D., Lutterotti, L., Quiros, M., Serebryanaya, N.R., Moeck, P., Downs, R. T. & Le Bail, A. (2012) Nucl. Acids Res. 40 (D1), D420-D427 <br> Grazulis, S., Chateigner, D., Downs, R. T., Yokochi, A.  F.  T., Quiros, M., Lutterotti, L., Manakova, E., Butkus, J., Moeck, P. & Le Bail, A. (2009). J. Appl. Cryst. 42, 726-729.<br> </font> </div> </html>";
            Help_dialog hd = new Help_dialog("COD acknowledgements",codack);
            hd.setSize(750,320);
            hd.setLocationRelativeTo(this);
            hd.setLocation(hd.getLocation().x, hd.getLocation().y-240);
            hd.setVisible(true);
            this.setAlwaysOnTop(true); //to get the focus
//            this.requestFocusInWindow();
            cod_acknowledged=true;
        }
    }

    protected void do_okButton_actionPerformed(ActionEvent arg0) {
        this.dispose();
    }

    public PDCompound getCurrentCompound() {
        if (listCompounds == null){return null;}
        if (listCompounds.getSelectedIndex() >= 0) {
            String sel = (String) listCompounds.getSelectedValue();
            int ov_index = Integer.parseInt((sel.trim().split("\\s+"))[0]);
            PDCompound comp = PDDatabase.get_compound_from_ovNum(ov_index);
            tAOut.ln(comp.printInfoMultipleLines());
            return comp;
        } else {
            return null;
        }
    }

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
            lm.addElement(c.printCompound()); //he tret el .trim()
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
        for (int i=0; i<c.getDspacings().size();i++){
            tAOut.ln(c.getDspacings().get(i)+" "+c.getIntensities().get(i));
        }
        
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
        mf.getPanelImatge().setShowDSPRings(this.isShowDataRings(), this.getCurrentCompound());
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
        ArrayList<Float> lab6pks = new ArrayList<Float>();
        lab6pks.add(4.15710f);
        lab6pks.add(2.93950f);
        lab6pks.add(2.40010f);
        lab6pks.add(2.07850f);
        lab6pks.add(1.85910f);
        lab6pks.add(1.69710f);
        lab6pks.add(1.46970f);
        lab6pks.add(1.38570f);
        lab6pks.add(1.38570f);
        lab6pks.add(1.31460f);
        lab6pks.add(1.25340f);
        PDDatabase.takeClosest(lab6pks,4.141f);
        PDDatabase.takeClosest(lab6pks,2.936f);
        PDDatabase.takeClosest(lab6pks,2.395f);
        PDDatabase.takeClosest(lab6pks,2.075f);
        PDDatabase.takeClosest(lab6pks,1.861f);
        PDDatabase.takeClosest(lab6pks,1.697f);
        PDDatabase.takeClosest(lab6pks,1.466f);
        PDDatabase.takeClosest(lab6pks,1.383f);
        PDDatabase.takeClosest(lab6pks,1.312f);
        PDDatabase.takeClosest(lab6pks,1.249f);
    }
    
    //CANVIEM PER NO UTILITZAR EL LM AMB TOTS ELS COMPOSTOS  -- AL FINAL NO, fem boto per tornar a mostrar tots
    public void loadSearchPeaksResults(ArrayList<Float> searchlist){
        //aqui en principi tindrem una llista de resultats a PDDatabase i s'haurà de mostrar
        lm.clear();
        float minDSPin = Collections.min(searchlist);
        int nDSPin = searchlist.size();
        
        ArrayList<PDDatabase.SearchResult> res = PDDatabase.getSearchresults();
        
        //mirem si hi ha criteris complementaris pel residual
        if (chckbxIntensityInfo.isSelected() || chckbxNpksInfo.isSelected()){
            Iterator<PDDatabase.SearchResult> itrcomp = res.iterator();
            while (itrcomp.hasNext()){
                PDDatabase.SearchResult c = itrcomp.next();
                float resid = c.getResidual();
                if (chckbxIntensityInfo.isSelected()){
                    resid = resid + c.getResidual_intensities();
                }
                if (chckbxNpksInfo.isSelected()){
                    resid = resid * (Math.max(c.getC().getNrRefUpToDspacing(minDSPin)/nDSPin,1));
                }
                c.setResidual(resid);
            }            
        }
        
        Collections.sort(res);
        Iterator<PDDatabase.SearchResult> itrcomp = res.iterator();
        int nsol = 0;
        while (itrcomp.hasNext()){
            if (nsol >= maxNsol) break;
            PDDatabase.SearchResult c = itrcomp.next();
            int nrefcomp = c.getC().getNrRefUpToDspacing(minDSPin);
            String outString = String.format("%6d  %7.3f  %d/%d  %s [%s]", c.getC().getOv_number(),c.getResidual(),nDSPin,nrefcomp,c.getC().getCompName(),c.getC().getFormula());
            lm.addElement(outString);
            nsol = nsol + 1;
        }
        lblHeader.setText(" RefNum  Residual  inputRefs/compoundRefs  CompoundName  [Formula] ");
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
        if (mf.getPanelImatge().getPuntsCercles().isEmpty()){
            tAOut.stat("Please select some peaks clicking in the image");
            return;
        }
        Iterator<PuntCercle> itrP = mf.getPanelImatge().getPuntsCercles().iterator();
        ArrayList<Float> searchlist = new ArrayList<Float>();
        ArrayList<Float> searchlistInten = new ArrayList<Float>();
        while (itrP.hasNext()) {
            PuntCercle pa = itrP.next();
            float dsp = (float)mf.getPanelImatge().calcDsp(pa.getT2());
            if (dsp > MainFrame.minDspacingToSearch){
                searchlist.add(dsp);    
                //per la intensitat fem el promig de l'anell
                float circleInt = mf.getPatt2D().intRadCircle((float)FastMath.toDegrees(pa.getT2()), -1.f);
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
                    String s = (String)element;
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
}
