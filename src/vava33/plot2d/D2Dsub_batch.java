// TODO: Inicialment cal comprovar si existeix fitxer EXZ i llegir-lo.
// si no existeix, cada cop que s'executi d2dsub fer que es comprovi si existeix fitxer EXZ 
// i sin� crear-lo amb la informaci� introdu�da.

package vava33.plot2d;

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
import java.io.File;
import java.util.Iterator;
import java.util.concurrent.ArrayBlockingQueue;

import javax.swing.ButtonModel;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
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

import vava33.plot2d.auxi.ImgOps;
import vava33.plot2d.auxi.ImgFileUtils;
import vava33.plot2d.auxi.Pattern2D;

import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ButtonGroup;

import com.vava33.jutils.FileUtils;
import com.vava33.jutils.LogJTextArea;

import javax.swing.JSeparator;

import javax.swing.JList;

public class D2Dsub_batch extends JFrame {

    private static final long serialVersionUID = 7730139568601028032L;
    private JButton btnSelectFile;
    private JButton btnStop;
    private JCheckBox chckbxFactor;
    private JCheckBox checkBox;
    private final JPanel contentPanel = new JPanel();
    private JLabel label;
    private JLabel lblGlassF;
    private JLabel lblIter;
    private JLabel lblN;
    private JPanel panel;
    private JPanel panel_output;
    private JPanel panel_top;
    private JPanel panelBkg;
    private JPanel panelGlass;
    private JProgressBar progressBar;
    private JScrollPane scrollPane_1;
    private JSplitPane splitPane;
    private JTextField txtFactor;
    private JTextField txtIter;
    private JTextField txtN;
    private LogJTextArea tAOut;
    
    private File glassD2File;
    private JComboBox comboBox;
    private JTextField txtAmplada;
    private JTextField txtAngle;
    private JLabel lblAwidth;
    private JLabel lblAangle;
    private JCheckBox chckbxHor;
    private JCheckBox chckbxVer;
    private JCheckBox chckbxHorver;
    private JTextField txtStep;
    private JLabel lblStep;
    private JCheckBox chckbxLorOscil;
    private JLabel lblLor;
    private JLabel lblPol;
    private JCheckBox chckbxLorPow;
    private JCheckBox chckbxPolSyn;
    private JCheckBox chckbxPolLab;
    private final NoneSelectedButtonGroup buttonGroup = new NoneSelectedButtonGroup();
    private final NoneSelectedButtonGroup buttonGroup_1 = new NoneSelectedButtonGroup();
    private JCheckBox chk_doGlass;
    private JCheckBox chk_doBkg;
    private JLabel lblMehod;
    private JCheckBox chk_doLP;
    private JSeparator separator;
    private JSeparator separator_1;
    private JSeparator separator_2;
    private JSeparator separator_3;
    private JSeparator separator_4;
    private JButton btnRun;
    private JPanel panel_runcontrols;
    private JPanel panel_1;
    private JButton btnSelectFiles;
    private JList<File> listfiles;
    private JButton btnClear;
    private JScrollPane scrollPane;
    
