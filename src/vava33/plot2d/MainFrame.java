package vava33.plot2d;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.EventQueue;
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
import java.io.File;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.Scanner;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.UIManager;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.vava33.jutils.FileUtils;
import com.vava33.jutils.LogJTextArea;
import com.vava33.jutils.VavaLogger;

import vava33.plot2d.auxi.Ellipse;
import vava33.plot2d.auxi.ImgFileUtils;
import vava33.plot2d.auxi.ImgOps;
import vava33.plot2d.auxi.LAT_data;
import vava33.plot2d.auxi.OrientSolucio;
import vava33.plot2d.auxi.Pattern2D;
import vava33.plot2d.auxi.PuntCercle;

import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JSeparator;

public class MainFrame extends JFrame {
    private static final long serialVersionUID = 4368250280987133953L;
    private static String separator = System.getProperty("file.separator");
    private static String binDir = System.getProperty("user.dir") + separator + "bin" + separator;
    private static String d2dsubExec = "d2Dsub";
    private static String welcomeMSG = "d2Dplot v1503 (150306) by OV";
    private static String workdir = System.getProperty("user.dir");
//    private static String workdir = "C:\\Ori_TMP\\";
//    private static String workdir = "lau1_data.bin";
//    private static String workdir = "C:\\ovallcorba\\Dades_difraccio\\2D-INCO\\Mesures-Juliol-ALBA\\";
    private static boolean debug = false;
    
    private JButton btn05x;
    private JButton btn2x;
    private JButton btnD2Dint;
    private JButton btnD2Dsub;
    private JButton btnDdpeaksearch;
    private JButton btnExZones;
    private JButton btnLab6;
    private JButton btnMidaReal;
    private JButton btnOpen;
    private JButton btnOpenSol;
    private JButton btnResetView;
    private JButton btnSaveBin;
    private JButton btnSaveDicvol;
    private JButton btnSavePng;
    private JButton btnSetParams;
    private JCheckBox chckbxIndex;
    private JCheckBox chckbxShowHkl;
    private JCheckBox chckbxShowSol;
    private JLabel lblhelp;
    private JLabel lblOpened;
    private JList listSol;
    private JPanel contentPane;
    private JPanel panel;
    private JPanel panel_1;
    private JPanel panel_all;
    private JPanel panel_controls;
    private JPanel panel_function;
    private JPanel panel_opcions;
    private JPanel panel_stat;
    private JScrollPane scrollPane;
    private JScrollPane scrollPane_1;
    private JSplitPane splitPane;
    private JSplitPane splitPane_1;
    
    private D2Dsub_frame d2DsubWin;
    private D2Dsub_batch d2DsubWinBatch;
    private Calib_dialog calibration;
    private ExZones_dialog exZones;
    private About_dialog p2dAbout;
    private File openedFile;
    boolean fileOpened = false;
    private ImagePanel panelImatge;
    private Pattern2D patt2D;
    private Pklist_dialog pkListWin;
    private LogJTextArea tAOut;
    private IntegracioRadial irWin;
    private database_dialog dbDialog;
    private ArrayList<LAT_data> customLATs;

    public static String getBinDir() {return binDir;}
    public static String getD2dsubExec() {return d2dsubExec;}
    public static String getSeparator() {return separator;}
    public static String getWorkdir() {return workdir;}
    public static final int shortsize = Short.MAX_VALUE;
    public static final float minDspacingToSearch = 1.15f;
    private JButton btn_yarc;
    private JButton btnRadialInteg;
    private JButton btnProva;
    private JButton btnSumImages;
    private JButton btnWorkSol;
    private JCheckBox chckbxWorkSol;
    private JButton btnNext;
    private JButton btnPrev;
    private JPanel panel_2;
    private JCheckBox chckbxShowRings;
    private JComboBox combo_LATdata;
    private JButton btnDbdialog;
    private JPanel panel_3;
    private JButton btnAddLat;
    private JSeparator separator_1;

    /**
     * Launch the application. ES POT PASSAR COM A ARGUMENT EL DIRECTORI DE TREBALL ON S'OBRIRAN PER DEFECTE ELS DIALEGS
     * 
     */
    public static void main(String[] args) {
    	//LOGGER
    	VavaLogger.initLogger();
    	if(debug){
    		VavaLogger.enableLogger();
//    		VavaLogger.setINFO();
    		VavaLogger.LOG.info("LOGGING OF D2Dplot ENABLED");
//    		VavaLogger.LOG.fine("LOGGING OF D2Dplot ENABLED");
//    		VavaLogger.LOG.config("LOGGING OF D2Dplot ENABLED");
//    		VavaLogger.LOG.warning("LOGGING OF D2Dplot ENABLED");
    	}else{
    		VavaLogger.LOG.info("LOGGING OF D2Dplot DISABLED");
    		VavaLogger.disableLogger();
    	}
    	
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            if(UIManager.getLookAndFeel().toString().contains("metal")){
                UIManager.setLookAndFeel("com.sun.java.swing.plaf.gtk.GTKLookAndFeel");    
            }
            // UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName()); //java metal
            
        } catch (Throwable e) {
            e.printStackTrace();
        }

        StringBuilder path = new StringBuilder();
        for (int i = 0; i < args.length; i++) {
            path.append(args[i]).append(" ");
        }
        if (args.length > 0)
            workdir = path.toString().trim();

        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
//                    workdir = "/home/ovallcorba/ovallcorba/Dades_difraccio/2D_INCO/WORK_140925/ov/proves_integ";
                    MainFrame frame = new MainFrame();
                    frame.inicialitza();
                    frame.setLocationRelativeTo(null);
                    frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Create the frame.
     */
    public MainFrame() {
        setIconImage(Toolkit.getDefaultToolkit().getImage(MainFrame.class.getResource("/img/Icona.png")));
        setTitle("d2Dplot");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 1180, 853);
        this.contentPane = new JPanel();
        this.contentPane.setBorder(null);
        setContentPane(this.contentPane);
        GridBagLayout gbl_contentPane = new GridBagLayout();
        gbl_contentPane.columnWidths = new int[] { 0, 0, 0, 0, 0 };
        gbl_contentPane.rowHeights = new int[] { 0, 0 };
        gbl_contentPane.columnWeights = new double[] { 0.0, 1.0, 0.0, 0.0, Double.MIN_VALUE };
        gbl_contentPane.rowWeights = new double[] { 1.0, Double.MIN_VALUE };
        this.contentPane.setLayout(gbl_contentPane);
        this.splitPane = new JSplitPane();
        this.splitPane.setBorder(null);
        this.splitPane.setContinuousLayout(true);
        this.splitPane.setResizeWeight(0.9);
        this.splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
        GridBagConstraints gbc_splitPane = new GridBagConstraints();
        gbc_splitPane.fill = GridBagConstraints.BOTH;
        gbc_splitPane.gridwidth = 4;
        gbc_splitPane.gridx = 0;
        gbc_splitPane.gridy = 0;
        this.contentPane.add(this.splitPane, gbc_splitPane);
        this.panel_stat = new JPanel();
        this.splitPane.setRightComponent(this.panel_stat);
        this.panel_stat.setBackground(Color.BLACK);
        this.panel_stat.setBorder(null);
        GridBagLayout gbl_panel_stat = new GridBagLayout();
        gbl_panel_stat.columnWidths = new int[] { 0, 0 };
        gbl_panel_stat.rowHeights = new int[] { 100, 0 };
        gbl_panel_stat.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
        gbl_panel_stat.rowWeights = new double[] { 1.0, Double.MIN_VALUE };
        this.panel_stat.setLayout(gbl_panel_stat);
        this.scrollPane = new JScrollPane();
        scrollPane.setViewportBorder(null);
        this.scrollPane.setBorder(null);
        GridBagConstraints gbc_scrollPane = new GridBagConstraints();
        gbc_scrollPane.insets = new Insets(5, 5, 5, 5);
        gbc_scrollPane.fill = GridBagConstraints.BOTH;
        gbc_scrollPane.gridx = 0;
        gbc_scrollPane.gridy = 0;
        this.panel_stat.add(this.scrollPane, gbc_scrollPane);
        this.tAOut = new LogJTextArea();
        this.tAOut.setTabSize(4);
        this.tAOut.setWrapStyleWord(true);
        this.tAOut.setLineWrap(true);
        this.tAOut.setMaximumSize(new Dimension(32767, 32767));
        this.tAOut.setRows(1);
        this.tAOut.setBackground(Color.BLACK);
//        this.tAOut.setBorder(null);
        this.scrollPane.setViewportView(this.tAOut);
//        this.scrollPane.setBorder(BorderFactory.createEmptyBorder());
        this.panel_all = new JPanel();
        this.splitPane.setLeftComponent(this.panel_all);
        GridBagLayout gbl_panel_all = new GridBagLayout();
        gbl_panel_all.columnWidths = new int[] { 0, 0 };
        gbl_panel_all.rowHeights = new int[] { 0, 0, 0 };
        gbl_panel_all.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
        gbl_panel_all.rowWeights = new double[] { 0.0, 1.0, Double.MIN_VALUE };
        this.panel_all.setLayout(gbl_panel_all);
        
