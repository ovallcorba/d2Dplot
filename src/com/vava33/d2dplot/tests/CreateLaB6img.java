package com.vava33.d2dplot.tests;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import net.miginfocom.swing.MigLayout;

import javax.swing.JTextField;

import com.vava33.jutils.FileUtils;
import com.vava33.jutils.VavaLogger;

import com.vava33.d2dplot.D2Dplot_global;
import com.vava33.d2dplot.MainFrame;
import com.vava33.d2dplot.auxi.CalibOps;
import com.vava33.d2dplot.auxi.ImgFileUtils;
import com.vava33.d2dplot.auxi.Pattern2D;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.File;
import javax.swing.JLabel;

public class CreateLaB6img extends JDialog {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    
    private final JPanel contentPanel = new JPanel();
    private JTextField txtCenx;
    private JTextField txtCeny;
    private JTextField txtDist;
    private JTextField txtTiltdeg;
    private JTextField txtRotdeg;
    private JTextField txtWavea;
    private static VavaLogger log = D2Dplot_global.log;
    MainFrame mf;
    Pattern2D lab6;
    private JButton btnSaveimg;
    private JLabel lblX;
    private JLabel lblY;
    private JLabel lblFidy;
    private JLabel lblTilt;
    private JLabel lblRot;
    private JLabel lblWave;
    private JTextField txtPixszMM;
    private JLabel lblPix;
    /**
     * Launch the application.
     */
//    public static void main(String[] args) {
//        try {
//            CreateLaB6img dialog = new CreateLaB6img();
//            dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
//            dialog.setVisible(true);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    /**
     * Create the dialog.
     */
    public CreateLaB6img(MainFrame mf) {
        this.mf = mf;
        setBounds(100, 100, 450, 339);
        getContentPane().setLayout(new BorderLayout());
        contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        getContentPane().add(contentPanel, BorderLayout.CENTER);
        contentPanel.setLayout(new MigLayout("", "[][grow]", "[][][][][][][][]"));
        {
            lblX = new JLabel("x");
            contentPanel.add(lblX, "cell 0 0,alignx trailing");
        }
        {
            txtCenx = new JTextField();
            txtCenx.setText("cenx");
            contentPanel.add(txtCenx, "cell 1 0,growx");
            txtCenx.setColumns(10);
        }
        {
            lblY = new JLabel("y");
            contentPanel.add(lblY, "cell 0 1,alignx trailing");
        }
        {
            txtCeny = new JTextField();
            txtCeny.setText("ceny");
            contentPanel.add(txtCeny, "cell 1 1,growx");
            txtCeny.setColumns(10);
        }
        {
            lblFidy = new JLabel("dist");
            contentPanel.add(lblFidy, "cell 0 2,alignx trailing");
        }
        {
            txtDist = new JTextField();
            txtDist.setText("dist");
            contentPanel.add(txtDist, "cell 1 2,growx");
            txtDist.setColumns(10);
        }
        {
            lblTilt = new JLabel("tilt");
            contentPanel.add(lblTilt, "cell 0 3,alignx trailing");
        }
        {
            txtTiltdeg = new JTextField();
            txtTiltdeg.setText("tiltdeg");
            contentPanel.add(txtTiltdeg, "cell 1 3,growx");
            txtTiltdeg.setColumns(10);
        }
        {
            lblRot = new JLabel("rot");
            contentPanel.add(lblRot, "cell 0 4,alignx trailing");
        }
        {
            txtRotdeg = new JTextField();
            txtRotdeg.setText("rotdeg");
            contentPanel.add(txtRotdeg, "cell 1 4,growx");
            txtRotdeg.setColumns(10);
        }
        {
            lblWave = new JLabel("wave");
            contentPanel.add(lblWave, "cell 0 5,alignx trailing");
        }
        {
            txtWavea = new JTextField();
            txtWavea.setText("waveA");
            contentPanel.add(txtWavea, "cell 1 5,growx");
            txtWavea.setColumns(10);
        }
        {
            JButton btnCreateImg = new JButton("create img");
            btnCreateImg.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    do_btnCreateImg_actionPerformed(e);
                }
            });
            {
                lblPix = new JLabel("pix");
                contentPanel.add(lblPix, "cell 0 6,alignx trailing");
            }
            {
                txtPixszMM = new JTextField();
                txtPixszMM.setText("0.079");
                contentPanel.add(txtPixszMM, "cell 1 6,growx");
                txtPixszMM.setColumns(10);
            }
            contentPanel.add(btnCreateImg, "flowx,cell 1 7");
        }
        {
            btnSaveimg = new JButton("saveImg");
            contentPanel.add(btnSaveimg, "cell 1 7");
        }
        {
            JPanel buttonPane = new JPanel();
            buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
            getContentPane().add(buttonPane, BorderLayout.SOUTH);
            {
                JButton okButton = new JButton("OK");
                okButton.setActionCommand("OK");
                buttonPane.add(okButton);
                getRootPane().setDefaultButton(okButton);
            }
            {
                JButton cancelButton = new JButton("Cancel");
                cancelButton.setActionCommand("Cancel");
                buttonPane.add(cancelButton);
            }
        }
        
        inicia();
    }

    private void inicia(){
        txtCenx.setText("1024");
        txtCeny.setText("1024");
        txtDist.setText("180");
        txtTiltdeg.setText("15");
        txtRotdeg.setText("90");
        txtWavea.setText("0.4246");
        txtPixszMM.setText("0.079");
    }
    protected void do_btnCreateImg_actionPerformed(ActionEvent e) {
        float cenx = Float.parseFloat(txtCenx.getText());
        float ceny = Float.parseFloat(txtCeny.getText());
        float dist = Float.parseFloat(txtDist.getText());
        float tiltd = Float.parseFloat(txtTiltdeg.getText());
        float rotd = Float.parseFloat(txtRotdeg.getText());
        float wavea = Float.parseFloat(txtWavea.getText());
        float pixszMM = Float.parseFloat(txtPixszMM.getText());
        
        lab6 = CalibOps.createLaB6Img(cenx, ceny, dist, tiltd, rotd, wavea,pixszMM);
        mf.updatePatt2D(lab6,true);
    }
    
    protected void saveImg(){
        File f = FileUtils.fchooserSaveAsk(this, new File(D2Dplot_global.getWorkdir()), null);
        log.debug("writting "+f.toString());
        ImgFileUtils.writeEDF(f, lab6);
    }
}
