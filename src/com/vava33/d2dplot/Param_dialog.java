package vava33.d2dplot;

import java.awt.Color;
import java.awt.Dimension;
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

import vava33.d2dplot.auxi.ImageTiltRot_diag;
import vava33.d2dplot.auxi.ImgFileUtils;
import vava33.d2dplot.auxi.Pattern2D;
import net.miginfocom.swing.MigLayout;
import javax.swing.JCheckBox;

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
    private JButton label;
    private JButton btnUpdate;
    private JCheckBox chckbxKeepCalib;

    private Pattern2D patt2D;
    private ImagePanel ip;
    
    /**
     * Create the dialog.
     */
    public Param_dialog(ImagePanel ipanel,Pattern2D p2d) {
        setIconImage(Toolkit.getDefaultToolkit().getImage(Param_dialog.class.getResource("/img/Icona.png")));
        setModal(false);
        setAlwaysOnTop(true);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Instrumental Parameters");
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        int width = 660;
        int height = 730;
        int x = (screen.width - width) / 2;
        int y = (screen.height - height) / 2;
        setBounds(x, y, 476, 498);
        getContentPane().setLayout(new MigLayout("", "[]", "[grow][]"));
        this.contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        getContentPane().add(this.contentPanel, "cell 0 0,grow");
        contentPanel.setLayout(new MigLayout("fill, insets 5", "[][][grow]", "[grow][grow][grow][grow][grow][grow][grow][grow][][][]"));
        {
            JLabel lblSampledetectorDistancemm = new JLabel("Sample-Detector distance (mm)=");
            contentPanel.add(lblSampledetectorDistancemm, "cell 0 0 2 1,alignx trailing,aligny center");
        }
        {
            this.txtDistOD = new JTextField();
            this.txtDistOD.setText("150.000");
            contentPanel.add(this.txtDistOD, "cell 2 0,growx,aligny center");
            this.txtDistOD.setColumns(10);
        }
        {
            JLabel lblPixelSizeX = new JLabel("Pixel size X (mm)=");
            contentPanel.add(lblPixelSizeX, "cell 0 1 2 1,alignx trailing,aligny center");
        }
        {
            this.txtPicSizeX = new JTextField();
            this.txtPicSizeX.setText("0.1024");
            contentPanel.add(this.txtPicSizeX, "cell 2 1,growx,aligny center");
            this.txtPicSizeX.setColumns(10);
        }
        {
            JLabel lblPixelSizeY = new JLabel("Pixel size Y (mm)=");
            contentPanel.add(lblPixelSizeY, "cell 0 2 2 1,alignx trailing,aligny center");
        }
        {
            this.txtPicSizeY = new JTextField();
            this.txtPicSizeY.setText("0.1024");
            contentPanel.add(this.txtPicSizeY, "cell 2 2,growx,aligny center");
            this.txtPicSizeY.setColumns(10);
        }
        {
            JLabel lblBeamCentreX = new JLabel("Beam centre X (pixel)=");
            contentPanel.add(lblBeamCentreX, "cell 0 3 2 1,alignx trailing,aligny center");
        }
        {
            this.txtCentrX = new JTextField();
            this.txtCentrX.setText("1024");
            contentPanel.add(this.txtCentrX, "cell 2 3,growx,aligny center");
            this.txtCentrX.setColumns(10);
        }
        {
            JLabel lblBeamCentreY = new JLabel("Beam centre Y (pixel)=");
            contentPanel.add(lblBeamCentreY, "cell 0 4 2 1,alignx trailing,aligny center");
        }
        {
            this.txtCentrY = new JTextField();
            this.txtCentrY.setText("1024");
            contentPanel.add(this.txtCentrY, "cell 2 4,growx,aligny center");
            this.txtCentrY.setColumns(10);
        }
        {
            this.lblWavelengtha = new JLabel("Wavelength (A)=");
            contentPanel.add(this.lblWavelengtha, "cell 0 5 2 1,alignx trailing,aligny center");
        }
        {
            this.txtWave = new JTextField();
            this.txtWave.setText("0.0");
            contentPanel.add(this.txtWave, "cell 2 5,growx,aligny center");
            this.txtWave.setColumns(10);
        }
        {
            label = new JButton("?");
            label.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    do_label_actionPerformed(e);
                }
            });
            contentPanel.add(label, "cell 0 6,alignx left");
        }
        {
            lblDetectorTilt = new JLabel("Detector Tilt (º)=");
            lblDetectorTilt.setToolTipText("");
            contentPanel.add(lblDetectorTilt, "cell 1 6,alignx trailing,aligny center");
        }
        {
            txtTilt = new JTextField();
            txtTilt.setText("0.0");
            txtTilt.setColumns(10);
            contentPanel.add(txtTilt, "cell 2 6,growx,aligny center");
        }
        {
            lblDetectorRot = new JLabel("Detector Rot (º)=");
            contentPanel.add(lblDetectorRot, "cell 0 7 2 1,alignx trailing,aligny center");
        }
        {
            txtRot = new JTextField();
            txtRot.setText("0.0");
            txtRot.setColumns(10);
            contentPanel.add(txtRot, "cell 2 7,growx,aligny center");
        }
        {
            lblscanOmegaIni = new JLabel("(scan) omega ini (º)=");
            contentPanel.add(lblscanOmegaIni, "cell 0 8 2 1,alignx trailing,aligny center");
        }
        {
            txtOmeini = new JTextField();
            txtOmeini.setText("0.0");
            contentPanel.add(txtOmeini, "cell 2 8,growx,aligny center");
            txtOmeini.setColumns(10);
        }
        {
            lblscanOmegaEnd = new JLabel("(scan) omega end (º)=");
            contentPanel.add(lblscanOmegaEnd, "cell 0 9 2 1,alignx trailing,aligny center");
        }
        {
            txtOmefin = new JTextField();
            txtOmefin.setText("0.0");
            contentPanel.add(txtOmefin, "cell 2 9,growx,aligny center");
            txtOmefin.setColumns(10);
        }
        {
            lblAcquisitionTime = new JLabel("Acquisition time (s)=");
            contentPanel.add(lblAcquisitionTime, "cell 0 10 2 1,alignx trailing,aligny center");
        }
        {
            txtAcqtime = new JTextField();
            txtAcqtime.setText("0.0");
            contentPanel.add(txtAcqtime, "cell 2 10,growx,aligny center");
            txtAcqtime.setColumns(10);
        }
        {
            JPanel buttonPane = new JPanel();
            getContentPane().add(buttonPane, "cell 0 1,growx,aligny top");
            buttonPane.setLayout(new MigLayout("fill, insets 5", "[][grow]", "[][grow][]"));
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
                {
                    btnUpdate = new JButton("Update from header");
                    btnUpdate.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent e) {
                            do_btnUpdate_actionPerformed(e);
                        }
                    });
                    {
                        chckbxKeepCalib = new JCheckBox("keep calibration info for the session");
                        buttonPane.add(chckbxKeepCalib, "cell 0 0 2 1");
                    }
                    buttonPane.add(btnUpdate, "cell 0 1");
                }
                buttonPane.add(btnApply, "flowx,cell 1 1,growx,aligny center");
            }
            okButton.setActionCommand("OK");
            buttonPane.add(okButton, "cell 1 1,grow");
            getRootPane().setDefaultButton(okButton);
            {
                btnCancel = new JButton("Cancel");
                btnCancel.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        do_btnCancel_actionPerformed(e);
                    }
                });
                buttonPane.add(btnCancel, "cell 1 1,grow");
            }
            {
                lblCheckValues = new JLabel("");
                buttonPane.add(lblCheckValues, "hidemode 3,cell 1 2,alignx center,aligny center");
            }
        }

        // Llegim els parametres de la imatge, agafarem ipanel o si no s'ha donat doncs patt2d
        this.ip=ipanel;
        this.patt2D=p2d;
        this.inicia();

    }

    public void inicia(){
        if (this.ip!=null){
            this.patt2D=ip.getPatt2D();    
        }
        this.getParameters();
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
            if(D2Dplot_global.isDebug())e.printStackTrace();
            lblCheckValues.setForeground(col[counter % 3]);
            lblCheckValues.setText("check values!");
            counter = counter + 1;
            return false;
        }
        return true;
    }
    
    public void setTiltRotFields(float tilt, float rot){
        this.txtRot.setText(Float.toString(rot));
        this.txtTilt.setText(Float.toString(tilt));
    }
    
    protected boolean do_btnApply_actionPerformed(ActionEvent e) {
        boolean ok = this.setParameters();
        D2Dplot_global.setKeepCalibration(chckbxKeepCalib.isSelected());
        if (D2Dplot_global.isKeepCalibration()){
            D2Dplot_global.setCalib(this.patt2D.getDistMD(), this.patt2D.getCentrX(), this.patt2D.getCentrY(), this.patt2D.getTiltDeg(), this.patt2D.getRotDeg());
        }
        if (this.ip!=null)this.ip.actualitzarVista();
        return ok;
    }
    
    protected void do_okButton_actionPerformed(ActionEvent arg0) {
        boolean ok = do_btnApply_actionPerformed(null);
        if (ok) {
            patt2D.recalcularCercles();
            if (this.ip!=null)this.ip.actualitzarVista();
            this.dispose();
        }
    }
    
    protected void do_btnCancel_actionPerformed(ActionEvent e) {
        if (this.ip!=null)this.ip.actualitzarVista();
        this.dispose();
    }
    protected void do_label_actionPerformed(ActionEvent e) {
        ImageTiltRot_diag id = new ImageTiltRot_diag(this);
        id.setVisible(true);
    }
    
    protected void do_btnUpdate_actionPerformed(ActionEvent e) {
        //it should update from image header
        this.chckbxKeepCalib.setSelected(false);
        D2Dplot_global.setKeepCalibration(false);
        Pattern2D temp = ImgFileUtils.readPatternFile(patt2D.getImgfile(),false);
        patt2D.setCentrX(temp.getCentrX());
        patt2D.setCentrY(temp.getCentrY());
        patt2D.setDistMD(temp.getDistMD());
        patt2D.setTiltDeg(temp.getTiltDeg());
        patt2D.setRotDeg(temp.getRotDeg());
        this.getParameters();
        if (this.ip!=null)this.ip.actualitzarVista();
    }
}
