package com.vava33.d2dplot.auxi;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

import com.vava33.d2dplot.MainFrame;

import net.miginfocom.swing.MigLayout;

public class ImageTiltRot_diag extends JDialog {

    private static final long serialVersionUID = -4616295467870001148L;
    private final JPanel contentPanel = new JPanel();
    private JTextField txtCenterx;
    private JTextField txtCentery;
    private JTextField txtDist;
    private JTextField txtRot;
    private JTextField txtTilt;
    private JTextField txtWave;
    private JTextField txtPixsize;
    MainFrame mf;
    Pattern2D lab6;
    private JComboBox<String> comboCalib;

    /**
     * Create the dialog.
     */
    public ImageTiltRot_diag(MainFrame mf) {
        this.mf = mf;
        this.setTitle("Tilt/Rot Convention");
        this.setModal(true);
        this.setBounds(100, 100, 684, 494);
        this.getContentPane().setLayout(new BorderLayout());
        this.contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        this.getContentPane().add(this.contentPanel, BorderLayout.CENTER);
        this.contentPanel.setLayout(new MigLayout("", "[][grow]", "[grow]"));
        {
            final JLabel lblImatge = new JLabel("");
            lblImatge.setIcon(new ImageIcon(ImageTiltRot_diag.class.getResource("/img/tilt_rot_new.png")));
            this.contentPanel.add(lblImatge, "cell 0 0,alignx left,aligny top");
        }
        {
            final JPanel panel = new JPanel();
            panel.setBorder(
                    new TitledBorder(new LineBorder(new Color(184, 207, 229)), "Simulate Calibrant 2D-XRPD (2048x2048)",
                            TitledBorder.LEADING, TitledBorder.TOP, null, new Color(51, 51, 51)));
            this.contentPanel.add(panel, "cell 1 0,grow");
            panel.setLayout(new MigLayout("", "[][grow]", "[][][][][][][][][][grow]"));
            {
                final JLabel lblBeamCenterX = new JLabel("Beam Center X (pix)");
                panel.add(lblBeamCenterX, "cell 0 0,alignx trailing");
            }
            {
                this.txtCenterx = new JTextField();
                this.txtCenterx.setText("1024");
                panel.add(this.txtCenterx, "cell 1 0,growx");
                this.txtCenterx.setColumns(10);
            }
            {
                final JLabel lblBeamCenterY = new JLabel("Beam Center Y (pix)");
                panel.add(lblBeamCenterY, "cell 0 1,alignx trailing");
            }
            {
                this.txtCentery = new JTextField();
                this.txtCentery.setText("1024");
                panel.add(this.txtCentery, "cell 1 1,growx");
                this.txtCentery.setColumns(10);
            }
            {
                final JLabel lblDeteTorDistance = new JLabel("Detector Distance (mm)");
                panel.add(lblDeteTorDistance, "cell 0 2,alignx trailing");
            }
            {
                this.txtDist = new JTextField();
                this.txtDist.setText("180");
                panel.add(this.txtDist, "cell 1 2,growx");
                this.txtDist.setColumns(10);
            }
            {
                final JLabel lblDetectorRot = new JLabel("Detector ROT (º)");
                panel.add(lblDetectorRot, "cell 0 3,alignx trailing");
            }
            {
                this.txtRot = new JTextField();
                this.txtRot.setText("0");
                panel.add(this.txtRot, "cell 1 3,growx");
                this.txtRot.setColumns(10);
            }
            {
                final JLabel lblDetectorTilt = new JLabel("Detector TILT (º)");
                panel.add(lblDetectorTilt, "cell 0 4,alignx trailing");
            }
            {
                this.txtTilt = new JTextField();
                this.txtTilt.setText("0");
                panel.add(this.txtTilt, "cell 1 4,growx");
                this.txtTilt.setColumns(10);
            }
            {
                final JLabel lblWavelengtha = new JLabel("Wavelength (A)");
                panel.add(lblWavelengtha, "cell 0 5,alignx trailing");
            }
            {
                this.txtWave = new JTextField();
                this.txtWave.setText("0.4246");
                panel.add(this.txtWave, "cell 1 5,growx");
                this.txtWave.setColumns(10);
            }
            {
                final JLabel lblPixelSizemicron = new JLabel("Pixel Size (mm)");
                panel.add(lblPixelSizemicron, "cell 0 6,alignx trailing");
            }
            {
                this.txtPixsize = new JTextField();
                this.txtPixsize.setText("0.079");
                panel.add(this.txtPixsize, "cell 1 6,growx");
                this.txtPixsize.setColumns(10);
            }
            {
                final JButton btnGenerateFrame = new JButton("Generate Frame");
                btnGenerateFrame.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        ImageTiltRot_diag.this.do_btnGenerateFrame_actionPerformed(e);
                    }
                });
                {
                    final JLabel lblCalibrant = new JLabel("calibrant");
                    panel.add(lblCalibrant, "cell 0 7,alignx trailing");
                }
                {
                    this.comboCalib = new JComboBox<String>();
                    panel.add(this.comboCalib, "cell 1 7,growx");
                }
                panel.add(btnGenerateFrame, "cell 0 8 2 1,growx");
            }
            {
                final JButton btnClose = new JButton("Close");
                btnClose.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        ImageTiltRot_diag.this.do_btnClose_actionPerformed(e);
                    }
                });
                panel.add(btnClose, "cell 1 9,alignx right,aligny bottom");
            }
        }
        this.inicia();
    }

    private void inicia() {
        this.txtCenterx.setText("1024");
        this.txtCentery.setText("1024");
        this.txtDist.setText("180");
        this.txtTilt.setText("0");
        this.txtRot.setText("0");
        this.txtWave.setText("0.4246");
        this.txtPixsize.setText("0.079");
        //La llista de calibrants s'ha omplert a l'obrir el programa (opcions), la llegim i posem calibrant per defecte (LaB6)
        final Iterator<Calibrant> itrC = CalibOps.getCalibrants().iterator();
        while (itrC.hasNext()) {
            this.comboCalib.addItem(itrC.next().getName());
            this.comboCalib.setSelectedIndex(0);//we suppose the first is LaB6
        }
    }

    protected void do_btnClose_actionPerformed(ActionEvent e) {
        this.dispose();
    }

    protected void do_btnGenerateFrame_actionPerformed(ActionEvent e) {
        final float cenx = Float.parseFloat(this.txtCenterx.getText());
        final float ceny = Float.parseFloat(this.txtCentery.getText());
        final float dist = Float.parseFloat(this.txtDist.getText());
        final float tiltd = Float.parseFloat(this.txtTilt.getText());
        final float rotd = Float.parseFloat(this.txtRot.getText());
        final float wavea = Float.parseFloat(this.txtWave.getText());
        final float pixszMM = Float.parseFloat(this.txtPixsize.getText());
        this.lab6 = CalibOps.createLaB6Img(cenx, ceny, dist, tiltd, rotd, wavea, pixszMM,
                CalibOps.getCalibrants().get(this.comboCalib.getSelectedIndex()).getDsp());
        this.mf.updatePatt2D(this.lab6, true, true);
    }
}
