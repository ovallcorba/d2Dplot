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

import javax.swing.ButtonModel;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ButtonGroup;

import com.vava33.d2dplot.auxi.ImgFileUtils;
import com.vava33.d2dplot.auxi.ImgOps;
import com.vava33.d2dplot.auxi.Pattern2D;
import com.vava33.jutils.FileUtils;
import com.vava33.jutils.VavaLogger;

import javax.swing.JSeparator;
import javax.swing.JList;

import net.miginfocom.swing.MigLayout;

public class BackgroundEstimation_batch {
	
    private JDialog d2dsubBatchDialog;
    private JButton btnSelectFile;
    private JButton btnStop;
    private JCheckBox chckbxFactor;
    private JCheckBox checkBox;
    private JPanel contentPanel;
    private JLabel label;
    private JLabel lblGlassF;
    private JLabel lblIter;
    private JLabel lblN;
    private JPanel panel;
    private JPanel panel_top;
    private JPanel panelBkg;
    private JPanel panelGlass;
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
    	this.mf=mf;
    	d2dsubBatchDialog = new JDialog(mf.getMainF(),"Background subtraction and LP correction (batch processing)",false);
    	this.contentPanel=new JPanel();
        UIManager.put("ProgressBar.selectionBackground", Color.black);
        d2dsubBatchDialog.setIconImage(Toolkit.getDefaultToolkit().getImage(BackgroundEstimation_batch.class.getResource("/img/Icona.png")));
        d2dsubBatchDialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        d2dsubBatchDialog.getContentPane().setLayout(new BorderLayout());
        d2dsubBatchDialog.getContentPane().add(this.contentPanel, BorderLayout.CENTER);
        contentPanel.setLayout(new MigLayout("", "[grow]", "[grow]"));
        this.panel_top = new JPanel();
        contentPanel.add(panel_top, "cell 0 0,grow");
        panel_top.setLayout(new MigLayout("insets 0,fill", "[grow][]", "[][][][]"));
        {
        	panel_1 = new JPanel();
        	panel_top.add(panel_1, "cell 0 0 1 4,grow");
        	{
        		btnSelectFiles = new JButton("Select files");
        		btnSelectFiles.addActionListener(new ActionListener() {
        			public void actionPerformed(ActionEvent arg0) {
        				do_btnSelectFiles_actionPerformed(arg0);
        			}
        		});
        		panel_1.setLayout(new MigLayout("", "[grow][]", "[][grow]"));
        		panel_1.add(btnSelectFiles, "cell 0 0,alignx left,aligny top");
        	}
        	{
        		btnClear = new JButton("clear");
        		btnClear.addActionListener(new ActionListener() {
        			public void actionPerformed(ActionEvent arg0) {
        				do_btnClear_actionPerformed(arg0);
        			}
        		});
        		panel_1.add(btnClear, "cell 1 0,alignx center,aligny center");
        	}
        	{
        		scrollPane = new JScrollPane();
        		panel_1.add(scrollPane, "cell 0 1 2 1,grow");
        		{
        			listfiles = new JList<File>();
        			scrollPane.setViewportView(listfiles);
        		}
        	}
        }
        this.panelGlass = new JPanel();
        this.panelGlass.setBorder(new TitledBorder(null, "Glass subtraction", TitledBorder.LEADING,
                TitledBorder.TOP, null, null));
        this.panel_top.add(this.panelGlass, "cell 1 0,grow");
        {
            this.btnSelectFile = new JButton("select glass file");
            this.btnSelectFile.setMargin(new Insets(2, 6, 2, 6));
            this.btnSelectFile.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent arg0) {
                    do_btnSelectFile_actionPerformed(arg0);
                }
            });
            panelGlass.setLayout(new MigLayout("", "[][][][grow][][]", "[]"));
            {
            	chk_doGlass = new JCheckBox("Do it!");
            	panelGlass.add(chk_doGlass, "cell 0 0,alignx center,aligny center");
            }
            {
            	separator_3 = new JSeparator();
            	separator_3.setOrientation(SwingConstants.VERTICAL);
            	panelGlass.add(separator_3, "cell 1 0,alignx center,growy");
            }
            this.panelGlass.add(this.btnSelectFile, "cell 2 0,alignx left,aligny center");
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
                this.panelGlass.add(this.lblGlassF, "cell 3 0,alignx left,aligny center");
            }
            this.panelGlass.add(this.chckbxFactor, "cell 4 0,alignx center,aligny center");
        }
        {
            this.txtFactor = new JTextField();
            txtFactor.setBackground(Color.WHITE);
            this.txtFactor.setEnabled(false);
            this.txtFactor.setText("1.000");
            this.txtFactor.setColumns(4);
            this.panelGlass.add(this.txtFactor, "cell 5 0,growx,aligny center");
        }
        this.panelBkg = new JPanel();
        this.panelBkg.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Background estimation & subtraction", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
        this.panel_top.add(this.panelBkg, "cell 1 1,grow");
        {
            this.comboBox = new JComboBox<String>();
            this.comboBox.addItemListener(new ItemListener() {
                public void itemStateChanged(ItemEvent arg0) {
                    do_comboBox_itemStateChanged(arg0);
                }
            });
            panelBkg.setLayout(new MigLayout("", "[][][][grow][][grow][][grow][][][grow][][grow][][grow][][][]", "[]"));
            {
            	chk_doBkg = new JCheckBox("Do it!");
            	panelBkg.add(chk_doBkg, "cell 0 0,alignx center,aligny center");
            }
            {
            	separator_2 = new JSeparator();
            	separator_2.setOrientation(SwingConstants.VERTICAL);
            	panelBkg.add(separator_2, "cell 1 0,alignx center,growy");
            }
            {
            	lblMehod = new JLabel("Mehod");
            	panelBkg.add(lblMehod, "cell 2 0,alignx right,aligny center");
            }
            this.comboBox.setModel(new DefaultComboBoxModel<String>(new String[] {"avsq", "avarc", "avcirc", "minsq", "minarc"}));
            this.panelBkg.add(this.comboBox, "cell 3 0,growx,aligny center");
        }
        {
            this.lblN = new JLabel("Npix=");
            this.panelBkg.add(this.lblN, "cell 4 0,alignx right,aligny center");
        }
        {
            this.txtN = new JTextField();
            txtN.setBackground(Color.WHITE);
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
            txtIter.setBackground(Color.WHITE);
            this.txtIter.setText("10");
            this.panelBkg.add(this.txtIter, "cell 7 0,growx,aligny center");
            this.txtIter.setColumns(2);
        }
        {
        	separator_4 = new JSeparator();
        	separator_4.setOrientation(SwingConstants.VERTICAL);
        	panelBkg.add(separator_4, "cell 8 0,alignx center,growy");
        }
        {
            this.lblAwidth = new JLabel("wth=");
            this.panelBkg.add(this.lblAwidth, "cell 9 0,alignx right,aligny center");
        }
        {
            this.txtAmplada = new JTextField();
            txtAmplada.setBackground(Color.WHITE);
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
            txtAngle.setBackground(Color.WHITE);
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
            txtStep.setBackground(Color.WHITE);
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
        this.panel.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Lorentz & Polarization corrections", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
        this.panel_top.add(this.panel, "cell 1 2,grow");
        panel.setLayout(new MigLayout("", "[][][][][][][][][][][][][]", "[]"));
        {
        	chk_doLP = new JCheckBox("Do it!");
        	panel.add(chk_doLP, "cell 0 0,alignx center,aligny center");
        }
        {
        	separator_1 = new JSeparator();
        	separator_1.setOrientation(SwingConstants.VERTICAL);
        	panel.add(separator_1, "cell 1 0,alignx center,growy");
        }
        {
            this.lblLor = new JLabel("Lorentz:");
            this.panel.add(this.lblLor, "cell 2 0,alignx center,aligny center");
        }
        {
            this.chckbxLorOscil = new JCheckBox("Oscillating");
            buttonGroup.add(this.chckbxLorOscil);
            this.panel.add(this.chckbxLorOscil, "cell 3 0,alignx left,aligny center");
        }
        {
            this.chckbxLorPow = new JCheckBox("Powder");
            buttonGroup.add(this.chckbxLorPow);
            this.panel.add(this.chckbxLorPow, "cell 4 0,alignx left,aligny center");
        }
        {
        	separator = new JSeparator();
        	separator.setOrientation(SwingConstants.VERTICAL);
        	panel.add(separator, "cell 5 0,alignx center,growy");
        }
        {
            this.lblPol = new JLabel("Polarization:");
            this.panel.add(this.lblPol, "cell 6 0,alignx center,aligny center");
        }
        {
            this.chckbxPolSyn = new JCheckBox("Synchrotron");
            buttonGroup_1.add(this.chckbxPolSyn);
            this.panel.add(this.chckbxPolSyn, "cell 7 0,alignx left,aligny center");
        }
        {
            this.chckbxPolLab = new JCheckBox("Laboratory");
            buttonGroup_1.add(this.chckbxPolLab);
            this.panel.add(this.chckbxPolLab, "cell 8 0,alignx left,aligny center");
        }
        {
            separator_5 = new JSeparator();
            separator_5.setOrientation(SwingConstants.VERTICAL);
            panel.add(separator_5, "cell 9 0,alignx center,growy");
        }
        {
            lblRotAxis = new JLabel("Rot axis:");
            panel.add(lblRotAxis, "cell 10 0,alignx center,aligny center");
        }
        {
            chckbxH = new JCheckBox("H");
            buttonGroup_2.add(chckbxH);
            panel.add(chckbxH, "cell 11 0,alignx center,aligny center");
        }
        {
            chckbxV = new JCheckBox("V");
            buttonGroup_2.add(chckbxV);
            panel.add(chckbxV, "cell 12 0,alignx center,aligny center");
        }
        {
        	panel_runcontrols = new JPanel();
        	panel_top.add(panel_runcontrols, "cell 1 3,grow");
        	{
        		btnRun = new JButton("Run!");
        		btnRun.addActionListener(new ActionListener() {
        			public void actionPerformed(ActionEvent arg0) {
        				do_btnRun_actionPerformed(arg0);
        			}
        		});
        		panel_runcontrols.setLayout(new MigLayout("", "[][][][grow]", "[]"));
        		panel_runcontrols.add(btnRun, "cell 0 0,growx,aligny center");
        	}
        	{
        	    this.btnStop = new JButton("Stop!");
        	    panel_runcontrols.add(btnStop, "cell 1 0,alignx center,aligny center");
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
        		panel_runcontrols.add(chckbxSaveBkg, "cell 2 0,alignx center,aligny center");
        	}
        	{
        	    this.progressBar = new JProgressBar();
        	    panel_runcontrols.add(progressBar, "cell 3 0,grow");
        	    this.progressBar.setFont(new Font("Tahoma", Font.BOLD, 15));
        	}
        }
        
        {
            JPanel buttonPane = new JPanel();
            d2dsubBatchDialog.getContentPane().add(buttonPane, BorderLayout.SOUTH);
            buttonPane.setLayout(new MigLayout("", "[][][grow]", "[]"));
            {
                {
                    this.checkBox = new JCheckBox("on top");
                    buttonPane.add(this.checkBox, "cell 0 0,alignx left,aligny center");
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
            buttonPane.add(this.label, "cell 1 0,alignx left,aligny center");
            this.label.setFont(new Font("Tahoma", Font.BOLD, 14));
            JButton okButton = new JButton("close");
            buttonPane.add(okButton, "cell 2 0,alignx right,aligny center");
            okButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent arg0) {
                    do_okButton_actionPerformed(arg0);
                }
            });
            okButton.setActionCommand("OK");
            d2dsubBatchDialog.getRootPane().setDefaultButton(okButton);
        }
        
        this.userInit();
        this.activeOptions();
    }

    private void userInit(){
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        int width = d2dsubBatchDialog.getWidth();
        int height = d2dsubBatchDialog.getHeight();
        int x = (screen.width - width) / 2;
        int y = (screen.height - height) / 2;
        d2dsubBatchDialog.setBounds(x, y, width, height);
        d2dsubBatchDialog.pack();        
        //mostrem titol i versio D2Dsub
        log.info("** Background estimation/subtraction & LP correction **");
    }
	
    protected void do_btnSelectFile_actionPerformed(ActionEvent arg0) {

    	FileNameExtensionFilter[] filter = {new FileNameExtensionFilter("2D Data file (bin,img,spr,gfrm,edf)", "bin", "img",
                "spr", "gfrm", "edf")};
        glassD2File = FileUtils.fchooserOpen(d2dsubBatchDialog,new File(MainFrame.getWorkdir()), filter, 0);
        if (glassD2File == null) return;
        log.info("Glass file selected: " + glassD2File.getPath());
        lblGlassF.setText(glassD2File.getName());
    }
    

    private void stopProcess() {
    	if(runBkg!=null){
    		try{
    			runBkg.interrupt();	
    		}catch(Exception e){
    		    if (D2Dplot_global.isDebug())e.printStackTrace();
    		    log.warning("Error in runBkg");
    		}
    	}
        runBkg = null;

    }
    
    protected void do_btnStop_actionPerformed(ActionEvent arg0) {
    	//preguntar per si de cas
        int n = JOptionPane.showConfirmDialog(d2dsubBatchDialog, "Abort current operation?", "Stop",
                JOptionPane.YES_NO_OPTION);
        if (n == JOptionPane.NO_OPTION) {
        	return;
        }
        this.stopProcess();
    }

    protected void do_chckbxFactor_itemStateChanged(ItemEvent arg0) {
        txtFactor.setEnabled(chckbxFactor.isSelected());
    }

    protected void do_checkBox_itemStateChanged(ItemEvent arg0) {
    	d2dsubBatchDialog.setAlwaysOnTop(checkBox.isSelected());
    }

    protected void do_label_mouseEntered(MouseEvent arg0) {
        // lbllist.setText("<html><font color='red'>?</font></html>");
        label.setForeground(Color.red);
    }

    protected void do_label_mouseExited(MouseEvent e) {
        label.setForeground(Color.black);
    }

    protected void do_label_mouseReleased(MouseEvent e) {

    	String d2dsubHelp="<html><div style=\"text-align:justify\"> 1) To subtract glass contribution, select a glass file and check the glass option. The scale factor will be automatically calculated. After the first run you can manually adjust the scale factor (Iglass*factor).<br><br>2) There are 5 methods to estimate the background:<ul><li> avsq: Each iteration estimates the background by averaging square areas around each pixel from the previous iteration. Set the number of pixels for the side of the square (Npix) and the number of iterations (Niter). It is a slow process for high Npix and Niter values.</li><li> avarc: The same as previous option but using arc shaped areas (within 2T) around each pixel. Set the number of iterations (Niter) and the factors for the width (wdt) and angular aperture (ang) for the arcs. This is a VERY slow method.</li><li> avcirc: The background estimation for each pixel is the mean intensity from a radial integration (in the 2T circle containing each pixel). Set the stepsize for the 2T ranges (step).</li><li> minsq: The background intensity value for each pixel (v0) is calculated as: Minimum(v0,v1,v2,v3) where v1,v2 and v3 are related pixels applying a reflection of the image (vertical, horizontal and both). Set which operations to use (v,h,vh), and the number of pixels (Npix) defining the square zone to be averaged after the operation (use 0 to consider only 1 pixel). It is a FAST method but some peak intensity may be subtracted.</li><li> minarc: The same as MINSQ but using an arc shaped zone for each pixel. Set the operations (v,h,vh) and the factors for width and angular aperture (wdt,ang).</li></ul> Visual inspection for residual peak intensity in the subtracted background can be done by clicking the [view BKG] button.<br><br>3) LP correction with the options selected is applied if the LP option is checked.<br><br>IMPORTANT: <ul><li> Result images can be seen on the main window and source image can be reloaded if wanted. It is recommended to save the result to a image file before applying more corrections to the result file. </li><li> Define any excluded zones BEFORE subtracting the background [pixels with intensity=-1 (BIN files) or .EXZ file with the same filename] as they will lead to incorrect background subtraction.</li></ul></div> </html>";
        Help hd = new Help(this.getMf().getMainF(),"d2Dsub Help",d2dsubHelp);
        hd.getHelpDialog().setSize(700,700);
        hd.getHelpDialog().setLocationRelativeTo(d2dsubBatchDialog);
        hd.getHelpDialog().setVisible(true);
    }

    protected void do_okButton_actionPerformed(ActionEvent arg0) {
    	this.dispose();
    }
	
    public void dispose() {
    	this.stopProcess();
    	d2dsubBatchDialog.dispose();
    }
    
    public void setVisible(boolean vis) {
    	d2dsubBatchDialog.setVisible(vis);
    }
    
