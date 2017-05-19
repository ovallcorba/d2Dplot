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

import org.apache.commons.math3.util.FastMath;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

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
    
    private MainFrame mf;
    
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
        contentPanel.setLayout(new MigLayout("", "[grow][grow]", "[grow]"));
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
                JLabel lblInitialAngle = new JLabel("Initial angle=");
                panel.add(lblInitialAngle, "cell 0 2,alignx trailing");
            }
            {
                txtIniangle = new JTextField();
                txtIniangle.setText("IniAngle");
                panel.add(txtIniangle, "cell 1 2,growx");
                txtIniangle.setColumns(10);
            }
            {
                JLabel lblStep = new JLabel("Step=");
                panel.add(lblStep, "cell 0 3,alignx trailing");
            }
            {
                txtStep = new JTextField();
                txtStep.setText("step");
                panel.add(txtStep, "cell 1 3,growx");
                txtStep.setColumns(10);
            }
            {
                JLabel lblFinalAngle = new JLabel("Final angle=");
                panel.add(lblFinalAngle, "cell 0 4,alignx trailing");
            }
            {
                txtFinalangle = new JTextField();
                txtFinalangle.setText("finalAngle");
                panel.add(txtFinalangle, "cell 1 4,growx");
                txtFinalangle.setColumns(10);
            }
        }
        {
            JPanel panel = new JPanel();
            panel.setBorder(new TitledBorder(null, "Output data", TitledBorder.LEADING, TitledBorder.TOP, null, null));
            contentPanel.add(panel, "cell 1 0,grow");
            panel.setLayout(new MigLayout("", "[][grow]", "[][][][][][][]"));
            {
                JLabel lblInitialAngle_1 = new JLabel("Initial Angle=");
                panel.add(lblInitialAngle_1, "cell 0 0,alignx trailing");
            }
            {
                txtOutiniangle = new JTextField();
                txtOutiniangle.setText("-22.5");
                panel.add(txtOutiniangle, "cell 1 0,growx");
                txtOutiniangle.setColumns(10);
            }
            {
                JLabel lblFinalAngle_1 = new JLabel("Final Angle=");
                panel.add(lblFinalAngle_1, "cell 0 1,alignx trailing");
            }
            {
                txtOutfinalangle = new JTextField();
                txtOutfinalangle.setText("22.5");
                panel.add(txtOutfinalangle, "cell 1 1,growx");
                txtOutfinalangle.setColumns(10);
            }
            {
                JLabel lblOmegaStep = new JLabel("Omega Aqu. Step=");
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
                JLabel lblOmegaIncrement = new JLabel("Omega Increment=");
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
                txtOutfname.setText("test_inco_conv");
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
                panel.add(btnWriteFiles, "cell 0 6");
            }
        }
        {
            JPanel buttonPane = new JPanel();
            buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
            getContentPane().add(buttonPane, BorderLayout.SOUTH);
            {
                JButton cancelButton = new JButton("Close");
                cancelButton.setActionCommand("Cancel");
                buttonPane.add(cancelButton);
            }
        }
    }

    protected void do_btnLoadFiles_actionPerformed(ActionEvent e) {
        infiles = FileUtils.fchooserMultiple(this, D2Dplot_global.getWorkdirFile(), ImgFileUtils.getExtensionFilterRead(), ImgFileUtils.getExtensionFilterRead().length-1);
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
        txtStep.setText(String.format("%.3f", img.getOmeFin()-img.getOmeIni()));
        img = ImgFileUtils.readEDFheaderOnly(infiles[infiles.length-1]);
        txtFinalangle.setText(String.format("%.3f", img.getOmeFin()));
    }
    
    private void updateList(){
        
        DefaultListModel<File> lm = new DefaultListModel<File>();
        lm.clear();
        for (int i=0;i<infiles.length;i++){
            lm.addElement(infiles[i]);
        }
        list.setModel(lm);
    }
    
    