    /**
     * Create the dialog.
     */
    public D2Dsub_batch(MainFrame mf) {
        UIManager.put("ProgressBar.selectionBackground", Color.black);
        setIconImage(Toolkit.getDefaultToolkit().getImage(D2Dsub_batch.class.getResource("/img/Icona.png")));
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Background subtraction and LP correction (batch processing)");
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(this.contentPanel, BorderLayout.CENTER);
        GridBagLayout gbl_contentPanel = new GridBagLayout();
        gbl_contentPanel.columnWidths = new int[] { 0, 0 };
        gbl_contentPanel.rowHeights = new int[] { 0, 0 };
        gbl_contentPanel.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
        gbl_contentPanel.rowWeights = new double[] { 1.0, Double.MIN_VALUE };
        contentPanel.setLayout(gbl_contentPanel);
        this.splitPane = new JSplitPane();
        GridBagConstraints gbc_splitPane = new GridBagConstraints();
        gbc_splitPane.fill = GridBagConstraints.BOTH;
        gbc_splitPane.gridx = 0;
        gbc_splitPane.gridy = 0;
        contentPanel.add(splitPane, gbc_splitPane);
        splitPane.setEnabled(false);
        this.splitPane.setBorder(null);
        this.splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
        this.panel_top = new JPanel();
        this.splitPane.setLeftComponent(this.panel_top);
        GridBagLayout gbl_panel_top = new GridBagLayout();
        gbl_panel_top.columnWidths = new int[] { 0, 0 };
        gbl_panel_top.rowHeights = new int[] { 0, 0, 0, 0, 0 };
        gbl_panel_top.columnWeights = new double[] { 0.0, 1.0 };
        gbl_panel_top.rowWeights = new double[] { 1.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };
        this.panel_top.setLayout(gbl_panel_top);
        {
        	panel_1 = new JPanel();
        	GridBagConstraints gbc_panel_1 = new GridBagConstraints();
        	gbc_panel_1.gridheight = 4;
        	gbc_panel_1.insets = new Insets(0, 0, 0, 5);
        	gbc_panel_1.fill = GridBagConstraints.BOTH;
        	gbc_panel_1.gridx = 0;
        	gbc_panel_1.gridy = 0;
        	panel_top.add(panel_1, gbc_panel_1);
        	GridBagLayout gbl_panel_1 = new GridBagLayout();
        	gbl_panel_1.columnWidths = new int[]{48, 0, 0};
        	gbl_panel_1.rowHeights = new int[]{25, 0, 0};
        	gbl_panel_1.columnWeights = new double[]{1.0, 0.0, Double.MIN_VALUE};
        	gbl_panel_1.rowWeights = new double[]{0.0, 1.0, Double.MIN_VALUE};
        	panel_1.setLayout(gbl_panel_1);
        	{
        		btnSelectFiles = new JButton("Select files");
        		btnSelectFiles.addActionListener(new ActionListener() {
        			public void actionPerformed(ActionEvent arg0) {
        				do_btnSelectFiles_actionPerformed(arg0);
        			}
        		});
        		GridBagConstraints gbc_btnSelectFiles = new GridBagConstraints();
        		gbc_btnSelectFiles.insets = new Insets(5, 5, 5, 5);
        		gbc_btnSelectFiles.anchor = GridBagConstraints.NORTHWEST;
        		gbc_btnSelectFiles.gridx = 0;
        		gbc_btnSelectFiles.gridy = 0;
        		panel_1.add(btnSelectFiles, gbc_btnSelectFiles);
        	}
        	{
        		btnClear = new JButton("clear");
        		btnClear.addActionListener(new ActionListener() {
        			public void actionPerformed(ActionEvent arg0) {
        				do_btnClear_actionPerformed(arg0);
        			}
        		});
        		GridBagConstraints gbc_btnClear = new GridBagConstraints();
        		gbc_btnClear.insets = new Insets(5, 0, 5, 5);
        		gbc_btnClear.gridx = 1;
        		gbc_btnClear.gridy = 0;
        		panel_1.add(btnClear, gbc_btnClear);
        	}
        	{
        		scrollPane = new JScrollPane();
        		GridBagConstraints gbc_scrollPane = new GridBagConstraints();
        		gbc_scrollPane.fill = GridBagConstraints.BOTH;
        		gbc_scrollPane.gridwidth = 2;
        		gbc_scrollPane.insets = new Insets(0, 5, 0, 5);
        		gbc_scrollPane.gridx = 0;
        		gbc_scrollPane.gridy = 1;
        		panel_1.add(scrollPane, gbc_scrollPane);
        		{
        			listfiles = new JList<File>();
        			scrollPane.setViewportView(listfiles);
        		}
        	}
        }
        this.panelGlass = new JPanel();
        this.panelGlass.setBorder(new TitledBorder(null, "Glass subtraction", TitledBorder.LEADING,
                TitledBorder.TOP, null, null));
        GridBagConstraints gbc_panelGlass = new GridBagConstraints();
        gbc_panelGlass.insets = new Insets(5, 0, 5, 5);
        gbc_panelGlass.fill = GridBagConstraints.BOTH;
        gbc_panelGlass.gridx = 1;
        gbc_panelGlass.gridy = 0;
        this.panel_top.add(this.panelGlass, gbc_panelGlass);
        GridBagLayout gbl_panelGlass = new GridBagLayout();
        gbl_panelGlass.columnWidths = new int[] { 0, 0, 0, 0, 0, 0, 0 };
        gbl_panelGlass.rowHeights = new int[] { 0, 0 };
        gbl_panelGlass.columnWeights = new double[] { 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, Double.MIN_VALUE };
        gbl_panelGlass.rowWeights = new double[] { 0.0, Double.MIN_VALUE };
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
            {
            	chk_doGlass = new JCheckBox("Do it!");
            	GridBagConstraints gbc_chk_doGlass = new GridBagConstraints();
            	gbc_chk_doGlass.insets = new Insets(2, 0, 0, 5);
            	gbc_chk_doGlass.gridx = 0;
            	gbc_chk_doGlass.gridy = 0;
            	panelGlass.add(chk_doGlass, gbc_chk_doGlass);
            }
            {
            	separator_3 = new JSeparator();
            	separator_3.setOrientation(SwingConstants.VERTICAL);
            	GridBagConstraints gbc_separator_3 = new GridBagConstraints();
            	gbc_separator_3.fill = GridBagConstraints.VERTICAL;
            	gbc_separator_3.insets = new Insets(0, 0, 0, 5);
            	gbc_separator_3.gridx = 1;
            	gbc_separator_3.gridy = 0;
            	panelGlass.add(separator_3, gbc_separator_3);
            }
            GridBagConstraints gbc_btnSelectFile = new GridBagConstraints();
            gbc_btnSelectFile.anchor = GridBagConstraints.WEST;
            gbc_btnSelectFile.insets = new Insets(2, 2, 0, 5);
            gbc_btnSelectFile.gridx = 2;
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
            {
                this.lblGlassF = new JLabel("(no glass data file selected)");
                GridBagConstraints gbc_lblGlassF = new GridBagConstraints();
                gbc_lblGlassF.anchor = GridBagConstraints.WEST;
                gbc_lblGlassF.insets = new Insets(2, 2, 0, 5);
                gbc_lblGlassF.gridx = 3;
                gbc_lblGlassF.gridy = 0;
                this.panelGlass.add(this.lblGlassF, gbc_lblGlassF);
            }
            GridBagConstraints gbc_chckbxFactor = new GridBagConstraints();
            gbc_chckbxFactor.insets = new Insets(2, 0, 0, 5);
            gbc_chckbxFactor.gridx = 4;
            gbc_chckbxFactor.gridy = 0;
            this.panelGlass.add(this.chckbxFactor, gbc_chckbxFactor);
        }
        {
            this.txtFactor = new JTextField();
            txtFactor.setBackground(Color.WHITE);
            this.txtFactor.setEnabled(false);
            this.txtFactor.setText("1.000");
            this.txtFactor.setColumns(4);
            GridBagConstraints gbc_txtFactor = new GridBagConstraints();
            gbc_txtFactor.fill = GridBagConstraints.HORIZONTAL;
            gbc_txtFactor.insets = new Insets(2, 0, 0, 2);
            gbc_txtFactor.gridx = 5;
            gbc_txtFactor.gridy = 0;
            this.panelGlass.add(this.txtFactor, gbc_txtFactor);
        }
        this.panelBkg = new JPanel();
        this.panelBkg.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Background estimation & subtraction", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
        GridBagConstraints gbc_panelBkg = new GridBagConstraints();
        gbc_panelBkg.insets = new Insets(5, 0, 5, 5);
        gbc_panelBkg.fill = GridBagConstraints.BOTH;
        gbc_panelBkg.gridx = 1;
        gbc_panelBkg.gridy = 1;
        this.panel_top.add(this.panelBkg, gbc_panelBkg);
        GridBagLayout gbl_panelBkg = new GridBagLayout();
        gbl_panelBkg.columnWidths = new int[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
        gbl_panelBkg.rowHeights = new int[] { 0, 0 };
        gbl_panelBkg.columnWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 1.0, 0.0, 0.0, 1.0, 0.0, 1.0, 0.0, 1.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };
        gbl_panelBkg.rowWeights = new double[] { 0.0, Double.MIN_VALUE };
        this.panelBkg.setLayout(gbl_panelBkg);
        {
            this.comboBox = new JComboBox();
            this.comboBox.addItemListener(new ItemListener() {
                public void itemStateChanged(ItemEvent arg0) {
                    do_comboBox_itemStateChanged(arg0);
                }
            });
            {
            	chk_doBkg = new JCheckBox("Do it!");
            	GridBagConstraints gbc_chk_doBkg = new GridBagConstraints();
            	gbc_chk_doBkg.insets = new Insets(2, 0, 0, 5);
            	gbc_chk_doBkg.gridx = 0;
            	gbc_chk_doBkg.gridy = 0;
            	panelBkg.add(chk_doBkg, gbc_chk_doBkg);
            }
            {
            	separator_2 = new JSeparator();
            	separator_2.setOrientation(SwingConstants.VERTICAL);
            	GridBagConstraints gbc_separator_2 = new GridBagConstraints();
            	gbc_separator_2.fill = GridBagConstraints.VERTICAL;
            	gbc_separator_2.insets = new Insets(0, 0, 0, 5);
            	gbc_separator_2.gridx = 1;
            	gbc_separator_2.gridy = 0;
            	panelBkg.add(separator_2, gbc_separator_2);
            }
            {
            	lblMehod = new JLabel("Mehod");
            	GridBagConstraints gbc_lblMehod = new GridBagConstraints();
            	gbc_lblMehod.insets = new Insets(2, 0, 0, 5);
            	gbc_lblMehod.anchor = GridBagConstraints.EAST;
            	gbc_lblMehod.gridx = 2;
            	gbc_lblMehod.gridy = 0;
            	panelBkg.add(lblMehod, gbc_lblMehod);
            }
            this.comboBox.setModel(new DefaultComboBoxModel(new String[] {"avsq", "avarc", "avcirc", "minsq", "minarc"}));
            GridBagConstraints gbc_comboBox = new GridBagConstraints();
            gbc_comboBox.insets = new Insets(2, 2, 0, 5);
            gbc_comboBox.fill = GridBagConstraints.HORIZONTAL;
            gbc_comboBox.gridx = 3;
            gbc_comboBox.gridy = 0;
            this.panelBkg.add(this.comboBox, gbc_comboBox);
        }
        {
            this.lblN = new JLabel("Npix=");
            GridBagConstraints gbc_lblN = new GridBagConstraints();
            gbc_lblN.insets = new Insets(2, 0, 0, 5);
            gbc_lblN.anchor = GridBagConstraints.EAST;
            gbc_lblN.gridx = 4;
            gbc_lblN.gridy = 0;
            this.panelBkg.add(this.lblN, gbc_lblN);
        }
        {
            this.txtN = new JTextField();
            txtN.setBackground(Color.WHITE);
            this.txtN.setText("20");
            GridBagConstraints gbc_txtN = new GridBagConstraints();
            gbc_txtN.fill = GridBagConstraints.HORIZONTAL;
            gbc_txtN.insets = new Insets(2, 0, 0, 5);
            gbc_txtN.gridx = 5;
            gbc_txtN.gridy = 0;
            this.panelBkg.add(this.txtN, gbc_txtN);
            this.txtN.setColumns(3);
        }
        {
            this.lblIter = new JLabel("Iter=");
            GridBagConstraints gbc_lblIter = new GridBagConstraints();
            gbc_lblIter.insets = new Insets(2, 0, 0, 5);
            gbc_lblIter.anchor = GridBagConstraints.EAST;
            gbc_lblIter.gridx = 6;
            gbc_lblIter.gridy = 0;
            this.panelBkg.add(this.lblIter, gbc_lblIter);
        }
        {
            this.txtIter = new JTextField();
            txtIter.setBackground(Color.WHITE);
            this.txtIter.setText("10");
            GridBagConstraints gbc_txtIter = new GridBagConstraints();
            gbc_txtIter.fill = GridBagConstraints.HORIZONTAL;
            gbc_txtIter.insets = new Insets(2, 0, 0, 5);
            gbc_txtIter.gridx = 7;
            gbc_txtIter.gridy = 0;
            this.panelBkg.add(this.txtIter, gbc_txtIter);
            this.txtIter.setColumns(2);
        }
        {
        	separator_4 = new JSeparator();
        	separator_4.setOrientation(SwingConstants.VERTICAL);
        	GridBagConstraints gbc_separator_4 = new GridBagConstraints();
        	gbc_separator_4.fill = GridBagConstraints.VERTICAL;
        	gbc_separator_4.insets = new Insets(0, 0, 0, 5);
        	gbc_separator_4.gridx = 8;
        	gbc_separator_4.gridy = 0;
        	panelBkg.add(separator_4, gbc_separator_4);
        }
        {
            this.lblAwidth = new JLabel("wth=");
            GridBagConstraints gbc_lblAwidth = new GridBagConstraints();
            gbc_lblAwidth.insets = new Insets(2, 0, 0, 5);
            gbc_lblAwidth.anchor = GridBagConstraints.EAST;
            gbc_lblAwidth.gridx = 9;
            gbc_lblAwidth.gridy = 0;
            this.panelBkg.add(this.lblAwidth, gbc_lblAwidth);
        }
        {
            this.txtAmplada = new JTextField();
            txtAmplada.setBackground(Color.WHITE);
            this.txtAmplada.setText("5.0");
            GridBagConstraints gbc_txtAmplada = new GridBagConstraints();
            gbc_txtAmplada.insets = new Insets(2, 0, 0, 5);
            gbc_txtAmplada.fill = GridBagConstraints.HORIZONTAL;
            gbc_txtAmplada.gridx = 10;
            gbc_txtAmplada.gridy = 0;
            this.panelBkg.add(this.txtAmplada, gbc_txtAmplada);
            this.txtAmplada.setColumns(4);
        }
        {
            this.lblAangle = new JLabel("ang=");
            GridBagConstraints gbc_lblAangle = new GridBagConstraints();
            gbc_lblAangle.insets = new Insets(2, 0, 0, 5);
            gbc_lblAangle.anchor = GridBagConstraints.EAST;
            gbc_lblAangle.gridx = 11;
            gbc_lblAangle.gridy = 0;
            this.panelBkg.add(this.lblAangle, gbc_lblAangle);
        }
        {
            this.txtAngle = new JTextField();
            txtAngle.setBackground(Color.WHITE);
            this.txtAngle.setText("4.0");
            GridBagConstraints gbc_txtAngle = new GridBagConstraints();
            gbc_txtAngle.insets = new Insets(2, 0, 0, 5);
            gbc_txtAngle.fill = GridBagConstraints.HORIZONTAL;
            gbc_txtAngle.gridx = 12;
            gbc_txtAngle.gridy = 0;
            this.panelBkg.add(this.txtAngle, gbc_txtAngle);
            this.txtAngle.setColumns(4);
        }
        {
            this.lblStep = new JLabel("step=");
            GridBagConstraints gbc_lblStep = new GridBagConstraints();
            gbc_lblStep.insets = new Insets(2, 0, 0, 5);
            gbc_lblStep.anchor = GridBagConstraints.EAST;
            gbc_lblStep.gridx = 13;
            gbc_lblStep.gridy = 0;
            this.panelBkg.add(this.lblStep, gbc_lblStep);
        }
        {
            this.txtStep = new JTextField();
            txtStep.setBackground(Color.WHITE);
            this.txtStep.setText("0.05");
            GridBagConstraints gbc_txtStep = new GridBagConstraints();
            gbc_txtStep.insets = new Insets(2, 0, 0, 5);
            gbc_txtStep.fill = GridBagConstraints.HORIZONTAL;
            gbc_txtStep.gridx = 14;
            gbc_txtStep.gridy = 0;
            this.panelBkg.add(this.txtStep, gbc_txtStep);
            this.txtStep.setColumns(4);
        }
        {
            this.chckbxHor = new JCheckBox("h");
            this.chckbxHor.setSelected(true);
            GridBagConstraints gbc_chckbxHor = new GridBagConstraints();
            gbc_chckbxHor.anchor = GridBagConstraints.EAST;
            gbc_chckbxHor.insets = new Insets(2, 0, 0, 5);
            gbc_chckbxHor.gridx = 15;
            gbc_chckbxHor.gridy = 0;
            this.panelBkg.add(this.chckbxHor, gbc_chckbxHor);
        }
        {
            this.chckbxVer = new JCheckBox("v");
            GridBagConstraints gbc_chckbxVer = new GridBagConstraints();
            gbc_chckbxVer.insets = new Insets(2, 0, 0, 5);
            gbc_chckbxVer.gridx = 16;
            gbc_chckbxVer.gridy = 0;
            this.panelBkg.add(this.chckbxVer, gbc_chckbxVer);
        }
        {
            this.chckbxHorver = new JCheckBox("hv");
            GridBagConstraints gbc_chckbxHorver = new GridBagConstraints();
            gbc_chckbxHorver.anchor = GridBagConstraints.WEST;
            gbc_chckbxHorver.insets = new Insets(2, 0, 0, 2);
            gbc_chckbxHorver.gridx = 17;
            gbc_chckbxHorver.gridy = 0;
            this.panelBkg.add(this.chckbxHorver, gbc_chckbxHorver);
        }
        this.panel = new JPanel();
        this.panel.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Lorentz & Polarization corrections", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
        GridBagConstraints gbc_panel = new GridBagConstraints();
        gbc_panel.insets = new Insets(5, 0, 5, 5);
        gbc_panel.fill = GridBagConstraints.BOTH;
        gbc_panel.gridx = 1;
        gbc_panel.gridy = 2;
        this.panel_top.add(this.panel, gbc_panel);
        GridBagLayout gbl_panel = new GridBagLayout();
        gbl_panel.columnWidths = new int[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
        gbl_panel.rowHeights = new int[] { 0, 0 };
        gbl_panel.columnWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };
        gbl_panel.rowWeights = new double[] { 0.0, Double.MIN_VALUE };
        this.panel.setLayout(gbl_panel);
        {
        	chk_doLP = new JCheckBox("Do it!");
        	GridBagConstraints gbc_chk_doLP = new GridBagConstraints();
        	gbc_chk_doLP.insets = new Insets(2, 0, 0, 5);
        	gbc_chk_doLP.gridx = 0;
        	gbc_chk_doLP.gridy = 0;
        	panel.add(chk_doLP, gbc_chk_doLP);
        }
        {
        	separator_1 = new JSeparator();
        	separator_1.setOrientation(SwingConstants.VERTICAL);
        	GridBagConstraints gbc_separator_1 = new GridBagConstraints();
        	gbc_separator_1.fill = GridBagConstraints.VERTICAL;
        	gbc_separator_1.insets = new Insets(0, 0, 0, 5);
        	gbc_separator_1.gridx = 1;
        	gbc_separator_1.gridy = 0;
        	panel.add(separator_1, gbc_separator_1);
        }
        {
            this.lblLor = new JLabel("Lorentz:");
            GridBagConstraints gbc_lblLor = new GridBagConstraints();
            gbc_lblLor.insets = new Insets(2, 2, 0, 5);
            gbc_lblLor.gridx = 2;
            gbc_lblLor.gridy = 0;
            this.panel.add(this.lblLor, gbc_lblLor);
        }
        {
            this.chckbxLorOscil = new JCheckBox("Oscillating");
            buttonGroup.add(this.chckbxLorOscil);
            GridBagConstraints gbc_chckbxLorOscil = new GridBagConstraints();
            gbc_chckbxLorOscil.anchor = GridBagConstraints.WEST;
            gbc_chckbxLorOscil.insets = new Insets(2, 0, 0, 5);
            gbc_chckbxLorOscil.gridx = 3;
            gbc_chckbxLorOscil.gridy = 0;
            this.panel.add(this.chckbxLorOscil, gbc_chckbxLorOscil);
        }
        {
            this.chckbxLorPow = new JCheckBox("Powder");
            buttonGroup.add(this.chckbxLorPow);
            GridBagConstraints gbc_chckbxLorPow = new GridBagConstraints();
            gbc_chckbxLorPow.anchor = GridBagConstraints.WEST;
            gbc_chckbxLorPow.insets = new Insets(2, 0, 0, 5);
            gbc_chckbxLorPow.gridx = 4;
            gbc_chckbxLorPow.gridy = 0;
            this.panel.add(this.chckbxLorPow, gbc_chckbxLorPow);
        }
        {
        	separator = new JSeparator();
        	separator.setOrientation(SwingConstants.VERTICAL);
        	GridBagConstraints gbc_separator = new GridBagConstraints();
        	gbc_separator.fill = GridBagConstraints.VERTICAL;
        	gbc_separator.insets = new Insets(0, 0, 0, 5);
        	gbc_separator.gridx = 5;
        	gbc_separator.gridy = 0;
        	panel.add(separator, gbc_separator);
        }
        {
            this.lblPol = new JLabel("Polarization:");
            GridBagConstraints gbc_lblPol = new GridBagConstraints();
            gbc_lblPol.insets = new Insets(2, 2, 0, 5);
            gbc_lblPol.gridx = 6;
            gbc_lblPol.gridy = 0;
            this.panel.add(this.lblPol, gbc_lblPol);
        }
        {
            this.chckbxPolSyn = new JCheckBox("Synchrotron");
            buttonGroup_1.add(this.chckbxPolSyn);
            GridBagConstraints gbc_chckbxPolSyn = new GridBagConstraints();
            gbc_chckbxPolSyn.anchor = GridBagConstraints.WEST;
            gbc_chckbxPolSyn.insets = new Insets(2, 0, 0, 5);
            gbc_chckbxPolSyn.gridx = 7;
            gbc_chckbxPolSyn.gridy = 0;
            this.panel.add(this.chckbxPolSyn, gbc_chckbxPolSyn);
        }
        {
            this.chckbxPolLab = new JCheckBox("Laboratory");
            buttonGroup_1.add(this.chckbxPolLab);
            GridBagConstraints gbc_chckbxPolLab = new GridBagConstraints();
            gbc_chckbxPolLab.anchor = GridBagConstraints.WEST;
            gbc_chckbxPolLab.insets = new Insets(2, 0, 0, 0);
            gbc_chckbxPolLab.gridx = 8;
            gbc_chckbxPolLab.gridy = 0;
            this.panel.add(this.chckbxPolLab, gbc_chckbxPolLab);
        }
        {
        	panel_runcontrols = new JPanel();
        	GridBagConstraints gbc_panel_runcontrols = new GridBagConstraints();
        	gbc_panel_runcontrols.fill = GridBagConstraints.BOTH;
        	gbc_panel_runcontrols.gridx = 1;
        	gbc_panel_runcontrols.gridy = 3;
        	panel_top.add(panel_runcontrols, gbc_panel_runcontrols);
        	GridBagLayout gbl_panel_runcontrols = new GridBagLayout();
        	gbl_panel_runcontrols.columnWidths = new int[]{0, 0, 0, 0, 0, 0, 0, 0};
        	gbl_panel_runcontrols.rowHeights = new int[]{0, 0};
        	gbl_panel_runcontrols.columnWeights = new double[]{0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
        	gbl_panel_runcontrols.rowWeights = new double[]{0.0, Double.MIN_VALUE};
        	panel_runcontrols.setLayout(gbl_panel_runcontrols);
        	{
        		btnRun = new JButton("Run!");
        		btnRun.addActionListener(new ActionListener() {
        			public void actionPerformed(ActionEvent arg0) {
        				do_btnRun_actionPerformed(arg0);
        			}
        		});
        		GridBagConstraints gbc_btnRun = new GridBagConstraints();
        		gbc_btnRun.fill = GridBagConstraints.HORIZONTAL;
        		gbc_btnRun.insets = new Insets(0, 0, 0, 5);
        		gbc_btnRun.gridx = 0;
        		gbc_btnRun.gridy = 0;
        		panel_runcontrols.add(btnRun, gbc_btnRun);
        	}
        	{
        	    this.btnStop = new JButton("Stop!");
        	    GridBagConstraints gbc_btnStop = new GridBagConstraints();
        	    gbc_btnStop.insets = new Insets(0, 0, 0, 5);
        	    gbc_btnStop.gridx = 1;
        	    gbc_btnStop.gridy = 0;
        	    panel_runcontrols.add(btnStop, gbc_btnStop);
        	    this.btnStop.addActionListener(new ActionListener() {
        	        @Override
        	        public void actionPerformed(ActionEvent arg0) {
        	            do_btnStop_actionPerformed(arg0);
        	        }
        	    });
        	    this.btnStop.setBackground(Color.RED);
        	}
        	{
        		chckbxSaveBkg = new JCheckBox("save BKG");
        		GridBagConstraints gbc_chckbxSaveBkg = new GridBagConstraints();
        		gbc_chckbxSaveBkg.insets = new Insets(0, 0, 0, 5);
        		gbc_chckbxSaveBkg.gridx = 2;
        		gbc_chckbxSaveBkg.gridy = 0;
        		panel_runcontrols.add(chckbxSaveBkg, gbc_chckbxSaveBkg);
        	}
        	{
        	    this.progressBar = new JProgressBar();
        	    GridBagConstraints gbc_progressBar = new GridBagConstraints();
        	    gbc_progressBar.insets = new Insets(0, 0, 0, 5);
        	    gbc_progressBar.gridwidth = 4;
        	    gbc_progressBar.fill = GridBagConstraints.BOTH;
        	    gbc_progressBar.gridx = 3;
        	    gbc_progressBar.gridy = 0;
        	    panel_runcontrols.add(progressBar, gbc_progressBar);
        	    this.progressBar.setFont(new Font("Tahoma", Font.BOLD, 15));
        	}
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
                    this.tAOut = new LogJTextArea();
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
            JPanel buttonPane = new JPanel();
            getContentPane().add(buttonPane, BorderLayout.SOUTH);
            {
                GridBagLayout gbl_buttonPane = new GridBagLayout();
                gbl_buttonPane.columnWidths = new int[] { 0, 0, 0, 0, 0 };
                gbl_buttonPane.rowHeights = new int[] { 0, 0 };
                gbl_buttonPane.columnWeights = new double[] { 0.0, 1.0, 0.0, 0.0, Double.MIN_VALUE };
                gbl_buttonPane.rowWeights = new double[] { 0.0, Double.MIN_VALUE };
                buttonPane.setLayout(gbl_buttonPane);
            }
            {
                {
                    this.checkBox = new JCheckBox("on top");
                    GridBagConstraints gbc_checkBox = new GridBagConstraints();
                    gbc_checkBox.anchor = GridBagConstraints.WEST;
                    gbc_checkBox.insets = new Insets(5, 5, 5, 5);
                    gbc_checkBox.gridx = 0;
                    gbc_checkBox.gridy = 0;
                    buttonPane.add(this.checkBox, gbc_checkBox);
                    this.checkBox.addItemListener(new ItemListener() {
                        @Override
                        public void itemStateChanged(ItemEvent arg0) {
                            do_checkBox_itemStateChanged(arg0);
                        }
                    });
                    this.checkBox.setFont(new Font("Tahoma", Font.PLAIN, 11));
                    this.checkBox.setActionCommand("on top");
                }
            }
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
            gbc_label.anchor = GridBagConstraints.WEST;
            gbc_label.insets = new Insets(5, 5, 5, 5);
            gbc_label.gridx = 1;
            gbc_label.gridy = 0;
            buttonPane.add(this.label, gbc_label);
            this.label.setFont(new Font("Tahoma", Font.BOLD, 14));
            JButton okButton = new JButton("close");
            GridBagConstraints gbc_okButton = new GridBagConstraints();
            gbc_okButton.anchor = GridBagConstraints.EAST;
            gbc_okButton.insets = new Insets(5, 5, 5, 5);
            gbc_okButton.gridx = 3;
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
        }
        
        this.userInit();
        this.activeOptions();
    }

