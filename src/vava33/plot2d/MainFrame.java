package vava33.plot2d;

/**    
 * // TODO: es podrien unificar les classes Punt...
 *    ATENCIO!! COMPROVAR A FILEUTILS FILENAMENOEXT QUE ES MENJA UNA LLETRA DE MES, HO HE CANVIAT JA A -1 (130723) pero el
 *    proper cop comprovar-ho
 *    
 * 140127
 *  - Contrast   
 *  - Canvi a recalcScale a pattern2D que dividia per l'escala quan havia de multiplicar
 *    
 * 131008
 *  - Ordenem arraylist orientSolucio segons Fsum abans d'omplir la llista
 *  
 * 130923
 *  - Cal reestructurar tot lo de les zones excloses per acceptar trapezoides.
 *  - CurrentRect es seguirà fent servir per Calibració, però generaré un de nou que sigui CurrentShape que serà el que
 *    treballarà amb ExZones i tot es definirà amb Shape.Polygon. Aleshores també cal canviar la crida a editquadrat per
 *    la crida a una nova subrutina editpoligon quan s'esta definint les zones excloses i es clica.
 *  - Save BIN ara també considera Y0toMask (afegit a patt2D el Y0toMask option)
 *    
 * 130918
 *  - Obertura fitxers EDF (ALBA)
 *  - ImagePanel, prova de flexibilitzar contrast (posant dinamics max, min)
 *  
 * 130611
 *  - Adaptacio D2Dsub nova versio amb més opcions
 *  - Canvi noms opcions D2Dsub
 * 
 * 130604
 *  - TREC el CODI de D2Dsub
 *  - S'ha fet que abans de guardar el BIN es faci un REESCALAT rellegint si cal les intensitats originals per tal d'aplicar
 *    les possibles zones excloses que s'han afegit
 *  - Poso a FileUtils els DecimalFormats i el locale
 *  - Faig que no apliqui l'escala al mostrar la intensitat
 *  - Faig que consideri les intensitats ZERO pel calcul de minI
 *  - Canvi color lletra consola a groc
 *  - Obertura fitxers bin antics? -> deteccio automatica segons num de bytes (capçalera de 20 (old) vs 60 (new) bytes)
 * 
 * 130601 ** AQUESTA ÉS LA VERSIÓ CONSIDERADA "ACABADA" PER 1306 **
 *  - Hem fet que el fortran escrigui molts !! per omplir el buffer de consola i així poder mostrar els missatges que volem
 *    i QUAN volem per pantalla. Simplement ignorarem les linies que comencin per !!!!! (a veure si funciona)
 *  - TextArea output amb boto dret.
 *  - Neteja general i preparacio per distribuïr.
 *  
 * 130530
 * - Escrits els HELP dels diferents dialogs.
 * - Reset IMATGE complert
 * - Al tancar finestres es deseleccionen checkboxes que afecten el que es mostra a la imatge (calibracio i exZ son exclusius)
 * - SaveBin contempla zones excloses (es guarden a pattern2d) i s'ha passat a FileUtils (igual que savePNG)
 * - Acabada la implementacio de zones excloses (writeEXZ,etc..)
 * - Determinacio de les zones excloses passa a ser responsabilitat del programa principal. 
 * 
 * 130529 
 * - inici implemetnacio d2DExZones 
 * - Centratge imatge al panell amb reset view
 * - Neteja fitxers i opcio clean up.
 * 
 * 130528-2 
 * - Petites correccions de varis errors de nullpointers, access directe a camps privats, etc...
 * - Redissenyat el d2Dsub, ara tot es fa a la mateixa finestra. Ja funciona tot menys zones excloses.
 * - Classe fileUtils, amb operacions fitxers i LECTURA FITXERS DADES
 * - Canvi filosofia: Tota la responsabilitat de representacio (mouse, etc..) a Imagepanel (per reaprofitar-la a altres llocs)
 * 
 * 130528
 * - Execucio del d2dsub des  de D2Dsub_dialog
 * - Canvi workdir a string i afegides variables static globals per execucions
 * - Creacio classe output text area (JtxtAreaOut)
 * 
 * Changes (130523):
 * - Finestra FONS
 * - CANVI FORMAT BIN ACTUAL (capçalera 60) per open i save. 
 * - Del IMG ara es llegeixen tots els parametres.
 * 
 * Changes (130517-21):
 * - Intentat aproximar millor els pixels en pantalla
 * - Calibració amb el·lipse dibuixada i ara es poden triar quins anells s'utilitzen per calibrar
 * 
 * Changes (130515):
 * - UI amb parts resizables (Splitpanes)
 * - LaB6 calibració centre, distOD i Lambda?.
 * 
 * Changes (130514):
 * - Classe Pattern2D, canvi a tot arreu.
 *  
 * Changes (130510,13,14):
 * - Format sol, 1a linia NPEAKS FSUMVAL
 * - Canvi funcions botó esquerra i central
 * - SetParams -> nova finestra amb tots els parameters
 * - Canvi centrX,centrY a float
 * - Reacalcul dels PuntCercles si es canvia el cercle
 * - Llista pics a dialog apart
 * - labels HKL a les reflexions solucio (showHKLsol)
 * - Passat tot a pixels fraccionaris (getPixel, getFrameFromPixel...)
 * - Obrir fitxers de bruker GADDS .gfrm
 * 
 * Changes (130318):
 * - obre el format SOL nou (shape and all)
 * - Moguda la comprovacio de parametres entrats dins la subrutina d'entrada de parametres instrumentals
 * - tret el color gris fosc de la representacio de solucions
 * 
 * Old changes:
 * - CANVI FILOSOFIA GENERAL DE REPRESENTACIO I PER APROXIMAR MILLOR LES COORDENADES... ara es
 *   pot moure i ampliar la imatge millor, es representa més lliure.
 * - NETEJA general, tretes les opcions d'utilitzar Enters per guardar la imatge o de fer servir capçalera antiga
 *   si es vol, fer servir una version anterior (130219-2)
 * - Opcio d'entrar informacio per mostrar 2T a sota
 * - Valors defecte JSlide contrast (aplicat factorContrast)
 * - Creacio classe OrientSolucio i reestructuracio de tota la representacio de solucions de forma
 *   que es poden obrir mes d'una alhora.
 * - Zoom amb botó dret del mouse i movent.
 * - Creacio classe puntCercle i canvi dels mètodes de ImagePanel que l'afecten
 * - recorda el directori (i se li pot passar com a paràmetre l'inicial)
 * - Opcio de capçalera amb integer*4
 * - Considerem valors de zero pel minI
 */

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
import java.util.Collections;
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

