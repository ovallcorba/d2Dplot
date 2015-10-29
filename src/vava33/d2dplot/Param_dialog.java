package vava33.d2dplot;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;

import vava33.d2dplot.auxi.Pattern2D;
import net.miginfocom.swing.MigLayout;

public class Param_dialog extends JDialog {

    private static final long serialVersionUID = -7972051523318564847L;
    private final JPanel contentPanel = new JPanel();
    private JTextField txtCentrX;
    private JTextField txtCentrY;
    private JTextField txtDistOD;
    private JTextField txtPicSizeX;
    private JTextField txtPicSizeY;
    private Color[] col = { Color.RED, Color.BLUE, Color.GREEN, Color.YELLOW };
    private int counter = 0;
    private Pattern2D patt2D;
    private JLabel lblWavelengtha;
    private JTextField txtWave;
    private JTextField txtTilt;
    private JTextField txtRot;
    private JLabel lblDetectorTilt;
    private JLabel lblDetectorRot;
    private JButton btnCancel;
    private JButton btnApply;
    private JLabel lblCheckValues;
    private JLabel lblscanOmegaIni;
    private JLabel lblscanOmegaEnd;
    private JLabel lblAcquisitionTime;
    private JTextField txtOmeini;
    private JTextField txtOmefin;
    private JTextField txtAcqtime;