    private void userInit(){
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        this.setBounds(0, 0, 1100, 600);
        int width = this.getWidth();
        int height = this.getHeight();
        int x = (screen.width - width) / 2;
        int y = (screen.height - height) / 2;
        this.setBounds(x, y, width, height);
        
        //mostrem titol i versio D2Dsub
        tAOut.ln("** Background estimation/subtraction & LP correction **");
    }
	
    protected void do_btnSelectFile_actionPerformed(ActionEvent arg0) {

    	FileNameExtensionFilter[] filter = {new FileNameExtensionFilter("2D Data file (bin,img,spr,gfrm,edf)", "bin", "img",
                "spr", "gfrm", "edf")};
        glassD2File = FileUtils.fchooser(new File(MainFrame.getWorkdir()), filter, false);
        if (glassD2File == null) return;
        tAOut.ln("Glass file selected: " + glassD2File.getPath());
        lblGlassF.setText(glassD2File.getName());
    }
    

    protected void do_btnStop_actionPerformed(ActionEvent arg0) {
    	//preguntar per si de cas
        int n = JOptionPane.showConfirmDialog(this, "Abort current operation?", "Stop",
                JOptionPane.YES_NO_OPTION);
        if (n == JOptionPane.NO_OPTION) {
        	return;
        }
    	if(runBkg!=null){
    		try{
    			runBkg.interrupt();	
    		}catch(Exception e){
    			e.printStackTrace();
    		}
    	}
        runBkg = null;
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
// el bo abans canvis: String d2dsubHelp="<html><div style=\"text-align:justify\"> 1) To subtract glass contribution, select a glass file (IMG or BIN) and click on subtract. The scale factor will be automatically calculated. After the first run you can manually adjust the scale factor (Iglass*factor). You can see the results in the result image and compare with the initial one. The result file is written in the same folder as the data file with the name *-glass.bin<br><br>2) There are 5 ways to estimate the background:<ul><li> avsq: Each iteration estimates the background by averaging square areas around each pixel from the previous iteration. Set the number of pixels for the side of the square (Npix) and the number of iterations (Niter). It is a slow process for high Npix and Niter values.</li><li> avarc: The same as previous option but using arc shaped areas (within 2T) around each pixel. Set the number of iterations (Niter) and the factors for the width (wdt) and angular aperture (ang) for the arcs. This is a VERY slow method.</li><li> avcirc: The background estimation for each pixel is the mean intensity from a radial integration (in the 2T circle containing each pixel). Set the stepsize for the 2T ranges (step).</li><li> minsq: The background intensity value for each pixel (v0) is calculated as: Minimum(v0,v1,v2,v3) where v1,v2 and v3 are related pixels applying a reflection of the image (vertical, horizontal and both). Set which operations to use (v,h,vh), and the number of pixels (Npix) defining the square zone to be averaged after the operation (use 0 to consider only 1 pixel). It is a FAST method but some peak intensity may be subtracted.</li><li> minarc: The same as FLIP but using an arc shaped zone for each pixel. Set the operations (v,h,vh) and the factors for width and angular aperture (wdt,ang).</li></ul>For all options, two output files are written in the same folder as the data file: the data file where the background have been subtracted (*-subBkg.bin) and the background intensity that has been subtracted (*-Bkg.bin). You can inspect for residual peak intensity in the last one by clicking the [view BKG] button.<br>3) LP correction is automatically applied by clicking the button.<br><br>IMPORTANT: <ul><li> The corrections are always applied to the SOURCE image. To continue working with the RESULT image, click the [Set as source] button to set the result image as the source image.</li><li> Define any excluded zones BEFORE subtracting the background [pixels with intensity=-1 (BIN files) or .EXZ file with the same filename] as they will lead to incorrect background subtraction.</li><li> Clean .BIN files generated in tests or failed runs from the working folder that will not be used anymore and keep only the desired ones.</li></ul></div> </html>";
    	String d2dsubHelp="<html><div style=\"text-align:justify\"> 1) To subtract glass contribution, select a glass file and check the glass option. The scale factor will be automatically calculated. After the first run you can manually adjust the scale factor (Iglass*factor).<br><br>2) There are 5 methods to estimate the background:<ul><li> avsq: Each iteration estimates the background by averaging square areas around each pixel from the previous iteration. Set the number of pixels for the side of the square (Npix) and the number of iterations (Niter). It is a slow process for high Npix and Niter values.</li><li> avarc: The same as previous option but using arc shaped areas (within 2T) around each pixel. Set the number of iterations (Niter) and the factors for the width (wdt) and angular aperture (ang) for the arcs. This is a VERY slow method.</li><li> avcirc: The background estimation for each pixel is the mean intensity from a radial integration (in the 2T circle containing each pixel). Set the stepsize for the 2T ranges (step).</li><li> minsq: The background intensity value for each pixel (v0) is calculated as: Minimum(v0,v1,v2,v3) where v1,v2 and v3 are related pixels applying a reflection of the image (vertical, horizontal and both). Set which operations to use (v,h,vh), and the number of pixels (Npix) defining the square zone to be averaged after the operation (use 0 to consider only 1 pixel). It is a FAST method but some peak intensity may be subtracted.</li><li> minarc: The same as MINSQ but using an arc shaped zone for each pixel. Set the operations (v,h,vh) and the factors for width and angular aperture (wdt,ang).</li></ul> Visual inspection for residual peak intensity in the subtracted background can be done by clicking the [view BKG] button.<br><br>3) LP correction with the options selected is applied if the LP option is checked.<br><br>IMPORTANT: <ul><li> Result images can be seen on the main window and source image can be reloaded if wanted. It is recommended to save the result to a image file before applying more corrections to the result file. </li><li> Define any excluded zones BEFORE subtracting the background [pixels with intensity=-1 (BIN files) or .EXZ file with the same filename] as they will lead to incorrect background subtraction.</li></ul></div> </html>";
        Help_dialog hd = new Help_dialog("d2Dsub Help",d2dsubHelp);
        hd.setSize(700,700);
        hd.setLocationRelativeTo(this);
        hd.setVisible(true);
    }

