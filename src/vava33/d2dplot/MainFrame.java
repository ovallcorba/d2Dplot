package vava33.d2dplot;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.Iterator;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.KeyStroke;
import javax.swing.ProgressMonitor;
import javax.swing.UIManager;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import net.miginfocom.swing.MigLayout;

import vava33.d2dplot.auxi.ArgumentLauncher;
import vava33.d2dplot.auxi.ImgFileUtils;
import vava33.d2dplot.auxi.ImgOps;
import vava33.d2dplot.auxi.PDCompound;
import vava33.d2dplot.auxi.PDDatabase;
import vava33.d2dplot.auxi.Pattern2D;

import com.vava33.jutils.FileUtils;
import com.vava33.jutils.LogJTextArea;
import com.vava33.jutils.VavaLogger;

public class MainFrame extends JFrame {
    private static final long serialVersionUID = 4368250280987133953L;

    private static VavaLogger log;
    
    private Pattern2D patt2D;
    private boolean fileOpened = false;
    private File openedFile;
    

    private ImagePanel panelImatge;    
    private D2Dsub_frame d2DsubWin;
    private D2Dsub_batch d2DsubWinBatch;
    private Calib_dialog calibration;
    private ExZones_dialog exZones;
    private About_dialog p2dAbout;
    private Pklist_dialog pkListWin;
    private LogJTextArea tAOut;
    private IntegracioRadial irWin;
    private DB_dialog dbDialog;
    private Dinco_frame dincoFrame;
    private Param_dialog paramDialog;
    private HPtools_frame hpframe;
    private PKsearch_frame pksframe;
    
    private JButton btn05x;
    private JButton btn2x;
    private JButton btnMidaReal;
    private JButton btnOpen;
    private JButton btnResetView;
    private JButton btnSaveDicvol;
    private JCheckBox chckbxIndex;
    private JLabel lblOpened;
    private JPanel contentPane;
    private JPanel panel_all;
    private JPanel panel_controls;
    private JPanel panel_opcions;
    private JPanel panel_stat;
    private JScrollPane scrollPane;
    private JSplitPane splitPane;
    private JSplitPane splitPane_1;
    private JButton btnNext;
    private JButton btnPrev;
    private JPanel panel_2;
    private JCheckBox chckbxShowRings;
    private static JComboBox combo_LATdata;
    private JButton btnDbdialog;
    private JPanel panel_3;
    private JButton btnAddLat;
    private JSeparator separator_1;
    private JMenuBar menuBar;
    private JMenu mnFile;
    private JMenuItem mntmAbout;
    private JMenuItem mntmOpen;
    private JMenuItem mntmSaveImage;
    private JMenuItem mntmExportAsPng;
    private JMenuItem mntmQuit;
    private JCheckBox chckbxColor;
    private JCheckBox chckbxInvertY;
    private JMenu mnGrainAnalysis;
    private JMenuItem mntmDincoSol;
    private JMenuItem mntmLoadXdsFile;
    private JMenuItem mntmClearAll;
    private JMenu mnImageOps;
    private JMenuItem mntmInstrumentalParameters;
    private JMenuItem mntmLabCalibration;
    private JMenuItem mntmExcludedZones;
    private JMenuItem mntmBackgroundSubtraction;
    private JMenuItem mntmRadialIntegration;
    private JMenuItem mntmFindPeaks;
    private JMenu mnPhaseId;
    private JMenuItem mntmDatabase;
    private JMenuItem mntmSumImages;
    private JMenuItem mntmBatchConvert;
    private JMenuItem mntmHpTools;
    private JPanel panel;

    //sumimages
    ProgressMonitor pm;
    ImgFileUtils.batchConvertFileWorker convwk;
    ImgOps.sumImagesFileWorker sumwk;
    private JButton btnInstParameters;
    private JSeparator separator;
    private JPanel panel_1;
    private JButton btnRadIntegr;
    private JButton btnTtsdincoSol;
    private JMenuItem mntmSubtractImages;
    private JCheckBox chckbxPaintExz;
    private JButton btnPeakSearchint;
    private JMenu mnHelp;
    private JMenuItem mntmManual;
    
    public static String getBinDir() {return D2Dplot_global.binDir;}
    public static String getSeparator() {return D2Dplot_global.separator;}
    public static String getWorkdir() {return D2Dplot_global.getWorkdir();}
    public static void setWorkdir(String wdir) {D2Dplot_global.setWorkdir(wdir);}
    
