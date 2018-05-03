package com.vava33.d2dplot;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Toolkit;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import com.vava33.d2dplot.auxi.ImgFileUtils;
import com.vava33.d2dplot.auxi.ImgOps;
import com.vava33.d2dplot.auxi.Pattern2D;
import com.vava33.jutils.FileUtils;
import com.vava33.jutils.VavaLogger;

import net.miginfocom.swing.MigLayout;

import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.ProgressMonitor;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import javax.swing.JCheckBox;

public class SC_to_INCO_dialog extends JDialog {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private static VavaLogger log = D2Dplot_global.getVavaLogger(SC_to_INCO_dialog.class.getName());
    private final JPanel contentPanel = new JPanel();
    private JTextField txtIniangle;
    private JTextField txtStep;
    private JTextField txtFinalangle;
    private JTextField txtOutiniangle;
    private JTextField txtOutfinalangle;
    private JTextField txtOutomegaacq;
    private JTextField txtOutomegainc;
    private JTextField txtOutfname;

    private File[] infiles; 
    private JList<File> list;
    
    ProgressMonitor pm;
    ImgOps.sumImagesIncoFileWorker sumwk;
    private JCheckBox chckbxBackgroundSubtraction;
    private JTextField txtBkgFile;
    
    private MainFrame mf;
    private JTextField txtBkgScale;
    private JCheckBox chckbxBkgScaleAuto;
    
