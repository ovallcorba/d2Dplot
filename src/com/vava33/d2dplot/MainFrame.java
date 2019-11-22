package com.vava33.d2dplot;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Rectangle;
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
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Date;
import java.util.Iterator;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
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
import javax.swing.ScrollPaneConstants;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.plaf.metal.DefaultMetalTheme;
import javax.swing.plaf.metal.MetalLookAndFeel;
import javax.swing.plaf.metal.OceanTheme;

import org.apache.commons.math3.util.FastMath;

import com.vava33.cellsymm.CellSymm_global;
import com.vava33.d2dplot.auxi.ArgumentLauncher;
import com.vava33.d2dplot.auxi.ImgFileUtils;
import com.vava33.d2dplot.auxi.ImgOps;
import com.vava33.d2dplot.auxi.PDCompound;
import com.vava33.d2dplot.auxi.PDDatabase;
import com.vava33.d2dplot.auxi.Pattern2D;
import com.vava33.jutils.FileUtils;
import com.vava33.jutils.LogJTextArea;
import com.vava33.jutils.SystemInfo;
import com.vava33.jutils.VavaLogger;

import net.miginfocom.swing.MigLayout;

public class MainFrame {

    private static final String className = "d2Dplot_main";

    private static int def_Width = 1100;
    private static int def_Height = 768;

    private static VavaLogger log;
    private static MainFrame frame;

    private Pattern2D patt2D;
    private boolean fileOpened = false;
    private File openedFile;

    private JFrame mainF;
    private final ImagePanel panelImatge;
    private BackgroundEstimation d2DsubWin;
    private BackgroundEstimation_batch d2DsubWinBatch;
    private Calibration calibration;
    private ExZones exZones;
    private About p2dAbout;
    private PeakList pkListWin;
    private final LogJTextArea tAOut;
    private ConvertTo1DXRD irWin;
    private AzimuthalPlot iazWin;
    private Database dbDialog;
    private IncoPlot dincoFrame;
    private ImageParameters paramDialog;
    private HPtools hpframe;
    private PeakSearch pksframe;
    private TTS tts;
    private SC_to_INCO scToInco;
    private FastViewer fastView;

    private final JButton btn05x;
    private final JButton btn2x;
    private final JButton btnMidaReal;
    private final JButton btnOpen;
    private final JButton btnResetView;
    private final JButton btnSaveDicvol;
    private final JCheckBox chckbxIndex;
    private final JLabel lblOpened;
    private final JPanel contentPane;
    private final JPanel panel_all;
    private final JPanel panel_controls;
    private final JPanel panel_opcions;
    private final JPanel panel_stat;
    private final JScrollPane scrollPane;
    private final JSplitPane splitPane;
    private final JSplitPane splitPane_1;
    private final JButton btnNext;
    private final JButton btnPrev;
    private final JPanel panel_2;
    private final JCheckBox chckbxShowRings;
    private static JComboBox<PDCompound> combo_LATdata;
    private final JButton btnDbdialog;
    private final JPanel panel_3;
    private final JButton btnAddLat;
    private final JSeparator separator_1;
    private final JMenuBar menuBar;
    private final JMenu mnFile;
    private final JMenuItem mntmAbout;
    private final JMenuItem mntmOpen;
    private final JMenuItem mntmSaveImage;
    private final JMenuItem mntmExportAsPng;
    private final JMenuItem mntmQuit;
    private final JCheckBox chckbxColor;
    private final JCheckBox chckbxInvertY;
    private final JMenu mnGrainAnalysis;
    private final JMenuItem mntmDincoSol;
    private final JMenuItem mntmLoadXdsFile;
    //    private JMenuItem mntmClearAll;
    private final JMenu mnImageOps;
    private final JMenuItem mntmInstrumentalParameters;
    private final JMenuItem mntmLabCalibration;
    private final JMenuItem mntmExcludedZones;
    private final JMenuItem mntmBackgroundSubtraction;
    private final JMenuItem mntmRadialIntegration;
    private final JMenuItem mntmFindPeaks;
    private final JMenu mnPhaseId;
    private final JMenuItem mntmDatabase;
    private final JMenuItem mntmSumImages;
    private final JMenuItem mntmBatchConvert;
    private final JMenuItem mntmHpTools;
    private final JPanel panel;

