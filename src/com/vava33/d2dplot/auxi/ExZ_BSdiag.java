package com.vava33.d2dplot.auxi;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import net.miginfocom.swing.MigLayout;

import javax.swing.JLabel;
import javax.swing.JTextField;

import org.apache.commons.math3.util.FastMath;

import com.vava33.d2dplot.D2Dplot_global;
import com.vava33.d2dplot.ExZones;
import com.vava33.jutils.VavaLogger;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class ExZ_BSdiag {

    private JDialog exzBSdialog;
    private JPanel contentPanel;
    private JTextField txtRadi;
    private JTextField txtBsarmx;
    private JTextField txtBsarmy;
    private JTextField txtBsarmw;
    private static final String className = "ExZ_BSdialog";
    private static VavaLogger log = D2Dplot_global.getVavaLogger(className);

    private boolean finishedOK;
    private int radiBS=-1;
    private int ampladaArm=-1;
    private int ipX=-1;
    private int ipY=-1;
    ExZones pare;
    
    /**
     * Create the dialog.
     */
    public ExZ_BSdiag(JFrame parent, ExZones exzd) {
        this.pare=exzd;
        contentPanel = new JPanel();
        exzBSdialog = new JDialog(parent,"Add BeamStop Mask",false);
        exzBSdialog.setBounds(100, 100, 344, 191);
        exzBSdialog.getContentPane().setLayout(new BorderLayout());
        contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        exzBSdialog.getContentPane().add(contentPanel, BorderLayout.CENTER);
        contentPanel.setLayout(new MigLayout("", "[][grow]", "[][][][]"));
        {
            JLabel lblBSradi = new JLabel("Beamstop Radius (px)");
            contentPanel.add(lblBSradi, "cell 0 0,alignx trailing");
        }
        {
            txtRadi = new JTextField();
            contentPanel.add(txtRadi, "cell 1 0,growx");
            txtRadi.setColumns(10);
        }
        {
            JLabel lblBsArmPixel = new JLabel("BS arm pixel X");
            contentPanel.add(lblBsArmPixel, "cell 0 1,alignx trailing");
        }
        {
            txtBsarmx = new JTextField();
            contentPanel.add(txtBsarmx, "cell 1 1,growx");
            txtBsarmx.setColumns(10);
        }
        {
            JLabel lblBsArmPixel_1 = new JLabel("BS arm pixel Y");
            contentPanel.add(lblBsArmPixel_1, "cell 0 2,alignx trailing");
        }
        {
            txtBsarmy = new JTextField();
            contentPanel.add(txtBsarmy, "cell 1 2,growx");
            txtBsarmy.setColumns(10);
        }
        {
            JLabel lblBsArmWidth = new JLabel("BS arm width (px)");
            contentPanel.add(lblBsArmWidth, "cell 0 3,alignx trailing");
        }
        {
            txtBsarmw = new JTextField();
            contentPanel.add(txtBsarmw, "cell 1 3,growx");
            txtBsarmw.setColumns(10);
        }
        {
            JPanel buttonPane = new JPanel();
            buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
            exzBSdialog.getContentPane().add(buttonPane, BorderLayout.SOUTH);
            {
                JButton okButton = new JButton("OK");
                okButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        do_okButton_actionPerformed(e);
                    }
                });
                okButton.setActionCommand("OK");
                buttonPane.add(okButton);
                exzBSdialog.getRootPane().setDefaultButton(okButton);
            }
            {
                JButton cancelButton = new JButton("Cancel");
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

    
    private boolean getInfoFromFields(){
        boolean allReaded = true;
        try{
            radiBS = FastMath.round(Float.parseFloat(txtRadi.getText()));    
        }catch(Exception e){
            log.info("Could not read beamstop central radius (pixels)");
            allReaded = false;
        }
        try{
            ampladaArm = FastMath.round(Float.parseFloat(txtBsarmw.getText()));  
        }catch(Exception e){
            log.info("Could not read beamstop arm width (pixels)");
            allReaded = false;
        }
        try{
            ipX = FastMath.round(Float.parseFloat(txtBsarmx.getText()));  
        }catch(Exception e){
            log.info("Could not read pixel X");
            allReaded = false;
        }
        try{
            ipY = FastMath.round(Float.parseFloat(txtBsarmy.getText()));  
        }catch(Exception e){
            log.info("Could not read pixel Y");
            allReaded = false;
        }
        return allReaded;
    }
    
    public int getRadiBS() {
        return radiBS;
    }


    public void setRadiBS(int radiBS) {
        this.radiBS = radiBS;
    }


    public int getAmpladaArm() {
        return ampladaArm;
    }


    public void setAmpladaArm(int ampladaArm) {
        this.ampladaArm = ampladaArm;
    }


    public int getIpX() {
        return ipX;
    }


    public void setIpX(int ipX) {
        this.ipX = ipX;
    }


    public int getIpY() {
        return ipY;
    }


    public void setIpY(int ipY) {
        this.ipY = ipY;
    }


    public boolean isFinishedOK() {
        return finishedOK;
    }


    public void setFinishedOK(boolean finishedOK) {
        this.finishedOK = finishedOK;
    }


    protected void do_okButton_actionPerformed(ActionEvent evt) {
        finishedOK = getInfoFromFields();
        if (finishedOK)pare.applyBSparameters(this.radiBS,this.ampladaArm,this.ipX,this.ipY);
        exzBSdialog.dispose();
    }
    protected void do_cancelButton_actionPerformed(ActionEvent e) {
        finishedOK = false;
        exzBSdialog.dispose();
    }


	/**
	 * @return the exzBSdialog
	 */
	public JDialog getExzBSdialog() {
		return exzBSdialog;
	}


	/**
	 * @param exzBSdialog the exzBSdialog to set
	 */
	public void setExzBSdialog(JDialog exzBSdialog) {
		this.exzBSdialog = exzBSdialog;
	}
}