    protected void do_okButton_actionPerformed(ActionEvent arg0) {
    	this.dispose();
    }

    protected void do_comboBox_itemStateChanged(ItemEvent arg0) {
        this.activeOptions();
    }
    
    private void activeOptions(){
        String opt = (String)comboBox.getSelectedItem();
        //1r desactivem tot
        txtAmplada.setEnabled(false);
        txtAngle.setEnabled(false);
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
            txtAmplada.setEnabled(true);
            txtAngle.setEnabled(true);
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
            txtAmplada.setEnabled(true);
            txtAngle.setEnabled(true);
        }
    }
    
	protected void do_btnRun_actionPerformed(ActionEvent arg0) {
		//hem d'anar una per una de la llista aplicant totes les opcions seleccionades
		
		//1r comprovar si hi ha alguna opcio assenyalada i despres executar
		boolean doGlass=false;
		boolean doBkg=false;
		boolean doLP=false;

		//variables de les opcions (valors defecte)
        String opt = "";
        float glassFactor = -1.f;
        float amplada=5.0f;
        float angle=6.0f;
        int bkgIter = 10;
        int bkgN = 10;
        float stepsize = 0.05f;
        int fhor=1;
        int fver=0;
        int fhorver=0;
        int aresta=4;
        int ilor=1;
        int ipol=1;
        
        //vidre
		if(chk_doGlass.isSelected()){
			doGlass=true;
			glassFactor = -1.f;
            if (chckbxFactor.isSelected()) {
            	try{
            		glassFactor = Float.parseFloat(txtFactor.getText());	
            	}catch(Exception e){
            		e.printStackTrace();
            		glassFactor = -1.f;
            	}
            }
		}
		
		if(chk_doBkg.isSelected()){
			doBkg=true;
			opt = ((String)comboBox.getSelectedItem()).trim();
			try{
				bkgN = Integer.parseInt(txtN.getText());
				bkgIter = Integer.parseInt(txtIter.getText());
				amplada = Float.parseFloat(txtAmplada.getText());
				angle = Float.parseFloat(txtAngle.getText());
                fhor = chckbxHor.isSelected()?1:0;
                fver = chckbxVer.isSelected()?1:0;
                fhorver = chckbxHorver.isSelected()?1:0;
                stepsize = Float.parseFloat(txtStep.getText());
			} catch (Exception e) {
				e.printStackTrace();
				tAOut.ln("Enter valid values for background subtraction options");
				return;
			}
		}
		
		if(chk_doLP.isSelected()){
			doLP=true;
            ilor=chckbxLorOscil.isSelected()?1:0;
            ilor=chckbxLorPow.isSelected()?2:ilor;
            ipol=chckbxPolSyn.isSelected()?1:0;
            ipol=chckbxPolLab.isSelected()?2:ipol;
		}
		
		if(!doGlass&&!doBkg&&!doLP){
			tAOut.ln("No operation selected");
			return;
		}
		
		//fem una cua de bkgsubtraction (operacions) que s'executara en un altre thread apart aix� no bloqueja el GUI
		this.cua=new ArrayBlockingQueue<bkgsubtraction>(50);
		for(int i=0; i<listfiles.getModel().getSize();i++){
			File f = listfiles.getModel().getElementAt(i);
			Pattern2D data = ImgFileUtils.openPatternFile(f);
			
			bkgsubtraction opts = new bkgsubtraction(data,doGlass,doBkg,doLP,opt);
			opts.readOptions(bkgIter, bkgN, fhor, fver, fhorver, aresta, ilor, ipol, glassFactor, amplada, angle, stepsize);
//			Thread th = new Thread(opts);
//			cua.add(th);
			cua.add(opts);
		}
		
		Thread cuaProcess = new Thread(new cuaProcessor());
		cuaProcess.start();
	}
    
	public ArrayBlockingQueue<bkgsubtraction> cua;
	public boolean interrupted = false;
	public Thread runBkg;
	private JCheckBox chckbxSaveBkg;
	
	private class cuaProcessor implements Runnable {

		@Override
		public void run() {
			interrupted = false;
	        tAOut.stat("Batch process started...");
			progressBar.setIndeterminate(true);
			progressBar.setString("Batch process started...");
			progressBar.setStringPainted(true);
			
//			Iterator<bkgsubtraction> it = cua.iterator();
			int nfile = 1; //file being processed
			int nfiles = cua.size();
			try{
				while(cua.size()>0){
					bkgsubtraction bs = cua.poll();
					tAOut.afegirLinia('*');
					String fpath = bs.getDataWork().getImgfile().toString();
					tAOut.stat("Processing file ("+nfile+" of "+nfiles+"): "+fpath);
					runBkg = new Thread(bs);
					runBkg.run();
					
					//save file
					Pattern2D result = bs.getDataWork();
					String pathOut = FileUtils.getFNameNoExt(fpath).concat("_BkgSub.bin");
					ImgFileUtils.saveBIN(new File(pathOut), result);
//					try{
//						runBkg.interrupt();
//					}catch(Exception e){
//						e.printStackTrace();
//					}
					tAOut.stat("File: "+ fpath +" --> finished");
					tAOut.stat("Result saved to: "+ pathOut);
					
					//save fons
					if(chckbxSaveBkg.isSelected()){
						Pattern2D fons = bs.getDataFons();
						pathOut = FileUtils.getFNameNoExt(fpath).concat("_Bkg.bin");
						ImgFileUtils.saveBIN(new File(pathOut), fons);
						tAOut.stat("Background saved to: "+ pathOut);	
					}
					
					if(interrupted){
						tAOut.stat("*** Run STOPPED ***");
		                progressBar.setIndeterminate(false);
		                progressBar.setStringPainted(false);
		                return;
					}
					nfile = nfile+1;
				}
				
				tAOut.afegirLinia('*');
		        // un cop aqui s'hauria d'haver acabat l'execucio
		        tAOut.stat("Batch process finished");
		        progressBar.setIndeterminate(false);
		        progressBar.setStringPainted(false);
            }catch(Exception e){
				e.printStackTrace();
                tAOut.stat("*** Run ERROR ***");
                progressBar.setIndeterminate(false);
                progressBar.setStringPainted(false);
            }
		
		}
	}
	
    //new d2dsub java implemented
    protected class bkgsubtraction implements Runnable {

        private String bkgOpt;
        private boolean doGlass, doBkg, doLP;
        float glassFactor,amplada,angle,stepsize;
        int bkgIter,bkgN,fhor,fver,fhorver,aresta,ilor,ipol;
        Pattern2D dataWork,datafons;

        public Pattern2D getDataWork(){
        	return this.dataWork;
        }
        
        public Pattern2D getDataFons(){
        	return this.datafons;
        }
        
        public bkgsubtraction(Pattern2D dataIn, boolean doGlass, boolean doBkg, boolean doLP,String bkgOption) {
            super();
            this.dataWork=dataIn;
            this.bkgOpt = bkgOption;
            this.doGlass=doGlass;
            this.doLP=doLP;
            this.doBkg=doBkg;
        }
        public void readOptions(int bkgIter, int bkgN, int fhor, int fver, int fhorver, int aresta, int ilor, int ipol,
        		 float glassFactor, float amplada, float angle, float stepsize){
            this.glassFactor = glassFactor;
            this.amplada = amplada;
            this.angle = angle;
            this.bkgIter = bkgIter;
            this.bkgN = bkgN;
            this.stepsize = stepsize;
            this.fhor = fhor;
            this.fver= fver;
            this.fhorver= fhorver;
            this.aresta=aresta;
            this.ilor=ilor;
            this.ipol=ipol;
        }
    	
		@Override
		public void run(){
			ImgOps.setBkgIter(0);
			try {

				// 1r sostraiem el vidre si s'ha seleccionat
				if (doGlass) {
				    // comprovem si hi ha seleccionat fitxer vidre
				    if (glassD2File == null || !glassD2File.exists()) {
				        tAOut.ln("select a valid glass image first");
				        throw new Exception();
				    }
				    
				    tAOut.stat("Glass subtraction... ");
				    
				    //preparacio dades
				    Pattern2D glass = ImgFileUtils.openPatternFile(glassD2File);
				    glass.copyMaskPixelsFromImage(dataWork);
				    
				    //escalat del vidre
				    glass = ImgOps.correctGlass(glass);
				    if (this.glassFactor < 0){
				    	//calculem factor d'escala mes petit
				    	this.glassFactor = ImgOps.calcGlassScale(dataWork, glass);
				    }
				    
				    //treiem el fons
				    //les dades amb vidre sostret seran les d'entrada a posteriors operacions
				    dataWork = ImgOps.subtractBKG_v2(dataWork, glass, this.glassFactor, tAOut)[0];
				    
				    tAOut.stat("Glass subtraction... DONE!");
				}
				
				if (doBkg){
				    tAOut.stat("Background subtraction... ");
					//metode
					if(this.bkgOpt.equalsIgnoreCase("avsq")){
						//necessitem 2 imatges (la iteracio anterior i la nova)
				        Pattern2D it[] = new Pattern2D[2];
				        it[0] = ImgOps.firstBkgPass(dataWork);
//				        tAOut.ln("Iterations started...");
				        for(int i=0;i<this.bkgIter;i++){
				        	it[1] = ImgOps.calcIterAvsq(it[0], this.bkgN, tAOut, progressBar);
				        	it[0] = it[1]; //actualitzem l'anterior
				        }
				        datafons = it[1];
				        //treiem el fons (ultima iteracio)
				        dataWork = ImgOps.subtractBKG(dataWork, it[1]);
					}
					
				    if (this.bkgOpt.equalsIgnoreCase("avarc")) {
						//necessitem 2 imatges (la iteracio anterior i la nova)
				        Pattern2D it[] = new Pattern2D[2];
				        it[0] = ImgOps.firstBkgPass(dataWork);
//				        tAOut.ln("Iterations started...");
				        for(int i=0;i<this.bkgIter;i++){
				        	it[1] = ImgOps.calcIterAvarc(it[0], this.amplada, this.angle, tAOut, progressBar);
				        	it[0] = it[1]; //actualitzem l'anterior
				        }
				        datafons = it[1];
				        //treiem el fons
				        dataWork = ImgOps.subtractBKG(dataWork, it[1]);
//				        dataIn = it[1]; //debug
				    }
					
				    if (this.bkgOpt.equalsIgnoreCase("avcirc")) {
				    	//nomes s'ha de fer una passada
				        Pattern2D it[] = new Pattern2D[2];
				        it[0] = ImgOps.firstBkgPass(dataWork);
				        it[1] = ImgOps.calcIterAvcirc(it[0], this.stepsize, tAOut, progressBar);
				        datafons = it[1];
				        //treiem el fons
				        dataWork = ImgOps.subtractBKG(dataWork, it[1]);
				    }

				    //calcul del fons metode min (antic "flip")
				    if (this.bkgOpt.equalsIgnoreCase("minsq")) {
				    	Pattern2D fons = ImgOps.bkgMin(dataWork, this.fhor, this.fver, this.fhorver, this.aresta, this.angle, this.amplada, false, tAOut, progressBar);
				    	datafons = fons;
				        //treiem el fons
				        dataWork = ImgOps.subtractBKG(dataWork, fons);
				    }
				    
				    if (this.bkgOpt.equalsIgnoreCase("minarc")) {
				    	Pattern2D fons = ImgOps.bkgMin(dataWork, this.fhor, this.fver, this.fhorver, this.aresta, this.angle, this.amplada, true, tAOut, progressBar);
				    	datafons = fons;
				        //treiem el fons
				        dataWork = ImgOps.subtractBKG(dataWork, fons);
				    }
				    tAOut.stat("Background subtraction... DONE!");
				}
				
				if(doLP){
				    tAOut.stat("LP correction... ");
				    dataWork = ImgOps.corrLP(dataWork, this.ipol, this.ilor, false);
				    tAOut.stat("LP correction... DONE!");
				}
				
			} catch (Exception e) {
				e.printStackTrace();
				interrupted = true;
				//per si s'ha aturat
                return;
			}
            
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
	
	protected void do_btnSelectFiles_actionPerformed(ActionEvent arg0) {
        
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
        
        //poblem la llista
        DefaultListModel<File> lm = new DefaultListModel<File>();
        for(int i=0; i<flist.length; i++){
        	lm.addElement(flist[i]);	
        }
        listfiles.setModel(lm);
        
	}
	protected void do_btnClear_actionPerformed(ActionEvent arg0) {
		try{
			((DefaultListModel<File>)listfiles.getModel()).clear();
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
}