import vava33.plot2d.auxi.FileUtils;
import vava33.plot2d.auxi.JtxtAreaOut;
import vava33.plot2d.auxi.OrientSolucio;
import vava33.plot2d.auxi.Pattern2D;

public class MainFrame extends JFrame {
    private static final long serialVersionUID = 4368250280987133953L;
    private static String separator = System.getProperty("file.separator");
    private static String binDir = System.getProperty("user.dir") + separator + "bin" + separator;
    private static String d2dsubExec = "d2Dsub";
    private static String welcomeMSG = "d2Dplot v1309 (131126) by OV";
    private static String workdir = "C:\\Ori_TMP\\proves_bin_scale\\";
//    private static String workdir = "C:\\ovallcorba\\Dades_difraccio\\2D-INCO\\Mesures-Juliol-ALBA\\";
    
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
    private Calib_dialog calibration;
    private ExZones_dialog exZones;
    private About_dialog p2dAbout;
    private File openedFile;
    boolean fileOpened = false;
    private ImagePanel panelImatge;
    private Pattern2D patt2D;
    private Pklist_dialog pkListWin;
    private JtxtAreaOut tAOut;

    public static String getBinDir() {return binDir;}
    public static String getD2dsubExec() {return d2dsubExec;}
    public static String getSeparator() {return separator;}
    public static String getWorkdir() {return workdir;}
    public static final int shortsize = 32767;