    /**
     * Create the dialog.
     */
    public Param_dialog(Pattern2D pattern) {
        setIconImage(Toolkit.getDefaultToolkit().getImage(Param_dialog.class.getResource("/img/Icona.png")));
        setModal(false);
        setAlwaysOnTop(true);
        this.patt2D = pattern;
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Instrumental Parameters");
        // setBounds(100, 100, 660, 730);
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        int width = 660;
        int height = 730;
        int x = (screen.width - width) / 2;
        int y = (screen.height - height) / 2;
        setBounds(x, y, 420, 500);
        getContentPane().setLayout(new MigLayout("", "[]", "[grow][]"));
        this.contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        getContentPane().add(this.contentPanel, "cell 0 0,grow");
        contentPanel.setLayout(new MigLayout("fill, insets 5", "[][grow]", "[grow][grow][grow][grow][grow][grow][grow][grow][][][]"));
        {
            JLabel lblSampledetectorDistancemm = new JLabel("Sample-Detector distance (mm)=");
            contentPanel.add(lblSampledetectorDistancemm, "cell 0 0,alignx trailing,aligny center");
        }
        {
            this.txtDistOD = new JTextField();
            this.txtDistOD.setText("150.000");
            contentPanel.add(this.txtDistOD, "cell 1 0,growx,aligny center");
            this.txtDistOD.setColumns(10);
        }
        {
            JLabel lblPixelSizeX = new JLabel("Pixel size X (mm)=");
            contentPanel.add(lblPixelSizeX, "cell 0 1,alignx trailing,aligny center");
        }
        {
            this.txtPicSizeX = new JTextField();
            this.txtPicSizeX.setText("0.1024");
            contentPanel.add(this.txtPicSizeX, "cell 1 1,growx,aligny center");
            this.txtPicSizeX.setColumns(10);
        }
        {
            JLabel lblPixelSizeY = new JLabel("Pixel size Y (mm)=");
            contentPanel.add(lblPixelSizeY, "cell 0 2,alignx trailing,aligny center");
        }
        {
            this.txtPicSizeY = new JTextField();
            this.txtPicSizeY.setText("0.1024");
            contentPanel.add(this.txtPicSizeY, "cell 1 2,growx,aligny center");
            this.txtPicSizeY.setColumns(10);
        }
        {
            JLabel lblBeamCentreX = new JLabel("Beam centre X (pixel)=");
            contentPanel.add(lblBeamCentreX, "cell 0 3,alignx trailing,aligny center");
        }
        {
            this.txtCentrX = new JTextField();
            this.txtCentrX.setText("1024");
            contentPanel.add(this.txtCentrX, "cell 1 3,growx,aligny center");
            this.txtCentrX.setColumns(10);
        }
        {
            JLabel lblBeamCentreY = new JLabel("Beam centre Y (pixel)=");
            contentPanel.add(lblBeamCentreY, "cell 0 4,alignx trailing,aligny center");
        }
        {
            this.txtCentrY = new JTextField();
            this.txtCentrY.setText("1024");
            contentPanel.add(this.txtCentrY, "cell 1 4,growx,aligny center");
            this.txtCentrY.setColumns(10);
        }
        {
            this.lblWavelengtha = new JLabel("Wavelength (A)=");
            contentPanel.add(this.lblWavelengtha, "cell 0 5,alignx trailing,aligny center");
        }
        {
            this.txtWave = new JTextField();
            this.txtWave.setText("0.0");
            contentPanel.add(this.txtWave, "cell 1 5,growx,aligny center");
            this.txtWave.setColumns(10);
        }
        {
            lblDetectorTilt = new JLabel("Detector Tilt (º)=");
            lblDetectorTilt.setToolTipText("");
            contentPanel.add(lblDetectorTilt, "cell 0 6,alignx trailing,aligny center");
        }
        {
            txtTilt = new JTextField();
            txtTilt.setText("0.0");
            txtTilt.setColumns(10);
            contentPanel.add(txtTilt, "cell 1 6,growx,aligny center");
        }
        {
            lblDetectorRot = new JLabel("Detector Rot (º)=");
            contentPanel.add(lblDetectorRot, "cell 0 7,alignx trailing,aligny center");
        }
        {
            txtRot = new JTextField();
            txtRot.setText("0.0");
            txtRot.setColumns(10);
            contentPanel.add(txtRot, "cell 1 7,growx,aligny center");
        }
        {
            lblscanOmegaIni = new JLabel("(scan) omega ini (º)=");
            contentPanel.add(lblscanOmegaIni, "cell 0 8,alignx trailing,aligny center");
        }
        {
            txtOmeini = new JTextField();
            txtOmeini.setText("0.0");
            contentPanel.add(txtOmeini, "cell 1 8,growx,aligny center");
            txtOmeini.setColumns(10);
        }
        {
            lblscanOmegaEnd = new JLabel("(scan) omega end (º)=");
            contentPanel.add(lblscanOmegaEnd, "cell 0 9,alignx trailing,aligny center");
        }
        {
            txtOmefin = new JTextField();
            txtOmefin.setText("0.0");
            contentPanel.add(txtOmefin, "cell 1 9,growx,aligny center");
            txtOmefin.setColumns(10);
        }
        {
            lblAcquisitionTime = new JLabel("Acquisition time (s)=");
            contentPanel.add(lblAcquisitionTime, "cell 0 10,alignx trailing,aligny center");
        }
        {
            txtAcqtime = new JTextField();
            txtAcqtime.setText("0.0");
            contentPanel.add(txtAcqtime, "cell 1 10,growx,aligny center");
            txtAcqtime.setColumns(10);
        }
        {
            JPanel buttonPane = new JPanel();
            getContentPane().add(buttonPane, "cell 0 1,growx,aligny top");
            buttonPane.setLayout(new MigLayout("fill, insets 5", "[grow]", "[grow][]"));
            JButton okButton = new JButton("Apply and Close");
            okButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent arg0) {
                    do_okButton_actionPerformed(arg0);
                }
            });
            {
                btnApply = new JButton("Apply");
                btnApply.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        do_btnApply_actionPerformed(e);
                    }
                });
                buttonPane.add(btnApply, "flowx,cell 0 0,growx,aligny center");
            }
            okButton.setActionCommand("OK");
            buttonPane.add(okButton, "cell 0 0,grow");
            getRootPane().setDefaultButton(okButton);
            {
                btnCancel = new JButton("Cancel");
                btnCancel.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        do_btnCancel_actionPerformed(e);
                    }
                });
                buttonPane.add(btnCancel, "cell 0 0,grow");
            }
            {
                lblCheckValues = new JLabel("");
                buttonPane.add(lblCheckValues, "hidemode 3,cell 0 1,alignx center,aligny center");
            }
        }

        // this.setIconImage(new
        // ImageIcon(Param_dialog.class.getResource("/img/icona.png")).getImage());

        // Llegim els parametres de la imatge
        this.getParameters();

    }

    protected void do_okButton_actionPerformed(ActionEvent arg0) {
        boolean ok = this.setParameters();
        if (ok) {
//            tAOut.stat("Edited parameters:");
//            tAOut.ln(patt2D.getInfo());
            patt2D.recalcularCercles();
            this.dispose();
        }
    }

    private void getParameters() {
        this.patt2D.getDistMD();
        this.txtDistOD.setText(Float.toString(this.patt2D.getDistMD()));
        this.txtPicSizeX.setText(Float.toString(this.patt2D.getPixSx()));
        this.txtPicSizeY.setText(Float.toString(this.patt2D.getPixSy()));
        this.txtCentrX.setText(Float.toString(this.patt2D.getCentrX()));
        this.txtCentrY.setText(Float.toString(this.patt2D.getCentrY()));
        this.txtWave.setText(Float.toString(this.patt2D.getWavel()));
        this.txtTilt.setText(Float.toString(this.patt2D.getTiltDeg()));
        this.txtRot.setText(Float.toString(this.patt2D.getRotDeg()));
        this.txtOmeini.setText(Float.toString(this.patt2D.getOmeIni()));
        this.txtOmefin.setText(Float.toString(this.patt2D.getOmeFin()));
        this.txtAcqtime.setText(Float.toString(this.patt2D.getAcqTime()));
        this.counter = 0;
    }

    // true=tot correcte
    private boolean setParameters() {
        try {
            this.patt2D.setDistMD(Float.parseFloat(this.txtDistOD.getText()));
            this.patt2D.setPixSx(Float.parseFloat(this.txtPicSizeX.getText()));
            this.patt2D.setPixSy(Float.parseFloat(this.txtPicSizeY.getText()));
            this.patt2D.setCentrX(Float.parseFloat(this.txtCentrX.getText()));
            this.patt2D.setCentrY(Float.parseFloat(this.txtCentrY.getText()));
            this.patt2D.setWavel(Float.parseFloat(this.txtWave.getText()));
            this.patt2D.setTiltDeg(Float.parseFloat(this.txtTilt.getText()));
            this.patt2D.setRotDeg(Float.parseFloat(this.txtRot.getText()));
            this.patt2D.setOmeIni(Float.parseFloat(this.txtOmeini.getText()));
            this.patt2D.setOmeFin(Float.parseFloat(this.txtOmefin.getText()));
            this.patt2D.setAcqTime(Float.parseFloat(this.txtAcqtime.getText()));
        } catch (Exception e) {
            e.printStackTrace();
            lblCheckValues.setForeground(col[counter % 3]);
            lblCheckValues.setText("check values!");
            counter = counter + 1;
            return false;
        }
        return true;
    }
    protected void do_btnApply_actionPerformed(ActionEvent e) {
        this.setParameters();
    }
    protected void do_btnCancel_actionPerformed(ActionEvent e) {
        this.dispose();
    }
}
