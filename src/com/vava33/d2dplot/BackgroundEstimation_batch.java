package com.vava33.d2dplot;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.concurrent.ArrayBlockingQueue;

import javax.swing.ButtonGroup;
import javax.swing.ButtonModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.vava33.d2dplot.auxi.ImgFileUtils;
import com.vava33.d2dplot.auxi.ImgOps;
import com.vava33.d2dplot.auxi.Pattern2D;
import com.vava33.jutils.FileUtils;
import com.vava33.jutils.VavaLogger;

import net.miginfocom.swing.MigLayout;

public class BackgroundEstimation_batch {

    private final JDialog d2dsubBatchDialog;
    private JButton btnSelectFile;
    private JButton btnStop;
    private JCheckBox chckbxFactor;
    private JCheckBox checkBox;
    private final JPanel contentPanel;
    private JLabel label;
    private JLabel lblGlassF;
    private JLabel lblIter;
    private JLabel lblN;
    private final JPanel panel;
    private final JPanel panel_top;
    private final JPanel panelBkg;
    private final JPanel panelGlass;
    private JProgressBar progressBar;
    private JTextField txtFactor;
    private JTextField txtIter;
    private JTextField txtN;

    private File glassD2File;
    private JComboBox<String> comboBox;
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
    private final NoneSelectedButtonGroup buttonGroup_2 = new NoneSelectedButtonGroup();
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
    private MainFrame mf;
    private static final String className = "BKG_sub_batch";
    private static VavaLogger log = D2Dplot_global.getVavaLogger(className);

