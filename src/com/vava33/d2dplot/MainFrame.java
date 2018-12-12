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
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Date;
import java.util.Iterator;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.plaf.metal.DefaultMetalTheme;
import javax.swing.plaf.metal.MetalLookAndFeel;
import javax.swing.plaf.metal.OceanTheme;

import org.apache.commons.math3.util.FastMath;

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

import java.awt.event.KeyEvent;
import java.awt.event.InputEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.border.LineBorder;
import javax.swing.*;          


public class MainFrame {

    private static final String className = "d2Dplot_main";
    
    private static int def_Width=1100;
    private static int def_Height=768;
    
    private static VavaLogger log;
    private static MainFrame frame;
    
    private Pattern2D patt2D;
    private boolean fileOpened = false;
    private File openedFile;
    
    private JFrame mainF;
    private ImagePanel panelImatge;    
    private BackgroundEstimation d2DsubWin;
    private BackgroundEstimation_batch d2DsubWinBatch;
    private Calibration calibration;
    private ExZones exZones;
    private About p2dAbout;
    private PeakList pkListWin;
    private LogJTextArea tAOut;
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
    private static JComboBox<PDCompound> combo_LATdata;
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
//    private JMenuItem mntmClearAll;
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
    private JMenuItem mntmFastopen;
    private JMenuItem mntmScDataTo;
    private JMenuItem mntmAzimuthalIntegration;
    private JMenuItem mntmTtssoftware;
    private JMenuItem mntmReset;
    private JMenuItem mntmCheckForUpdates;
    Border etchedborder = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);
    
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
        SystemInfo si = new SystemInfo();
        log.info(" ======================== d2Dplot session on "+FileUtils.fDiaHora.format(new Date())+" ========================");
        log.debug(si.MemInfo());
      
        
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            log.debug("L&F="+UIManager.getLookAndFeel().toString());
            if(UIManager.getLookAndFeel().toString().contains("metal")){
//                UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsClassicLookAndFeel");
                UIManager.setLookAndFeel("com.sun.java.swing.plaf.gtk.GTKLookAndFeel");    
//                log.debug("L&F="+UIManager.getLookAndFeel().toString());
//                UIManager.setLookAndFeel("com.sun.java.swing.plaf.motif.MotifLookAndFeel");
//                log.debug("L&F="+UIManager.getLookAndFeel().toString());
            }
            // UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName()); //java metal
            
        } catch (Throwable e) {
            if (D2Dplot_global.isDebug())e.printStackTrace();
            log.warning("Error initializing System look and feel");
//            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        }
        log.debug("L&F="+UIManager.getLookAndFeel().toString());

        final String THEME = "Ocean";
         
        if (UIManager.getLookAndFeel().toString().contains("metal")) {
            if (THEME.equals("DefaultMetal"))
               MetalLookAndFeel.setCurrentTheme(new DefaultMetalTheme());
            else if (THEME.equals("Ocean"))
               MetalLookAndFeel.setCurrentTheme(new OceanTheme());
               
            try {
                UIManager.setLookAndFeel(new MetalLookAndFeel());
            } catch (UnsupportedLookAndFeelException e) {
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
                    if (ArgumentLauncher.isLaunchGraphics()){
                        frame.showMainFrame();
                    }else{
                        log.info("Exiting...");
                        frame.mainF.dispose();
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
    	mainF.setLocationRelativeTo(null);
    	mainF.setVisible(true);    
    }
    
    /**
     * Create the frame.
     */
    public MainFrame() {
    	mainF = new JFrame();
    	mainF.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                do_this_windowClosing(e);
            }
        });
    	mainF.setIconImage(Toolkit.getDefaultToolkit().getImage(MainFrame.class.getResource("/img/Icona.png")));
    	mainF.setTitle("d2Dplot");
    	mainF.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    	mainF.setBounds(100, 100, 1129, 945);
        
        menuBar = new JMenuBar();
        mainF.setJMenuBar(menuBar);
        
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
        
        mntmFastopen = new JMenuItem("Fast Viewer");
        mntmFastopen.setMnemonic('f');
        mntmFastopen.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                do_mntmFastopen_actionPerformed(e);
            }
        });
        mnFile.add(mntmFastopen);
        
        separator = new JSeparator();
        mnFile.add(separator);
        
        mntmQuit = new JMenuItem("Quit");
        mntmQuit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                do_mntmQuit_actionPerformed(arg0);
            }
        });
        
        mntmReset = new JMenuItem("Reset");
        mntmReset.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                do_mntmReset_actionPerformed(e);
            }
        });
        mnFile.add(mntmReset);
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
        
        mntmLabCalibration = new JMenuItem("Inst. Param. Calibration");
        mntmLabCalibration.setMnemonic('c');
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
        
        mntmRadialIntegration = new JMenuItem("Conversion to 1D PXRD");
        mntmRadialIntegration.setToolTipText("Debye rings integration (cakes)");
        mntmRadialIntegration.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, InputEvent.CTRL_MASK));
        mntmRadialIntegration.setMnemonic('1');
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
        
        mntmAzimuthalIntegration = new JMenuItem("Azimuthal (circular) plot");
        mntmAzimuthalIntegration.setMnemonic('a');
        mntmAzimuthalIntegration.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                do_mntmAzimuthalIntegration_actionPerformed(arg0);
            }
        });
        mnImageOps.add(mntmAzimuthalIntegration);
        mntmHpTools.setMnemonic('h');
        mnImageOps.add(mntmHpTools);
        
        mnGrainAnalysis = new JMenu("Grain Analysis");
        mnGrainAnalysis.setMnemonic('g');
        menuBar.add(mnGrainAnalysis);
        
        mntmDincoSol = new JMenuItem("Load tts-INCO SOL/PCS files");
        mntmDincoSol.setMnemonic('l');
        mntmDincoSol.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                do_mntmDincoSol_actionPerformed(e);
            }
        });
        
        mntmFindPeaks = new JMenuItem("Find/Integrate Peaks");
        mntmFindPeaks.setMnemonic('f');
        mntmFindPeaks.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                do_mntmFindPeaks_actionPerformed(arg0);
            }
        });
                mnGrainAnalysis.add(mntmFindPeaks);
        
        mntmTtssoftware = new JMenuItem("Run tts_Software");
        mntmTtssoftware.setMnemonic('t');
        mntmTtssoftware.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                do_mntmTtssoftware_actionPerformed(e);
            }
        });
        mnGrainAnalysis.add(mntmTtssoftware);
        mnGrainAnalysis.add(mntmDincoSol);
        
        mntmLoadXdsFile = new JMenuItem("Load XDS file");
        mntmLoadXdsFile.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                do_mntmLoadXdsFile_actionPerformed(e);
            }
        });
        mnGrainAnalysis.add(mntmLoadXdsFile);
        