//    //es una manera de posar al log tot el que surt pel txtArea local
//	public void printTaOut(String msg) {
//        this.tAOut.stat(msg);
//		log.debug(msg);
//	}
	
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
        int iosc=2;
        
        //vidre
		if(chk_doGlass.isSelected()){
			doGlass=true;
			glassFactor = -1.f;
            if (chckbxFactor.isSelected()) {
            	try{
            		glassFactor = Float.parseFloat(txtFactor.getText());	
            	}catch(Exception e){
            	    if (D2Dplot_global.isDebug())e.printStackTrace();
            	    log.warning("Error parsing glass factor");
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
			    if (D2Dplot_global.isDebug())e.printStackTrace();
			    log.warning("Enter valid values for background subtraction options");
				return;
			}
		}
		
		if(chk_doLP.isSelected()){
			doLP=true;
            ilor=chckbxLorOscil.isSelected()?1:0;
            ilor=chckbxLorPow.isSelected()?2:ilor;
            ipol=chckbxPolSyn.isSelected()?1:0;
            ipol=chckbxPolLab.isSelected()?2:ipol;
            iosc=chckbxH.isSelected()?1:2;
		}
		
		if(!doGlass&&!doBkg&&!doLP){
			log.warning("No operation selected");
			return;
		}
		
		//fem una cua de bkgsubtraction (operacions) que s'executara en un altre thread apart aix� no bloqueja el GUI
		this.cua=new ArrayBlockingQueue<bkgsubtraction>(50);
		for(int i=0; i<listfiles.getModel().getSize();i++){
			File f = (File)listfiles.getModel().getElementAt(i);
			Pattern2D data = ImgFileUtils.readPatternFile(f,false);
			
			bkgsubtraction opts = new bkgsubtraction(data,doGlass,doBkg,doLP,opt);
			opts.readOptions(bkgIter, bkgN, fhor, fver, fhorver, aresta, ilor, ipol, iosc, glassFactor, amplada, angle, stepsize);
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
	private JSeparator separator_5;
	private JLabel lblRotAxis;
	private JCheckBox chckbxH;
	private JCheckBox chckbxV;
	
	private class cuaProcessor implements Runnable {

		@Override
		public void run() {
			interrupted = false;
			log.info("Batch process started...");
			progressBar.setIndeterminate(true);
			progressBar.setString("Batch process started...");
			progressBar.setStringPainted(true);
			
//			Iterator<bkgsubtraction> it = cua.iterator();
			int nfile = 1; //file being processed
			int nfiles = cua.size();
			try{
				while(cua.size()>0){
					bkgsubtraction bs = cua.poll();
					log.info(FileUtils.getCharLine('*',80));
					String fpath = bs.getDataWork().getImgfile().toString();
					log.info("Processing file ("+nfile+" of "+nfiles+"): "+fpath);
					runBkg = new Thread(bs);
					runBkg.run();
					
					//save file
					Pattern2D result = bs.getDataWork();
//					String pathOut = FileUtils.getFNameNoExt(fpath).concat("_BkgSub.bin");
//					ImgFileUtils.writeBIN(new File(pathOut), result);
					String pathOut = FileUtils.getFNameNoExt(fpath).concat("_BkgSub.d2d");
                    ImgFileUtils.writePatternFile(new File(pathOut), result, false); //ja l'hem posat aqui dalt...
//					try{
//						runBkg.interrupt();
//					}catch(Exception e){
//						e.printStackTrace();
//					}
                    log.info("File: "+ fpath +" --> finished");
                    log.info("Result saved to: "+ pathOut);
					
					//save fons
					if(chckbxSaveBkg.isSelected()){
						Pattern2D fons = bs.getDataFons();
//						pathOut = FileUtils.getFNameNoExt(fpath).concat("_Bkg.bin");
//						ImgFileUtils.writeBIN(new File(pathOut), fons);
						pathOut = FileUtils.getFNameNoExt(fpath).concat("_Bkg.d2d");
                        ImgFileUtils.writePatternFile(new File(pathOut), fons, false);
                        log.info("Background saved to: "+ pathOut);	
					}
					
					if(interrupted){
						log.warning("*** Run STOPPED ***");
		                progressBar.setIndeterminate(false);
		                progressBar.setStringPainted(false);
		                return;
					}
					nfile = nfile+1;
				}
				
				log.info(FileUtils.getCharLine('*',80));
		        // un cop aqui s'hauria d'haver acabat l'execucio
				log.info("Batch process finished");
		        progressBar.setIndeterminate(false);
		        progressBar.setStringPainted(false);
            }catch(Exception e){
                if (D2Dplot_global.isDebug())e.printStackTrace();
                log.warning("*** Run ERROR ***");
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
        int bkgIter,bkgN,fhor,fver,fhorver,aresta,ilor,ipol,iosc;
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
        		 int iosc, float glassFactor, float amplada, float angle, float stepsize){
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
            this.iosc=iosc;
        }
    	
		@Override
		public void run(){
			ImgOps.setBkgIter(0);
			try {

				// 1r sostraiem el vidre si s'ha seleccionat
				if (doGlass) {
				    // comprovem si hi ha seleccionat fitxer vidre
				    if (glassD2File == null || !glassD2File.exists()) {
				    	log.warning("Select a valid glass image first");
				        throw new Exception();
				    }
				    
				    log.info("Glass subtraction... ");
				    
				    //preparacio dades
				    Pattern2D glass = ImgFileUtils.readPatternFile(glassD2File,false);
				    glass.copyExZonesFromImage(dataWork); //TODO comprovar que funcioni he canviat el metode (era copyMaskPixelsFromImage)
				    
				    //escalat del vidre
				    glass = ImgOps.correctGlass(glass);
                    if (glass==null) {
                    	log.warning("Error during glass correction, please check instrumental parameters");
                        return;
                    }
				    if (this.glassFactor < 0){
				    	//calculem factor d'escala mes petit
				    	this.glassFactor = ImgOps.calcGlassScale(dataWork, glass);
				    }
				    
				    //treiem el fons
				    //les dades amb vidre sostret seran les d'entrada a posteriors operacions
				    dataWork = ImgOps.subtractBKG_v2(dataWork, glass, this.glassFactor)[0];
				    
				    log.info("Glass subtraction... DONE!");
				}
				
				if (doBkg){
					log.info("Background subtraction... ");
					//metode
					if(this.bkgOpt.equalsIgnoreCase("avsq")){
						//necessitem 2 imatges (la iteracio anterior i la nova)
				        Pattern2D it[] = new Pattern2D[2];
				        it[0] = ImgOps.firstBkgPass(dataWork);
				        for(int i=0;i<this.bkgIter;i++){
				        	it[1] = ImgOps.calcIterAvsq(it[0], this.bkgN, progressBar);
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
				        for(int i=0;i<this.bkgIter;i++){
				        	it[1] = ImgOps.calcIterAvarc(it[0], this.amplada, this.angle, progressBar);
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
				        it[1] = ImgOps.calcIterAvcirc(it[0], this.stepsize, progressBar);
				        datafons = it[1];
				        //treiem el fons
				        dataWork = ImgOps.subtractBKG(dataWork, it[1]);
				    }

				    //calcul del fons metode min (antic "flip")
				    if (this.bkgOpt.equalsIgnoreCase("minsq")) {
				    	Pattern2D fons = ImgOps.bkgMin(dataWork, this.fhor, this.fver, this.fhorver, this.aresta, this.angle, this.amplada, false, progressBar);
				    	datafons = fons;
				        //treiem el fons
				        dataWork = ImgOps.subtractBKG(dataWork, fons);
				    }
				    
				    if (this.bkgOpt.equalsIgnoreCase("minarc")) {
				    	Pattern2D fons = ImgOps.bkgMin(dataWork, this.fhor, this.fver, this.fhorver, this.aresta, this.angle, this.amplada, true, progressBar);
				    	datafons = fons;
				        //treiem el fons
				        dataWork = ImgOps.subtractBKG(dataWork, fons);
				    }
				    log.info("Background subtraction... DONE!");
				}
				
				if(doLP){
					log.info("LP correction... ");
				    dataWork = ImgOps.corrLP(dataWork, this.ipol, this.ilor, -1, false);//EIX PER DEFECTE -1 VERTICAL
				    log.info("LP correction... DONE!");
				}
				
			} catch (Exception e) {
			    if (D2Dplot_global.isDebug())e.printStackTrace();
				interrupted = true;
				//per si s'ha aturat
                return;
			}
            
		}
    }
    

    
    private class NoneSelectedButtonGroup extends ButtonGroup {

        /**
         * 
         */
        private static final long serialVersionUID = -6414612082140761684L;

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
		    if (D2Dplot_global.isDebug())e.printStackTrace();
		    log.warning("Error clearing list");
		}
		
	}

	public MainFrame getMf() {
		return mf;
	}

	public void setMf(MainFrame mf) {
		this.mf = mf;
	}
}