    /**
     * Launch the application. ES POT PASSAR COM A ARGUMENT EL DIRECTORI DE TREBALL ON S'OBRIRAN PER DEFECTE ELS DIALEGS
     * 
     */
    public static void main(final String[] args) {
        
        //first thing to do is read PAR files if exist
        FileUtils.detectOS();
        D2Dplot_global.readParFile();
        
    	//LOGGER with the read parameters from file
        log = D2Dplot_global.getVavaLogger(MainFrame.class.getName());
        System.out.println(log.logStatus());
    	    	
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            if(UIManager.getLookAndFeel().toString().contains("metal")){
                UIManager.setLookAndFeel("com.sun.java.swing.plaf.gtk.GTKLookAndFeel");    
            }
            // UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName()); //java metal
            
        } catch (Throwable e) {
            if (D2Dplot_global.isDebug())e.printStackTrace();
            log.warning("Error initializing System look and feel");
        }

        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    MainFrame frame = new MainFrame();
                    frame.inicialitza();
                    //AQUI POSO EL ARGUMENT LAUNCHER
                    D2Dplot_global.printAllOptions("info");
                    ArgumentLauncher.readArguments(frame, args);
                    if (ArgumentLauncher.isLaunchGraphics()){
                        frame.showMainFrame();
                    }else{
                        log.info("Exiting...");
                        frame.dispose();
                        return;
                    }
                } catch (Exception e) {
                    if (D2Dplot_global.isDebug())e.printStackTrace();
                    log.severe("Error initializing main window");
                }
            }
        });
    }

    public void showMainFrame(){
        this.setLocationRelativeTo(null);
        this.setVisible(true);    
    }
    
    /**
     * Create the frame.
     */
    public MainFrame() {
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                do_this_windowClosing(e);
            }
        });
        setIconImage(Toolkit.getDefaultToolkit().getImage(MainFrame.class.getResource("/img/Icona.png")));
        setTitle("d2Dplot");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 1180, 853);
        
        menuBar = new JMenuBar();
        setJMenuBar(menuBar);
        
        mnFile = new JMenu("File");
        mnFile.setMnemonic('f');
        menuBar.add(mnFile);
        
        mntmOpen = new JMenuItem("Open Image");
        mntmOpen.setMnemonic('o');
        mntmOpen.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_MASK));
        mntmOpen.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                do_mntmOpen_actionPerformed(e);
            }
        });
        mnFile.add(mntmOpen);
        
        mntmSaveImage = new JMenuItem("Save Image");
        mntmSaveImage.setMnemonic('s');
        mntmSaveImage.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_MASK));
        mntmSaveImage.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                do_mntmSaveImage_actionPerformed(e);
            }
        });
        mnFile.add(mntmSaveImage);
        
        mntmExportAsPng = new JMenuItem("Export as PNG");
        mntmExportAsPng.setMnemonic('e');
        mntmExportAsPng.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                do_mntmExportAsPng_actionPerformed(e);
            }
        });
        mnFile.add(mntmExportAsPng);
        
        mntmSumImages = new JMenuItem("Sum Images");
        mntmSumImages.setMnemonic('s');
        mntmSumImages.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                do_mntmSumImages_actionPerformed(e);
            }
        });
        mnFile.add(mntmSumImages);
        
        mntmBatchConvert = new JMenuItem("Batch Convert");
        mntmBatchConvert.setMnemonic('a');
        mntmBatchConvert.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                do_mntmBatchConvert_actionPerformed(e);
            }
        });
        
        mntmSubtractImages = new JMenuItem("Subtract Images");
        mntmSubtractImages.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                do_mntmSubtractImages_actionPerformed(e);
            }
        });
        mntmSubtractImages.setMnemonic('u');
        mnFile.add(mntmSubtractImages);
        mnFile.add(mntmBatchConvert);
        
        separator = new JSeparator();
        mnFile.add(separator);
        
        mntmQuit = new JMenuItem("Quit");
        mntmQuit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                do_mntmQuit_actionPerformed(arg0);
            }
        });
        mntmQuit.setMnemonic('q');
        mntmQuit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, InputEvent.CTRL_MASK));
        mnFile.add(mntmQuit);
        
        mnImageOps = new JMenu("Image");
        mnImageOps.setMnemonic('i');
        menuBar.add(mnImageOps);
        
        mntmInstrumentalParameters = new JMenuItem("Instrumental Parameters");
        mntmInstrumentalParameters.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_I, InputEvent.CTRL_MASK));
        mntmInstrumentalParameters.setMnemonic('i');
        mntmInstrumentalParameters.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                do_mntmInstrumentalParameters_actionPerformed(e);
            }
        });
        mnImageOps.add(mntmInstrumentalParameters);
        
        mntmLabCalibration = new JMenuItem("LaB6 Calibration");
        mntmLabCalibration.setMnemonic('l');
        mntmLabCalibration.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                do_mntmLabCalibration_actionPerformed(e);
            }
        });
        mnImageOps.add(mntmLabCalibration);
        
        mntmExcludedZones = new JMenuItem("Excluded Zones");
        mntmExcludedZones.setMnemonic('x');
        mntmExcludedZones.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                do_mntmExcludedZones_actionPerformed(e);
            }
        });
        mnImageOps.add(mntmExcludedZones);
        
        mntmBackgroundSubtraction = new JMenuItem("Background Subtraction");
        mntmBackgroundSubtraction.setMnemonic('b');
        mntmBackgroundSubtraction.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                do_mntmBackgroundSubtraction_actionPerformed(e);
            }
        });
        mnImageOps.add(mntmBackgroundSubtraction);
        
        mntmRadialIntegration = new JMenuItem("Radial Integration");
        mntmRadialIntegration.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, InputEvent.CTRL_MASK));
        mntmRadialIntegration.setMnemonic('r');
        mntmRadialIntegration.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                do_mntmRadialIntegration_actionPerformed(e);
            }
        });
        mnImageOps.add(mntmRadialIntegration);
        
        mntmHpTools = new JMenuItem("HP Cu Pcalc.");
        mntmHpTools.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                do_mntmHpTools_actionPerformed(e);
            }
        });
        mntmHpTools.setMnemonic('h');
        mnImageOps.add(mntmHpTools);
        
        mnGrainAnalysis = new JMenu("Grain Analysis");
        mnGrainAnalysis.setMnemonic('g');
        menuBar.add(mnGrainAnalysis);
        
        mntmDincoSol = new JMenuItem("Load tts-INCO SOL/PCS files");
        mntmDincoSol.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                do_mntmDincoSol_actionPerformed(e);
            }
        });
        mnGrainAnalysis.add(mntmDincoSol);
        
        mntmLoadXdsFile = new JMenuItem("Load XDS file");
        mntmLoadXdsFile.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                do_mntmLoadXdsFile_actionPerformed(e);
            }
        });
        mnGrainAnalysis.add(mntmLoadXdsFile);
        
        mntmFindPeaks = new JMenuItem("Find/Integrate Peaks");
        mntmFindPeaks.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                do_mntmFindPeaks_actionPerformed(arg0);
            }
        });
