// TODO: Inicialment cal comprovar si existeix fitxer EXZ i llegir-lo.
// si no existeix, cada cop que s'executi d2dsub fer que es comprovi si existeix fitxer EXZ 
// i sin� crear-lo amb la informaci� introdu�da.

package vava33.plot2d;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.ComponentOrientation;
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
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

import javax.swing.ButtonModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

import vava33.plot2d.auxi.FileUtils;
import vava33.plot2d.auxi.JtxtAreaOut;
import vava33.plot2d.auxi.Pattern2D;
import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ButtonGroup;

public class D2Dsub_frame extends JFrame {

    private static final long serialVersionUID = 7730139568601028032L;
    private JButton btnDoIt;
    private JButton btnFit;
    private JButton btnSelectFile;
    private JButton btnSetAsSource;
    private JButton btnStop;
    private JButton btnSubBkg;
    private JButton btnSubGlass;
    private JButton btnToLeft;
    private JButton btnToRight;
    private JButton btnViewbkg;
    private JCheckBox chckbxFactor;
    private JCheckBox checkBox;
    private final JPanel contentPanel = new JPanel();
    private JLabel label;
    private JLabel lblAfterCalculation;
    private JLabel lblBeforeCalculation;
    private JLabel lblGlassF;
    private JLabel lblIter;
    private JLabel lblN;
    private JPanel panel;
    private JPanel panel_bottom;
    private JPanel panel_output;
    private JPanel panel_top;
    private JPanel panelBkg;
    private JPanel panelGlass;
    private JProgressBar progressBar;
    private JScrollPane scrollPane_1;
    private JSplitPane splitPane;
    private JSplitPane splitPane_1;
    private JTextField txtFactor;
    private JTextField txtIter;
    private JTextField txtN;
    private JTextField txtPathBefore;
    private JTextField txtResultImage;
    private JtxtAreaOut tAOut;
    
    private File FBef, FAft;
    private File glassD2File;
    private ImagePanel ipanel_after;
    private ImagePanel ipanel_before;
    private Pattern2D pattBef, pattAft;
    private Process pr;
    private MainFrame principal;
    private JCheckBox chckbxCustomCmd;
    private JTextField txtCmd;
    private JComboBox comboBox;
    private JTextField txtFactamp;
    private JTextField txtFactangle;
    private JLabel lblAwidth;
    private JLabel lblAangle;
    private JCheckBox chckbxHor;
    private JCheckBox chckbxVer;
    private JCheckBox chckbxHorver;
    private JTextField txtStep;
    private JLabel lblStep;
    private JButton btnRun;
    private JCheckBox chckbxLorOscil;
    private JLabel lblLor;
    private JLabel lblPol;
    private JCheckBox chckbxLorPow;
    private JCheckBox chckbxPolSyn;
    private JCheckBox chckbxPolLab;
    private final NoneSelectedButtonGroup buttonGroup = new NoneSelectedButtonGroup();
    private final NoneSelectedButtonGroup buttonGroup_1 = new NoneSelectedButtonGroup();