    /**
     * Create the dialog.
     */
    public SC_to_INCO_dialog(MainFrame mf) {
        this.mf=mf;
        setTitle("SC to INCO");
        setBounds(100, 100, 740, 512);
        setIconImage(Toolkit.getDefaultToolkit().getImage(SC_to_INCO_dialog.class.getResource("/img/Icona.png")));
        getContentPane().setLayout(new BorderLayout());
        contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        getContentPane().add(contentPanel, BorderLayout.CENTER);
        contentPanel.setLayout(new MigLayout("", "[grow][]", "[grow]"));
        {
            JPanel panel = new JPanel();
            panel.setBorder(new TitledBorder(null, "Input data", TitledBorder.LEADING, TitledBorder.TOP, null, null));
            contentPanel.add(panel, "cell 0 0,grow");
            panel.setLayout(new MigLayout("", "[grow][grow]", "[][grow][][][]"));
            {
                JButton btnLoadFiles = new JButton("Load Files");
                btnLoadFiles.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        do_btnLoadFiles_actionPerformed(e);
                    }
                });
                panel.add(btnLoadFiles, "cell 0 0");
            }
            {
                JScrollPane scrollPane = new JScrollPane();
                panel.add(scrollPane, "cell 0 1 2 1,grow");
                {
                    list = new JList<File>();
                    scrollPane.setViewportView(list);
                }
            }
            {
                JLabel lblInitialAngle = new JLabel("Initial phi (º)");
                panel.add(lblInitialAngle, "cell 0 2,alignx trailing");
            }
            {
                txtIniangle = new JTextField();
                txtIniangle.setText("-30");
                panel.add(txtIniangle, "cell 1 2,growx");
                txtIniangle.setColumns(10);
            }
            {
                JLabel lblStep = new JLabel("Phi step (º)");
                panel.add(lblStep, "cell 0 3,alignx trailing");
            }
            {
                txtStep = new JTextField();
                txtStep.setText("0.25");
                panel.add(txtStep, "cell 1 3,growx");
                txtStep.setColumns(10);
            }
            {
                JLabel lblFinalAngle = new JLabel("Final Phi (º)");
                panel.add(lblFinalAngle, "cell 0 4,alignx trailing");
            }
            {
                txtFinalangle = new JTextField();
                txtFinalangle.setText("30");
                panel.add(txtFinalangle, "cell 1 4,growx");
                txtFinalangle.setColumns(10);
            }
        }
        {
            JPanel panel = new JPanel();
            panel.setBorder(new TitledBorder(null, "Output data", TitledBorder.LEADING, TitledBorder.TOP, null, null));
            contentPanel.add(panel, "cell 1 0,grow");
            panel.setLayout(new MigLayout("", "[][]", "[][][][][][grow][]"));
            {
                JLabel lblInitialAngle_1 = new JLabel("Initial Phi (º)");
                panel.add(lblInitialAngle_1, "cell 0 0,alignx trailing");
            }
            {
                txtOutiniangle = new JTextField();
                txtOutiniangle.setText("-22.5");
                panel.add(txtOutiniangle, "cell 1 0,growx");
                txtOutiniangle.setColumns(10);
            }
            {
                JLabel lblFinalAngle_1 = new JLabel("Final Phi (º)");
                panel.add(lblFinalAngle_1, "cell 0 1,alignx trailing");
            }
            {
                txtOutfinalangle = new JTextField();
                txtOutfinalangle.setText("22.5");
                panel.add(txtOutfinalangle, "cell 1 1,growx");
                txtOutfinalangle.setColumns(10);
            }
            {
                JLabel lblOmegaStep = new JLabel("Phi Acquisition Step (º)");
                lblOmegaStep.setToolTipText("Scan of the Image");
                panel.add(lblOmegaStep, "cell 0 2,alignx trailing");
            }
            {
                txtOutomegaacq = new JTextField();
                txtOutomegaacq.setText("15");
                panel.add(txtOutomegaacq, "cell 1 2,growx");
                txtOutomegaacq.setColumns(10);
            }
            {
                JLabel lblOmegaIncrement = new JLabel("Phi Increment (º)");
                lblOmegaIncrement.setToolTipText("Increment between images");
                panel.add(lblOmegaIncrement, "cell 0 3,alignx trailing");
            }
            {
                txtOutomegainc = new JTextField();
                txtOutomegainc.setText("7.5");
                panel.add(txtOutomegainc, "cell 1 3,growx");
                txtOutomegainc.setColumns(10);
            }
            {
                JLabel lblBaseFilename = new JLabel("Base Filename");
                panel.add(lblBaseFilename, "cell 0 4,alignx trailing");
            }
            {
                txtOutfname = new JTextField();
                txtOutfname.setText("inco_conv");
                panel.add(txtOutfname, "cell 1 4,growx");
                txtOutfname.setColumns(10);
            }
            {
                JButton btnWriteFiles = new JButton("Write Files");
                btnWriteFiles.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        do_btnWriteFiles_actionPerformed(e);
                    }
                });
                {
                    JPanel panel_1 = new JPanel();
                    panel_1.setBorder(new TitledBorder(null, "Background subtraction", TitledBorder.LEADING, TitledBorder.TOP, null, null));
                    panel.add(panel_1, "cell 0 5 2 1,grow");
                    panel_1.setLayout(new MigLayout("", "[][][][]", "[][][]"));
                    {
                        chckbxBackgroundSubtraction = new JCheckBox("Enabled");
                        panel_1.add(chckbxBackgroundSubtraction, "cell 0 0");
                    }
                    {
                        JButton btnSelectFile = new JButton("Select File");
                        panel_1.add(btnSelectFile, "cell 0 1");
                        btnSelectFile.addActionListener(new ActionListener() {
                            public void actionPerformed(ActionEvent e) {
                                do_btnSelectFile_actionPerformed(e);
                            }
                        });
                        {
                            JLabel lblScaleFactor = new JLabel("Scale Factor");
                            panel_1.add(lblScaleFactor, "cell 1 1,alignx trailing");
                        }
                        {
                            txtBkgScale = new JTextField();
                            txtBkgScale.setText("1.0");
                            panel_1.add(txtBkgScale, "cell 2 1,growx");
                            txtBkgScale.setColumns(5);
                        }
                    }
                    {
                        chckbxBkgScaleAuto = new JCheckBox("auto");
                        chckbxBkgScaleAuto.setSelected(true);
                        panel_1.add(chckbxBkgScaleAuto, "cell 3 1");
                    }
                    {
                        txtBkgFile = new JTextField();
                        panel_1.add(txtBkgFile, "cell 0 2 4 1,growx");
                        txtBkgFile.setText("(no bkg file)");
                        txtBkgFile.setEditable(false);
                        txtBkgFile.setColumns(10);
                    }
                }
                panel.add(btnWriteFiles, "cell 0 6 2 1,alignx center");
            }
        }
        {
            JPanel buttonPane = new JPanel();
            buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
            getContentPane().add(buttonPane, BorderLayout.SOUTH);
            {
                JButton cancelButton = new JButton("Close");
                cancelButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        do_cancelButton_actionPerformed(e);
                    }
                });
                cancelButton.setActionCommand("Cancel");
                buttonPane.add(cancelButton);
            }
        }
    }

    protected void do_btnLoadFiles_actionPerformed(ActionEvent e) {
        infiles = FileUtils.fchooserMultiple(this, D2Dplot_global.getWorkdirFile(), ImgFileUtils.getExtensionFilterRead(), 0);
        if (infiles==null){
            log.info("no files selected");
            return;
        }
        if (infiles.length==0){
            log.info("no files selected");
            return;
        }
        updateList();
        //try to read first and last image to guess parameters
        log.info("trying to guess parameters");
        Pattern2D img = ImgFileUtils.readEDFheaderOnly(infiles[0]);
        txtIniangle.setText(String.format("%.3f", img.getOmeIni()));
        txtOutiniangle.setText(String.format("%.3f", img.getOmeIni()));
        txtStep.setText(String.format("%.3f", img.getOmeFin()-img.getOmeIni()));
        img = ImgFileUtils.readEDFheaderOnly(infiles[infiles.length-1]);
        txtFinalangle.setText(String.format("%.3f", img.getOmeFin()));
        txtOutfinalangle.setText(String.format("%.3f", img.getOmeFin()));
        
    }
    
    private void updateList(){
        
        DefaultListModel<File> lm = new DefaultListModel<File>();
        lm.clear();
        for (int i=0;i<infiles.length;i++){
            lm.addElement(infiles[i]);
        }
        list.setModel(lm);
    }

    
    
    protected void do_btnWriteFiles_actionPerformed(ActionEvent e) {
        
        File dir = FileUtils.fchooserOpenDir(this, D2Dplot_global.getWorkdirFile(), "folder to save INCO-ready image files");
        log.info("outdir="+dir.getAbsolutePath());
        String baseFname = txtOutfname.getText().trim();
        
        //TODO preguntar format??
        
        //ara cal guardar 1 2 3 4 5...
        
        float scAini,scStep,outIni,outFin,outomegaacq,outomegaInc;
        
        try{
            scAini = Float.parseFloat(txtIniangle.getText());
            scStep = Float.parseFloat(txtStep.getText());
            outIni = Float.parseFloat(txtOutiniangle.getText());
            outFin = Float.parseFloat(txtOutfinalangle.getText());
            outomegaacq = Float.parseFloat(txtOutomegaacq.getText());
            outomegaInc = Float.parseFloat(txtOutomegainc.getText());
        }catch(Exception ex) {
            log.info("error reading parameters, please check");
            return;
        }
        if (outFin<outIni) {
            FileUtils.InfoDialog(this, "Please, final angle must be bigger than initial angle", "");
            return;
        }
        if (outomegaacq<=0) {
            FileUtils.InfoDialog(this, "Please, acquisition angle must be positive", "");
            return;
        }
        if (outomegaInc<=0) {
            FileUtils.InfoDialog(this, "Please, increment angle must be positive", "");
            return;
        }

        File bkgfile = new File(txtBkgFile.getText());
        float bkgscale = 1.0f;
        try {
            bkgscale = Float.parseFloat(txtBkgScale.getText());    
        }catch(Exception ex) {
            log.debug("error reading background scale, set to 1.0");
        }
        if (chckbxBkgScaleAuto.isSelected()) {
            bkgscale = -1.0f; //aixo vol dir auto calcular
        }
        
        if (!bkgfile.exists()) {
            bkgfile = null;
        }
        
        sumwk = new ImgOps.sumImagesIncoFileWorker(infiles, bkgfile, bkgscale, scAini, scStep, outIni, outFin, outomegaacq, outomegaInc, chckbxBackgroundSubtraction.isSelected(), baseFname, dir, mf.gettAOut());
        
        pm = new ProgressMonitor(null,
                "Summing Images...",
                "", 0, 100);
        pm.setProgress(0);
        sumwk.addPropertyChangeListener(new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                log.fine(evt.getPropertyName());
                if ("progress" == evt.getPropertyName() ) {
                    int progress = (Integer) evt.getNewValue();
                    pm.setProgress(progress);
                    pm.setNote(String.format("%d%%\n", progress));
                    log.fine("hi from inside if progress");
                    if (pm.isCanceled() || sumwk.isDone()) {
                        log.fine("hi from inside if cancel/done");
                        Toolkit.getDefaultToolkit().beep();
                        if (pm.isCanceled()) {
                            sumwk.cancel(true);
                            log.info("sumwk canceled");
                        } else {
                            log.info("sumwk finished!!");
                        }
                        pm.close();
                    }
                }
                if (sumwk.isDone()){
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    log.info("finished summing");
                }
            }
        });
        sumwk.execute();
       
    }
    
    protected void do_cancelButton_actionPerformed(ActionEvent e) {
        this.dispose();
    }

    protected void do_btnSelectFile_actionPerformed(ActionEvent e) {
        FileNameExtensionFilter filt[] = ImgFileUtils.getExtensionFilterRead();
        File f = FileUtils.fchooserOpen(this, D2Dplot_global.getWorkdirFile(), filt, 0);
        if (f!=null) {
            txtBkgFile.setText(f.getAbsolutePath());
        }
    }
}