//        mntmFindPeaks.setEnabled(false);
        mnGrainAnalysis.add(mntmFindPeaks);
        
        mntmClearAll = new JMenuItem("Clear all");
        mntmClearAll.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                do_mntmClearAll_actionPerformed(e);
            }
        });
        mnGrainAnalysis.add(mntmClearAll);
        
        mnPhaseId = new JMenu("Phase ID");
        mnPhaseId.setMnemonic('p');
        menuBar.add(mnPhaseId);
        
        mntmDatabase = new JMenuItem("Database");
        mntmDatabase.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                do_mntmDatabase_actionPerformed(e);
            }
        });
        mntmDatabase.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_D, InputEvent.CTRL_MASK));
        mntmDatabase.setMnemonic('d');
        mnPhaseId.add(mntmDatabase);
        
        mnHelp = new JMenu("Help");
        menuBar.add(mnHelp);
        
        mntmAbout = new JMenuItem("About");
        mnHelp.add(mntmAbout);
        
        mntmManual = new JMenuItem("Manual");
        mntmManual.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                do_mntmManual_actionPerformed(e);
            }
        });
        mnHelp.add(mntmManual);
        mntmAbout.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                do_mntmAbout_actionPerformed(e);
            }
        });
        this.contentPane = new JPanel();
        this.contentPane.setBorder(null);
        setContentPane(this.contentPane);
        contentPane.setLayout(new MigLayout("fill, insets 0", "[1200px,grow]", "[900px,grow]"));
        this.splitPane = new JSplitPane();
        this.splitPane.setBorder(null);
        this.splitPane.setContinuousLayout(true);
        this.splitPane.setResizeWeight(0.9);
        this.splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
        this.contentPane.add(this.splitPane, "cell 0 0,grow,");
        this.panel_stat = new JPanel();
        this.splitPane.setRightComponent(this.panel_stat);
        this.panel_stat.setBackground(Color.BLACK);
        this.panel_stat.setBorder(null);
        panel_stat.setLayout(new MigLayout("fill, insets 5", "[1166px]", "[100px,grow]"));
        this.scrollPane = new JScrollPane();
        scrollPane.setViewportBorder(null);
        this.scrollPane.setBorder(null);
        this.panel_stat.add(this.scrollPane, "cell 0 0,grow");
        this.tAOut = new LogJTextArea();
        this.tAOut.setTabSize(4);
        this.tAOut.setWrapStyleWord(true);
        this.tAOut.setLineWrap(true);
        this.tAOut.setMaximumSize(new Dimension(32767, 32767));
        this.tAOut.setRows(1);
        this.tAOut.setBackground(Color.BLACK);
        this.scrollPane.setViewportView(this.tAOut);
        this.panel_all = new JPanel();
        this.splitPane.setLeftComponent(this.panel_all);
        panel_all.setLayout(new MigLayout("fill, insets 0", "[1200px,grow]", "[][604px,grow]"));
        
        panel_2 = new JPanel();
        panel_all.add(panel_2, "cell 0 0,grow");
                panel_2.setLayout(new MigLayout("fill, insets 0", "[][grow][][]", "[grow]"));
        
                this.btnOpen = new JButton("Open Image");
                panel_2.add(btnOpen, "cell 0 0,alignx center,aligny center");
                this.btnOpen.setMargin(new Insets(2, 7, 2, 7));
                this.btnOpen.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent arg0) {
                        do_btnOpen_actionPerformed(arg0);
                    }
                });
        this.lblOpened = new JLabel("(no image loaded)");
        lblOpened.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                do_lblOpened_mouseReleased(e);
            }
            @Override
            public void mouseEntered(MouseEvent e) {
                do_lblOpened_mouseEntered(e);
            }
            @Override
            public void mouseExited(MouseEvent e) {
                do_lblOpened_mouseExited(e);
            }
        });
        panel_2.add(lblOpened, "cell 1 0,alignx left,aligny center");
        
        btnPrev = new JButton("<");
        btnPrev.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                do_btnPrev_actionPerformed(e);
            }
        });
        panel_2.add(btnPrev, "cell 2 0,alignx center,aligny center");
        btnPrev.setToolTipText("Previous image (by index)");
        
        btnNext = new JButton(">");
        btnNext.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                do_btnNext_actionPerformed(arg0);
            }
        });
        panel_2.add(btnNext, "cell 3 0,alignx center,aligny center");
        btnNext.setToolTipText("Next image (by index)");
        this.splitPane_1 = new JSplitPane();
        this.splitPane_1.setBorder(new TitledBorder(null, "", TitledBorder.LEADING, TitledBorder.TOP, null, null));
        this.splitPane_1.setContinuousLayout(true);
        this.splitPane_1.setResizeWeight(1.0);
        this.panel_all.add(this.splitPane_1, "cell 0 1,grow");

        this.panelImatge = new ImagePanel(D2Dplot_global.sideControls);
        this.splitPane_1.setLeftComponent(this.panelImatge);
        this.panelImatge.setBorder(null);
        this.panel_controls = new JPanel();
        this.splitPane_1.setRightComponent(this.panel_controls);
        panel_controls.setLayout(new MigLayout("fill, insets 0", "[120px,grow]", "[][][][][][grow]"));
        
        btnInstParameters = new JButton("I. Parameters");
        btnInstParameters.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                do_btnInstParameters_actionPerformed(arg0);
            }
        });
        panel_controls.add(btnInstParameters, "cell 0 0,growx,aligny center");
        this.panel_opcions = new JPanel();
        this.panel_controls.add(this.panel_opcions, "cell 0 1,grow");
        this.panel_opcions.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Plot",
                TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
        this.btnResetView = new JButton("Reset view");
        btnResetView.setPreferredSize(new Dimension(90, 32));
        this.btnResetView.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                do_btnResetView_actionPerformed(arg0);
            }
        });
        panel_opcions.setLayout(new MigLayout("fill, insets 0", "[60px][60]", "[][][][][][]"));
        this.btnResetView.setMargin(new Insets(2, 2, 2, 2));
        this.panel_opcions.add(this.btnResetView, "cell 0 0 2 1,growx,aligny center");
        this.btnMidaReal = new JButton("True size");
        btnMidaReal.setPreferredSize(new Dimension(90, 32));
        this.btnMidaReal.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                do_btnMidaReal_actionPerformed(e);
            }
        });
        this.btnMidaReal.setMargin(new Insets(2, 2, 2, 2));
        this.panel_opcions.add(this.btnMidaReal, "cell 0 1 2 1,growx,aligny center");
        this.btn05x = new JButton("0.5x");
        panel_opcions.add(btn05x, "cell 0 2,alignx center");
        btn05x.setPreferredSize(new Dimension(50, 32));
        this.btn05x.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                do_btn05x_actionPerformed(arg0);
            }
        });
        this.btn05x.setMargin(new Insets(1, 2, 1, 2));
        this.btn2x = new JButton("2x");
        panel_opcions.add(btn2x, "cell 1 2,alignx center");
        btn2x.setPreferredSize(new Dimension(50, 32));
        this.btn2x.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                do_btn2x_actionPerformed(e);
            }
        });
        this.btn2x.setMargin(new Insets(1, 7, 1, 7));
        
        chckbxColor = new JCheckBox("Color");
        chckbxColor.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent arg0) {
                do_chckbxColor_itemStateChanged(arg0);
            }
        });
        panel_opcions.add(chckbxColor, "cell 0 3 2 1");
        
        chckbxInvertY = new JCheckBox("Invert Y");
        chckbxInvertY.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                do_chckbxInvertY_itemStateChanged(e);
            }
        });
        panel_opcions.add(chckbxInvertY, "cell 0 4 2 1");
        
        chckbxPaintExz = new JCheckBox("Paint ExZ");
        chckbxPaintExz.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent arg0) {
                do_chckbxPaintExz_itemStateChanged(arg0);
            }
        });
        panel_opcions.add(chckbxPaintExz, "cell 0 5 2 1");
        
        panel = new JPanel();
        panel.setBorder(new TitledBorder(null, "Points", TitledBorder.LEADING, TitledBorder.TOP, null, null));
        panel_controls.add(panel, "cell 0 2,grow");
        panel.setLayout(new MigLayout("fill, insets 0", "[]", "[][]"));
        this.chckbxIndex = new JCheckBox("Sel. Points");
        panel.add(chckbxIndex, "cell 0 0,alignx left,aligny center");
        chckbxIndex.setSelected(true);
        this.btnSaveDicvol = new JButton("Points List");
        panel.add(btnSaveDicvol, "cell 0 1,growx,aligny center");
        this.btnSaveDicvol.setMinimumSize(new Dimension(100, 28));
        this.btnSaveDicvol.setPreferredSize(new Dimension(100, 32));
        this.btnSaveDicvol.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                do_btnSaveDicvol_actionPerformed(arg0);
            }
        });
        this.btnSaveDicvol.setMargin(new Insets(2, 2, 2, 2));
        
        panel_3 = new JPanel();
        panel_3.setBorder(new TitledBorder(null, "Identification", TitledBorder.LEADING, TitledBorder.TOP, null, null));
        panel_controls.add(panel_3, "cell 0 3,grow");
        panel_3.setLayout(new MigLayout("fill, insets 0, hidemode 3", "[120px]", "[][][][][]"));
        
        btnDbdialog = new JButton("Database");
        btnDbdialog.setPreferredSize(new Dimension(100, 32));
        btnDbdialog.setMargin(new Insets(2, 2, 2, 2));
        panel_3.add(btnDbdialog, "cell 0 0,growx,aligny center");
        
        separator_1 = new JSeparator();
        panel_3.add(separator_1, "cell 0 1,growx,aligny center");
        
        chckbxShowRings = new JCheckBox("Quicklist");
        panel_3.add(chckbxShowRings, "cell 0 2,alignx left,aligny center");
        
        combo_LATdata = new JComboBox();
        combo_LATdata.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent arg0) {
                do_combo_showRings_itemStateChanged(arg0);
            }
        });
        panel_3.add(combo_LATdata, "cell 0 3,growx,aligny center");
        combo_LATdata.setModel(new DefaultComboBoxModel(new String[] {}));
        
        btnAddLat = new JButton("Add to List");
        btnAddLat.setVisible(false);
        btnAddLat.setEnabled(false);
        btnAddLat.setPreferredSize(new Dimension(100, 32));
        panel_3.add(btnAddLat, "cell 0 4,growx,aligny center");
        
        panel_1 = new JPanel();
        panel_1.setBorder(new TitledBorder(null, "Shortcuts", TitledBorder.LEADING, TitledBorder.TOP, null, null));
        panel_controls.add(panel_1, "cell 0 4,grow");
        panel_1.setLayout(new MigLayout("fill, insets 0", "[grow]", "[][][][][]"));
        
        btnPeakSearchint = new JButton("Peak Search");
        btnPeakSearchint.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                do_btnPeakSearchint_actionPerformed(arg0);
            }
        });
        panel_1.add(btnPeakSearchint, "cell 0 0,growx,aligny center");
        
        btnTtsdincoSol = new JButton("tts-INCO");
        btnTtsdincoSol.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                do_btnTtsdincoSol_actionPerformed(e);
            }
        });
        panel_1.add(btnTtsdincoSol, "flowy,cell 0 1,growx,aligny center");
        
        btnRadIntegr = new JButton("Rad. Integr");
        btnRadIntegr.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                do_btnRadIntegr_actionPerformed(e);
            }
        });
        panel_1.add(btnRadIntegr, "cell 0 2,growx,aligny center");
        
        chckbxShowRings.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent arg0) {
                do_chckbxShowRings_itemStateChanged(arg0);
            }
        });
        btnDbdialog.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                do_btnDbdialog_actionPerformed(arg0);
            }
        });
    }

    private void inicialitza() {
        this.setSize(1200, 960); //ho centra el metode main
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        if(this.getWidth()>screenSize.width||this.getHeight()>screenSize.height){
            this.setSize(screenSize.width-100,screenSize.height-100);
        }
        tAOut.stat(D2Dplot_global.welcomeMSG);
        FileUtils.setLocale();
        
        //inicialitzem l'arraylist a D2Dplot_global --- ja ho fa el metode en questio a global
        //BUSQUEM SI HI HA FITXERS .LAT al directori del programa i els afegim a quicklist (D2Dplot_global)
        if(D2Dplot_global.isConfigFileReaded()==null){
            tAOut.stat(String.format("No config file found on: %s, it will be created on exit!",D2Dplot_global.configFilePath));
        }else{
            if(D2Dplot_global.isConfigFileReaded()==true){
                tAOut.stat(String.format("Config file readed: %s",D2Dplot_global.configFilePath));    
            }else{
                tAOut.stat(String.format("Error reading config file: %s",D2Dplot_global.configFilePath));
            }
        }
        D2Dplot_global.initPars();
        D2Dplot_global.init_ApplyColorsToIPanel(this.getPanelImatge());
        D2Dplot_global.checkDBs();
        boolean ok = PDDatabase.populateQuickList(false);
        if (ok){
            tAOut.stat(String.format("QuickList DB file: %s",PDDatabase.getLocalQL()));
        }
        combo_LATdata.setPrototypeDisplayValue("XX"); 
        updateQuickList();
    }
    
    public static void updateQuickList(){
        Iterator<PDCompound> itrC= PDDatabase.getQuickListIterator();
        combo_LATdata.removeAllItems();
        while (itrC.hasNext()){
            combo_LATdata.addItem(itrC.next());
        }
    }
    
    //tanco tot
    private void reset() {
        this.fileOpened = false;
        this.panelImatge.setImagePatt2D(null);
        this.patt2D = null;
    }
    
    private void closePanels(){
        if (this.d2DsubWin != null) {
            this.d2DsubWin.dispose();
            this.d2DsubWin = null;
        }
        if (this.calibration != null) {
            this.calibration.inicia();;
        }
        if (this.exZones != null) {
            this.exZones.inicia();
        }
        if (this.dincoFrame != null) {
            this.dincoFrame.inicia();;
        }
        if (this.pksframe != null) {
            this.pksframe.inicia(false);
        }
        if (this.paramDialog != null) {
            this.updateIparameters();
        }
        if (this.dbDialog != null) {
            this.dbDialog.inicia();;
        }
        if (this.irWin != null) {
            this.irWin.inicia();;
            
        }
        if (this.pkListWin != null) {
            this.pkListWin.loadPeakList();
        }
        
        //TODO ADDD NEW THINGS IF NECESSARY
    }
    
    public void updatePatt2D(File d2File) {
        D2Dplot_global.setWorkdir(d2File);
        log.info("workdir="+getWorkdir());
        patt2D = ImgFileUtils.readPatternFile(d2File,true);
        
        if (patt2D != null) {
            panelImatge.setImagePatt2D(patt2D);
            panelImatge.setMainFrame(this);
            lblOpened.setText(d2File.toString());
            tAOut.stat("File opened: " + d2File.getName() + ", " + patt2D.getPixCount() + " pixels ("
                    + (int) patt2D.getMillis() + " ms)");
            if(patt2D.oldBIN)tAOut.ln("*** old BIN format detected and considered ***");
            tAOut.ln(patt2D.getInfo());
            fileOpened = true;
            openedFile = d2File;
            this.updateIparameters();
        } else {
            tAOut.stat("Error reading 2D file");
            tAOut.stat("No file opened");
            fileOpened = false;
            openedFile = null;
        }
    }
    
    public void updatePatt2D(Pattern2D p2D, boolean verbose) {
      this.patt2D = p2D;
      if (patt2D != null) {
          panelImatge.setImagePatt2D(patt2D);
          panelImatge.setMainFrame(this);
          String fname;
          if(patt2D.getImgfile()!=null){
              fname = patt2D.getImgfile().getName();
              tAOut.stat("File opened: " + fname + ", " + patt2D.getPixCount() + " pixels ("
                      + (int) patt2D.getMillis() + " ms)");
          }else{
              fname = "Data image not saved to a file";
              tAOut.stat("File opened: Data image not saved to a file");
          }
          lblOpened.setText(fname);
          if(patt2D.oldBIN)tAOut.ln("*** old BIN format detected and considered ***");
          if(verbose)tAOut.ln(patt2D.getInfo());
          fileOpened = true;
          openedFile = patt2D.getImgfile();
          this.updateIparameters();
      } else {
          tAOut.stat("Error reading 2D file");
          tAOut.stat("No file opened");
          fileOpened = false;
          openedFile = null;
      }
    }
    
    public void updateIparameters(){
        if (patt2D != null) {
            if (paramDialog!=null){
                paramDialog.inicia();
            }
        }
    }
    
    public PDCompound getQuickListCompound(){
        log.debug("getQuickListCompound CALLED");
        try{
            PDCompound pdc = (PDCompound) combo_LATdata.getSelectedItem();    
            return pdc;
        }catch(Exception e){
            log.debug("error in quicklist casting to PDCompound");
            return null;
        }
    }
    
    protected void do_this_windowClosing(WindowEvent e) {
      //FIRST SAVE OPTIONS
      D2Dplot_global.writeParFile();
      
      //SECOND SAVE QL FILE IF MODIFIED
      if(PDDatabase.isQLmodified()){
          //prompt and save QL file if necessary
          Object[] options = {"Yes","No"};
          int n = JOptionPane.showOptionDialog(null,
                  "QuickList has changed, overwrite current default file?",
                  "Update QL",
                  JOptionPane.YES_NO_OPTION,
                  JOptionPane.QUESTION_MESSAGE,
                  null, //do not use a custom Icon
                  options, //the titles of buttons
                  options[1]); //default button title
          if (n==JOptionPane.YES_OPTION){
              PDDatabase.saveDBfileWorker saveDBFwk = new PDDatabase.saveDBfileWorker(new File(PDDatabase.getLocalQL()),true);
              saveDBFwk.execute();    
              int maxCount = 20; //maximum wait 10 seconds
              while (!saveDBFwk.isDone() || maxCount <=0){
                  try {
                    Thread.sleep(500);
                    maxCount = maxCount -1;
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
              }
          } 
      }
      this.dispose();
    }
    
    protected void do_btn05x_actionPerformed(ActionEvent arg0) {
        if (panelImatge.getScalefit() > ImagePanel.getMinScaleFit()) {
            panelImatge.setScalefit(panelImatge.getScalefit() * 0.5f);
        }
    }

    protected void do_btn2x_actionPerformed(ActionEvent e) {
        if (panelImatge.getScalefit() < ImagePanel.getMaxScaleFit()) {
            panelImatge.setScalefit(panelImatge.getScalefit() * 2.0f);
        }
    }

    protected void do_mntmInstrumentalParameters_actionPerformed(ActionEvent e) {
        if (patt2D != null) {
            if (paramDialog==null){
                paramDialog = new Param_dialog(this.getPanelImatge(),patt2D);
            }
            paramDialog.setVisible(true);
        }
    }

    protected void do_btnInstParameters_actionPerformed(ActionEvent arg0) {
        mntmInstrumentalParameters.doClick();
    }
    
    protected void do_mntmLabCalibration_actionPerformed(ActionEvent e) {
        if (patt2D != null) {
            // tanquem zones excloses en cas que estigui obert
            if (exZones != null)
                exZones.dispose();
            if (pksframe != null)
                pksframe.dispose();
            if (calibration == null) {
                calibration = new Calib_dialog(this.getPanelImatge());
            }
            calibration.setVisible(true);
            panelImatge.setCalibration(calibration);
        }
    }
    protected void do_mntmExcludedZones_actionPerformed(ActionEvent e) {
        if (patt2D != null) {
            // tanquem calibracio en cas que estigui obert
            if (calibration != null)
                calibration.dispose();
            if (pksframe != null)
                pksframe.dispose();
            if (exZones == null) {
                exZones = new ExZones_dialog(this.getPanelImatge());
            }
            exZones.setVisible(true);
            this.chckbxPaintExz.setSelected(true);
            panelImatge.setExZones(exZones);
        }
    }
    
    protected void do_mntmFindPeaks_actionPerformed(ActionEvent arg0) {
        if (patt2D != null) {
            if (exZones != null)
                exZones.dispose();
            if (calibration != null)
                calibration.dispose();
            if (pksframe == null) {
                pksframe = new PKsearch_frame(this.panelImatge,false,null);
            }
//            pksframe.setPatt2D(patt2D);
            pksframe.setVisible(true);
            panelImatge.setPKsearch(pksframe);
        }

    }
    
    protected void do_mntmBackgroundSubtraction_actionPerformed(ActionEvent e) {
        if (patt2D != null) {
            if (d2DsubWin == null) {
                d2DsubWin = new D2Dsub_frame(patt2D,this);
            }
            d2DsubWin.setVisible(true);
        }else{
            //obrim el batch
            if (d2DsubWinBatch == null){
                d2DsubWinBatch = new D2Dsub_batch(this);
            }
            d2DsubWinBatch.setVisible(true);
        }
    }
    protected void do_mntmRadialIntegration_actionPerformed(ActionEvent e) {
        if(patt2D != null){
            if(this.irWin==null){
                irWin = new IntegracioRadial(this.getPanelImatge());
            }
            irWin.setVisible(true); 
        }
    }
    

    protected void do_btnTtsdincoSol_actionPerformed(ActionEvent e) {
        mntmDincoSol.doClick();
    }
    
    protected void do_btnRadIntegr_actionPerformed(ActionEvent e) {
//        testLP test = new testLP(patt2D);
//        debug_ellipse();
        mntmRadialIntegration.doClick();
    }

    protected void do_btnMidaReal_actionPerformed(ActionEvent e) {
        panelImatge.setScalefit(1.0f);
    }

    protected void do_btnOpen_actionPerformed(ActionEvent arg0) {
        this.openImgFile();
    }
    
    protected void do_mntmOpen_actionPerformed(ActionEvent e) {
        this.openImgFile();
    }

    protected void do_mntmSaveImage_actionPerformed(ActionEvent e) {
        this.saveImgFile();
    }

    protected void do_btnResetView_actionPerformed(ActionEvent arg0) {
        panelImatge.resetView();
    }
    
    // Obre finestra amb llista de pics
    protected void do_btnSaveDicvol_actionPerformed(ActionEvent arg0) {
        if (patt2D != null) {
            if (pkListWin != null) {
                pkListWin.tanca();
            }
            pkListWin = new Pklist_dialog(panelImatge);
            pkListWin.setVisible(true);
            pkListWin.toFront();
        }
    }

    protected void do_btnNext_actionPerformed(ActionEvent arg0) {
        
        String fnameCurrent = FileUtils.getFNameNoExt(patt2D.getImgfile());
        String fextCurrent = FileUtils.getExtension(patt2D.getImgfile());
        log.debug("fnameCurrent (no Ext): "+fnameCurrent);
        log.debug("fextCurrent: "+fextCurrent);
        
        //agafem ultims 4 digits (index), sumem 1 i el tornem a posar com a fitxer a veure què
        String fnameExtNew = "";
        try{
            log.debug("substring "+fnameCurrent.substring(fnameCurrent.length()-4, fnameCurrent.length()));
            int imgNum = Integer.parseInt(fnameCurrent.substring(fnameCurrent.length()-4, fnameCurrent.length()));
            imgNum = imgNum+1;
            fnameExtNew = fnameCurrent.substring(0, fnameCurrent.length()-4)+String.format("%04d", imgNum)+"."+fextCurrent;
        }catch(Exception e){
            log.debug("trying to get the file numbering");
            int indexGuio = fnameCurrent.lastIndexOf("_");
            if (indexGuio>0){
                log.debug("index guio="+indexGuio);
                int imgNum = Integer.parseInt(fnameCurrent.substring(indexGuio+1, fnameCurrent.length()));
                imgNum = imgNum+1;
                int lenformat = fnameCurrent.length()-indexGuio-1;
                log.debug("lenformat="+lenformat);
                String format = "%0"+lenformat+"d";
                log.debug("format="+format);
                fnameExtNew = fnameCurrent.substring(0, fnameCurrent.length()-lenformat)+String.format(format, imgNum)+"."+fextCurrent;
                log.debug("fnameExtNew="+fnameExtNew);
            }
        }
        if (fnameExtNew.isEmpty())return;
        
        File d2File = new File(fnameExtNew);
        if (d2File.exists()){
            this.reset();
            this.updatePatt2D(d2File);
            this.closePanels();
        }else{
            tAOut.stat("No file found with fname "+fnameExtNew);
        }
        
    }
    protected void do_btnPrev_actionPerformed(ActionEvent e) {
        String fnameCurrent = FileUtils.getFNameNoExt(patt2D.getImgfile());
        String fextCurrent = FileUtils.getExtension(patt2D.getImgfile());
        log.debug("fnameCurrent (no Ext): "+fnameCurrent);
        log.debug("fextCurrent: "+fextCurrent);
        //agafem ultims 4 digits (index), sumem 1 i el tornem a posar com a fitxer a veure què
        
        String fnameExtNew = "";
        try{
            log.debug("substring "+fnameCurrent.substring(fnameCurrent.length()-4, fnameCurrent.length()));
            int imgNum = Integer.parseInt(fnameCurrent.substring(fnameCurrent.length()-4, fnameCurrent.length()));
            imgNum = imgNum-1;
            fnameExtNew = fnameCurrent.substring(0, fnameCurrent.length()-4)+String.format("%04d", imgNum)+"."+fextCurrent;
        }catch(Exception ex){
            log.debug("trying to get the file numbering");
            int indexGuio = fnameCurrent.lastIndexOf("_");
            if (indexGuio>0){
                log.debug("index guio="+indexGuio);
                int imgNum = Integer.parseInt(fnameCurrent.substring(indexGuio+1, fnameCurrent.length()));
                imgNum = imgNum-1;
                int lenformat = fnameCurrent.length()-indexGuio-1;
                log.debug("lenformat="+lenformat);
                String format = "%0"+lenformat+"d";
                log.debug("format="+format);
                fnameExtNew = fnameCurrent.substring(0, fnameCurrent.length()-lenformat)+String.format(format, imgNum)+"."+fextCurrent;
                log.debug("fnameExtNew="+fnameExtNew);
            }
        }
        if (fnameExtNew.isEmpty())return;
        File d2File = new File(fnameExtNew);
        if (d2File.exists()){
            this.reset();
            this.updatePatt2D(d2File);
            this.closePanels();
        }else{
            tAOut.stat("No file found with fname "+fnameExtNew);
        }
    }

    protected void do_mntmQuit_actionPerformed(ActionEvent arg0) {
        this.do_this_windowClosing(null);
    }
    
    protected void do_mntmDatabase_actionPerformed(ActionEvent e) {
        btnDbdialog.doClick();
    }
    
    protected void do_btnDbdialog_actionPerformed(ActionEvent arg0) {
        // tanquem calibracio en cas que estigui obert
        if (calibration != null) calibration.dispose();
        if (exZones != null) exZones.dispose();

        if (dbDialog == null) {
            dbDialog = new DB_dialog(this.getPanelImatge());
        }
        dbDialog.setVisible(true);
        panelImatge.setDBdialog(dbDialog);
    }

    protected void do_chckbxShowRings_itemStateChanged(ItemEvent arg0) {
        if (combo_LATdata.getItemCount()>0){
            log.debug("do_chckbxShowRings_itemStateChanged CALLED");
            panelImatge.setShowQuickListCompoundRings(chckbxShowRings.isSelected(), this.getQuickListCompound());
        }

    }
   
    protected void do_combo_showRings_itemStateChanged(ItemEvent arg0) {
        if (arg0.getStateChange() == ItemEvent.SELECTED) {
            panelImatge.setShowQuickListCompoundRings(chckbxShowRings.isSelected(), this.getQuickListCompound());
        }
    }
    
    protected void do_mntmExportAsPng_actionPerformed(ActionEvent e) {
        File imFile = null;
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(new File(getWorkdir()));
        int selection = fileChooser.showSaveDialog(null);
        if (selection == JFileChooser.APPROVE_OPTION) {
            imFile = fileChooser.getSelectedFile();
            imFile = ImgFileUtils.exportPNG(imFile, panelImatge.getSubimage());
            if (imFile == null) {
                tAOut.stat("Error saving PNG file");
                return;
            }
        } else {
            tAOut.stat("Error saving PNG file");
            return;
        }
        tAOut.stat("File PNG saved: " + imFile.toString());
        D2Dplot_global.setWorkdir(imFile);
    }
    
    protected void do_mntmAbout_actionPerformed(ActionEvent e) {
        if (p2dAbout == null) {
            p2dAbout = new About_dialog();
        }
        p2dAbout.setVisible(true);
    }
    
    protected void do_mntmManual_actionPerformed(ActionEvent e) {
        if (p2dAbout == null) {
            p2dAbout = new About_dialog();
        }
        p2dAbout.openManual();
    }
    
    protected void do_chckbxColor_itemStateChanged(ItemEvent arg0) {
        panelImatge.setColor(chckbxColor.isSelected());
        log.debug("do_chckbxColor_itemStateChanged called");
        panelImatge.pintaImatge();
    }
    protected void do_chckbxInvertY_itemStateChanged(ItemEvent e) {
        panelImatge.setInvertY(chckbxInvertY.isSelected());
        log.debug("do_chckbxInvertY_itemStateChanged called");
        panelImatge.pintaImatge();
    }
    
    protected void do_chckbxPaintExz_itemStateChanged(ItemEvent arg0) {
        panelImatge.setPaintExZ(chckbxPaintExz.isSelected());
        log.debug("do_chckbxPaintExz_itemStateChanged called");
        panelImatge.pintaImatge();
    }
    
    public void setViewExZ(boolean state){
        this.chckbxPaintExz.setSelected(state);
    }

    protected void do_lblOpened_mouseReleased(MouseEvent e) {
        if (lblOpened.getText().startsWith("(no")||lblOpened.getText().isEmpty())return;
        
        File f = new File(lblOpened.getText());
        String fpath = f.getParent();
        boolean opened=false;
        try {
            if(Desktop.isDesktopSupported()){
                Desktop.getDesktop().open(new File(fpath));
                opened=true;
            }else{
                if(FileUtils.getOS().equalsIgnoreCase("win")){
                    new ProcessBuilder("explorer.exe","/select,",lblOpened.getText()).start();
                    opened=true;
                }
                if(FileUtils.getOS().equalsIgnoreCase("lin")){
                    //kde dolphin
                    try{
                        new ProcessBuilder("dolphin",lblOpened.getText()).start(); 
                        opened=true;
                    }catch(Exception ex){
                        if(D2Dplot_global.isDebug())ex.printStackTrace();
                    }
                    //gnome nautilus
                    try{
                        new ProcessBuilder("nautilus",lblOpened.getText()).start(); 
                        opened=true;
                    }catch(Exception ex){
                        if(D2Dplot_global.isDebug())ex.printStackTrace();
                    }
                }
            }
        } catch (Exception ex) {
            if(D2Dplot_global.isDebug())ex.printStackTrace();
        }
        if(!opened)tAOut.addtxt(true,true,"Unable to open folder");
    }

    protected void do_lblOpened_mouseEntered(MouseEvent e) {
        this.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        this.lblOpened.setForeground(Color.blue);
    }
    protected void do_lblOpened_mouseExited(MouseEvent e) {
        this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        this.lblOpened.setForeground(Color.black);
    }
    
    protected void do_mntmDincoSol_actionPerformed(ActionEvent e) {
        //Preguntem per obrir un fitxer SOL pero de totes formes obrirem el dialeg
        if (dincoFrame == null) {
            dincoFrame = new Dinco_frame(this.getPanelImatge());
        }
        if (!dincoFrame.hasSolutionsLoaded()){
            dincoFrame.setSOLMode();
            dincoFrame.openSOL();
        }
        dincoFrame.setVisible(true);
        panelImatge.setDinco(dincoFrame);
    }

    protected void do_mntmLoadXdsFile_actionPerformed(ActionEvent e) {
        //Preguntem per obrir un fitxer XDS pero de totes formes obrirem el dialeg
        if (dincoFrame == null) {
            dincoFrame = new Dinco_frame(this.getPanelImatge());
        }
        if (!dincoFrame.hasSolutionsLoaded()){
            dincoFrame.setXDSMode();
            dincoFrame.openXDS();
        }
        dincoFrame.setVisible(true);
        panelImatge.setDinco(dincoFrame);
    }
    
    protected void do_mntmClearAll_actionPerformed(ActionEvent e) {
        this.getPatt2D().clearSolutions();
        this.dincoFrame.dispose();
    }

    protected void do_mntmHpTools_actionPerformed(ActionEvent e) {
        if (hpframe == null) {
            hpframe = new HPtools_frame(this.getPanelImatge());
        }
        hpframe.setVisible(true);
    }
    
    protected void do_mntmSubtractImages_actionPerformed(ActionEvent e) {
        
        Subtract_dialog sdiag = new Subtract_dialog();
        sdiag.setVisible(true);
        
        Pattern2D img = ImgFileUtils.readPatternFile(sdiag.getImage(),true);
        float fac = sdiag.getFactor();
        Pattern2D img2 = ImgFileUtils.readPatternFile(sdiag.getBkgImage(),true);
        
        if(img==null)return;
        if(img2==null)return;
        
        Pattern2D dataSub = ImgOps.subtractBKG_v2(img, img2, fac, tAOut)[0];
        updatePatt2D(dataSub,false);    
    }
    

    
    protected void do_mntmSumImages_actionPerformed(ActionEvent e) {
        FileNameExtensionFilter filt[] = ImgFileUtils.getExtensionFilterRead();
        File[] flist = FileUtils.fchooserMultiple(this,new File(getWorkdir()), filt, filt.length-1);
        if (flist==null) return;
        D2Dplot_global.setWorkdir(flist[0]);

        pm = new ProgressMonitor(null,
                "Summing Images...",
                "", 0, 100);
        pm.setProgress(0);
        sumwk = new ImgOps.sumImagesFileWorker(flist,tAOut);
        sumwk.addPropertyChangeListener(new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                log.debug(evt.getPropertyName());
                if ("progress" == evt.getPropertyName() ) {
                    int progress = (Integer) evt.getNewValue();
                    pm.setProgress(progress);
                    pm.setNote(String.format("%d%%\n", progress));
                    log.fine("hi from inside if progress");
                    if (pm.isCanceled() || sumwk.isDone()) {
                        log.fine("hi from inside if cancel/done");
                        Toolkit.getDefaultToolkit().beep();
                        if (pm.isCanceled()) {
                            sumwk.cancel(true);
                            log.debug("sumwk canceled");
                        } else {
                            log.debug("sumwk finished!!");
                        }
                        pm.close();
                    }
                }
                if (sumwk.isDone()){
                    log.fine("hi from outside if progress");
                    Pattern2D suma = sumwk.getpattSum();
                    if (suma==null){
                        tAOut.stat("Error summing files");
                        return;
                    }else{
                        updatePatt2D(suma,false);    
                    }
                }
            }
        });
        sumwk.execute();
    }
    

    
    protected void do_mntmBatchConvert_actionPerformed(ActionEvent e) {
        FileNameExtensionFilter filt[] = ImgFileUtils.getExtensionFilterRead();
        File[] flist = FileUtils.fchooserMultiple(this,new File(getWorkdir()), filt, filt.length-1);
        if (flist==null) return;
        D2Dplot_global.setWorkdir(flist[0]);
        
        pm = new ProgressMonitor(null,
                "Converting Images...",
                "", 0, 100);
        pm.setProgress(0);
        convwk = new ImgFileUtils.batchConvertFileWorker(flist,tAOut);
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
                            log.debug("Batch convert stopped by user");
                        } else {
                            log.debug("Batch convert finished!!");
                        }
                        pm.close();
                    }
                }
            }
        });
        convwk.execute();
    }
    
    private void openImgFile(){
        FileNameExtensionFilter filt[] = ImgFileUtils.getExtensionFilterRead();
        File d2File = FileUtils.fchooser(this,new File(getWorkdir()), filt, filt.length-1, false, false);
        if (d2File == null){
            if (!fileOpened){
                tAOut.stat("No data file selected");
            }
            return;
        }
        D2Dplot_global.setWorkdir(d2File);
        
        // resetejem
        this.reset();
        this.updatePatt2D(d2File);
        this.closePanels();
    }
    
    private void saveImgFile(){
        if (this.getPatt2D()!=null){
            FileNameExtensionFilter filt[] = ImgFileUtils.getExtensionFilterWrite();
            File f = FileUtils.fchooser(this,new File(getWorkdir()), filt, filt.length-1, true, true);
            if (f!=null){
                D2Dplot_global.setWorkdir(f);
                File outf = ImgFileUtils.writePatternFile(f,this.getPatt2D());
                if (outf!=null){
                    int n = JOptionPane.showConfirmDialog(this, "Load the new saved file?", "Refresh file", JOptionPane.YES_NO_OPTION);
                    if (n == JOptionPane.YES_OPTION) {
                        this.reset();
                        this.updatePatt2D(outf);
                        this.closePanels();
                    }
                }else{
                    tAOut.stat("Error saving file");
                }
            }
        }else{
            tAOut.stat("Choose an image file saving");
        }
    }
    
    public File getOpenedFile() {
        return openedFile;
    }

    public ImagePanel getPanelImatge() {
        return panelImatge;
    }

    public Pattern2D getPatt2D() {
        return this.patt2D;
    }
    
    public void setOpenedFile(File openedFile) {
        this.openedFile = openedFile;
    }
    
    public LogJTextArea gettAOut() {
        return tAOut;
    }
    
    public boolean isSelectPoints(){
        return this.chckbxIndex.isSelected();
    }

    protected void do_btnPeakSearchint_actionPerformed(ActionEvent arg0) {
        mntmFindPeaks.doClick();
    }
    
    public Dinco_frame getDincoFrame() {
        return dincoFrame;
    }
    public void setDincoFrame(Dinco_frame dincoFrame) {
        this.dincoFrame = dincoFrame;
    }
    public Calib_dialog getCalibration() {
        return calibration;
    }
    public void setCalibration(Calib_dialog calibration) {
        this.calibration = calibration;
    }
    public IntegracioRadial getIrWin() {
        return irWin;
    }
    public void setIrWin(IntegracioRadial irWin) {
        this.irWin = irWin;
    }

}