//    ProgressMonitor pm;
//    ImgOps.sumImagesFileWorker sumwk;
    
    private void generatePattern(final float aini, final float afin, final File outfile){

        //aqui cal agafar els SC i sumarlos
        
        float inAini = Float.parseFloat(txtIniangle.getText());
        float inStep = Float.parseFloat(txtStep.getText());
        
        int n = FastMath.round((aini -inAini)/inStep);
        int nfin = FastMath.round((afin -inAini)/inStep)-1;
        log.info(String.format("images from %d to %d comprises angles %.3f to %.3f",n,nfin,aini,afin));
        
        //ara hem de treure el fons a cada imatge individualment i sumar-les
        
        Pattern2D fons = new Pattern2D(ImgFileUtils.readPatternFile(infiles[n],false),true);
        
        //estimem el fons
        for (int i=n+1; i<nfin;i++){
            Pattern2D p = ImgFileUtils.readPatternFile(infiles[i],false);
            for (int k = 0; k < p.getDimY(); k++) { // per cada fila (Y)
                for (int j = 0; j < p.getDimX(); j++) { // per cada columna (X)
                    if (p.getInten(j, k)<fons.getInten(j, k))fons.setInten(j, k, p.getInten(j, k));
                }
            }
        }
        
        File[] flist = Arrays.copyOfRange(infiles, n, nfin);
        
        //ara ja tenim el fons estimat al rang
        final ProgressMonitor pm = new ProgressMonitor(null,
                "Summing Images...",
                "", 0, 100);
        pm.setProgress(0);
        final ImgOps.sumImagesFileWorker sumwk = new ImgOps.sumImagesFileWorker(flist,mf.gettAOut());
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
                            log.debug("sumwk canceled");
                        } else {
                            log.debug("sumwk finished!!");
                        }
                        pm.close();
                    }
                }
                if (sumwk.isDone()){
                    log.fine("hi from outside if progress");
                    Pattern2D suma = sumwk.getpattSum();
                    if (suma==null){
                        mf.gettAOut().stat("Error summing files");
                        return;
                    }else{
                        log.info("finished summing");
                        mf.updatePatt2D(suma,false);
                        suma.setScanParameters(aini, afin, 0);
                        writePattern(suma,outfile);
                    }
                }
            }
        });
        sumwk.execute();
        while(!sumwk.isDone()){
            
        }
    }
    
    private void writePattern(Pattern2D p, File outf){
//        ImgFileUtils.writePatternFile(outf, p);
        ImgFileUtils.writeEDF(outf, p);
    }
    
    
    protected void do_btnWriteFiles_actionPerformed(ActionEvent e) {
        File dir = FileUtils.fchooserOpenDir(this, D2Dplot_global.getWorkdirFile(), "folder to save INCO-ready image files");
        log.info("outdir="+dir.getAbsolutePath());
        String baseFname = txtOutfname.getText().trim();
        
        //TODO preguntar format??
        
        //ara cal guardar 1 2 3 4 5...
        
        float outIni = Float.parseFloat(txtOutiniangle.getText());
        float outFin = Float.parseFloat(txtOutfinalangle.getText());
        float outomegaacq = Float.parseFloat(txtOutomegaacq.getText());
        float outomegaInc = Float.parseFloat(txtOutomegainc.getText());

        float currOme = outIni;
        
        int index = 0;
        
        while((currOme+outomegaacq)<=outFin){
            String outfname = dir.getAbsolutePath()+D2Dplot_global.separator+baseFname+"_";
            //ara el numero
            outfname = String.format("%s%04d", outfname,index);
            log.info("outfname="+outfname+".edf");
            log.info("currome="+currOme+" outomegaacq="+outomegaacq+" outomegaInc="+outomegaInc+" outFin="+outFin);
            generatePattern(currOme, currOme+outomegaacq, new File(outfname));
            currOme = currOme+outomegaInc;
            index=index+1;
        }
        
        
        
    }
}