//        mntmClearAll = new JMenuItem("Clear all");
//        mntmClearAll.addActionListener(new ActionListener() {
//            public void actionPerformed(ActionEvent e) {
//                do_mntmClearAll_actionPerformed(e);
//            }
//        });
        
        mntmScDataTo = new JMenuItem("SC data to tts_INCO");
        mntmScDataTo.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                do_mntmScDataTo_actionPerformed(e);
            }
        });
        mnGrainAnalysis.add(mntmScDataTo);
//        mnGrainAnalysis.add(mntmClearAll);
        
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
        
        mntmCheckForUpdates = new JMenuItem("Check for updates");
        mntmCheckForUpdates.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		do_mntmCheckForUpdates_actionPerformed(e);
        	}
        });
        mnHelp.add(mntmCheckForUpdates);
        
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
        mntmAbout.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                do_mntmAbout_actionPerformed(e);
            }
        });
        this.contentPane = new JPanel();
        this.contentPane.setBorder(null);
        mainF.setContentPane(this.contentPane);
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
        panel_stat.setLayout(new MigLayout("fill", "[1166px]", "[100px,grow]"));
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
        panel_all.setLayout(new MigLayout("fill", "[1200px,grow]", "[][grow]"));
        
        panel_2 = new JPanel();
        panel_all.add(panel_2, "cell 0 0,grow");
                panel_2.setLayout(new MigLayout("fill,insets 0", "[][grow][][]", "[grow]"));
        
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
        this.splitPane_1.setContinuousLayout(true);
        this.splitPane_1.setResizeWeight(1.0);
        this.panel_all.add(this.splitPane_1, "cell 0 1,grow");

        this.panelImatge = new ImagePanel();
        
        JScrollPane jspimage = new JScrollPane();
        jspimage.setViewportBorder(null);
        jspimage.setBorder(null);
        jspimage.setViewportView(this.panelImatge.getIpanelMain());
        this.splitPane_1.setLeftComponent(jspimage);
        this.panelImatge.getIpanelMain().setBorder(null);
        this.panelImatge.getIpanelMain().setBorder(etchedborder);
        this.panel_controls = new JPanel();
        
        JScrollPane jscontrols = new JScrollPane();
        jscontrols.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        jscontrols.setViewportBorder(null);
        jscontrols.setBorder(null);
        jscontrols.setViewportView(this.panel_controls);
        this.splitPane_1.setRightComponent(jscontrols);
        
        panel_controls.setLayout(new MigLayout("fill", "[130px,grow]", "[5px:20px:35px,grow][::160px,grow][::85px,grow][::140px,grow][::130px,grow][grow]"));
        
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
        this.btnResetView = new JButton("Fit");
        this.btnResetView.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                do_btnResetView_actionPerformed(arg0);
            }
        });
        panel_opcions.setLayout(new MigLayout("fill, insets 2", "[60px][60]", "[5px:20px:35px][5px:20px:35px][5px:20px:35px][5px:20px:35px]"));
        this.btnResetView.setMargin(new Insets(2, 2, 2, 2));
        this.panel_opcions.add(this.btnResetView, "cell 0 0,growx,aligny center");
        this.btnMidaReal = new JButton("100%");
        this.btnMidaReal.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                do_btnMidaReal_actionPerformed(e);
            }
        });
        this.btnMidaReal.setMargin(new Insets(2, 2, 2, 2));
        this.panel_opcions.add(this.btnMidaReal, "cell 1 0,growx,aligny center");
        this.btn05x = new JButton("0.5x");
        panel_opcions.add(btn05x, "cell 0 1,growx,aligny center");
        this.btn05x.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                do_btn05x_actionPerformed(arg0);
            }
        });
        this.btn05x.setMargin(new Insets(1, 2, 1, 2));
        this.btn2x = new JButton("2x");
        panel_opcions.add(btn2x, "cell 1 1,growx,aligny center");
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
        panel_opcions.add(chckbxColor, "cell 0 2");
        
        chckbxPaintExz = new JCheckBox("Paint ExZ");
        chckbxPaintExz.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent arg0) {
                do_chckbxPaintExz_itemStateChanged(arg0);
            }
        });
        
        chckbxInvertY = new JCheckBox("flip Y");
        chckbxInvertY.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                do_chckbxInvertY_itemStateChanged(e);
            }
        });
        panel_opcions.add(chckbxInvertY, "cell 1 2");
        panel_opcions.add(chckbxPaintExz, "cell 0 3 2 1");
        
        panel = new JPanel();
        panel.setBorder(new TitledBorder(null, "Points", TitledBorder.LEADING, TitledBorder.TOP, null, null));
        panel_controls.add(panel, "cell 0 2,grow");
        panel.setLayout(new MigLayout("fill, insets 2", "[]", "[5px:20px][5px:20px,grow]"));
        this.chckbxIndex = new JCheckBox("Sel. Points");
        panel.add(chckbxIndex, "cell 0 0,growx,aligny center");
        chckbxIndex.setSelected(true);
        this.btnSaveDicvol = new JButton("Points List");
        panel.add(btnSaveDicvol, "cell 0 1,growx,aligny center");
        this.btnSaveDicvol.setMinimumSize(new Dimension(100, 28));
        this.btnSaveDicvol.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                do_btnSaveDicvol_actionPerformed(arg0);
            }
        });
        this.btnSaveDicvol.setMargin(new Insets(2, 2, 2, 2));
        
        panel_3 = new JPanel();
        panel_3.setBorder(new TitledBorder(new LineBorder(new Color(184, 207, 229)), "Phase ID", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(51, 51, 51)));
        panel_controls.add(panel_3, "cell 0 3,grow");
        panel_3.setLayout(new MigLayout("fill, insets 2, hidemode 3", "[120px]", "[5px:20px][][5px:20px][5px:20px][]"));
        
        btnDbdialog = new JButton("Database");
        btnDbdialog.setMargin(new Insets(2, 2, 2, 2));
        panel_3.add(btnDbdialog, "cell 0 0,growx,aligny center");
        
        separator_1 = new JSeparator();
        panel_3.add(separator_1, "cell 0 1,growx,aligny center");
        
        chckbxShowRings = new JCheckBox("Quicklist");
        panel_3.add(chckbxShowRings, "cell 0 2,growx,aligny center");
        
        combo_LATdata = new JComboBox<PDCompound>();
        combo_LATdata.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent arg0) {
                do_combo_showRings_itemStateChanged(arg0);
            }
        });
        panel_3.add(combo_LATdata, "cell 0 3,growx,aligny center");
        combo_LATdata.setModel(new DefaultComboBoxModel<PDCompound>(new PDCompound[] {}));
        
        btnAddLat = new JButton("Add to List");
        btnAddLat.setVisible(false);
        btnAddLat.setEnabled(false);
        panel_3.add(btnAddLat, "cell 0 4,growx,aligny center");
        
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
        panel_1 = new JPanel();
        panel_1.setBorder(new TitledBorder(null, "Shortcuts", TitledBorder.LEADING, TitledBorder.TOP, null, null));
        panel_controls.add(panel_1, "cell 0 4,grow");
        panel_1.setLayout(new MigLayout("fill, insets 2", "[grow]", "[5px:20px][5px:20px][5px:20px]"));

        btnPeakSearchint = new JButton("Peak search");
        btnPeakSearchint.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                do_btnPeakSearchint_actionPerformed(arg0);
            }
        });
        panel_1.add(btnPeakSearchint, "cell 0 1,growx,aligny center");


        btnTtsdincoSol = new JButton("tts-INCO SOL");
        btnTtsdincoSol.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                do_btnTtsdincoSol_actionPerformed(e);
            }
        });
        panel_1.add(btnTtsdincoSol, "flowy,cell 0 2,growx,aligny center");

        btnRadIntegr = new JButton("1D PXRD");
        btnRadIntegr.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                do_btnRadIntegr_actionPerformed(e);
            }
        });
        panel_1.add(btnRadIntegr, "cell 0 0,growx,aligny center");
    }

    private void inicialitza() {
        
    	if (D2Dplot_global.loggingTA)VavaLogger.setTArea(tAOut);
//    	D2Dplot_global.setMainTA(this.gettAOut());
    	
        //HO FEM CABRE (170322)
    	mainF.setSize(MainFrame.getDef_Width(), MainFrame.getDef_Height()); //ho centra el metode main
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        while(mainF.getWidth()>screenSize.width){
        	mainF.setSize(mainF.getWidth()-100, mainF.getHeight());
        }
        while(mainF.getHeight()>screenSize.height){
        	mainF.setSize(mainF.getWidth(), mainF.getHeight()-100);
        }
        
        log.info(D2Dplot_global.welcomeMSG);

        if(D2Dplot_global.isConfigFileReaded()==null){
            log.info(String.format("No config file found on: %s, it will be created on exit!",D2Dplot_global.configFilePath));
        }else{
            if(D2Dplot_global.isConfigFileReaded()==true){
                log.info(String.format("Config file readed: %s",D2Dplot_global.configFilePath));    
            }else{
                log.info(String.format("Error reading config file: %s",D2Dplot_global.configFilePath));
            }
        }
        D2Dplot_global.initPars();
        D2Dplot_global.init_ApplyColorsToIPanel(this.getPanelImatge());
        D2Dplot_global.checkDBs();
        boolean ok = PDDatabase.populateQuickList(false);
        if (ok){
            log.info(String.format("QuickList DB file: %s",PDDatabase.getLocalQL()));
        }
        combo_LATdata.setPrototypeDisplayValue(new PDCompound("Silicon")); //mida minima
        updateQuickList();
        
        //MIDES BUTONS
        btnInstParameters.setPreferredSize(new Dimension(110,30));
        btnInstParameters.setMinimumSize(new Dimension(110,15));
        btnResetView.setPreferredSize(new Dimension(50,30));
        btnResetView.setMinimumSize(new Dimension(50,15));
        btnMidaReal.setPreferredSize(new Dimension(50,30));
        btnMidaReal.setMinimumSize(new Dimension(50,15));
        btn05x.setPreferredSize(new Dimension(50,30));
        btn05x.setMinimumSize(new Dimension(50,15));
        btn2x.setPreferredSize(new Dimension(50,30));
        btn2x.setMinimumSize(new Dimension(50,15));
        btnSaveDicvol.setPreferredSize(new Dimension(110,30));
        btnSaveDicvol.setMinimumSize(new Dimension(110,15));
        btnDbdialog.setPreferredSize(new Dimension(110,30));
        btnDbdialog.setMinimumSize(new Dimension(110,15));
        btnPeakSearchint.setPreferredSize(new Dimension(110,30));
        btnPeakSearchint.setMinimumSize(new Dimension(110,15));
        btnTtsdincoSol.setPreferredSize(new Dimension(110,30));
        btnTtsdincoSol.setMinimumSize(new Dimension(110,15));
        btnRadIntegr.setPreferredSize(new Dimension(110,30));
        btnRadIntegr.setMinimumSize(new Dimension(110,15));
        
//        mainF.pack();
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
        log.info(D2Dplot_global.welcomeMSG);
    }
    
    private void closePanels(){
        if (this.paramDialog != null) this.paramDialog.dispose(); //no cal fer res addicional
        if (this.d2DsubWin != null) this.d2DsubWin.dispose();//ja te stop process
        if (this.d2DsubWinBatch != null) this.d2DsubWinBatch.dispose(); //ja te stop process
        if (this.calibration != null) this.calibration.dispose();
        if (this.exZones != null) this.exZones.dispose();
        if (this.dincoFrame != null) this.dincoFrame.dispose();
        if (this.pksframe != null) this.pksframe.dispose();
        if (this.pkListWin != null) this.pkListWin.dispose();
        if (this.irWin != null) this.irWin.dispose();
        if (this.p2dAbout != null) this.p2dAbout.dispose();
        if (this.dbDialog != null) this.dbDialog.dispose();
        if (this.hpframe != null) this.hpframe.dispose();
        if (this.iazWin != null) this.iazWin.dispose();
        if (this.tts != null) this.tts.dispose();
        if (this.fastView != null) this.fastView.dispose();
        if (this.scToInco != null) this.scToInco.dispose();
        //TODO ADDD NEW THINGS IF NECESSARY
    }
    
    private void updatePanels(){
        if (this.paramDialog != null) { //actualitza els camps
            this.updateIparameters();
        }
        if (this.d2DsubWin != null) { //cal deixar-lo pel tema de patt before and after
            this.d2DsubWin.userInit();
        }
        if (this.calibration != null) { //el deixo perque inicia algunes coses extres
            this.calibration.inicia();;
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
        log.debug("workdir="+getWorkdir());
        patt2D = ImgFileUtils.readPatternFile(d2File,true);
        
        if (patt2D != null) {
            panelImatge.setImagePatt2D(patt2D);
            panelImatge.setMainFrame(this);
            lblOpened.setText(d2File.toString());
            log.info("File opened: " + d2File.getName() + ", " + patt2D.getPixCount() + " pixels ("
                    + (int) patt2D.getMillis() + " ms)\n"+patt2D.getInfo());
//            tAOut.stat("File opened: " + d2File.getName() + ", " + patt2D.getPixCount() + " pixels ("
//                    + (int) patt2D.getMillis() + " ms)");
//            tAOut.ln(patt2D.getInfo());
            fileOpened = true;
            openedFile = d2File;
            this.updateIparameters();
        } else {
            log.info("Error reading 2D file");
            log.info("No file opened");
            fileOpened = false;
            openedFile = null;
        }
    }
    
    public void updatePatt2D(Pattern2D p2D, boolean verbose, boolean refreshPanels) {
      this.patt2D = p2D;
      if (patt2D != null) {
          panelImatge.setImagePatt2D(patt2D);
          panelImatge.setMainFrame(this);
          String fname;
          if(patt2D.getImgfile()!=null){
              fname = patt2D.getImgfile().getAbsolutePath(); //tret el getname
              if(verbose) {
                  log.info("File opened: " + fname + ", " + patt2D.getPixCount() + " pixels ("
                          + (int) patt2D.getMillis() + " ms)\n"+patt2D.getInfo());
              }else {
            	  log.info("File opened: " + fname + ", " + patt2D.getPixCount() + " pixels ("
                          + (int) patt2D.getMillis() + " ms)");  
              }
          }else{
              fname = "Data image not saved to a file";
              log.info("File opened: Data image not saved to a file");
          }
          lblOpened.setText(fname);
          fileOpened = true;
          openedFile = patt2D.getImgfile();
          this.updateIparameters();
          if(refreshPanels)this.updatePanels();
      } else {
          log.info("Error reading 2D file");
          log.info("No file opened");
          fileOpened = false;
          openedFile = null;
      }
    }
    
    public void updateFromTTS(File d2File) {
        if (d2File.exists()){
            this.reset();
            this.updatePatt2D(d2File);
            this.updatePanels();
            return;
        }else{
            log.info("No file found with filename: "+d2File.getAbsolutePath());
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
    
    public void setViewExZ(boolean state){
        this.chckbxPaintExz.setSelected(state);
    }

    private void openImgFile(){
        FileNameExtensionFilter filt[] = ImgFileUtils.getExtensionFilterRead();
        File d2File = FileUtils.fchooserOpen(mainF,new File(getWorkdir()), filt, 0);
        if (d2File == null){
            if (!fileOpened){
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
    
    private void saveImgFile(){
        if (this.getPatt2D()!=null){
            FileNameExtensionFilter filt[] = ImgFileUtils.getExtensionFilterWrite();
            File f = FileUtils.fchooserSaveAsk(mainF,new File(getWorkdir()), filt, null);
            if (f!=null){
                D2Dplot_global.setWorkdir(f);
                File outf = ImgFileUtils.writePatternFile(f,this.getPatt2D(),true); //true, malgrat en principi el fchooserSaveAsk hauria de detectar l'extensio
                if (outf!=null){
                    int n = JOptionPane.showConfirmDialog(mainF, "Load the new saved file?", "Refresh file", JOptionPane.YES_NO_OPTION);
                    if (n == JOptionPane.YES_OPTION) {
                        this.reset();
                        this.updatePatt2D(outf);
                        this.updatePanels();
                    }
                }else{
                	log.warning("Error saving file");
                }
            }
        }else{
        	log.info("No image to save!");
        }
    }
    
    private void stopBatchConvert() {
    	if (convwk!=null) {
    		convwk.cancel(true);
    	}
    }
    
    private void stopSumPatterns() {
    	if (sumwk!=null) {
    		sumwk.cancel(true);
    	}
    }
    
    protected void do_this_windowClosing(WindowEvent e) {
      //FIRST SAVE OPTIONS
      log.debug("WINDOW CLOSING CALLED");
      D2Dplot_global.writeParFile();
    
      //SECOND SAVE QL FILE IF MODIFIED
      if(PDDatabase.isQLmodified()){
          //prompt and save QL file if necessary
          Object[] options = {"Yes","No"};
          int n = JOptionPane.showOptionDialog(mainF,
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
      mainF.dispose();
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
    protected void do_btnInstParameters_actionPerformed(ActionEvent arg0) {
        mntmInstrumentalParameters.doClick();
    }
    protected void do_btnTtsdincoSol_actionPerformed(ActionEvent e) {
        mntmDincoSol.doClick();
    }
    protected void do_btnRadIntegr_actionPerformed(ActionEvent e) {
            mntmRadialIntegration.doClick();
        }
    protected void do_btnMidaReal_actionPerformed(ActionEvent e) {
        panelImatge.setScalefit(1.0f);
    }
    protected void do_btnOpen_actionPerformed(ActionEvent arg0) {
        this.openImgFile();
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
            pkListWin = new PeakList(mainF,panelImatge);
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
            
        File d2File = new File(fnameExtNew);
        if (d2File.exists()){
            this.reset();
            this.updatePatt2D(d2File);
            this.updatePanels();
            return;
        }else{
        	log.info("No file found with filename: "+fnameExtNew);
        }
    
        //SECOND TEST buscar dXXX_0000.edf (realment podriem mirar la part esquerra del guio per ser mes generals...)
        
        //agafem el nom sense el _000X.edf
        String basFname = fnameCurrent.substring(0, fnameCurrent.length()-5);
        log.debug("basFname="+basFname);
        int indexNoDigit=-1;
        for (int i=1;i<basFname.length()-2;i++){
            char c = basFname.charAt(basFname.length()-i);
            if (!Character.isDigit(c)) {
                indexNoDigit=i;
                break;
            }
        }
        log.debug("indexNoDigit="+indexNoDigit);
        if (indexNoDigit>0){
            String sdom = basFname.substring(basFname.length()-(indexNoDigit-1), basFname.length());
            log.debug("sdom="+sdom);
            int ndom = -1;
            try{
                ndom = Integer.parseInt(sdom);
            }catch(Exception ex){
                log.debug("error parsing domain number");
            }
            if (ndom>=0){
                ndom = ndom + 1;
                fnameExtNew = basFname.substring(0,basFname.length()-(indexNoDigit-1)).concat(Integer.toString(ndom)).concat("_0000.").concat(fextCurrent);
                log.debug("fnameExtNew="+fnameExtNew);
            }
        }
        
        d2File = new File(fnameExtNew);
        if (d2File.exists()){
            this.reset();
            this.updatePatt2D(d2File);
            this.updatePanels();
            return;
        }else{
            tAOut.stat("No file found with fname "+fnameExtNew);
        }
        
        if (fnameExtNew.isEmpty())return;
        
        //prova pksearchframe
        if (pksframe!=null){
            if (pksframe.getFileOut()!=null){
                pksframe.importOUT(pksframe.getFileOut());
            }
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
        
        File d2File = new File(fnameExtNew);
        if (d2File.exists()){
            this.reset();
            this.updatePatt2D(d2File);
            this.updatePanels();
            return;
        }else{
        	log.info("No file found with filename: "+fnameExtNew);
        }
        
        //agafem el nom sense el _000X.edf
        String basFname = fnameCurrent.substring(0, fnameCurrent.length()-5);
        log.debug("basFname="+basFname);
        int indexNoDigit=-1;
        for (int i=1;i<basFname.length()-2;i++){
            char c = basFname.charAt(basFname.length()-i);
            if (!Character.isDigit(c)) {
                indexNoDigit=i;
                break;
            }
        }
        log.debug("indexNoDigit="+indexNoDigit);
        if (indexNoDigit>0){
            String sdom = basFname.substring(basFname.length()-(indexNoDigit-1), basFname.length());
            log.debug("sdom="+sdom);
            int ndom = -1;
            try{
                ndom = Integer.parseInt(sdom);
            }catch(Exception ex){
                log.debug("error parsing domain number");
            }
            if (ndom>=0){
                ndom = ndom - 1;
                fnameExtNew = basFname.substring(0,basFname.length()-(indexNoDigit-1)).concat(Integer.toString(ndom)).concat("_0000.").concat(fextCurrent);
                log.debug("fnameExtNew="+fnameExtNew);
            }
        }
        
        d2File = new File(fnameExtNew);
        if (d2File.exists()){
            this.reset();
            this.updatePatt2D(d2File);
            this.updatePanels();
            return;
        }else{
        	log.info("No file found with filename: "+fnameExtNew);
        }
        
        if (fnameExtNew.isEmpty())return;
        
        //prova pksearchframe
        if (pksframe!=null){
            if (pksframe.getFileOut()!=null){
                pksframe.importOUT(pksframe.getFileOut());
            }
        }
    }
    protected void do_btnDbdialog_actionPerformed(ActionEvent arg0) {
        // tanquem calibracio en cas que estigui obert
        if (calibration != null) calibration.dispose();
        if (exZones != null) exZones.dispose();
    
        if (dbDialog == null) {
            dbDialog = new Database(mainF,this.getPanelImatge());
        }
        dbDialog.setVisible(true);
        panelImatge.setDBdialog(dbDialog);
    }
    protected void do_btnPeakSearchint_actionPerformed(ActionEvent arg0) {
        mntmFindPeaks.doClick();
    }
    protected void do_mntmInstrumentalParameters_actionPerformed(ActionEvent e) {
        if (patt2D != null) {
            if (paramDialog==null){
                paramDialog = new ImageParameters(mainF,this.getPanelImatge());
            }
            paramDialog.setVisible(true);
        }
    }
    protected void do_mntmLabCalibration_actionPerformed(ActionEvent e) {
        if (patt2D != null) {
            // tanquem zones excloses en cas que estigui obert
            if (exZones != null)
                exZones.dispose();
            if (pksframe != null)
                pksframe.dispose();
            if (calibration == null) {
                calibration = new Calibration(mainF, this.getPanelImatge());
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
                exZones = new ExZones(mainF,this.getPanelImatge());
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
                    pksframe = new PeakSearch(mainF,this.panelImatge,false,null);
                }
                pksframe.setVisible(true);
                panelImatge.setPKsearch(pksframe);
            }
    
        }
    protected void do_mntmBackgroundSubtraction_actionPerformed(ActionEvent e) {
        if (patt2D != null) {
            if (d2DsubWin == null) {
                d2DsubWin = new BackgroundEstimation(this);
            }
            d2DsubWin.setVisible(true);
        }else{
            //obrim el batch
            if (d2DsubWinBatch == null){
                d2DsubWinBatch = new BackgroundEstimation_batch(this);
            }
            d2DsubWinBatch.setVisible(true);
        }
    }
    protected void do_mntmRadialIntegration_actionPerformed(ActionEvent e) {
        if(patt2D != null){
            if(this.irWin==null){
                irWin = new ConvertTo1DXRD(mainF,this.getPanelImatge());
            }
            irWin.setVisible(true); 
        }
    }
    protected void do_mntmAzimuthalIntegration_actionPerformed(ActionEvent arg0) {
        if(patt2D != null){
            if(this.iazWin==null){
                iazWin = new AzimuthalPlot(this.getMainF(),this.getPanelImatge());
            }
            iazWin.setVisible(true); 
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
        btnDbdialog.doClick();
    }
    protected void do_mntmExportAsPng_actionPerformed(ActionEvent e) {
            
            if (patt2D == null) {
                //batch convert
                FileNameExtensionFilter filt[] = ImgFileUtils.getExtensionFilterRead();
                File[] flist = FileUtils.fchooserMultiple(mainF,new File(getWorkdir()), filt, 0,null);
                if (flist==null) return;
                D2Dplot_global.setWorkdir(flist[0]);
                this.reset();
                
                for(int i=0;i<flist.length;i++) {
                    //openImage
                    this.updatePatt2D(flist[i]);
                    //change ext
                    File f = FileUtils.canviExtensio(flist[i], "png");
                    log.info(f.toString());
                    ImgFileUtils.exportPNG(f, panelImatge.getImage(),true);
                }
                
                return;
            }
            
            File imFile = FileUtils.fchooserSaveAsk(mainF, new File(getWorkdir()), null, "png");
            
            if (imFile !=null) {
                int w = panelImatge.getPanelImatge().getSize().width;
                int h = panelImatge.getPanelImatge().getSize().height;
                
                log.debug(String.format("(frame) w=%d h=%d", w,h));
                
                Rectangle r = panelImatge.calcSubimatgeDinsFrame();
                Rectangle2D.Float rfr = new Rectangle2D.Float(r.x,r.y,r.width,r.height);
                
                rfr = panelImatge.rectangleToFrameCoords(rfr);
                w = (int) rfr.getWidth();
                h = (int) rfr.getHeight();
                
                log.debug(String.format("(rect) w=%d h=%d", w,h));
    
                
                float factor = (float)patt2D.getDimX()/(float)w;
                
                String s = (String)JOptionPane.showInputDialog(
                		mainF,
                        "Current plot size (Width x Heigth) is "+Integer.toString(w)+" x "+Integer.toString(h)+"pixels\n"
                                + "Scale factor to apply (-1 for clean image in real size)=",
                        "Apply scale factor",
                        JOptionPane.PLAIN_MESSAGE,
                        null,
                        null,
                        FileUtils.dfX_2.format(factor));
                
                if ((s != null) && (s.length() > 0)) {
                    try{
                        factor=Float.parseFloat(s);
                    }catch(Exception ex){
                        log.warning("Error reading factor");
                    }
                    log.writeNameNumPairs("config", true, "factor", factor);
                }
                
                if (factor>0){
                    double pageWidth = panelImatge.getPanelImatge().getSize().width*factor;
                    double pageHeight = panelImatge.getPanelImatge().getSize().height*factor;
                    double imageWidth = panelImatge.getPanelImatge().getSize().width;
                    double imageHeight = panelImatge.getPanelImatge().getSize().height;
//                    double scaleFactor = ImgFileUtils.getScaleFactorToFit(
//                            new Dimension((int) Math.round(imageWidth), (int) Math.round(imageHeight)),
//                            new Dimension((int) Math.round(pageWidth), (int) Math.round(pageHeight)));
    
                    double scaleFactor = ImgFileUtils.getScaleFactorToFit(
                            new Dimension((int) imageWidth, (int) imageHeight),
                            new Dimension((int) pageWidth, (int) pageHeight));
                    
                    int width = (int) Math.round(pageWidth)+1;
                    int height = (int) Math.round(pageHeight)+1;

                    log.writeNameNumPairs("config", true, "pageWidth, pageHeight, imageWidth, imageHeight,scaleFactor,width,height", pageWidth, pageHeight, imageWidth, imageHeight,scaleFactor,width,height);
                    
                    int xnew = FastMath.max((int) (rfr.getX()*scaleFactor),0);
                    int ynew = FastMath.max((int) (rfr.getY()*scaleFactor),0);
                    int wnew = (int) (w*scaleFactor);
                    int hnew = (int) (h*scaleFactor);
                    
                    BufferedImage img = new BufferedImage(
                            width,
                            height,
                            BufferedImage.TYPE_INT_ARGB);
                    Graphics2D g2d = img.createGraphics();
                    g2d.scale(scaleFactor, scaleFactor);
                    panelImatge.getPanelImatge().paintComponent(g2d);
                    g2d.dispose();
    
                    log.writeNameNumPairs("config", true, "xnew, ynew, wnew, hnew", xnew, ynew, wnew, hnew);
                    //comprovacions que no peti //TODO:s'hauria de trobar el problema real
                    if ((wnew)>width)wnew=width;
                    if ((hnew)>height)hnew=height;
                    log.writeNameNumPairs("config", true, "xnew, ynew, wnew, hnew", xnew, ynew, wnew, hnew);

                    imFile = ImgFileUtils.exportPNG(imFile, img.getSubimage(xnew, ynew, wnew, hnew),false); //l'extensio l'ha forçada el filechooser
                    
                }else{ //salvem imatge base amb mida ideal
                    if (panelImatge.getSubimage()==null){
                        imFile = ImgFileUtils.exportPNG(imFile, panelImatge.getImage(),false);
                    }else {
                        imFile = ImgFileUtils.exportPNG(imFile, panelImatge.getSubimage(),false);    
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
        if (p2dAbout == null) {
            p2dAbout = new About(mainF);
        }
        p2dAbout.setVisible(true);
    }
    protected void do_mntmManual_actionPerformed(ActionEvent e) {
        if (p2dAbout == null) {
            p2dAbout = new About(mainF);
        }
        p2dAbout.openManual();
    }
    protected void do_mntmDincoSol_actionPerformed(ActionEvent e) {
        //Preguntem per obrir un fitxer SOL pero de totes formes obrirem el dialeg
        if (dincoFrame == null) {
            dincoFrame = new IncoPlot(mainF,this.getPanelImatge());
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
            dincoFrame = new IncoPlot(mainF,this.getPanelImatge());
        }
        if (!dincoFrame.hasSolutionsLoaded()){
            dincoFrame.setXDSMode();
            dincoFrame.openXDS();
        }
        dincoFrame.setVisible(true);
        panelImatge.setDinco(dincoFrame);
    }
//    protected void do_mntmClearAll_actionPerformed(ActionEvent e) {
//        this.getPatt2D().clearSolutions();
//        this.dincoFrame.dispose();
//    }
    protected void do_mntmHpTools_actionPerformed(ActionEvent e) {
        if (hpframe == null) {
            hpframe = new HPtools(mainF,this.getPanelImatge());
        }
        hpframe.setVisible(true);
    }
    protected void do_mntmSubtractImages_actionPerformed(ActionEvent e) {
        
        SubtractImages sdiag = new SubtractImages(mainF);
        sdiag.setVisible(true);
        
        if (sdiag.isCancel())return;
        if (sdiag.getImage()==null)	return;
        if (sdiag.getImage()==null) return;
        
        Pattern2D img = ImgFileUtils.readPatternFile(sdiag.getImage(),true);
        float fac = sdiag.getFactor();
        Pattern2D img2 = ImgFileUtils.readPatternFile(sdiag.getBkgImage(),true);
        
        if(img==null)return;
        if(img2==null)return;
        
        Pattern2D dataSub = ImgOps.subtractBKG_v2(img, img2, fac)[0];
        updatePatt2D(dataSub,false,true);    
    }
    protected void do_mntmSumImages_actionPerformed(ActionEvent e) {
        FileNameExtensionFilter filt[] = ImgFileUtils.getExtensionFilterRead();
        File[] flist = FileUtils.fchooserMultiple(mainF,new File(getWorkdir()), filt, 0,"Select 2DXRD data files to sum");
        if (flist==null) return;
        D2Dplot_global.setWorkdir(flist[0]);
    
        pm = new ProgressMonitor(mainF,
                "Summing Images...",
                "", 0, 100);
        pm.setProgress(0);
        sumwk = new ImgOps.sumImagesFileWorker(flist);
        sumwk.addPropertyChangeListener(new PropertyChangeListener() {
    
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                log.debug(evt.getPropertyName());
                if ("progress" == evt.getPropertyName() ) {
                    int progress = (Integer) evt.getNewValue();
                    pm.setProgress(progress);
                    pm.setNote(String.format("%d%%\n", progress));
                    if (pm.isCanceled() || sumwk.isDone()) {
                        Toolkit.getDefaultToolkit().beep();
                        if (pm.isCanceled()) {
                            sumwk.cancel(true);
                            log.info("Sum canceled");
                        } else {
                            log.info("Sum finished!!");
                        }
                        pm.close();
                    }
                }
                if (sumwk.isDone()){
                    Pattern2D suma = sumwk.getpattSum();
                    if (suma==null){
                        log.warning("Error summing files");
                        return;
                    }else{
                        suma.recalcMaxMinI();
                        suma.calcMeanI();
                        suma.recalcExcludedPixels();
                        updatePatt2D(suma,true,true);    
                    }
                }
            }
        });
        sumwk.execute();
    }
    protected void do_mntmBatchConvert_actionPerformed(ActionEvent e) {
        FileNameExtensionFilter filt[] = ImgFileUtils.getExtensionFilterRead();
        File[] flist = FileUtils.fchooserMultiple(mainF,new File(getWorkdir()), filt, 0,"Select 2DXRD data files to convert");
        if (flist==null) return;
        D2Dplot_global.setWorkdir(flist[0]);
        
        pm = new ProgressMonitor(mainF,
                "Converting Images...",
                "", 0, 100);
        pm.setProgress(0);
        convwk = new ImgFileUtils.batchConvertFileWorker(flist,this.getPanelImatge());
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
                            log.info("Batch convert stopped by user");
                        } else {
                            log.info("Batch convert finished!!");
                        }
                        pm.close();
                    }
                }
            }
        });
        convwk.execute();
    }
    protected void do_mntmFastopen_actionPerformed(ActionEvent e) {
        fastView = new FastViewer(mainF);
        fastView.setVisible(true);
        fastView.showOpenImgsDialog();
    }
    protected void do_mntmScDataTo_actionPerformed(ActionEvent e) {
        scToInco = new SC_to_INCO(mainF);
        scToInco.setVisible(true);
    }
    //    protected void do_mntmSavetif_actionPerformed(ActionEvent e) {
    //        File out = FileUtils.fchooserSaveAsk(this, D2Dplot_global.getWorkdirFile(), null);
    //        ImgFileUtils.writeTIF(out, this.getPanelImatge().getImage());
    //    }
    protected void do_mntmTtssoftware_actionPerformed(ActionEvent e) {
        tts = new TTS(this);
        tts.setVisible(true);
    }
    protected void do_mntmReset_actionPerformed(ActionEvent e) {
        this.restart();
    }
    
	protected void do_mntmCheckForUpdates_actionPerformed(ActionEvent e) {
        String bona="";
        String url="https://www.cells.es/en/beamlines/bl04-mspd/preparing-your-experiment";
        
		try {
			URL mspd = new URL(url);
			BufferedReader in = new BufferedReader(new InputStreamReader(mspd.openStream()));
	        String inputLine;
	        while ((inputLine = in.readLine()) != null) {
	            if (FileUtils.containsIgnoreCase(inputLine, "d2Dplot software for linux v")) {
	            	bona = inputLine;
	            	break;
	            }
	        }
	        in.close();
			
		} catch (Exception e1) {
			if(D2Dplot_global.isDebug())e1.printStackTrace();
			tAOut.stat("Error checking for new versions");
		}

		//d2Dplot software for linux v1811" href="https://www.cells.es/en/beamlines/bl04-mspd/d2dplot1811win_181122-tar.gz
		
		if (bona.length()>0) {
//			String data = bona.split("tar.gz")[0];
//			data = data.split("lin_")[1];
			String data = bona.split(".zip")[0];
			data = data.split("win_")[1];
			int webVersion = Integer.parseInt(data);
			if (webVersion != D2Dplot_global.build_date) {
//				FileUtils.InfoDialog(this, "New d2Dplot version is available ("+webVersion+"). Please download at\nhttps://www.cells.es/en/beamlines/bl04-mspd/preparing-your-experiment", "New version available!");
				boolean yes = FileUtils.YesNoDialog(mainF, "New d2Dplot version is available ("+webVersion+"). Please download at\n"+url,"New version available!");
				if (yes) {
					FileUtils.openURL(url);
				}
			}
			if (webVersion == D2Dplot_global.build_date) {
				FileUtils.InfoDialog(mainF, "You have the last version of d2Dplot ("+D2Dplot_global.build_date+").", "d2Dplot is up to date");
			}
		}
		
		
		
	}
	
    protected void do_chckbxShowRings_itemStateChanged(ItemEvent arg0) {
        if (combo_LATdata.getItemCount()>0){
            log.debug("do_chckbxShowRings_itemStateChanged CALLED");
            panelImatge.setShowQuickListCompoundRings(chckbxShowRings.isSelected(), this.getQuickListCompound());
        }
    
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
    protected void do_combo_showRings_itemStateChanged(ItemEvent arg0) {
        if (arg0.getStateChange() == ItemEvent.SELECTED) {
            panelImatge.setShowQuickListCompoundRings(chckbxShowRings.isSelected(), this.getQuickListCompound());
        }
    }
    protected void do_lblOpened_mouseReleased(MouseEvent e) {
            if (lblOpened.getText().startsWith("(no")||lblOpened.getText().isEmpty())return;
            
            File f = new File(lblOpened.getText());
    //        String fpath = f.getParent();
            String fpath = new File(f.getAbsolutePath()).getParent();
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
    	mainF.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        this.lblOpened.setForeground(Color.blue);
    }
    protected void do_lblOpened_mouseExited(MouseEvent e) {
    	mainF.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        this.lblOpened.setForeground(Color.black);
    }
    //    private JMenu mnDebug;
    //    private JMenuItem mntmSavetif;
        
    public static String getBinDir() {return D2Dplot_global.binDir;}
    public static String getSeparator() {return D2Dplot_global.separator;}
    public static String getWorkdir() {return D2Dplot_global.getWorkdir();}
    public static void setWorkdir(String wdir) {D2Dplot_global.setWorkdir(wdir);}
    public static void updateQuickList(){
        Iterator<PDCompound> itrC= PDDatabase.getQuickListIterator();
        combo_LATdata.removeAllItems();
        while (itrC.hasNext()){
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
		return mainF;
	}

	/**
	 * @param mainF the mainF to set
	 */
	public void setMainF(JFrame mainF) {
		this.mainF = mainF;
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
//    public LogJTextArea gettAOut() {
//        return tAOut;
//    }
    public boolean isSelectPoints(){
        return this.chckbxIndex.isSelected();
    }
    public IncoPlot getDincoFrame() {
        return dincoFrame;
    }
    public void setDincoFrame(IncoPlot dincoFrame) {
        this.dincoFrame = dincoFrame;
    }
    public Calibration getCalibration() {
        return calibration;
    }
    public void setCalibration(Calibration calibration) {
        this.calibration = calibration;
    }
    public ConvertTo1DXRD getIrWin() {
        return irWin;
    }
    public void setIrWin(ConvertTo1DXRD irWin) {
        this.irWin = irWin;
    }
    public PeakSearch getpksearchframe() {
        return this.pksframe;
    }


}