    /**
     * Create the dialog.
     */
    public D2Dsub_frame(MainFrame mf) {
//        UIManager.put("ProgressBar.background", Color.black);
//        UIManager.put("ProgressBar.foreground", Color.yellow);
//        UIManager.put("ProgressBar.selectionBackground", Color.red);
//        UIManager.put("ProgressBar.selectionForeground", Color.green);
        UIManager.put("ProgressBar.selectionBackground", Color.black);

        this.principal = mf;
        setIconImage(Toolkit.getDefaultToolkit().getImage(D2Dsub_frame.class.getResource("/img/Icona.png")));
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Background subtraction and LP correction");
        // setBounds(100, 100, 660, 730);
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        int width = 660;
        int height = 730;
        int x = (screen.width - width) / 2;
        int y = (screen.height - height) / 2;
        setBounds(x, y, 920, 940);
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(this.contentPanel, BorderLayout.CENTER);
        GridBagLayout gbl_contentPanel = new GridBagLayout();
        gbl_contentPanel.columnWidths = new int[] { 0, 0 };
        gbl_contentPanel.rowHeights = new int[] { 0, 0 };
        gbl_contentPanel.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
        gbl_contentPanel.rowWeights = new double[] { 1.0, Double.MIN_VALUE };
        contentPanel.setLayout(gbl_contentPanel);
        {
            this.splitPane_1 = new JSplitPane();
            this.splitPane_1.setResizeWeight(0.4);
            this.splitPane_1.setOrientation(JSplitPane.VERTICAL_SPLIT);
            GridBagConstraints gbc_splitPane_1 = new GridBagConstraints();
            gbc_splitPane_1.fill = GridBagConstraints.BOTH;
            gbc_splitPane_1.gridx = 0;
            gbc_splitPane_1.gridy = 0;
            contentPanel.add(this.splitPane_1, gbc_splitPane_1);
            this.splitPane = new JSplitPane();
            this.splitPane.setBorder(null);
            this.splitPane_1.setLeftComponent(this.splitPane);
            this.splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
            this.panel_top = new JPanel();
            this.splitPane.setLeftComponent(this.panel_top);
            GridBagLayout gbl_panel_top = new GridBagLayout();
            gbl_panel_top.columnWidths = new int[] { 0, 0, 0, 0, 0, 0 };
            gbl_panel_top.rowHeights = new int[] { 0, 0, 0, 0, 0 };
            gbl_panel_top.columnWeights = new double[] { 0.0, 0.0, 1.0, 0.0, 0.0, 0.0 };
            gbl_panel_top.rowWeights = new double[] { 0.0, 1.0, 0.0, 0.0, Double.MIN_VALUE };
            this.panel_top.setLayout(gbl_panel_top);
            {
                this.panelGlass = new JPanel();
                this.panelGlass.setBorder(new TitledBorder(null, "Glass subtraction", TitledBorder.LEADING,
                        TitledBorder.TOP, null, null));
                GridBagConstraints gbc_panelGlass = new GridBagConstraints();
                gbc_panelGlass.gridwidth = 2;
                gbc_panelGlass.gridheight = 2;
                gbc_panelGlass.insets = new Insets(5, 5, 5, 5);
                gbc_panelGlass.fill = GridBagConstraints.BOTH;
                gbc_panelGlass.gridx = 0;
                gbc_panelGlass.gridy = 0;
                this.panel_top.add(this.panelGlass, gbc_panelGlass);
                GridBagLayout gbl_panelGlass = new GridBagLayout();
                gbl_panelGlass.columnWidths = new int[] { 0, 0, 0, 0 };
                gbl_panelGlass.rowHeights = new int[] { 0, 0, 0 };
                gbl_panelGlass.columnWeights = new double[] { 0.0, 0.0, 0.0, Double.MIN_VALUE };
                gbl_panelGlass.rowWeights = new double[] { 0.0, 0.0, Double.MIN_VALUE };
                this.panelGlass.setLayout(gbl_panelGlass);
                {
                    this.btnSelectFile = new JButton("select glass file");
                    this.btnSelectFile.setMargin(new Insets(2, 6, 2, 6));
                    this.btnSelectFile.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent arg0) {
                            do_btnSelectFile_actionPerformed(arg0);
                        }
                    });
                    GridBagConstraints gbc_btnSelectFile = new GridBagConstraints();
                    gbc_btnSelectFile.anchor = GridBagConstraints.WEST;
                    gbc_btnSelectFile.insets = new Insets(2, 2, 5, 5);
                    gbc_btnSelectFile.gridx = 0;
                    gbc_btnSelectFile.gridy = 0;
                    this.panelGlass.add(this.btnSelectFile, gbc_btnSelectFile);
                }
                {
                    this.chckbxFactor = new JCheckBox("factor=");
                    this.chckbxFactor.addItemListener(new ItemListener() {
                        @Override
                        public void itemStateChanged(ItemEvent arg0) {
                            do_chckbxFactor_itemStateChanged(arg0);
                        }
                    });
                    GridBagConstraints gbc_chckbxFactor = new GridBagConstraints();
                    gbc_chckbxFactor.insets = new Insets(2, 0, 5, 5);
                    gbc_chckbxFactor.gridx = 1;
                    gbc_chckbxFactor.gridy = 0;
                    this.panelGlass.add(this.chckbxFactor, gbc_chckbxFactor);
                }
                {
                    this.txtFactor = new JTextField();
                    this.txtFactor.setEnabled(false);
                    this.txtFactor.setText("1.000");
                    this.txtFactor.setColumns(4);
                    GridBagConstraints gbc_txtFactor = new GridBagConstraints();
                    gbc_txtFactor.fill = GridBagConstraints.HORIZONTAL;
                    gbc_txtFactor.insets = new Insets(2, 0, 5, 2);
                    gbc_txtFactor.gridx = 2;
                    gbc_txtFactor.gridy = 0;
                    this.panelGlass.add(this.txtFactor, gbc_txtFactor);
                }
                {
                    this.btnSubGlass = new JButton("Subtract!");
                    this.btnSubGlass.setMargin(new Insets(2, 6, 2, 6));
                    this.btnSubGlass.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent arg0) {
                            do_btnSubGlass_actionPerformed(arg0);
                        }
                    });
                    {
                        this.lblGlassF = new JLabel("(no glass data file selected)");
                        GridBagConstraints gbc_lblGlassF = new GridBagConstraints();
                        gbc_lblGlassF.gridwidth = 2;
                        gbc_lblGlassF.insets = new Insets(0, 2, 2, 5);
                        gbc_lblGlassF.gridx = 0;
                        gbc_lblGlassF.gridy = 1;
                        this.panelGlass.add(this.lblGlassF, gbc_lblGlassF);
                    }
                    GridBagConstraints gbc_btnSubGlass = new GridBagConstraints();
                    gbc_btnSubGlass.gridx = 2;
                    gbc_btnSubGlass.gridy = 1;
                    this.panelGlass.add(this.btnSubGlass, gbc_btnSubGlass);
                }
            }
            {
                this.panelBkg = new JPanel();
                this.panelBkg.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Background estimation & subtraction", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
                GridBagConstraints gbc_panelBkg = new GridBagConstraints();
                gbc_panelBkg.gridwidth = 2;
                gbc_panelBkg.gridheight = 2;
                gbc_panelBkg.insets = new Insets(5, 0, 5, 5);
                gbc_panelBkg.fill = GridBagConstraints.BOTH;
                gbc_panelBkg.gridx = 2;
                gbc_panelBkg.gridy = 0;
                this.panel_top.add(this.panelBkg, gbc_panelBkg);
                GridBagLayout gbl_panelBkg = new GridBagLayout();
                gbl_panelBkg.columnWidths = new int[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
                gbl_panelBkg.rowHeights = new int[] { 0, 0, 0 };
                gbl_panelBkg.columnWeights = new double[] { 0.0, 0.0, 1.0, 0.0, 1.0, 0.0, 1.0, 0.0, 0.0, Double.MIN_VALUE };
                gbl_panelBkg.rowWeights = new double[] { 0.0, 0.0, Double.MIN_VALUE };
                this.panelBkg.setLayout(gbl_panelBkg);
                {
                    this.comboBox = new JComboBox();
                    this.comboBox.addItemListener(new ItemListener() {
                        public void itemStateChanged(ItemEvent arg0) {
                            do_comboBox_itemStateChanged(arg0);
                        }
                    });
                    this.comboBox.setModel(new DefaultComboBoxModel(new String[] {"avsq", "avarc", "avcirc", "minsq", "minarc"}));
                    GridBagConstraints gbc_comboBox = new GridBagConstraints();
                    gbc_comboBox.insets = new Insets(2, 2, 5, 5);
                    gbc_comboBox.fill = GridBagConstraints.HORIZONTAL;
                    gbc_comboBox.gridx = 0;
                    gbc_comboBox.gridy = 0;
                    this.panelBkg.add(this.comboBox, gbc_comboBox);
                }
                {
                    this.lblN = new JLabel("Npix=");
                    GridBagConstraints gbc_lblN = new GridBagConstraints();
                    gbc_lblN.insets = new Insets(2, 0, 5, 5);
                    gbc_lblN.anchor = GridBagConstraints.EAST;
                    gbc_lblN.gridx = 1;
                    gbc_lblN.gridy = 0;
                    this.panelBkg.add(this.lblN, gbc_lblN);
                }
                {
                    this.txtN = new JTextField();
                    this.txtN.setText("20");
                    GridBagConstraints gbc_txtN = new GridBagConstraints();
                    gbc_txtN.fill = GridBagConstraints.HORIZONTAL;
                    gbc_txtN.insets = new Insets(2, 0, 5, 5);
                    gbc_txtN.gridx = 2;
                    gbc_txtN.gridy = 0;
                    this.panelBkg.add(this.txtN, gbc_txtN);
                    this.txtN.setColumns(4);
                }
                {
                    this.lblAwidth = new JLabel("wth=");
                    GridBagConstraints gbc_lblAwidth = new GridBagConstraints();
                    gbc_lblAwidth.insets = new Insets(2, 0, 5, 5);
                    gbc_lblAwidth.anchor = GridBagConstraints.EAST;
                    gbc_lblAwidth.gridx = 3;
                    gbc_lblAwidth.gridy = 0;
                    this.panelBkg.add(this.lblAwidth, gbc_lblAwidth);
                }
                {
                    this.txtFactamp = new JTextField();
                    this.txtFactamp.setText("0.001");
                    GridBagConstraints gbc_txtFactamp = new GridBagConstraints();
                    gbc_txtFactamp.gridwidth = 2;
                    gbc_txtFactamp.insets = new Insets(2, 0, 5, 5);
                    gbc_txtFactamp.fill = GridBagConstraints.HORIZONTAL;
                    gbc_txtFactamp.gridx = 4;
                    gbc_txtFactamp.gridy = 0;
                    this.panelBkg.add(this.txtFactamp, gbc_txtFactamp);
                    this.txtFactamp.setColumns(4);
                }
                {
                    this.chckbxHor = new JCheckBox("h");
                    this.chckbxHor.setSelected(true);
                    GridBagConstraints gbc_chckbxHor = new GridBagConstraints();
                    gbc_chckbxHor.anchor = GridBagConstraints.EAST;
                    gbc_chckbxHor.insets = new Insets(2, 0, 5, 0);
                    gbc_chckbxHor.gridx = 6;
                    gbc_chckbxHor.gridy = 0;
                    this.panelBkg.add(this.chckbxHor, gbc_chckbxHor);
                }
                this.btnSubBkg = new JButton("Subtract!");
                this.btnSubBkg.setMargin(new Insets(2, 6, 2, 6));
                this.btnSubBkg.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        do_btnSubBkg_actionPerformed(e);
                    }
                });
                {
                    this.chckbxVer = new JCheckBox("v");
                    GridBagConstraints gbc_chckbxVer = new GridBagConstraints();
                    gbc_chckbxVer.insets = new Insets(2, 0, 5, 0);
                    gbc_chckbxVer.gridx = 7;
                    gbc_chckbxVer.gridy = 0;
                    this.panelBkg.add(this.chckbxVer, gbc_chckbxVer);
                }
                {
                    this.chckbxHorver = new JCheckBox("hv");
                    GridBagConstraints gbc_chckbxHorver = new GridBagConstraints();
                    gbc_chckbxHorver.anchor = GridBagConstraints.WEST;
                    gbc_chckbxHorver.insets = new Insets(2, 0, 5, 2);
                    gbc_chckbxHorver.gridx = 8;
                    gbc_chckbxHorver.gridy = 0;
                    this.panelBkg.add(this.chckbxHorver, gbc_chckbxHorver);
                }
                GridBagConstraints gbc_btnSubBkg = new GridBagConstraints();
                gbc_btnSubBkg.fill = GridBagConstraints.HORIZONTAL;
                gbc_btnSubBkg.insets = new Insets(0, 2, 2, 5);
                gbc_btnSubBkg.gridx = 0;
                gbc_btnSubBkg.gridy = 1;
                this.panelBkg.add(this.btnSubBkg, gbc_btnSubBkg);
                {
                    this.lblIter = new JLabel("Iter=");
                    GridBagConstraints gbc_lblIter = new GridBagConstraints();
                    gbc_lblIter.insets = new Insets(0, 0, 2, 5);
                    gbc_lblIter.anchor = GridBagConstraints.EAST;
                    gbc_lblIter.gridx = 1;
                    gbc_lblIter.gridy = 1;
                    this.panelBkg.add(this.lblIter, gbc_lblIter);
                }
                {
                    {
                        this.txtIter = new JTextField();
                        this.txtIter.setText("10");
                        GridBagConstraints gbc_txtIter = new GridBagConstraints();
                        gbc_txtIter.fill = GridBagConstraints.HORIZONTAL;
                        gbc_txtIter.insets = new Insets(0, 0, 2, 5);
                        gbc_txtIter.gridx = 2;
                        gbc_txtIter.gridy = 1;
                        this.panelBkg.add(this.txtIter, gbc_txtIter);
                        this.txtIter.setColumns(4);
                    }
                }
                {
                    this.btnViewbkg = new JButton("view BKG");
                    this.btnViewbkg.setEnabled(false);
                    this.btnViewbkg.setMargin(new Insets(2, 4, 2, 4));
                    this.btnViewbkg.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            do_btnViewbkg_actionPerformed(e);
                        }
                    });
                    {
                        this.lblAangle = new JLabel("ang=");
                        GridBagConstraints gbc_lblAangle = new GridBagConstraints();
                        gbc_lblAangle.insets = new Insets(0, 0, 2, 5);
                        gbc_lblAangle.anchor = GridBagConstraints.EAST;
                        gbc_lblAangle.gridx = 3;
                        gbc_lblAangle.gridy = 1;
                        this.panelBkg.add(this.lblAangle, gbc_lblAangle);
                    }
                    {
                        this.txtFactangle = new JTextField();
                        this.txtFactangle.setText("4.0");
                        GridBagConstraints gbc_txtFactangle = new GridBagConstraints();
                        gbc_txtFactangle.insets = new Insets(0, 0, 2, 5);
                        gbc_txtFactangle.fill = GridBagConstraints.HORIZONTAL;
                        gbc_txtFactangle.gridx = 4;
                        gbc_txtFactangle.gridy = 1;
                        this.panelBkg.add(this.txtFactangle, gbc_txtFactangle);
                        this.txtFactangle.setColumns(4);
                    }
                    {
                        this.lblStep = new JLabel("step=");
                        GridBagConstraints gbc_lblStep = new GridBagConstraints();
                        gbc_lblStep.insets = new Insets(0, 0, 2, 5);
                        gbc_lblStep.anchor = GridBagConstraints.EAST;
                        gbc_lblStep.gridx = 5;
                        gbc_lblStep.gridy = 1;
                        this.panelBkg.add(this.lblStep, gbc_lblStep);
                    }
                    {
                        this.txtStep = new JTextField();
                        this.txtStep.setText("0.05");
                        GridBagConstraints gbc_txtStep = new GridBagConstraints();
                        gbc_txtStep.insets = new Insets(0, 0, 2, 5);
                        gbc_txtStep.fill = GridBagConstraints.HORIZONTAL;
                        gbc_txtStep.gridx = 6;
                        gbc_txtStep.gridy = 1;
                        this.panelBkg.add(this.txtStep, gbc_txtStep);
                        this.txtStep.setColumns(4);
                    }
                    GridBagConstraints gbc_btnViewbkg = new GridBagConstraints();
                    gbc_btnViewbkg.insets = new Insets(0, 0, 2, 2);
                    gbc_btnViewbkg.gridwidth = 2;
                    gbc_btnViewbkg.gridx = 7;
                    gbc_btnViewbkg.gridy = 1;
                    this.panelBkg.add(this.btnViewbkg, gbc_btnViewbkg);
                }
            }
            {
                this.panel = new JPanel();
                this.panel.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "LP corr.", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
                GridBagConstraints gbc_panel = new GridBagConstraints();
                gbc_panel.gridwidth = 2;
                gbc_panel.gridheight = 3;
                gbc_panel.insets = new Insets(5, 0, 5, 0);
                gbc_panel.fill = GridBagConstraints.BOTH;
                gbc_panel.gridx = 4;
                gbc_panel.gridy = 0;
                this.panel_top.add(this.panel, gbc_panel);
                GridBagLayout gbl_panel = new GridBagLayout();
                gbl_panel.columnWidths = new int[] { 0, 0, 0, 0 };
                gbl_panel.rowHeights = new int[] { 0, 0, 0, 0 };
                gbl_panel.columnWeights = new double[] { 0.0, 0.0, 0.0, Double.MIN_VALUE };
                gbl_panel.rowWeights = new double[] { 0.0, 0.0, 0.0, Double.MIN_VALUE };
                this.panel.setLayout(gbl_panel);
                {
                    {
                        this.lblLor = new JLabel("Lor:");
                        GridBagConstraints gbc_lblLor = new GridBagConstraints();
                        gbc_lblLor.insets = new Insets(2, 2, 5, 5);
                        gbc_lblLor.gridx = 0;
                        gbc_lblLor.gridy = 0;
                        this.panel.add(this.lblLor, gbc_lblLor);
                    }
                    {
                        this.chckbxLorOscil = new JCheckBox("Osc");
                        buttonGroup.add(this.chckbxLorOscil);
                        GridBagConstraints gbc_chckbxLorOscil = new GridBagConstraints();
                        gbc_chckbxLorOscil.anchor = GridBagConstraints.WEST;
                        gbc_chckbxLorOscil.insets = new Insets(2, 0, 5, 2);
                        gbc_chckbxLorOscil.gridx = 1;
                        gbc_chckbxLorOscil.gridy = 0;
                        this.panel.add(this.chckbxLorOscil, gbc_chckbxLorOscil);
                    }
                    {
                        this.chckbxLorPow = new JCheckBox("Pow");
                        buttonGroup.add(this.chckbxLorPow);
                        GridBagConstraints gbc_chckbxLorPow = new GridBagConstraints();
                        gbc_chckbxLorPow.anchor = GridBagConstraints.WEST;
                        gbc_chckbxLorPow.insets = new Insets(2, 0, 5, 2);
                        gbc_chckbxLorPow.gridx = 2;
                        gbc_chckbxLorPow.gridy = 0;
                        this.panel.add(this.chckbxLorPow, gbc_chckbxLorPow);
                    }
                    {
                        this.lblPol = new JLabel("Pol:");
                        GridBagConstraints gbc_lblPol = new GridBagConstraints();
                        gbc_lblPol.insets = new Insets(0, 2, 5, 5);
                        gbc_lblPol.gridx = 0;
                        gbc_lblPol.gridy = 1;
                        this.panel.add(this.lblPol, gbc_lblPol);
                    }
                }
                this.btnDoIt = new JButton("Do it!");
                this.btnDoIt.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        do_btnDoIt_actionPerformed(e);
                    }
                });
                {
                    this.chckbxPolSyn = new JCheckBox("Syn");
                    buttonGroup_1.add(this.chckbxPolSyn);
                    GridBagConstraints gbc_chckbxPolSyn = new GridBagConstraints();
                    gbc_chckbxPolSyn.anchor = GridBagConstraints.WEST;
                    gbc_chckbxPolSyn.insets = new Insets(0, 0, 5, 2);
                    gbc_chckbxPolSyn.gridx = 1;
                    gbc_chckbxPolSyn.gridy = 1;
                    this.panel.add(this.chckbxPolSyn, gbc_chckbxPolSyn);
                }
                {
                    this.chckbxPolLab = new JCheckBox("Lab");
                    buttonGroup_1.add(this.chckbxPolLab);
                    GridBagConstraints gbc_chckbxPolLab = new GridBagConstraints();
                    gbc_chckbxPolLab.anchor = GridBagConstraints.WEST;
                    gbc_chckbxPolLab.insets = new Insets(0, 0, 5, 2);
                    gbc_chckbxPolLab.gridx = 2;
                    gbc_chckbxPolLab.gridy = 1;
                    this.panel.add(this.chckbxPolLab, gbc_chckbxPolLab);
                }
                this.btnDoIt.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
                GridBagConstraints gbc_btnDoIt = new GridBagConstraints();
                gbc_btnDoIt.gridwidth = 2;
                gbc_btnDoIt.gridx = 1;
                gbc_btnDoIt.gridy = 2;
                this.panel.add(this.btnDoIt, gbc_btnDoIt);
            }
            {
                this.chckbxCustomCmd = new JCheckBox("Custom cmd=");
                this.chckbxCustomCmd.addItemListener(new ItemListener() {
                    public void itemStateChanged(ItemEvent arg0) {
                        do_chckbxCustomCmd_itemStateChanged(arg0);
                    }
                });
                GridBagConstraints gbc_chckbxCustomCmd = new GridBagConstraints();
                gbc_chckbxCustomCmd.insets = new Insets(0, 5, 5, 5);
                gbc_chckbxCustomCmd.gridx = 0;
                gbc_chckbxCustomCmd.gridy = 2;
                this.panel_top.add(this.chckbxCustomCmd, gbc_chckbxCustomCmd);
            }
            {
                this.txtCmd = new JTextField();
                this.txtCmd.setToolTipText("<html>\r\n** Background estimation and subtraction **<br>\r\n     bkg [nPixels] [nIterations]<br>\r\n     rad [stepsize] [nIteracions]<br>\r\n     arc [factAmplada] [factAngle] [nIteracions]<br>\r\n     flip [fhor] [fver] [fhorver] (1 or 0) [npix]<br>\r\n     fliparc [fhor] [fver] [fhorver] (1 or 0) [factAmplada] [factAngle]<br>\r\n<br>\r\n** Direct background subtraction (def. glassScale=auto) **<br>\r\n     sub background_file.ext<br>\r\n     glass glass_file.ext [glassScale]<br>\r\n<br>\r\n** Lorentz-Polarization correction (def. distOD and pixSize from input file) **<br>\r\n     lp [distOD] [pixSize]<br>\r\n</html>");
                GridBagConstraints gbc_txtCmd = new GridBagConstraints();
                gbc_txtCmd.gridwidth = 2;
                gbc_txtCmd.insets = new Insets(0, 0, 5, 5);
                gbc_txtCmd.fill = GridBagConstraints.HORIZONTAL;
                gbc_txtCmd.gridx = 1;
                gbc_txtCmd.gridy = 2;
                this.panel_top.add(this.txtCmd, gbc_txtCmd);
                this.txtCmd.setColumns(10);
            }
            {
                this.btnRun = new JButton("Run");
                this.btnRun.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent arg0) {
                        do_btnRun_actionPerformed(arg0);
                    }
                });
                this.btnRun.setEnabled(false);
                GridBagConstraints gbc_btnRun = new GridBagConstraints();
                gbc_btnRun.fill = GridBagConstraints.HORIZONTAL;
                gbc_btnRun.insets = new Insets(0, 0, 5, 5);
                gbc_btnRun.gridx = 3;
                gbc_btnRun.gridy = 2;
                this.panel_top.add(this.btnRun, gbc_btnRun);
            }
            {
                this.progressBar = new JProgressBar();
                this.progressBar.setFont(new Font("Tahoma", Font.BOLD, 15));
                GridBagConstraints gbc_progressBar = new GridBagConstraints();
                gbc_progressBar.gridwidth = 3;
                gbc_progressBar.fill = GridBagConstraints.BOTH;
                gbc_progressBar.insets = new Insets(0, 5, 2, 5);
                gbc_progressBar.gridx = 0;
                gbc_progressBar.gridy = 3;
                this.panel_top.add(this.progressBar, gbc_progressBar);
            }
            {
                this.btnStop = new JButton("Stop calculation!");
                GridBagConstraints gbc_btnStop = new GridBagConstraints();
                gbc_btnStop.insets = new Insets(0, 0, 2, 0);
                gbc_btnStop.fill = GridBagConstraints.HORIZONTAL;
                gbc_btnStop.gridwidth = 3;
                gbc_btnStop.gridx = 3;
                gbc_btnStop.gridy = 3;
                this.panel_top.add(this.btnStop, gbc_btnStop);
                this.btnStop.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent arg0) {
                        do_btnStop_actionPerformed(arg0);
                    }
                });
                this.btnStop.setBackground(Color.RED);
            }
            {
            }
            {
                this.panel_output = new JPanel();
                this.panel_output.setBackground(Color.BLACK);
                this.splitPane.setRightComponent(this.panel_output);
                GridBagLayout gbl_panel_output = new GridBagLayout();
                gbl_panel_output.columnWidths = new int[] { 0, 0 };
                gbl_panel_output.rowHeights = new int[] { 0, 0 };
                gbl_panel_output.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
                gbl_panel_output.rowWeights = new double[] { 1.0, Double.MIN_VALUE };
                this.panel_output.setLayout(gbl_panel_output);
                {
                    this.scrollPane_1 = new JScrollPane();
                    this.scrollPane_1.setBorder(null);
                    GridBagConstraints gbc_scrollPane_1 = new GridBagConstraints();
                    gbc_scrollPane_1.insets = new Insets(5, 5, 5, 5);
                    gbc_scrollPane_1.fill = GridBagConstraints.BOTH;
                    gbc_scrollPane_1.gridx = 0;
                    gbc_scrollPane_1.gridy = 0;
                    this.panel_output.add(this.scrollPane_1, gbc_scrollPane_1);
                    {
                        this.tAOut = new JtxtAreaOut();
                        this.tAOut.setBorder(null);
                        this.tAOut.setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));
                        this.tAOut.setLineWrap(true);
                        this.tAOut.setWrapStyleWord(true);
                        this.tAOut.setEditable(false);
                        this.tAOut.setBackground(Color.BLACK);
                        this.scrollPane_1.setViewportView(this.tAOut);
                    }
                }
            }
            {
                this.panel_bottom = new JPanel();
                this.splitPane_1.setRightComponent(this.panel_bottom);
                GridBagLayout gbl_panel_bottom = new GridBagLayout();
                gbl_panel_bottom.columnWidths = new int[] { 0, 0, 0, 0, 0, 0 };
                gbl_panel_bottom.rowHeights = new int[] { 0, 0, 0, 0, 0 };
                gbl_panel_bottom.columnWeights = new double[] { 0.0, 1.0, 0.0, 0.0, 1.0, Double.MIN_VALUE };
                gbl_panel_bottom.rowWeights = new double[] { 0.0, 0.0, 0.0, 1.0, Double.MIN_VALUE };
                this.panel_bottom.setLayout(gbl_panel_bottom);
                {
                    this.lblBeforeCalculation = new JLabel("Source Image");
                    GridBagConstraints gbc_lblBeforeCalculation = new GridBagConstraints();
                    gbc_lblBeforeCalculation.insets = new Insets(5, 5, 5, 5);
                    gbc_lblBeforeCalculation.gridx = 0;
                    gbc_lblBeforeCalculation.gridy = 0;
                    this.panel_bottom.add(this.lblBeforeCalculation, gbc_lblBeforeCalculation);
                }
                {
                    this.txtPathBefore = new JTextField();
                    this.txtPathBefore.setEditable(false);
                    GridBagConstraints gbc_txtPathBefore = new GridBagConstraints();
                    gbc_txtPathBefore.insets = new Insets(5, 0, 5, 5);
                    gbc_txtPathBefore.fill = GridBagConstraints.HORIZONTAL;
                    gbc_txtPathBefore.gridx = 1;
                    gbc_txtPathBefore.gridy = 0;
                    this.panel_bottom.add(this.txtPathBefore, gbc_txtPathBefore);
                    this.txtPathBefore.setColumns(10);
                }
                {
                    this.lblAfterCalculation = new JLabel("Result Image");
                    GridBagConstraints gbc_lblAfterCalculation = new GridBagConstraints();
                    gbc_lblAfterCalculation.anchor = GridBagConstraints.EAST;
                    gbc_lblAfterCalculation.insets = new Insets(5, 0, 5, 5);
                    gbc_lblAfterCalculation.gridx = 3;
                    gbc_lblAfterCalculation.gridy = 0;
                    this.panel_bottom.add(this.lblAfterCalculation, gbc_lblAfterCalculation);
                }
                {
                    this.btnFit = new JButton("fit");
                    this.btnFit.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            do_btnFit_actionPerformed(e);
                        }
                    });
                    {
                        this.txtResultImage = new JTextField();
                        this.txtResultImage.setEditable(false);
                        GridBagConstraints gbc_txtResultImage = new GridBagConstraints();
                        gbc_txtResultImage.insets = new Insets(5, 0, 5, 5);
                        gbc_txtResultImage.fill = GridBagConstraints.HORIZONTAL;
                        gbc_txtResultImage.gridx = 4;
                        gbc_txtResultImage.gridy = 0;
                        this.panel_bottom.add(this.txtResultImage, gbc_txtResultImage);
                        this.txtResultImage.setColumns(10);
                    }
                    this.btnFit.setMargin(new Insets(2, 3, 2, 3));
                    GridBagConstraints gbc_btnFit = new GridBagConstraints();
                    gbc_btnFit.insets = new Insets(0, 0, 5, 5);
                    gbc_btnFit.gridx = 2;
                    gbc_btnFit.gridy = 1;
                    this.panel_bottom.add(this.btnFit, gbc_btnFit);
                }
                {
                    this.ipanel_before = new ImagePanel();
                    GridBagConstraints gbc_ipanel_before = new GridBagConstraints();
                    gbc_ipanel_before.gridwidth = 2;
                    gbc_ipanel_before.gridheight = 3;
                    gbc_ipanel_before.insets = new Insets(0, 5, 5, 5);
                    gbc_ipanel_before.fill = GridBagConstraints.BOTH;
                    gbc_ipanel_before.gridx = 0;
                    gbc_ipanel_before.gridy = 1;
                    this.panel_bottom.add(this.ipanel_before, gbc_ipanel_before);
                }
                {
                    this.btnToRight = new JButton("->");
                    this.btnToRight.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            do_btnToRight_actionPerformed(e);
                        }
                    });
                    this.btnToRight.setMargin(new Insets(2, 2, 2, 2));
                    GridBagConstraints gbc_btnToRight = new GridBagConstraints();
                    gbc_btnToRight.anchor = GridBagConstraints.SOUTH;
                    gbc_btnToRight.insets = new Insets(0, 0, 5, 5);
                    gbc_btnToRight.gridx = 2;
                    gbc_btnToRight.gridy = 2;
                    this.panel_bottom.add(this.btnToRight, gbc_btnToRight);
                }
                {
                    this.ipanel_after = new ImagePanel();
                    GridBagConstraints gbc_ipanel_after = new GridBagConstraints();
                    gbc_ipanel_after.insets = new Insets(0, 0, 5, 5);
                    gbc_ipanel_after.gridwidth = 2;
                    gbc_ipanel_after.gridheight = 3;
                    gbc_ipanel_after.fill = GridBagConstraints.BOTH;
                    gbc_ipanel_after.gridx = 3;
                    gbc_ipanel_after.gridy = 1;
                    this.panel_bottom.add(this.ipanel_after, gbc_ipanel_after);
                }
                {
                    this.btnToLeft = new JButton("<-");
                    this.btnToLeft.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            do_btnToLeft_actionPerformed(e);
                        }
                    });
                    this.btnToLeft.setMargin(new Insets(2, 2, 2, 2));
                    GridBagConstraints gbc_btnToLeft = new GridBagConstraints();
                    gbc_btnToLeft.anchor = GridBagConstraints.NORTH;
                    gbc_btnToLeft.insets = new Insets(0, 0, 0, 5);
                    gbc_btnToLeft.gridx = 2;
                    gbc_btnToLeft.gridy = 3;
                    this.panel_bottom.add(this.btnToLeft, gbc_btnToLeft);
                }
            }
            JPanel buttonPane = new JPanel();
            getContentPane().add(buttonPane, BorderLayout.SOUTH);
            {
                GridBagLayout gbl_buttonPane = new GridBagLayout();
                gbl_buttonPane.columnWidths = new int[] { 0, 0, 0, 0, 0 };
                gbl_buttonPane.rowHeights = new int[] { 0, 0 };
                gbl_buttonPane.columnWeights = new double[] { 0.0, 0.0, 0.0, 1.0, Double.MIN_VALUE };
                gbl_buttonPane.rowWeights = new double[] { 0.0, Double.MIN_VALUE };
                buttonPane.setLayout(gbl_buttonPane);
            }
            JButton okButton = new JButton("close");
            GridBagConstraints gbc_okButton = new GridBagConstraints();
            gbc_okButton.anchor = GridBagConstraints.WEST;
            gbc_okButton.insets = new Insets(5, 5, 5, 5);
            gbc_okButton.gridx = 0;
            gbc_okButton.gridy = 0;
            buttonPane.add(okButton, gbc_okButton);
            okButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent arg0) {
                    do_okButton_actionPerformed(arg0);
                }
            });
            okButton.setActionCommand("OK");
            getRootPane().setDefaultButton(okButton);
            {
                this.btnSetAsSource = new JButton("set as source");
                this.btnSetAsSource.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        do_btnSetAsSource_actionPerformed(e);
                    }
                });
                this.checkBox = new JCheckBox("on top");
                GridBagConstraints gbc_checkBox = new GridBagConstraints();
                gbc_checkBox.insets = new Insets(5, 0, 5, 5);
                gbc_checkBox.gridx = 1;
                gbc_checkBox.gridy = 0;
                buttonPane.add(this.checkBox, gbc_checkBox);
                this.checkBox.addItemListener(new ItemListener() {
                    @Override
                    public void itemStateChanged(ItemEvent arg0) {
                        do_checkBox_itemStateChanged(arg0);
                    }
                });
                this.checkBox.setHorizontalTextPosition(SwingConstants.LEADING);
                this.checkBox.setFont(new Font("Tahoma", Font.PLAIN, 11));
                this.checkBox.setActionCommand("on top");
                {
                    this.label = new JLabel("?");
                    this.label.addMouseListener(new MouseAdapter() {
                        @Override
                        public void mouseEntered(MouseEvent arg0) {
                            do_label_mouseEntered(arg0);
                        }

                        @Override
                        public void mouseExited(MouseEvent e) {
                            do_label_mouseExited(e);
                        }

                        @Override
                        public void mouseReleased(MouseEvent e) {
                            do_label_mouseReleased(e);
                        }
                    });
                    GridBagConstraints gbc_label = new GridBagConstraints();
                    gbc_label.insets = new Insets(5, 0, 5, 5);
                    gbc_label.gridx = 2;
                    gbc_label.gridy = 0;
                    buttonPane.add(this.label, gbc_label);
                    this.label.setFont(new Font("Tahoma", Font.BOLD, 14));
                }
                GridBagConstraints gbc_btnSetAsSource = new GridBagConstraints();
                gbc_btnSetAsSource.insets = new Insets(5, 0, 5, 5);
                gbc_btnSetAsSource.anchor = GridBagConstraints.EAST;
                gbc_btnSetAsSource.gridx = 3;
                gbc_btnSetAsSource.gridy = 0;
                buttonPane.add(this.btnSetAsSource, gbc_btnSetAsSource);
            }
        }
        this.splitPane_1.setDividerLocation(360);

        this.setBefore(mf.getOpenedFile());
        tAOut.ln("Initial file= " + this.FBef.toString());
        this.activeOptions();
        
        //comprovem que no superem la mida de la pantalla
        this.setLocationRelativeTo(null);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        if(this.getWidth()>screenSize.width||this.getHeight()>screenSize.height){
//            this.setExtendedState(this.getExtendedState() | JFrame.MAXIMIZED_BOTH);
            this.setSize(screenSize.width-100,screenSize.height-100);
        }
        
        
        //comprovem que existeixi l'executable
        if (!new File(MainFrame.getBinDir() + MainFrame.getD2dsubExec()).exists()){
            tAOut.ln("ERROR: D2Dsub executable not found. Please reinstall the program.");
            return;
        }
        
        //mostrem titol i versio D2Dsub
