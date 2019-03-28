package com.vava33.d2dplot;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ProgressMonitor;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.vava33.d2dplot.auxi.ImgFileUtils;
import com.vava33.d2dplot.auxi.ImgFileUtils.EdfHeaderPatt2D;
import com.vava33.d2dplot.auxi.ImgOps;
import com.vava33.jutils.FileUtils;
import com.vava33.jutils.VavaLogger;

import net.miginfocom.swing.MigLayout;

public class SC_to_INCO {
    private static final String className = "SCtoINCO";
    private static VavaLogger log = D2Dplot_global.getVavaLogger(className);
    private final JDialog scTOincoDialog;
    private final JPanel contentPanel;
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
    ImgOps.SumImagesIncoFileWorker sumwk;
    private JCheckBox chckbxBackgroundSubtraction;
    private JTextField txtBkgFile;

    private JTextField txtBkgScale;
    private JCheckBox chckbxBkgScaleAuto;

    /**
     * Create the dialog.
     */
    public SC_to_INCO(JFrame parent) {
        this.contentPanel = new JPanel();
        this.scTOincoDialog = new JDialog(parent, "SC to INCO", false);
        this.scTOincoDialog.setBounds(100, 100, 740, 512);
        this.scTOincoDialog
                .setIconImage(Toolkit.getDefaultToolkit().getImage(SC_to_INCO.class.getResource("/img/Icona.png")));
        this.scTOincoDialog.getContentPane().setLayout(new BorderLayout());
        this.contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        this.scTOincoDialog.getContentPane().add(this.contentPanel, BorderLayout.CENTER);
        this.contentPanel.setLayout(new MigLayout("", "[grow][]", "[grow]"));
        {
            final JPanel panel = new JPanel();
            panel.setBorder(new TitledBorder(null, "Input data", TitledBorder.LEADING, TitledBorder.TOP, null, null));
            this.contentPanel.add(panel, "cell 0 0,grow");
            panel.setLayout(new MigLayout("", "[grow][grow]", "[][grow][][][]"));
            {
                final JButton btnLoadFiles = new JButton("Load Files");
                btnLoadFiles.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        SC_to_INCO.this.do_btnLoadFiles_actionPerformed(e);
                    }
                });
                panel.add(btnLoadFiles, "cell 0 0");
            }
            {
                final JScrollPane scrollPane = new JScrollPane();
                panel.add(scrollPane, "cell 0 1 2 1,grow");
                {
                    this.list = new JList<File>();
                    scrollPane.setViewportView(this.list);
                }
            }
            {
                final JLabel lblInitialAngle = new JLabel("Initial phi (º)");
                panel.add(lblInitialAngle, "cell 0 2,alignx trailing");
            }
            {
                this.txtIniangle = new JTextField();
                this.txtIniangle.setText("-30");
                panel.add(this.txtIniangle, "cell 1 2,growx");
                this.txtIniangle.setColumns(10);
            }
            {
                final JLabel lblStep = new JLabel("Phi step (º)");
                panel.add(lblStep, "cell 0 3,alignx trailing");
            }
            {
                this.txtStep = new JTextField();
                this.txtStep.setText("0.25");
                panel.add(this.txtStep, "cell 1 3,growx");
                this.txtStep.setColumns(10);
            }
            {
                final JLabel lblFinalAngle = new JLabel("Final Phi (º)");
                panel.add(lblFinalAngle, "cell 0 4,alignx trailing");
            }
            {
                this.txtFinalangle = new JTextField();
                this.txtFinalangle.setText("30");
                panel.add(this.txtFinalangle, "cell 1 4,growx");
                this.txtFinalangle.setColumns(10);
            }
        }
        {
            final JPanel panel = new JPanel();
            panel.setBorder(new TitledBorder(null, "Output data", TitledBorder.LEADING, TitledBorder.TOP, null, null));
            this.contentPanel.add(panel, "cell 1 0,grow");
            panel.setLayout(new MigLayout("", "[][]", "[][][][][][grow][]"));
            {
                final JLabel lblInitialAngle_1 = new JLabel("Initial Phi (º)");
                panel.add(lblInitialAngle_1, "cell 0 0,alignx trailing");
            }
            {
                this.txtOutiniangle = new JTextField();
                this.txtOutiniangle.setText("-22.5");
                panel.add(this.txtOutiniangle, "cell 1 0,growx");
                this.txtOutiniangle.setColumns(10);
            }
            {
                final JLabel lblFinalAngle_1 = new JLabel("Final Phi (º)");
                panel.add(lblFinalAngle_1, "cell 0 1,alignx trailing");
            }
            {
                this.txtOutfinalangle = new JTextField();
                this.txtOutfinalangle.setText("22.5");
                panel.add(this.txtOutfinalangle, "cell 1 1,growx");
                this.txtOutfinalangle.setColumns(10);
            }
            {
                final JLabel lblOmegaStep = new JLabel("Phi Acquisition Step (º)");
                lblOmegaStep.setToolTipText("Scan of the Image");
                panel.add(lblOmegaStep, "cell 0 2,alignx trailing");
            }
            {
                this.txtOutomegaacq = new JTextField();
                this.txtOutomegaacq.setText("15");
                panel.add(this.txtOutomegaacq, "cell 1 2,growx");
                this.txtOutomegaacq.setColumns(10);
            }
            {
                final JLabel lblOmegaIncrement = new JLabel("Phi Increment (º)");
                lblOmegaIncrement.setToolTipText("Increment between images");
                panel.add(lblOmegaIncrement, "cell 0 3,alignx trailing");
            }
            {
                this.txtOutomegainc = new JTextField();
                this.txtOutomegainc.setText("7.5");
                panel.add(this.txtOutomegainc, "cell 1 3,growx");
                this.txtOutomegainc.setColumns(10);
            }
            {
                final JLabel lblBaseFilename = new JLabel("Base Filename");
                panel.add(lblBaseFilename, "cell 0 4,alignx trailing");
            }
            {
                this.txtOutfname = new JTextField();
                this.txtOutfname.setText("inco_conv");
                panel.add(this.txtOutfname, "cell 1 4,growx");
                this.txtOutfname.setColumns(10);
            }
            {
                final JButton btnWriteFiles = new JButton("Write Files");
                btnWriteFiles.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        SC_to_INCO.this.do_btnWriteFiles_actionPerformed(e);
                    }
                });
                {
                    final JPanel panel_1 = new JPanel();
                    panel_1.setBorder(new TitledBorder(null, "Background subtraction", TitledBorder.LEADING,
                            TitledBorder.TOP, null, null));
                    panel.add(panel_1, "cell 0 5 2 1,grow");
                    panel_1.setLayout(new MigLayout("", "[][][][]", "[][][]"));
                    {
                        this.chckbxBackgroundSubtraction = new JCheckBox("Enabled");
                        panel_1.add(this.chckbxBackgroundSubtraction, "cell 0 0");
                    }
                    {
                        final JButton btnSelectFile = new JButton("Select File");
                        panel_1.add(btnSelectFile, "cell 0 1");
                        btnSelectFile.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                SC_to_INCO.this.do_btnSelectFile_actionPerformed(e);
                            }
                        });
                        {
                            final JLabel lblScaleFactor = new JLabel("Scale Factor");
                            panel_1.add(lblScaleFactor, "cell 1 1,alignx trailing");
                        }
                        {
                            this.txtBkgScale = new JTextField();
                            this.txtBkgScale.setText("1.0");
                            panel_1.add(this.txtBkgScale, "cell 2 1,growx");
                            this.txtBkgScale.setColumns(5);
                        }
                    }
                    {
                        this.chckbxBkgScaleAuto = new JCheckBox("auto");
                        this.chckbxBkgScaleAuto.setSelected(true);
                        panel_1.add(this.chckbxBkgScaleAuto, "cell 3 1");
                    }
                    {
                        this.txtBkgFile = new JTextField();
                        panel_1.add(this.txtBkgFile, "cell 0 2 4 1,growx");
                        this.txtBkgFile.setText("(no bkg file)");
                        this.txtBkgFile.setEditable(false);
                        this.txtBkgFile.setColumns(10);
                    }
                }
                panel.add(btnWriteFiles, "cell 0 6 2 1,alignx center");
            }
        }
        {
            final JPanel buttonPane = new JPanel();
            buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
            this.scTOincoDialog.getContentPane().add(buttonPane, BorderLayout.SOUTH);
            {
                final JButton cancelButton = new JButton("Close");
                cancelButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        SC_to_INCO.this.do_cancelButton_actionPerformed(e);
                    }
                });
                cancelButton.setActionCommand("Cancel");
                buttonPane.add(cancelButton);
            }
        }
    }

    protected void do_btnLoadFiles_actionPerformed(ActionEvent e) {
        this.infiles = FileUtils.fchooserMultiple(this.scTOincoDialog, D2Dplot_global.getWorkdirFile(),
                ImgFileUtils.getExtensionFilterRead(), 0, "Select 2DXRD data files to load");
        if (this.infiles == null) {
            log.info("no files selected");
            return;
        }
        if (this.infiles.length == 0) {
            log.info("no files selected");
            return;
        }
        this.updateList();
        //try to read first and last image to guess parameters TODO: he canviat a EdfHeaderPatt2D
        log.info("Trying to guess parameters..-");
        EdfHeaderPatt2D img = ImgFileUtils.readEDFheaderOnly(this.infiles[0]);
        this.txtIniangle.setText(String.format("%.3f", img.getPatt2D().getOmeIni()));
        this.txtOutiniangle.setText(String.format("%.3f", img.getPatt2D().getOmeIni()));
        this.txtStep.setText(String.format("%.3f", img.getPatt2D().getOmeFin() - img.getPatt2D().getOmeIni()));
        img = ImgFileUtils.readEDFheaderOnly(this.infiles[this.infiles.length - 1]);
        this.txtFinalangle.setText(String.format("%.3f", img.getPatt2D().getOmeFin()));
        this.txtOutfinalangle.setText(String.format("%.3f", img.getPatt2D().getOmeFin()));

    }

    private void updateList() {

        final DefaultListModel<File> lm = new DefaultListModel<File>();
        lm.clear();
        for (final File infile : this.infiles) {
            lm.addElement(infile);
        }
        this.list.setModel(lm);
    }

    protected void do_btnWriteFiles_actionPerformed(ActionEvent e) {

        final File dir = FileUtils.fchooserOpenDir(this.scTOincoDialog, D2Dplot_global.getWorkdirFile(),
                "folder to save INCO-ready image files");
        log.info("Outdir=" + dir.getAbsolutePath());
        final String baseFname = this.txtOutfname.getText().trim();

        //TODO preguntar format??

        //ara cal guardar 1 2 3 4 5...

        float scAini, scStep, outIni, outFin, outomegaacq, outomegaInc;

        try {
            scAini = Float.parseFloat(this.txtIniangle.getText());
            scStep = Float.parseFloat(this.txtStep.getText());
            outIni = Float.parseFloat(this.txtOutiniangle.getText());
            outFin = Float.parseFloat(this.txtOutfinalangle.getText());
            outomegaacq = Float.parseFloat(this.txtOutomegaacq.getText());
            outomegaInc = Float.parseFloat(this.txtOutomegainc.getText());
        } catch (final Exception ex) {
            log.warning("Error reading parameters, please check");
            return;
        }
        if (outFin < outIni) {
            log.warning("Please, final angle must be bigger than initial angle");
            return;
        }
        if (outomegaacq <= 0) {
            log.warning("Please, acquisition angle must be positive");
            return;
        }
        if (outomegaInc <= 0) {
            log.warning("Please, increment angle must be positive");
            return;
        }

        File bkgfile = new File(this.txtBkgFile.getText());
        float bkgscale = 1.0f;
        try {
            bkgscale = Float.parseFloat(this.txtBkgScale.getText());
        } catch (final Exception ex) {
            log.warning("Error reading background scale, set to 1.0");
        }
        if (this.chckbxBkgScaleAuto.isSelected()) {
            bkgscale = -1.0f; //aixo vol dir auto calcular
        }

        if (!bkgfile.exists()) {
            bkgfile = null;
        }

        this.sumwk = new ImgOps.SumImagesIncoFileWorker(this.infiles, bkgfile, bkgscale, scAini, scStep, outIni, outFin,
                outomegaacq, outomegaInc, this.chckbxBackgroundSubtraction.isSelected(), baseFname, dir);

        this.pm = new ProgressMonitor(null, "Summing Images...", "", 0, 100);
        this.pm.setProgress(0);
        this.sumwk.addPropertyChangeListener(new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                log.fine(evt.getPropertyName());
                if ("progress" == evt.getPropertyName()) {
                    final int progress = (Integer) evt.getNewValue();
                    SC_to_INCO.this.pm.setProgress(progress);
                    SC_to_INCO.this.pm.setNote(String.format("%d%%\n", progress));
                    if (SC_to_INCO.this.pm.isCanceled() || SC_to_INCO.this.sumwk.isDone()) {
                        Toolkit.getDefaultToolkit().beep();
                        if (SC_to_INCO.this.pm.isCanceled()) {
                            SC_to_INCO.this.sumwk.cancel(true);
                            log.info("sum (inco) canceled");
                        } else {
                            log.info("sum (inco) finished!!");
                        }
                        SC_to_INCO.this.pm.close();
                    }
                }
                if (SC_to_INCO.this.sumwk.isDone()) {
                    try {
                        Thread.sleep(500);
                    } catch (final InterruptedException e) {
                        e.printStackTrace();
                    }
                    log.info("Sum finished!");
                }
            }
        });
        this.sumwk.execute();

    }

    private void stopExecution() {
        if (this.sumwk != null) {
            this.sumwk.cancel(true);
        }
    }

    protected void do_cancelButton_actionPerformed(ActionEvent e) {
        this.dispose();
    }

    protected void do_btnSelectFile_actionPerformed(ActionEvent e) {
        final FileNameExtensionFilter filt[] = ImgFileUtils.getExtensionFilterRead();
        final File f = FileUtils.fchooserOpen(this.scTOincoDialog, D2Dplot_global.getWorkdirFile(), filt, 0);
        if (f != null) {
            this.txtBkgFile.setText(f.getAbsolutePath());
        }
    }

    public void setVisible(boolean vis) {
        this.scTOincoDialog.setVisible(vis);
    }

    public void dispose() {
        this.stopExecution();
        this.scTOincoDialog.dispose();
    }

}