        panel_2 = new JPanel();
        GridBagConstraints gbc_panel_2 = new GridBagConstraints();
        gbc_panel_2.fill = GridBagConstraints.BOTH;
        gbc_panel_2.insets = new Insets(5, 5, 5, 5);
        gbc_panel_2.gridx = 0;
        gbc_panel_2.gridy = 0;
        panel_all.add(panel_2, gbc_panel_2);
        GridBagLayout gbl_panel_2 = new GridBagLayout();
        gbl_panel_2.columnWidths = new int[]{0, 0, 0, 0, 0, 0, 0};
        gbl_panel_2.rowHeights = new int[]{32, 0};
        gbl_panel_2.columnWeights = new double[]{0.0, 1.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
        gbl_panel_2.rowWeights = new double[]{0.0, Double.MIN_VALUE};
        panel_2.setLayout(gbl_panel_2);
        
                this.btnOpen = new JButton("Open Image");
                GridBagConstraints gbc_btnOpen = new GridBagConstraints();
                gbc_btnOpen.insets = new Insets(0, 0, 0, 5);
                gbc_btnOpen.gridx = 0;
                gbc_btnOpen.gridy = 0;
                panel_2.add(btnOpen, gbc_btnOpen);
                this.btnOpen.setMargin(new Insets(2, 7, 2, 7));
                this.btnOpen.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent arg0) {
                        do_btnOpen_actionPerformed(arg0);
                    }
                });
        this.lblOpened = new JLabel("(no image opened)");
        GridBagConstraints gbc_lblOpened = new GridBagConstraints();
        gbc_lblOpened.anchor = GridBagConstraints.WEST;
        gbc_lblOpened.insets = new Insets(0, 0, 0, 5);
        gbc_lblOpened.gridx = 1;
        gbc_lblOpened.gridy = 0;
        panel_2.add(lblOpened, gbc_lblOpened);
        
        btnPrev = new JButton("<");
        btnPrev.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                do_btnPrev_actionPerformed(e);
            }
        });
        GridBagConstraints gbc_btnPrev = new GridBagConstraints();
        gbc_btnPrev.insets = new Insets(0, 0, 0, 5);
        gbc_btnPrev.gridx = 2;
        gbc_btnPrev.gridy = 0;
        panel_2.add(btnPrev, gbc_btnPrev);
        btnPrev.setToolTipText("Previous image (by index)");
        
        btnNext = new JButton(">");
        btnNext.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                do_btnNext_actionPerformed(arg0);
            }
        });
        GridBagConstraints gbc_btnNext = new GridBagConstraints();
        gbc_btnNext.insets = new Insets(0, 0, 0, 5);
        gbc_btnNext.gridx = 3;
        gbc_btnNext.gridy = 0;
        panel_2.add(btnNext, gbc_btnNext);
        btnNext.setToolTipText("Next image (by index)");
        
        btnSumImages = new JButton("Sum Images");
        GridBagConstraints gbc_btnSumImages = new GridBagConstraints();
        gbc_btnSumImages.fill = GridBagConstraints.HORIZONTAL;
        gbc_btnSumImages.insets = new Insets(0, 0, 0, 5);
        gbc_btnSumImages.gridx = 4;
        gbc_btnSumImages.gridy = 0;
        panel_2.add(btnSumImages, gbc_btnSumImages);
        btnSumImages.setMargin(new Insets(2, 2, 2, 2));
        btnSumImages.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent arg0) {
        		do_btnSumImages_actionPerformed(arg0);
        	}
        });
        this.lblhelp = new JLabel(" ? ");
        GridBagConstraints gbc_lblhelp = new GridBagConstraints();
        gbc_lblhelp.gridx = 5;
        gbc_lblhelp.gridy = 0;
        panel_2.add(lblhelp, gbc_lblhelp);
        this.lblhelp.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent arg0) {
                do_lblhelp_mouseClicked(arg0);
            }
        });
        this.lblhelp.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        this.lblhelp.setFont(new Font("Tahoma", Font.BOLD, 15));
        this.splitPane_1 = new JSplitPane();
        this.splitPane_1.setBorder(new TitledBorder(null, "", TitledBorder.LEADING, TitledBorder.TOP, null, null));
        this.splitPane_1.setContinuousLayout(true);
        this.splitPane_1.setResizeWeight(1.0);
        GridBagConstraints gbc_splitPane_1 = new GridBagConstraints();
        gbc_splitPane_1.fill = GridBagConstraints.BOTH;
        gbc_splitPane_1.gridx = 0;
        gbc_splitPane_1.gridy = 1;
        this.panel_all.add(this.splitPane_1, gbc_splitPane_1);

        this.panelImatge = new ImagePanel();
        this.splitPane_1.setLeftComponent(this.panelImatge);
        this.panelImatge.setBorder(null);
        this.panel_controls = new JPanel();
        this.splitPane_1.setRightComponent(this.panel_controls);
        GridBagLayout gbl_panel_controls = new GridBagLayout();
        gbl_panel_controls.columnWidths = new int[] { 0, 112, 0 };
        gbl_panel_controls.rowHeights = new int[] { 0, 0, 0, 0 };
        gbl_panel_controls.columnWeights = new double[] { 1.0, 1.0, Double.MIN_VALUE };
        gbl_panel_controls.rowWeights = new double[] { 0.0, 0.0, 1.0, Double.MIN_VALUE };
        this.panel_controls.setLayout(gbl_panel_controls);
        this.panel_opcions = new JPanel();
        GridBagConstraints gbc_panel_opcions = new GridBagConstraints();
        gbc_panel_opcions.fill = GridBagConstraints.BOTH;
        gbc_panel_opcions.insets = new Insets(0, 5, 5, 5);
        gbc_panel_opcions.gridx = 0;
        gbc_panel_opcions.gridy = 0;
        this.panel_controls.add(this.panel_opcions, gbc_panel_opcions);
        this.panel_opcions.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Plot",
                TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
        GridBagLayout gbl_panel_opcions = new GridBagLayout();
        gbl_panel_opcions.columnWidths = new int[] { 0, 0 };
        gbl_panel_opcions.rowHeights = new int[] { 0, 0, 0, 0, 0, 0, 0, 0, 0 };
        gbl_panel_opcions.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
        gbl_panel_opcions.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };
        this.panel_opcions.setLayout(gbl_panel_opcions);
        this.btnResetView = new JButton("Reset view");
        btnResetView.setPreferredSize(new Dimension(90, 32));
        this.btnResetView.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                do_btnResetView_actionPerformed(arg0);
            }
        });
        this.btnResetView.setMargin(new Insets(2, 2, 2, 2));
        GridBagConstraints gbc_btnResetView = new GridBagConstraints();
        gbc_btnResetView.fill = GridBagConstraints.HORIZONTAL;
        gbc_btnResetView.insets = new Insets(0, 2, 5, 2);
        gbc_btnResetView.gridx = 0;
        gbc_btnResetView.gridy = 0;
        this.panel_opcions.add(this.btnResetView, gbc_btnResetView);
        this.chckbxShowSol = new JCheckBox("Show sol");
        this.chckbxShowSol.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent arg0) {
                do_chckbxShowSol_itemStateChanged(arg0);
            }
        });
        this.btnMidaReal = new JButton("True size");
        btnMidaReal.setPreferredSize(new Dimension(90, 32));
        this.btnMidaReal.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                do_btnMidaReal_actionPerformed(e);
            }
        });
        this.btnMidaReal.setMargin(new Insets(2, 2, 2, 2));
        GridBagConstraints gbc_btnMidaReal = new GridBagConstraints();
        gbc_btnMidaReal.fill = GridBagConstraints.HORIZONTAL;
        gbc_btnMidaReal.insets = new Insets(0, 2, 5, 2);
        gbc_btnMidaReal.gridx = 0;
        gbc_btnMidaReal.gridy = 1;
        this.panel_opcions.add(this.btnMidaReal, gbc_btnMidaReal);
        this.panel = new JPanel();
        GridBagConstraints gbc_panel = new GridBagConstraints();
        gbc_panel.insets = new Insets(0, 0, 5, 0);
        gbc_panel.fill = GridBagConstraints.BOTH;
        gbc_panel.gridx = 0;
        gbc_panel.gridy = 2;
        this.panel_opcions.add(this.panel, gbc_panel);
        GridBagLayout gbl_panel = new GridBagLayout();
        gbl_panel.columnWidths = new int[] { 0, 0, 0 };
        gbl_panel.rowHeights = new int[] { 21, 0 };
        gbl_panel.columnWeights = new double[] { 1.0, 1.0, Double.MIN_VALUE };
        gbl_panel.rowWeights = new double[] { 0.0, Double.MIN_VALUE };
        this.panel.setLayout(gbl_panel);
        this.btn2x = new JButton("2x");
        btn2x.setPreferredSize(new Dimension(50, 32));
        this.btn2x.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                do_btn2x_actionPerformed(e);
            }
        });
        this.btn05x = new JButton("0.5x");
        btn05x.setPreferredSize(new Dimension(50, 32));
        this.btn05x.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                do_btn05x_actionPerformed(arg0);
            }
        });
        this.btn05x.setMargin(new Insets(1, 2, 1, 2));
        GridBagConstraints gbc_btn05x = new GridBagConstraints();
        gbc_btn05x.fill = GridBagConstraints.HORIZONTAL;
        gbc_btn05x.insets = new Insets(0, 2, 0, 5);
        gbc_btn05x.gridx = 0;
        gbc_btn05x.gridy = 0;
        this.panel.add(this.btn05x, gbc_btn05x);
        this.btn2x.setMargin(new Insets(1, 7, 1, 7));
        GridBagConstraints gbc_btn2x = new GridBagConstraints();
        gbc_btn2x.fill = GridBagConstraints.HORIZONTAL;
        gbc_btn2x.insets = new Insets(0, 2, 0, 2);
        gbc_btn2x.gridx = 1;
        gbc_btn2x.gridy = 0;
        this.panel.add(this.btn2x, gbc_btn2x);
        GridBagConstraints gbc_chckbxShowSol = new GridBagConstraints();
        gbc_chckbxShowSol.insets = new Insets(5, 2, 5, 0);
        gbc_chckbxShowSol.anchor = GridBagConstraints.WEST;
        gbc_chckbxShowSol.gridx = 0;
        gbc_chckbxShowSol.gridy = 3;
        this.panel_opcions.add(this.chckbxShowSol, gbc_chckbxShowSol);
        this.chckbxShowHkl = new JCheckBox("Show hkl");
        this.chckbxShowHkl.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent arg0) {
                do_chckbxShowHkl_itemStateChanged(arg0);
            }
        });
        GridBagConstraints gbc_chckbxShowHkl = new GridBagConstraints();
        gbc_chckbxShowHkl.anchor = GridBagConstraints.WEST;
        gbc_chckbxShowHkl.insets = new Insets(0, 2, 5, 0);
        gbc_chckbxShowHkl.gridx = 0;
        gbc_chckbxShowHkl.gridy = 4;
        this.panel_opcions.add(this.chckbxShowHkl, gbc_chckbxShowHkl);
        this.chckbxIndex = new JCheckBox("Sel. Points");
        chckbxIndex.setSelected(true);
        GridBagConstraints gbc_chckbxIndex = new GridBagConstraints();
        gbc_chckbxIndex.anchor = GridBagConstraints.WEST;
        gbc_chckbxIndex.insets = new Insets(0, 2, 5, 0);
        gbc_chckbxIndex.gridx = 0;
        gbc_chckbxIndex.gridy = 5;
        this.panel_opcions.add(this.chckbxIndex, gbc_chckbxIndex);
        
        chckbxWorkSol = new JCheckBox("Work SOL");
        chckbxWorkSol.addItemListener(new ItemListener() {
        	public void itemStateChanged(ItemEvent arg0) {
        		do_chckbxWorkSol_itemStateChanged(arg0);
        	}
        });
        GridBagConstraints gbc_chckbxWorkSol = new GridBagConstraints();
        gbc_chckbxWorkSol.anchor = GridBagConstraints.WEST;
        gbc_chckbxWorkSol.insets = new Insets(0, 2, 5, 0);
        gbc_chckbxWorkSol.gridx = 0;
        gbc_chckbxWorkSol.gridy = 6;
        panel_opcions.add(chckbxWorkSol, gbc_chckbxWorkSol);
        this.btnSaveDicvol = new JButton("Points List");
        GridBagConstraints gbc_btnSaveDicvol = new GridBagConstraints();
        gbc_btnSaveDicvol.fill = GridBagConstraints.HORIZONTAL;
        gbc_btnSaveDicvol.insets = new Insets(0, 2, 0, 2);
        gbc_btnSaveDicvol.gridx = 0;
        gbc_btnSaveDicvol.gridy = 7;
        this.panel_opcions.add(this.btnSaveDicvol, gbc_btnSaveDicvol);
        this.btnSaveDicvol.setMinimumSize(new Dimension(100, 28));
        this.btnSaveDicvol.setPreferredSize(new Dimension(100, 32));
        this.btnSaveDicvol.setEnabled(false);
        this.btnSaveDicvol.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                do_btnSaveDicvol_actionPerformed(arg0);
            }
        });
        this.btnSaveDicvol.setMargin(new Insets(2, 2, 2, 2));
        this.chckbxIndex.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent arg0) {
                do_chckbxIndex_itemStateChanged(arg0);
            }
        });
        this.panel_function = new JPanel();
        GridBagConstraints gbc_panel_function = new GridBagConstraints();
        gbc_panel_function.gridheight = 3;
        gbc_panel_function.fill = GridBagConstraints.BOTH;
        gbc_panel_function.gridx = 1;
        gbc_panel_function.gridy = 0;
        this.panel_controls.add(this.panel_function, gbc_panel_function);
        this.panel_function.setBorder(new TitledBorder(null, "Options", TitledBorder.LEADING, TitledBorder.TOP, null,
                null));
        GridBagLayout gbl_panel_function = new GridBagLayout();
        gbl_panel_function.columnWidths = new int[] { 0, 0 };
        gbl_panel_function.rowHeights = new int[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
        gbl_panel_function.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
        gbl_panel_function.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };
        this.panel_function.setLayout(gbl_panel_function);
        this.btnSetParams = new JButton("Set Parameters");
        this.btnSetParams.setMinimumSize(new Dimension(100, 28));
        this.btnSetParams.setPreferredSize(new Dimension(101, 32));
        this.btnSetParams.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                do_btnSetParams_actionPerformed(arg0);
            }
        });
        this.btnSetParams.setMargin(new Insets(2, 2, 2, 2));
        GridBagConstraints gbc_btnSetParams = new GridBagConstraints();
        gbc_btnSetParams.fill = GridBagConstraints.BOTH;
        gbc_btnSetParams.insets = new Insets(0, 0, 5, 0);
        gbc_btnSetParams.gridx = 0;
        gbc_btnSetParams.gridy = 0;
        this.panel_function.add(this.btnSetParams, gbc_btnSetParams);
        this.btnLab6 = new JButton("Calibr. LaB6");
        btnLab6.setPreferredSize(new Dimension(101, 32));
        btnLab6.setMargin(new Insets(2, 2, 2, 2));
        this.btnLab6.setMinimumSize(new Dimension(100, 28));
        this.btnLab6.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                do_btnLab6_actionPerformed(arg0);
            }
        });
        GridBagConstraints gbc_btnLab6 = new GridBagConstraints();
        gbc_btnLab6.fill = GridBagConstraints.HORIZONTAL;
        gbc_btnLab6.insets = new Insets(0, 0, 5, 0);
        gbc_btnLab6.gridx = 0;
        gbc_btnLab6.gridy = 1;
        this.panel_function.add(this.btnLab6, gbc_btnLab6);
        this.btnD2Dsub = new JButton("d2Dsub");
        btnD2Dsub.setMargin(new Insets(2, 2, 2, 2));
        this.btnD2Dsub.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                do_btnD2Dsub_actionPerformed(e);
            }
        });
        this.btnExZones = new JButton("Ex. Zones");
        btnExZones.setMargin(new Insets(2, 2, 2, 2));
        this.btnExZones.setMinimumSize(new Dimension(100, 28));
        this.btnExZones.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                do_btnExZones_actionPerformed(arg0);
            }
        });
        this.btnExZones.setPreferredSize(new Dimension(101, 32));
        GridBagConstraints gbc_btnExZones = new GridBagConstraints();
        gbc_btnExZones.fill = GridBagConstraints.HORIZONTAL;
        gbc_btnExZones.insets = new Insets(0, 0, 5, 0);
        gbc_btnExZones.gridx = 0;
        gbc_btnExZones.gridy = 2;
        this.panel_function.add(this.btnExZones, gbc_btnExZones);
        this.btnD2Dsub.setMinimumSize(new Dimension(100, 28));
        this.btnD2Dsub.setPreferredSize(new Dimension(101, 32));
        GridBagConstraints gbc_btnD2Dsub = new GridBagConstraints();
        gbc_btnD2Dsub.fill = GridBagConstraints.HORIZONTAL;
        gbc_btnD2Dsub.insets = new Insets(0, 0, 5, 0);
        gbc_btnD2Dsub.gridx = 0;
        gbc_btnD2Dsub.gridy = 3;
        this.panel_function.add(this.btnD2Dsub, gbc_btnD2Dsub);
        
        btnRadialInteg = new JButton("Radial Integ.");
        btnRadialInteg.setPreferredSize(new Dimension(101, 32));
        btnRadialInteg.setMargin(new Insets(2, 2, 2, 2));
        btnRadialInteg.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent arg0) {
        		do_btnRadialInteg_actionPerformed(arg0);
        	}
        });
        GridBagConstraints gbc_btnRadialInteg = new GridBagConstraints();
        gbc_btnRadialInteg.fill = GridBagConstraints.HORIZONTAL;
        gbc_btnRadialInteg.insets = new Insets(0, 0, 5, 0);
        gbc_btnRadialInteg.gridx = 0;
        gbc_btnRadialInteg.gridy = 4;
        panel_function.add(btnRadialInteg, gbc_btnRadialInteg);
        this.btnDdpeaksearch = new JButton("d2Dpksearch");
        btnDdpeaksearch.setVisible(false);
        this.btnDdpeaksearch.setPreferredSize(new Dimension(101, 32));
        this.btnDdpeaksearch.setEnabled(false);
        this.btnDdpeaksearch.setMargin(new Insets(2, 2, 2, 2));
        GridBagConstraints gbc_btnDdpeaksearch = new GridBagConstraints();
        gbc_btnDdpeaksearch.fill = GridBagConstraints.HORIZONTAL;
        gbc_btnDdpeaksearch.insets = new Insets(0, 0, 5, 0);
        gbc_btnDdpeaksearch.gridx = 0;
        gbc_btnDdpeaksearch.gridy = 5;
        this.panel_function.add(this.btnDdpeaksearch, gbc_btnDdpeaksearch);
        this.btnOpenSol = new JButton("Open .SOL");
        this.btnOpenSol.setMinimumSize(new Dimension(100, 28));
        this.btnOpenSol.setPreferredSize(new Dimension(101, 32));
        GridBagConstraints gbc_btnOpenSol = new GridBagConstraints();
        gbc_btnOpenSol.fill = GridBagConstraints.HORIZONTAL;
        gbc_btnOpenSol.insets = new Insets(0, 2, 5, 2);
        gbc_btnOpenSol.gridx = 0;
        gbc_btnOpenSol.gridy = 6;
        this.panel_function.add(this.btnOpenSol, gbc_btnOpenSol);
        this.btnOpenSol.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                do_btnOpenSol_actionPerformed(arg0);
            }
        });
        this.btnOpenSol.setMargin(new Insets(2, 2, 2, 2));
        
        btnWorkSol = new JButton("Work SOL");
        btnWorkSol.setPreferredSize(new Dimension(101, 32));
        btnWorkSol.setMargin(new Insets(2, 2, 2, 2));
        btnWorkSol.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent arg0) {
        		do_btnWorkSol_actionPerformed(arg0);
        	}
        });
        GridBagConstraints gbc_btnWorkSol = new GridBagConstraints();
        gbc_btnWorkSol.fill = GridBagConstraints.HORIZONTAL;
        gbc_btnWorkSol.insets = new Insets(0, 0, 5, 0);
        gbc_btnWorkSol.gridx = 0;
        gbc_btnWorkSol.gridy = 7;
        panel_function.add(btnWorkSol, gbc_btnWorkSol);
        this.scrollPane_1 = new JScrollPane();
        this.scrollPane_1.setPreferredSize(new Dimension(112, 80));
        GridBagConstraints gbc_scrollPane_1 = new GridBagConstraints();
        gbc_scrollPane_1.insets = new Insets(0, 0, 5, 0);
        gbc_scrollPane_1.fill = GridBagConstraints.BOTH;
        gbc_scrollPane_1.gridx = 0;
        gbc_scrollPane_1.gridy = 8;
        this.panel_function.add(this.scrollPane_1, gbc_scrollPane_1);
        this.listSol = new JList();
        this.listSol.setVisibleRowCount(5);
        this.scrollPane_1.setViewportView(this.listSol);
        this.btnD2Dint = new JButton("d2Dint");
        btnD2Dint.setMargin(new Insets(2, 2, 2, 2));
        btnD2Dint.setVisible(false);
        this.btnD2Dint.setPreferredSize(new Dimension(101, 32));
        this.btnD2Dint.setEnabled(false);
        GridBagConstraints gbc_btnD2Dint = new GridBagConstraints();
        gbc_btnD2Dint.fill = GridBagConstraints.HORIZONTAL;
        gbc_btnD2Dint.insets = new Insets(0, 0, 5, 0);
        gbc_btnD2Dint.gridx = 0;
        gbc_btnD2Dint.gridy = 9;
        this.panel_function.add(this.btnD2Dint, gbc_btnD2Dint);
        
        btn_yarc = new JButton("debug Yarc");
        btn_yarc.setVisible(false);
        btn_yarc.setPreferredSize(new Dimension(101, 32));
        btn_yarc.setMargin(new Insets(2, 2, 2, 2));
        btn_yarc.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		do_btn_yarc_actionPerformed(e);
        	}
        });
        GridBagConstraints gbc_btn_yarc = new GridBagConstraints();
        gbc_btn_yarc.fill = GridBagConstraints.HORIZONTAL;
        gbc_btn_yarc.insets = new Insets(0, 0, 5, 0);
        gbc_btn_yarc.gridx = 0;
        gbc_btn_yarc.gridy = 10;
        panel_function.add(btn_yarc, gbc_btn_yarc);
        
        btnProva = new JButton("prova");
        btnProva.setPreferredSize(new Dimension(101, 32));
        btnProva.setMargin(new Insets(2, 2, 2, 2));
        btnProva.setEnabled(false);
        btnProva.setVisible(false);
        btnProva.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent arg0) {
        		do_btnProva_actionPerformed(arg0);
        	}
        });
        GridBagConstraints gbc_btnProva = new GridBagConstraints();
        gbc_btnProva.fill = GridBagConstraints.HORIZONTAL;
        gbc_btnProva.gridx = 0;
        gbc_btnProva.gridy = 11;
        panel_function.add(btnProva, gbc_btnProva);
        this.listSol.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent arg0) {
                do_listSol_valueChanged(arg0);
            }
        });
        this.panel_1 = new JPanel();
        this.panel_1.setBorder(new TitledBorder(null, "Output", TitledBorder.LEADING, TitledBorder.TOP, null, null));
        GridBagConstraints gbc_panel_1 = new GridBagConstraints();
        gbc_panel_1.insets = new Insets(0, 5, 5, 5);
        gbc_panel_1.fill = GridBagConstraints.BOTH;
        gbc_panel_1.gridx = 0;
        gbc_panel_1.gridy = 1;
        this.panel_controls.add(this.panel_1, gbc_panel_1);
        GridBagLayout gbl_panel_1 = new GridBagLayout();
        gbl_panel_1.columnWidths = new int[] { 0, 0 };
        gbl_panel_1.rowHeights = new int[] { 0, 0, 0 };
        gbl_panel_1.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
        gbl_panel_1.rowWeights = new double[] { 1.0, 1.0, Double.MIN_VALUE };
        this.panel_1.setLayout(gbl_panel_1);
        this.btnSaveBin = new JButton("Save .BIN");
        GridBagConstraints gbc_btnSaveBin = new GridBagConstraints();
        gbc_btnSaveBin.fill = GridBagConstraints.HORIZONTAL;
        gbc_btnSaveBin.insets = new Insets(0, 2, 5, 2);
        gbc_btnSaveBin.gridx = 0;
        gbc_btnSaveBin.gridy = 0;
        this.panel_1.add(this.btnSaveBin, gbc_btnSaveBin);
        this.btnSaveBin.setMinimumSize(new Dimension(100, 28));
        this.btnSaveBin.setPreferredSize(new Dimension(100, 32));
        this.btnSaveBin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                do_btnSaveBin_actionPerformed(arg0);
            }
        });
        this.btnSaveBin.setMargin(new Insets(2, 2, 2, 2));
        this.btnSavePng = new JButton("Save .PNG");
        GridBagConstraints gbc_btnSavePng = new GridBagConstraints();
        gbc_btnSavePng.fill = GridBagConstraints.HORIZONTAL;
        gbc_btnSavePng.insets = new Insets(0, 2, 5, 2);
        gbc_btnSavePng.gridx = 0;
        gbc_btnSavePng.gridy = 1;
        this.panel_1.add(this.btnSavePng, gbc_btnSavePng);
        this.btnSavePng.setMinimumSize(new Dimension(100, 28));
        this.btnSavePng.setPreferredSize(new Dimension(100, 32));
        this.btnSavePng.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                do_btnSavePng_actionPerformed(arg0);
            }
        });
        this.btnSavePng.setMargin(new Insets(2, 2, 2, 2));
        
        panel_3 = new JPanel();
        panel_3.setBorder(new TitledBorder(null, "Identification", TitledBorder.LEADING, TitledBorder.TOP, null, null));
        GridBagConstraints gbc_panel_3 = new GridBagConstraints();
        gbc_panel_3.insets = new Insets(0, 5, 5, 5);
        gbc_panel_3.fill = GridBagConstraints.BOTH;
        gbc_panel_3.gridx = 0;
        gbc_panel_3.gridy = 2;
        panel_controls.add(panel_3, gbc_panel_3);
        GridBagLayout gbl_panel_3 = new GridBagLayout();
        gbl_panel_3.columnWidths = new int[]{0, 0};
        gbl_panel_3.rowHeights = new int[]{0, 0, 0, 0, 0, 0};
        gbl_panel_3.columnWeights = new double[]{1.0, Double.MIN_VALUE};
        gbl_panel_3.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
        panel_3.setLayout(gbl_panel_3);
        
        btnDbdialog = new JButton("Search DB");
        btnDbdialog.setPreferredSize(new Dimension(100, 32));
        btnDbdialog.setMargin(new Insets(2, 2, 2, 2));
        GridBagConstraints gbc_btnDbdialog = new GridBagConstraints();
        gbc_btnDbdialog.insets = new Insets(0, 2, 5, 2);
        gbc_btnDbdialog.fill = GridBagConstraints.HORIZONTAL;
        gbc_btnDbdialog.gridx = 0;
        gbc_btnDbdialog.gridy = 0;
        panel_3.add(btnDbdialog, gbc_btnDbdialog);
        
        separator_1 = new JSeparator();
        GridBagConstraints gbc_separator_1 = new GridBagConstraints();
        gbc_separator_1.fill = GridBagConstraints.HORIZONTAL;
        gbc_separator_1.insets = new Insets(0, 0, 5, 0);
        gbc_separator_1.gridx = 0;
        gbc_separator_1.gridy = 1;
        panel_3.add(separator_1, gbc_separator_1);
        
        chckbxShowRings = new JCheckBox("Custom LAT");
        GridBagConstraints gbc_chckbxShowRings = new GridBagConstraints();
        gbc_chckbxShowRings.anchor = GridBagConstraints.WEST;
        gbc_chckbxShowRings.insets = new Insets(0, 0, 5, 0);
        gbc_chckbxShowRings.gridx = 0;
        gbc_chckbxShowRings.gridy = 2;
        panel_3.add(chckbxShowRings, gbc_chckbxShowRings);
        
        combo_LATdata = new JComboBox();
        combo_LATdata.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent arg0) {
                do_combo_showRings_itemStateChanged(arg0);
            }
        });
        GridBagConstraints gbc_combo_showRings = new GridBagConstraints();
        gbc_combo_showRings.fill = GridBagConstraints.HORIZONTAL;
        gbc_combo_showRings.insets = new Insets(0, 2, 5, 2);
        gbc_combo_showRings.gridx = 0;
        gbc_combo_showRings.gridy = 3;
        panel_3.add(combo_LATdata, gbc_combo_showRings);
        combo_LATdata.setModel(new DefaultComboBoxModel(new String[] {}));
        
        btnAddLat = new JButton("Add LAT");
        btnAddLat.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                do_btnAddLat_actionPerformed(arg0);
            }
        });
        btnAddLat.setPreferredSize(new Dimension(100, 32));
        GridBagConstraints gbc_btnAddLat = new GridBagConstraints();
        gbc_btnAddLat.fill = GridBagConstraints.HORIZONTAL;
        gbc_btnAddLat.insets = new Insets(0, 2, 0, 2);
        gbc_btnAddLat.gridx = 0;
        gbc_btnAddLat.gridy = 4;
        panel_3.add(btnAddLat, gbc_btnAddLat);
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
        
        //si el workdir apunta a un fitxer, s'obrir� automaticament, altrament
        //mantenim el workdir que sera el directori inicial als filechooser
        File f = new File(workdir);
        if(f.exists()){
        	if (f.isFile()){//l'obrim
                this.updatePatt2D(f);
                workdir=f.getAbsolutePath();
                VavaLogger.LOG.info("workdir:"+workdir);
        	}
        }else{
        	workdir = System.getProperty("user.dir");
        }
    }

    protected void do_btn05x_actionPerformed(ActionEvent arg0) {
        if (panelImatge.getScalefit() > 0.10f) {
            panelImatge.setScalefit(panelImatge.getScalefit() * 0.5f);
        }
    }

    protected void do_btn2x_actionPerformed(ActionEvent e) {
        if (panelImatge.getScalefit() < 25.f) {
            panelImatge.setScalefit(panelImatge.getScalefit() * 2.0f);
        }
    }

    protected void do_btnD2Dsub_actionPerformed(ActionEvent e) {
        if (patt2D != null) {
            if (d2DsubWin == null) {
                d2DsubWin = new D2Dsub_frame(this);
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

    protected void do_btnExZones_actionPerformed(ActionEvent arg0) {
        if (patt2D != null) {
            // tanquem calibracio en cas que estigui obert
            if (calibration != null)
                calibration.dispose();
            if (exZones == null) {
                exZones = new ExZones_dialog(this);
            }
            exZones.setVisible(true);
            panelImatge.setExZones(exZones);
        }
    }

    protected void do_btnLab6_actionPerformed(ActionEvent arg0) {
        if (patt2D != null) {
            // tanquem zones excloses en cas que estigui obert
            if (exZones != null)
                exZones.dispose();
            if (calibration == null) {
                calibration = new Calib_dialog(patt2D, this);
            }
            calibration.setVisible(true);
            panelImatge.setCalibration(calibration);
        }
    }

    protected void do_btnMidaReal_actionPerformed(ActionEvent e) {
        panelImatge.setScalefit(1.0f);
    }

    protected void do_btnOpen_actionPerformed(ActionEvent arg0) {

        File d2File;
        JFileChooser fileChooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("2D Data file (bin,img,spr,gfrm,edf)", "bin", "img",
                "spr", "gfrm", "edf");
        fileChooser.addChoosableFileFilter(filter);
        fileChooser.setCurrentDirectory(new File(workdir));
        int selection = fileChooser.showOpenDialog(null);
        if (selection != JFileChooser.APPROVE_OPTION) {
            if (!fileOpened) {
                tAOut.stat("No data file selected");
            }
            return;
        }
        // resetejem
        this.reset();
        d2File = fileChooser.getSelectedFile();
        this.updatePatt2D(d2File);

    }

    protected void do_btnOpenSol_actionPerformed(ActionEvent arg0) {
        File solFile;
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(new File(workdir));
        int selection = fileChooser.showOpenDialog(null);
        if (selection != JFileChooser.APPROVE_OPTION) {
            tAOut.stat("No sol file selected");
            return;
        }
        solFile = fileChooser.getSelectedFile();
        panelImatge.clearSolutions();
        solFile = openSol(solFile);
        if (solFile == null) {
            tAOut.stat("No SOL file opened");
        } else {
            // afegim les solucions a la llista
            DefaultListModel lm = new DefaultListModel();
            // ordenem arraylist
            Collections.sort(panelImatge.getSolucions(),Collections.reverseOrder());
            for (int i = 0; i < panelImatge.getSolucions().size(); i++) {
                lm.addElement((i + 1) + " " + panelImatge.getSolucions().get(i).getNumReflexions() + " "
                        + panelImatge.getSolucions().get(i).getValorFrot());
            }
            this.listSol.setModel(lm);
            this.listSol.setSelectedIndex(0);
        }
    }

    protected void do_btnResetView_actionPerformed(ActionEvent arg0) {
        panelImatge.resetView();
    }

    // 130523: canvi a format bin nou (cap�alera 60)
    protected void do_btnSaveBin_actionPerformed(ActionEvent arg0) {

        //si teniem obert un fitxer img el reobrim per recalcular l'escala en cas que
        //haguem afegit zones excloses
//        if(FileUtils.getExtension(openedFile).equalsIgnoreCase("img")){
//            this.reset();
//            this.updatePatt2D(openedFile);
//        }
        
        //FAREM UN REESCALAT abans de salvar per si les zones excloses ho han modificat
    	if(openedFile!=null){
    		if(patt2D.getScale()>1)ImgFileUtils.rescale(patt2D,openedFile);	
    	}
        
        File d2File = null;
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(new File(workdir));
        int selection = fileChooser.showSaveDialog(null);
        // si s'ha seleccionat un fitxer
        if (selection == JFileChooser.APPROVE_OPTION) {
            d2File = fileChooser.getSelectedFile();
            d2File = ImgFileUtils.saveBIN(d2File, patt2D);
            if (d2File == null) {
                tAOut.stat("Error saving BIN file");
            }
        } else {
            tAOut.stat("no file selected");
        }
        tAOut.stat("File BIN saved: " + d2File.toString());
        // ara hauriem d'obrir el fitxer bin per treballar sobre aquest:
        this.reset();
        this.updatePatt2D(d2File);
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

    // guarda imatge en format png
    protected void do_btnSavePng_actionPerformed(ActionEvent arg0) {
        File imFile = null;
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(new File(workdir));
        int selection = fileChooser.showSaveDialog(null);
        if (selection == JFileChooser.APPROVE_OPTION) {
            imFile = fileChooser.getSelectedFile();
            imFile = ImgFileUtils.savePNG(imFile, panelImatge.getSubimage());
            if (imFile == null) {
                tAOut.stat("Error saving PNG file");
                return;
            }
        } else {
            tAOut.stat("Error saving PNG file");
            return;
        }
        tAOut.stat("File PNG saved: " + imFile.toString());
    }
    
    protected void do_btnSetParams_actionPerformed(ActionEvent arg0) {
        if (patt2D != null) {
            Param_dialog param = new Param_dialog(patt2D);
            param.setVisible(true);
            tAOut.stat("Edited parameters:");
            tAOut.ln(patt2D.getInfo());
            panelImatge.recalcularCercles();
        }
    }

    protected void do_chckbxIndex_itemStateChanged(ItemEvent arg0) {
    	if(chckbxIndex.isSelected()){
    		chckbxWorkSol.setSelected(false);
    	}
        panelImatge.setShowIndexing(chckbxIndex.isSelected());
        this.btnSaveDicvol.setEnabled(chckbxIndex.isSelected());
        VavaLogger.LOG.info("panelImatge.isShowIndexing="+panelImatge.isShowIndexing());
        VavaLogger.LOG.info("panelImatge.isShowHKLIndexing="+panelImatge.isShowHKLIndexing());
    }

    protected void do_chckbxShowHkl_itemStateChanged(ItemEvent arg0) {
        panelImatge.setShowHKLsol(chckbxShowHkl.isSelected());
    }

    protected void do_chckbxShowSol_itemStateChanged(ItemEvent arg0) {
        panelImatge.setShowSolPoints(chckbxShowSol.isSelected());
    }

	protected void do_chckbxWorkSol_itemStateChanged(ItemEvent arg0) {
        if(chckbxWorkSol.isSelected()){
        	chckbxIndex.setSelected(false);
        }
		panelImatge.setShowHKLIndexing(chckbxWorkSol.isSelected());
        this.btnSaveDicvol.setEnabled(chckbxWorkSol.isSelected());
        VavaLogger.LOG.info("panelImatge.isShowIndexing="+panelImatge.isShowIndexing());
        VavaLogger.LOG.info("panelImatge.isShowHKLIndexing="+panelImatge.isShowHKLIndexing());
	}
    
    protected void do_lblhelp_mouseClicked(MouseEvent arg0) {
        if (p2dAbout == null) {
            p2dAbout = new About_dialog();
        }
        p2dAbout.setVisible(true);
    }

    protected void do_listSol_valueChanged(ListSelectionEvent arg0) {
        for (int i = 0; i < panelImatge.getSolucions().size(); i++) {
            panelImatge.getSolucions().get(i).setShowSol(listSol.isSelectedIndex(i));
        }
    }

    public File getOpenedFile() {
        return openedFile;
    }

    public ImagePanel getPanelImatge() {
        return panelImatge;
    }

    public Pattern2D getPatt2D() {
        return patt2D;
    }

    private void inicialitza() {
        this.setSize(1200, 960); //ho centra el metode main
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        if(this.getWidth()>screenSize.width||this.getHeight()>screenSize.height){
            this.setSize(screenSize.width-100,screenSize.height-100);
        }
        tAOut.stat(welcomeMSG);
        FileUtils.setLocale();
//        chckbxIndex.setSelected(true);
//        panelImatge.setShowIndexing(true);
        do_chckbxIndex_itemStateChanged(null);
        
        //inicialitzem array i provem de llegir els fitxers LAT que tenim dins
        customLATs = new ArrayList<LAT_data>();
        int nread = 0;
        for (int i=0; i<LAT_data.getDefaultdatafiles().length; i++){
            String latfile = LAT_data.getDefaultdatafiles()[i];
            String name = FileUtils.getFNameNoExt(latfile);
            LAT_data ld = new LAT_data(name);
            
//            File lfile;
//            try {
//                VavaLogger.LOG.info(LAT_data.getDefaultdatafilespath()+latfile);
//                lfile = new File(ClassLoader.getSystemResource(LAT_data.getDefaultdatafilespath()+latfile).toURI());
                boolean ok = ld.readinternalResourceLATfile(latfile);
                if (ok){
                    combo_LATdata.addItem(name);
                    this.getCustomLATs().add(ld);
                    nread = nread +1;                    
                }else{
                    VavaLogger.LOG.info("error reading HKL for compound "+ld.getName().trim());
                }
//            } catch (URISyntaxException e) {
//                // TODO Auto-generated catch block
//                e.printStackTrace();
//            }
        }
        if (nread > 0){
            LAT_data.setDefaultDataRead(true);
        }
    }

    private File openSol(File solFile) {

        String line;
        boolean endSol = false;

        try {
            Scanner scSolfile = new Scanner(solFile);
            scSolfile.nextLine(); // number of solutions
            OrientSolucio.setNumSolucions(scSolfile.nextInt()); // num de solucions
            scSolfile.nextLine();
            scSolfile.nextLine(); // structure factor calculation
            OrientSolucio.setHasFc(scSolfile.nextInt()); // 0 sense Fc, 1 amb Fc
            scSolfile.nextLine();
            scSolfile.nextLine(); // grain identificator
            OrientSolucio.setGrainIdent(scSolfile.nextInt());
            scSolfile.nextLine();
            scSolfile.nextLine(); // grain nr (cap�alera) comen�a el primer gra

            /*
             * En el cas que grain identificator sigui 0, hi ha #NumSolucions mostrant nom�s la solucio amb major Frot
             * (CENTRE). En cas que sigui 1, hi ha X solucions properes a la del gra seleccionat (indicat per grain
             * identificator). Les solucions estan etiquetades per la cap�alera ORIENT excepte la de major valor de Frot
             * que �s CENTRE.
             */

            if (OrientSolucio.getGrainIdent() == 0) {
                for (int i = 0; i < OrientSolucio.getNumSolucions(); i++) {
                    panelImatge.getSolucions().add(new OrientSolucio(i)); // afegim una solucio
                    int npunts = 0;
                    endSol = false;
                    panelImatge.getSolucions().get(i).setGrainNr(scSolfile.nextInt());
                    line = scSolfile.nextLine();
                    VavaLogger.LOG.info(scSolfile.nextLine());// CENTRE
//                    scSolfile.nextLine();// CENTRE
                    panelImatge.getSolucions().get(i).setNumReflexions(scSolfile.nextInt());
                    line = scSolfile.nextLine();
                    panelImatge.getSolucions().get(i).setValorFrot(Float.parseFloat(line)); // valor funcio rotacio
//                    VavaLogger.LOG.info(scSolfile.nextLine());
                    VavaLogger.LOG.info(panelImatge.getSolucions().get(i).getNumReflexions()+" "+panelImatge.getSolucions().get(i).getValorFrot());
                    VavaLogger.LOG.info(scSolfile.nextLine());// matriu Rot
                    VavaLogger.LOG.info(scSolfile.nextLine());// matriu Rot
                    VavaLogger.LOG.info(scSolfile.nextLine());// matriu Rot
//                    scSolfile.nextLine();// matriu Rot
//                    scSolfile.nextLine();// matriu Rot
//                    scSolfile.nextLine();// matriu Rot
                    // ara comencen les reflexions
                    while (!endSol) {
                        if (!scSolfile.hasNextLine()) {
                            endSol = true;
                            continue;
                        }
                        line = scSolfile.nextLine();
                        VavaLogger.LOG.info("bona "+line);
                        if (line.trim().isEmpty())
                            continue;
                        if (line.trim().startsWith("GRAIN")) {
                            endSol = true;
                            continue;
                        }
                        npunts = npunts + 1;
                        String[] lineS = line.trim().split("\\s+");
                        panelImatge
                                .getSolucions()
                                .get(i)
                                .addSolPoint(Float.parseFloat(lineS[1]), Float.parseFloat(lineS[2]),
                                        Integer.parseInt(lineS[3]), Integer.parseInt(lineS[4]),
                                        Integer.parseInt(lineS[5]), Float.parseFloat(lineS[6]),
                                        Float.parseFloat(lineS[7]));
                    }
                    panelImatge.getSolucions().get(i).setNumReflexions(npunts);
                }

            } else { // cas d'un sol gra

                int grainNr;
                for (int j = 0; j < OrientSolucio.getNumSolucions(); j++) {
                    grainNr = scSolfile.nextInt();
                    line = scSolfile.nextLine();
                    if (grainNr != OrientSolucio.getGrainIdent()) {
                        // no es el gra del que trobem orientacions properes, llegim seguent cap�alera i el saltem
                        if (scSolfile.hasNextLine()) {
                            scSolfile.nextLine();
                        } // cap�alera GRAIN NR.
                        continue;
                    }
                    // es el gra correcte
                    scSolfile.nextLine();// ORIENT o CENTRE (cap�alera)
                    int i = 0;
                    boolean endGrain = false;
                    while (!endGrain) {
                        panelImatge.getSolucions().add(new OrientSolucio(i)); // afegim una solucio
                        panelImatge.getSolucions().get(i).setGrainNr(grainNr);
                        line = scSolfile.nextLine();
                        String[] lineS = line.trim().split("\\s+");
                        // valor funcio rotacio. S'haura de canviar si es passa de int a float en futurs fitxers sol
                        panelImatge.getSolucions().get(i).setValorFrot(Integer.parseInt(lineS[0]));
                        scSolfile.nextLine();// matriu Rot
                        scSolfile.nextLine();// matriu Rot
                        scSolfile.nextLine();// matriu Rot
                        // ara comencen les reflexions
                        endSol = false;
                        int npunts = 0;
                        while (!endSol) {
                            if (!scSolfile.hasNextLine()) {
                                endGrain = true;
                                endSol = true;
                                continue;
                            }
                            line = scSolfile.nextLine();
                            if (line.trim().isEmpty())
                                continue;
                            if (line.trim().startsWith("ORIENT") || line.trim().startsWith("CENTRE")) {
                                endSol = true;
                                continue;
                            }
                            if (line.trim().startsWith("GRAIN")) {
                                endGrain = true;
                                endSol = true;
                                continue;
                            }
                            npunts = npunts + 1;
                            lineS = line.trim().split("\\s+");
                            panelImatge
                                    .getSolucions()
                                    .get(i)
                                    .addSolPoint(Float.parseFloat(lineS[1]), Float.parseFloat(lineS[2]),
                                            Integer.parseInt(lineS[3]), Integer.parseInt(lineS[4]),
                                            Integer.parseInt(lineS[5]), Float.parseFloat(lineS[6]),
                                            Float.parseFloat(lineS[7]));
                        }
                        panelImatge.getSolucions().get(i).setNumReflexions(npunts);
                        i++;
                    }
                }
            }
            scSolfile.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return solFile;
    }

    private void reset() {
        this.fileOpened = false;
        this.panelImatge.setImagePatt2D(null);
        this.panelImatge.getSlider_contrast().setMinimum(0);
        this.panelImatge.getSlider_contrast().setValue(0);
        this.patt2D = null;
        if (this.d2DsubWin != null) {
            this.d2DsubWin.dispose();
            this.d2DsubWin = null;
        }
        if (this.calibration != null) {
            this.calibration.dispose();
            this.calibration = null;
        }
        if (this.exZones != null) {
            this.exZones.dispose();
            this.exZones = null;
        }
    }

    public void setOpenedFile(File openedFile) {
        this.openedFile = openedFile;
    }

    public void updatePatt2D(File d2File) {
        workdir = d2File.getPath().substring(0, d2File.getPath().length() - d2File.getName().length());
        VavaLogger.LOG.info("workdir="+workdir);
        patt2D = ImgFileUtils.openPatternFile(d2File);
        
        if (patt2D != null) {
            panelImatge.setImagePatt2D(patt2D);
            lblOpened.setText(d2File.toString());
            tAOut.stat("File opened: " + d2File.getName() + ", " + patt2D.getPixCount() + " pixels ("
                    + (int) patt2D.getMillis() + " ms)");
            if(patt2D.oldBIN)tAOut.ln("*** old BIN format detected and considered ***");
            tAOut.ln(patt2D.getInfo());
            fileOpened = true;
            openedFile = d2File;
        } else {
            tAOut.stat("Error reading 2D file");
            tAOut.stat("No file opened");
            fileOpened = false;
            openedFile = null;
        }
    }
    
    public void updatePatt2D(Pattern2D p2D) {
      this.patt2D = p2D;
      if (patt2D != null) {
          panelImatge.setImagePatt2D(patt2D);
          lblOpened.setText("");
          tAOut.stat("Opened pattern (not from a file, consider saving)");
          tAOut.ln(patt2D.getInfo());
          fileOpened = true;
          openedFile = null;
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
      } else {
          tAOut.stat("Error reading 2D file");
          tAOut.stat("No file opened");
          fileOpened = false;
          openedFile = null;
      }
    }
	public LogJTextArea gettAOut() {
		return tAOut;
	}
	protected void do_btn_yarc_actionPerformed(ActionEvent e) {
		DebugYarc dya = new DebugYarc(this.getPatt2D());
		dya.setVisible(true);
	}
	protected void do_btnRadialInteg_actionPerformed(ActionEvent arg0) {
		if(patt2D != null){
			if(this.irWin==null){
				irWin = new IntegracioRadial(this.getPatt2D());
			}
			irWin.setVisible(true);	
		}
	}
	protected void do_btnProva_actionPerformed(ActionEvent arg0) {
        Iterator<PuntCercle> itrP = panelImatge.getPuntsCercles().iterator();
        int size = panelImatge.getPuntsCercles().size();
        double[] x = new double[size];
        double[] y = new double[size];
        int i=0;
        while (itrP.hasNext()) {
            PuntCercle pa = itrP.next();
            x[i] = pa.getX();
            y[i] = pa.getY();
            i++;
        }
		Ellipse e = new Ellipse(x,y);
		VavaLogger.LOG.info("Angle: "+e.getAngle());
		VavaLogger.LOG.info("centX: "+e.getCenter().getX());
		VavaLogger.LOG.info("centY: "+e.getCenter().getY());
		VavaLogger.LOG.info("Major: "+e.getMajor());
		VavaLogger.LOG.info("Minor: "+e.getMinor());
	}
	
	protected void do_btnSumImages_actionPerformed(ActionEvent arg0) {
        // Creem un filechooser per seleccionar el fitxer obert
        JFileChooser fileChooser = new JFileChooser();
        File startDir = new File(MainFrame.getWorkdir());
        fileChooser.setCurrentDirectory(startDir); // directori inicial: el del
        fileChooser.setMultiSelectionEnabled(true);
    	FileNameExtensionFilter[] filter = {new FileNameExtensionFilter("2D Data file (bin,img,spr,gfrm,edf)", "bin", "img",
                "spr", "gfrm", "edf")};
        for (int i = 0; i < filter.length; i++) {
            fileChooser.addChoosableFileFilter(filter[i]);
        }
        // si s'ha seleccionat un fitxer
        File[] flist;
        if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            flist =  fileChooser.getSelectedFiles();
        } else {
            return;
        }
        Pattern2D suma = ImgOps.sumImages(flist);
        this.updatePatt2D(suma);
	}
	
	protected void do_btnWorkSol_actionPerformed(ActionEvent arg0) {
		if(listSol.getModel().getSize()<=0){
			return;
		}
		OrientSolucio os = panelImatge.getSolucions().get(listSol.getSelectedIndex());
		WorkSOL_dialog wsol = new WorkSOL_dialog(this.getPatt2D(),os,(String)listSol.getSelectedValue());
		wsol.setVisible(true);
	}
    protected void do_btnNext_actionPerformed(ActionEvent arg0) {
        
        String fnameCurrent = FileUtils.getFNameNoExt(patt2D.getImgfile());
        String fextCurrent = FileUtils.getExtension(patt2D.getImgfile());
        VavaLogger.LOG.info("fnameCurrent (no Ext): "+fnameCurrent);
        VavaLogger.LOG.info("fextCurrent: "+fextCurrent);
        //agafem ultims 4 digits (index), sumem 1 i el tornem a posar com a fitxer a veure què
        VavaLogger.LOG.info("substring "+fnameCurrent.substring(fnameCurrent.length()-4, fnameCurrent.length()));
        int imgNum = Integer.parseInt(fnameCurrent.substring(fnameCurrent.length()-4, fnameCurrent.length()));
        imgNum = imgNum+1;
        String fnameExtNew = fnameCurrent.substring(0, fnameCurrent.length()-4)+String.format("%04d", imgNum)+"."+fextCurrent;
        File d2File = new File(fnameExtNew);
        if (d2File.exists()){
            this.reset();
            this.updatePatt2D(d2File);
        }else{
            tAOut.stat("No file found with fname "+fnameExtNew);
        }
        
    }
    protected void do_btnPrev_actionPerformed(ActionEvent e) {
        String fnameCurrent = FileUtils.getFNameNoExt(patt2D.getImgfile());
        String fextCurrent = FileUtils.getExtension(patt2D.getImgfile());
        VavaLogger.LOG.info("fnameCurrent (no Ext): "+fnameCurrent);
        VavaLogger.LOG.info("fextCurrent: "+fextCurrent);
        //agafem ultims 4 digits (index), sumem 1 i el tornem a posar com a fitxer a veure què
        VavaLogger.LOG.info("substring "+fnameCurrent.substring(fnameCurrent.length()-4, fnameCurrent.length()));
        int imgNum = Integer.parseInt(fnameCurrent.substring(fnameCurrent.length()-4, fnameCurrent.length()));
        imgNum = imgNum-1;
        String fnameExtNew = fnameCurrent.substring(0, fnameCurrent.length()-4)+String.format("%04d", imgNum)+"."+fextCurrent;
        File d2File = new File(fnameExtNew);
        if (d2File.exists()){
            this.reset();
            this.updatePatt2D(d2File);
        }else{
            tAOut.stat("No file found with fname "+fnameExtNew);
        }
    }

    protected void do_btnDbdialog_actionPerformed(ActionEvent arg0) {
//        if (patt2D != null) {
            // tanquem calibracio en cas que estigui obert
            if (calibration != null) calibration.dispose();
            if (exZones != null) exZones.dispose();

            if (dbDialog == null) {
                dbDialog = new database_dialog(this);
            }
            dbDialog.setVisible(true);
            panelImatge.setDBdialog(dbDialog);
//        }
    }

    protected void do_chckbxShowRings_itemStateChanged(ItemEvent arg0) {
        if (combo_LATdata.getItemCount()>0){
            VavaLogger.LOG.info("do_chckbxShowRings_itemStateChanged CALLED");
            panelImatge.setShowRings(chckbxShowRings.isSelected(), this.getShowRingCompound());
        }

    }
    public LAT_data getShowRingCompound(){
        VavaLogger.LOG.info("getShowRingCompound CALLED");
        String name = this.combo_LATdata.getSelectedItem().toString();
        Iterator<LAT_data> it = this.getCustomLATs().iterator();
        while (it.hasNext()){
            LAT_data ld = it.next();
            VavaLogger.LOG.info("ldgetname= "+ld.getName()+" comboname="+name);
            if (ld.getName().equalsIgnoreCase(name)){
                VavaLogger.LOG.info("getShowRingCompound RETURNED A COMPOUND");
                return ld;
            }
        }
        VavaLogger.LOG.info("getShowRingCompound RETURNED NULL");
        return null;
//        if (combo_LATdata.getItemCount()>0){
//            return this.combo_LATdata.getSelectedItem();
//        }
        
    }

    public ArrayList<LAT_data> getCustomLATs() {
        return customLATs;
    }   
    
    protected void do_btnAddLat_actionPerformed(ActionEvent arg0) {
        //TODO:lectura fitxer lat i addició al combobox
//        File d2File;
        JFileChooser fileChooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("LAT file", "lat", "LAT");
        fileChooser.addChoosableFileFilter(filter);
        fileChooser.setCurrentDirectory(new File(workdir));
        int selection = fileChooser.showOpenDialog(null);
        if (selection != JFileChooser.APPROVE_OPTION) {
            if (!fileOpened) {
                tAOut.stat("No LAT file selected");
            }
            return;
        }
        // llegim el LAT
        File latf = fileChooser.getSelectedFile();
        LAT_data ld = new LAT_data(FileUtils.getFNameNoExt(latf));
        boolean ok = ld.readLATfile(latf);
        if (ok){
            combo_LATdata.addItem(FileUtils.getFNameNoExt(latf));
        }
        
    }

    protected void do_combo_showRings_itemStateChanged(ItemEvent arg0) {
        panelImatge.setShowRings(chckbxShowRings.isSelected(), this.getShowRingCompound());
    }
}