    //sumimages
    ProgressMonitor pm;
    ImgFileUtils.BatchConvertFileWorker convwk;
    ImgOps.SumImagesFileWorker sumwk;
    private final JButton btnInstParameters;
    private final JSeparator separator;
    private final JPanel panel_1;
    private final JButton btnRadIntegr;
    private final JButton btnTtsdincoSol;
    private final JMenuItem mntmSubtractImages;
    private final JCheckBox chckbxPaintExz;
    private final JButton btnPeakSearchint;
    private final JMenu mnHelp;
    private final JMenuItem mntmManual;
    private final JMenuItem mntmFastopen;
    private final JMenuItem mntmScDataTo;
    private final JMenuItem mntmAzimuthalIntegration;
    private final JMenuItem mntmTtssoftware;
    private final JMenuItem mntmReset;
    private final JMenuItem mntmCheckForUpdates;
    Border etchedborder = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);
    private JMenuItem mntmAzimtheta;

    /**
     * Launch the application. ES POT PASSAR COM A ARGUMENT EL DIRECTORI DE TREBALL ON S'OBRIRAN PER DEFECTE ELS DIALEGS
     *
     */
    public static void main(final String[] args) {

        //first thing to do is read PAR files if exist
        FileUtils.getOS(); //sets the OS in fileUtils
        D2Dplot_global.readParFile();

        //LOGGER with the read parameters from file
        log = D2Dplot_global.getVavaLogger(className);
        System.out.println(log.logStatus());
        //        FileUtils.setLocale(Locale.getDefault());
        FileUtils.setLocale(null);
        final SystemInfo si = new SystemInfo();
        log.info(" ======================== d2Dplot session on " + FileUtils.fDiaHora.format(new Date())
                + " ========================");
        log.debug(si.MemInfo());

        CellSymm_global.setLogLevel(log.getLogLevelString());
        CellSymm_global.setLogging(D2Dplot_global.isLoggingConsole(), D2Dplot_global.isLoggingFile(),
                D2Dplot_global.isLoggingTA());

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            log.debug("L&F=" + UIManager.getLookAndFeel().toString());
            if (UIManager.getLookAndFeel().toString().contains("metal")) {
                //                UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsClassicLookAndFeel");
                UIManager.setLookAndFeel("com.sun.java.swing.plaf.gtk.GTKLookAndFeel");
                //                log.debug("L&F="+UIManager.getLookAndFeel().toString());
                //                UIManager.setLookAndFeel("com.sun.java.swing.plaf.motif.MotifLookAndFeel");
                //                log.debug("L&F="+UIManager.getLookAndFeel().toString());
            }
            // UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName()); //java metal

        } catch (final Throwable e) {
            if (D2Dplot_global.isDebug())
                e.printStackTrace();
            log.warning("Error initializing System look and feel");
            //            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        }
        log.debug("L&F=" + UIManager.getLookAndFeel().toString());

        final String THEME = "Ocean";

        if (UIManager.getLookAndFeel().toString().contains("metal")) {
            if (THEME.equals("DefaultMetal"))
                MetalLookAndFeel.setCurrentTheme(new DefaultMetalTheme());
            else if (THEME.equals("Ocean"))
                MetalLookAndFeel.setCurrentTheme(new OceanTheme());

            try {
                UIManager.setLookAndFeel(new MetalLookAndFeel());
            } catch (final UnsupportedLookAndFeelException e) {
                // Auto-generated catch block
                e.printStackTrace();
            }
        }

        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    frame = new MainFrame();
                    frame.inicialitza();
                    //AQUI POSO EL ARGUMENT LAUNCHER
                    D2Dplot_global.printAllOptions("config");
                    ArgumentLauncher.readArguments(frame, args);
                    if (ArgumentLauncher.isLaunchGraphics()) {
                        frame.showMainFrame();
                    } else {
                        log.info("Exiting...");
                        frame.mainF.dispose();
                        return;
                    }
                } catch (final Exception e) {
                    if (D2Dplot_global.isDebug())
                        e.printStackTrace();
                    log.severe("Error initializing main window");
                }
            }
        });
    }

    public void showMainFrame() {
        this.mainF.setLocationRelativeTo(null);
        this.mainF.setVisible(true);
    }

    /**
     * Create the frame.
     */
    public MainFrame() {
        this.mainF = new JFrame();
        this.mainF.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                MainFrame.this.do_this_windowClosing(e);
            }
        });
        this.mainF.setIconImage(Toolkit.getDefaultToolkit().getImage(MainFrame.class.getResource("/img/Icona.png")));
        this.mainF.setTitle("d2Dplot");
        this.mainF.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.mainF.setBounds(100, 100, 1129, 945);

        this.menuBar = new JMenuBar();
        this.mainF.setJMenuBar(this.menuBar);

        this.mnFile = new JMenu("File");
        this.mnFile.setMnemonic('f');
        this.menuBar.add(this.mnFile);

        this.mntmOpen = new JMenuItem("Open Image");
        this.mntmOpen.setMnemonic('o');
        this.mntmOpen.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_MASK));
        this.mntmOpen.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                MainFrame.this.do_mntmOpen_actionPerformed(e);
            }
        });
        this.mnFile.add(this.mntmOpen);

        this.mntmSaveImage = new JMenuItem("Save Image");
        this.mntmSaveImage.setMnemonic('s');
        this.mntmSaveImage.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_MASK));
        this.mntmSaveImage.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                MainFrame.this.do_mntmSaveImage_actionPerformed(e);
            }
        });
        this.mnFile.add(this.mntmSaveImage);

        this.mntmExportAsPng = new JMenuItem("Export as PNG");
        this.mntmExportAsPng.setMnemonic('e');
        this.mntmExportAsPng.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                MainFrame.this.do_mntmExportAsPng_actionPerformed(e);
            }
        });
        this.mnFile.add(this.mntmExportAsPng);

        this.mntmSumImages = new JMenuItem("Sum Images");
        this.mntmSumImages.setMnemonic('s');
        this.mntmSumImages.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                MainFrame.this.do_mntmSumImages_actionPerformed(e);
            }
        });
        this.mnFile.add(this.mntmSumImages);

        this.mntmBatchConvert = new JMenuItem("Batch Convert");
        this.mntmBatchConvert.setMnemonic('a');
        this.mntmBatchConvert.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                MainFrame.this.do_mntmBatchConvert_actionPerformed(e);
            }
        });

        this.mntmSubtractImages = new JMenuItem("Subtract Images");
        this.mntmSubtractImages.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                MainFrame.this.do_mntmSubtractImages_actionPerformed(e);
            }
        });
        this.mntmSubtractImages.setMnemonic('u');
        this.mnFile.add(this.mntmSubtractImages);
        this.mnFile.add(this.mntmBatchConvert);

        this.mntmFastopen = new JMenuItem("Fast Viewer");
        this.mntmFastopen.setMnemonic('f');
        this.mntmFastopen.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                MainFrame.this.do_mntmFastopen_actionPerformed(e);
            }
        });
        this.mnFile.add(this.mntmFastopen);

        this.separator = new JSeparator();
        this.mnFile.add(this.separator);

        this.mntmQuit = new JMenuItem("Quit");
        this.mntmQuit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                MainFrame.this.do_mntmQuit_actionPerformed(arg0);
            }
        });

        this.mntmReset = new JMenuItem("Reset");
        this.mntmReset.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                MainFrame.this.do_mntmReset_actionPerformed(e);
            }
        });
        this.mnFile.add(this.mntmReset);
        this.mntmQuit.setMnemonic('q');
        this.mntmQuit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, InputEvent.CTRL_MASK));
        this.mnFile.add(this.mntmQuit);

        this.mnImageOps = new JMenu("Image");
        this.mnImageOps.setMnemonic('i');
        this.menuBar.add(this.mnImageOps);

        this.mntmInstrumentalParameters = new JMenuItem("Instrumental Parameters");
        this.mntmInstrumentalParameters.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_I, InputEvent.CTRL_MASK));
        this.mntmInstrumentalParameters.setMnemonic('i');
        this.mntmInstrumentalParameters.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                MainFrame.this.do_mntmInstrumentalParameters_actionPerformed(e);
            }
        });
        this.mnImageOps.add(this.mntmInstrumentalParameters);

        this.mntmLabCalibration = new JMenuItem("Inst. Param. Calibration");
        this.mntmLabCalibration.setMnemonic('c');
        this.mntmLabCalibration.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                MainFrame.this.do_mntmLabCalibration_actionPerformed(e);
            }
        });
        this.mnImageOps.add(this.mntmLabCalibration);

        this.mntmExcludedZones = new JMenuItem("Excluded Zones");
        this.mntmExcludedZones.setMnemonic('x');
        this.mntmExcludedZones.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                MainFrame.this.do_mntmExcludedZones_actionPerformed(e);
            }
        });
        this.mnImageOps.add(this.mntmExcludedZones);

        this.mntmBackgroundSubtraction = new JMenuItem("Background Subtraction");
        this.mntmBackgroundSubtraction.setMnemonic('b');
        this.mntmBackgroundSubtraction.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                MainFrame.this.do_mntmBackgroundSubtraction_actionPerformed(e);
            }
        });
        this.mnImageOps.add(this.mntmBackgroundSubtraction);

        this.mntmRadialIntegration = new JMenuItem("Conversion to 1D PXRD");
        this.mntmRadialIntegration.setToolTipText("Debye rings integration (cakes)");
        this.mntmRadialIntegration.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, InputEvent.CTRL_MASK));
        this.mntmRadialIntegration.setMnemonic('1');
        this.mntmRadialIntegration.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                MainFrame.this.do_mntmRadialIntegration_actionPerformed(e);
            }
        });
        this.mnImageOps.add(this.mntmRadialIntegration);

        this.mntmHpTools = new JMenuItem("HP Cu Pcalc.");
        this.mntmHpTools.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                MainFrame.this.do_mntmHpTools_actionPerformed(e);
            }
        });

        this.mntmAzimuthalIntegration = new JMenuItem("Azimuthal (circular) plot");
        this.mntmAzimuthalIntegration.setMnemonic('a');
        this.mntmAzimuthalIntegration.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                MainFrame.this.do_mntmAzimuthalIntegration_actionPerformed(arg0);
            }
        });
        this.mnImageOps.add(this.mntmAzimuthalIntegration);
        
        mntmAzimtheta = new JMenuItem("Azim2theta");
        mntmAzimtheta.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                do_mntmAzimtheta_actionPerformed(e);
            }
        });
        mnImageOps.add(mntmAzimtheta);
        this.mntmHpTools.setMnemonic('h');
        this.mnImageOps.add(this.mntmHpTools);

        this.mnGrainAnalysis = new JMenu("Grain Analysis");
        this.mnGrainAnalysis.setMnemonic('g');
        this.menuBar.add(this.mnGrainAnalysis);

        this.mntmDincoSol = new JMenuItem("Load tts-INCO SOL/PCS files");
        this.mntmDincoSol.setMnemonic('l');
        this.mntmDincoSol.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                MainFrame.this.do_mntmDincoSol_actionPerformed(e);
            }
        });

        this.mntmFindPeaks = new JMenuItem("Find/Integrate Peaks");
        this.mntmFindPeaks.setMnemonic('f');
        this.mntmFindPeaks.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                MainFrame.this.do_mntmFindPeaks_actionPerformed(arg0);
            }
        });
        this.mnGrainAnalysis.add(this.mntmFindPeaks);

        this.mntmTtssoftware = new JMenuItem("Run tts_Software");
        this.mntmTtssoftware.setMnemonic('t');
        this.mntmTtssoftware.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                MainFrame.this.do_mntmTtssoftware_actionPerformed(e);
            }
        });
        this.mnGrainAnalysis.add(this.mntmTtssoftware);
        this.mnGrainAnalysis.add(this.mntmDincoSol);

        this.mntmLoadXdsFile = new JMenuItem("Load XDS file");
        this.mntmLoadXdsFile.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                MainFrame.this.do_mntmLoadXdsFile_actionPerformed(e);
            }
        });
        this.mnGrainAnalysis.add(this.mntmLoadXdsFile);

        //        mntmClearAll = new JMenuItem("Clear all");
        //        mntmClearAll.addActionListener(new ActionListener() {
        //            public void actionPerformed(ActionEvent e) {
        //                do_mntmClearAll_actionPerformed(e);
        //            }
        //        });

        this.mntmScDataTo = new JMenuItem("SC data to tts_INCO");
        this.mntmScDataTo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                MainFrame.this.do_mntmScDataTo_actionPerformed(e);
            }
        });
        this.mnGrainAnalysis.add(this.mntmScDataTo);
        //        mnGrainAnalysis.add(mntmClearAll);

        this.mnPhaseId = new JMenu("Phase ID");
        this.mnPhaseId.setMnemonic('p');
        this.menuBar.add(this.mnPhaseId);

        this.mntmDatabase = new JMenuItem("Database");
        this.mntmDatabase.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                MainFrame.this.do_mntmDatabase_actionPerformed(e);
            }
        });
        this.mntmDatabase.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_D, InputEvent.CTRL_MASK));
        this.mntmDatabase.setMnemonic('d');
        this.mnPhaseId.add(this.mntmDatabase);

        this.mnHelp = new JMenu("Help");
        this.menuBar.add(this.mnHelp);

        this.mntmAbout = new JMenuItem("About");
        this.mnHelp.add(this.mntmAbout);

        this.mntmManual = new JMenuItem("Manual");
        this.mntmManual.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                MainFrame.this.do_mntmManual_actionPerformed(e);
            }
        });
        this.mnHelp.add(this.mntmManual);

        this.mntmCheckForUpdates = new JMenuItem("Check for updates");
        this.mntmCheckForUpdates.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                MainFrame.this.do_mntmCheckForUpdates_actionPerformed(e);
            }
        });
        this.mnHelp.add(this.mntmCheckForUpdates);

        //        mnDebug = new JMenu("debug");
        //        menuBar.add(mnDebug);
        //        
        //        mntmSavetif = new JMenuItem("saveTif");
        //        mntmSavetif.addActionListener(new ActionListener() {
        //            public void actionPerformed(ActionEvent e) {
        //                do_mntmSavetif_actionPerformed(e);
        //            }
        //        });
        //        mnDebug.add(mntmSavetif);
        this.mntmAbout.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                MainFrame.this.do_mntmAbout_actionPerformed(e);
            }
        });
        this.contentPane = new JPanel();
        this.contentPane.setBorder(null);
        this.mainF.setContentPane(this.contentPane);
        this.contentPane.setLayout(new MigLayout("fill, insets 0", "[1200px,grow]", "[900px,grow]"));
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
        this.panel_stat.setLayout(new MigLayout("fill", "[1166px]", "[100px,grow]"));
        this.scrollPane = new JScrollPane();
        this.scrollPane.setViewportBorder(null);
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
        this.panel_all.setLayout(new MigLayout("fill", "[1200px,grow]", "[][grow]"));

        this.panel_2 = new JPanel();
        this.panel_all.add(this.panel_2, "cell 0 0,grow");
        this.panel_2.setLayout(new MigLayout("fill,insets 0", "[][grow][][]", "[grow]"));

        this.btnOpen = new JButton("Open Image");
        this.panel_2.add(this.btnOpen, "cell 0 0,alignx center,aligny center");
        this.btnOpen.setMargin(new Insets(2, 7, 2, 7));
        this.btnOpen.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                MainFrame.this.do_btnOpen_actionPerformed(arg0);
            }
        });
        this.lblOpened = new JLabel("(no image loaded)");
        this.lblOpened.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                MainFrame.this.do_lblOpened_mouseReleased(e);
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                MainFrame.this.do_lblOpened_mouseEntered(e);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                MainFrame.this.do_lblOpened_mouseExited(e);
            }
        });
        this.panel_2.add(this.lblOpened, "cell 1 0,alignx left,aligny center");

        this.btnPrev = new JButton("<");
        this.btnPrev.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                MainFrame.this.do_btnPrev_actionPerformed(e);
            }
        });
        this.panel_2.add(this.btnPrev, "cell 2 0,alignx center,aligny center");
        this.btnPrev.setToolTipText("Previous image (by index)");

        this.btnNext = new JButton(">");
        this.btnNext.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                MainFrame.this.do_btnNext_actionPerformed(arg0);
            }
        });
        this.panel_2.add(this.btnNext, "cell 3 0,alignx center,aligny center");
        this.btnNext.setToolTipText("Next image (by index)");
        this.splitPane_1 = new JSplitPane();
        this.splitPane_1.setContinuousLayout(true);
        this.splitPane_1.setResizeWeight(1.0);
        this.panel_all.add(this.splitPane_1, "cell 0 1,grow");

        this.panelImatge = new ImagePanel();

        final JScrollPane jspimage = new JScrollPane();
        jspimage.setViewportBorder(null);
        jspimage.setBorder(null);
        jspimage.setViewportView(this.panelImatge.getIpanelMain());
        this.splitPane_1.setLeftComponent(jspimage);
        this.panelImatge.getIpanelMain().setBorder(null);
        this.panelImatge.getIpanelMain().setBorder(this.etchedborder);
        this.panel_controls = new JPanel();

        final JScrollPane jscontrols = new JScrollPane();
        jscontrols.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        jscontrols.setViewportBorder(null);
        jscontrols.setBorder(null);
        jscontrols.setViewportView(this.panel_controls);
        this.splitPane_1.setRightComponent(jscontrols);

        this.panel_controls.setLayout(new MigLayout("fill", "[130px,grow]",
                "[5px:20px:35px,grow][::160px,grow][::85px,grow][::140px,grow][::130px,grow][grow]"));

        this.btnInstParameters = new JButton("I. Parameters");
        this.btnInstParameters.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                MainFrame.this.do_btnInstParameters_actionPerformed(arg0);
            }
        });
        this.panel_controls.add(this.btnInstParameters, "cell 0 0,growx,aligny center");
        this.panel_opcions = new JPanel();
        this.panel_controls.add(this.panel_opcions, "cell 0 1,grow");
        this.panel_opcions.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Plot",
                TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
        this.btnResetView = new JButton("Fit");
        this.btnResetView.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                MainFrame.this.do_btnResetView_actionPerformed(arg0);
            }
        });
        this.panel_opcions.setLayout(new MigLayout("fill, insets 2", "[60px][60]",
                "[5px:20px:35px][5px:20px:35px][5px:20px:35px][5px:20px:35px]"));
        this.btnResetView.setMargin(new Insets(2, 2, 2, 2));
        this.panel_opcions.add(this.btnResetView, "cell 0 0,growx,aligny center");
        this.btnMidaReal = new JButton("100%");
        this.btnMidaReal.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                MainFrame.this.do_btnMidaReal_actionPerformed(e);
            }
        });
        this.btnMidaReal.setMargin(new Insets(2, 2, 2, 2));
        this.panel_opcions.add(this.btnMidaReal, "cell 1 0,growx,aligny center");
        this.btn05x = new JButton("0.5x");
        this.panel_opcions.add(this.btn05x, "cell 0 1,growx,aligny center");
        this.btn05x.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                MainFrame.this.do_btn05x_actionPerformed(arg0);
            }
        });
        this.btn05x.setMargin(new Insets(1, 2, 1, 2));
        this.btn2x = new JButton("2x");
        this.panel_opcions.add(this.btn2x, "cell 1 1,growx,aligny center");
        this.btn2x.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                MainFrame.this.do_btn2x_actionPerformed(e);
            }
        });
        this.btn2x.setMargin(new Insets(1, 7, 1, 7));

        this.chckbxColor = new JCheckBox("Color");
        this.chckbxColor.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent arg0) {
                MainFrame.this.do_chckbxColor_itemStateChanged(arg0);
            }
        });
        this.panel_opcions.add(this.chckbxColor, "cell 0 2");

        this.chckbxPaintExz = new JCheckBox("Paint ExZ");
        this.chckbxPaintExz.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent arg0) {
                MainFrame.this.do_chckbxPaintExz_itemStateChanged(arg0);
            }
        });

        this.chckbxInvertY = new JCheckBox("flip Y");
        this.chckbxInvertY.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                MainFrame.this.do_chckbxInvertY_itemStateChanged(e);
            }
        });
        this.panel_opcions.add(this.chckbxInvertY, "cell 1 2");
        this.panel_opcions.add(this.chckbxPaintExz, "cell 0 3 2 1");

        this.panel = new JPanel();
        this.panel.setBorder(new TitledBorder(null, "Points", TitledBorder.LEADING, TitledBorder.TOP, null, null));
        this.panel_controls.add(this.panel, "cell 0 2,grow");
        this.panel.setLayout(new MigLayout("fill, insets 2", "[]", "[5px:20px][5px:20px,grow]"));
        this.chckbxIndex = new JCheckBox("Sel. Points");
        chckbxIndex.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                do_chckbxIndex_itemStateChanged(e);
            }
        });
        this.panel.add(this.chckbxIndex, "cell 0 0,growx,aligny center");
        this.chckbxIndex.setSelected(true);
        this.btnSaveDicvol = new JButton("Points List");
        this.panel.add(this.btnSaveDicvol, "cell 0 1,growx,aligny center");
        this.btnSaveDicvol.setMinimumSize(new Dimension(100, 28));
        this.btnSaveDicvol.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                MainFrame.this.do_btnSaveDicvol_actionPerformed(arg0);
            }
        });
        this.btnSaveDicvol.setMargin(new Insets(2, 2, 2, 2));

        this.panel_3 = new JPanel();
        this.panel_3.setBorder(new TitledBorder(new LineBorder(new Color(184, 207, 229)), "Phase ID",
                TitledBorder.LEADING, TitledBorder.TOP, null, new Color(51, 51, 51)));
        this.panel_controls.add(this.panel_3, "cell 0 3,grow");
        this.panel_3.setLayout(
                new MigLayout("fill, insets 2, hidemode 3", "[120px]", "[5px:20px][][5px:20px][5px:20px][]"));

        this.btnDbdialog = new JButton("Database");
        this.btnDbdialog.setMargin(new Insets(2, 2, 2, 2));
        this.panel_3.add(this.btnDbdialog, "cell 0 0,growx,aligny center");

        this.separator_1 = new JSeparator();
        this.panel_3.add(this.separator_1, "cell 0 1,growx,aligny center");

        this.chckbxShowRings = new JCheckBox("Quicklist");
        this.panel_3.add(this.chckbxShowRings, "cell 0 2,growx,aligny center");

        combo_LATdata = new JComboBox<PDCompound>();
        combo_LATdata.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent arg0) {
                MainFrame.this.do_combo_showRings_itemStateChanged(arg0);
            }
        });
        this.panel_3.add(combo_LATdata, "cell 0 3,growx,aligny center");
        combo_LATdata.setModel(new DefaultComboBoxModel<PDCompound>(new PDCompound[] {}));

        this.btnAddLat = new JButton("Add to List");
        this.btnAddLat.setVisible(false);
        this.btnAddLat.setEnabled(false);
        this.panel_3.add(this.btnAddLat, "cell 0 4,growx,aligny center");

        this.chckbxShowRings.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent arg0) {
                MainFrame.this.do_chckbxShowRings_itemStateChanged(arg0);
            }
        });
        this.btnDbdialog.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                MainFrame.this.do_btnDbdialog_actionPerformed(arg0);
            }
        });
        this.panel_1 = new JPanel();
        this.panel_1.setBorder(new TitledBorder(null, "Shortcuts", TitledBorder.LEADING, TitledBorder.TOP, null, null));
        this.panel_controls.add(this.panel_1, "cell 0 4,grow");
        this.panel_1.setLayout(new MigLayout("fill, insets 2", "[grow]", "[5px:20px][5px:20px][5px:20px]"));

        this.btnPeakSearchint = new JButton("Peak search");
        this.btnPeakSearchint.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                MainFrame.this.do_btnPeakSearchint_actionPerformed(arg0);
            }
        });
        this.panel_1.add(this.btnPeakSearchint, "cell 0 1,growx,aligny center");

        this.btnTtsdincoSol = new JButton("tts-INCO SOL");
        this.btnTtsdincoSol.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                MainFrame.this.do_btnTtsdincoSol_actionPerformed(e);
            }
        });
        this.panel_1.add(this.btnTtsdincoSol, "flowy,cell 0 2,growx,aligny center");

        this.btnRadIntegr = new JButton("1D PXRD");
        this.btnRadIntegr.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                MainFrame.this.do_btnRadIntegr_actionPerformed(e);
            }
        });
        this.panel_1.add(this.btnRadIntegr, "cell 0 0,growx,aligny center");
    }

    private void inicialitza() {

        if (D2Dplot_global.loggingTA)
            VavaLogger.setTArea(this.tAOut);
        //    	D2Dplot_global.setMainTA(this.gettAOut());

        //HO FEM CABRE (170322)
        this.mainF.setSize(MainFrame.getDef_Width(), MainFrame.getDef_Height()); //ho centra el metode main
        final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        while (this.mainF.getWidth() > screenSize.width) {
            this.mainF.setSize(this.mainF.getWidth() - 100, this.mainF.getHeight());
        }
        while (this.mainF.getHeight() > screenSize.height) {
            this.mainF.setSize(this.mainF.getWidth(), this.mainF.getHeight() - 100);
        }
        this.welcomeMsg();

        if (D2Dplot_global.isConfigFileReaded() == null) {
            log.info(String.format("No config file found on: %s, it will be created on exit!",
                    D2Dplot_global.configFilePath));
        } else {
            if (D2Dplot_global.isConfigFileReaded() == true) {
                log.info(String.format("Config file readed: %s", D2Dplot_global.configFilePath));
            } else {
                log.info(String.format("Error reading config file: %s", D2Dplot_global.configFilePath));
            }
        }
        D2Dplot_global.initPars();
        D2Dplot_global.init_ApplyColorsToIPanel(this.getPanelImatge());
        D2Dplot_global.checkDBs();
        final boolean ok = PDDatabase.populateQuickList(false);
        if (ok) {
            log.info(String.format("QuickList DB file: %s", PDDatabase.getLocalQL()));
        }
        combo_LATdata.setPrototypeDisplayValue(new PDCompound("Silicon")); //mida minima
        updateQuickList();

        //MIDES BUTONS
        this.btnInstParameters.setPreferredSize(new Dimension(110, 30));
        this.btnInstParameters.setMinimumSize(new Dimension(110, 15));
        this.btnResetView.setPreferredSize(new Dimension(50, 30));
        this.btnResetView.setMinimumSize(new Dimension(50, 15));
        this.btnMidaReal.setPreferredSize(new Dimension(50, 30));
        this.btnMidaReal.setMinimumSize(new Dimension(50, 15));
        this.btn05x.setPreferredSize(new Dimension(50, 30));
        this.btn05x.setMinimumSize(new Dimension(50, 15));
        this.btn2x.setPreferredSize(new Dimension(50, 30));
        this.btn2x.setMinimumSize(new Dimension(50, 15));
        this.btnSaveDicvol.setPreferredSize(new Dimension(110, 30));
        this.btnSaveDicvol.setMinimumSize(new Dimension(110, 15));
        this.btnDbdialog.setPreferredSize(new Dimension(110, 30));
        this.btnDbdialog.setMinimumSize(new Dimension(110, 15));
        this.btnPeakSearchint.setPreferredSize(new Dimension(110, 30));
        this.btnPeakSearchint.setMinimumSize(new Dimension(110, 15));
        this.btnTtsdincoSol.setPreferredSize(new Dimension(110, 30));
        this.btnTtsdincoSol.setMinimumSize(new Dimension(110, 15));
        this.btnRadIntegr.setPreferredSize(new Dimension(110, 30));
        this.btnRadIntegr.setMinimumSize(new Dimension(110, 15));

        if (D2Dplot_global.autoCheckNewVersion)
            this.checkNewVersion(false);

        //        mainF.pack();
        
        if (!D2Dplot_global.developing) { //coses a amagar al fer release
            mntmAzimtheta.setVisible(false);
        }
    }

    //tanco tot
    private void reset() {
        this.fileOpened = false;
        this.panelImatge.setPatt2D(null);
        this.panelImatge.setImage(null);
        this.patt2D = null;
    }

    private void restart() {
        this.reset();
        this.panelImatge.actualitzarVista();
        this.closePanels();
        //        this.resetPanels(); //TODO:mirar si cal...
        this.stopBatchConvert();
        this.stopSumPatterns();
        this.tAOut.cls();
        this.lblOpened.setText("(no image loaded)");
        this.welcomeMsg();
    }

    private void welcomeMsg() {

//      log.info(FileUtils.getCenteredString(D2Dplot_global.welcomeMSG[0],60));
//      log.info(FileUtils.getCenteredString(D2Dplot_global.welcomeMSG[1],60));
//      log.info(FileUtils.getCenteredString(D2Dplot_global.welcomeMSG[2],60));
//      log.info("");
//      tAOut.afegirText(true, false, FileUtils.getCenteredString(D2Dplot_global.welcomeMSG[0]+" session on "+FileUtils.fDiaHora.format(new Date()),80));
//      tAOut.afegirText(true, false, FileUtils.getCenteredString(D2Dplot_global.welcomeMSG[1],80));
//      tAOut.afegirText(true, false, FileUtils.getCenteredString(D2Dplot_global.welcomeMSG[2],80));
//      tAOut.afegirText(true, false, "");

      tAOut.afegirText(true, false, "--> "+D2Dplot_global.welcomeMSG[0]);
      tAOut.afegirText(true, false, "    "+D2Dplot_global.welcomeMSG[1]);
//      tAOut.afegirText(true, false, D2Dplot_global.welcomeMSG[2]);
      tAOut.afegirText(true, false, "");
//      tAOut.afegirText(true, false, "Session started on "+FileUtils.fDiaHora.format(new Date()));
      tAOut.afegirText(true, false, FileUtils.fDiaHora.format(new Date())+" See help menu for info");
    }
    
    private void closePanels() {
        if (this.paramDialog != null)
            this.paramDialog.dispose(); //no cal fer res addicional
        if (this.d2DsubWin != null)
            this.d2DsubWin.dispose();//ja te stop process
        if (this.d2DsubWinBatch != null)
            this.d2DsubWinBatch.dispose(); //ja te stop process
        if (this.calibration != null)
            this.calibration.dispose();
        if (this.exZones != null)
            this.exZones.dispose();
        if (this.dincoFrame != null)
            this.dincoFrame.dispose();
        if (this.pksframe != null)
            this.pksframe.dispose();
        if (this.pkListWin != null)
            this.pkListWin.dispose();
        if (this.irWin != null)
            this.irWin.dispose();
        if (this.p2dAbout != null)
            this.p2dAbout.dispose();
        if (this.dbDialog != null)
            this.dbDialog.dispose();
        if (this.hpframe != null)
            this.hpframe.dispose();
        if (this.iazWin != null)
            this.iazWin.dispose();
        if (this.tts != null)
            this.tts.dispose();
        if (this.fastView != null)
            this.fastView.dispose();
        if (this.scToInco != null)
            this.scToInco.dispose();
        //TODO ADDD NEW THINGS IF NECESSARY
    }

    private void updatePanels() {
        if (this.paramDialog != null) { //actualitza els camps
            this.updateIparameters();
        }
        if (this.d2DsubWin != null) { //cal deixar-lo pel tema de patt before and after
            this.d2DsubWin.userInit();
        }
        if (this.calibration != null) { //el deixo perque inicia algunes coses extres
            this.calibration.inicia();
            ;
        }
        if (this.exZones != null) { //actualitza les exzones
            this.exZones.inicia();
        }
        if (this.dincoFrame != null) {
            this.dincoFrame.inicia(); //borra les solucions de la llista
        }
        if (this.pksframe != null) { //borra la llista de pics
            this.pksframe.inicia(false);
        }
        if (this.pkListWin != null) { //borra la llista de pics
            this.pkListWin.loadPeakList();
        }
        //TODO ADDD NEW THINGS IF NECESSARY

    }

    public void updatePatt2D(File d2File) {
        D2Dplot_global.setWorkdir(d2File);
        log.debug("workdir=" + getWorkdir());
        this.patt2D = ImgFileUtils.readPatternFile(d2File, true);

        if (this.patt2D != null) {
            this.panelImatge.setImagePatt2D(this.patt2D);
            this.panelImatge.setMainFrame(this);
            this.lblOpened.setText(d2File.toString());
            log.info("File opened: " + d2File.getName() + ", " + this.patt2D.getPixCount() + " pixels ("
                    + (int) this.patt2D.getMillis() + " ms)\n" + this.patt2D.getInfo());
            //            tAOut.stat("File opened: " + d2File.getName() + ", " + patt2D.getPixCount() + " pixels ("
            //                    + (int) patt2D.getMillis() + " ms)");
            //            tAOut.ln(patt2D.getInfo());
            this.fileOpened = true;
            this.openedFile = d2File;
            this.updateIparameters();
        } else {
            log.info("Error reading 2D file");
            log.info("No file opened");
            this.fileOpened = false;
            this.openedFile = null;
        }
    }

    public void updatePatt2D(Pattern2D p2D, boolean verbose, boolean refreshPanels) {
        this.patt2D = p2D;
        if (this.patt2D != null) {
            this.panelImatge.setImagePatt2D(this.patt2D);
            this.panelImatge.setMainFrame(this);
            String fname;
            if (this.patt2D.getImgfile() != null) {
                fname = this.patt2D.getImgfile().getAbsolutePath(); //tret el getname
                if (verbose) {
                    log.info("File opened: " + fname + ", " + this.patt2D.getPixCount() + " pixels ("
                            + (int) this.patt2D.getMillis() + " ms)\n" + this.patt2D.getInfo());
                } else {
                    log.info("File opened: " + fname + ", " + this.patt2D.getPixCount() + " pixels ("
                            + (int) this.patt2D.getMillis() + " ms)");
                }
            } else {
                fname = "Data image not saved to a file";
                log.info("File opened: Data image not saved to a file");
            }
            this.lblOpened.setText(fname);
            this.fileOpened = true;
            this.openedFile = this.patt2D.getImgfile();
            this.updateIparameters();
            if (refreshPanels)
                this.updatePanels();
        } else {
            log.info("Error reading 2D file");
            log.info("No file opened");
            this.fileOpened = false;
            this.openedFile = null;
        }
    }

    public void updateFromTTS(File d2File) {
        if (d2File.exists()) {
            this.reset();
            this.updatePatt2D(d2File);
            this.updatePanels();
            return;
        } else {
            log.info("No file found with filename: " + d2File.getAbsolutePath());
        }
    }

    public void updateIparameters() {
        if (this.patt2D != null) {
            if (this.paramDialog != null) {
                this.paramDialog.inicia();
            }
        }
    }

    public PDCompound getQuickListCompound() {
        log.debug("getQuickListCompound CALLED");
        try {
            final PDCompound pdc = (PDCompound) combo_LATdata.getSelectedItem();
            return pdc;
        } catch (final Exception e) {
            log.debug("error in quicklist casting to PDCompound");
            return null;
        }
    }

    public void setViewExZ(boolean state) {
        this.chckbxPaintExz.setSelected(state);
    }

    private void openImgFile() {
        final FileNameExtensionFilter filt[] = ImgFileUtils.getExtensionFilterRead();
        final File d2File = FileUtils.fchooserOpen(this.mainF, new File(getWorkdir()), filt, 0);
        if (d2File == null) {
            if (!this.fileOpened) {
                log.info("No data file selected");
            }
            return;
        }
        D2Dplot_global.setWorkdir(d2File);

        // resetejem
        this.reset();
        this.updatePatt2D(d2File);
        this.updatePanels();
    }

    private void saveImgFile() {
        if (this.getPatt2D() != null) {
            final FileNameExtensionFilter filt[] = ImgFileUtils.getExtensionFilterWrite();
            final File f = FileUtils.fchooserSaveAsk(this.mainF, new File(getWorkdir()), filt, null);
            if (f != null) {
                D2Dplot_global.setWorkdir(f);
                final File outf = ImgFileUtils.writePatternFile(f, this.getPatt2D(), true); //true, malgrat en principi el fchooserSaveAsk hauria de detectar l'extensio
                if (outf != null) {
                    final int n = JOptionPane.showConfirmDialog(this.mainF, "Load the new saved file?", "Refresh file",
                            JOptionPane.YES_NO_OPTION);
                    if (n == JOptionPane.YES_OPTION) {
                        this.reset();
                        this.updatePatt2D(outf);
                        this.updatePanels();
                    }
                } else {
                    log.warning("Error saving file");
                }
            }
        } else {
            log.info("No image to save!");
        }
    }

    private void checkNewVersion(boolean dialogShowNo) {
        String bona = "";
        final String url = "https://www.cells.es/en/beamlines/bl04-mspd/preparing-your-experiment";
        try {
            final URL mspd = new URL(url);
            final BufferedReader in = new BufferedReader(new InputStreamReader(mspd.openStream()));
            try {
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    if (FileUtils.containsIgnoreCase(inputLine, "d2Dplot software for windows v")) {
                        bona = inputLine;
                        break;
                    }
                }
            } catch (final Exception e2) {
                if (D2Dplot_global.isDebug())
                    e2.printStackTrace();
            } finally {
                in.close();
            }
        } catch (final Exception e1) {
            if (D2Dplot_global.isDebug())
                e1.printStackTrace();
            this.tAOut.stat("Error checking for new versions");
        }

        //<li><strong>D2Dplot:</strong> 2D diffraction data visualization with basic operations and phase ID [<a title="d2Dplot software for windows v1811" href="https://www.cells.es/en/beamlines/bl04-mspd/d2dplot1901win_181212.zip" class="internal-link" target="_self">windows</a> <a title="d2Dplot software for linux v1811" href="https://www.cells.es/en/beamlines/bl04-mspd/d2dplot1901lin_181212-tar.gz" class="internal-link" target="_self">linux</a>].<small><em> Last update: 12 Dec 2018</em></small>.</li>

        if (bona.length() > 0) {
            //          String data = bona.split("tar.gz")[0];
            //          data = data.split("lin_")[1];
            String data = bona.split(".zip")[0];
            data = data.split("win_")[1];
            final int webVersion = Integer.parseInt(data);
            if (webVersion > D2Dplot_global.build_date) {
                //              FileUtils.InfoDialog(this, "New d2Dplot version is available ("+webVersion+"). Please download at\nhttps://www.cells.es/en/beamlines/bl04-mspd/preparing-your-experiment", "New version available!");
                final boolean yes = FileUtils.YesNoDialog(this.mainF,
                        "New d2Dplot version is available (" + webVersion + "). Please download at\n" + url,
                        "New version available!");
                if (yes) {
                    FileUtils.openURL(url);
                }
            }
            if ((webVersion <= D2Dplot_global.build_date) && (dialogShowNo)) {
                FileUtils.InfoDialog(this.mainF,
                        "You have the last version of d2Dplot (" + D2Dplot_global.build_date + ").",
                        "d2Dplot is up to date");
            }
        }
    }

    private void stopBatchConvert() {
        if (this.convwk != null) {
            this.convwk.cancel(true);
        }
    }

    private void stopSumPatterns() {
        if (this.sumwk != null) {
            this.sumwk.cancel(true);
        }
    }

    protected void do_this_windowClosing(WindowEvent e) {
        //FIRST SAVE OPTIONS
        log.debug("WINDOW CLOSING CALLED");
        D2Dplot_global.writeParFile();

        //SECOND SAVE QL FILE IF MODIFIED
        if (PDDatabase.isQLmodified()) {
            //prompt and save QL file if necessary
            final Object[] options = { "Yes", "No" };
            final int n = JOptionPane.showOptionDialog(this.mainF,
                    "QuickList has changed, overwrite current default file?", "Update QL", JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE, null, //do not use a custom Icon
                    options, //the titles of buttons
                    options[1]); //default button title
            if (n == JOptionPane.YES_OPTION) {
                final PDDatabase.SaveDBfileWorker saveDBFwk = new PDDatabase.SaveDBfileWorker(
                        new File(PDDatabase.getLocalQL()), true);
                saveDBFwk.execute();
                int maxCount = 20; //maximum wait 10 seconds
                while (!saveDBFwk.isDone() || maxCount <= 0) {
                    try {
                        Thread.sleep(500);
                        maxCount = maxCount - 1;
                    } catch (final InterruptedException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        }
        this.mainF.dispose();
    }

    protected void do_btn05x_actionPerformed(ActionEvent arg0) {
        if (this.panelImatge.getScalefit() > ImagePanel.getMinScaleFit()) {
            this.panelImatge.setScalefit(this.panelImatge.getScalefit() * 0.5f);
        }
    }

    protected void do_btn2x_actionPerformed(ActionEvent e) {
        if (this.panelImatge.getScalefit() < ImagePanel.getMaxScaleFit()) {
            this.panelImatge.setScalefit(this.panelImatge.getScalefit() * 2.0f);
        }
    }

    protected void do_btnInstParameters_actionPerformed(ActionEvent arg0) {
        this.mntmInstrumentalParameters.doClick();
    }

    protected void do_btnTtsdincoSol_actionPerformed(ActionEvent e) {
        this.mntmDincoSol.doClick();
    }

    protected void do_btnRadIntegr_actionPerformed(ActionEvent e) {
        this.mntmRadialIntegration.doClick();
    }

    protected void do_btnMidaReal_actionPerformed(ActionEvent e) {
        this.panelImatge.setScalefit(1.0f);
    }

    protected void do_btnOpen_actionPerformed(ActionEvent arg0) {
        this.openImgFile();
    }

    protected void do_btnResetView_actionPerformed(ActionEvent arg0) {
        this.panelImatge.resetView();
    }

    // Obre finestra amb llista de pics
    protected void do_btnSaveDicvol_actionPerformed(ActionEvent arg0) {
        if (this.patt2D != null) {
            if (this.pkListWin != null) {
                this.pkListWin.tanca();
            }
            this.pkListWin = new PeakList(this.mainF, this.panelImatge);
            this.pkListWin.setVisible(true);
            this.pkListWin.toFront();
        }
    }

    protected void do_btnNext_actionPerformed(ActionEvent arg0) {

        final String fnameCurrent = FileUtils.getFNameNoExt(this.patt2D.getImgfile());
        final String fextCurrent = FileUtils.getExtension(this.patt2D.getImgfile());
        log.debug("fnameCurrent (no Ext): " + fnameCurrent);
        log.debug("fextCurrent: " + fextCurrent);

        //agafem ultims 4 digits (index), sumem 1 i el tornem a posar com a fitxer a veure què
        String fnameExtNew = "";
        try {
            log.debug("substring " + fnameCurrent.substring(fnameCurrent.length() - 4, fnameCurrent.length()));
            int imgNum = Integer.parseInt(fnameCurrent.substring(fnameCurrent.length() - 4, fnameCurrent.length()));
            imgNum = imgNum + 1;
            fnameExtNew = fnameCurrent.substring(0, fnameCurrent.length() - 4) + String.format("%04d", imgNum) + "."
                    + fextCurrent;
        } catch (final Exception e) {
            log.debug("trying to get the file numbering");
            final int indexGuio = fnameCurrent.lastIndexOf("_");
            if (indexGuio > 0) {
                log.debug("index guio=" + indexGuio);
                int imgNum = Integer.parseInt(fnameCurrent.substring(indexGuio + 1, fnameCurrent.length()));
                imgNum = imgNum + 1;
                final int lenformat = fnameCurrent.length() - indexGuio - 1;
                log.debug("lenformat=" + lenformat);
                final String format = "%0" + lenformat + "d";
                log.debug("format=" + format);
                fnameExtNew = fnameCurrent.substring(0, fnameCurrent.length() - lenformat)
                        + String.format(format, imgNum) + "." + fextCurrent;
                log.debug("fnameExtNew=" + fnameExtNew);
            }
        }

        File d2File = new File(fnameExtNew);
        if (d2File.exists()) {
            this.reset();
            this.updatePatt2D(d2File);
            this.updatePanels();
            return;
        } else {
            log.info("No file found with filename: " + fnameExtNew);
        }

        //SECOND TEST buscar dXXX_0000.edf (realment podriem mirar la part esquerra del guio per ser mes generals...)

        //agafem el nom sense el _000X.edf
        final String basFname = fnameCurrent.substring(0, fnameCurrent.length() - 5);
        log.debug("basFname=" + basFname);
        int indexNoDigit = -1;
        for (int i = 1; i < basFname.length() - 2; i++) {
            final char c = basFname.charAt(basFname.length() - i);
            if (!Character.isDigit(c)) {
                indexNoDigit = i;
                break;
            }
        }
        log.debug("indexNoDigit=" + indexNoDigit);
        if (indexNoDigit > 0) {
            final String sdom = basFname.substring(basFname.length() - (indexNoDigit - 1), basFname.length());
            log.debug("sdom=" + sdom);
            int ndom = -1;
            try {
                ndom = Integer.parseInt(sdom);
            } catch (final Exception ex) {
                log.debug("error parsing domain number");
            }
            if (ndom >= 0) {
                ndom = ndom + 1;
                fnameExtNew = basFname.substring(0, basFname.length() - (indexNoDigit - 1))
                        .concat(Integer.toString(ndom)).concat("_0000.").concat(fextCurrent);
                log.debug("fnameExtNew=" + fnameExtNew);
            }
        }

        d2File = new File(fnameExtNew);
        if (d2File.exists()) {
            this.reset();
            this.updatePatt2D(d2File);
            this.updatePanels();
            return;
        } else {
            this.tAOut.stat("No file found with fname " + fnameExtNew);
        }

        if (fnameExtNew.isEmpty())
            return;

        //prova pksearchframe
        if (this.pksframe != null) {
            if (this.pksframe.getFileOut() != null) {
                this.pksframe.importOUT(this.pksframe.getFileOut());
            }
        }

    }

    protected void do_btnPrev_actionPerformed(ActionEvent e) {
        final String fnameCurrent = FileUtils.getFNameNoExt(this.patt2D.getImgfile());
        final String fextCurrent = FileUtils.getExtension(this.patt2D.getImgfile());
        log.debug("fnameCurrent (no Ext): " + fnameCurrent);
        log.debug("fextCurrent: " + fextCurrent);
        //agafem ultims 4 digits (index), sumem 1 i el tornem a posar com a fitxer a veure què

        String fnameExtNew = "";
        try {
            log.debug("substring " + fnameCurrent.substring(fnameCurrent.length() - 4, fnameCurrent.length()));
            int imgNum = Integer.parseInt(fnameCurrent.substring(fnameCurrent.length() - 4, fnameCurrent.length()));
            imgNum = imgNum - 1;
            fnameExtNew = fnameCurrent.substring(0, fnameCurrent.length() - 4) + String.format("%04d", imgNum) + "."
                    + fextCurrent;
        } catch (final Exception ex) {
            log.debug("trying to get the file numbering");
            final int indexGuio = fnameCurrent.lastIndexOf("_");
            if (indexGuio > 0) {
                log.debug("index guio=" + indexGuio);
                int imgNum = Integer.parseInt(fnameCurrent.substring(indexGuio + 1, fnameCurrent.length()));
                imgNum = imgNum - 1;
                final int lenformat = fnameCurrent.length() - indexGuio - 1;
                log.debug("lenformat=" + lenformat);
                final String format = "%0" + lenformat + "d";
                log.debug("format=" + format);
                fnameExtNew = fnameCurrent.substring(0, fnameCurrent.length() - lenformat)
                        + String.format(format, imgNum) + "." + fextCurrent;
                log.debug("fnameExtNew=" + fnameExtNew);
            }
        }

        File d2File = new File(fnameExtNew);
        if (d2File.exists()) {
            this.reset();
            this.updatePatt2D(d2File);
            this.updatePanels();
            return;
        } else {
            log.info("No file found with filename: " + fnameExtNew);
        }

        //agafem el nom sense el _000X.edf
        final String basFname = fnameCurrent.substring(0, fnameCurrent.length() - 5);
        log.debug("basFname=" + basFname);
        int indexNoDigit = -1;
        for (int i = 1; i < basFname.length() - 2; i++) {
            final char c = basFname.charAt(basFname.length() - i);
            if (!Character.isDigit(c)) {
                indexNoDigit = i;
                break;
            }
        }
        log.debug("indexNoDigit=" + indexNoDigit);
        if (indexNoDigit > 0) {
            final String sdom = basFname.substring(basFname.length() - (indexNoDigit - 1), basFname.length());
            log.debug("sdom=" + sdom);
            int ndom = -1;
            try {
                ndom = Integer.parseInt(sdom);
            } catch (final Exception ex) {
                log.debug("error parsing domain number");
            }
            if (ndom >= 0) {
                ndom = ndom - 1;
                fnameExtNew = basFname.substring(0, basFname.length() - (indexNoDigit - 1))
                        .concat(Integer.toString(ndom)).concat("_0000.").concat(fextCurrent);
                log.debug("fnameExtNew=" + fnameExtNew);
            }
        }

        d2File = new File(fnameExtNew);
        if (d2File.exists()) {
            this.reset();
            this.updatePatt2D(d2File);
            this.updatePanels();
            return;
        } else {
            log.info("No file found with filename: " + fnameExtNew);
        }

        if (fnameExtNew.isEmpty())
            return;

        //prova pksearchframe
        if (this.pksframe != null) {
            if (this.pksframe.getFileOut() != null) {
                this.pksframe.importOUT(this.pksframe.getFileOut());
            }
        }
    }

    protected void do_btnDbdialog_actionPerformed(ActionEvent arg0) {
        // tanquem calibracio en cas que estigui obert
        if (this.calibration != null)
            this.calibration.dispose();
        if (this.exZones != null)
            this.exZones.dispose();

        if (this.dbDialog == null) {
            this.dbDialog = new Database(this.mainF, this.getPanelImatge());
        }
        this.dbDialog.setVisible(true);
        this.panelImatge.setDBdialog(this.dbDialog);
    }

    protected void do_btnPeakSearchint_actionPerformed(ActionEvent arg0) {
        this.mntmFindPeaks.doClick();
    }

    protected void do_mntmInstrumentalParameters_actionPerformed(ActionEvent e) {
        if (this.patt2D != null) {
            if (this.paramDialog == null) {
                this.paramDialog = new ImageParameters(this.mainF, this.getPanelImatge());
            }
            this.paramDialog.setVisible(true);
        }
    }

    protected void do_mntmLabCalibration_actionPerformed(ActionEvent e) {
        if (this.patt2D != null) {
            // tanquem zones excloses en cas que estigui obert
            if (this.exZones != null)
                this.exZones.dispose();
            if (this.pksframe != null)
                this.pksframe.dispose();
            if (this.calibration == null) {
                this.calibration = new Calibration(this.mainF, this.getPanelImatge());
            }
            this.calibration.setVisible(true);
            this.panelImatge.setCalibration(this.calibration);
        }
    }

    protected void do_mntmExcludedZones_actionPerformed(ActionEvent e) {
        if (this.patt2D != null) {
            // tanquem calibracio en cas que estigui obert
            if (this.calibration != null)
                this.calibration.dispose();
            if (this.pksframe != null)
                this.pksframe.dispose();
            if (this.exZones == null) {
                this.exZones = new ExZones(this.mainF, this.getPanelImatge());
            }
            this.exZones.setVisible(true);
            this.chckbxPaintExz.setSelected(true);
            this.panelImatge.setExZones(this.exZones);
        }
    }

    protected void do_mntmFindPeaks_actionPerformed(ActionEvent arg0) {
        if (this.patt2D != null) {
            if (this.exZones != null)
                this.exZones.dispose();
            if (this.calibration != null)
                this.calibration.dispose();
            if (this.pksframe == null) {
                this.pksframe = new PeakSearch(this.mainF, this.panelImatge, false, null);
            }
            this.pksframe.setVisible(true);
            this.panelImatge.setPKsearch(this.pksframe);
        }

    }

    protected void do_mntmBackgroundSubtraction_actionPerformed(ActionEvent e) {
        if (this.patt2D != null) {
            if (this.d2DsubWin == null) {
                this.d2DsubWin = new BackgroundEstimation(this);
            }
            this.d2DsubWin.setVisible(true);
        } else {
            //obrim el batch
            if (this.d2DsubWinBatch == null) {
                this.d2DsubWinBatch = new BackgroundEstimation_batch(this);
            }
            this.d2DsubWinBatch.setVisible(true);
        }
    }

    protected void do_mntmRadialIntegration_actionPerformed(ActionEvent e) {
        if (this.patt2D != null) {
            if (this.irWin == null) {
                this.irWin = new ConvertTo1DXRD(this.mainF, this.getPanelImatge());
            }
            this.irWin.setVisible(true);
        }
    }

    protected void do_mntmAzimuthalIntegration_actionPerformed(ActionEvent arg0) {
        if (this.patt2D != null) {
            if (this.iazWin == null) {
                this.iazWin = new AzimuthalPlot(this.getMainF(), this.getPanelImatge());
            }
            this.iazWin.setVisible(true);
        }
    }

    protected void do_mntmOpen_actionPerformed(ActionEvent e) {
        this.openImgFile();
    }

    protected void do_mntmSaveImage_actionPerformed(ActionEvent e) {
        this.saveImgFile();
    }

    protected void do_mntmQuit_actionPerformed(ActionEvent arg0) {
        this.do_this_windowClosing(null);
    }

    protected void do_mntmDatabase_actionPerformed(ActionEvent e) {
        this.btnDbdialog.doClick();
    }

    protected void do_mntmExportAsPng_actionPerformed(ActionEvent e) {

        if (this.patt2D == null) {
            //batch convert
            final FileNameExtensionFilter filt[] = ImgFileUtils.getExtensionFilterRead();
            final File[] flist = FileUtils.fchooserMultiple(this.mainF, new File(getWorkdir()), filt, 0, null);
            if (flist == null)
                return;
            D2Dplot_global.setWorkdir(flist[0]);
            this.reset();

            for (final File element : flist) {
                //openImage
                this.updatePatt2D(element);
                //change ext
                final File f = FileUtils.canviExtensio(element, "png");
                log.info(f.toString());
                ImgFileUtils.exportPNG(f, this.panelImatge.getImage(), true);
            }

            return;
        }

        File imFile = FileUtils.fchooserSaveAsk(this.mainF, new File(getWorkdir()), null, "png");

        if (imFile != null) {
            int w = this.panelImatge.getPanelImatge().getSize().width;
            int h = this.panelImatge.getPanelImatge().getSize().height;

            log.debug(String.format("(frame) w=%d h=%d", w, h));

            final Rectangle r = this.panelImatge.calcSubimatgeDinsFrame();
            Rectangle2D.Float rfr = new Rectangle2D.Float(r.x, r.y, r.width, r.height);

            rfr = this.panelImatge.rectangleToFrameCoords(rfr);
            w = (int) rfr.getWidth();
            h = (int) rfr.getHeight();

            log.debug(String.format("(rect) w=%d h=%d", w, h));

            float factor = (float) this.patt2D.getDimX() / (float) w;

            final String s = (String) JOptionPane.showInputDialog(this.mainF,
                    "Current plot size (Width x Heigth) is " + Integer.toString(w) + " x " + Integer.toString(h)
                            + "pixels\n" + "Scale factor to apply (-1 for clean image in real size)=",
                    "Apply scale factor", JOptionPane.PLAIN_MESSAGE, null, null, FileUtils.dfX_2.format(factor));

            if ((s != null) && (s.length() > 0)) {
                try {
                    factor = Float.parseFloat(s);
                } catch (final Exception ex) {
                    log.warning("Error reading factor");
                }
                log.writeNameNumPairs("config", true, "factor", factor);
            }

            if (factor > 0) {
                final double pageWidth = this.panelImatge.getPanelImatge().getSize().width * factor;
                final double pageHeight = this.panelImatge.getPanelImatge().getSize().height * factor;
                final double imageWidth = this.panelImatge.getPanelImatge().getSize().width;
                final double imageHeight = this.panelImatge.getPanelImatge().getSize().height;
                //                    double scaleFactor = ImgFileUtils.getScaleFactorToFit(
                //                            new Dimension((int) Math.round(imageWidth), (int) Math.round(imageHeight)),
                //                            new Dimension((int) Math.round(pageWidth), (int) Math.round(pageHeight)));

                final double scaleFactor = ImgFileUtils.getScaleFactorToFit(
                        new Dimension((int) imageWidth, (int) imageHeight),
                        new Dimension((int) pageWidth, (int) pageHeight));

                final int width = (int) Math.round(pageWidth) + 1;
                final int height = (int) Math.round(pageHeight) + 1;

                log.writeNameNumPairs("config", true,
                        "pageWidth, pageHeight, imageWidth, imageHeight,scaleFactor,width,height", pageWidth,
                        pageHeight, imageWidth, imageHeight, scaleFactor, width, height);

                final int xnew = FastMath.max((int) (rfr.getX() * scaleFactor), 0);
                final int ynew = FastMath.max((int) (rfr.getY() * scaleFactor), 0);
                int wnew = (int) (w * scaleFactor);
                int hnew = (int) (h * scaleFactor);

                final BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
                final Graphics2D g2d = img.createGraphics();
                g2d.scale(scaleFactor, scaleFactor);
                this.panelImatge.getPanelImatge().paintComponent(g2d);
                g2d.dispose();

                log.writeNameNumPairs("config", true, "xnew, ynew, wnew, hnew", xnew, ynew, wnew, hnew);
                //comprovacions que no peti //TODO:s'hauria de trobar el problema real
                if ((wnew) > width)
                    wnew = width;
                if ((hnew) > height)
                    hnew = height;
                log.writeNameNumPairs("config", true, "xnew, ynew, wnew, hnew", xnew, ynew, wnew, hnew);

                imFile = ImgFileUtils.exportPNG(imFile, img.getSubimage(xnew, ynew, wnew, hnew), false); //l'extensio l'ha forçada el filechooser

            } else { //salvem imatge base amb mida ideal
                if (this.panelImatge.getSubimage() == null) {
                    imFile = ImgFileUtils.exportPNG(imFile, this.panelImatge.getImage(), false);
                } else {
                    imFile = ImgFileUtils.exportPNG(imFile, this.panelImatge.getSubimage(), false);
                }

            }

            if (imFile == null) {
                log.warning("Error saving PNG file");
                return;
            }

        } else {
            log.warning("Error saving PNG file");
            return;
        }
        log.info("File PNG saved: " + imFile.toString());
        D2Dplot_global.setWorkdir(imFile);
    }

    protected void do_mntmAbout_actionPerformed(ActionEvent e) {
        if (this.p2dAbout == null) {
            this.p2dAbout = new About(this.mainF);
        }
        this.p2dAbout.setVisible(true);
    }

    protected void do_mntmManual_actionPerformed(ActionEvent e) {
        if (this.p2dAbout == null) {
            this.p2dAbout = new About(this.mainF);
        }
        this.p2dAbout.openManual();
    }

    protected void do_mntmDincoSol_actionPerformed(ActionEvent e) {
        //Preguntem per obrir un fitxer SOL pero de totes formes obrirem el dialeg
        if (this.dincoFrame == null) {
            this.dincoFrame = new IncoPlot(this.mainF, this.getPanelImatge());
        }
        if (!this.dincoFrame.hasSolutionsLoaded()) {
            this.dincoFrame.setSOLMode();
            this.dincoFrame.openSOL();
        }
        this.dincoFrame.setVisible(true);
        this.panelImatge.setDinco(this.dincoFrame);
    }

    protected void do_mntmLoadXdsFile_actionPerformed(ActionEvent e) {
        //Preguntem per obrir un fitxer XDS pero de totes formes obrirem el dialeg
        if (this.dincoFrame == null) {
            this.dincoFrame = new IncoPlot(this.mainF, this.getPanelImatge());
        }
        if (!this.dincoFrame.hasSolutionsLoaded()) {
            this.dincoFrame.setXDSMode();
            this.dincoFrame.openXDS();
        }
        this.dincoFrame.setVisible(true);
        this.panelImatge.setDinco(this.dincoFrame);
    }

    //    protected void do_mntmClearAll_actionPerformed(ActionEvent e) {
    //        this.getPatt2D().clearSolutions();
    //        this.dincoFrame.dispose();
    //    }
    protected void do_mntmHpTools_actionPerformed(ActionEvent e) {
        if (this.hpframe == null) {
            this.hpframe = new HPtools(this.mainF, this.getPanelImatge());
        }
        this.hpframe.setVisible(true);
    }

    protected void do_mntmSubtractImages_actionPerformed(ActionEvent e) {

        final SubtractImages sdiag = new SubtractImages(this.mainF);
        sdiag.setVisible(true);

        if (sdiag.isCancel())
            return;
        if (sdiag.getImage() == null)
            return;
        if (sdiag.getImage() == null)
            return;

        final Pattern2D img = ImgFileUtils.readPatternFile(sdiag.getImage(), true);
        final float fac = sdiag.getFactor();
        final Pattern2D img2 = ImgFileUtils.readPatternFile(sdiag.getBkgImage(), true);

        if (img == null)
            return;
        if (img2 == null)
            return;

        final Pattern2D dataSub = ImgOps.subtractBKG_v2(img, img2, fac)[0];
        this.updatePatt2D(dataSub, false, true);
    }

    protected void do_mntmSumImages_actionPerformed(ActionEvent e) {
        final FileNameExtensionFilter filt[] = ImgFileUtils.getExtensionFilterRead();
        final File[] flist = FileUtils.fchooserMultiple(this.mainF, new File(getWorkdir()), filt, 0,
                "Select 2DXRD data files to sum");
        if (flist == null)
            return;
        D2Dplot_global.setWorkdir(flist[0]);

        this.pm = new ProgressMonitor(this.mainF, "Summing Images...", "", 0, 100);
        this.pm.setProgress(0);
        this.sumwk = new ImgOps.SumImagesFileWorker(flist);
        this.sumwk.addPropertyChangeListener(new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                log.debug(evt.getPropertyName());
                if ("progress" == evt.getPropertyName()) {
                    final int progress = (Integer) evt.getNewValue();
                    MainFrame.this.pm.setProgress(progress);
                    MainFrame.this.pm.setNote(String.format("%d%%\n", progress));
                    if (MainFrame.this.pm.isCanceled() || MainFrame.this.sumwk.isDone()) {
                        Toolkit.getDefaultToolkit().beep();
                        if (MainFrame.this.pm.isCanceled()) {
                            MainFrame.this.sumwk.cancel(true);
                            log.info("Sum canceled");
                        } else {
                            log.info("Sum finished!!");
                        }
                        MainFrame.this.pm.close();
                    }
                }
                if (MainFrame.this.sumwk.isDone()) {
                    final Pattern2D suma = MainFrame.this.sumwk.getpattSum();
                    if (suma == null) {
                        log.warning("Error summing files");
                        return;
                    } else {
                        suma.recalcMaxMinI();
                        suma.calcMeanI();
                        suma.recalcExcludedPixels();
                        MainFrame.this.updatePatt2D(suma, true, true);
                    }
                }
            }
        });
        this.sumwk.execute();
    }

    protected void do_mntmBatchConvert_actionPerformed(ActionEvent e) {
        final FileNameExtensionFilter filt[] = ImgFileUtils.getExtensionFilterRead();
        final File[] flist = FileUtils.fchooserMultiple(this.mainF, new File(getWorkdir()), filt, 0,
                "Select 2DXRD data files to convert");
        if (flist == null)
            return;
        D2Dplot_global.setWorkdir(flist[0]);

        this.pm = new ProgressMonitor(this.mainF, "Converting Images...", "", 0, 100);
        this.pm.setProgress(0);
        this.convwk = new ImgFileUtils.BatchConvertFileWorker(flist);
        this.convwk.addPropertyChangeListener(new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                log.debug(evt.getPropertyName());
                if ("progress" == evt.getPropertyName()) {
                    final int progress = (Integer) evt.getNewValue();
                    MainFrame.this.pm.setProgress(progress);
                    MainFrame.this.pm.setNote(String.format("%d%%\n", progress));
                    if (MainFrame.this.pm.isCanceled() || MainFrame.this.convwk.isDone()) {
                        Toolkit.getDefaultToolkit().beep();
                        if (MainFrame.this.pm.isCanceled()) {
                            MainFrame.this.convwk.cancel(true);
                            log.info("Batch convert stopped by user");
                        } else {
                            log.info("Batch convert finished!!");
                        }
                        MainFrame.this.pm.close();
                    }
                }
            }
        });
        this.convwk.execute();
    }

    protected void do_mntmFastopen_actionPerformed(ActionEvent e) {
        this.fastView = new FastViewer(this.mainF);
        this.fastView.setVisible(true);
        this.fastView.showOpenImgsDialog();
    }

    protected void do_mntmScDataTo_actionPerformed(ActionEvent e) {
        this.scToInco = new SC_to_INCO(this.mainF);
        this.scToInco.setVisible(true);
    }

    //    protected void do_mntmSavetif_actionPerformed(ActionEvent e) {
    //        File out = FileUtils.fchooserSaveAsk(this, D2Dplot_global.getWorkdirFile(), null);
    //        ImgFileUtils.writeTIF(out, this.getPanelImatge().getImage());
    //    }
    protected void do_mntmTtssoftware_actionPerformed(ActionEvent e) {
        this.tts = new TTS(this);
        this.tts.setVisible(true);
    }

    protected void do_mntmReset_actionPerformed(ActionEvent e) {
        this.restart();
    }

    protected void do_mntmCheckForUpdates_actionPerformed(ActionEvent e) {
        this.checkNewVersion(true);
    }

    protected void do_chckbxShowRings_itemStateChanged(ItemEvent arg0) {
        if (combo_LATdata.getItemCount() > 0) {
            log.debug("do_chckbxShowRings_itemStateChanged CALLED");
            this.panelImatge.setShowQuickListCompoundRings(this.chckbxShowRings.isSelected(),
                    this.getQuickListCompound());
        }

    }

    protected void do_chckbxIndex_itemStateChanged(ItemEvent e) {
        this.panelImatge.pintaImatge();
    }
    
    protected void do_chckbxColor_itemStateChanged(ItemEvent arg0) {
        this.panelImatge.setColor(this.chckbxColor.isSelected());
        log.debug("do_chckbxColor_itemStateChanged called");
        this.panelImatge.pintaImatge();
    }

    protected void do_chckbxInvertY_itemStateChanged(ItemEvent e) {
        this.panelImatge.setInvertY(this.chckbxInvertY.isSelected());
        log.debug("do_chckbxInvertY_itemStateChanged called");
        this.panelImatge.pintaImatge();
    }

    protected void do_chckbxPaintExz_itemStateChanged(ItemEvent arg0) {
        this.panelImatge.setPaintExZ(this.chckbxPaintExz.isSelected());
        log.debug("do_chckbxPaintExz_itemStateChanged called");
        this.panelImatge.pintaImatge();
    }

    protected void do_combo_showRings_itemStateChanged(ItemEvent arg0) {
        if (arg0.getStateChange() == ItemEvent.SELECTED) {
            this.panelImatge.setShowQuickListCompoundRings(this.chckbxShowRings.isSelected(),
                    this.getQuickListCompound());
        }
    }

    protected void do_lblOpened_mouseReleased(MouseEvent e) {
        if (this.lblOpened.getText().startsWith("(no") || this.lblOpened.getText().isEmpty())
            return;

        final File f = new File(this.lblOpened.getText());
        //        String fpath = f.getParent();
        final String fpath = new File(f.getAbsolutePath()).getParent();
        boolean opened = false;
        try {
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().open(new File(fpath));
                opened = true;
            } else {
                if (FileUtils.getOS().equalsIgnoreCase("win")) {
                    new ProcessBuilder("explorer.exe", "/select,", this.lblOpened.getText()).start();
                    opened = true;
                }
                if (FileUtils.getOS().equalsIgnoreCase("lin")) {
                    //kde dolphin
                    try {
                        new ProcessBuilder("dolphin", this.lblOpened.getText()).start();
                        opened = true;
                    } catch (final Exception ex) {
                        if (D2Dplot_global.isDebug())
                            ex.printStackTrace();
                    }
                    //gnome nautilus
                    try {
                        new ProcessBuilder("nautilus", this.lblOpened.getText()).start();
                        opened = true;
                    } catch (final Exception ex) {
                        if (D2Dplot_global.isDebug())
                            ex.printStackTrace();
                    }
                }
            }
        } catch (final Exception ex) {
            if (D2Dplot_global.isDebug())
                ex.printStackTrace();
        }
        if (!opened)
            this.tAOut.addtxt(true, true, "Unable to open folder");
    }

    protected void do_lblOpened_mouseEntered(MouseEvent e) {
        this.mainF.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        this.lblOpened.setForeground(Color.blue);
    }

    protected void do_lblOpened_mouseExited(MouseEvent e) {
        this.mainF.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        this.lblOpened.setForeground(Color.black);
    }
    //    private JMenu mnDebug;
    //    private JMenuItem mntmSavetif;

    public static String getBinDir() {
        return D2Dplot_global.binDir;
    }

    public static String getSeparator() {
        return D2Dplot_global.separator;
    }

    public static String getWorkdir() {
        return D2Dplot_global.getWorkdir();
    }

    public static void setWorkdir(String wdir) {
        D2Dplot_global.setWorkdir(wdir);
    }

    public static void updateQuickList() {
        final Iterator<PDCompound> itrC = PDDatabase.getQuickListIterator();
        combo_LATdata.removeAllItems();
        while (itrC.hasNext()) {
            combo_LATdata.addItem(itrC.next());
        }
    }

    /**
     * @return the def_Width
     */
    public static int getDef_Width() {
        return def_Width;
    }

    /**
     * @param def_Width the def_Width to set
     */
    public static void setDef_Width(int def_Width) {
        MainFrame.def_Width = def_Width;
    }

    /**
     * @return the def_Height
     */
    public static int getDef_Height() {
        return def_Height;
    }

    /**
     * @param def_Height the def_Height to set
     */
    public static void setDef_Height(int def_Height) {
        MainFrame.def_Height = def_Height;
    }

    /**
     * @return the mainF
     */
    public JFrame getMainF() {
        return this.mainF;
    }

    /**
     * @param mainF the mainF to set
     */
    public void setMainF(JFrame mainF) {
        this.mainF = mainF;
    }

    public File getOpenedFile() {
        return this.openedFile;
    }

    public ImagePanel getPanelImatge() {
        return this.panelImatge;
    }

    public Pattern2D getPatt2D() {
        return this.patt2D;
    }

    public void setOpenedFile(File openedFile) {
        this.openedFile = openedFile;
    }

    //    public LogJTextArea gettAOut() {
    //        return tAOut;
    //    }
    public boolean isSelectPoints() {
        return this.chckbxIndex.isSelected();
    }

    public IncoPlot getDincoFrame() {
        return this.dincoFrame;
    }

    public void setDincoFrame(IncoPlot dincoFrame) {
        this.dincoFrame = dincoFrame;
    }

    public Calibration getCalibration() {
        return this.calibration;
    }

    public void setCalibration(Calibration calibration) {
        this.calibration = calibration;
    }

    public ConvertTo1DXRD getIrWin() {
        return this.irWin;
    }
    
    public AzimuthalPlot getAzWin() {
        return this.iazWin;
    }

    public void setIrWin(ConvertTo1DXRD irWin) {
        this.irWin = irWin;
    }
    
    public void setAzWin(AzimuthalPlot azWin) {
        this.iazWin = azWin;
    }

    public PeakSearch getpksearchframe() {
        return this.pksframe;
    }


    protected void do_mntmAzimtheta_actionPerformed(ActionEvent e) {
        final Azimuthal2Th sid = new Azimuthal2Th(this.getMainF(), this.patt2D,
                "2D image");
        sid.setVisible(true);
    }
}
