package com.vava33.d2dplot;

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
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;

import javax.swing.ButtonGroup;
import javax.swing.ButtonModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
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

public class BackgroundEstimation {

    private final JDialog d2DsubDialog;
    private JButton btnSelectFile;
    private JButton btnReload;
    private JButton btnStop;
    private JCheckBox chckbxFactor;
    private JCheckBox checkBox;
    private final JPanel contentPanel;
    private JLabel label;
    private JLabel lblBeforeCalculation;
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
    private JTextField txtPathBefore;

    //    private Thread runBkg;
    private bkgsubtraction_worker bkgwkr;
    private File glassD2File;
    private Pattern2D pattBef, pattAft, pattBkg;
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
    private JButton btnViewbkg;

    private Pattern2D initialPattern;
    private MainFrame mf;
    private static final String className = "BKG_sub";
    private static VavaLogger log = D2Dplot_global.getVavaLogger(className);
    private JSeparator separator_5;
    private JCheckBox chckbxVRotAxis;
    private JCheckBox chckbxHRotAxis;
    private JLabel lblRotAxis;

    /**
     * Create the dialog.
     */
    public BackgroundEstimation(MainFrame m) {
        this.d2DsubDialog = new JDialog(m.getMainF(), "Background subtraction and LP correction", false);
        this.contentPanel = new JPanel();
        this.d2DsubDialog.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent arg0) {
                BackgroundEstimation.this.do_this_windowClosing(arg0);
            }
        });
        UIManager.put("ProgressBar.selectionBackground", Color.black);
        this.d2DsubDialog.setIconImage(
                Toolkit.getDefaultToolkit().getImage(BackgroundEstimation.class.getResource("/img/Icona.png")));
        this.d2DsubDialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        this.d2DsubDialog.getContentPane().setLayout(new MigLayout("", "", ""));
        this.d2DsubDialog.getContentPane().add(this.contentPanel, "cell 0 0,alignx left,aligny top");
        this.contentPanel.setLayout(new MigLayout("fill,insets 0", "[grow]", "[grow]"));
        this.panel_top = new JPanel();
        this.contentPanel.add(this.panel_top, "cell 0 0,grow");
        this.panel_top.setLayout(new MigLayout("fill", "[grow]", "[][][][grow]"));
        this.panelGlass = new JPanel();
        this.panelGlass.setBorder(
                new TitledBorder(null, "Glass subtraction", TitledBorder.LEADING, TitledBorder.TOP, null, null));
        this.panel_top.add(this.panelGlass, "cell 0 0,grow");
        {
            this.btnSelectFile = new JButton("select glass file");
            this.btnSelectFile.setMargin(new Insets(2, 6, 2, 6));
            this.btnSelectFile.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent arg0) {
                    BackgroundEstimation.this.do_btnSelectFile_actionPerformed(arg0);
                }
            });
            this.panelGlass.setLayout(new MigLayout("", "[][][][grow][][]", "[]"));
            {
                this.chk_doGlass = new JCheckBox("Do it!");
                this.panelGlass.add(this.chk_doGlass, "cell 0 0,alignx left,aligny center");
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
                    BackgroundEstimation.this.do_chckbxFactor_itemStateChanged(arg0);
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
        this.panel_top.add(this.panelBkg, "cell 0 1,grow");
        {
            this.comboBox = new JComboBox<String>();
            this.comboBox.addItemListener(new ItemListener() {
                @Override
                public void itemStateChanged(ItemEvent arg0) {
                    BackgroundEstimation.this.do_comboBox_itemStateChanged(arg0);
                }
            });
            this.panelBkg
                    .setLayout(new MigLayout("", "[][][][grow][][grow][][grow][][][grow][][grow][][grow][][][]", "[]"));
            {
                this.chk_doBkg = new JCheckBox("Do it!");
                this.panelBkg.add(this.chk_doBkg, "cell 0 0,alignx left,aligny center");
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
        this.panel_top.add(this.panel, "cell 0 2,grow");
        this.panel.setLayout(new MigLayout("", "[][][][][][][][][][][][][]", "[]"));
        {
            this.chk_doLP = new JCheckBox("Do it!");
            this.panel.add(this.chk_doLP, "cell 0 0,alignx left,aligny center");
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
            this.chckbxVRotAxis = new JCheckBox("V");
            this.buttonGroup_2.add(this.chckbxVRotAxis);
            this.panel.add(this.chckbxVRotAxis, "cell 11 0,alignx center,aligny center");
        }
        {
            this.chckbxHRotAxis = new JCheckBox("H");
            this.buttonGroup_2.add(this.chckbxHRotAxis);
            this.panel.add(this.chckbxHRotAxis, "cell 12 0,alignx center,aligny center");
        }
        {
            this.panel_runcontrols = new JPanel();
            this.panel_top.add(this.panel_runcontrols, "cell 0 3,grow");
            {
                this.btnRun = new JButton("Run!");
                this.btnRun.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent arg0) {
                        BackgroundEstimation.this.do_btnRun_actionPerformed(arg0);
                    }
                });
                this.panel_runcontrols.setLayout(new MigLayout("", "[][][grow][][]", "[][]"));
                {
                    this.lblBeforeCalculation = new JLabel("Source Image");
                    this.panel_runcontrols.add(this.lblBeforeCalculation, "cell 0 0,growx,aligny center");
                }
                {
                    this.txtPathBefore = new JTextField();
                    this.panel_runcontrols.add(this.txtPathBefore, "cell 1 0 2 1,growx,aligny center");
                    this.txtPathBefore.setEditable(false);
                    this.txtPathBefore.setColumns(10);
                }
                {
                    this.btnViewbkg = new JButton("view BKG");
                    this.btnViewbkg.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            BackgroundEstimation.this.do_btnViewBkg_actionPerformed(e);
                        }
                    });
                    this.btnReload = new JButton("Reload Source");
                    this.panel_runcontrols.add(this.btnReload, "cell 3 0,alignx center,aligny center");
                    this.btnReload.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            BackgroundEstimation.this.do_btnReload_actionPerformed(e);
                        }
                    });
                    this.btnViewbkg.setMargin(new Insets(2, 4, 2, 4));
                    this.btnViewbkg.setEnabled(false);
                    this.panel_runcontrols.add(this.btnViewbkg, "cell 4 0,alignx center,aligny center");
                }
                this.panel_runcontrols.add(this.btnRun, "cell 0 1,growx,aligny center");
            }
            {
                this.btnStop = new JButton("Stop!");
                this.panel_runcontrols.add(this.btnStop, "cell 1 1,alignx center,aligny center");
                this.btnStop.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent arg0) {
                        BackgroundEstimation.this.do_btnStop_actionPerformed(arg0);
                    }
                });
                this.btnStop.setBackground(Color.RED);
            }
            {
                this.progressBar = new JProgressBar();
                this.panel_runcontrols.add(this.progressBar, "cell 2 1 3 1,grow");
                this.progressBar.setFont(new Font("Tahoma", Font.BOLD, 15));
            }
        }

        {
            final JPanel buttonPane = new JPanel();
            this.d2DsubDialog.getContentPane().add(buttonPane, "cell 0 1,growx,aligny top");
            buttonPane.setLayout(new MigLayout("", "[][][grow]", "[]"));
            {
                {
                    this.checkBox = new JCheckBox("on top");
                    buttonPane.add(this.checkBox, "cell 0 0,alignx left,aligny center");
                    this.checkBox.addItemListener(new ItemListener() {
                        @Override
                        public void itemStateChanged(ItemEvent arg0) {
                            BackgroundEstimation.this.do_checkBox_itemStateChanged(arg0);
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
                    BackgroundEstimation.this.do_label_mouseEntered(arg0);
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    BackgroundEstimation.this.do_label_mouseExited(e);
                }

                @Override
                public void mouseReleased(MouseEvent e) {
                    BackgroundEstimation.this.do_label_mouseReleased(e);
                }
            });
            buttonPane.add(this.label, "cell 1 0,alignx left,aligny center");
            this.label.setFont(new Font("Tahoma", Font.BOLD, 14));
            final JButton okButton = new JButton("close");
            buttonPane.add(okButton, "cell 2 0,alignx right,aligny top");
            okButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent arg0) {
                    BackgroundEstimation.this.do_okButton_actionPerformed(arg0);
                }
            });
            okButton.setActionCommand("OK");
            this.d2DsubDialog.getRootPane().setDefaultButton(okButton);
        }

        final Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        this.d2DsubDialog.setBounds(0, 0, 1000, 313);
        final int width = this.d2DsubDialog.getWidth();
        final int height = this.d2DsubDialog.getHeight();
        final int x = (screen.width - width) / 2;
        final int y = (screen.height - height) / 2;
        this.d2DsubDialog.setBounds(x, y, width, height);

        this.setMf(m);
        this.d2DsubDialog.pack();
        this.userInit();
        this.activeOptions();
    }

    public void userInit() {
        this.setInitialPattern(this.mf.getPatt2D());
        //Diagrama inicial
        if (this.getPattBef() == null) {
            this.setPattBef(this.getInitialPattern());
            this.txtPathBefore.setText(this.getPattBef().getImgfile().toString());
        }
        //mostrem titol i versio D2Dsub
        log.info("** Background estimation/subtraction & LP correction **");
        log.info("Initial file= " + this.getPattBef().getImgfile().toString());
    }

    public Pattern2D getInitialPattern() {
        return this.initialPattern;
    }

    public void setInitialPattern(Pattern2D patt) {
        this.initialPattern = patt;
    }

    public MainFrame getMf() {
        return this.mf;
    }

    public void setMf(MainFrame mf) {
        this.mf = mf;
    }

    public Pattern2D getPattBef() {
        return this.pattBef;
    }

    public void setPattBef(Pattern2D pattBef) {
        this.pattBef = pattBef;
        if (this.getPattBef().getImgfile() != null) {
            this.txtPathBefore.setText(this.getPattBef().getImgfile().toString());
        } else {
            this.txtPathBefore.setText("Data not saved");
        }

    }

    public Pattern2D getPattAft() {
        return this.pattAft;
    }

    public void setPattAft(Pattern2D pattAft) {
        this.pattAft = pattAft;
        //s'ha de mostrar al mainframe i activar reload previous:
        if (this.getMf() != null) {
            this.getMf().updatePatt2D(this.pattAft, false, false);
        }
        this.btnReload.setText("Reload Source");
    }

    public Pattern2D getPattBkg() {
        return this.pattBkg;
    }

    public void setPattBkg(Pattern2D pattBkg) {
        this.pattBkg = pattBkg;
    }

    public File getGlassD2File() {
        return this.glassD2File;
    }

    public void setGlassD2File(File glassD2File) {
        this.glassD2File = glassD2File;
    }

    protected void do_btnViewBkg_actionPerformed(ActionEvent e) {
        final SimpleImageDialog sid = new SimpleImageDialog(this.getMf().getMainF(), this.getPattBkg(),
                "Background image");
        sid.setVisible(true);
    }

    protected void do_btnSelectFile_actionPerformed(ActionEvent arg0) {

        final FileNameExtensionFilter[] filter = { new FileNameExtensionFilter("2D Data file (bin,img,spr,gfrm,edf)",
                "bin", "img", "spr", "gfrm", "edf") };
        this.glassD2File = FileUtils.fchooserOpen(this.d2DsubDialog, new File(MainFrame.getWorkdir()), filter, 0);
        if (this.glassD2File == null)
            return;
        D2Dplot_global.setWorkdir(this.glassD2File);
        log.info("Glass file selected: " + this.glassD2File.getPath());
        this.lblGlassF.setText(this.glassD2File.getName());
    }

    protected void do_btnReload_actionPerformed(ActionEvent e) {
        //torna a carregar el pattBefore al mainframe o b� el resultat
        if (this.btnReload.getText().startsWith("Reload")) {
            if (this.getMf() != null) {
                this.getMf().updatePatt2D(this.getPattBef(), false, false);
            }
            this.btnReload.setText("Load Result");
        } else { //load result
            if (this.getMf() != null) {
                this.getMf().updatePatt2D(this.getPattAft(), false, false);
            }
            this.btnReload.setText("Reload Source");
        }
    }

    //    protected boolean saveResult(){
    //    	if(this.getPattAft()==null)return true;
    //    	//filechoser save file
    //    	FileNameExtensionFilter[] filter = {new FileNameExtensionFilter("2D Data file (bin)", "bin")};
    //    	File fsave = FileUtils.fchooserSaveAsk(d2DsubDialog,new File(MainFrame.getWorkdir()), filter, null);
    //    	if (fsave == null){
    //    		log.warning("File not saved");
    //    		return false;
    //    	}
    //        D2Dplot_global.setWorkdir(fsave);
    //    	fsave = ImgFileUtils.writeBIN(fsave, this.getPattAft());
    //    	this.getPattAft().setImgfile(fsave);
    //        if (this.getMf()!=null){
    //            this.getMf().updatePatt2D(fsave);    
    //        }
    //        log.info("File saved: "+fsave.toString());
    //    	return true;
    //    }

    private void stopProcess() {
        if (this.bkgwkr != null) {
            try {
                this.bkgwkr.cancel(true);
            } catch (final Exception e) {
                if (D2Dplot_global.isDebug())
                    e.printStackTrace();
                log.warning("Error stopping Background Subtraction");
            }
        }
        this.bkgwkr = null;
    }

    protected void do_btnStop_actionPerformed(ActionEvent arg0) {
        //preguntar per si de cas
        final int n = JOptionPane.showConfirmDialog(this.d2DsubDialog, "Abort current operation?", "Stop",
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
        this.d2DsubDialog.setAlwaysOnTop(this.checkBox.isSelected());
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
        hd.getHelpDialog().setLocationRelativeTo(this.d2DsubDialog);
        hd.getHelpDialog().setVisible(true);
    }

    protected void do_okButton_actionPerformed(ActionEvent arg0) {
        this.tancarD2Dsub();
    }

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
        //hi ha imatge after i es diferent a la before? quina utilizar d'entrada
        if (this.getPattAft() != null && !this.getPattBef().esIgualA(this.getPattAft())) {
            //Preguntem
            final int n = JOptionPane.showConfirmDialog(this.d2DsubDialog,
                    "There is already a result image. Use it as source?", "Update Source Image",
                    JOptionPane.YES_NO_OPTION);
            if (n == JOptionPane.YES_OPTION) {
                this.setPattBef(this.getPattAft());
                if (this.getMf() != null) {
                    this.getMf().updatePatt2D(this.getPattBef(), false, false);
                }
            }
        }

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
        int iosc = 2; // eix de tilt, 1=horitzontal, 2=vertical

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
                    log.warning("Error reading glass factor");
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
            iosc = this.chckbxHRotAxis.isSelected() ? 1 : 2;
        }

        if (!doGlass && !doBkg && !doLP) {
            log.warning("No operation selected");
            return;
        }

        //		bkgsubtraction opts = new bkgsubtraction(doGlass,doBkg,doLP,opt);
        //		opts.readOptions(bkgIter, bkgN, fhor, fver, fhorver, aresta, ilor, ipol, iosc, glassFactor, amplada, angle, stepsize);
        //		runBkg = new Thread(opts);
        //		runBkg.start();	

        this.bkgwkr = new bkgsubtraction_worker(doGlass, doBkg, doLP, opt);
        this.bkgwkr.readOptions(bkgIter, bkgN, fhor, fver, fhorver, aresta, ilor, ipol, iosc, glassFactor, amplada,
                angle, stepsize);
        this.bkgwkr.addPropertyChangeListener(new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if (BackgroundEstimation.this.bkgwkr.isDone()) {
                    if (BackgroundEstimation.this.bkgwkr.isCancelled()) {
                        log.info("Background Estimation canceled");
                    } else {
                        log.info("Background Estimation finished!!");
                    }
                }
            }
        });
        this.bkgwkr.execute();
    }

    //new d2dsub java implemented
    public class bkgsubtraction_worker extends SwingWorker<Integer, Integer> {
        private final String bkgOpt;
        private final boolean doGlass, doBkg, doLP;
        float glassFactor, amplada, angle, stepsize;
        int bkgIter, bkgN, fhor, fver, fhorver, aresta, ilor, ipol, iosc;

        public bkgsubtraction_worker(boolean doGlass, boolean doBkg, boolean doLP, String bkgOption) {
            super();
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
        protected Integer doInBackground() throws Exception {

            Pattern2D dataWork;
            boolean bkgfile;
            try {
                dataWork = BackgroundEstimation.this.getPattBef();
                bkgfile = false;

                BackgroundEstimation.this.progressBar.setIndeterminate(true);
                BackgroundEstimation.this.progressBar.setString("d2Dsub Running");
                BackgroundEstimation.this.progressBar.setStringPainted(true);

                // 1r sostraiem el vidre si s'ha seleccionat
                if (this.doGlass) {
                    // comprovem si hi ha seleccionat fitxer vidre
                    if (BackgroundEstimation.this.glassD2File == null
                            || !BackgroundEstimation.this.glassD2File.exists()) {
                        log.warning("Select a valid glass image first");
                        throw new Exception();
                    }

                    log.info("Glass subtraction... ");

                    //preparacio dades
                    Pattern2D glass = ImgFileUtils.readPatternFile(BackgroundEstimation.this.glassD2File, false);
                    if (glass == null) {
                        log.warning("Error during glass reading, please check");
                        return -1;
                    }
                    glass.copyExZonesFromImage(dataWork); //TODO comprovar que funcioni he canviat el metode (era copyMaskPixelsFromImage)

                    //escalat del vidre
                    glass = ImgOps.correctGlass(glass);
                    if (this.glassFactor < 0) {
                        //calculem factor d'escala mes petit
                        this.glassFactor = ImgOps.calcGlassScale(dataWork, glass);
                    }

                    //treiem el fons
                    //les dades amb vidre sostret seran les d'entrada a posteriors operacions
                    dataWork = ImgOps.subtractBKG_v2(dataWork, glass, this.glassFactor)[0];

                    log.info("Glass subtraction... DONE!");
                }

                if (this.doBkg) {
                    log.info("Background subtraction... ");
                    //metode
                    if (this.bkgOpt.equalsIgnoreCase("avsq")) {
                        //necessitem 2 imatges (la iteracio anterior i la nova)
                        final Pattern2D it[] = new Pattern2D[2];
                        //				        it[0] = ImgOps.firstBkgPassStronger(dataWork); //Nov18 TODO test changed to stronger!
                        it[0] = ImgOps.firstBkgPass(dataWork);
                        for (int i = 0; i < this.bkgIter; i++) {
                            it[1] = ImgOps.calcIterAvsq(it[0], this.bkgN, BackgroundEstimation.this.progressBar);
                            it[0] = it[1]; //actualitzem l'anterior
                        }
                        //guardem l'ultima iteracio del fons
                        BackgroundEstimation.this.setPattBkg(it[1]);
                        //treiem el fons
                        dataWork = ImgOps.subtractBKG(dataWork, it[1]);
                    }

                    if (this.bkgOpt.equalsIgnoreCase("avarc")) {
                        //necessitem 2 imatges (la iteracio anterior i la nova)
                        final Pattern2D it[] = new Pattern2D[2];
                        it[0] = ImgOps.firstBkgPass(dataWork);
                        for (int i = 0; i < this.bkgIter; i++) {
                            it[1] = ImgOps.calcIterAvarc(it[0], this.amplada, this.angle,
                                    BackgroundEstimation.this.progressBar);
                            it[0] = it[1]; //actualitzem l'anterior
                        }
                        //guardem l'ultima iteracio del fons
                        BackgroundEstimation.this.setPattBkg(it[1]);
                        //treiem el fons
                        dataWork = ImgOps.subtractBKG(dataWork, it[1]);
                    }

                    if (this.bkgOpt.equalsIgnoreCase("avcirc")) {
                        //nomes s'ha de fer una passada
                        final Pattern2D it[] = new Pattern2D[2];
                        it[1] = ImgOps.calcIterAvcirc(it[0], this.stepsize, BackgroundEstimation.this.progressBar);
                        //guardem el fons
                        BackgroundEstimation.this.setPattBkg(it[1]);
                        //treiem el fons
                        dataWork = ImgOps.subtractBKG(dataWork, it[1]);
                    }

                    //calcul del fons metode min (antic "flip")
                    if (this.bkgOpt.equalsIgnoreCase("minsq")) {
                        final Pattern2D fons = ImgOps.bkgMin(dataWork, this.fhor, this.fver, this.fhorver, this.aresta,
                                this.angle, this.amplada, false, BackgroundEstimation.this.progressBar);
                        //guardem el fons
                        BackgroundEstimation.this.setPattBkg(fons);
                        //treiem el fons
                        dataWork = ImgOps.subtractBKG(dataWork, fons);
                    }

                    if (this.bkgOpt.equalsIgnoreCase("minarc")) {
                        final Pattern2D fons = ImgOps.bkgMin(dataWork, this.fhor, this.fver, this.fhorver, this.aresta,
                                this.angle, this.amplada, true, BackgroundEstimation.this.progressBar);
                        //guardem el fons
                        BackgroundEstimation.this.setPattBkg(fons);
                        //treiem el fons
                        dataWork = ImgOps.subtractBKG(dataWork, fons);
                    }
                    bkgfile = true;
                    log.info("Background subtraction... DONE!");
                }

                if (this.doLP) {
                    log.info("LP correction... ");
                    dataWork = ImgOps.corrLP(dataWork, this.ipol, this.ilor, this.iosc, false); //EIX PER DEFECTE -1 VERTICAL
                    log.info("LP correction... DONE!");
                }

            } catch (final Exception e) {
                if (D2Dplot_global.isDebug())
                    e.printStackTrace();
                if (BackgroundEstimation.this.bkgwkr == null) {
                    log.warning("*** Run STOPPED ***");
                } else {
                    log.warning("*** Run ERROR ***");
                }
                BackgroundEstimation.this.progressBar.setIndeterminate(false);
                BackgroundEstimation.this.progressBar.setStringPainted(false);
                BackgroundEstimation.this.bkgwkr = null;
                return -1;
            }

            // un cop aqui s'hauria d'haver acabat l'execucio
            log.info("End of Background Estimation procedure");
            BackgroundEstimation.this.progressBar.setIndeterminate(false);
            BackgroundEstimation.this.progressBar.setStringPainted(false);

            //carreguem el resultat
            BackgroundEstimation.this.setPattAft(dataWork);
            if (bkgfile) {
                BackgroundEstimation.this.btnViewbkg.setEnabled(true);
            } else {
                BackgroundEstimation.this.btnViewbkg.setEnabled(false);
            }
            return 0;
        }
    }


    private static class NoneSelectedButtonGroup extends ButtonGroup {

        /**
         *
         */
        private static final long serialVersionUID = 1L;

        @Override
        public void setSelected(ButtonModel model, boolean selected) {

            if (selected) {

                super.setSelected(model, selected);

            } else {

                this.clearSelection();
            }
        }
    }

    protected void do_this_windowClosing(WindowEvent arg0) {
        this.tancarD2Dsub();
    }

    private void tancarD2Dsub() {
        if (this.getPattAft() != null && this.getPattAft().getImgfile() == null) {
            //preguntar per salvar
            //	        int n = JOptionPane.showConfirmDialog(d2DsubDialog, "Save result image?", "Save image",
            //	                JOptionPane.YES_NO_OPTION);
            //	        if (n == JOptionPane.YES_OPTION) {
            //	        	//filechoser save file
            //	        	FileNameExtensionFilter[] filter = {new FileNameExtensionFilter("2D Data file (bin)", "bin")};
            //	        	File fsave = FileUtils.fchooserSaveAsk(d2DsubDialog,new File(MainFrame.getWorkdir()), filter, null);
            //	        	if (fsave == null){
            //	        		log.warning("Background subtracted data not saved to image file!");
            //	        	}
            //	        	ImgFileUtils.writeBIN(fsave, this.getPattAft());
            //	        	D2Dplot_global.setWorkdir(fsave);
            //	        	this.getPattAft().setImgfile(fsave);
            //	            if (this.getMf()!=null){
            //	                this.getMf().updatePatt2D(fsave);    
            //	            }
            //	        }
            log.info("Remember to save the result pattern");
        }
        this.dispose();
    }

    //	protected void do_btnSaveResult_actionPerformed(ActionEvent arg0) {
    //		boolean saved = this.saveResult();
    //		if (saved){
    //	    	//preguntar per posar com a source
    //	        int n = JOptionPane.showConfirmDialog(d2DsubDialog, "Set saved image as source?", "Update source image",
    //	                JOptionPane.YES_NO_OPTION);
    //	        if (n == JOptionPane.YES_OPTION) {
    //	        	//filechoser save file
    //	        	this.setPattBef(this.getPattAft());
    //	        }
    //		}
    //	}
    public void dispose() {
        this.stopProcess();
        this.d2DsubDialog.dispose();
    }

    public void setVisible(boolean vis) {
        this.d2DsubDialog.setVisible(vis);
    }
}
