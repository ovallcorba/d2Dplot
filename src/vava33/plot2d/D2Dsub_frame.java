// TODO: Inicialment cal comprovar si existeix fitxer EXZ i llegir-lo.
// si no existeix, cada cop que s'executi d2dsub fer que es comprovi si existeix fitxer EXZ 
// i sinó crear-lo amb la informació introduïda.

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

    /**
     * Create the dialog.
     */
    public D2Dsub_frame(MainFrame mf) {
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
        setBounds(x, y, 820, 780);
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
            this.splitPane_1.setOrientation(JSplitPane.VERTICAL_SPLIT);
            GridBagConstraints gbc_splitPane_1 = new GridBagConstraints();
            gbc_splitPane_1.fill = GridBagConstraints.BOTH;
            gbc_splitPane_1.gridx = 0;
            gbc_splitPane_1.gridy = 0;
            contentPanel.add(this.splitPane_1, gbc_splitPane_1);
            this.splitPane = new JSplitPane();
            this.splitPane_1.setLeftComponent(this.splitPane);
            this.splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
            this.panel_top = new JPanel();
            this.splitPane.setLeftComponent(this.panel_top);
            GridBagLayout gbl_panel_top = new GridBagLayout();
            gbl_panel_top.columnWidths = new int[] { 280, 175, 0, 100 };
            gbl_panel_top.rowHeights = new int[] { 0, 0, 0, 0 };
            gbl_panel_top.columnWeights = new double[] { 1.0, 1.0, 0.0, 1.0 };
            gbl_panel_top.rowWeights = new double[] { 0.0, 1.0, 0.0, Double.MIN_VALUE };
            this.panel_top.setLayout(gbl_panel_top);
            {
                this.panelGlass = new JPanel();
                this.panelGlass.setBorder(new TitledBorder(null, "Glass subtraction", TitledBorder.LEADING,
                        TitledBorder.TOP, null, null));
                GridBagConstraints gbc_panelGlass = new GridBagConstraints();
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
                    this.btnSelectFile.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent arg0) {
                            do_btnSelectFile_actionPerformed(arg0);
                        }
                    });
                    GridBagConstraints gbc_btnSelectFile = new GridBagConstraints();
                    gbc_btnSelectFile.anchor = GridBagConstraints.WEST;
                    gbc_btnSelectFile.insets = new Insets(0, 0, 5, 5);
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
                    gbc_chckbxFactor.insets = new Insets(0, 0, 5, 5);
                    gbc_chckbxFactor.gridx = 1;
                    gbc_chckbxFactor.gridy = 0;
                    this.panelGlass.add(this.chckbxFactor, gbc_chckbxFactor);
                }
                {
                    this.txtFactor = new JTextField();
                    this.txtFactor.setEnabled(false);
                    this.txtFactor.setText("1.000");
                    this.txtFactor.setColumns(6);
                    GridBagConstraints gbc_txtFactor = new GridBagConstraints();
                    gbc_txtFactor.fill = GridBagConstraints.HORIZONTAL;
                    gbc_txtFactor.insets = new Insets(0, 0, 5, 0);
                    gbc_txtFactor.gridx = 2;
                    gbc_txtFactor.gridy = 0;
                    this.panelGlass.add(this.txtFactor, gbc_txtFactor);
                }
                {
                    this.btnSubGlass = new JButton("Subtract!");
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
                        gbc_lblGlassF.insets = new Insets(0, 0, 0, 5);
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
                this.panelBkg.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"),
                        "Bkg estimation/subtraction", TitledBorder.LEADING, TitledBorder.TOP, null, null));
                GridBagConstraints gbc_panelBkg = new GridBagConstraints();
                gbc_panelBkg.gridwidth = 2;
                gbc_panelBkg.gridheight = 2;
                gbc_panelBkg.insets = new Insets(5, 0, 5, 5);
                gbc_panelBkg.fill = GridBagConstraints.BOTH;
                gbc_panelBkg.gridx = 1;
                gbc_panelBkg.gridy = 0;
                this.panel_top.add(this.panelBkg, gbc_panelBkg);
                GridBagLayout gbl_panelBkg = new GridBagLayout();
                gbl_panelBkg.columnWidths = new int[] { 0, 0, 0, 0 };
                gbl_panelBkg.rowHeights = new int[] { 0, 0, 0 };
                gbl_panelBkg.columnWeights = new double[] { 1.0, 1.0, 0.0, Double.MIN_VALUE };
                gbl_panelBkg.rowWeights = new double[] { 0.0, 0.0, Double.MIN_VALUE };
                this.panelBkg.setLayout(gbl_panelBkg);
                {
                    this.lblN = new JLabel("Npix=");
                    GridBagConstraints gbc_lblN = new GridBagConstraints();
                    gbc_lblN.insets = new Insets(0, 0, 5, 5);
                    gbc_lblN.anchor = GridBagConstraints.EAST;
                    gbc_lblN.gridx = 0;
                    gbc_lblN.gridy = 0;
                    this.panelBkg.add(this.lblN, gbc_lblN);
                }
                {
                    this.txtN = new JTextField();
                    this.txtN.setText("20");
                    GridBagConstraints gbc_txtN = new GridBagConstraints();
                    gbc_txtN.fill = GridBagConstraints.HORIZONTAL;
                    gbc_txtN.insets = new Insets(0, 0, 5, 5);
                    gbc_txtN.gridx = 1;
                    gbc_txtN.gridy = 0;
                    this.panelBkg.add(this.txtN, gbc_txtN);
                    this.txtN.setColumns(4);
                }
                this.btnSubBkg = new JButton("Subtract!");
                this.btnSubBkg.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        do_btnSubBkg_actionPerformed(e);
                    }
                });
                GridBagConstraints gbc_btnSubBkg = new GridBagConstraints();
                gbc_btnSubBkg.insets = new Insets(0, 0, 5, 0);
                gbc_btnSubBkg.gridx = 2;
                gbc_btnSubBkg.gridy = 0;
                this.panelBkg.add(this.btnSubBkg, gbc_btnSubBkg);
                {
                    this.lblIter = new JLabel("Iter=");
                    GridBagConstraints gbc_lblIter = new GridBagConstraints();
                    gbc_lblIter.insets = new Insets(0, 0, 0, 5);
                    gbc_lblIter.anchor = GridBagConstraints.EAST;
                    gbc_lblIter.gridx = 0;
                    gbc_lblIter.gridy = 1;
                    this.panelBkg.add(this.lblIter, gbc_lblIter);
                }
                {
                    {
                        this.txtIter = new JTextField();
                        this.txtIter.setText("10");
                        GridBagConstraints gbc_txtIter = new GridBagConstraints();
                        gbc_txtIter.fill = GridBagConstraints.HORIZONTAL;
                        gbc_txtIter.anchor = GridBagConstraints.NORTH;
                        gbc_txtIter.insets = new Insets(0, 0, 0, 5);
                        gbc_txtIter.gridx = 1;
                        gbc_txtIter.gridy = 1;
                        this.panelBkg.add(this.txtIter, gbc_txtIter);
                        this.txtIter.setColumns(4);
                    }
                }
                {
                    this.btnViewbkg = new JButton("view BKG");
                    this.btnViewbkg.setEnabled(false);
                    this.btnViewbkg.setMargin(new Insets(2, 5, 2, 5));
                    this.btnViewbkg.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            do_btnViewbkg_actionPerformed(e);
                        }
                    });
                    GridBagConstraints gbc_btnViewbkg = new GridBagConstraints();
                    gbc_btnViewbkg.gridx = 2;
                    gbc_btnViewbkg.gridy = 1;
                    this.panelBkg.add(this.btnViewbkg, gbc_btnViewbkg);
                }
            }
            {
                this.panel = new JPanel();
                this.panel.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "LP correction",
                        TitledBorder.LEADING, TitledBorder.TOP, null, null));
                GridBagConstraints gbc_panel = new GridBagConstraints();
                gbc_panel.gridheight = 2;
                gbc_panel.insets = new Insets(5, 0, 5, 5);
                gbc_panel.fill = GridBagConstraints.BOTH;
                gbc_panel.gridx = 3;
                gbc_panel.gridy = 0;
                this.panel_top.add(this.panel, gbc_panel);
                GridBagLayout gbl_panel = new GridBagLayout();
                gbl_panel.columnWidths = new int[] { 0, 0 };
                gbl_panel.rowHeights = new int[] { 0, 0 };
                gbl_panel.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
                gbl_panel.rowWeights = new double[] { 1.0, Double.MIN_VALUE };
                this.panel.setLayout(gbl_panel);
                {
                    this.btnDoIt = new JButton("Do it!");
                    this.btnDoIt.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            do_btnDoIt_actionPerformed(e);
                        }
                    });
                    this.btnDoIt.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
                    GridBagConstraints gbc_btnDoIt = new GridBagConstraints();
                    gbc_btnDoIt.gridx = 0;
                    gbc_btnDoIt.gridy = 0;
                    this.panel.add(this.btnDoIt, gbc_btnDoIt);
                }
            }
            {
                this.progressBar = new JProgressBar();
                GridBagConstraints gbc_progressBar = new GridBagConstraints();
                gbc_progressBar.gridwidth = 2;
                gbc_progressBar.fill = GridBagConstraints.HORIZONTAL;
                gbc_progressBar.insets = new Insets(0, 5, 5, 5);
                gbc_progressBar.gridx = 0;
                gbc_progressBar.gridy = 2;
                this.panel_top.add(this.progressBar, gbc_progressBar);
            }
            {
                this.btnStop = new JButton("Stop calculation!");
                GridBagConstraints gbc_btnStop = new GridBagConstraints();
                gbc_btnStop.fill = GridBagConstraints.HORIZONTAL;
                gbc_btnStop.insets = new Insets(0, 0, 5, 5);
                gbc_btnStop.gridwidth = 2;
                gbc_btnStop.gridx = 2;
                gbc_btnStop.gridy = 2;
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
        this.splitPane_1.setDividerLocation(250);

        tAOut.ln("** d2Dsub: Background estimation/subtraction & LP correction **");
        this.setBefore(mf.getOpenedFile());
        tAOut.ln("Initial file= " + this.FBef.toString());
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
        Thread d2dsub = new Thread(new d2dsubRunnable("-bkg"));
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

    protected void do_btnViewbkg_actionPerformed(ActionEvent e) {
        // fitxer de fons before-bkgItX.ext (ext sempre sera bin)

        String bkgFileS = FileUtils.getFNameNoExt(FBef); // primer treiem l'extensio a before
        bkgFileS = bkgFileS.concat("-BkgIt").concat(txtIter.getText().trim()).concat(".bin");
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
        tAOut.ln("");
        tAOut.ln("** d2Dsub HELP **");
        tAOut.ln("1) To subtract glass contribution, select a glass file (IMG or BIN) and click "
                + "on subtract. The scale factor will be automatically calculated. After the first run you "
                + "can manually \"refine\" the scale factor (Iglass*factor). You can see the results in "
                + "the result image and compare with the initial one. The result file is written in the "
                + "same folder as the data file with the name: " + FileUtils.getFNameNoExt(FBef).concat("-glass.bin")
                + ".");
        tAOut.ln("");
        tAOut.ln("2) To estimate and subtract the background, set the number of pixels (Npix) which is the "
                + "neighbour pixels that will be used to estimate the background for each pixel, and the number "
                + "of iterations (Niter). It is a slow process and a lot of time is required for high Npix and "
                + "Niter values. Two output files are written in the same folder as the data file: (a) the data "
                + "file where the background has been subtracted:  "
                + FileUtils.getFNameNoExt(FBef).concat("-subBkgItX.bin")
                + "; and (b) the background intensity that has been subtracted: "
                + FileUtils.getFNameNoExt(FBef).concat("-BkgItX.bin") + " [X is the iteration number]. You can "
                + "inspect for residual intensity in the last one by clicking the \"view BKG\" button.");
        tAOut.ln("");
        tAOut.ln("3) LP correction is automatically applied by clicking the button.");
        tAOut.ln("");
        tAOut.ln("IMPORTANT: ");
        tAOut.ln(" - The corrections are always applied to the SOURCE image. To continue working "
                + "with the RESULT image, click the \"Set as source\" button to set the result image as the "
                + "source image.");
        tAOut.ln(" - Define any excluded zones BEFORE subtracting the background [pixels with intensity=-1 (BIN files) "
                + "or .EXZ file with the same filename] as they will lead to incorrect background subtraction.");
        tAOut.ln(" - Clean .BIN files generated in tests or failed runs from the working folder that will not be "
                + "used anymore and keep only the desired ones.");
        tAOut.ln("");
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
            if (opt.equalsIgnoreCase("-bkg")) {
                // el fitxer sortida sera source-subBkgItX.ext (ext sempre sera
                // bin)
                resFileS = sourceNoExt.concat("-subBkgIt").concat(txtIter.getText().trim()).concat(".bin");
            }
            if (opt.equalsIgnoreCase("-lp")) {
                // el fitxer sortida sera source-lp.ext (ext sempre sera bin)
                resFileS = sourceNoExt.concat("-lp.bin");
            }
            if (resFileS.length() > 0) {
                // Mostrem resultat
                setAfter(new File(resFileS));
                btnToRight.doClick();
                if (opt.equalsIgnoreCase("-bkg")) {
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
                    // comprovem el camí del fitxer vidre
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

                if (opt.equalsIgnoreCase("-bkg")) {
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

                if (opt.equalsIgnoreCase("-lp")) {
//                    cmd = new String[] { MainFrame.getBinDir() + MainFrame.getD2dsubExec(), "130505", opt, datafile };
                   cmd = new String[] { MainFrame.getBinDir() + MainFrame.getD2dsubExec(), opt, datafile };
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
            tAOut.stat("End of d2Dsub execution");
            this.openResult(opt, principal.getOpenedFile());
        }

    }
}