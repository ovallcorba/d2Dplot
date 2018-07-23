package com.vava33.d2dplot.auxi;

import java.awt.BorderLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.ImageIcon;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import net.miginfocom.swing.MigLayout;

import javax.swing.JTextField;

import com.vava33.d2dplot.MainFrame;
import javax.swing.border.TitledBorder;
import javax.swing.border.LineBorder;
import java.awt.Color;

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
    
    /**
     * Create the dialog.
     */
    public ImageTiltRot_diag(MainFrame mf) {
        this.mf = mf;
        setTitle("Tilt/Rot Convention");
        setModal(true);
        setBounds(100, 100, 684, 494);
        getContentPane().setLayout(new BorderLayout());
        contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        getContentPane().add(contentPanel, BorderLayout.CENTER);
        contentPanel.setLayout(new MigLayout("", "[][grow]", "[grow]"));
        {
            JLabel lblImatge = new JLabel("");
            lblImatge.setIcon(new ImageIcon(ImageTiltRot_diag.class.getResource("/img/tilt_rot_new.png")));
            contentPanel.add(lblImatge, "cell 0 0,alignx left,aligny top");
        }
        {
            JPanel panel = new JPanel();
            panel.setBorder(new TitledBorder(new LineBorder(new Color(184, 207, 229)), "Simulate LaB6 2D-XRPD (2048x2048)", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(51, 51, 51)));
            contentPanel.add(panel, "cell 1 0,grow");
            panel.setLayout(new MigLayout("", "[][grow]", "[][][][][][][][][grow]"));
            {
                JLabel lblBeamCenterX = new JLabel("Beam Center X (pix)");
                panel.add(lblBeamCenterX, "cell 0 0,alignx trailing");
            }
            {
                txtCenterx = new JTextField();
                txtCenterx.setText("1024");
                panel.add(txtCenterx, "cell 1 0,growx");
                txtCenterx.setColumns(10);
            }
            {
                JLabel lblBeamCenterY = new JLabel("Beam Center Y (pix)");
                panel.add(lblBeamCenterY, "cell 0 1,alignx trailing");
            }
            {
                txtCentery = new JTextField();
                txtCentery.setText("1024");
                panel.add(txtCentery, "cell 1 1,growx");
                txtCentery.setColumns(10);
            }
            {
                JLabel lblDeteTorDistance = new JLabel("Detector Distance (mm)");
                panel.add(lblDeteTorDistance, "cell 0 2,alignx trailing");
            }
            {
                txtDist = new JTextField();
                txtDist.setText("180");
                panel.add(txtDist, "cell 1 2,growx");
                txtDist.setColumns(10);
            }
            {
                JLabel lblDetectorRot = new JLabel("Detector ROT (º)");
                panel.add(lblDetectorRot, "cell 0 3,alignx trailing");
            }
            {
                txtRot = new JTextField();
                txtRot.setText("0");
                panel.add(txtRot, "cell 1 3,growx");
                txtRot.setColumns(10);
            }
            {
                JLabel lblDetectorTilt = new JLabel("Detector TILT (º)");
                panel.add(lblDetectorTilt, "cell 0 4,alignx trailing");
            }
            {
                txtTilt = new JTextField();
                txtTilt.setText("0");
                panel.add(txtTilt, "cell 1 4,growx");
                txtTilt.setColumns(10);
            }
            {
                JLabel lblWavelengtha = new JLabel("Wavelength (A)");
                panel.add(lblWavelengtha, "cell 0 5,alignx trailing");
            }
            {
                txtWave = new JTextField();
                txtWave.setText("0.4246");
                panel.add(txtWave, "cell 1 5,growx");
                txtWave.setColumns(10);
            }
            {
                JLabel lblPixelSizemicron = new JLabel("Pixel Size (mm)");
                panel.add(lblPixelSizemicron, "cell 0 6,alignx trailing");
            }
            {
                txtPixsize = new JTextField();
                txtPixsize.setText("0.079");
                panel.add(txtPixsize, "cell 1 6,growx");
                txtPixsize.setColumns(10);
            }
            {
                JButton btnGenerateFrame = new JButton("Generate Frame");
                btnGenerateFrame.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        do_btnGenerateFrame_actionPerformed(e);
                    }
                });
                panel.add(btnGenerateFrame, "cell 0 7 2 1,growx");
            }
            {
                JButton btnClose = new JButton("Close");
                btnClose.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        do_btnClose_actionPerformed(e);
                    }
                });
                panel.add(btnClose, "cell 1 8,alignx right,aligny bottom");
            }
        }
        inicia();
    }
    
    private void inicia(){
        txtCenterx.setText("1024");
        txtCentery.setText("1024");
        txtDist.setText("180");
        txtTilt.setText("0");
        txtRot.setText("0");
        txtWave.setText("0.4246");
        txtPixsize.setText("0.079");
    }

    protected void do_btnClose_actionPerformed(ActionEvent e) {
        this.dispose();
    }
    protected void do_btnGenerateFrame_actionPerformed(ActionEvent e) {
        float cenx = Float.parseFloat(txtCenterx.getText());
        float ceny = Float.parseFloat(txtCentery.getText());
        float dist = Float.parseFloat(txtDist.getText());
        float tiltd = Float.parseFloat(txtTilt.getText());
        float rotd = Float.parseFloat(txtRot.getText());
        float wavea = Float.parseFloat(txtWave.getText());
        float pixszMM = Float.parseFloat(txtPixsize.getText());
        
        lab6 = CalibOps.createLaB6Img(cenx, ceny, dist, tiltd, rotd, wavea,pixszMM);
        mf.updatePatt2D(lab6,true,true);
    }
}