    /**
     * Launch the application. ES POT PASSAR COM A ARGUMENT EL DIRECTORI DE TREBALL ON S'OBRIRAN PER DEFECTE ELS DIALEGS
     * 
     */
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            // UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName()); //java metal
        } catch (Throwable e) {
            e.printStackTrace();
        }

        StringBuilder path = new StringBuilder();
        for (int i = 0; i < args.length; i++) {
            path.append(args[i]).append(" ");
        }
        if (args.length > 0)
            workdir = path.toString();

        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
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
        setBounds(100, 100, 877, 614);
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
        this.scrollPane.setBorder(null);
        GridBagConstraints gbc_scrollPane = new GridBagConstraints();
        gbc_scrollPane.insets = new Insets(5, 5, 5, 5);
        gbc_scrollPane.fill = GridBagConstraints.BOTH;
        gbc_scrollPane.gridx = 0;
        gbc_scrollPane.gridy = 0;
        this.panel_stat.add(this.scrollPane, gbc_scrollPane);
        this.tAOut = new JtxtAreaOut();
        this.tAOut.setTabSize(4);
        this.tAOut.setWrapStyleWord(true);
        this.tAOut.setLineWrap(true);
        this.tAOut.setMaximumSize(new Dimension(32767, 32767));
        this.tAOut.setRows(1);
        this.tAOut.setBackground(Color.BLACK);
        this.tAOut.setBorder(null);
        this.scrollPane.setViewportView(this.tAOut);
        this.panel_all = new JPanel();
        this.splitPane.setLeftComponent(this.panel_all);
        GridBagLayout gbl_panel_all = new GridBagLayout();
        gbl_panel_all.columnWidths = new int[] { 0, 0, 0, 0 };
        gbl_panel_all.rowHeights = new int[] { 0, 0, 0, 0 };
        gbl_panel_all.columnWeights = new double[] { 0.0, 1.0, 0.0, Double.MIN_VALUE };
        gbl_panel_all.rowWeights = new double[] { 0.0, 1.0, 0.0, Double.MIN_VALUE };
        this.panel_all.setLayout(gbl_panel_all);

        this.btnOpen = new JButton("Open Image");
        GridBagConstraints gbc_btnOpen = new GridBagConstraints();
        gbc_btnOpen.insets = new Insets(5, 5, 5, 5);
        gbc_btnOpen.gridx = 0;
        gbc_btnOpen.gridy = 0;
        this.panel_all.add(this.btnOpen, gbc_btnOpen);
        this.btnOpen.setMargin(new Insets(2, 7, 2, 7));
        this.lblOpened = new JLabel("(no image opened)");
        GridBagConstraints gbc_lblOpened = new GridBagConstraints();
        gbc_lblOpened.anchor = GridBagConstraints.WEST;
        gbc_lblOpened.insets = new Insets(5, 5, 5, 5);
        gbc_lblOpened.gridx = 1;
        gbc_lblOpened.gridy = 0;
        this.panel_all.add(this.lblOpened, gbc_lblOpened);
        this.lblhelp = new JLabel(" ? ");
        GridBagConstraints gbc_lblhelp = new GridBagConstraints();
        gbc_lblhelp.insets = new Insets(0, 0, 5, 0);
        gbc_lblhelp.gridx = 2;
        gbc_lblhelp.gridy = 0;
        this.panel_all.add(this.lblhelp, gbc_lblhelp);
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
        gbc_splitPane_1.gridwidth = 3;
        gbc_splitPane_1.fill = GridBagConstraints.BOTH;
        gbc_splitPane_1.gridheight = 2;
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
        gbl_panel_opcions.rowHeights = new int[] { 0, 0, 0, 0, 0, 0, 0, 0 };
        gbl_panel_opcions.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
        gbl_panel_opcions.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };
        this.panel_opcions.setLayout(gbl_panel_opcions);
        this.btnResetView = new JButton("Reset view");
        this.btnResetView.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                do_btnResetView_actionPerformed(arg0);
            }
        });
        this.btnResetView.setMargin(new Insets(2, 7, 2, 7));
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
        this.btnMidaReal.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                do_btnMidaReal_actionPerformed(e);
            }
        });
        this.btnMidaReal.setMargin(new Insets(2, 7, 2, 7));
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
        this.btn2x.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                do_btn2x_actionPerformed(e);
            }
        });
        this.btn05x = new JButton("0.5x");
        this.btn05x.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                do_btn05x_actionPerformed(arg0);
            }
        });
        this.btn05x.setMargin(new Insets(1, 2, 1, 2));
        GridBagConstraints gbc_btn05x = new GridBagConstraints();
        gbc_btn05x.insets = new Insets(0, 2, 0, 5);
        gbc_btn05x.gridx = 0;
        gbc_btn05x.gridy = 0;
        this.panel.add(this.btn05x, gbc_btn05x);
        this.btn2x.setMargin(new Insets(1, 7, 1, 7));
        GridBagConstraints gbc_btn2x = new GridBagConstraints();
        gbc_btn2x.insets = new Insets(0, 2, 0, 2);
        gbc_btn2x.gridx = 1;
        gbc_btn2x.gridy = 0;
        this.panel.add(this.btn2x, gbc_btn2x);
        this.chckbxShowSol.setSelected(true);
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
        GridBagConstraints gbc_chckbxIndex = new GridBagConstraints();
        gbc_chckbxIndex.anchor = GridBagConstraints.WEST;
        gbc_chckbxIndex.insets = new Insets(0, 2, 5, 0);
        gbc_chckbxIndex.gridx = 0;
        gbc_chckbxIndex.gridy = 5;
        this.panel_opcions.add(this.chckbxIndex, gbc_chckbxIndex);
        this.btnSaveDicvol = new JButton("Points List");
        GridBagConstraints gbc_btnSaveDicvol = new GridBagConstraints();
        gbc_btnSaveDicvol.insets = new Insets(0, 2, 5, 2);
        gbc_btnSaveDicvol.gridx = 0;
        gbc_btnSaveDicvol.gridy = 6;
        this.panel_opcions.add(this.btnSaveDicvol, gbc_btnSaveDicvol);
        this.btnSaveDicvol.setMinimumSize(new Dimension(100, 25));
        this.btnSaveDicvol.setPreferredSize(new Dimension(101, 25));
        this.btnSaveDicvol.setEnabled(false);
        this.btnSaveDicvol.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                do_btnSaveDicvol_actionPerformed(arg0);
            }
        });
        this.btnSaveDicvol.setMargin(new Insets(2, 7, 2, 7));
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
        gbc_panel_function.insets = new Insets(0, 0, 5, 0);
        gbc_panel_function.gridx = 1;
        gbc_panel_function.gridy = 0;
        this.panel_controls.add(this.panel_function, gbc_panel_function);
        this.panel_function.setBorder(new TitledBorder(null, "Options", TitledBorder.LEADING, TitledBorder.TOP, null,
                null));
        GridBagLayout gbl_panel_function = new GridBagLayout();
        gbl_panel_function.columnWidths = new int[] { 0, 0 };
        gbl_panel_function.rowHeights = new int[] { 0, 0, 0, 0, 0, 0, 0, 0, 0 };
        gbl_panel_function.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
        gbl_panel_function.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, Double.MIN_VALUE };
        this.panel_function.setLayout(gbl_panel_function);
        this.btnSetParams = new JButton("Set Parameters");
        this.btnSetParams.setMinimumSize(new Dimension(100, 25));
        this.btnSetParams.setPreferredSize(new Dimension(101, 25));
        this.btnSetParams.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                do_btnSetParams_actionPerformed(arg0);
            }
        });
        this.btnSetParams.setMargin(new Insets(2, 2, 2, 2));
        GridBagConstraints gbc_btnSetParams = new GridBagConstraints();
        gbc_btnSetParams.insets = new Insets(0, 2, 5, 2);
        gbc_btnSetParams.gridx = 0;
        gbc_btnSetParams.gridy = 0;
        this.panel_function.add(this.btnSetParams, gbc_btnSetParams);
        this.btnLab6 = new JButton("Calibr. LaB6");
        this.btnLab6.setMinimumSize(new Dimension(100, 25));
        this.btnLab6.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                do_btnLab6_actionPerformed(arg0);
            }
        });
        GridBagConstraints gbc_btnLab6 = new GridBagConstraints();
        gbc_btnLab6.insets = new Insets(0, 0, 5, 0);
        gbc_btnLab6.gridx = 0;
        gbc_btnLab6.gridy = 1;
        this.panel_function.add(this.btnLab6, gbc_btnLab6);
        this.btnD2Dsub = new JButton("d2Dsub");
        this.btnD2Dsub.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                do_btnD2Dsub_actionPerformed(e);
            }
        });
        this.btnExZones = new JButton("Ex. Zones");
        this.btnExZones.setMinimumSize(new Dimension(100, 25));
        this.btnExZones.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                do_btnExZones_actionPerformed(arg0);
            }
        });
        this.btnExZones.setPreferredSize(new Dimension(101, 25));
        GridBagConstraints gbc_btnExZones = new GridBagConstraints();
        gbc_btnExZones.insets = new Insets(0, 0, 5, 0);
        gbc_btnExZones.gridx = 0;
        gbc_btnExZones.gridy = 2;
        this.panel_function.add(this.btnExZones, gbc_btnExZones);
        this.btnD2Dsub.setMinimumSize(new Dimension(100, 25));
        this.btnD2Dsub.setPreferredSize(new Dimension(101, 25));
        GridBagConstraints gbc_btnD2Dsub = new GridBagConstraints();
        gbc_btnD2Dsub.insets = new Insets(0, 0, 5, 0);
        gbc_btnD2Dsub.gridx = 0;
        gbc_btnD2Dsub.gridy = 3;
        this.panel_function.add(this.btnD2Dsub, gbc_btnD2Dsub);
        this.btnDdpeaksearch = new JButton("d2Dpksearch");
        this.btnDdpeaksearch.setPreferredSize(new Dimension(101, 25));
        this.btnDdpeaksearch.setEnabled(false);
        this.btnDdpeaksearch.setMargin(new Insets(2, 12, 2, 12));
        GridBagConstraints gbc_btnDdpeaksearch = new GridBagConstraints();
        gbc_btnDdpeaksearch.insets = new Insets(0, 0, 5, 0);
        gbc_btnDdpeaksearch.gridx = 0;
        gbc_btnDdpeaksearch.gridy = 4;
        this.panel_function.add(this.btnDdpeaksearch, gbc_btnDdpeaksearch);
        this.btnOpenSol = new JButton("Open .SOL");
        this.btnOpenSol.setMinimumSize(new Dimension(100, 25));
        this.btnOpenSol.setPreferredSize(new Dimension(101, 25));
        GridBagConstraints gbc_btnOpenSol = new GridBagConstraints();
        gbc_btnOpenSol.insets = new Insets(0, 2, 5, 2);
        gbc_btnOpenSol.gridx = 0;
        gbc_btnOpenSol.gridy = 5;
        this.panel_function.add(this.btnOpenSol, gbc_btnOpenSol);
        this.btnOpenSol.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                do_btnOpenSol_actionPerformed(arg0);
            }
        });
        this.btnOpenSol.setMargin(new Insets(2, 7, 2, 7));
        this.scrollPane_1 = new JScrollPane();
        this.scrollPane_1.setPreferredSize(new Dimension(112, 80));
        GridBagConstraints gbc_scrollPane_1 = new GridBagConstraints();
        gbc_scrollPane_1.insets = new Insets(0, 0, 5, 0);
        gbc_scrollPane_1.fill = GridBagConstraints.BOTH;
        gbc_scrollPane_1.gridx = 0;
        gbc_scrollPane_1.gridy = 6;
        this.panel_function.add(this.scrollPane_1, gbc_scrollPane_1);
        this.listSol = new JList();
        this.listSol.setVisibleRowCount(5);
        this.scrollPane_1.setViewportView(this.listSol);
        this.btnD2Dint = new JButton("d2Dint");
        this.btnD2Dint.setPreferredSize(new Dimension(101, 25));
        this.btnD2Dint.setEnabled(false);
        GridBagConstraints gbc_btnD2Dint = new GridBagConstraints();
        gbc_btnD2Dint.gridx = 0;
        gbc_btnD2Dint.gridy = 7;
        this.panel_function.add(this.btnD2Dint, gbc_btnD2Dint);
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
        gbl_panel_1.columnWeights = new double[] { 0.0, Double.MIN_VALUE };
        gbl_panel_1.rowWeights = new double[] { 0.0, 0.0, Double.MIN_VALUE };
        this.panel_1.setLayout(gbl_panel_1);
        this.btnSaveBin = new JButton("Save .BIN");
        GridBagConstraints gbc_btnSaveBin = new GridBagConstraints();
        gbc_btnSaveBin.insets = new Insets(0, 2, 5, 2);
        gbc_btnSaveBin.gridx = 0;
        gbc_btnSaveBin.gridy = 0;
        this.panel_1.add(this.btnSaveBin, gbc_btnSaveBin);
        this.btnSaveBin.setMinimumSize(new Dimension(100, 25));
        this.btnSaveBin.setPreferredSize(new Dimension(101, 25));
        this.btnSaveBin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                do_btnSaveBin_actionPerformed(arg0);
            }
        });
        this.btnSaveBin.setMargin(new Insets(2, 7, 2, 7));
        this.btnSavePng = new JButton("Save .PNG");
        GridBagConstraints gbc_btnSavePng = new GridBagConstraints();
        gbc_btnSavePng.insets = new Insets(0, 2, 5, 2);
        gbc_btnSavePng.gridx = 0;
        gbc_btnSavePng.gridy = 1;
        this.panel_1.add(this.btnSavePng, gbc_btnSavePng);
        this.btnSavePng.setMinimumSize(new Dimension(100, 25));
        this.btnSavePng.setPreferredSize(new Dimension(101, 25));
        this.btnSavePng.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                do_btnSavePng_actionPerformed(arg0);
            }
        });
        this.btnSavePng.setMargin(new Insets(2, 7, 2, 7));
        this.btnOpen.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                do_btnOpen_actionPerformed(arg0);
            }
        });
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

    // 130523: canvi a format bin nou (capçalera 60)
    protected void do_btnSaveBin_actionPerformed(ActionEvent arg0) {

        //si teniem obert un fitxer img el reobrim per recalcular l'escala en cas que
        //haguem afegit zones excloses
//        if(FileUtils.getExtension(openedFile).equalsIgnoreCase("img")){
//            this.reset();
//            this.updatePatt2D(openedFile);
//        }
        
        //FAREM UN REESCALAT abans de salvar per si les zones excloses ho han modificat
        if(patt2D.getScale()>1)FileUtils.rescale(patt2D,openedFile);
        
        File d2File = null;
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(new File(workdir));
        int selection = fileChooser.showSaveDialog(null);
        // si s'ha seleccionat un fitxer
        if (selection == JFileChooser.APPROVE_OPTION) {
            d2File = fileChooser.getSelectedFile();
            d2File = FileUtils.saveBIN(d2File, patt2D);
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
            imFile = FileUtils.savePNG(imFile, panelImatge.getSubimage());
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
        panelImatge.setShowIndexing(chckbxIndex.isSelected());
        this.btnSaveDicvol.setEnabled(chckbxIndex.isSelected());
    }

    protected void do_chckbxShowHkl_itemStateChanged(ItemEvent arg0) {
        panelImatge.setShowHKLsol(chckbxShowHkl.isSelected());
    }

    protected void do_chckbxShowSol_itemStateChanged(ItemEvent arg0) {
        panelImatge.setShowSolPoints(chckbxShowSol.isSelected());
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
            scSolfile.nextLine(); // grain nr (capçalera) comença el primer gra

            /*
             * En el cas que grain identificator sigui 0, hi ha #NumSolucions mostrant només la solucio amb major Frot
             * (CENTRE). En cas que sigui 1, hi ha X solucions properes a la del gra seleccionat (indicat per grain
             * identificator). Les solucions estan etiquetades per la capçalera ORIENT excepte la de major valor de Frot
             * que és CENTRE.
             */

            if (OrientSolucio.getGrainIdent() == 0) {
                for (int i = 0; i < OrientSolucio.getNumSolucions(); i++) {
                    panelImatge.getSolucions().add(new OrientSolucio(i)); // afegim una solucio
                    int npunts = 0;
                    endSol = false;
                    panelImatge.getSolucions().get(i).setGrainNr(scSolfile.nextInt());
                    line = scSolfile.nextLine();
//                    System.out.println(scSolfile.nextLine());// CENTRE
                    scSolfile.nextLine();// CENTRE
                    panelImatge.getSolucions().get(i).setNumReflexions(scSolfile.nextInt());
                    line = scSolfile.nextLine();
                    panelImatge.getSolucions().get(i).setValorFrot(Float.parseFloat(line)); // valor funcio rotacio
//                    System.out.println(scSolfile.nextLine());
//                    System.out.println(scSolfile.nextLine());// matriu Rot
//                    System.out.println(scSolfile.nextLine());// matriu Rot
//                    System.out.println(scSolfile.nextLine());// matriu Rot
                    scSolfile.nextLine();// matriu Rot
                    scSolfile.nextLine();// matriu Rot
                    scSolfile.nextLine();// matriu Rot
                    // ara comencen les reflexions
                    while (!endSol) {
                        if (!scSolfile.hasNextLine()) {
                            endSol = true;
                            continue;
                        }
                        line = scSolfile.nextLine();
                        System.out.println("bona "+line);
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
                        // no es el gra del que trobem orientacions properes, llegim seguent capçalera i el saltem
                        if (scSolfile.hasNextLine()) {
                            scSolfile.nextLine();
                        } // capçalera GRAIN NR.
                        continue;
                    }
                    // es el gra correcte
                    scSolfile.nextLine();// ORIENT o CENTRE (capçalera)
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
//        System.out.println(workdir);
        patt2D = FileUtils.openPatternFile(d2File);
        
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
}