    /**
     * Create the dialog.
     */
    public BackgroundEstimation_batch(MainFrame mf) {
        this.mf = mf;
        this.d2dsubBatchDialog = new JDialog(mf.getMainF(),
                "Background subtraction and LP correction (batch processing)", false);
        this.contentPanel = new JPanel();
        UIManager.put("ProgressBar.selectionBackground", Color.black);
        this.d2dsubBatchDialog.setIconImage(
                Toolkit.getDefaultToolkit().getImage(BackgroundEstimation_batch.class.getResource("/img/Icona.png")));
        this.d2dsubBatchDialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        this.d2dsubBatchDialog.getContentPane().setLayout(new BorderLayout());
        this.d2dsubBatchDialog.getContentPane().add(this.contentPanel, BorderLayout.CENTER);
        this.contentPanel.setLayout(new MigLayout("", "[grow]", "[grow]"));
        this.panel_top = new JPanel();
        this.contentPanel.add(this.panel_top, "cell 0 0,grow");
        this.panel_top.setLayout(new MigLayout("insets 0,fill", "[grow][]", "[][][][]"));
        {
            this.panel_1 = new JPanel();
            this.panel_top.add(this.panel_1, "cell 0 0 1 4,grow");
            {
                this.btnSelectFiles = new JButton("Select files");
                this.btnSelectFiles.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent arg0) {
                        BackgroundEstimation_batch.this.do_btnSelectFiles_actionPerformed(arg0);
                    }
                });
                this.panel_1.setLayout(new MigLayout("", "[grow][]", "[][grow]"));
                this.panel_1.add(this.btnSelectFiles, "cell 0 0,alignx left,aligny top");
            }
            {
                this.btnClear = new JButton("clear");
                this.btnClear.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent arg0) {
                        BackgroundEstimation_batch.this.do_btnClear_actionPerformed(arg0);
                    }
                });
                this.panel_1.add(this.btnClear, "cell 1 0,alignx center,aligny center");
            }
            {
                this.scrollPane = new JScrollPane();
                this.panel_1.add(this.scrollPane, "cell 0 1 2 1,grow");
                {
                    this.listfiles = new JList<File>();
                    this.scrollPane.setViewportView(this.listfiles);
                }
            }
        }
        this.panelGlass = new JPanel();
        this.panelGlass.setBorder(
                new TitledBorder(null, "Glass subtraction", TitledBorder.LEADING, TitledBorder.TOP, null, null));
        this.panel_top.add(this.panelGlass, "cell 1 0,grow");
        {
            this.btnSelectFile = new JButton("select glass file");
            this.btnSelectFile.setMargin(new Insets(2, 6, 2, 6));
            this.btnSelectFile.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent arg0) {
                    BackgroundEstimation_batch.this.do_btnSelectFile_actionPerformed(arg0);
                }
            });
            this.panelGlass.setLayout(new MigLayout("", "[][][][grow][][]", "[]"));
            {
                this.chk_doGlass = new JCheckBox("Do it!");
                this.panelGlass.add(this.chk_doGlass, "cell 0 0,alignx center,aligny center");
            }
            {
                this.separator_3 = new JSeparator();
                this.separator_3.setOrientation(SwingConstants.VERTICAL);
                this.panelGlass.add(this.separator_3, "cell 1 0,alignx center,growy");
            }
            this.panelGlass.add(this.btnSelectFile, "cell 2 0,alignx left,aligny center");
        }
        {
            this.chckbxFactor = new JCheckBox("factor=");
            this.chckbxFactor.addItemListener(new ItemListener() {
                @Override
                public void itemStateChanged(ItemEvent arg0) {
                    BackgroundEstimation_batch.this.do_chckbxFactor_itemStateChanged(arg0);
                }
            });
            {
                this.lblGlassF = new JLabel("(no glass data file selected)");
                this.panelGlass.add(this.lblGlassF, "cell 3 0,alignx left,aligny center");
            }
            this.panelGlass.add(this.chckbxFactor, "cell 4 0,alignx center,aligny center");
        }
        {
            this.txtFactor = new JTextField();
            this.txtFactor.setBackground(Color.WHITE);
            this.txtFactor.setEnabled(false);
            this.txtFactor.setText("1.000");
            this.txtFactor.setColumns(4);
            this.panelGlass.add(this.txtFactor, "cell 5 0,growx,aligny center");
        }
        this.panelBkg = new JPanel();
        this.panelBkg.setBorder(
                new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Background estimation & subtraction",
                        TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
        this.panel_top.add(this.panelBkg, "cell 1 1,grow");
        {
            this.comboBox = new JComboBox<String>();
            this.comboBox.addItemListener(new ItemListener() {
                @Override
                public void itemStateChanged(ItemEvent arg0) {
                    BackgroundEstimation_batch.this.do_comboBox_itemStateChanged(arg0);
                }
            });
            this.panelBkg
                    .setLayout(new MigLayout("", "[][][][grow][][grow][][grow][][][grow][][grow][][grow][][][]", "[]"));
            {
                this.chk_doBkg = new JCheckBox("Do it!");
                this.panelBkg.add(this.chk_doBkg, "cell 0 0,alignx center,aligny center");
            }
            {
                this.separator_2 = new JSeparator();
                this.separator_2.setOrientation(SwingConstants.VERTICAL);
                this.panelBkg.add(this.separator_2, "cell 1 0,alignx center,growy");
            }
            {
                this.lblMehod = new JLabel("Mehod");
                this.panelBkg.add(this.lblMehod, "cell 2 0,alignx right,aligny center");
            }
            this.comboBox.setModel(
                    new DefaultComboBoxModel<String>(new String[] { "avsq", "avarc", "avcirc", "minsq", "minarc" }));
            this.panelBkg.add(this.comboBox, "cell 3 0,growx,aligny center");
        }
        {
            this.lblN = new JLabel("Npix=");
            this.panelBkg.add(this.lblN, "cell 4 0,alignx right,aligny center");
        }
        {
            this.txtN = new JTextField();
            this.txtN.setBackground(Color.WHITE);
            this.txtN.setText("20");
            this.panelBkg.add(this.txtN, "cell 5 0,growx,aligny center");
            this.txtN.setColumns(3);
        }
        {
            this.lblIter = new JLabel("Iter=");
            this.panelBkg.add(this.lblIter, "cell 6 0,alignx right,aligny center");
        }
        {
            this.txtIter = new JTextField();
            this.txtIter.setBackground(Color.WHITE);
            this.txtIter.setText("10");
            this.panelBkg.add(this.txtIter, "cell 7 0,growx,aligny center");
            this.txtIter.setColumns(2);
        }
        {
            this.separator_4 = new JSeparator();
            this.separator_4.setOrientation(SwingConstants.VERTICAL);
            this.panelBkg.add(this.separator_4, "cell 8 0,alignx center,growy");
        }
        {
            this.lblAwidth = new JLabel("wth=");
            this.panelBkg.add(this.lblAwidth, "cell 9 0,alignx right,aligny center");
        }
        {
            this.txtAmplada = new JTextField();
            this.txtAmplada.setBackground(Color.WHITE);
            this.txtAmplada.setText("5.0");
            this.panelBkg.add(this.txtAmplada, "cell 10 0,growx,aligny center");
            this.txtAmplada.setColumns(4);
        }
        {
            this.lblAangle = new JLabel("ang=");
            this.panelBkg.add(this.lblAangle, "cell 11 0,alignx right,aligny center");
        }
        {
            this.txtAngle = new JTextField();
            this.txtAngle.setBackground(Color.WHITE);
            this.txtAngle.setText("4.0");
            this.panelBkg.add(this.txtAngle, "cell 12 0,growx,aligny center");
            this.txtAngle.setColumns(4);
        }
        {
            this.lblStep = new JLabel("step=");
            this.panelBkg.add(this.lblStep, "cell 13 0,alignx right,aligny center");
        }
        {
            this.txtStep = new JTextField();
            this.txtStep.setBackground(Color.WHITE);
            this.txtStep.setText("0.05");
            this.panelBkg.add(this.txtStep, "cell 14 0,growx,aligny center");
            this.txtStep.setColumns(4);
        }
        {
            this.chckbxHor = new JCheckBox("h");
            this.chckbxHor.setSelected(true);
            this.panelBkg.add(this.chckbxHor, "cell 15 0,alignx right,aligny center");
        }
        {
            this.chckbxVer = new JCheckBox("v");
            this.panelBkg.add(this.chckbxVer, "cell 16 0,alignx center,aligny center");
        }
        {
            this.chckbxHorver = new JCheckBox("hv");
            this.panelBkg.add(this.chckbxHorver, "cell 17 0,alignx left,aligny center");
        }
        this.panel = new JPanel();
        this.panel.setBorder(
                new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Lorentz & Polarization corrections",
                        TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
        this.panel_top.add(this.panel, "cell 1 2,grow");
        this.panel.setLayout(new MigLayout("", "[][][][][][][][][][][][][]", "[]"));
        {
            this.chk_doLP = new JCheckBox("Do it!");
            this.panel.add(this.chk_doLP, "cell 0 0,alignx center,aligny center");
        }
        {
            this.separator_1 = new JSeparator();
            this.separator_1.setOrientation(SwingConstants.VERTICAL);
            this.panel.add(this.separator_1, "cell 1 0,alignx center,growy");
        }
        {
            this.lblLor = new JLabel("Lorentz:");
            this.panel.add(this.lblLor, "cell 2 0,alignx center,aligny center");
        }
        {
            this.chckbxLorOscil = new JCheckBox("Oscillating");
            this.buttonGroup.add(this.chckbxLorOscil);
            this.panel.add(this.chckbxLorOscil, "cell 3 0,alignx left,aligny center");
        }
        {
            this.chckbxLorPow = new JCheckBox("Powder");
            this.buttonGroup.add(this.chckbxLorPow);
            this.panel.add(this.chckbxLorPow, "cell 4 0,alignx left,aligny center");
        }
        {
            this.separator = new JSeparator();
            this.separator.setOrientation(SwingConstants.VERTICAL);
            this.panel.add(this.separator, "cell 5 0,alignx center,growy");
        }
        {
            this.lblPol = new JLabel("Polarization:");
            this.panel.add(this.lblPol, "cell 6 0,alignx center,aligny center");
        }
        {
            this.chckbxPolSyn = new JCheckBox("Synchrotron");
            this.buttonGroup_1.add(this.chckbxPolSyn);
            this.panel.add(this.chckbxPolSyn, "cell 7 0,alignx left,aligny center");
        }
        {
            this.chckbxPolLab = new JCheckBox("Laboratory");
            this.buttonGroup_1.add(this.chckbxPolLab);
            this.panel.add(this.chckbxPolLab, "cell 8 0,alignx left,aligny center");
        }
        {
            this.separator_5 = new JSeparator();
            this.separator_5.setOrientation(SwingConstants.VERTICAL);
            this.panel.add(this.separator_5, "cell 9 0,alignx center,growy");
        }
        {
            this.lblRotAxis = new JLabel("Rot axis:");
            this.panel.add(this.lblRotAxis, "cell 10 0,alignx center,aligny center");
        }
        {
            this.chckbxH = new JCheckBox("H");
            this.buttonGroup_2.add(this.chckbxH);
            this.panel.add(this.chckbxH, "cell 11 0,alignx center,aligny center");
        }
        {
            this.chckbxV = new JCheckBox("V");
            this.buttonGroup_2.add(this.chckbxV);
            this.panel.add(this.chckbxV, "cell 12 0,alignx center,aligny center");
        }
        {
            this.panel_runcontrols = new JPanel();
            this.panel_top.add(this.panel_runcontrols, "cell 1 3,grow");
            {
                this.btnRun = new JButton("Run!");
                this.btnRun.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent arg0) {
                        BackgroundEstimation_batch.this.do_btnRun_actionPerformed(arg0);
                    }
                });
                this.panel_runcontrols.setLayout(new MigLayout("", "[][][][grow]", "[]"));
                this.panel_runcontrols.add(this.btnRun, "cell 0 0,growx,aligny center");
            }
            {
                this.btnStop = new JButton("Stop!");
                this.panel_runcontrols.add(this.btnStop, "cell 1 0,alignx center,aligny center");
                this.btnStop.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent arg0) {
                        BackgroundEstimation_batch.this.do_btnStop_actionPerformed(arg0);
                    }
                });
                this.btnStop.setBackground(Color.RED);
            }
            {
                this.chckbxSaveBkg = new JCheckBox("save BKG");
                this.panel_runcontrols.add(this.chckbxSaveBkg, "cell 2 0,alignx center,aligny center");
            }
            {
                this.progressBar = new JProgressBar();
                this.panel_runcontrols.add(this.progressBar, "cell 3 0,grow");
                this.progressBar.setFont(new Font("Tahoma", Font.BOLD, 15));
            }
        }

        {
            final JPanel buttonPane = new JPanel();
            this.d2dsubBatchDialog.getContentPane().add(buttonPane, BorderLayout.SOUTH);
            buttonPane.setLayout(new MigLayout("", "[][][grow]", "[]"));
            {
                {
                    this.checkBox = new JCheckBox("on top");
                    buttonPane.add(this.checkBox, "cell 0 0,alignx left,aligny center");
                    this.checkBox.addItemListener(new ItemListener() {
                        @Override
                        public void itemStateChanged(ItemEvent arg0) {
                            BackgroundEstimation_batch.this.do_checkBox_itemStateChanged(arg0);
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
                    BackgroundEstimation_batch.this.do_label_mouseEntered(arg0);
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    BackgroundEstimation_batch.this.do_label_mouseExited(e);
                }

                @Override
                public void mouseReleased(MouseEvent e) {
                    BackgroundEstimation_batch.this.do_label_mouseReleased(e);
                }
            });
            buttonPane.add(this.label, "cell 1 0,alignx left,aligny center");
            this.label.setFont(new Font("Tahoma", Font.BOLD, 14));
            final JButton okButton = new JButton("close");
            buttonPane.add(okButton, "cell 2 0,alignx right,aligny center");
            okButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent arg0) {
                    BackgroundEstimation_batch.this.do_okButton_actionPerformed(arg0);
                }
            });
            okButton.setActionCommand("OK");
            this.d2dsubBatchDialog.getRootPane().setDefaultButton(okButton);
        }

        this.userInit();
        this.activeOptions();
    }

    private void userInit() {
        final Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        final int width = this.d2dsubBatchDialog.getWidth();
        final int height = this.d2dsubBatchDialog.getHeight();
        final int x = (screen.width - width) / 2;
        final int y = (screen.height - height) / 2;
        this.d2dsubBatchDialog.setBounds(x, y, width, height);
        this.d2dsubBatchDialog.pack();
        //mostrem titol i versio D2Dsub
        log.info("** Background estimation/subtraction & LP correction **");
    }

    protected void do_btnSelectFile_actionPerformed(ActionEvent arg0) {

        final FileNameExtensionFilter[] filter = { new FileNameExtensionFilter("2D Data file (bin,img,spr,gfrm,edf)",
                "bin", "img", "spr", "gfrm", "edf") };
        this.glassD2File = FileUtils.fchooserOpen(this.d2dsubBatchDialog, new File(MainFrame.getWorkdir()), filter, 0);
        if (this.glassD2File == null)
            return;
        log.info("Glass file selected: " + this.glassD2File.getPath());
        this.lblGlassF.setText(this.glassD2File.getName());
    }

    private void stopProcess() {
        if (this.runBkg != null) {
            try {
                this.runBkg.interrupt();
            } catch (final Exception e) {
                if (D2Dplot_global.isDebug())
                    e.printStackTrace();
                log.warning("Error in runBkg");
            }
        }
        this.runBkg = null;

    }

    protected void do_btnStop_actionPerformed(ActionEvent arg0) {
        //preguntar per si de cas
        final int n = JOptionPane.showConfirmDialog(this.d2dsubBatchDialog, "Abort current operation?", "Stop",
                JOptionPane.YES_NO_OPTION);
        if (n == JOptionPane.NO_OPTION) {
            return;
        }
        this.stopProcess();
    }

    protected void do_chckbxFactor_itemStateChanged(ItemEvent arg0) {
        this.txtFactor.setEnabled(this.chckbxFactor.isSelected());
    }

    protected void do_checkBox_itemStateChanged(ItemEvent arg0) {
        this.d2dsubBatchDialog.setAlwaysOnTop(this.checkBox.isSelected());
    }

    protected void do_label_mouseEntered(MouseEvent arg0) {
        // lbllist.setText("<html><font color='red'>?</font></html>");
        this.label.setForeground(Color.red);
    }

    protected void do_label_mouseExited(MouseEvent e) {
        this.label.setForeground(Color.black);
    }

    protected void do_label_mouseReleased(MouseEvent e) {

        final String d2dsubHelp = "<html><div style=\"text-align:justify\"> 1) To subtract glass contribution, select a glass file and check the glass option. The scale factor will be automatically calculated. After the first run you can manually adjust the scale factor (Iglass*factor).<br><br>2) There are 5 methods to estimate the background:<ul><li> avsq: Each iteration estimates the background by averaging square areas around each pixel from the previous iteration. Set the number of pixels for the side of the square (Npix) and the number of iterations (Niter). It is a slow process for high Npix and Niter values.</li><li> avarc: The same as previous option but using arc shaped areas (within 2T) around each pixel. Set the number of iterations (Niter) and the factors for the width (wdt) and angular aperture (ang) for the arcs. This is a VERY slow method.</li><li> avcirc: The background estimation for each pixel is the mean intensity from a radial integration (in the 2T circle containing each pixel). Set the stepsize for the 2T ranges (step).</li><li> minsq: The background intensity value for each pixel (v0) is calculated as: Minimum(v0,v1,v2,v3) where v1,v2 and v3 are related pixels applying a reflection of the image (vertical, horizontal and both). Set which operations to use (v,h,vh), and the number of pixels (Npix) defining the square zone to be averaged after the operation (use 0 to consider only 1 pixel). It is a FAST method but some peak intensity may be subtracted.</li><li> minarc: The same as MINSQ but using an arc shaped zone for each pixel. Set the operations (v,h,vh) and the factors for width and angular aperture (wdt,ang).</li></ul> Visual inspection for residual peak intensity in the subtracted background can be done by clicking the [view BKG] button.<br><br>3) LP correction with the options selected is applied if the LP option is checked.<br><br>IMPORTANT: <ul><li> Result images can be seen on the main window and source image can be reloaded if wanted. It is recommended to save the result to a image file before applying more corrections to the result file. </li><li> Define any excluded zones BEFORE subtracting the background [pixels with intensity=-1 (BIN files) or .EXZ file with the same filename] as they will lead to incorrect background subtraction.</li></ul></div> </html>";
        final Help hd = new Help(this.getMf().getMainF(), "d2Dsub Help", d2dsubHelp);
        hd.getHelpDialog().setSize(700, 700);
        hd.getHelpDialog().setLocationRelativeTo(this.d2dsubBatchDialog);
        hd.getHelpDialog().setVisible(true);
    }

    protected void do_okButton_actionPerformed(ActionEvent arg0) {
        this.dispose();
    }

    public void dispose() {
        this.stopProcess();
        this.d2dsubBatchDialog.dispose();
    }

    public void setVisible(boolean vis) {
        this.d2dsubBatchDialog.setVisible(vis);
    }

    //    //es una manera de posar al log tot el que surt pel txtArea local
    //	public void printTaOut(String msg) {
    //        this.tAOut.stat(msg);
    //		log.debug(msg);
    //	}

    protected void do_comboBox_itemStateChanged(ItemEvent arg0) {
        this.activeOptions();
    }

    private void activeOptions() {
        final String opt = (String) this.comboBox.getSelectedItem();
        //1r desactivem tot
        this.txtAmplada.setEnabled(false);
        this.txtAngle.setEnabled(false);
        this.txtIter.setEnabled(false);
        this.txtN.setEnabled(false);
        this.txtStep.setEnabled(false);
        this.chckbxVer.setEnabled(false);
        this.chckbxHor.setEnabled(false);
        this.chckbxHorver.setEnabled(false);
        if (opt.trim().equalsIgnoreCase("avsq")) {
            this.txtIter.setEnabled(true);
            this.txtN.setEnabled(true);
        }
        if (opt.trim().equalsIgnoreCase("avcirc")) {
            this.txtStep.setEnabled(true);
        }
        if (opt.trim().equalsIgnoreCase("avarc")) {
            this.txtAmplada.setEnabled(true);
            this.txtAngle.setEnabled(true);
            this.txtIter.setEnabled(true);
        }
        if (opt.trim().equalsIgnoreCase("minsq")) {
            this.txtN.setEnabled(true);
            this.chckbxVer.setEnabled(true);
            this.chckbxHor.setEnabled(true);
            this.chckbxHorver.setEnabled(true);
        }
        if (opt.trim().equalsIgnoreCase("minarc")) {
            this.chckbxVer.setEnabled(true);
            this.chckbxHor.setEnabled(true);
            this.chckbxHorver.setEnabled(true);
            this.txtAmplada.setEnabled(true);
            this.txtAngle.setEnabled(true);
        }
    }

    protected void do_btnRun_actionPerformed(ActionEvent arg0) {
        //hem d'anar una per una de la llista aplicant totes les opcions seleccionades

        //1r comprovar si hi ha alguna opcio assenyalada i despres executar
        boolean doGlass = false;
        boolean doBkg = false;
        boolean doLP = false;

        //variables de les opcions (valors defecte)
        String opt = "";
        float glassFactor = -1.f;
        float amplada = 5.0f;
        float angle = 6.0f;
        int bkgIter = 10;
        int bkgN = 10;
        float stepsize = 0.05f;
        int fhor = 1;
        int fver = 0;
        int fhorver = 0;
        final int aresta = 4;
        int ilor = 1;
        int ipol = 1;
        int iosc = 2;

        //vidre
        if (this.chk_doGlass.isSelected()) {
            doGlass = true;
            glassFactor = -1.f;
            if (this.chckbxFactor.isSelected()) {
                try {
                    glassFactor = Float.parseFloat(this.txtFactor.getText());
                } catch (final Exception e) {
                    if (D2Dplot_global.isDebug())
                        e.printStackTrace();
                    log.warning("Error parsing glass factor");
                    glassFactor = -1.f;
                }
            }
        }

        if (this.chk_doBkg.isSelected()) {
            doBkg = true;
            opt = ((String) this.comboBox.getSelectedItem()).trim();
            try {
                bkgN = Integer.parseInt(this.txtN.getText());
                bkgIter = Integer.parseInt(this.txtIter.getText());
                amplada = Float.parseFloat(this.txtAmplada.getText());
                angle = Float.parseFloat(this.txtAngle.getText());
                fhor = this.chckbxHor.isSelected() ? 1 : 0;
                fver = this.chckbxVer.isSelected() ? 1 : 0;
                fhorver = this.chckbxHorver.isSelected() ? 1 : 0;
                stepsize = Float.parseFloat(this.txtStep.getText());
            } catch (final Exception e) {
                if (D2Dplot_global.isDebug())
                    e.printStackTrace();
                log.warning("Enter valid values for background subtraction options");
                return;
            }
        }

        if (this.chk_doLP.isSelected()) {
            doLP = true;
            ilor = this.chckbxLorOscil.isSelected() ? 1 : 0;
            ilor = this.chckbxLorPow.isSelected() ? 2 : ilor;
            ipol = this.chckbxPolSyn.isSelected() ? 1 : 0;
            ipol = this.chckbxPolLab.isSelected() ? 2 : ipol;
            iosc = this.chckbxH.isSelected() ? 1 : 2;
        }

        if (!doGlass && !doBkg && !doLP) {
            log.warning("No operation selected");
            return;
        }

        //fem una cua de bkgsubtraction (operacions) que s'executara en un altre thread apart aix� no bloqueja el GUI
        this.cua = new ArrayBlockingQueue<Bkgsubtraction>(50);
        for (int i = 0; i < this.listfiles.getModel().getSize(); i++) {
            final File f = this.listfiles.getModel().getElementAt(i);
            final Pattern2D data = ImgFileUtils.readPatternFile(f, false);

            final Bkgsubtraction opts = new Bkgsubtraction(data, doGlass, doBkg, doLP, opt);
            opts.readOptions(bkgIter, bkgN, fhor, fver, fhorver, aresta, ilor, ipol, iosc, glassFactor, amplada, angle,
                    stepsize);
            //			Thread th = new Thread(opts);
            //			cua.add(th);
            this.cua.add(opts);
        }

        final Thread cuaProcess = new Thread(new cuaProcessor());
        cuaProcess.start();
    }

    public ArrayBlockingQueue<Bkgsubtraction> cua;
    public boolean interrupted = false;
    public Thread runBkg;
    private JCheckBox chckbxSaveBkg;
    private JSeparator separator_5;
    private JLabel lblRotAxis;
    private JCheckBox chckbxH;
    private JCheckBox chckbxV;

    private class cuaProcessor implements Runnable {

        @Override
        public void run() {
            BackgroundEstimation_batch.this.interrupted = false;
            log.info("Batch process started...");
            BackgroundEstimation_batch.this.progressBar.setIndeterminate(true);
            BackgroundEstimation_batch.this.progressBar.setString("Batch process started...");
            BackgroundEstimation_batch.this.progressBar.setStringPainted(true);

            //			Iterator<bkgsubtraction> it = cua.iterator();
            int nfile = 1; //file being processed
            final int nfiles = BackgroundEstimation_batch.this.cua.size();
            try {
                while (BackgroundEstimation_batch.this.cua.size() > 0) {
                    final Bkgsubtraction bs = BackgroundEstimation_batch.this.cua.poll();
                    log.info(FileUtils.getCharLine('*', 80));
                    final String fpath = bs.getDataWork().getImgfile().toString();
                    log.info("Processing file (" + nfile + " of " + nfiles + "): " + fpath);
                    BackgroundEstimation_batch.this.runBkg = new Thread(bs);
                    //					runBkg.run();
                    BackgroundEstimation_batch.this.runBkg.start();//comprovar TODO (fet el març 2019)

                    //save file
                    final Pattern2D result = bs.getDataWork();
                    //					String pathOut = FileUtils.getFNameNoExt(fpath).concat("_BkgSub.bin");
                    //					ImgFileUtils.writeBIN(new File(pathOut), result);
                    String pathOut = FileUtils.getFNameNoExt(fpath).concat("_BkgSub.d2d");
                    ImgFileUtils.writePatternFile(new File(pathOut), result, false); //ja l'hem posat aqui dalt...
                    //					try{
                    //						runBkg.interrupt();
                    //					}catch(Exception e){
                    //						e.printStackTrace();
                    //					}
                    log.info("File: " + fpath + " --> finished");
                    log.info("Result saved to: " + pathOut);

                    //save fons
                    if (BackgroundEstimation_batch.this.chckbxSaveBkg.isSelected()) {
                        final Pattern2D fons = bs.getDataFons();
                        //						pathOut = FileUtils.getFNameNoExt(fpath).concat("_Bkg.bin");
                        //						ImgFileUtils.writeBIN(new File(pathOut), fons);
                        pathOut = FileUtils.getFNameNoExt(fpath).concat("_Bkg.d2d");
                        ImgFileUtils.writePatternFile(new File(pathOut), fons, false);
                        log.info("Background saved to: " + pathOut);
                    }

                    if (BackgroundEstimation_batch.this.interrupted) {
                        log.warning("*** Run STOPPED ***");
                        BackgroundEstimation_batch.this.progressBar.setIndeterminate(false);
                        BackgroundEstimation_batch.this.progressBar.setStringPainted(false);
                        return;
                    }
                    nfile = nfile + 1;
                }

                log.info(FileUtils.getCharLine('*', 80));
                // un cop aqui s'hauria d'haver acabat l'execucio
                log.info("Batch process finished");
                BackgroundEstimation_batch.this.progressBar.setIndeterminate(false);
                BackgroundEstimation_batch.this.progressBar.setStringPainted(false);
            } catch (final Exception e) {
                if (D2Dplot_global.isDebug())
                    e.printStackTrace();
                log.warning("*** Run ERROR ***");
                BackgroundEstimation_batch.this.progressBar.setIndeterminate(false);
                BackgroundEstimation_batch.this.progressBar.setStringPainted(false);
            }

        }
    }

    //new d2dsub java implemented
    protected class Bkgsubtraction implements Runnable {

        private final String bkgOpt;
        private final boolean doGlass, doBkg, doLP;
        float glassFactor, amplada, angle, stepsize;
        int bkgIter, bkgN, fhor, fver, fhorver, aresta, ilor, ipol, iosc;
        Pattern2D dataWork, datafons;

        public Pattern2D getDataWork() {
            return this.dataWork;
        }

        public Pattern2D getDataFons() {
            return this.datafons;
        }

        public Bkgsubtraction(Pattern2D dataIn, boolean doGlass, boolean doBkg, boolean doLP, String bkgOption) {
            super();
            this.dataWork = dataIn;
            this.bkgOpt = bkgOption;
            this.doGlass = doGlass;
            this.doLP = doLP;
            this.doBkg = doBkg;
        }

        public void readOptions(int bkgIter, int bkgN, int fhor, int fver, int fhorver, int aresta, int ilor, int ipol,
                int iosc, float glassFactor, float amplada, float angle, float stepsize) {
            this.glassFactor = glassFactor;
            this.amplada = amplada;
            this.angle = angle;
            this.bkgIter = bkgIter;
            this.bkgN = bkgN;
            this.stepsize = stepsize;
            this.fhor = fhor;
            this.fver = fver;
            this.fhorver = fhorver;
            this.aresta = aresta;
            this.ilor = ilor;
            this.ipol = ipol;
            this.iosc = iosc;
        }

        @Override
        public void run() {
            ImgOps.setBkgIter(0);
            try {

                // 1r sostraiem el vidre si s'ha seleccionat
                if (this.doGlass) {
                    // comprovem si hi ha seleccionat fitxer vidre
                    if (BackgroundEstimation_batch.this.glassD2File == null
                            || !BackgroundEstimation_batch.this.glassD2File.exists()) {
                        log.warning("Select a valid glass image first");
                        throw new Exception();
                    }

                    log.info("Glass subtraction... ");

                    //preparacio dades
                    Pattern2D glass = ImgFileUtils.readPatternFile(BackgroundEstimation_batch.this.glassD2File, false);
                    if (glass == null) {
                        log.warning("Error during glass reading, please check");
                        return;
                    }
                    glass.copyExZonesFromImage(this.dataWork); //TODO comprovar que funcioni he canviat el metode (era copyMaskPixelsFromImage)

                    //escalat del vidre
                    glass = ImgOps.correctGlass(glass);
                    if (this.glassFactor < 0) {
                        //calculem factor d'escala mes petit
                        this.glassFactor = ImgOps.calcGlassScale(this.dataWork, glass);
                    }

                    //treiem el fons
                    //les dades amb vidre sostret seran les d'entrada a posteriors operacions
                    this.dataWork = ImgOps.subtractBKG_v2(this.dataWork, glass, this.glassFactor)[0];

                    log.info("Glass subtraction... DONE!");
                }

                if (this.doBkg) {
                    log.info("Background subtraction... ");
                    //metode
                    if (this.bkgOpt.equalsIgnoreCase("avsq")) {
                        //necessitem 2 imatges (la iteracio anterior i la nova)
                        final Pattern2D it[] = new Pattern2D[2];
                        it[0] = ImgOps.firstBkgPass(this.dataWork);
                        for (int i = 0; i < this.bkgIter; i++) {
                            it[1] = ImgOps.calcIterAvsq(it[0], this.bkgN, BackgroundEstimation_batch.this.progressBar);
                            it[0] = it[1]; //actualitzem l'anterior
                        }
                        this.datafons = it[1];
                        //treiem el fons (ultima iteracio)
                        this.dataWork = ImgOps.subtractBKG(this.dataWork, it[1]);
                    }

                    if (this.bkgOpt.equalsIgnoreCase("avarc")) {
                        //necessitem 2 imatges (la iteracio anterior i la nova)
                        final Pattern2D it[] = new Pattern2D[2];
                        it[0] = ImgOps.firstBkgPass(this.dataWork);
                        for (int i = 0; i < this.bkgIter; i++) {
                            it[1] = ImgOps.calcIterAvarc(it[0], this.amplada, this.angle,
                                    BackgroundEstimation_batch.this.progressBar);
                            it[0] = it[1]; //actualitzem l'anterior
                        }
                        this.datafons = it[1];
                        //treiem el fons
                        this.dataWork = ImgOps.subtractBKG(this.dataWork, it[1]);
                        //				        dataIn = it[1]; //debug
                    }

                    if (this.bkgOpt.equalsIgnoreCase("avcirc")) {
                        //nomes s'ha de fer una passada
                        final Pattern2D it[] = new Pattern2D[2];
                        it[0] = ImgOps.firstBkgPass(this.dataWork);
                        it[1] = ImgOps.calcIterAvcirc(it[0], this.stepsize,
                                BackgroundEstimation_batch.this.progressBar);
                        this.datafons = it[1];
                        //treiem el fons
                        this.dataWork = ImgOps.subtractBKG(this.dataWork, it[1]);
                    }

                    //calcul del fons metode min (antic "flip")
                    if (this.bkgOpt.equalsIgnoreCase("minsq")) {
                        final Pattern2D fons = ImgOps.bkgMin(this.dataWork, this.fhor, this.fver, this.fhorver,
                                this.aresta, this.angle, this.amplada, false,
                                BackgroundEstimation_batch.this.progressBar);
                        this.datafons = fons;
                        //treiem el fons
                        this.dataWork = ImgOps.subtractBKG(this.dataWork, fons);
                    }

                    if (this.bkgOpt.equalsIgnoreCase("minarc")) {
                        final Pattern2D fons = ImgOps.bkgMin(this.dataWork, this.fhor, this.fver, this.fhorver,
                                this.aresta, this.angle, this.amplada, true,
                                BackgroundEstimation_batch.this.progressBar);
                        this.datafons = fons;
                        //treiem el fons
                        this.dataWork = ImgOps.subtractBKG(this.dataWork, fons);
                    }
                    log.info("Background subtraction... DONE!");
                }

                if (this.doLP) {
                    log.info("LP correction... ");
                    this.dataWork = ImgOps.corrLP(this.dataWork, this.ipol, this.ilor, -1, false);//EIX PER DEFECTE -1 VERTICAL
                    log.info("LP correction... DONE!");
                }

            } catch (final Exception e) {
                if (D2Dplot_global.isDebug())
                    e.printStackTrace();
                BackgroundEstimation_batch.this.interrupted = true;
                //per si s'ha aturat
                return;
            }

        }
    }

    private static class NoneSelectedButtonGroup extends ButtonGroup {

        /**
         *
         */
        private static final long serialVersionUID = -6414612082140761684L;

        @Override
        public void setSelected(ButtonModel model, boolean selected) {

            if (selected) {

                super.setSelected(model, selected);

            } else {

                this.clearSelection();
            }
        }
    }

    protected void do_btnSelectFiles_actionPerformed(ActionEvent arg0) {

        // Creem un filechooser per seleccionar el fitxer obert
        final JFileChooser fileChooser = new JFileChooser();
        final File startDir = new File(MainFrame.getWorkdir());
        fileChooser.setCurrentDirectory(startDir); // directori inicial: el del
        fileChooser.setMultiSelectionEnabled(true);
        final FileNameExtensionFilter[] filter = { new FileNameExtensionFilter("2D Data file (bin,img,spr,gfrm,edf)",
                "bin", "img", "spr", "gfrm", "edf") };
        for (final FileNameExtensionFilter element : filter) {
            fileChooser.addChoosableFileFilter(element);
        }
        // si s'ha seleccionat un fitxer
        File[] flist;
        if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            flist = fileChooser.getSelectedFiles();
        } else {
            return;
        }

        //poblem la llista
        final DefaultListModel<File> lm = new DefaultListModel<File>();
        for (final File element : flist) {
            lm.addElement(element);
        }
        this.listfiles.setModel(lm);

    }

    protected void do_btnClear_actionPerformed(ActionEvent arg0) {
        try {
            ((DefaultListModel<File>) this.listfiles.getModel()).clear();
        } catch (final Exception e) {
            if (D2Dplot_global.isDebug())
                e.printStackTrace();
            log.warning("Error clearing list");
        }

    }

    public MainFrame getMf() {
        return this.mf;
    }

    public void setMf(MainFrame mf) {
        this.mf = mf;
    }
}