//        tAOut.ln("** d2Dsub: Background estimation/subtraction & LP correction **");
        if (pr == null) {
            Thread d2dsub = new Thread(new d2dsubRunnable("-ver"));
            d2dsub.start();
        }
        
    }

    protected void do_btnDoIt_actionPerformed(ActionEvent e) {
        // comprovar que no estigui corrent ja el proces
        if (pr != null) {
            tAOut.ln("Process still running espera a que acabi!");
            return;
        }
        Thread d2dsub = new Thread(new d2dsubRunnable("-lp"));
        d2dsub.start();
    }

    protected void do_btnFit_actionPerformed(ActionEvent e) {
        if (ipanel_before.getPatt2D() != null) {
            ipanel_before.resetView();
        }
        if (ipanel_after.getPatt2D() != null) {
            ipanel_after.resetView();
        }

    }

    protected void do_btnSelectFile_actionPerformed(ActionEvent arg0) {

        JFileChooser fileChooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("2D Data file (bin,img)", "bin", "img");
        fileChooser.addChoosableFileFilter(filter);
        fileChooser.setCurrentDirectory(new File(MainFrame.getWorkdir()));
        int selection = fileChooser.showOpenDialog(null);
        if (selection != JFileChooser.APPROVE_OPTION) {
            tAOut.ln("no glass file selected");
            return;
        }
        glassD2File = fileChooser.getSelectedFile();
        tAOut.ln("Glass file selected: " + glassD2File.getPath());
        lblGlassF.setText(glassD2File.getName());
    }

    protected void do_btnSetAsSource_actionPerformed(ActionEvent e) {
        int n = JOptionPane.showConfirmDialog(this, "Set result image as working image?", "Update image",
                JOptionPane.YES_NO_OPTION);
        if (n == JOptionPane.YES_OPTION) {
            this.setBefore(this.FAft);
            principal.updatePatt2D(this.FAft);
            btnFit.doClick();
        }

    }

    protected void do_btnStop_actionPerformed(ActionEvent arg0) {
        pr.destroy();
        pr = null;
    }

    protected void do_btnSubBkg_actionPerformed(ActionEvent e) {
        // comprovar que no estigui corrent ja el proces
        if (pr != null) {
            tAOut.ln("Process still running espera a que acabi!");
            return;
        }
        String opt = "-".concat((String)comboBox.getSelectedItem()).trim();
        Thread d2dsub = new Thread(new d2dsubRunnable(opt));
        d2dsub.start();
    }
    
    protected void do_btnRun_actionPerformed(ActionEvent arg0) {
        // comprovar que no estigui corrent ja el proces
        if (pr != null) {
            tAOut.ln("Process still running espera a que acabi!");
            return;
        }
        Thread d2dsub = new Thread(new d2dsubRunnable("custom"));
        d2dsub.start();
    }

    protected void do_btnSubGlass_actionPerformed(ActionEvent arg0) {
        // comprovar que no estigui corrent ja el proces
        if (pr != null) {
            tAOut.ln("Process still running espera a que acabi!");
            return;
        }
        Thread d2dsub = new Thread(new d2dsubRunnable("-glass"));
        d2dsub.start();
    }

    protected void do_btnToLeft_actionPerformed(ActionEvent e) {
        if (ipanel_before.getPatt2D() != null && ipanel_after.getPatt2D() != null) {
            ipanel_before.setScalefit(ipanel_after.getScalefit());
            ipanel_before.setOriginX(ipanel_after.getOriginX());
            ipanel_before.setOriginY(ipanel_after.getOriginY());
            ipanel_before.repaint();
        }
    }

    protected void do_btnToRight_actionPerformed(ActionEvent e) {
        if (ipanel_before.getPatt2D() != null && ipanel_after.getPatt2D() != null) {
            ipanel_after.setScalefit(ipanel_before.getScalefit());
            ipanel_after.setOriginX(ipanel_before.getOriginX());
            ipanel_after.setOriginY(ipanel_before.getOriginY());
            ipanel_after.repaint();
        }
    }

    //el nom del fitxer de fons ara es sempre igual
    protected void do_btnViewbkg_actionPerformed(ActionEvent e) {
        // fitxer de fons before-bkgItX.ext (ext sempre sera bin) en cas BKG/RAD/ARC
        // fitxer de fons before-bkgflip.ext en cas FLIP/FLIPARC
//        String opt = ((String)comboBox.getSelectedItem()).trim();
        String bkgFileS = FileUtils.getFNameNoExt(FBef); // primer treiem l'extensio a before
//        boolean iterbkg = opt.equalsIgnoreCase("-bkg")||opt.equalsIgnoreCase("-arc")||opt.equalsIgnoreCase("-rad");
//        boolean flip = opt.equalsIgnoreCase("-flip")||opt.equalsIgnoreCase("-fliparc");
//        if(iterbkg){
//            bkgFileS = bkgFileS.concat("-BkgIt").concat(txtIter.getText().trim()).concat(".bin");    
//        }
//        if(flip){
//            bkgFileS = bkgFileS.concat("-Bkgflip.bin");
//        }
        bkgFileS = bkgFileS.concat("-Bkg.bin");
        
        File bkgFile = new File(bkgFileS);
        if (!bkgFile.exists()) {
            tAOut.ln("Background intensity image file not found");
            return;
        }

        final JDialog bwin = new JDialog(this, "Subtracted background", true);
        ImagePanel panel = new ImagePanel();
        Dimension d = new Dimension(600, 650);
        panel.setSize(d);
        panel.setPreferredSize(d);
        bwin.getContentPane().add(panel);
        panel.setImagePatt2D(FileUtils.openPatternFile(bkgFile));
        panel.resetView();
        while (panel.getPatt2D() == null) {
            try {
                Thread.sleep(50);
                // System.out.println("lalala");
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
        }
        bwin.pack();
        bwin.setVisible(true);
    }

    protected void do_chckbxFactor_itemStateChanged(ItemEvent arg0) {
        txtFactor.setEnabled(chckbxFactor.isSelected());
    }

    protected void do_checkBox_itemStateChanged(ItemEvent arg0) {
        this.setAlwaysOnTop(checkBox.isSelected());
    }

    protected void do_label_mouseEntered(MouseEvent arg0) {
        // lbllist.setText("<html><font color='red'>?</font></html>");
        label.setForeground(Color.red);
    }

    protected void do_label_mouseExited(MouseEvent e) {
        label.setForeground(Color.black);
    }

    protected void do_label_mouseReleased(MouseEvent e) {
//        tAOut.ln("");
//        tAOut.ln("** d2Dsub HELP **");
//        tAOut.ln("1) To subtract glass contribution, select a glass file (IMG or BIN) and click "
//                + "on subtract. The scale factor will be automatically calculated. After the first run you "
//                + "can manually \"refine\" the scale factor (Iglass*factor). You can see the results in "
//                + "the result image and compare with the initial one. The result file is written in the "
//                + "same folder as the data file with the name: " + FileUtils.getFNameNoExt(FBef).concat("-glass.bin")
//                + ".");
//        tAOut.ln("");
//        tAOut.ln("2) There are 5 ways to estimate the background:");
//        tAOut.ln("   -bkg: Each iteration estimates the background by averaging square areas around each pixel "
//               + "from the previous iteration. Set the number of pixels for the side of the square (Npix) and "
//               + "the number of iterations (Niter). It is a slow process for high Npix and Niter values. Two "
//               + "output files are written in the same folder as the data file: (a) the data file where the "
//               + "background have been subtracted: "+ FileUtils.getFNameNoExt(FBef).concat("-subBkgItX.bin")
//               + "; and (b) the background intensity that has been subtracted: "
//               + FileUtils.getFNameNoExt(FBef).concat("-BkgItX.bin") + " [X is the iteration number]. You can "
//               + "inspect for residual intensity in the last one by clicking the \"view BKG\" button.");
//        tAOut.ln("   -rad: Each iteration estimates the background from the previous iteration using the radial "
//               + "integration in the 2T of each pixel. Set the stepsize for the 2T ranges (step) and "
//               + "the number of iterations (Niter). The same output files as BKG option are written.");
//        tAOut.ln("   -arc: The same as the BKG option but using arc shaped areas (2T ring) around each pixel. "
//               + "Set the number of iterations (Niter) and the factors for the width (wdt) and angular aperture " 
//               + "(ang) for the arcs. This is a VERY slow method. The same output files as BKG option are written.");
//        tAOut.ln("   -flip: The background intensity value for each pixel (v0) is calculated as: Minimum(v0,v1,v2,v3); "
//               + "where v1,v2 and v3 are related pixels applying a reflection of the image (vertical, horizontal and " 
//               + "both). Set the which operations to use (v,h,vh), and the number of pixels (Npix) defining the square "
//               + "zone to be averaged after the operation (use 0 for only 1 pixel). The output files in this case are "
//               + FileUtils.getFNameNoExt(FBef).concat("-subBkgflip.bin") + " and "
//               + FileUtils.getFNameNoExt(FBef).concat("-Bkgflip.bin") + ". It is a FAST method but some peak intensity "
//               + "may be subtracted.");
//        tAOut.ln("   -fliparc: The same as FLIP but using an arc shaped zone for each pixel. Set the operations (v,h,vh) "
//               + "and the factors for width and angular aperture (wdt,ang). Same output files as FLIP.");
//        tAOut.ln("");
//        tAOut.ln("3) LP correction is automatically applied by clicking the button.");
//        tAOut.ln("");
//        tAOut.ln("IMPORTANT: ");
//        tAOut.ln(" - The corrections are always applied to the SOURCE image. To continue working "
//                + "with the RESULT image, click the \"Set as source\" button to set the result image as the "
//                + "source image.");
//        tAOut.ln(" - Define any excluded zones BEFORE subtracting the background [pixels with intensity=-1 (BIN files) "
//                + "or .EXZ file with the same filename] as they will lead to incorrect background subtraction.");
//        tAOut.ln(" - Clean .BIN files generated in tests or failed runs from the working folder that will not be "
//                + "used anymore and keep only the desired ones.");
//        tAOut.ln("");
//        String d2dsubHelp="<html><div style=\"text-align:justify\"> 1) To subtract glass contribution, select a glass file (IMG or BIN) and click on subtract. The scale factor will be automatically calculated. After the first run you can manually adjust the scale factor (Iglass*factor). You can see the results in the result image and compare with the initial one. The result file is written in the same folder as the data file with the name *-glass.bin<br><br>2) There are 5 ways to estimate the background:<ul><li> avsq: Each iteration estimates the background by averaging square areas around each pixel from the previous iteration. Set the number of pixels for the side of the square (Npix) and the number of iterations (Niter). It is a slow process for high Npix and Niter values. Two output files are written in the same folder as the data file: the data file where the background have been subtracted (*-subBkgItX.bin) and the background intensity that has been subtracted (*-BkgItX.bin) [X is the iteration number]. You can inspect for residual intensity in the last one by clicking the [view BKG] button.</li><li> avcirc: Each iteration estimates the background from the previous iteration using the radial integration in the 2T of each pixel. Set the stepsize for the 2T ranges (step) and the number of iterations (Niter). The same output files as BKG option are written.</li><li> avarc: The same as the BKG option but using arc shaped areas (2T ring) around each pixel. Set the number of iterations (Niter) and the factors for the width (wdt) and angular aperture (ang) for the arcs. This is a VERY slow method. The same output files as BKG option are written.</li><li> minsq: The background intensity value for each pixel (v0) is calculated as: Minimum(v0,v1,v2,v3) where v1,v2 and v3 are related pixels applying a reflection of the image (vertical, horizontal and both). Set the which operations to use (v,h,vh), and the number of pixels (Npix) defining the square zone to be averaged after the operation (use 0 for only 1 pixel). The output files in this case are *-subBkgflip.bin and *-Bkgflip.bin). It is a FAST method but some peak intensity may be subtracted.</li><li> minarc: The same as FLIP but using an arc shaped zone for each pixel. Set the operations (v,h,vh) and the factors for width and angular aperture (wdt,ang). Same output files as FLIP.</li></ul><br>3) LP correction is automatically applied by clicking the button.<br><br>IMPORTANT: <ul><li> The corrections are always applied to the SOURCE image. To continue working with the RESULT image, click the [Set as source] button to set the result image as the source image.</li><li> Define any excluded zones BEFORE subtracting the background [pixels with intensity=-1 (BIN files) or .EXZ file with the same filename] as they will lead to incorrect background subtraction.</li><li> Clean .BIN files generated in tests or failed runs from the working folder that will not be used anymore and keep only the desired ones.</li></ul></div> </html>";
        String d2dsubHelp="<html><div style=\"text-align:justify\"> 1) To subtract glass contribution, select a glass file (IMG or BIN) and click on subtract. The scale factor will be automatically calculated. After the first run you can manually adjust the scale factor (Iglass*factor). You can see the results in the result image and compare with the initial one. The result file is written in the same folder as the data file with the name *-glass.bin<br><br>2) There are 5 ways to estimate the background:<ul><li> avsq: Each iteration estimates the background by averaging square areas around each pixel from the previous iteration. Set the number of pixels for the side of the square (Npix) and the number of iterations (Niter). It is a slow process for high Npix and Niter values.</li><li> avarc: The same as previous option but using arc shaped areas (within 2T) around each pixel. Set the number of iterations (Niter) and the factors for the width (wdt) and angular aperture (ang) for the arcs. This is a VERY slow method.</li><li> avcirc: The background estimation for each pixel is the mean intensity from a radial integration (in the 2T circle containing each pixel). Set the stepsize for the 2T ranges (step).</li><li> minsq: The background intensity value for each pixel (v0) is calculated as: Minimum(v0,v1,v2,v3) where v1,v2 and v3 are related pixels applying a reflection of the image (vertical, horizontal and both). Set which operations to use (v,h,vh), and the number of pixels (Npix) defining the square zone to be averaged after the operation (use 0 to consider only 1 pixel). It is a FAST method but some peak intensity may be subtracted.</li><li> minarc: The same as FLIP but using an arc shaped zone for each pixel. Set the operations (v,h,vh) and the factors for width and angular aperture (wdt,ang).</li></ul>For all options, two output files are written in the same folder as the data file: the data file where the background have been subtracted (*-subBkg.bin) and the background intensity that has been subtracted (*-Bkg.bin). You can inspect for residual peak intensity in the last one by clicking the [view BKG] button.<br>3) LP correction is automatically applied by clicking the button.<br><br>IMPORTANT: <ul><li> The corrections are always applied to the SOURCE image. To continue working with the RESULT image, click the [Set as source] button to set the result image as the source image.</li><li> Define any excluded zones BEFORE subtracting the background [pixels with intensity=-1 (BIN files) or .EXZ file with the same filename] as they will lead to incorrect background subtraction.</li><li> Clean .BIN files generated in tests or failed runs from the working folder that will not be used anymore and keep only the desired ones.</li></ul></div> </html>";
        Help_dialog hd = new Help_dialog("d2Dsub Help",d2dsubHelp);
        hd.setSize(700,800);
        hd.setLocationRelativeTo(this);
        hd.setVisible(true);
    }

    protected void do_okButton_actionPerformed(ActionEvent arg0) {
        this.dispose();
    }

    // sets file and pattern before
    private void setAfter(File f) {
        this.FAft = f;
        this.pattAft = FileUtils.openPatternFile(this.FAft);
        ipanel_after.setImagePatt2D(this.pattAft);
        ipanel_after.repaint();
        txtResultImage.setText(f.toString());
    }

    // sets file and pattern before
    private void setBefore(File f) {
        this.FBef = f;
        this.pattBef = FileUtils.openPatternFile(this.FBef);
        ipanel_before.setImagePatt2D(this.pattBef);
        ipanel_before.repaint();
        txtPathBefore.setText(f.toString());
    }

    protected void do_comboBox_itemStateChanged(ItemEvent arg0) {
        this.activeOptions();
    }
    
    private void activeOptions(){
        String opt = (String)comboBox.getSelectedItem();
        //1r desactivem tot
        txtFactamp.setEnabled(false);
        txtFactangle.setEnabled(false);
        txtIter.setEnabled(false);
        txtN.setEnabled(false);
        txtStep.setEnabled(false);
        chckbxVer.setEnabled(false);
        chckbxHor.setEnabled(false);
        chckbxHorver.setEnabled(false);
        if(opt.trim().equalsIgnoreCase("avsq")){
            txtIter.setEnabled(true);
            txtN.setEnabled(true);
        }
        if(opt.trim().equalsIgnoreCase("avcirc")){
            txtStep.setEnabled(true);
        }
        if(opt.trim().equalsIgnoreCase("avarc")){
            txtFactamp.setEnabled(true);
            txtFactangle.setEnabled(true);
            txtIter.setEnabled(true);
        }
        if(opt.trim().equalsIgnoreCase("minsq")){
            txtN.setEnabled(true);
            chckbxVer.setEnabled(true);
            chckbxHor.setEnabled(true);
            chckbxHorver.setEnabled(true);
        }
        if(opt.trim().equalsIgnoreCase("minarc")){
            chckbxVer.setEnabled(true);
            chckbxHor.setEnabled(true);
            chckbxHorver.setEnabled(true);
            txtFactamp.setEnabled(true);
            txtFactangle.setEnabled(true);
        }
    }
    
    protected void do_chckbxCustomCmd_itemStateChanged(ItemEvent arg0) {
        btnRun.setEnabled(chckbxCustomCmd.isSelected());
    }
    
    private class d2dsubRunnable implements Runnable {
        private String opt;

        public d2dsubRunnable(String option) {
            super();
            this.opt = option;
        }

        private void openResult(String opt, File source) {
            // Aqui hem d'obrir el fitxer resultat segons la opcio que hem donat
            String sourceNoExt = FileUtils.getFNameNoExt(source);
            String resFileS = "";
            if (opt.equalsIgnoreCase("-glass")) {
                // el fitxer sortida sera source-glass.ext (ext sempre sera bin)
                resFileS = sourceNoExt.concat("-glass.bin");
            }
//            boolean iterbkg = opt.equalsIgnoreCase("-bkg")||opt.equalsIgnoreCase("-arc")||opt.equalsIgnoreCase("-rad");
//            if (iterbkg) {
//                // el fitxer sortida sera source-subBkgItX.ext (ext sempre sera bin)
//                resFileS = sourceNoExt.concat("-subBkgIt").concat(txtIter.getText().trim()).concat(".bin");
//            }
//            boolean flip = opt.equalsIgnoreCase("-flip")||opt.equalsIgnoreCase("-fliparc");
//            if (flip) {
//                // el fitxer sortida sera source-subBkgflip.ext (ext sempre sera bin)
//                resFileS = sourceNoExt.concat("-subBkgflip.bin");
//            }
            boolean bkgfile = opt.equalsIgnoreCase("-avsq")||opt.equalsIgnoreCase("-avcirc")||opt.equalsIgnoreCase("-avarc")||opt.equalsIgnoreCase("-minsq")||opt.equalsIgnoreCase("-minarc");
            if (bkgfile){
                resFileS = sourceNoExt.concat("-subBkg.bin");
            }
            if (opt.equalsIgnoreCase("-lp")) {
                // el fitxer sortida sera source-lp.ext (ext sempre sera bin)
                resFileS = sourceNoExt.concat("-lp.bin");
            }
            if (resFileS.length() > 0) {
                // Mostrem resultat
                setAfter(new File(resFileS));
                btnToRight.doClick();
                if (bkgfile) {
                    btnViewbkg.setEnabled(true);
                } else {
                    btnViewbkg.setEnabled(false);
                }
            }
        }

        @Override
        public void run() {
            // farem correr el prog a workdir, on hi ha d'haver les imatges
            try {
                String datafile = principal.getOpenedFile().getName();
                String workdir = MainFrame.getWorkdir();
                ProcessBuilder pb = null;
                String[] cmd = null;
                
                // cas del vidre
                if (opt.equalsIgnoreCase("-glass")) {
                    // comprovem si hi ha seleccionat fitxer vidre
                    if (glassD2File == null || !glassD2File.exists()) {
                        tAOut.ln("select a valid glass image first");
                        return;
                    }
                    // comprovem el cam� del fitxer vidre
                    String glassFpath = glassD2File.getPath();
                    glassFpath = glassFpath.substring(0, glassFpath.length() - glassD2File.getName().length());
                    // String dataFpath = principal.getOpenedFile().getPath();
                    // dataFpath=dataFpath.substring(0,dataFpath.length()-datafile.length());

                    if (!workdir.equalsIgnoreCase(glassFpath)) { // si son
                                                                 // diferents
                        // copiem el fitxer de vidre
                        File newGlassfile = new File(workdir + glassD2File.getName());
                        int ok = FileUtils.copyFile(glassD2File, newGlassfile);
                        if (ok >= 0) {
                            String c1 = "\u250C"; // corner sup-esquerra
                            String c2 = "\u2514"; // corner inf-esquerra
                            tAOut.ln(c1 + " " + glassD2File.toString());
                            tAOut.ln(c2 + "> copied to: " + newGlassfile.toString());
                        }
                        glassD2File = newGlassfile; // actualitzem glassD2File
                        lblGlassF.setText(glassD2File.toString());
                    }
                    String factor = "";
                    if (chckbxFactor.isSelected()) {
                        factor = txtFactor.getText();
                    }
//                    cmd = new String[] { MainFrame.getBinDir() + MainFrame.getD2dsubExec(), "130505", opt, datafile,
//                            glassD2File.getName(), factor };
                    cmd = new String[] { MainFrame.getBinDir() + MainFrame.getD2dsubExec(), opt, datafile,
                            glassD2File.getName(), factor };
                }

                if (opt.equalsIgnoreCase("-avsq")) {
                    int Npix, Niter;
                    try {
                        Npix = Integer.parseInt(txtN.getText());
                        Niter = Integer.parseInt(txtIter.getText());
                    } catch (Exception e) {
                        e.printStackTrace();
                        tAOut.ln("Enter valid Npixels and Iterations values");
                        return;
                    }
//                    cmd = new String[] { MainFrame.getBinDir() + MainFrame.getD2dsubExec(), "130505", opt, datafile,
//                            Integer.toString(Npix), Integer.toString(Niter) };
                    cmd = new String[] { MainFrame.getBinDir() + MainFrame.getD2dsubExec(), opt, datafile,
                      Integer.toString(Npix), Integer.toString(Niter) };
                }
                if (opt.equalsIgnoreCase("-avcirc")) {
                    float step;
                    try {
                        step = Float.parseFloat(txtStep.getText());
                    } catch (Exception e) {
                        e.printStackTrace();
                        tAOut.ln("Enter valid Step and Iterations values");
                        return;
                    }
                    cmd = new String[] { MainFrame.getBinDir() + MainFrame.getD2dsubExec(), opt, datafile,
                      Float.toString(step) };
                }
                //d2Dsub -arc Input_file.ext [factAmplada] [factAngle] [nIteracions]"
                if (opt.equalsIgnoreCase("-avarc")) {
                    int Niter;
                    float fAmp,fAng;
                    try {
                        fAmp = Float.parseFloat(txtFactamp.getText());
                        fAng = Float.parseFloat(txtFactangle.getText());
                        Niter = Integer.parseInt(txtIter.getText());
                    } catch (Exception e) {
                        e.printStackTrace();
                        tAOut.ln("Enter valid Step and Iterations values");
                        return;
                    }
                    cmd = new String[] { MainFrame.getBinDir() + MainFrame.getD2dsubExec(), opt, datafile,
                      Float.toString(fAmp), Float.toString(fAng), Integer.toString(Niter) };
                }
                //d2Dsub -flip Input_file.ext [fhor] [fver] [fhorver] (1 or 0) [npix]"
                if (opt.equalsIgnoreCase("-minsq")) {
                    int Npix,fhor,fver,fhorver;
                    try {
                        fhor = chckbxHor.isSelected()?1:0;
                        fver = chckbxVer.isSelected()?1:0;
                        fhorver = chckbxHorver.isSelected()?1:0;
                        Npix = Integer.parseInt(txtN.getText());
                    } catch (Exception e) {
                        e.printStackTrace();
                        tAOut.ln("Enter valid Step and Iterations values");
                        return;
                    }
                    cmd = new String[] { MainFrame.getBinDir() + MainFrame.getD2dsubExec(), opt, datafile,
                      Integer.toString(fhor), Integer.toString(fver), Integer.toString(fhorver), Integer.toString(Npix) };
                }
                //d2Dsub -fliparc Input_file.ext [fhor] [fver] [fhorver] (1 or 0) [factAmplada] [factAngle]"
                if (opt.equalsIgnoreCase("-minarc")) {
                    int fhor,fver,fhorver;
                    float fAmp,fAng;
                    try {
                        fhor = chckbxHor.isSelected()?1:0;
                        fver = chckbxVer.isSelected()?1:0;
                        fhorver = chckbxHorver.isSelected()?1:0;
                        fAmp = Float.parseFloat(txtFactamp.getText());
                        fAng = Float.parseFloat(txtFactangle.getText());
                    } catch (Exception e) {
                        e.printStackTrace();
                        tAOut.ln("Enter valid Step and Iterations values");
                        return;
                    }
                    cmd = new String[] { MainFrame.getBinDir() + MainFrame.getD2dsubExec(), opt, datafile,
                      Integer.toString(fhor), Integer.toString(fver), Integer.toString(fhorver), Float.toString(fAmp), Float.toString(fAng) };
                }

                if (opt.equalsIgnoreCase("-lp")) {
                    int ilor,ipol;
                    ilor=chckbxLorOscil.isSelected()?1:0;
                    ilor=chckbxLorPow.isSelected()?2:ilor;
                    ipol=chckbxPolSyn.isSelected()?1:0;
                    ipol=chckbxPolLab.isSelected()?2:ipol;
                    cmd = new String[] { MainFrame.getBinDir() + MainFrame.getD2dsubExec(), opt, datafile, Integer.toString(ipol),
                            Integer.toString(ilor)};
                }
                
                if (opt.equalsIgnoreCase("-ver")) {
                    cmd = new String[] { MainFrame.getBinDir() + MainFrame.getD2dsubExec(), opt};
                }
                
                //CAS CUSTOM (no tenim opt) --> HA D'ANAR AL FINAL PERQUE REESCRIUREM OPT AMB LA CORRECTE (per openresult)
                //al custom tenim algo com:
                //fliparc [fhor] [fver] [fhorver] [factAmplada] [factAngle]
                //es a dir, hem de posar el fitxer d'entrada on toca i el - a la opcio
                if (opt.equalsIgnoreCase("custom")) {
                    String[] cmdt = txtCmd.getText().trim().split(" ");
                    cmd= new String[cmdt.length+2];
                    cmd[0] = MainFrame.getBinDir() + MainFrame.getD2dsubExec(); //executable
                    cmd[1] = "-".concat(cmdt[0].trim()); //opcio
                    cmd[2] = datafile; //fitxer d'entrada
                    //ara ja venen les opcions que van de cmdt 1...n i han d'anar a cmd 3...m
                    for(int i=1;i<cmdt.length;i++){
                        cmd[i+2]=cmdt[i];
                    }
                    opt = cmd[1];
                }

                // PART COMUNA EXECUCIO I SORTIDA
                pb = new ProcessBuilder(cmd);
                pb.directory(new File(workdir));
                pb.redirectErrorStream(true);
//                System.out.println(workdir);
                pr = pb.start();

                InputStreamReader d2dsubOutput = new InputStreamReader(pr.getInputStream());
                progressBar.setIndeterminate(true);
                progressBar.setString("d2Dsub Running");
                progressBar.setStringPainted(true);

                BufferedReader reader = new BufferedReader(d2dsubOutput);
                String line = reader.readLine();
                while (line != null) {
//                    if(!line.startsWith("!!!!!"))tAOut.ln(line);
                    if(line.trim().endsWith("%")){
                        progressBar.setString(line.trim());
                    }else{
                        tAOut.ln(line);    
                    }
                    line = reader.readLine();
                }
                reader.close();
                progressBar.setIndeterminate(false);
                progressBar.setStringPainted(false);
                pr.destroy(); // per si de cas
                pr = null;

            } catch (Exception e) {
                e.printStackTrace();
                if (pr == null) {
                    tAOut.ln("*** Run STOPPED ***");
                } else {
                    tAOut.ln("*** Run ERROR ***");
                }
                pr.destroy();
                pr = null;
                progressBar.setIndeterminate(false);
                progressBar.setStringPainted(false);
                return;
            }

            // un cop aqui s'hauria d'haver acabat l'execucio
            if(!opt.equalsIgnoreCase("-ver"))tAOut.stat("End of d2Dsub execution");
            this.openResult(opt, principal.getOpenedFile());
        }

    }
    
    private class NoneSelectedButtonGroup extends ButtonGroup {

        @Override
        public void setSelected(ButtonModel model, boolean selected) {

          if (selected) {

            super.setSelected(model, selected);

          } else {

            clearSelection();
          }
        }
      }
}